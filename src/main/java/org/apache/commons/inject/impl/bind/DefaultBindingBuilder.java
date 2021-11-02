/**
 Licensed to the Apache Software Foundation (ASF) under one or more
 contributor license agreements.  See the NOTICE file distributed with
 this work for additional information regarding copyright ownership.
 The ASF licenses this file to You under the Apache License, Version 2.0
 (the "License"); you may not use this file except in compliance with
 the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
*/
package org.apache.commons.inject.impl.bind;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import org.apache.commons.inject.api.IBinding;
import org.apache.commons.inject.api.IInjector;
import org.apache.commons.inject.api.IKey;
import org.apache.commons.inject.api.IPoint;
import org.apache.commons.inject.api.IProvider;
import org.apache.commons.inject.api.Key;
import org.apache.commons.inject.api.bind.IAnnotatedBindingBuilder;
import org.apache.commons.inject.api.bind.ILinkedBindingBuilder;
import org.apache.commons.inject.api.bind.IScopedBindingBuilder;
import org.apache.commons.inject.api.bind.Scopes;
import org.apache.commons.inject.api.bind.IBinder.IInjectionListener;
import org.apache.commons.inject.api.bind.IBinder.IInjectionParticipator;
import org.apache.commons.inject.impl.AbstractBaseProvider;
import org.apache.commons.inject.impl.AbstractScopedProvider;
import org.apache.commons.inject.impl.Introspector;
import org.apache.commons.inject.impl.ListPoint;
import org.apache.commons.inject.impl.MutableBindingSet;
import org.apache.commons.inject.impl.AbstractBindingSet.MappedKey;

import javax.inject.Provider;


/**
 * Default implementation of a binding builder. Implements
 * {@link IAnnotatedBindingBuilder}, thus {@link IScopedBindingBuilder},
 * and {@link ILinkedBindingBuilder} as well. In other words: Under the
 * hood, you are always using this one and only binding builder.
 */
public class DefaultBindingBuilder<T> implements IAnnotatedBindingBuilder<T> {
	private final Class<T> sourceType;
	private final IKey<T> sourceKey;
	private Annotation sourceAnnotation;
	private Class<? extends Annotation> sourceAnnotationType;
	private T targetInstance;
	private Class<? extends T> targetType;
	private Constructor<? extends T> targetConstructor;
	private Method targetMethod;
	private Provider<? extends T> targetProvider;
	private IProvider<? extends T> targetIProvider;
	private Scopes scope;

	public DefaultBindingBuilder(Class<T> pType) {
		this(pType, Key.NO_NAME);
	}

	public DefaultBindingBuilder(Class<T> pType, String pName) {
		sourceKey = new Key<T>(pType, pName);
		sourceType = pType;
	}

	public DefaultBindingBuilder(IKey<T> pKey) {
		sourceKey = pKey;
		sourceType = null;
	}
	
	@Override
	public void toInstance(T pInstance) {
		if (pInstance == null) {
			throw new NullPointerException("The target instance must not be null.");
		}
		checkNoTarget();
		targetInstance = pInstance;
		asEagerSingleton();
	}

	private void checkNoTarget() {
		if (targetInstance != null
			||  targetType != null
			||  targetConstructor != null
			||  targetMethod != null
			||  targetProvider != null
			||  targetIProvider != null) {
			throw new IllegalStateException("The methods " + TARGET_METHOD_LIST
					+ " are mutually exclusive, and may be invoked only once.");
		}
	}

	private static final String SCOPE_METHOD_LIST
		= "toInstance(Object), scope(Scopes), asEagerSingleton(), and asLazySingleton()";
	private static final String TARGET_METHOD_LIST
		= "toInstance(Object), to(Class), to(Constructor), to(Method),"
		+ " to(Provider, Class), to(IProvider)";
	
	@Override
	public IScopedBindingBuilder<T> to(Class<? extends T> pImplClass) {
		if (pImplClass == null) {
			throw new NullPointerException("The target class must not be null.");
		}
		checkNoTarget();
		targetType = pImplClass;
		return this;
	}

	@Override
	public IScopedBindingBuilder<T> to(Constructor<? extends T> pConstructor) {
		if (pConstructor == null) {
			throw new NullPointerException("The target constructor must not be null.");
		}
		checkNoTarget();
		targetConstructor = pConstructor;
		return this;
	}

	@Override
	public IScopedBindingBuilder<T> to(Method pFactoryMethod) {
		if (pFactoryMethod == null) {
			throw new NullPointerException("The target constructor must not be null.");
		}
		if (!Modifier.isStatic(pFactoryMethod.getModifiers())) {
			throw new IllegalStateException("The target method must be static.");
		}
		if (pFactoryMethod.getReturnType().isPrimitive()) {
			throw new IllegalStateException("The target method must return a non-primitive result.");
		}
		if (pFactoryMethod.getReturnType().isArray()) {
			throw new IllegalStateException("The target method must return a single object, and not an array.");
		}
		if (Void.TYPE == pFactoryMethod.getReturnType()) {
			throw new IllegalStateException("The target method must return a non-void result.");
		}
		checkNoTarget();
		targetMethod = pFactoryMethod;
		return this;
	}

	@Override
	public <S extends T> IScopedBindingBuilder<T> to(Class<S> pType,
			Provider<S> pProvider) {
		if (pType == null) {
			throw new NullPointerException("The target type must not be null.");
		}
		if (pProvider == null) {
			throw new NullPointerException("The target provider must not be null.");
		}
		checkNoTarget();
		targetType = pType;
		targetProvider = pProvider;
		return this;
	}

	@Override
	public IScopedBindingBuilder<T> to(IProvider<? extends T> pProvider) {
		if (pProvider == null) {
			throw new NullPointerException("The target provider must not be null.");
		}
		checkNoTarget();
		targetIProvider = pProvider;
		return this;
	}

	@Override
	public void scope(Scopes pScope) {
		if (pScope == null) {
			throw new NullPointerException("The target scope must not be null.");
		}
		if (scope != null) {
			throw new IllegalStateException("The methods " + SCOPE_METHOD_LIST
					+ " are mutually exclusive, and may be invoked only once.");
		}
		scope = pScope;
	}

	@Override
	public void asEagerSingleton() {
		scope(Scopes.EAGER_SINGLETON);
	}

	@Override
	public void asLazySingleton() {
		scope(Scopes.LAZY_SINGLETON);
	}

	@Override
	public ILinkedBindingBuilder<T> annotatedWith(Annotation pAnnotation) {
		if (pAnnotation == null) {
			throw new NullPointerException("The annotation must not be null.");
		}
		if (sourceAnnotation != null) {
			throw new IllegalStateException("The method annotatedWith(Annotation) must not be invoked twice.");
		}
		sourceAnnotation = pAnnotation;
		return this;
	}

	@Override
	public ILinkedBindingBuilder<T> annotatedWith(
			Class<? extends Annotation> pAnnotationType) {
		if (pAnnotationType == null) {
			throw new NullPointerException("The annotation type must not be null.");
		}
		if (sourceAnnotationType != null) {
			throw new IllegalStateException("The method annotatedWith(Class) must not be invoked twice.");
		}
		sourceAnnotationType = pAnnotationType;
		return this;
	}

	public void build(MutableBindingSet pBindings, final List<IInjectionListener> pListeners,
			final List<IInjectionParticipator> pParticipators) {
		final Class<T> baseType = getBaseType();
		ListPoint<T> point = Introspector.getInstance().getPoint(baseType, pBindings);
		final IKey<T> key = sourceKey;
		if (pParticipators != null) {
			for (IInjectionParticipator participator : pParticipators) {
				final List<IPoint<Object>> points = participator.getPoints(key, baseType);
				if (points != null) {
					for (IPoint<Object> p : points) {
						@SuppressWarnings("unchecked")
						final IPoint<T> pnt = (IPoint<T>) p;
						point.add(pnt);
					}
				}
			}
		}
		if (pListeners != null  &&  !pListeners.isEmpty()) {
			point.add(new IPoint<T>(){
				@Override
				public void injectTo(T pInstance, IInjector pInjector) {
					for (IInjectionListener listener : pListeners) {
						listener.initialized(key, pInstance);
					}
				}
			});
		}
		final IProvider<T> baseProvider = getBaseProvider(baseType, point, pBindings);
		final IProvider<T> scopedProvider = getScopedProvider(baseProvider);
		final IBinding<T> binding = new DefaultBinding<T>(scopedProvider, point);
		final Annotation[] annotations;
		if (sourceAnnotation == null) {
			annotations = Key.NO_ANNOTATIONS;
		} else {
			annotations = new Annotation[]{ sourceAnnotation };
		}
		final MappedKey<T> mkey = new MappedKey<T>(sourceKey.getType(), sourceKey.getName(),
				annotations, sourceAnnotationType);
				
		pBindings.add(mkey, binding);
	}

	private Class<T> getBaseType() {
		if (targetInstance != null) {
			@SuppressWarnings("unchecked")
			final Class<T> cl = (Class<T>) targetInstance.getClass();
			return cl;
		}
		if (targetProvider != null) {
			@SuppressWarnings("unchecked")
			final Class<T> cl = (Class<T>) targetType;
			return cl;
		}
		if (targetIProvider != null) {
			@SuppressWarnings("unchecked")
			final Class<T> cl = (Class<T>) targetIProvider.getType();
			return cl;
		}
		if (targetType != null) {
			@SuppressWarnings("unchecked")
			final Class<T> cl = (Class<T>) targetType;
			return cl;
		}
		if (targetConstructor != null) {
			@SuppressWarnings("unchecked")
			final Class<T> cl = (Class<T>) targetConstructor.getDeclaringClass();
			return cl;
		}
		if (targetMethod != null) {
			@SuppressWarnings("unchecked")
			final Class<T> cl = (Class<T>) targetMethod.getReturnType();
			return cl;
		}
		if (sourceType == null) {
			throw new IllegalStateException("Neither of the methods "
					+ TARGET_METHOD_LIST + " has been invoked on this binding builder,"
					+ " which is required when binding a key.");
		}
		if (sourceType.isInterface()  ||  Modifier.isAbstract(sourceType.getModifiers())) {
			throw new IllegalStateException("Neither of the methods "
					+ TARGET_METHOD_LIST + " has been invoked on this binding builder, "
					+ " but cannot bind " + sourceType.getName()
					+ " as target type, because it is an interface, or an abstract class.");
		}
		return sourceType;
	}
	private AbstractBaseProvider<T> getBaseProvider(Class<T> pType, IPoint<T> pPoint, MutableBindingSet pBindings) {
		if (targetInstance != null) {
			return new AbstractBaseProvider<T>(pType, pPoint){
				@Override
				public T get() {
					return targetInstance;
				}
			};
		}
		if (targetProvider != null) {
			return new AbstractBaseProvider<T>(pType, pPoint){
				@Override
				public T get() {
					return (T) targetProvider.get();
				}
			};
		}
		if (targetIProvider != null) {
			return new AbstractBaseProvider<T>(pType, pPoint){
				@Override
				public T get() {
					return (T) targetIProvider.get();
				}
				
			};
		}
		if (targetType != null) {
			@SuppressWarnings("unchecked")
			final Class<T> cl = (Class<T>) targetType;
			final AbstractBaseProvider<T> abp = (AbstractBaseProvider<T>) Introspector.getInstance().getProvider(cl, pPoint, pBindings);
			return abp;
		}
		if (targetConstructor != null) {
			final AbstractBaseProvider<T> abp = (AbstractBaseProvider<T>) Introspector.getInstance().getProvider(targetConstructor, pBindings);
			return abp;
		}
		if (targetMethod != null) {
			@SuppressWarnings("unchecked")
			final AbstractBaseProvider<T> abp = (AbstractBaseProvider<T>) Introspector.getInstance().getProvider(targetMethod, pBindings);
			return abp;
		}
		if (sourceType != null) {
			final AbstractBaseProvider<T> abp = (AbstractBaseProvider<T>) Introspector.getInstance().getProvider(sourceType, pPoint, pBindings);
			return abp;
		}
		throw new IllegalStateException("Neither of the methods "
				+ TARGET_METHOD_LIST + " has been invoked on this binding builder.");
	}

	public AbstractScopedProvider<T> getScopedProvider(IProvider<T> pBaseProvider) {
		if (scope == null) {
			throw new IllegalStateException("Neither of the methods "
					+ SCOPE_METHOD_LIST + " has been invoked on this binding builder.");
			
		}
		switch(scope) {
		case PER_CALL:
			return new PerCallProvider<T>(pBaseProvider);
		case EAGER_SINGLETON:
			return new EagerSingletonProvider<T>(pBaseProvider);
		case LAZY_SINGLETON:
			return new LazySingletonProvider<T>(pBaseProvider);
		default:
			throw new IllegalStateException("Invalid scope: " + scope);
		}
	}
}

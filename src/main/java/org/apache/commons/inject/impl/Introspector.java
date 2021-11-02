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
package org.apache.commons.inject.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import org.apache.commons.inject.api.IBinding;
import org.apache.commons.inject.api.IInjector;
import org.apache.commons.inject.api.IKey;
import org.apache.commons.inject.api.IPoint;
import org.apache.commons.inject.api.IProvider;
import org.apache.commons.inject.api.Key;
import org.apache.commons.inject.impl.bind.DefaultBinding;
import org.apache.commons.inject.util.Generics;

public class Introspector {
	private static final Introspector introspector = new Introspector();
	private static final Class<Object> PROVIDER_CLASS = Generics.cast(Provider.class);

	// Private constructor, to avoid accidental instantiation.
	private Introspector() {
	}

	public static Introspector getInstance() {
		return introspector;
	}

	public <T> AbstractBaseProvider<T> getProvider(Class<T> pType, IPoint<T> pPoint, IMutableBindingSource pBindings) {
		@SuppressWarnings("unchecked")
		final Constructor<T>[] constructors = (Constructor<T>[]) pType.getDeclaredConstructors();
		for (Constructor<T> constructor : constructors) {
			if (constructor.isAnnotationPresent(Inject.class)) {
				return getProvider(constructor, pBindings);
			}
		}
		return new DefaultProvider<T>(pType, pPoint);
	}

	public <T> AbstractBaseProvider<T> getProvider(Constructor<? extends T> pConstructor, IMutableBindingSource pBindings) {
		@SuppressWarnings("unchecked")
		final Class<Object>[] parameterClasses = (Class<Object>[]) pConstructor.getParameterTypes();
		final Type[] parameterTypes = pConstructor.getGenericParameterTypes();
		final Annotation[][] parameterAnnotations = pConstructor.getParameterAnnotations();
		final IBinding<Object>[] parameterBindings = getBindings(parameterClasses, parameterTypes,
				parameterAnnotations, pBindings,
				"Required to invoke the constructor " + pConstructor);
		@SuppressWarnings("unchecked")
		final Constructor<T> constructor = (Constructor<T>) pConstructor;
		return new FactoryMethodProvider<T>(constructor,
											getPoint(constructor.getDeclaringClass(), pBindings),
											parameterBindings);
	}

	public <T> AbstractBaseProvider<T> getProvider(Method pMethod, IMutableBindingSource pBindings) {
		@SuppressWarnings("unchecked")
		final Class<Object>[] parameterClasses = (Class<Object>[]) pMethod.getParameterTypes();
		final Type[] parameterTypes = pMethod.getGenericParameterTypes();
		final Annotation[][] parameterAnnotations = pMethod.getParameterAnnotations();
		final IBinding<Object>[] parameterBindings = getBindings(parameterClasses, parameterTypes, parameterAnnotations, pBindings,
			"Required to invoke the method " + pMethod);
		@SuppressWarnings("unchecked")
		final Class<T> cl = (Class<T>) pMethod.getReturnType();
		final IPoint<T> point = getPoint(cl, pBindings);
		return new FactoryMethodProvider<T>(pMethod, parameterBindings, point);
	}

	private IBinding<Object>[] getBindings(Class<Object>[] pParameterClasses, Type[] pParameterTypes, Annotation[][] pParameterAnnotations, IMutableBindingSource pBindings,
			String pCause) {
		@SuppressWarnings("unchecked")
		final IBinding<Object>[] bindings = (IBinding<Object>[]) Array.newInstance(IBinding.class, pParameterTypes.length);
		for (int i = 0;  i < bindings.length;  i++) {
			final Class<Object> cl = pParameterClasses[i];
			final Annotation[] annotations = pParameterAnnotations[i];
			bindings[i] = getBinding(cl, pParameterTypes[i], annotations, pBindings, pCause);
		}
		return bindings;
	}

	private IBinding<Object> getBinding(Class<Object> pClass, Type pType, Annotation[] pAnnotations, IMutableBindingSource pBindings, String pCause) {
		String name = Key.NO_NAME;
		for (Annotation annotation : pAnnotations) {
			if (annotation instanceof Named) {
				name = ((Named) annotation).value();
				break;
			}
		}
		if (pClass == PROVIDER_CLASS  &&  pType != null  &&  pType instanceof ParameterizedType) {
			final ParameterizedType ptype = (ParameterizedType) pType;
			final Type[] typeArgs = ptype.getActualTypeArguments();
			if (typeArgs != null  &&  typeArgs.length == 1) {
				final Type typeArg = typeArgs[0];
				if (typeArg instanceof Class<?>) {
					@SuppressWarnings("unchecked")
					final Class<Object> cl = (Class<Object>) typeArg;
					final IKey<Object> key = new Key<Object>(cl, name);
					final IBinding<Object> binding1 = pBindings.requireBinding(key, pCause);
					final IProvider<Object> provider = new IProvider<Object>(){
						@Override
						public Object get() {
							return new Provider<Object>(){
								@Override
								public Object get() {
									return binding1.getProvider().get();
								}
							};
						}

						@Override
						public Class<? extends Object> getType() {
							return Provider.class;
						}

						@Override
						public Object get(IInjector pInjector) {
							return get();
						}
					};
					final IPoint<Object> point = new IPoint<Object>(){
						@Override
						public void injectTo(Object pInstance,
								IInjector pInjector) {
							// Does nothing.
						}
					};
					final IBinding<Object> binding2 = new DefaultBinding<Object>(provider, point);
					return binding2;
				}
			}
		}
		final IKey<Object> key = new Key<Object>(pClass, name);
		return pBindings.requireBinding(key, pCause);
	}

	public <T> ListPoint<T> getPoint(Class<T> pType, IMutableBindingSource pBindings) {
		final List<IPoint<T>> points = new ArrayList<IPoint<T>>();
		final Field[] fields = pType.getDeclaredFields();
		for (final Field f : fields) {
			if (Modifier.isStatic(f.getModifiers())) {
				continue;
			}
			if (!f.isAnnotationPresent(Inject.class)) {
				continue;
			}
			@SuppressWarnings("unchecked")
			Class<Object> type = (Class<Object>) f.getType();
			Type genericType = f.getGenericType();
			IBinding<Object> binding = null;
			Annotation[] annotations = f.getAnnotations();
			binding = getBinding(type, genericType, annotations, pBindings,
					"Required to inject to an instance of " + pType.getName());
			final IPoint<T> point = new FieldPoint<T>(binding, f);
			points.add(point);
		}
		final Method[] methods = pType.getDeclaredMethods();
		for (final Method m : methods) {
			if (Modifier.isStatic(m.getModifiers())) {
				continue;
			}
			if (!m.isAnnotationPresent(Inject.class)) {
				continue;
			}
			@SuppressWarnings("unchecked")
			Class<Object>[] parameterClasses = (Class<Object>[]) m.getParameterTypes();
			Type[] parameterTypes = m.getGenericParameterTypes();
			Annotation[][] annotations = m.getParameterAnnotations();
			final IBinding<Object>[] bindings = getBindings(parameterClasses, parameterTypes, annotations, pBindings,
					"Required to inject to an instance of " + pType.getName());
			final IPoint<T> point = new MethodPoint<T>(bindings, m);
			points.add(point);
		}
		return new ListPoint<T>(points);
	}
}

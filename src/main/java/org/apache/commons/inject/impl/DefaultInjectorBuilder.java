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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.inject.api.IBinding;
import org.apache.commons.inject.api.IInjector;
import org.apache.commons.inject.api.IInjectorBuilder;
import org.apache.commons.inject.api.IKey;
import org.apache.commons.inject.api.IPoint;
import org.apache.commons.inject.api.IProvider;
import org.apache.commons.inject.api.Key;
import org.apache.commons.inject.api.bind.IAnnotatedBindingBuilder;
import org.apache.commons.inject.api.bind.IBinder;
import org.apache.commons.inject.api.bind.ILinkedBindingBuilder;
import org.apache.commons.inject.api.bind.IModule;
import org.apache.commons.inject.api.bind.IBinder.IInjectionListener;
import org.apache.commons.inject.api.bind.IBinder.IInjectionParticipator;
import org.apache.commons.inject.api.bind.IBinder.IInjectorBuildListener;
import org.apache.commons.inject.impl.bind.DefaultBinding;
import org.apache.commons.inject.impl.bind.DefaultBindingBuilder;

public class DefaultInjectorBuilder implements IInjectorBuilder {
	private final Collection<IModule> modules;
	private final List<IInjectionListener> injectionListeners = new ArrayList<IInjectionListener>();
	private final List<IInjectionParticipator> injectionParticipators = new ArrayList<IInjectionParticipator>();
	private final List<IInjectorBuildListener> injectorBuildListeners = new ArrayList<IInjectorBuildListener>();

	public DefaultInjectorBuilder(Collection<IModule> pModules) {
		modules = pModules;
	}

	public DefaultInjectorBuilder() {
		modules = new ArrayList<IModule>();
	}

	public IInjector build() {
		final MutableBindingSet mutableBindings = new MutableBindingSet();
		configure(mutableBindings);
		final IKey<IInjector> key = new Key<IInjector>(IInjector.class);
		final BindingProxy<IInjector> bindingProxy = (BindingProxy<IInjector>) mutableBindings.requireBinding(key, "to create an automatic binding for an injector.");
		bindingProxy.setResolvedLater(true);
		final ResolvableBindingSet resolvableBindings = new ResolvableBindingSet(mutableBindings);
		resolve(resolvableBindings);
		final ImmutableBindingSet immutableBindings = new ImmutableBindingSet(resolvableBindings);
		final DefaultInjector injector = new DefaultInjector(immutableBindings);
		final IProvider<IInjector> provider = new IProvider<IInjector>(){
			@Override
			public IInjector get() {
				return injector;
			}

			@Override
			public Class<? extends IInjector> getType() {
				return injector.getClass();
			}

			@Override
			public IInjector get(IInjector pInjector) {
				return get();
			}
		};
		final IPoint<IInjector> point = new IPoint<IInjector>(){
			@Override
			public void injectTo(IInjector pInstance, IInjector pInjector) {
				// Does nothing.
			}
		};
		bindingProxy.setBinding(new DefaultBinding<IInjector>(provider, point));
		for (IBinding<?> binding : immutableBindings.getAllBindings()) {
			if (binding instanceof IInjectorAware) {
				((IInjectorAware) binding).init(injector);
			} else {
				throw new IllegalStateException("No InjectorAware: " + binding);
			}
		}
		for (IInjectorBuildListener listener : injectorBuildListeners) {
			listener.created(injector);
		}
		return injector;
	}

	protected void resolve(ResolvableBindingSet pBindings) {
		pBindings.resolve();
	}
	
	protected void configure(final MutableBindingSet mutableBindings) {
		final List<DefaultBindingBuilder<?>> builders = new ArrayList<DefaultBindingBuilder<?>>();
		final IBinder binder = newBinder(builders);
		for (IModule module : modules) {
			module.configure(binder);
		}
		for (DefaultBindingBuilder<?> builder : builders) {
			builder.build(mutableBindings, injectionListeners, injectionParticipators);
		}
	}

	private IBinder newBinder(final List<DefaultBindingBuilder<?>> builders) {
		return new IBinder(){
			@Override
			public <T> IAnnotatedBindingBuilder<T> bind(Class<T> pType) {
				final DefaultBindingBuilder<T> builder = new DefaultBindingBuilder<T>(pType);
				builders.add(builder);
				return builder;
			}

			@Override
			public <T> IAnnotatedBindingBuilder<T> bind(Class<T> pType,
					String pName) {
				final DefaultBindingBuilder<T> builder = new DefaultBindingBuilder<T>(pType, pName);
				builders.add(builder);
				return builder;
			}

			@Override
			public <T> ILinkedBindingBuilder<T> bind(IKey<T> pKey) {
				final DefaultBindingBuilder<T> builder = new DefaultBindingBuilder<T>(pKey);
				builders.add(builder);
				return builder;
			}

			@Override
			public boolean add(IInjectionParticipator pParticipator) {
				if (pParticipator == null) {
					throw new NullPointerException("The participator must not be null.");
				}
				return injectionParticipators.add(pParticipator);
			}

			@Override
			public boolean add(IInjectionListener pListener) {
				if (pListener == null) {
					throw new NullPointerException("The listener must not be null.");
				}
				return injectionListeners.add(pListener);
			}

			@Override
			public boolean add(IInjectorBuildListener pListener) {
				if (pListener == null) {
					throw new NullPointerException("The listener must not be null.");
				}
				return injectorBuildListeners.add(pListener);
			}
		};
	}

	@Override
	public IInjectorBuilder modules(IModule... pModules) {
		if (pModules == null) {
			throw new NullPointerException("The module list must not be null.");
		}
		modules.addAll(Arrays.asList(pModules));
		return this;
	}

	@Override
	public IInjectorBuilder modules(Collection<IModule> pModules) {
		if (pModules == null) {
			throw new NullPointerException("The module list must not be null.");
		}
		modules.addAll(pModules);
		return this;
	}
}

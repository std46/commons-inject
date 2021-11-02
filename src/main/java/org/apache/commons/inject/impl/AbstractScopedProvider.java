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

import org.apache.commons.inject.api.IInjector;
import org.apache.commons.inject.api.IProvider;

/**
 * Abstract base class for providers, which handle scopes.
 * The details of instantiation and value injection are left to the
 * {@link AbstractBaseProvider}.
 */
public abstract class AbstractScopedProvider<T> implements IProvider<T>, IInjectorAware {
	private final IProvider<T> baseProvider;
	private IInjector injector;
	protected boolean initialized;

	public AbstractScopedProvider(IProvider<T> pBaseProvider) {
		baseProvider = pBaseProvider;
	}
	
	protected IInjector getInjector() {
		if (injector == null) {
			throw new IllegalStateException("The Injector has not been initialized.");
		}
		return injector;
	}

	@Override
	public T get() {
		final T t = baseProvider.get(getInjector());
		return t;
	}

	@Override
	public Class<? extends T> getType() {
		return baseProvider.getType();
	}

	@Override
	public T get(IInjector pInjector) {
		return get();
	}

	@Override
	public void init(IInjector pInjector) {
		injector = pInjector;
		if (baseProvider instanceof IInjectorAware) {
			((IInjectorAware) baseProvider).init(pInjector);
		}
		initialized = true;
	}
}

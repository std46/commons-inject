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
import org.apache.commons.inject.api.IPoint;
import org.apache.commons.inject.api.IProvider;

/**
 * Abstract implementation of a base provider: In general, bindings are using
 * a {@link AbstractScopedProvider scoped provider}, which controls the bindings
 * scope ({@code When} is the instance created?) The actual instantiation, and
 * the injection of values, is delegated to the base provider. ({@code How} is
 * the instance created?)
 */
public abstract class AbstractBaseProvider<T> implements IProvider<T>, IInjectorAware {
	private final Class<T> type;
	private final IPoint<T> point;

	/**
	 * Creates a new base provider.
	 * @param pType The type of the instance, which is being created
	 * by the provider.
	 * @param pPoint The point, which is being used to inject values
	 * into the created instance.
	 */
	protected AbstractBaseProvider(Class<T> pType, IPoint<T> pPoint) {
		type = pType;
		point = pPoint;
	}

	@Override
	public T get(IInjector pInjector) {
		final T t = get();
		point.injectTo(t, pInjector);
		return t;
	}

	@Override
	public Class<T> getType() {
		return type;
	}

	@Override
	public void init(IInjector pInjector) {
		if (point instanceof IInjectorAware) {
			((IInjectorAware) point).init(pInjector);
		}
	}
}

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

import org.apache.commons.inject.api.IBinding;
import org.apache.commons.inject.api.IInjector;
import org.apache.commons.inject.api.IPoint;
import org.apache.commons.inject.api.IProvider;
import org.apache.commons.inject.impl.IInjectorAware;

/**
 * Default implementation of {@link IBinding}; basically a simple
 * wrapper for instances of {@link IProvider}, and {@link IPoint}.
 */
public class DefaultBinding<T> implements IBinding<T>, IInjectorAware {
	private final IProvider<T> provider;
	private final IPoint<T> point;
	
	public DefaultBinding(IProvider<T> pProvider, IPoint<T> pPoint) {
		super();
		provider = pProvider;
		point = pPoint;
	}

	@Override
	public IProvider<T> getProvider() {
		return provider;
	}

	@Override
	public IPoint<T> getPoint() {
		return point;
	}

	@Override
	public void init(IInjector pInjector) {
		if (provider instanceof IInjectorAware) {
			((IInjectorAware) provider).init(pInjector);
		}
		if (point instanceof IInjectorAware) {
			((IInjectorAware) point).init(pInjector);
		}
	}

}

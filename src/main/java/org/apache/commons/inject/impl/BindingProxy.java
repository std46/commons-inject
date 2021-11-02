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

import org.apache.commons.inject.api.IBinding;
import org.apache.commons.inject.api.IInjector;
import org.apache.commons.inject.api.IPoint;
import org.apache.commons.inject.api.IProvider;

public class BindingProxy<T> implements IBinding<T>, IInjectorAware {
	private IBinding<T> binding;
	private boolean initialized;
	private boolean isResolvedLater;
	private final String cause;
	private final RuntimeException rte = new RuntimeException();

	public BindingProxy(String pCause) {
		cause = pCause;
	}

	public boolean isResolvedLater() {
		return isResolvedLater;
	}

	public void setResolvedLater(boolean pResolvedLater) {
		isResolvedLater = pResolvedLater;
	}
	
	@Override
	public synchronized IProvider<T> getProvider() {
		if (!initialized) {
			throw new IllegalStateException("This Binding hasn't been initialized.", rte);
		}
		return binding.getProvider();
	}

	@Override
	public IPoint<T> getPoint() {
		if (!initialized) {
			throw new IllegalStateException("This Binding hasn't been initialized.");
		}
		return binding.getPoint();
	}

	public synchronized void setBinding(IBinding<T> pBinding) {
		if (pBinding == null) {
			throw new NullPointerException("The proxied binding must not be null.");
		}
		binding = pBinding;
		initialized = true;
	}

	public String getCause() {
		return cause;
	}

	@Override
	public void init(IInjector pInjector) {
		if (binding != null  &&  binding instanceof IInjectorAware) {
			((IInjectorAware) binding).init(pInjector);
		}
	}
}

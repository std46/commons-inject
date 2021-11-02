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

import java.lang.reflect.Method;

import org.apache.commons.inject.api.IBinding;
import org.apache.commons.inject.api.IInjector;
import org.apache.commons.inject.api.IPoint;
import org.apache.commons.inject.util.Exceptions;

public class MethodPoint<T> implements IPoint<T>, IInjectorAware {
	private final IBinding<Object>[] bindings;
	private final Method method;

	public MethodPoint(IBinding<Object>[] pBindings, Method pMethod) {
		bindings = pBindings;
		method = pMethod;
	}

	@Override
	public void injectTo(T pInstance, IInjector pInjector) {
		try {
			final Object[] args = new Object[bindings.length];
			for (int i = 0;  i < args.length;  i++) {
				args[i] = bindings[i].getProvider().get();
			}
			if (!method.isAccessible()) {
				method.setAccessible(true);
			}
			method.invoke(pInstance, args);
		} catch (Throwable t) {
			throw Exceptions.show(t);
		}
	}

	@Override
	public void init(IInjector pInjector) {
		for (IBinding<Object> binding : bindings) {
			if (binding instanceof IInjectorAware) {
				((IInjectorAware) binding).init(pInjector);
			}
		}
	}
}

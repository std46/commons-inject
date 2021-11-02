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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.apache.commons.inject.api.IBinding;
import org.apache.commons.inject.api.IInjector;
import org.apache.commons.inject.api.IPoint;
import org.apache.commons.inject.util.Exceptions;

public class FactoryMethodProvider<T> extends AbstractBaseProvider<T> implements IInjectorAware {
	private final Constructor<T> constructor;
	private final Method method;
	private final IBinding<Object>[] parameterBindings;

	public FactoryMethodProvider(Constructor<T> pConstructor, IPoint<T> pPoint, IBinding<Object>[] pBindings) {
		super(pConstructor.getDeclaringClass(), pPoint);
		constructor = pConstructor;
		method = null;
		parameterBindings = pBindings;
	}

	@SuppressWarnings("unchecked")
	public FactoryMethodProvider(Method pMethod, IBinding<Object>[] pBindings, IPoint<T> pPoint) {
		super((Class<T>) pMethod.getReturnType(), pPoint);
		constructor = null;
		method = pMethod;
		parameterBindings = pBindings;
		
	}

	@Override
	public T get() {
		try {
			final Object[] parameters = new Object[parameterBindings.length];
			for (int i = 0;  i < parameters.length;  i++) {
				parameters[i] = parameterBindings[i].getProvider().get();
			}
			if (constructor == null) {
				@SuppressWarnings("unchecked")
				final T instance = (T) method.invoke(null, parameters);
				return instance;
			} else {
				if (!constructor.isAccessible()) {
					constructor.setAccessible(true);
				}
				return constructor.newInstance(parameters);
			}
		} catch (Throwable t) {
			throw Exceptions.show(t);
		}
	}

	@Override
	public void init(IInjector pInjector) {
		for (IBinding<Object> binding : parameterBindings) {
			if (binding instanceof IInjectorAware) {
				((IInjectorAware) binding).init(pInjector);
			}
		}
	}
}

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

import java.lang.reflect.Field;

import org.apache.commons.inject.api.IBinding;
import org.apache.commons.inject.api.IInjector;
import org.apache.commons.inject.api.IPoint;
import org.apache.commons.inject.util.Exceptions;

public class FieldPoint<T> implements IPoint<T>, IInjectorAware {
	private final IBinding<Object> binding;
	private final Field f;

	FieldPoint(IBinding<Object> pBinding, Field pF) {
		binding = pBinding;
		f = pF;
	}

	@Override
	public void injectTo(T pInstance, IInjector pInjector) {
		try {
			if (!f.isAccessible()) {
				f.setAccessible(true);
			}
			final Object value = binding.getProvider().get();
			f.set(pInstance, value);
		} catch (Throwable t) {
			throw Exceptions.show(t);
		}
	}

	@Override
	public void init(IInjector pInjector) {
		if (binding instanceof IInjectorAware) {
			((IInjectorAware) binding).init(pInjector);
		}
	}
}
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
import org.apache.commons.inject.api.IKey;
import org.apache.commons.inject.api.Key;
import org.apache.commons.inject.api.NoSuchBindingException;

/**
 * Abstract implementation of an {@link IInjector injector}.
 */
public abstract class AbstractInjector implements IInjector {
	protected abstract <T> IBinding<T> getBinding(IKey<T> pKey);
	protected abstract <T> IBinding<T> requireBinding(IKey<T> pKey);

	@Override
	public <T> T getInstance(Class<T> pType) {
		final IKey<T> key = new Key<T>(pType);
		return getInstance(key);
	}

	@Override
	public <T> T getInstance(Class<T> pType, String pName) {
		final IKey<T> key = new Key<T>(pType, pName);
		return getInstance(key);
	}

	@Override
	public <T> T getInstance(IKey<T> pKey) {
		final IBinding<T> binding = getBinding(pKey);
		if (binding == null) {
			return null;
		} else {
			return binding.getProvider().get();
		}
	}

	@Override
	public <T> T requireInstance(Class<T> pType) throws NoSuchBindingException {
		final IKey<T> key = new Key<T>(pType);
		return requireInstance(key);
	}

	@Override
	public <T> T requireInstance(Class<T> pType, String pName)
			throws NoSuchBindingException {
		final IKey<T> key = new Key<T>(pType, pName);
		return requireInstance(key);
	}

	@Override
	public <T> T requireInstance(IKey<T> pKey) throws NoSuchBindingException {
		final IBinding<T> binding = getBinding(pKey);
		if (binding == null) {
			throw new NoSuchBindingException("No binding registered for key: " + pKey);
		} else {
			return binding.getProvider().get();
		}
	}

	@Override
	public void injectMembers(Object pInstance) {
		if (pInstance == null) {
			throw new NullPointerException("The instance must not be null.");
		}
		@SuppressWarnings("unchecked")
		final Class<Object> cl = (Class<Object>) pInstance.getClass();
		final IKey<Object> key = new Key<Object>(cl);
		IBinding<Object> binding = requireBinding(key);
		binding.getPoint().injectTo(pInstance, this);
	}
}

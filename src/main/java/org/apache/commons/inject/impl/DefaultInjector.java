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
import org.apache.commons.inject.api.IPoint;
import org.apache.commons.inject.api.IProvider;
import org.apache.commons.inject.api.NoSuchBindingException;
import org.apache.commons.inject.impl.AbstractBindingSet.MappedKey;
import org.apache.commons.inject.impl.AbstractBindingSet.ReducedKey;
import org.apache.commons.inject.impl.bind.DefaultBinding;

/**
 * Default implementation of an {@link IInjector injector}.
 */
public class DefaultInjector extends AbstractInjector {
	private final ImmutableBindingSet bindings;
	
	public DefaultInjector(ImmutableBindingSet pBindings) {
		bindings = pBindings;
	}

	protected <T> IBinding<T> getBinding(IKey<T> pKey) {
		synchronized(bindings) {
			return bindings.getBinding(pKey);
		}
	}
	
	protected <T> IBinding<T> requireBinding(IKey<T> pKey) {
		synchronized(bindings) {
			IBinding<T> binding;
			binding = bindings.getBinding(pKey);
			if (binding == null) {
				final IMutableBindingSource bindingSource = getBindingSource();
				final Class<T> cl = pKey.getType();
				final IPoint<T> point = Introspector.getInstance().getPoint(cl, bindingSource);
				final IProvider<T> provider = Introspector.getInstance().getProvider(cl, point, bindingSource);
				binding = new DefaultBinding<T>(provider, point);
				final ReducedKey<T> rkey = new ReducedKey<T>(cl, pKey.getName());
				final MappedKey<T> key = new MappedKey<T>(cl, pKey.getName(), null, null);
				bindings.add(rkey, key, binding);
			}
			return binding;
		}
	}

	protected IMutableBindingSource getBindingSource() {
		return new IMutableBindingSource() {
			@Override
			public <T> IBinding<T> requireBinding(IKey<T> pKey, String pCause) {
				final IBinding<T> binding = bindings.getBinding(pKey);
				if (binding == null) {
					throw new NoSuchBindingException("No binding registered for key: " + pKey);
				}
				return binding;
			}
			
		};
	}
	
}

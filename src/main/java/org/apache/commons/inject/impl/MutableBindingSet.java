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

import java.util.List;

import org.apache.commons.inject.api.IBinding;
import org.apache.commons.inject.api.IKey;


public class MutableBindingSet extends AbstractBindingSet implements IMutableBindingSource {
	public MutableBindingSet() {
		super();
	}

	public <T> void add(MappedKey<T> pKey, IBinding<T> pBinding) {
		final ReducedKey<T> rkey = newReducedKey(pKey);
		final List<BindingAndKey<?>> list = findOrCreateList(rkey);
		list.add(new BindingAndKey<T>(pBinding, pKey));
	}

	@Override
	public <T> IBinding<T> requireBinding(IKey<T> pKey, String pCause) {
		final MappedKey<T> mkey = new MappedKey<T>(pKey.getType(), pKey.getName(), pKey.getAnnotations(), null);
		final ReducedKey<T> rkey = newReducedKey(pKey);
		final List<BindingAndKey<?>> list = findOrCreateList(rkey);
		for (BindingAndKey<?> bak : list) {
			if (isMatching(pKey, bak.getKey())) {
				@SuppressWarnings("unchecked")
				final IBinding<T> binding = (IBinding<T>) bak.getBinding();
				return binding;
			}
		}
		final IBinding<T> binding = new BindingProxy<T>(pCause);
		list.add(new BindingAndKey<T>(binding, mkey));
		return binding;
	}
}

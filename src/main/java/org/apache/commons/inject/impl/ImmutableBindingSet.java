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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.inject.api.IBinding;
import org.apache.commons.inject.api.IKey;

public class ImmutableBindingSet extends AbstractBindingSet {
	public ImmutableBindingSet(ResolvableBindingSet pResolvableBindings) {
		super(pResolvableBindings.map);
	}

	<T> IBinding<T> getBinding(IKey<T> pKey) {
		final ReducedKey<T> rkey = new ReducedKey<T>(pKey.getType(), pKey.getName());
		final List<BindingAndKey<?>> list = map.get(rkey);
		if (list != null) {
			for (BindingAndKey<?> bak : list) {
				if (isMatching(pKey, bak.getKey())) {
					@SuppressWarnings("unchecked")
					final IBinding<T> binding = (IBinding<T>) bak.getBinding();
					return binding;
				}
			}
		}
		return null;
	}

	<T> void add(ReducedKey<T> pRKey, MappedKey<T> pMKey, IBinding<T> pBinding) {
		final List<BindingAndKey<?>> list = findOrCreateList(pRKey);
		final BindingAndKey<T> bak = new BindingAndKey<T>(pBinding, pMKey);
		list.add(bak);
	}

	Iterable<IBinding<?>> getAllBindings() {
		final List<IBinding<?>> list = new ArrayList<IBinding<?>>();
		for (Map.Entry<ReducedKey<?>,List<BindingAndKey<?>>> en : map.entrySet()) {
			final List<BindingAndKey<?>> baklist = en.getValue();
			for (BindingAndKey<?> bak : baklist) {
				list.add(bak.getBinding());
			}
		}
		return list;
	}
}

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
import java.util.Map;

import org.apache.commons.inject.api.IBinding;
import org.apache.commons.inject.api.Key;
import org.apache.commons.inject.api.NoSuchBindingException;

public class ResolvableBindingSet extends AbstractBindingSet {
	public ResolvableBindingSet(MutableBindingSet pMutableBindings) {
		super(pMutableBindings.map);
	}

	public void resolve() {
		for (Map.Entry<ReducedKey<?>, List<BindingAndKey<?>>> en : map.entrySet()) {
			List<BindingAndKey<?>> list = en.getValue();
			for (BindingAndKey<?> bak : list) {
				final IBinding<?> binding = bak.getBinding();
				if (binding instanceof BindingProxy) {
					@SuppressWarnings("unchecked")
					final BindingProxy<Object> bnd = (BindingProxy<Object>) binding;
					if (bnd.isResolvedLater()) {
						continue;
					}
					@SuppressWarnings("unchecked")
					final IBinding<Object> realBinding = (IBinding<Object>) findRealBinding(list, bak.getKey());
					if (realBinding == null) {
						throw new NoSuchBindingException("No Binding has been registered for key "
								+ Key.toString(bak.getKey()) + ". " + ((BindingProxy<?>) binding).getCause());
					}
					bnd.setBinding(realBinding);
				}
			}
		}
	}

	private IBinding<?> findRealBinding(List<BindingAndKey<?>> pList,
			MappedKey<?> pKey) {
		for (BindingAndKey<?> bak : pList) {
			if (bak.getKey() == pKey) {
				continue;
			}
			if (!isMatching(bak.getKey(), pKey)) {
				continue;
			}
			return bak.getBinding();
		}
		return null;
	}
}

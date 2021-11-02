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

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.inject.api.IBinding;
import org.apache.commons.inject.api.IKey;
import org.apache.commons.inject.api.Key;

/**
 * A set of bindings, which are being collected to create, or implement
 * an {@link IInjector}.
 */

public class AbstractBindingSet {
	/**
	 * The internal map of bindings uses this key.
	 */
	public static class MappedKey<T> extends Key<T> {
		private final Class<? extends Annotation> annotationType;
		public MappedKey(Class<T> pType, String pName,
				         Annotation[] pAnnotations,
				         Class<? extends Annotation> pAnnotationType) {
			super(pType, pName, pAnnotations);
			annotationType = pAnnotationType;
		}

		public Class<? extends Annotation> getAnnotationType() {
			return annotationType;
		}
	}
	/**
	 * The internal map of bindings uses this value.
	 */
	protected static class BindingAndKey<T> {
		private IBinding<T> binding;
		private final MappedKey<T> key;

		BindingAndKey(IBinding<T> pBinding, MappedKey<T> pKey) {
			binding = pBinding;
			key = pKey;
		}

		public IBinding<T> getBinding() {
			return binding;
		}

		public void setBinding(IBinding<T> pBinding) {
			binding = pBinding;
		}

		public MappedKey<T> getKey() {
			return key;
		}
	}
	protected static class ReducedKey<T> {
		private final Class<T> type;
		private final String name;
		ReducedKey(Class<T> pType, String pName) {
			type = pType;
			name = pName;
		}
		public Class<T> getType() {
			return type;
		}
		public String getName() {
			return name;
		}
		@Override
		public int hashCode() {
			return 31 * (31 + name.hashCode()) + type.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ReducedKey<?> other = (ReducedKey<?>) obj;
			return getType() == other.getType() &&  getName().equals(other.getName());
		}

		
	}

	protected final Map<ReducedKey<?>, List<BindingAndKey<?>>> map;

	protected AbstractBindingSet(Map<ReducedKey<?>, List<BindingAndKey<?>>> pMap) {
		map = pMap;
	}

	protected AbstractBindingSet() {
		this(new HashMap<ReducedKey<?>, List<BindingAndKey<?>>>());
	}


	protected <T> ReducedKey<T> newReducedKey(IKey<T> pKey) {
		return new ReducedKey<T>(pKey.getType(), pKey.getName());
	}

	protected List<BindingAndKey<?>> findOrCreateList(ReducedKey<?> pKey) {
		List<BindingAndKey<?>> list = map.get(pKey);
		if (list == null) {
			list = new ArrayList<BindingAndKey<?>>();
			map.put(pKey, list);
		}
		return list;
	}

	protected boolean isMatching(IKey<?> pSearchKey, MappedKey<?> pMapKey) {
		// No need to compare type and name. They are matching, because
		// we did a lookup with a ReducedKey to find the list of
		// bindings and keys, from which pMapKey was taken.
		if (!hasAnnotations(pMapKey.getAnnotations(), pSearchKey)
			||  !hasAnnotations(pSearchKey.getAnnotations(), pMapKey)) {
			return false;
		}
		final Class<? extends Annotation> mappedAnnotationType = pMapKey.getAnnotationType();
		if (mappedAnnotationType != null) {
			boolean found = false;
			for (Annotation searchAnnotation : pSearchKey.getAnnotations()) {
				if (searchAnnotation != null  &&  mappedAnnotationType == searchAnnotation.getClass()) {
					found = true;
					break;
				}
			}
			if (!found) {
				return false;
			}
			
		}
		return true;
	}

	private boolean hasAnnotations(Annotation[] pAnnotations, IKey<?> pKey) {
		for (Annotation annotation : pAnnotations) {
			boolean found = false;
			for (Annotation ann : pKey.getAnnotations()) {
				if (annotation.equals(ann)) {
					found = true;
					break;
				}
			}
			if (!found) {
				return false;
			}
		}
		return true;
	}
}

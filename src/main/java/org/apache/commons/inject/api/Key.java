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
package org.apache.commons.inject.api;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;

/**
 * Default implementation of {@link IKey}.
 */
public class Key<T> implements IKey<T> {
	public static final String NO_NAME = "";
	public static final Annotation[] NO_ANNOTATIONS = (Annotation[]) Array.newInstance(Annotation.class, 0);
	private final Class<T> type;
	private final String name;
	private final Annotation[] annotations;

	public Key(Class<T> pType, String pName, Annotation[] pAnnotations) {
		if (pType == null) {
			throw new NullPointerException("The keys type must not be null.");
		}
		this.type = pType;
		if (pName == null) {
			name = NO_NAME;
		} else {
			name = pName;
		}
		if (pAnnotations == null) {
			annotations = NO_ANNOTATIONS;
		} else {
			annotations = pAnnotations;
		}
	}

	public Key(Class<T> pType, String pName) {
		this(pType, pName, NO_ANNOTATIONS);
	}

	public Key(Class<T> pType) {
		this(pType, NO_NAME, NO_ANNOTATIONS);
	}

	@Override
	public Class<T> getType() {
		return type;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Annotation[] getAnnotations() {
		return annotations;
	}

	@Override
	public String toString() {
		return toString(this);
	}

	public static String toString(IKey<?> pKey) {
		final StringBuilder sb = new StringBuilder();
		sb.append("Type=");
		sb.append(pKey.getType().getName());
		final String name = pKey.getName();
		if (name.length() > 0) {
			sb.append(", Name=");
			sb.append(name);
		}
		final Annotation[] annotations = pKey.getAnnotations();
		if (annotations.length > 0) {
			sb.append(", Annotations=[");
			for (int i = 0;  i < annotations.length;  i++) {
				if (i > 0) {
					sb.append(",");
				}
				sb.append(annotations[i]);
			}
			sb.append("]");
		}
		return sb.toString();
	}
}

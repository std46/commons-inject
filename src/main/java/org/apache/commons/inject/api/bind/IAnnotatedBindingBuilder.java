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
package org.apache.commons.inject.api.bind;

import java.lang.annotation.Annotation;

/**
 * A binding builder, which allows to specify annotations, or annotation
 * types as constraints, depending on which POJO's may, or may not be
 * injected.
 */
public interface IAnnotatedBindingBuilder<T> extends ILinkedBindingBuilder<T> {
	/**
	 * Specifies, that the binding can only be used for injection, if a
	 * field is annotated with an {@link Annotation}, that equals the
	 * given. In general, this means that the annotation type, and all
	 * attributes are equal.
	 * @see Annotation#equals(Object)
	 */
	ILinkedBindingBuilder<T> annotatedWith(Annotation pAnnotation);
	/**
	 * Specifies, that the binding can only be used for injection, if a
	 * field is annotated with an {@link Annotation} of the given type.
	 */
	ILinkedBindingBuilder<T> annotatedWith(Class<? extends Annotation> pAnnotation);
}

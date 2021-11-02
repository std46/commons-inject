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

/**
 * List of the supported scopes.
 */
public enum Scopes {
	/**
	 * A binding with scope {@code PER_CALL} will create a new instance,
	 * whenever the binding is used to inject a value.
	 */
	PER_CALL,
	/**
	 * A binding with scope {@code EAGER_SINGLETON} will create a single instance,
	 * which is injected whenever the binding is used to inject a value. The
	 * instance will be created immediately.
	 */
	EAGER_SINGLETON,
	/**
	 * A binding with scope {@code EAGER_SINGLETON} will create a single instance,
	 * which is injected whenever the binding is used to inject a value. The
	 * instance will be created as soon as required.
	 */
	LAZY_SINGLETON
}

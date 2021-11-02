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
package org.apache.commons.inject.util;


/**
 * A utility class for dealing with generics.
 */
public class Generics {
	/** Casts the parameter into the given result type.
	 * {@em Warning:} Using this method is inheritly unsafe, because it's
	 * tricking out the compiler. (Use it at your own risk.) On the other
	 * hand, it is an effective way to omit unnecessary compiler warnings.
	 */
	public static <T> T cast(Object pObject) {
		@SuppressWarnings("unchecked")
		final T t = (T) pObject;
		return t;
	}
}

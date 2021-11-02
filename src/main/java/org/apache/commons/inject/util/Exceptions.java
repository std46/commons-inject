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

import java.lang.reflect.UndeclaredThrowableException;


/**
 * A utility class for dealing with Exceptions.
 */
public class Exceptions {
	/**
	 * Throws the given exception, or another exception, which doesn't affect
	 * the method signature.
	 * @param pTh The {@link Throwable} to show. If this is an instance of
	 *   {@link RuntimeException}, or {@link Error}, then this Throwable itself
	 *   will be thrown. Otherwise, the Throwable will be wrapped into an instance
	 *   of {@link UndeclaredThrowableException}, and that will be thrown.
	 * @return Nothing, an exception will always be thrown: This method is
	 *   effectively void. To declare it otherwise makes it possible to write
	 *   {@code throw show(myThrowable);}, which allows the compiler to detect
	 *   what's happening.
	 */
	public static RuntimeException show(Throwable pTh) {
		if (pTh == null) {
			return new NullPointerException("The Throwable to show must not be null.");
		} else if (pTh instanceof RuntimeException) {
			return (RuntimeException) pTh;
		} else if (pTh instanceof Error) {
			throw (Error) pTh;
		} else {
			return new UndeclaredThrowableException(pTh);
		}
	}

	public static <E extends Throwable> RuntimeException show(Throwable pTh, Class<E> pClass) throws E{
		if (pTh == null) {
			return new NullPointerException("The Throwable to show must not be null.");
		} else if (pTh instanceof RuntimeException) {
			return (RuntimeException) pTh;
		} else if (pTh instanceof Error) {
			throw (Error) pTh;
		} else if (pClass.isAssignableFrom(pTh.getClass())) {
			throw pClass.cast(pTh);
		} else {
			return new UndeclaredThrowableException(pTh);
		}
	}
}

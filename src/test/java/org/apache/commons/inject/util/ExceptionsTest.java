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

import static org.junit.Assert.*;

import java.lang.reflect.UndeclaredThrowableException;

import org.junit.Assert;
import org.junit.Test;

public class ExceptionsTest {
	@Test
	public void testShowRTE() {
		final NullPointerException npe = new NullPointerException();
		Throwable th = null;
		try {
			throw Exceptions.show(npe);
		} catch (Throwable t) {
			th = t;
		}
		Assert.assertNotNull(th);
		Assert.assertSame(npe, th);
	}

	@Test
	public void testShowError() {
		final OutOfMemoryError oome = new OutOfMemoryError();
		Throwable th = null;
		try {
			throw Exceptions.show(oome);
		} catch (Throwable t) {
			th = t;
		}
		Assert.assertNotNull(th);
		Assert.assertSame(oome, th);
	}

	@Test
	public void testShowThrowable() {
		final Throwable t0 = new Throwable("Some throwable"){
		};
		Throwable th = null;
		try {
			throw Exceptions.show(t0);
		} catch (UndeclaredThrowableException ute) {
			th = ute.getCause();
		}
		Assert.assertNotNull(th);
		Assert.assertSame(t0, th);	}
	
}

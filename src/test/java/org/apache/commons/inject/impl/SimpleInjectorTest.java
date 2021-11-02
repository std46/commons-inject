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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.inject.api.CommonsInject;
import org.apache.commons.inject.api.IInjector;
import org.apache.commons.inject.api.IKey;
import org.apache.commons.inject.api.Key;
import org.apache.commons.inject.api.NoSuchBindingException;
import org.apache.commons.inject.api.bind.IBinder;
import org.apache.commons.inject.api.bind.IModule;
import org.apache.commons.inject.api.bind.Scopes;
import org.junit.Assert;
import org.junit.Test;

public class SimpleInjectorTest {
	private static final List<Object> FOO_LIST = new ArrayList<Object>();
	private static final List<Object> BAR_LIST = new ArrayList<Object>();

	private IInjector newInjector() {
		final IModule module = new IModule(){
			@Override
			public void configure(IBinder pBinder) {
				pBinder.bind(List.class).to(ArrayList.class).scope(Scopes.PER_CALL);
				pBinder.bind(List.class, "foo").toInstance(FOO_LIST);
				pBinder.bind(List.class, "bar").toInstance(BAR_LIST);
				
			}
		};
		return CommonsInject.build(module);
	}

	@Test
	public void testSimpleInjector() throws Exception {
		final IInjector injector = newInjector();
		final List<?> fooList = injector.requireInstance(List.class, "foo");
		Assert.assertNotNull(fooList);
		Assert.assertSame(FOO_LIST, fooList);
		final List<?> barList = injector.requireInstance(List.class, "bar");
		Assert.assertNotNull(barList);
		Assert.assertSame(BAR_LIST, barList);
		final List<?> someList1 = injector.requireInstance(List.class);
		Assert.assertNotNull(someList1);
		Assert.assertNotSame(FOO_LIST, someList1);
		Assert.assertNotSame(BAR_LIST, someList1);
		final List<?> someList2 = injector.requireInstance(List.class);
		Assert.assertNotNull(someList2);
		Assert.assertNotSame(FOO_LIST, someList2);
		Assert.assertNotSame(BAR_LIST, someList2);
		Assert.assertNotSame(someList1, someList2);
	}

	@Test
	public void testAutomaticInjectorBinding() throws Exception {
		final IInjector injector1 = newInjector();
		final IInjector injector2 = injector1.requireInstance(IInjector.class);
		Assert.assertSame(injector1, injector2);
	}

	@Test
	public void testNoBinding() throws Exception {
		final IInjector injector = newInjector();
		{
			boolean haveException = false;
			try {
				injector.requireInstance(Map.class);
				Assert.fail("Expected exception");
			} catch (NoSuchBindingException e) {
				Assert.assertEquals("No binding registered for key: Type=java.util.Map", e.getMessage());
				haveException = true;
			}
			Assert.assertTrue(haveException);
		}
		{
			boolean haveException = false;
			try {
				injector.requireInstance(List.class, "foobar");
				Assert.fail("Expected exception");
			} catch (NoSuchBindingException e) {
				Assert.assertEquals("No binding registered for key: Type=java.util.List, Name=foobar", e.getMessage());
				haveException = true;
			}
			Assert.assertTrue(haveException);
		}
		{
			boolean haveException = false;
			try {
				final Annotation[] annotations = new Annotation[]{getTestAnnotation()};
				@SuppressWarnings("rawtypes")
				final IKey<List> key = new Key<List>(List.class, "foo", annotations);
				injector.requireInstance(key);
				Assert.fail("Expected exception");
			} catch (NoSuchBindingException e) {
				Assert.assertTrue(e.getMessage().startsWith("No binding registered for key: Type=java.util.List, Name=foo, Annotations=["));
				haveException = true;
			}
			Assert.assertTrue(haveException);
		}
	}
	
	private Annotation getTestAnnotation() throws Exception {
		final Class<?> cl = SimpleInjectorTest.class;
		final Method method = cl.getMethod("testScopes");
		return method.getAnnotation(Test.class);
	}

	@Test
	public void testScopes() throws Exception {
		final IModule module = new IModule(){
			@Override
			public void configure(IBinder pBinder) {
				pBinder.bind(TimeRecordingObject.class, "eager").asEagerSingleton();
				pBinder.bind(TimeRecordingObject.class, "lazy").asLazySingleton();
			}
		};
		long time0 = System.currentTimeMillis();
		final IInjector injector = CommonsInject.build(module);
		long time1 = System.currentTimeMillis();
		final TimeRecordingObject lazy0 = injector.getInstance(TimeRecordingObject.class, "lazy");
		final TimeRecordingObject lazy1 = injector.getInstance(TimeRecordingObject.class, "lazy");
		final TimeRecordingObject eager0 = injector.getInstance(TimeRecordingObject.class, "eager");
		final TimeRecordingObject eager1 = injector.getInstance(TimeRecordingObject.class, "eager");
		long time2 = System.currentTimeMillis();
		Assert.assertSame(eager0, eager1);
		Assert.assertSame(lazy0, lazy1);
		long eager0Time = eager0.getTimeOfCreation();
		long lazy0Time = lazy0.getTimeOfCreation();
		Assert.assertTrue(time0 <= eager0Time &&  eager0Time <= time1 &&  eager0Time <= time2);
		Assert.assertTrue(time0 <= lazy0Time &&  time1 <= lazy0Time &&  lazy0Time <= time2);
	}
}

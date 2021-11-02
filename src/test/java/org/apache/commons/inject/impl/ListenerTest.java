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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import junit.framework.Assert;

import org.apache.commons.inject.api.CommonsInject;
import org.apache.commons.inject.api.IInjector;
import org.apache.commons.inject.api.IKey;
import org.apache.commons.inject.api.ILifecycleController;
import org.apache.commons.inject.api.PostConstructModule;
import org.apache.commons.inject.api.bind.IBinder;
import org.apache.commons.inject.api.bind.IModule;
import org.apache.commons.inject.api.bind.Scopes;
import org.junit.Test;

public class ListenerTest {
	private static class ListenerModule implements IModule {
		private boolean injectorBuilListenerCalled;
		private final List<Object> initializedObjects  = new ArrayList<Object>();
		@Override
		public void configure(IBinder pBinder) {
			pBinder.add(new IBinder.IInjectorBuildListener() {
				@Override
				public void created(IInjector pInjector) {
					injectorBuilListenerCalled = true;
				}
			});
			pBinder.add(new IBinder.IInjectionListener() {
				@Override
				public void initialized(IKey<?> pKey, Object pObject) {
					initializedObjects.add(pObject);
				}
			});
			pBinder.bind(TimeRecordingObject.class, "perCall").scope(Scopes.PER_CALL);
			pBinder.bind(TimeRecordingObject.class, "lazy").asLazySingleton();
			pBinder.bind(TimeRecordingObject.class, "eager").asEagerSingleton();
		}

		boolean isInitialized() {
			return injectorBuilListenerCalled;
		}
		List<Object> getInitializedObjects(){
			return initializedObjects;
		}
	}
	public static class InitializableObject {
		private final long timeOfCreation = System.currentTimeMillis();
		private long timeOfInitialization, timeOfShutdown;
		private int state;

		@PostConstruct
		public void start() {
			state = 1;
			timeOfInitialization = System.currentTimeMillis();
		}

		@PreDestroy
		public void shutdown() {
			state = 2;
			timeOfShutdown = System.currentTimeMillis();
		}

		public void assertStarted() {
			Assert.assertTrue(state > 0);
			Assert.assertTrue(timeOfInitialization >= timeOfCreation);
		}

		public void assertTerminated() {
			assertStarted();
			Assert.assertTrue(state > 1);
			Assert.assertTrue(timeOfShutdown >= timeOfInitialization);
			
		}
	}

	@Test
	public void testListeners() {
		final ListenerModule module = new ListenerModule();
		final IInjector injector = CommonsInject.build(module);
		Assert.assertTrue(module.isInitialized());
		final Object perCall1 = injector.requireInstance(TimeRecordingObject.class, "perCall");
		final Object perCall2 = injector.requireInstance(TimeRecordingObject.class, "perCall");
		final Object lazy1 = injector.requireInstance(TimeRecordingObject.class, "lazy");
		final Object lazy2 = injector.requireInstance(TimeRecordingObject.class, "lazy");
		final Object eager1 = injector.requireInstance(TimeRecordingObject.class, "eager");
		final Object eager2 = injector.requireInstance(TimeRecordingObject.class, "eager");
		Assert.assertSame(eager1, eager2);
		Assert.assertSame(lazy1, lazy2);
		Assert.assertNotSame(perCall1, perCall2);
		List<Object> initializedObjects = module.getInitializedObjects();
		Assert.assertEquals(4, initializedObjects.size());
		Assert.assertSame(eager1, initializedObjects.get(0));
		Assert.assertSame(perCall1, initializedObjects.get(1));
		Assert.assertSame(perCall2, initializedObjects.get(2));
		Assert.assertSame(lazy1, initializedObjects.get(3));
	}

	@Test
	public void testPostConstruct() {
		final PostConstructModule module0 = new PostConstructModule();
		final IModule module1 = new IModule(){
			@Override
			public void configure(IBinder pBinder) {
				pBinder.bind(InitializableObject.class, "perCall").scope(Scopes.PER_CALL);
				pBinder.bind(InitializableObject.class, "lazy").asLazySingleton();
				pBinder.bind(InitializableObject.class, "eager").asEagerSingleton();
			}
		};
		final ILifecycleController controller = module0.getLifecycleController();
		controller.start();
		final IInjector injector = CommonsInject.build(module0, module1);
		final InitializableObject perCall1 = injector.requireInstance(InitializableObject.class, "perCall");
		final InitializableObject perCall2 = injector.requireInstance(InitializableObject.class, "perCall");
		final InitializableObject lazy1 = injector.requireInstance(InitializableObject.class, "lazy");
		final InitializableObject lazy2 = injector.requireInstance(InitializableObject.class, "lazy");
		final InitializableObject eager1 = injector.requireInstance(InitializableObject.class, "eager");
		final InitializableObject eager2 = injector.requireInstance(InitializableObject.class, "eager");
		Assert.assertSame(eager1, eager2);
		Assert.assertSame(lazy1, lazy2);
		Assert.assertNotSame(perCall1, perCall2);
		perCall1.assertStarted();
		perCall2.assertStarted();
		eager1.assertStarted();
		lazy1.assertStarted();
		controller.shutdown();
		perCall1.assertTerminated();
		perCall2.assertTerminated();
		eager1.assertTerminated();
		lazy1.assertTerminated();
	}
}

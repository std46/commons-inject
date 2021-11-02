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

import org.apache.commons.inject.api.AbstractLoggerInjectingModule;
import org.apache.commons.inject.api.CommonsInject;
import org.apache.commons.inject.api.IInjector;
import org.apache.commons.inject.api.ILifecycleController;
import org.apache.commons.inject.api.PostConstructModule;
import org.apache.commons.inject.api.bind.IBinder;
import org.apache.commons.inject.api.bind.IModule;
import org.apache.commons.inject.api.bind.Scopes;
import org.apache.commons.inject.impl.log.SimpleLoggerFactory;
import org.apache.commons.inject.impl.log.SimpleLoggerFactory.SimpleLogger;
import org.junit.Assert;
import org.junit.Test;

public class LoggerModuleTest {
	@Test
	public void testLoggerModule() {
		final SimpleLoggerFactory factory = new SimpleLoggerFactory();
		final PostConstructModule module0 = new PostConstructModule();
		final IModule module1 = new AbstractLoggerInjectingModule<SimpleLogger>(){
			@Override
			protected SimpleLogger newLogger(String pId) {
				return factory.getLogger(pId);
			}
		};
		final IModule module2 = new IModule(){
			@Override
			public void configure(IBinder pBinder) {
				pBinder.bind(InitializableObject.class).scope(Scopes.PER_CALL);
			}
		};
		final ILifecycleController controller = module0.getLifecycleController();
		final IInjector injector = CommonsInject.build(module0, module1, module2);
		final InitializableObject io = injector.getInstance(InitializableObject.class);
		controller.start();
		io.run();
		controller.shutdown();
		Assert.assertEquals(5, factory.getNumEvents());
		Assert.assertEquals("DEBUG MyLogger start: ->", factory.getMessage(0));
		Assert.assertEquals("DEBUG MyLogger start: <-", factory.getMessage(1));
		Assert.assertEquals("INFO  MyLogger run: Running", factory.getMessage(2));
		Assert.assertEquals("DEBUG MyLogger shutdown: ->", factory.getMessage(3));
		Assert.assertEquals("DEBUG MyLogger shutdown: <-", factory.getMessage(4));
	}
}

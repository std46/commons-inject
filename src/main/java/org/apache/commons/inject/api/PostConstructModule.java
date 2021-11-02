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
import java.lang.reflect.Method;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.inject.api.bind.IBinder;
import org.apache.commons.inject.api.bind.IModule;
import org.apache.commons.inject.api.bind.IBinder.IInjectionListener;
import org.apache.commons.inject.impl.DefaultLifecycleController;
import org.apache.commons.inject.util.Exceptions;

public class PostConstructModule implements IModule {
	private ILifecycleController controller;

	public ILifecycleController getLifecycleController() {
		if (controller == null) {
			controller = new DefaultLifecycleController();
		}
		return controller;
	}

	public void setLifecycleController(ILifecycleController pController) {
		controller = pController;
	}

	@Override
	public void configure(IBinder pBinder) {
		final ILifecycleController lcController = getLifecycleController();
		pBinder.add(new IInjectionListener() {
			@Override
			public void initialized(IKey<?> pKey, Object pObject) {
				final ILifecycleListener listener = getListenerFor(pObject);
				if (listener != null  &&  listener != lcController) {
					lcController.add(listener);
				}
			}
		});
		pBinder.bind(ILifecycleController.class).toInstance(lcController);
	}

	protected ILifecycleListener getListenerFor(final Object pObject) {
		if (pObject instanceof ILifecycleListener) {
			return (ILifecycleListener) pObject;
		}
		final Method postConstructMethod = findMethod(PostConstruct.class, pObject.getClass());
		final Method preDestroyMethod = findMethod(PreDestroy.class, pObject.getClass());
		if (postConstructMethod == null  &&  preDestroyMethod == null) {
			return null;
		}
		return new ILifecycleListener(){
			@Override
			public void start() {
				invoke(postConstructMethod, pObject);
			}

			@Override
			public void shutdown() {
				invoke(preDestroyMethod, pObject);
			}

			private void invoke(Method pMethod, Object pObject) {
				if (pMethod == null) {
					return;
				}
				try {
					if (!pMethod.isAccessible()) {
						pMethod.setAccessible(true);
					}
					pMethod.invoke(pObject);
				} catch (Throwable t) {
					throw Exceptions.show(t);
				}
			}
		};
	}

	private Method findMethod(Class<? extends Annotation> pAnnotationClass, Class<?> pType) {
		final Method[] methods = pType.getDeclaredMethods();
		for (Method method : methods) {
			if (method.isAnnotationPresent(pAnnotationClass)) {
				return method;
			}
		}
		return null;
	}
}

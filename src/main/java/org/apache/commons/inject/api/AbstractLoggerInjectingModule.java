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
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.inject.api.bind.IBinder;
import org.apache.commons.inject.api.bind.IModule;
import org.apache.commons.inject.api.bind.IBinder.IInjectionParticipator;
import org.apache.commons.inject.util.Exceptions;

/**
 * Abstract implementation of a module, which injects loggers into fields.
 * The field must be annotated like this:
 * <pre>
 *   @InjLogger private Logger log;
 * </pre>
 * or
 * <pre>
 *   @InjLogger(id="LoggerId") private Logger log;
 * </pre>
 * The second example would create a logger with the id "LoggerId". The first
 * example would use the class name as a default value. (The class being that
 * class, which declares the field.
 */
public abstract class AbstractLoggerInjectingModule<Log> implements IModule {

	@Override
	public void configure(IBinder pBinder) {
		pBinder.add(new IInjectionParticipator() {
			@Override
			public List<IPoint<Object>> getPoints(IKey<?> pKey, Class<?> pType) {
				final List<IPoint<Object>> points = new ArrayList<IPoint<Object>>();
				final Field[] fields = pType.getDeclaredFields();
				final Class<? extends Annotation> annotationClass = getAnnotationClass();
				for (final Field f : fields) {
					if (f.isAnnotationPresent(annotationClass)) {
						final Annotation annotation = f.getAnnotation(annotationClass);
						String id = getId(annotation, f.getDeclaringClass());
						if (id == null  ||  id.length() == 0) {
							id = f.getDeclaringClass().getName();
						}
						final Log logger = newLogger(id);
						points.add(new IPoint<Object>(){
							@Override
							public void injectTo(Object pInstance,
									IInjector pInjector) {
								try {
									if (!f.isAccessible()) {
										f.setAccessible(true);
									}
									f.set(pInstance, logger);
								} catch (Throwable t) {
									throw Exceptions.show(t);
								}
							}
						});
					}
				}
				return points;
			}
		});
	}

	/**
	 * Creates a new logger with the given Id. Subclasses <em>must</em>
	 * overwrite this to return a suitable logger implementation.
	 */
	protected abstract Log newLogger(String pId);

	/**
	 * Returns the annotation class, which denotes suitable target fields.
	 * By default, the class {@link InjLogger} is used.
	 */
	protected Class<? extends Annotation> getAnnotationClass() {
		return InjLogger.class;
	}

	/**
	 * Called to calculate the Id, which is used to invoke {@link #newLogger(String)}.
	 * By default, {@link InjLogger#name()} is returned.
	 */
	protected String getId(Annotation pAnnotation, Class<?> pClass) {
		final InjLogger injLogger = (InjLogger) pAnnotation;
		final String id = injLogger.id();
		if (id == null  ||  id.length() == 0) {
			return pClass.getName();
		} else {
			return id;
		}
	}
}

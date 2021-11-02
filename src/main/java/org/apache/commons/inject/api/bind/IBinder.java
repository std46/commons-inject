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

import java.util.List;

import org.apache.commons.inject.api.IInjector;
import org.apache.commons.inject.api.IKey;
import org.apache.commons.inject.api.IPoint;

/**
 * A {@link IBinder} is passed as an argument to modules. Modules use
 * the binder to create {@link IBinding bindings}.
 */

public interface IBinder {
	public interface IInjectionParticipator {
		List<IPoint<Object>> getPoints(IKey<?> pKey, Class<?> pType);
	}
	public interface IInjectionListener {
		void initialized(IKey<?> pKey, Object pObject);
	}
	public interface IInjectorBuildListener {
		void created(IInjector pInjector);
	}
	<T> IAnnotatedBindingBuilder<T> bind(Class<T> pType);
	<T> IAnnotatedBindingBuilder<T> bind(Class<T> pType, String pName);
	<T> ILinkedBindingBuilder<T> bind(IKey<T> pKey);
	boolean add(IInjectionListener pListener);
	boolean add(IInjectorBuildListener pListener);
	boolean add(IInjectionParticipator pParticipator);
}

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

import org.apache.commons.inject.api.bind.IBinder;
import org.apache.commons.inject.api.bind.IModule;


/**
 * A point implements injection of values into a field, or method.
 * Of course, several points may be combined into more complex points,
 * etc.
 * {@link IModule modules} may inject additional values by implementing
 * {@link IBinder.InjectionParticipator} and adding listeners via
 * {@link IBinder#add(org.apache.commons.inject.api.bind.IBinder.IInjectionParticipator)}.
 * This is done, for example, by the {@link AbstractLoggerInjectingModule}.
 */
public interface IPoint<T> {
	void injectTo(T pInstance, IInjector pInjector);
}

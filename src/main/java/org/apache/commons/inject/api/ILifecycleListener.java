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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/** Interface of an object, which can be managed by the
 * {@link ILifecycleController}. The {@link PostConstructModule}
 * detects objects annotated with {@link PostConstruct}, and
 * {@link PreDestroy}, wraps them in an instance of
 * {@link ILifecycleListener}, which delegates to the respective
 * methods, and adds the wrappers to the lifecycle controller.
 */
public interface ILifecycleListener {
	void start();
	void shutdown();
}

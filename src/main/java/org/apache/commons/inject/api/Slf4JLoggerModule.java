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

import org.apache.commons.inject.api.bind.IModule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link IModule module}, which injects SLF4J loggers, aka instances of
 * {@link Logger}.
 * If you are using the module, it should precede all others in your module list,
 * when invoking {@link CommonsInject#build(org.apache.commons.inject.api.bind.IModule...)},
 * or {@link CommonsInject#build(java.util.Collection)}. However, the
 * {@link PostConstructModule} takes precedence, if you are using that as well.
 * @see Log4jLoggerModule
 * @see Slf4JLoggerModule
 * @see CommonsLoggingLoggerModule
 * @see AbstractLoggerInjectingModule
 */
public class Slf4JLoggerModule extends AbstractLoggerInjectingModule<Logger> {
	/**
	 * Creates an instance of {@link Logger}.
	 */
	@Override
	protected Logger newLogger(String pId) {
		return LoggerFactory.getLogger(pId);
	}
}

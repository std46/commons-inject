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

import org.apache.commons.inject.api.ILifecycleListener;
import org.apache.commons.inject.api.InjLogger;
import org.apache.commons.inject.impl.log.SimpleLoggerFactory.Level;
import org.apache.commons.inject.impl.log.SimpleLoggerFactory.SimpleLogger;

public class InitializableObject implements ILifecycleListener {
	@InjLogger(id="MyLogger") private SimpleLogger log;

	@Override
	public void start() {
		log.log("start: ->");
		log.log("start: <-");
	}

	public void run() {
		log.log(Level.INFO, "run: Running");
	}
	
	@Override
	public void shutdown() {
		log.log("shutdown: ->");
		log.log("shutdown: <-");
	}
}
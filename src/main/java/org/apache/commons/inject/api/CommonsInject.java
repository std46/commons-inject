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

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.inject.api.bind.IModule;
import org.apache.commons.inject.impl.DefaultInjectorBuilder;

/**
 * This class provides the anchor point to create {@link IInjector injectors} via
 * factory methods.
 */
public class CommonsInject {
	/**
	 * Creates a new {@link IInjector injector}, which is configured by invoking
	 * the given modules.
	 * @param pModules The modules, which provide the injectors bindings.
	 * @return A new {@link IInjector injector}.
	 * @see #build(Collection)
	 */
	public static IInjector build(IModule... pModules) {
		if (pModules == null) {
			throw new NullPointerException("The module list must not be null.");
		}
		final Collection<IModule> modules = Arrays.asList(pModules);
		return build(modules);
	}
	/**
	 * Creates a new {@link IInjector injector}, which is configured by invoking
	 * the given modules.
	 * @param pModules The modules, which provide the injectors bindings.
	 * @return A new {@link IInjector injector}.
	 * @see #build(IModule...)
	 */
	public static IInjector build(Collection<IModule> pModules) {
		if (pModules == null) {
			throw new NullPointerException("The module list must not be null.");
		}
		if (pModules.isEmpty()) {
			throw new IllegalArgumentException("The module list must not be empty.");
		}
		return new DefaultInjectorBuilder(pModules).build();
	}
	/**
	 * An alternative to {@link #build(IModule...)}, which provides additional
	 * control on the injectors configuration. Rather than directly returning
	 * an {@link IInjector injector}, this method returns an {@link IInjectorBuilder
	 * injector builder}.
	 * @return A new {@link IInjectorBuilder injector builder}.
	 * @see #build(IModule...)
	 * @see #build(Collection)
	 */
	public static IInjectorBuilder newBuilder() {
		return new DefaultInjectorBuilder();
	}
}

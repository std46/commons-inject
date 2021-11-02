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

/**
 * The {@link IInjector injector} is used to build objects for your application.
 * It does so by maintaining a map of {@link IKey keys} and associated
 * {@link IBinding bindings}. The map is created by {@link IModule modules}.
 *
 * Some bindings are present automatically:
 * - The {@link Injector} itself.
 * - For any {@link IBinding IBinding<T>} (binding of type T), there is also
 *   a {@link IProvider provider} of type T.
 *
 * To create an {@link IInjector injector}, use the class {@link CommonsInject}.
 */
public interface IInjector {
	/**
	 * Returns an instance of {@code pType}, if a matching binding is present.
	 * This is a shortcut for
	 * <pre>
	 *   getInstance(pType, "")
	 * </pre>
	 * or
	 * <pre>
	 *   getInstance(pType, Key.NO_NAME)
	 * </pre>
	 * @param pType The requested type.
	 * @return The created instance, or null.
	 * @see #getInstance(Class, String)
	 * @see #getInstance(IKey)
	 * @see #requireInstance(Class)
	 */
	<T> T getInstance(Class<T> pType);
	/**
	 * Returns an instance of the given type, with the given name, if a
	 * matching binding is present. This is a shortcut for
	 * <pre>
	 *   Key key = new Key(pType, pName);
	 *   getInstance(key)
	 * </pre>
	 * or
	 * <pre>
	 *   Key key = new Key(pType, pName, Key.NO_ANNOTATIONS);
	 *   getInstance(key)
	 * </pre>
	 * @param pType The requested type.
	 * @param pName The requested objects name.
	 * @return The created instance, or null.
	 * @see #getInstance(IKey)
	 * @see #requireInstance(Class, String)
	 */
	<T> T getInstance(Class<T> pType, String pName);
	/**
	 * Returns an instance of the binding that has been registered for the 
	 * given key.
	 * @param pKey A binding key, for which a binding has been registered.
	 * @return The created instance, or null.
	 * @see #getInstance(Class)
	 * @see #getInstance(Class, String)
	 * @see #requireInstance(IKey)
	 */
	<T> T getInstance(IKey<T> pKey);

	/**
	 * Returns an instance of {@code pType}, if a matching binding is present.
	 * This is a shortcut for
	 * <pre>
	 *   requireInstance(pType, "")
	 * </pre>
	 * or
	 * <pre>
	 *   requireInstance(pType, Key.NO_NAME)
	 * </pre>
	 * @param pType The requested type.
	 * @return The created instance.
	 * @throws NoSuchBindingException No matching binding has been registered with
	 * the injector.
	 * @see #getInstance(Class, String)
	 * @see #getInstance(IKey)
	 * @see #requireInstance(Class)
	 */
	<T> T requireInstance(Class<T> pType) throws NoSuchBindingException;
	/**
	 * Returns an instance of the given type, with the given name, if a
	 * matching binding is present. This is a shortcut for
	 * <pre>
	 *   Key key = new Key(pType, pName);
	 *   requireInstance(key)
	 * </pre>
	 * or
	 * <pre>
	 *   Key key = new Key(pType, pName, Key.NO_ANNOTATIONS);
	 *  requireInstance(key)
	 * </pre>
	 * @param pType The requested type.
	 * @param pName The requested objects name.
	 * @return The created instance.
	 * @throws NoSuchBindingException No matching binding has been registered with
	 * the injector.
	 * @see #getInstance(Class, String)
	 * @see #requireInstance(Class)
	 * @see #requireInstance(IKey)
	 */
	<T> T requireInstance(Class<T> pType, String pName) throws NoSuchBindingException;
	/**
	 * Returns an instance of the binding that has been registered for the 
	 * given key.
	 * @param pKey A binding key, for which a binding has been registered.
	 * @return The created instance.
	 * @throws NoSuchBindingException No matching binding has been registered with
	 * the injector.
	 * @see #getInstance(IKey)
	 * @see #requireInstance(Class)
	 * @see #requireInstance(Class, String)
	 */
	<T> T requireInstance(IKey<T> pKey) throws NoSuchBindingException;

	/**
	 * Injects members into the given instance, as if it where created by
	 * the {@link IInjector injector} itself. In other words, fills fields
	 * and invokes methods annotated with @Inject, assuming that a binding
	 * is present for those fields, and method parameters.
	 */
	void injectMembers(Object pInstance);
}

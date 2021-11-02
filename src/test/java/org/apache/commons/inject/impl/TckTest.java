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

import org.apache.commons.inject.api.CommonsInject;
import org.apache.commons.inject.api.IInjector;
import org.apache.commons.inject.api.bind.IBinder;
import org.apache.commons.inject.api.bind.IModule;
import org.apache.commons.inject.api.bind.Scopes;
import org.atinject.tck.Tck;
import org.atinject.tck.auto.Car;
import org.atinject.tck.auto.Convertible;
import org.atinject.tck.auto.Drivers;
import org.atinject.tck.auto.DriversSeat;
import org.atinject.tck.auto.Engine;
import org.atinject.tck.auto.FuelTank;
import org.atinject.tck.auto.Seat;
import org.atinject.tck.auto.Tire;
import org.atinject.tck.auto.V8Engine;
import org.atinject.tck.auto.accessories.Cupholder;
import org.atinject.tck.auto.accessories.SpareTire;
import org.junit.Test;

public class TckTest {
	@Test
	public void testTckWithStaticInjection() throws Exception {
		final IInjector injector = newInjector(true);
		final Car car = injector.requireInstance(Car.class);
		Tck.testsFor(car, true, true);
	}

	@Test
	public void testTckWithoutStaticInjection() throws Exception {
		final IInjector injector = newInjector(false);
		final Car car = injector.requireInstance(Car.class);
		Tck.testsFor(car, false, true);
	}

	private IInjector newInjector(boolean pWithStaticInjection) {
		final IModule module = new IModule(){
			@Override
			public void configure(IBinder pBinder) {
				pBinder.bind(Car.class).to(Convertible.class).scope(Scopes.PER_CALL);;
				pBinder.bind(Seat.class).annotatedWith(Drivers.class).to(DriversSeat.class).scope(Scopes.PER_CALL);
				pBinder.bind(Engine.class).to(V8Engine.class).scope(Scopes.PER_CALL);
				pBinder.bind(Tire.class, "spare").to(SpareTire.class).scope(Scopes.PER_CALL);
				pBinder.bind(SpareTire.class).scope(Scopes.PER_CALL);
				pBinder.bind(Cupholder.class).scope(Scopes.PER_CALL);
				pBinder.bind(Tire.class).scope(Scopes.PER_CALL);
				pBinder.bind(FuelTank.class).scope(Scopes.PER_CALL);
			}
		};
		return CommonsInject.build(module);
	}

}

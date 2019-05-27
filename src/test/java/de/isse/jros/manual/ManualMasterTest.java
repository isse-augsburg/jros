/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2016-2019 ISSE, University of Augsburg 
 */

package de.isse.jros.manual;

import java.io.IOException;

import de.isse.jros.RosMaster;

/**
 * Manual test starting a ROS master
 */
public class ManualMasterTest {
	public static void main(String[] args) throws IOException {
		System.out.println("Starting ROS master on 11311");
		RosMaster master = new RosMaster(11311);
		System.in.read();
		master.shutdown();
	}
}

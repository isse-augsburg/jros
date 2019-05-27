/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2016-2019 ISSE, University of Augsburg 
 */

package de.isse.jros.messages;

import de.isse.jros.fields.RInteger;
import de.isse.jros.fields.RMessage;
import de.isse.jros.fields.RString;
import de.isse.jros.fields.RTime;
import de.isse.jros.types.ROSstruct;

/** Standard ROS Messages */
public class StdMsgs {

	/** Standard metadata for higher-level stamped data types */
	public static ROSstruct Header() {
		return new ROSstruct("std_msgs/Header").withUint32("seq").withTime("stamp").withString("frame_id");
	}

	/** Standard metadata for higher-level stamped data types */
	public static class Header extends RMessage {
		public final RInteger seq = field("seq", RInteger.uint32());
		public final RTime stamp = field("stamp", RTime.time());
		public final RString frame_id = field("frame_id", RString.string());
		public final ROSstruct TYPE = type("std_msgs/Header");
	}

}

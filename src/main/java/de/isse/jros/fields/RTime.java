/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2016-2019 ISSE, University of Augsburg 
 */

package de.isse.jros.fields;

import java.util.function.Supplier;

import de.isse.jros.descriptors.ElementDescriptor;
import de.isse.jros.types.ROSType;
import de.isse.jros.types.ROSduration;
import de.isse.jros.types.ROStime;

/**
 * Definition of a time value within a ROS message
 */
public class RTime extends RField {
	private ROSType<?> type;

	/**
	 * Creates a new time field of the given type
	 */
	private RTime(ROSType<?> type, Supplier<RField> cloner) {
		super(cloner);
		this.type = type;
	}

	@Override
	public ROSType<?> getType() {
		return type;
	}

	/**
	 * Creates a new time field
	 */
	public static RTime time() {
		return new RTime(ROStime.TYPE, RTime::time);
	}

	/**
	 * Creates a new duration field
	 */
	public static RTime duration() {
		return new RTime(ROSduration.TYPE, RTime::time);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ElementDescriptor<int[]> getDescriptor() {
		return (ElementDescriptor<int[]>) super.getDescriptor();
	}

	/**
	 * Retrieves the seconds of this field from a ROS binary message
	 * 
	 * @param message ROS binary message to read the value from
	 * @return value from the message
	 */
	public int readSec(byte[] message) {
		return getDescriptor().read(message)[0];
	}

	/**
	 * Retrieves the nanoseconds of this field from a ROS binary message
	 * 
	 * @param message ROS binary message to read the value from
	 * @return value from the message
	 */
	public int readNSec(byte[] message) {
		return getDescriptor().read(message)[1];
	}

	/**
	 * Writes seconds and nanoseconds to this field in a ROS binary message
	 * 
	 * @param message ROS binary message to write the value to
	 * @param value   value to write
	 */
	public void write(byte[] message, int sec, int nsec) {
		getDescriptor().write(message, new int[] { sec, nsec });
	}

}

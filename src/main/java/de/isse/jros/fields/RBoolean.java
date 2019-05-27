/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2016-2019 ISSE, University of Augsburg 
 */

package de.isse.jros.fields;

import de.isse.jros.descriptors.ElementDescriptor;
import de.isse.jros.types.ROSType;
import de.isse.jros.types.ROSbool;

/**
 * Definition of a Boolean value within a ROS message
 */
public class RBoolean extends RField {

	@Override
	public ROSType<?> getType() {
		return ROSbool.TYPE;
	}

	/**
	 * Creates a new Boolean field
	 */
	private RBoolean() {
		super(RBoolean::bool);
	}

	/**
	 * Creates a new Boolean field
	 */
	public static RBoolean bool() {
		return new RBoolean();
	}

	@SuppressWarnings("unchecked")
	@Override
	public ElementDescriptor<Boolean> getDescriptor() {
		return (ElementDescriptor<Boolean>) super.getDescriptor();
	}

	/**
	 * Retrieves the value of this field from a ROS binary message
	 * 
	 * @param message ROS binary message to read the value from
	 * @return value from the message
	 */
	public boolean read(byte[] message) {
		return getDescriptor().read(message).booleanValue();
	}

	/**
	 * Writes a Boolean value to this field in a ROS binary message
	 * 
	 * @param message ROS binary message to write the value to
	 * @param value   value to write
	 */
	public void write(byte[] message, boolean value) {
		getDescriptor().write(message, value);
	}

}

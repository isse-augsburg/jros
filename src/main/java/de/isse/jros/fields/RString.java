/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2016-2019 ISSE, University of Augsburg 
 */

package de.isse.jros.fields;

import de.isse.jros.descriptors.ElementDescriptor;
import de.isse.jros.types.ROSstring;

/**
 * Definition of a string value within a ROS message
 */
public class RString extends RField {

	/**
	 * Creates a new string field
	 */
	private RString() {
		super(RString::string);
	}

	@Override
	public ROSstring getType() {
		return ROSstring.TYPE;
	}

	/**
	 * Creates a new string field
	 */
	public static RString string() {
		return new RString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public ElementDescriptor<String> getDescriptor() {
		return (ElementDescriptor<String>) super.getDescriptor();
	}

	/**
	 * Retrieves the string value of this field from a ROS binary message
	 * 
	 * @param message ROS binary message to read the value from
	 * @return value from the message
	 */
	public String read(byte[] message) {
		return (String) getDescriptor().read(message);
	}

	/**
	 * Writes a string value to this field in a ROS binary message
	 * 
	 * @param message ROS binary message to write the value to
	 * @param value   value to write
	 */
	public void write(byte[] message, String value) {
		getDescriptor().write(message, value);
	}

}

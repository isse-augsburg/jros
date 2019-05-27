/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2016-2019 ISSE, University of Augsburg 
 */

package de.isse.jros.types;

/**
 * A ROS byte type (unsigned int8)
 */
public class ROSchar extends ROSuint8 {
	/**
	 * A ROS byte type (unsigned int8)
	 */
	public static final ROSchar TYPE = new ROSchar();

	/**
	 * ROSchar serves as singleton
	 */
	protected ROSchar() {
	}

	@Override
	public String getName() {
		return "char";
	}

	@Override
	public Long fromConstant(String value) {
		return (long) Short.parseShort(value);
	}
}

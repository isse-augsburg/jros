/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2016-2019 ISSE, University of Augsburg 
 */

package de.isse.jros.types;

/**
 * A ROS boolean type
 */
public class ROSbool extends ROSType<Boolean> {
	/**
	 * A ROS boolean type
	 */
	public static final ROSbool TYPE = new ROSbool();

	/**
	 * ROSbool serves as singleton
	 */
	protected ROSbool() {
	}

	@Override
	public String getName() {
		return "bool";
	}

	@Override
	public Boolean fromConstant(String value) {
		return value.equals("true");
	}

	@Override
	public Boolean read(byte[] data, int position) {
		return data[position] == 1 ? true : false;
	}

	@Override
	public void write(byte[] data, int position, Boolean value) {
		data[position] = (byte) (value ? 1 : 0);
	}

	@Override
	public int skip(byte[] data, int position) {
		return position + 1;
	}

	@Override
	public String toString(byte[] data, int position) {
		return read(data, position) ? "true" : "false";
	}
}

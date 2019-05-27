/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2016-2019 ISSE, University of Augsburg 
 */

package de.isse.jros.types;

import java.nio.ByteBuffer;

/**
 * A ROS signed int16 type
 */
public class ROSint16 extends ROSType<Long> {
	/**
	 * A ROS signed int16 type
	 */
	public static final ROSint16 TYPE = new ROSint16();

	/**
	 * ROSint16 serves as singleton
	 */
	protected ROSint16() {
	}

	@Override
	public String getName() {
		return "int16";
	}

	@Override
	public Long fromConstant(String value) {
		return (long) Short.parseShort(value);
	}

	@Override
	public Long read(byte[] data, int position) {
		return (long) Short.reverseBytes(ByteBuffer.wrap(data, position, 2).getShort());
	}

	/**
	 * Writes the given Java value into the ROS message binary representation
	 * 
	 * @param data     byte array containing the message
	 * @param position position in the byte array where the current type is stored
	 * @param value    value to write as Java representation
	 */
	public void write(byte[] data, int position, int value) {
		write(data, position, (long) value);
	}

	@Override
	public void write(byte[] data, int position, Long value) {
		ByteBuffer.wrap(data, position, 2).putShort(Short.reverseBytes(value.shortValue()));
	}

	@Override
	public int skip(byte[] data, int position) {
		return position + 2;
	}

	@Override
	public String toString(byte[] data, int position) {
		return read(data, position) + "";
	}
}

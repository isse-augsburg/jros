/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2016-2019 ISSE, University of Augsburg 
 */

package de.isse.jros.types;

import java.nio.ByteBuffer;

/**
 * A ROS unsigned int32 type
 */
public class ROSuint32 extends ROSType<Long> {
	/**
	 * A ROS unsigned int32 type
	 */
	public static final ROSuint32 TYPE = new ROSuint32();

	/** 
	 * ROSuint32 serves as singleton
	 */
	protected ROSuint32() {
	}

	@Override
	public String getName() {
		return "uint32";
	}

	@Override
	public Long fromConstant(String value) {
		return Long.parseLong(value);
	}

	@Override
	public Long read(byte[] data, int position) {
		long value = Integer.reverseBytes(ByteBuffer.wrap(data, position, 4).getInt());
		if (value < 0)
			value += 1L << 32;
		return value;
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
		ByteBuffer.wrap(data, position, 4).putInt(Integer.reverseBytes(value.intValue()));
	}

	@Override
	public int skip(byte[] data, int position) {
		return position + 4;
	}

	@Override
	public String toString(byte[] data, int position) {
		return read(data, position) + "";
	}
}

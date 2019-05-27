/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2016-2019 ISSE, University of Augsburg 
 */

package de.isse.jros.types;

import java.nio.ByteBuffer;

/**
 * A ROS float32 type
 */
public class ROSfloat32 extends ROSType<Double> {
	/**
	 * A ROS float32 type
	 */
	public static final ROSfloat32 TYPE = new ROSfloat32();

	/**
	 * ROSfloat32 serves as singleton
	 */
	protected ROSfloat32() {
	}

	@Override
	public String getName() {
		return "float32";
	}

	@Override
	public Double fromConstant(String value) {
		return (double) Float.parseFloat(value);
	}

	@Override
	public Double read(byte[] data, int position) {
		return (double) Float.intBitsToFloat(Integer.reverseBytes(ByteBuffer.wrap(data, position, 4).getInt()));
	}

	/**
	 * Writes the given Java value into the ROS message binary representation
	 * 
	 * @param data     byte array containing the message
	 * @param position position in the byte array where the current type is stored
	 * @param value    value to write as Java representation
	 */
	public void write(byte[] data, int position, float value) {
		write(data, position, (double) value);
	}

	@Override
	public void write(byte[] data, int position, Double value) {
		ByteBuffer.wrap(data, position, 4).putInt(Integer.reverseBytes(Float.floatToIntBits(value.floatValue())));
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

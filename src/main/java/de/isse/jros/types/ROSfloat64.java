/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2016-2019 ISSE, University of Augsburg 
 */

package de.isse.jros.types;

import java.nio.ByteBuffer;

/**
 * A ROS float64 type
 */
public class ROSfloat64 extends ROSType<Double> {
	/**
	 * A ROS float64 type
	 */
	public static final ROSfloat64 TYPE = new ROSfloat64();

	/**
	 * ROSfloat64 serves as singleton
	 */
	protected ROSfloat64() {
	}

	@Override
	public String getName() {
		return "float64";
	}

	@Override
	public Double fromConstant(String value) {
		return Double.parseDouble(value);
	}

	@Override
	public Double read(byte[] data, int position) {
		return Double.longBitsToDouble(Long.reverseBytes(ByteBuffer.wrap(data, position, 8).getLong()));
	}

	@Override
	public void write(byte[] data, int position, Double value) {
		ByteBuffer.wrap(data, position, 8).putLong(Long.reverseBytes(Double.doubleToLongBits(value)));
	}

	@Override
	public int skip(byte[] data, int position) {
		return position + 8;
	}

	@Override
	public String toString(byte[] data, int position) {
		return read(data, position) + "";
	}
}

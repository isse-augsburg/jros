/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2016-2019 ISSE, University of Augsburg 
 */

package de.isse.jros.types;

import java.nio.ByteBuffer;

/**
 * A ROS duration type
 */
public class ROSduration extends ROSType<int[]> {
	/**
	 * A ROS duration type
	 */
	public static final ROSduration TYPE = new ROSduration();

	/**
	 * ROSduration serves as singleton
	 */
	protected ROSduration() {
	}

	@Override
	public String getName() {
		return "duration";
	}

	/**
	 * Reads the seconds from the given ROS message binary representation
	 * 
	 * @param data     array with the message binary representation
	 * @param position position in the array where the duration is stored
	 * @return seconds of the duration
	 */
	public int readSec(byte[] data, int position) {
		return Integer.reverseBytes(ByteBuffer.wrap(data, position, 4).getInt());
	}

	/**
	 * Reads the nanoseconds from the given ROS message binary representation
	 * 
	 * @param data     array with the message binary representation
	 * @param position position in the array where the duration is stored
	 * @return nanoseconds of the duration
	 */
	public int readNsec(byte[] data, int position) {
		return Integer.reverseBytes(ByteBuffer.wrap(data, position + 4, 4).getInt());
	}

	@Override
	public int[] fromConstant(String value) {
		return null;
	}

	@Override
	public int[] read(byte[] data, int position) {
		return new int[] { Integer.reverseBytes(ByteBuffer.wrap(data, position, 4).getInt()),
				Integer.reverseBytes(ByteBuffer.wrap(data, position + 4, 4).getInt()) };
	}

	@Override
	public void write(byte[] data, int position, int[] value) {
		if (!(value instanceof int[]))
			throw new IllegalArgumentException();
		ByteBuffer.wrap(data, position, 4).putInt(Integer.reverseBytes(value[0]));
		ByteBuffer.wrap(data, position + 4, 4).putInt(Integer.reverseBytes(value[1]));
	}

	@Override
	public int skip(byte[] data, int position) {
		return position + 8;
	}

	@Override
	public String toString(byte[] data, int position) {
		return readSec(data, position) + "#" + readNsec(data, position);
	}

}

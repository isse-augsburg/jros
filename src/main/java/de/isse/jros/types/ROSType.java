/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2016-2019 ISSE, University of Augsburg 
 */

package de.isse.jros.types;

/**
 * Description of a ROS data type (immutable)
 * 
 * @param <T> Corresponding Java type
 */
public abstract class ROSType<T> {

	/**
	 * Retrieves the name of the type
	 */
	public abstract String getName();

	@Override
	public String toString() {
		return getName();
	}

	/**
	 * Converts a textual representation of this type to its Java counterpart (for
	 * use with given constant values in msg files)
	 * 
	 * @param value textual representation of this type
	 * @return Java value
	 */
	public abstract T fromConstant(String value);

	/**
	 * Moves the second half of an array to the left, and fills the end with \0
	 * bytes
	 * 
	 * @param data   array containing the bytes
	 * @param start  split position
	 * @param amount number of bytes to move the right part to the left
	 */
	protected void moveBytesLeft(byte[] data, int start, int amount) {
		for (int i = start; i < data.length - amount; i++) {
			data[i] = data[i + amount];
		}
		for (int i = data.length - amount; i < data.length; i++) {
			data[i] = 0;
		}
	}

	/**
	 * Moves the second half of an array to the right, and fills the gap with \0
	 * bytes
	 * 
	 * @param data   array containing the bytes
	 * @param start  split position
	 * @param amount number of bytes to move the right part to the right
	 */
	protected void moveBytesRight(byte[] data, int start, int amount) {
		for (int i = data.length - 1; i >= start + amount; i--) {
			data[i] = data[i - amount];
		}
		for (int i = start; i < start + amount; i++) {
			data[i] = 0;
		}
	}

	/**
	 * Copies bytes to a specific position in an array
	 * 
	 * @param data    array to copy the data into
	 * @param start   start position in data array
	 * @param newData data to copy into the array
	 */
	protected void overwriteBytes(byte[] data, int start, byte[] newData) {
		for (int i = 0; i < newData.length; i++) {
			data[i + start] = newData[i];
		}
	}

	/**
	 * Reads the given bytes from the ROS message binary representation and converts
	 * them into Java values
	 * 
	 * @param data     byte array containing the message
	 * @param position position in the byte array where the current type is stored
	 * @return value as Java representation
	 */
	public abstract T read(byte[] data, int position);

	/**
	 * Calculates the position in the byte array where the next element starts
	 * 
	 * @param data     byte array containing the message
	 * @param position position in the byte array where the current type is stored
	 * @return position of the next element
	 */
	public abstract int skip(byte[] data, int position);

	/**
	 * Writes the given Java value into the ROS message binary representation
	 * 
	 * @param data     byte array containing the message
	 * @param position position in the byte array where the current type is stored
	 * @param value    value to write as Java representation
	 */
	public abstract void write(byte[] data, int position, T value);

	/**
	 * Creates a JSON representation of the ROS message in binary representation
	 * 
	 * @param data array containing a binary representation of this ROS type
	 * @return JSON string
	 */
	public String toString(byte[] data) {
		return toString(data, 0);
	}

	/**
	 * Creates a JSON representation of the ROS message in binary representation,
	 * assuming that this field starts at the given position
	 * 
	 * @param data     array containing a binary representation of this ROS type
	 * @param position start position in the array where this type starts
	 * @return JSON string
	 */
	public abstract String toString(byte[] data, int position);

}

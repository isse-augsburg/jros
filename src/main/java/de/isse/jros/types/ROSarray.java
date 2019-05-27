/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2016-2019 ISSE, University of Augsburg 
 */

package de.isse.jros.types;

/**
 * A ROS variable size array type
 */
public class ROSarray extends ROSType<Integer> {
	/** The element type */
	private ROSType<?> prototype;

	@Override
	public String getName() {
		return prototype.getName() + "[]";
	}

	/**
	 * Retrieves the element type
	 */
	public ROSType<?> getPrototype() {
		return prototype;
	}

	@Override
	public Integer fromConstant(String value) {
		return null;
	}

	/**
	 * Creates a ROS variable size array
	 * 
	 * @param prototype element type
	 */
	public ROSarray(ROSType<?> prototype) {
		this.prototype = prototype;
	}

	@Override
	public String toString() {
		return getName();
	}

	/**
	 * Calculates the position where the element with the given index starts
	 * 
	 * @param index    element index to skip to
	 * @param data     ROS binary message representation
	 * @param position start position of the array
	 * @return position of the element with the given index
	 */
	public int skipToIndex(int index, byte[] data, int position) {
		int len = read(data, position);
		position += 4;
		for (int i = 0; i < len; i++) {
			if (i == index) {
				return position;
			} else {
				position = prototype.skip(data, position);
			}
		}
		return -1;
	}

	@Override
	public Integer read(byte[] data, int position) {
		int len = ROSint32.TYPE.read(data, position).intValue();
		return len;
	}

	@Override
	public void write(byte[] data, int position, Integer value) {
		int size = read(data, position);

		int end = skip(data, position);
		int newend = ROSint32.TYPE.skip(data, position);
		for (int i = 0; i < value; i++) {
			if (i <= size)
				newend = prototype.skip(data, newend);
			else
				newend = prototype.skip(null, newend);
		}

		if (newend > end)
			moveBytesRight(data, end, newend - end);
		else if (end > newend)
			moveBytesLeft(data, newend, end - newend);
		ROSint32.TYPE.write(data, position, value);
	}

	@Override
	public int skip(byte[] data, int position) {
		if (data == null)
			return position + 4;
		int len = read(data, position);
		position += 4;
		for (int i = 0; i < len; i++)
			position = prototype.skip(data, position);
		return position;
	}

	@Override
	public String toString(byte[] data, int position) {
		int size = read(data, position);
		position += 4;
		StringBuffer ret = new StringBuffer().append("[");
		for (int i = 0; i < size; i++) {
			getPrototype().toString(data, position);
			position = getPrototype().skip(data, position);
		}
		return ret.append("]").toString();
	}

}

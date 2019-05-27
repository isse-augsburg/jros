/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2016-2019 ISSE, University of Augsburg 
 */

package de.isse.jros.types;

/**
 * A ROS fixed size array type
 */
public class ROSfixedArray extends ROSType<Void> {
	/** The element type */
	private ROSType<?> prototype;
	/** The array size */
	int size;

	@Override
	public String getName() {
		return prototype.getName() + "[" + size + "]";
	}

	/**
	 * Creates a ROS variable size array type
	 * 
	 * @param prototype element type
	 * @param size      array size
	 */
	public ROSfixedArray(ROSType<?> prototype, int size) {
		this.prototype = prototype;
		this.size = size;
	}

	/**
	 * Retrieves the (fixed) array size
	 */
	public int getSize() {
		return size;
	}

	@Override
	public Void fromConstant(String value) {
		return null;
	}

	/**
	 * Retrieves the element type
	 */
	public ROSType<?> getPrototype() {
		return prototype;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public Void read(byte[] data, int position) {
		return null;
	}

	@Override
	public void write(byte[] data, int position, Void value) {
	}

	@Override
	public int skip(byte[] data, int position) {
		for (int i = 0; i < getSize(); i++)
			position = prototype.skip(data, position);
		return position;
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
		for (int i = 0; i < size; i++) {
			if (i == index) {
				return position;
			} else {
				position = prototype.skip(data, position);
			}
		}
		return -1;
	}

	@Override
	public String toString(byte[] data, int position) {
		StringBuffer ret = new StringBuffer().append("[");
		for (int i = 0; i < size; i++) {
			getPrototype().toString(data, position);
			position = getPrototype().skip(data, position);
		}
		return ret.append("]").toString();
	}

}

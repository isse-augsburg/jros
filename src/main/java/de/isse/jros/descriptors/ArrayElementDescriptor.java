/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2016-2019 ISSE, University of Augsburg 
 */

package de.isse.jros.descriptors;

import de.isse.jros.types.ROSType;
import de.isse.jros.types.ROSarray;
import de.isse.jros.types.ROSfixedArray;

/**
 * Descriptor for an element within a array
 *
 * @param <T> type of element this descriptor points to
 */
public class ArrayElementDescriptor<T> extends ElementDescriptor<T> {

	private ElementDescriptor<?> parent;
	private int index;
	private ROSType<?> parentType;

	/**
	 * Creates an element descriptor that points at a given index within an array
	 * 
	 * @param type   type of element this descriptor points at
	 * @param parent element descriptor for the array to find the element in
	 * @param index  index of the element within the given array
	 */
	public ArrayElementDescriptor(ROSType<T> type, ElementDescriptor<?> parent, int index) {
		super(type);
		this.parent = parent;
		this.index = index;
		this.parentType = parent.getType();
		if (parentType instanceof ROSarray) {
			if (index < 0)
				throw new IllegalArgumentException("Index out of (fixed size) bounds.");
		} else if (parentType instanceof ROSfixedArray) {
			if (index >= ((ROSfixedArray) parentType).getSize() || index < 0)
				throw new IllegalArgumentException("Index out of (fixed size) bounds.");
		} else {
			throw new IllegalArgumentException("Parent must be an array type.");
		}
	}

	/**
	 * Retrieves the position of the desired array element by skipping all preceding
	 * elements
	 */
	@Override
	public int getPosition(byte[] data, int start) {
		int pos = parent.getPosition(data, start);
		if (parentType instanceof ROSarray)
			return ((ROSarray) parentType).skipToIndex(index, data, pos);
		else if (parentType instanceof ROSfixedArray)
			return ((ROSfixedArray) parentType).skipToIndex(index, data, pos);
		else
			return -1;
	}

}

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2016-2019 ISSE, University of Augsburg 
 */

package de.isse.jros.fields;

import java.util.HashMap;
import java.util.Map;

import de.isse.jros.descriptors.ArrayElementDescriptor;
import de.isse.jros.descriptors.ElementDescriptor;
import de.isse.jros.types.ROSType;
import de.isse.jros.types.ROSarray;
import de.isse.jros.types.ROSfixedArray;

/**
 * Definition of an array within a ROS message
 */
public class RArray<T extends RField> extends RField {

	private ROSType<?> type;
	private T prototype;

	private Map<Integer, T> children = new HashMap<>();

	/**
	 * Creates a new fixed-size array
	 */
	private RArray(T prototype, int size) {
		super(() -> RArray.fixed(prototype.duplicate(), size));
		prototype.setParent(this);
		this.prototype = prototype;
		this.type = new ROSfixedArray(prototype.getType(), size);
	}

	/**
	 * Creates a new variable-size array
	 */
	private RArray(T prototype) {
		super(() -> RArray.variable(prototype.duplicate()));
		prototype.setParent(this);
		this.prototype = prototype;
		this.type = new ROSarray(prototype.getType());
	}

	/**
	 * Creates a new fixed-size array
	 * 
	 * @param type type of array elements
	 * @param size szie of the array
	 */
	public static <T extends RField> RArray<T> fixed(T type, int size) {
		return new RArray<T>(type, size);
	}

	/**
	 * Creates a new variable-size array
	 * 
	 * @param type type of array elements
	 */
	public static <T extends RField> RArray<T> variable(T type) {
		return new RArray<T>(type);
	}

	@Override
	public ROSType<?> getType() {
		return type;
	}

	/**
	 * Retrieves the size of the array from a given ROS binary message
	 * 
	 * @param message ROS binary message
	 * @return size of the array in the message
	 */
	public int size(byte[] message) {
		if (type instanceof ROSfixedArray)
			return ((ROSfixedArray) type).getSize();
		else if (type instanceof ROSarray)
			return (Integer) getDescriptor().read(message);
		else
			throw new IllegalArgumentException();
	}

	/**
	 * Writes the size of the array to a given ROS binary message
	 * 
	 * @param message ROS binary message
	 * @param size    new size of the array
	 * @throws IllegalArgumentException if the type is no variable-sized array
	 */
	@SuppressWarnings("unchecked")
	public void resize(byte[] message, int size) {
		if (type instanceof ROSfixedArray)
			throw new IllegalArgumentException();
		else if (type instanceof ROSarray)
			((ElementDescriptor<Integer>) getDescriptor()).write(message, size);
		else
			throw new IllegalArgumentException();
	}

	/**
	 * Retrieves the array element with a given index
	 * 
	 * @param index index of the element
	 * @return field within the array
	 */
	@SuppressWarnings("unchecked")
	public T get(int index) {
		if (!children.containsKey(index)) {
			T copy = (T) prototype.duplicate();
			copy.setParent(this);
			children.put(index, copy);
		}
		return children.get(index);
	}

	/**
	 * Retrieves the element descriptor for indexed children (cannot work on the
	 * prototype, only on children retrieved by {@link #get(int)}).
	 */
	@Override
	protected ElementDescriptor<?> findDescriptor(RField child) {
		if (child == prototype)
			throw new IllegalArgumentException();
		for (int key : children.keySet()) {
			if (children.get(key) == child)
				return new ArrayElementDescriptor<>(child.getType(), getDescriptor(), key);
		}
		throw new IllegalArgumentException();
	}

}

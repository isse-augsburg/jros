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
import de.isse.jros.types.ROSstruct;

/**
 * Descriptor pointing to a field within a message type, allowing to retrieve
 * and change its value within a ROS binary message
 * 
 * @param <T> type of field this descriptor points to
 */
public abstract class ElementDescriptor<T> {
	private ROSType<T> type;

	/**
	 * Type of the field this descriptor points to
	 */
	public ROSType<T> getType() {
		return type;
	}

	/**
	 * Creates an element descriptor of the given type
	 */
	protected ElementDescriptor(ROSType<T> type) {
		this.type = type;
	}

	/**
	 * Retrieves the value of the field this descriptor points to from a ROS binary
	 * message.
	 * 
	 * @param data ROS binary message
	 * @return value of the field this descriptor points to
	 */
	public T read(byte[] data) {
		return type.read(data, getPosition(data));
	}

	/**
	 * Updates the ROS binary message so that the field this descriptor points to
	 * contains the given value
	 * 
	 * @param data  ROS binary message
	 * @param value value to write to the field this descriptor points to
	 */
	public void write(byte[] data, T value) {
		type.write(data, getPosition(data), value);
	}

	/**
	 * Finds the position of this field in a ROS binary message
	 * 
	 * @param data  ROS binary message
	 * @param start start position of the surrounding type in the binary message
	 * @return position of the field this descriptor points to
	 */
	public abstract int getPosition(byte[] data, int start);

	/**
	 * Finds the position of this field in a ROS binary message
	 * 
	 * @param data ROS binary message
	 * @return position of the field this descriptor points to
	 */
	public int getPosition(byte[] data) {
		return getPosition(data, 0);
	}

	/**
	 * Creates a descriptor pointing to the given field in the given message type
	 * 
	 * @param <T>        type of element to point at
	 * @param structType type of messages this descriptor works on
	 * @param fieldType  type of the element to point at
	 * @param path       path to address the desired elements. For structs, this
	 *                   path String contains the name of the field, whereas for
	 *                   arrays the String contains the array index.
	 * @return Element descriptor for the given element
	 */
	@SuppressWarnings("unchecked")
	public static <T> ElementDescriptor<T> createFor(ROSType<?> structType, ROSType<T> fieldType, String... path) {
		ElementDescriptor<?> desc = new RootElementDescriptor<>(structType);
		ROSType<?> cur = structType;
		for (int i = 0; i < path.length; i++) {
			if (cur instanceof ROSstruct) {
				cur = ((ROSstruct) cur).getFieldType(path[i]);
				desc = new StructElementDescriptor<>(cur, desc, path[i]);
			} else if (cur instanceof ROSarray) {
				cur = ((ROSarray) cur).getPrototype();
				desc = new ArrayElementDescriptor<>(cur, desc, Integer.parseInt(path[i]));
			} else if (cur instanceof ROSfixedArray) {
				cur = ((ROSfixedArray) cur).getPrototype();
				desc = new ArrayElementDescriptor<>(cur, desc, Integer.parseInt(path[i]));
			}
		}
		if (cur.getName().equals(fieldType.getName()))
			return (ElementDescriptor<T>) desc;
		else
			return null;
	}

	/**
	 * Creates an (untyped) descriptor pointing to the given field in the given
	 * message type
	 * 
	 * @param structType type of messages this descriptor works on
	 * @param path       path to address the desired elements. For structs, this
	 *                   path String contains the name of the field, whereas for
	 *                   arrays the String contains the array index.
	 * @return Element descriptor for the given element
	 */
	public static ElementDescriptor<?> createFor(ROSType<?> structType, String... path) {
		ElementDescriptor<?> desc = new RootElementDescriptor<>(structType);
		ROSType<?> cur = structType;
		for (int i = 0; i < path.length; i++) {
			if (cur instanceof ROSstruct) {
				cur = ((ROSstruct) cur).getFieldType(path[i]);
				desc = new StructElementDescriptor<>(cur, desc, path[i]);
			} else if (cur instanceof ROSarray) {
				cur = ((ROSarray) cur).getPrototype();
				desc = new ArrayElementDescriptor<>(cur, desc, Integer.parseInt(path[i]));
			} else if (cur instanceof ROSfixedArray) {
				cur = ((ROSfixedArray) cur).getPrototype();
				desc = new ArrayElementDescriptor<>(cur, desc, Integer.parseInt(path[i]));
			}
		}
		return desc;
	}

}

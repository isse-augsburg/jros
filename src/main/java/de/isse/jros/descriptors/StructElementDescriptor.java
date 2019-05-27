/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2016-2019 ISSE, University of Augsburg 
 */

package de.isse.jros.descriptors;

import de.isse.jros.types.ROSType;
import de.isse.jros.types.ROSstruct;

/**
 * Descriptor for a field within a struct
 *
 * @param <T> type of element this descriptor points to
 */
public class StructElementDescriptor<T> extends ElementDescriptor<T> {
	private ElementDescriptor<?> parent;
	private String field;

	/**
	 * Creates a struct element descriptor for a field of the struct
	 * 
	 * @param type   type of element this descriptor points at
	 * @param parent element descriptor for the struct to find the element in
	 * @param field  name of the element in the struct
	 */
	public StructElementDescriptor(ROSType<T> type, ElementDescriptor<?> parent, String field) {
		super(type);
		this.parent = parent;
		this.field = field;
		if (parent.getType() instanceof ROSstruct) {
			if (((ROSstruct) parent.getType()).getFieldType(field) == null)
				throw new IllegalArgumentException("Struct element " + field + " does not exist.");
		} else {
			throw new IllegalArgumentException("Parent must be a struct type.");
		}
	}

	/**
	 * Retrieves the position of the desired struct field by skipping all preceding
	 * fields
	 */
	@Override
	public int getPosition(byte[] data, int start) {
		int pos = parent.getPosition(data, start);
		if (parent.getType() instanceof ROSstruct)
			return ((ROSstruct) parent.getType()).skipToField(field, data, pos);
		else
			return -1;
	}

}

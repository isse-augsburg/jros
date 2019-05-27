/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2016-2019 ISSE, University of Augsburg 
 */

package de.isse.jros.fields;

import java.util.function.Supplier;

import de.isse.jros.descriptors.ElementDescriptor;
import de.isse.jros.descriptors.RootElementDescriptor;
import de.isse.jros.types.ROSType;

/**
 * Definition of a field within a ROS message, supporting convenient access to
 * the corresponding element descriptor
 */
public abstract class RField {

	private Supplier<RField> cloner;

	/**
	 * Creates a new field with a given cloner (to create more instances of this
	 * type)
	 */
	protected RField(Supplier<RField> cloner) {
		this.cloner = cloner;
	}

	/**
	 * Sets the cloner (to create more instances of this type)
	 */
	protected void setCloner(Supplier<RField> cloner) {
		this.cloner = cloner;
	}

	private RField parent = null;

	/**
	 * Sets the container this field appears in
	 */
	protected void setParent(RField parent) {
		this.parent = parent;
	}

	/**
	 * Retrieves the type of the field
	 */
	public abstract ROSType<?> getType();

	/**
	 * Retrieves the descriptor of the field
	 */
	public ElementDescriptor<?> getDescriptor() {
		if (parent == null)
			return new RootElementDescriptor<>(getType());
		else
			return parent.findDescriptor(this);
	}

	/**
	 * Retrieves the element descriptor of the given child within this field
	 */
	protected ElementDescriptor<?> findDescriptor(RField child) {
		return null;
	}

	/**
	 * Duplicates the field (for use with arrays)
	 */
	protected RField duplicate() {
		return cloner.get();
	}

	/**
	 * Formats the value of this field within a ROS binary message as a JSON string
	 * 
	 * @param message to extract the field from
	 * @return JSON representation of the value
	 */
	public String toString(byte[] message) {
		return getType().toString(message);
	}

}

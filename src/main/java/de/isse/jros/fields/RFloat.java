/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2016-2019 ISSE, University of Augsburg 
 */

package de.isse.jros.fields;

import java.util.function.Supplier;

import de.isse.jros.descriptors.ElementDescriptor;
import de.isse.jros.types.ROSType;
import de.isse.jros.types.ROSfloat32;
import de.isse.jros.types.ROSfloat64;

/**
 * Definition of a float value within a ROS message
 */
public class RFloat extends RField {

	private ROSType<?> type;

	@Override
	public ROSType<?> getType() {
		return type;
	}

	/**
	 * Creates a new floating point field of the given type
	 */
	private RFloat(ROSType<?> type, Supplier<RField> cloner) {
		super(cloner);
		this.type = type;
	}

	/**
	 * Creates a new 64 bit floating point field
	 */
	public static RFloat float64() {
		return new RFloat(ROSfloat64.TYPE, RFloat::float64);
	}

	/**
	 * Creates a new 32 bit floating point field
	 */
	public static RFloat float32() {
		return new RFloat(ROSfloat32.TYPE, RFloat::float32);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ElementDescriptor<Double> getDescriptor() {
		return (ElementDescriptor<Double>) super.getDescriptor();
	}

	/**
	 * Retrieves the floating point value of this field from a ROS binary message
	 * 
	 * @param message ROS binary message to read the value from
	 * @return value from the message
	 */
	public double read(byte[] message) {
		Double data = getDescriptor().read(message);
		return data.doubleValue();
	}

	/**
	 * Writes a floating point value to this field in a ROS binary message
	 * 
	 * @param message ROS binary message to write the value to
	 * @param value   value to write
	 */
	public void write(byte[] message, double value) {
		getDescriptor().write(message, value);
	}

}

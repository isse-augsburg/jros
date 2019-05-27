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
import de.isse.jros.types.ROSbyte;
import de.isse.jros.types.ROSchar;
import de.isse.jros.types.ROSint16;
import de.isse.jros.types.ROSint32;
import de.isse.jros.types.ROSint64;
import de.isse.jros.types.ROSint8;
import de.isse.jros.types.ROSuint16;
import de.isse.jros.types.ROSuint32;
import de.isse.jros.types.ROSuint64;
import de.isse.jros.types.ROSuint8;

/**
 * Definition of an integer value within a ROS message
 */
public class RInteger extends RField {

	private ROSType<?> type;

	/**
	 * Creates a new integer field of the given type
	 */
	private RInteger(ROSType<?> type, Supplier<RField> cloner) {
		super(cloner);
		this.type = type;
	}

	/**
	 * Creates a new 8 bit signed integer field
	 */
	public static RInteger int8() {
		return new RInteger(ROSint8.TYPE, RInteger::int8);
	}

	/**
	 * Creates a new 16 bit signed integer field
	 */
	public static RInteger int16() {
		return new RInteger(ROSint16.TYPE, RInteger::int16);
	}

	/**
	 * Creates a new 32 bit signed integer field
	 */
	public static RInteger int32() {
		return new RInteger(ROSint32.TYPE, RInteger::int32);
	}

	/**
	 * Creates a new 64 bit signed integer field
	 */
	public static RInteger int64() {
		return new RInteger(ROSint64.TYPE, RInteger::int64);
	}

	/**
	 * Creates a new byte field
	 */
	public static RInteger _byte() {
		return new RInteger(ROSbyte.TYPE, RInteger::_byte);
	}

	/**
	 * Creates a new char field
	 */
	public static RInteger _char() {
		return new RInteger(ROSchar.TYPE, RInteger::_char);
	}

	/**
	 * Creates a new 8 bit unsigned integer field
	 */
	public static RInteger uint8() {
		return new RInteger(ROSuint8.TYPE, RInteger::uint8);
	}

	/**
	 * Creates a new 16 bit unsigned integer field
	 */
	public static RInteger uint16() {
		return new RInteger(ROSuint16.TYPE, RInteger::int16);
	}

	/**
	 * Creates a new 32 bit unsigned integer field
	 */
	public static RInteger uint32() {
		return new RInteger(ROSuint32.TYPE, RInteger::int32);
	}

	/**
	 * Creates a new 64 bit unsigned integer field.
	 * 
	 * Note: RInteger treats this type as Long, i.e. 64 bit signed.
	 */
	public static RInteger uint64() {
		return new RInteger(ROSuint64.TYPE, RInteger::int64);
	}

	@Override
	public ROSType<?> getType() {
		return type;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ElementDescriptor<Long> getDescriptor() {
		return (ElementDescriptor<Long>) super.getDescriptor();
	}

	/**
	 * Retrieves the integer value of this field from a ROS binary message
	 * 
	 * @param message ROS binary message to read the value from
	 * @return value from the message
	 */
	public long read(byte[] message) {
		Long ret = getDescriptor().read(message);
		return ret.longValue();
	}

	/**
	 * Writes a integer value to this field in a ROS binary message
	 * 
	 * @param message ROS binary message to write the value to
	 * @param value   value to write
	 */
	public void write(byte[] message, long value) {
		getDescriptor().write(message, value);
	}

}

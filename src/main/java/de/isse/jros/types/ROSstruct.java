/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2016-2019 ISSE, University of Augsburg 
 */

package de.isse.jros.types;

/**
 * A ROS struct
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ROSstruct extends ROSType<Void> {
	private String type;
	private Map<String, ROSType<?>> constants = new LinkedHashMap<String, ROSType<?>>();
	private Map<String, String> constantValues = new HashMap<String, String>();
	private Map<String, ROSType<?>> fields = new LinkedHashMap<String, ROSType<?>>();

	@Override
	public String getName() {
		return type;
	}

	/**
	 * Creates a ROS struct
	 * 
	 * @param type           name of the struct type
	 * @param constants      names and types of defined constants
	 * @param constantValues values of the defined constants
	 * @param fields         names and types of fields
	 */
	public ROSstruct(String type, Map<String, ROSType<?>> constants, Map<String, String> constantValues,
			Map<String, ROSType<?>> fields) {
		this.type = type;
		if (constants != null)
			this.constants.putAll(constants);
		if (constantValues != null)
			this.constantValues.putAll(constantValues);
		if (fields != null)
			this.fields.putAll(fields);
	}

	/**
	 * Creates an empty ROS struct
	 * 
	 * @param type name of the struct type
	 */
	public ROSstruct(String type) {
		this(type, null, null, null);
	}

	@Override
	public Void fromConstant(String value) {
		return null;
	}

	/**
	 * Retrieves the names of available constants
	 */
	public List<String> getConstantNames() {
		return new ArrayList<String>(constants.keySet());
	}

	/**
	 * Retrieves the type of a constant
	 * 
	 * @param name name of the constant
	 * @return type (object) of the constant
	 */
	public ROSType<?> getConstantType(String name) {
		return constants.get(name);
	}

	/**
	 * Retrieves the value of a constant
	 * 
	 * @param name name of the constant
	 * @return (string representation of the) value of the constant
	 */
	public String getConstantValue(String name) {
		return constantValues.get(name);
	}

	/**
	 * Retrieves the names of available fields
	 */
	public List<String> getFieldNames() {
		return new ArrayList<String>(fields.keySet());
	}

	/**
	 * Retrieves the type (object) of a field
	 * 
	 * @param name name of the constant
	 * @return type (object) of the field
	 */
	public ROSType<?> getFieldType(String name) {
		return fields.get(name);
	}

	/**
	 * Creates a struct that includes an additional constant
	 * 
	 * @param name     name of the constant
	 * @param type     type of the constant
	 * @param rawValue string representation of the value
	 * @return ROSstruct with the additional constant
	 */
	public ROSstruct withConstant(String name, ROSType<?> type, String rawValue) {
		Map<String, ROSType<?>> constants = new LinkedHashMap<String, ROSType<?>>(this.constants);
		constants.put(name, type);
		Map<String, String> constantValues = new HashMap<String, String>(this.constantValues);
		constantValues.put(name, rawValue);
		return new ROSstruct(name, constants, constantValues, fields);
	}

	/**
	 * Creates a struct that includes an additional field
	 * 
	 * @param name name of the field
	 * @param type type of the field
	 * @return ROSstruct with the additional field
	 */
	public ROSstruct withField(String name, ROSType<?> type) {
		Map<String, ROSType<?>> fields = new LinkedHashMap<String, ROSType<?>>(this.fields);
		fields.put(name, type);
		return new ROSstruct(getName(), constants, constantValues, fields);
	}

	@Override
	public String toString() {
		return fields.toString();
	}

	/**
	 * Creates a struct that includes an additional string field
	 * 
	 * @param name name of the field
	 * @return ROSstruct with the additional field
	 */
	public ROSstruct withString(String name) {
		return withField(name, new ROSstring());
	}

	/**
	 * Creates a struct that includes an additional int64 field
	 * 
	 * @param name name of the field
	 * @return ROSstruct with the additional field
	 */
	public ROSstruct withInt64(String name) {
		return withField(name, new ROSint64());
	}

	/**
	 * Creates a struct that includes an additional int32 field
	 * 
	 * @param name name of the field
	 * @return ROSstruct with the additional field
	 */
	public ROSstruct withInt32(String name) {
		return withField(name, ROSint32.TYPE);
	}

	/**
	 * Creates a struct that includes an additional int16 field
	 * 
	 * @param name name of the field
	 * @return ROSstruct with the additional field
	 */
	public ROSstruct withInt16(String name) {
		return withField(name, new ROSint16());
	}

	/**
	 * Creates a struct that includes an additional int8 field
	 * 
	 * @param name name of the field
	 * @return ROSstruct with the additional field
	 */
	public ROSstruct withInt8(String name) {
		return withField(name, new ROSint8());
	}

	/**
	 * Creates a struct that includes an additional uint64 field
	 * 
	 * @param name name of the field
	 * @return ROSstruct with the additional field
	 */
	public ROSstruct withUint64(String name) {
		return withField(name, new ROSuint64());
	}

	/**
	 * Creates a struct that includes an additional uint32 field
	 * 
	 * @param name name of the field
	 * @return ROSstruct with the additional field
	 */
	public ROSstruct withUint32(String name) {
		return withField(name, new ROSuint32());
	}

	/**
	 * Creates a struct that includes an additional uint16 field
	 * 
	 * @param name name of the field
	 * @return ROSstruct with the additional field
	 */
	public ROSstruct withUint16(String name) {
		return withField(name, new ROSuint16());
	}

	/**
	 * Creates a struct that includes an additional uint8 field
	 * 
	 * @param name name of the field
	 * @return ROSstruct with the additional field
	 */
	public ROSstruct withUint8(String name) {
		return withField(name, new ROSuint8());
	}

	/**
	 * Creates a struct that includes an additional char field
	 * 
	 * @param name name of the field
	 * @return ROSstruct with the additional field
	 */
	public ROSstruct withChar(String name) {
		return withField(name, new ROSchar());
	}

	/**
	 * Creates a struct that includes an additional byte field
	 * 
	 * @param name name of the field
	 * @return ROSstruct with the additional field
	 */
	public ROSstruct withByte(String name) {
		return withField(name, new ROSbyte());
	}

	/**
	 * Creates a struct that includes an additional float64 field
	 * 
	 * @param name name of the field
	 * @return ROSstruct with the additional field
	 */
	public ROSstruct withFloat64(String name) {
		return withField(name, new ROSfloat64());
	}

	/**
	 * Creates a struct that includes an additional float32 field
	 * 
	 * @param name name of the field
	 * @return ROSstruct with the additional field
	 */
	public ROSstruct withFloat32(String name) {
		return withField(name, new ROSfloat32());
	}

	/**
	 * Creates a struct that includes an additional boolean field
	 * 
	 * @param name name of the field
	 * @return ROSstruct with the additional field
	 */
	public ROSstruct withBool(String name) {
		return withField(name, new ROSbool());
	}

	/**
	 * Creates a struct that includes an additional duration field
	 * 
	 * @param name name of the field
	 * @return ROSstruct with the additional field
	 */
	public ROSstruct withDuration(String name) {
		return withField(name, new ROSduration());
	}

	/**
	 * Creates a struct that includes an additional time field
	 * 
	 * @param name name of the field
	 * @return ROSstruct with the additional field
	 */
	public ROSstruct withTime(String name) {
		return withField(name, new ROStime());
	}

	@Override
	public Void read(byte[] data, int position) {
		return null;
	}

	@Override
	public void write(byte[] data, int position, Void value) {
		return;
	}

	@Override
	public int skip(byte[] data, int position) {
		for (ROSType<?> field : fields.values())
			position = field.skip(data, position);
		return position;
	}

	/**
	 * Calculates the position where the field with the given name starts
	 * 
	 * @param name     field name to skip to
	 * @param data     ROS binary message representation
	 * @param position start position of the struct
	 * @return position of the element with the given name
	 */
	public int skipToField(String name, byte[] data, int position) {
		for (Entry<String, ROSType<?>> field : fields.entrySet()) {
			if (field.getKey().equals(name)) {
				return position;
			} else {
				position = field.getValue().skip(data, position);
			}
		}
		return -1;
	}

	@Override
	public String toString(byte[] data, int position) {
		StringBuffer ret = new StringBuffer().append("{");
		boolean first = true;
		for (Entry<String, ROSType<?>> field : fields.entrySet()) {
			if (first)
				first = false;
			else
				ret.append(",");
			ret.append(field.getKey()).append(":");
			ret.append(field.getValue().toString(data, position));
			position = field.getValue().skip(data, position);
		}
		return ret.append("}").toString();
	}

}

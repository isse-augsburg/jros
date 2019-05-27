/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2016-2019 ISSE, University of Augsburg 
 */

package de.isse.jros;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import de.isse.jros.types.ROSType;
import de.isse.jros.types.ROSarray;
import de.isse.jros.types.ROSbool;
import de.isse.jros.types.ROSbyte;
import de.isse.jros.types.ROSchar;
import de.isse.jros.types.ROSduration;
import de.isse.jros.types.ROSfixedArray;
import de.isse.jros.types.ROSfloat32;
import de.isse.jros.types.ROSfloat64;
import de.isse.jros.types.ROSint16;
import de.isse.jros.types.ROSint32;
import de.isse.jros.types.ROSint64;
import de.isse.jros.types.ROSint8;
import de.isse.jros.types.ROSstring;
import de.isse.jros.types.ROSstruct;
import de.isse.jros.types.ROStime;
import de.isse.jros.types.ROSuint16;
import de.isse.jros.types.ROSuint32;
import de.isse.jros.types.ROSuint64;
import de.isse.jros.types.ROSuint8;

/**
 * Helper class for working with ROS message type objects: register named types,
 * compute MD5 checksum, convert from and to message definitions
 */
public class MessageHelper {

	private static Map<String, ROSType<?>> prototypes = initPrototypes(ROSbool.TYPE, ROSduration.TYPE, ROSfloat32.TYPE,
			ROSfloat64.TYPE, ROSint8.TYPE, ROSint16.TYPE, ROSint32.TYPE, ROSint64.TYPE, ROSuint8.TYPE, ROSuint16.TYPE,
			ROSuint32.TYPE, ROSuint64.TYPE, ROSstring.TYPE, ROStime.TYPE, ROSbyte.TYPE, ROSchar.TYPE, createHeader());

	/**
	 * Creates a ROSstruct for the builtin Header type
	 */
	public static ROSstruct createHeader() {
		return new ROSstruct("Header").withUint32("seq").withTime("stamp").withString("frame_id");
	}

	/**
	 * Retrieves the registered type object with the given name
	 * 
	 * @param name name of the message type
	 * @return registered message prototype
	 */
	public static ROSType<?> getPrototype(String name) {
		return prototypes.get(name);
	}

	static Map<String, ROSType<?>> initPrototypes(ROSType<?>... prototypes) {
		Map<String, ROSType<?>> ret = new HashMap<String, ROSType<?>>();
		for (ROSType<?> p : prototypes) {
			ret.put(p.getName(), p);
		}
		return ret;
	}

	/**
	 * Retrieves the recursive type definition (with indented subtype definitions,
	 * as seen in rosmsg show)
	 * 
	 * @param message       message type
	 * @param useLocalNames decides whether all types should be written with package
	 *                      name (false), or if the package name of types in the
	 *                      same package as the given message should be omitted
	 * @return recursive type definition
	 */
	public static String getRecursiveStructDefinition(ROSstruct message, boolean useLocalNames) {
		return getRecursiveStructDefinition("", message, useLocalNames);
	}

	/**
	 * Creates the recursive type definition (with indented subtype definitions) and
	 * a prefix added in each line (for use with subtypes)
	 */
	private static String getRecursiveStructDefinition(String prefix, ROSstruct message, boolean useLocalNames) {
		StringBuffer ret = new StringBuffer();
		String namespace = useLocalNames ? getNamespace(message) : null;

		for (String name : message.getConstantNames()) {
			ret.append(prefix).append(getConstantDefinition(message, name));
		}

		for (String name : message.getFieldNames()) {
			ret.append(prefix).append(getFieldDefinition(message, name, namespace));
			ROSType<?> type = message.getFieldType(name);
			ROSstruct struct = getIncludedStruct(type);
			if (struct != null)
				ret.append(getRecursiveStructDefinition("  " + prefix, struct, useLocalNames));
		}

		return ret.toString();
	}

	/**
	 * Finds the struct type contained in a type (e.g. array)
	 * 
	 * @param value type to get the struct type for
	 * @return the struct type contained in the given type, or null if none
	 */
	private static ROSstruct getIncludedStruct(ROSType<?> value) {
		if (value instanceof ROSstruct) {
			return (ROSstruct) value;
		} else if (value instanceof ROSarray) {
			return getIncludedStruct(((ROSarray) value).getPrototype());
		} else if (value instanceof ROSfixedArray) {
			return getIncludedStruct(((ROSfixedArray) value).getPrototype());
		}
		return null;
	}

	/**
	 * Retrieves the namespace of a struct
	 * 
	 * @param message struct to get the namespace for
	 * @return namespace, if available, otherwise an empty string
	 */
	private static String getNamespace(ROSstruct message) {
		if (message.getName().contains("/"))
			return message.getName().substring(0, message.getName().lastIndexOf("/") + 1);
		return "";
	}

	/**
	 * Creates the definition of a field in a struct
	 * 
	 * @param message         struct to get the field definition for
	 * @param name            name of the field
	 * @param parentNamespace namespace of the context (for local names)
	 * @return line defining the field
	 */
	private static String getFieldDefinition(ROSstruct message, String name, String parentNamespace) {
		StringBuffer ret = new StringBuffer();
		String type = message.getFieldType(name).getName();
		type = getLocalName(type, parentNamespace);
		ret.append(type).append(" ").append(name).append("\n");
		return ret.toString();
	}

	/**
	 * Retrieves the (relative) name of a type when used in a given namespace
	 * 
	 * @param type            name of the type to use
	 * @param parentNamespace namespace to use the type in
	 * @return the given type name if it is not in the given parent namespace, or
	 *         type name without namespace if it is in the given namespace
	 */
	private static String getLocalName(String type, String parentNamespace) {
		if (parentNamespace == null) {
			if (type.equals("Header"))
				return "std_msgs/Header";
			else
				return type;
		}
		if (type.startsWith(parentNamespace))
			type = type.substring(parentNamespace.length());
		if (type.equals("std_msgs/Header"))
			type = "Header";
		return type;
	}

	/**
	 * Creates the definition of a constant in a struct
	 * 
	 * @param message struct to get the constant definition for
	 * @param name    name of the constant
	 * @return line defining the constant
	 */
	private static String getConstantDefinition(ROSstruct message, String name) {
		StringBuffer ret = new StringBuffer();
		ROSType<?> type = message.getConstantType(name);
		String rawValue = message.getConstantValue(name);
		ret.append(type.getName()).append(" ").append(name).append("=").append(rawValue).append("\n");
		return ret.toString();
	}

	/**
	 * Retrieves the type definition (without indented subtype definitions, as seen
	 * in rosmsg show)
	 * 
	 * @param message       message type
	 * @param useLocalNames decides whether all types should be written with package
	 *                      name (false), or if the package name of types in the
	 *                      same package as the given message should be omitted
	 * @return type definition
	 */
	public static String getSingleStructDefinition(ROSstruct message, boolean useLocalNames) {
		StringBuffer ret = new StringBuffer();
		String namespace = useLocalNames ? getNamespace(message) : null;
		for (String name : message.getConstantNames()) {
			ret.append(getConstantDefinition(message, name));
		}
		for (String name : message.getFieldNames()) {
			ret.append(getFieldDefinition(message, name, namespace));
		}
		return ret.toString();
	}

	/**
	 * Retrieves the type definition as seen in msg files, including the definitions
	 * for all used subtypes
	 * 
	 * @param message       message type
	 * @param useLocalNames decides whether all types should be written with package
	 *                      name (false), or if the package name of types in the
	 *                      same package as the given message should be omitted
	 * @return type definition
	 */
	public static String getStructDefinitionWithDependencies(ROSstruct message, boolean useLocalNames) {
		StringBuilder ret = new StringBuilder();
		List<ROSstruct> todo = new ArrayList<ROSstruct>();
		List<String> done = new ArrayList<String>();
		todo.add(message);
		while (!todo.isEmpty()) {
			ROSstruct msg = todo.remove(0);
			if (done.contains(msg.getName()))
				continue;
			if (msg != message) {
				ret.append("\n");
				ret.append("================================================================================\n");
				ret.append("MSG: ").append(getLocalName(msg.getName(), "")).append("\n");
			}
			ret.append(getSingleStructDefinition(msg, useLocalNames));
			done.add(msg.getName());
			for (String name : msg.getFieldNames()) {
				ROSType<?> type = msg.getFieldType(name);
				ROSstruct struct = getIncludedStruct(type);
				if (struct != null)
					todo.add(struct);
			}
		}
		return ret.toString();
	}

	/**
	 * Retrieves the type definition as used to compute the md5 checksum
	 * 
	 * @param message message type
	 * @return type definition for md5 checksum
	 */
	public static String getStructDefinitionForMD5(ROSstruct message) {
		StringBuffer ret = new StringBuffer();
		for (String name : message.getConstantNames()) {
			ROSType<?> type = message.getConstantType(name);
			String rawValue = message.getConstantValue(name);
			ret.append(type.getName()).append(" ").append(name).append("=").append(rawValue).append("\n");
		}
		for (String name : message.getFieldNames()) {
			ROSType<?> rtype = message.getFieldType(name);
			String type = rtype.getName();
			ROSstruct struct = getIncludedStruct(rtype);
			if (struct != null)
				type = getStructMD5(struct);
			ret.append(type).append(" ").append(name).append("\n");
		}
		return ret.toString();
	}

	/**
	 * Retrieves the md5 checksum for a given type
	 * 
	 * @param message message type
	 * @return md5 checksum for the type
	 */
	public static String getStructMD5(ROSstruct message) {
		String def = getStructDefinitionForMD5(message);
		try {
			MessageDigest md;
			md = MessageDigest.getInstance("MD5");
			byte[] bytes = md.digest(def.toString().trim().getBytes());
			StringBuffer ret = new StringBuffer();
			for (int i = 0; i < bytes.length; i++) {
				ret.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			return ret.toString();
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}

	/**
	 * Registers a message type from a definition with dependencies
	 * 
	 * @param type       name of the type to register
	 * @param definition message definition (as seen in msg file) with dependencies
	 */
	public static void registerStructFromMessageDefinitionWithDependencies(String type, String definition) {
		String[] types = (type + "\n" + definition).split("======+\nMSG: ");
		for (int i = types.length - 1; i >= 0; i--) {
			String[] parts = types[i].split("\n", 2);
			String name = parts[0], def = parts[1];
			if (prototypes.get(type) == null) {
				registerStructFromMessageDefinition(name, def);
			}
		}
	}

	/**
	 * Registers a message type from a message definition
	 * 
	 * @param type       name of the type to register
	 * @param definition message definition (as seen in msg file)
	 */
	public static void registerStructFromMessageDefinition(String type, String definition) {
		prototypes.put(type, instantiateStructFromMessageDefinition(type, definition));
	}

	/**
	 * Retrieves the type object for a given name from a given map of prototypes
	 * 
	 * @param type       type to get the object for
	 * @param context    context to search the type in (to resolve local names)
	 * @param prototypes map of type objects
	 * @return Type object
	 */
	private static ROSType<?> getPrototype(String type, String context, Map<String, ROSType<?>> prototypes) {
		if (prototypes.containsKey(type))
			return prototypes.get(type);
		if (!type.contains("/") && context.contains("/")) {
			type = context.substring(0, context.lastIndexOf("/") + 1) + type;
			if (prototypes.containsKey(type))
				return prototypes.get(type);
		}
		if (type.equals("std_msgs/Header")) {
			return prototypes.get("Header");
		}
		return null;
	}

	/**
	 * Creates a type object from a given message definition with dependencies
	 * 
	 * @param type       name of the type
	 * @param definition message definition (as seen in msg file) with dependencies
	 * @return Type object
	 */
	public static ROSstruct instantiateStructFromMessageDefinitionWithDependencies(String type, String definition) {
		String[] types = (type + "\n" + definition).split("======+\nMSG: ");
		Map<String, ROSType<?>> known = new HashMap<>(prototypes);
		for (int i = types.length - 1; i >= 0; i--) {
			String[] parts = types[i].split("\n", 2);
			String name = parts[0], def = parts[1];
			if (prototypes.get(type) == null) {
				known.put(name, instantiateStructFromMessageDefinition(name, def, known));
			}
		}
		return (ROSstruct) known.get(type);
	}

	/**
	 * Creates a type object from a given message definition (using registered
	 * subtypes)
	 * 
	 * @param type       name of the type
	 * @param definition message definition (as seen in msg file)
	 * @return Type object
	 */
	public static ROSstruct instantiateStructFromMessageDefinition(String type, String definition) {
		return instantiateStructFromMessageDefinition(type, definition, prototypes);
	}

	/**
	 * Creates a type object from a given message definition and a map of prototypes
	 * for subtypes
	 * 
	 * @param type       name of the type
	 * @param definition message definition (as seen in msg file)
	 * @param prototypes map of type objects
	 * @return Type object
	 */
	public static ROSstruct instantiateStructFromMessageDefinition(String type, String definition,
			Map<String, ROSType<?>> prototypes) {
		StringTokenizer st = new StringTokenizer(definition, "\n");
		Map<String, ROSType<?>> constants = new LinkedHashMap<>();
		Map<String, String> constantValues = new LinkedHashMap<>();
		Map<String, ROSType<?>> fields = new LinkedHashMap<>();
		while (st.hasMoreTokens()) {
			String line = st.nextToken().trim();
			if (line.contains("#") && (!line.contains("=") || line.indexOf("=") > line.indexOf("#"))) {
				line = line.substring(0, line.indexOf("#"));
			}
			if (line.isEmpty())
				continue;
			String value = null;
			if (line.contains("=")) {
				value = line.substring(line.indexOf("=") + 1).trim();
				line = line.substring(0, line.indexOf("="));
			}

			if (!line.contains(" "))
				continue;
			String ftype = line.substring(0, line.indexOf(" ")).trim();
			String fname = line.substring(line.indexOf(" ") + 1).trim();
			if (value != null) {
				if (!ftype.equals("string")) {
					if (value.contains("#"))
						value = value.substring(0, value.indexOf("#"));
					value = value.trim();
				}
				constants.put(fname, getPrototype(ftype, type, prototypes));
				constantValues.put(fname, value);
			} else {
				if (ftype.contains("[")) {
					String idx = ftype.substring(ftype.indexOf("[") + 1);
					ftype = ftype.substring(0, ftype.indexOf("["));
					if (idx.equals("]")) {
						fields.put(fname, new ROSarray(getPrototype(ftype, type, prototypes)));
					} else {
						idx = idx.substring(0, idx.indexOf("]"));
						fields.put(fname,
								new ROSfixedArray(getPrototype(ftype, type, prototypes), Integer.parseInt(idx)));
					}
				} else {
					fields.put(fname, getPrototype(ftype, type, prototypes));
				}
			}
		}
		return new ROSstruct(type, constants, constantValues, fields);
	}

}

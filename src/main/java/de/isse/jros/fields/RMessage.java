/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2016-2019 ISSE, University of Augsburg 
 */

package de.isse.jros.fields;

import java.util.LinkedHashMap;
import java.util.Map;

import de.isse.jros.descriptors.ElementDescriptor;
import de.isse.jros.descriptors.StructElementDescriptor;
import de.isse.jros.types.ROSstruct;

/**
 * Definition of a struct for a ROS message. It can be used to define types in
 * code, allowing easy access to the corresponding fields. The type class should
 * call {@link #field(String, RField)}, {@link #array(String, RField)} and
 * {@link #array(String, int, RField)} before {@link #type(String)}.
 * 
 * <pre>
 * public class Header extends RMessage {
 * 	public final RInteger seq = field("seq", RInteger.uint32());
 * 	public final RTime stamp = field("stamp", RTime.time());
 * 	public final RString frame_id = field("frame_id", RString.string());
 * 	public final ROSstruct TYPE = type("std_msgs/Header");
 * }
 * </pre>
 */
public abstract class RMessage extends RField {

	/**
	 * Creates a new struct field that can be cloned by invoking the default
	 * constructor
	 */
	public RMessage() {
		super(null);
		setCloner(() -> {
			try {
				return getClass().getDeclaredConstructor().newInstance();
			} catch (Exception e) {
				return null;
			}
		});
	}

	private ROSstruct type = null;
	private Map<String, RField> fields = new LinkedHashMap<>();

	/**
	 * Adds a field to the struct
	 * 
	 * @param <T>     type of the field
	 * @param name    name of the field
	 * @param message field to add
	 * @return the added field
	 */
	protected <T extends RField> T field(String name, T message) {
		return add(name, message);
	}

	/**
	 * Adds a (variable size) array to the struct
	 * 
	 * @param <T>     type of the field
	 * @param name    name of the field
	 * @param message field to add
	 * @return the added field
	 */
	protected <T extends RField> RArray<T> array(String name, T type) {
		return add(name, RArray.variable(type));
	}

	/**
	 * Adds a (fixed size) array to the struct
	 * 
	 * @param <T>     type of the field
	 * @param name    name of the field
	 * @param size    size of the array
	 * @param message field to add
	 * @return the added field
	 */
	protected <T extends RField> RArray<T> array(String name, int size, T type) {
		return add(name, RArray.fixed(type, size));
	}

	/**
	 * Creates the ROStype object for this message (to be called after invoking all
	 * the {@link #field(String, RField)}, {@link #array(String, RField)},
	 * {@link #array(String, int, RField)} calls for fields)
	 * 
	 * @param type name of the type
	 * @return ROSstruct for this message
	 */
	protected ROSstruct type(String type) {
		ROSstruct struct = new ROSstruct(type);
		for (String key : fields.keySet())
			struct = struct.withField(key, fields.get(key).getType());
		this.type = struct;
		return struct;
	}

	/**
	 * Adds a field and sets its parent
	 */
	private <T extends RField> T add(String name, T instance) {
		instance.setParent(this);
		fields.put(name, instance);
		return instance;
	}

	@Override
	public ROSstruct getType() {
		return type;
	}

	@Override
	protected ElementDescriptor<?> findDescriptor(RField child) {
		for (String key : fields.keySet()) {
			if (fields.get(key) == child)
				return new StructElementDescriptor<>(child.getType(), getDescriptor(), key);
		}
		return null;
	}

}

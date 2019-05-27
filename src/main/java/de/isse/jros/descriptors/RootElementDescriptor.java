/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2016-2019 ISSE, University of Augsburg 
 */

package de.isse.jros.descriptors;

import de.isse.jros.types.ROSType;

/**
 * Element descriptor for the root type of a message
 *
 * @param <T> type of message
 */
public class RootElementDescriptor<T> extends ElementDescriptor<T> {

	/**
	 * Creates a descriptor for the root type of a message
	 * 
	 * @param type type to create the descriptor for
	 */
	public RootElementDescriptor(ROSType<T> type) {
		super(type);
	}

	/**
	 * The root element starts at the given start
	 */
	@Override
	public int getPosition(byte[] data, int start) {
		return start;
	}

}

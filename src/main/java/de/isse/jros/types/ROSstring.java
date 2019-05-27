/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2016-2019 ISSE, University of Augsburg 
 */

package de.isse.jros.types;

import java.io.UnsupportedEncodingException;

/**
 * A ROS string type
 */
public class ROSstring extends ROSType<String> {
	/**
	 * A ROS string type
	 */	
	public static final ROSstring TYPE = new ROSstring();

	/**
	 * ROSstring serves as singleton
	 */
	protected ROSstring() {
	}

	@Override
	public String getName() {
		return "string";
	}

	@Override
	public String fromConstant(String value) {
		return value.trim();
	}

	@Override
	public String read(byte[] data, int position) {
		try {
			int len = ROSint32.TYPE.read(data, position).intValue();
			return new String(data, position + 4, len, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public void write(byte[] data, int position, String value) {
		try {
			byte[] bs = value.getBytes("UTF-8");
			int oldLen = ROSint32.TYPE.read(data, position).intValue();
			int newLen = bs.length;
			ROSint32.TYPE.write(data, position, (long) bs.length);
			if (newLen > oldLen)
				moveBytesRight(data, position + 4, newLen - oldLen);
			else if (newLen < oldLen)
				moveBytesLeft(data, position + 4, oldLen - newLen);
			overwriteBytes(data, position + 4, bs);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public int skip(byte[] data, int position) {
		if (data == null)
			return position + 4;
		int len = ROSint32.TYPE.read(data, position).intValue();
		return position + 4 + len;
	}

	@Override
	public String toString(byte[] data, int position) {
		return "\"" + read(data, position).replaceAll("\\", "\\\\").replaceAll("\n", "\\n") + "\"";
	}
}

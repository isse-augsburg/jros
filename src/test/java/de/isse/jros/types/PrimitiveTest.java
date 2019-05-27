/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2016-2019 ISSE, University of Augsburg 
 */

package de.isse.jros.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import de.isse.jros.types.ROSType;
import de.isse.jros.types.ROSbool;
import de.isse.jros.types.ROSbyte;
import de.isse.jros.types.ROSchar;
import de.isse.jros.types.ROSduration;
import de.isse.jros.types.ROSfloat32;
import de.isse.jros.types.ROSfloat64;
import de.isse.jros.types.ROSint16;
import de.isse.jros.types.ROSint32;
import de.isse.jros.types.ROSint64;
import de.isse.jros.types.ROSstring;
import de.isse.jros.types.ROStime;
import de.isse.jros.types.ROSuint16;
import de.isse.jros.types.ROSuint32;
import de.isse.jros.types.ROSuint64;
import de.isse.jros.types.ROSuint8;

/**
 * Testing primitive ROSTypes: Checks if different values correctly written and
 * read from the binary representation
 */
public class PrimitiveTest {

	@Test
	public void testFloat32() throws IOException {
		testPrimitive(ROSfloat32.TYPE, 0.0, 3.5);
	}

	@Test
	public void testFloat64() throws IOException {
		testPrimitive(ROSfloat64.TYPE, 0.0, 3.5);
	}

	@Test
	public void testTime() throws IOException {
		testPrimitive(ROStime.TYPE, new int[] { 1, 2 }, new int[] { 3, 4 });
	}

	@Test
	public void testDuration() throws IOException {
		testPrimitive(ROSduration.TYPE, new int[] { 1, 2 }, new int[] { 3, 4 });
	}

	@Test
	public void testString() throws IOException {
		testPrimitive(ROSstring.TYPE, "xyz", "abcd");
	}

	@Test
	public void testBool() throws IOException {
		testPrimitive(ROSbool.TYPE, true, false);
	}

	@Test
	public void testChar() throws IOException {
		testPrimitive(ROSchar.TYPE, 1L, 2L);
	}

	@Test
	public void testByte() throws IOException {
		testPrimitive(ROSbyte.TYPE, 1L, 2L);
	}

	@Test
	public void testInt16() throws IOException {
		testPrimitive(ROSint16.TYPE, 1L, -1L);
	}

	@Test
	public void testInt32() throws IOException {
		testPrimitive(ROSint32.TYPE, 1L, -1L);
	}

	@Test
	public void testInt64() throws IOException {
		testPrimitive(ROSint64.TYPE, 1L, -1L);
	}

	@Test
	public void testUInt8() throws IOException {
		testPrimitive(ROSuint8.TYPE, 1L, 255L);
	}

	@Test
	public void testUInt16() throws IOException {
		testPrimitive(ROSuint16.TYPE, 1L, 65535L);
	}

	@Test
	public void testUInt32() throws IOException {
		testPrimitive(ROSuint32.TYPE, 1L, (1L << 31) + 1);
	}

	@Test
	public void testUInt64() throws IOException {
		testPrimitive(ROSuint64.TYPE, 1L, 1L << 40);
	}

	public <T> void testPrimitive(ROSType<T> type, T first, T second) throws IOException {
		byte[] msg = new byte[1000], msg2 = new byte[1000];

		type.write(msg, 0, first);
		type.write(msg2, 0, second);
		assertNotEquals(msg, msg2);

		int end1 = type.skip(msg, 0), end2 = type.skip(msg2, 0);
		for (int pos = end1; pos < msg.length; pos++)
			assertEquals(0, msg[pos]);
		for (int pos = end2; pos < msg2.length; pos++)
			assertEquals(0, msg2[pos]);

		assertEquals(toComparableString(first), toComparableString(type.read(msg, 0)));
		assertEquals(toComparableString(second), toComparableString(type.read(msg2, 0)));
	}

	private String toComparableString(Object obj) {
		if (obj instanceof int[]) {
			return Arrays.toString((int[]) obj);
		} else if (obj instanceof Object[]) {
			return Arrays.toString((Object[]) obj);
		}
		return obj.toString();
	}

}

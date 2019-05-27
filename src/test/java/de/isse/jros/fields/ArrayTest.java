/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2016-2019 ISSE, University of Augsburg 
 */

package de.isse.jros.fields;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Testing RArray: changing different length elements does not break other array
 * elements
 */
public class ArrayTest {

	@Test
	public void testStringArrayWrite() {
		byte[] msg = new byte[1000];
		RArray<RString> arr = RArray.variable(RString.string());
		arr.resize(msg, 3);
		arr.get(0).write(msg, "ab");
		arr.get(1).write(msg, "cd");
		arr.get(2).write(msg, "ef");

		assertEquals(arr.get(0).read(msg), "ab");
		assertEquals(arr.get(1).read(msg), "cd");
		assertEquals(arr.get(2).read(msg), "ef");

		arr.get(0).write(msg, "gh");
		arr.get(1).write(msg, "ij");
		arr.get(2).write(msg, "kl");

		assertEquals(arr.get(0).read(msg), "gh");
		assertEquals(arr.get(1).read(msg), "ij");
		assertEquals(arr.get(2).read(msg), "kl");

		arr.get(0).write(msg, "m");
		assertEquals(arr.get(0).read(msg), "m");
		assertEquals(arr.get(1).read(msg), "ij");

		arr.get(0).write(msg, "nop");
		assertEquals(arr.get(0).read(msg), "nop");
		assertEquals(arr.get(1).read(msg), "ij");
	}

	@Test
	public void testInt8ArrayWrite() {
		byte[] msg = new byte[1000];
		RArray<RInteger> arr = RArray.variable(RInteger.int8());
		arr.resize(msg, 4);

		for (int i = 0; i < 4; i++) {
			assertEquals(arr.get(i).read(msg), 0);
		}

		for (int i = 0; i < 4; i++) {
			arr.get(i).write(msg, i);
		}

		for (int i = 0; i < 4; i++) {
			assertEquals(arr.get(i).read(msg), i);
		}
	}

	@Test
	public void testInt8FixedArrayWrite() {
		byte[] msg = new byte[1000];
		RArray<RInteger> arr = RArray.fixed(RInteger.int8(), 6);

		for (int i = 0; i < 4; i++) {
			assertEquals(arr.get(i).read(msg), 0);
		}

		for (int i = 0; i < 4; i++) {
			arr.get(i).write(msg, i);
		}

		for (int i = 0; i < 4; i++) {
			assertEquals(arr.get(i).read(msg), i);
		}
	}

}

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2016-2019 ISSE, University of Augsburg 
 */

package de.isse.jros.fields;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.isse.jros.fields.RInteger;
import de.isse.jros.fields.RMessage;
import de.isse.jros.fields.RString;
import de.isse.jros.types.ROSstruct;

/**
 * Testing RMessage structs: changing variable-length elements does not break
 * other unrelated elements
 */
public class StructTest {

	class TestStruct extends RMessage {
		RInteger a = field("a", RInteger.int32());
		RString b = field("b", RString.string());
		RInteger c = field("c", RInteger.int32());
		ROSstruct TYPE = type("test");
	}

	@Test
	public void testStructWith() {
		byte[] msg = new byte[1000];
		TestStruct struct = new TestStruct();
		struct.a.write(msg, 0);
		struct.b.write(msg, "abc");
		struct.c.write(msg, 2);

		struct.a.write(msg, 3);
		assertEquals(struct.a.read(msg), 3);
		assertEquals(struct.b.read(msg), "abc");
		assertEquals(struct.c.read(msg), 2);

		struct.b.write(msg, "def");
		assertEquals(struct.a.read(msg), 3);
		assertEquals(struct.b.read(msg), "def");
		assertEquals(struct.c.read(msg), 2);

		struct.b.write(msg, "g");
		assertEquals(struct.a.read(msg), 3);
		assertEquals(struct.b.read(msg), "g");
		assertEquals(struct.c.read(msg), 2);
	}

	class NestedStruct extends RMessage {
		RString a = field("a", RString.string());
		TestStruct b = field("b", new TestStruct());
		RString c = field("c", RString.string());
		ROSstruct type = type("nested");
	}

	@Test
	public void testNestedStructWith() {
		byte[] msg = new byte[1000];
		NestedStruct struct = new NestedStruct();
		struct.a.write(msg, "start");
		struct.b.a.write(msg, 0);
		struct.b.b.write(msg, "abc");
		struct.b.c.write(msg, 0);
		struct.c.write(msg, "end");

		assertEquals(struct.a.read(msg), "start");
		assertEquals(struct.b.a.read(msg), 0);
		assertEquals(struct.b.b.read(msg), "abc");
		assertEquals(struct.b.c.read(msg), 0);
		assertEquals(struct.c.read(msg), "end");

		struct.b.b.write(msg, "def");
		assertEquals(struct.a.read(msg), "start");
		assertEquals(struct.b.a.read(msg), 0);
		assertEquals(struct.b.b.read(msg), "def");
		assertEquals(struct.b.c.read(msg), 0);
		assertEquals(struct.c.read(msg), "end");

		struct.b.b.write(msg, "g");
		assertEquals(struct.a.read(msg), "start");
		assertEquals(struct.b.a.read(msg), 0);
		assertEquals(struct.b.b.read(msg), "g");
		assertEquals(struct.b.c.read(msg), 0);
		assertEquals(struct.c.read(msg), "end");

		struct.b.b.write(msg, "hi");
		assertEquals(struct.a.read(msg), "start");
		assertEquals(struct.b.a.read(msg), 0);
		assertEquals(struct.b.b.read(msg), "hi");
		assertEquals(struct.b.c.read(msg), 0);
		assertEquals(struct.c.read(msg), "end");

		struct.a.write(msg, "init");
		assertEquals(struct.a.read(msg), "init");
		assertEquals(struct.b.a.read(msg), 0);
		assertEquals(struct.b.b.read(msg), "hi");
		assertEquals(struct.b.c.read(msg), 0);
		assertEquals(struct.c.read(msg), "end");

	}

}

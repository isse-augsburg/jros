/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2016-2019 ISSE, University of Augsburg 
 */

package de.isse.jros.descriptors;

import org.junit.Test;

import de.isse.jros.messages.GeometryMsgs;
import de.isse.jros.types.ROSarray;
import de.isse.jros.types.ROSstring;
import de.isse.jros.types.ROSstruct;

/**
 * Testing manually created element descriptors for tfMessage
 */
public class TfArrayTest {
	
	@Test
	public void testTfMessage() {
		ROSstruct msg = new ROSstruct("tf/tfMessage").withField("transforms",
				new ROSarray(GeometryMsgs.TransformStamped()));
		ElementDescriptor<Integer> transforms = ElementDescriptor.createFor(msg,
				new ROSarray(GeometryMsgs.TransformStamped()), "transforms");

		ElementDescriptor<String> transforms0child = ElementDescriptor.createFor(msg, ROSstring.TYPE, "transforms", "0",
				"child_frame_id");
		ElementDescriptor<String> transforms1child = ElementDescriptor.createFor(msg, ROSstring.TYPE, "transforms", "1",
				"child_frame_id");

		byte[] data = new byte[msg.skip(null, 0) + 512];
		System.out.println("Settings transforms to 2");
		transforms.write(data, 2);
		System.out.println(" transforms[0].child = " + transforms0child.read(data));
		System.out.println(" transforms[1].child = " + transforms1child.read(data));

		System.out.println("Settings transforms[0].child to /f1");
		transforms0child.write(data, "/f1");
		System.out.println(" transforms[0].child = " + transforms0child.read(data));
		System.out.println(" transforms[1].child = " + transforms1child.read(data));

		System.out.println("Settings transforms[1].child to /f2");
		transforms1child.write(data, "/f2");
		System.out.println(" transforms[0].child = " + transforms0child.read(data));
		System.out.println(" transforms[1].child = " + transforms1child.read(data));

		System.out.println("Settings transforms[0].child to /fr1");
		transforms0child.write(data, "/fr1");
		System.out.println(" transforms[0].child = " + transforms0child.read(data));
		System.out.println(" transforms[1].child = " + transforms1child.read(data));

		System.out.println("Settings transforms to 3");
		transforms.write(data, 3);
		System.out.println(" transforms[0].child = " + transforms0child.read(data));
		System.out.println(" transforms[1].child = " + transforms1child.read(data));

		System.out.println("Settings transforms to 1");
		transforms.write(data, 1);
		System.out.println(" transforms[0].child = " + transforms0child.read(data));
		System.out.println(" transforms[1].child = " + transforms1child.read(data));

		System.out.println("Settings transforms to 2");
		transforms.write(data, 2);
		System.out.println(" transforms[0].child = " + transforms0child.read(data));
		System.out.println(" transforms[1].child = " + transforms1child.read(data));

	}
}

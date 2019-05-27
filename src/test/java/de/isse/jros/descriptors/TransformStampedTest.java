/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2016-2019 ISSE, University of Augsburg 
 */

package de.isse.jros.descriptors;

import org.junit.Assert;
import org.junit.Test;

import de.isse.jros.descriptors.ElementDescriptor;
import de.isse.jros.messages.GeometryMsgs;
import de.isse.jros.types.ROSfloat64;
import de.isse.jros.types.ROSstring;
import de.isse.jros.types.ROSstruct;

/**
 * Testing manually created element descriptors for TransformStamped
 */
public class TransformStampedTest {

	@Test
	public void testTransformationStamped() {
		ROSstruct msg = GeometryMsgs.TransformStamped();
		ElementDescriptor<Double> trans_x = ElementDescriptor.createFor(msg, ROSfloat64.TYPE, "transform",
				"translation", "x");
		ElementDescriptor<String> frame_id = ElementDescriptor.createFor(msg, ROSstring.TYPE, "header", "frame_id");
		ElementDescriptor<String> child_frame_id = ElementDescriptor.createFor(msg, ROSstring.TYPE, "child_frame_id");
		byte[] data = new byte[msg.skip(null, 0) + 20];

		System.out.println("Setting trans_x to 2.4");
		trans_x.write(data, 2.4);
		System.out.println(" trans_x = " + trans_x.read(data));
		System.out.println(" frame_id = " + frame_id.read(data));
		System.out.println(" child_frame_id = " + child_frame_id.read(data));
		Assert.assertEquals("", frame_id.read(data));
		Assert.assertEquals(2.4, trans_x.read(data), 1e-10);
		Assert.assertEquals("", child_frame_id.read(data));

		System.out.println("Setting frame_id to /f1");
		frame_id.write(data, "/f1");
		System.out.println(" trans_x = " + trans_x.read(data));
		System.out.println(" frame_id = " + frame_id.read(data));
		System.out.println(" child_frame_id = " + child_frame_id.read(data));
		Assert.assertEquals("/f1", frame_id.read(data));
		Assert.assertEquals(2.4, trans_x.read(data), 1e-10);
		Assert.assertEquals("", child_frame_id.read(data));
		

		System.out.println("Setting child_frame_id to /f2");
		child_frame_id.write(data, "/f2");
		System.out.println(" trans_x = " + trans_x.read(data));
		System.out.println(" frame_id = " + frame_id.read(data));
		System.out.println(" child_frame_id = " + child_frame_id.read(data));
		Assert.assertEquals("/f1", frame_id.read(data));
		Assert.assertEquals(2.4, trans_x.read(data), 1e-10);
		Assert.assertEquals("/f2", child_frame_id.read(data));


		System.out.println("Setting frame_id to /fr1");
		frame_id.write(data, "/fr1");
		System.out.println(" trans_x = " + trans_x.read(data));
		System.out.println(" frame_id = " + frame_id.read(data));
		System.out.println(" child_frame_id = " + child_frame_id.read(data));
		Assert.assertEquals("/fr1", frame_id.read(data));
		Assert.assertEquals(2.4, trans_x.read(data), 1e-10);
		Assert.assertEquals("/f2", child_frame_id.read(data));

		System.out.println("Setting child_frame_id to /fr2");
		child_frame_id.write(data, "/fr2");
		System.out.println(" trans_x = " + trans_x.read(data));
		System.out.println(" frame_id = " + frame_id.read(data));
		System.out.println(" child_frame_id = " + child_frame_id.read(data));
		Assert.assertEquals("/fr1", frame_id.read(data));
		Assert.assertEquals(2.4, trans_x.read(data), 1e-10);
		Assert.assertEquals("/fr2", child_frame_id.read(data));

	}
}

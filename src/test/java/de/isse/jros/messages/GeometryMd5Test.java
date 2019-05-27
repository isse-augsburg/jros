/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2016-2019 ISSE, University of Augsburg 
 */

package de.isse.jros.messages;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.isse.jros.MessageHelper;
import de.isse.jros.messages.GeometryMsgs;

/**
 * Testing MD5 sums: Checking that the computed MD5 sums are the same as the
 * ones created by ROS tools
 */
public class GeometryMd5Test {

	@Test
	public void testMd5() {
		assertEquals("4a842b65f413084dc2b10fb484ea7f17", //
				MessageHelper.getStructMD5(GeometryMsgs.Point()));

		assertEquals("e45d45a5a1ce597b249e23fb30fc871f", //
				MessageHelper.getStructMD5(GeometryMsgs.Pose()));

		assertEquals("d3812c3cbc69362b77dc0b19b345f8f5", //
				MessageHelper.getStructMD5(GeometryMsgs.PoseStamped()));

		assertEquals("c23e848cf1b7533a8d7c259073a97e6f", //
				MessageHelper.getStructMD5(GeometryMsgs.PoseWithCovariance()));

		assertEquals("a779879fadf0160734f906b8c19c7004", //
				MessageHelper.getStructMD5(GeometryMsgs.Quaternion()));

		assertEquals("ac9eff44abf714214112b05d54a3cf9b", //
				MessageHelper.getStructMD5(GeometryMsgs.Transform()));

		assertEquals("b5764a33bfeb3588febc2682852579b0", //
				MessageHelper.getStructMD5(GeometryMsgs.TransformStamped()));

		assertEquals("9f195f881246fdfa2798d1d3eebca84a", //
				MessageHelper.getStructMD5(GeometryMsgs.Twist()));

		assertEquals("4a842b65f413084dc2b10fb484ea7f17", //
				MessageHelper.getStructMD5(GeometryMsgs.Vector3()));

	}

}

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2016-2019 ISSE, University of Augsburg 
 */

package de.isse.jros.messages;

import org.junit.Assert;
import org.junit.Test;

import de.isse.jros.MessageHelper;
import de.isse.jros.types.ROSstruct;

/**
 * Testing types created by RMessage: Checking that RMessage and ROSstruct
 * versions of standard messages are equal
 */
public class GeometryTypeTest {

	@Test
	public void testGeometryMsgs() {
		compare(StdMsgs.Header(), new StdMsgs.Header().TYPE);
		compare(GeometryMsgs.Point(), new GeometryMsgs.Point().TYPE);
		compare(GeometryMsgs.Pose(), new GeometryMsgs.Pose().TYPE);
		compare(GeometryMsgs.PoseStamped(), new GeometryMsgs.PoseStamped().TYPE);
		compare(GeometryMsgs.PoseWithCovariance(), new GeometryMsgs.PoseWithCovariance().TYPE);
		compare(GeometryMsgs.Quaternion(), new GeometryMsgs.Quaternion().TYPE);
		compare(GeometryMsgs.Transform(), new GeometryMsgs.Transform().TYPE);
		compare(GeometryMsgs.Transform(), new GeometryMsgs.Transform().TYPE);
		compare(GeometryMsgs.TransformStamped(), new GeometryMsgs.TransformStamped().TYPE);
		compare(GeometryMsgs.Twist(), new GeometryMsgs.Twist().TYPE);
		compare(GeometryMsgs.TwistWithCovariance(), new GeometryMsgs.TwistWithCovariance().TYPE);
		compare(GeometryMsgs.Vector3(), new GeometryMsgs.Vector3().TYPE);
	}

	private void compare(ROSstruct first, ROSstruct second) {
		Assert.assertEquals(first.getName() + ": Short name", first.toString(), second.toString());
		Assert.assertEquals(first.getName() + ": MD5 checksum", MessageHelper.getStructMD5(first),
				MessageHelper.getStructMD5(second));
		Assert.assertEquals(first.getName() + ": Recursive struct definition",
				MessageHelper.getRecursiveStructDefinition(first, true),
				MessageHelper.getRecursiveStructDefinition(second, true));
		Assert.assertEquals(first.getName() + ": Struct definition with dependencies",
				MessageHelper.getStructDefinitionWithDependencies(first, true),
				MessageHelper.getStructDefinitionWithDependencies(second, true));
	}

}

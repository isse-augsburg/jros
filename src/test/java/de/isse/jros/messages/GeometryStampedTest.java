/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2016-2019 ISSE, University of Augsburg 
 */

package de.isse.jros.messages;

import org.junit.Assert;
import org.junit.Test;

import de.isse.jros.messages.GeometryMsgs.Point;
import de.isse.jros.messages.GeometryMsgs.PoseStamped;
import de.isse.jros.messages.GeometryMsgs.PoseWithCovariance;

/**
 * Testing read and write to different elements within geometry messages
 */
public class GeometryStampedTest {

	@Test
	public void testGeometryConcepts() {

		byte[] message = new byte[1000];
		Point point = new GeometryMsgs.Point();
		System.out.println(point.x.read(message));
		Assert.assertEquals(0, point.x.read(message), 1e-10);

		point.x.write(message, 3);
		System.out.println(point.x.read(message));
		Assert.assertEquals(3, point.x.read(message), 1e-10);

		
		message = new byte[1000];
		PoseWithCovariance pwc = new PoseWithCovariance();
		System.out.println(pwc.covariance.get(2).read(message));
		Assert.assertEquals(0, pwc.covariance.get(2).read(message), 1e-10);

		pwc.covariance.get(2).write(message, 3);
		System.out.println(pwc.covariance.get(2).read(message));
		Assert.assertEquals(3, pwc.covariance.get(2).read(message), 1e-10);
		
		System.out.println(pwc.covariance.get(3).read(message));
		Assert.assertEquals(0, pwc.covariance.get(3).read(message), 1e-10);


		message = new byte[1000];
		PoseStamped ps = new PoseStamped();
		ps.pose.position.x.write(message, 0.3);
		Assert.assertEquals(0.3, ps.pose.position.x.read(message), 1e-10);

		ps.header.frame_id.write(message, "/tf");
		System.out.println(ps.pose.position.x.read(message));
		Assert.assertEquals(0.3, ps.pose.position.x.read(message), 1e-10);
		
		ps.header.frame_id.write(message, "/");
		System.out.println(ps.pose.position.x.read(message));
		Assert.assertEquals(0.3, ps.pose.position.x.read(message), 1e-10);

	}

}

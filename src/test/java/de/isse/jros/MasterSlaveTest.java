/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2016-2019 ISSE, University of Augsburg 
 */

package de.isse.jros;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import de.isse.jros.RosNode.Publishing;
import de.isse.jros.RosNode.Subscriber;
import de.isse.jros.messages.StdMsgs;
import de.isse.jros.messages.StdMsgs.Header;

/**
 * Testing RosMaster and RosNode in a localhost setup
 */
public class MasterSlaveTest {
	byte[] received;

	@Test
	public void testMaster() throws IOException, InterruptedException {
		RosMaster master = new RosMaster();

		RosNode a = new RosNode("/a", "http://127.0.0.1:11311");
		RosNode b = new RosNode("/b", "http://127.0.0.1:11311");

		Publishing publishing = a.publish("/test", StdMsgs.Header(), true);

		byte[] msg = new byte[64];
		Header header = new StdMsgs.Header();
		header.seq.write(msg, 1);
		header.stamp.write(msg, 1, 0);
		header.frame_id.write(msg, "/");

		publishing.send(msg);

		b.subscribe("/test", header.TYPE, new Subscriber() {
			@Override
			public void received(byte[] message) {
				received = Arrays.copyOf(message, message.length);
			}
		});
		Thread.sleep(50);
		assertNotNull(null, received);
		assertEquals("/", header.frame_id.read(received));

		a.shutdown();
		b.shutdown();
		master.shutdown();
	}

}

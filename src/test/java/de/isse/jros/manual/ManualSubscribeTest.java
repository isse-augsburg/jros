/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2016-2019 ISSE, University of Augsburg 
 */

package de.isse.jros.manual;

import java.io.IOException;

import de.isse.jros.RosNode;
import de.isse.jros.RosNode.Subscriber;
import de.isse.jros.descriptors.ElementDescriptor;
import de.isse.jros.messages.StdMsgs;
import de.isse.jros.types.ROSstring;
import de.isse.jros.types.ROSstruct;

/**
 * Manual test connecting to a ros master and subscribing to a topic
 */
public class ManualSubscribeTest {
	public static void main(String[] args) throws IOException {
		System.out.println("Subscribing on /test");
		ROSstruct header = StdMsgs.Header();
		ElementDescriptor<String> frameId = ElementDescriptor.createFor(header, ROSstring.TYPE, "frame_id");

		RosNode node = new RosNode("/subscriber", "http://127.0.0.1:11311");
		node.subscribe("/test", header, new Subscriber() {
			@Override
			public void received(byte[] message) {
				System.out.println("frame_id is " + frameId.read(message));
			}
		});
		System.in.read();
		node.shutdown();
	}
}

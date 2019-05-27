/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2016-2019 ISSE, University of Augsburg 
 */

package de.isse.jros.manual;

import java.io.IOException;

import de.isse.jros.RosNode;
import de.isse.jros.RosNode.Publishing;
import de.isse.jros.descriptors.ElementDescriptor;
import de.isse.jros.messages.StdMsgs;
import de.isse.jros.types.ROSstring;
import de.isse.jros.types.ROSstruct;

/**
 * Manual test connecting to a ros master and publishing a topic
 */
public class ManualPublishTest {
	public static void main(String[] args) throws IOException {
		System.out.println("Publishing on /test");
		ROSstruct header = StdMsgs.Header();
		ElementDescriptor<String> frameId = ElementDescriptor.createFor(header, ROSstring.TYPE, "frame_id");
		byte[] msg = new byte[header.skip(null, 0) + 60];
		frameId.write(msg, "/frame_name");

		RosNode node = new RosNode("/publisher", "http://127.0.0.1:11311");
		Publishing publish = node.publish("/test", header, true);
		publish.send(msg);

		System.in.read();
		node.shutdown();
	}
}

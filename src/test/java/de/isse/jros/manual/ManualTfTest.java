/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2016-2019 ISSE, University of Augsburg 
 */

package de.isse.jros.manual;

import java.io.IOException;
import java.util.Map.Entry;

import de.isse.jros.MessageHelper;
import de.isse.jros.RosNode;
import de.isse.jros.RosNode.Subscriber;
import de.isse.jros.types.ROSstruct;

/**
 * Manual test connecting to a ros master and retrieving information about the /tf topic
 */
public class ManualTfTest {

	public static void main(String[] args) throws IOException {

		RosNode ros = new RosNode("/apitest", "http://rosmaster:11311", "127.0.0.1");

		showMessageDefinition(ros, "/tf");
		final ROSstruct tf = (ROSstruct) ros.getTopicPrototype("/tf");
		ros.subscribe("/tf", tf, new Subscriber() {
			@Override
			public void received(byte[] message) {
				System.out.println(tf.toString(message, 0));
			}
		});

		System.in.read();

		for (String topic : ros.getPublishers().keySet()) {
			ROSstruct proto = (ROSstruct) ros.getTopicPrototype(topic);
			ros.subscribe(topic, proto, new Subscriber() {
				@Override
				public void received(byte[] message) {
					System.out.println(proto.toString(message, 0));
				}
			});
		}

		System.in.read();

		for (Entry<String, String> entry : ros.getTopicTypes().entrySet()) {
			ros.unsubscribe(entry.getKey());
		}

		ros.shutdown();
	}

	private static void showMessageDefinition(RosNode ros, String topic) throws IOException {
		ROSstruct proto = (ROSstruct) ros.getTopicPrototype(topic);
		// System.out.println("MSG:" + proto.getName());
		// System.out.println(MessageHelper.getStructDefinitionWithDependencies(proto));
		System.err.println(MessageHelper.getRecursiveStructDefinition(proto, false));

	}

}

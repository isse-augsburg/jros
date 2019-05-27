/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2016-2019 ISSE, University of Augsburg 
 */

package de.isse.jros.internal.tcpros;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.isse.jros.MessageHelper;
import de.isse.jros.RosNode.Subscriber;
import de.isse.jros.types.ROSType;
import de.isse.jros.types.ROSint32;
import de.isse.jros.types.ROSstruct;

/**
 * Class handling TCPROS subscription for a given topic
 */
public class TcpRosSubscriber {

	private DataInputStream dis;
	private Socket socket;

	/**
	 * Creates a new subscription
	 * 
	 * @param host       host name of the publisher
	 * @param port       port of the publisher
	 * @param callerId   caller id
	 * @param topic      topic of publication
	 * @param message    message type of publication
	 * @param subscriber callback to handle received messages
	 * @throws IOException if an I/O error occurs
	 */
	public TcpRosSubscriber(String host, int port, String callerId, final String topic, final ROSstruct message,
			final Subscriber subscriber) throws IOException {
		this.socket = new Socket(host, port);
		InputStream is = socket.getInputStream();
		OutputStream os = socket.getOutputStream();

		TcpRosHelper.sendConnectionHeader(os, message, callerId, false, topic);
		Map<String, String> header = TcpRosHelper.receiveConnectionHeader(is);
		String md5 = MessageHelper.getStructMD5(message);
		if (header.containsKey("error")) {
			socket.close();
			throw new IOException(header.get("error"));
		}

		if (!header.get("md5sum").equals(md5)) {
			this.socket.close();
			throw new IOException("Client " + host + ":" + port + " provides topic " + topic
					+ " to have datatype/md5sum [" + header.get("type") + "/" + header.get("md5sum")
					+ "], but our version has [" + message.getName() + "/" + md5 + "]. Dropping connection.");
		}
		dis = new DataInputStream(is);

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					byte[] msg = null, lmsg = new byte[4];
					try {
						for (int pos = 0; pos < 4; pos += dis.read(lmsg, pos, 4 - pos))
							;
						int len = ROSint32.TYPE.read(lmsg, 0).intValue();
						if (len <= 0)
							throw new IllegalArgumentException(
									"Unexepected message length " + len + " in topic " + topic);
						msg = new byte[len];
						for (int pos = 0; pos < len; pos += dis.read(msg, pos, len - pos))
							;
						subscriber.received(msg);
					} catch (IOException e) {
						break;
					}
				}
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		thread.setDaemon(true);
		thread.start();

	}

	/**
	 * Ends the subscription
	 * 
	 * @throws IOException if an I/O error occurs
	 */
	public void stop() throws IOException {
		socket.close();
	}

	/**
	 * Creates a message prototype for the type of message published by the given
	 * host
	 * 
	 * @param host     host name of the publisher
	 * @param port     port of the publisher
	 * @param callerId caller id
	 * @param topic    topic of publication
	 * @param type     name of the message type
	 * @return ROSType created from the connection header received by the publisher
	 * @throws IOException if an I/O error occurs
	 */
	public static ROSType<?> getMessagePrototype(String host, int port, String callerId, String topic, String type)
			throws IOException {
		String md5 = "*";
		while (true) {
			Socket socket = new Socket(host, port);
			OutputStream os = socket.getOutputStream();
			InputStream is = socket.getInputStream();
			ROSstruct message = new ROSstruct(type);
			TcpRosHelper.sendConnectionHeader(os,
					TcpRosHelper.createConnectionHeader(message, callerId, false, topic, md5));
			Map<String, String> header = TcpRosHelper.receiveConnectionHeader(is);
			socket.close();
			if (header.containsKey("error")) {
				if (!md5.equals("*"))
					return null;
				Matcher matcher = Pattern.compile("\\[((?:[^/\\]]+/)+)([^/\\]]+)\\]").matcher(header.get("error"));
				if (matcher.find()) {
					md5 = matcher.group(2);
					continue;
				} else {
					return null;
				}

			} else if (header.containsKey("message_definition")) {
				MessageHelper.registerStructFromMessageDefinitionWithDependencies(type,
						header.get("message_definition"));
				return MessageHelper.getPrototype(type);

			} else {
				return null;
			}
		}
	}

}

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2016-2019 ISSE, University of Augsburg 
 */

package de.isse.jros.internal.tcpros;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import de.isse.jros.MessageHelper;
import de.isse.jros.types.ROSint32;
import de.isse.jros.types.ROSstring;
import de.isse.jros.types.ROSstruct;

/**
 * Helper class for TCPROS protocol
 */
public class TcpRosHelper {

	/**
	 * Sends a connection header (with 4-byte length prefix)
	 * 
	 * @param os     output stream to send the connection header to
	 * @param header header to send
	 * @throws IOException if an I/O error occurs
	 */
	public static void sendConnectionHeader(OutputStream os, byte[] header) throws IOException {
		DataOutputStream dos = new DataOutputStream(os);
		byte[] data = new byte[4];
		ROSint32.TYPE.write(data, 0, header.length);
		dos.write(data, 0, 4);
		dos.write(header, 0, header.length);
		dos.flush();
	}

	/**
	 * Sends a connection header for a message publication
	 * 
	 * @param os       output stream to send the connection header to
	 * @param message  message type of the publication
	 * @param callerId caller ID of the publication
	 * @param latching flag whether the publication is latching
	 * @param topic    topic of the publication
	 * @throws IOException if an I/O error occurs
	 */
	public static void sendConnectionHeader(OutputStream os, ROSstruct message, String callerId, boolean latching,
			String topic) throws IOException {
		sendConnectionHeader(os, createConnectionHeader(message, callerId, latching, topic));
	}

	/**
	 * Creates an error header / message
	 * 
	 * @param error error message
	 * @return byte array representation of the error
	 * @throws IOException if an I/O error occcurs
	 */
	public static byte[] createErrorHeader(String error) throws IOException {
		return serializeString(error);
	}

	/**
	 * Sends a message (with 4-byte length prefix)
	 * 
	 * @param os      output stream to send the connection header to
	 * @param message message to send
	 * @throws IOException if an I/O error occurs
	 */
	public static void sendMessage(OutputStream os, byte[] message) throws IOException {
		DataOutputStream dos = new DataOutputStream(os);
		byte[] data = new byte[4];
		ROSint32.TYPE.write(data, 0, message.length);
		dos.write(data, 0, 4);
		dos.write(message, 0, message.length);
		dos.flush();
	}

	/**
	 * Creates a connection header for a publication
	 * 
	 * @param message  message type of the publication
	 * @param callerId caller ID of the publication
	 * @param latching flag whether the publication is latching
	 * @param topic    topic of the publication
	 * @param md5sum   md5 checksum of the message type
	 * @return connection header for the publication
	 * @throws IOException if an I/O error occurs
	 */
	public static byte[] createConnectionHeader(ROSstruct message, String callerId, boolean latching, String topic,
			String md5sum) throws IOException {
		ByteArrayOutputStream ba = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(ba);
		dos.write(serializeString(
				"message_definition=" + MessageHelper.getStructDefinitionWithDependencies(message, true)));
		dos.write(serializeString("callerid=" + callerId));
		dos.write(serializeString("latching=" + (latching ? "1" : "0")));
		dos.write(serializeString("md5sum=" + md5sum));
		dos.write(serializeString("topic=" + topic));
		dos.write(serializeString("type=" + message.getName()));
		dos.close();
		return ba.toByteArray();
	}

	/**
	 * Serializes a string
	 * 
	 * @param string string to serialize
	 * @return byte array representation
	 */
	private static byte[] serializeString(String string) {
		byte[] ret = new byte[string.length() + 4];
		ROSstring.TYPE.write(ret, 0, string);
		return ret;
	}

	/**
	 * Creates a connection header for a publication
	 * 
	 * @param message  message type of the publication
	 * @param callerId caller ID of the publication
	 * @param latching flag whether the publication is latching
	 * @param topic    topic of the publication
	 * @return connection header for the publication
	 * @throws IOException if an I/O error occurs
	 */
	public static byte[] createConnectionHeader(ROSstruct message, String callerId, boolean latching, String topic)
			throws IOException {
		return createConnectionHeader(message, callerId, latching, topic, MessageHelper.getStructMD5(message));
	}

	/**
	 * Retrieves a connection header for a subscription
	 * 
	 * @param is input stream to receive the connection header from
	 * @return parsed connection header
	 * @throws IOException if an I/O error occurs
	 */
	public static Map<String, String> receiveConnectionHeader(InputStream is) throws IOException {
		DataInputStream dis = new DataInputStream(is);
		byte[] data = new byte[4];
		dis.read(data);
		int len = ROSint32.TYPE.read(data, 0).intValue();
		byte[] header = new byte[len];
		for (int read = 0; read < len; read += dis.read(header, read, len - read))
			;
		return parseConnectionHeader(header);
	}

	/**
	 * Parses a connection header
	 * 
	 * @param header header to parse
	 * @return map representing the data from the connection header
	 * @throws IOException if an I/O error occurs
	 */
	public static Map<String, String> parseConnectionHeader(byte[] header) throws IOException {
		Map<String, String> ret = new HashMap<String, String>();
		int position = 0;
		while (position < header.length) {
			String line = ROSstring.TYPE.read(header, position);
			position = ROSstring.TYPE.skip(header, position);
			String[] parts = line.split("=", 2);
			ret.put(parts[0], parts[1]);
		}
		return ret;
	}

}

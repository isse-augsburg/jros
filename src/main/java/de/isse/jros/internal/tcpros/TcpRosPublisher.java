/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2016-2019 ISSE, University of Augsburg 
 */

package de.isse.jros.internal.tcpros;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import de.isse.jros.MessageHelper;
import de.isse.jros.types.ROSstruct;

/**
 * Class handling the TCPROS publication for a defined topic
 */
public class TcpRosPublisher {
	private ServerSocket ss;
	private List<Socket> clients = new ArrayList<Socket>();
	private byte[] header;
	private byte[] message;
	private boolean latching;
	private String type, md5sum;

	/**
	 * Creates a publication
	 * 
	 * @param port      local port to use for this TCPROS publisher
	 * @param callerId  caller id of the publication
	 * @param topic     topic of the publication
	 * @param prototype message type of the publication
	 * @param latching  flag whether messages should be repeated on new connections
	 * @throws IOException if an I/O error occurs
	 */
	public TcpRosPublisher(int port, String callerId, String topic, ROSstruct prototype, boolean latching)
			throws IOException {
		this.latching = latching;
		this.header = TcpRosHelper.createConnectionHeader(prototype, callerId, latching, topic);
		this.message = new byte[0];
		this.ss = new ServerSocket(port);
		this.type = prototype.getName();
		this.md5sum = MessageHelper.getStructMD5(prototype);
	}

	/**
	 * Retrieves the (server) port of this TCPROS connection
	 */
	public int getPort() {
		return ss.getLocalPort();
	}

	/**
	 * Starts publication
	 */
	public void start() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (ss != null) {
					try {
						final Socket s = ss.accept();
						Thread thread = new Thread(new Runnable() {
							@Override
							public void run() {
								handle(s);
							}
						});
						thread.setDaemon(true);
						thread.start();
					} catch (IOException e) {
					}
				}
				message = null;
				synchronized (this) {
					notifyAll();
					for (Socket s : clients) {
						try {
							s.close();
						} catch (IOException e) {
						}
					}
				}
			}
		});
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * Handles incoming connection requests from subscribers
	 * 
	 * @param s socket containing the connection request
	 */
	public void handle(Socket s) {
		byte[] lastMsg = null;
		if (!latching)
			lastMsg = message;
		clients.add(s);
		try {
			InputStream is = new BufferedInputStream(s.getInputStream());
			OutputStream os = new BufferedOutputStream(s.getOutputStream());
			Map<String, String> reqHeader = TcpRosHelper.receiveConnectionHeader(is);
			if ((!reqHeader.get("type").equals(type) && !reqHeader.get("type").equals("*"))
					|| (!reqHeader.get("md5sum").equals(md5sum) && !reqHeader.get("md5sum").equals("*"))) {
				TcpRosHelper.sendConnectionHeader(os,
						TcpRosHelper.createErrorHeader("Client [" + reqHeader.get("callerid") + "] wants topic "
								+ reqHeader.get("topic") + " to have datatype/md5sum [" + reqHeader.get("type") + "/"
								+ reqHeader.get("md5sum") + "], but our version has [" + type + "/" + md5sum
								+ "]. Dropping connection."));
				os.flush();
				s.close();
			} else {
				TcpRosHelper.sendConnectionHeader(os, header);
				while (message != null) {
					if (message != lastMsg) {
						try {
							if (message.length > 0)
								TcpRosHelper.sendMessage(os, message);
							lastMsg = message;
						} catch (IOException e) {
							break;
						}
					}
					synchronized (this) {
						if (message == null)
							break;
						try {
							wait(1000);
						} catch (InterruptedException e) {
						}
					}
				}
			}
			clients.remove(s);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Publishes a new message
	 * 
	 * @param buffer message buffer
	 * @param start  start position in buffer
	 * @param len    length in buffer
	 * @throws IOException if an I/O error occurs
	 */
	public void setMessage(byte[] buffer, int start, int len) throws IOException {
		byte[] msg = Arrays.copyOfRange(buffer, start, start + len);
		this.message = msg;
		synchronized (this) {
			notifyAll();
		}
	}

	/**
	 * Stops publication
	 * 
	 * @throws IOException if an I/O error occurs
	 */
	public void stop() throws IOException {
		ss.close();
		ss = null;
	}

}

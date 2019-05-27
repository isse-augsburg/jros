/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2016-2019 ISSE, University of Augsburg 
 */

package de.isse.jros.internal.xmlrpc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple XMLRCP server
 */
public class RpcServer {
	/**
	 * Interface to handle XMLRPC requests
	 */
	public interface ServerInterface {
		/**
		 * Execute an XMLRPC request
		 * 
		 * @param method request method
		 * @param params request parameters
		 * @return response
		 */
		List<?> execute(String method, List<?> params);
	}

	private String ip = "127.0.0.1";
	private ServerInterface si;
	private ServerSocket ss;
	private boolean stopped = true;

	/**
	 * Creates a new XMLRPC server
	 * 
	 * @param port local port
	 * @param si   interface to handle requests
	 * @throws IOException if an I/O error occurs
	 */
	public RpcServer(int port, ServerInterface si) throws IOException {
		ss = new ServerSocket(port);
		this.si = si;
	}

	/**
	 * Retrieves the local port
	 */
	public int getPort() {
		return ss.getLocalPort();
	}

	/**
	 * Starts the server
	 * 
	 * @throws IOException if an I/O error occurs
	 */
	public void start() throws IOException {
		stopped = false;
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (!stopped) {
					try {
						final Socket socket = ss.accept();
						ip = socket.getLocalAddress().getHostName();
						new Thread(new Runnable() {
							@Override
							public void run() {
								try {
									parseRequest(socket);
								} catch (IOException e) {
								}
							}
						}).start();
					} catch (IOException e) {
					}
				}
			}
		});
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * Stops the server
	 * 
	 * @throws IOException if an I/O error occurs
	 */
	public void stop() throws IOException {
		stopped = true;
		ss.close();
	}

	/**
	 * Parses the request received on a socket, handles it and sends the response
	 * 
	 * @param socket socket to handle
	 * @throws IOException if an I/O error occurs
	 */
	private void parseRequest(Socket socket) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		String line = br.readLine();
		if (line == null)
			throw new EOFException();
		line = line.trim();
		if (line.startsWith("POST ")) {
			Map<String, String> headers = new HashMap<String, String>();
			while (true) {
				line = br.readLine();
				if (line.trim().isEmpty())
					break;
				String[] parts = line.split(":", 2);
				headers.put(parts[0].toLowerCase(), parts[1].trim());
			}
			int len = Integer.parseInt(headers.get("content-length"));
			char[] data = new char[len];
			for (int pos = 0; pos < len; pos += br.read(data, pos, len - pos))
				;
			String response = handleRequest(new String(data));

			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			bw.write("HTTP/1.0 200 OK\n");
			bw.write("Connection: close\n");
			bw.write("Content-Type: text/xml\n");
			bw.write("Content-length: " + response.length() + "\n");
			bw.write("\n");
			bw.write(response);
			bw.flush();
			socket.close();
		} else {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			String response = "XMLRPC sever ready.";
			bw.write("HTTP/1.0 200 OK\n");
			bw.write("Connection: close\n");
			bw.write("Content-Type: text/plain\n");
			bw.write("Content-length: " + response.length() + "\n");
			bw.write("\n");
			bw.write(response);
			bw.flush();
			socket.close();
		}
	}

	/**
	 * Handles an XMLRPC request string and returns the response
	 * 
	 * @param request XMLRPC request string
	 * @return XMLRPC response string
	 */
	private String handleRequest(String request) {
		List<?> rq = XmlRpc.parseRequest(request);
		String method = rq.remove(0).toString();
		List<?> ret = si.execute(method, rq);
		return XmlRpc.formatResponse(ret);
	}

	/**
	 * Retrieves the URI of the XMLRPC server
	 * 
	 * @return URI of the server
	 */
	public String getUri() {
		return "http://" + ip + ":" + getPort() + "/";
	}
}

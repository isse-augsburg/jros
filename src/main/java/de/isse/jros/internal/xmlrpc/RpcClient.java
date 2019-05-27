/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2016-2019 ISSE, University of Augsburg 
 */

package de.isse.jros.internal.xmlrpc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple XMLRPC client
 */
public class RpcClient {

	private int port;
	private String host;

	/**
	 * Creates a new XMLRPC client
	 * 
	 * @param host host name of the communication partner
	 * @param port port of the communication partner
	 */
	public RpcClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	/**
	 * Executes an XMLRPC request
	 * 
	 * @param method request method
	 * @param params request parameters
	 * @return XMLRPC response
	 * @throws IOException if an I/O error occurs
	 */
	public Object execute(String method, List<?> params) throws IOException {
		Socket socket = new Socket(host, port);
		String request = XmlRpc.formatRequest(method, params);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		bw.write("POST / HTTP/1.0\n");
		bw.write("User-Agent: RCC-ROS-Bridge\n");
		bw.write("Content-Type: text/xml\n");
		bw.write("Host: " + host + "\n");
		bw.write("Content-Length: " + request.length() + "\n");
		bw.write("\n");
		bw.write(request);
		bw.flush();
		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		String line = br.readLine().trim();
		if (line.startsWith("HTTP/") && line.contains("200")) {
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
			socket.close();
			return XmlRpc.parseResponse(new String(data));
		} else {
			socket.close();
			throw new IOException(line);
		}

	}

	/**
	 * Retrieves the host name of the local IP address used to connect to the XMLRPC
	 * server
	 * 
	 * @return host name of the local IP address
	 * @throws IOException if an I/O error occurs
	 */
	public String getLocalHost() throws IOException {
		Socket s = new Socket(host, port);
		String ret = s.getLocalAddress().getHostAddress();
		s.close();
		return ret;
	}

}

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2016-2019 ISSE, University of Augsburg 
 */

package de.isse.jros.internal.xmlrpc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple XMLRPC parser
 */
public class XmlRpc {

	/**
	 * Creates an XMLRPC request
	 * 
	 * @param method request method
	 * @param params request parameters
	 * @return request string
	 */
	public static String formatRequest(String method, List<?> params) {
		StringBuffer ret = new StringBuffer();
		ret.append("<?xml version='1.0'?>");
		ret.append("<methodCall>");
		ret.append("<methodName>").append(method).append("</methodName>");
		ret.append("<params>");
		for (Object p : params) {
			ret.append("<param><value>");
			appendParam(ret, p);
			ret.append("</value></param>");
		}
		ret.append("</params>");
		ret.append("</methodCall>");
		return ret.toString();
	}

	/**
	 * Creates an XMLRPC response
	 * 
	 * @param params response parameters
	 * @return response string
	 */
	public static String formatResponse(Object params) {
		StringBuffer ret = new StringBuffer();
		ret.append("<?xml version='1.0'?>");
		ret.append("<methodResponse>");
		ret.append("<params>");
		ret.append("<param><value>");
		appendParam(ret, params);
		ret.append("</value></param>");
		ret.append("</params>");
		ret.append("</methodResponse>");
		return ret.toString();
	}

	/**
	 * Parses an XMLRPC request
	 * 
	 * @param request request string
	 * @return request parameters
	 */
	public static List<Object> parseRequest(String request) {
		List<Object> ret = new ArrayList<Object>();
		ByteArrayInputStream is = new ByteArrayInputStream(request.getBytes());
		try {
			expectNext(is, "<?xml version='1.0'?>", "<?xml version=\"1.0\"?>");
			expectNext(is, "<methodCall>");
			expectNext(is, "<methodName>");
			String method = readNext(is);
			ret.add(method);
			expectNext(is, "</methodName>");
			expectNext(is, "<params>");
			while (true) {
				String tag = readNext(is);
				if (tag.equals("<param>")) {
					expectNext(is, "<value>");
					Object p = readParam(is);
					if (!"</value>".equals(p)) {
						ret.add(p);
						expectNext(is, "</value>");
					}
				} else if (tag.equals("</params>")) {
					break;
				}
			}
			expectNext(is, "</methodCall>");
			return ret;
		} catch (IOException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	/**
	 * Parses an XMLRPC response
	 * 
	 * @param response response string
	 * @return response parameters
	 */
	public static Object parseResponse(String response) {
		Object ret = null;
		ByteArrayInputStream is = new ByteArrayInputStream(response.getBytes());
		try {
			expectNext(is, "<?xml version='1.0'?>", "<?xml version=\"1.0\"?>");
			expectNext(is, "<methodResponse>");
			String tag = readNext(is);
			if (tag.equals("<params>")) {
				expectNext(is, "<param>");
				expectNext(is, "<value>");
				ret = readParam(is);
				expectNext(is, "</value>");
				expectNext(is, "</param>");
				expectNext(is, "</params>");
			} else if (tag.equals("<fault>")) {
				throw new IllegalArgumentException(response);
			}

		} catch (IOException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
		return ret;
	}

	/**
	 * Reads a XMLRPC value
	 * 
	 * @param is input stream to read the value from
	 * @return parsed value
	 * @throws IOException if an I/O error occurs or the stream contains invalid
	 *                     data
	 */
	private static Object readParam(ByteArrayInputStream is) throws IOException {
		String type = readNext(is);
		if (type.equals("<i4>")) {
			String value = readNext(is);
			if (value.equals("</i4>"))
				return 0;
			expectNext(is, "</i4>");
			return Integer.parseInt(value);
		} else if (type.equals("<int>")) {
			String value = readNext(is);
			if (value.equals("</int>"))
				return 0;
			expectNext(is, "</int>");
			return Integer.parseInt(value);
		} else if (type.equals("<string>")) {
			String value = readNext(is);
			if (value.equals("</string>"))
				return "";
			expectNext(is, "</string>");
			return value;
		} else if (type.equals("<array>")) {
			expectNext(is, "<data>");
			List<Object> ret = new ArrayList<Object>();
			while (true) {
				type = readNext(is);
				if (type.equals("</data>")) {
					expectNext(is, "</array>");
					return ret;
				} else if (type.equals("<value>")) {
					Object param = readParam(is);
					if (param.equals("</value>")) {
						ret.add("");
					} else {
						ret.add(param);
						expectNext(is, "</value>");
					}
				}
			}
		} else if (type.equals("<struct>")) {
			Map<String, Object> ret = new HashMap<String, Object>();
			while (true) {
				type = readNext(is);
				if (type.equals("</struct>")) {
					return ret;
				} else if (type.equals("<member>")) {
					expectNext(is, "<name>");
					String name = readNext(is);
					expectNext(is, "</name>");
					expectNext(is, "<value>");
					Object v = readParam(is);
					expectNext(is, "</value>");
					expectNext(is, "</member>");
					ret.put(name, v);
				}
			}
		} else {
			return type;
		}
	}

	/**
	 * Ensure the following token in the InputStream is as expected
	 * 
	 * @param is   input stream to read from
	 * @param next expected token
	 * @throws IOException if the input stream contains incorrect data
	 */
	private static void expectNext(InputStream is, String... next) throws IOException {
		String read = readNext(is);
		if (!Arrays.asList(next).contains(read)) {
			throw new IllegalArgumentException("Unexpected token " + read + ", expected " + Arrays.asList(next));
		}
	}

	/**
	 * Reads the next token from an input stream (tag or text)
	 * 
	 * @param is input stream to read from
	 * @return next token found
	 * @throws IOException if an I/O error occurs
	 */
	private static String readNext(InputStream is) throws IOException {
		StringBuffer ret = new StringBuffer();
		boolean inTag = false, inText = false;
		while (true) {
			is.mark(1);
			int ch = is.read();
			if (ch == -1)
				throw new IOException("Unexpected end of stream");
			if (inText) {
				if (ch == '<') {
					is.reset();
					return ret.toString();
				}
				ret.append((char) ch);
			} else if (inTag) {
				ret.append((char) ch);
				if (ch == '>') {
					return ret.toString();
				}
			} else if (ch == ' ' || ch == '\n' || ch == '\r' || ch == '\t') {
				// ignore white space
			} else if (ch == '<') {
				ret.append("<");
				inTag = true;
			} else if (ch == -1) {
				return ret.toString();
			} else {
				ret.append((char) ch);
				inText = true;
			}
		}
	}

	/**
	 * Writes an XMLRPC value
	 * 
	 * @param ret string buffer to write to
	 * @param p   object to write
	 */
	private static void appendParam(StringBuffer ret, Object p) {
		if (p instanceof String) {
			ret.append("<string>").append(p).append("</string>");
		} else if (p instanceof Integer) {
			ret.append("<int>").append(((Integer) p).intValue()).append("</int>");
		} else if (p instanceof List<?>) {
			ret.append("<array>");
			ret.append("<data>");
			for (Object c : (List<?>) p) {
				ret.append("<value>");
				appendParam(ret, c);
				ret.append("</value>");
			}
			ret.append("</data>");
			ret.append("</array>");
		} else if (p instanceof Map<?, ?>) {
			ret.append("<struct>");
			for (Map.Entry<?, ?> e : ((Map<?, ?>) p).entrySet()) {
				ret.append("<member>");
				ret.append("<name>").append(e.getKey()).append("</name>");
				ret.append("<value>");
				appendParam(ret, e.getValue());
				ret.append("</value>");
				ret.append("</member>");
			}
			ret.append("</struct>");
		}
	}
}

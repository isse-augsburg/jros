/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2016-2019 ISSE, University of Augsburg 
 */

package de.isse.jros;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import de.isse.jros.internal.tcpros.TcpRosPublisher;
import de.isse.jros.internal.tcpros.TcpRosSubscriber;
import de.isse.jros.internal.xmlrpc.RpcClient;
import de.isse.jros.internal.xmlrpc.RpcServer;
import de.isse.jros.internal.xmlrpc.RpcServer.ServerInterface;
import de.isse.jros.types.ROSType;
import de.isse.jros.types.ROSstruct;

/**
 * Minimal implementation of a ROS node supporting topics (publish / subscribe)
 */
public class RosNode {
	private RpcClient master;
	private String callerId;
	private String nodeUri = "http://127.0.0.1:8080";
	private Map<String, Integer> portsForPublish = new HashMap<String, Integer>();
	private Map<String, ROSstruct> prototypes = new HashMap<String, ROSstruct>();
	private Map<String, List<String>> publishers = new HashMap<String, List<String>>();
	private Map<String, TcpRosSubscriber> connections = new HashMap<String, TcpRosSubscriber>();
	private Map<String, String> hostMap = new HashMap<String, String>();
	private RpcServer server;
	private Map<String, Publishing> publishings = new HashMap<String, Publishing>();
	private Map<String, Subscriber> subscriptions = new HashMap<String, Subscriber>();

	/**
	 * Interface to publish messages
	 */
	public interface Publishing {
		/**
		 * Publish a message (given in ROS binary format)
		 * 
		 * @param message message to send
		 * @throws IOException if an I/O error occurs
		 */
		void send(byte[] message) throws IOException;

		/**
		 * Stop publishing the message
		 * 
		 * @throws IOException if an I/O error occurs
		 */
		void stop() throws IOException;
	}

	/**
	 * Callback to handle received messages
	 */
	public interface Subscriber {
		/**
		 * Handle a received message (given in ROS binary format)
		 * 
		 * @param message message received
		 */
		void received(byte[] message);
	}

	/**
	 * Starts a new ROS node with the given name and ROS master
	 * 
	 * @param name      name of the node
	 * @param masterUri URI of the ROS master to use
	 * @throws IOException if an I/O error occurs
	 */
	public RosNode(String name, String masterUri) throws IOException {
		this(name, masterUri, new HashMap<String, String>());
	}

	/**
	 * Starts a new ROS node with the given name and ROS master
	 * 
	 * @param name      name of the node
	 * @param masterUri URI of the ROS master to use
	 * @param masterIp  IP address of the ROS master to use (when DNS is not set up
	 *                  correctly)
	 * @throws IOException if an I/O error occurs
	 */
	public RosNode(String name, String masterUri, String masterIp) throws IOException {
		this(name, masterUri, makeMap(masterUri, masterIp));
	}

	private static Map<String, String> makeMap(String Uri, String Ip) throws IOException {
		Map<String, String> ret = new HashMap<String, String>();
		ret.put(new URL(Uri).getHost(), Ip);
		return ret;
	}

	/**
	 * Starts a new ROS node with the given name and ROS master
	 * 
	 * @param name      name of the node
	 * @param masterUri URI of the ROS master to use
	 * @param hostMap   Map assigning IP addresses to host names (when DNS is not
	 *                  set up correctly)
	 * @throws IOException if an I/O error occurs
	 */
	public RosNode(String name, String masterUri, Map<String, String> hostMap) throws IOException {
		this.callerId = name;
		this.hostMap.putAll(hostMap);
		String host = new URL(masterUri).getHost();
		int port = new URL(masterUri).getPort();
		master = new RpcClient(resolveHost(host), port);
		final String local = master.getLocalHost();

		server = new RpcServer(0, new ServerInterface() {
			@Override
			public List<?> execute(String method, List<?> params) {
				if (method.equals("publisherUpdate")) {
					String tp = (String) params.get(1);
					List<?> ps = (List<?>) params.get(2);
					publisherUpdate(tp, ps);
					return Arrays.asList(1, "Publishers updated", 0);
				} else if (method.equals("requestTopic")) {
					Integer port = portsForPublish.get(params.get(1));
					for (Object o : ((List<?>) params.get(2))) {
						if (((List<?>) o).get(0).equals("TCPROS") && port != null) {
							return Arrays.asList(1, "ready", Arrays.asList("TCPROS", local, port));
						}
					}
					return Arrays.asList(-1, "Unsupported method", Arrays.asList());
				} else {
					Logger.getGlobal().warning("Unknown call: " + method + params);
					return Arrays.asList(-1, "Unknown Command");
				}
			}

		});
		server.start();

		nodeUri = "http://" + local + ":" + server.getPort() + "/";

	}

	/**
	 * Retrieves all publishers present on the ROS master
	 * 
	 * @return Assignment of publishers (XMLRPC addresses) to topic names
	 * @throws IOException if an I/O error occurs
	 */
	public Map<String, List<String>> getPublishers() throws IOException {
		Map<String, List<String>> ret = new HashMap<String, List<String>>();
		List<?> resp = (List<?>) master.execute("getSystemState", Arrays.asList(callerId));
		if ((Integer) resp.get(0) == 1) {
			List<?> state = (List<?>) resp.get(2);
			List<?> publishers = (List<?>) state.get(0);
			for (int i = 0; i < publishers.size(); i++) {
				List<?> line = (List<?>) publishers.get(i);
				List<String> pubs = new ArrayList<String>();
				for (Object o : (List<?>) line.get(1))
					pubs.add((String) o);
				ret.put((String) line.get(0), pubs);
			}
		}
		return ret;
	}

	/**
	 * Retrieves all subscribers present on the ROS master
	 * 
	 * @return Assignment of subscribers (XMLRPC addresses) to topic names
	 * @throws IOException if an I/O error occurs
	 */
	public Map<String, List<String>> getSubscribers() throws IOException {
		Map<String, List<String>> ret = new HashMap<String, List<String>>();
		List<?> resp = (List<?>) master.execute("getSystemState", Arrays.asList(callerId));
		if ((Integer) resp.get(0) == 1) {
			List<?> state = (List<?>) resp.get(2);
			List<?> subscribers = (List<?>) state.get(1);
			for (int i = 0; i < subscribers.size(); i++) {
				List<?> line = (List<?>) subscribers.get(i);
				List<String> pubs = new ArrayList<String>();
				for (Object o : (List<?>) line.get(1))
					pubs.add((String) o);
				ret.put((String) line.get(0), pubs);
			}
		}
		return ret;
	}

	/**
	 * Retrieves the XMLRPC address of a node
	 * 
	 * @param node name of the node
	 * @return XMLRPC address of the node
	 * @throws IOException if an I/O error occurs
	 */
	public String lookupNode(String node) throws IOException {
		List<?> ret = (List<?>) master.execute("lookupNode", Arrays.asList(callerId, node));
		return (String) ret.get(2);
	}

	/**
	 * Retrieves the type of a given topic (requires at least one publisher)
	 * 
	 * @param topic name of the topic
	 * @return type object for the topic
	 * @throws IOException if an I/O error occurs
	 */
	public ROSType<?> getTopicPrototype(String topic) throws IOException {
		String type = getTopicTypes().get(topic);
		List<String> publishers = getPublishers().get(topic);
		if (publishers == null)
			throw new IOException("No publishers found for topic " + topic);
		;
		for (String pub : publishers) {
			URL uri = new URL(lookupNode(pub));
			RpcClient client = new RpcClient(resolveHost(uri.getHost()), uri.getPort());
			List<?> provider = (List<?>) client.execute("requestTopic",
					Arrays.asList(callerId, topic, Arrays.asList(Arrays.asList("TCPROS"))));

			Object spec = provider.get(2);
			if (((List<?>) spec).size() == 0)
				continue;
			if (((List<?>) spec).get(0).equals("TCPROS")) {
				String host = ((List<?>) spec).get(1).toString();
				Integer port = (Integer) (((List<?>) spec).get(2));
				return TcpRosSubscriber.getMessagePrototype(resolveHost(host), port, callerId, topic, type);
			}
		}
		throw new IOException("No publishers found for topic " + topic);
	}

	/**
	 * Retrieves the types of existing topics
	 * 
	 * @return Assignment of topic types to topic names
	 * @throws IOException if an I/O error occurs
	 */
	public Map<String, String> getTopicTypes() throws IOException {
		Map<String, String> ret = new HashMap<String, String>();
		List<?> resp = (List<?>) master.execute("getTopicTypes", Arrays.asList(callerId));
		if ((Integer) resp.get(0) == 1) {
			List<?> lines = (List<?>) resp.get(2);
			for (int i = 0; i < lines.size(); i++) {
				List<?> line = (List<?>) lines.get(i);
				ret.put((String) line.get(0), (String) line.get(1));
			}
		}
		return ret;
	}

	/**
	 * Publish a given topic
	 * 
	 * @param topic    topic to publish
	 * @param msg      type of the message to publish
	 * @param latching decides whether new subscribers should get the previously
	 *                 published message (true), or only new messages (false)
	 * @return Publishing that allows to control the publication
	 * @throws IOException if the node already publishes the topic, or if an I/O
	 *                     error occurs
	 */
	public Publishing publish(final String topic, ROSstruct msg, boolean latching) throws IOException {
		if (portsForPublish.containsKey(topic))
			throw new IOException("Topic already published");
		Logger.getGlobal().info("Publishing " + topic);

		final TcpRosPublisher tcps = new TcpRosPublisher(0, callerId, topic, msg, latching);
		portsForPublish.put(topic, tcps.getPort());
		tcps.start();

		master.execute("registerPublisher", Arrays.asList(callerId, topic, msg.getName(), nodeUri));

		Publishing ret = new Publishing() {

			@Override
			public void send(byte[] message) throws IOException {
				tcps.setMessage(message, 0, msg.skip(message, 0));
			}

			@Override
			public void stop() throws IOException {
				Logger.getGlobal().info("Unpublishing " + topic);
				master.execute("unregisterPublisher", Arrays.asList(callerId, topic, nodeUri));
				tcps.stop();
				portsForPublish.remove(topic);
				publishings.remove(topic);
			}
		};
		publishings.put(topic, ret);
		return ret;
	}

	/**
	 * Subscribe to a given topic
	 * 
	 * @param topic      topic to publish
	 * @param msg        type of the message to publish
	 * @param subscriber callback to receive the messages
	 * @throws IOException if the node already subscribes to the topic, or if an I/O
	 *                     error occurs
	 */
	public void subscribe(String topic, ROSstruct msg, Subscriber subscriber) throws IOException {
		Logger.getGlobal().info("Subscribing " + topic);
		if (subscriptions.containsKey(topic))
			throw new IOException("Topic already subscribed");
		subscriptions.put(topic, subscriber);
		prototypes.put(topic, msg);
		List<?> reg = (List<?>) master.execute("registerSubscriber",
				Arrays.asList(callerId, topic, msg.getName(), nodeUri));
		publisherUpdate(topic, (List<?>) reg.get(2));
	}

	/**
	 * Unsubscribe from a given topic
	 * 
	 * @param topic topic to publish
	 * @throws IOException if an I/O error occurs
	 */
	public void unsubscribe(String topic) throws IOException {
		Logger.getGlobal().info("Unsubscribing " + topic);
		master.execute("unregisterSubscriber", Arrays.asList(callerId, topic, nodeUri));
		publisherUpdate(topic, Arrays.asList());
		prototypes.remove(topic);
		subscriptions.remove(topic);
	}

	/**
	 * Handles the reception of a complete list of publishers for a topic this node
	 * is subscribed to
	 */
	private void publisherUpdate(String topic, List<?> pubs) {
		try {
			if (!publishers.containsKey(topic))
				publishers.put(topic, new ArrayList<String>());
			for (String pub : new ArrayList<String>(publishers.get(topic))) {
				if (!pubs.contains(pub)) {
					publisherRemoved(topic, pub);
					publishers.get(topic).remove(pub);
				}
			}
			for (Object sub : pubs) {
				if (!publishers.get(topic).contains(sub)) {
					publisherAdded(topic, (String) sub);
					publishers.get(topic).add((String) sub);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handles the addition of a publisher to a topic this node is subscribed to
	 */
	private void publisherAdded(String topic, String publisher) throws IOException {
		Logger.getGlobal().info("Got publisher " + publisher + " for topic " + topic);
		URL uri = new URL(publisher);
		RpcClient client = new RpcClient(resolveHost(uri.getHost()), uri.getPort());
		List<?> provider = (List<?>) client.execute("requestTopic", Arrays
				.asList("/rapi-org.roboticsapi.communication.ros", topic, Arrays.asList(Arrays.asList("TCPROS"))));

		Object spec = provider.get(2);
		if (((List<?>) spec).size() == 0)
			return;
		if (((List<?>) spec).get(0).equals("TCPROS")) {
			connections.put(publisher + " for " + topic,
					new TcpRosSubscriber(resolveHost(((List<?>) spec).get(1).toString()),
							(Integer) (((List<?>) spec).get(2)), "/rapi-org.roboticsapi.communication.ros", topic,
							prototypes.get(topic), subscriptions.get(topic)));
		}
	}

	/**
	 * Handles the removal of a publisher for a topic this node is subscribed to
	 */
	private void publisherRemoved(String topic, String publisher) throws IOException {
		Logger.getGlobal().info("Removed publisher " + publisher + " for topic " + topic);
		if (connections.containsKey(publisher + " for " + topic)) {
			connections.remove(publisher + " for " + topic).stop();
		}
	}

	/**
	 * Resolves the host from the defined host map
	 */
	private String resolveHost(String host) {
		if (hostMap.containsKey(host))
			return hostMap.get(host);
		return host;
	}

	/**
	 * Shuts down the node
	 * 
	 * @throws IOException IOException if an I/O error occurs
	 */
	public void shutdown() throws IOException {
		try {
			for (Publishing p : publishings.values()) {
				try {
					p.stop();
				} catch (IOException e) {
				}
			}
		} catch (ConcurrentModificationException e) {
		}

		try {
			for (String t : subscriptions.keySet()) {
				try {
					unsubscribe(t);
				} catch (IOException e) {
				}
			}
		} catch (ConcurrentModificationException e) {
		}

		server.stop();
	}

}

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2016-2019 ISSE, University of Augsburg 
 */

package de.isse.jros;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.isse.jros.internal.xmlrpc.RpcClient;
import de.isse.jros.internal.xmlrpc.RpcServer;
import de.isse.jros.internal.xmlrpc.RpcServer.ServerInterface;

/**
 * Minimal implementation of a ROS master supporting topics (but no services or
 * parameter server), for use in tests and publish/subscribe-only scenarios
 */
public class RosMaster {

	private Map<String, String> topicTypes = new HashMap<String, String>();
	private Map<String, List<String>> topicSubscribers = new HashMap<String, List<String>>();
	private Map<String, List<String>> topicPublishers = new HashMap<String, List<String>>();
	private Map<String, String> nodeUris = new HashMap<String, String>();

	private RpcServer server;

	/**
	 * Starts a new ROS master
	 * 
	 * @param port port to listen on
	 * @throws IOException when the port is already in use
	 */
	public RosMaster(int port) throws IOException {
		server = new RpcServer(port, new ServerInterface() {
			@Override
			public List<?> execute(String method, List<?> params) {
				if ("registerService".equals(method)) {
					return registerService((String) params.get(0), (String) params.get(1), (String) params.get(2),
							(String) params.get(3));
				} else if ("unregisterService".equals(method)) {
					return unregisterService((String) params.get(0), (String) params.get(1), (String) params.get(2));
				} else if ("registerSubscriber".equals(method)) {
					return registerSubscriber((String) params.get(0), (String) params.get(1), (String) params.get(2),
							(String) params.get(3));
				} else if ("unregisterSubscriber".equals(method)) {
					return unregisterSubscriber((String) params.get(0), (String) params.get(1), (String) params.get(2));
				} else if ("registerPublisher".equals(method)) {
					return registerPublisher((String) params.get(0), (String) params.get(1), (String) params.get(2),
							(String) params.get(3));
				} else if ("unregisterPublisher".equals(method)) {
					return unregisterPublisher((String) params.get(0), (String) params.get(1), (String) params.get(2));
				} else if ("lookupNode".equals(method)) {
					return lookupNode((String) params.get(0), (String) params.get(1));
				} else if ("getPublishedTopics".equals(method)) {
					return getPublishedTopics((String) params.get(0), (String) params.get(1));
				} else if ("getTopicTypes".equals(method)) {
					return getTopicTypes((String) params.get(0));
				} else if ("getSystemState".equals(method)) {
					return getSystemState((String) params.get(0));
				} else if ("getUri".equals(method)) {
					return getUri((String) params.get(0));
				} else if ("lookupService".equals(method)) {
					return lookupNode((String) params.get(0), (String) params.get(1));
				}
				return null;
			}
		});
		server.start();
	}

	/**
	 * Register the caller as a provider of the specified service.
	 * 
	 * @param caller_id   ROS caller ID
	 * @param service     Fully-qualified name of service
	 * @param service_api ROSRPC Service URI
	 * @param caller_api  XML-RPC URI of caller node
	 * @return
	 */
	protected List<?> registerService(String caller_id, String service, String service_api, String caller_api) {
		return error("not implemented", 0);
	}

	/**
	 * Unregister the caller as a provider of the specified service.
	 * 
	 * @param caller_id   ROS caller ID
	 * @param service     Fully-qualified name of service
	 * @param service_api API URI of service to unregister. Unregistration will only
	 *                    occur if current registration matches.
	 * @return Number of unregistrations (either 0 or 1). If this is zero it means
	 *         that the caller was not registered as a service provider. The call
	 *         still succeeds as the intended final state is reached.
	 */
	protected List<?> unregisterService(String caller_id, String service, String service_api) {
		return error("not implemented", 0);
	}

	/**
	 * Subscribe the caller to the specified topic. In addition to receiving a list
	 * of current publishers, the subscriber will also receive notifications of new
	 * publishers via the publisherUpdate API.
	 * 
	 * @param caller_id  ROS caller ID
	 * @param topic      Fully-qualified name of topic
	 * @param topic_type Datatype for topic. Must be a package-resource name, i.e.
	 *                   the .msg name
	 * @param caller_api API URI of subscriber to register. Will be used for new
	 *                   publisher notifications
	 * @return a list of XMLRPC API URIs for nodes currently publishing the
	 *         specified topic.
	 */
	protected List<?> registerSubscriber(String caller_id, String topic, String topic_type, String caller_api) {
		if (!topicTypes.containsKey(topic)) {
			topicTypes.put(topic, topic_type);
		} else if (!topicTypes.get(topic).equals(topic_type)) {
			return failure("Incorrect topic type for topic.", new ArrayList<String>());
		}
		if (!topicSubscribers.containsKey(topic))
			topicSubscribers.put(topic, new ArrayList<String>());
		topicSubscribers.get(topic).add(caller_api);

		List<String> ret = new ArrayList<String>();
		if (topicPublishers.containsKey(topic))
			ret.addAll(topicPublishers.get(topic));

		nodeUris.put(caller_id, caller_api);

		return success("subscriber registered", ret);
	}

	/**
	 * Unregister the caller as a publisher of the topic
	 * 
	 * @param caller_id  ROS caller ID
	 * @param topic      Fully-qualified name of topic
	 * @param caller_api API URI of service to unregister. Unregistration will only
	 *                   occur if current registration matches
	 * @return If numUnsubscribed is zero it means that the caller was not
	 *         registered as a subscriber. The call still succeeds as the intended
	 *         final state is reached.
	 */
	protected List<?> unregisterSubscriber(String caller_id, String topic, String caller_api) {
		if (!topicSubscribers.containsKey(topic))
			return success("subscriber removed", 0);

		boolean removed = topicSubscribers.get(topic).remove(caller_api);
		if (topicSubscribers.get(topic).isEmpty()) {
			topicSubscribers.remove(topic);
			if (!topicPublishers.containsKey(topic)) {
				topicTypes.remove(topic);
			}
		}

		return success("subscriber removed", removed ? 1 : 0);
	}

	/**
	 * Register the caller as a publisher the topic
	 * 
	 * @param caller_id  ROS caller ID
	 * @param topic      Fully-qualified name of topic to register
	 * @param topic_type Datatype for topic. Must be a package-resource name, i.e.
	 *                   the .msg name
	 * @param caller_api API URI of publisher to register
	 * @return List of current subscribers of topic in the form of XMLRPC URIs.
	 */
	protected List<?> registerPublisher(String caller_id, String topic, String topic_type, String caller_api) {
		if (!topicTypes.containsKey(topic)) {
			topicTypes.put(topic, topic_type);
		} else if (!topicTypes.get(topic).equals(topic_type)) {
			return failure("Incorrect topic type for topic.", new ArrayList<String>());
		}
		if (!topicPublishers.containsKey(topic))
			topicPublishers.put(topic, new ArrayList<String>());
		topicPublishers.get(topic).add(caller_api);

		publisherUpdate(caller_id, topic);
		List<String> ret = new ArrayList<String>();
		if (topicSubscribers.containsKey(topic)) {
			ret.addAll(topicSubscribers.get(topic));
		}

		nodeUris.put(caller_id, caller_api);

		return success("publisher registered", ret);
	}

	/**
	 * Unregister the caller as a publisher of the topic
	 * 
	 * @param caller_id  ROS caller ID
	 * @param topic      Fully-qualified name of topic to unregister
	 * @param caller_api API URI of publisher to unregister. Unregistration will
	 *                   only occur if current registration matches
	 * @return If numUnregistered is zero it means that the caller was not
	 *         registered as a publisher. The call still succeeds as the intended
	 *         final state is reached.
	 */
	protected List<?> unregisterPublisher(String caller_id, String topic, String caller_api) {
		if (!topicPublishers.containsKey(topic))
			return success("publisher removed", 0);

		boolean removed = topicPublishers.get(topic).remove(caller_api);
		if (topicPublishers.get(topic).isEmpty()) {
			topicPublishers.remove(topic);
			if (!topicSubscribers.containsKey(topic)) {
				topicTypes.remove(topic);
			}
		}
		publisherUpdate(caller_id, topic);

		return success("publisher removed", removed ? 1 : 0);
	}

	private void publisherUpdate(String caller_id, String topic) {
		List<String> publishers = new ArrayList<String>();
		if (topicPublishers.containsKey(topic))
			publishers.addAll(topicPublishers.get(topic));
		if (topicSubscribers.containsKey(topic)) {
			for (String subscriber : topicSubscribers.get(topic)) {
				try {
					URI uri = new URI(subscriber);
					new RpcClient(uri.getHost(), uri.getPort()).execute("publisherUpdate",
							Arrays.asList(getMasterUri(caller_id), topic, publishers));
				} catch (IOException e) {
					e.printStackTrace();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Get the XML-RPC URI of the node with the associated name/caller_id. This API
	 * is for looking information about publishers and subscribers. Use
	 * lookupService instead to lookup ROS-RPC URIs.
	 * 
	 * @param caller_id ROS caller ID
	 * @param node_name Name of node to lookup
	 * @return URI
	 */
	protected List<?> lookupNode(String caller_id, String node_name) {
		return success("here you are", nodeUris.get(node_name));
	}

	/**
	 * Get list of topics that can be subscribed to. This does not return topics
	 * that have no publishers. See getSystemState() to get more comprehensive list.
	 * 
	 * @param caller_id ROS caller ID
	 * @param subgraph  Restrict topic names to match within the specified subgraph.
	 *                  Subgraph namespace is resolved relative to the caller's
	 *                  namespace. Use emptry string to specify all names.
	 * @return [ [topic1, type1]...[topicN, typeN] ]
	 */
	protected List<?> getPublishedTopics(String caller_id, String subgraph) {
		List<List<String>> ret = new ArrayList<List<String>>();
		for (String topic : topicPublishers.keySet()) {
			if (topic.startsWith(subgraph))
				ret.add(Arrays.asList(topic, topicTypes.get(topic)));
		}
		return success("here you are", ret);
	}

	/**
	 * Retrieve list topic names and their types
	 * 
	 * @param caller_id ROS caller ID
	 * @return topicTypes is a list of [topicName, topicType] pairs.
	 */
	protected List<?> getTopicTypes(String caller_id) {
		List<List<String>> ret = new ArrayList<List<String>>();
		for (String topic : topicTypes.keySet()) {
			ret.add(Arrays.asList(topic, topicTypes.get(topic)));
		}
		return success("here you are", ret);
	}

	/**
	 * Retrieve list representation of system state (i.e. publishers, subscribers,
	 * and services).
	 * 
	 * @param caller_id ROS caller ID
	 * @return System state is in list representation
	 * 
	 *         [publishers, subscribers, services]
	 * 
	 *         publishers is of the form
	 * 
	 *         [ [topic1, [topic1Publisher1...topic1PublisherN]] ... ]
	 * 
	 *         subscribers is of the form
	 * 
	 *         [ [topic1, [topic1Subscriber1...topic1SubscriberN]] ... ]
	 * 
	 *         services is of the form
	 * 
	 *         [ [service1, [service1Provider1...service1ProviderN]] ... ]
	 */
	protected List<?> getSystemState(String caller_id) {

		List<Object> publishers = new ArrayList<Object>();
		List<Object> subscribers = new ArrayList<Object>();
		List<Object> services = new ArrayList<Object>();

		for (Entry<String, List<String>> entry : topicPublishers.entrySet()) {
			publishers.add(Arrays.asList(entry.getKey(), entry.getValue()));
		}

		for (Entry<String, List<String>> entry : topicSubscribers.entrySet()) {
			subscribers.add(Arrays.asList(entry.getKey(), entry.getValue()));
		}

		return success("here you are", Arrays.asList(publishers, subscribers, services));
	}

	/**
	 * Get the URI of the master.
	 * 
	 * @param caller_id ROS caller ID
	 * @return masterURI
	 */
	protected List<?> getUri(String caller_id) {
		return success("here you are", getMasterUri(caller_id));
	}

	/**
	 * Lookup all provider of a particular service.
	 * 
	 * @param caller_id ROS caller ID
	 * @param service   Fully-qualified name of service
	 * @return service URL is provides address and port of the service. Fails if
	 *         there is no provider.
	 */
	protected List<?> lookupService(String caller_id, String service) {
		return error("not implemented", 0);
	}

	private String getMasterUri(String caller_id) {
		return server.getUri();
	}

	/**
	 * Error on the part of the caller, e.g. an invalid parameter. In general, this
	 * means that the master/slave did not attempt to execute the action
	 * 
	 * @param statusMessage
	 * @param response
	 * @return
	 */
	private List<?> error(String statusMessage, Object response) {
		return Arrays.asList((Integer) (-1), (String) statusMessage, response);
	}

	/**
	 * Method failed to complete correctly. In general, this means that the
	 * master/slave attempted the action and failed, and there may have been
	 * side-effects as a result.
	 * 
	 * @param statusMessage
	 * @param response
	 * @return
	 */
	private List<?> failure(String statusMessage, Object response) {
		return Arrays.asList((Integer) 0, (String) statusMessage, response);
	}

	/**
	 * Method completed successfully
	 * 
	 * @param statusMessage
	 * @param response
	 * @return
	 */
	private List<?> success(String statusMessage, Object response) {
		return Arrays.asList((Integer) 1, statusMessage, response);
	}

	/**
	 * Stops the ROS master
	 * 
	 * @throws IOException if an I/O error occurs
	 */
	public void shutdown() throws IOException {
		server.stop();
		server = null;
	}

	/**
	 * Starts a ROS master on the default port 11311
	 * 
	 * @throws IOException if the port is already in use
	 */
	public RosMaster() throws IOException {
		this(11311);
	}

}

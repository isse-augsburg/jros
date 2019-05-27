/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2016-2019 ISSE, University of Augsburg 
 */

package de.isse.jros.messages;

import de.isse.jros.fields.RArray;
import de.isse.jros.fields.RFloat;
import de.isse.jros.fields.RMessage;
import de.isse.jros.fields.RString;
import de.isse.jros.messages.StdMsgs.Header;
import de.isse.jros.types.ROSfixedArray;
import de.isse.jros.types.ROSfloat64;
import de.isse.jros.types.ROSstruct;

/**
 * Messages for common geometric primitives such as points, vectors, and poses.
 */
public class GeometryMsgs {

	/** A vector in free space. */
	public static ROSstruct Vector3() {
		return new ROSstruct("geometry_msgs/Vector3").withFloat64("x").withFloat64("y").withFloat64("z");
	}

	/** A vector in free space. */
	public static class Vector3 extends RMessage {
		public final RFloat x = field("x", RFloat.float64());
		public final RFloat y = field("y", RFloat.float64());
		public final RFloat z = field("z", RFloat.float64());
		public final ROSstruct TYPE = type("geometry_msgs/Vector3");
	}

	/** The position of a point in free space */
	public static ROSstruct Point() {
		return new ROSstruct("geometry_msgs/Point").withFloat64("x").withFloat64("y").withFloat64("z");
	}

	/** The position of a point in free space */
	public static class Point extends RMessage {
		public final RFloat x = field("x", RFloat.float64());
		public final RFloat y = field("y", RFloat.float64());
		public final RFloat z = field("z", RFloat.float64());
		public final ROSstruct TYPE = type("geometry_msgs/Point");
	}

	/** An orientation in free space in quaternion form */
	public static ROSstruct Quaternion() {
		return new ROSstruct("geometry_msgs/Quaternion").withFloat64("x").withFloat64("y").withFloat64("z")
				.withFloat64("w");
	}

	/** An orientation in free space in quaternion form */
	public static class Quaternion extends RMessage {
		public final RFloat x = field("x", RFloat.float64());
		public final RFloat y = field("y", RFloat.float64());
		public final RFloat z = field("z", RFloat.float64());
		public final RFloat w = field("w", RFloat.float64());
		public final ROSstruct TYPE = type("geometry_msgs/Quaternion");
	}

	/** A pose in free space */
	public static ROSstruct Pose() {
		return new ROSstruct("geometry_msgs/Pose").withField("position", Point()).withField("orientation",
				Quaternion());
	}

	/** A pose in free space */
	public static class Pose extends RMessage {
		public final Point position = field("position", new Point());
		public final Quaternion orientation = field("orientation", new Quaternion());
		public final ROSstruct TYPE = type("geometry_msgs/Pose");
	}

	/** A pose in free space with uncertainty */
	public static ROSstruct PoseWithCovariance() {
		return new ROSstruct("geometry_msgs/PoseWithCovariance").withField("pose", Pose()).withField("covariance",
				new ROSfixedArray(ROSfloat64.TYPE, 36));
	}

	/** A pose in free space with uncertainty */
	public static class PoseWithCovariance extends RMessage {
		public final Pose pose = field("pose", new Pose());
		public final RArray<RFloat> covariance = array("covariance", 36, RFloat.float64());
		public final ROSstruct TYPE = type("geometry_msgs/PoseWithCovariance");
	}

	/** A Pose with reference coordinate frame and timestamp */
	public static ROSstruct PoseStamped() {
		return new ROSstruct("geometry_msgs/PoseStamped").withField("header", StdMsgs.Header()).withField("pose",
				Pose());
	}

	/** A Pose with reference coordinate frame and timestamp */
	public static class PoseStamped extends RMessage {
		public final Header header = field("header", new Header());
		public final Pose pose = field("pose", new Pose());
		public final ROSstruct TYPE = type("geometry_msgs/PoseStamped");
	}

	/** A velocity in free space */
	public static ROSstruct Twist() {
		return new ROSstruct("geometry_msgs/Twist").withField("linear", Vector3()).withField("angular", Vector3());
	}

	/** A velocity in free space */
	public static class Twist extends RMessage {
		public final Vector3 linear = field("linear", new Vector3());
		public final Vector3 angular = field("angular", new Vector3());
		public final ROSstruct TYPE = type("geometry_msgs/Twist");
	}

	/** A velocity in free space with uncertainty. */
	public static ROSstruct TwistWithCovariance() {
		return new ROSstruct("geometry_msgs/TwistWithCovariance").withField("twist", Twist()).withField("covariance",
				new ROSfixedArray(ROSfloat64.TYPE, 36));
	}

	/** A velocity in free space with uncertainty. */
	public static class TwistWithCovariance extends RMessage {
		public final Twist twist = field("twist", new Twist());
		public final RArray<RFloat> covariance = array("covariance", 36, RFloat.float64());
		public final ROSstruct TYPE = type("geometry_msgs/TwistWithCovariance");
	}

	/** The transform between two coordinate frames in free space. */
	public static ROSstruct Transform() {
		return new ROSstruct("geometry_msgs/Transform").withField("translation", Vector3()).withField("rotation",
				Quaternion());
	}

	/** The transform between two coordinate frames in free space. */
	public static class Transform extends RMessage {
		public final Vector3 translation = field("translation", new Vector3());
		public final Quaternion rotation = field("rotation", new Quaternion());
		public final ROSstruct TYPE = type("geometry_msgs/Transform");
	}

	/** This expresses a transform from header.frame_id to child_frame_id */
	public static ROSstruct TransformStamped() {
		return new ROSstruct("geometry_msgs/TransformStamped").withField("header", StdMsgs.Header())
				.withString("child_frame_id").withField("transform", Transform());
	}

	/** This expresses a transform from header.frame_id to child_frame_id */
	public static class TransformStamped extends RMessage {
		public final Header header = field("header", new Header());
		public final RString child_frame_id = field("child_frame_id", RString.string());
		public final Transform transform = field("transform", new Transform());
		public final ROSstruct TYPE = type("geometry_msgs/TransformStamped");
	}

}

jros - A Lightweight Java client library for ROS publish / subscribe with dynamic type support
==============================================================================================

jros is a lightweight Java implementation of a ROS node (and master). 
It has no external dependencies, and supports dynamic message types 
(i.e. message types that are not yet known at compile time).
 
The current implementation mainly supports publishing and subscribing to topics,
* using types defined at compile time
* using types programmatically generated at run time 
* using types derived from messages currently published by other nodes.

Additionally, the included tests use a minimal ROS master implementation 
with support for publish/subscribe, however without services or parameters.

In most cases, using the official ros-java implementation is preferred, 
however in cases where code generation for message types is not an option, 
this implementation may be helpful to fill the gap.

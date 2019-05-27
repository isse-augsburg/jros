/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2016-2019 ISSE, University of Augsburg
 */

module de.isse.jros {
	requires java.base;
	requires java.logging;

	exports de.isse.jros;
	exports de.isse.jros.descriptors;
	exports de.isse.jros.types;
	exports de.isse.jros.fields;
	exports de.isse.jros.messages;
}
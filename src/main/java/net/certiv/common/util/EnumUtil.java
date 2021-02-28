/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.certiv.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Utility library to provide helper methods for Java enums. */
public class EnumUtil {

	private EnumUtil() {}

	/**
	 * Gets the {@code Map} of enums by name.
	 *
	 * @param <E> the type of the enumeration
	 * @param enumClass the class of the enum to query, not null
	 * @return the modifiable map of enum names to enums, never null
	 */
	public static <E extends Enum<E>> Map<String, E> getEnumMap(final Class<E> enumClass) {
		final Map<String, E> map = new LinkedHashMap<>();
		for (final E e : enumClass.getEnumConstants()) {
			map.put(e.name(), e);
		}
		return map;
	}

	/**
	 * Gets the {@code List} of enums.
	 *
	 * @param <E> the type of the enumeration
	 * @param enumClass the class of the enum to query, not null
	 * @return the modifiable list of enums, never null
	 */
	public static <E extends Enum<E>> List<E> getEnumList(final Class<E> enumClass) {
		return new ArrayList<>(Arrays.asList(enumClass.getEnumConstants()));
	}

	/**
	 * Checks if the specified name is a valid enum for the class.
	 * <p>
	 * This method differs from {@link Enum#valueOf} in that checks if the name is a
	 * valid enum without needing to catch the exception.
	 *
	 * @param <E> the type of the enumeration
	 * @param enumClass the class of the enum to query, not null
	 * @param name the enum name, null returns false
	 * @return true if the enum name is valid, otherwise false
	 */
	public static <E extends Enum<E>> boolean isValidEnum(final Class<E> enumClass, final String name) {
		return getEnum(enumClass, name) != null;
	}

	/**
	 * Checks if the specified name is a valid enum for the class.
	 * <p>
	 * This method differs from {@link Enum#valueOf} in that checks if the name is a
	 * valid enum without needing to catch the exception and performs case
	 * insensitive matching of the name.
	 *
	 * @param <E> the type of the enumeration
	 * @param enumClass the class of the enum to query, not null
	 * @param name the enum name, null returns false
	 * @return true if the enum name is valid, otherwise false
	 */
	public static <E extends Enum<E>> boolean isValidEnumIgnoreCase(final Class<E> enumClass, final String name) {
		return getEquivEnum(enumClass, name) != null;
	}

	/**
	 * Gets the enum for the class, returning {@code null} if not found.
	 * <p>
	 * Replaces every dash character in the name with an underscore.
	 * <p>
	 * This method differs from {@link Enum#valueOf} in that it does not throw an
	 * exception for an invalid enum name.
	 *
	 * @param <E> the type of the enumeration
	 * @param enumClass the class of the enum to query, not null
	 * @param name the enum name, null returns null
	 * @return the enum, null if not found
	 */
	public static <E extends Enum<E>> E getEnum(final Class<E> enumClass, final String name) {
		if (name == null) return null;

		try {
			return Enum.valueOf(enumClass, name.replace(Chars.DASH, Chars.LOWDASH));
		} catch (final IllegalArgumentException ex) {
			return null;
		}
	}

	/**
	 * Gets the enum for the class, returning {@code null} if not found.
	 * <p>
	 * Replaces every dash character in the name with an underscore.
	 * <p>
	 * This method differs from {@link Enum#valueOf} in that it does not throw an
	 * exception for an invalid enum name and performs case insensitive matching of
	 * the name.
	 *
	 * @param <E> the type of the enumeration
	 * @param enumClass the class of the enum to query, not null
	 * @param name the enum name, null returns null
	 * @return the enum, null if not found
	 */
	public static <E extends Enum<E>> E getEquivEnum(final Class<E> enumClass, final String name) {
		if (name == null || !enumClass.isEnum()) return null;

		for (final E value : enumClass.getEnumConstants()) {
			if (value.name().equalsIgnoreCase(name.replace(Chars.DASH, Chars.LOWDASH))) return value;
		}
		return null;
	}
}

package com.fo0.vaadin.projects.utils;

import java.util.Collection;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.Lists;

public class StreamUtils {

	/**
	 * parallelization of optimized streaming
	 * 
	 * @param <T>
	 * @param values
	 * @return unordered stream result
	 * @Created 08.09.2019 - 22:30:01
	 * @author Thomas Euringer (GH:fo0)
	 */
	@SafeVarargs
	public static <T> Stream<T> orderedParallelStream(T... values) {
		return Stream.of(values).unordered().parallel().distinct();
	}

	@SuppressWarnings("unchecked")
	public static <T> Stream<T> stream(T... values) {
		if (ArrayUtils.isEmpty(values)) {
			return (Stream<T>) Lists.newArrayList().stream();
		}

		return Stream.of(values);
	}

	@SuppressWarnings("unchecked")
	public static <T> Stream<T> stream(Collection<T> values) {
		if (CollectionUtils.isEmpty(values)) {
			return (Stream<T>) Lists.newArrayList().stream();
		}

		return values.stream();
	}
}
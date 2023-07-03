package net.certiv.common.util;

import java.nio.file.Path;

import one.microstream.persistence.binary.util.ObjectCopier;
import one.microstream.storage.embedded.types.EmbeddedStorage;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;

public class Persist {

	/**
	 * Create a deep copy of the given argument.
	 *
	 * @param <T> the argument type
	 * @param arg the argument to copy
	 * @return a deep copy
	 */
	public static <T> T dup(T arg) {
		return ObjectCopier.New().copy(arg);
	}

	/**
	 * Store the given data graph in the object store located in the given path directory.
	 *
	 * @param <T>  the data graph type
	 * @param path object store directory
	 * @param data a data graph to store
	 */
	public static <T> void store(Path path, T data) {
		EmbeddedStorageManager mgr = EmbeddedStorage.start(path);
		mgr.store(data);
	}

	/**
	 * Load the data contents of the object store located in the given path directory.
	 *
	 * @param <T>  the data graph type
	 * @param path object store directory
	 * @return the restored data graph, or {@code null} if the object store does not exist
	 */
	@SuppressWarnings("unchecked")
	public static <T> T load(Path path) {
		EmbeddedStorageManager mgr = EmbeddedStorage.start(path);
		return (T) mgr.root();
	}
}

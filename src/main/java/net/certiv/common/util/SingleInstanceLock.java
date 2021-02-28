/**
 * <pre>
 * 		void test() {
 *   		SingleInstanceLock ua = new SingleInstanceLock("JustOneId");
 *    		if (ua.isAppActive()) {
 * 				System.out.println("Already active.");
 * 				System.exit(1);    
 * 			}
 *		}
 * </pre>
 */
package net.certiv.common.util;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

public class SingleInstanceLock {

	private String appName;
	private File file;
	private FileChannel channel;
	private FileLock lock;

	public SingleInstanceLock(String appName) {
		this.appName = appName;
	}

	@SuppressWarnings("resource")
	public boolean isAppActive() {
		try {
			file = new File(System.getProperty("user.home"), appName + ".tmp");
			channel = new RandomAccessFile(file, "rw").getChannel();

			try {
				lock = channel.tryLock();
			} catch (OverlappingFileLockException e) {
				// already locked
				closeLock();
				return true;
			}

			if (lock == null) {
				closeLock();
				return true;
			}

			Runtime.getRuntime().addShutdownHook(new Thread() {

				// destroy the lock when the JVM is closing
				@Override
				public void run() {
					closeLock();
					deleteFile();
				}
			});
			return false;
		} catch (Exception e) {
			closeLock();
			return true;
		}
	}

	private void closeLock() {
		try {
			lock.release();
		} catch (Exception e) {}
		try {
			channel.close();
		} catch (Exception e) {}
	}

	private void deleteFile() {
		try {
			file.delete();
		} catch (Exception e) {}
	}
}

package net.certiv.common.util;

import java.io.PrintStream;
import java.io.PrintWriter;

public class PrintStackUtil {

	static public void printChildren(Status status, PrintStream output) {
		Status[] children = status.getChildren();
		if (children == null || children.length == 0) return;
		for (Status child : children) {
			output.println("Contains: " + child.getMessage()); //$NON-NLS-1$
			Throwable exception = child.getException();
			if (exception != null) exception.printStackTrace(output);
			printChildren(child, output);
		}
	}

	static public void printChildren(Status status, PrintWriter output) {
		Status[] children = status.getChildren();
		if (children == null || children.length == 0) return;
		for (Status child : children) {
			output.println("Contains: " + child.getMessage()); //$NON-NLS-1$
			output.flush(); // call to synchronize output
			Throwable exception = child.getException();
			if (exception != null) exception.printStackTrace(output);
			printChildren(child, output);
		}
	}

}

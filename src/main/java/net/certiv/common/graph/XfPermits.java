package net.certiv.common.graph;

public enum XfPermits {
	CONSOLIDATE("Consolidation(s) not permitted."),
	COPY("Copying not permitted."),
	MOVE("Move(s) not permitted."),
	TRANSFER("Transfer(s) not permitted."),
	RETERMINATE("Retermination(s) not permitted."),
	REDUCE("Reduction(s) not permitted."),
	REMOVE_NODE("Remove node(s) not permitted."),
	REMOVE_EDGE("Remove edge(s) not permitted."),
	REPLICATE("Replication(s) not permitted."),

	;

	private final String err;

	XfPermits(String err) {
		this.err = err;
	}

	public String err() {
		return err;
	}
}

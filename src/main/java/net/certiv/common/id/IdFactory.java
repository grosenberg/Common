package net.certiv.common.id;

import java.util.List;

public class IdFactory extends SIdFactory<Seq> {

	private static Id ANON_ID;
	private static Id UNKNOWN_ID;

	public static Id anon() {
		if (ANON_ID == null) {
			ANON_ID = new Id(DEFAULT, List.of(ANON));
		}
		return ANON_ID;
	}

	public static Id unknown() {
		if (UNKNOWN_ID == null) {
			UNKNOWN_ID = new Id(DEFAULT, List.of(UNKNOWN));
		}
		return UNKNOWN_ID;
	}

	public IdFactory(String ns) {
		super(ns);
	}

	@Override
	protected Seq __make(List<String> names) {
		return new Seq(names);
	}
}

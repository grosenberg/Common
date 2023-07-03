package net.certiv.common.dot;

import java.util.stream.Stream;

public enum Shape implements IDotStr {
	assembly,
	box,
	box3d,
	cds,
	circle,
	component,
	cylinder,
	diamond,
	doublecircle,
	doubleoctagon,
	egg,
	ellipse,
	fivepoverhang,
	folder,
	hexagon,
	house,
	insulator,
	invhouse,
	invtrapezium,
	invtriange,
	larrow,
	lpromoter,
	Mcircle,
	Mdiamond,
	Mrecord,
	Mhouse,
	none,
	note,
	noverhang,
	octagon,
	oval,
	parallelogram,
	pentagon,
	plain,
	plaintext,
	point,
	polygon,
	primersite,
	promotor,
	proteasite,
	proteinstab,
	rarrow,
	record,
	rect,
	rectangle,
	restrictionsite,
	ribosite,
	rnastab,
	rpromoter,
	septagon,
	signature,
	square,
	star,
	tab,
	terminator,
	threepoverhang,
	trapezium,
	triange,
	tripleoctagon,
	underline,
	utr;

	private static String[] valArray;

	public static String[] toArray() {
		if (valArray == null) {
			valArray = Stream.of(values()).map(Shape::name).toArray(String[]::new);
		}
		return valArray;
	}
}

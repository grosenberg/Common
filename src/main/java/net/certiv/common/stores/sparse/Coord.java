package net.certiv.common.stores.sparse;

import java.util.Comparator;
import java.util.Objects;

public class Coord implements Comparable<Coord>, Comparator<Coord> {

	public static Coord of() {
		return new Coord(0, 0);
	}

	public static Coord of(int col, int row) {
		return new Coord(col, row);
	}

	public static Coord of(long x, long y) {
		return new Coord(x, y);
	}

	// --------------------------------

	private long x;
	private long y;

	protected Coord(long x, long y) {
		this.x = x;
		this.y = y;
	}

	public long x() {
		return x;
	}

	public long y() {
		return y;
	}

	public long col() {
		return x;
	}

	public long row() {
		return y;
	}

	public void setX(long x) {
		this.x = x;
	}

	public void setY(long y) {
		this.y = y;
	}

	public Coord plus(Coord coord) {
		return new Coord(x + coord.x, y + coord.y);
	}

	public Coord minus(Coord coord) {
		return new Coord(x - coord.x, y - coord.y);
	}

	@Override
	public int compareTo(Coord o) {
		return compare(this, o);
	}

	@Override
	public int compare(Coord c1, Coord c2) {
		if (c1.x < c2.x) return -1;
		if (c1.x > c2.x) return 1;
		if (c1.y < c2.y) return -1;
		if (c1.y > c2.y) return 1;
		return 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Coord)) return false;
		Coord other = (Coord) obj;
		return x == other.x && y == other.y;
	}

	@Override
	public String toString() {
		return String.format("%d:%d", x, y);
	}
}

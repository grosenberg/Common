package net.certiv.common.sheet;

import java.util.Objects;

public class Cell {

	/** Cell content indent. */
	private int indent;
	/** Cell content. */
	private String content;

	public Cell(int indent) {
		this.indent = indent;
	}

	/**
	 * Constructs a new Cell with the given content.
	 *
	 * @param indent  content indent
	 * @param content cell content
	 */
	public Cell(int indent, String content) {
		this.indent = indent;
		this.content = content;
	}

	public boolean setIndent(int indent) {
		this.indent = indent;
		return true;
	}

	public String content() {
		return content;
	}

	public boolean setContent(String content) {
		this.content = content;
		return true;
	}

	/** Returns the computed width of the indent+content. */
	public int width() {
		return indent + content.length();
	}

	@Override
	public int hashCode() {
		return Objects.hash(content);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Cell other = (Cell) obj;
		return Objects.equals(content, other.content);
	}

	@Override
	public String toString() {
		return content.toString();
	}
}

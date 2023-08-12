package net.certiv.common.stores.context.type;

import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

import net.certiv.common.util.Chars;
import net.certiv.common.util.Strings;

/** Signed Hexadecimal value. */
public class Hex extends Number implements Comparable<Hex> {

	private static final String NFE = "Invalid hex value [%s] at offset %s in '%s'.";

	public static final Pattern PREFIX = Pattern.compile("[+-]?0[xX]");

	/** All possible 'digits' for representing a hex character. */
	public static final Set<Character> DIGITS = Set.of( //
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', //
			'a', 'b', 'c', 'd', 'e', 'f', //
			'A', 'B', 'C', 'D', 'E', 'F' //
	);

	public final boolean neg;
	public final String hex;

	/**
	 * Returns a {@code Hex} object holding the value of the specified {@code String}. The
	 * argument is interpreted as representing a hexadecimal integer, optionally with a
	 * {@code '0x'} prefix.
	 *
	 * @param arg the string to be parsed.
	 * @return a {@code Hex} object holding the value represented by the string argument
	 * @throws NumberFormatException if the string cannot be parsed as a {@code Hex}
	 *                               object.
	 */
	public static Hex parseString(String arg) throws NumberFormatException {
		String hex = Strings.deQuote(arg);
		hex = !Strings.blank(hex) ? hex : "0";
		boolean neg = hex.charAt(0) == Chars.DASH ? true : false;
		hex = PREFIX.matcher(hex).replaceFirst(Strings.EMPTY);
		for (int idx = 0, len = hex.length(); idx < len; idx++) {
			if (!DIGITS.contains(hex.charAt(idx))) {
				throw new NumberFormatException(String.format(NFE, hex.charAt(idx), idx, hex));
			}
		}
		return new Hex(hex, neg);
	}

	public static Hex parseByteArray(byte[] bytes) {
		return new Hex(encode(bytes).toUpperCase());
	}

	private static String encode(byte[] bytes) {
		StringBuilder result = new StringBuilder();
		for (byte num : bytes) {
			result.append(Character.forDigit((num & 0xF), 16));
			result.append(Character.forDigit((num >> 4) & 0xF, 16));
		}
		return result.toString();
	}

	public static Hex parseDouble(double val) {
		return new Hex(Double.toHexString(val));
	}

	public static Hex parseLong(long val) {
		return new Hex(Long.toHexString(val));
	}

	public static Hex parseInt(int val) {
		return new Hex(Integer.toHexString(val));
	}

	protected Hex(String hex) {
		this(hex, false);
	}

	protected Hex(String hex, boolean neg) {
		this.hex = hex;
		this.neg = neg;
	}

	/** Unsigned byte array. */
	public byte[] byteValues() {
		return decode(hex);
	}

	private byte[] decode(String hex) {
		if ((hex.length() % 2) > 0) {
			hex = "0" + hex;
		}
		byte[] bytes = new byte[hex.length() / 2];
		for (int idx = 0; idx < hex.length() - 1; idx += 2) {
			int high = toDigit(hex.charAt(idx));
			int low = toDigit(hex.charAt(idx + 1));
			bytes[idx / 2] = (byte) ((high << 4) + low);
		}
		return bytes;
	}

	private int toDigit(char ch) {
		int digit = Character.digit(ch, 16);
		if (digit == -1) throw new IllegalArgumentException("Invalid Hexadecimal: " + ch);
		return digit;
	}

	@Override
	public int intValue() {
		return Integer.parseInt(hex, 16);
	}

	@Override
	public double doubleValue() {
		long arg = unsignedLongValue(hex);
		double dbl = Double.longBitsToDouble(arg);
		return !neg ? dbl : -dbl;
	}

	@Override
	public float floatValue() {
		return ((Long) longValue()).floatValue();
	}

	@Override
	public long longValue() {
		long arg = unsignedLongValue(hex);
		return !neg ? arg : -arg;
	}

	private long unsignedLongValue(String hex) {
		if (hex.length() != 16) return Long.parseLong(hex, 16);
		return (unsignedLongValue(hex.substring(0, 1)) << 60) | unsignedLongValue(hex.substring(1));
	}

	@Override
	public int compareTo(Hex o) {
		if (!neg && o.neg) return 1;
		if (neg && !o.neg) return -1;
		return hex.compareToIgnoreCase(o.hex);
	}

	@Override
	public int hashCode() {
		return Objects.hash(hex, neg);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Hex)) return false;
		Hex other = (Hex) obj;
		return Objects.equals(hex, other.hex) && neg == other.neg;
	}

	@Override
	public String toString() {
		return (neg ? Strings.DASH : Strings.EMPTY) + "0x" + hex;
	}
}

package net.certiv.common.stores.context.type;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.certiv.common.ex.NotImplementedException;
import net.certiv.common.stores.context.Value;
import net.certiv.common.util.Strings;

public class Numeric {

	private static final String ERR_NUM = "Unsupported numeric type: %s";

	/** List of the known numerics. */
	public static final List<Class<?>> NUMS = List.of(Hex.class, Double.class, Long.class, Integer.class);

	public static final Set<Character> HEX_CHARS = Set.of( //
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', //
			'a', 'b', 'c', 'd', 'e', 'f', //
			'A', 'B', 'C', 'D', 'E', 'F', //
			'x', 'X', '+', '-' //
	);
	public static final Set<Character> HEX_HIGH = Set.of( //
			'a', 'b', 'c', 'd', 'e', 'f', //
			'A', 'B', 'C', 'D', 'E', 'F', //
			'x', 'X' //
	);

	/**
	 * Resolves the given string, reprsenting a possibly signed number, to a
	 * {@code Number}.
	 * <p>
	 * Hex should be prefixed with {@code 0x}; others suffixed with {@code d, f, or l} for
	 * double, float, or long. Otherwise, resolves to hex if the number contains a high
	 * hex digit or to double if a decimal is present. Defaults to integer.
	 */
	public static Number resolve(String num) {
		if (Hex.PREFIX.matcher(num).lookingAt()) return Hex.parseString(num);
		char ch = num.charAt(num.length() - 1);
		String lead = num.substring(0, num.length() - 1);
		switch (ch) {
			case 'd':
			case 'D':
				if (lead.codePoints().anyMatch(d -> HEX_HIGH.contains((char) d))) return Hex.parseString(num);
				return Double.parseDouble(num);

			case 'f':
			case 'F':
				if (lead.codePoints().anyMatch(d -> HEX_HIGH.contains((char) d))) return Hex.parseString(num);
				return ((Float) Float.parseFloat(num)).doubleValue();

			case 'l':
			case 'L':
				return Long.parseLong(num);

			default:
				if (num.codePoints().anyMatch(d -> HEX_HIGH.contains((char) d))) return Hex.parseString(num);
				if (num.contains(Strings.DOT)) return Double.parseDouble(num);
				return Integer.parseInt(num);
		}
	}

	/** Returns the effectively wider known class of the two given numerics. */
	public static Class<?> effective(Class<Number> a, Class<Number> b) {
		if (a == b && NUMS.contains(a)) return a;
		int adx = NUMS.indexOf(a);
		if (adx < 0) throw new NotImplementedException(ERR_NUM, a.getName());
		int bdx = NUMS.indexOf(b);
		if (bdx < 0) throw new NotImplementedException(ERR_NUM, b.getName());
		return NUMS.get(Math.min(adx, bdx));
	}

	/**
	 * Returns a new {@code Value} having the underlying value(s) of the given
	 * {@code Value } conformed to the given class type. Any associated {@code Key} type
	 * must be {@code Number} to prevent a key cast problem.
	 *
	 * @param value
	 * @param target
	 * @return
	 */
	public static Value<?> conformTo(Value<?> value, Class<?> target) {
		switch (NUMS.indexOf(target)) {
			case 0:
				return hexValue(value);
			case 1:
				return dblValue(value);
			case 2:
				return longValue(value);
			case 3:
				return intValue(value);
			default:
				throw new IllegalArgumentException(String.format(ERR_NUM, target.getName()));
		}
	}

	@SuppressWarnings("unchecked")
	private static Value<?> hexValue(Value<?> value) {
		if (value.collection()) {
			Collection<Number> beg = hexValues((Collection<Number>) value.beg());
			if (value.range()) {
				Collection<Number> end = hexValues((Collection<Number>) value.end());
				return Value.of(beg, end, value.unit());
			}
			return Value.of(beg, value.unit());

		}

		Number beg = hexValue((Number) value.beg());
		if (value.range()) {
			Number end = hexValue((Number) value.end());
			return Value.of(beg, end, value.unit());
		}
		return Value.of(beg, value.unit());
	}

	private static Collection<Number> hexValues(Collection<Number> values) {
		Collection<Number> list = values.stream().map(n -> hexValue(n)).collect(Collectors.toList());
		values.clear();
		values.addAll(list);
		return values;
	}

	private static Number hexValue(Number value) {
		if (value instanceof Double) return Hex.parseDouble((double) value);
		if (value instanceof Long) return Hex.parseLong((long) value);
		if (value instanceof Integer) return Hex.parseInt((int) value);
		return value;
	}

	@SuppressWarnings("unchecked")
	private static Value<?> dblValue(Value<?> value) {
		if (value.collection()) {
			Collection<Number> beg = dblValues((Collection<Number>) value.beg());
			if (value.range()) {
				Collection<Number> end = dblValues((Collection<Number>) value.end());
				return Value.of(beg, end, value.unit());
			}
			return Value.of(beg, value.unit());

		}

		Number beg = dblValue((Number) value.beg());
		if (value.range()) {
			Number end = dblValue((Number) value.end());
			return Value.of(beg, end, value.unit());
		}
		return Value.of(beg, value.unit());
	}

	private static Collection<Number> dblValues(Collection<Number> values) {
		Collection<Number> list = values.stream().map(n -> dblValue(n)).collect(Collectors.toList());
		values.clear();
		values.addAll(list);
		return values;
	}

	private static Number dblValue(Number value) {
		if (value instanceof Hex) return ((Hex) value).doubleValue();
		return (double) value;
	}

	@SuppressWarnings("unchecked")
	private static Value<?> longValue(Value<?> value) {
		if (value.collection()) {
			Collection<Number> beg = longValues((Collection<Number>) value.beg());
			if (value.range()) {
				Collection<Number> end = longValues((Collection<Number>) value.end());
				return Value.of(beg, end, value.unit());
			}
			return Value.of(beg, value.unit());

		}

		Number beg = longValue((Number) value.beg());
		if (value.range()) {
			Number end = longValue((Number) value.end());
			return Value.of(beg, end, value.unit());
		}
		return Value.of(beg, value.unit());
	}

	private static Collection<Number> longValues(Collection<Number> values) {
		Collection<Number> list = values.stream().map(n -> longValue(n)).collect(Collectors.toList());
		values.clear();
		values.addAll(list);
		return values;
	}

	private static Number longValue(Number value) {
		if (value instanceof Hex) return ((Hex) value).longValue();
		return (long) value;
	}

	@SuppressWarnings("unchecked")
	private static Value<?> intValue(Value<?> value) {
		if (value.collection()) {
			Collection<Number> beg = intValues((Collection<Number>) value.beg());
			if (value.range()) {
				Collection<Number> end = intValues((Collection<Number>) value.end());
				return Value.of(beg, end, value.unit());
			}
			return Value.of(beg, value.unit());

		}

		Number beg = intValue((Number) value.beg());
		if (value.range()) {
			Number end = intValue((Number) value.end());
			return Value.of(beg, end, value.unit());
		}
		return Value.of(beg, value.unit());
	}

	private static Collection<Number> intValues(Collection<Number> values) {
		Collection<Number> list = values.stream().map(n -> intValue(n)).collect(Collectors.toList());
		values.clear();
		values.addAll(list);
		return values;
	}

	private static Number intValue(Number value) {
		if (value instanceof Hex) return ((Hex) value).intValue();
		return (int) value;
	}
}

package net.certiv.common.sheet;

import java.util.IllegalFormatException;
import java.util.LinkedHashMap;
import java.util.Map;

import net.certiv.common.util.Chars;

public class Ascii {

	/** Ascii line character types: none, single, double, or thick. */
	public enum Line {
		NONE,
		SINGLE,
		DOUBLE,
		THICK;
	}

	private static final Map<Integer, Integer> Cache = new LinkedHashMap<>();

	/**
	 * Find an Ascii line art character with the given positional {@linkplain Line}
	 * attributes.
	 *
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 * @return the corresponding ascii character
	 */
	public static int find(Line left, Line top, Line right, Line bottom) {
		int pos = left.ordinal() << 3;
		pos += top.ordinal() << 2;
		pos += right.ordinal() << 1;
		pos += bottom.ordinal();

		if (Cache.containsKey(pos)) return Cache.get(pos);

		try {
			int ch = LineChar.valueOf(String.format("A%04d", pos)).cp;
			Cache.put(pos, ch);
			return ch;

		} catch (IllegalFormatException e) {
			throw new RuntimeException(e);

		} catch (IllegalArgumentException e) {
			boolean adj = false;

			if (left == Line.DOUBLE) {
				left = Line.THICK;
				adj = true;
			}
			if (top == Line.DOUBLE) {
				top = Line.THICK;
				adj = true;
			}
			if (right == Line.DOUBLE) {
				right = Line.THICK;
				adj = true;
			}
			if (bottom == Line.DOUBLE) {
				bottom = Line.THICK;
				adj = true;
			}

			if (adj) return find(left, top, right, bottom);

			int ch = Chars.STAR; // denotes unknown
			Cache.put(pos, ch);
			return ch;
		}
	}

	private enum LineChar {
		A0001('\u2577'),	// ╷
		A0003('\u257b'),	// ╻

		A0100('\u2575'),	// ╵
		A0300('\u2579'),	// ╹

		A0011('\u250c'),	// ┌
		A0012('\u2553'),	// ╓
		A0013('\u250e'),	// ┎

		A0021('\u2552'),	// ╒
		A0022('\u2554'),	// ╔

		A0031('\u250d'),	// ┍
		A0033('\u250f'),	// ┏

		A0111('\u251c'),	// ├
		A0212('\u255f'),	// ╟
		A0313('\u2520'),	// ┠

		A0311('\u251e'),	// ┞

		A0113('\u251f'),	// ┟

		A0110('\u2514'),	// └
		A0210('\u2559'),	// ╙
		A0310('\u2516'),	// ┖

		A0120('\u2558'),	// ╘
		A0130('\u2515'),	// ┕

		A0220('\u255a'),	// ╚
		A0330('\u2517'),	// ┗

		A1001('\u2510'),	// ┐
		A1002('\u2556'),	// ╖
		A1003('\u2512'),	// ┒

		A1101('\u2524'),	// ┤
		A1202('\u2562'),	// ╢
		A1303('\u2528'),	// ┨

		A0121('\u255e'),	// ╞
		A0131('\u251d'),	// ┝

		A0222('\u2560'),	// ╠
		A0333('\u2523'),	// ┣

		A1301('\u2526'),	// ┦
		A1103('\u2527'),	// ┧

		A0331('\u2521'),	// ┡
		A0133('\u2522'),	// ┢

		A1100('\u2518'),	// ┘
		A1200('\u255c'),	// ╜
		A1300('\u251a'),	// ┚

		A1011('\u252c'),	// ┬
		A1012('\u2565'),	// ╥
		A1013('\u2530'),	// ┰

		A1110('\u2534'),	// ┴
		A1210('\u2538'),	// ┸
		A1130('\u2536'),	// ┶
		A1330('\u253a'),	// ┺

		A1111('\u253c'),	// ┼

		A1113('\u2541'),	// ╁
		A1131('\u253e'),	// ┾
		A1133('\u2546'),	// ╆

		A1212('\u256b'),	// ╫

		A1311('\u2540'),	// ╀
		A1313('\u2542'),	// ╂
		A1331('\u2544'),	// ╄
		A1333('\u254a'),	// ╊

		A2001('\u2555'),	// ╕
		A2002('\u2557'),	// ╗

		A2021('\u2564'),	// ╤
		A2022('\u2566'),	// ╦

		A2100('\u255b'),	// ╛
		A2101('\u2561'),	// ╡
		A2121('\u256a'),	// ╪

		A2220('\u2569'),	// ╩
		A2222('\u256c'),	// ╬

		A2200('\u255d'),	// ╝

		A2202('\u2563'),	// ╣
		A2301('\u2529'),	// ┩

		A3003('\u2513'),	// ┓
		A3011('\u252d'),	// ┭
		A3013('\u2531'),	// ┱
		A3031('\u252f'),	// ┯
		A3033('\u2533'),	// ┳

		A3100('\u2519'),	// ┙
		A3101('\u2525'),	// ┥

		A3103('\u252a'),	// ┪
		A3110('\u2535'),	// ┵
		A3111('\u253d'),	// ┽
		A3113('\u2545'),	// ╅
		A3130('\u2537'),	// ┷
		A3131('\u253f'),	// ┿
		A3133('\u2548'),	// ╈

		A3300('\u251b'),	// ┛
		A3301('\u2529'),	// ┩
		A3303('\u252b'),	// ┫

		A3310('\u2539'),	// ┹
		A3311('\u2543'),	// ╃
		A3313('\u2549'),	// ╉

		A3330('\u253b'),	// ┻
		A3331('\u2547'),	// ╇
		A3333('\u254b'),	// ╋

		;

		public final int cp;

		LineChar(int cp) {
			this.cp = cp;
		}
	}
}

/*******************************************************************************
 * Copyright (c) 2016, 2018 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation (bug #461506)
 *******************************************************************************/
package net.certiv.common.dot;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.certiv.common.util.Strings;

/**
 * The color information contained by this class has been taken from the
 * graphviz website: http://www.graphviz.org/content/color-names
 */
public class DotColors {

	/**
	 * Returns the valid dot color scheme names.
	 *
	 * @return The list of valid dot color scheme names.
	 */
	public static List<String> getColorSchemes() {
		List<String> colorSchemes = new ArrayList<>();
		colorSchemes.add("x11"); //$NON-NLS-1$
		colorSchemes.add("svg"); //$NON-NLS-1$
		colorSchemes.addAll(brewerColorSchemes.keySet());
		return colorSchemes;
	}

	public static String[] getColorNames() {
		return getColorNames(Strings.EMPTY).toArray(new String[0]);
	}

	/**
	 * Returns the valid dot color names defined within the given
	 * <i>colorScheme</i>.
	 *
	 * @param colorScheme The name of the color scheme.
	 * @return The list of valid dot color names defined within the given
	 *             <i>colorScheme</i>.
	 */
	public static List<String> getColorNames(String colorScheme) {
		List<String> colorNames = new ArrayList<>();
		switch (colorScheme) {
			case "x11": //$NON-NLS-1$
				colorNames.addAll(x11ColorScheme.keySet());
				break;
			case "svg": //$NON-NLS-1$
				colorNames.addAll(svgColorScheme.keySet());
				break;
			default:
				String[] colorValuesArray = brewerColorSchemes.get(colorScheme);
				if (colorValuesArray != null) {
					for (int i = 0; i < colorValuesArray.length; i++) {
						colorNames.add(Integer.toString(i + 1));
					}
				}
		}
		return colorNames;
	}

	/**
	 * Calculates the detailed description in html form providing more information
	 * about the given color.
	 *
	 * @param colorScheme The name of the color scheme, can be null.
	 * @param colorName The name of the color, can be null.
	 * @param colorCode The hex code of the color, should not be null.
	 * @return the detailed description in html form
	 */
	public static String getColorDescription(String colorScheme, String colorName, String colorCode) {
		String nl = System.lineSeparator();
		StringBuilder sb = new StringBuilder();
		sb.append("<table border=1>" + nl);
		sb.append("	<tr>" + nl);
		sb.append("		<td><b>color preview</b></td>" + nl);
		if (colorScheme != null) {
			sb.append("		<td><b>color scheme</b></td>" + nl);
		}
		if (colorName != null) {
			sb.append("		<td><b>color name</b></td>" + nl);
		}
		sb.append("		<td><b>color code</b></td>" + nl);
		sb.append("	</tr>" + nl);
		sb.append("	<tr>" + nl);
		sb.append(
				"		<td border=0 align=\"center\"><div style=\"border:1px solid black;width:50px;height:16px;background-color:");
		sb.append(colorCode);
		sb.append(";\"</div></td>" + nl);
		if (colorScheme != null) {
			sb.append("		<td align=\"center\">" + colorScheme + "</td>" + nl);
		}
		if (colorName != null) {
			sb.append("		<td align=\"center\">" + colorName + "</td>" + nl);
		}
		sb.append("		<td align=\"center\">" + colorCode + "</td>" + nl);
		sb.append("	</tr>" + nl);
		sb.append("</table>" + nl);
		return sb.toString();
	}

	/**
	 * Returns the color code (in hexadecimal form) of the given <i>colorName</i>
	 * considering the given <i>colorScheme</i>, or null if the color code cannot be
	 * determined.
	 *
	 * @param colorScheme The name of the color scheme.
	 * @param colorName The name of the color.
	 * @return the color code or null if the color code cannot be determined.
	 */
	public static String get(String colorScheme, String colorName) {
		switch (colorScheme) {
			case "x11": //$NON-NLS-1$
				return x11ColorScheme.get(colorName);
			case "svg": //$NON-NLS-1$
				return svgColorScheme.get(colorName);
			default:
				String[] colorValuesArray = brewerColorSchemes.get(colorScheme);
				if (colorValuesArray != null) {
					int colorID;
					try {
						colorID = Integer.parseInt(colorName);
					} catch (NumberFormatException e) {
						return null;
					}
					if (colorID > 0 && colorValuesArray.length >= colorID) {
						return colorValuesArray[colorID - 1];
					}
				}
		}
		return null;
	}

	private static Map<String, String> x11ColorScheme = new LinkedHashMap<>();
	static {
		x11ColorScheme.put("aliceblue", "#f0f8ff");  //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("antiquewhite", "#faebd7"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("antiquewhite1", "#ffefdb"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("antiquewhite2", "#eedfcc"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("antiquewhite3", "#cdc0b0"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("antiquewhite4", "#8b8378"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("aquamarine", "#7fffd4"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("aquamarine1", "#7fffd4"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("aquamarine2", "#76eec6"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("aquamarine3", "#66cdaa"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("aquamarine4", "#458b74"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("azure", "#f0ffff"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("azure1", "#f0ffff"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("azure2", "#e0eeee"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("azure3", "#c1cdcd"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("azure4", "#838b8b"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("beige", "#f5f5dc"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("bisque", "#ffe4c4"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("bisque1", "#ffe4c4"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("bisque2", "#eed5b7"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("bisque3", "#cdb79e"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("bisque4", "#8b7d6b"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("black", "#000000"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("blanchedalmond", "#ffebcd"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("blue", "#0000ff"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("blue1", "#0000ff"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("blue2", "#0000ee"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("blue3", "#0000cd"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("blue4", "#00008b"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("blueviolet", "#8a2be2"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("brown", "#a52a2a"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("brown1", "#ff4040"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("brown2", "#ee3b3b"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("brown3", "#cd3333"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("brown4", "#8b2323"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("burlywood", "#deb887"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("burlywood1", "#ffd39b"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("burlywood2", "#eec591"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("burlywood3", "#cdaa7d"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("burlywood4", "#8b7355"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("cadetblue", "#5f9ea0"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("cadetblue1", "#98f5ff"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("cadetblue2", "#8ee5ee"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("cadetblue3", "#7ac5cd"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("cadetblue4", "#53868b"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("chartreuse", "#7fff00"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("chartreuse1", "#7fff00"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("chartreuse2", "#76ee00"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("chartreuse3", "#66cd00"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("chartreuse4", "#458b00"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("chocolate", "#d2691e"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("chocolate1", "#ff7f24"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("chocolate2", "#ee7621"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("chocolate3", "#cd661d"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("chocolate4", "#8b4513"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("coral", "#ff7f50"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("coral1", "#ff7256"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("coral2", "#ee6a50"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("coral3", "#cd5b45"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("coral4", "#8b3e2f"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("cornflowerblue", "#6495ed"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("cornsilk", "#fff8dc"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("cornsilk1", "#fff8dc"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("cornsilk2", "#eee8cd"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("cornsilk3", "#cdc8b1"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("cornsilk4", "#8b8878"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("crimson", "#dc143c"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("cyan", "#00ffff"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("cyan1", "#00ffff"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("cyan2", "#00eeee"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("cyan3", "#00cdcd"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("cyan4", "#008b8b"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("darkgoldenrod", "#b8860b"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("darkgoldenrod1", "#ffb90f"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("darkgoldenrod2", "#eead0e"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("darkgoldenrod3", "#cd950c"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("darkgoldenrod4", "#8b6508"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("darkgreen", "#006400"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("darkkhaki", "#bdb76b"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("darkolivegreen", "#556b2f"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("darkolivegreen1", "#caff70"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("darkolivegreen2", "#bcee68"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("darkolivegreen3", "#a2cd5a"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("darkolivegreen4", "#6e8b3d"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("darkorange", "#ff8c00"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("darkorange1", "#ff7f00"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("darkorange2", "#ee7600"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("darkorange3", "#cd6600"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("darkorange4", "#8b4500"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("darkorchid", "#9932cc"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("darkorchid1", "#bf3eff"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("darkorchid2", "#b23aee"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("darkorchid3", "#9a32cd"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("darkorchid4", "#68228b"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("darksalmon", "#e9967a"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("darkseagreen", "#8fbc8f"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("darkseagreen1", "#c1ffc1"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("darkseagreen2", "#b4eeb4"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("darkseagreen3", "#9bcd9b"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("darkseagreen4", "#698b69"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("darkslateblue", "#483d8b"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("darkslategray", "#2f4f4f"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("darkslategray1", "#97ffff"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("darkslategray2", "#8deeee"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("darkslategray3", "#79cdcd"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("darkslategray4", "#528b8b"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("darkslategrey", "#2f4f4f"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("darkturquoise", "#00ced1"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("darkviolet", "#9400d3"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("deeppink", "#ff1493"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("deeppink1", "#ff1493"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("deeppink2", "#ee1289"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("deeppink3", "#cd1076"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("deeppink4", "#8b0a50"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("deepskyblue", "#00bfff"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("deepskyblue1", "#00bfff"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("deepskyblue2", "#00b2ee"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("deepskyblue3", "#009acd"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("deepskyblue4", "#00688b"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("dimgray", "#696969"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("dimgrey", "#696969"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("dodgerblue", "#1e90ff"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("dodgerblue1", "#1e90ff"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("dodgerblue2", "#1c86ee"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("dodgerblue3", "#1874cd"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("dodgerblue4", "#104e8b"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("firebrick", "#b22222"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("firebrick1", "#ff3030"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("firebrick2", "#ee2c2c"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("firebrick3", "#cd2626"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("firebrick4", "#8b1a1a"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("floralwhite", "#fffaf0"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("forestgreen", "#228b22"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gainsboro", "#dcdcdc"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("ghostwhite", "#f8f8ff"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gold", "#ffd700"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gold1", "#ffd700"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gold2", "#eec900"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gold3", "#cdad00"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gold4", "#8b7500"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("goldenrod", "#daa520"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("goldenrod1", "#ffc125"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("goldenrod2", "#eeb422"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("goldenrod3", "#cd9b1d"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("goldenrod4", "#8b6914"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray", "#c0c0c0"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray0", "#000000"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray1", "#030303"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray10", "#1a1a1a"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray100", "#ffffff"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray11", "#1c1c1c"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray12", "#1f1f1f"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray13", "#212121"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray14", "#242424"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray15", "#262626"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray16", "#292929"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray17", "#2b2b2b"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray18", "#2e2e2e"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray19", "#303030"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray2", "#050505"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray20", "#333333"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray21", "#363636"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray22", "#383838"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray23", "#3b3b3b"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray24", "#3d3d3d"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray25", "#404040"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray26", "#424242"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray27", "#454545"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray28", "#474747"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray29", "#4a4a4a"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray3", "#080808"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray30", "#4d4d4d"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray31", "#4f4f4f"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray32", "#525252"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray33", "#545454"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray34", "#575757"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray35", "#595959"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray36", "#5c5c5c"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray37", "#5e5e5e"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray38", "#616161"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray39", "#636363"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray4", "#0a0a0a"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray40", "#666666"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray41", "#696969"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray42", "#6b6b6b"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray43", "#6e6e6e"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray44", "#707070"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray45", "#737373"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray46", "#757575"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray47", "#787878"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray48", "#7a7a7a"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray49", "#7d7d7d"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray5", "#0d0d0d"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray50", "#7f7f7f"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray51", "#828282"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray52", "#858585"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray53", "#878787"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray54", "#8a8a8a"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray55", "#8c8c8c"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray56", "#8f8f8f"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray57", "#919191"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray58", "#949494"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray59", "#969696"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray6", "#0f0f0f"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray60", "#999999"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray61", "#9c9c9c"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray62", "#9e9e9e"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray63", "#a1a1a1"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray64", "#a3a3a3"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray65", "#a6a6a6"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray66", "#a8a8a8"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray67", "#ababab"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray68", "#adadad"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray69", "#b0b0b0"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray7", "#121212"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray70", "#b3b3b3"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray71", "#b5b5b5"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray72", "#b8b8b8"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray73", "#bababa"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray74", "#bdbdbd"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray75", "#bfbfbf"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray76", "#c2c2c2"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray77", "#c4c4c4"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray78", "#c7c7c7"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray79", "#c9c9c9"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray8", "#141414"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray80", "#cccccc"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray81", "#cfcfcf"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray82", "#d1d1d1"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray83", "#d4d4d4"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray84", "#d6d6d6"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray85", "#d9d9d9"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray86", "#dbdbdb"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray87", "#dedede"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray88", "#e0e0e0"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray89", "#e3e3e3"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray9", "#171717"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray90", "#e5e5e5"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray91", "#e8e8e8"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray92", "#ebebeb"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray93", "#ededed"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray94", "#f0f0f0"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray95", "#f2f2f2"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray96", "#f5f5f5"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray97", "#f7f7f7"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray98", "#fafafa"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("gray99", "#fcfcfc"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("green", "#00ff00"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("green1", "#00ff00"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("green2", "#00ee00"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("green3", "#00cd00"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("green4", "#008b00"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("greenyellow", "#adff2f"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey", "#c0c0c0"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey0", "#000000"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey1", "#030303"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey10", "#1a1a1a"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey100", "#ffffff"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey11", "#1c1c1c"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey12", "#1f1f1f"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey13", "#212121"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey14", "#242424"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey15", "#262626"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey16", "#292929"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey17", "#2b2b2b"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey18", "#2e2e2e"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey19", "#303030"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey2", "#050505"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey20", "#333333"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey21", "#363636"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey22", "#383838"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey23", "#3b3b3b"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey24", "#3d3d3d"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey25", "#404040"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey26", "#424242"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey27", "#454545"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey28", "#474747"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey29", "#4a4a4a"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey3", "#080808"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey30", "#4d4d4d"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey31", "#4f4f4f"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey32", "#525252"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey33", "#545454"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey34", "#575757"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey35", "#595959"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey36", "#5c5c5c"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey37", "#5e5e5e"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey38", "#616161"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey39", "#636363"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey4", "#0a0a0a"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey40", "#666666"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey41", "#696969"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey42", "#6b6b6b"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey43", "#6e6e6e"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey44", "#707070"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey45", "#737373"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey46", "#757575"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey47", "#787878"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey48", "#7a7a7a"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey49", "#7d7d7d"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey5", "#0d0d0d"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey50", "#7f7f7f"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey51", "#828282"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey52", "#858585"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey53", "#878787"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey54", "#8a8a8a"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey55", "#8c8c8c"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey56", "#8f8f8f"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey57", "#919191"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey58", "#949494"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey59", "#969696"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey6", "#0f0f0f"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey60", "#999999"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey61", "#9c9c9c"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey62", "#9e9e9e"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey63", "#a1a1a1"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey64", "#a3a3a3"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey65", "#a6a6a6"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey66", "#a8a8a8"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey67", "#ababab"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey68", "#adadad"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey69", "#b0b0b0"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey7", "#121212"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey70", "#b3b3b3"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey71", "#b5b5b5"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey72", "#b8b8b8"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey73", "#bababa"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey74", "#bdbdbd"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey75", "#bfbfbf"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey76", "#c2c2c2"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey77", "#c4c4c4"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey78", "#c7c7c7"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey79", "#c9c9c9"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey8", "#141414"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey80", "#cccccc"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey81", "#cfcfcf"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey82", "#d1d1d1"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey83", "#d4d4d4"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey84", "#d6d6d6"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey85", "#d9d9d9"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey86", "#dbdbdb"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey87", "#dedede"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey88", "#e0e0e0"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey89", "#e3e3e3"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey9", "#171717"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey90", "#e5e5e5"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey91", "#e8e8e8"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey92", "#ebebeb"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey93", "#ededed"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey94", "#f0f0f0"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey95", "#f2f2f2"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey96", "#f5f5f5"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey97", "#f7f7f7"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey98", "#fafafa"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("grey99", "#fcfcfc"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("honeydew", "#f0fff0"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("honeydew1", "#f0fff0"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("honeydew2", "#e0eee0"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("honeydew3", "#c1cdc1"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("honeydew4", "#838b83"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("hotpink", "#ff69b4"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("hotpink1", "#ff6eb4"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("hotpink2", "#ee6aa7"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("hotpink3", "#cd6090"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("hotpink4", "#8b3a62"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("indianred", "#cd5c5c"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("indianred1", "#ff6a6a"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("indianred2", "#ee6363"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("indianred3", "#cd5555"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("indianred4", "#8b3a3a"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("indigo", "#4b0082"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("invis", "#fffffe"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("ivory", "#fffff0"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("ivory1", "#fffff0"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("ivory2", "#eeeee0"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("ivory3", "#cdcdc1"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("ivory4", "#8b8b83"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("khaki", "#f0e68c"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("khaki1", "#fff68f"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("khaki2", "#eee685"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("khaki3", "#cdc673"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("khaki4", "#8b864e"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lavender", "#e6e6fa"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lavenderblush", "#fff0f5"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lavenderblush1", "#fff0f5"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lavenderblush2", "#eee0e5"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lavenderblush3", "#cdc1c5"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lavenderblush4", "#8b8386"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lawngreen", "#7cfc00"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lemonchiffon", "#fffacd"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lemonchiffon1", "#fffacd"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lemonchiffon2", "#eee9bf"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lemonchiffon3", "#cdc9a5"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lemonchiffon4", "#8b8970"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lightblue", "#add8e6"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lightblue1", "#bfefff"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lightblue2", "#b2dfee"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lightblue3", "#9ac0cd"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lightblue4", "#68838b"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lightcoral", "#f08080"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lightcyan", "#e0ffff"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lightcyan1", "#e0ffff"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lightcyan2", "#d1eeee"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lightcyan3", "#b4cdcd"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lightcyan4", "#7a8b8b"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lightgoldenrod", "#eedd82"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lightgoldenrod1", "#ffec8b"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lightgoldenrod2", "#eedc82"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lightgoldenrod3", "#cdbe70"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lightgoldenrod4", "#8b814c"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lightgoldenrodyellow", "#fafad2"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lightgray", "#d3d3d3"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lightgrey", "#d3d3d3"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lightpink", "#ffb6c1"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lightpink1", "#ffaeb9"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lightpink2", "#eea2ad"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lightpink3", "#cd8c95"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lightpink4", "#8b5f65"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lightsalmon", "#ffa07a"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lightsalmon1", "#ffa07a"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lightsalmon2", "#ee9572"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lightsalmon3", "#cd8162"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lightsalmon4", "#8b5742"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lightseagreen", "#20b2aa"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lightskyblue", "#87cefa"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lightskyblue1", "#b0e2ff"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lightskyblue2", "#a4d3ee"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lightskyblue3", "#8db6cd"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lightskyblue4", "#607b8b"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lightslateblue", "#8470ff"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lightslategray", "#778899"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lightslategrey", "#778899"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lightsteelblue", "#b0c4de"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lightsteelblue1", "#cae1ff"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lightsteelblue2", "#bcd2ee"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lightsteelblue3", "#a2b5cd"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lightsteelblue4", "#6e7b8b"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lightyellow", "#ffffe0"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lightyellow1", "#ffffe0"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lightyellow2", "#eeeed1"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lightyellow3", "#cdcdb4"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("lightyellow4", "#8b8b7a"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("limegreen", "#32cd32"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("linen", "#faf0e6"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("magenta", "#ff00ff"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("magenta1", "#ff00ff"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("magenta2", "#ee00ee"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("magenta3", "#cd00cd"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("magenta4", "#8b008b"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("maroon", "#b03060"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("maroon1", "#ff34b3"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("maroon2", "#ee30a7"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("maroon3", "#cd2990"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("maroon4", "#8b1c62"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("mediumaquamarine", "#66cdaa"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("mediumblue", "#0000cd"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("mediumorchid", "#ba55d3"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("mediumorchid1", "#e066ff"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("mediumorchid2", "#d15fee"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("mediumorchid3", "#b452cd"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("mediumorchid4", "#7a378b"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("mediumpurple", "#9370db"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("mediumpurple1", "#ab82ff"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("mediumpurple2", "#9f79ee"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("mediumpurple3", "#8968cd"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("mediumpurple4", "#5d478b"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("mediumseagreen", "#3cb371"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("mediumslateblue", "#7b68ee"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("mediumspringgreen", "#00fa9a"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("mediumturquoise", "#48d1cc"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("mediumvioletred", "#c71585"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("midnightblue", "#191970"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("mintcream", "#f5fffa"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("mistyrose", "#ffe4e1"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("mistyrose1", "#ffe4e1"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("mistyrose2", "#eed5d2"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("mistyrose3", "#cdb7b5"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("mistyrose4", "#8b7d7b"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("moccasin", "#ffe4b5"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("navajowhite", "#ffdead"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("navajowhite1", "#ffdead"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("navajowhite2", "#eecfa1"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("navajowhite3", "#cdb38b"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("navajowhite4", "#8b795e"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("navy", "#000080"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("navyblue", "#000080"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("none", "#fffffe"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("oldlace", "#fdf5e6"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("olivedrab", "#6b8e23"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("olivedrab1", "#c0ff3e"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("olivedrab2", "#b3ee3a"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("olivedrab3", "#9acd32"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("olivedrab4", "#698b22"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("orange", "#ffa500"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("orange1", "#ffa500"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("orange2", "#ee9a00"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("orange3", "#cd8500"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("orange4", "#8b5a00"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("orangered", "#ff4500"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("orangered1", "#ff4500"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("orangered2", "#ee4000"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("orangered3", "#cd3700"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("orangered4", "#8b2500"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("orchid", "#da70d6"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("orchid1", "#ff83fa"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("orchid2", "#ee7ae9"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("orchid3", "#cd69c9"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("orchid4", "#8b4789"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("palegoldenrod", "#eee8aa"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("palegreen", "#98fb98"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("palegreen1", "#9aff9a"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("palegreen2", "#90ee90"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("palegreen3", "#7ccd7c"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("palegreen4", "#548b54"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("paleturquoise", "#afeeee"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("paleturquoise1", "#bbffff"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("paleturquoise2", "#aeeeee"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("paleturquoise3", "#96cdcd"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("paleturquoise4", "#668b8b"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("palevioletred", "#db7093"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("palevioletred1", "#ff82ab"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("palevioletred2", "#ee799f"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("palevioletred3", "#cd6889"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("palevioletred4", "#8b475d"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("papayawhip", "#ffefd5"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("peachpuff", "#ffdab9"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("peachpuff1", "#ffdab9"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("peachpuff2", "#eecbad"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("peachpuff3", "#cdaf95"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("peachpuff4", "#8b7765"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("peru", "#cd853f"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("pink", "#ffc0cb"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("pink1", "#ffb5c5"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("pink2", "#eea9b8"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("pink3", "#cd919e"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("pink4", "#8b636c"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("plum", "#dda0dd"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("plum1", "#ffbbff"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("plum2", "#eeaeee"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("plum3", "#cd96cd"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("plum4", "#8b668b"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("powderblue", "#b0e0e6"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("purple", "#a020f0"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("purple1", "#9b30ff"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("purple2", "#912cee"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("purple3", "#7d26cd"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("purple4", "#551a8b"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("red", "#ff0000"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("red1", "#ff0000"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("red2", "#ee0000"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("red3", "#cd0000"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("red4", "#8b0000"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("rosybrown", "#bc8f8f"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("rosybrown1", "#ffc1c1"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("rosybrown2", "#eeb4b4"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("rosybrown3", "#cd9b9b"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("rosybrown4", "#8b6969"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("royalblue", "#4169e1"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("royalblue1", "#4876ff"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("royalblue2", "#436eee"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("royalblue3", "#3a5fcd"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("royalblue4", "#27408b"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("saddlebrown", "#8b4513"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("salmon", "#fa8072"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("salmon1", "#ff8c69"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("salmon2", "#ee8262"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("salmon3", "#cd7054"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("salmon4", "#8b4c39"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("sandybrown", "#f4a460"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("seagreen", "#2e8b57"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("seagreen1", "#54ff9f"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("seagreen2", "#4eee94"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("seagreen3", "#43cd80"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("seagreen4", "#2e8b57"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("seashell", "#fff5ee"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("seashell1", "#fff5ee"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("seashell2", "#eee5de"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("seashell3", "#cdc5bf"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("seashell4", "#8b8682"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("sienna", "#a0522d"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("sienna1", "#ff8247"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("sienna2", "#ee7942"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("sienna3", "#cd6839"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("sienna4", "#8b4726"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("skyblue", "#87ceeb"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("skyblue1", "#87ceff"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("skyblue2", "#7ec0ee"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("skyblue3", "#6ca6cd"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("skyblue4", "#4a708b"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("slateblue", "#6a5acd"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("slateblue1", "#836fff"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("slateblue2", "#7a67ee"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("slateblue3", "#6959cd"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("slateblue4", "#473c8b"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("slategray", "#708090"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("slategray1", "#c6e2ff"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("slategray2", "#b9d3ee"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("slategray3", "#9fb6cd"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("slategray4", "#6c7b8b"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("slategrey", "#708090"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("snow", "#fffafa"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("snow1", "#fffafa"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("snow2", "#eee9e9"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("snow3", "#cdc9c9"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("snow4", "#8b8989"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("springgreen", "#00ff7f"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("springgreen1", "#00ff7f"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("springgreen2", "#00ee76"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("springgreen3", "#00cd66"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("springgreen4", "#008b45"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("steelblue", "#4682b4"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("steelblue1", "#63b8ff"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("steelblue2", "#5cacee"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("steelblue3", "#4f94cd"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("steelblue4", "#36648b"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("tan", "#d2b48c"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("tan1", "#ffa54f"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("tan2", "#ee9a49"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("tan3", "#cd853f"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("tan4", "#8b5a2b"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("thistle", "#d8bfd8"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("thistle1", "#ffe1ff"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("thistle2", "#eed2ee"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("thistle3", "#cdb5cd"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("thistle4", "#8b7b8b"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("tomato", "#ff6347"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("tomato1", "#ff6347"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("tomato2", "#ee5c42"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("tomato3", "#cd4f39"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("tomato4", "#8b3626"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("transparent", "#fffffe"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("turquoise", "#40e0d0"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("turquoise1", "#00f5ff"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("turquoise2", "#00e5ee"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("turquoise3", "#00c5cd"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("turquoise4", "#00868b"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("violet", "#ee82ee"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("violetred", "#d02090"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("violetred1", "#ff3e96"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("violetred2", "#ee3a8c"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("violetred3", "#cd3278"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("violetred4", "#8b2252"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("wheat", "#f5deb3"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("wheat1", "#ffe7ba"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("wheat2", "#eed8ae"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("wheat3", "#cdba96"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("wheat4", "#8b7e66"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("white", "#ffffff"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("whitesmoke", "#f5f5f5"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("yellow", "#ffff00"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("yellow1", "#ffff00"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("yellow2", "#eeee00"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("yellow3", "#cdcd00"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("yellow4", "#8b8b00"); //$NON-NLS-1$ //$NON-NLS-2$
		x11ColorScheme.put("yellowgreen", "#9acd32"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private static Map<String, String> svgColorScheme = new LinkedHashMap<>();
	static {
		svgColorScheme.put("aliceblue", "#f0f8ff"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("antiquewhite", "#faebd7"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("aqua", "#00ffff");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("aquamarine", "#7fffd4"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("azure", "#f0ffff");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("beige", "#f5f5dc"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("bisque", "#ffe4c4");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("black", "#000000"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("blanchedalmond", "#ffebcd");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("blue", "#0000ff"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("blueviolet", "#8a2be2");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("brown", "#a52a2a"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("burlywood", "#deb887");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("cadetblue", "#5f9ea0"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("chartreuse", "#7fff00");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("chocolate", "#d2691e"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("coral", "#ff7f50");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("cornflowerblue", "#6495ed"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("cornsilk", "#fff8dc");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("crimson", "#dc143c"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("cyan", "#00ffff");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("darkblue", "#00008b"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("darkcyan", "#008b8b");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("darkgoldenrod", "#b8860b"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("darkgray", "#a9a9a9");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("darkgreen", "#006400"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("darkgrey", "#a9a9a9");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("darkkhaki", "#bdb76b"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("darkmagenta", "#8b008b");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("darkolivegreen", "#556b2f"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("darkorange", "#ff8c00");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("darkorchid", "#9932cc"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("darkred", "#8b0000");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("darksalmon", "#e9967a"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("darkseagreen", "#8fbc8f");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("darkslateblue", "#483d8b"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("darkslategray", "#2f4f4f");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("darkslategrey", "#2f4f4f"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("darkturquoise", "#00ced1");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("darkviolet", "#9400d3"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("deeppink", "#ff1493");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("deepskyblue", "#00bfff"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("dimgray", "#696969");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("dimgrey", "#696969"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("dodgerblue", "#1e90ff");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("firebrick", "#b22222"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("floralwhite", "#fffaf0");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("forestgreen", "#228b22"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("fuchsia", "#ff00ff");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("gainsboro", "#dcdcdc"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("ghostwhite", "#f8f8ff");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("gold", "#ffd700"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("goldenrod", "#daa520");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("gray", "#808080"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("grey", "#808080");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("green", "#008000"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("greenyellow", "#adff2f");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("honeydew", "#f0fff0"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("hotpink", "#ff69b4");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("indianred", "#cd5c5c"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("indigo", "#4b0082");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("ivory", "#fffff0"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("khaki", "#f0e68c");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("lavender", "#e6e6fa"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("lavenderblush", "#fff0f5");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("lawngreen", "#7cfc00"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("lemonchiffon", "#fffacd");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("lightblue", "#add8e6"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("lightcoral", "#f08080");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("lightcyan", "#e0ffff"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("lightgoldenrodyellow", "#fafad2");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("lightgray", "#d3d3d3"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("lightgreen", "#90ee90");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("lightgrey", "#d3d3d3"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("lightpink", "#ffb6c1");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("lightsalmon", "#ffa07a"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("lightseagreen", "#20b2aa");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("lightskyblue", "#87cefa"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("lightslategray", "#778899");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("lightslategrey", "#778899"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("lightsteelblue", "#b0c4de");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("lightyellow", "#ffffe0"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("lime", "#00ff00");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("limegreen", "#32cd32"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("linen", "#faf0e6");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("magenta", "#ff00ff"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("maroon", "#800000");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("mediumaquamarine", "#66cdaa"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("mediumblue", "#0000cd");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("mediumorchid", "#ba55d3"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("mediumpurple", "#9370db");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("mediumseagreen", "#3cb371"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("mediumslateblue", "#7b68ee");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("mediumspringgreen", "#00fa9a"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("mediumturquoise", "#48d1cc"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("mediumvioletred", "#c71585");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("midnightblue", "#191970"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("mintcream", "#f5fffa");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("mistyrose", "#ffe4e1"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("moccasin", "#ffe4b5");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("navajowhite", "#ffdead"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("navy", "#000080");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("oldlace", "#fdf5e6"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("olive", "#808000");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("olivedrab", "#6b8e23"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("orange", "#ffa500");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("orangered", "#ff4500"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("orchid", "#da70d6");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("palegoldenrod", "#eee8aa"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("palegreen", "#98fb98");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("paleturquoise", "#afeeee"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("palevioletred", "#db7093");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("papayawhip", "#ffefd5"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("peachpuff", "#ffdab9");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("peru", "#cd853f"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("pink", "#ffc0cb");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("plum", "#dda0dd"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("powderblue", "#b0e0e6");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("purple", "#800080"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("red", "#ff0000");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("rosybrown", "#bc8f8f"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("royalblue", "#4169e1");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("saddlebrown", "#8b4513"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("salmon", "#fa8072");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("sandybrown", "#f4a460"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("seagreen", "#2e8b57");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("seashell", "#fff5ee"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("sienna", "#a0522d");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("silver", "#c0c0c0"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("skyblue", "#87ceeb");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("slateblue", "#6a5acd"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("slategray", "#708090");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("slategrey", "#708090"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("snow", "#fffafa");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("springgreen", "#00ff7f"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("steelblue", "#4682b4");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("tan", "#d2b48c"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("teal", "#008080");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("thistle", "#d8bfd8"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("tomato", "#ff6347");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("turquoise", "#40e0d0"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("violet", "#ee82ee");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("wheat", "#f5deb3"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("white", "#ffffff");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("whitesmoke", "#f5f5f5"); //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("yellow", "#ffff00");  //$NON-NLS-1$ //$NON-NLS-2$
		svgColorScheme.put("yellowgreen", "#9acd32"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private static Map<String, String[]> brewerColorSchemes = new LinkedHashMap<>();
	static {
		brewerColorSchemes.put("accent3", new String[] { "#7fc97f", "#beaed4", "#fdc086" });
		brewerColorSchemes.put("accent4", new String[] { "#7fc97f", "#beaed4", "#fdc086", "#ffff99" });
		brewerColorSchemes.put("accent5",
				new String[] { "#7fc97f", "#beaed4", "#fdc086", "#ffff99", "#386cb0" });
		brewerColorSchemes.put("accent6",
				new String[] { "#7fc97f", "#beaed4", "#fdc086", "#ffff99", "#386cb0", "#f0027f" });
		brewerColorSchemes.put("accent7",
				new String[] { "#7fc97f", "#beaed4", "#fdc086", "#ffff99", "#386cb0", "#f0027f", "#bf5b17" });
		brewerColorSchemes.put("accent8", new String[] { "#7fc97f", "#beaed4", "#fdc086", "#ffff99",
				"#386cb0", "#f0027f", "#bf5b17", "#666666" });
		brewerColorSchemes.put("blues3", new String[] { "#deebf7", "#9ecae1", "#3182bd" });
		brewerColorSchemes.put("blues4", new String[] { "#eff3ff", "#bdd7e7", "#6baed6", "#2171b5" });
		brewerColorSchemes.put("blues5",
				new String[] { "#eff3ff", "#bdd7e7", "#6baed6", "#3182bd", "#08519c" });
		brewerColorSchemes.put("blues6",
				new String[] { "#eff3ff", "#c6dbef", "#9ecae1", "#6baed6", "#3182bd", "#08519c" });
		brewerColorSchemes.put("blues7",
				new String[] { "#eff3ff", "#c6dbef", "#9ecae1", "#6baed6", "#4292c6", "#2171b5", "#084594" });
		brewerColorSchemes.put("blues8", new String[] { "#f7fbff", "#deebf7", "#c6dbef", "#9ecae1", "#6baed6",
				"#4292c6", "#2171b5", "#084594" });
		brewerColorSchemes.put("blues9", new String[] { "#f7fbff", "#deebf7", "#c6dbef", "#9ecae1", "#6baed6",
				"#4292c6", "#2171b5", "#08519c", "#08306b" });
		brewerColorSchemes.put("brbg10", new String[] { "#543005", "#8c510a", "#bf812d", "#dfc27d", "#f6e8c3",
				"#c7eae5", "#80cdc1", "#35978f", "#01665e", "#003c30" });
		brewerColorSchemes.put("brbg11", new String[] { "#543005", "#8c510a", "#bf812d", "#dfc27d", "#f6e8c3",
				"#f5f5f5", "#c7eae5", "#80cdc1", "#35978f", "#01665e", "#003c30" });
		brewerColorSchemes.put("brbg3", new String[] { "#d8b365", "#f5f5f5", "#5ab4ac" });
		brewerColorSchemes.put("brbg4", new String[] { "#a6611a", "#dfc27d", "#80cdc1", "#018571" });
		brewerColorSchemes.put("brbg5",
				new String[] { "#a6611a", "#dfc27d", "#f5f5f5", "#80cdc1", "#018571" });
		brewerColorSchemes.put("brbg6",
				new String[] { "#8c510a", "#d8b365", "#f6e8c3", "#c7eae5", "#5ab4ac", "#01665e" });
		brewerColorSchemes.put("brbg7",
				new String[] { "#8c510a", "#d8b365", "#f6e8c3", "#f5f5f5", "#c7eae5", "#5ab4ac", "#01665e" });
		brewerColorSchemes.put("brbg8", new String[] { "#8c510a", "#bf812d", "#dfc27d", "#f6e8c3", "#c7eae5",
				"#80cdc1", "#35978f", "#01665e" });
		brewerColorSchemes.put("brbg9", new String[] { "#8c510a", "#bf812d", "#dfc27d", "#f6e8c3", "#f5f5f5",
				"#c7eae5", "#80cdc1", "#35978f", "#01665e" });
		brewerColorSchemes.put("bugn3", new String[] { "#e5f5f9", "#99d8c9", "#2ca25f" });
		brewerColorSchemes.put("bugn4", new String[] { "#edf8fb", "#b2e2e2", "#66c2a4", "#238b45" });
		brewerColorSchemes.put("bugn5",
				new String[] { "#edf8fb", "#b2e2e2", "#66c2a4", "#2ca25f", "#006d2c" });
		brewerColorSchemes.put("bugn6",
				new String[] { "#edf8fb", "#ccece6", "#99d8c9", "#66c2a4", "#2ca25f", "#006d2c" });
		brewerColorSchemes.put("bugn7",
				new String[] { "#edf8fb", "#ccece6", "#99d8c9", "#66c2a4", "#41ae76", "#238b45", "#005824" });
		brewerColorSchemes.put("bugn8", new String[] { "#f7fcfd", "#e5f5f9", "#ccece6", "#99d8c9", "#66c2a4",
				"#41ae76", "#238b45", "#005824" });
		brewerColorSchemes.put("bugn9", new String[] { "#f7fcfd", "#e5f5f9", "#ccece6", "#99d8c9", "#66c2a4",
				"#41ae76", "#238b45", "#006d2c", "#00441b" });
		brewerColorSchemes.put("bupu3", new String[] { "#e0ecf4", "#9ebcda", "#8856a7" });
		brewerColorSchemes.put("bupu4", new String[] { "#edf8fb", "#b3cde3", "#8c96c6", "#88419d" });
		brewerColorSchemes.put("bupu5",
				new String[] { "#edf8fb", "#b3cde3", "#8c96c6", "#8856a7", "#810f7c" });
		brewerColorSchemes.put("bupu6",
				new String[] { "#edf8fb", "#bfd3e6", "#9ebcda", "#8c96c6", "#8856a7", "#810f7c" });
		brewerColorSchemes.put("bupu7",
				new String[] { "#edf8fb", "#bfd3e6", "#9ebcda", "#8c96c6", "#8c6bb1", "#88419d", "#6e016b" });
		brewerColorSchemes.put("bupu8", new String[] { "#f7fcfd", "#e0ecf4", "#bfd3e6", "#9ebcda", "#8c96c6",
				"#8c6bb1", "#88419d", "#6e016b" });
		brewerColorSchemes.put("bupu9", new String[] { "#f7fcfd", "#e0ecf4", "#bfd3e6", "#9ebcda", "#8c96c6",
				"#8c6bb1", "#88419d", "#810f7c", "#4d004b" });
		brewerColorSchemes.put("dark23", new String[] { "#1b9e77", "#d95f02", "#7570b3" });
		brewerColorSchemes.put("dark24", new String[] { "#1b9e77", "#d95f02", "#7570b3", "#e7298a" });
		brewerColorSchemes.put("dark25",
				new String[] { "#1b9e77", "#d95f02", "#7570b3", "#e7298a", "#66a61e" });
		brewerColorSchemes.put("dark26",
				new String[] { "#1b9e77", "#d95f02", "#7570b3", "#e7298a", "#66a61e", "#e6ab02" });
		brewerColorSchemes.put("dark27",
				new String[] { "#1b9e77", "#d95f02", "#7570b3", "#e7298a", "#66a61e", "#e6ab02", "#a6761d" });
		brewerColorSchemes.put("dark28", new String[] { "#1b9e77", "#d95f02", "#7570b3", "#e7298a", "#66a61e",
				"#e6ab02", "#a6761d", "#666666" });
		brewerColorSchemes.put("gnbu3", new String[] { "#e0f3db", "#a8ddb5", "#43a2ca" });
		brewerColorSchemes.put("gnbu4", new String[] { "#f0f9e8", "#bae4bc", "#7bccc4", "#2b8cbe" });
		brewerColorSchemes.put("gnbu5",
				new String[] { "#f0f9e8", "#bae4bc", "#7bccc4", "#43a2ca", "#0868ac" });
		brewerColorSchemes.put("gnbu6",
				new String[] { "#f0f9e8", "#ccebc5", "#a8ddb5", "#7bccc4", "#43a2ca", "#0868ac" });
		brewerColorSchemes.put("gnbu7",
				new String[] { "#f0f9e8", "#ccebc5", "#a8ddb5", "#7bccc4", "#4eb3d3", "#2b8cbe", "#08589e" });
		brewerColorSchemes.put("gnbu8", new String[] { "#f7fcf0", "#e0f3db", "#ccebc5", "#a8ddb5", "#7bccc4",
				"#4eb3d3", "#2b8cbe", "#08589e" });
		brewerColorSchemes.put("gnbu9", new String[] { "#f7fcf0", "#e0f3db", "#ccebc5", "#a8ddb5", "#7bccc4",
				"#4eb3d3", "#2b8cbe", "#0868ac", "#084081" });
		brewerColorSchemes.put("greens3", new String[] { "#e5f5e0", "#a1d99b", "#31a354" });
		brewerColorSchemes.put("greens4", new String[] { "#edf8e9", "#bae4b3", "#74c476", "#238b45" });
		brewerColorSchemes.put("greens5",
				new String[] { "#edf8e9", "#bae4b3", "#74c476", "#31a354", "#006d2c" });
		brewerColorSchemes.put("greens6",
				new String[] { "#edf8e9", "#c7e9c0", "#a1d99b", "#74c476", "#31a354", "#006d2c" });
		brewerColorSchemes.put("greens7",
				new String[] { "#edf8e9", "#c7e9c0", "#a1d99b", "#74c476", "#41ab5d", "#238b45", "#005a32" });
		brewerColorSchemes.put("greens8", new String[] { "#f7fcf5", "#e5f5e0", "#c7e9c0", "#a1d99b",
				"#74c476", "#41ab5d", "#238b45", "#005a32" });
		brewerColorSchemes.put("greens9", new String[] { "#f7fcf5", "#e5f5e0", "#c7e9c0", "#a1d99b",
				"#74c476", "#41ab5d", "#238b45", "#006d2c", "#00441b" });
		brewerColorSchemes.put("greys3", new String[] { "#f0f0f0", "#bdbdbd", "#636363" });
		brewerColorSchemes.put("greys4", new String[] { "#f7f7f7", "#cccccc", "#969696", "#525252" });
		brewerColorSchemes.put("greys5",
				new String[] { "#f7f7f7", "#cccccc", "#969696", "#636363", "#252525" });
		brewerColorSchemes.put("greys6",
				new String[] { "#f7f7f7", "#d9d9d9", "#bdbdbd", "#969696", "#636363", "#252525" });
		brewerColorSchemes.put("greys7",
				new String[] { "#f7f7f7", "#d9d9d9", "#bdbdbd", "#969696", "#737373", "#525252", "#252525" });
		brewerColorSchemes.put("greys8", new String[] { "#ffffff", "#f0f0f0", "#d9d9d9", "#bdbdbd", "#969696",
				"#737373", "#525252", "#252525" });
		brewerColorSchemes.put("greys9", new String[] { "#ffffff", "#f0f0f0", "#d9d9d9", "#bdbdbd", "#969696",
				"#737373", "#525252", "#252525", "#000000" });
		brewerColorSchemes.put("oranges3", new String[] { "#fee6ce", "#fdae6b", "#e6550d" });
		brewerColorSchemes.put("oranges4", new String[] { "#feedde", "#fdbe85", "#fd8d3c", "#d94701" });
		brewerColorSchemes.put("oranges5",
				new String[] { "#feedde", "#fdbe85", "#fd8d3c", "#e6550d", "#a63603" });
		brewerColorSchemes.put("oranges6",
				new String[] { "#feedde", "#fdd0a2", "#fdae6b", "#fd8d3c", "#e6550d", "#a63603" });
		brewerColorSchemes.put("oranges7",
				new String[] { "#feedde", "#fdd0a2", "#fdae6b", "#fd8d3c", "#f16913", "#d94801", "#8c2d04" });
		brewerColorSchemes.put("oranges8", new String[] { "#fff5eb", "#fee6ce", "#fdd0a2", "#fdae6b",
				"#fd8d3c", "#f16913", "#d94801", "#8c2d04" });
		brewerColorSchemes.put("oranges9", new String[] { "#fff5eb", "#fee6ce", "#fdd0a2", "#fdae6b",
				"#fd8d3c", "#f16913", "#d94801", "#a63603", "#7f2704" });
		brewerColorSchemes.put("orrd3", new String[] { "#fee8c8", "#fdbb84", "#e34a33" });
		brewerColorSchemes.put("orrd4", new String[] { "#fef0d9", "#fdcc8a", "#fc8d59", "#d7301f" });
		brewerColorSchemes.put("orrd5",
				new String[] { "#fef0d9", "#fdcc8a", "#fc8d59", "#e34a33", "#b30000" });
		brewerColorSchemes.put("orrd6",
				new String[] { "#fef0d9", "#fdd49e", "#fdbb84", "#fc8d59", "#e34a33", "#b30000" });
		brewerColorSchemes.put("orrd7",
				new String[] { "#fef0d9", "#fdd49e", "#fdbb84", "#fc8d59", "#ef6548", "#d7301f", "#990000" });
		brewerColorSchemes.put("orrd8", new String[] { "#fff7ec", "#fee8c8", "#fdd49e", "#fdbb84", "#fc8d59",
				"#ef6548", "#d7301f", "#990000" });
		brewerColorSchemes.put("orrd9", new String[] { "#fff7ec", "#fee8c8", "#fdd49e", "#fdbb84", "#fc8d59",
				"#ef6548", "#d7301f", "#b30000", "#7f0000" });
		brewerColorSchemes.put("paired10", new String[] { "#a6cee3", "#1f78b4", "#b2df8a", "#33a02c",
				"#fb9a99", "#e31a1c", "#fdbf6f", "#ff7f00", "#cab2d6", "#6a3d9a" });
		brewerColorSchemes.put("paired11", new String[] { "#a6cee3", "#1f78b4", "#b2df8a", "#33a02c",
				"#fb9a99", "#e31a1c", "#fdbf6f", "#ff7f00", "#cab2d6", "#6a3d9a", "#ffff99" });
		brewerColorSchemes.put("paired12", new String[] { "#a6cee3", "#1f78b4", "#b2df8a", "#33a02c",
				"#fb9a99", "#e31a1c", "#fdbf6f", "#ff7f00", "#cab2d6", "#6a3d9a", "#ffff99", "#b15928" });
		brewerColorSchemes.put("paired3", new String[] { "#a6cee3", "#1f78b4", "#b2df8a" });
		brewerColorSchemes.put("paired4", new String[] { "#a6cee3", "#1f78b4", "#b2df8a", "#33a02c" });
		brewerColorSchemes.put("paired5",
				new String[] { "#a6cee3", "#1f78b4", "#b2df8a", "#33a02c", "#fb9a99" });
		brewerColorSchemes.put("paired6",
				new String[] { "#a6cee3", "#1f78b4", "#b2df8a", "#33a02c", "#fb9a99", "#e31a1c" });
		brewerColorSchemes.put("paired7",
				new String[] { "#a6cee3", "#1f78b4", "#b2df8a", "#33a02c", "#fb9a99", "#e31a1c", "#fdbf6f" });
		brewerColorSchemes.put("paired8", new String[] { "#a6cee3", "#1f78b4", "#b2df8a", "#33a02c",
				"#fb9a99", "#e31a1c", "#fdbf6f", "#ff7f00" });
		brewerColorSchemes.put("paired9", new String[] { "#a6cee3", "#1f78b4", "#b2df8a", "#33a02c",
				"#fb9a99", "#e31a1c", "#fdbf6f", "#ff7f00", "#cab2d6" });
		brewerColorSchemes.put("pastel13", new String[] { "#fbb4ae", "#b3cde3", "#ccebc5" });
		brewerColorSchemes.put("pastel14", new String[] { "#fbb4ae", "#b3cde3", "#ccebc5", "#decbe4" });
		brewerColorSchemes.put("pastel15",
				new String[] { "#fbb4ae", "#b3cde3", "#ccebc5", "#decbe4", "#fed9a6" });
		brewerColorSchemes.put("pastel16",
				new String[] { "#fbb4ae", "#b3cde3", "#ccebc5", "#decbe4", "#fed9a6", "#ffffcc" });
		brewerColorSchemes.put("pastel17",
				new String[] { "#fbb4ae", "#b3cde3", "#ccebc5", "#decbe4", "#fed9a6", "#ffffcc", "#e5d8bd" });
		brewerColorSchemes.put("pastel18", new String[] { "#fbb4ae", "#b3cde3", "#ccebc5", "#decbe4",
				"#fed9a6", "#ffffcc", "#e5d8bd", "#fddaec" });
		brewerColorSchemes.put("pastel19", new String[] { "#fbb4ae", "#b3cde3", "#ccebc5", "#decbe4",
				"#fed9a6", "#ffffcc", "#e5d8bd", "#fddaec", "#f2f2f2" });
		brewerColorSchemes.put("pastel23", new String[] { "#b3e2cd", "#fdcdac", "#cbd5e8" });
		brewerColorSchemes.put("pastel24", new String[] { "#b3e2cd", "#fdcdac", "#cbd5e8", "#f4cae4" });
		brewerColorSchemes.put("pastel25",
				new String[] { "#b3e2cd", "#fdcdac", "#cbd5e8", "#f4cae4", "#e6f5c9" });
		brewerColorSchemes.put("pastel26",
				new String[] { "#b3e2cd", "#fdcdac", "#cbd5e8", "#f4cae4", "#e6f5c9", "#fff2ae" });
		brewerColorSchemes.put("pastel27",
				new String[] { "#b3e2cd", "#fdcdac", "#cbd5e8", "#f4cae4", "#e6f5c9", "#fff2ae", "#f1e2cc" });
		brewerColorSchemes.put("pastel28", new String[] { "#b3e2cd", "#fdcdac", "#cbd5e8", "#f4cae4",
				"#e6f5c9", "#fff2ae", "#f1e2cc", "#cccccc" });
		brewerColorSchemes.put("piyg10", new String[] { "#8e0152", "#c51b7d", "#de77ae", "#f1b6da", "#fde0ef",
				"#e6f5d0", "#b8e186", "#7fbc41", "#4d9221", "#276419" });
		brewerColorSchemes.put("piyg11", new String[] { "#8e0152", "#c51b7d", "#de77ae", "#f1b6da", "#fde0ef",
				"#f7f7f7", "#e6f5d0", "#b8e186", "#7fbc41", "#4d9221", "#276419" });
		brewerColorSchemes.put("piyg3", new String[] { "#e9a3c9", "#f7f7f7", "#a1d76a" });
		brewerColorSchemes.put("piyg4", new String[] { "#d01c8b", "#f1b6da", "#b8e186", "#4dac26" });
		brewerColorSchemes.put("piyg5",
				new String[] { "#d01c8b", "#f1b6da", "#f7f7f7", "#b8e186", "#4dac26" });
		brewerColorSchemes.put("piyg6",
				new String[] { "#c51b7d", "#e9a3c9", "#fde0ef", "#e6f5d0", "#a1d76a", "#4d9221" });
		brewerColorSchemes.put("piyg7",
				new String[] { "#c51b7d", "#e9a3c9", "#fde0ef", "#f7f7f7", "#e6f5d0", "#a1d76a", "#4d9221" });
		brewerColorSchemes.put("piyg8", new String[] { "#c51b7d", "#de77ae", "#f1b6da", "#fde0ef", "#e6f5d0",
				"#b8e186", "#7fbc41", "#4d9221" });
		brewerColorSchemes.put("piyg9", new String[] { "#c51b7d", "#de77ae", "#f1b6da", "#fde0ef", "#f7f7f7",
				"#e6f5d0", "#b8e186", "#7fbc41", "#4d9221" });
		brewerColorSchemes.put("prgn10", new String[] { "#40004b", "#762a83", "#9970ab", "#c2a5cf", "#e7d4e8",
				"#d9f0d3", "#a6dba0", "#5aae61", "#1b7837", "#00441b" });
		brewerColorSchemes.put("prgn11", new String[] { "#40004b", "#762a83", "#9970ab", "#c2a5cf", "#e7d4e8",
				"#f7f7f7", "#d9f0d3", "#a6dba0", "#5aae61", "#1b7837", "#00441b" });
		brewerColorSchemes.put("prgn3", new String[] { "#af8dc3", "#f7f7f7", "#7fbf7b" });
		brewerColorSchemes.put("prgn4", new String[] { "#7b3294", "#c2a5cf", "#a6dba0", "#008837" });
		brewerColorSchemes.put("prgn5",
				new String[] { "#7b3294", "#c2a5cf", "#f7f7f7", "#a6dba0", "#008837" });
		brewerColorSchemes.put("prgn6",
				new String[] { "#762a83", "#af8dc3", "#e7d4e8", "#d9f0d3", "#7fbf7b", "#1b7837" });
		brewerColorSchemes.put("prgn7",
				new String[] { "#762a83", "#af8dc3", "#e7d4e8", "#f7f7f7", "#d9f0d3", "#7fbf7b", "#1b7837" });
		brewerColorSchemes.put("prgn8", new String[] { "#762a83", "#9970ab", "#c2a5cf", "#e7d4e8", "#d9f0d3",
				"#a6dba0", "#5aae61", "#1b7837" });
		brewerColorSchemes.put("prgn9", new String[] { "#762a83", "#9970ab", "#c2a5cf", "#e7d4e8", "#f7f7f7",
				"#d9f0d3", "#a6dba0", "#5aae61", "#1b7837" });
		brewerColorSchemes.put("pubu3", new String[] { "#ece7f2", "#a6bddb", "#2b8cbe" });
		brewerColorSchemes.put("pubu4", new String[] { "#f1eef6", "#bdc9e1", "#74a9cf", "#0570b0" });
		brewerColorSchemes.put("pubu5",
				new String[] { "#f1eef6", "#bdc9e1", "#74a9cf", "#2b8cbe", "#045a8d" });
		brewerColorSchemes.put("pubu6",
				new String[] { "#f1eef6", "#d0d1e6", "#a6bddb", "#74a9cf", "#2b8cbe", "#045a8d" });
		brewerColorSchemes.put("pubu7",
				new String[] { "#f1eef6", "#d0d1e6", "#a6bddb", "#74a9cf", "#3690c0", "#0570b0", "#034e7b" });
		brewerColorSchemes.put("pubu8", new String[] { "#fff7fb", "#ece7f2", "#d0d1e6", "#a6bddb", "#74a9cf",
				"#3690c0", "#0570b0", "#034e7b" });
		brewerColorSchemes.put("pubu9", new String[] { "#fff7fb", "#ece7f2", "#d0d1e6", "#a6bddb", "#74a9cf",
				"#3690c0", "#0570b0", "#045a8d", "#023858" });
		brewerColorSchemes.put("pubugn3", new String[] { "#ece2f0", "#a6bddb", "#1c9099" });
		brewerColorSchemes.put("pubugn4", new String[] { "#f6eff7", "#bdc9e1", "#67a9cf", "#02818a" });
		brewerColorSchemes.put("pubugn5",
				new String[] { "#f6eff7", "#bdc9e1", "#67a9cf", "#1c9099", "#016c59" });
		brewerColorSchemes.put("pubugn6",
				new String[] { "#f6eff7", "#d0d1e6", "#a6bddb", "#67a9cf", "#1c9099", "#016c59" });
		brewerColorSchemes.put("pubugn7",
				new String[] { "#f6eff7", "#d0d1e6", "#a6bddb", "#67a9cf", "#3690c0", "#02818a", "#016450" });
		brewerColorSchemes.put("pubugn8", new String[] { "#fff7fb", "#ece2f0", "#d0d1e6", "#a6bddb",
				"#67a9cf", "#3690c0", "#02818a", "#016450" });
		brewerColorSchemes.put("pubugn9", new String[] { "#fff7fb", "#ece2f0", "#d0d1e6", "#a6bddb",
				"#67a9cf", "#3690c0", "#02818a", "#016c59", "#014636" });
		brewerColorSchemes.put("puor10", new String[] { "#7f3b08", "#b35806", "#e08214", "#fdb863", "#fee0b6",
				"#d8daeb", "#b2abd2", "#8073ac", "#542788", "#2d004b" });
		brewerColorSchemes.put("puor11", new String[] { "#7f3b08", "#b35806", "#e08214", "#fdb863", "#fee0b6",
				"#f7f7f7", "#d8daeb", "#b2abd2", "#8073ac", "#542788", "#2d004b" });
		brewerColorSchemes.put("puor3", new String[] { "#f1a340", "#f7f7f7", "#998ec3" });
		brewerColorSchemes.put("puor4", new String[] { "#e66101", "#fdb863", "#b2abd2", "#5e3c99" });
		brewerColorSchemes.put("puor5",
				new String[] { "#e66101", "#fdb863", "#f7f7f7", "#b2abd2", "#5e3c99" });
		brewerColorSchemes.put("puor6",
				new String[] { "#b35806", "#f1a340", "#fee0b6", "#d8daeb", "#998ec3", "#542788" });
		brewerColorSchemes.put("puor7",
				new String[] { "#b35806", "#f1a340", "#fee0b6", "#f7f7f7", "#d8daeb", "#998ec3", "#542788" });
		brewerColorSchemes.put("puor8", new String[] { "#b35806", "#e08214", "#fdb863", "#fee0b6", "#d8daeb",
				"#b2abd2", "#8073ac", "#542788" });
		brewerColorSchemes.put("puor9", new String[] { "#b35806", "#e08214", "#fdb863", "#fee0b6", "#f7f7f7",
				"#d8daeb", "#b2abd2", "#8073ac", "#542788" });
		brewerColorSchemes.put("purd3", new String[] { "#e7e1ef", "#c994c7", "#dd1c77" });
		brewerColorSchemes.put("purd4", new String[] { "#f1eef6", "#d7b5d8", "#df65b0", "#ce1256" });
		brewerColorSchemes.put("purd5",
				new String[] { "#f1eef6", "#d7b5d8", "#df65b0", "#dd1c77", "#980043" });
		brewerColorSchemes.put("purd6",
				new String[] { "#f1eef6", "#d4b9da", "#c994c7", "#df65b0", "#dd1c77", "#980043" });
		brewerColorSchemes.put("purd7",
				new String[] { "#f1eef6", "#d4b9da", "#c994c7", "#df65b0", "#e7298a", "#ce1256", "#91003f" });
		brewerColorSchemes.put("purd8", new String[] { "#f7f4f9", "#e7e1ef", "#d4b9da", "#c994c7", "#df65b0",
				"#e7298a", "#ce1256", "#91003f" });
		brewerColorSchemes.put("purd9", new String[] { "#f7f4f9", "#e7e1ef", "#d4b9da", "#c994c7", "#df65b0",
				"#e7298a", "#ce1256", "#980043", "#67001f" });
		brewerColorSchemes.put("purples3", new String[] { "#efedf5", "#bcbddc", "#756bb1" });
		brewerColorSchemes.put("purples4", new String[] { "#f2f0f7", "#cbc9e2", "#9e9ac8", "#6a51a3" });
		brewerColorSchemes.put("purples5",
				new String[] { "#f2f0f7", "#cbc9e2", "#9e9ac8", "#756bb1", "#54278f" });
		brewerColorSchemes.put("purples6",
				new String[] { "#f2f0f7", "#dadaeb", "#bcbddc", "#9e9ac8", "#756bb1", "#54278f" });
		brewerColorSchemes.put("purples7",
				new String[] { "#f2f0f7", "#dadaeb", "#bcbddc", "#9e9ac8", "#807dba", "#6a51a3", "#4a1486" });
		brewerColorSchemes.put("purples8", new String[] { "#fcfbfd", "#efedf5", "#dadaeb", "#bcbddc",
				"#9e9ac8", "#807dba", "#6a51a3", "#4a1486" });
		brewerColorSchemes.put("purples9", new String[] { "#fcfbfd", "#efedf5", "#dadaeb", "#bcbddc",
				"#9e9ac8", "#807dba", "#6a51a3", "#54278f", "#3f007d" });
		brewerColorSchemes.put("rdbu10", new String[] { "#67001f", "#b2182b", "#d6604d", "#f4a582", "#fddbc7",
				"#d1e5f0", "#92c5de", "#4393c3", "#2166ac", "#053061" });
		brewerColorSchemes.put("rdbu11", new String[] { "#67001f", "#b2182b", "#d6604d", "#f4a582", "#fddbc7",
				"#f7f7f7", "#d1e5f0", "#92c5de", "#4393c3", "#2166ac", "#053061" });
		brewerColorSchemes.put("rdbu3", new String[] { "#ef8a62", "#f7f7f7", "#67a9cf" });
		brewerColorSchemes.put("rdbu4", new String[] { "#ca0020", "#f4a582", "#92c5de", "#0571b0" });
		brewerColorSchemes.put("rdbu5",
				new String[] { "#ca0020", "#f4a582", "#f7f7f7", "#92c5de", "#0571b0" });
		brewerColorSchemes.put("rdbu6",
				new String[] { "#b2182b", "#ef8a62", "#fddbc7", "#d1e5f0", "#67a9cf", "#2166ac" });
		brewerColorSchemes.put("rdbu7",
				new String[] { "#b2182b", "#ef8a62", "#fddbc7", "#f7f7f7", "#d1e5f0", "#67a9cf", "#2166ac" });
		brewerColorSchemes.put("rdbu8", new String[] { "#b2182b", "#d6604d", "#f4a582", "#fddbc7", "#d1e5f0",
				"#92c5de", "#4393c3", "#2166ac" });
		brewerColorSchemes.put("rdbu9", new String[] { "#b2182b", "#d6604d", "#f4a582", "#fddbc7", "#f7f7f7",
				"#d1e5f0", "#92c5de", "#4393c3", "#2166ac" });
		brewerColorSchemes.put("rdgy10", new String[] { "#67001f", "#b2182b", "#d6604d", "#f4a582", "#fddbc7",
				"#e0e0e0", "#bababa", "#878787", "#4d4d4d", "#1a1a1a" });
		brewerColorSchemes.put("rdgy11", new String[] { "#67001f", "#b2182b", "#d6604d", "#f4a582", "#fddbc7",
				"#ffffff", "#e0e0e0", "#bababa", "#878787", "#4d4d4d", "#1a1a1a" });
		brewerColorSchemes.put("rdgy3", new String[] { "#ef8a62", "#ffffff", "#999999" });
		brewerColorSchemes.put("rdgy4", new String[] { "#ca0020", "#f4a582", "#bababa", "#404040" });
		brewerColorSchemes.put("rdgy5",
				new String[] { "#ca0020", "#f4a582", "#ffffff", "#bababa", "#404040" });
		brewerColorSchemes.put("rdgy6",
				new String[] { "#b2182b", "#ef8a62", "#fddbc7", "#e0e0e0", "#999999", "#4d4d4d" });
		brewerColorSchemes.put("rdgy7",
				new String[] { "#b2182b", "#ef8a62", "#fddbc7", "#ffffff", "#e0e0e0", "#999999", "#4d4d4d" });
		brewerColorSchemes.put("rdgy8", new String[] { "#b2182b", "#d6604d", "#f4a582", "#fddbc7", "#e0e0e0",
				"#bababa", "#878787", "#4d4d4d" });
		brewerColorSchemes.put("rdgy9", new String[] { "#b2182b", "#d6604d", "#f4a582", "#fddbc7", "#ffffff",
				"#e0e0e0", "#bababa", "#878787", "#4d4d4d" });
		brewerColorSchemes.put("rdpu3", new String[] { "#fde0dd", "#fa9fb5", "#c51b8a" });
		brewerColorSchemes.put("rdpu4", new String[] { "#feebe2", "#fbb4b9", "#f768a1", "#ae017e" });
		brewerColorSchemes.put("rdpu5",
				new String[] { "#feebe2", "#fbb4b9", "#f768a1", "#c51b8a", "#7a0177" });
		brewerColorSchemes.put("rdpu6",
				new String[] { "#feebe2", "#fcc5c0", "#fa9fb5", "#f768a1", "#c51b8a", "#7a0177" });
		brewerColorSchemes.put("rdpu7",
				new String[] { "#feebe2", "#fcc5c0", "#fa9fb5", "#f768a1", "#dd3497", "#ae017e", "#7a0177" });
		brewerColorSchemes.put("rdpu8", new String[] { "#fff7f3", "#fde0dd", "#fcc5c0", "#fa9fb5", "#f768a1",
				"#dd3497", "#ae017e", "#7a0177" });
		brewerColorSchemes.put("rdpu9", new String[] { "#fff7f3", "#fde0dd", "#fcc5c0", "#fa9fb5", "#f768a1",
				"#dd3497", "#ae017e", "#7a0177", "#49006a" });
		brewerColorSchemes.put("rdylbu10", new String[] { "#a50026", "#d73027", "#f46d43", "#fdae61",
				"#fee090", "#e0f3f8", "#abd9e9", "#74add1", "#4575b4", "#313695" });
		brewerColorSchemes.put("rdylbu11", new String[] { "#a50026", "#d73027", "#f46d43", "#fdae61",
				"#fee090", "#ffffbf", "#e0f3f8", "#abd9e9", "#74add1", "#4575b4", "#313695" });
		brewerColorSchemes.put("rdylbu3", new String[] { "#fc8d59", "#ffffbf", "#91bfdb" });
		brewerColorSchemes.put("rdylbu4", new String[] { "#d7191c", "#fdae61", "#abd9e9", "#2c7bb6" });
		brewerColorSchemes.put("rdylbu5",
				new String[] { "#d7191c", "#fdae61", "#ffffbf", "#abd9e9", "#2c7bb6" });
		brewerColorSchemes.put("rdylbu6",
				new String[] { "#d73027", "#fc8d59", "#fee090", "#e0f3f8", "#91bfdb", "#4575b4" });
		brewerColorSchemes.put("rdylbu7",
				new String[] { "#d73027", "#fc8d59", "#fee090", "#ffffbf", "#e0f3f8", "#91bfdb", "#4575b4" });
		brewerColorSchemes.put("rdylbu8", new String[] { "#d73027", "#f46d43", "#fdae61", "#fee090",
				"#e0f3f8", "#abd9e9", "#74add1", "#4575b4" });
		brewerColorSchemes.put("rdylbu9", new String[] { "#d73027", "#f46d43", "#fdae61", "#fee090",
				"#ffffbf", "#e0f3f8", "#abd9e9", "#74add1", "#4575b4" });
		brewerColorSchemes.put("rdylgn10", new String[] { "#a50026", "#d73027", "#f46d43", "#fdae61",
				"#fee08b", "#d9ef8b", "#a6d96a", "#66bd63", "#1a9850", "#006837" });
		brewerColorSchemes.put("rdylgn11", new String[] { "#a50026", "#d73027", "#f46d43", "#fdae61",
				"#fee08b", "#ffffbf", "#d9ef8b", "#a6d96a", "#66bd63", "#1a9850", "#006837" });
		brewerColorSchemes.put("rdylgn3", new String[] { "#fc8d59", "#ffffbf", "#91cf60" });
		brewerColorSchemes.put("rdylgn4", new String[] { "#d7191c", "#fdae61", "#a6d96a", "#1a9641" });
		brewerColorSchemes.put("rdylgn5",
				new String[] { "#d7191c", "#fdae61", "#ffffbf", "#a6d96a", "#1a9641" });
		brewerColorSchemes.put("rdylgn6",
				new String[] { "#d73027", "#fc8d59", "#fee08b", "#d9ef8b", "#91cf60", "#1a9850" });
		brewerColorSchemes.put("rdylgn7",
				new String[] { "#d73027", "#fc8d59", "#fee08b", "#ffffbf", "#d9ef8b", "#91cf60", "#1a9850" });
		brewerColorSchemes.put("rdylgn8", new String[] { "#d73027", "#f46d43", "#fdae61", "#fee08b",
				"#d9ef8b", "#a6d96a", "#66bd63", "#1a9850" });
		brewerColorSchemes.put("rdylgn9", new String[] { "#d73027", "#f46d43", "#fdae61", "#fee08b",
				"#ffffbf", "#d9ef8b", "#a6d96a", "#66bd63", "#1a9850" });
		brewerColorSchemes.put("reds3", new String[] { "#fee0d2", "#fc9272", "#de2d26" });
		brewerColorSchemes.put("reds4", new String[] { "#fee5d9", "#fcae91", "#fb6a4a", "#cb181d" });
		brewerColorSchemes.put("reds5",
				new String[] { "#fee5d9", "#fcae91", "#fb6a4a", "#de2d26", "#a50f15" });
		brewerColorSchemes.put("reds6",
				new String[] { "#fee5d9", "#fcbba1", "#fc9272", "#fb6a4a", "#de2d26", "#a50f15" });
		brewerColorSchemes.put("reds7",
				new String[] { "#fee5d9", "#fcbba1", "#fc9272", "#fb6a4a", "#ef3b2c", "#cb181d", "#99000d" });
		brewerColorSchemes.put("reds8", new String[] { "#fff5f0", "#fee0d2", "#fcbba1", "#fc9272", "#fb6a4a",
				"#ef3b2c", "#cb181d", "#99000d" });
		brewerColorSchemes.put("reds9", new String[] { "#fff5f0", "#fee0d2", "#fcbba1", "#fc9272", "#fb6a4a",
				"#ef3b2c", "#cb181d", "#a50f15", "#67000d" });
		brewerColorSchemes.put("set13", new String[] { "#e41a1c", "#377eb8", "#4daf4a" });
		brewerColorSchemes.put("set14", new String[] { "#e41a1c", "#377eb8", "#4daf4a", "#984ea3" });
		brewerColorSchemes.put("set15",
				new String[] { "#e41a1c", "#377eb8", "#4daf4a", "#984ea3", "#ff7f00" });
		brewerColorSchemes.put("set16",
				new String[] { "#e41a1c", "#377eb8", "#4daf4a", "#984ea3", "#ff7f00", "#ffff33" });
		brewerColorSchemes.put("set17",
				new String[] { "#e41a1c", "#377eb8", "#4daf4a", "#984ea3", "#ff7f00", "#ffff33", "#a65628" });
		brewerColorSchemes.put("set18", new String[] { "#e41a1c", "#377eb8", "#4daf4a", "#984ea3", "#ff7f00",
				"#ffff33", "#a65628", "#f781bf" });
		brewerColorSchemes.put("set19", new String[] { "#e41a1c", "#377eb8", "#4daf4a", "#984ea3", "#ff7f00",
				"#ffff33", "#a65628", "#f781bf", "#999999" });
		brewerColorSchemes.put("set23", new String[] { "#66c2a5", "#fc8d62", "#8da0cb" });
		brewerColorSchemes.put("set24", new String[] { "#66c2a5", "#fc8d62", "#8da0cb", "#e78ac3" });
		brewerColorSchemes.put("set25",
				new String[] { "#66c2a5", "#fc8d62", "#8da0cb", "#e78ac3", "#a6d854" });
		brewerColorSchemes.put("set26",
				new String[] { "#66c2a5", "#fc8d62", "#8da0cb", "#e78ac3", "#a6d854", "#ffd92f" });
		brewerColorSchemes.put("set27",
				new String[] { "#66c2a5", "#fc8d62", "#8da0cb", "#e78ac3", "#a6d854", "#ffd92f", "#e5c494" });
		brewerColorSchemes.put("set28", new String[] { "#66c2a5", "#fc8d62", "#8da0cb", "#e78ac3", "#a6d854",
				"#ffd92f", "#e5c494", "#b3b3b3" });
		brewerColorSchemes.put("set310", new String[] { "#8dd3c7", "#ffffb3", "#bebada", "#fb8072", "#80b1d3",
				"#fdb462", "#b3de69", "#fccde5", "#d9d9d9", "#bc80bd" });
		brewerColorSchemes.put("set311", new String[] { "#8dd3c7", "#ffffb3", "#bebada", "#fb8072", "#80b1d3",
				"#fdb462", "#b3de69", "#fccde5", "#d9d9d9", "#bc80bd", "#ccebc5" });
		brewerColorSchemes.put("set312", new String[] { "#8dd3c7", "#ffffb3", "#bebada", "#fb8072", "#80b1d3",
				"#fdb462", "#b3de69", "#fccde5", "#d9d9d9", "#bc80bd", "#ccebc5", "#ffed6f" });
		brewerColorSchemes.put("set33", new String[] { "#8dd3c7", "#ffffb3", "#bebada" });
		brewerColorSchemes.put("set34", new String[] { "#8dd3c7", "#ffffb3", "#bebada", "#fb8072" });
		brewerColorSchemes.put("set35",
				new String[] { "#8dd3c7", "#ffffb3", "#bebada", "#fb8072", "#80b1d3" });
		brewerColorSchemes.put("set36",
				new String[] { "#8dd3c7", "#ffffb3", "#bebada", "#fb8072", "#80b1d3", "#fdb462" });
		brewerColorSchemes.put("set37",
				new String[] { "#8dd3c7", "#ffffb3", "#bebada", "#fb8072", "#80b1d3", "#fdb462", "#b3de69" });
		brewerColorSchemes.put("set38", new String[] { "#8dd3c7", "#ffffb3", "#bebada", "#fb8072", "#80b1d3",
				"#fdb462", "#b3de69", "#fccde5" });
		brewerColorSchemes.put("set39", new String[] { "#8dd3c7", "#ffffb3", "#bebada", "#fb8072", "#80b1d3",
				"#fdb462", "#b3de69", "#fccde5", "#d9d9d9" });
		brewerColorSchemes.put("spectral10", new String[] { "#9e0142", "#d53e4f", "#f46d43", "#fdae61",
				"#fee08b", "#e6f598", "#abdda4", "#66c2a5", "#3288bd", "#5e4fa2" });
		brewerColorSchemes.put("spectral11", new String[] { "#9e0142", "#d53e4f", "#f46d43", "#fdae61",
				"#fee08b", "#ffffbf", "#e6f598", "#abdda4", "#66c2a5", "#3288bd", "#5e4fa2" });
		brewerColorSchemes.put("spectral3", new String[] { "#fc8d59", "#ffffbf", "#99d594" });
		brewerColorSchemes.put("spectral4", new String[] { "#d7191c", "#fdae61", "#abdda4", "#2b83ba" });
		brewerColorSchemes.put("spectral5",
				new String[] { "#d7191c", "#fdae61", "#ffffbf", "#abdda4", "#2b83ba" });
		brewerColorSchemes.put("spectral6",
				new String[] { "#d53e4f", "#fc8d59", "#fee08b", "#e6f598", "#99d594", "#3288bd" });
		brewerColorSchemes.put("spectral7",
				new String[] { "#d53e4f", "#fc8d59", "#fee08b", "#ffffbf", "#e6f598", "#99d594", "#3288bd" });
		brewerColorSchemes.put("spectral8", new String[] { "#d53e4f", "#f46d43", "#fdae61", "#fee08b",
				"#e6f598", "#abdda4", "#66c2a5", "#3288bd" });
		brewerColorSchemes.put("spectral9", new String[] { "#d53e4f", "#f46d43", "#fdae61", "#fee08b",
				"#ffffbf", "#e6f598", "#abdda4", "#66c2a5", "#3288bd" });
		brewerColorSchemes.put("ylgn3", new String[] { "#f7fcb9", "#addd8e", "#31a354" });
		brewerColorSchemes.put("ylgn4", new String[] { "#ffffcc", "#c2e699", "#78c679", "#238443" });
		brewerColorSchemes.put("ylgn5",
				new String[] { "#ffffcc", "#c2e699", "#78c679", "#31a354", "#006837" });
		brewerColorSchemes.put("ylgn6",
				new String[] { "#ffffcc", "#d9f0a3", "#addd8e", "#78c679", "#31a354", "#006837" });
		brewerColorSchemes.put("ylgn7",
				new String[] { "#ffffcc", "#d9f0a3", "#addd8e", "#78c679", "#41ab5d", "#238443", "#005a32" });
		brewerColorSchemes.put("ylgn8", new String[] { "#ffffe5", "#f7fcb9", "#d9f0a3", "#addd8e", "#78c679",
				"#41ab5d", "#238443", "#005a32" });
		brewerColorSchemes.put("ylgn9", new String[] { "#ffffe5", "#f7fcb9", "#d9f0a3", "#addd8e", "#78c679",
				"#41ab5d", "#238443", "#006837", "#004529" });
		brewerColorSchemes.put("ylgnbu3", new String[] { "#edf8b1", "#7fcdbb", "#2c7fb8" });
		brewerColorSchemes.put("ylgnbu4", new String[] { "#ffffcc", "#a1dab4", "#41b6c4", "#225ea8" });
		brewerColorSchemes.put("ylgnbu5",
				new String[] { "#ffffcc", "#a1dab4", "#41b6c4", "#2c7fb8", "#253494" });
		brewerColorSchemes.put("ylgnbu6",
				new String[] { "#ffffcc", "#c7e9b4", "#7fcdbb", "#41b6c4", "#2c7fb8", "#253494" });
		brewerColorSchemes.put("ylgnbu7",
				new String[] { "#ffffcc", "#c7e9b4", "#7fcdbb", "#41b6c4", "#1d91c0", "#225ea8", "#0c2c84" });
		brewerColorSchemes.put("ylgnbu8", new String[] { "#ffffd9", "#edf8b1", "#c7e9b4", "#7fcdbb",
				"#41b6c4", "#1d91c0", "#225ea8", "#0c2c84" });
		brewerColorSchemes.put("ylgnbu9", new String[] { "#ffffd9", "#edf8b1", "#c7e9b4", "#7fcdbb",
				"#41b6c4", "#1d91c0", "#225ea8", "#253494", "#081d58" });
		brewerColorSchemes.put("ylorbr3", new String[] { "#fff7bc", "#fec44f", "#d95f0e" });
		brewerColorSchemes.put("ylorbr4", new String[] { "#ffffd4", "#fed98e", "#fe9929", "#cc4c02" });
		brewerColorSchemes.put("ylorbr5",
				new String[] { "#ffffd4", "#fed98e", "#fe9929", "#d95f0e", "#993404" });
		brewerColorSchemes.put("ylorbr6",
				new String[] { "#ffffd4", "#fee391", "#fec44f", "#fe9929", "#d95f0e", "#993404" });
		brewerColorSchemes.put("ylorbr7",
				new String[] { "#ffffd4", "#fee391", "#fec44f", "#fe9929", "#ec7014", "#cc4c02", "#8c2d04" });
		brewerColorSchemes.put("ylorbr8", new String[] { "#ffffe5", "#fff7bc", "#fee391", "#fec44f",
				"#fe9929", "#ec7014", "#cc4c02", "#8c2d04" });
		brewerColorSchemes.put("ylorbr9", new String[] { "#ffffe5", "#fff7bc", "#fee391", "#fec44f",
				"#fe9929", "#ec7014", "#cc4c02", "#993404", "#662506" });
		brewerColorSchemes.put("ylorrd3", new String[] { "#ffeda0", "#feb24c", "#f03b20" });
		brewerColorSchemes.put("ylorrd4", new String[] { "#ffffb2", "#fecc5c", "#fd8d3c", "#e31a1c" });
		brewerColorSchemes.put("ylorrd5",
				new String[] { "#ffffb2", "#fecc5c", "#fd8d3c", "#f03b20", "#bd0026" });
		brewerColorSchemes.put("ylorrd6",
				new String[] { "#ffffb2", "#fed976", "#feb24c", "#fd8d3c", "#f03b20", "#bd0026" });
		brewerColorSchemes.put("ylorrd7",
				new String[] { "#ffffb2", "#fed976", "#feb24c", "#fd8d3c", "#fc4e2a", "#e31a1c", "#b10026" });
		brewerColorSchemes.put("ylorrd8", new String[] { "#ffffcc", "#ffeda0", "#fed976", "#feb24c",
				"#fd8d3c", "#fc4e2a", "#e31a1c", "#b10026" });
		brewerColorSchemes.put("ylorrd9", new String[] { "#ffffcc", "#ffeda0", "#fed976", "#feb24c",
				"#fd8d3c", "#fc4e2a", "#e31a1c", "#bd0026", "#800026" });
	}
}

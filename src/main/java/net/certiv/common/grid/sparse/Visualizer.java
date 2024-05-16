package net.certiv.common.grid.sparse;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import net.certiv.common.ex.IllegalArgsEx;
import net.certiv.common.stores.Result;
import net.certiv.common.stores.UniqueList;

public final class Visualizer<K extends Region, V> {

	public static final String PNG = "PNG";

	private final SparseGrid<K, V> grid;
	private final Region gridview;

	private Region view;

	private int maxCellSize;
	private int margin = 24;
	private int width = 1280;
	private int height = 1280;

	/**
	 * Returns a {@link Visualizer} for the given grid.
	 *
	 * @param grid grid to visualize
	 */
	public Visualizer(SparseGrid<K, V> grid) {
		this.grid = grid;
		this.gridview = defineGridView(grid);
		this.view = gridview;
	}

	// --------------------------------

	/**
	 * Returns the {@link Visualizer} qualified to an image size defined by the given
	 * width hint in pixels. The width hint is adopted subject to adjustments to minimize
	 * rounding errors. The image height will be calculated to maintain a unit aspect
	 * ratio.
	 *
	 * @param width image width hint in pixels
	 * @return visualizer instance
	 */
	public Visualizer<K, V> dim(int width) {
		if (width < 200) throw IllegalArgsEx.of("Width size must be GTE 200.");
		this.width = width;
		this.height = width;
		return this;
	}

	/**
	 * Returns the {@link Visualizer} qualified to have an all-around background margin
	 * width defined by the given margin pixel value. Defaults to 24.
	 *
	 * @param margin image background margin width in pixels
	 * @return visualizer instance
	 */
	public Visualizer<K, V> margins(int margin) {
		if (margin < 0) throw IllegalArgsEx.of("Margin size must be GTE 0.");
		this.margin = margin;
		return this;
	}

	/**
	 * Returns the {@link Visualizer} limited to the subset of the grid that intersects
	 * the given view constraint.
	 *
	 * @param view view constraint, or {@code null} for the entire grid
	 * @return visualizer instance
	 */
	public Visualizer<K, V> limit(Region view) {
		if (view != null) {
			this.view = gridview.intersection(view);
		} else {
			this.view = gridview;
		}
		return this;
	}

	// --------------------------------

	/**
	 * Returns a {@link BufferedImage} containing an image generated based on the current
	 * {@link Visualizer} configuration.
	 *
	 * @return generated image
	 */
	public BufferedImage get() {
		maxCellSize = maxCellSize(view);
		normalizeSizes();

		int horz = width - 2 * margin;
		int vert = height - 2 * margin;

		BufferedImage image = createImage(horz, vert);
		return createBackground(image, horz, vert);
	}

	private void normalizeSizes() {
		long cntX = view.x().span();
		long cntY = view.y().span();
		if (cntX >= Integer.MAX_VALUE || cntY >= Integer.MAX_VALUE)
			throw IllegalArgsEx.of("Too many cells [%s:%s].", cntX, cntY);

		width = norm(width, (int) cntX);
		height = norm(height, (int) cntY);
	}

	private int norm(int dim, int cnt) {
		int val = dim - 2 * margin;
		int rem = val % cnt;
		return val + (rem * cnt) + 2 * margin;
	}

	/**
	 * Returns a {@link Result} indicating the success or failure of a PNG image generated
	 * based on the current {@link Visualizer} configuration and saved to a file of the
	 * given filename, or an exception, if thrown.
	 *
	 * @param filename file name for save operation
	 * @return result containing the save status, or an exception, if thrown
	 */
	public Result<Boolean> save(String filename) {
		return save(new File(filename), PNG);
	}

	/**
	 * Returns a {@link Result} indicating the success or failure of an image generated
	 * based on the current {@link Visualizer} configuration and saved with the given
	 * image format type name to a file of the given filename, or an exception, if thrown.
	 *
	 * @param filename file name for save operation
	 * @param fmtName  image format type name
	 * @return result containing the save status, or an exception, if thrown
	 */
	public Result<Boolean> save(String filename, String fmtName) {
		return save(new File(filename), fmtName);
	}

	/**
	 * Returns a {@link Result} indicating the success or failure of an image generated
	 * based on the current {@link Visualizer} configuration and saved with the given
	 * image format type name to the given file, or an exception, if thrown.
	 *
	 * @param out     image output file for save operation
	 * @param fmtName image format type name
	 * @return result containing the save status, or an exception, if thrown
	 */
	public Result<Boolean> save(File out, String fmtName) {
		try {
			return Result.of(ImageIO.write(get(), fmtName, out));
		} catch (IOException e) {
			return Result.of(e);
		}
	}

	private Region defineGridView(SparseGrid<K, V> grid) {
		int x0 = Integer.MAX_VALUE;
		int x1 = Integer.MIN_VALUE;
		int y0 = Integer.MAX_VALUE;
		int y1 = Integer.MIN_VALUE;

		for (K key : grid.navigableKeySet()) {
			x0 = Math.min(x0, key.xMin());
			x1 = Math.max(x1, key.xMax());
			y0 = Math.min(y0, key.yMin());
			y1 = Math.max(y1, key.yMax());
		}
		return new Region(x0, x1, y0, y1);
	}

	private int maxCellSize(Region view) {
		return grid.stream() //
				.filter(k -> view.contains(k)) //
				.map(k -> cellSize(k)) //
				.max(Comparator.naturalOrder()).get();
	}

	private int cellSize(Region key) {
		return (key.xMax() - key.xMin() + 1) * (key.yMax() - key.yMin() + 1);
	}

	private BufferedImage createBackground(BufferedImage image, int horz, int vert) {
		BufferedImage bg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) bg.getGraphics();
		g.setBackground(Color.WHITE);
		g.clearRect(0, 0, width, height);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));

		String fontname = fontFamily("Roboto", "Fira Code", "Source Code Pro", "Segoe UI", "Lucida Sans");
		g.setFont(new Font(fontname, Font.PLAIN, 12));
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		FontMetrics fm = g.getFontMetrics();
		g.setStroke(new BasicStroke(0.85f));
		g.setColor(Color.BLACK);

		// draw columns
		long cols = view.x().span();
		int colWidth = rnd(horz / (double) cols);

		for (long idx = 0; idx <= cols; idx++) {

			// draw left edge line
			int left = rnd(margin + idx * colWidth);
			g.drawLine(left, 0, left, height - margin);

			if (idx < cols) {
				// write col number in top margin
				long col = view.xMin() + idx;
				int x = rnd(left + 0.5 * colWidth);
				int y = rnd(margin / 2);
				drawCentered(g, fm, String.valueOf(col), x, y);
			}
		}

		// draw rows
		long rows = view.x().span();
		int rowHeight = rnd(vert / (double) rows);

		for (long idx = 0; idx <= rows; idx++) {

			// draw top edge line
			int top = rnd(margin + idx * rowHeight);
			g.drawLine(0, top, width - margin, top);

			if (idx < rows) {
				// write row number in left margin
				long row = view.yMin() + idx;
				int x = rnd(margin / 2);
				int y = rnd(top + 0.5 * rowHeight);
				drawCentered(g, fm, String.valueOf(row), x, y);
			}
		}

		// draw image offset onto background
		g.drawImage(image, margin, margin, null);

		// draw margin
		g.setColor(Color.BLUE);
		g.drawRect(margin, margin, horz, vert);

		g.dispose();
		return bg;
	}

	private BufferedImage createImage(int horz, int vert) {
		BufferedImage image = new BufferedImage(horz, vert, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) image.getGraphics();
		g.setBackground(Color.WHITE);
		g.clearRect(0, 0, horz, vert);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));

		grid.stream() //
				.filter(r -> view.contains(r)) //
				.forEach(r -> drawNode(g, r, horz, vert));

		g.dispose();
		return image;
	}

	private void drawNode(Graphics2D g, Region key, int horz, int vert) {
		double xSpan = view.x().span();
		double ySpan = view.y().span();

		double left = ((key.xMin() - 1) / xSpan) * horz;
		double right = (key.xMax() / xSpan) * horz;
		double top = ((key.yMin() - 1) / ySpan) * vert;
		double bot = (key.yMax() / ySpan) * vert;

		int x = rnd(left);
		int y = rnd(top);
		int w = Math.max(rnd(right - left), 1);
		int h = Math.max(rnd(bot - top), 1);

		Color color = Color.getHSBColor(cellSize(key) / (maxCellSize + 1f), 1f, 1f);
		g.setStroke(new BasicStroke(0.85f));
		g.setColor(color);
		g.fillRect(x, y, w, h);
	}

	private void drawCentered(Graphics g, FontMetrics fm, String text, int x, int y) {
		int len = fm.stringWidth(text);
		int fheight = fm.getAscent() - fm.getDescent() - fm.getLeading();
		g.drawString(text, x - len / 2, y + fheight / 2);
	}

	private String fontFamily(String... prefs) {
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		List<String> avail = Arrays.stream(env.getAllFonts()) //
				.map(f -> f.getFamily()) //
				.collect(Collectors.toCollection(UniqueList::new));
		// Collections.sort(avail);
		// Log.debug("Available font families: %s", avail);

		for (String family : prefs) {
			// Log.debug("Font family selected: %s", family);
			if (avail.contains(family)) return family;
		}
		return "Monospaced";
	}

	private int rnd(double d) {
		return (int) Math.round(d);
	}

	// private int maxCellSize(SparseGrid<K, V> grid) {
	// return grid.stream() //
	// .map(k -> cellSize(k)) //
	// .max(Comparator.naturalOrder()).get();
	// }
}

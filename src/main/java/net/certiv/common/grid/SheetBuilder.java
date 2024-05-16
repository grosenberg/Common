package net.certiv.common.grid;

public class SheetBuilder extends Builder<String> {

	public static SheetBuilder on(Sheet sheet) {
		return new SheetBuilder(sheet);
	}

	// --------------------------------

	private SheetBuilder(Sheet sheet) {
		super(sheet);
	}

	// --------------------------------
	// ---- Sheet functions -----------

	public String render() {
		Sheet sheet = get(true);
		return sheet.render();
	}

}

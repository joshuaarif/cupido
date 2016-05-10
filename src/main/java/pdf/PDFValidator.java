package pdf;

import java.io.File;

public class PDFValidator {

	public static boolean isWorksheetAnswer(File f) {
		return f.isFile() && f.getName().toLowerCase().endsWith("a.pdf");
	}

}

package pdf;

public class PDFMergerMain {
//	private static final Log LOG = LogFactory.getLog(PDFMergerMain.class);

	public static void main(String[] args) {
		PDFWorkbookGenerator generator = new PDFWorkbookGenerator();
		String rootWorksheetFolder = "D:/Personal/Cupido/Education.com/Worksheet/Generator/Addition/1-10/worksheet";
		String targetFile = "D:/Personal/Cupido/Education.com/Worksheet/Generator/Addition/1-10/Test.pdf";
		
		generator.generate(rootWorksheetFolder, targetFile, 10);
	}

}

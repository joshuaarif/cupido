package pdf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class PDFMergerMain {

	public static void main(String[] args) {
		File file = new File("D:/Personal/Cupido/Education.com/Worksheet/Generator");
		File target = new File("D:/Personal/Cupido/Education.com/Worksheet/Generator/Merged.pdf");
		PDFMergerExample pdfMergerExample = new PDFMergerExample();
		List<InputStream> sources = new ArrayList<InputStream>();

		for (File f : file.listFiles()) {
			try {
				sources.add(FileUtils.openInputStream(f));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			InputStream merged = pdfMergerExample.merge(sources);
			FileUtils.copyInputStreamToFile(merged, target);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

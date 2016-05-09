package pdf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class PDFMergerMain {

	public static void main(String[] args) {
		File file = new File("D:/Personal/Cupido/Education.com/Worksheet/Generator/Addition/worksheet");
		File target = new File("D:/Personal/Cupido/Education.com/Worksheet/Generator/Addition/Tools.pdf");
		PDFMerger pdfMerger = new PDFMerger();
		// original file
		List<File> worksheetFiles = Lists.newArrayList(file.listFiles());
				
		// to processed as pdf
		Set<File> worksheets = Sets.newTreeSet();
		Set<File> answers = Sets.newTreeSet();
		List<InputStream> sources = Lists.newArrayList();

		// get random x worksheet
		do {
			int randomIndex = ThreadLocalRandom.current().nextInt(worksheetFiles.size());
			File worksheet = worksheetFiles.get(randomIndex) ;
			File answer = getWorksheetAnswer(worksheet);
			
			worksheets.add(worksheet);
			answers.add(answer);
		} while (worksheets.size() < 2);
				
		for (File f : worksheets) {
			try {
				sources.add(FileUtils.openInputStream(f));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		for (File f : answers) {
			try {
				sources.add(FileUtils.openInputStream(f));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			pdfMerger.merge(sources, target, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static File getWorksheetAnswer(File worksheet) {
		//based on name
		String worksheetName = worksheet.getName();
		String answerName = worksheetName.replaceFirst(".pdf", "a.pdf");
		
		String f = worksheet.getParentFile().getParentFile().getPath() + "/answer/" + answerName; 
		
		return new File(f);
	}

}

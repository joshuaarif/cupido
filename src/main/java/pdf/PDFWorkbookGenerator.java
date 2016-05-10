package pdf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class PDFWorkbookGenerator {
	private static final Log LOG = LogFactory.getLog(PDFWorkbookGenerator.class);

	public void generate(String rootWorksheetFolder, String targetFile, int size) {
		File file = new File("D:/Personal/Cupido/Education.com/Worksheet/Generator/Addition/1-10/worksheet");
		File target = new File("D:/Personal/Cupido/Education.com/Worksheet/Generator/Addition/1-10/Test.pdf");
		PDFMerger pdfMerger = new PDFMerger();

		// original file
		List<File> worksheetFiles = Lists.newArrayList(file.listFiles());

		// to processed as pdf
		Set<Integer> randomSeed = Sets.newHashSet();
		List<File> worksheets = Lists.newArrayList();
		List<File> answers = Lists.newArrayList();
		List<InputStream> sources = Lists.newArrayList();

		if (size > worksheetFiles.size()) {
			size = worksheetFiles.size();
		}

		do {
			int randomIndex = ThreadLocalRandom.current().nextInt(worksheetFiles.size());

			randomSeed.add(randomIndex);
		} while (randomSeed.size() < size);

		LOG.info("Random seed " + randomSeed);

		// get random x worksheet
		for (int i : randomSeed) {
			File worksheet = worksheetFiles.get(i);
			File answer = getWorksheetAnswer(worksheet);

			worksheets.add(worksheet);
			answers.add(answer);
		}

		if (worksheets.size() != answers.size()) {
			throw new IllegalArgumentException(
					"Worksheet size " + worksheets.size() + " != Answer size " + answers.size());
		}

		// must be in different loop, because worksheet comes first before
		// answer
		LOG.info("Generating worksheet");
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
				if (PDFValidator.isWorksheetAnswer(f)) {
					sources.add(FileUtils.openInputStream(f));
				}
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
		LOG.info("Generating worksheet : DONE");
	}

	private static File getWorksheetAnswer(File worksheet) {
		// based on name
		File file = null;
		String worksheetName = worksheet.getName();
		String answerName = worksheetName.replaceFirst(".pdf", "a.pdf");

		String filePath = worksheet.getParentFile().getParentFile().getPath() + "/answer/" + answerName;

		file = new File(filePath);

		return (file.exists() ? file : null);
	}

}

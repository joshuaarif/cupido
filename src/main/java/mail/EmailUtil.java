package mail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.io.IOUtils;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.StringRenderer;
import org.stringtemplate.v4.compiler.CompiledST;

import pdf.PDFMergerMain;

public class EmailUtil {

	public String createEmailBody(String templatePath, char delimiter, Map<String, String> emailValues)
			throws IOException {
		InputStream templateStream = PDFMergerMain.class.getClassLoader().getResourceAsStream(templatePath);

		String myString = IOUtils.toString(templateStream, "UTF-8");
		STGroup stGroup = new STGroup(delimiter, delimiter);
		String tempGroup = Integer.toString(ThreadLocalRandom.current().nextInt());
		CompiledST compiledST = stGroup.defineTemplate(tempGroup, myString);

		stGroup.registerRenderer(String.class, new StringRenderer());
		compiledST.hasFormalArgs = false;

		ST st = stGroup.getInstanceOf(tempGroup);

		for (String key : emailValues.keySet()) {
			String value = emailValues.get(key);
			st.add(key, value);
		}

		return st.render();
	}

}

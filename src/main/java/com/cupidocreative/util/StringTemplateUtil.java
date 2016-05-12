package com.cupidocreative.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.io.IOUtils;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.StringRenderer;
import org.stringtemplate.v4.compiler.CompiledST;

import com.cupidocreative.main.PDFMergerMain;

public class StringTemplateUtil {

	/**
	 * Create email body using StringTemplate
	 * 
	 * @param templatePath
	 *            path, relative to ClassLoader
	 * @param delimiter
	 *            delimiter for bound param
	 * @param values
	 *            map contains values for bound param. Map key = param on
	 *            template, Map value = replacement value for bound param
	 * @return string with bound param replaced
	 * @throws IOException
	 *             if template can't be found
	 */
	public static String createFromST(String templatePath, char delimiter, Map<String, String> values)
			throws IOException {
		InputStream templateStream = PDFMergerMain.class.getClassLoader().getResourceAsStream(templatePath);

		String myString = IOUtils.toString(templateStream, "UTF-8");
		STGroup stGroup = new STGroup(delimiter, delimiter);
		String tempGroup = Integer.toString(ThreadLocalRandom.current().nextInt());
		CompiledST compiledST = stGroup.defineTemplate(tempGroup, myString);

		stGroup.registerRenderer(String.class, new StringRenderer());
		compiledST.hasFormalArgs = false;

		ST st = stGroup.getInstanceOf(tempGroup);

		for (String key : values.keySet()) {
			String value = values.get(key);
			st.add(key, value);
		}

		return st.render();
	}

}

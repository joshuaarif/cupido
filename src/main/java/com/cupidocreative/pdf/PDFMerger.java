package com.cupidocreative.pdf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.xmpbox.type.BadFieldValueException;

import com.cupidocreative.util.PDFUtil;

public class PDFMerger extends PDFCreator {

	private static final Log LOG = LogFactory.getLog(PDFMerger.class);

	public PDFMerger(String title, String creator, String subject) {
		super(title, creator, subject);
	}

	private PDFMergerUtility createPDFMergerUtility(List<InputStream> sources,
			ByteArrayOutputStream mergedPDFOutputStream) {
		PDFMergerUtility pdfMerger = new PDFMergerUtility();
		pdfMerger.addSources(sources);
		pdfMerger.setDestinationStream(mergedPDFOutputStream);
		return pdfMerger;
	}

	public void merge(List<InputStream> sources, File target, boolean readOnly, String footerImage) throws IOException {
		InputStream mergedPDFStream = mergeToStream(sources);
		this.create(mergedPDFStream, target, readOnly, footerImage);
	}

	/**
	 * Creates a compound PDF document from a list of input documents.
	 * <p>
	 * The merged document is PDF/A-1b compliant, provided the source documents
	 * are as well. It contains document properties title, creator and subject,
	 * currently hard-coded.
	 *
	 * @param sources
	 *            list of source PDF document streams.
	 * @return compound PDF document as a readable input stream.
	 * @throws IOException
	 *             if anything goes wrong during PDF merge.
	 */
	private InputStream mergeToStream(final List<InputStream> sources) throws IOException {
		try (ByteArrayOutputStream mergedPDFOutputStream = new ByteArrayOutputStream();
				COSStream cosStream = new COSStream()) {
			PDFMergerUtility pdfMerger = createPDFMergerUtility(sources, mergedPDFOutputStream);
			Calendar calendar = Calendar.getInstance();

			// PDF and XMP properties must be identical, otherwise document is
			// not PDF/A compliant
			PDDocumentInformation pdfDocumentInfo = PDFUtil.createPDFDocumentInfo(getTitle(), getCreator(),
					getSubject(), calendar);
			PDMetadata xmpMetadata = PDFUtil.createXMPMetadata(cosStream, getTitle(), getCreator(), getSubject(),
					calendar);
			pdfMerger.setDestinationDocumentInformation(pdfDocumentInfo);
			pdfMerger.setDestinationMetadata(xmpMetadata);

			LOG.debug("Merging " + sources.size() + " source documents into one PDF");
			pdfMerger.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
			LOG.debug("PDF merge successful, size = {" + mergedPDFOutputStream.size() + "} bytes");

			InputStream mergedPDFStream = new ByteArrayInputStream(mergedPDFOutputStream.toByteArray());
			return mergedPDFStream;
		} catch (BadFieldValueException e) {
			throw new IOException("PDF merge problem", e);
		} catch (TransformerException e) {
			throw new IOException("PDF merge problem", e);
		} finally {
			for (InputStream source : sources) {
				IOUtils.closeQuietly(source);
			}
		}
	}

}

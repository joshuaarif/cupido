package pdf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import javax.xml.transform.TransformerException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.DublinCoreSchema;
import org.apache.xmpbox.schema.PDFAIdentificationSchema;
import org.apache.xmpbox.schema.XMPBasicSchema;
import org.apache.xmpbox.type.BadFieldValueException;
import org.apache.xmpbox.xml.XmpSerializer;

public class PDFMerger {
	private static final Log LOG = LogFactory.getLog(PDFMerger.class);
	private String creator;
	private String subject;
	private String title;
	private String footerImage;

	public PDFMerger() {
		this.title = "Cupido Creative worksheet";
		this.creator = "www.cupidocreative.com";
		this.subject = "Cupido Creative worksheet";
		this.footerImage = "D:/Personal/Cupido/Education.com/Worksheet/Generator/Addition/index.jpg";
	}

	public PDFMerger(String title, String creator, String subject, String footerImage) {
		super();
		this.title = title;
		this.creator = creator;
		this.subject = subject;
		this.footerImage = footerImage;
	}

	public PDDocumentInformation createPDFDocumentInfo(String title, String creator, String subject,
			Calendar creationDate) {
		LOG.info("Setting document info (title, author, subject) for merged PDF");
		PDDocumentInformation documentInformation = new PDDocumentInformation();
		documentInformation.setTitle(title);
		documentInformation.setCreator(creator);
		documentInformation.setSubject(subject);

		documentInformation.setAuthor(creator);
		documentInformation.setCreationDate(creationDate);
		documentInformation.setModificationDate(creationDate);

		return documentInformation;
	}

	private PDFMergerUtility createPDFMergerUtility(List<InputStream> sources,
			ByteArrayOutputStream mergedPDFOutputStream) {
		LOG.info("Initialising PDF merge utility");
		PDFMergerUtility pdfMerger = new PDFMergerUtility();
		pdfMerger.addSources(sources);
		pdfMerger.setDestinationStream(mergedPDFOutputStream);
		return pdfMerger;
	}

	private PDMetadata createXMPMetadata(COSStream cosStream, String title, String creator, String subject,
			Calendar creationDate) throws BadFieldValueException, TransformerException, IOException {
		LOG.info("Setting XMP metadata (title, author, subject) for merged PDF");
		XMPMetadata xmpMetadata = XMPMetadata.createXMPMetadata();

		// PDF/A-1b properties
		PDFAIdentificationSchema pdfaSchema = xmpMetadata.createAndAddPFAIdentificationSchema();
		pdfaSchema.setPart(1);
		pdfaSchema.setConformance("B");

		// Dublin Core properties
		DublinCoreSchema dublinCoreSchema = xmpMetadata.createAndAddDublinCoreSchema();
		dublinCoreSchema.setTitle(title);
		dublinCoreSchema.addCreator(creator);
		dublinCoreSchema.setDescription(subject);

		// XMP Basic properties
		XMPBasicSchema basicSchema = xmpMetadata.createAndAddXMPBasicSchema();
		basicSchema.setCreateDate(creationDate);
		basicSchema.setModifyDate(creationDate);
		basicSchema.setMetadataDate(creationDate);
		basicSchema.setCreatorTool(creator);

		// Create and return XMP data structure in XML format
		ByteArrayOutputStream xmpOutputStream = null;
		OutputStream cosXMPStream = null;
		try {
			xmpOutputStream = new ByteArrayOutputStream();
			cosXMPStream = cosStream.createOutputStream();
			new XmpSerializer().serialize(xmpMetadata, xmpOutputStream, true);
			cosXMPStream.write(xmpOutputStream.toByteArray());
			return new PDMetadata(cosStream);
		} finally {
			IOUtils.closeQuietly(xmpOutputStream);
			IOUtils.closeQuietly(cosXMPStream);
		}
	}

	public void merge(List<InputStream> sources, File target, boolean readOnly) throws IOException {
		InputStream mergedPDFStream = mergeToStream(sources);

		try (PDDocument doc = PDDocument.load(mergedPDFStream)) {
			// add image
			for (PDPage page : doc.getPages()) {
				PDImageXObject pdImage = PDImageXObject.createFromFile(footerImage, doc);
				PDPageContentStream contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND, true);

				// contentStream.drawImage(ximage, 20, 20 );
				// better method inspired by
				// http://stackoverflow.com/a/22318681/535646
				// reduce this value if the image is too large
				float scale = 1f;
				contentStream.drawImage(pdImage, 0, 0, pdImage.getWidth() * scale, pdImage.getHeight() * scale);

				contentStream.close();
			}

			int keyLength = 128;

			AccessPermission accessPermission = new AccessPermission();
			if (readOnly) {
				accessPermission.setReadOnly();
			}

			StandardProtectionPolicy spp = new StandardProtectionPolicy("12345", "", accessPermission);
			spp.setEncryptionKeyLength(keyLength);
			spp.setPermissions(accessPermission);
			doc.protect(spp);

			doc.save(target);
		}
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
			PDDocumentInformation pdfDocumentInfo = createPDFDocumentInfo(title, creator, subject, calendar);
			PDMetadata xmpMetadata = createXMPMetadata(cosStream, title, creator, subject, calendar);
			pdfMerger.setDestinationDocumentInformation(pdfDocumentInfo);
			pdfMerger.setDestinationMetadata(xmpMetadata);

			LOG.info("Merging " + sources.size() + " source documents into one PDF");
			pdfMerger.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
			LOG.info("PDF merge successful, size = {" + mergedPDFOutputStream.size() + "} bytes");

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

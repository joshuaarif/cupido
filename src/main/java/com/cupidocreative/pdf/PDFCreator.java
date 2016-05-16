package com.cupidocreative.pdf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class PDFCreator {

	private static final int KEY_LENGTH = 128;
	
	private String pdfAdminPassword = "12345678Aa";
	private String creator;
	private String subject;
	private String title;

	public PDFCreator(String title, String creator, String subject) {
		super();
		this.title = title;
		this.creator = creator;
		this.subject = subject;
	}

	public String getPdfAdminPassword() {
		return pdfAdminPassword;
	}

	public void setPdfAdminPassword(String pdfAdminPassword) {
		this.pdfAdminPassword = pdfAdminPassword;
	}

	public void create(InputStream inputStream, File target, boolean readOnly, String footerImage) throws IOException {
		try (PDDocument doc = PDDocument.load(inputStream)) {
			// add image
			if (footerImage != null) {
				File imageFile = new File(footerImage);

				if (imageFile.exists() && imageFile.isFile()) {
					for (PDPage page : doc.getPages()) {
						PDImageXObject pdImage = PDImageXObject.createFromFile(footerImage, doc);
						PDPageContentStream contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND, true);

						float scale = 1f;
						contentStream.drawImage(pdImage, 0, 0, pdImage.getWidth() * scale, pdImage.getHeight() * scale);

						contentStream.close();
					}
				}
			}

			AccessPermission accessPermission = new AccessPermission();
			if (readOnly) {
				accessPermission.setReadOnly();
			}

			StandardProtectionPolicy spp = new StandardProtectionPolicy(getPdfAdminPassword(), "", accessPermission);
			spp.setEncryptionKeyLength(KEY_LENGTH);
			spp.setPermissions(accessPermission);
			doc.protect(spp);

			doc.save(target);
		}
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}

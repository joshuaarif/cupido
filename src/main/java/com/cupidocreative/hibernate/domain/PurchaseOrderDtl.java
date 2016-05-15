package com.cupidocreative.hibernate.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "po_dtl")
public class PurchaseOrderDtl implements Serializable {

	private static final long serialVersionUID = 5863224042004185484L;

	public static final int PRICE_BASE_PER_PAGE = 400;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "po_hdr_id", nullable = false)
	private PurchaseOrderHdr poHeader;

	@Id
	@Column(name = "po_dtl_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "po_dtl_s")
	@SequenceGenerator(name = "po_dtl_s", sequenceName = "po_dtl_s", allocationSize = 1, initialValue = 1)
	private long id;

	@Column(name = "workbook_code", length = 100, nullable = false)
	private String workbookCode;

	@Column(name = "workbook_size", length = 4, nullable = false)
	private int workbookSize;

	@Column(name = "creation_date", nullable = false)
	private Date creationDate;

	@Column(name = "last_update_date", nullable = false)
	private Date lastUpdateDate;

	@Column(name = "created_by")
	private int createdBy;

	@Column(name = "last_updated_by")
	private int lastUpdatedBy;

	@Column(name = "pdf_filename", length = 25)
	private String pdfFilename;

	@Column(name = "price_base", length = 7)
	private int priceBase;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PurchaseOrderDtl other = (PurchaseOrderDtl) obj;
		if (id != other.id)
			return false;
		if (poHeader == null) {
			if (other.poHeader != null)
				return false;
		} else if (!poHeader.equals(other.poHeader))
			return false;
		return true;
	}

	public int getCreatedBy() {
		return createdBy;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public long getId() {
		return id;
	}

	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	public int getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public String getPdfFilename() {
		return pdfFilename;
	}

	public PurchaseOrderHdr getPoHeader() {
		return poHeader;
	}

	public int getPriceBase() {
		return priceBase;
	}

	public String getWorkbookCode() {
		return workbookCode;
	}

	public int getWorkbookSize() {
		return workbookSize;
	}

	@Override
	public int hashCode() {
		final int prime = 29;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((poHeader == null) ? 0 : poHeader.hashCode());
		return result;
	}

	public boolean isEmpty() {
		return this.getWorkbookSize() < 1 || this.getWorkbookCode() == null || this.getWorkbookCode().length() < 1;
	}

	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public void setLastUpdatedBy(int lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	public void setPdfFilename(String pdfFilename) {
		this.pdfFilename = pdfFilename;
	}

	public void setPoHeader(PurchaseOrderHdr poHeader) {
		this.poHeader = poHeader;
	}

	public void setPriceBase(int priceBase) {
		this.priceBase = priceBase;
	}

	public void setWorkbookCode(String workbookCode) {
		this.workbookCode = workbookCode;
	}

	public void setWorkbookSize(int workbookSize) {
		this.workbookSize = workbookSize;
		this.priceBase = this.workbookSize * PRICE_BASE_PER_PAGE;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PurchaseOrderDtl [poHeader=").append(poHeader == null ? "null" : poHeader.getPoNumber())
				.append(", id=").append(id).append(", workbookCode=").append(workbookCode).append(", workbookSize=")
				.append(workbookSize).append("]");
		return builder.toString();
	}

}

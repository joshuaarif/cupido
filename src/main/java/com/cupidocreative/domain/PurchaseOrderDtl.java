package com.cupidocreative.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "po_dtl")
public class PurchaseOrderDtl implements Serializable {

	private static final long serialVersionUID = 5863224042004185484L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "po_hdr_id", nullable = false)
	private PurchaseOrderHdr poHeader;

	@Id
	@Column(name = "po_dtl_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(length = 100, nullable = false)
	private String workbookCode;

	@Column(length = 4, nullable = false)
	private int workbookSize;

	public PurchaseOrderHdr getPoHeader() {
		return poHeader;
	}

	public void setPoHeader(PurchaseOrderHdr poHeader) {
		this.poHeader = poHeader;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getWorkbookCode() {
		return workbookCode;
	}

	public void setWorkbookCode(String workbookCode) {
		this.workbookCode = workbookCode;
	}

	public int getWorkbookSize() {
		return workbookSize;
	}

	public void setWorkbookSize(int workbookSize) {
		this.workbookSize = workbookSize;
	}

}

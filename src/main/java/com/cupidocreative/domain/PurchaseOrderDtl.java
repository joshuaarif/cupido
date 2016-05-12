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

	@Column(name = "workbook_code", length = 100, nullable = false)
	private String workbookCode;

	@Column(name = "workbook_size", length = 4, nullable = false)
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

	@Override
	public int hashCode() {
		final int prime = 29;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((poHeader == null) ? 0 : poHeader.hashCode());
		return result;
	}

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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PurchaseOrderDtl [poHeader=").append(poHeader.getPoNumber()).append(", id=").append(id)
				.append(", workbookCode=").append(workbookCode).append(", workbookSize=").append(workbookSize)
				.append("]");
		return builder.toString();
	}

}

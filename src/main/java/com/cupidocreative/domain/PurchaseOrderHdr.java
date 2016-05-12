package com.cupidocreative.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.google.common.collect.Lists;

@Entity
@Table(name = "po_hdr")
public class PurchaseOrderHdr implements Serializable {

	private static final long serialVersionUID = 6106622432348004462L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "po_hdr_id", nullable = false, unique = true)
	private long id;

	@Column(length = 60, nullable = false)
	private String email;

	@Column(name = "po_number", length = 20, nullable = false, unique = true)
	private String poNumber;

	/**
	 * Must be list, kalo set ga bisa di parse dari excelnya karena surrogate id
	 * nya sama semua saat initialize (=0)
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "poHeader")
	private List<PurchaseOrderDtl> poDetails = Lists.newArrayListWithCapacity(5);

	@Column(name = "creation_date", nullable = false)
	private Date creationDate;

	@Column(name = "last_update_date", nullable = false)
	private Date lastUpdateDate;

	@Column(name = "created_by")
	private int createdBy;

	@Column(name = "last_updated_by")
	private int lastUpdatedBy;

	/**
	 * Payment status, apakah sudah diterima atau belum. Urutannya : NEW > PAID,
	 * atau NEW > CANCEL
	 */
	@Column(name = "payment_status", length = 15)
	private String payment_status;

	/**
	 * Process status, apakah sudah digenerate / sent. Urutannya : NEW >
	 * GENERATED > COMPLETE atau NEW > CANCEL
	 */
	@Column(name = "process_status", length = 15)
	private String processStatus;

	public void addOrderDetail(PurchaseOrderDtl orderDtl) {
		getPoDetails().add(orderDtl);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPoNumber() {
		return poNumber;
	}

	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	public List<PurchaseOrderDtl> getPoDetails() {
		return poDetails;
	}

	public void setPoDetails(List<PurchaseOrderDtl> poDetails) {
		this.poDetails = poDetails;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((poNumber == null) ? 0 : poNumber.hashCode());
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
		PurchaseOrderHdr other = (PurchaseOrderHdr) obj;
		if (poNumber == null) {
			if (other.poNumber != null)
				return false;
		} else if (!poNumber.equals(other.poNumber))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PurchaseOrderHdr [id=").append(id).append(", email=").append(email).append(", poNumber=")
				.append(poNumber).append(", poDetails=").append(poDetails).append("]");
		return builder.toString();
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public int getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}

	public int getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(int lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	public String getPayment_status() {
		return payment_status;
	}

	public void setPayment_status(String payment_status) {
		this.payment_status = payment_status;
	}

	public String getProcessStatus() {
		return processStatus;
	}

	public void setProcessStatus(String processStatus) {
		this.processStatus = processStatus;
	}
}

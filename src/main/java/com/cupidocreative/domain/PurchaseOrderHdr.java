package com.cupidocreative.domain;

import java.io.Serializable;
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
}

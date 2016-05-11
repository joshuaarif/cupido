package com.cupidocreative.domain;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.google.common.collect.Sets;

@Entity
@Table(name = "po_dtl")
public class PurchaseOrderHdr {

	@Id
	@Column(name = "po_hdr_id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(length = 60, nullable = false)
	private String email;

	@Column(length = 20, nullable = false, unique = true)
	private String poNumber;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "poHeader")
	private Set<PurchaseOrderDtl> purchaseOrderDetails = Sets.newLinkedHashSet();

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

	public Set<PurchaseOrderDtl> getPurchaseOrderDetails() {
		return purchaseOrderDetails;
	}

	public void setPurchaseOrderDetails(Set<PurchaseOrderDtl> purchaseOrderDetails) {
		this.purchaseOrderDetails = purchaseOrderDetails;
	}

	public long getId() {
		return id;
	}

}

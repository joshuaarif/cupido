package com.cupidocreative.domain;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "po_hdr")
public class PurchaseOrderHdr implements Serializable {

	private static final long serialVersionUID = 6106622432348004462L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(length = 60, nullable = false)
	private String email;

	@Column(length = 20, nullable = false, unique = true)
	private String poNumber;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "poHeader")
	private Set<PurchaseOrderDtl> poDetails = new LinkedHashSet<>(0);

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

	public Set<PurchaseOrderDtl> getPoDetails() {
		return poDetails;
	}

	public void setPoDetails(Set<PurchaseOrderDtl> poDetails) {
		this.poDetails = poDetails;
	}
}

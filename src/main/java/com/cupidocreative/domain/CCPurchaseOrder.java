package com.cupidocreative.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PURCHASE_ORDERS")
public class CCPurchaseOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(length = 60, nullable = false)
	private String email;

	@Column(length = 20, nullable = false, unique = true)
	private String poNumber;

	@Column(length = 100, nullable = false)
	private String workbookCode;

	@Column(length = 4, nullable = false)
	private int workbookSize;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((poNumber == null) ? 0 : poNumber.hashCode());
		result = prime * result + ((workbookCode == null) ? 0 : workbookCode.hashCode());
		result = prime * result + workbookSize;
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
		CCPurchaseOrder other = (CCPurchaseOrder) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (poNumber == null) {
			if (other.poNumber != null)
				return false;
		} else if (!poNumber.equals(other.poNumber))
			return false;
		if (workbookCode == null) {
			if (other.workbookCode != null)
				return false;
		} else if (!workbookCode.equals(other.workbookCode))
			return false;
		if (workbookSize != other.workbookSize)
			return false;
		return true;
	}

	public String getEmail() {
		return email;
	}

	public String getPoNumber() {
		return poNumber;
	}

	public String getWorkbookCode() {
		return workbookCode;
	}

	public int getWorkbookSize() {
		return workbookSize;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	public void setWorkbookCode(String workbookCode) {
		this.workbookCode = workbookCode;
	}

	public void setWorkbookSize(int workbookSize) {
		this.workbookSize = workbookSize;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CCPurchaseOrder [email=").append(email).append(", poNumber=").append(poNumber)
				.append(", workbookCode=").append(workbookCode).append(", workbookSize=").append(workbookSize)
				.append("]");
		return builder.toString();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}

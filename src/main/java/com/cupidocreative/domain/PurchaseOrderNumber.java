package com.cupidocreative.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "po_hdr_numbers")
public class PurchaseOrderNumber implements Serializable {

	private static final long serialVersionUID = 9149180171708398843L;

	@Column(unique = true, nullable = false, length = 4)
	@Id
	private int year;

	// non id column (tanpa @Id) tidak bisa ambil dari sequence hibernate
	@Column(nullable = false, length = 6)
	private int sequence;

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PurchaseOrderNumber [year=").append(year).append(", sequence=").append(sequence).append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + sequence;
		result = prime * result + year;
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
		PurchaseOrderNumber other = (PurchaseOrderNumber) obj;
		if (sequence != other.sequence)
			return false;
		if (year != other.year)
			return false;
		return true;
	}

}

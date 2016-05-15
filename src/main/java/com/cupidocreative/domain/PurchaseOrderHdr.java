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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.google.common.collect.Lists;

@Entity
@Table(name = "po_hdr")
public class PurchaseOrderHdr implements Serializable {

	private static final long serialVersionUID = 6106622432348004462L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "po_hdr_s")
	@SequenceGenerator(name = "po_hdr_s", sequenceName = "po_hdr_s", allocationSize = 1, initialValue = 1)
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

	@Column(name = "promo_code", length = 25)
	private String promoCode;

	@Column(name = "price_base", length = 8)
	private int priceBase;

	/**
	 * 0 to 100
	 */
	@Column(name = "discount", length = 3)
	private byte discount;

	@Column(name = "price_after_disc", length = 8)
	private int priceAfterDisc;

	@Column(name = "price_admin_fee", length = 4)
	private int priceAdminFee;

	@Column(name = "price_invoice", length = 8)
	private int priceInvoice;

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

		setPriceBase(getPriceBase() + orderDtl.getPriceBase());
	}

	private void calculatePrices() {
		this.priceAfterDisc = (int) Math.floor(priceBase * ((100 - discount) / 100));
		this.priceInvoice = this.priceAfterDisc + priceAdminFee;
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

	public int getCreatedBy() {
		return createdBy;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public float getDiscount() {
		return discount;
	}

	public String getEmail() {
		return email;
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

	public String getPayment_status() {
		return payment_status;
	}

	public List<PurchaseOrderDtl> getPoDetails() {
		return poDetails;
	}

	public String getPoNumber() {
		return poNumber;
	}

	public int getPriceAdminFee() {
		return priceAdminFee;
	}

	public int getPriceAfterDisc() {
		return priceAfterDisc;
	}

	public int getPriceBase() {
		return priceBase;
	}

	public int getPriceInvoice() {
		return priceInvoice;
	}

	public String getProcessStatus() {
		return processStatus;
	}

	public String getPromoCode() {
		return promoCode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((poNumber == null) ? 0 : poNumber.hashCode());
		return result;
	}

	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public void setDiscount(byte discount) {
		this.discount = discount;
		calculatePrices();
	}

	public void setEmail(String email) {
		this.email = email;
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

	public void setPayment_status(String payment_status) {
		this.payment_status = payment_status;
	}

	public void setPoDetails(List<PurchaseOrderDtl> poDetails) {
		int totalBasePrice = 0;
		this.poDetails = poDetails;

		for (PurchaseOrderDtl dtl : poDetails) {
			totalBasePrice += dtl.getPriceBase();
		}

		setPriceBase(totalBasePrice);
	}

	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	public void setPriceAdminFee(int priceAdminFee) {
		this.priceAdminFee = priceAdminFee;
		calculatePrices();
	}

	public void setPriceAfterDisc(int priceAfterDisc) {
		this.priceAfterDisc = priceAfterDisc;
	}

	public void setPriceBase(int priceBase) {
		this.priceBase = priceBase;
		calculatePrices();
	}

	public void setPriceInvoice(int priceInvoice) {
		this.priceInvoice = priceInvoice;
	}

	public void setProcessStatus(String processStatus) {
		this.processStatus = processStatus;
	}

	public void setPromoCode(String promoCode) {
		this.promoCode = promoCode;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PurchaseOrderHdr [id=").append(id).append(", email=").append(email).append(", poNumber=")
				.append(poNumber).append(", poDetails=").append(poDetails).append("]");
		return builder.toString();
	}
}

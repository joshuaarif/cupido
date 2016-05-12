package com.cupidocreative.common;

public enum PaymentStatus {

	NEW("NEW"), PAID("PAID"), CANCEL("CANCEL");

	private final String value;

	PaymentStatus(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}

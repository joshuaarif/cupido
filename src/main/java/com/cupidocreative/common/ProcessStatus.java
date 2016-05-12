package com.cupidocreative.common;

public enum ProcessStatus {

	NEW("NEW"), GENERATED("GENERATED"), COMPLETE("COMPLETE"), CANCEL("CANCEL");

	private final String value;

	ProcessStatus(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}

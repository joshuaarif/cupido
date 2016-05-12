package com.cupidocreative.common;

public enum TaskStatus {

	SUCCESS((byte) 1), FAILED((byte) 0);

	private final byte value;

	TaskStatus(byte value) {
		this.value = value;
	}

	public byte getValue() {
		return value;
	}
}

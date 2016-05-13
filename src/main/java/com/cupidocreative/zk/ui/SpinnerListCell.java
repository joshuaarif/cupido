package com.cupidocreative.zk.ui;

import org.zkoss.zul.Spinner;

public class SpinnerListCell<T> extends TabularListCell<T, Integer, Spinner> {

	private static final long serialVersionUID = 1L;

	public SpinnerListCell(T data, Integer value, String fieldName, boolean readonly, int maxLength, String width) {
		super(data, value, fieldName, readonly, maxLength, width);
	}

	public SpinnerListCell(T data, Integer value, String fieldName, boolean readonly) {
		super(data, value, fieldName, readonly);
	}

	public SpinnerListCell(T data, Integer value, String fieldName) {
		super(data, value, fieldName);
	}

	@Override
	protected Spinner createComponent(Integer value) {
		if (value == null) {
			return new Spinner();
		}
		return new Spinner(value);
	}

	@Override
	protected Integer getComponentValue() {
		return this.component.getValue();
	}

	@Override
	protected Spinner createComponent(Integer value, int maxLength, String width) {
		Spinner Spinner = new Spinner();
		if (value != null)
			Spinner.setValue(value);
		if (maxLength != 0)
			Spinner.setMaxlength(maxLength);
		if (width != null)
			Spinner.setWidth(width);
		return Spinner;
	}

}

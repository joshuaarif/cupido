package com.cupidocreative.zk.ui;

import org.zkoss.zul.Combobox;

public class ComboboxListCell<T> extends TabularListCell<T, String, Combobox> {

	private static final long serialVersionUID = 1L;

	public ComboboxListCell(T data, String value, String fieldName, boolean readonly, int maxLength, String width) {
		super(data, value, fieldName, readonly, maxLength, width);
	}

	public ComboboxListCell(T data, String value, String fieldName, boolean readonly) {
		super(data, value, fieldName, readonly);
	}

	public ComboboxListCell(T data, String value, String fieldName) {
		super(data, value, fieldName);
	}

	@Override
	protected Combobox createComponent(String value) {
		return new Combobox(value);
	}

	@Override
	protected String getComponentValue() {
		return this.component.getValue().equals("") ? null : this.component.getValue();
	}

	@Override
	protected Combobox createComponent(String value, int maxLength, String width) {
		Combobox Combobox = new Combobox(value);
		if (maxLength != 0)
			Combobox.setMaxlength(maxLength);
		if (width != null)
			Combobox.setWidth(width);
		return Combobox;
	}
}

package com.cupidocreative.zk.ui;

import org.zkoss.zul.Textbox;

public class TextboxListCell<T> extends TabularListCell<T, String, Textbox> {

	private static final long serialVersionUID = 1L;

	public TextboxListCell(T data, String value, String fieldName, boolean readonly, int maxLength, String width) {
		super(data, value, fieldName, readonly, maxLength, width);
	}

	public TextboxListCell(T data, String value, String fieldName, boolean readonly) {
		super(data, value, fieldName, readonly);
	}

	public TextboxListCell(T data, String value, String fieldName) {
		super(data, value, fieldName);
	}

	@Override
	protected Textbox createComponent(String value) {
		return new Textbox(value);
	}

	@Override
	protected String getComponentValue() {
		return this.component.getValue().equals("") ? null : this.component.getValue();
	}

	@Override
	protected Textbox createComponent(String value, int maxLength, String width) {
		Textbox textbox = new Textbox(value);
		if (maxLength != 0)
			textbox.setMaxlength(maxLength);
		if (width != null)
			textbox.setWidth(width);
		return textbox;
	}
}

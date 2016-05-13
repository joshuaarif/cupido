package com.cupidocreative.zk.ui;

import org.zkoss.zul.Intbox;

public class IntboxListCell<T> extends TabularListCell<T, Integer, Intbox> {
	
	private static final long serialVersionUID = 1L;
	
	public IntboxListCell(T data, Integer value, String fieldName, boolean readonly, int maxLength, String width) {
		super(data, value, fieldName, readonly, maxLength, width);
	}
	
	public IntboxListCell(T data, Integer value, String fieldName, boolean readonly) {
		super(data, value, fieldName, readonly);
	}
	
	public IntboxListCell(T data, Integer value, String fieldName) {
		super(data, value, fieldName);
	}

	@Override
	protected Intbox createComponent(Integer value) {
		if(value == null) {
			return new Intbox();
		}
		return new Intbox(value);
	}

	@Override
	protected Integer getComponentValue() {
		return this.component.getValue();
	}

	@Override
	protected Intbox createComponent(Integer value, int maxLength, String width) {
		Intbox intbox = new Intbox();
		if(value != null)
			intbox.setValue(value);
		if(maxLength != 0)
			intbox.setMaxlength(maxLength);
		if(width != null)
			intbox.setWidth(width);
		return intbox;
	}
	
}

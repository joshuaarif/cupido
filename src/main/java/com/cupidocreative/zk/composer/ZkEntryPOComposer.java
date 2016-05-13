package com.cupidocreative.zk.composer;

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.cupidocreative.domain.PurchaseOrderDtl;
import com.cupidocreative.zk.ui.TabularEntry;

public class ZkEntryPOComposer extends SelectorComposer<Window> {

	private static final long serialVersionUID = -2717733290837314444L;

	@Wire
	private Textbox txtEmail;
	@Wire
	private Label lblPoNumber;
	@Wire
	private Button btnSubmit;
	@Wire
	private TabularEntry<PurchaseOrderDtl> lstPoDetail;
}

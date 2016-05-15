package com.cupidocreative.zk.composer;

import java.util.List;

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

import com.cupidocreative.dao.PurchaseOrderDAO;
import com.cupidocreative.domain.PurchaseOrderHdr;

public class ZkPOInquiryComposer extends SelectorComposer<Div> {

	private static final long serialVersionUID = 7180971231679917998L;

	@Wire
	private Textbox txtEmail;

	@Wire
	private Textbox txtPONumber;

	@Wire
	private Listbox lstPoHeaders;
	// end of UI Wire

	private PurchaseOrderDAO poDAO = new PurchaseOrderDAO();

	private List<PurchaseOrderHdr> poHeaders;

	@Wire
	private Button btnFind;

	public void onFind() {
		String email = txtEmail.getValue().trim();
		String poNumber = txtPONumber.getValue().trim();

		poHeaders = poDAO.findPoHeaders(email, poNumber, null, null);
	}

	@Override
	public void doAfterCompose(Div comp) throws Exception {
		super.doAfterCompose(comp);
	}

}

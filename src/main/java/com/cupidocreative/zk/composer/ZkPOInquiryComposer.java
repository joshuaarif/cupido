package com.cupidocreative.zk.composer;

import java.util.List;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

import com.cupidocreative.hibernate.dao.PurchaseOrderDAO;
import com.cupidocreative.hibernate.domain.PurchaseOrderHdr;
import com.google.common.collect.Lists;

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

	private List<PurchaseOrderHdr> poHeaders = Lists.newArrayList();

	@Wire
	private Button btnFind;

	@Override
	public void doAfterCompose(Div comp) throws Exception {
		super.doAfterCompose(comp);

		lstPoHeaders.setModel(new ListModelList<>(poHeaders));

		btnFind.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

			@Override
			public void onEvent(Event arg0) throws Exception {
				String email = txtEmail.getValue().trim();
				String poNumber = txtPONumber.getValue().trim();

				poHeaders = poDAO.findPoHeaders(email, poNumber, null, null);
			poHeaders.forEach(d -> System.out.println(d.getPoNumber()));
			}
		});
	}

	// @Listen("onClick = #btnFind")
	public void onFind() {
		String email = txtEmail.getValue().trim();
		String poNumber = txtPONumber.getValue().trim();

		poHeaders = poDAO.findPoHeaders(email, poNumber, null, null);
	}

}

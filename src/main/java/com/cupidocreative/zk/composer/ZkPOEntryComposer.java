package com.cupidocreative.zk.composer;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Button;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;

import com.cupidocreative.common.PaymentStatus;
import com.cupidocreative.common.ProcessStatus;
import com.cupidocreative.dao.PurchaseOrderDAO;
import com.cupidocreative.domain.PurchaseOrderDtl;
import com.cupidocreative.domain.PurchaseOrderHdr;
import com.cupidocreative.domain.PurchaseOrderNumber;
import com.cupidocreative.hibernate.HibernateUtil;
import com.cupidocreative.zk.ui.ComboboxListCell;
import com.cupidocreative.zk.ui.SpinnerListCell;
import com.cupidocreative.zk.ui.TabularEntry;
import com.google.common.collect.Lists;

@VariableResolver(DelegatingVariableResolver.class)
public class ZkPOEntryComposer extends SelectorComposer<Div> {

	private static final long serialVersionUID = -2717733290837314444L;

	// UI Wire
	@Wire
	private Textbox txtEmail;
	@Wire
	private Label lblPoNumber;
	@Wire
	private Button btnSubmit;
	@Wire
	private Button btnAddPODetail;
	@Wire
	private Button btnDelPODetail;
	@Wire
	private TabularEntry<PurchaseOrderDtl> lstPoDetails;
	// end of UI Wire

	private List<PurchaseOrderDtl> poDetails;

	private PurchaseOrderDAO poDAO = new PurchaseOrderDAO();

	@Override
	public void doAfterCompose(Div comp) throws Exception {
		super.doAfterCompose(comp);

		lstPoDetails.setDataType(PurchaseOrderDtl.class);
		lstPoDetails.setMultiple(true);
		lstPoDetails.setModel(getPODetailModel());
		lstPoDetails.setItemRenderer(getPODetailRenderer());

		// add minimum 1 row
		if (lstPoDetails.getRows() == 0) {
			lstPoDetails.addRow();
		}
	}

	private ListModel<PurchaseOrderDtl> getPODetailModel() {
		if (poDetails == null) {
			poDetails = Lists.newArrayList();
		}

		return new ListModelList<PurchaseOrderDtl>(poDetails);
	}

	private ListitemRenderer<PurchaseOrderDtl> getPODetailRenderer() {
		ListitemRenderer<PurchaseOrderDtl> renderer = new ListitemRenderer<PurchaseOrderDtl>() {

			@Override
			public void render(Listitem item, PurchaseOrderDtl data, int index) throws Exception {
				// item dummy ini harus ada, karena entah gimana, user harus
				// pilih dulu itemnya, baru commbobox nya bisa dimapping ke pojo
				Comboitem itemDummy = new Comboitem("-- Pilih produk --");
				Comboitem itemAddition = new Comboitem("Addition");
				Comboitem itemSubtraction = new Comboitem("Subtraction");
				ComboboxListCell<PurchaseOrderDtl> cboWorkbookCode = new ComboboxListCell<PurchaseOrderDtl>(data,
						data.getWorkbookCode(), "workbookCode");
				cboWorkbookCode.getComponent().appendChild(itemDummy);
				cboWorkbookCode.getComponent().appendChild(itemAddition);
				cboWorkbookCode.getComponent().appendChild(itemSubtraction);
				cboWorkbookCode.getComponent().setSelectedIndex(0);

				SpinnerListCell<PurchaseOrderDtl> spnWorkbookSize = new SpinnerListCell<PurchaseOrderDtl>(data,
						data.getWorkbookSize(), "workbookSize");
				spnWorkbookSize.getComponent().setConstraint("no empty, min 0 max 150");
				spnWorkbookSize.getComponent().setCols(4);
				spnWorkbookSize.addEventListener(Events.ON_BLUR, new EventListener<Event>() {
					@Override
					public void onEvent(Event event) throws Exception {
						int value = spnWorkbookSize.getValue();

						System.out.println(value * PurchaseOrderDtl.PRICE_BASE_PER_PAGE);
						txtEmail.setValue("blur " + value * 111);
					}
				});

				spnWorkbookSize.addEventListener("onChange", new EventListener<InputEvent>() {
					@Override
					public void onEvent(InputEvent event) throws Exception {
						int value = Integer.parseInt(event.getValue());

						System.out.println("Value from event : " + value * 400);
						txtEmail.setValue("change " + value * 111);
					}
				});

				item.appendChild(new Listcell());
				item.appendChild(cboWorkbookCode);
				item.appendChild(spnWorkbookSize);
			}
		};

		return renderer;
	}

	@Listen("onClick = #btnAddPODetail")
	public void onAddPODetail() {
		lstPoDetails.addRow();
	}

	@Listen("onClick = #btnDelPODetail")
	public void onDelPODetail() {
		lstPoDetails.deleteRow();
	}

	@Listen("onClick = #btnSubmit")
	public void onSubmit() {
		// set model nya nanti, PO detail dummy jangan diproses
		lstPoDetails.getValue();

		PurchaseOrderHdr poHeader = populatePurchaseOrder();

		// HibernateUtil.close();
	}

	private PurchaseOrderHdr populatePurchaseOrder() {
		PurchaseOrderHdr poHeader = new PurchaseOrderHdr();
		Date now = Calendar.getInstance().getTime();

		PurchaseOrderNumber poHeaderNumber = poDAO.getPoNumber();

		poHeader.setEmail(txtEmail.getValue());
		poHeader.setCreatedBy(-1);
		poHeader.setCreationDate(now);
		poHeader.setLastUpdateDate(now);
		poHeader.setLastUpdatedBy(-1);
		poHeader.setPayment_status(PaymentStatus.NEW.getValue());
		poHeader.setProcessStatus(ProcessStatus.NEW.getValue());
		poHeader.setPoNumber(Integer.toString(poHeaderNumber.getSequence()));

		// set po detail (model)
		// index based, karena disini id nya masih 0 semua, sehingga equals
		// nya
		// akan menghasilkan duplikat dan tidak bisa di remove by object
		poDetails.clear();

		for (int i = 0; i < lstPoDetails.getValue().size(); i++) {
			PurchaseOrderDtl poDetail = lstPoDetails.getValue().get(i);

			if (poDetail.isEmpty()) {
				lstPoDetails.getValue().remove(i);
			} else {
				poDetail.setCreatedBy(-1);
				poDetail.setCreationDate(now);
				poDetail.setLastUpdateDate(now);
				poDetail.setLastUpdatedBy(-1);
				poDetail.setPoHeader(poHeader);

				poDetails.add(poDetail);
			}
		}

		poHeader.setPoDetails(poDetails);

		poDAO.save(poHeader);

		return poHeader;
	}

}

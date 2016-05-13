package com.cupidocreative.zk.composer;

import java.util.List;

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Spinner;
import org.zkoss.zul.Textbox;

import com.cupidocreative.domain.PurchaseOrderDtl;
import com.cupidocreative.zk.ui.ComboboxListCell;
import com.cupidocreative.zk.ui.IntboxListCell;
import com.cupidocreative.zk.ui.SpinnerListCell;
import com.cupidocreative.zk.ui.TabularEntry;
import com.google.common.collect.Lists;

@VariableResolver(DelegatingVariableResolver.class)
public class ZkEntryPOComposer extends SelectorComposer<Div> {

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
	private TabularEntry<PurchaseOrderDtl> lstPoDetail;
	// end of UI Wire

	private List<PurchaseOrderDtl> poDetails;

	@Override
	public void doAfterCompose(Div comp) throws Exception {
		super.doAfterCompose(comp);

		lstPoDetail.setDataType(PurchaseOrderDtl.class);
		lstPoDetail.setMultiple(true);
		lstPoDetail.setModel(getPODetailModel());
		lstPoDetail.setItemRenderer(getPODetailRenderer());

		// add minimum 1 row
		if (lstPoDetail.getRows() == 0) {
			lstPoDetail.addRow();
		}
	}

	private ListModel<PurchaseOrderDtl> getPODetailModel() {
		if (poDetails == null) {
			poDetails = Lists.newArrayList();
		}

		return new ListModelList<PurchaseOrderDtl>(poDetails);
	}

	@Listen("onClick = #btnAddPODetail")
	public void onAddPODetail() {
		lstPoDetail.addRow();
	}

	@Listen("onClick = #btnDelPODetail")
	public void onDelPODetail() {
		lstPoDetail.deleteRow();
	}

	@Listen("onClick = #btnSubmit")
	public void onSubmit() {
		System.out.println(lstPoDetail.getValue().size());
		System.out.println(poDetails.size());

		for (PurchaseOrderDtl poDetail : lstPoDetail.getValue()) {
			System.out.println("on listbox : " + poDetail);
		}

		for (PurchaseOrderDtl poDetail : poDetails) {
			System.out.println("on array : " + poDetail);
		}
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

				item.appendChild(new Listcell());
				item.appendChild(cboWorkbookCode);
				item.appendChild(spnWorkbookSize);
			}
		};

		return renderer;
	}

}

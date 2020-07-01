package com.fo0.vaadin.scrumtool.ui.views.dialogs;

import java.util.function.Supplier;

import com.fo0.vaadin.scrumtool.ui.views.components.ShareComponent;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class ShareLayout extends Dialog {

	private static final long serialVersionUID = 3160984240233717631L;

	public ShareLayout(String title, Supplier<String> idSupplier, Supplier<String> urlSupplier) {
		ShareComponent boardIdShare = new ShareComponent("Board-ID", "", idSupplier.get());
		boardIdShare.setWidthFull();
		ShareComponent urlShare = new ShareComponent("URL", "", urlSupplier.get());
		urlShare.setWidthFull();

		VerticalLayout root = new VerticalLayout(boardIdShare, urlShare);
		root.setAlignItems(Alignment.CENTER);
		root.setWidthFull();
		setWidth("500px");
		add(root);
	}

}

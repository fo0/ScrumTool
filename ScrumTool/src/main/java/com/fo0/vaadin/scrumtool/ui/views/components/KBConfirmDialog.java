package com.fo0.vaadin.scrumtool.ui.views.components;

import org.claspina.confirmdialog.ConfirmDialog;

public class KBConfirmDialog extends ConfirmDialog {

	private static final long serialVersionUID = 501671212472064502L;

	public KBConfirmDialog() {
		super();
		getChildren().iterator().next().getElement().getStyle().set("padding-left", "30px").set("padding-right", "30px");
	}

	   /**
     * Creates the ConfirmDialog instance without an icon.
     *
     * @return The {@link ConfirmDialog} instance
     */
    public static KBConfirmDialog create() {
        return new KBConfirmDialog();
    }

    /**
     * Creates the ConfirmDialog instance with an info icon.
     *
     * @return The {@link ConfirmDialog} instance
     */
    public static ConfirmDialog createInfo() {
        return create().withIcon(DIALOG_DEFAULT_ICON_FACTORY.getInfoIcon());
    }

    /**
     * Creates the ConfirmDialog instance with a question icon.
     *
     * @return The {@link ConfirmDialog} instance
     */
    public static ConfirmDialog createQuestion() {
        return create().withIcon(DIALOG_DEFAULT_ICON_FACTORY.getQuestionIcon());
    }

    /**
     * Creates the ConfirmDialog instance with a warning icon.
     *
     * @return The {@link ConfirmDialog} instance
     */
    public static ConfirmDialog createWarning() {
        return create().withIcon(DIALOG_DEFAULT_ICON_FACTORY.getWarningIcon());
    }

    /**
     * Creates the ConfirmDialog instance with an error icon.
     *
     * @return The {@link ConfirmDialog} instance
     */
    public static ConfirmDialog createError() {
        return create().withIcon(DIALOG_DEFAULT_ICON_FACTORY.getErrorIcon());
    }
	
}

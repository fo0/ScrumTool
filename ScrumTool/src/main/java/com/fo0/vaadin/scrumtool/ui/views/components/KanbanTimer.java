package com.fo0.vaadin.scrumtool.ui.views.components;

import com.fo0.vaadin.scrumtool.ui.broadcast.BroadcasterBoardTimer;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import lombok.Getter;
import lombok.NonNull;

public class KanbanTimer extends HorizontalLayout {

	private static final long serialVersionUID = 3177233081265413859L;

	@Getter
	private MySimpleTimer timer;

	private String boardId;

	public KanbanTimer(@NonNull String boardId, double defaultValue) {
		this.boardId = boardId;

		timer = new MySimpleTimer();
		timer.setStartTime(defaultValue);

//		timer.addStartListener(e -> {
//		});
//
//		timer.addStopListener(e -> {
//		});

		timer.addTimerEndEvent(e -> {
			Notification.show("Timer ends", 5000, Position.MIDDLE);
		});

		Button btnStart = new Button(VaadinIcon.PLAY.create());
		ToolTip.add(btnStart, "Start timer");
		btnStart.addClickListener(e -> {
			BroadcasterBoardTimer.broadcast(boardId, String.format("start.%s", timer.getTime()));
		});

		Button btnStop = new Button(VaadinIcon.STOP.create());
		ToolTip.add(btnStop, "Stop running timer");
		btnStop.addClickListener(e -> {
			BroadcasterBoardTimer.broadcast(boardId, String.format("stop.%s", timer.getTime()));
		});

		Button btnSettings = new Button(VaadinIcon.COG.create());
		ToolTip.add(btnSettings, "Configure Timer");
		btnSettings.addClickListener(e -> {
			Dialog d = new Dialog();
			CustomNumberField numberField = new CustomNumberField("Edit Timer", 0, 60 * 60, timer.getTime().doubleValue(), false);
			Button btnOk = new Button(VaadinIcon.CHECK.create(), x -> {
				BroadcasterBoardTimer.broadcast(boardId, String.format("time.%s", numberField.getValue()));
				d.close();
			});
			HorizontalLayout layout = new HorizontalLayout(numberField, btnOk);
			layout.setMargin(true);
			d.add(layout);
			d.open();
		});

		add(btnStart, timer, btnStop, btnSettings);
		setSpacing(false);
		getStyle().set("border", "2px solid black");
	}

}

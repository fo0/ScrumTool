package com.fo0.vaadin.scrumtool.views.components;

import com.flowingcode.vaadin.addons.simpletimer.SimpleTimer;
import com.vladsch.flexmark.util.collection.Consumer;

import lombok.Getter;

public class MySimpleTimer extends SimpleTimer {

	private static final long serialVersionUID = 1769434397627915884L;

	private Consumer<Long> startListener;
	private Consumer<Long> stopListener;

	@Getter
	private Number time;
	
	public MySimpleTimer() {
		getStyle().set("font-size", "25px");
	}

	public void addStartListener(Consumer<Long> pcl) {
		this.startListener = pcl;
	}

	public void addStopListener(Consumer<Long> pcl) {
		this.stopListener = pcl;
	}
	
	@Override
	public void setStartTime(Number startTime) {
		super.setStartTime(startTime);
		this.time = startTime;
	}

	@Override
	public void start() {
		super.start();
		executeListener(startListener, 0);
	}

	@Override
	public void pause() {
		super.pause();
	}

	@Override
	public void reset() {
		pause();
		super.reset();
		executeListener(startListener, 0);
	}

	private void executeListener(Consumer<Long> listener, long value) {
		if (listener != null)
			listener.accept(value);
	}
}

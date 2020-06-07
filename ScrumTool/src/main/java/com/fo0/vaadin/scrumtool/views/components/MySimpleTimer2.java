package com.fo0.vaadin.scrumtool.views.components;

import java.time.Duration;
import java.time.LocalTime;

import com.flowingcode.vaadin.addons.simpletimer.SimpleTimer;
import com.flowingcode.vaadin.addons.simpletimer.SimpleTimer.TimerEndedEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventBus;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.shared.Registration;

/**
 * 
 * @created 07.06.2020 - 01:30:10
 * @author KaesDingeling
 * @version 0.1
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class MySimpleTimer2 extends HorizontalLayout {
	private static final long serialVersionUID = -3263965044748574643L;
	
	// Layout
	private TimePicker inputField;
	private SimpleTimer timer;
	private Button buttonStartStop;
	
	// Data
	private ComponentEventBus startListeners;
	private ComponentEventBus stopListeners;

	private ComponentEventBus buttonStartListeners;
	private ComponentEventBus buttonStopListeners;
	
	/**
	 * 
	 */
	public MySimpleTimer2() {
		super();
		
		startListeners = new ComponentEventBus(this);
		stopListeners = new ComponentEventBus(this);
		
		buttonStartListeners = new ComponentEventBus(this);
		buttonStopListeners = new ComponentEventBus(this);
		
		inputField = new TimePicker();
		timer = new SimpleTimer();
		buttonStartStop = new Button();
		
		buttonStartStop.addClickListener(e -> {
			if (isRunning()) {
				buttonStopListeners.fireEvent(e);
			} else {
				buttonStartListeners.fireEvent(e);
			}
		});
		buttonStartStop.setIcon(VaadinIcon.PLAY.create());
		
		add(inputField, timer, buttonStartStop);
		
		expand(inputField);
		expand(timer);
		
		inputField.setValue(LocalTime.of(0, 18));
		inputField.setMinTime(LocalTime.of(0, 1));
		inputField.setMaxTime(LocalTime.of(0, 20));
		inputField.setStep(Duration.ofSeconds(30));
		
		getStyle().set("border", "1px solid var(--_material-button-outline-color)");
		
		timer.getStyle().set("font-size", "25px");
		timer.setFractions(false);
		timer.setVisible(false);
		timer.setMinutes(true);
		
		getStyle().set("margin-top", "5px");
		getStyle().set("margin-bottom", "5px");
	}
	
	/**
	 * 
	 * @param listener
	 * @return
	 * @Created 07.06.2020 - 02:30:51
	 * @author KaesDingeling
	 */
	public Registration addStartListener(ComponentEventListener<ComponentEvent<MySimpleTimer2>> listener) {
		return startListeners.addListener(ComponentEvent.class, (ComponentEventListener) listener);
	}
	
	/**
	 * 
	 * @param listener
	 * @return
	 * @Created 07.06.2020 - 02:30:48
	 * @author KaesDingeling
	 */
	public Registration addStopListener(ComponentEventListener<ComponentEvent<MySimpleTimer2>> listener) {
		return stopListeners.addListener(ComponentEvent.class, (ComponentEventListener) listener);
	}
	
	/**
	 * 
	 * @param listener
	 * @return
	 * @Created 07.06.2020 - 02:30:46
	 * @author KaesDingeling
	 */
	public Registration addButtonStartListener(ComponentEventListener<ClickEvent<Button>> listener) {
		return buttonStartListeners.addListener(ClickEvent.class, (ComponentEventListener) listener);
	}
	
	/**
	 * 
	 * @param listener
	 * @return
	 * @Created 07.06.2020 - 02:30:44
	 * @author KaesDingeling
	 */
	public Registration addButtonStopListener(ComponentEventListener<ClickEvent<Button>> listener) {
		return buttonStopListeners.addListener(ClickEvent.class, (ComponentEventListener) listener);
	}
	
	/**
	 * 
	 * @return
	 * @Created 07.06.2020 - 02:33:24
	 * @author KaesDingeling
	 */
	public Number getTime() {
		return inputField.getValue().toSecondOfDay();
	}
	
	/**
	 * 
	 * @return
	 * @Created 07.06.2020 - 02:30:07
	 * @author KaesDingeling
	 */
	public boolean isRunning() {
		return timer.isRunning();
	}
	
	/**
	 * 
	 * @param startTime
	 * @Created 07.06.2020 - 02:27:20
	 * @author KaesDingeling
	 */
	public void setStartTime(Number startTime) {
		timer.setStartTime(startTime);
		inputField.setValue(LocalTime.ofSecondOfDay(startTime.longValue()));
	}
	
	/**
	 * 
	 * @param listener
	 * @return
	 * @Created 07.06.2020 - 01:13:11
	 * @author KaesDingeling
	 */
	public Registration addTimerEndEvent(final ComponentEventListener<TimerEndedEvent> listener) {
		return timer.addTimerEndEvent(listener);
	}

	/**
	 * 
	 * 
	 * @Created 07.06.2020 - 02:29:38
	 * @author KaesDingeling
	 */
	public void start() {
		if (!isRunning()) {
			timer.start();
			startListeners.fireEvent(new ComponentEvent<MySimpleTimer2>(this, false));
		}
		
		buttonStartStop.setIcon(VaadinIcon.STOP.create());
		inputField.setVisible(false);
		timer.setVisible(true);
	}

	/**
	 * 
	 * 
	 * @Created 07.06.2020 - 02:29:36
	 * @author KaesDingeling
	 */
	public void pause() {
		if (isRunning()) {
			timer.pause();
			stopListeners.fireEvent(new ComponentEvent<MySimpleTimer2>(this, false));
		}

		buttonStartStop.setIcon(VaadinIcon.PLAY.create());
		inputField.setVisible(true);
		timer.setVisible(false);
	}

	/**
	 * 
	 * 
	 * @Created 07.06.2020 - 02:29:33
	 * @author KaesDingeling
	 */
	public void reset() {
		pause();
		timer.reset();
	}
}
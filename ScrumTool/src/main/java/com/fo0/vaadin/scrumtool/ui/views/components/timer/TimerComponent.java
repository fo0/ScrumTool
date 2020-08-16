package com.fo0.vaadin.scrumtool.ui.views.components.timer;

import java.util.concurrent.TimeUnit;

import com.flowingcode.vaadin.addons.simpletimer.SimpleTimer;
import com.flowingcode.vaadin.addons.simpletimer.SimpleTimer.TimerEndedEvent;
import com.fo0.vaadin.scrumtool.ui.views.components.ToolTip;
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventBus;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.shared.Registration;

import lombok.Getter;

/**
 * 
 * @created 07.06.2020 - 01:30:10
 * @author KaesDingeling
 * @version 0.1
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@CssImport(value = "./styles/custom-button-style.css", themeFor = "vaadin-button")
@CssImport(value = "./styles/custom-number-field-style.css", themeFor = "vaadin-number-field")
public class TimerComponent extends HorizontalLayout {
	private static final long serialVersionUID = -3263965044748574643L;
	
	// Layout
	private NumberField inputMinutesField;
	private NumberField inputSecondsField;
	private Label inputSeperator;
	private SimpleTimer timer;
	private Button buttonStartStop;
	private Button buttonPause;
	
	// Data
	private ComponentEventBus startListeners;
	private ComponentEventBus pauseListeners;
	private ComponentEventBus playListeners;
	private ComponentEventBus stopListeners;

	private ComponentEventBus buttonStartListeners;
	private ComponentEventBus buttonStopListeners;
	
	private Component startIcon;
	private Component stopIcon;

	private Component pauseIcon;
	private Component playIcon;

	@Getter
	private boolean running = false;
	@Getter
	private boolean paused = false;
	
	/**
	 * 
	 */
	public TimerComponent() {
		super();
		
		startListeners = new ComponentEventBus(this);
		pauseListeners = new ComponentEventBus(this);
		playListeners = new ComponentEventBus(this);
		stopListeners = new ComponentEventBus(this);
		
		buttonStartListeners = new ComponentEventBus(this);
		buttonStopListeners = new ComponentEventBus(this);
		
		inputMinutesField = new NumberField();
		
		inputSecondsField = new NumberField();
		ToolTip.add(inputSecondsField, "Edit the Timer");
		
		inputSeperator = new Label(":");
		timer = new SimpleTimer();
		buttonStartStop = new Button();
		buttonPause = new Button();
		ToolTip.add(buttonPause, "Pause the timer");
		
		buttonStartStop.addClassName("only-icon");
		ToolTip.add(buttonStartStop, "Start/Stop the Timer");
		
		
		buttonStartStop.addClickListener(e -> {
			if (isRunning() || isPaused()) {
				fireStopEvent();
			} else {
				fireStartEvent();
			}
		});

		buttonPause.addClassName("only-icon");
		buttonPause.addClickListener(e -> {
			if (isPaused()) {
				firePlayEvent();
			} else {
				firePauseEvent();
			}
		});
		buttonPause.setVisible(false);
		
		setAlignItems(Alignment.CENTER);
		add(inputMinutesField, inputSeperator, inputSecondsField, timer, buttonStartStop, buttonPause);
		
		inputSeperator.getStyle().set("padding-bottom", "3px");
		inputSeperator.getStyle().set("text-align", "center");
		inputSeperator.setWidth("5px");
		
		inputMinutesField.addValueChangeListener(this::createValueChangeListener);
		inputMinutesField.setPrefixComponent(VaadinIcon.CLOCK.create());
		inputMinutesField.addThemeName("timer-component-minutes");
		inputMinutesField.addThemeName("timer-component");
		inputMinutesField.setPlaceholder("0");
		inputMinutesField.setWidth("65px");
		inputMinutesField.setMax(60);
		inputMinutesField.setMin(0);
		
		inputSecondsField.addValueChangeListener(this::createValueChangeListener);
		inputSecondsField.addThemeName("timer-component-seconds");
		inputSecondsField.addThemeName("timer-component");
		inputSecondsField.setPlaceholder("00");
		inputSecondsField.setWidth("40px");
		inputSecondsField.setMax(60);
		inputSecondsField.setMin(0);

		timer.getStyle().set("margin-right", "10px");
		timer.getStyle().set("text-align", "center");
		timer.getStyle().set("overflow", "hidden");
//		timer.getStyle().set("font-size", "25px");
		timer.getStyle().set("width", "0");
		timer.setFractions(false);
		timer.setMinutes(true);

		setStartIcon(VaadinIcon.PLAY.create());
		setPlayIcon(VaadinIcon.PLAY.create());
		setPauseIcon(VaadinIcon.PAUSE.create());
		setStopIcon(VaadinIcon.STOP.create());

		getStyle().set("border", "1px dashed var(--material-divider-color)");
		getStyle().set("border-radius", "5px");
		getStyle().set("padding-left", "10px");
		getStyle().set("margin", "5px");
		setSpacing(false);
	}
	
	/**
	 * 
	 * @param event
	 * @Created 14.06.2020 - 01:06:42
	 * @author KaesDingeling
	 */
	private void createValueChangeListener(ComponentValueChangeEvent<NumberField, Double> event) {
		if (event.getValue() != null) {
			if (event.getValue().intValue() <= 0) {
				event.getSource().setValue(null);
			} else if (event.getValue().intValue() > 60) {
				event.getSource().setValue(60d);
			}
		}
	}
	
	/**
	 * 
	 * @param startIcon
	 * @Created 13.06.2020 - 23:17:21
	 * @author KaesDingeling
	 */
	public void setStartIcon(Component startIcon) {
		if (buttonStartStop.getIcon() == null || buttonStartStop.getIcon().equals(this.startIcon)) {
			buttonStartStop.setIcon(startIcon);
		}
		
		this.startIcon = startIcon;
	}
	
	/**
	 * 
	 * @param playIcon
	 * @Created 13.06.2020 - 23:16:19
	 * @author KaesDingeling
	 */
	public void setPlayIcon(Component playIcon) {
		if (buttonPause.getIcon() == null || buttonPause.getIcon().equals(this.playIcon)) {
			buttonPause.setIcon(playIcon);
		}
		
		this.playIcon = playIcon;
	}
	
	/**
	 * 
	 * @param pauseIcon
	 * @Created 13.06.2020 - 23:16:16
	 * @author KaesDingeling
	 */
	public void setPauseIcon(Component pauseIcon) {
		if (buttonPause.getIcon() == null || buttonPause.getIcon().equals(this.pauseIcon)) {
			buttonPause.setIcon(pauseIcon);
		}
		
		this.pauseIcon = pauseIcon;
	}
	
	/**
	 * 
	 * @param stopIcon
	 * @Created 13.06.2020 - 23:16:13
	 * @author KaesDingeling
	 */
	public void setStopIcon(Component stopIcon) {
		if (buttonStartStop.getIcon() == null || buttonStartStop.getIcon().equals(this.stopIcon)) {
			buttonStartStop.setIcon(stopIcon);
		}
		
		this.stopIcon = stopIcon;
	}
	
	/**
	 * 
	 * @param listener
	 * @return
	 * @Created 07.06.2020 - 02:30:51
	 * @author KaesDingeling
	 */
	public Registration addStartListener(ComponentEventListener<ComponentEvent<TimerComponent>> listener) {
		return startListeners.addListener(ComponentEvent.class, (ComponentEventListener) listener);
	}
	
	/**
	 * 
	 * @param listener
	 * @return
	 * @Created 13.06.2020 - 22:20:14
	 * @author KaesDingeling
	 */
	public Registration addPauseListener(ComponentEventListener<ComponentEvent<TimerComponent>> listener) {
		return pauseListeners.addListener(ComponentEvent.class, (ComponentEventListener) listener);
	}
	
	/**
	 * 
	 * @param listener
	 * @return
	 * @Created 13.06.2020 - 22:20:14
	 * @author KaesDingeling
	 */
	public Registration addPlayListener(ComponentEventListener<ComponentEvent<TimerComponent>> listener) {
		return playListeners.addListener(ComponentEvent.class, (ComponentEventListener) listener);
	}
	
	/**
	 * 
	 * @param listener
	 * @return
	 * @Created 07.06.2020 - 02:30:48
	 * @author KaesDingeling
	 */
	public Registration addStopListener(ComponentEventListener<ComponentEvent<TimerComponent>> listener) {
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
	public long getTime() {
		Double minutes = inputMinutesField.getValue();
		
		if (minutes == null) {
			minutes = 0d;
		}
		
		Double secounds = inputSecondsField.getValue();
		
		if (secounds == null) {
			secounds = 0d;
		}
		
		return TimeUnit.MINUTES.toMillis(minutes.longValue()) + TimeUnit.SECONDS.toMillis(secounds.longValue());
	}
	
	/**
	 * 
	 * @param time - milliseconds
	 * @Created 13.06.2020 - 23:31:03
	 * @author KaesDingeling
	 */
	public void setTime(long time) {
		timer.setStartTime(TimeUnit.MILLISECONDS.toSeconds(time));
		
		Long minutes = TimeUnit.MILLISECONDS.toMinutes(time);
		
		if (minutes == 0) {
			inputMinutesField.setValue(null);
		} else {
			inputMinutesField.setValue(minutes.doubleValue());
		}
		
		time = time - TimeUnit.MINUTES.toMillis(minutes);
		
		Long secounds = TimeUnit.MILLISECONDS.toSeconds(time);

		if (secounds == 0) {
			inputSecondsField.setValue(null);
		} else {
			inputSecondsField.setValue(secounds.doubleValue());
		}
	}
	
	/**
	 * 
	 * @return
	 * @Created 13.06.2020 - 23:40:35
	 * @author KaesDingeling
	 */
	public long getCurrentTime() {
		return timer.getCurrentTime().longValue();
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
		startListeners.fireEvent(new ComponentEvent<TimerComponent>(this, false));
		startSilent();
	}
	
	/**
	 * 
	 * 
	 * @Created 07.06.2020 - 02:29:38
	 * @author KaesDingeling
	 */
	public void fireStartEvent() {
		startListeners.fireEvent(new ComponentEvent<TimerComponent>(this, false));
	}
	
	/**
	 * 
	 * 
	 * @Created 13.06.2020 - 22:10:26
	 * @author KaesDingeling
	 */
	public void startSilent() {
		paused = false;
		running = true;

		timer.getStyle().set("width", "60px");
		timer.getStyle().remove("overflow");
		inputMinutesField.setVisible(false);
		inputSecondsField.setVisible(false);
		buttonStartStop.setIcon(stopIcon);
		inputSeperator.setVisible(false);
		buttonPause.setIcon(pauseIcon);
		buttonPause.setVisible(true);
		
		timer.setStartTime(TimeUnit.MILLISECONDS.toSeconds(getTime()));
		
		if (!timer.isRunning()) {
			timer.start();
		}
	}

	/**
	 * 
	 * 
	 * @Created 07.06.2020 - 02:29:36
	 * @author KaesDingeling
	 */
	public void pause() {
		pauseListeners.fireEvent(new ComponentEvent<TimerComponent>(this, false));
		pauseSilent();
	}
	
	public void firePauseEvent() {
		pauseListeners.fireEvent(new ComponentEvent<TimerComponent>(this, false));
	}
	
	/**
	 * 
	 * 
	 * @Created 13.06.2020 - 22:09:08
	 * @author KaesDingeling
	 */
	public void pauseSilent() {
		paused = true;
		
		buttonPause.setIcon(playIcon);
		
		if (timer.isRunning()) {
			timer.pause();
		}
	}

	/**
	 * 
	 * 
	 * @Created 07.06.2020 - 02:29:38
	 * @author KaesDingeling
	 */
	public void play() {
		playListeners.fireEvent(new ComponentEvent<TimerComponent>(this, false));
		playSilent();
	}

	public void firePlayEvent() {
		playListeners.fireEvent(new ComponentEvent<TimerComponent>(this, false));
	}
	
	/**
	 * 
	 * 
	 * @Created 13.06.2020 - 22:10:26
	 * @author KaesDingeling
	 */
	public void playSilent() {
		paused = false;

		buttonPause.setIcon(pauseIcon);
		
		if (!timer.isRunning()) {
			timer.start();
		}
	}

	/**
	 * 
	 * 
	 * @Created 07.06.2020 - 02:29:33
	 * @author KaesDingeling
	 */
	public void stop() {
		stopListeners.fireEvent(new ComponentEvent<TimerComponent>(this, false));
		stopSilent();
	}
	
	/**
	 * 
	 * 
	 * @Created 07.06.2020 - 02:29:33
	 * @author KaesDingeling
	 */
	public void fireStopEvent() {
		stopListeners.fireEvent(new ComponentEvent<TimerComponent>(this, false));
	}
	
	/**
	 * 
	 * 
	 * @Created 13.06.2020 - 22:07:48
	 * @author KaesDingeling
	 */
	public void stopSilent() {
		paused = false;
		running = false;

		timer.getStyle().set("overflow", "hidden");
		timer.getStyle().set("width", "0");
		inputMinutesField.setVisible(true);
		inputSecondsField.setVisible(true);
		buttonStartStop.setIcon(startIcon);
		inputSeperator.setVisible(true);
		buttonPause.setVisible(false);
		
		if (timer.isRunning()) {
			timer.pause();
		}
		
		timer.reset();
	}
}
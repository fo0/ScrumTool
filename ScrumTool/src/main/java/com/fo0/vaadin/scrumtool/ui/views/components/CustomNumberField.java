package com.fo0.vaadin.scrumtool.ui.views.components;

import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextFieldVariant;

import lombok.Getter;

/**
 * 
 * @created 31.05.2020 - 21:41:13
 * @author KaesDingeling
 * @version 0.1
 */
public class CustomNumberField extends NumberField {
	private static final long serialVersionUID = 5473865186412736958L;
	
	@Getter
	private boolean zeroIsInfinite = false;
	@Getter
	private double infiniteValue = 0d;
	
	/**
	 * 
	 * @param min
	 * @param max
	 * @param defaultValue
	 */
	public CustomNumberField(double min, double max, double defaultValue) {
		this(min, max, defaultValue, false);
	}
	
	/**
	 * 
	 * @param min
	 * @param max
	 * @param defaultValue
	 * @param zeroIsInfinite
	 */
	public CustomNumberField(double min, double max, double defaultValue, boolean zeroIsInfinite) {
		super();
		
		this.zeroIsInfinite = zeroIsInfinite;
		
		init(min, max, defaultValue);
	}
	
	/**
	 * 
	 * @param label
	 * @param min
	 * @param max
	 * @param defaultValue
	 */
	public CustomNumberField(String label, double min, double max, double defaultValue) {
		this(label, min, max, defaultValue, false);
	}
	
	/**
	 * 
	 * @param label
	 * @param min
	 * @param max
	 * @param defaultValue
	 * @param zeroIsInfinite
	 */
	public CustomNumberField(String label, double min, double max, double defaultValue, boolean zeroIsInfinite) {
		super(label);
		
		this.zeroIsInfinite = zeroIsInfinite;
		
		init(min, max, defaultValue);
	}
	
	/**
	 * 
	 * @param min
	 * @param max
	 * @param defaultValue
	 * @Created 31.05.2020 - 21:47:07
	 * @author KaesDingeling
	 */
	private void init(double min, double max, double defaultValue) {
		setMin(min);
		setMax(max);
		
		setHasControls(true);
		
		if (isZeroIsInfinite()) {
			addThemeVariants(TextFieldVariant.MATERIAL_ALWAYS_FLOAT_LABEL);
			setPlaceholder("∞");
			
			if (defaultValue == getInfiniteValue()) {
				setValue(null);
			} else {
				setValue(defaultValue);
			}

			addValueChangeListener(e -> {
				if (isZeroIsInfinite() && e.getValue() != null && e.getValue() == getInfiniteValue()) {
					e.getSource().setValue(null);
				}
			});
		} else {
			setValue(defaultValue);
		}
		
		if (!(defaultValue >= min && defaultValue <= max && min <= max)) {
			throw new IllegalStateException("Invalid parameters: min(" + min + ") max(" + max + ") defaultValue(" + defaultValue + ")");
		}
	}
	
	/**
	 * 
	 * @param infiniteValue
	 * @Created 31.05.2020 - 21:55:18
	 * @author KaesDingeling
	 */
	public void setInfiniteValue(double infiniteValue) {
		this.infiniteValue = infiniteValue;
		
		if (isZeroIsInfinite()) {
			if (getValue() == getInfiniteValue()) {
				setValue(null);
			}
		}
	}
	
	@Override
	public Double getValue() {
		if (super.getValue() == null && zeroIsInfinite) {
			return getInfiniteValue();
		}
		
		return super.getValue();
	}
}
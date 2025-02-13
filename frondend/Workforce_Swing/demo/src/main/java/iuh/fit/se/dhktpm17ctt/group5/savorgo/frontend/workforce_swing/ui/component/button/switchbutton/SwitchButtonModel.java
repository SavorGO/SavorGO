package iuh.fit.se.dhktpm17ctt.group5.savorgo.frontend.workforce_swing.ui.component.button.switchbutton;

import java.awt.Color;

public class SwitchButtonModel {
	private String stateOn, stateOff;
	private Color stateOnColor, stateOffColor;
	public String getStateOn() {
		return stateOn;
	}
	public void setStateOn(String stateOn) {
		this.stateOn = stateOn;
	}
	public String getStateOff() {
		return stateOff;
	}
	public void setStateOff(String stateOff) {
		this.stateOff = stateOff;
	}
	public Color getStateOnColor() {
		return stateOnColor;
	}
	public void setStateOnColor(Color stateOnColor) {
		this.stateOnColor = stateOnColor;
	}
	public Color getStateOffColor() {
		return stateOffColor;
	}
	public void setStateOffColor(Color stateOffColor) {
		this.stateOffColor = stateOffColor;
	}
	
	public SwitchButtonModel(String stateOn, String stateOff, Color stateOnColor, Color stateOffColor) {
		super();
		this.stateOn = stateOn;
		this.stateOff = stateOff;
		this.stateOnColor = stateOnColor;
		this.stateOffColor = stateOffColor;
	}
	public SwitchButtonModel() {
		super();
	}
	
}

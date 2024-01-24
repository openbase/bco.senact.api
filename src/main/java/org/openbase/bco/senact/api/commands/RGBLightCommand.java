package org.openbase.bco.senact.api.commands;

/*-
 * #%L
 * BCO Senact API
 * %%
 * Copyright (C) 2013 - 2024 openbase.org
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import java.util.Map;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.bco.senact.api.data.Color;

/**
 *
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public class RGBLightCommand extends AbstractSenactCommand {

	public static final String VALUE_KEY_RED = "red";
	public static final String VALUE_KEY_GREEN = "green";
	public static final String VALUE_KEE_BLUE = "blue";
	public final Color color;

	public RGBLightCommand(Map<String, String> rawValueMap) throws org.openbase.jul.exception.InstantiationException {
		super(rawValueMap);
		try {
			this.color = new Color(readIntValue(VALUE_KEY_RED), readIntValue(VALUE_KEY_GREEN), readIntValue(VALUE_KEE_BLUE));
		} catch(NotAvailableException ex) {
			throw new org.openbase.jul.exception.InstantiationException(this, ex);
		}
	}
	
	public RGBLightCommand(int red, int green, int blue) {
		this(new Color(red, green, blue));
	}

	public RGBLightCommand(Color color) {
		this.color = color;
		registerValue(VALUE_KEY_RED, color.getRed());
		registerValue(VALUE_KEY_GREEN, color.getGreen());
		registerValue(VALUE_KEE_BLUE, color.getBlue());
	}

	public Color getColor() {
		return color;
	}

	@Override
	public void execute() {
		
	}
}

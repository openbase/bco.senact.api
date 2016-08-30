package org.openbase.bco.senact.api.commands;

/*-
 * #%L
 * BCO Senact API
 * %%
 * Copyright (C) 2013 - 2016 openbase.org
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

/**
 *
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public class BuzzerCommand extends AbstractSenactCommand {

	public enum Sound {
	Monkey, Duckling, S1, S2, S3, Confirm };
	public static final String VALUE_KEY_PLAY = "play";
	private String sound;

	public BuzzerCommand(Map<String, String> rawValueMap) throws org.openbase.jul.exception.InstantiationException {
		super(rawValueMap);
		try {
			this.sound = readStringValue(VALUE_KEY_PLAY);
		} catch(NotAvailableException ex) {
			throw new org.openbase.jul.exception.InstantiationException(this, ex);
		}
	}

	public BuzzerCommand(Sound sound) {
		registerValue(VALUE_KEY_PLAY, sound.ordinal());
	}

	@Override
	public void execute() {
		
	}
}

package org.openbase.bco.senact.api.commands;

/*-
 * #%L
 * BCO Senact API
 * %%
 * Copyright (C) 2013 - 2018 openbase.org
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
import org.openbase.jul.exception.InstantiationException;

/**
 *
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public class AmbientLightCommand extends AbstractSenactCommand {

	public static final String COMMAND_VALUE_INTENSITY = "inten";
	private final int  intensity;
	
	public enum MotionState {
		Unknown,
		MovementDetected,
		NothingDeteced,
		SensorDisabled,
	}
	
	public AmbientLightCommand(Map<String, String> rawValueMap) throws InstantiationException {
		super(rawValueMap);
		try {
			this.intensity = readIntValue(COMMAND_VALUE_INTENSITY);
		} catch(NotAvailableException ex) {
			throw new InstantiationException(this, ex);
		}
	}

	public AmbientLightCommand() {
		this.intensity = -1;
	}
	
	@Override
	public void execute() {
		LOGGER.info("update intensity["+intensity+"]");
		if(senact != null) {
			senact.setLightIntensity(intensity);
		}
	}
}

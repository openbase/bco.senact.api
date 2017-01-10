package org.openbase.bco.senact.api.commands;

/*-
 * #%L
 * BCO Senact API
 * %%
 * Copyright (C) 2013 - 2017 openbase.org
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
public class MotionDetectorCommand extends AbstractSenactCommand {

	public static final String COMMAND_VALUE_STATE = "state";
	private final MotionState motionState;
	
	public enum MotionState {
		Unknown,
		MovementDetected,
		NothingDeteced,
		SensorDisabled,
	}
	
	public MotionDetectorCommand(Map<String, String> rawValueMap) throws org.openbase.jul.exception.InstantiationException {
		super(rawValueMap);
		try {
			this.motionState = MotionState.values()[readIntValue(COMMAND_VALUE_STATE)];
		} catch(NotAvailableException ex) {
			throw new org.openbase.jul.exception.InstantiationException(this, ex);
		}
	}

	public MotionDetectorCommand() {
		this.motionState = MotionState.Unknown;
	}
	
	@Override
	public void execute() {
		LOGGER.info("update MotionState["+motionState.name()+"]");
		if(senact != null) {
			senact.setMotionState(motionState);
		}
	}
	
}

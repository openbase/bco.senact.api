package org.openbase.bco.senact.api.commands;

/*-
 * #%L
 * BCO Senact API
 * %%
 * Copyright (C) 2013 - 2019 openbase.org
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

/**
 *
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public class ByeCommand extends AbstractSenactCommand {

	public ByeCommand(Map<String, String> rawValueMap) {
		super(rawValueMap);
	}
	
	public ByeCommand() {
		
	}

	@Override
	public void execute() {
		LOGGER.info("Senact says bye.");
	}
}

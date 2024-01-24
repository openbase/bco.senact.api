package org.openbase.bco.senact.api.control;

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


import org.openbase.jul.exception.CouldNotPerformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public abstract class AbstractExecuter<COM> {

    protected static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(AbstractExecuter.class);
    
	private final Object lock = new Object();
	private Thread executer;
	private final COM command;

	public AbstractExecuter(COM command) {
		this.command = command;
	}

	public void start() {
		synchronized (lock) {
			if(executer != null && !executer.isInterrupted()) {
				return;
			}
			
			this.executer = new Thread(command.getClass().getSimpleName()+"Executer") {

				@Override
				public void run() {
					try {
						execute(command);
					} catch(CouldNotPerformException ex) {
						LOGGER.error("Error during execution!", ex);
					}
				}
			};
			executer.start();
		}
	}

	public void cancel() {
		synchronized (lock) {
			if(executer != null) {
				executer.interrupt();
			}
		}
	}
	
	public abstract void execute(COM command) throws CouldNotPerformException;

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[execute:" + command + "]";
	}
}

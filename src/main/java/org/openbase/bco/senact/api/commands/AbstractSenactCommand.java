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


import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.NotAvailableException;
import org.slf4j.LoggerFactory;
import org.openbase.bco.senact.api.SenactInstance;


/**
 *
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public abstract class AbstractSenactCommand {

    protected static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(AbstractSenactCommand.class);
	protected SenactInstance senact;
	
	
	public void setSenact(SenactInstance senact) {
		this.senact = senact;
	}

	public enum State {
		Initialized,
		Transfered,
		Rejected,
		Affirmed,
		Received,
		Executed;
	};
	
	public static final char NAME_TERMINATOR = '[';
	public static final char VALUES_TERMINATOR = ']';
	public static final char VALUE_SEPARATOR = '|';
	public static final char VALUE_ASSIGN_SYMBOLD = '=';
	public static final String SENACT_COMMAND_PACKAGE = AbstractSenactCommand.class.getPackage().getName();
	public static final int ID_SUFIX_SERVER = 0;
	public static final int ID_SUFIX_CLIENT = 1;
	public static final String COMMAND_VALUE_ID = "id";
	public static final int MAX_ID_COUNTER_VALUE = Integer.MAX_VALUE/10;
	
	private static int idCounter = 0;
	
	private Map<String, String> valueMap;
	private int id;
	private State state;

	public AbstractSenactCommand(Map<String, String> rawValueMap) {
		this.id = Integer.parseInt(rawValueMap.get(COMMAND_VALUE_ID));
		this.valueMap = new HashMap<String, String>(rawValueMap);
		this.state = State.Received;
	}
	
	public AbstractSenactCommand() {
		this.id = generateID();
		this.valueMap = new HashMap<String, String>();
		this.registerValue(COMMAND_VALUE_ID, Integer.toString(id));
		this.state = State.Initialized;
	}

	public final void registerValue(String key, int value) {
		registerValue(key, Integer.toString(value));
	}

	public final void registerValue(String key, long value) {
		registerValue(key, Long.toString(value));
	}

	public final void registerValue(String key, boolean value) {
		registerValue(key, Boolean.toString(value));
	}

	public final void registerValue(String key, byte value) {
		registerValue(key, Byte.toString(value));
	}

	public final void registerValue(String key, float value) {
		registerValue(key, Float.toString(value));
	}

	public final void registerValue(String key, double value) {
		registerValue(key, Double.toString(value));
	}

	public final void registerValue(String key, String value) {
		valueMap.put(key, value);
	}

	public final int readIntValue(String key) throws NotAvailableException {
		try {
			return Integer.parseInt(readStringValue(key));
		} catch (Exception ex) {
			throw new NotAvailableException(key, "Not parse Value["+readStringValue(key)+"]!", ex);
		}
	}

	public final long readLongValue(String key) throws NotAvailableException {
		try {
			return Integer.parseInt(readStringValue(key));
		} catch (Exception ex) {
			throw new NotAvailableException(key, "Not parse Value["+readStringValue(key)+"]!", ex);
		}
	}

	public final boolean readBooleanValue(String key) throws NotAvailableException {
		try {
			return Boolean.parseBoolean(readStringValue(key));
		} catch (Exception ex) {
			throw new NotAvailableException(key, "Not parse Value["+readStringValue(key)+"]!", ex);
		}
	}

	public final byte readByteValue(String key) throws NotAvailableException {
		try {
			return Byte.parseByte(readStringValue(key));
		} catch (Exception ex) {
			throw new NotAvailableException(key, "Not parse Value["+readStringValue(key)+"]!", ex);
		}
	}

	public final float readFloatValue(String key) throws NotAvailableException {
		throw new NotAvailableException(key, "Method not supported jet!");
	}

	public final double readDoubleValue(String key) throws NotAvailableException {
		throw new NotAvailableException(key, "Method not supported jet!");
	}

	public final String readStringValue(String key) throws NotAvailableException {
		if(!valueMap.containsKey(key)) {
			throw new NotAvailableException(key, "Not in received values map!");
		}
		return valueMap.get(key);
	}
	
	private synchronized int generateID() {
		return ((idCounter++ % MAX_ID_COUNTER_VALUE) * 10) + ID_SUFIX_SERVER ;
	}
	
	public String serialize() {
		return AbstractSenactCommand.serialize(this);
	}

	public static String serialize(AbstractSenactCommand command) {
		return command.getClass().getSimpleName() + NAME_TERMINATOR + serializeValues(command) + VALUES_TERMINATOR;
	}

	private static String serializeValues(AbstractSenactCommand command) {
		String valueString = "";

		if (command.valueMap.isEmpty()) {
			return valueString;
		}

		for (Entry<String, String> entry : command.valueMap.entrySet()) {
			valueString += entry.getKey() + VALUE_ASSIGN_SYMBOLD + entry.getValue() + VALUE_SEPARATOR;
		}
		valueString = valueString.substring(0, valueString.length() - 1); // remove last value seperator.
		return valueString;
	}

	public static AbstractSenactCommand deserialize(String rawCommand) throws CouldNotPerformException {
		try {
			// parse base command
			String[] nameRawSplit = rawCommand.split("\\"+NAME_TERMINATOR);
			Class<? extends AbstractSenactCommand> commandClass = (Class<? extends AbstractSenactCommand>) AbstractSenactCommand.class.getClassLoader().loadClass(SENACT_COMMAND_PACKAGE + "." + nameRawSplit[0]);
			
			// parse values
			Map<String, String> rawValueMap = new HashMap<String, String>();
			String[] valueSplit;
			String[] values = nameRawSplit[1].split("\\"+VALUES_TERMINATOR)[0].split("\\"+VALUE_SEPARATOR);
			for(String valueString : values) {
				valueSplit = valueString.split(String.valueOf(VALUE_ASSIGN_SYMBOLD));
				rawValueMap.put(valueSplit[0], valueSplit[1]);			
			}
			
			// build command
			return commandClass.getConstructor(Map.class).newInstance(rawValueMap);
		} catch (Exception ex) {
			throw new CouldNotPerformException("Could not serialize Command["+rawCommand+"]!", ex);
		}
	}

	public void setState(State state) {
		this.state = state;
	}
	
	public abstract void execute();

	@Override
	public String toString() {
		return serialize(this);
	}
}

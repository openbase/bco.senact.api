package org.openbase.bco.senact.api;

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


import org.openbase.bco.senact.api.commands.AbstractSenactCommand;
import org.openbase.bco.senact.api.commands.AbstractSenactCommand.State;
import org.openbase.bco.senact.api.commands.WelcomeCommand;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.openbase.jul.exception.CouldNotPerformException;
import org.openbase.jul.exception.NotAvailableException;
import org.openbase.bco.senact.api.control.AbstractExecuter;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 */
public class SenactClientConnection implements Runnable {

	public enum SenactEvent {Connected, Disconnected, Send, Receive, Error};
	
	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SenactClientConnection.class);
    
	private PropertyChangeSupport change;
	private SenactServerService serverService;
	private Thread autoConnectionThread, analyseInputThread, handelOutputThread;
	private boolean terminate, connected;
	private Socket socket;
	private OutputStream out;
	private InputStream in;
	private String clientName;
	private List<AbstractSenactCommand> outgoingCommands;
	private final Object outgoingCommandsLock = new Object();
	private final Object outgoingWaiter = new Object();

	public SenactClientConnection(Socket socket, SenactServerService serverService) {
		this.serverService = serverService;
		this.change = new PropertyChangeSupport(this);
		this.socket = socket;
		this.clientName = "Debug";
		outgoingCommands = new ArrayList<AbstractSenactCommand>();
	}

	public synchronized void autoConnectionhandling() {
		if (autoConnectionThread == null) {
			autoConnectionThread = new Thread(this, "AutoConnection");
			autoConnectionThread.start();
		}
	}

	@Override
	public void run() {
		while (!terminate) {
			LOGGER.info("init.");
			LOGGER.debug("Initialize TCP connection.");
			if (connect()) {
				analyseInputThread = new Thread(
						new Runnable() {
					@Override
					public void run() {
						analyseInput();
					}
				}, "AnalyseInput");
				handelOutputThread = new Thread(
						new Runnable() {
					@Override
					public void run() {
						handelOutput();
					}
				}, "HandelOutput");

				analyseInputThread.start();
				handelOutputThread.start();

				try {
					analyseInputThread.join();
				} catch (InterruptedException ex) {
					LOGGER.warn("Could not join analyseInputThread. ", ex);
					change.firePropertyChange(SenactEvent.Error.name(), null, ex);
					disconnect();
				}
				try {
					handelOutputThread.join();
				} catch (InterruptedException ex) {
					LOGGER.warn("Could not join handelOutputThread. " + ex.getMessage());
					change.firePropertyChange(SenactEvent.Error.name(), null, ex);
					disconnect();
				}
			}
		}
		autoConnectionThread = null;
	}

	protected synchronized boolean connect() {

		LOGGER.info("Connecting to Senact " + clientName + " on " + socket.getInetAddress().getHostName());
		//notifyConnecting();

		try {
			out = socket.getOutputStream();
		} catch (IOException ex) {
			LOGGER.error("Couldn't create outputStream.", ex);
			change.firePropertyChange(SenactEvent.Error.name(), null, ex);
			disconnect();
			return false;
		}

		try {
			in = socket.getInputStream();
		} catch (IOException ex) {
			LOGGER.error("Couldn't create InputStream.", ex);
			change.firePropertyChange(SenactEvent.Error.name(), null, ex);
			disconnect();
			return false;
		}

		setConnected(true);

		LOGGER.info("Established connection to Senact " + clientName + ".");
		return true;
	}

	public synchronized void disconnect() {
		if (connected) {
			LOGGER.info("Close connection to Senact " + clientName + " on " + socket.getInetAddress().getHostName());
			setConnected(false);
			terminate = true;
			//notifyConnectionClosed();
		}


		if (in != null) {
			try {
				in.close();
			} catch (IOException ex) {
				LOGGER.debug("Could not close input stream!", ex);
			}
			in = null;
		}

		if (out != null) {
			try {
				out.close();
			} catch (IOException ex) {
				LOGGER.debug("Could not close output stream!", ex);
			}
			out = null;
		}
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException ex) {
				LOGGER.debug("Could not close socket!", ex);
			}
		}
	}

	public boolean isConnected() {
		return connected;
	}

	private void analyseInput() {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
		String dataInputString;
		try {
			while (connected) {
//				LOGGER.info("analyse input:");
				try {
					LOGGER.info("wait for next command...");
					if((dataInputString = bufferedReader.readLine()) == null) {
						LOGGER.warn("Connection lost!");
						disconnect();
						break;
					}
					LOGGER.info("recive: " + dataInputString);
					change.firePropertyChange(SenactEvent.Receive.name(), null, null);
//					newCommand = mapper.readValue(parser, AbstractCommand.class);
//					lastCommunication = System.currentTimeMillis();
//					timeOut.cancel();
//					LOGGER.debug("New incomming command: " + newCommand);
				} catch (NullPointerException ex) {
					LOGGER.warn("Connection lost!", ex);
//					notifyConnectionError("Connection lost!");
					disconnect();
					continue;
//				} catch (JsonMappingException ex) {
//					LOGGER.warn("Connection closed unexpected!", ex);
//					notifyConnectionError("Connection broken!");
//					disconnect();
//					continue;
//				} catch (JsonParseException ex) {
//					LOGGER.warn("Connection closed unexpected!", ex);
//					notifyConnectionError("Programm version may out of date!");
//					disconnect();
//					continue;
//				} catch (JsonProcessingException ex) {
//					LOGGER.warn("Connection closed unexpected!", ex);
//					notifyConnectionError("Connection broken!");
//					disconnect();
//					continue;
				} catch (SocketException ex) {
					LOGGER.info("Connection closed.", ex);
					change.firePropertyChange(SenactEvent.Error.name(), null, ex);
//					notifyConnectionError("Connection closed.");
					disconnect();
					continue;
				} catch (IOException ex) {
					LOGGER.error("Connection error!", ex);
					change.firePropertyChange(SenactEvent.Error.name(), null, ex);
//					notifyConnectionError("Fatal connection error! Please connect developer!");
					disconnect();
					continue;
				}
//				notifyInputActivity();
//				if (newCommand == null) {
//					LOGGER.error("Bad Incomming Data! Command is null!");
//					continue;
//				}
//
				new AbstractExecuter<String>(dataInputString) {
					@Override
					public void execute(String rawCommand) throws CouldNotPerformException {
						AbstractSenactCommand command = AbstractSenactCommand.deserialize(rawCommand);
						command.setSenact(serverService.getSenact());
						command.execute();
					}
				}.start();
			}
		} catch (Exception ex) {
			LOGGER.error("Fatal connection error!", ex);
			change.firePropertyChange(SenactEvent.Error.name(), null, ex);
//			notifyConnectionError("Fatal connection error! Please contact developer!");
			disconnect();
		}
	}

	private void handelOutput() {
		BufferedWriter bufferedReader = new BufferedWriter(new OutputStreamWriter(out));
		try {
			/* send Welcome command */
			writeCommand(new WelcomeCommand(), bufferedReader);
		} catch (IOException ex) {
			LOGGER.warn("Could not send "+WelcomeCommand.class.getSimpleName());
			change.firePropertyChange(SenactEvent.Error.name(), null, ex);
		}
		
		AbstractSenactCommand nextCommand;
		
		try {
//			generator = jsonFactory.createGenerator(out);
//			sendCommand(new PingCommand(this)); //TODO pinging seems to be buggy! Sometimes sends inifinity pings without timeout. Please check!
//				/*while ( && connected) */
			while (connected) {
				while (connected && (!outgoingCommands.isEmpty())) {
					/* select next command */
					synchronized (outgoingCommandsLock) {
						nextCommand = outgoingCommands.remove(0); // get first command
					}

					/* send next command */
					try {
						LOGGER.debug("Send Command: " + nextCommand);
						writeCommand(nextCommand, bufferedReader);
						nextCommand.setState(State.Transfered);
						change.firePropertyChange(SenactEvent.Send.name(), null, nextCommand);
					} catch (IOException ex) {
						LOGGER.error("Could not send command: " + nextCommand, ex);
						change.firePropertyChange(SenactEvent.Error.name(), null, ex);
					}
				}
				bufferedReader.flush();
				synchronized (outgoingWaiter) {
					outgoingWaiter.wait();
				}
			}
			LOGGER.info("Communication finished.");
		} catch (Exception ex) {
			LOGGER.error("Fatal connection error!", ex);
			ex.printStackTrace(System.err);
			disconnect();
		}
	}

	private void writeCommand(AbstractSenactCommand command, BufferedWriter bufferedReader) throws IOException {
		assert out != null;
		assert command != null;
		bufferedReader.write(command.serialize() + '\0');
		bufferedReader.flush();
		try {
			Thread.sleep(20);
		} catch (InterruptedException ex) {
			java.util.logging.Logger.getLogger(SenactClientConnection.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public AbstractSenactCommand sendCommand(AbstractSenactCommand command) throws CouldNotPerformException { //TODO implement with future obejct
		if (command == null) {
			throw new CouldNotPerformException("Could not send command!", new NotAvailableException("Command"));
		}

		synchronized (outgoingCommandsLock) {
			outgoingCommands.add(command);
		}
		synchronized (outgoingWaiter) {
			outgoingWaiter.notifyAll();
		}
		return command;
	}

	protected synchronized void setConnected(boolean connected) {
		if (this.connected == connected) {
			return;
		}
		this.connected = connected;
		
		if(this.connected) {
			change.firePropertyChange(SenactEvent.Connected.name(), null, null);
		} else {
			change.firePropertyChange(SenactEvent.Disconnected.name(), null, null);
		}
	}
}

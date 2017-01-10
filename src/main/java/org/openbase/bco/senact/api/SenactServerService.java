package org.openbase.bco.senact.api;

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

import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.TreeMap;
import org.openbase.jps.core.JPService;
import org.openbase.jps.preset.JPDebugMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:divine@openbase.org">Divine Threepwood</a>
 *
 */
public class SenactServerService extends Thread {
	
	public final static int SERVER_PORT = 45368;
	public final static long RECONNECTION_TIME = 30000;
	public final static int REFRESH_TIME = 60000;
	
	public enum SenactServerState {Online, Offline, Error};

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SenactServerService.class);
    
	private SenactInstance senact;
	private PropertyChangeSupport change;
	private final TreeMap<Integer, SenactClientConnection> clientConnections;
	private ServerSocket serverSocket;
	private boolean online;
	
	public static void main(String[] args) {
		JPService.overwriteDefaultValue(JPDebugMode.class, true);
		JPService.parseAndExitOnError(args);
//		Logger.setDebugMode(true);
		new SenactServerService();
	}
	
	public SenactClientConnection getLastSenactConnection() {
		return clientConnections.lastEntry().getValue();
	}
	
	public SenactServerService(SenactInstance senact) {
		super(SenactServerService.class.getSimpleName().toString());
		this.online = false;
		this.change = new PropertyChangeSupport(this);
		this.clientConnections = new TreeMap<Integer, SenactClientConnection>();
		this.senact = senact;
		this.start();
	}
	
	public SenactServerService() {
		super(SenactServerService.class.getSimpleName().toString());
		this.online = false;
		this.change = new PropertyChangeSupport(this);
		this.clientConnections = new TreeMap<Integer, SenactClientConnection>();
		this.start();
	}

	@Override
	public void run() {
		while (true) {
			LOGGER.info(".");
			Socket clientSocket = null;
			SenactClientConnection clientConnection = null;

			try {
				serverSocket = new ServerSocket(SERVER_PORT);
				setOnline(true);

			} catch (IOException e) {
				LOGGER.info("Couldn't create server socket. " + e.getMessage() + " Try again in " + RECONNECTION_TIME / 1000 + " secunds.");
				setOnline(false);
				try {
					Thread.sleep(RECONNECTION_TIME);
				} catch (InterruptedException e1) {
					LOGGER.error(e1.getMessage());
				}
			}

			while (online) {
				LOGGER.info(".");
				try {
					LOGGER.debug("Waiting for clients...");
					clientSocket = serverSocket.accept();
					try {
						LOGGER.info("Connecting with client...");
						clientConnection = new SenactClientConnection(clientSocket, this);
						clientConnection.autoConnectionhandling();
						if(senact != null) {
							senact.setSenactClientConnection(clientConnection);
						}
						//clientConnection.sendCommand(new GetCommand());
						LOGGER.info("Register " + clientSocket + " instance:" + clientConnection);
						clientConnections.put(generateClientID(), clientConnection);
					} catch (Exception ex) {
						clientSocket.close();
						change.firePropertyChange(SenactServerState.Error.name(), null, ex);
						throw ex;
					}
				} catch (IOException ex) {
					LOGGER.info("Couldn't connect to client.", ex);
					change.firePropertyChange(SenactServerState.Error.name(), null, ex);
				} catch (Exception ex) {
				LOGGER.error("Couldn't connect to client because error during clint service initialisation!", ex);
					change.firePropertyChange(SenactServerState.Error.name(), null, ex);
				}
			}
		}
	}
	
	public SenactInstance getSenact() {
		return senact;
	}

	private void setOnline(boolean online) {
		this.online = online;
		if (online) {
			LOGGER.info("Server Online.");
			change.firePropertyChange(SenactServerState.Online.name(), null, null);
		} else {
			LOGGER.info("Server Offline.");
			change.firePropertyChange(SenactServerState.Offline.name(), null, null);
		}
	}

//	public void sendCommand(AbstractCommand command, int clientID) throws CouldNotPerformException {
//		if (!clientConnections.containsKey(clientID)) {
//			throw new CouldNotPerformException("Could not send " + command + ". ClientID[" + clientID + "] unknown!");
//		}
//		clientConnections.get(clientID).sendCommand(command);
//	}
//
//	public void sendCommandToAllClients(AbstractCommand command) throws CouldNotPerformException {
//		Iterator<ClientConnection> iterator = clientConnections.values().iterator();
//		while (iterator.hasNext()) {
//			iterator.next().sendCommand(command);
//		}
//	}
	private int clientIDCounter = 0;

	private synchronized int generateClientID() {
		return ++clientIDCounter % Integer.MAX_VALUE;
	}

//	public void closeClientConnection(int clientID) {
//		clientConnections.get(clientID).close();
//	}
//
//	public void finishAllConnections() {
//		for (ClientConnection clientConnection : clientConnections.values()) {
//			clientConnection.finishConnection();
//		}
//	}
//
//	public ClientConnection getClientConnection(int clientID) throws NotAvailableException {
//		if (!clientConnections.containsKey(clientID)) {
//			throw new NotAvailableException("ClientConnection", "clientID[" + clientID + "] is unknown.");
//		}
//		return clientConnections.get(clientID);
//	}
//
//	protected void removeClientConnection(int clientID) {
//		clientConnections.remove(clientID);
//	}

	public boolean isOnline() {
		return online;
	}
}

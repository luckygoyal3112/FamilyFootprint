/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ack;

import com.j256.ormlite.dao.Dao;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLSocketFactory;

/**
 * SmackCcsClient provides communication with GCM Cloud Connection Server (XMPP Server).
 * This sample uses Smack version 4.1.0.
 */
public class SmackCcsClient {

  private static final Logger logger = Logger.getLogger("SmackCssClient");

  private static Dao<User, String> userDao;
  
  private AbstractXMPPConnection connection;

  public SmackCcsClient(String apiKey, String username, String serviceName, String host, int port) {
    XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
        .setServiceName(serviceName)
        .setHost(host)
        .setSocketFactory(SSLSocketFactory.getDefault())
        .setSendPresence(false)
        .setPort(port)
        .build();

    connection = new XMPPTCPConnection(config);
    Roster.getInstanceFor(connection).setRosterLoadedAtLogin(false);

    connection.addConnectionListener(new ConnectionListener() {
      @Override
      public void connected(XMPPConnection connection) {
        logger.info("Connected to CCS");
      }

      @Override
      public void authenticated(XMPPConnection connection, boolean resumed) {
        logger.info("Authenticated with CCS");
      }

      @Override
      public void connectionClosed() {
        logger.info("Connection to CCS closed");
      }

      @Override
      public void connectionClosedOnError(Exception e) {
        logger.log(Level.WARNING, "Connection closed because of an error.", e);
      }

      @Override
      public void reconnectionSuccessful() {
        logger.info("Reconnected to CCS");
      }

      @Override
      public void reconnectingIn(int seconds) {
        logger.info("Reconnecting to CCS in " + seconds);
      }

      @Override
      public void reconnectionFailed(Exception e) {
        logger.log(Level.WARNING, "Reconnection to CCS failed", e);
      }
    });

    //String databaseURL = "jdbc:sqlite:gcm.db";

    try {
      // Connect and authenticate with to XMPP server (GCM CCS in this case).
      connection.connect();
      connection.login(username, apiKey);
    } catch (SmackException | IOException | XMPPException e) {
      logger.log(Level.SEVERE, "Unable to connect or login to GCM CCS.", e);
    }
  }
  /**
   * Begin listening for incoming messages.
   *
   * @param stanzaListener Listener that handles accepted messages. This is defined in
   *                       FriendlyPingServer.
   * @param stanzaFilter Filter that determines what messages are handled by the listener.
   */
  public void listen(StanzaListener stanzaListener, StanzaFilter stanzaFilter) {
    connection.addAsyncStanzaListener(stanzaListener, stanzaFilter);
    logger.info("Listening for incoming XMPP Stanzas...");
  }

  public void sendStanza(Stanza stanza) {
    try {
      connection.sendStanza(stanza);
    } catch (SmackException.NotConnectedException e) {
      logger.log(Level.SEVERE, "Error occurred while sending stanza.", e);
    }
  }
    /**
     *
     * @param gcm_id
     */
  public void removeUser(String gcm_id)
  {
    try
    {
      User user = userDao.queryForId(gcm_id);
      userDao.deleteById(gcm_id);
      //removeUserNotification(user);
      // If all user's have been removed from a notification key remove the key
      List<User> users =  userDao.queryForEq("name", user.getName());
      if(users.size() == 0)
      {
        //notificationDao.deleteById(user.getName());
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }

}

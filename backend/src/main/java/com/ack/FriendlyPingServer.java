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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.jivesoftware.smack.util.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * FriendlyPingServer provides the logic to allow clients to register and be notified of other
 * clients registering. It also responds to pings from clients.
 */
public class FriendlyPingServer {
  private static Dao<User, String> userDao;
  private static Dao<UserConnections, String> userConnectionsDao;

  // FriendlyPing Client.
  private class Client {
    @SerializedName("registration_token")
    String registrationToken;
    @SerializedName("profile_picture_url")
    String profilePictureUrl;
    @SerializedName("profile_Name")
    String profileName;
    @SerializedName("profile_Phone")
    String profilePhone;
    @SerializedName("connection_Status")
    String connectionStatus;

    public boolean isValid() {
      return StringUtils.isNotEmpty(profilePictureUrl) && StringUtils.isNotEmpty(registrationToken) &&
              StringUtils.isNotEmpty(profileName) && StringUtils.isNotEmpty(profilePhone);
      //&&
          //    StringUtils.isNotEmpty(connectionStatus);
    }
  }
  /* FriendlyPing Client.
  private class Connection {
    @SerializedName("registration_token")
    String registrationToken;
    @SerializedName("profile_picture_url")
    String profilePictureUrl;
    @SerializedName("profile_Name")
    String profileName;
    @SerializedName("profile_Phone")
    String profilePhone;
    @SerializedName("connection_Status")
    String connectionStatus;
  }*/

  // FriendlyGcmServer defines onMessage to handle incoming friendly ping messages.
  private class FriendlyGcmServer extends GcmServer {


    public FriendlyGcmServer(String apiKey, String senderId, String serviceName) {
      super(apiKey, senderId, serviceName);
      logger.info("Inside FriendlyGcmServer");
    }

    @Override
    public void onMessage(String from, JsonObject jData) throws SQLException {
      if (jData.has("action")) {
        String action = jData.get("action").getAsString();
        final String payload = gson.toJson(jData);
        logger.info("Received message: " + payload);
        if (action.equals(REGISTER_NEW_CLIENT)) {
          registerNewClient(jData);
        } else if (action.equals(NEW_LOC)) {
          String toPhone = jData.get("tophone").getAsString();
          String toToken = getLatestToken(toPhone);
          //String toToken = jData.get("to").getAsString();
          logger.info("sharing location with: " + toToken);
          String senderToken = jData.get("registration_token").getAsString();
          if (StringUtils.isNotEmpty(toToken) && StringUtils.isNotEmpty(senderToken)) {
            pingLocToClient(jData, toToken);
          } else {
            logger.info("Unable to ping unless to and sender tokens are available.");
          }
        } else if (action.equals(INVITE_REQUEST)) {
          //String toToken = jData.get("invitedRegid").getAsString();
          String toPhone = jData.get("invitedPhone").getAsString();
          String toToken = getLatestToken(toPhone);
          String toName = jData.get("invitedName").getAsString();
          logger.info("sending invitation to: " + toPhone);
          String senderPhone = jData.get("senderPhone").getAsString();
          String senderToken = jData.get("senderRegid").getAsString();
          String senderName = jData.get("senderName").getAsString();
          logger.info("sending invitation to: " + toPhone);
          logger.info("invitation from;: " + senderPhone);
          if (StringUtils.isNotEmpty(toToken) && StringUtils.isNotEmpty(senderToken)) {
            inviteClient(getClient(senderPhone), toToken );
            addInvitedClient(senderPhone, senderName, senderToken, toPhone, toName, toToken);
          } else {
            logger.info("Unable to ping unless to and sender tokens are available.");
          }
        } else if (action.equals(REQUEST_CLIENT_LIST)) {
          requestClientLists(jData);
        } else if (action.equals(INVITE_REQUEST_RESP)) {
          //String toToken = jData.get("receiverRegid").getAsString();
          String toPhone = jData.get("receiverPhone").getAsString();
          String toToken = getLatestToken(toPhone);
          String inv_resp_flag = jData.get("invresponse").getAsString();
          String senderPhone = jData.get("senderPhone").getAsString();
          logger.info("sending invitation_resp response to: " + toPhone);
          logger.info("invitation response_resp from;: " + senderPhone);
          if (StringUtils.isNotEmpty(toToken) && StringUtils.isNotEmpty(senderPhone)) {
            updateInvitedResp(toPhone, senderPhone, inv_resp_flag);
            sendInviteClientResp(getClient(senderPhone), toToken, inv_resp_flag);
          } else {
            logger.info("Unable to ping unless to and sender tokens are available.");
          }
        } else if (action.equals(INVITE_REQUEST_RESP_LOC)) {
          String toPhone = jData.get("receiverPhone").getAsString();
          String toToken = getLatestToken(toPhone);
          String inv_resp_flag = jData.get("invresponse").getAsString();
          String senderPhone = jData.get("senderPhone").getAsString();
          logger.info(  "sending invitation_resp_loc response to: " + toPhone);
          logger.info("invitation response_resp_loc from;: " + senderPhone);
          if (StringUtils.isNotEmpty(toToken) && StringUtils.isNotEmpty(senderPhone)) {
            updateInvitedResp(toPhone, senderPhone, inv_resp_flag);
            sendInviteClientRespLoc(getClient(senderPhone), jData, toToken, inv_resp_flag);
          } else {
            logger.info("Unable to ping unless to and sender tokens are available.");
          }
        } else if (action.equals(UNFRIEND_NOTIFICATION)) {
          //String toToken = jData.get("invitedRegid").getAsString();
          String toPhone = jData.get("invitedPhone").getAsString();
          String toToken = getLatestToken(toPhone);
          logger.info("sending Unfriend Notification to: " + toPhone);
          String senderPhone = jData.get("senderPhone").getAsString();
          logger.info("sending Unfriend Notification to to: " + toPhone);
          logger.info("Unfriend invitation from;: " + senderPhone);
          if (StringUtils.isNotEmpty(toToken) && StringUtils.isNotEmpty(senderPhone)) {
            updateInvitedResp(toPhone, senderPhone, "UF");
            unFriendClient(getClient(senderPhone), toToken);
          } else {
            logger.info("Unable to ping unless to and sender tokens are available.");
          }
        }
      } else {
        logger.info("No action found. Message received missing action.");
      }
    }


  }

  private static final Logger logger = Logger.getLogger("FriendlyPingServer");

  private static final String SENDER_ID = "155711707848";
  private static final String SERVER_API_KEY = "AIzaSyAlUva4LfWRB-woihHi-MM6_ruW-uZ134k";
  // Actions
  private static final String REGISTER_NEW_CLIENT = "register_new_client";
  private static final String NEW_LOC = "new_loc";
  private static final String BROADCAST_NEW_CLIENT = "broadcast_new_client";
  private static final String REQUEST_CLIENT_LIST = "request_client_list";
  private static final String SEND_CLIENT_LIST = "send_client_list";
  private static final String SEND_CONNECTION_LIST = "send_connection_list";
  private static final String PING_CLIENT = "ping_client";
  private static final String INVITE_REQUEST = "invite_request";
  private static final String INVITE_REQUEST_RESP = "invite_request_resp";
  private static final String INVITE_REQUEST_RESP_LOC = "invite_request_resp_loc";
  private static final String SIM_CHANGE_DETECTED = "sim_change_detected";

  public static final String UNFRIEND_NOTIFICATION = "unfriend_notification";
  private static final String INV_RESPONSE = "invresponse";

  // Keys
  private static final String ACTION_KEY = "action";
  private static final String CLIENT_KEY = "client";
  private static final String CLIENTS_KEY = "clients";
  private static final String DATA_KEY = "data";
  private static final String SENDER_KEY = "sender";
  private static final String NEW_LOCATION_KEY = "new_loc";

  private static final String NEW_CLIENT_TOPIC = "/topics/newclient";
  private static final String PING_TITLE = "TrackBook Notification";
  private static final String PING_TITLE_INVITE = "TrackBook Invite";
  private static final String PING_ICON = "mipmap/ic_launcher";
  private static final String CLICK_ACTION = "click_invite_action";

  public static final String SERVICE_NAME = "Friendly Ping Server";

  // Store of clients registered with FriendlyPingServer.
  private Map<String, Client> clientMap;
  // Listener responsible for handling incoming registrations and pings.
  private FriendlyGcmServer friendlyGcmServer;

  // Gson helper to assist with going to and from JSON and Client.
  private Gson gson;

  public FriendlyPingServer(String apiKey, String senderId) throws SQLException {
    logger.log(Level.WARNING, "Inside FriendlyPingServer");
    clientMap = new ConcurrentHashMap<String, Client>();


    //Client serverClient = createServerClient();
    //clientMap.put(serverClient.registrationToken, serverClient);

    gson = new GsonBuilder().create();

    friendlyGcmServer = new FriendlyGcmServer(apiKey, senderId, SERVICE_NAME);
    String databaseURL = "jdbc:sqlite:trackgcmNew.db";

    ConnectionSource connectionSource = new JdbcConnectionSource(databaseURL);
    userDao = DaoManager.createDao(connectionSource, User.class);
    userConnectionsDao = DaoManager.createDao(connectionSource, UserConnections.class);
    TableUtils.createTableIfNotExists(connectionSource, User.class);
    TableUtils.createTableIfNotExists(connectionSource, UserConnections.class);

    //UserHelper userHelper = new UserHelper(this);

    /*if (userHelper  == null) {
      //userHelper = OpenHelperManager.getHelper(this, UserHelper.class);
      UserHelper userHelper = new UserHelper(this);
    }*/
  }

  /**
   * Create Client from given JSON data, add client to client list, broadcast newly registered
   * /
   * client to
   * 6all previously registered clients and send client list to new client.
   *
   * @param jData JSON data containing properties of new Client.
   */
  private void registerNewClient(JsonObject jData) throws SQLException {
    Client newClient = gson.fromJson(jData, Client.class);
    if (newClient.isValid() && !isOld(newClient)) {
      broadcastNewClient(newClient);
      sendClientList_new(newClient);
      addClient(newClient);
    } else if (isOld(newClient)) {
      updateUser(newClient);
      //broadcastNewClient(newClient);
      sendClientList_old(newClient);
      sendExsistingConnections(newClient);
    } else {
      logger.log(Level.WARNING, "Could not unpack received data into a Client.");
    }
  }

  private boolean isOld(Client Client) throws SQLException {
    ArrayList<Client> clientList = new ArrayList();
    List<User> users = userDao.queryForAll();
    for (User user : users) {
      if (user.getPhone().equals(Client.profilePhone)) {
        logger.log(Level.INFO, "Old Client found");
        return true;
      } else {
        logger.log(Level.INFO, "New Client " + user.getPhone());
      }
    }
    return false;

  }

  private String getLatestToken(String phone) throws SQLException {
    List<User> users = userDao.queryForEq("phone",phone);
    for (User user : users) {
      if (user.getPhone().equals(phone)) {
        logger.log(Level.INFO, "Token found" + user.getGcm_id() );
        return user.getGcm_id();
      } else {
        logger.log(Level.INFO, "Token Not found. Cannot send Message to" + user.getPhone());
      }
    }
    return null;
  }
  private void requestClientLists(JsonObject jData) throws SQLException {
    Client oldClient = gson.fromJson(jData, Client.class);
    if (oldClient.isValid()) {
      sendClientList_new(oldClient);
    } else {
      logger.log(Level.WARNING, "Could not unpack received data into a Client.");
    }
  }

  /**
   * Add given client to Map of Clients.
   *
   * @param client Client to be added.
   */
  private void addClient(Client client) {
    clientMap.put(client.profilePhone, client);
    addUser(client.profileName, client.profilePhone, client.registrationToken);
  }

  /**
   * Broadcast the newly registered client to clients that have already been registered. The
   * broadcast is sent via the PubSub topic "/topics/newuser" all registered clients should be
   * subscribed to this topic.
   *
   * @param client Newly registered client.
   */
  private void broadcastNewClient(Client client) throws SQLException {
    List<User> users = userDao.queryForAll();
    for (User user : users) {
      if (!user.getPhone().equals(client.profilePhone)) {
        logger.log(Level.INFO, "Sending New connection Broadcast to ALL registered users" + user.getPhone());
        JsonObject jBroadcast = new JsonObject();

        JsonObject jData = new JsonObject();
        jData.addProperty(ACTION_KEY, BROADCAST_NEW_CLIENT);

        JsonObject jClient = gson.toJsonTree(client).getAsJsonObject();
        jData.add(CLIENT_KEY, jClient);

        jBroadcast.add(DATA_KEY, jData);
        friendlyGcmServer.send(user.getGcm_id(), jBroadcast);
      }
    }
  }
        /*JsonObject jBroadcast = new JsonObject();

        JsonObject jData = new JsonObject();
        jData.addProperty(ACTION_KEY, BROADCAST_NEW_CLIENT);

        JsonObject jClient = gson.toJsonTree(client).getAsJsonObject();
        jData.add(CLIENT_KEY, jClient);

        jBroadcast.add(DATA_KEY, jData);
        friendlyGcmServer.send(NEW_CLIENT_TOPIC, jBroadcast);*

  /**
   * Send client list to newly registered client. When a new client is registered, that client must
   * be informed about the other registered clients.
   *
   * @param client Newly registered client.
   */
  private void sendClientList(Client client) {
    ArrayList<Client> clientList = new ArrayList();
    for (Entry<String, Client> clientEntry : clientMap.entrySet()) {
      Client currentClient = clientEntry.getValue();
      if (currentClient.registrationToken != client.registrationToken) {
        clientList.add(currentClient);
      }
    }
    JsonElement clientElements = gson.toJsonTree(clientList,

            new TypeToken<Collection<Client>>() {
            }.getType());
    if (clientElements.isJsonArray()) {
      JsonObject jSendClientList = new JsonObject();

      JsonObject jData = new JsonObject();
      jData.addProperty(ACTION_KEY, SEND_CLIENT_LIST);
      jData.add(CLIENTS_KEY, clientElements);
      jSendClientList.add(DATA_KEY, jData);
      friendlyGcmServer.send(client.registrationToken, jSendClientList);
    }
  }


  private void sendClientList_new(Client client) throws SQLException {
    ArrayList<Client> clientList = new ArrayList();
    int i=0,j=0;
    List<User> users = userDao.queryForAll();
    long user_count = userDao.countOf();
    for (User user : users) {
      i++;
      j++;
      //ArrayList<Client> clientList = new ArrayList();
      //for (Entry<String, Client> clientEntry : clientMap.entrySet()) {
      //Client currentClient = user.getValue();
      if (user.getPhone() != client.profilePhone) {
        Client client_new = new Client();
        client_new.profileName = user.getName();
        client_new.profilePhone = user.getPhone();
        client_new.registrationToken = user.getGcm_id();
        client_new.profilePictureUrl = "NA";
        client_new.connectionStatus = "NA";
        clientList.add(client_new);
      }

      if (i == 10 || j== user_count) {
        i = 0;
        logger.log(Level.INFO, "Sending 10 connection list");
        JsonElement clientElements = gson.toJsonTree(clientList,
                new TypeToken<Collection<Client>>() {
                }.getType());
        if (clientElements.isJsonArray()) {
          JsonObject jSendClientList = new JsonObject();

          JsonObject jData = new JsonObject();
          jData.addProperty(ACTION_KEY, SEND_CLIENT_LIST);
          jData.add(CLIENTS_KEY, clientElements);

          jSendClientList.add(DATA_KEY, jData);
          friendlyGcmServer.send(client.registrationToken, jSendClientList);
          clientList.clear();
        }
      }
    }
  }

  private void sendClientList_old(Client client) throws SQLException {
      ArrayList<Client> clientList = new ArrayList();
      int i=0,j=0;
      List<User> users = userDao.queryForAll();
      long user_count = userDao.countOf();
      for (User user : users) {
        i++;
        j++;
        //ArrayList<Client> clientList = new ArrayList();
        //for (Entry<String, Client> clientEntry : clientMap.entrySet()) {
        //Client currentClient = user.getValue();
        if (user.getPhone() != client.profilePhone) {
          Client client_new = new Client();
          client_new.profileName = user.getName();
          client_new.profilePhone = user.getPhone();
          client_new.registrationToken = user.getGcm_id();
          client_new.profilePictureUrl = "NA";
          client_new.connectionStatus = "NA";
          if (checkIfConnectionDoesntExsists(client, client_new.profilePhone))
            clientList.add(client_new);
        }
        if (i == 10 || j== user_count) {
          i=0;
          logger.log(Level.INFO, "Sending 10 connection list");
          JsonElement clientElements = gson.toJsonTree(clientList,
                  new TypeToken<Collection<Client>>() {
                  }.getType());
          if (clientElements.isJsonArray()) {
            JsonObject jSendClientList = new JsonObject();

            JsonObject jData = new JsonObject();
            jData.addProperty(ACTION_KEY, SEND_CLIENT_LIST);
            jData.add(CLIENTS_KEY, clientElements);

            jSendClientList.add(DATA_KEY, jData);
            friendlyGcmServer.send(client.registrationToken, jSendClientList);
            clientList.clear();
          }
        }
      }
    }
  private boolean checkIfConnectionDoesntExsists(Client client, String conn_Phone) throws SQLException {
    Map<String, Object> fieldValueMap = new HashMap<String, Object>();
    fieldValueMap.put("m_phone", client.profilePhone);
    fieldValueMap.put("f_phone", conn_Phone);
    List<UserConnections> usersConn = userConnectionsDao.queryForFieldValues(fieldValueMap);
    if (usersConn.isEmpty()){
      logger.log(Level.INFO, "Client"+ conn_Phone +
              "is not a connection yet of" + client.profilePhone +". Please send");
      return true;
    } else {
      logger.log(Level.INFO, "Client"+ conn_Phone +
              "is already a connection yet of" + client.profilePhone);
      return false;
    }
  }
  private void sendExsistingConnections(Client client) throws SQLException {
    ArrayList<Client> connectionList = new ArrayList();
    List<UserConnections> usersConn = userConnectionsDao.queryForEq("m_phone",client.profilePhone);
    //Long usercon_count = userConnectionsDao.queryForEq("m_phone",client.profilePhone);
    for (UserConnections userConn : usersConn) {
      //ArrayList<Client> clientList = new ArrayList();
      //for (Entry<String, Client> clientEntry : clientMap.entrySet()) {
      //Client currentClient = user.getValue();
      if (userConn.get_m_Phone().equals(client.profilePhone)) {
        Client connection_new = new Client();
        connection_new.profileName = userConn.get_f_Name();
        connection_new.profilePhone = userConn.get_f_Phone();
        connection_new.registrationToken = userConn.get_f_Gcm_id();
        connection_new.connectionStatus = userConn.get_conn_id();
        connection_new.profilePictureUrl = "NA";
        connectionList.add(connection_new);
      }
    }
    JsonElement clientElements = gson.toJsonTree(connectionList,
            new TypeToken<Collection<Client>>() {
            }.getType());
    if (clientElements.isJsonArray()) {
      JsonObject jSendConnectionList = new JsonObject();

      JsonObject jData = new JsonObject();
      jData.addProperty(ACTION_KEY, SEND_CONNECTION_LIST);
      jData.add(CLIENTS_KEY, clientElements);

      jSendConnectionList.add(DATA_KEY, jData);
      friendlyGcmServer.send(client.registrationToken, jSendConnectionList);
    }
  }

  /**
   * Send message to Client with matching toToken. The validity of to and sender tokens
   * should be check before this method is called.
   *
   * @param
   * @param toToken     Token of recipient of ping.
   */
  private void pingLocToClient(JsonObject jDataReq, String toToken) {
    JsonObject jPing = new JsonObject();
    JsonObject jData = new JsonObject();
    jData.addProperty(ACTION_KEY, PING_CLIENT);
    jData.add(NEW_LOCATION_KEY, jDataReq);

    jPing.add(DATA_KEY, jData);

    friendlyGcmServer.send(toToken, jPing);
  }
  private Client getClient(String senderPhone) throws SQLException {
    List<User> users = userDao.queryForAll();
    for (User user : users) {
      if (user.getPhone().equals(senderPhone)) {
        Client client_new = new Client();
        client_new.profileName = user.getName();
        client_new.profilePhone = user.getPhone();
        client_new.registrationToken = user.getGcm_id();
        client_new.profilePictureUrl = "NA";
        client_new.connectionStatus = "NA";
        return client_new;
      }
    }
      logger.log(Level.WARNING, "No Client found.");
      return null;
  }
  /**
   * Send message to Client with matching toToken. The validity of to and sender tokens
   * should be check before this method is called.
   *
   * @param toToken     Token of recipient of ping.
   */
  private void inviteClient(Client client, String toToken) throws SQLException {

    JsonObject jClient = gson.toJsonTree(client).getAsJsonObject();
    JsonObject jPing = new JsonObject();
    JsonObject jData = new JsonObject();
    jData.add(CLIENT_KEY, jClient);
    jData.addProperty(ACTION_KEY, INVITE_REQUEST);
    jData.addProperty(SENDER_KEY, toToken);
    jPing.add(DATA_KEY, jData);

    friendlyGcmServer.send(toToken, jPing);
  }

  private void unFriendClient(Client client, String toToken) throws SQLException {
    JsonObject jClient = gson.toJsonTree(client).getAsJsonObject();
    JsonObject jPing = new JsonObject();
    JsonObject jData = new JsonObject();
    jData.add(CLIENT_KEY, jClient);
    jData.addProperty(ACTION_KEY, UNFRIEND_NOTIFICATION);
    jData.addProperty(SENDER_KEY, toToken);
    jPing.add(DATA_KEY, jData);

    friendlyGcmServer.send(toToken, jPing);
  }

  private void sendInviteClientRespLoc(Client client, JsonObject jDataReq, String toToken, String inv_resp_flag) {
    JsonObject jClient = gson.toJsonTree(client).getAsJsonObject();
    JsonObject jPing = new JsonObject();
    JsonObject jData = new JsonObject();
    jData.add(CLIENT_KEY, jClient);
    jData.addProperty(ACTION_KEY, INVITE_REQUEST_RESP_LOC);
    jData.add(NEW_LOCATION_KEY, jDataReq);
    jData.addProperty(INV_RESPONSE, inv_resp_flag);
    jData.addProperty(SENDER_KEY, toToken);
    jPing.add(DATA_KEY, jData);

    friendlyGcmServer.send(toToken, jPing);
  }

  private void sendInviteClientResp(Client client,String toToken, String inv_resp_flag) {
    JsonObject jClient = gson.toJsonTree(client).getAsJsonObject();
    JsonObject jPing = new JsonObject();
    JsonObject jData = new JsonObject();
    jData.add(CLIENT_KEY, jClient);
    jData.addProperty(ACTION_KEY, INVITE_REQUEST_RESP);
    jData.addProperty(INV_RESPONSE, inv_resp_flag);
    jData.addProperty(SENDER_KEY, toToken);
    jPing.add(DATA_KEY, jData);

    friendlyGcmServer.send(toToken, jPing);
  }

  public static void main(String[] args) throws SQLException {
    // Initialize FriendlyPingServer with appropriate API Key and SenderID.
    new FriendlyPingServer(SERVER_API_KEY, SENDER_ID);

    // Keep main thread alive.
    try {
      CountDownLatch latch = new CountDownLatch(1);
      latch.await();
    } catch (InterruptedException e) {
      logger.log(Level.SEVERE, "An error occurred while latch was waiting.", e);
    }
  }

  public void addUser(String name, String phone, String gcm_id) {
    try {
      logger.info("Adding new user: " + name + ":" + gcm_id);
      final User user = userDao.createIfNotExists(new User(name, phone, gcm_id));
      //addUserNotificationKey(user);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void addInvitedClient(String senderPhone, String senderName, String senderToken,
                               String toPhone, String toName, String toToken) {
    try {
      logger.info("Adding new connection: " + toPhone + " for User" + senderPhone);
      final UserConnections userConnection_Master = userConnectionsDao.createIfNotExists(
              new UserConnections(senderName, senderPhone, senderToken, toName, toPhone, toToken, "IS"));
      final UserConnections userConnection_Friend = userConnectionsDao.createIfNotExists(
              new UserConnections(toName, toPhone, toToken, senderName, senderPhone, senderToken, "IR"));

      //addUserNotificationKey(user);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void updateUser(Client client) throws SQLException {
    List<User> users = userDao.queryForEq("phone", client.profilePhone);
    for (User user : users) {

      if (user.getPhone().equals(client.profilePhone)) {
        logger.log(Level.INFO, "Old Client found. ");
        logger.log(Level.INFO, "Sending disable instruction to old regId if its exsisting. ");
        //sendDisableNotification(client, client.registrationToken);
        logger.log(Level.INFO, "Updating name from" + user.getName() + "to" + client.profileName);
        logger.log(Level.INFO, "Updating regId from" + user.getGcm_id() + "to" + client.registrationToken);

        user.setName(client.profileName);
        user.setGcm_id(client.registrationToken);
        userDao.update(user);
      }
    }
  }

  private void sendDisableNotification(Client client, String registrationToken) {
    JsonObject jClient = gson.toJsonTree(client).getAsJsonObject();
    JsonObject jPing = new JsonObject();
    JsonObject jData = new JsonObject();
    jData.add(CLIENT_KEY, jClient);
    jData.addProperty(ACTION_KEY, SIM_CHANGE_DETECTED);
    jData.addProperty(SENDER_KEY, registrationToken);
    jPing.add(DATA_KEY, jData);

    friendlyGcmServer.send(registrationToken, jPing);
  }

  public void updateInvitedResp(String toPhone, String senderPhone, String inv_resp_flag) throws SQLException {
    logger.log(Level.INFO, "Updating inv response" + senderPhone + ":" + toPhone
            + "as: " + inv_resp_flag);
    Map<String, Object> query = new HashMap<String, Object>();
    query.put("m_phone", toPhone);
    query.put("f_phone", senderPhone);
    List<UserConnections> usersConnection = userConnectionsDao.queryForFieldValues(query);
    for (UserConnections userConn : usersConnection) {
      if (userConn.get_m_Phone().equals(toPhone) && userConn.get_f_Phone().equals(senderPhone)) {
        logger.log(Level.INFO, "Original Connection found." + userConn.get_f_Name() + userConn.get_m_Name());
        userConn.set_conn_id(inv_resp_flag);
        userConnectionsDao.update(userConn);
      }
    }
    Map<String, Object> query_2 = new HashMap<String, Object>();
    query_2.put("m_phone", senderPhone);
    query_2.put("f_phone", toPhone);
    List<UserConnections> usersConnection_2 = userConnectionsDao.queryForFieldValues(query_2);
    for (UserConnections userConn_2 : usersConnection_2) {
      if (userConn_2.get_m_Phone().equals(senderPhone) && userConn_2.get_f_Phone().equals(toPhone)) {
        logger.log(Level.INFO, "Response sent Connection found." + userConn_2.get_m_Name() + userConn_2.get_f_Name());
        userConn_2.set_conn_id(inv_resp_flag);
        userConnectionsDao.update(userConn_2);
      }
    }
  }
}

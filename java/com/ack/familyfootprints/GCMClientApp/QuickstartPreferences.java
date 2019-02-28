package com.ack.familyfootprints.GCMClientApp;

/**
 * Created by Lucky Goyal on 1/7/2016.
 */
public class QuickstartPreferences {

    public static final String SENT_TOKEN_TO_SERVER_E = "sentTokenToServere";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    //public static final String PROPERTY_APP_VERSION = 1;
    public static final String REG_ID_F = "reg_id_f";
    public static final String CONTACT_NAME = "contactName";
    public static final String MOBILE_PHONE = "mobilePhone";
    public static final String SHARE_PERMISSION = "sharePermissionFalse";
    public static final String PROFILE_IND = "profileIndicator";
    public static final String PROFILE_NAME = "profileName";
    public static final String PROFILE_PHONE = "profilePhone";

    //public static final String REG_ID_F = ;

    String PACKAGE = "com.ack.luckygoyal.com.ack.traveller.ack";
    // actions for server interaction
    String ACTION_REGISTER = PACKAGE + ".REGISTER";
    String ACTION_UNREGISTER = PACKAGE + ".UNREGISTER";
    String ACTION_ECHO = PACKAGE + ".ECHO";

    // action for notification intent
    String NOTIFICATION_ACTION = PACKAGE + ".NOTIFICATION";

    String DEFAULT_USER = "fakeUser";

    enum EventbusMessageType {
        REGISTRATION_FAILED, REGISTRATION_SUCCEEDED, UNREGISTRATION_SUCCEEDED, UNREGISTRATION_FAILED;
    }

    enum State {
        REGISTERED, UNREGISTERED;
    }


}

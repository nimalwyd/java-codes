package com.anaiglobal.valetroid.support;

public class Constants
{
    public static final long SECOND = 1000;
    public static final long MINUTE = SECOND * 60;
    public static final long HOUR = MINUTE * 60;

    public static final String BROADCAST_CLOUD_OPERATIONAL = "com.anaiglobal.valetroid.CloudIsOperationalListener";
    public static final String BROADCAST_CLOUD_CMD_DONE = "com.anaiglobal.valetroid.CloudIsCommandDoneListener";
    public static final String BROADCAST_SESSION_CLOSED = "com.anaiglobal.valetroid.SessionClosed";

    public static final int IDLE_TOO_LONG = 1;
    public static final int SESSION_TOO_LONG = 2;

    public static final String LOGIN = "Login";
    public static final String LOGOUT = "Logout";
    public static final String IDLE = "Idle";
    public static final String SESSION = "Session";

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";
    public static final String LOCATION_PROVIDER_CHANGED = "LocationProviderChanged";

    // Request codes
    public static final int REQUEST_CODE_CLOUDINFO = 1;
    public static final int REQUEST_PRINT_SETUP = 2;
    public static final int REQUEST_CONNECT_PRINTER = 3;
    public static final int REQUEST_VALET_CREATE = 4;
    public static final int REQUEST_VALET_LIST = 5;
    public static final int REQUEST_NEW_VALET = 6;
    public static final int REQUEST_PREVIEW_VALET = 7;
    public static final int REQUEST_CODE_PATROLLERLOG = 8;
    public static final int REQUEST_VALET_MENU = 9;
    public static final int REQUEST_LOCATION_SETTINGS = 12;
    public static final int REQUEST_ADD_PERSON_TO_VALET = 13;
    public static final int REQUEST_ADD_DAMAGE_TO_VALET = 14;
    public static final int REQUEST_SHOW_PROPERTIES = 15;

    // Result codes
    public static final int RESULT_PRINTER_CONNECTED = 2;
    public static final int RESULT_LOGOUT = 3;
    public static final int RESULT_DISCARD = 4;
    public static final int RESULT_NOT_OK = 5;
}

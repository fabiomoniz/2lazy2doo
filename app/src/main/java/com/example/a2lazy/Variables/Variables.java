package com.example.a2lazy.Variables;

public class Variables {
    // static variable single_instance of type Variables
    private static Variables single_instance = null;

    public String EMAIL_INFO;
    public String PASSWORD_INFO;
    public int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE;
    public static String LOGTAG;
    public static String PICTURE_URI;

    private Variables()
    {
        LOGTAG = "LOGTAG";
        EMAIL_INFO = "EmailInfo";
        PASSWORD_INFO = "PasswordInfo";
        CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
        PICTURE_URI = "pictureUri";
    }

    // static method to create instance of Singleton class
    public static Variables getInstance()
    {
        if (single_instance == null)
            single_instance = new Variables();

        return single_instance;
    }
}
package com.michael.apps.sportshi.model;

/**
 * Created by Michael on 02/02/2016.
 */
public class Config {
    public static final String BASE_URL = "http://192.168.98.50/sportshi/server/";
    public static final String LOGIN_URL = BASE_URL + "login_user";
    public static final String REGISTER_URL = BASE_URL + "register_user";
    public static final String UPDATE_PROFILE_URL = BASE_URL + "change_profile";
    public static final String UPDATE_PASSWORD_URL = BASE_URL + "change_password";
    public static final String RESET_URL = BASE_URL + "reset_password";
    public static final String INFO_URL = BASE_URL + "getDataUser";
    public static final String CATEGORY_URL = BASE_URL + "getCategory";
    public static final String MOMENT_URL = BASE_URL + "postMoment";
    public static final String EVENT_URL = BASE_URL + "postEvent";
    public static final String GET_DATA_URL = BASE_URL + "get_data";
    public static final String URL_FEED = BASE_URL + "get_TimelineData";

    //Keys for email and password as defined in our $_POST['key'] in login.php
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_DATE_OF_BIRTH = "tanggalLahir";
    public static final String KEY_OLD_PASSWORD = "old_password";
    public static final String KEY_NEW_PASSWORD = "new_password";
    public static final String KEY_NAME = "nama";
    public static final String KEY_GENDER = "jenisKelamin";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_IMAGE = "foto";
    public static final String KEY_IMAGE_NAME = "waktu";
    public static final String KEY_STATUS = "status";
    public static final String KEY_DESCRIPTION = "deskripsi";
    public static final String KEY_TAG_FRIEND_USERNAME = "username_tag";
    public static final String KEY_TAG_LOCATION = "location";
    public static final String KEY_TAG_CATEGORY = "kategori";

    //If server response is equal to this that means login is successful
    public static final String LOGIN_SUCCESS = "success";

    //Keys for Sharedpreferences
    //This would be the name of our shared preferences
    public static final String SHARED_PREF_NAME = "myloginapp";

    //This would be used to store the username of current logged in user
    public static final String USERNAME_SHARED_PREF = "username";
    public static final String NAME_SHARED_PREF = "nama";
    public static final String GENDER_SHARED_PREF = "jenisKelamin";
    public static final String DATE_OF_BIRTH_SHARED_PREF = "tanggalLahir";
    public static final String EMAIL_SHARED_PREF = "email";
    public static final String IMAGE_SHARED_PREF = "foto";

    //We will use this to store the boolean in sharedpreference to track user is loggedin or not
    public static final String LOGGEDIN_SHARED_PREF = "loggedin";
}
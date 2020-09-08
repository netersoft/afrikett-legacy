package com.neteru.afrikett.core.utilities;

import android.os.Environment;

import com.neteru.afrikett.R;

@SuppressWarnings("unused, WeakerAccess")
public final class Constants {

    public final static String DATABASE_ROOT = "ROOT-NODE";

    public final static int RANDOM_VALUE = 4320;

    public final static int SHORT_DELAY = 750;
    public final static int MEDIUM_DELAY = 1500;
    public final static int LONG_DELAY = 3000;

    public final static String MAIL_RGX = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

    public final static String START = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 $!?@*/:#";
    public final static String ARRIVAL = "4L$YNTqCtODRdHVQ7h9Iop:/WcKs5@uJ8eSiABaEfM+Xk0g6r?j1*v2yPw#lGnzU3m!ZFxb";

    public final static String LAUNCH_PREFS = "LAUNCH_PREFS";
    public final static String INFO_PREFS = "INFO_PREFS";
    public final static String USER_PREFS = "USER_PREFS";
    public final static String MESSENGER_PREFS = "MESSENGER_PREFS";
    public final static String MESSENGER_UPLOAD_PREFS = "MESSENGER_UPLOAD_PREFS";
    public final static String MESSENGER_DOWNLOAD_PREFS = "MESSENGER_DOWNLOAD_PREFS";

    public final static int[] COLORS = new int[]{
            R.color.dimgray,
            R.color.red,
            R.color.black,
            R.color.blue,
            R.color.darkorange,
            R.color.gray,
            R.color.green,
            R.color.skyblue,
            R.color.darkgray,
            R.color.darkgreen,
            R.color.darkpink
    };

    public final static int REQUEST_WRITE_PERMISSION = 2;
    public final static int REQUEST_READ_PERMISSION = 1;
    public final static int GALLERY = 1;
    public final static int CAMERA = 2;
    public final static int RESULT_CANCELED = 0;

    public final static String PICTURE_DOWNLOAD_DIRECTORY = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/Afrikett";
    public final static String MEDIA_DIRECTORY = Environment.getExternalStorageDirectory().getPath() + "/Afrikett/Media";
    public final static String IMAGE_DIRECTORY = MEDIA_DIRECTORY + "/Afrikett Images";
    public final static String VIDEO_DIRECTORY = MEDIA_DIRECTORY + "/Afrikett Videos";
    public final static String DOCUMENT_DIRECTORY = MEDIA_DIRECTORY + "/Afrikett Documents";
    public final static String AUDIO_DIRECTORY = MEDIA_DIRECTORY + "/Afrikett Audios";
    public final static String SENT_IMAGE_DIRECTORY = IMAGE_DIRECTORY + "/Sent";
    public final static String SENT_VIDEO_DIRECTORY = VIDEO_DIRECTORY + "/Sent";
    public final static String SENT_AUDIO_DIRECTORY = AUDIO_DIRECTORY + "/Sent";
    public final static String SENT_DOCUMENT_DIRECTORY = DOCUMENT_DIRECTORY + "/Sent";
    public final static String SENT_VIDEO_THUMBNAIL_DIRECTORY = SENT_VIDEO_DIRECTORY + "/.Thumbs";
    public final static String STORIES_DIRECTORY = MEDIA_DIRECTORY + "/.Stories";
    public final static String IMAGE_EDITOR_DIRECTORY = MEDIA_DIRECTORY + "/.Editor";
    public final static String POSTS_DIRECTORY = MEDIA_DIRECTORY + "/.Posts";

    public final static String DEFAULT = "default";
    public final static String PHONE = "phone";
    public final static String EMAIL = "email";
    public final static String EMPTY = "";

    public final static String[] LETTER_RES = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};

    public final static int[] NUMBER_RES = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};

    public final static int TEXT_MESSAGE = 0;
    public final static int DOCUMENT_MESSAGE = 1;
    public final static int IMAGE_MESSAGE = 2;
    public final static int AUDIO_MESSAGE = 3;
    public final static int VIDEO_MESSAGE = 4;
    public final static int CONTACT_MESSAGE = 5;
    public final static int DATE_MESSAGE = 6;

    public final static String[] DOCUMENT_MIME_TYPES =
                            {"application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                             "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                             "application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                             "text/plain",
                             "application/pdf",
                             "application/zip"};

    public final static String DATE_PATTERN = "E dd.MM.yyyy '-' HH:mm";

    public final static int MESSAGE_LOAD = 0;
    public final static int MESSAGE_SEND = 1;
    public final static int MESSAGE_READ = 2;

    public final static int LINK_URL = 8;
    public final static int LINK_PHONE = 4;
    public final static int LINK_EMAIL = 16;
    public final static int LINK_HASHTAG = 1;

    public final static int UNSPECIFIED = 0;
    public final static int MALE = 1;
    public final static int FEMALE = 2;

    public final static int AGRO = -8;
    public final static int INFORMATION_SCIENCE = -7;
    public final static int FASHION_AND_CLOTHING = -6;
    public final static int COMMUNICATION = -5;
    public final static int ENTERTAINMENT = -4;
    public final static int EDUCATION_AND_TRAINING = -3;
    public final static int FINANCE_AND_BANKING = -2;
    public final static int TRANSPORTS_AND_LOGISTICS = -1;
    public final static int TRADE_AND_DISTRIBUTION = 1;
    public final static int PUBLIC_SERVICES = 2;
    public final static int HOTEL_BUSINESS = 3;
    public final static int HEALTH = 4;
    public final static int PROFESSIONAL_SERVICES = 5;
    public final static int RETAIL_SALE = 6;
    public final static int CATERING = 7;
    public final static int OTHER = 8;

    public final static int SINGLE = 0;
    public final static int MULTI = 1;

    public final static int PRODUCT_AND_SERVICE = 0;
    public final static int EVENT = 1;
    public final static int NEWS = 2;

    public final static int USER = 0;
    public final static int SHOWCASE = 1;

    public final static int USER_REPORT = 0;
    public final static int POST_REPORT = 1;
    public final static int SHOWCASE_REPORT = 2;
}

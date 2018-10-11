package com.rkovaliov.bu.resources;

public class VkApiResources {

    private static final String API_VERSION = "5.85";
    public static final String REDIRECT_URL = "https://oauth.vk.com/blank.html";
    public static final int APP_ID = 6710144;
    public static final String CLIENT_SECRET = "4u7UnPOvNn64BiLVt52z";

    public static final String AUTH_URL = "https://oauth.vk.com/authorize"
            + "?client_id=" + APP_ID
            + "&redirect_uri=" + REDIRECT_URL
            + "&v=" + API_VERSION
            + "&display=page"
            + "&response_type=code";
}

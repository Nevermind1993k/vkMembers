package com.rkovaliov.bu.services.interfaces;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;

public interface VkService {

    void loadMembers(String code) throws ClientException, ApiException, InterruptedException;

    void convertToUsers(String code) throws ClientException, ApiException, InterruptedException;

    void calculateLikes(String code) throws ClientException, ApiException, InterruptedException;
}

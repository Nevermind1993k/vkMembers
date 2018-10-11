package com.rkovaliov.bu.controllers;

import com.rkovaliov.bu.resources.VkApiResources;
import com.rkovaliov.bu.services.interfaces.VkService;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class VkController {

    private static final String LOAD_MEMBERS_URL = "/loadMembers";
    private static final String CONVERT_TO_USERS_URL = "/convertToMembers";
    private static final String CALCULATE_LIKES_URL = "/calculateLikes";
    private static final String INSTRUCTIONS_URL = "/instructions";

    private final VkService vkService;

    @Autowired
    public VkController(VkService vkService) {
        this.vkService = vkService;
    }

    @GetMapping(value = INSTRUCTIONS_URL, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Object> showInstructions() {
        String result = "Instructions:\n " +
                "1.Run any VPN program.\n" +
                "2.To manipulate with vkMembers service you need to provide code parameter issued by VK API. Follow by this link in your browser to receive code: \n" + VkApiResources.AUTH_URL + "\n " +
                "3.Copy the \"code\" parameter. It will work for 1 hour.\n" +
                "4.Follow this link in your web client (postman, etc) to load members from group : http://localhost:8080/loadMembers?code={code}\n" +
                "5.Follow this link in your web client to convert received id's from step 4 to members : http://localhost:8080/convertToMembers?code={code}\n" +
                "6.Follow this link in your web client to calculate likes in group for users from step 5 : http://localhost:8080/calculateLikes?code={code}";

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping(value = LOAD_MEMBERS_URL)
    public ResponseEntity<Object> loadMembersFromGroup(@RequestParam("code") String code) throws ClientException, ApiException, InterruptedException {
        vkService.loadMembers(code);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = CONVERT_TO_USERS_URL)
    public ResponseEntity<Object> convertToUsers(@RequestParam("code") String code) throws ClientException, ApiException, InterruptedException {
        vkService.convertToUsers(code);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = CALCULATE_LIKES_URL)
    public ResponseEntity<Object> calculateLikes(@RequestParam("code") String code) throws ClientException, ApiException, InterruptedException {
        vkService.calculateLikes(code);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

package com.rkovaliov.bu.services.impl;

import com.rkovaliov.bu.entities.User;
import com.rkovaliov.bu.repositories.UserDAO;
import com.rkovaliov.bu.resources.Sex;
import com.rkovaliov.bu.services.interfaces.VkService;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.UserAuthResponse;
import com.vk.api.sdk.objects.groups.responses.GetMembersResponse;
import com.vk.api.sdk.objects.utils.DomainResolved;
import com.vk.api.sdk.objects.wall.responses.GetResponse;
import com.vk.api.sdk.queries.likes.LikesType;
import com.vk.api.sdk.queries.users.UserField;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.rkovaliov.bu.resources.Resources.groupNameStatic;
import static com.rkovaliov.bu.resources.VkApiResources.*;


@Service
public class VkServiceImpl implements VkService {

    private final UserDAO userDAO;

    private static final Logger LOG = LoggerFactory.getLogger(VkServiceImpl.class);
    private static final int POSTS_PER_REQUEST = 100; //100 posts is maximum value for 1 request
    private static final int LIKES_PER_REQUEST = 1000; //1000 likes is maximum value for 1 request

    private static int offsetForUserIds = -1;
    private static int offsetForPostIds = -1;
    private static int offsetForUsers = 0;
    private static boolean isOutsideOfBound = false;

    private VkApiClient vk = getVkApiClient();

    public VkServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    @Transactional
    public void loadMembers(String code) throws ClientException, ApiException, InterruptedException {
        UserActor actor = getUserActor(getUserAuthResponse(vk, code));

        List<String> userIdsList = getUserIds(groupNameStatic, actor);
        userIdsList.forEach(id -> userDAO.save(new User(Long.valueOf(id))));
    }

    @Override
    @Transactional
    public void convertToUsers(String code) throws ClientException, ApiException, InterruptedException {
        UserActor actor = getUserActor(getUserAuthResponse(vk, code));

        List<User> usersWithIdsList = userDAO.findAll();
        List<User> userList; //users with info
        List<String> IdsList = convertFromUsersToUsersIdsList(usersWithIdsList);
        userList = getUsersFromIds(IdsList, actor);
        userList.forEach(userDAO::save);
    }

    @Override
    public void calculateLikes(String code) throws ClientException, ApiException, InterruptedException {
        UserActor actor = getUserActor(getUserAuthResponse(vk, code));

        List<User> userList = userDAO.findAll();
        List<User> userListWithLikesCount = getUsersWithLikesCount(userList, groupNameStatic, actor);
        userListWithLikesCount.forEach(userDAO::save);
    }

    private List<String> convertFromUsersToUsersIdsList(List<User> usersWithIdsList) {
        List<String> toReturn = new ArrayList<>();
        usersWithIdsList.forEach(user -> toReturn.add(String.valueOf(user.getId())));
        return toReturn;
    }

    private List<User> getUsersWithLikesCount(List<User> userList, String groupName, UserActor actor) throws ClientException, ApiException, InterruptedException {
//        List<Integer> usersThatPutLike = new ArrayList<>();
        List<Integer> postsIds = getPostsIds(groupName, actor);
        int groupId = getGroupId(groupName, actor);

        int repeatTimes = (int) Math.ceil(postsIds.size() / 25.0); //we can check 25 posts per request
        int postIdIndex = 0;
        for (int i = 0; i < 4; i++) { //100 last posts for better speed (can be replaced for repeatTimes value for iterate through all posts but can take more time)
            JSONArray response = new JSONArray(vk.execute().batch(actor,
                    vk.likes().getList(actor, LikesType.POST).ownerId(-groupId).itemId(postsIds.get(postIdIndex++)).count(LIKES_PER_REQUEST),
                    vk.likes().getList(actor, LikesType.POST).ownerId(-groupId).itemId(postsIds.get(postIdIndex++)).count(LIKES_PER_REQUEST),
                    vk.likes().getList(actor, LikesType.POST).ownerId(-groupId).itemId(postsIds.get(postIdIndex++)).count(LIKES_PER_REQUEST),
                    vk.likes().getList(actor, LikesType.POST).ownerId(-groupId).itemId(postsIds.get(postIdIndex++)).count(LIKES_PER_REQUEST),
                    vk.likes().getList(actor, LikesType.POST).ownerId(-groupId).itemId(postsIds.get(postIdIndex++)).count(LIKES_PER_REQUEST),
                    vk.likes().getList(actor, LikesType.POST).ownerId(-groupId).itemId(postsIds.get(postIdIndex++)).count(LIKES_PER_REQUEST),
                    vk.likes().getList(actor, LikesType.POST).ownerId(-groupId).itemId(postsIds.get(postIdIndex++)).count(LIKES_PER_REQUEST),
                    vk.likes().getList(actor, LikesType.POST).ownerId(-groupId).itemId(postsIds.get(postIdIndex++)).count(LIKES_PER_REQUEST),
                    vk.likes().getList(actor, LikesType.POST).ownerId(-groupId).itemId(postsIds.get(postIdIndex++)).count(LIKES_PER_REQUEST),
                    vk.likes().getList(actor, LikesType.POST).ownerId(-groupId).itemId(postsIds.get(postIdIndex++)).count(LIKES_PER_REQUEST),
                    vk.likes().getList(actor, LikesType.POST).ownerId(-groupId).itemId(postsIds.get(postIdIndex++)).count(LIKES_PER_REQUEST),
                    vk.likes().getList(actor, LikesType.POST).ownerId(-groupId).itemId(postsIds.get(postIdIndex++)).count(LIKES_PER_REQUEST),
                    vk.likes().getList(actor, LikesType.POST).ownerId(-groupId).itemId(postsIds.get(postIdIndex++)).count(LIKES_PER_REQUEST),
                    vk.likes().getList(actor, LikesType.POST).ownerId(-groupId).itemId(postsIds.get(postIdIndex++)).count(LIKES_PER_REQUEST),
                    vk.likes().getList(actor, LikesType.POST).ownerId(-groupId).itemId(postsIds.get(postIdIndex++)).count(LIKES_PER_REQUEST),
                    vk.likes().getList(actor, LikesType.POST).ownerId(-groupId).itemId(postsIds.get(postIdIndex++)).count(LIKES_PER_REQUEST),
                    vk.likes().getList(actor, LikesType.POST).ownerId(-groupId).itemId(postsIds.get(postIdIndex++)).count(LIKES_PER_REQUEST),
                    vk.likes().getList(actor, LikesType.POST).ownerId(-groupId).itemId(postsIds.get(postIdIndex++)).count(LIKES_PER_REQUEST),
                    vk.likes().getList(actor, LikesType.POST).ownerId(-groupId).itemId(postsIds.get(postIdIndex++)).count(LIKES_PER_REQUEST),
                    vk.likes().getList(actor, LikesType.POST).ownerId(-groupId).itemId(postsIds.get(postIdIndex++)).count(LIKES_PER_REQUEST),
                    vk.likes().getList(actor, LikesType.POST).ownerId(-groupId).itemId(postsIds.get(postIdIndex++)).count(LIKES_PER_REQUEST),
                    vk.likes().getList(actor, LikesType.POST).ownerId(-groupId).itemId(postsIds.get(postIdIndex++)).count(LIKES_PER_REQUEST),
                    vk.likes().getList(actor, LikesType.POST).ownerId(-groupId).itemId(postsIds.get(postIdIndex++)).count(LIKES_PER_REQUEST),
                    vk.likes().getList(actor, LikesType.POST).ownerId(-groupId).itemId(postsIds.get(postIdIndex++)).count(LIKES_PER_REQUEST),
                    vk.likes().getList(actor, LikesType.POST).ownerId(-groupId).itemId(postsIds.get(postIdIndex++)).count(LIKES_PER_REQUEST)).execute().toString());

            Thread.sleep(200); //5 requests in 1 sec

            response.forEach(o -> {
                JSONObject object = new JSONObject(o.toString());
                JSONArray items = object.getJSONArray("items");
                List<String> usersThatPutLike = new ArrayList<>(); //1000 ids
                items.forEach(userId -> usersThatPutLike.add(userId.toString()));
                userList.forEach(user -> {
                    long count = usersThatPutLike.stream().filter(String.valueOf(user.getId())::equals).count();
                    if (count > 0) {
                        user.setLikeCount(user.getLikeCount() + count);
                        userList.set(userList.indexOf(user), user);
                    }
                });
            });
        }
        LOG.info("Likes received from group: " + groupName);
        return userList;
    }

   /* private List<User> compareLists(List<User> userList, List<Integer> usersThatPutLike) {
        List<User> toReturn = new ArrayList<>();

        List<String> usersThatPutLikeStr = new ArrayList<>();
        usersThatPutLike.forEach(id -> usersThatPutLikeStr.add(id.toString()));

        userList.forEach(user -> {
            long count = usersThatPutLikeStr.stream().filter(String.valueOf(user.getId())::equals).count();
            user.setLikeCount(count);
            toReturn.add(user);
        });

        *//*for (User user : userList) {
            long count = usersThatPutLikeStr.stream().filter(String.valueOf(user.getId())::equals).count();
            user.setLikeCount(count);
            toReturn.add(user);
        }*//*
        System.out.println("123");
        return toReturn;
    }*/

    private int getGroupId(String groupName, UserActor actor) throws ClientException, ApiException {
        DomainResolved execute = vk.utils().resolveScreenName(actor, groupName).execute();
        return execute.getObjectId();
    }

    private List<Integer> getPostsIds(String groupName, UserActor actor) throws ClientException, ApiException, InterruptedException {
        List<Integer> result = new ArrayList<>();

        int postsCount = getPostsCount(groupName, actor);
        int repeatTimes = (int) Math.ceil(postsCount / 2500.0); //because we can get 2500 posts in one request (25 execute requests is max)

        for (int i = 0; i < repeatTimes; i++) {
            JSONArray response = new JSONArray(vk.execute().batch(actor,
                    vk.wall().get(actor).domain(groupName).count(POSTS_PER_REQUEST).offset(incrementPostIdOffset()),
                    vk.wall().get(actor).domain(groupName).count(POSTS_PER_REQUEST).offset(incrementPostIdOffset()),
                    vk.wall().get(actor).domain(groupName).count(POSTS_PER_REQUEST).offset(incrementPostIdOffset()),
                    vk.wall().get(actor).domain(groupName).count(POSTS_PER_REQUEST).offset(incrementPostIdOffset()),
                    vk.wall().get(actor).domain(groupName).count(POSTS_PER_REQUEST).offset(incrementPostIdOffset()),
                    vk.wall().get(actor).domain(groupName).count(POSTS_PER_REQUEST).offset(incrementPostIdOffset()),
                    vk.wall().get(actor).domain(groupName).count(POSTS_PER_REQUEST).offset(incrementPostIdOffset()),
                    vk.wall().get(actor).domain(groupName).count(POSTS_PER_REQUEST).offset(incrementPostIdOffset()),
                    vk.wall().get(actor).domain(groupName).count(POSTS_PER_REQUEST).offset(incrementPostIdOffset()),
                    vk.wall().get(actor).domain(groupName).count(POSTS_PER_REQUEST).offset(incrementPostIdOffset()),
                    vk.wall().get(actor).domain(groupName).count(POSTS_PER_REQUEST).offset(incrementPostIdOffset()),
                    vk.wall().get(actor).domain(groupName).count(POSTS_PER_REQUEST).offset(incrementPostIdOffset()),
                    vk.wall().get(actor).domain(groupName).count(POSTS_PER_REQUEST).offset(incrementPostIdOffset()),
                    vk.wall().get(actor).domain(groupName).count(POSTS_PER_REQUEST).offset(incrementPostIdOffset()),
                    vk.wall().get(actor).domain(groupName).count(POSTS_PER_REQUEST).offset(incrementPostIdOffset()),
                    vk.wall().get(actor).domain(groupName).count(POSTS_PER_REQUEST).offset(incrementPostIdOffset()),
                    vk.wall().get(actor).domain(groupName).count(POSTS_PER_REQUEST).offset(incrementPostIdOffset()),
                    vk.wall().get(actor).domain(groupName).count(POSTS_PER_REQUEST).offset(incrementPostIdOffset()),
                    vk.wall().get(actor).domain(groupName).count(POSTS_PER_REQUEST).offset(incrementPostIdOffset()),
                    vk.wall().get(actor).domain(groupName).count(POSTS_PER_REQUEST).offset(incrementPostIdOffset()),
                    vk.wall().get(actor).domain(groupName).count(POSTS_PER_REQUEST).offset(incrementPostIdOffset()),
                    vk.wall().get(actor).domain(groupName).count(POSTS_PER_REQUEST).offset(incrementPostIdOffset()),
                    vk.wall().get(actor).domain(groupName).count(POSTS_PER_REQUEST).offset(incrementPostIdOffset()),
                    vk.wall().get(actor).domain(groupName).count(POSTS_PER_REQUEST).offset(incrementPostIdOffset()),
                    vk.wall().get(actor).domain(groupName).count(POSTS_PER_REQUEST).offset(incrementPostIdOffset())).execute().toString());

            Thread.sleep(200); //5 requests in 1 sec

            response.forEach(o -> {
                JSONObject obj = new JSONObject(o.toString());
                JSONArray items = obj.getJSONArray("items");
                items.forEach(post -> {
                    JSONObject postObj = new JSONObject(post.toString());
                    result.add(postObj.getInt("id"));
                });
            });
        }

        LOG.info(result.size() + " Posts received from group: " + groupName);
        return result;
    }

    private List<User> getUsersFromIds(List<String> userIdsList, UserActor actor) throws ClientException, ApiException, InterruptedException {
        List<User> result = new ArrayList<>();

        int usersInGroup = userIdsList.size();
        int repeatTimes = (int) Math.ceil(usersInGroup / 25000.0); //because we can get 25000 users info per request
        for (int i = 0; i < repeatTimes; i++) {
            JSONArray response = new JSONArray(vk.execute().batch(actor,
                    vk.users().get(actor).userIds(getNextThousandUserIds(userIdsList)).fields(UserField.SEX),
                    vk.users().get(actor).userIds(getNextThousandUserIds(userIdsList)).fields(UserField.SEX),
                    vk.users().get(actor).userIds(getNextThousandUserIds(userIdsList)).fields(UserField.SEX),
                    vk.users().get(actor).userIds(getNextThousandUserIds(userIdsList)).fields(UserField.SEX),
                    vk.users().get(actor).userIds(getNextThousandUserIds(userIdsList)).fields(UserField.SEX),
                    vk.users().get(actor).userIds(getNextThousandUserIds(userIdsList)).fields(UserField.SEX),
                    vk.users().get(actor).userIds(getNextThousandUserIds(userIdsList)).fields(UserField.SEX),
                    vk.users().get(actor).userIds(getNextThousandUserIds(userIdsList)).fields(UserField.SEX),
                    vk.users().get(actor).userIds(getNextThousandUserIds(userIdsList)).fields(UserField.SEX),
                    vk.users().get(actor).userIds(getNextThousandUserIds(userIdsList)).fields(UserField.SEX),
                    vk.users().get(actor).userIds(getNextThousandUserIds(userIdsList)).fields(UserField.SEX),
                    vk.users().get(actor).userIds(getNextThousandUserIds(userIdsList)).fields(UserField.SEX),
                    vk.users().get(actor).userIds(getNextThousandUserIds(userIdsList)).fields(UserField.SEX),
                    vk.users().get(actor).userIds(getNextThousandUserIds(userIdsList)).fields(UserField.SEX),
                    vk.users().get(actor).userIds(getNextThousandUserIds(userIdsList)).fields(UserField.SEX),
                    vk.users().get(actor).userIds(getNextThousandUserIds(userIdsList)).fields(UserField.SEX),
                    vk.users().get(actor).userIds(getNextThousandUserIds(userIdsList)).fields(UserField.SEX),
                    vk.users().get(actor).userIds(getNextThousandUserIds(userIdsList)).fields(UserField.SEX),
                    vk.users().get(actor).userIds(getNextThousandUserIds(userIdsList)).fields(UserField.SEX),
                    vk.users().get(actor).userIds(getNextThousandUserIds(userIdsList)).fields(UserField.SEX),
                    vk.users().get(actor).userIds(getNextThousandUserIds(userIdsList)).fields(UserField.SEX),
                    vk.users().get(actor).userIds(getNextThousandUserIds(userIdsList)).fields(UserField.SEX),
                    vk.users().get(actor).userIds(getNextThousandUserIds(userIdsList)).fields(UserField.SEX),
                    vk.users().get(actor).userIds(getNextThousandUserIds(userIdsList)).fields(UserField.SEX),
                    vk.users().get(actor).userIds(getNextThousandUserIds(userIdsList)).fields(UserField.SEX)).execute().toString());

            Thread.sleep(200); //5 requests in 1 sec

            response.forEach(thousandUsers -> {
                JSONArray array = new JSONArray(thousandUsers.toString());
                array.forEach(o -> {
                    JSONObject obj = new JSONObject(o.toString());
                    Sex sex = obj.getInt("sex") == 1 ? Sex.FEMALE : Sex.MALE;
                    User user = new User(obj.getInt("id"), obj.getString("first_name"), obj.getString("last_name"), sex);
                    result.add(user);
                });
            });

        }
        //com.vk.api.sdk.exceptions.ApiAuthException: User authorization failed (5): User authorization failed: no access_token passed.
        return result;
    }

    private static List<String> getNextThousandUserIds(List<String> userIdsList) {
        List<String> toReturn;

        if (isOutsideOfBound) {
            return new ArrayList<>();
        }
        if (!(offsetForUsers + 1000 > userIdsList.size())) {
            toReturn = userIdsList.subList(offsetForUsers, offsetForUsers + 1000);
            offsetForUsers += 1000;
            return toReturn;
        } else {
            toReturn = userIdsList.subList(offsetForUsers, userIdsList.size());
            isOutsideOfBound = true;
            return toReturn;
        }
    }

    private List<String> getUserIds(String groupName, UserActor actor) throws ClientException, InterruptedException {
        List<String> result = new ArrayList<>();

        try {
            int membersCount = getMembersCount(groupName, actor);
            int repeatTimes = (int) Math.ceil(membersCount / 25000.0); //because we can get 25000 users per one request (25 execute requests is max)
            for (int i = 0; i < repeatTimes; i++) {
                JSONArray response = new JSONArray(vk.execute().batch(actor,
                        vk.groups().getMembers(actor).groupId(groupName).offset(incrementUserIdOffset()),
                        vk.groups().getMembers(actor).groupId(groupName).offset(incrementUserIdOffset()),
                        vk.groups().getMembers(actor).groupId(groupName).offset(incrementUserIdOffset()),
                        vk.groups().getMembers(actor).groupId(groupName).offset(incrementUserIdOffset()),
                        vk.groups().getMembers(actor).groupId(groupName).offset(incrementUserIdOffset()),
                        vk.groups().getMembers(actor).groupId(groupName).offset(incrementUserIdOffset()),
                        vk.groups().getMembers(actor).groupId(groupName).offset(incrementUserIdOffset()),
                        vk.groups().getMembers(actor).groupId(groupName).offset(incrementUserIdOffset()),
                        vk.groups().getMembers(actor).groupId(groupName).offset(incrementUserIdOffset()),
                        vk.groups().getMembers(actor).groupId(groupName).offset(incrementUserIdOffset()),
                        vk.groups().getMembers(actor).groupId(groupName).offset(incrementUserIdOffset()),
                        vk.groups().getMembers(actor).groupId(groupName).offset(incrementUserIdOffset()),
                        vk.groups().getMembers(actor).groupId(groupName).offset(incrementUserIdOffset()),
                        vk.groups().getMembers(actor).groupId(groupName).offset(incrementUserIdOffset()),
                        vk.groups().getMembers(actor).groupId(groupName).offset(incrementUserIdOffset()),
                        vk.groups().getMembers(actor).groupId(groupName).offset(incrementUserIdOffset()),
                        vk.groups().getMembers(actor).groupId(groupName).offset(incrementUserIdOffset()),
                        vk.groups().getMembers(actor).groupId(groupName).offset(incrementUserIdOffset()),
                        vk.groups().getMembers(actor).groupId(groupName).offset(incrementUserIdOffset()),
                        vk.groups().getMembers(actor).groupId(groupName).offset(incrementUserIdOffset()),
                        vk.groups().getMembers(actor).groupId(groupName).offset(incrementUserIdOffset()),
                        vk.groups().getMembers(actor).groupId(groupName).offset(incrementUserIdOffset()),
                        vk.groups().getMembers(actor).groupId(groupName).offset(incrementUserIdOffset()),
                        vk.groups().getMembers(actor).groupId(groupName).offset(incrementUserIdOffset()),
                        vk.groups().getMembers(actor).groupId(groupName).offset(incrementUserIdOffset())).execute().toString());

                Thread.sleep(200); //5 requests in 1 sec

                response.forEach(o -> {
                    JSONObject obj = new JSONObject(o.toString());
                    JSONArray items = obj.getJSONArray("items");
                    items.forEach(id -> result.add(id.toString()));
                });
            }
        } catch (ApiException e) {
            LOG.error("While getting user ids got ApiException.", e);
            Thread.sleep(1000);
        }

        LOG.info(result.size() + " Users Ids received from group: " + groupName);
        return result;
    }

    private int getMembersCount(String groupName, UserActor actor) throws ClientException, ApiException {
        GetMembersResponse response = vk.groups().getMembers(actor).groupId(groupName).execute();
        return response.getCount();
    }

    private int getPostsCount(String groupName, UserActor actor) throws ClientException, ApiException {
        GetResponse response = vk.wall().get(actor).domain(groupName).execute();
        return response.getCount();
    }

    private VkApiClient getVkApiClient() {
        TransportClient transportClient = HttpTransportClient.getInstance();
        return new VkApiClient(transportClient);
    }

    private UserAuthResponse getUserAuthResponse(VkApiClient vk, String code) throws ClientException, ApiException {
        return vk.oauth().userAuthorizationCodeFlow(APP_ID, CLIENT_SECRET, REDIRECT_URL, code).execute();
    }

    private UserActor getUserActor(UserAuthResponse authResponse) {
        return new UserActor(authResponse.getUserId(), authResponse.getAccessToken());
    }

    private int incrementUserIdOffset() {
        if (offsetForUserIds < 0) {
            offsetForUserIds = 0;
            return offsetForUserIds;
        }
        offsetForUserIds += 1000;
        return offsetForUserIds;
    }

    private int incrementPostIdOffset() {
        if (offsetForPostIds < 0) {
            offsetForPostIds = 0;
            return offsetForPostIds;
        }
        offsetForPostIds += 100;
        return offsetForPostIds;
    }
}
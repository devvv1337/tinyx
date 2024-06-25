package com.tinyx.social;

import com.tinyx.models.Social;
import java.util.List;

public interface SocialService extends com.tinyx.base.SocialService {
    void performAction(Social social);
    List<String> getFollowers(String userId);
    List<String> getFollowing(String userId);
    List<String> getBlockedUsers(String userId);
    List<String> getBlockingUsers(String userId);
}

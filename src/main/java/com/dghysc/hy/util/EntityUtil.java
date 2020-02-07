package com.dghysc.hy.util;

import com.dghysc.hy.user.model.User;

import java.util.HashMap;
import java.util.Map;

public class EntityUtil {

    public static Map<String, Object> getCreateAndUpdateInfo(
            User creator, User updater
    ) {
        Map<String, Object> map = new HashMap<>();

        map.put("creatorId", creator.getId());
        map.put("creatorName", creator.getName());
        map.put("updaterId", updater.getId());
        map.put("updaterName", updater.getName());

        return map;
    }
}

package com.dghysc.hy.util;

import com.dghysc.hy.user.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EntityUtil {

    public static Map<String, Object> getCreateAndUpdateInfo(
            User creator, User updater
    ) {
        Map<String, Object> map = new HashMap<>();

        Optional.ofNullable(creator).ifPresentOrElse(user -> {
            map.put("creatorId", user.getId());
            Optional.ofNullable(user.getName()).ifPresentOrElse(
                    name -> map.put("creatorName", name),
                    () -> map.put("creatorName", "null")
            );
        }, () -> {
            map.put("creatorId", "null");
            map.put("creatorName", "null");
        });

        Optional.ofNullable(updater).ifPresentOrElse(user -> {
            map.put("updaterId", user.getId());
            Optional.ofNullable(user.getName()).ifPresentOrElse(
                    name -> map.put("updaterName", name),
                    () -> map.put("updaterName", "null")
            );
        }, () -> {
            map.put("updaterId", "null");
            map.put("updaterName", "null");
        });

        return map;
    }
}

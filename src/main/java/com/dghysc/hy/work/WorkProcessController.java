package com.dghysc.hy.work;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.work.model.WorkProcess;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Work Process Controller
 * @author lorry
 * @author lin864464995@163.com
 */
@Controller
@RequestMapping("/api/workProcess")
public class WorkProcessController {
    private final Log logger = LogFactory.getLog(this.getClass());

    private final WorkProcessService workProcessService;

    public WorkProcessController(WorkProcessService workProcessService) {
        this.workProcessService = workProcessService;
    }

    @ResponseBody
    @PostMapping("add")
    public JSONObject add(@RequestBody JSONObject request) {
        JSONObject response = new JSONObject();

        JSONArray array = request.getJSONArray("data");
        Object[] data = array.toArray();
        Map<Integer, WorkProcess> map = new HashMap<>();

        for (Object j : data) {
            JSONObject t = (JSONObject) j;
            map.put(t.getInteger("processId"),
                    new WorkProcess(null, null, t.getInteger("sequenceNumber"))
            );
        }
        if (workProcessService.addProcessesInWork(
                request.getInteger("workId"),
                map
        )) {
            response.put("status", 1);
            response.put("message", "Add Process in Work Success");
        } else {
            response.put("status", 0);
            response.put("message", "Add Process in Work Failed");
        }

        return response;
    }
}

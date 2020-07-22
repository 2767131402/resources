package com.zhenglei.utils.requestparam;

import cn.hutool.core.io.IoUtil;
import com.alibaba.fastjson.JSONObject;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 获取request中参数
 */
public class HttpServletRequestParams {
    /**
     * @Description: 获取request的参数
     */
    public static Map<String, Object> getParamMapObject(HttpServletRequest request) {
        Map<String, Object> paramMap = new HashMap<>();

        try {
            //1. 从request.getParameterMap中获取参数
            //Content-Type: text/plain;的情况
            Map<String, String[]> requestMap = request.getParameterMap();
            for (Map.Entry<String, String[]> entry : requestMap.entrySet()) {
                if (entry.getValue().length == 1) {
                    paramMap.put(entry.getKey(), entry.getValue()[0]);
                } else {
                    String[] values = entry.getValue();
                    String value = "";
                    for (String _val : values) {
                        value += _val + ",";
                    }
                    value = value.substring(0, value.length() - 1);
                    paramMap.put(entry.getKey(), value);
                }
            }

            //2. 从request.getReader()中 获取参数
            //Content-Type: application/json;的情况
            String read = IoUtil.read(request.getReader());
            Map<String, Object> map = JSONObject.parseObject(read, Map.class);

            if (map != null) {
                paramMap.putAll(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return paramMap;
    }

}

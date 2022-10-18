package com.iyyxx.token.utils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @className: WebUtils
 * @description: TODO 类描述
 * @author: eric 4575252@gmail.com
 * @date: 2022/10/17/0017 15:04:36
 **/
public class WebUtils {
    public static String renderString(HttpServletResponse response, String string){
        try {
            response.setStatus(200);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().print(string);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

package com.myweb.community.controller;

import com.myweb.community.util.CommunityUtil;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.http.HttpResponse;

@Controller
@RequestMapping("/alpha")
public class Alpha {

    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello() {
        return "Hello Spring Boot.";
    }


    /**
     * cookie 创建
     * @param httpServletResponse
     * @return
     */
    @RequestMapping(value = "/cookie/set",method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse httpServletResponse){
        //创建cookie
        Cookie cookie=new Cookie("code", CommunityUtil.getUUID());
        //设置cookie生效范围
        cookie.setPath("/Community/alpha");
        //设置生效时间
        cookie.setMaxAge(60*10);
        httpServletResponse.addCookie(cookie);

        return "setcookie";
    }

    /**
     * cookie 获取
     * @param code
     * @return
     */
     //   @RequestMapping(value = "/cookie/get",method = RequestMethod.GET)
//    @ResponseBody
//    public String getCookie(@CookieValue ("code") String code){
//        System.out.println("code");
//
//
//        return "getcookie";
//    }
    @RequestMapping(value = "/cookie/get", method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("code") String code) {
        System.out.println("code");
        return "getcookie";
    }

    @RequestMapping(value = "/session/set",method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpSession session){
        session.setAttribute("id",1);
        session.setAttribute("name",2);
        return "setsession";
    }

    @RequestMapping(value = "/session/get",method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session){
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "get session";
    }

}

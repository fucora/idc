package com.iwellmass.dispatcher.admin.web.interceptor;
//package com.dmall.dispatcher.admin.web.interceptor;
//
//import java.util.Date;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.apache.commons.lang.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.web.servlet.HandlerInterceptor;
//import org.springframework.web.servlet.ModelAndView;
//
//import com.wm.nb.domain.security.AesKey;
//import com.wm.nb.web.interceptor.LoginContext;
//
///**
// * Created by xkwu on 2016/5/20.
// */
//public class LoginInterceptor implements HandlerInterceptor {
//    private static final Logger LOGGER = LoggerFactory.getLogger(com.wm.nb.web.interceptor.LoginInterceptor.class);
//    @Resource
//    private AesKey aesKey;
//    private String erpDomain;
//    private String domain;
//    public LoginInterceptor() {
//    }
//
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object obj, Exception e) throws Exception {
//        LoginContext.remove();
//    }
//
//    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object obj, ModelAndView mav) throws Exception {
//    }
//
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object obj) throws Exception {
//        String url = request.getRequestURL().toString();
//        LOGGER.debug("look up:" + url);
//        if(StringUtils.isBlank(url)) {
//            url = this.erpDomain + "/main";
//        }
///*
//        String loginUrl = this.erpDomain + "/login";
//        String redirectUrl = loginUrl + "?returnUrl=" + domain;
//        LOGGER.debug("return url:" + redirectUrl);
//        String loginValue = CookieUtils.getCookieValue(request, CookieEnum.loginValue.getKey());
//        if(StringUtils.isBlank(loginValue)) {
//            LOGGER.error("用户未登录");
//            DataResult result = new DataResult();
//            result.addAttribute("redirectUrl",redirectUrl);
//            PrintWriter out = response.getWriter();
//            out.print(JSON.toJSONString(result));
//            response.setStatus(666);
//            return false;
//        } else {
//            String cookieValue = "";
//
//            try {
//                cookieValue = AESUtils.decrypt(loginValue, this.aesKey.getKey());
//            } catch (RuntimeException var17) {
//                LOGGER.error("cookie 解密失败", var17);
//                DataResult result = new DataResult();
//                result.addAttribute("redirectUrl",loginUrl);
//                PrintWriter out = response.getWriter();
//                out.print(JSON.toJSONString(result));
//                response.setStatus(666);
//                return false;
//            }
//
//            String[] cookieValues = cookieValue.split("##");
//            long loginTime = NumberUtils.toLong(cookieValues[3]);
//            long now = DateUtils.getServerTime();
//            long ms = 259200000L;
//            if(now - loginTime > ms) {
//                LOGGER.error("用户cookie已过期");
//                response.sendRedirect(redirectUrl);
//                return false;
//            } else {
//                LoginContext lc = new LoginContext();
//                lc.setLongId(NumberUtils.toInt(cookieValues[0]));
//                lc.setAccount(cookieValues[1]);
//                lc.setUserName(URLDecoder.decode(cookieValues[2], "UTF-8"));
//                lc.setLoginTime(new Date(loginTime));
//                LoginContext.setLoginContext(lc);
//                request.setAttribute("login_context", lc);
//                return true;
//            }
//        }*/
//
//        LoginContext lc = new LoginContext();
//        lc.setLongId(1);
//        lc.setAccount("admin");
//        lc.setUserName("admin");
//        lc.setLoginTime(new Date());
//        LoginContext.setLoginContext(lc);
//        request.setAttribute("login_context", lc);
//        return true;
//    }
//
//    public String getErpDomain() {
//        return this.erpDomain;
//    }
//
//    public void setErpDomain(String erpDomain) {
//        this.erpDomain = erpDomain;
//    }
//
//    public String getDomain() {
//        return domain;
//    }
//
//    public void setDomain(String domain) {
//        this.domain = domain;
//    }
//}
//

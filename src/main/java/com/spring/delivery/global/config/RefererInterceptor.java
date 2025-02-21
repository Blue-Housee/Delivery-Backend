package com.spring.delivery.global.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.net.MalformedURLException;
import java.net.URL;

@Component
public class RefererInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String referer = request.getHeader("Referer");
        if (referer != null) {
            try {
                // URL 파싱을 통해 필요한 경로 부분만 추출
                String refererPath = new URL(referer).getPath();
                // 예: /recipe/communityPage에서 communityPage만 추출
                String origin = refererPath.substring(refererPath.lastIndexOf("/") + 1);
                request.getSession().setAttribute("referer", origin);
                System.out.println("Referer Path: " + refererPath); // 로그 추가
                System.out.println("Origin: " + origin); // 로그 추가
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

}

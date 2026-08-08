package com.hongyun.navcampus.admin.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * SPA 路由控制器
 * 处理前端路由刷新导致的 404 问题，将其转发到 index.html
 */
@Controller
public class SpaController implements ErrorController {

    @RequestMapping("/error")
    public String handleError() {
        // 将所有未找到的路径（404）转发到 index.html，由前端 Vue Router 处理路由
        return "forward:/index.html";
    }
}

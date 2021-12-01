package com.janson.netty.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: index 控制器
 * @Author: Janson
 * @Date: 2021/8/15 14:49
 **/
@RestController
@RequestMapping("/index")
public class IndexController {

    @GetMapping("/demo")
    public String demo() {
        return "zhaoxiaoxiao520=shanjian";
    }
}
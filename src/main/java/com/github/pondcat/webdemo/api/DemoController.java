package com.github.pondcat.webdemo.api;

import com.github.pondcat.webdemo.entity.User;
import com.github.pondcat.webdemo.service.DemoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pondcat.commons.combine.ApiResponse;

import java.time.LocalDateTime;

/**
 * @author gejian
 */
@RestController
@RequestMapping("/api/demo")
public class DemoController {

    private static final Logger log = LoggerFactory.getLogger(DemoController.class);

    private @Autowired
	DemoService demoService;

    @GetMapping("ss")
    public ApiResponse<String> demo() {
        return ApiResponse.ok(LocalDateTime.now().toString());
    }

    @GetMapping("{id}/ss")
    public ApiResponse<String> demo1(@PathVariable Integer id) {
        return ApiResponse.ok(LocalDateTime.now().toString());
    }

    @PostMapping("serve")
    public ApiResponse<String> serve(@RequestBody User user) {
        log.debug(user.toString());
//		LocalDateTime dateTime = demoService.serve(user);
        return ApiResponse.ok(LocalDateTime.now().toString());
    }

    @PostMapping(value = "tt", params = {"id=1"})
    public void t1(@RequestBody User user) {
        log.debug(user.toString());
    }

    @RequestMapping("tt")
    public User t2(@RequestBody User user) {
        System.out.println(user);
        user.setCtime(LocalDateTime.now());
        return user;
    }

    @GetMapping("ping")
    public String pong() {
        return "pong";
    }
}

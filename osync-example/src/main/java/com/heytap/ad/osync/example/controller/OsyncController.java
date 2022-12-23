package com.heytap.ad.osync.example.controller;

import com.heytap.ad.osync.example.service.OsyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("osync")
public class OsyncController {

    @Autowired
    private OsyncService osyncService;


    /**
     * Performance string.
     *
     * @return the string
     */
    @RequestMapping("performance")
    public String performance() {
        osyncService.performanceTest();
        return "";
    }

    @RequestMapping("osyncTest")
    public String osyncTest() {
        osyncService.osyncTest();
        return "success";
    }


    /**
     * Update rule string.
     *
     * @return the string
     */
    @RequestMapping("updateRule")
    public String updateRule() {
        osyncService.updateRule();
        return "success";
    }

    /**
     * Future.
     */
    @RequestMapping("future")
    public void future() {
    }


    /**
     * Sets gobrs async.
     */
    @RequestMapping("osync")
    public void setOsync() {
        //开始时间: 获取当前时间毫秒数
        long start = System.currentTimeMillis();
        osyncService.osync();
        //结束时间: 当前时间 - 开始时间
        long coust = System.currentTimeMillis() - start;
        System.out.println("cost " + coust);

    }

}

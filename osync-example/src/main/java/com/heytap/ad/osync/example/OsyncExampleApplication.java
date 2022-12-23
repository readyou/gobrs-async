package com.heytap.ad.osync.example;

import com.yomahub.tlog.core.enhance.bytes.AspectLogEnhance;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 启动类
 */
@SpringBootApplication
/**
 * 使用osync-test 模块创建的任务 为了方便不重复创建任务了
 */
@ComponentScan(value = {"com.heytap.ad.osync"})
public class OsyncExampleApplication {

    /**
     * Tlog 日志打印框架 官网: https://tlog.yomahub.com/
     */
    static {
        AspectLogEnhance.enhance();
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(OsyncExampleApplication.class, args);
    }

}

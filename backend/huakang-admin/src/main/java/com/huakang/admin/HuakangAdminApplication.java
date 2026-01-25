package com.huakang.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 后台管理API启动类
 *
 * @author huakang
 */
@SpringBootApplication(scanBasePackages = "com.huakang")
@MapperScan("com.huakang.mapper")
@EnableScheduling  // 启用定时任务
public class HuakangAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(HuakangAdminApplication.class, args);
    }
}

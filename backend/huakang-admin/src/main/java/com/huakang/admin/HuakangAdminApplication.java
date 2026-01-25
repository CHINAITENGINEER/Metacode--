package com.huakang.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 后台管理API启动类
 *
 * @author huakang
 */
@SpringBootApplication(scanBasePackages = "com.huakang")
public class HuakangAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(HuakangAdminApplication.class, args);
    }
}

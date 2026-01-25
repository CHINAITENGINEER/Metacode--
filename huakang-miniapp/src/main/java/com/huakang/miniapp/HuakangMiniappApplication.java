package com.huakang.miniapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 小程序API启动类
 *
 * @author huakang
 */
@SpringBootApplication(scanBasePackages = "com.huakang")
public class HuakangMiniappApplication {

    public static void main(String[] args) {
        SpringApplication.run(HuakangMiniappApplication.class, args);
    }
}

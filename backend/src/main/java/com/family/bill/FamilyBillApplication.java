package com.family.bill;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 家庭记账系统启动类
 * 
 * @author family-bill
 */
@SpringBootApplication
@MapperScan("com.family.bill.mapper")
@EnableCaching
public class FamilyBillApplication {

    public static void main(String[] args) {
        SpringApplication.run(FamilyBillApplication.class, args);
    }
}


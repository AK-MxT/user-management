package com.user.mng;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Jsr330ScopeMetadataResolver;

@SpringBootApplication
// Mapperクラスのパッケージを正しく指定する。
// 1個上のdomainとかで指定すると関係ないクラスまでScanしようとしてエラーになる
@MapperScan("com.user.mng.domain.repository")
@ComponentScan(basePackages = "com.user.mng", scopeResolver = Jsr330ScopeMetadataResolver.class)
public class UserMngMockApplication extends SpringBootServletInitializer  {

	// warとしてデプロイするための設定
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(UserMngMockApplication.class);
    }

	public static void main(String[] args) {
		SpringApplication.run(UserMngMockApplication.class, args);
	}

}

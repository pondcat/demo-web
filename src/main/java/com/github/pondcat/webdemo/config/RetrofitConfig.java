package com.github.pondcat.webdemo.config;

import com.github.pondcat.webdemo.manager.SscService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * 配置retrofit接口生成实现, 在第三方http调用非常多时, 可考虑采用类似mybatis的MapperScan
 *
 * @author gejian at 2018/6/19 0:31
 */
@Configuration
public class RetrofitConfig {
	@Bean
	public SscService sscService() {
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl("http://f.apiplus.net/")
				.addConverterFactory(JacksonConverterFactory.create())
				.build();
		return retrofit.create(SscService.class);
	}
}

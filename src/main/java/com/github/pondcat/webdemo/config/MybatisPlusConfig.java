package com.github.pondcat.webdemo.config;

import com.baomidou.mybatisplus.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author gejian at 2018/6/19 0:45
 */
@Configuration
@MapperScan("com.domii.project.demo.dao")
public class MybatisPlusConfig {

	/**
	 * 分页插件，自动识别数据库类型
	 * 多租户，请参考官网【插件扩展】
	 */
	@Bean
	public PaginationInterceptor paginationInterceptor() {
		return new PaginationInterceptor();
	}
}

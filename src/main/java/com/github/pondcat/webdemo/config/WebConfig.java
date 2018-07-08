package com.github.pondcat.webdemo.config;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 *
 * 启用@EnableWebMvc后, 会导致某些spring-boot-starter自动配置失效,
 * 如springfox自动配置的webjars和swagger-ui等资源映射失效,
 * 需手动配置{@link #addResourceHandlers(ResourceHandlerRegistry)}:
 * <code>/webjars/** -> classpath:/META-INF/resources/webjars/, swagger-ui.html -> classpath:/META-INF/resources/</code>
 *
 * @author gejian at 2018/5/13 23:46
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		Iterator<HttpMessageConverter<?>> it = converters.iterator();
		while (it.hasNext()) {
			HttpMessageConverter<?> next = it.next();
			String name = next.getClass().getName();
			if (name.startsWith("xml.MappingJackson",
					"org.springframework.http.converter.".length())
					|| name.startsWith("json.MappingJackson",
							"org.springframework.http.converter.".length())) {
				it.remove();
			}
		}
		FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
		converter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON_UTF8,
				MediaType.APPLICATION_JSON, MediaType.ALL));
		converters.add(converter);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new HandlerInterceptor() {
			@Override
			public boolean preHandle(HttpServletRequest request,
					HttpServletResponse response, Object handler) throws Exception {
				String matchingPattern = (String) request
						.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
				if (matchingPattern == null) {
					// 以/api开头的必须有handlerMapping处理
					return false;
				}
				// 查询用户是否具备访问权限
				matchingPattern = request.getMethod() + " " + matchingPattern;
				// todo 权限控制
				return true;
			}

			@Override
			public void postHandle(HttpServletRequest request,
					HttpServletResponse response, Object handler,
					ModelAndView modelAndView) throws Exception {
			}

			@Override
			public void afterCompletion(HttpServletRequest request,
					HttpServletResponse response, Object handler, Exception ex)
					throws Exception {
			}
		}).addPathPatterns("/api/**");
	}

}

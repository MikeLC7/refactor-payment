package com.refactor.payment.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.refactor.mall.common.method.SubjectmMethodArgumentResolver;
import com.refactor.mall.common.util.IdGenerator;
import com.refactor.mall.common.method.SubjectMethodArgumentResolver;
import com.refactor.mall.common.util.redis.RedisUtil;
import feign.Request;
import feign.Retryer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.refactor.mall.common.handler.GlobalExceptionHandler;

/**
 * Project: RefactorMall
 *
 * File: WebConfig
 *
 * Description:
 *
 * @author: MikeLC
 *
 * @date: 2018/4/4 下午 02:24
 *
 * Copyright ( c ) 2018
 *
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {



	@Bean
    SubjectMethodArgumentResolver getSubjectMethodArgumentResolver(){
		return new SubjectMethodArgumentResolver();
	}

	@Bean
	SubjectmMethodArgumentResolver getSubjectmMethodArgumentResolver() { return new SubjectmMethodArgumentResolver(); }

	@Bean
	GlobalExceptionHandler getGlobalExceptionHandler() {
		return new GlobalExceptionHandler();
	}

	/**
	 * 使用 @Bean 注入 FastJsonHttpMessageConverter
	 * @return
	 */
	@Bean
	public HttpMessageConverters fastJsonHttpMessageConverters()
	{
		FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
		FastJsonConfig fastJsonConfig = new FastJsonConfig();
		fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);
		fastConverter.setFastJsonConfig(fastJsonConfig);
		HttpMessageConverter<?> converter = fastConverter;

		return new HttpMessageConverters(converter);
	}

	@Bean
	IdGenerator getIdGenerator(@Qualifier("redisTemplate") RedisTemplate redisTemplate){
		IdGenerator idGenerator = new IdGenerator(redisTemplate);
		return idGenerator;
	}

	@Bean
	RedisUtil getRedisUtil(@Qualifier("redisTemplate") RedisTemplate redisTemplate){
		RedisUtil redisUtil = new RedisUtil(redisTemplate);
		return redisUtil;
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(getSubjectMethodArgumentResolver());
		argumentResolvers.add(getSubjectmMethodArgumentResolver());
	}

	@Bean
	Retryer feignRetryer() {
		return Retryer.NEVER_RETRY;
	}

	@Bean
	Request.Options feignOptions() {
		return new Request.Options(/**connectTimeoutMillis**/300 * 1000, /** readTimeoutMillis **/300 * 1000);
	}



}

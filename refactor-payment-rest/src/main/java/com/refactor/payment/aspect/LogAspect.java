package com.refactor.payment.aspect;

import com.google.common.net.HttpHeaders;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

@Aspect
@Component
public class LogAspect {

	private final static Logger logger = LoggerFactory.getLogger(LogAspect.class);// 参数为当前使用的类名

	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	ThreadLocal<Long> time = new ThreadLocal<Long>();

	/**
	 * 
	 * 在所有rest,rpc包&标注@Log的地方注入方法打印日志
	 *
	 * @param joinPoint
	 */
	@Before("execution(* com.sinochem.yunlian.ship..rest..*Controller.*(..)) || execution(* com.sinochem.yunlian.ship..rpc..*Rest.*(..))")
	public void beforeExce(JoinPoint joinPoint) {
		long curStart = System.currentTimeMillis();
		time.set(curStart);
		MethodSignature ms = (MethodSignature) joinPoint.getSignature();
		String methodName =ms.getName();// 获取类名及类方法
		logger.info("TAG为<{}>的方法,开始运行时间:{}", methodName, formatter.format(new Date(curStart)));
		info(joinPoint);
		// 方法
	}

	@After("execution(* com.sinochem.yunlian.ship..rest..*Controller.*(..)) || execution(* com.sinochem.yunlian.ship..rpc..*Rest.*(..))")
	public void afterExce(JoinPoint joinPoint) {
		long curEnd = System.currentTimeMillis();
		MethodSignature ms = (MethodSignature) joinPoint.getSignature();
		Method method = ms.getMethod();
		System.out.println(time.get());
		logger.info("TAG为<{}>的方法,结束运行时间:{},运行消耗:{}ms", method.getName(), formatter.format(new Date(curEnd)),
				(curEnd - time.get()));
		time.remove();
	}

	@Around("execution(* com.sinochem.yunlian.ship..rest..*Controller.*(..)) || execution(* com.sinochem.yunlian.ship..rpc..*Rest.*(..))")
	public Object aroundExce(ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature ms = (MethodSignature) joinPoint.getSignature();
		Method method = ms.getMethod();
		return joinPoint.proceed();
	}

	private void info(JoinPoint joinPoint) {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();
		// url
		String url = request.getRequestURL().toString();
		// uri
		String uri = request.getRequestURI();
		// http method
		String httpMethod = request.getMethod();
		// ip
		String ip = request.getHeader(HttpHeaders.X_FORWARDED_FOR);
		// 类
		String className = joinPoint.getSignature().getDeclaringTypeName();
		// 方法
		String classMethod = joinPoint.getSignature().getName();// 获取类名及类方法
		// 参数
		String args = Arrays.toString(joinPoint.getArgs());

		logger.info("TAG为<{}>的方法, request url: {}, http method: {}, ip: {}, class: {}, method: {}, params: {}",
				classMethod, uri, httpMethod, ip,className, classMethod, args);
	}
}

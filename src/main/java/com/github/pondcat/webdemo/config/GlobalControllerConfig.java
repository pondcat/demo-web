package com.github.pondcat.webdemo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import pondcat.commons.combine.ApiResponse;
import pondcat.commons.combine.exception.BusinessException;

import javax.servlet.http.HttpServletRequest;

import static pondcat.commons.combine.ApiResponse.Status;
import static pondcat.commons.combine.ApiResponse.error;

/**
 * @author gejian at 2018/5/13 21:55
 */
@RestController
@ControllerAdvice("com.gj1913894.web.starter.api")
// todo 日志加入路径
public class GlobalControllerConfig {
	private static final Logger log = LoggerFactory.getLogger(GlobalControllerConfig.class);

	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
	@Autowired
	private HttpServletRequest request;

	@ExceptionHandler(AsyncRequestTimeoutException.class)
	private ApiResponse handleAsyncRequestTimeoutException(AsyncRequestTimeoutException ex) {
		log.warn(ex.getMessage());
		return error(Status.SERVICE_UNAVAILABLE, "服务不可用");
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	private ApiResponse handleNoHandlerFoundException(NoHandlerFoundException ex) {
		log.debug(ex.getMessage());
		return error(Status.NOT_FOUND, "url路径或请求方法错误");
	}

	/**
	 * hibernate-validator验证出错处理, 仅在请求头Content-Type=application/x-www-form-urlencoded时
	 */
	@ExceptionHandler(BindException.class)
	private ApiResponse<Void> handleBindException(BindException ex) {
		// 只记录第一个参数错误
		FieldError fieldError = ex.getFieldError();
		String errorMsg = resolveFieldError(ex, fieldError);
		return error(Status.ILLEGAL_ARGUMENT, errorMsg);
	}

	private String resolveFieldError(Exception ex, FieldError fieldError) {
		String errorMsg;
		if (fieldError == null) {
			if (log.isDebugEnabled()) {
				log.warn("参数绑定出错,无字段错误," + ex.getMessage());
			}
			errorMsg = "参数绑定出错";
		}
		else {
			errorMsg = "参数绑定出错," + fieldError.getObjectName() + '.'
					+ fieldError.getField() + ':' + fieldError.getRejectedValue() + ", "
					+ fieldError.getDefaultMessage();
			log.warn(errorMsg);
		}
		return errorMsg;
	}

	@ExceptionHandler(MissingServletRequestPartException.class)
	private ApiResponse handleMissingServletRequestPartException(MissingServletRequestPartException ex) {
		if (log.isDebugEnabled()) {
			log.debug("上传文件出错," + ex.getMessage());
		}
		return error(Status.ILLEGAL_ARGUMENT, "上传文件出错,请确保已提交上传文件且请求头中Content-Type=multipart/form-data");
	}

	/**
	 * hibernate-validator验证出错处理, 仅在入参通过@RequestBody绑定时
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	private ApiResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
		FieldError fieldError = ex.getBindingResult().getFieldError();
		String errorMsg = resolveFieldError(ex, fieldError);
		return error(Status.ILLEGAL_ARGUMENT, errorMsg);
	}

	@ExceptionHandler(HttpMessageNotWritableException.class)
	private ApiResponse handleHttpMessageNotWritable(HttpMessageNotWritableException ex) {
		log.error("写出出参出错", ex);
		return error("写出出参出错");
	}

	// todo 判断new DateTimeFormatter的消耗, 判断fastjson和jackson对Date和LocalDateTime的处理
	@ExceptionHandler(HttpMessageNotReadableException.class)
	private ApiResponse handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
		log.error("读取入参出错", ex);
		return error(Status.ILLEGAL_ARGUMENT, "读取入参出错");
	}

	@ExceptionHandler(TypeMismatchException.class)
	private ApiResponse handleTypeMismatch(TypeMismatchException ex) {
		String errorMsg = "参数类型错误, 参数名:" + ex.getPropertyName() + ",参数值:" + ex.getValue() + ",需求类型:" + ex.getRequiredType();
		log.warn(errorMsg);
		return error(Status.ILLEGAL_ARGUMENT, errorMsg);
	}

	@ExceptionHandler(ConversionNotSupportedException.class)
	private ApiResponse handleConversionNotSupported(ConversionNotSupportedException ex) {
		String errorMsg = "参数转换错误, 参数名:" + ex.getPropertyName() + ",参数值:" + ex.getValue() + ",需求类型:" + ex.getRequiredType();
		log.warn(errorMsg);
		return error(Status.ILLEGAL_ARGUMENT, errorMsg);
	}

	@ExceptionHandler(ServletRequestBindingException.class)
	private ApiResponse handleServletRequestBindingException(ServletRequestBindingException ex) {
		log.warn("servlet请求绑定异常", ex);
		return error(Status.ILLEGAL_ARGUMENT, ex.getMessage());
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	private ApiResponse handleMissingServletRequestParameter(MissingServletRequestParameterException ex) {
		String errorMsg = "缺少参数:" + ex.getParameterName() + ",参数类型:" + ex.getParameterType();
		log.warn(errorMsg);
		return error(Status.ILLEGAL_ARGUMENT, errorMsg);
	}

	@ExceptionHandler(MissingPathVariableException.class)
	private ApiResponse handleMissingPathVariable(MissingPathVariableException ex) {
		String errorMsg = "缺少URI路径参数:" + ex.getVariableName() +
				",参数类型:" + ex.getParameter().getParameterType().getSimpleName();
		log.warn(errorMsg);
		return error(Status.LACK_ARGUMENT, errorMsg);
	}

	@ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
	private ApiResponse handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex) {
		String errorMsg = "不支持响应头Content-Type=" + request.getContentType();
		log.debug(errorMsg);
		return error(errorMsg);
	}

	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	private ApiResponse handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
		String errorMsg = "不支持请求头Content-Type=" + request.getContentType();
		log.debug(errorMsg);
		return error(errorMsg);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	private ApiResponse handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
		String errorMsg = "不支持的http请求方式:" + ex.getMethod();
		log.debug(errorMsg);
		return error(errorMsg);
	}

	@ExceptionHandler(BusinessException.class)
	private ApiResponse handleBusinessException(BusinessException ex) {
		log.error("业务异常", ex);
		return error(ex.getMessage());
	}

	@ExceptionHandler(Exception.class)
	private ApiResponse handleException(Exception ex) {
		log.error("内部异常", ex);
		return error(ex.getMessage());
	}

}

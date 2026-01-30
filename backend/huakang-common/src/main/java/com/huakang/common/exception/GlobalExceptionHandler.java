package com.huakang.common.exception;

import com.huakang.common.core.Result;
import com.huakang.common.core.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.dao.DataAccessException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 *
 * @author huakang
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常：code={}, message={}", e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 参数校验异常
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public Result<?> handleValidationException(Exception e) {
        String message = "参数校验失败";
        if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException ex = (MethodArgumentNotValidException) e;
            message = ex.getBindingResult().getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
        } else if (e instanceof BindException) {
            BindException ex = (BindException) e;
            message = ex.getBindingResult().getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
        }
        log.warn("参数校验异常：{}", message);
        return Result.error(ResultCode.PARAM_ERROR.getCode(), message);
    }

    /**
     * 数据库访问异常
     */
    @ExceptionHandler(DataAccessException.class)
    public Result<?> handleDataAccessException(DataAccessException e, HttpServletRequest request) {
        String path = request != null ? request.getRequestURI() : "";
        log.error("数据库异常：path={}", path, e);
        return Result.error(ResultCode.ERROR.getCode(), "数据库操作异常，请稍后重试");
    }

    /**
     * 其他异常
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e, HttpServletRequest request) {
        String traceId = MDC.get("traceId");
        String errorId = UUID.randomUUID().toString();
        String path = request != null ? request.getRequestURI() : "";
        String method = request != null ? request.getMethod() : "";
        String ip = getClientIp(request);

        log.error("系统异常 - errorId: {}, traceId: {}, path: {}, method: {}, ip: {}",
                errorId, traceId, path, method, ip, e);

        // 对外统一返回通用错误信息，避免泄露内部实现细节
        return Result.error(ResultCode.ERROR.getCode(),
                "系统异常，请联系管理员。错误ID: " + errorId);
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return "";
        }
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}

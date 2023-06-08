package com.imooc.bilibili.service.handler;

import com.imooc.bilibili.domain.JsonResponse;
import com.imooc.bilibili.service.exception.ConditionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 描述: TODO
 */

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class CommonGlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public JsonResponse<String> commonExceptionHandler(HttpServletRequest request, Exception e) {
        String errorMsg = e.getMessage();
        log.error(errorMsg, e);
        if (e instanceof ConditionException) {
            String errorCode = ((ConditionException) e).getCode();
            return new JsonResponse<>(errorCode, errorMsg);
        } else {
            return new JsonResponse<>("500", errorMsg);
        }
    }

}

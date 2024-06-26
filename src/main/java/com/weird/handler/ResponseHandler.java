package com.weird.handler;

import com.weird.model.ResultModel;
import com.weird.utils.OperationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.sqlite.SQLiteException;

/**
 * 返回处理类
 *
 * @author Nidhogg
 */
@ControllerAdvice
@Slf4j
public class ResponseHandler implements ResponseBodyAdvice {
    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        return true;
    }

    /**
     * 正常返回，code=200
     */
    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        if (!(o instanceof ResultModel)) {
            return new ResultModel<>(200, o);
        }

        return o;
    }

    /**
     * 异常返回，code=500
     */
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResultModel<String> customException(Exception e) {
        if (!(e instanceof OperationException || e instanceof MissingServletRequestParameterException)) {
            e.printStackTrace();
        }
        log.error(e.getMessage());
        // 隐藏数据库错误
        if (e instanceof SQLiteException) {
            return new ResultModel<>(500, "数据库异常，请联系管理员");
        } else {
            return new ResultModel<>(500, e.getMessage());
        }
    }
}

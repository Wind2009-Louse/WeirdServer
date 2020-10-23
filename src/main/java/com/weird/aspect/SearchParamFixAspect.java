package com.weird.aspect;

import com.weird.interfaces.Fixable;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * 修复结构体中的null参数
 *
 * @author Nidhogg
 * @date 2020.10.23
 */
@Aspect
@Component
public class SearchParamFixAspect {
    @Around("@within(SearchParamFix)")
    public Object aroundClass(ProceedingJoinPoint point) throws Throwable {
        return aroundMethod(point);
    }

    @Around("@annotation(SearchParamFix)")
    public Object aroundMethod(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();
        for (int argsIndex = 0; argsIndex < args.length; ++argsIndex) {
            args[argsIndex] = fix(args[argsIndex]);
        }
        return point.proceed(args);
    }

    private Object fix(Object object) {
        // 对继承了Fixable接口的类进行Fix操作
        for (Class<?> interFace : object.getClass().getInterfaces()) {
            if (interFace.equals(Fixable.class)) {
                Field[] fields = object.getClass().getDeclaredFields();
                for (Field field : fields) {
                    boolean flag = field.isAccessible();
                    field.setAccessible(true);
                    ((Fixable) object).fix();
                    field.setAccessible(flag);
                }
            }
        }

        return object;
    }
}

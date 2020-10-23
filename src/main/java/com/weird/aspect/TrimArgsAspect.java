package com.weird.aspect;

import com.weird.model.Trimable;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * 对参数进行切面的注解实现
 *
 * @author Nidhogg
 * @date 2020.10.10
 */
@Aspect
@Component
public class TrimArgsAspect {
    @Around("@within(TrimArgs)")
    public Object aroundClass(ProceedingJoinPoint point) throws Throwable {
        return aroundMethod(point);
    }

    @Around("@annotation(TrimArgs)")
    public Object aroundMethod(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();
        for (int argsIndex = 0; argsIndex < args.length; ++argsIndex) {
            args[argsIndex] = trim(args[argsIndex]);
        }
        return point.proceed(args);
    }

    private Object trim(Object object) throws IllegalAccessException {
        if (object instanceof String) {
            // 对String进行Trim
            String originArg = (String) object;
            object = originArg.trim();
        } else if (object instanceof java.util.List) {
            java.util.List list = (java.util.List) object;
            for (int index = 0; index < list.size(); ++index) {
                Object subObject = trim(list.get(index));
                list.set(index, subObject);
            }
        } else {
            // 对继承了Trimable接口的类进行Trim
            for (Class<?> interFace : object.getClass().getInterfaces()) {
                if (interFace.equals(Trimable.class)) {
                    Field[] fields = object.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        boolean flag = field.isAccessible();
                        field.setAccessible(true);
                        field.set(object, trim(field.get(object)));
                        field.setAccessible(flag);
                    }
                }
            }
        }

        return object;
    }
}

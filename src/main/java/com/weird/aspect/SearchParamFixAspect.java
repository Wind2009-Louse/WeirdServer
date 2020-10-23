package com.weird.aspect;

import com.weird.model.param.SearchCardParam;
import com.weird.model.param.SearchHistoryParam;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.LinkedList;

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

    private Object fix(Object object) throws IllegalAccessException {
        if (object instanceof SearchHistoryParam) {
            SearchHistoryParam historyParam = (SearchHistoryParam) object;
            if (historyParam.getPackageName() == null) {
                historyParam.setPackageName("");
            }
            if (historyParam.getCardName() == null) {
                historyParam.setCardName("");
            }
            if (historyParam.getRareList() == null) {
                historyParam.setRareList(new LinkedList<>());
            }
            if (historyParam.getPage() == 0) {
                historyParam.setPage(1);
            }
            if (historyParam.getPageSize() == 0) {
                historyParam.setPageSize(20);
            }
            object = (Object) historyParam;
        } else if (object instanceof SearchCardParam) {
            SearchCardParam cardParam = (SearchCardParam) object;
            if (cardParam.getPackageNameList() == null) {
                cardParam.setPackageNameList(new LinkedList<>());
            }
            if (cardParam.getCardName() == null) {
                cardParam.setCardName("");
            }
            if (cardParam.getTargetUserList() == null) {
                cardParam.setTargetUserList(new LinkedList<>());
            }
            if (cardParam.getRareList() == null) {
                cardParam.setRareList(new LinkedList<>());
            }
            if (cardParam.getPage() == 0) {
                cardParam.setPage(1);
            }
            if (cardParam.getPageSize() == 0) {
                cardParam.setPageSize(20);
            }
            object = (Object) cardParam;
        }

        return object;
    }
}

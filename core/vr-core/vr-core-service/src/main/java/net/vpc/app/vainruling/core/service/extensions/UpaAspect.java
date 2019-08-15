package net.vpc.app.vainruling.core.service.extensions;

import net.vpc.upa.Action;
import net.vpc.upa.UPA;
import net.vpc.upa.exceptions.ExecutionException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;

/**
 * Created by vpc on 5/17/16.
 */
//@Aspect
//@Component
public class UpaAspect {
    public UpaAspect() {
//        System.out.println("Hello");
    }

    @Around("@within(net.vpc.app.vainruling.core.service.plugins.UpaAware)")
    public Object aroundUpaMethods1(final ProceedingJoinPoint pjp) throws Throwable {
        return aroundUpaMethods0(pjp);
    }

    @Around("@annotation(net.vpc.app.vainruling.core.service.plugins.UpaAware)")
    public Object aroundUpaMethods2(final ProceedingJoinPoint pjp) throws Throwable {
        return aroundUpaMethods0(pjp);
    }

    public Object aroundUpaMethods0(final ProceedingJoinPoint pjp) throws Throwable {
        try {
            return UPA.getContext().invoke(new Action<Object>() {
                @Override
                public Object run() {
                    try {
                        return pjp.proceed();
                    } catch (Throwable throwable) {
                        throw new ExecutionException(throwable);
                    }
                }
            });
        } catch (ExecutionException ex) {
            throw ex.getCause();
        }
    }
}

package net.thevpc.app.vainruling.core.service.extensions;

import net.thevpc.upa.Action;
import net.thevpc.upa.UPA;
import net.thevpc.upa.exceptions.ExecutionException;
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

    @Around("@within(net.thevpc.app.vainruling.core.service.plugins.UpaAware)")
    public Object aroundUpaMethods1(final ProceedingJoinPoint pjp) throws Throwable {
        return aroundUpaMethods0(pjp);
    }

    @Around("@annotation(net.thevpc.app.vainruling.core.service.plugins.UpaAware)")
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

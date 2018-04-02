/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.core.web;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * #{@link VrControllerInfoResolver}
 * #{@link VrActionEnabler}
 * #{@link VRMenuProvider}
 * @author taha.bensalah@gmail.com
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Controller
@Scope(value = "session")
public @interface VrController {

    UPathItem[] breadcrumb() default {};

    String css() default "";

    String title() default "";

    String subTitle() default "";

    String url() default "";

    String menu() default "";

    String securityKey() default "";

    String[] declareSecurityKeys() default {};

    /**
     * menu order, the higher the later to put in the menu
     * @return menu order, the higher the later to put in the menu
     */
    int order() default 100;

    /**
     * needed if several controller are exclusives
     * (for instance student profile and teacher profile)
     * @return priority value, le higher the more priority
     */
    int priority() default 0;

    /**
     * if this mentioned, whenever contract is called, this controller
     * will be used as long as this one have a greater priority
     * @return
     */
    String replacementFor() default "";
}

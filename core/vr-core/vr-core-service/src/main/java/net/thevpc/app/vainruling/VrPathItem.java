/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author taha.bensalah@gmail.com
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Controller
@Scope(value = "session")
public @interface VrPathItem {

    String title() default "";
    String subTitle() default "";

    String css() default "";

    String ctrl() default "";

    String cmd() default "";
}

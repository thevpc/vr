/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class that implements a derivative of EntityViewAction 
 * EntityViewActionDialog
 * EntityViewActionInvoke
 * @author taha.bensalah@gmail.com
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface VrEditorAction {

    String entityName() default "";

    String actionName() default "";

    boolean confirm() default false;

    String actionIcon() default "";
}

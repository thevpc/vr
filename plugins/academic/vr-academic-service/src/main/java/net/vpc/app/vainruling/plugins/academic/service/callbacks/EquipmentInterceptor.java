/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.callbacks;

import net.vpc.app.vainruling.plugins.academic.service.model.current.AcademicCoursePlan;
import net.vpc.upa.DefaultFieldBuilder;
import net.vpc.upa.config.Callback;
import net.vpc.upa.config.OnCreate;
import net.vpc.upa.events.EntityEvent;
import net.vpc.upa.types.ManyToOneType;

/**
 * if the equipment is found will add course to the request
 * @author vpc
 */
@Callback
public class EquipmentInterceptor {
//    @OnCreate(name = "EquipmentBorrowRequest")
//    private void onEquipmentBorrowRequestCreate(EntityEvent e){
//        e.getEntity().addField(new DefaultFieldBuilder()
//                .setName("course")
//                .setDataType(new ManyToOneType("AcademicCoursePlan", AcademicCoursePlan.class, "AcademicCoursePlan", true, true))
//        );
//    }
}

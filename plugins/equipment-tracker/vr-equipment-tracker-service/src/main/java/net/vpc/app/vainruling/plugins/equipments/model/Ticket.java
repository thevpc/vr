///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package net.vpc.app.vainruling.plugins.equipments.model;
//
//import java.util.Date;
//import net.vpc.app.vainruling.core.service.model.AppUser;
//import net.vpc.app.vainruling.plugins.equipments.service.model.Equipment;
//import net.vpc.upa.config.BoolEnum;
//import net.vpc.upa.config.Entity;
//import net.vpc.upa.config.Field;
//import net.vpc.upa.config.Id;
//import net.vpc.upa.config.Sequence;
//
///**
// *
// * @author vpc
// */
//@Entity
//public class Ticket {
//    @Id @Sequence
//    private int id;
//    
//    private AppUser user;
//    private Equipment equipment;
//    private Date time;
//    private String message;
//    @Field(max="4000",nullable=BoolEnum.FALSE)
//    private String observations;
//    
//}

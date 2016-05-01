///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package net.vpc.app.vainruling.api.web.mail;
//
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import javax.servlet.http.HttpServletRequest;
//import javax.ws.rs.Consumes;
//import javax.ws.rs.GET;
//import javax.ws.rs.Path;
//import javax.ws.rs.Produces;
//import javax.ws.rs.QueryParam;
//import javax.ws.rs.core.Context;
//import javax.ws.rs.core.MediaType;
//import net.vpc.app.vainruling.email.EmailPlugin;
//import net.vpc.vmail.XMail;
//import net.vpc.app.vainruling.api.web.core.WRSpring;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//
///**
// *
// * @author vpc
// */
//@Controller
//@Path("/mail")
//public class MailResource {
//
//    @Autowired
//    private EmailPlugin mailService;
//
//    @GET
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    @Path("/updateForm")
//    public MailData updateForm(@QueryParam("a") MailData data, @Context HttpServletRequest req) {
//        MailModel model = WRSpring.getSessionBean(MailModel.class, req);
//        try {
//            XMail u = createXMail(data);
//            if (u != null) {
//                mailService.send(u);
//            }
//        } catch (Exception ex) {
//            Logger.getLogger(MailResource.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return data;
//    }
//
//    public XMail createXMail(MailData m) {
//        XMail xm = new XMail();
//        xm.from("taha.bensalah@gmail.com");
//        xm.to(m.getToAll());
//        xm.toeach(m.getToEach());
//        xm.subject(m.getSubject());
//        xm.setSimulate(true);
//        return xm;
//    }
//}

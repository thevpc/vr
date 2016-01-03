/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.inbox.service;

import java.io.IOException;
import java.util.Properties;
import net.vpc.common.gomail.GoMail;
import net.vpc.common.gomail.GoMailContext;
import net.vpc.common.gomail.modules.DefaultGoMailAgent;
import net.vpc.common.gomail.modules.GoMailAgent;

/**
 *
 * @author vpc
 */
public class VrExternalMailAgent implements GoMailAgent {
    
    private DefaultGoMailAgent baseAgent = new DefaultGoMailAgent();
    public VrExternalMailAgent() {
    }

    @Override
    public int sendExpandedMail(GoMail mail, Properties roProperties, GoMailContext expr) throws IOException {
        return baseAgent.sendExpandedMail(mail, roProperties, expr);
        //            try {
        //                Thread.sleep(2000);
        //            } catch (Exception e) {
        //                e.printStackTrace();
        //            }
        //            if (Math.random() > 0.5) {
        //                throw new UnsupportedOperationException("Not supported yet to " + mail.to() + " ;  " + mail.subject());
        //            }
        //            return 1;
    }
    
}

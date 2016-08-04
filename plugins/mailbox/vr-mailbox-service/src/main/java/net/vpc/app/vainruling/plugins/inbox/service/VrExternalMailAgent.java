/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.inbox.service;

import net.vpc.common.gomail.GoMailAgent;
import net.vpc.common.gomail.GoMailContext;
import net.vpc.common.gomail.GoMailFactory;
import net.vpc.common.gomail.GoMailMessage;

import java.io.IOException;
import java.util.Properties;

/**
 * @author taha.bensalah@gmail.com
 */
public class VrExternalMailAgent implements GoMailAgent {

    private GoMailAgent baseAgent;

    public VrExternalMailAgent(GoMailFactory factory) throws IOException {
        baseAgent = factory.createAgent();
    }

    public VrExternalMailAgent(GoMailAgent baseAgent) throws IOException {
        if (baseAgent == null) {
            throw new NullPointerException();
        }
        this.baseAgent = baseAgent;
    }

    @Override
    public int sendMessage(GoMailMessage mail, Properties properties, GoMailContext expr) throws IOException {
        return baseAgent.sendMessage(mail, properties, expr);
    }

}

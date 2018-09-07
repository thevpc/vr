/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.service.test;

import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.upa.Document;
import net.vpc.upa.impl.DefaultDocument;
import org.junit.Test;

/**
 *
 * @author vpc
 */
public class TestSerializeDocument {

    @Test
    public void test() {
        DefaultDocument d = new DefaultDocument();
        d.setObject("Hello", "World");
        String s = VrUtils.GSON.toJson(d);
        System.out.println(s);
        Document d2 = (Document) VrUtils.GSON.fromJson(s, Document.class);
        System.out.println(d2);
    }
}

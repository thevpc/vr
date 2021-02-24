/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.service.test;

import net.thevpc.app.vainruling.core.service.util.VrUtils;
import net.thevpc.upa.Document;
import net.thevpc.upa.impl.DefaultDocument;
import org.junit.Test;

/**
 *
 * @author vpc
 */
public class TestSerializeDocument {

    @Test
    public void test() {
        DefaultDocument d = new DefaultDocument();
        System.out.println(d);
        d.setObject("Hello", "World");
        String s = VrUtils.GSON.toJson(d);
        System.out.println(s);
        Document d2 = (Document) VrUtils.GSON.fromJson(s, Document.class);
        System.out.println(d2);
        org.junit.Assert.assertEquals(d, d2);
    }
}

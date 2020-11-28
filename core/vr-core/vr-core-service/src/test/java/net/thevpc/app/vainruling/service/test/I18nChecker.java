/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.service.test;

import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.app.vainruling.core.service.util.I18n;
import net.thevpc.upa.*;


/**
 * @author taha.bensalah@gmail.com
 */
public class I18nChecker {

    public static void main(String[] args) {

        VrApp.runStandalone();
        int count = checkI18n();
        System.out.println(count + " Missing resources!");
    }


    private static int checkI18n() {
        int count = 0;
        for (net.thevpc.upa.Package p : UPA.getPersistenceUnit().getPackages()) {
            count += checkUPAObject(p);
        }
        for (Entity entity : UPA.getPersistenceUnit().getEntities()) {
            count += checkUPAObject(entity);
            for (Field field : entity.getFields()) {
                if (field.isId() && field.getModifiers().contains(FieldModifier.PERSIST_SEQUENCE)) {
                    continue;
                }
                if (field.getModifiers().contains(FieldModifier.FOREIGN)) {
                    continue;
                }
                count += checkUPAObject(field);
            }
        }
        for (Relationship relationship : UPA.getPersistenceUnit().getRelationships()) {
            count += checkUPAObject(relationship);
            count += checkUPAObject(relationship.getSourceRole());
            count += checkUPAObject(relationship.getTargetRole());
        }
        return count;
    }

    private static int checkUPAObject(UPAObject o) {
        I18n i18n = VrApp.getBean(I18n.class);
        String s = o.getTitle();
        if (s != null && (s.endsWith("!") || s.equals("Lien Manquant"))) {
            final String k = o.getI18NTitle().getKeys().get(0);
            if (!k.endsWith(".Target")) {
                System.err.println(k + "=");
            }
            return 1;
        }
        return 0;
    }
}

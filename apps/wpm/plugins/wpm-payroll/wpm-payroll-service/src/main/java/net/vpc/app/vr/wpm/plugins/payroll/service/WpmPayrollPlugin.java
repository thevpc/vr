package net.vpc.app.vr.wpm.plugins.payroll.service;

import net.vpc.app.vainruling.core.service.AppPlugin;
import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.Install;
import net.vpc.app.vr.wpm.plugins.payroll.service.model.AppFinancialInstitution;
import net.vpc.app.vr.wpm.plugins.payroll.service.model.AppFinancialInstitutionType;
import net.vpc.app.vr.wpm.plugins.payroll.service.model.EmployeeSalaryBonusType;
import net.vpc.upa.PersistenceUnit;
import net.vpc.upa.UPA;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by vpc on 6/13/16.
 */
@AppPlugin(version = "1.0")
public class WpmPayrollPlugin {
    @Autowired
    private CorePlugin core;

    @Install
    private void install() {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        AppFinancialInstitutionType appFinancialInstitutionType_banque = new AppFinancialInstitutionType();
        appFinancialInstitutionType_banque.setName("Banque");
        appFinancialInstitutionType_banque = core.findOrCreate(appFinancialInstitutionType_banque);

        AppFinancialInstitutionType appFinancialInstitutionType_poste = new AppFinancialInstitutionType();
        appFinancialInstitutionType_poste.setName("Poste");
        appFinancialInstitutionType_poste = core.findOrCreate(appFinancialInstitutionType_poste);

        AppFinancialInstitution appFinancialInstitution_stb = new AppFinancialInstitution();
        appFinancialInstitution_stb.setName("STB");
        appFinancialInstitution_stb.setInstitutionType(appFinancialInstitutionType_banque);
        appFinancialInstitution_stb = core.findOrCreate(appFinancialInstitution_stb);

        for (String n : new String[]{
                "Tenue 1er Mai",
                "Ind. Transport 1",
                "Congés Payés",
                "Ind. Panier",
                "Ind. Présence",
                "Ind. 1/2 journée",
                "Ind. Responsabilité",
                "Ind. Fonction",
                "Avantage Nature",
                "Ind. de Caisse",
                "Ind. Technicité",
                "Ind. d'astreinte",
                "Prime scolarité",
                "Prime d'assiduité",
        }) {
            EmployeeSalaryBonusType bt = new EmployeeSalaryBonusType();
            bt.setName(n);
            core.findOrCreate(bt);
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.extensions.importfiles;

import java.io.IOException;
import java.util.Arrays;
import java.util.TreeSet;
import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.extensions.VrImportFileAction;
import net.vpc.app.vainruling.core.service.extensions.VrImportFileActionContext;
import net.vpc.app.vainruling.core.service.model.AppConfig;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;

/**
 *
 * @author vpc
 */
public class AbstractAcademicImportAction implements VrImportFileAction {

    private String name;
    private TreeSet<String> formats = new TreeSet<>();

    public AbstractAcademicImportAction(String name, String... formats) {
        this.name = name;
        this.formats.addAll(Arrays.asList(formats));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getFileNameDescription() {
        return String.join(",", formats);
    }

    @Override
    public boolean isAcceptFileName(String name) {
        for (String format : formats) {
            if (format.startsWith("*")) {
                if (name.endsWith(format.substring(1))) {
                    return true;
                }
            }
            if (name.equals(format)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public long importFile(VrImportFileActionContext context) throws IOException {
        long count = 0;

        AcademicPlugin a = VrApp.getBean(AcademicPlugin.class);
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        AppConfig appConfig = core.getCurrentConfig();
        if (appConfig != null && appConfig.getMainPeriod() != null) {
            int periodId = appConfig.getMainPeriod().getId();
            count = a.importFile(periodId, context.getFilePath(), null);
        }
        return count;
    }

}

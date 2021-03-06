/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.service.extensions.importfiles;

import java.io.IOException;
import java.util.Arrays;
import java.util.TreeSet;

import net.thevpc.app.vainruling.VrImportFileAction;
import net.thevpc.app.vainruling.VrImportFileActionContext;
import net.thevpc.app.vainruling.VrImportFileOptions;
import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.app.vainruling.core.service.model.AppConfig;
import net.thevpc.app.vainruling.plugins.academic.service.integration.ImportOptions;

/**
 *
 * @author vpc
 */
public class AbstractAcademicImportAction implements VrImportFileAction {

    private String name;
    private String exampleFilePath;
    private TreeSet<String> formats = new TreeSet<>();

    public AbstractAcademicImportAction(String name, String[] formats, String exampleFilePath) {
        this.name = name;
        this.exampleFilePath = exampleFilePath;
        this.formats.addAll(Arrays.asList(formats));
    }

    public AbstractAcademicImportAction(String name) {
        this.name = name;
        this.exampleFilePath = "/Config/import-templates/example." + name + ".xlsx";
        this.formats.addAll(Arrays.asList(name + ".xlsx", "*." + name + ".xlsx"));
    }

    public String getExampleFilePath() {
        return exampleFilePath;
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
    public boolean isAcceptFileName(String name,VrImportFileOptions importFileOptions) {
        if(importFileOptions!=null){
            if(importFileOptions.getFileTypeName()!=null && importFileOptions.getFileTypeName().length()>0){
                if(importFileOptions.getFileTypeName().equals(getName())){
                    return true;
                }
                    return false;
            }
        }
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
            count = a.importFile(periodId, context.getFilePath(), new ImportOptions().setContentTypeName(name));
        }
        return count;
    }

}

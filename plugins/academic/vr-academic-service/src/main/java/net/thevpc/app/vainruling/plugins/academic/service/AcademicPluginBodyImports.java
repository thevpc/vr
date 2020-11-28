package net.thevpc.app.vainruling.plugins.academic.service;

import net.thevpc.app.vainruling.core.service.CorePlugin;
import net.thevpc.app.vainruling.core.service.CorePluginSecurity;
import net.thevpc.app.vainruling.core.service.model.AppPeriod;
import net.thevpc.app.vainruling.plugins.academic.service.integration.ImportOptions;
import net.thevpc.app.vainruling.plugins.academic.service.integration.ImportStudentContext;
import net.thevpc.app.vainruling.plugins.academic.service.integration.ImportTeacherContext;
import net.thevpc.app.vainruling.plugins.academic.service.integration.XlsxLoadImporter;
import net.thevpc.app.vainruling.core.service.TraceService;
import net.thevpc.app.vainruling.core.service.VrApp;
import net.thevpc.app.vainruling.plugins.academic.model.imp.AcademicStudentImport;
import net.thevpc.app.vainruling.plugins.academic.model.imp.AcademicTeacherImport;
import net.thevpc.common.strings.StringConverterMap;
import net.thevpc.common.strings.StringUtils;
import net.thevpc.common.vfs.VFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AcademicPluginBodyImports extends AcademicPluginBody {

    private CorePlugin core;
    private TraceService trace;
    private AcademicPlugin academic;

    @Override
    public void onStart() {
        core = CorePlugin.get();
        trace = TraceService.get();
        academic = getContext().getPlugin();
    }

    public void importStudent(int periodId, AcademicStudentImport s) throws IOException {
        CorePluginSecurity.requireRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_CONFIG_IMPORT);
        XlsxLoadImporter i = new XlsxLoadImporter();
        ImportStudentContext ctx = new ImportStudentContext();
        ctx.setMainPeriod(core.findPeriodOrMain(periodId));
        i.importStudent(s, ctx);
    }

    public void importTeacher(int periodId, AcademicTeacherImport t) throws IOException {
        CorePluginSecurity.requireRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_CONFIG_IMPORT);
        XlsxLoadImporter i = new XlsxLoadImporter();
        ImportTeacherContext ctx = new ImportTeacherContext();
        ctx.setMainPeriod(core.findPeriodOrMain(periodId));
        i.importTeacher(t, ctx);
    }

    public int importFile(int periodId, VFile folder, ImportOptions importOptions) throws IOException {
        CorePluginSecurity.requireRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_CONFIG_IMPORT);
        XlsxLoadImporter i = new XlsxLoadImporter();
        return i.importFile(periodId, folder, importOptions);
    }


    public void importTeachingLoad(int periodId) {
        CorePluginSecurity.requireRight(AcademicPluginSecurity.RIGHT_CUSTOM_EDUCATION_CONFIG_IMPORT);
        CorePlugin core = VrApp.getBean(CorePlugin.class);
        try {
            AppPeriod mainPeriod = core.findPeriodOrMain(periodId);
            String year = mainPeriod.getName();
            String version = (String) core.getOrCreateAppPropertyValue("AcademicPlugin.import.version", null, "v01");
            String dir = (String) core.getOrCreateAppPropertyValue("AcademicPlugin.import.configFolder", null, "/Config/Import/${year}");
            Map<String, String> vars = new HashMap<>();
            vars.put("home", System.getProperty("user.home"));
            vars.put("year", year);
            vars.put("version", version);

            dir = StringUtils.replaceDollarPlaceHolders(dir, new StringConverterMap(vars));

            String dataFolder = dir + "/data";

            AcademicPlugin s = VrApp.getBean(AcademicPlugin.class);

            net.thevpc.common.vfs.VirtualFileSystem fs = core.getRootFileSystem();
            s.resetModuleTeaching();
            s.importFile(mainPeriod.getId(),
                    fs.get(dataFolder),
                    new ImportOptions()
            );
        } catch (Exception ex) {
            Logger.getLogger(XlsxLoadImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


}

package net.vpc.app.vainruling.plugins.academic.service.extensions.editor;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.model.AppConfig;
import net.vpc.app.vainruling.core.service.model.AppUser;
import net.vpc.app.vainruling.core.service.editor.AppEntityExtendedPropertiesProvider;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;
import net.vpc.app.vainruling.plugins.academic.model.config.AcademicTeacherPeriod;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AcademicPluginAppEntityExtendedPropertiesProvider implements AppEntityExtendedPropertiesProvider{
    @Override
    public Map<String, Object> getExtendedPropertyValues(Object o) {
        CorePlugin core = CorePlugin.get();
        AcademicPlugin academic=AcademicPlugin.get();
        if (o instanceof AppUser) {
            AcademicTeacher t = academic.findTeacherByUser(((AppUser) o).getId());
            if (t != null) {
                AppConfig appConfig = core.getCurrentConfig();
                if (appConfig != null && appConfig.getMainPeriod() != null) {
                    AcademicTeacherPeriod pp = academic.findTeacherPeriod(appConfig.getMainPeriod().getId(), t.getId());
                    HashMap<String, Object> m = new HashMap<>();
                    m.put("discipline", t.getDiscipline());
                    m.put("degree", pp.getDegree() == null ? null : pp.getDegree().getName());
                    m.put("degreeCode", pp.getDegree() == null ? null : pp.getDegree().getCode());
                    m.put("situation", pp.getSituation() == null ? null : pp.getSituation().getName());
                    m.put("enabled", pp.isEnabled());
                    return m;
                }
            }
        }
        return null;
    }

    @Override
    public Set<String> getExtendedPropertyNames(Class o) {
        if (o.equals(AppUser.class)) {
            return new HashSet<>(Arrays.asList("discipline", "degree", "degreeCode", "situation", "enabled"));
        }
        return null;
    }
}

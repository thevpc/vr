package net.vpc.app.vr.core.toolbox;

import net.vpc.app.vr.core.toolbox.util.JavaUtils;
import net.vpc.common.strings.StringUtils;

public class VrProjectConfigListener implements ProjectConfigListener {
    private ProjectConfig config;

    public VrProjectConfigListener(ProjectConfig config) {
        this.config = config;
    }

    @Override
    public void onSetProperty(String propertyName, String value) {
        switch (propertyName) {
            case "vrProjectName": {
                config.get("vrProjectShortTitle").setDefaultValue(StringUtils.isEmpty(value)?"my-project": JavaUtils.toIdFormat(value));
                config.get("vrProjectLongTitle").setDefaultValue(StringUtils.isEmpty(value)?"My Project": JavaUtils.toNameFormat(value));
                break;
            }
            case "vrConfigPublicTheme": {
                config.set("vrEnableModule_vr-public-theme-crew", "true");
                break;
            }
            case "vrConfigPrivateTheme": {
                if ("adminlte".equals(value)) {
                    config.set("vrEnableModule_vr-private-theme-adminlte", "true");
                }
                break;
            }
            case "vrEnableModule_vr-academic": {
                if ("false".equals(value)) {
                    config.set("vrEnableModule_vr-academic-report", value);
                    config.set("vrEnableModule_vr-academic-planning", value);
                    config.set("vrEnableModule_vr-academic-perf-eval", value);
                    config.set("vrEnableModule_vr-academic-profile", value);
                    config.set("vrEnableModule_vr-academic-project-based-learning", value);
                }
                break;
            }
            case "vrEnableModule_vr-equipments": {
                if ("false".equals(value)) {
                    config.set("vrEnableModule_vr-equipment-tracker", value);
                }
                break;
            }
        }
    }
}

package net.vpc.app.vainruling.plugins.themes.crew;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppProperty;
import net.vpc.app.vainruling.core.service.plugins.VrPlugin;
import net.vpc.app.vainruling.core.service.util.VrUtils;
import net.vpc.app.vainruling.core.web.jsf.ctrl.LoginCtrl;
import net.vpc.common.strings.StringUtils;
import net.vpc.upa.Action;
import net.vpc.upa.UPA;
import net.vpc.upa.VoidAction;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.util.Date;

@VrPlugin
public class CrewPublicThemePlugin {
    @Autowired
    private CorePlugin core;

    private void onInstall() {

    }

    public String getCountDownText() {
        return UPA.getPersistenceUnit().invokePrivileged(new Action<String>() {
            @Override
            public String run() {
                String countDownText = (String) core.getAppPropertyValue("System.UI.Theme.CountDown.Text", null);
                if (StringUtils.isEmpty(countDownText)) {
                    return "";
                }
                return countDownText;
            }
        });
    }

    public String getCountDownDate() {
        return UPA.getPersistenceUnit().invokePrivileged(new Action<String>() {
            @Override
            public String run() {
                String countDownText = (String) core.getAppPropertyValue("System.UI.Theme.CountDown.Text", null);
                Object countDownDate = core.getAppPropertyValue("System.UI.Theme.CountDown.Date", null);
                if (StringUtils.isEmpty(countDownText)) {
                    return "";
                }
                if (countDownDate != null && countDownDate instanceof String) {
                    return countDownDate.toString();
                }
                if (countDownDate == null || !(countDownDate instanceof Date)) {
                    return "";
                }
                return VrUtils.UNIVERSAL_DATE_FORMAT.format(countDownDate);
            }
        });
    }

    public String getLoginBackgroundUrl() {
        String[] login = core.resolveLogin(VrApp.getBean(LoginCtrl.class).getModel().getLogin());

        String domain = login[0];
        String key = "System.UI.Theme.Login.BackgroundUrl@" + domain;
        return UPA.getPersistenceUnit("main").invokePrivileged(new Action<String>() {
            @Override
            public String run() {
                Object appProperty = core.getAppPropertyValue(key, null);
                return appProperty == null ? null : appProperty.toString();
            }
        });
    }
}

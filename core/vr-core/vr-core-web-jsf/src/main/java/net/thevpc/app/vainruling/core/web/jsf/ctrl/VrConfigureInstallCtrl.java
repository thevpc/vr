/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.core.web.jsf.ctrl;

import net.thevpc.app.vainruling.core.service.model.AppProperty;
import net.thevpc.app.vainruling.core.service.util.VrPlatformUtils;
import net.thevpc.common.strings.StringUtils;
import net.thevpc.upa.PersistenceUnit;
import net.thevpc.upa.QueryBuilder;
import net.thevpc.upa.UPA;
import net.thevpc.upa.VoidAction;
import org.springframework.stereotype.Controller;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author taha.bensalah@gmail.com
 */
@Controller
public class VrConfigureInstallCtrl {

    private static final String LOCK_PATH = "/META-INF/private/install.lock";

    private final Model model = new Model();

    public void init() {

    }

    public boolean isMandatoryConfig(ServletContext servletContext) {
        if (servletContext.getAttribute(LOCK_PATH) != null) {
            return false;
        }
        String rp = servletContext.getRealPath("/") + LOCK_PATH;
        File lockFile = new File(VrPlatformUtils.validatePath(rp));
        if (lockFile.exists()) {
            return false;
        }
        AppProperty p = findSystemProperty("System.FileSystem");
        getModel().setRootPath(p == null ? null : p.getPropertyValue());
        String s = VrPlatformUtils.validatePath(getModel().getRootPath());
        if (StringUtils.isBlank(s) || !new File(s).exists() || !new File(s).isDirectory()) {
            return true;
        }
        try {
            if (lockFile.getParentFile() != null) {
                lockFile.getParentFile().mkdirs();
            }
            lockFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public AppProperty findSystemProperty(String name) {
        PersistenceUnit pu = UPA.getPersistenceUnit();
        QueryBuilder q = pu.createQueryBuilder(AppProperty.class);
        q.byField("propertyName", name);
        q.byExpression("(userId = null)");
        List<AppProperty> props = q.getResultList();
        if (props.isEmpty()) {
            return null;
        }
        return props.get(0);
    }

    public String configureInstall() {
        UPA.getContext().invokePrivileged(new VoidAction() {
            @Override
            public void run() {
                PersistenceUnit pu = UPA.getPersistenceUnit();
                AppProperty p = findSystemProperty("System.FileSystem");
                if (p == null) {
                    p = new AppProperty();
                    p.setEnabled(true);
                    p.setPropertyName("System.FileSystem");
                    p.setPropertyValue(getModel().getRootPath());
                    pu.persist(p);
                } else {
                    p.setPropertyValue(getModel().getRootPath());
                    pu.merge(p);
                }
            }
        });

        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String rp = servletContext.getRealPath("/") + LOCK_PATH;
        File f = new File(VrPlatformUtils.validatePath(rp));
        try {
            f.getParentFile().mkdirs();
            f.createNewFile();
        } catch (IOException ex) {
            Logger.getLogger(VrConfigureInstallCtrl.class.getName()).log(Level.SEVERE, null, ex);
        }
        servletContext.setAttribute(LOCK_PATH, true);
        return "/login";
    }

    public Model getModel() {
        return model;
    }

    public static class Model {

        private String rootPath;

        public String getRootPath() {
            return rootPath;
        }

        public void setRootPath(String rootPath) {
            this.rootPath = rootPath;
        }

    }
}

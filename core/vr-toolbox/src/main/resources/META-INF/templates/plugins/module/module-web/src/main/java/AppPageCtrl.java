/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ${{vrProjectGroup}}.${{packageName(vrModuleName)}}.web;

import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.VrController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ${{vrProjectGroup}}.${{packageName(vrModuleName)}}.service;
/**
 *
 * @author ${{vrConfigAuthor}}
 */
@VrController(
        menu = "${{vrPageMenuPath}}",
        url = "modules/${{vrPageName}}",
        securityKey = "Custom.Page.$${vrPageName}}"
)
@Controller
public class ${{className(vrPageName)}}Ctrl {
    private static final Logger log = Logger.getLogger(${{className(vrPageName)}}Ctrl.class.getName());

    private Model model = new Model();
    
    @Autowired
    private CorePlugin core;

    @Autowired
    private ${{className(vrProjectName)}}Plugin srv;

    public Model getModel() {
        return model;
    }

    @OnPageLoad
    private void init(Config conf) {
        if(conf!=null && conf.updateMessage){
            getModel().setMessage("Hello World");
        }
    }

    public class Model {

        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }
    
    public static class Config {
        boolean updateMessage;
    }
}
 

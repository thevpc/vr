#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package web;

import net.vpc.app.vainruling.core.service.*;
import net.vpc.app.vainruling.core.service.util.*;
import net.vpc.app.vainruling.core.web.*;

/**
 * @author me
 */
@VrController(
        menu = "/CustomPlugins",
        url = "modules/sample/sample"
)
public class SampleCtrl {
    private Model model=new Model();

    @OnPageLoad
    public void onLoad(String command) {
        Config c = VrUtils.parseJSONObject(command, Config.class);
    }

    public void onMyAction(){

    }

    public Model getModel(){
        return model;
    }

    public static class Model{
        private String message;
        public String getMessage(){
            return message;
        }
        public void setMessage(String message){
            this.message=message;
        }
    }

    public static class Config{
        String loadParam;
    }
}

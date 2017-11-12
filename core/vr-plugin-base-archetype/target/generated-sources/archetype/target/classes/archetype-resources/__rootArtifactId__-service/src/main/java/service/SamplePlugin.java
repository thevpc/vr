#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package service;
import net.vpc.app.vainruling.core.service.plugins.*;
import net.vpc.app.vainruling.core.service.*;
import org.springframework.beans.factory.annotation.*;

@AppPlugin
public class SamplePlugin {
    @Autowired
    private CorePlugin core;

    @Install
    private void onInstall() {
        //do install (new plugin install or plugin version update)

    }

    @Start
    private void onStart() {
        //application startup
    }

    public String getMySampleHelloWorldServiceMethod(){
        return "Hello "+core.getCurrentUser().getLogin();
    }
}

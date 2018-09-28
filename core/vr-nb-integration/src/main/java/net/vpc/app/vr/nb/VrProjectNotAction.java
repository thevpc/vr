/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vr.nb;

import java.io.File;
import javax.swing.JOptionPane;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup.Template;
import org.openide.util.actions.*;
import org.netbeans.api.project.Project;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author vpc
 */
public class VrProjectNotAction extends NodeAction {

    @Override
    protected void performAction(Node[] activatedNodes) {
        Node currentNode = activatedNodes[0];

        FileObject fileObject = null;

        Project projects[] = (Project[]) currentNode.getLookup().lookup(new Template(Project.class)).allInstances().toArray(new Project[0]);

        if (projects != null && projects.length == 1 && projects[0] != null) {
            fileObject = projects[0].getProjectDirectory();
        }

        if (fileObject == null) {
            DataObject dataObject = currentNode.getCookie(DataObject.class);
            if (dataObject != null) {
                fileObject = dataObject.getPrimaryFile();
            }
        }

        if (fileObject != null) {

            //building a file allow to get the absolute path with the correct separator (/ or \)
            File file = FileUtil.toFile(fileObject);
            JOptionPane.showMessageDialog(null, file);
        }
    }

    @Override
    protected boolean enable(Node[] node) {
        if (node != null && node.length == 1) {
            Node currentNode = node[0];

            Project projects[] = (Project[]) currentNode.getLookup().lookup(new Template(Project.class)).allInstances().toArray(new Project[0]);

            if (projects != null && projects.length == 1 && projects[0] != null) {
                return true;
            }

            DataObject dataObject = currentNode.getCookie(DataObject.class);
            if (dataObject != null) {
                FileObject fileObj = dataObject.getPrimaryFile();
                return fileObj.isValid() && !fileObj.isVirtual();
            }

        }
        return false;
    }

    @Override
    public String getName() {
        return "VrProjectNotAction";
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

}

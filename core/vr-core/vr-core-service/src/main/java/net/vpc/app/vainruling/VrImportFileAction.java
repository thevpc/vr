/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling;

import java.io.IOException;

/**
 *
 * @author vpc
 */
public interface VrImportFileAction {

    String getName();

    String getFileNameDescription();

    boolean isAcceptFileName(String name);

    long importFile(VrImportFileActionContext context) throws IOException;
}

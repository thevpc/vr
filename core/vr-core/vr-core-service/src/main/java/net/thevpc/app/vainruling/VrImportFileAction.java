/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling;

import java.io.IOException;

/**
 *
 * @author vpc
 */
public interface VrImportFileAction {

    String getName();

    String getExampleFilePath();

    String getFileNameDescription();

    boolean isAcceptFileName(String name,VrImportFileOptions options);

    long importFile(VrImportFileActionContext context) throws IOException;
}

package net.vpc.app.vainruling.plugins.academic.service.extensions.importfiles;

import org.springframework.stereotype.Service;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author vpc
 */
@Service
public class TeachersImportAction extends AbstractAcademicImportAction {

    // khaouwla bel kahla
    public TeachersImportAction() {
        super("Import ", "*.teachers.xlsx","teachers.xlsx");
    }

}

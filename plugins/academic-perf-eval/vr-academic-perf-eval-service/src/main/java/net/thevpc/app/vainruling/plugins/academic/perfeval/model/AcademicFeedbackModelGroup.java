/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.academic.perfeval.model;

import net.thevpc.upa.config.Entity;
import net.thevpc.upa.config.Id;
import net.thevpc.upa.config.Main;
import net.thevpc.upa.config.Path;
import net.thevpc.upa.config.Sequence;

/**
 *
 * @author vpc
 */
@Entity
@Path("/Education/Evaluation")
public class AcademicFeedbackModelGroup {
    @Id @Sequence
    private int id;
    @Main
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}

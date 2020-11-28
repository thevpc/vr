package net.thevpc.app.vainruling.plugins.academic.pbl.service.dto;

import net.thevpc.app.vainruling.plugins.academic.pbl.model.ApblCoaching;

/**
 * Created by vpc on 9/25/16.
 */
public class CoachNode extends ApblNode{
    private ApblCoaching coaching;

    public CoachNode() {
    }

    public CoachNode(ApblCoaching coaching) {
        this.coaching = coaching;
    }

    public ApblCoaching getCoaching() {
        return coaching;
    }

    public void setCoaching(ApblCoaching coaching) {
        this.coaching = coaching;
    }
}

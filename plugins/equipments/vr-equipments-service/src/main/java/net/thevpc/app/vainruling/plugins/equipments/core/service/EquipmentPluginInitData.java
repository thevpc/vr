/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.app.vainruling.plugins.equipments.core.service;

import net.thevpc.app.vainruling.core.service.model.AppAreaType;

/**
 *
 * @author vpc
 */
public class EquipmentPluginInitData {

    AppAreaType areaType_etablissement = new AppAreaType("etablissement");
    AppAreaType areaType_bloc = new AppAreaType("bloc");
    AppAreaType areaType_salle = new AppAreaType("salle");
    AppAreaType areaType_armoire = new AppAreaType("armoire");
    AppAreaType areaType_rangement = new AppAreaType("rangement");
}

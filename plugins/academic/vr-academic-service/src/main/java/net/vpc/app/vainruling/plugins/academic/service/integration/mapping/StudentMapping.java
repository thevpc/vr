/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service.integration.mapping;

import net.vpc.app.vainruling.core.service.util.ColumnMapping;

/**
 *
 * @author vpc
 */
public class StudentMapping {
    
    @ColumnMapping(value = {"Numero CIN", "CIN"})
    public int COL_NIN = 0;
    @ColumnMapping(value = {"Numero Inscrption"})
    public int COL_SUBSCRIPTION_NBR = 1;
    @ColumnMapping(value = {"Prenom"})
    public int COL_FIRST_NAME = 2;
    @ColumnMapping(value = {"Nom"})
    public int COL_LAST_NAME = 3;
    @ColumnMapping(value = {"Email", "Email address"})
    public int COL_EMAIL = 4;
    @ColumnMapping(value = {"Téléphone"})
    public int COL_GSM = 5;
    @ColumnMapping(value = {"Annee", "Period", "Année universitaire", "periode"})
    public int COL_YEAR1 = 6;
    @ColumnMapping(value = {"Classe"})
    public int COL_CLASS = 7;
    @ColumnMapping(value = {"Genre"})
    public int COL_GENDER = 8;
    @ColumnMapping(value = {"Civilité"})
    public int COL_CIVILITY = 9;
    @ColumnMapping(value = {"Prenom arabe"})
    public int COL_LAST_NAME2 = 10;
    @ColumnMapping(value = {"Nom arabe"})
    public int COL_FIRST_NAME2 = 11;
    @ColumnMapping(value = {"Prepa"})
    public int COL_PREP = 12;
    @ColumnMapping(value = {"Section"})
    public int COL_PREP_SECTION = 13;
    @ColumnMapping(value = {"Rang prepa"})
    public int COL_PREP_RANK = 14;
    @ColumnMapping(value = {"Rang prepa Max"})
    public int COL_PREP_RANK_MAX = 15;
    @ColumnMapping(value = {"Score prepa Max"})
    public int COL_PREP_SCORE = 16;
    @ColumnMapping(value = {"Bac"})
    public int COL_BAC = 17;
    @ColumnMapping(value = {"Score Bac"})
    public int COL_BAC_SCORE = 18;
    @ColumnMapping(value = {"BirthDate", "Date naissance"})
    public int COL_BIRTH_DATE = 19;
    
}

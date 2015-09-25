/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.service;

/**
 *
 * @author vpc
 */
public class TeacherGenerationOptions {

    private Integer[] teacherIds;
    private String semester;
    private GeneratedContent[] contents;
    private String templateFolder;
    private String outputFolder;
    private String outputNamePattern;
    private boolean includeIntents;

    public Integer[] getTeacherIds() {
        return teacherIds;
    }

    public TeacherGenerationOptions setTeacherIds(Integer... teacherIds) {
        this.teacherIds = teacherIds;
        return this;
    }

    public String getSemester() {
        return semester;
    }

    public TeacherGenerationOptions setSemester(String semester) {
        this.semester = semester;
        return this;
    }

    public GeneratedContent[] getContents() {
        return contents;
    }

    public TeacherGenerationOptions setContents(GeneratedContent... contents) {
        this.contents = contents;
        return this;
    }

    public String getTemplateFolder() {
        return templateFolder;
    }

    public TeacherGenerationOptions setTemplateFolder(String templateFolder) {
        this.templateFolder = templateFolder;
        return this;
    }

    public String getOutputFolder() {
        return outputFolder;
    }

    public TeacherGenerationOptions setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder;
        return this;
    }

    public String getOutputNamePattern() {
        return outputNamePattern;
    }

    public TeacherGenerationOptions setOutputNamePattern(String outputNamePattern) {
        this.outputNamePattern = outputNamePattern;
        return this;
    }

    public TeacherGenerationOptions setIncludeIntents(boolean inclueIntents) {
        this.includeIntents = inclueIntents;
        return this;
    }

    public boolean isIncludeIntents() {
        return includeIntents;
    }

}

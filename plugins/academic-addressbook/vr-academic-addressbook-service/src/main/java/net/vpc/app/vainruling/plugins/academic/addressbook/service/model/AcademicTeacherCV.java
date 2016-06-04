/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.addressbook.service.model;

import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import net.vpc.upa.AccessLevel;
import net.vpc.upa.UserFieldModifier;
import net.vpc.upa.config.Entity;
import net.vpc.upa.config.Field;
import net.vpc.upa.config.Id;
import net.vpc.upa.config.Path;
import net.vpc.upa.config.Properties;
import net.vpc.upa.config.Property;

/**
 *
 * @author vpc
 */
@Entity
@Path("Education")
public class AcademicTeacherCV {

    @Id
    @Field(modifiers = UserFieldModifier.MAIN)
    private AcademicTeacher teacher;

    @Field(modifiers = UserFieldModifier.SUMMARY)
    private String title1;
    private String subTitle1;

    private String title2;
    private String subTitle2;

    private String title3;
    private String subTitle3;

    @Field(max = "32000")
    @Properties(
            @Property(name = UIConstants.FIELD_FORM_CONTROL, value = UIConstants.ControlType.RICHTEXTAREA))
    private String aboutText;

    @Field(max = "32000")
    @Properties(
            @Property(name = UIConstants.FIELD_FORM_CONTROL, value = UIConstants.ControlType.RICHTEXTAREA))
    private String teachingText;

    @Field(max = "32000")
    @Properties(
            @Property(name = UIConstants.FIELD_FORM_CONTROL, value = UIConstants.ControlType.RICHTEXTAREA))
    private String researchText;

    @Field(max = "32000")
    @Properties(
            @Property(name = UIConstants.FIELD_FORM_CONTROL, value = UIConstants.ControlType.RICHTEXTAREA))
    private String educationText;

    @Field(max = "32000")
    @Properties(
            @Property(name = UIConstants.FIELD_FORM_CONTROL, value = UIConstants.ControlType.RICHTEXTAREA))
    private String projectsText;

    private String extraImage;

    private String extraTitle;

    @Properties(
            @Property(name = UIConstants.FIELD_FORM_CONTROL, value = UIConstants.ControlType.RICHTEXTAREA))
    @Field(max = "32000")
    private String extraText;

    private String wwwURL;
    private String socialURL1;
    private String socialURL2;
    private String socialURL3;
    private String socialURL4;
    private String rssURL;
    @Field(defaultValue = "0", modifiers = {UserFieldModifier.SUMMARY}, persistAccessLevel = AccessLevel.PROTECTED, updateAccessLevel = AccessLevel.PROTECTED, readAccessLevel = AccessLevel.PUBLIC
    )
    private long viewsCounter;

    public AcademicTeacher getTeacher() {
        return teacher;
    }

    public void setTeacher(AcademicTeacher teacher) {
        this.teacher = teacher;
    }

    public String getAboutText() {
        return aboutText;
    }

    public void setAboutText(String aboutText) {
        this.aboutText = aboutText;
    }

    public String getTeachingText() {
        return teachingText;
    }

    public void setTeachingText(String teachingText) {
        this.teachingText = teachingText;
    }

    public String getResearchText() {
        return researchText;
    }

    public void setResearchText(String researchText) {
        this.researchText = researchText;
    }

    public String getEducationText() {
        return educationText;
    }

    public void setEducationText(String educationText) {
        this.educationText = educationText;
    }

    public String getProjectsText() {
        return projectsText;
    }

    public void setProjectsText(String projectsText) {
        this.projectsText = projectsText;
    }

    public String getExtraImage() {
        return extraImage;
    }

    public void setExtraImage(String extraImage) {
        this.extraImage = extraImage;
    }

    public String getExtraTitle() {
        return extraTitle;
    }

    public void setExtraTitle(String extraTitle) {
        this.extraTitle = extraTitle;
    }

    public String getExtraText() {
        return extraText;
    }

    public void setExtraText(String extraText) {
        this.extraText = extraText;
    }

    public String getTitle1() {
        return title1;
    }

    public void setTitle1(String title1) {
        this.title1 = title1;
    }

    public String getTitle2() {
        return title2;
    }

    public void setTitle2(String title2) {
        this.title2 = title2;
    }

    public String getTitle3() {
        return title3;
    }

    public void setTitle3(String title3) {
        this.title3 = title3;
    }

    public String getWwwURL() {
        return wwwURL;
    }

    public void setWwwURL(String wwwURL) {
        this.wwwURL = wwwURL;
    }

    public String getSocialURL1() {
        return socialURL1;
    }

    public void setSocialURL1(String socialURL1) {
        this.socialURL1 = socialURL1;
    }

    public String getSocialURL2() {
        return socialURL2;
    }

    public void setSocialURL2(String socialURL2) {
        this.socialURL2 = socialURL2;
    }

    public String getSocialURL3() {
        return socialURL3;
    }

    public void setSocialURL3(String socialURL3) {
        this.socialURL3 = socialURL3;
    }

    public String getRssURL() {
        return rssURL;
    }

    public void setRssURL(String rssURL) {
        this.rssURL = rssURL;
    }

    public String getSocialURL4() {
        return socialURL4;
    }

    public void setSocialURL4(String socialURL4) {
        this.socialURL4 = socialURL4;
    }

    public long getViewsCounter() {
        return viewsCounter;
    }

    public void setViewsCounter(long viewsCounter) {
        this.viewsCounter = viewsCounter;
    }

    public String getSubTitle1() {
        return subTitle1;
    }

    public void setSubTitle1(String subTitle1) {
        this.subTitle1 = subTitle1;
    }

    public String getSubTitle2() {
        return subTitle2;
    }

    public void setSubTitle2(String subTitle2) {
        this.subTitle2 = subTitle2;
    }

    public String getSubTitle3() {
        return subTitle3;
    }

    public void setSubTitle3(String subTitle3) {
        this.subTitle3 = subTitle3;
    }

}

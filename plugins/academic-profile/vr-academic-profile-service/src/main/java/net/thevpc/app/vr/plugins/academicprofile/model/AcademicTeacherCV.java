/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.thevpc.app.vr.plugins.academicprofile.model;

import net.thevpc.app.vainruling.core.service.util.UIConstants;
import net.thevpc.app.vainruling.plugins.academic.model.config.AcademicTeacher;
import net.thevpc.upa.ProtectionLevel;
import net.thevpc.upa.config.*;

/**
 * @author taha.bensalah@gmail.com
 */
@Entity
@Path("Education")
public class AcademicTeacherCV {

    @Id
    @Main
    private AcademicTeacher teacher;

    @Summary
    private String title1;
    private String subTitle1;

    private String title2;
    private String subTitle2;

    private String title3;
    private String subTitle3;

    @Field(max = "max")
    @Properties(
            @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA))
    private String aboutText;

    @Field(max = "max")
    @Properties(
            @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA))
    private String teachingText;

    @Field(max = "max")
    @Properties(
            @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA))
    private String researchText;

    @Field(max = "max")
    @Properties(
            @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA))
    private String educationText;

    @Field(max = "max")
    @Properties(
            @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA))
    private String projectsText;

    private String extraImage;

    private String extraTitle;

    @Properties(
            @Property(name = UIConstants.Form.CONTROL, value = UIConstants.Control.TEXTAREA))
    @Field(max = "max")
    private String extraText;

    private String wwwURL;
    private String socialURL1;
    private String socialURL2;
    private String socialURL3;
    private String socialURL4;
    private String socialURL5;
    private String rssURL;
    @Summary
    @Field(protectionLevel = ProtectionLevel.PROTECTED)
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

    public String getSocialURL5() {
        return socialURL5;
    }

    public void setSocialURL5(String socialURL5) {
        this.socialURL5 = socialURL5;
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

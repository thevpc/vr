package net.vpc.app.vainruling.core.service.model;

import net.vpc.upa.config.Id;
import net.vpc.upa.config.Sequence;

import java.sql.Date;

//should move this to an external plugin!!
//@Entity(listOrder = "this.name")
public class AppEvent {
    @Id
    @Sequence
    private int id ;

    private Date beginDate;

    private Date endDate;
    private String description;
    private String name;
    private String url;

    public AppEvent(){}
    public int getId() { return id;}

    public void setId(int id) {this.id = id; }

    public Date getBeginDate() { return beginDate; }

    public void setBeginDate( Date beginDate) { this.beginDate = beginDate; }

    public Date getEndDate() {return endDate; }

    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public String getDescription() {  return description; }

    public void setDescription(String description) { this.description = description; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getUrl() { return url; }

    public void setUrl(String url) { this.url = url; }




}

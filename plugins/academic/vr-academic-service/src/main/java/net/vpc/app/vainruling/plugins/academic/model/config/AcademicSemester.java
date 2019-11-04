/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.model.config;

import net.vpc.common.strings.StringUtils;
import net.vpc.upa.config.*;

import java.util.Objects;

/**
 * semester 1 or 2 ...
 *
 * @author taha.bensalah@gmail.com
 */
@Entity(listOrder = "index, name")
@Path("Repository/Education")
public class AcademicSemester {

    @Path("Main")
    @Id
    @Sequence
    private int id;
    @Summary
    private int index;
    @Summary
    private String code;
    @Main
    private String name;
    private String name2;
    @Summary
    private String fromDate;
    @Summary
    private String toDate;

    public AcademicSemester() {
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public AcademicSemester(int id, String name) {
        this.id = id;
        this.name = name;
    }

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return StringUtils.nonNull(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AcademicSemester that = (AcademicSemester) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}

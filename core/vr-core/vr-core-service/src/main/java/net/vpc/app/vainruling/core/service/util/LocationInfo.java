package net.vpc.app.vainruling.core.service.util;

import net.vpc.app.vainruling.core.service.model.AppCompany;
import net.vpc.app.vainruling.core.service.model.AppCountry;
import net.vpc.app.vainruling.core.service.model.AppCountryRegion;
import net.vpc.app.vainruling.core.service.model.AppGovernorate;

/**
 * Created by vpc on 6/17/17.
 */
public class LocationInfo {

    private AppCompany company;
    private AppGovernorate governorate;
    private AppCountryRegion region;
    private AppCountry country;
    private String companyName;
    private String governorateName;
    private String regionName;
    private String countryName;

    public AppCompany getCompany() {
        return company;
    }

    public void setCompany(AppCompany company) {
        this.company = company;
    }

    public AppGovernorate getGovernorate() {
        return governorate;
    }

    public void setGovernorate(AppGovernorate governorate) {
        this.governorate = governorate;
    }

    public AppCountryRegion getRegion() {
        return region;
    }

    public void setRegion(AppCountryRegion region) {
        this.region = region;
    }

    public AppCountry getCountry() {
        return country;
    }

    public void setCountry(AppCountry country) {
        this.country = country;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getGovernorateName() {
        return governorateName;
    }

    public void setGovernorateName(String governorateName) {
        this.governorateName = governorateName;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
}

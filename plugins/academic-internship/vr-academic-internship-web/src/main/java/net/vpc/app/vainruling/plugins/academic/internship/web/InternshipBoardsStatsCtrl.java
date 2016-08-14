/*
 * To change this license header, choose License Headers in Project Properties.
 *
 * and open the template in the editor.
 */
package net.vpc.app.vainruling.plugins.academic.internship.web;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.VrApp;
import net.vpc.app.vainruling.core.service.model.AppCompany;
import net.vpc.app.vainruling.core.web.OnPageLoad;
import net.vpc.app.vainruling.core.web.UCtrl;
import net.vpc.app.vainruling.core.web.UPathItem;
import net.vpc.app.vainruling.core.web.util.ChartUtils;
import net.vpc.app.vainruling.plugins.academic.internship.service.AcademicInternshipPlugin;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.config.AcademicInternshipDuration;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.config.AcademicInternshipStatus;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.config.AcademicInternshipVariant;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.current.AcademicInternshipBoard;
import net.vpc.app.vainruling.plugins.academic.internship.service.model.ext.AcademicInternshipExtList;
import net.vpc.app.vainruling.plugins.academic.service.AcademicPlugin;
import net.vpc.app.vainruling.plugins.academic.service.model.config.AcademicTeacher;
import org.primefaces.model.chart.DonutChartModel;
import org.primefaces.model.chart.PieChartModel;

import java.util.*;

/**
 * internships for teachers
 *
 * @author taha.bensalah@gmail.com
 */
@UCtrl(
        breadcrumb = {
                @UPathItem(title = "Education", css = "fa-dashboard", ctrl = "")},
        css = "fa-table",
        title = "Stats Stages",
        menu = "/Education/Internship",
        securityKey = "Custom.Education.InternshipBoardsStat",
        url = "modules/academic/internship/internship-boards-stats"
)
public class InternshipBoardsStatsCtrl extends MyInternshipBoardsCtrl {

    @OnPageLoad
    public void onPageLoad() {
        super.onPageLoad();
    }

    @Override
    public List<AcademicInternshipBoard> findEnabledInternshipBoardsByTeacherAndBoard(int teacherId, int boardId) {
        AcademicPlugin p = VrApp.getBean(AcademicPlugin.class);
        AcademicInternshipPlugin pi = VrApp.getBean(AcademicInternshipPlugin.class);
        AcademicTeacher t = p.findTeacher(teacherId);
        if (t != null && t.getDepartment() != null) {
            return pi.findEnabledInternshipBoardsByDepartment(t.getDepartment().getId());
        }
        return new ArrayList<>();
    }

    @Override
    public AcademicInternshipExtList findActualInternshipsByTeacherAndBoard(int teacherId, int boardId, int internshipTypeId) {
        AcademicInternshipPlugin pi = VrApp.getBean(AcademicInternshipPlugin.class);
        AcademicTeacher t = getCurrentTeacher();
        return pi.findInternshipsByTeacherExt(
                -1,
                boardId,
                (t != null && t.getDepartment() != null) ? t.getDepartment().getId() : -1,
                internshipTypeId,
                true
        );
    }

    protected void onRefreshStats() {
        getModel().setDonut1(null);
        getModel().setDonut2(null);
        getModel().setDonut3(null);
        getModel().setDonut4(null);
        getModel().setDonut5(null);
        getModel().setBar1(null);
        getModel().setPie1(null);
        getModel().setPie2(null);

        if (!getModel().getInternshipInfos().isEmpty()) {
            {
                //Etats stages
                DonutChartModel d1 = new DonutChartModel();
                d1.setTitle("Préparation Stage");
                d1.setLegendPosition("e");
                d1.setSliceMargin(2);
                d1.setShowDataLabels(true);
                d1.setDataFormat("value");
                d1.setShadow(true);

                getModel().setDonut1(d1);

                Map<String, Number> circle1 = new LinkedHashMap<String, Number>();
                Map<String, Number> circle2 = new LinkedHashMap<String, Number>();

                circle1.put("Encad Affectés", 0);
                circle1.put("Encad Non Affectés", 0);
                circle2.put("Encad Affectés", 0);
                circle2.put("Encad Non Affectés", 0);

                for (AcademicInternshipInfo ii : getModel().getInternshipInfos()) {
                    String nn = ii.getInternship().getInternshipStatus().getName();
                    if (!circle1.containsKey(nn)) {
                        circle1.put(nn, 0);
                        circle2.put(nn, 0);
                    }
                }
//            for (AcademicInternshipStatus ast : allStatuses) {
//                circle1.put(ast.getName(), 0);
//                circle2.put(ast.getName(), 0);
//            }
                for (AcademicInternshipInfo ii : getModel().getInternshipInfos()) {
                    AcademicInternshipStatus s = ii.getInternship().getInternshipStatus();
                    String ss = s == null ? "?" : s.getName();
                    Number v = circle1.get(ss);
                    if (v == null) {
                        v = 1;
                    } else {
                        v = v.intValue() + 1;
                    }
                    circle1.put(ss, v);

                    ss = (ii.getInternship().getSupervisor() != null) ? "Encad Affectés" : "Encad Non Affectés";
                    v = circle2.get(ss);
                    if (v == null) {
                        v = 1;
                    } else {
                        v = v.intValue() + 1;
                    }
                    circle2.put(ss, v);
                }

                for (Map.Entry<String, Number> entry : circle1.entrySet()) {
                    if (!circle2.containsKey(entry.getKey())) {
                        circle2.put(entry.getKey(), 0);
                    }
                }

                d1.addCircle(circle1);
                d1.addCircle(circle2);
            }
            {
                //Etat Soutenance
                DonutChartModel d2 = new DonutChartModel();
                d2.setTitle("Préparation Soutenance");
                getModel().setDonut2(d2);

                d2.setLegendPosition("e");
                d2.setSliceMargin(2);
                d2.setShowDataLabels(true);
                d2.setDataFormat("value");
                d2.setShadow(true);

                Map<String, Number> circle3 = new LinkedHashMap<String, Number>();
                Map<String, Number> circle4 = new LinkedHashMap<String, Number>();
                circle3.put("Rapporteur Affectés", 0);
                circle3.put("Rapporteur Non Affectés", 0);
                circle3.put("Président Affectés", 0);
                circle3.put("Président Non Affectés", 0);
                circle4.put("Rapporteur Affectés", 0);
                circle4.put("Rapporteur Non Affectés", 0);
                circle4.put("Président Affectés", 0);
                circle4.put("Président Non Affectés", 0);

                for (Map.Entry<String, Number> entry : circle3.entrySet()) {
                    if (!circle4.containsKey(entry.getKey())) {
                        circle4.put(entry.getKey(), 0);
                    }
                }

                for (AcademicInternshipInfo ii : getModel().getInternshipInfos()) {
                    String ss;
                    Number v;

                    ss = (ii.getInternship().getChairExaminer() != null) ? "Président Affectés" : "Président Non Affectés";
                    v = circle3.get(ss);
                    if (v == null) {
                        v = 1;
                    } else {
                        v = v.intValue() + 1;
                    }
                    circle3.put(ss, v);

                    ss = (ii.getInternship().getFirstExaminer() != null) ? "Rapporteur Affectés" : "Rapporteur Non Affectés";
                    v = circle4.get(ss);
                    if (v == null) {
                        v = 1;
                    } else {
                        v = v.intValue() + 1;
                    }
                    circle4.put(ss, v);
                }

                d2.addCircle(circle3);
                d2.addCircle(circle4);
            }

            {
                DonutChartModel d1 = new DonutChartModel();
                d1.setTitle("Répartition Géographique");
                d1.setLegendPosition("e");
                d1.setSliceMargin(2);
                d1.setShowDataLabels(true);
                d1.setDataFormat("value");
                d1.setShadow(true);

                getModel().setDonut3(d1);

                Map<String, Number> circle1 = new LinkedHashMap<String, Number>();
                Map<String, Number> circle2 = new LinkedHashMap<String, Number>();

                CorePlugin cp = VrApp.getBean(CorePlugin.class);
                AppCompany currentCompany = cp.findAppConfig().getMainCompany();
                MyInternshipBoardsCtrl.LocationInfo currentLocation = resolveLocation(currentCompany);
                String id_company = currentLocation.companyName;
                String id_governorate = currentLocation.governorateName + " Sauf " + currentLocation.companyName;
                String id_region = currentLocation.regionName + " Sauf " + currentLocation.governorateName;
                String id_country = currentLocation.countryName + " Sauf " + currentLocation.regionName;
                String id_International = "Intenational";
                String id_Unknown = "Inconnu";
                circle1.put(id_company, 0);
                circle1.put(id_governorate, 0);
                circle1.put(id_region, 0);
                circle1.put(id_country, 0);
                circle1.put(id_International, 0);
                circle1.put(id_Unknown, 0);
//                circle2.put(id_company, 0);
//                circle2.put(id_governorate, 0);
//                circle2.put(id_region, 0);
//                circle2.put(id_country, 0);
//                circle2.put(id_International, 0);
//                circle2.put(id_Unknown, 0);

                for (AcademicInternshipInfo ii : getModel().getInternshipInfos()) {
                    AppCompany c = ii.getInternship().getCompany();
                    MyInternshipBoardsCtrl.LocationInfo lc = resolveLocation(c);
                    String gouvernorateName = lc.governorate == null ? "Gouvernorat inconnu" : lc.governorate.getName();
                    Number v2 = circle2.get(gouvernorateName);
                    if (v2 == null) {
                        v2 = 1;
//                        circle1.put(gouvernorateName, 0);
                        circle2.put(gouvernorateName, v2);
                    } else {
                        circle2.put(gouvernorateName, v2.intValue() + 1);
                    }

                    Boolean same_company = null;
                    Boolean same_governorate = null;
                    Boolean same_region = null;
                    Boolean same_country = null;
                    String best_id = null;
                    String first_diff = null;

                    if (lc.country != null && currentLocation.country != null) {
                        same_country = lc.country.getId() == currentLocation.country.getId();
                    }
                    if (lc.region != null && currentLocation.region != null) {
                        same_region = lc.region.getId() == currentLocation.region.getId();
                    }
                    if (lc.governorate != null && currentLocation.governorate != null) {
                        same_governorate = lc.governorate.getId() == currentLocation.governorate.getId();
                    }
                    if (lc.company != null && currentLocation.company != null) {
                        same_company = lc.company.getId() == currentLocation.company.getId();
                    }

                    if (same_company != null) {
                        if (same_company) {
                            best_id = id_company;
                        } else {
                            first_diff = id_company;
                        }
                    }

                    if (best_id == null) {
                        if (same_governorate != null) {
                            if (same_governorate) {
                                best_id = id_governorate;
                            } else {
                                first_diff = id_governorate;
                            }
                        }
                    }

                    if (best_id == null) {
                        if (same_region != null) {
                            if (same_region) {
                                best_id = id_region;
                            } else {
                                first_diff = id_region;
                            }
                        }
                    }

                    if (best_id == null) {
                        if (same_country != null) {
                            if (same_country) {
                                best_id = id_country;
                            } else {
                                first_diff = id_country;
                            }
                        }
                    }
                    if (best_id != null) {
                        //good found it
                    } else if (first_diff == null) {
                        best_id = id_Unknown;
                    } else if (first_diff.equals(id_country)) {
                        best_id = id_International;
                    } else if (first_diff.equals(id_region)) {
                        best_id = id_country;
                    } else if (first_diff.equals(id_governorate)) {
                        if (currentLocation.region != null) {
                            best_id = id_region;
                        } else {
                            best_id = id_country;
                        }
                    } else if (first_diff.equals(id_company)) {
                        best_id = id_governorate;
                    }
                    if (best_id == null) {
                        if (same_country != null) {
                            if (same_country) {
                                if (same_region != null) {
                                    if (same_region) {
                                        if (same_region != null) {
                                            if (same_region) {

                                            }
                                        } else {
                                            best_id = id_country;
                                        }

                                    }
                                } else {
                                    best_id = id_country;
                                }
                            } else {
                                best_id = id_International;
                            }
                        }
                    }
                    if (best_id != null) {
                        Integer old = ((Integer) circle1.get(best_id));
                        if (old == null) {
                            old = 0;
                        }
                        circle1.put(best_id, old + 1);
                    }
//                    if (same_company != null && same_company) {
//                        circle2.put(id_company, ((Integer) circle1.get(id_company)) + 1);
//                    }
//                    if (same_governorate != null && same_governorate) {
//                        circle2.put(id_governorate, ((Integer) circle1.get(id_governorate)) + 1);
//                    }
//                    if (same_governorate != null && same_governorate) {
//                        circle2.put(id_governorate, ((Integer) circle1.get(id_governorate)) + 1);
//                    }
//                    if (lc.country != null && currentLocation.country != null && lc.country.getId() == currentLocation.country.getId()) {
//                    } else if (lc.country != null) {
//                        circle1.put(id_International, ((Integer) circle1.get(id_International)) + 1);
//                    } else if (lc.region != null && currentLocation.region != null && lc.region.getId() == currentLocation.region.getId()) {
//                        circle1.put(id_region, ((Integer) circle1.get(id_region)) + 1);
//                    } else if (lc.company != null && currentLocation.company != null && lc.company.getId() == currentLocation.company.getId()) {
//                        circle1.put(id_company, ((Integer) circle1.get(id_company)) + 1);
//                    } else if (lc.governorate != null && currentLocation.governorate != null && lc.governorate.getId() == currentLocation.governorate.getId()) {
//                        circle1.put(id_governorate, ((Integer) circle1.get(id_governorate)) + 1);
//                    } else {
//                        circle1.put(id_Unknown, ((Integer) circle1.get(id_Unknown)) + 1);
//                    }
                }

//                for (Map.Entry<String, Number> entry : circle1.entrySet()) {
//                    if (!circle2.containsKey(entry.getKey())) {
//                        circle2.put(entry.getKey(), 0);
//                    }
//                }
                ChartUtils.mergeMapKeys(circle1, circle2);
                d1.addCircle(circle1);
                d1.addCircle(circle2);
            }
            // disciplines & technologies
//            {
//                DonutChartModel d1 = new DonutChartModel();
//                d1.setLegendPosition("e");
//                d1.setSliceMargin(2);
//                d1.setShowDataLabels(true);
//                d1.setDataFormat("value");
//                d1.setShadow(true);
//
//                getModel().setDonut4(d1);
//
//                Map<String, Number> circle1_ = new LinkedHashMap<String, Number>();
//                Map<String, Number> circle2_ = new LinkedHashMap<String, Number>();
//
//                AcademicPlugin academicPlugin = VrApp.getBean(AcademicPlugin.class);
//                for (AcademicInternshipInfo ii : getModel().getInternshipInfos()) {
//
//                    HashSet<String> set = new HashSet<String>(academicPlugin.parseDisciplinesNames(ii.getInternship().getMainDiscipline(), false));
//                    for (String s0 : set) {
//                        Number y = circle1_.get(s0);
//                        if (y == null) {
//                            y = 1;
//                        } else {
//                            y = y.intValue() + 1;
//                        }
//                        circle1_.put(s0, y);
//                    }
//                    set = new HashSet<String>(academicPlugin.parseWords(ii.getInternship().getTechnologies()));
//                    for (String s0 : set) {
//                        Number y = circle2_.get(s0);
//                        if (y == null) {
//                            y = 1;
//                        } else {
//                            y = y.intValue() + 1;
//                        }
//                        circle2_.put(s0, y);
//                    }
//                }
//                circle1_ = reverseSort(circle1_, 5, "Autres Disc.");
//                circle2_ = reverseSort(circle2_, 5, "Autres Technos");
//
//                Map<String, Number> circle1 = new LinkedHashMap<String, Number>();
//                Map<String, Number> circle2 = new LinkedHashMap<String, Number>();
//
//                circle1.putAll(circle1_);
//                for (String k : circle2_.keySet()) {
//                    circle1.put(k, 0);
//                }
//
//                for (String k : circle1_.keySet()) {
//                    circle2.put(k, 0);
//                }
//                circle2.putAll(circle2_);
//                d1.addCircle(circle1);
//                d1.addCircle(circle2);
//            }


            // disciplines & technologies
            {
                PieChartModel d1 = new PieChartModel();
                d1.setTitle("Disciplines");
                d1.setLegendPosition("e");
                d1.setSliceMargin(2);
                d1.setShowDataLabels(true);
                d1.setDataFormat("value");
                d1.setShadow(true);
                d1.setFill(true);

                PieChartModel d2 = new PieChartModel();
                d2.setTitle("Technologies");
                d2.setLegendPosition("e");
                d2.setSliceMargin(2);
                d2.setShowDataLabels(true);
                d2.setDataFormat("value");
                d2.setShadow(true);
                d2.setFill(true);

                getModel().setPie1(d1);
                getModel().setPie2(d2);

                Map<String, Number> circle1 = new LinkedHashMap<String, Number>();
                Map<String, Number> circle2 = new LinkedHashMap<String, Number>();

                AcademicPlugin academicPlugin = VrApp.getBean(AcademicPlugin.class);
                for (AcademicInternshipInfo ii : getModel().getInternshipInfos()) {

                    HashSet<String> set = new HashSet<String>(academicPlugin.parseDisciplinesNames(ii.getInternship().getMainDiscipline(), false));
                    for (String s0 : set) {
                        Number y = circle1.get(s0);
                        if (y == null) {
                            y = 1;
                        } else {
                            y = y.intValue() + 1;
                        }
                        circle1.put(s0, y);
                    }
                    set = new HashSet<String>(academicPlugin.parseWords(ii.getInternship().getTechnologies()));
                    for (String s0 : set) {
                        Number y = circle2.get(s0);
                        if (y == null) {
                            y = 1;
                        } else {
                            y = y.intValue() + 1;
                        }
                        circle2.put(s0, y);
                    }
                }
                circle1 = ChartUtils.reverseSortCount(circle1, 10, "Autres Disc.");
                circle2 = ChartUtils.reverseSortCount(circle2, 13, "Autres Technos");

                d1.setData(circle1);
                d2.setData(circle2);
            }

            //periods
            {
                DonutChartModel d1 = new DonutChartModel();
                d1.setTitle("Périodes/Variantes");
                d1.setLegendPosition("e");
                d1.setSliceMargin(2);
                d1.setShowDataLabels(true);
                d1.setDataFormat("value");
                d1.setShadow(true);

                getModel().setDonut5(d1);

                Map<String, Number> circle1 = new LinkedHashMap<String, Number>();
                Map<String, Number> circle2 = new LinkedHashMap<String, Number>();

                for (AcademicInternshipInfo ii : getModel().getInternshipInfos()) {
                    AcademicInternshipVariant v = ii.getInternship().getInternshipVariant();
                    String s0 = v == null ? "Autre Variante" : v.getName();
                    Number y = circle1.get(s0);
                    if (y == null) {
                        y = 1;
                    } else {
                        y = y.intValue() + 1;
                    }
                    circle1.put(s0, y);

                    AcademicInternshipDuration v2 = ii.getInternship().getDuration();
                    s0 = v2 == null ? "Autre Durée" : v2.getName();
                    y = circle2.get(s0);
                    if (y == null) {
                        y = 1;
                    } else {
                        y = y.intValue() + 1;
                    }
                    circle2.put(s0, y);
                }
                ChartUtils.mergeMapKeys(circle1, circle2);
                d1.addCircle(circle1);
                d1.addCircle(circle2);
            }

            //Teachers


        }
    }

    @Override
    public void onRefreshListMode() {
        super.onRefreshListMode();
        onRefreshStats();
    }


}

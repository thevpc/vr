package net.vpc.app.vainruling.plugins.academic.pbl.service.dto;

import net.vpc.app.vainruling.core.service.model.AppPeriod;
import net.vpc.app.vainruling.core.service.util.UIConstants;
import net.vpc.upa.config.*;

import java.util.Date;

/**
 * Created by vpc on 9/19/16.
 */
public enum ApblSessionLoadStrategy {
    ALL_STUDENTS_COUNT,
    TEAMED_STUDENTS_COUNT,
    COACHED_STUDENTS_COUNT,
    CUSTOM_LOAD,
    CUSTOM_STUDENTS_COUNT,
}

package net.vpc.app.vainruling.core.service.util;

import net.vpc.app.vainruling.core.service.model.AppContact;
import net.vpc.app.vainruling.core.service.model.AppUser;

public interface VrPasswordStrategy {
    String generatePassword(AppContact user);
}

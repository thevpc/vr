package net.thevpc.app.vainruling.core.service.util;

import net.thevpc.app.vainruling.core.service.model.AppUser;

public interface VrPasswordStrategy {
    String generatePassword(AppUser user);
}

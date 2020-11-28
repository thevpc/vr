package net.thevpc.app.vainruling.core.service.util;

import net.thevpc.app.vainruling.core.service.model.AppUser;
import net.thevpc.common.strings.StringUtils;

public class VrPasswordStrategyNin implements VrPasswordStrategy {
    public static final VrPasswordStrategy INSTANCE = new VrPasswordStrategyNin();

    @Override
    public String generatePassword(AppUser contact) {
        String nin = contact.getNin();
        if (StringUtils.isBlank(nin)) {
            throw new IllegalArgumentException("Empty NIN, unable to generate password");
        }
        return nin.trim();
    }
}

package net.thevpc.app.vainruling.plugins.inbox.service;

import net.thevpc.app.vainruling.core.service.util.VrPlatformUtils;

public class MailboxPluginSecurity {
    public static final String RIGHT_CUSTOM_SITE_MAILBOX = "Custom.Site.Mailbox";
    public static final String RIGHT_CUSTOM_ARTICLE_SEND_EXTERNAL_EMAIL = "Custom.Article.SendExternalEmail";
    public static final String RIGHT_CUSTOM_ARTICLE_SEND_INTERNAL_EMAIL = "Custom.Article.SendInternalEmail";
    public static final String RIGHT_CUSTOM_INBOX = "Custom.Inbox";
    public static final String[] RIGHTS_CORE = VrPlatformUtils.getStringArrayConstantsValues(MailboxPluginSecurity.class,"RIGHT_*");
}

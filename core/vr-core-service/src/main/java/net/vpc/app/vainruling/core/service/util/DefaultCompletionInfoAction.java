package net.vpc.app.vainruling.core.service.util;

public class DefaultCompletionInfoAction implements CompletionInfoAction{
    private String name;

    private String url;

    private String style;

    public DefaultCompletionInfoAction(String name, String url, String style) {
        this.name = name;
        this.url = url;
        this.style = style;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String getStyle() {
        return style;
    }
}

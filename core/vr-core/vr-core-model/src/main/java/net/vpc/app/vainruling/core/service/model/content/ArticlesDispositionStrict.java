package net.vpc.app.vainruling.core.service.model.content;

public class ArticlesDispositionStrict {

    private int id;
    private String name;
    private boolean enabled;

    public ArticlesDispositionStrict() {

    }

    public ArticlesDispositionStrict(AppArticleDisposition disposition) {
        this.id = disposition.getId();
        this.name = disposition.getName();
        this.enabled = disposition.isEnabled();
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
}

package net.vpc.app.vainruling.core.service.model.content;

public class ArticlesDispositionStrict {
    private int id;
    private String name;
    public ArticlesDispositionStrict(AppArticleDisposition disposition) {
        this.id=disposition.getId();
        this.name=disposition.getName();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

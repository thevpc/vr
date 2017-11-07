package net.vpc.app.vainruling.core.service.util.wiki;

import net.vpc.app.vainruling.core.service.CorePlugin;
import net.vpc.app.vainruling.core.service.security.UserSession;
import org.sweble.wikitext.engine.PageTitle;
import org.sweble.wikitext.engine.config.WikiConfig;
import org.sweble.wikitext.engine.output.HtmlRenderer;
import org.sweble.wikitext.engine.output.HtmlRendererCallback;
import org.sweble.wikitext.parser.nodes.*;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;

/**
 * @author taha.bensalah@gmail.com on 7/25/16.
 */
public class VrHtmlRenderer extends HtmlRenderer {
    public VrHtmlRenderer(HtmlRendererCallback callback, WikiConfig wikiConfig, PageTitle pageTitle, Writer w) {
        super(callback, wikiConfig, pageTitle, w);
    }


    public void visit(WtExternalLink n)
    {
        if (n.hasTitle())
        {
            p.indentAtBol();

            pt("<a rel=\"nofollow\" class=\"external text\" href=\"%s\">%!</a>",
                    callback.makeUrl(n.getTarget()),
                    n.getTitle());
        }
        else
        {
            p.indentAtBol();

            pt("<a rel=\"nofollow\" class=\"external text\" href=\"%s\">%!</a>",
                    callback.makeUrl(n.getTarget()),
                    "link");

            //throw new FmtNotYetImplementedError();
        }
    }

    @Override
    public void visit(WtHeading n)
    {
        // We handle this case in WtSection and don't dispatch to the heading.
        p.print(n.toString());
    }

    @Override
    public void visit(WtImEndTag n)
    {
        // Should not happen ...
        p.print(n.toString());
    }

    @Override
    public void visit(WtImStartTag n)
    {
        // Should not happen ...
        p.print(n.toString());
    }

    @Override
    public void visit(WtLinkOptionAltText n)
    {
        // Should not happen ...
        p.print(n.toString());
    }

    @Override
    public void visit(WtLinkOptionGarbage n)
    {
        // Should not happen ...
        p.print(n.toString());
    }

    @Override
    public void visit(WtLinkOptionKeyword n)
    {
        // Should not happen ...
        p.print(n.toString());
    }

    @Override
    public void visit(WtLinkOptionLinkTarget n)
    {
        // Should not happen ...
        p.print(n.toString());
    }

    @Override
    public void visit(WtLinkOptionResize n)
    {
        // Should not happen ...
        p.print(n.toString());
    }

    @Override
    public void visit(WtLinkOptions n)
    {
        // Should not happen ...
        p.print(n.toString());
    }

    @Override
    public void visit(WtPageName n)
    {
        // Should not happen ...
        p.print(n.toString());
    }
    @Override
    public void visit(WtRedirect n)
    {
        p.print(n.toString());
        // TODO: Implement
    }

    @Override
    public void visit(WtSignature n)
    {
        UserSession s = CorePlugin.get().getCurrentSession();

        String usr = (s == null || s.getUser() == null) ? "anonymous" : s.getUser().getLogin();
        switch (n.getTildeCount()){
            case 3:{
                p.print(usr);
                break;
            }
            case 4:{
                p.print(usr);
                p.print(" ");
                p.print(new Date().toString());
                break;
            }
            case 5:{
                p.print(new Date().toString());
                break;
            }
            default:{
                p.print(usr);
            }
        }
    }

    public static <T extends WtNode> String print(
            HtmlRendererCallback callback,
            WikiConfig wikiConfig,
            PageTitle pageTitle,
            T node)
    {
        return print(callback, wikiConfig, new StringWriter(), pageTitle, node).toString();
    }

    public static <T extends WtNode> Writer print(
            HtmlRendererCallback callback,
            WikiConfig wikiConfig,
            Writer writer,
            PageTitle pageTitle,
            T node)
    {
        new VrHtmlRenderer(callback, wikiConfig, pageTitle, writer).go(node);
        return writer;
    }
}

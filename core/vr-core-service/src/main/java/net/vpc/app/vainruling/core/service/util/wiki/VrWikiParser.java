package net.vpc.app.vainruling.core.service.util.wiki;

import de.fau.cs.osr.utils.StringUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.sweble.wikitext.engine.PageId;
import org.sweble.wikitext.engine.PageTitle;
import org.sweble.wikitext.engine.WtEngineImpl;
import org.sweble.wikitext.engine.config.WikiConfig;
import org.sweble.wikitext.engine.nodes.EngProcessedPage;
import org.sweble.wikitext.engine.output.HtmlRenderer;
import org.sweble.wikitext.engine.output.HtmlRendererCallback;
import org.sweble.wikitext.engine.output.MediaInfo;
import org.sweble.wikitext.engine.utils.DefaultConfigEnWp;
import org.sweble.wikitext.engine.utils.UrlEncoding;
import org.sweble.wikitext.parser.nodes.WtUrl;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author taha.bensalah@gmail.com on 7/24/16.
 */
public class VrWikiParser {
    private static final Logger log = Logger.getLogger(VrWikiParser.class.getName());

    public static String convertToHtml(String value, String title) {
        return renderWikiText(value, title, true);
    }

    public static String convertToText(String value, String title) {
        return renderWikiText(value, title, false);
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: java -jar swc-example-basic-archetype-VERSION.jar [--html|--text] TITLE");
            System.err.println();
            System.err.println("  The program will look for a file called `TITLE.wikitext',");
            System.err.println("  parse the file and write an HTML version to `TITLE.html'.");
            return;
        }

        boolean renderHtml = true;

        int i = 0;
        if (args[i].equalsIgnoreCase("--html")) {
            renderHtml = true;
            ++i;
        } else if (args[i].equalsIgnoreCase("--text")) {
            renderHtml = false;
            ++i;
        }

        String fileTitle = args[i];

        String html = renderWikiText(
                new File(fileTitle + ".wikitext"),
                fileTitle,
                renderHtml);

        FileUtils.writeStringToFile(
                new File(fileTitle + (renderHtml ? ".html" : ".text")),
                html);
    }

    static String renderWikiText(File file, String fileTitle, boolean renderHtml) throws Exception {
        // Set-up a simple wiki configuration
        WikiConfig config = DefaultConfigEnWp.generate();

        final int wrapCol = 80;

        // Instantiate a compiler for wiki pages
        WtEngineImpl engine = new WtEngineImpl(config);

        // Retrieve a page
        PageTitle pageTitle = PageTitle.make(config, fileTitle);

        PageId pageId = new PageId(pageTitle, -1);

        String wikitext = FileUtils.readFileToString(file);

        // Compile the retrieved page
        EngProcessedPage cp = engine.postprocess(pageId, wikitext, null);

        if (renderHtml) {
            String ourHtml = HtmlRenderer.print(new MyRendererCallback(), config, pageTitle, cp.getPage());

            String template = IOUtils.toString(VrWikiParser.class.getResourceAsStream("render-template.html"), "UTF8");
            String html = template;
            html = html.replace("{$TITLE}", StringUtils.escHtml(pageTitle.getDenormalizedFullTitle()));
            html = html.replace("{$CONTENT}", ourHtml);

            return html;
        } else {
            TextConverter p = new TextConverter(config, wrapCol);
            return (String) p.go(cp.getPage());
        }
    }

    static String renderWikiText(String wikitext, String fileTitle, boolean renderHtml) {
        try {
            // Set-up a simple wiki configuration
            WikiConfig config = DefaultConfigEnWp.generate();

            final int wrapCol = 80;

            // Instantiate a compiler for wiki pages
            WtEngineImpl engine = new WtEngineImpl(config);

            // Retrieve a page
            PageTitle pageTitle = PageTitle.make(config, fileTitle);

            PageId pageId = new PageId(pageTitle, -1);

            // Compile the retrieved page
            EngProcessedPage cp = engine.postprocess(pageId, wikitext, null);

            if (renderHtml) {
                String template = IOUtils.toString(VrWikiParser.class.getResourceAsStream("render-template.html"), "UTF8");
                String ourHtml = wikitext;
                try {
                    ourHtml = VrHtmlRenderer.print(new MyRendererCallback(), config, pageTitle, cp.getPage());
                } catch (Exception ex) {
                    log.log(Level.SEVERE, "Unable to convert wiki to html", ex);
                }
                String html = template;
                html = html.replace("{$TITLE}", StringUtils.escHtml(pageTitle.getDenormalizedFullTitle()));
                html = html.replace("{$CONTENT}", ourHtml);

                return html;
            } else {
                TextConverter p = new TextConverter(config, wrapCol);
                return (String) p.go(cp.getPage());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static final class MyRendererCallback
            implements
            HtmlRendererCallback {
        protected static final String LOCAL_URL = "";

        @Override
        public boolean resourceExists(PageTitle target) {
            // TODO: Add proper check
            return false;
        }

        @Override
        public MediaInfo getMediaInfo(String title, int width, int height) throws Exception {
            // TODO: Return proper media info
            return null;
        }

        @Override
        public String makeUrl(PageTitle target) {
            String page = UrlEncoding.WIKI.encode(target.getNormalizedFullTitle());
            String f = target.getFragment();
            String url = page;
            if (f != null && !f.isEmpty())
                url = page + "#" + UrlEncoding.WIKI.encode(f);
            return LOCAL_URL + "/" + url;
        }

        @Override
        public String makeUrl(WtUrl target) {
            if (target.getProtocol() == "")
                return target.getPath();
            return target.getProtocol() + ":" + target.getPath();
        }

        @Override
        public String makeUrlMissingTarget(String path) {
            return LOCAL_URL + "?title=" + path + "&amp;action=edit&amp;redlink=1";

        }
    }
}

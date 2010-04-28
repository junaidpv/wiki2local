/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package wiki2local;

import java.util.*;
import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import javax.swing.text.html.parser.ParserDelegator;

/**
 *
 * @author Junaid
 */
public class WikiPageExtractor {
    public List<String> wikiPageList;
    public String basePath;
    public String pageRequestFormat;
    public List<String> htmlPageList;
    public ImageExtractor imageExtractor;
    public FileOutputStream logger;
    public WikiPageExtractor(List<String> wikiPageList, String basePath, String lan, String project, File loggerFile) throws IOException {
        this.wikiPageList = wikiPageList;
        if(basePath == null) {
            throw new NullPointerException("given base is null");
        }
        else this.basePath = basePath;
        if(loggerFile == null) {
            throw new NullPointerException("given file logger is null.");
        }else if (!loggerFile.exists()) {
            throw new FileNotFoundException("given file logger does not exists.");
        }
        else {
            this.logger = new FileOutputStream(loggerFile);
        }
        File baseDir = new File(basePath);
        if(!baseDir.exists()) baseDir.mkdir();
        this.pageRequestFormat = "http://"+lan+"."+project+".org/w/api.php?action=parse&page=%s&format=xml";
        this.htmlPageList = new LinkedList<String>();
        this.imageExtractor = new ImageExtractor(this.basePath, htmlPageList, loggerFile );
    }

    public void extractPages() throws Exception {
        Iterator i = this.wikiPageList.iterator();
        WikiPageParserCallback callback = new WikiPageParserCallback(this.imageExtractor);
        int itemNum = 0;
        File imgDir = new File(this.basePath+"images/");
        if(!imgDir.exists()) imgDir.mkdir();
        while(i.hasNext()) {
            String wikiPage = (String) i.next();
            String pageUrl = String.format(this.pageRequestFormat, wikiPage);
            String fileName = String.format("%spage_%06d.html", this.basePath, itemNum );
            this.htmlPageList.add(fileName);
            FileOutputStream fos = new FileOutputStream(new File(fileName));
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document dd = db.parse(pageUrl);
            NodeList parseNodeList = dd.getElementsByTagName("parse");
            String pageTitle = parseNodeList.item(0).getAttributes().getNamedItem("displaytitle").getTextContent();
            NodeList textNodeList = dd.getElementsByTagName("text");
            String prepend = String.format("<html><head><title>%s</title></head><body><div><h1>%s</h1>", pageTitle, pageTitle);
            String append = "</div></body></html>";
            String text = textNodeList.item(0).getTextContent();
            String s = prepend + text +append;
            Reader reader = new CharArrayReader(s.toCharArray());
            new ParserDelegator().parse(reader, callback, true);
            Iterator mapi = this.imageExtractor.entrySet().iterator();
            while(mapi.hasNext()) {
                Map.Entry entry = (Map.Entry) mapi.next();
                String oldSrc = entry.getKey().toString();
                String newSrc = entry.getValue().toString();
                s = s.replace(oldSrc, "images/"+newSrc);
            }
            fos.write(s.getBytes());
            fos.close();
            itemNum++;
        }
        this.imageExtractor.extractImages();
    }

    public List<String> getHtmlFileList() {
        return this.htmlPageList;
    }

}

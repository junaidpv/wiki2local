/* File: WikiPageExtractor.java
 * $Author$
 * $LastChangedDate$
 * $Rev$
 * Licensed under GPL v3
 */
package wiki2local;

import java.util.*;
import java.io.*;
import java.io.BufferedWriter;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import javax.swing.text.html.parser.ParserDelegator;
import org.xml.sax.SAXException;

/**
 *
 * @author Junaid
 * @version 0.5
 * @since 0.1
 */
public class WikiPageExtractor {
    //public List<String> wikiPageList;

    public File baseDir;
    public String pageRequestFormat;
    public HashMap<String, String> htmlPageFileList;
    public ImageExtractor imageExtractor;
    public FileOutputStream logger;

    public WikiPageExtractor(HashMap<String, String> wikiPageList, File baseDir, String lan, String project, File loggerFile)
            throws FileNotFoundException {
        this.htmlPageFileList = wikiPageList;
        if (baseDir == null) {
            throw new NullPointerException("given base path is null");
        }
        else if(!baseDir.exists() || !baseDir.isDirectory()) {
            throw new FileNotFoundException("give base directory not exist.");
        }
        else this.baseDir = baseDir;
        if (loggerFile == null) {
            throw new NullPointerException("given file logger is null.");
        } else if (!loggerFile.exists()) {
            throw new FileNotFoundException("given file logger does not exists.");
        } else {
            this.logger = new FileOutputStream(loggerFile);
        }
        this.pageRequestFormat = "http://" + lan + "." + project + ".org/w/api.php?action=parse&page=%s&format=xml";
        this.imageExtractor = new ImageExtractor(this.baseDir, htmlPageFileList, loggerFile);
    }

    public void extractPages()
            throws UnsupportedEncodingException,
            ParserConfigurationException, SAXException, FileNotFoundException, IOException {
        Iterator i = this.htmlPageFileList.entrySet().iterator();
        WikiPageParserCallback callback= new WikiPageParserCallback(this.imageExtractor);
        int itemNum = 0;
        File pageTemplateFile = new File(this.baseDir, "page_template.html");
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(pageTemplateFile),"UTF-8"));
        StringBuilder textBuffer = new StringBuilder();
        char[] buffer = new char[1024];
        int numread;
        do {
            numread = br.read(buffer);
            if(numread>0) textBuffer.append(buffer,0,numread);
        } while(numread != -1);
        br.close();
        String pageText = textBuffer.toString();
        while (i.hasNext()) {
            Map.Entry<String, String> pageEntry = (Map.Entry) i.next();
            String pageUrl = String.format(this.pageRequestFormat, pageEntry.getKey());
            String uniqueFileName = String.format("page_%05d.html",itemNum);
            this.htmlPageFileList.put(pageEntry.getKey(), uniqueFileName);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(this.baseDir, uniqueFileName)), "UTF-8"));
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document dd = db.parse(pageUrl);
            NodeList parseNodeList = dd.getElementsByTagName("parse");
            String pageTitle = parseNodeList.item(0).getAttributes().getNamedItem("displaytitle").getTextContent();
            NodeList textNodeList = dd.getElementsByTagName("text");
            String text = textNodeList.item(0).getTextContent();
            pageText = pageText.replaceAll("\\{\\$TITLE\\$\\}", pageTitle);
            pageText = pageText.replaceAll("\\{\\$CONTENT\\$\\}", text);
            Reader reader = new CharArrayReader(pageText.toCharArray());
            new ParserDelegator().parse(reader, callback, true);
            Iterator mapi = this.imageExtractor.entrySet().iterator();
            while (mapi.hasNext()) {
                Map.Entry entry = (Map.Entry) mapi.next();
                String oldSrc = entry.getKey().toString();
                String newSrc = entry.getValue().toString();
                pageText = pageText.replace(oldSrc, "images/" + newSrc);
            }
            bw.write(pageText);
            bw.close();
            itemNum++;
        }
        this.imageExtractor.extractImages();
    }

    public HashMap<String, String> getHtmlFileList() {
        return this.htmlPageFileList;
    }
}

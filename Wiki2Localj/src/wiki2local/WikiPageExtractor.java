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
import java.util.regex.Matcher;
import javax.swing.text.BadLocationException;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import javax.swing.text.html.parser.ParserDelegator;
import org.xml.sax.SAXException;

/**
 *
 * @author Junaid
 * @version 0.6
 * @since 0.1
 */
public class WikiPageExtractor {

    private File baseDir;
    private String pageRequestFormat;
    private HashMap<String, String> htmlPageFileList;
    private ImageExtractor imageExtractor;
    //private FileOutputStream logger;

    public WikiPageExtractor(HashMap<String, String> wikiPageList, File baseDir, String lan, String project)
            throws FileNotFoundException {
        this.htmlPageFileList = wikiPageList;
        if (baseDir == null) {
            throw new NullPointerException("given base path is null");
        } else if (!baseDir.exists() || !baseDir.isDirectory()) {
            throw new FileNotFoundException("give base directory not exist.");
        } else {
            this.baseDir = baseDir;
        }
        /*
        if (loggerFile == null) {
        throw new NullPointerException("given file logger is null.");
        } else if (!loggerFile.exists()) {
        throw new FileNotFoundException("given file logger does not exists.");
        } else {
        this.logger = new FileOutputStream(loggerFile);
        }*/
        this.pageRequestFormat = "http://" + lan + "." + project + ".org/w/api.php?action=parse&page=%s&format=xml";
        this.imageExtractor = new ImageExtractor(this.baseDir, htmlPageFileList);
    }

    /**
     * Method to extract list pf wiki pages from wiki of specified type and language
     * @throws UnsupportedEncodingException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void extractPages()
            throws UnsupportedEncodingException, BadLocationException,
            ParserConfigurationException, SAXException, FileNotFoundException, IOException {
        // call back object helps while parsing html pages
        WikiPageParserCallback callback = new WikiPageParserCallback(this.imageExtractor);
        int itemNum = 0;
        // page template html file
        File pageTemplateFile = new File(this.baseDir, "page_template.html");
        // preparing to read from template file
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(pageTemplateFile), "UTF-8"));
        StringBuilder textBuffer = new StringBuilder();
        char[] buffer = new char[1024]; // temporary buffer to store read characters
        int numread;
        do {
            numread = br.read(buffer);  // read next sequence of available characters
            if (numread > 0) {
                textBuffer.append(buffer, 0, numread);  // if at least on character read, append it ti text buffer
            }
        } while (numread != -1); // while end of file
        br.close(); // close underlying stream resources
        // iterator over the list of html pages
        Iterator i = this.htmlPageFileList.entrySet().iterator();
        while (i.hasNext()) {   // while list has next element
            String tempText = textBuffer.toString();    // get template file text in string object
            Map.Entry<String, String> pageEntry = (Map.Entry<String, String>) i.next();
            // prepare wiki api request url from its format and argument(s)
            String pageUrl = String.format(this.pageRequestFormat, pageEntry.getKey());
            // file name to which wiki page contents to be written
            String uniqueFileName = String.format("page_%05d.html", itemNum);
            // pair generated file name wiki wiki page name
            // so image extractor end toc tree preparing codes can use it later
            this.htmlPageFileList.put(pageEntry.getKey(), uniqueFileName);
            // buffered writer object to write wikipage contnets to file as utf-8 encoded text
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(this.baseDir, uniqueFileName)), "UTF-8"));
            // create document builder object, which will help to parse obtained api result string
            // which is in xml format
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document dd = db.parse(pageUrl);    // parse contents obtained from given url
            // obtaine list of parse nodes in xml (only one would be there,
            // because we requesting only for one wiki page
            NodeList parseNodeList = dd.getElementsByTagName("parse");
            // parse node will have displaytitle attribute containing displye title of wiki page
            String pageTitle = parseNodeList.item(0).getAttributes().getNamedItem("displaytitle").getTextContent();
            // text node will contain actual parse result text of wiki page
            NodeList textNodeList = dd.getElementsByTagName("text");
            String text = textNodeList.item(0).getTextContent();
            text = Matcher.quoteReplacement(text);
            // put page title at correct positions
            tempText = tempText.replaceAll("\\{\\$TITLE\\$\\}", pageTitle);
            // put wiki page content at correct position
            tempText = tempText.replaceAll("\\{\\$CONTENT\\$\\}", text);
            // next, we are going to parse wiki page to obtain image tags information
            Reader reader = new CharArrayReader(tempText.toCharArray());
            new ParserDelegator().parse(reader, callback, true);    // call to parse it
            // iterate list of image sources obtained during parsing
            Iterator mapi = this.imageExtractor.entrySet().iterator();
            while (mapi.hasNext()) {
                Map.Entry entry = (Map.Entry) mapi.next();
                // original source attribute of img tag
                String oldSrc = entry.getKey().toString();
                // new source attribute for img tag
                String newSrc = entry.getValue().toString();
                // replace old src with new one, because we will store those images
                // at local 'images' directory under base directory
                tempText = tempText.replace(oldSrc, "images/" + newSrc);
            }
            bw.write(tempText);
            bw.close();
            itemNum++;
        }
        // extract list of images prepares so far
        this.imageExtractor.extractImages();
    }

    // return list of wikipage, html file name pairs
    public HashMap<String, String> getHtmlFileList() {
        return this.htmlPageFileList;
    }
}

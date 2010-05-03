/* File: ExtractWorker.java
 * $Author$
 * $LastChangedDate$
 * $Rev$
 * Licensed under GPL v3
 */
package wiki2local;

import java.awt.Component;
import java.io.*;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.text.BadLocationException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

import wiki2local.OptionsDialog.Options;

/**
 * Class to exceute page and image extraction process in a speperate thread
 * @author Junaid
 * @version 0.7
 * @since 0.7
 */
public class ExtractWorker extends SwingWorker<Void, String> {

    private File baseDir;
    private TocTreeModel tocTreeModel;
    private EnumMap<Options, String> options;
    private JTextArea textArea;
    private List<Component> componestsToDisable;

    public ExtractWorker(File baseDir, TocTreeModel tocTreeModel, EnumMap<Options, String> options, JTextArea textArea, List<Component> componestsToDisable) {
        this.baseDir = baseDir;
        this.tocTreeModel = tocTreeModel;
        this.options = options;
        this.textArea = textArea;
        this.componestsToDisable = componestsToDisable;
    }

    @Override
    protected Void doInBackground() throws InterruptedException {
        String message = "";
        for (Component component : this.componestsToDisable) {
            component.setEnabled(false);
        }
        try {
            // log file
            // TODO: something has to do with logger
            File loggerFile = new File(baseDir, "log.log");
            if (!loggerFile.exists()) {
                loggerFile.createNewFile();
            }
            this.publish("\nPreparing page list...\n");
            // get list of pages those to captured from wiki
            // it will be prepared from loaded topic list file
            HashMap<String, String> pageList = this.tocTreeModel.getPageList();
            this.publish("Preparing page list completed.\n");
            // create a page extractor object that helps us to capture wiki pages
            // we are giving wiki page list, base directory, wiki language, wiki project type and logger file
            WikiPageExtractor pageExtractor = new WikiPageExtractor(pageList, baseDir, this.options.get(Options.LANGUAGE), this.options.get(Options.PROJECT), this);
            this.publish("Extracting pages...\n");
            // request extractor to capture pages in the wiki
            pageExtractor.extractPages();
            this.publish("Extracting pages completed.\n");
            this.publish("Creating TOC tree HTML string...\n");
            // request for topic tree as html string
            String htmlTree = this.tocTreeModel.getHtmlTocTree(pageExtractor.getHtmlFileList());
            this.publish("Creating TOC tree HTML string completed.\n");
            this.publish("Reading TOC template...\n");
            // we have to read table of contes template file
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(baseDir, "toc_template.html")), "UTF-8"));
            char[] buffer = new char[1024]; // temporaty buffer to store read characters
            StringBuilder textBuffer = new StringBuilder(); // string builder to appedn read array of characters
            int numread;
            do {
                numread = br.read(buffer);  // read next available characters
                if (numread > 0) {  // ensure at least one character was read
                    textBuffer.append(buffer, 0, numread);  // append read characters to string builder
                }
            } while (numread != -1);
            br.close();     // close buffer and underlying stream
            this.publish("Reading TOC template completed.\n");
            this.publish("Writing TOC tree to file.\n");
            // set table of contents HTML file
            File tocFile = new File(baseDir, "toc.html");
            // we are writing UTF-8 encoded file
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tocFile), "UTF-8"));
            // post prepare table of contents html stream at correct position
            String newText = textBuffer.toString().replaceAll("\\{\\$TOC\\$\\}", htmlTree);
            // write contents to buffer, so to stream and file
            bw.write(newText);
            bw.flush();
            bw.close();
            this.publish("Page extraction process completed.\n");
        } catch (UnsupportedEncodingException e) {
            message = "No UTF-8 encoding/decoding support: \n" + this.getExceptionMessages(e);
            this.publish(message);
        } catch (BadLocationException e) {
            message = "BadLocation error: \n" + this.getExceptionMessages(e);
            this.publish(message);
        } catch (SAXException e) {
            message = "HTML parsing error: \n" + this.getExceptionMessages(e);
            this.publish(message);
        } catch (ParserConfigurationException e) {
            message = "HTML parser configuration error: \n" + e.getMessage();
            this.publish(message);
        } catch (FileNotFoundException e) {
            message = "File Not Found error: \n" + this.getExceptionMessages(e);
            this.publish(message);
        } catch (IOException e) {
            message = "Input/Output error: \n" + this.getExceptionMessages(e);
            this.publish(message);
        } catch (NullPointerException e) {
            message = "Nullponter error: \n" + this.getExceptionMessages(e);
            this.publish(message);
        } finally {
            for (Component component : this.componestsToDisable) {
                component.setEnabled(true);
            }
        }
        return null;
    }

    /**
     * We will get published strings as chunks here in a seperate thres, so can process
     * @param messages
     */
    @Override
    protected void process(List<String> messages) {
        for (String message : messages) {
            this.textArea.setText(this.textArea.getText() + message);
        }
    }

    /**
     * Allowing to call this method from outside of this class
     * original methods accessor is protected
     * @param message
     */
    public void publish(String message) {
        super.publish(message);
    }

    /**
     * Get exception messages from stacktrace to a single string
     * @param e
     * @return
     */
    private String getExceptionMessages(Exception e) {
        String exceptionMessage = "";
        StackTraceElement stackTrace[] = e.getStackTrace();
        for (StackTraceElement item : stackTrace) {
            exceptionMessage += item.toString() + "\n";
        }
        return exceptionMessage;
    }
}

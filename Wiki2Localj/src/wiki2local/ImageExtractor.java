/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wiki2local;

import java.util.HashMap;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import javax.swing.text.html.parser.ParserDelegator;

/**
 *
 * @author Junaid
 */
public class ImageExtractor extends HashMap<String, String> {

    /**
     * Directory to store images
     */
    private String imagesDirectory = "images/";
    private FileOutputStream logger;
    private HashMap<String, String> htmlPageFileList = null;
    private String baseDirPath;

    /**
     *
     * @param basePath Base directory to which contents to be loaded
     * @param htmlPageFileList Page list pairs containg Wiki page name and file name
     * @param loggerFile
     * @throws Exception
     */
    public ImageExtractor(String baseDirPath, HashMap<String, String> htmlPageFileList, File loggerFile)
            throws FileNotFoundException {
        // Set image director under base directory
        this.imagesDirectory = baseDirPath + "images/";
        File imageDir = new File(this.imagesDirectory);
        // if it does not exist, create it
        if (!imageDir.exists()) {
            imageDir.mkdir();
        }
        // If give loggerFile is null throw exception indicating it
        if (loggerFile == null) {
            throw new NullPointerException("given file logger is null.");
        } // If logger file reference not null but it does not exist on disk
        // throw exception
        else if (!loggerFile.exists()) {
            throw new FileNotFoundException("given file logger does not exists.");
        } else {
            this.logger = new FileOutputStream(loggerFile);
        }
        this.htmlPageFileList = htmlPageFileList;
        this.baseDirPath = baseDirPath;
    }

    public void prepareImageList() throws IOException {
        WikiPageParserCallback callback = new WikiPageParserCallback(this);
        Iterator i = this.htmlPageFileList.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry pageEntry = (Map.Entry) i.next();
            try {
                Reader reader = new FileReader((String) pageEntry.getValue());
                new ParserDelegator().parse(reader, callback, true);
                StringBuffer textBuffer = new StringBuffer();
                char[] buffer = new char[1024];
                int numread;
                do {
                    numread = reader.read(buffer);
                    if (numread > 0) {
                        textBuffer.append(buffer, 0, numread);
                    }
                } while (numread > -1);
                reader.close();
                String text = new String(textBuffer.toString());
                Iterator mapi = this.entrySet().iterator();
                while (mapi.hasNext()) {
                    Map.Entry entry = (Map.Entry) mapi.next();
                    String oldSrc = entry.getKey().toString();
                    String newSrc = entry.getValue().toString();
                    text = text.replace(oldSrc, "images/"+newSrc);
                }
                Writer writer = new FileWriter((String) pageEntry.getKey());
                writer.write(text);
                writer.close();
            } catch (IOException e) {
                this.logger.write(String.format("Error: %s not found: %s\n", (String) pageEntry.getKey(), e.getMessage()).getBytes());
            }
        }
    }

    public void extractImages() throws IOException {
        URL url = null;
        Iterator i = this.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            try {
                url = new URL(entry.getKey().toString());
                InputStream is = url.openStream();
                File imageFile = new File(this.imagesDirectory + entry.getValue().toString());
                if (!imageFile.exists()) {
                    imageFile.createNewFile();
                }
                FileOutputStream fos = new FileOutputStream(imageFile);
                int numread;
                byte[] b = new byte[1024];
                do {
                    numread = is.read(b);
                    if (numread > 0) {
                        fos.write(b, 0, numread);
                    }
                } while (numread > -1);
                fos.close();
            } catch (MalformedURLException e) {
                this.logger.write(String.format("Error: URL %s malformed: %s\n", entry.getKey().toString(), e.getMessage()).getBytes());
            } catch (FileNotFoundException e) {
                this.logger.write(String.format("Error: %s\n", e.getMessage()).getBytes());
            } catch (IOException e) {
                this.logger.write(String.format("Error: %s\n", e.getMessage()).getBytes());
            }

        }
    }
}

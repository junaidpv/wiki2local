/* File: ImageExtractor.java
 * $Author$
 * $LastChangedDate$
 * $Rev$
 * Licensed under GPL v3
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
        // If given loggerFile is null throw an exception indicating it
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
    }

    /**
     * This methode will prepare list of images at wikisite from captured
     * wikipages
     * @throws IOException
     */
    public void prepareImageList() throws IOException {
        // this object will help during the HTML page parsing process
        WikiPageParserCallback callback = new WikiPageParserCallback(this);
        // iterate through list of html pages
        Iterator i = this.htmlPageFileList.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry pageEntry = (Map.Entry) i.next();
            try {
                // input stream reader to read captured wiki html page files
                InputStreamReader reader = new InputStreamReader(new FileInputStream((String) pageEntry.getValue()),"UTF-8");
                //Reader reader = new FileReader((String) pageEntry.getValue());
                // create a parser and parse the html file
                new ParserDelegator().parse(reader, callback, true);
                // let us read entire file content to a string object
                BufferedReader br = new BufferedReader(reader);
                StringBuffer textBuffer = new StringBuffer();   // string buffer to store read text
                char[] buffer = new char[1024];     // character buffere to store read characters
                int numread;
                do {
                    numread = br.read(buffer);  // read available sequesnce of characters
                    if (numread > 0) {  // if at least on character was read
                        textBuffer.append(buffer, 0, numread);  // append them to string buffer
                    }
                } while (numread > -1); // while end of the file
                br.close(); // close the buffered reader
                String text = new String(textBuffer.toString());    // get string object from it
                // itereate over the list of images so far
                Iterator mapi = this.entrySet().iterator();
                while (mapi.hasNext()) {
                    Map.Entry entry = (Map.Entry) mapi.next();
                    String oldSrc = entry.getKey().toString();    // old src attribute value of IMG tag
                    String newSrc = entry.getValue().toString();  // new src attribute value to be set for IMG tag
                    text = text.replace(oldSrc, "images/" + newSrc);    // replace old src attribute value with new one
                }
                // save file changed file content back to file as UTF-8 encoded text
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File((String) pageEntry.getKey())),"UTF-8"));
                //Writer writer = new FileWriter((String) pageEntry.getKey());
                bw.write(text);
                bw.close();
            } catch (IOException e) {
                this.logger.write(String.format("Error: %s not found: %s\n", (String) pageEntry.getKey(), e.getMessage()).getBytes());
            }
        }
    }
    /**
     * Method helps to extract prepared list of images before
     * @throws IOException
     */
    public void extractImages() throws IOException {
        URL url = null;
        // iterate over the list of images
        Iterator i = this.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            try {
                url = new URL(entry.getKey().toString());   // create URL object from url string
                InputStream is = url.openStream();  // open a stream to read URL target's content
                // create a file to save image
                File imageFile = new File(this.imagesDirectory + entry.getValue().toString());
                /*
                if (!imageFile.exists()) {
                    imageFile.createNewFile();
                }*/
                // open a file output stream to store image data
                FileOutputStream fos = new FileOutputStream(imageFile);
                int numread;
                byte[] b = new byte[1024];  // buffer to store read image data
                do {
                    numread = is.read(b);   // read available data to buffer
                    if (numread > 0) {      // if at leat one byte read
                        fos.write(b, 0, numread);   // output it to image file
                    }
                } while (numread > -1);     // while end of the file
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

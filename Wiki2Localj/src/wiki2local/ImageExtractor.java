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

/**
 *
 * @author Junaid
 * @version 0.6
 * @since 0.1
 */
public class ImageExtractor extends HashMap<String, String> {

    /**
     * Directory to store images
     */
    private File imagesDir;

    /**
     *
     * @param basePath Base directory to which contents to be loaded
     * @param htmlPageFileList Page list pairs containg Wiki page name and file name
     * @param loggerFile
     * @throws Exception
     */
    public ImageExtractor(File baseDir, HashMap<String, String> htmlPageFileList)
            throws FileNotFoundException {
        // Set image director under base directory
        this.imagesDir = new File(baseDir, "images");
        // if it does not exist, create it
        if (!this.imagesDir.exists()) {
            this.imagesDir.mkdir();
        }
        /*
        // If given loggerFile is null throw an exception indicating it
        if (loggerFile == null) {
        throw new NullPointerException("given file logger is null.");
        } // If logger file reference not null but it does not exist on disk
        // throw exception
        else if (!loggerFile.exists()) {
        throw new FileNotFoundException("given file "+loggerFile.getPath()+" does not exists.");
        } else {
        this.logger = new FileOutputStream(loggerFile);
        }*/
        //this.htmlPageFileList = htmlPageFileList;
    }

    /**
     * Method helps to extract prepared list of images before
     * @throws IOException
     */
    public void extractImages()
            throws MalformedURLException, FileNotFoundException, IOException {
        URL url = null;
        File imageFile = null;
        // iterate over the list of images
        Iterator i = this.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            url = new URL(entry.getKey().toString());   // create URL object from url string
            InputStream is = url.openStream();  // open a stream to read URL target's content
            // create a file to save image
            imageFile = new File(this.imagesDir, entry.getValue().toString());
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
            is.close();
            fos.close();
        }
    }
}

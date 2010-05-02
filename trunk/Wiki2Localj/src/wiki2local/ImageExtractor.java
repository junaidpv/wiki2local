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
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Junaid
 * @version 0.7
 * @since 0.1
 */
public class ImageExtractor extends HashMap<String, String> {

    /**
     * Directory to store images
     */
    private File imagesDir;

    private ExtractWorker worker;

    /**
     *
     * @param basePath Base directory to which contents to be loaded
     * @param htmlPageFileList Page list pairs containg Wiki page name and file name
     * @param loggerFile
     * @throws Exception
     */
    public ImageExtractor(File baseDir, HashMap<String, String> htmlPageFileList, ExtractWorker worker)
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
        this.worker = worker;
    }

    /**
     * Method helps to extract prepared list of images before
     * @throws IOException
     */
    public void extractImages()
            throws MalformedURLException, FileNotFoundException, IOException, InterruptedException {
        URL url = null;
        File imageFile = null;
        // iterate over the list of images
        Iterator i = this.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            url = new URL(entry.getKey().toString());   // create URL object from url string
            this.worker.publish("Getting image " + url.toString());
            InputStream is = null;
            int tried = 0;  // no yet tried
            while(true) {
                try {
                    URLConnection connection = url.openConnection();
                    is = connection.getInputStream();
                } catch(IOException e) {
                    if(tried<5) {   // if tried less than 5 times
                        this.worker.publish("\nI/O error occured, will try in 3 seconds.\n");
                        Thread.sleep(3000);
                        tried++;
                        continue;   // try one time
                    }
                    else {  // we have tried 5 times, why shold try more? may some connection poblem
                        this.worker.publish("Aborting.\n");
                        throw e;
                    }
                }
                break;
            }
            this.worker.publish(" Done.\n");
            is = url.openStream();  // open a stream to read URL target's content
            // create a file to save image
            imageFile = new File(this.imagesDir, entry.getValue().toString());
            this.worker.publish("Writing to file " + imageFile.getName());
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
            this.worker.publish(" Done.\n");
        }
    }
}

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
import java.util.List;
import javax.swing.text.html.parser.ParserDelegator;

/**
 *
 * @author Junaid
 */
public class ImageExtractor extends HashMap<String, String> {

    /**
     * Directory to store images
     */
    private String imagesDirectory="images/";
    private FileOutputStream logger;
    private List<String> htmlFileNameList=null;
    private String basePath;

    public ImageExtractor(String basePath, List<String> htmlFileNameList, File loggerFile)
            throws NullPointerException, FileNotFoundException {
        if(imagesDirectory != null && !imagesDirectory.equals("")) {
            this.imagesDirectory = basePath+"images/";
            File imageDir = new File(this.imagesDirectory);
            if(!imageDir.exists()) imageDir.mkdir();
        }
        else {
            throw new NullPointerException("given basepath string is null.");
        }
        if(loggerFile == null) {
            throw new NullPointerException("given file logger is null.");
        }else if (!loggerFile.exists()) {
            throw new FileNotFoundException("given file logger does not exists.");
        }
        else {
            this.logger = new FileOutputStream(loggerFile);
        }

        this.htmlFileNameList = htmlFileNameList;
        this.basePath = basePath;
    }

    public void prepareImageList() throws IOException {
        WikiPageParserCallback callback = new WikiPageParserCallback(this);
        Iterator i = this.htmlFileNameList.iterator();
        while (i.hasNext()) {
            String htmlFileName = (String) i.next();
            try {
                Reader reader = new FileReader(htmlFileName);
                new ParserDelegator().parse(reader, callback, true);
                StringBuffer textBuffer = new StringBuffer();
                char[] buffer = new char[1024];
                int numread;
                do {
                    numread = reader.read(buffer);
                    if(numread>0) textBuffer.append(buffer, 0, numread);
                } while(numread > -1);
                reader.close();
                String text = new String(textBuffer.toString());
                Iterator mapi = this.entrySet().iterator();
                while(mapi.hasNext()) {
                    Map.Entry entry = (Map.Entry) mapi.next();
                    String oldSrc = entry.getKey().toString();
                    String newSrc = entry.getValue().toString();
                    text = text.replace(oldSrc, newSrc);
                }
                Writer writer = new FileWriter(htmlFileName);
                writer.write(text);
                writer.close();
            }
            catch(IOException e) {
                this.logger.write(String.format("Error: %s not found: %s\n", htmlFileName, e.getMessage()).getBytes());
            }
        }
    }

    public void extractImages() throws IOException {
        URL url = null;
        Iterator i = this.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry)i.next();
            try {
                url = new URL(entry.getKey().toString());
                InputStream is = url.openStream();
                File imageFile = new File(this.imagesDirectory+entry.getValue().toString());
                if(!imageFile.exists()) imageFile.createNewFile();
                FileOutputStream fos = new FileOutputStream(imageFile);
                int numread;
                byte[] b=new byte[1024];
                do {
                    numread=is.read(b);
                    if(numread > 0 ) fos.write(b, 0, numread);
                } while(numread > -1);
                fos.close();
            }
            catch(MalformedURLException e) {
                this.logger.write(String.format("Error: URL %s malformed: %s\n", entry.getKey().toString(), e.getMessage()).getBytes());
            }
            catch(FileNotFoundException e) {
                this.logger.write(String.format("Error: %s\n", e.getMessage()).getBytes());
            }
            catch(IOException e) {
                this.logger.write(String.format("Error: %s\n", e.getMessage()).getBytes());
            }
            
        }
    }
}

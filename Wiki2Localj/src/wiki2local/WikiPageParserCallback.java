/* File: WikiPageParserCallback.java
 * $Author$
 * $LastChangedDate$
 * $Rev$
 * Licensed under GPL v3
 */
package wiki2local;

import java.io.IOException;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTML.Attribute;
import javax.swing.text.MutableAttributeSet;
import java.util.HashMap;

/**
 * Class handling HTML parsing
 *
 * @author Junaid
 * @version 0.5
 * @since 0.1
 */
public class WikiPageParserCallback extends ParserCallback {
    private HashMap imageList = null;
    public WikiPageParserCallback(HashMap imageList) {
        this.imageList = imageList;
    }

    @Override
    public void handleSimpleTag (Tag tag, MutableAttributeSet as, int pos) {
        if(tag == Tag.IMG && as.isDefined(Attribute.SRC) ) {
            if(!imageList.containsKey(as.getAttribute(Attribute.SRC))) {
                String image = (String) as.getAttribute(Attribute.SRC);
                String extension = this.getExtension(image);
                imageList.put(image, String.format("img_%05d.%s",imageList.size(),extension));
            }
        }
    }

    /**
     * Returns image list prepared so far
     * @return HashMap<String, String>
     */
    public HashMap<String, String> getImageList() {
        return this.imageList;
    }

    public String getExtension(String url) {
        if(url !=null && !url.equals("")) {
            return url.substring(url.lastIndexOf(".")+1, url.length());
        }
        else return null;
    }

}

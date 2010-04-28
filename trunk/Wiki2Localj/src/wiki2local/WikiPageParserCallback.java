/*
 * 
 */

package wiki2local;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTML.Attribute;
import javax.swing.text.MutableAttributeSet;
import java.util.HashMap;

/**
 *
 * @author Junaid
 * @version $version
 * Class handling HTML parsing
 */
public class WikiPageParserCallback extends ParserCallback {
    private HashMap imageList = null;
    public WikiPageParserCallback(HashMap imageList) throws IOException {
        this.imageList = imageList;
    }

    @Override
    public void handleSimpleTag (Tag tag, MutableAttributeSet as, int pos) {
        if(tag == Tag.IMG && as.isDefined(Attribute.SRC) ) {
            if(!imageList.containsKey(as.getAttribute(Attribute.SRC))) {
                String image = (String) as.getAttribute(Attribute.SRC);
                String extension = this.getExtension(image);
                imageList.put(image, String.format("img_%06d.%s",imageList.size(),extension));
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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wiki2local;

import javax.swing.tree.*;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Enumeration;
import java.util.regex.*;

/**
 *
 * @author Junaid
 */
public class TocTreeModel extends DefaultTreeModel {

    public String treeID;
    public String treeClass;
    public String topicNodeClass;
    public String topicNodeSpanClass;
    public String pageNodeSpanClass;

    private TocTreeModel(DefaultMutableTreeNode root, String treeID, String treeClass, String topicNodeClass, String topicNodeSpanClass, String pageNodeSpanClass) {
        super(root);
        this.treeID = treeID;
        this.treeClass = treeClass;
        this.topicNodeClass = topicNodeClass;
        this.topicNodeSpanClass = topicNodeSpanClass;
        this.pageNodeSpanClass = pageNodeSpanClass;
    }

    public String getHtmlTocTree() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("<ul id='%s' class=\"%s\" >", treeID, treeClass));
        this.getHtmlString((DefaultMutableTreeNode) this.root, builder);
        builder.append("</ul>");
        return builder.toString();
    }

    public void getHtmlString(DefaultMutableTreeNode node, StringBuilder builder) {
        SimpleImmutableEntry<String, String> entry = (SimpleImmutableEntry) node.children().nextElement();
        if (node.isLeaf()) {
            builder.append(String.format("<li><span class='%s'><a href='%s' target='content'>%s</a></span></li>", this.pageNodeSpanClass, entry.getValue(), entry.getKey()));
        } else {
            Enumeration children = node.children();
            DefaultMutableTreeNode childNode = null;
            do {
                builder.append(String.format("<li class='%s'><span class='%s'>%s</span><ul>", this.topicNodeClass, this.topicNodeSpanClass, node.getUserObject().toString()));
                childNode = (DefaultMutableTreeNode) children.nextElement();
                if (childNode != null) {
                    this.getHtmlString(childNode, builder);
                }
            } while (childNode != null);
            builder.append("</ul></li>");
        }
    }

    public static TocTreeModel parse(String text, String treeID, String treeClass, String topicNodeClass, String topicNodeSpanClass, String pageNodeSpanClass) {
        TocTreeModel tocTree = null;
        boolean match = true;
        DefaultMutableTreeNode rootNode=null;
        String[] items = text.split("\\r\\n|\\n");
        if (items.length == 0 || !items[0].matches("^={1}[^=]+$")) {
            match = false;
        } else {
            int depth = 1;
            String patternString = "^(=+).+$";
            String item = items[0];
            rootNode = new DefaultMutableTreeNode(item);
            DefaultMutableTreeNode lastTopicNode = rootNode;
            int parentDepth = 1;
            for (int i=1; i< items.length; i++) {
                item = items[i];
                if(item.length()<1) {
                    match = false;
                    break;
                }
                DefaultMutableTreeNode child = null;
                if (item.matches(patternString)) {
                    Pattern pattern = Pattern.compile(patternString);
                    Matcher matcher = pattern.matcher(item);
                    matcher.find();
                    String equalString = matcher.group(1);
                    int currentDepth = equalString.length();
                    child = new DefaultMutableTreeNode(item.substring(currentDepth));
                    int depthChange = currentDepth - depth;
                    if (depthChange > 1 || currentDepth < 2) {
                        match = false;
                        break;
                    }
                    if (depthChange < 0) {
                        for(int j=0; j< Math.abs(depthChange); j++)
                            lastTopicNode = (DefaultMutableTreeNode) lastTopicNode.getParent();
                    }
                    depth = currentDepth;
                    lastTopicNode.add(child);
                    lastTopicNode = child;
                }
                else {
                    child = new DefaultMutableTreeNode(item);
                    lastTopicNode.add(child);
                }
            }
        }
        if(match) {
            tocTree = new TocTreeModel(new DefaultMutableTreeNode(), treeID, treeClass, topicNodeClass, topicNodeSpanClass, pageNodeSpanClass);
        }
        return tocTree;   
    }

    public static boolean match(String text) {
        boolean match = false;
        String[] items = text.split("\\r\\n|\\n");
        if (items.length == 0 || !items[1].startsWith("=")) {
            match = false;
        } else {
            int depth = 0;
            String patternString = "^(=+).*$";
            for (String item : items) {
                if (item.matches(patternString)) {
                    Pattern pattern = Pattern.compile(patternString);
                    Matcher matcher = pattern.matcher(item);
                    matcher.find();
                    String equalString = matcher.group(1);
                    int currentDepth = equalString.length();
                    if ((currentDepth - depth) > 1) {
                        break;
                    }
                    depth = currentDepth;
                }
            }
            match = true;
        }
        return match;
    }
}

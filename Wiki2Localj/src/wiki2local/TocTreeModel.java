/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wiki2local;

import javax.swing.tree.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.regex.*;

/**
 *
 * @author Junaid
 */
public class TocTreeModel extends DefaultTreeModel {

    private String treeID;
    private String treeClass;
    private String topicNodeClass;
    private String topicNodeSpanClass;
    private String pageNodeSpanClass;
    private HashMap<String, String> pageList;
    private String basePath;

    private TocTreeModel(DefaultMutableTreeNode root, HashMap<String, String> pageList, String treeID, String treeClass, String topicNodeClass, String topicNodeSpanClass, String pageNodeSpanClass, String basePath) {
        super(root);
        this.treeID = treeID;
        this.treeClass = treeClass;
        this.topicNodeClass = topicNodeClass;
        this.topicNodeSpanClass = topicNodeSpanClass;
        this.pageNodeSpanClass = pageNodeSpanClass;
        this.pageList = pageList;
        this.basePath = basePath;
    }

    public String getHtmlTocTree(HashMap<String, String> pageList) {
        this.pageList = pageList;
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("<h2>%s</h2>",((DefaultMutableTreeNode) this.root).getUserObject() ));
        builder.append(String.format("<ul id='%s' class=\"%s\" >", treeID, treeClass));
        DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) this.root.getChildAt(0);
        while(childNode != null) {
            this.getHtmlString(childNode, builder);
            childNode = childNode.getNextSibling();
        }
        builder.append("</ul>");
        return builder.toString();
    }

    public void getHtmlString(DefaultMutableTreeNode node, StringBuilder builder) {
        String entry = (String) node.getUserObject();
        if (node.isLeaf()) {
            builder.append(String.format("<li><span class='%s'><a href='%s' target='content'>%s</a></span></li>", this.pageNodeSpanClass, this.pageList.get(entry), entry));
        } else {
            builder.append(String.format("<li class='%s'><span class='%s'>%s</span><ul>", this.topicNodeClass, this.topicNodeSpanClass, node.getUserObject().toString()));
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getFirstChild();
            while(childNode != null) {
                this.getHtmlString(childNode, builder);
                childNode = childNode.getNextSibling();
            }
            builder.append("</ul></li>");
        }
    }

    public static TocTreeModel parse(ArrayList<String> sArray, String treeID, String treeClass, String topicNodeClass, String topicNodeSpanClass, String pageNodeSpanClass, String basePath) {
        TocTreeModel tocTreeModel = null;
        boolean match = true;
        DefaultMutableTreeNode rootNode = null;
        String[] items = new String[sArray.size()];
        sArray.toArray(items);
        HashMap<String, String> pageList = new HashMap<String, String>();
        // shoul contain at least tree heading
        if (items.length == 0 ) {
            match = false;
        } else {
            int depth = 1;
            String patternString = "^(=+).+$";
            String item = items[0];
            // Some text editors may add ByteOrderMask at begnning of the file, if so remove it
            rootNode = new DefaultMutableTreeNode(item.replace("\uFEFF", ""));
            DefaultMutableTreeNode lastTopicNode = rootNode;
            DefaultMutableTreeNode parentTopicNode = rootNode;
            int parentDepth = 0;
            for (int i = 1; i < items.length; i++) {
                item = items[i];
                if (item.length() < 1) {
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
                    if (depthChange > 1 /*|| currentDepth < 2*/) {
                        match = false;
                        break;
                    }
                    if (depthChange < 0) {
                        for (int j = 0; j < Math.abs(depthChange); j++) {
                            parentTopicNode = (DefaultMutableTreeNode) parentTopicNode.getParent();
                        }
                    } else if ((currentDepth - parentDepth) == 2) {
                        parentTopicNode = lastTopicNode;
                    }
                    depth = currentDepth;
                    parentTopicNode.add(child);
                    lastTopicNode = child;
                } else {
                    if (lastTopicNode == rootNode) {
                        match = false;
                        break;
                    }
                    child = new DefaultMutableTreeNode(item);
                    pageList.put(item, null);
                    lastTopicNode.add(child);
                }
            }
        }
        if (match) {
            tocTreeModel = new TocTreeModel(rootNode, pageList, treeID, treeClass, topicNodeClass, topicNodeSpanClass, pageNodeSpanClass, basePath);
        }
        return tocTreeModel;
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

    public void setPageFileName(String pageName, String fileName) {
        this.pageList.put(pageName, fileName);
    }

    public HashMap<String, String> getPageList() {
        return this.pageList;
    }
}

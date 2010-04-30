/* File: TocTreeModel.java
 * $Author$
 * $LastChangedDate$
 * $Rev$
 * Licensed under GPL v3
 */
package wiki2local;

import javax.swing.tree.*;
import java.util.*;
import java.util.regex.*;

/**
 * Class represing JTree's tree model
 * this also contain a static method to parse give topic stril list to tree model
 *
 * @author Junaid
 * @version 0.5
 * @since 0.1
 */
public class TocTreeModel extends DefaultTreeModel {

    private String treeID;
    private String treeClass;
    private String topicNodeClass;
    private String topicNodeSpanClass;
    private String pageNodeSpanClass;
    private HashMap<String, String> pageList;

    /**
     *
     * @param root  roote node
     * @param pageList  list of wikipagename, htmlfilename pairs
     * @param treeID    ID of html tag UL that hold table of contents
     * @param treeClass css class for TOC UL tag
     * @param topicNodeClass    css class for topic node LI tag
     * @param topicNodeSpanClass    css class for topic node SPAN tag
     * @param pageNodeSpanClass     css class for page node
     */
    private TocTreeModel(DefaultMutableTreeNode root, HashMap<String, String> pageList, String treeID, String treeClass, String topicNodeClass, String topicNodeSpanClass, String pageNodeSpanClass) {
        super(root);
        this.treeID = treeID;
        this.treeClass = treeClass;
        this.topicNodeClass = topicNodeClass;
        this.topicNodeSpanClass = topicNodeSpanClass;
        this.pageNodeSpanClass = pageNodeSpanClass;
        this.pageList = pageList;
    }
    /**
     * Method helps to create table of contents HTML tree
     * @param pageList  list of wikipage name, html file name pairs
     * @return  table of contents as HTML text
     */
    public String getHtmlTocTree(HashMap<String, String> pageList) {
        this.pageList = pageList;   // store page list to instance varibale
        // string builder to prepare html string
        StringBuilder builder = new StringBuilder();
        // prepare level to header tag with root node text within it
        builder.append(String.format("<h2>%s</h2>",((DefaultMutableTreeNode) this.root).getUserObject() ));
        builder.append(String.format("<ul id='%s' class=\"%s\" >", treeID, treeClass));
        // get first child under root node to start with
        DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) this.root.getChildAt(0);
        while(childNode != null) {  // iterate ovr all children of root node
            this.getHtmlString(childNode, builder); // get sub tree under that node
            childNode = childNode.getNextSibling(); // set next node to process
        }
        builder.append("</ul>");
        // return HTML table of contents string
        return builder.toString();
    }

    /** helps to prepare subtree under each node
     * 
     * @param node
     * @param builder
     */
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

    public static TocTreeModel parse(ArrayList<String> sArray, String treeID, String treeClass, String topicNodeClass, String topicNodeSpanClass, String pageNodeSpanClass) {
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
            tocTreeModel = new TocTreeModel(rootNode, pageList, treeID, treeClass, topicNodeClass, topicNodeSpanClass, pageNodeSpanClass);
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

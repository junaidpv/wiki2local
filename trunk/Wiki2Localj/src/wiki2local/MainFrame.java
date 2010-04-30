/* File: MainFrame.java
 * $Author$
 * $LastChangedDate$
 * $Rev$
 * Licensed under GPL v3
 */
package wiki2local;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * Main window class of this project
 * 
 * @author Junaid
 * @version 0.2
 */
public class MainFrame extends JFrame {

    JTree topicTree;
    GridBagLayout layout;
    TocTreeModel tocTreeModel;
    JTextField baseDirTextField;
    JButton pullButton;
    JLabel statusTextLabel;
    JButton openTopicButtion;
    JButton aboutButton;
    JButton quitButton;

    public MainFrame(String title) {
        super(title);
    }

    public void initialize() {
        setSize(400, 400);
        // call to initiate visual components in the form
        initDisplayComponents();
        setVisible(true);
    }

    public void initDisplayComponents() {
        /*
        Font font = null;
        try {
        font = Font.createFont(Font.TRUETYPE_FONT, new File("AnjaliOldLipi.ttf"));
        } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Exception occured: " + e.getMessage());
        }
        UIManager.put("Font", font);
         */
        this.layout = new GridBagLayout();
        this.setLayout(this.layout);
        GridBagConstraints gbc = new GridBagConstraints();
        // gap between components
        gbc.insets = new Insets(5, 5, 0, 5);

        this.openTopicButtion = new JButton("Open Topic File...");
        this.openTopicButtion.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent event) {
                openTopicButtonClicked(event);
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        this.add(this.openTopicButtion, gbc);

        this.aboutButton = new JButton("About");
        this.aboutButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent event) {
                aboutButtonClicked(event);
            }
        });
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        this.add(this.aboutButton, gbc);

        this.topicTree = new JTree(new DefaultMutableTreeNode());
        JScrollPane topicTreeScrollPane = new JScrollPane(this.topicTree);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.LINE_START;
        this.add(topicTreeScrollPane, gbc);

        this.baseDirTextField = new JTextField();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.LINE_START;
        this.add(this.baseDirTextField, gbc);

        JButton baseDirChooserButton = new JButton("Set Base Directory");
        baseDirChooserButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent event) {
                baseDirChooserButtonClicked(event);
            }
        });
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        this.add(baseDirChooserButton, gbc);


        this.pullButton = new JButton("Start...");
        this.pullButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent event) {
                pullButtonClicked(event);
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.gridwidth = 3;
        this.add(this.pullButton, gbc);

        this.quitButton = new JButton("Quit");
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        this.add(this.quitButton, gbc);

        this.statusTextLabel = new JLabel(": Clik 'Open Topic File' button to select topic list file.");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        this.add(this.statusTextLabel, gbc);

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent event) {
                frameClosing(event);
            }
        });
    }

    /**
     * Initialize menu of this window
     */
    public void frameClosing(WindowEvent event) {
        System.exit(0);
    }

    /*
     * Will be called when user click Open Topic button
     */
    public void openTopicButtonClicked(MouseEvent event) {
        JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
        fileChooser.showOpenDialog(this);
        try {
            // collect user slected file
            File topicListFile = fileChooser.getSelectedFile();
            // ensure file selection
            if (topicListFile != null) {
                FileInputStream fis = new FileInputStream(topicListFile);
                // always assuming file is encoded with utf-8
                BufferedReader br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
                // array of strings to store each line in the read file
                ArrayList<String> items = new ArrayList<String>();
                // temperory string to store read line
                String t;
                do {
                    t = br.readLine();  // read one line from file
                    if (t != null) {    // if read line is not numm
                        items.add(t);   // add it to list
                    }
                } while (t != null);    // read while null line, that is end of line
                fis.close();    // close file input stream

                // subsequent classes in this project assume base director name string
                // contains backslash ('/') as last character
                File baseDir = new File(this.baseDirTextField.getText());
                String baseDirName = baseDir.getName();
                // ensure base directory name contains backslash as last character
                baseDirName = baseDirName.endsWith("/") ? baseDirName : baseDirName + "/";
                // this call inlcludes list of items in the topic file
                // and classes and ids for HTML elements that form topic tree structure
                // later we may allow users to select different names for these html element properties
                this.tocTreeModel = TocTreeModel.parse(items, "example", "filetree", "closed", "folder", "file");
                if (this.tocTreeModel != null) {    // ensure returned topic tree is not null
                    this.topicTree.setModel(tocTreeModel); // set as tree model for JTree component
                } else { // if returend tree model is null, then topic file is not in the proper format
                    JOptionPane.showMessageDialog(this, "Topic file format not valid.");    // tell this issue to user
                }
            }
        } catch (IOException e) {
            this.showError(e);
        }
    }
    /**
     * Will be called when user click 'About' button
     * So we can show some information to user about this project
     * @param event
     */
    public void aboutButtonClicked(MouseEvent event) {
        // TODO:
    }

    /**
     * Will be called when user clicks on button to set directory as base directory to store
     * contents tha will be captured from wiki
     * @param event
     */
    public void baseDirChooserButtonClicked(MouseEvent event) {
        // alwawys show current direcory as defualt
        JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
        // we want select only directory not a file, so set it
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.showOpenDialog(this);
        if (fileChooser.getSelectedFile() != null) {    // if user selected a directory
            this.baseDirTextField.setText(fileChooser.getSelectedFile().toString());    // show it in the text field
        }
    }

    /**
     * Will be called when user clicks on 'Start...' or 'Pull' button
     * web capturing methods will be called inside this method
     * @param event
     */
    public void pullButtonClicked(MouseEvent event) {
        // get the file object represeing base directory
        File baseDir = new File(this.baseDirTextField.getText());
        String baseDirName = baseDir.getName();
        // ensure backslash is the last character
        baseDirName = baseDirName.endsWith("/") ? baseDirName : baseDirName + "/";
        // if user has not loaded proper topic list file tocTreeModel would be null
        if (this.tocTreeModel == null) {    // user not loaded topic list file
            JOptionPane.showMessageDialog(this, "Load topic list first."); // let user to know it
        } else if (this.baseDirTextField.getText().equals("")) { // if user has not specified base directory
            JOptionPane.showMessageDialog(this, "Specify base directory to which load contents.");
        } else if (!baseDir.exists()) { // if user specified directory does not exist
            JOptionPane.showMessageDialog(this, "Directory to which load contents does not exist.");
        } else {    // so every every requirements are met
            try {
                // log file
                // TODO: something has to do with logger
                File loggerFile = new File("log.log");
                // get list of pages those to captured from wiki
                // it will be prepared from loaded topic list file
                HashMap pageList = this.tocTreeModel.getPageList();
                // create a page extractor object that helps us to capture wiki pages
                // we are giving wiki page list, base directory, wiki language, wiki project type and logger file
                // we may later add funtionality in GUI to select diffrent languages and projects
                WikiPageExtractor pageExtractor = new WikiPageExtractor(pageList, baseDirName, "ml", "wikipedia", loggerFile);
                // request extractor to capture pages in the wiki
                pageExtractor.extractPages();
                // request for topic tree as html string
                String htmlTree = this.tocTreeModel.getHtmlTocTree(pageExtractor.getHtmlFileList());
                // we have to read table of contes template file
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(baseDirName + "toc_template.html")), "UTF-8"));
                char[] buffer = new char[1024]; // temporaty buffer to store read characters
                StringBuilder textBuffer = new StringBuilder(); // string builder to appedn read array of characters
                int numread;
                do {
                    numread = br.read(buffer);  // read next available characters
                    if (numread > 0) {  // ensure at least one character was read
                        textBuffer.append(buffer, 0, numread);  // append read characters to string builder
                    }
                } while (numread != -1);
                br.close();     // close buffer and underlying stream
                // set table of contents HTML file
                File tocFile = new File(baseDirName + "toc.html");
                // we are writing UTF-8 encoded file
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tocFile), "UTF-8"));
                // post prepare table of contents html stream at correct position
                String newText = textBuffer.toString().replaceAll("\\{\\$TOC\\$\\}", htmlTree);
                // write contents to buffer, so to stream and file
                bw.write(newText);
                bw.flush();
                bw.close();
                this.statusTextLabel.setText("Done.");  // let user to konw we are completed
            } catch (UnsupportedEncodingException e) {
                this.showError(e);
            } catch (SAXException e) {
                this.showError(e);
            } catch (ParserConfigurationException e) {
                this.showError(e);
            } catch (FileNotFoundException e) {
                this.showError(e);
            } catch (IOException e) {
                this.showError(e);
            } catch (NullPointerException e) {
                this.showError(e);
            }
        }
    }

    /**
     * Method helps to bring exceptions to user
     * @param e
     */
    private void showError(Exception e) {
        String msg = "";
        StackTraceElement stackTrace[] = e.getStackTrace();
        for (StackTraceElement item : stackTrace) {
            msg += item.toString() + "\n";
        }
        JOptionPane.showMessageDialog(this, "Exception occured: " + msg);
    }
}

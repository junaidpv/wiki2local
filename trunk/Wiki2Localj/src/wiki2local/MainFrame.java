/* File: MainFrame.java
 * $Author$
 * $LastChangedDate$
 * $Rev$
 * Licensed under GPL v3
 */
package wiki2local;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.text.BadLocationException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

import wiki2local.OptionsDialog.Options;

/**
 * Main window class of this project
 * 
 * @author Junaid
 * @version 0.6
 * @since 0.1
 */
public class MainFrame extends JFrame {

    private JTree topicTree;
    private GridBagLayout layout;
    private TocTreeModel tocTreeModel;
    private JTextField baseDirTextField;
    private JButton pullButton;
    private JLabel statusTextLabel;
    private JButton openTopicButtion;
    private JButton aboutButton;
    private JButton quitButton;
    private JButton optionsButton;


    private EnumMap<Options, String> options;

    public MainFrame(String title) {
        super(title);
    }

    public void initialize() {
        setSize(400, 400);
        
        this.setIconImage(new ImageIcon(getClass().getResource("/wiki2local/Logo.png")).getImage());
        // call to initiate visual components in the form
        initDisplayComponents();
        this.options = new EnumMap<Options, String>(Options.class);
        this.options.put(Options.LANGUAGE, "ml");
        this.options.put(Options.PROJECT, "wikipedia");
        this.options.put(Options.MENU_ID, "example");
        this.options.put(Options.MENU_CLASS, "filetree");
        this.options.put(Options.TOPIC_NODE_CLASS, "closed");
        this.options.put(Options.TOPIC_ITEM_CLASS, "folder");
        this.options.put(Options.PAGE_ITEM_CLASS, "file");
        setVisible(true);
    }

    /**
     * Method to initialize visual coponents of the form
     */
    private void initDisplayComponents() {
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
                openTopicButtonClicked();
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        this.add(this.openTopicButtion, gbc);

        this.optionsButton= new JButton("Options...");
        this.optionsButton.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent event) {
                optionsButtonClicked();
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        this.add(this.optionsButton, gbc);


        this.aboutButton = new JButton("About");
        this.aboutButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent event) {
                aboutButtonClicked();
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
                baseDirChooserButtonClicked();
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
                pullButtonClicked();
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.gridwidth = 3;
        this.add(this.pullButton, gbc);

        this.quitButton = new JButton("Quit");
        this.quitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                quitButtonClicked();
            }
        });
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        this.add(this.quitButton, gbc);

        this.statusTextLabel = new JLabel(": Clik 'Open Topic File' button to select topic list file.");
        this.statusTextLabel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        this.statusTextLabel.setMinimumSize(new Dimension(this.statusTextLabel.getSize().width,50));
        this.statusTextLabel.setVerticalAlignment(JLabel.TOP);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets.bottom = 5;
        this.add(this.statusTextLabel, gbc);

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent event) {
                frameClosing();
            }
        });
    }

    /**
     * Initialize menu of this window
     */
    private void frameClosing() {
        System.exit(0);
    }

    /*
     * Will be called when user click Open Topic button
     */
    private void openTopicButtonClicked() {
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
                
                // this call inlcludes list of items in the topic file
                // and classes and ids for HTML elements that form topic tree structure
                this.tocTreeModel = TocTreeModel.parse(items, this.options.get(Options.MENU_ID), this.options.get(Options.MENU_CLASS), this.options.get(Options.TOPIC_NODE_CLASS), this.options.get(Options.TOPIC_ITEM_CLASS),this.options.get(Options.PAGE_ITEM_CLASS));
                if (this.tocTreeModel != null) {    // ensure returned topic tree is not null
                    this.topicTree.setModel(tocTreeModel); // set as tree model for JTree component
                    this.statusTextLabel.setText(": Set base directory to store captured contents.");
                } else { // if returend tree model is null, then topic file is not in the proper format
                    this.topicTree.setModel(null);
                    JOptionPane.showMessageDialog(this, "Topic file format not valid.");    // tell this issue to user
                }
            }
        } catch (IOException e) {
            this.showError(e, "An I/O exception occured.");
        }
    }
    /**
     * Will be called when user click 'Options...' button
     * @param event
     */
    private void optionsButtonClicked() {
        // create an OptionsDialog
        OptionsDialog optionsDialog = new OptionsDialog(this, true, this.options);
        optionsDialog.setVisible(true);
        this.options.put(Options.LANGUAGE, optionsDialog.getLanguage());
        this.options.put(Options.PROJECT, optionsDialog.getProject());
        this.options.put(Options.MENU_ID, optionsDialog.getMenuId());
        this.options.put(Options.MENU_CLASS, optionsDialog.getMenuClass());
        this.options.put(Options.TOPIC_NODE_CLASS, optionsDialog.getTopicNodeClass());
        this.options.put(Options.TOPIC_ITEM_CLASS, optionsDialog.getTopicItemClass());
        this.options.put(Options.PAGE_ITEM_CLASS, optionsDialog.getPageItemClass());
        optionsDialog.dispose();
    }
    /**
     * Will be called when user click 'About' button
     * So we can show some information to user about this project
     * @param event
     */
    private void aboutButtonClicked() {
        AboutDialog aboutDialog = new AboutDialog(this,true);
        aboutDialog.setVisible(true);
    }

    /**
     * Will be called when user clicks on button to set directory as base directory to store
     * contents tha will be captured from wiki
     * @param event
     */
    private void baseDirChooserButtonClicked() {
        // alwawys show current direcory as defualt
        JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
        // we want select only directory not a file, so set it
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.showOpenDialog(this);
        if (fileChooser.getSelectedFile() != null) {    // if user selected a directory
            this.baseDirTextField.setText(fileChooser.getSelectedFile().toString());    // show it in the text field
            if (this.tocTreeModel!=null) this.statusTextLabel.setText(": Click 'Start...' to start capturing.");
            else this.statusTextLabel.setText(": Click 'Open Topic File' to load topic list.");
        }
    }

    /**
     * Will be called when user clicks on 'Start...' or 'Pull' button
     * web capturing methods will be called inside this method
     * @param event
     */
    private void pullButtonClicked() {
        // get the file object represeing base directory
        File baseDir = new File(this.baseDirTextField.getText());
        // if user has not loaded proper topic list file tocTreeModel would be null
        if (this.tocTreeModel == null) {    // user not loaded topic list file
            JOptionPane.showMessageDialog(this, "Load topic list first."); // let user to know it
        } else if (this.baseDirTextField.getText().equals("")) { // if user has not specified base directory
            JOptionPane.showMessageDialog(this, "Specify base directory to which load contents.");
        } else if (!baseDir.exists()) { // if user specified directory does not exist
            JOptionPane.showMessageDialog(this, "Directory to which load contents does not exist.");
        } else {    // so every every requirements are met
            try {
                this.setEnabled(false);
                // log file
                // TODO: something has to do with logger
                File loggerFile = new File(baseDir,"log.log");
                if(!loggerFile.exists()) {
                    loggerFile.createNewFile();
                }
                this.statusTextLabel.setText(": Preparing list of pages to capture...");
                // get list of pages those to captured from wiki
                // it will be prepared from loaded topic list file
                HashMap<String, String> pageList = this.tocTreeModel.getPageList();
                // create a page extractor object that helps us to capture wiki pages
                // we are giving wiki page list, base directory, wiki language, wiki project type and logger file
                WikiPageExtractor pageExtractor = new WikiPageExtractor(pageList, baseDir, this.options.get(Options.LANGUAGE), this.options.get(Options.PROJECT));
                this.statusTextLabel.setText(": Extracting pages...");
                // request extractor to capture pages in the wiki
                pageExtractor.extractPages();
                this.statusTextLabel.setText(": Writing TOC to toc.html ...");
                // request for topic tree as html string
                String htmlTree = this.tocTreeModel.getHtmlTocTree(pageExtractor.getHtmlFileList());
                // we have to read table of contes template file
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(baseDir, "toc_template.html")), "UTF-8"));
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
                File tocFile = new File(baseDir, "toc.html");
                // we are writing UTF-8 encoded file
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tocFile), "UTF-8"));
                // post prepare table of contents html stream at correct position
                String newText = textBuffer.toString().replaceAll("\\{\\$TOC\\$\\}", htmlTree);
                // write contents to buffer, so to stream and file
                bw.write(newText);
                bw.flush();
                bw.close();
                this.statusTextLabel.setText("Done.");  // let user to konw we are completed
                JOptionPane.showMessageDialog(this, "Capturing process completed.");
            } catch (UnsupportedEncodingException e) {
                this.showError(e, "No UTF-8 encoding/decoding support.");
            } catch(BadLocationException e) {
                this.showError(e, "BadLocation error.");
            }
            catch (SAXException e) {
                this.showError(e ,"HTML parsing error.");
            } catch (ParserConfigurationException e) {
                this.showError(e, "HTML parser configuration error.");
            } catch (FileNotFoundException e) {
                this.showError(e , "File Not Found error");
            } catch (IOException e) {
                this.showError(e , "Input/Output error.");
            } catch (NullPointerException e) {
                this.showError(e, "Nullponter error");
            }
            finally {
                this.setEnabled(true);
            }
        }
    }
    /**
     * Will be called when Quit button clicked
     * it will exit the application
     * @param event
     */
    private void quitButtonClicked() {
        this.setVisible(false);
        this.dispose();
        System.exit(0);
    }

    /**
     * Method helps to bring exceptions to user
     * @param e
     */
    private void showError(Exception e, String message) {
        String exceptionMessage = "";
        StackTraceElement stackTrace[] = e.getStackTrace();
        for (StackTraceElement item : stackTrace) {
            exceptionMessage += item.toString() + "\n";
        }
        ErrorMessageDialog errorMsgDialog = new ErrorMessageDialog(this, true, message, exceptionMessage);
        errorMsgDialog.setVisible(true);
    }
}

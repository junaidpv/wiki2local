/* File: MainFrame.java
 * $Author$
 * $LastChangedDate$
 * $Rev$
 * Licensed under GPL v3
 */
package wiki2local;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

import wiki2local.OptionsDialog.Options;

/**
 * Main window class of this project
 * 
 * @author Junaid
 * @version 0.7
 * @since 0.1
 */
public class MainFrame extends JFrame {

    private JTree topicTree;
    private GridBagLayout layout;
    private TocTreeModel tocTreeModel;
    private JTextField baseDirTextField;
    private JButton pullButton;
    private JTextArea statusTextArea;
    private JButton openTopicButtion;
    private JButton aboutButton;
    private JButton quitButton;
    private JButton optionsButton;
    private JButton baseDirChooserButton;

    private EnumMap<Options, String> options;

    public MainFrame(String title) {
        super(title);
    }

    public void initialize() {
        setSize(600, 500);
        
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

        this.baseDirChooserButton = new JButton("Set Base Directory");
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

        JLabel statusLabel= new JLabel("Messages:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        this.add(statusLabel, gbc);

        this.statusTextArea = new JTextArea("Clik 'Open Topic File' button to select topic list file.");
        //this.statusTextArea.setBorder(new BevelBorder(BevelBorder.LOWERED));
        this.statusTextArea.setEditable(false);
        //this.statusTextArea.setVerticalAlignment(JLabel.TOP);
        JScrollPane scrollPane = new JScrollPane(this.statusTextArea);
        scrollPane.setMinimumSize(new Dimension(this.statusTextArea.getSize().width,150));
        gbc.gridx = 0;
        gbc.gridy = 5;
        this.add(scrollPane, gbc);

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
                    this.statusTextArea.setText("Set base directory to store captured contents.");
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
            if (this.tocTreeModel!=null) this.statusTextArea.setText("Click 'Start...' to start capturing.");
            else this.statusTextArea.setText("Click 'Open Topic File' to load topic list.");
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
            List<Component> componentsToDisable = new ArrayList<Component>(6);
            componentsToDisable.add(this.openTopicButtion);
            componentsToDisable.add(this.optionsButton);
            componentsToDisable.add(this.topicTree);
            componentsToDisable.add(this.baseDirTextField);
            componentsToDisable.add(this.baseDirChooserButton);
            componentsToDisable.add(this.pullButton);
            ExtractWorker extractWorker = new ExtractWorker(baseDir, this.tocTreeModel, this.options, this.statusTextArea, componentsToDisable);
            extractWorker.execute();
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

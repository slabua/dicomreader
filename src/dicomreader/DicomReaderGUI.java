/***************************************************************************
* DicomReaderGUI.java                                                      *
*--------------------------------------------------------------------------*
*                                                                          *
* created on   : 20 mar 2005                                               *
* copyright    : (C) 2005, Salvatore La Bua      <SLaBua(at)SLBLabs.com>   *
* copyright    : (C) 2005, Calogero Crapanzano   <calosan(at)libero.it>    *
* copyright    : (C) 2005, Pietro Amato          <lovedstone(at)libero.it> *
*                                                                          * 
***************************************************************************/

/***************************************************************************
*                                                                          *
*   This program is free software; you can redistribute it and/or modify   *
*   it under the terms of the GNU General Public License as published by   *
*   the Free Software Foundation; either version 2 of the License, or      *
*   (at your option) any later version.                                    *
*                                                                          *
***************************************************************************/


package dicomreader;


import java.awt.BorderLayout;
//import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
//import javax.swing.border.BevelBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.UIManager;


/**
 * <code>DicomReaderGUI</code> class is the application's graphic user interface.<br />
 *  It refers to the <code>DicomReader</code> class.
 * 
 * @author Salvatore La Bua    <i>< slabua (at) gmail.com ></i>
 * 
 * @version 1.3.2-1
 * 
 * @see DicomReader
 */
public class DicomReaderGUI extends JFrame {
    
    private final static String version = DicomReader.getAppVersion();
    private final static String appName = "DicomReader " + version + " - Dicom file decoder";
    
    private String dicomFileName = null;
    private String headersFileName = null;
    private String asciiFileName = null;
    private String imageFileName = null;
    
    private File dicomF = null;
    private File headersF = null;
    private File asciiF = null;
    private File imageF = null;
    
    private JButton browseDicomButton = null;
    private JButton browseHeadersButton = null;
    private JButton browseAsciiButton = null;
    private JButton decodeButton = null;
    
    private JCheckBox pgmCheckBox = null;
    
    private JTextField dicomField = null;
    private JTextField headersField = null;
    private JTextField asciiField = null;
    
    private boolean tempSet = false;
    private boolean setDicomName = false;
    private boolean setHeadersName = false;
    private boolean setAsciiName = false;
    private boolean savePGM = false;
    
    
    /**
     * The class constructor puts all graphics application's components.
     */
    public DicomReaderGUI() {
        super("DicomReader - Dicom file decoder");
        
        // Handlers
        ActionEventHandler handler = new ActionEventHandler();
        ItemEventHandler itemHandler = new ItemEventHandler();
        
        // Main container
        Container c = getContentPane();
        c.setLayout(new BorderLayout(5, 10));
        // End Container
        
        // Panels
        
        // topPanel
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout(2, 10));
        JLabel voidLabel = new JLabel("                                       " +
        							  "                                       ");
        topPanel.add(voidLabel);
        
        c.add(topPanel, BorderLayout.NORTH);
        
        JPanel generalPanel = new JPanel();
        generalPanel.setLayout(new BorderLayout(10, 10));
        
        c.add(generalPanel, BorderLayout.CENTER);
        
        JPanel contentsPanel = new JPanel();
        contentsPanel.setLayout(new FlowLayout());
        
        JPanel boxPanel = new JPanel();
        boxPanel.setLayout(new BorderLayout());
        
        boxPanel.add(contentsPanel, BorderLayout.CENTER);
        generalPanel.add(boxPanel, BorderLayout.CENTER);
        
        // generalPanel.add(contentsPanel, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel();
        JLabel bottomLabel = new JLabel(appName);
        bottomLabel.setEnabled(true); // false
        bottomPanel.add(bottomLabel);
        
        c.add(bottomPanel, BorderLayout.SOUTH);
        
        // Logo
        Icon icon = new ImageIcon(getClass().getResource("data/resources/icons/DicomReader.png"));
        //Icon icon = new ImageIcon("data/resources/icons/DicomReader.png");
        JLabel iconLabel = new JLabel("", icon, SwingConstants.CENTER);
        Image favicon = Toolkit.getDefaultToolkit().getImage("data/resources/icons/DicomReaderIcon.png");
        this.setIconImage(favicon);
        
        //iconLabel.setBorder(new BevelBorder(BevelBorder.LOWERED, Color.LIGHT_GRAY, Color.DARK_GRAY));
        generalPanel.add(iconLabel, BorderLayout.WEST);
        
        // dicomPanel
        JPanel dicomPanel = new JPanel();
        dicomPanel.setLayout(new BorderLayout());
        
        JLabel dicomLabel = new JLabel("Dicom file:");
        dicomPanel.add(dicomLabel, BorderLayout.NORTH);
        
        JPanel dicomName = new JPanel();
        dicomName.setLayout(new BorderLayout(10, 10));
        
        dicomField = new JTextField(25);
        dicomName.add(dicomField, BorderLayout.CENTER);
        
        browseDicomButton = new JButton("1 Browse...");
        browseDicomButton.setMnemonic(KeyEvent.VK_1);
        browseDicomButton.addActionListener(handler);
        
        dicomName.add(browseDicomButton, BorderLayout.EAST);
        dicomPanel.add(dicomName, BorderLayout.SOUTH);
        
        contentsPanel.add(dicomPanel);
        
        // headersPanel
        JPanel headersPanel = new JPanel();
        headersPanel.setLayout(new BorderLayout());
        
        JLabel headersLabel = new JLabel("Headers file:");
        headersPanel.add(headersLabel, BorderLayout.NORTH);
        
        JPanel headersName = new JPanel();
        headersName.setLayout(new BorderLayout(10, 10));
        
        headersField = new JTextField(25);
        headersName.add(headersField, BorderLayout.CENTER);
        
        browseHeadersButton = new JButton("2 Browse...");
        browseHeadersButton.setMnemonic(KeyEvent.VK_2);
        browseHeadersButton.addActionListener(handler);
        
        headersName.add(browseHeadersButton, BorderLayout.EAST);
        headersPanel.add(headersName, BorderLayout.SOUTH);
        
        contentsPanel.add(headersPanel);
        
        // asciiPanel
        JPanel asciiPanel = new JPanel();
        asciiPanel.setLayout(new BorderLayout());
        
        JLabel asciiLabel = new JLabel("ASCII image file:");
        asciiPanel.add(asciiLabel, BorderLayout.NORTH);
        
        JPanel asciiName = new JPanel();
        asciiName.setLayout(new BorderLayout(10, 10));
        
        asciiField = new JTextField(25);
        asciiName.add(asciiField, BorderLayout.CENTER);
        
        browseAsciiButton = new JButton("3 Browse...");
        browseAsciiButton.setMnemonic(KeyEvent.VK_3);
        browseAsciiButton.addActionListener(handler);
        
        asciiName.add(browseAsciiButton, BorderLayout.EAST);
        asciiPanel.add(asciiName, BorderLayout.SOUTH);
        
        contentsPanel.add(asciiPanel);
        // End Panels
        
        // CheckBox
        pgmCheckBox = new JCheckBox("Save PGM image");
        pgmCheckBox.setMnemonic(KeyEvent.VK_P);
        pgmCheckBox.setSelected(false);
        pgmCheckBox.addItemListener(itemHandler);
        
        boxPanel.add(pgmCheckBox, BorderLayout.SOUTH);
        
        // contentsPanel.add(pgmCheckBox);
        // End CheckBox
        
        contentsPanel.add(topPanel);
        
        // Buttons
        JPanel decodePanel = new JPanel();
        decodeButton = new JButton("  Decode Dicom File  ");
        decodeButton.setMnemonic(KeyEvent.VK_D);
        decodeButton.addActionListener(handler);
        decodePanel.add(decodeButton);
        
        generalPanel.add(decodePanel, BorderLayout.SOUTH);
        // End Buttons
        
        // Menu
        JMenuBar bar = new JMenuBar();
		setJMenuBar(bar);
		
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic('F');
		
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.setMnemonic('x');
		exitItem.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.err.println("Closing...");
				    JOptionPane.showMessageDialog(null,
                        	"Thank you for using\n\n" + appName,
                			"DicomReader is closing...",
                			JOptionPane.PLAIN_MESSAGE);
					System.exit(0);
				}
			}
		);
		fileMenu.add(exitItem);
		
		bar.add(fileMenu);
		
		JMenu helpMenu = new JMenu("About");
		helpMenu.setMnemonic('A');
		
		/*
		JMenuItem usageItem = new JMenuItem("Usage");
		usageItem.setMnemonic('U');
		usageItem.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
					    JOptionPane.showMessageDialog(DicomReaderGUI.this,
					            						"Keep the Dicom.dic (Dicom dictionary)" + "\n" +
					            						" in the same directory of the DicomReader classes." + "\n\n" +
					            						"Fill the file names' fields and run the application.",
					            						"DicomReader usage hints",
					            						JOptionPane.INFORMATION_MESSAGE);
					}
				}
			);
			
		helpMenu.add(usageItem);
		*/
		
		JMenuItem gplItem = new JMenuItem("GPL");
		gplItem.setMnemonic('G');
		gplItem.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
				    ShowGPL.display();
				}
			}
		);
		
		helpMenu.add(gplItem);
		
		JSeparator separator = new JSeparator();
		helpMenu.add(separator);
		
		JMenuItem aboutItem = new JMenuItem("DicomReader Info");
		aboutItem.setMnemonic('I');
		aboutItem.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					/*
					JOptionPane.showMessageDialog(DicomReaderGUI.this,
    												appName + "." + "\n\n" +
    												"Author:" + "\n\n" +
    												" Salvatore La Bua" +
    												"\n   < slabua (at) gmail.com >" +
    												"\n   https://github.com/slabua/dicomreader   ",
    												"Credits",
    												JOptionPane.INFORMATION_MESSAGE);
    				*/
					String info = "<html><body>" +
								appName + "." + "<br><br>" +
								"Author:<br><br>" +
								"&nbsp;Salvatore La Bua" +
								"<br />&nbsp;&nbsp;&nbsp;&lt; slabua (at) gmail.com &gt;<br />" +
								"<br />&nbsp;&nbsp;&nbsp;<a href=\"http://www.slblabs.com/projects/dicomreader\">" +
								"http://www.slblabs.com/projects/dicomreader</a>&nbsp;&nbsp;&nbsp;" +
								"<br />&nbsp;&nbsp;&nbsp;<a href=\"https://github.com/slabua/dicomreader\">" +
								"https://github.com/slabua/dicomreader</a>&nbsp;&nbsp;&nbsp;" +
								"<br /></body></html>";
					
					JEditorPane infoPane = new JEditorPane("text/html", info);
					
					infoPane.addHyperlinkListener(new HyperlinkListener() {
						
					    @Override
				        public void hyperlinkUpdate(HyperlinkEvent e) {
					    	if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED))
								try {
									java.awt.Desktop.getDesktop().browse(java.net.URI.create(e.getURL().toString()));
								} catch (IOException e1) {
									e1.printStackTrace();
								}
				        }
				    });
					
					infoPane.setText(info);
					infoPane.setOpaque(false);
				    infoPane.setEditable(false);
				    infoPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
				    //infoPane.setFont(new Font("Lucida Console", Font.PLAIN, 12));
				    infoPane.setFont(new JLabel().getFont());
				    
				    JOptionPane.showMessageDialog(DicomReaderGUI.this, infoPane,
													"Credits",
													JOptionPane.INFORMATION_MESSAGE);
				}
			}
		);
		
		helpMenu.add(aboutItem);
		
		bar.add(helpMenu);
		// End Menu
    
    } // End of DicomReaderGUI constructor
    
    /**
     * The <code>openFileName</code> method shows a file chooser dialog window
     *  to select the file we wish to open.
     * 
     * @return fileName <code>File</code> File to open for the reading.
     */
    private File openFileName() {
        File fileName = null;
        
        JFileChooser chooser = new JFileChooser("./");
        int returnVal = chooser.showOpenDialog(DicomReaderGUI.this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            fileName = chooser.getSelectedFile();
            tempSet = true;
        } else {
            System.err.println("Operation canceled by user");
            tempSet = false;
        } // End of if-else block
        
        return fileName;
        
    } // End of getFileName method
    
    /**
     * The <code>saveFileName</code> method shows a file chooser dialog window
     *  to select the file we wish to save.
     * 
     * @return fileName <code>File</code> File to save the elaboration's results.
     */
    private File saveFileName() {
        File fileName = null;
        
        JFileChooser chooser = new JFileChooser("./");
        int returnVal = chooser.showSaveDialog(DicomReaderGUI.this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            fileName = chooser.getSelectedFile();
            tempSet = true;
        } else {
            System.err.println("Operation canceled by user");
            tempSet = false;
        } // End of if-else block
        
        return fileName;
        
    } // End of getFileName method
    
    /**
     * The <code>setDicomFileName</code> method sets the Dicom file to analyze.
     * 
     * @return dicomF <code>File</code> Dicom file to analyze.
     */
    private File setDicomFileName() {
        dicomF = this.openFileName();
        
        if (tempSet) {
        	setDicomName = true;
        	tempSet = false;
        	dicomFileName = dicomF.getAbsolutePath();
            dicomField.setText(dicomFileName);
        } else {
            // setDicomName = false;
            // dicomField.setText(null);
        } // End if-else block
        
        // this.enableButtons();
        
        return dicomF; 
    } // End of setDicomFileName method
    
    /**
     * The <code>setHeadersFileName</code> method sets the headers file where
     *  headers will be saved to.
     * 
     * @return headersF <code>File</code> Headers file where to save to.
     */
    private File setHeadersFileName() {
        headersF = this.saveFileName();
        
        if (tempSet) {
            setHeadersName = true;
            tempSet = false;
            headersFileName = headersF.getAbsolutePath();
            headersField.setText(headersFileName);
        } else {
            // setHeadersName = false;
            // headersField.setText(null);
        } // End if-else block
        
        // this.enableButtons();
        
        return headersF; 
    } // End of setHeadersFileName method
    
    /**
     * The <code>setAsciiFileName</code> method sets the ascii image file where
     *  pixel values will be saved to.
     * 
     * @return asciiF <code>File</code> Ascii image file where to save to.
     */
    private File setAsciiFileName() {
        asciiF = this.saveFileName();
        
        if (tempSet) {
            setAsciiName = true;
            tempSet = false;
            asciiFileName = asciiF.getAbsolutePath();
            asciiField.setText(asciiFileName);
        } else {
            // setAsciiName = false;
            // asciiField.setText(null);
        } // End if-else block
        
        // this.enableButtons();
        
        return asciiF; 
    } // End of setAsciiFileName method
    
    /**
     * The <code>enableButtons</code> method <i>was</i> used to enable the decode
     *  button and the pgm checkbox only when all file names fields were all filled.
     * 
     * @deprecated It isn't still in use.
     */
    private void enableButtons() {
        if (setDicomName && setHeadersName && setAsciiName) {
            pgmCheckBox.setEnabled(true);
            decodeButton.setEnabled(true);
        } else {
            pgmCheckBox.setEnabled(false);
            decodeButton.setEnabled(false);
        } // End of if-else block
    } // End of enableDecodeButton method
    
    /**
     * The <code>callDecode</code> method takes care of initializing the file
     *  names that will be used in the decode process, sets some variables and
     *  calls the <code>decode</code> method.
     */
    private void callDecode() {
        
        dicomFileName = dicomField.getText();
        headersFileName = headersField.getText();
        asciiFileName = asciiField.getText();
        
        int startExt = asciiFileName.lastIndexOf(".");
        
        if (startExt != -1) {
            imageFileName = asciiFileName.substring(0, startExt) + ".pgm";
        } else {
            imageFileName = asciiFileName + ".pgm";
        } // End of if-else block
        
        dicomF = new File(dicomFileName);
        
        headersF = new File(headersFileName);
        asciiF = new File(asciiFileName);
        imageF = new File(imageFileName);
        
		DicomReader dicom = null;
		if (savePGM) {
		    dicom = new DicomReader(dicomF, headersF, asciiF, imageF);
		} else {
		   dicom = new DicomReader(dicomF, headersF, asciiF);
		} // End of if-else block
		dicom.decode();
		
		String message = "";
		if (dicom.getHeadersSaved())
		    message += "Headers file saved at:\n " + headersFileName + "\n\n";
		if (dicom.getAsciiSaved())
		    message += "ASCII image file saved at:\n " + asciiFileName + "\n\n";
		if (dicom.getImageSaved())
		    message += "PGM image file saved at:\n " + imageFileName;
		
		System.out.println(message);
		JOptionPane.showMessageDialog(DicomReaderGUI.this, message, "Results",
		        								JOptionPane.INFORMATION_MESSAGE);
		
    } // End of callDecode method
    
    /**
     * This is the <i>main</i> method of the application.<br />
     * It sets the window's dimensions and shows it in the center of the screen.
     */
    public static void main(String[] args) {
        final DicomReaderGUI window = new DicomReaderGUI();
        
        
        // Setting Look and Feel
        try {
        	//UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        } catch (Exception e) {
		    System.err.println(e);
		    JOptionPane.showMessageDialog(null, e, "Exception", JOptionPane.ERROR_MESSAGE);
		    System.exit(-1);
		} // End of try-catch block
        
        
        window.setTitle(appName);
        window.setSize(560, 300); // 420 // 320
        window.setResizable(false);
        window.setLocationRelativeTo(null);
        // window.setAlwaysOnTop(true);
        window.setVisible(true);
        window.addWindowListener(
                new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                    	window.setVisible(false);
                        System.err.println("Closing...");
                        JOptionPane.showMessageDialog(null,
                                	"Thank you for using\n\n" + appName,
                        			"DicomReader is closing...",
                        			JOptionPane.PLAIN_MESSAGE);
                        System.exit(0);
                    }
                }
        );
        
    } // End of main method
    
    /**
     * The <code>ActionEventHandler</code> private class handles events for the
     *  application's buttons.
     */
    private class ActionEventHandler implements ActionListener {
    	public void actionPerformed(ActionEvent e) {
    	    
    	    if (e.getSource() == browseDicomButton) {
     		    setDicomFileName();
     		} else if (e.getSource() == browseHeadersButton) {
     		    setHeadersFileName();
     		} else if (e.getSource() == browseAsciiButton) {
     		    setAsciiFileName();
     		} else if (e.getSource() == decodeButton) {
     		    
     		   if (dicomField.getText().equals("")   ||
     		       headersField.getText().equals("") ||
     		       asciiField.getText().equals("")     ) {
     		       
     		       String message = "You must insert valid file names.";
     		       System.err.println(message);
     		       JOptionPane.showMessageDialog(DicomReaderGUI.this, message,
                          		"Missing file name", JOptionPane.ERROR_MESSAGE);
                   
     	       } else {
     	           
     	           dicomF = new File(dicomField.getText());
     	           if (!dicomF.canRead()) {
     	               String message = "Dicom file does not exist." + "\n" +
     	               					"Please specify a valid Dicom file to open.";
     	               System.err.println(message);
        		       JOptionPane.showMessageDialog(DicomReaderGUI.this, message,
                             		"Wrong file name", JOptionPane.ERROR_MESSAGE);
                      
     	           } else {
     	              callDecode();
     	           } // End of if-else block
     	           
     	       } // End of if-else block
     		   
     		} // End of if-else block
     		
     	} // End of actionPerformed method
    } // End of ActionEventHandler private class
    
    /**
     * The <code>ItemEventHandler</code> private class handles events for the
     *  application's checkbox.
     */
    private class ItemEventHandler implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            Object source = e.getItemSelectable();
            
            if (source == pgmCheckBox) {
	            
	            if (e.getStateChange() == ItemEvent.SELECTED) {
	                
	                if (!asciiField.getText().equals("")) {
	                    savePGM = true;
	                	System.err.println("Save PGM image flag status: Selected");
	                } else {
	                    savePGM = false;
	                    System.err.println("Save PGM image flag status: Selected");
	                    String message = "You must insert an ASCII image file name.";
	                    System.err.println(message);
	                    JOptionPane.showMessageDialog(DicomReaderGUI.this, message,
	                            	"Missing file name", JOptionPane.ERROR_MESSAGE);
	                    pgmCheckBox.setSelected(false);
	                } // End of if-else block
	                
	            } else {
	                savePGM = false;
	                System.err.println("Save PGM image flag status: Deselected");
	            } // End of if-else block
	            
            } // End of if block
            
        } // End of itemStateChanged method
    } // End of ItemEventHandler private class
    
} // End of DicomReaderGUI class
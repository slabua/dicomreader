/***************************************************************************
* DicomReader.java                                                         *
*--------------------------------------------------------------------------*
*                                                                          *
* created on   : 20 mar 2005                                               *
* copyright    : (C) 2005, Salvatore La Bua      <SLB(at)Shogoki.it>       *
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


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;


/**
 * The <code>DicomReader</code> class decodes Dicom files writing headers file
 *  and images files.<br />
 * The image(s) files is simply composed of ascii pixel values ordered by rows
 *  and columns as in a trivial image.
 * 
 * @author Salvatore La Bua    <i>< slabua (at) gmail.com ></i>
 * 
 * @version 1.3.1
 * 
 * @see LoadDict
 */
public class DicomReader {
    
    // Application version
    private final static String version = "1.3.1";
    
    // Debug flag
    private static boolean DEBUG = true;
    
    // Files
    private static File dicomF = null;
    private static File headersF = null;
    private static File asciiF = null;
    private static File imageF = null;
    
    // Utility Flags
    private boolean isDicomFile = true;
    private boolean asciiSaved = false;
    private boolean imageSaved = false;
    private boolean headersSaved = false;
    
    // Streams
    private FileInputStream dicomFile = null;
    private BufferedWriter headersFile = null;
    
    // Temporary Buffers
    private byte[] buff1 = new byte[1];
	private byte[] buff2 = new byte[2];
	private byte[] buff4 = new byte[4];
	private byte[] buff8 = new byte[8];
	private int tempBuff = 0;
	
	// Dicom tag's attributes
	private String tagID = null;
	private String tagName = null;
	private String tagVR = null;
	private String content = null;
	
	// Utility variable used to handle the "SQ ReferencedImageSequence" tag
	private int seqN = 0;
	
	// Image class object that will contain image's parameters
	private Image image = new Image();
	
	
	/**
	 * Four parametrs <code>DicomReader</code> class constructor.<br />
	 * The constructor simply initializes variables' values to file names passed
	 *  as parameters to the class.
	 * 
	 * @param dfn <code>File</code> Dicom file name to decode.
	 * @param hfn <code>File</code> Headers file name.
	 * @param afn <code>File</code> Ascii image file name.
	 * @param ifn <code>File</code> PGM-P2 image file name.
	 */
	public DicomReader(File dfn, File hfn, File afn, File ifn) {
	    dicomF = dfn;
	    headersF = hfn;
	    asciiF = afn;
	    imageF = ifn;
	} // End of DicomReader(dfn, hfn, afn, ifn) constructor
	
	/**
	 * Three parametrs <code>DicomReader</code> class constructor.<br />
	 * The constructor simply initializes variables' values to file names passed
	 *  as parameters to the class.
	 * 
	 * @param dfn <code>File</code> Dicom file name to decode.
	 * @param hfn <code>File</code> Headers file name.
	 * @param afn <code>File</code> Ascii image file name.
	 */
	public DicomReader(File dfn, File hfn, File afn) {
	    dicomF = dfn;
	    headersF = hfn;
	    asciiF = afn;
	    imageF = null;
	} // End of DicomReader(dfn, hfn, afn, ifn) constructor
	
	/**
	 * The <code>decode</code> method is the main method of the class.<br />
	 * It will use other private methods to do the more simplest functions such
	 *  as checks, informations reading/writing...
	 */
    public void decode() {
        
        // Utility flags initialization
        asciiSaved = false;
        imageSaved = false;
        headersSaved = false;
        
        // Dicom dictionary
        LoadDict dict = new LoadDict();
        
        try {
            
            // Streams opening
            dicomFile = new FileInputStream(dicomF);
            headersFile = new BufferedWriter(new FileWriter(headersF));
            
            // Dicom file type checking (with or without DICM string header)
            dicomFile = this.checkDICM(dicomFile);
            
            // Starting tags interpretation from Dicom file
            while (true) {
                
                // Reading next tag
                tagID = this.nextTagID(dicomFile);
                
                // If tagID doesn't exist in the Dicom dictionary, it will be
                //  ignored with its values
                while (!dict.isContained(tagID)) {
                    int bytesToRead = this.readBytes(dicomFile, tagID, null);
                    byte[] value = new byte[bytesToRead];
    		    	dicomFile.read(value);
    		    	tagID = this.nextTagID(dicomFile);
                } // End of while block
                
                // Getting tag's name and type
                tagName = dict.getName(tagID);
                tagVR = dict.getVR(tagID);
                
                // "ox PixelData" tag check (last tag)
                if (tagName.equals("PixelData")) {
                    // Skip 4 bytes
                    dicomFile.read(buff4);
                    
                    String sValue = this.lastTagHandling();
                    content = tagID + ": " + tagVR + ": " + tagName + ": " + sValue;
                    
                    headersFile.write(content);
                	headersFile.close();
                    
                	// Interrupts the while block when reading last tag
                    break;
                } // End of if block
                
                // "OB FileMetaInformationVersion" tag check
                if (tagID.equals("(0002,0001)"))
                    // Skip 4 bytes
                    dicomFile.read(buff4);
                
                // Reading the correct number of bytes by the current tag's type
                int bytesToRead = this.readBytes(dicomFile, tagID, tagVR);
                
                // "SQ ReferencedImageSequence" tag check
		    	if (tagID.equals("(0008,1140)")) {
		    	    seqN = ((bytesToRead / 86) * 2);
		    	    String sValue = Integer.toString(seqN);
		    	    
		    	    // Skip 8 bytes
		    	    dicomFile.read(buff8);
		    	    
		    	    content = tagID + ": " + tagVR + ": " + tagName + ": (" + sValue + ")";
                    
		    	    headersFile.write(content + "\n");
                	
                    tagID = this.nextTagID(dicomFile);
                    
                    tagName = dict.getName(tagID);
                    tagVR = dict.getVR(tagID);
                    
                    bytesToRead = this.readBytes(dicomFile, tagID, tagVR);
                    
		    	} // End of if block
		    	
		    	// "SQ ReferencedImageSequence" sub-tags handling
		    	if (seqN != 0)
		    	    bytesToRead = bytesToRead - 1;
		    	
		    	// Reading tag's value
		    	byte[] value = new byte[bytesToRead];
		    	dicomFile.read(value);
		    	String sValue = "";
		    	
		    	// "SQ ReferencedImageSequence" sub-tags handling
		    	if (seqN != 0)
		    	    dicomFile.read(buff1);
		    	
		    	////////////////////////////////////////////////////////////////
		    	// DEBUG  //////////////////////////////////////////////////////
		    	////////////////////////////////////////////////////////////////
		    	if (DEBUG)
		    	    System.out.println(content);
		    	////////////////////////////////////////////////////////////////
		    	
		    	// Handling tags' values by the current tag's type
		    	if ((tagVR.equals("US") || tagVR.equals("UL") || tagVR.equals("xs"))) {
		    	    int nValue = 0;
		    		nValue = (((0x000000ff & value[1]) << 8) |
		    		           (0x000000ff & value[0])        );
		    		
		    		sValue = Integer.toString(nValue);
		    		
		    	}
		    	// "SQ ReferencedImageSequence" tag check
		    	else if (tagID.equals("(0008,1140)")) {
		    	    seqN = bytesToRead / 86;
		    	    // Skip 8 bytes
		    	    dicomFile.read(buff8);
		    	    
		    	}
		    	// "OB FileMetaInformationVersion" tag check
		    	else if (tagID.equals("(0002,0001)")) {
		    	    int nValue = 0;
		    		nValue = ((0x000000ff & value[1])     |
		    		         ((0x000000ff & value[0]) << 8));
		    		
		    		sValue = Integer.toString(nValue);
		    		
		    	}
		    	else {
		    	    sValue = new String(value);
		    	} // End of if-else block
		    	
		    	content = tagID + ": " + tagVR + ": " + tagName + ": " + sValue;
		    	
		    	headersFile.write(content + "\n");
		    	
		    	// "SQ ReferencedImageSequence" sub-tags handling
		    	if (seqN != 0) {
		    	    if ((seqN % 2) == 0) {
		    	        seqN--;
		    	    } else {
		    	        if (seqN != 1) {
		    	            // Skip 8 bytes
		    	            dicomFile.read(buff8);
		    	        } // End of if block
			    	    seqN--;
		    	    } // End of if block
		    	} // End of if block
		    	
		    	// Storing tags that are image-related
		    	this.storeImageParam(tagID, sValue);
		    	
            } // End of while block
            
            headersFile.close();
            headersSaved = true;
            
            // System.out.println("Headers saved to file: \"" + headersFileName + "\"");
            
            // Image storing
            // If the saveImage method's parameter is set to true, a pgm image
            //  file will be saved
            if (imageF == null) {
                // this.saveImage(false);
                this.storeData(false);
            } else {
                // this.saveImage(true);
                this.storeData(true);
            } // End of if-else block
            
        } catch (IOException ioe) {
		    System.err.println(ioe);
		    JOptionPane.showMessageDialog(null, ioe, "Exception", JOptionPane.ERROR_MESSAGE);
		    System.exit(-1);
		} // End of try-catch block
        
    } // End of decode() method
    
    /**
     * The <code>getFileName</code> method shows a file chooser dialog window
     *  to select the file we wish to analyze.
     * 
     * @return fileName <code>File</code> File name to analyze.
     * 
     * @deprecated It isn't still in use.
     */
    private File getFileName() {
        File fileName = null;
        
        JFileChooser chooser = new JFileChooser("./");
        int returnVal = chooser.showOpenDialog(null);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            fileName = chooser.getSelectedFile();
            System.err.println("Another Dicom file selected");
        } else {
            System.err.println("Operation canceled by user");
        } // End of if-else block
        
        return fileName;
        
    } // End of getFileName method
    
    /**
     * The <code>chechDICM</code> method checks if the chosen file has the "DICM"
     *  header and acts to handle the file recognized.
     * 
     * @param df <code>FileInputStream</code> File to check.
     * 
     * @return dicomFile <code>FileInputStream</code> File eventually updated.
     */
    private FileInputStream checkDICM(FileInputStream df) {
        FileInputStream dicomFile = df;
        
        try {
            
            // Reading first four bytes of the Dicom file to decode.
            dicomFile.read(buff4);
            tempBuff = (((0x000000ff & buff4[3]) << 32) |
                    	((0x000000ff & buff4[2]) << 16) |
		   		   		((0x000000ff & buff4[1]) <<  8) |
		   		   		 (0x000000ff & buff4[0])         );
            dicomFile.close();
            dicomFile = new FileInputStream(dicomF);
            
            // If tempBuff contains a zero data value, we can have a Dicom file
            //  with "DICM" header (in this case first 128 bytes of the file are
            //  all zeros), either we can have a file that isn't a Dicom file.
            if (tempBuff == 0) {
                int counter = 0;
                dicomFile.read(buff1);
                counter++;
                
                while ((tempBuff = buff1[0]) != 'M') {
                    if (counter == 132) {
                        isDicomFile = false;
                        String errorString = "This file not seems to be a Dicom file." + "\n\n" +
                        					 "DICM string not found at 132nd byte.";
                    	System.err.println(errorString);
                    	JOptionPane.showMessageDialog(null, errorString, "Wrong File", JOptionPane.ERROR_MESSAGE);
                    	System.exit(-1);
                    	
                    } // End of if block
                    
                    dicomFile.read(buff1);
                    counter++;
                } // End of while block
                
            // If first four bytes aren't zeros, we can have a Dicom "DICM"
            //  headerless file (in this case we assume first four bytes identify
            //  a valid Dicom tag),  either we can have a file that isn't a Dicom
            //  file.
            } else {
                LoadDict dict = new LoadDict();
                
                String tagID = this.nextTagID(dicomFile);
                dicomFile.close();
                dicomFile = new FileInputStream(dicomF);
                
                if (!dict.isContained(tagID)) {
                    isDicomFile = false;
                    String errorString = "This file not seems to be a Dicom file." + "\n\n" +
                    					 "DICM stringless Dicom file:" + "\n" +
                    					 "First tag is not a valid Dicom tag.";
                	System.err.println(errorString);
                	JOptionPane.showMessageDialog(null, errorString, "Wrong File", JOptionPane.ERROR_MESSAGE);
                	System.exit(-1);
                } // End of if block
                
            } // End of if-else block
            
        } catch (IOException ioe) {
		    System.err.println(ioe);
		    JOptionPane.showMessageDialog(null, ioe, "Exception", JOptionPane.ERROR_MESSAGE);
		    System.exit(-1);
		} // End of try-catch block
        
        return dicomFile;
        
    } // End of checkDICM(df) method
    
    /**
     * The <code>nextTagID</code> method contructs the identifying string of the
     *  Dicom tag that will be searched into the Dicom dictionary to obtain its
     *  description. 
     * 
     * @param df <code>FileInputStream</code> File to check.
     *  
     * @return tagID <code>String</code> Current tag representative string.
     */
    private String nextTagID(FileInputStream df) {
        FileInputStream dicomFile = df;
        
        String group = null;
        String number = null;
        String tagID = null;
        
        try {
            
            // Extracting group from first two bytes of tagID
            dicomFile.read(buff2);
            tempBuff = (((0x000000ff & buff2[1]) << 8) |
                    	 (0x000000ff & buff2[0]));
            
            group  = Integer.toString((tempBuff & 0x0000f000) >> 12, 16);
            group += Integer.toString((tempBuff & 0x00000f00) >>  8, 16);
            group += Integer.toString((tempBuff & 0x000000f0) >>  4, 16);
            group += Integer.toString((tempBuff & 0x0000000f),       16);
            
            // Extracting number from last two bytes of tagID
            dicomFile.read(buff2);
            tempBuff = (((0x000000ff & buff2[1]) << 8) |
                    	 (0x000000ff & buff2[0]));
            
            number  = Integer.toString((tempBuff & 0x0000f000) >> 12, 16);
            number += Integer.toString((tempBuff & 0x00000f00) >>  8, 16);
            number += Integer.toString((tempBuff & 0x000000f0) >>  4, 16);
            number += Integer.toString((tempBuff & 0x0000000f),       16);
            
            // Constructing the tagID string
            tagID = ("(" + group + "," + number + ")");
            
        } catch (IOException ioe) {
		    System.err.println(ioe);
		    JOptionPane.showMessageDialog(null, ioe, "Exception", JOptionPane.ERROR_MESSAGE);
		    System.exit(-1);
		} // End of try-catch block
        
        return tagID;
        
    } // End of nextTagID(df) method
    
    /**
     * The <code>readBytes</code> method determines how many bytes must be read
     *  from time to time during the Dicom file's headers analysis.
     * 
     * @param df <code>FileInputStream</code> File to check.
     * @param tID <code>String</code> Current tag representative string.
     * 
     * @return readBytes <code>int</code> Number of bytes to read.
     */
    private int readBytes(FileInputStream df, String tID, String tVR) {
        FileInputStream dicomFile = df;
        String tagID = tID;
        String tagVR = tVR;
        int bytesToRead = 0;
        int bytesTemp = 0;
        
        String group = tagID.substring(1, 5);
        String number = tagID.substring(6, 10);
        
        try {
            
            dicomFile.read(buff4);
            
            if (tagID.equals("(0011,1001)")) {
		        bytesToRead = (((0x000000ff & buff4[3]) << 32) |
	   	   	   		   		   ((0x000000ff & buff4[2]) << 16) |
	   	   	   		   		   ((0x000000ff & buff4[1]) <<  8) |
	   	   	   		   		    (0x000000ff & buff4[0])         );
		    } else {
		    	bytesToRead = (((0x000000ff & buff4[3]) << 8) |
		              			(0x000000ff & buff4[2])        );
	           	
		    	if ((bytesToRead == 65535) /*FF FF FF FF*/ ) {
		    	    bytesTemp = (((0x000000ff & buff4[1]) << 8) |
        	        			  (0x000000ff & buff4[0])        );
		    	    
		    	    if (bytesTemp == bytesToRead) {
		    	        bytesToRead = 0;
		    	    } // End of if block
		    	    
		    	} else if (bytesToRead == 0) {
		        	bytesToRead = (((0x000000ff & buff4[1]) << 8) |
		        	        		(0x000000ff & buff4[0])        );
		        	
		        	if ((bytesToRead == 20819) /*SQ*/ ) {
		        	    
		        	    dicomFile.read(buff4);
		        	    bytesToRead = 0;
		        	    
		        	} else if ((bytesToRead == 16708) /*DA*/ || (bytesToRead == 20300) /*LO*/ ||
		        	           (bytesToRead == 19796) /*TM*/ || (bytesToRead == 18515) /*SH*/ ||
		        	           (bytesToRead == 21315) /*CS*/ || (bytesToRead == 21321) /*IS*/ ||
		        	           // Altri tag
		        	           (bytesToRead == 18773) /*UI*/ || (bytesToRead == 20048) /*PN*/ ||
		        	           (bytesToRead == 21580) /*LT*/ || (bytesToRead == 21316) /*DS*/   ) {
		        	    
		        	    bytesToRead = (((0x000000ff & buff4[3]) << 8) |
	    	           					(0x000000ff & buff4[2])        );
		        	    
	           		} // End of if-else block
		        	
		    	} // End of else-if block
		    } // End of if-else block
	        
        } catch (IOException ioe) {
		    System.err.println(ioe);
		    JOptionPane.showMessageDialog(null, ioe, "Exception", JOptionPane.ERROR_MESSAGE);
		    System.exit(-1);
		} // End of try-catch block
        
        return bytesToRead;
        
    } // End of readBytes(df, tID) method
    
    /**
     * The <code>lastTagHandling</code> method handles the last header's tag
     *  that requires a different treatment than others.
     * 
     * @return sValue <code>String</code> Last tag's numeric value.
     */
    private String lastTagHandling() {
        String sValue = null;
        
        tempBuff = (((0x000000ff & buff4[3]) << 32) |
                	((0x000000ff & buff4[2]) << 16) |
                	((0x000000ff & buff4[1]) <<  8) |
                	 (0x000000ff & buff4[0])         );
        
        sValue = Integer.toString(tempBuff);
        
        return sValue;
        
    } // End of lastTagHandling method
    
    /**
     * The <code>storeImageParam</code> method sets the <code>Image</code>
     *  object's fields.<br />
     * It will contain some informations to store the image, such as dimensions,
     *  color depth and so on.
     * 
     * @param tID <code>String</code> Current tag representative string.
     * @param sV <code>String</code> Value to store into the object's attribute.
     * 
     * @see Image
     */
    private void storeImageParam(String tID, String sV) {
        String tagID = tID;
        String sValue = sV;
        
        // Rows
        if (tagID.equals("(0028,0010)")) {
    	    image.rows = Integer.parseInt(sValue);
    	}
        // Columns
        else if (tagID.equals("(0028,0011)")) {
    	    image.cols = Integer.parseInt(sValue);
    	}
        // BitsAllocated, not BitsStored!
        else if (tagID.equals("(0028,0100)")) {
    	    image.colorDepth = Integer.parseInt(sValue);
    	}
        // NumberOfFrames (or NumberOfSlices or NumberOfFramesInRotation)
        else if (tagID.equals("(0054,0081)")) { // || tagID.equals("(0028,0008)")) || tagID.equals("(0054,0053)")) {
            image.slices = Integer.parseInt(sValue);
        }
        // PixelData
        else if (tagName.equals("PixelData")) {
            image.totBytes = Integer.parseInt(sValue);
        } // End of if-else block
    	
    } // End of storeImageParam(tID, sV) method
    
    /**
     * The <code>storeData</code> method recognizes Dicom files with more than
     *  one slice and executes the <code>saveImage</code> method to obtain the
     *  right number of image files.
     * 
     * @param pgm <code>boolean</code> If <code>true</code> the method will
     *  insert the PGM-P2 headers into the image file.
     */
    private void storeData(boolean pgm) {
        String asciiFileName = asciiF.getAbsolutePath();
        String imageFileName = null;
        if (imageF != null)
            imageFileName = imageF.getAbsolutePath();
        
        int totSlices = image.slices;
        
        if (totSlices == 1) {
            
            this.saveImage(asciiFileName, imageFileName, pgm);
            
        } else {
            
            int startExt = asciiFileName.lastIndexOf(".");
            String ext = null;
            if (startExt != -1)
                ext = asciiFileName.substring(startExt, asciiFileName.length());
            
            for (int currentSlice = 0; currentSlice < totSlices; currentSlice++) {
                
                if (startExt != -1) {
                    asciiFileName = asciiFileName.substring(0, startExt + 1) + (currentSlice + 1) + ext;
                    imageFileName = asciiFileName.substring(0, startExt + 1) + (currentSlice + 1) + ".pgm";
                } else {
                    asciiFileName = asciiFileName + "." + (currentSlice + 1);
                    imageFileName = asciiFileName + "." + (currentSlice + 1) + ".pgm";
                } // End of if-else block
                
                this.saveImage(asciiFileName, imageFileName, pgm);
            } // End of for block
            
        } // End of if-else block
        
    } // End of storeData method
    
    /**
     * The <code>saveImage</code> method stores the image or the images that are
     *  into the Dicom file to a pure ASCII text, ordering pixel values into rows
     *  and columns as in a trivial image.
     * 
     * @param asciiFN <code>String</code> Ascii image file name.
     * @param imageFN <code>String</code> PGM-P2 image file name.
     * @param pgm <code>boolean</code> If <code>true</code> the method will
     *  insert the PGM-P2 headers into the image file.
     */
    private void saveImage(String asciiFN, String imageFN, boolean pgm) {
        String asciiFileName = asciiFN;
        String imageFileName = imageFN;
        BufferedWriter asciiImageFile = null;
        
        int rows = image.rows;
        int cols = image.cols;
        int bpp = image.colorDepth;
        int maxPixValue = 1;
        
        String message = null;
        
        try {
            
            asciiImageFile = new BufferedWriter(new FileWriter(asciiFileName));
            
            for (int j = 0; j < cols; j++) {
                
                for (int i = 0; i < rows; i++) {
                    
                    // Color depth check
                    if (bpp == 16) { 
                        dicomFile.read(buff2);
                        tempBuff = (((0x000000ff & buff2[1]) <<  8) |
                                	 (0x000000ff & buff2[0])         );
                	} else if (bpp == 8) {
                	    dicomFile.read(buff1);
                		tempBuff = (0x000000ff & buff1[0]);
                    } // End of if-else block
                	
                	asciiImageFile.write(tempBuff + " ");
                	
                	// If the pgm image is needed, the maximum pixel value will
                	//  be held to write it in the pgm image headers
                	if (pgm) {
                	    if (tempBuff > maxPixValue)
                	        maxPixValue = tempBuff;
                	} // End of if block
                	
                } // End of for block
                
                asciiImageFile.write("\n");
                
            } // End of for block
            
            asciiImageFile.close();
            asciiSaved = true;
            
            // System.out.println("Image saved to file: \"" + imageFileName + "\"");
            
            // PGM-P2 image storing
            if (pgm) {
                BufferedReader asciiFile = null;
                BufferedWriter imageFile = null;
                
                imageFile = new BufferedWriter(new FileWriter(imageFileName));
            	asciiFile = new BufferedReader(new FileReader(asciiFileName));
            	
            	imageFile.write("P2\n" + cols + " " + rows + "\n" + maxPixValue + "\n");
            	
            	String line = null;
            	while ((line = asciiFile.readLine()) != null)
                	imageFile.write(line);
            	
            	asciiFile.close();
            	imageFile.close();
            	imageSaved = true;
            	
            } // End of if block
            
        } catch (IOException ioe) {
		    System.err.println(ioe);
		    JOptionPane.showMessageDialog(null, ioe, "Exception", JOptionPane.ERROR_MESSAGE);
		    System.exit(-1);
		} // End of try-catch block
         
    } // end of saveImage() method
    
    /**
     * The method returns the <code>isDicomFile</code> variable value.<br />
     * <code>True</code> is returned if the file to convert is recognized as a
     *  valid Dicom file.
     * 
     * @return isDicomFile <code>boolean</code> <code>isDicomFile</code> variable value.
     */
    public boolean getIsDicomFile() {
        return isDicomFile;
    } // End of getIsDicomFile method
    
    /**
     * The method returns the <code>headersSaved</code> variable value.<br />
     * <code>True</code> is returned if the headers file is stored.
     * 
     * @return headersSaved <code>boolean</code> <code>headersSaved</code> variable value.
     */
    public boolean getHeadersSaved() {
        return headersSaved;
    } // End of getHeadersSaved method
    
    /**
     * The method returns the <code>asciiSaved</code> variable value.<br />
     * <code>True</code> is returned if the ascii image file is stored.
     * 
     * @return asciiSaved <code>boolean</code> <code>asciiSaved</code> variable value.
     */
    public boolean getAsciiSaved() {
        return asciiSaved;
    } // End of getAsciiSaved method
    
    /**
     * The method returns the <code>imageSaved</code> variable value.<br />
     * <code>True</code> is returned if the pgm image file is stored.
     * 
     * @return imageSaved <code>boolean</code> <code>imageSaved</code> variable value.
     */
    public boolean getImageSaved() {
        return imageSaved;
    } // End of getImageSaved method
    
    /**
     * The method returns the <code>version</code> variable value.<br />
     * 
     * @return version <code>String</code> Application's version number.
     */
    public static String getAppVersion() {
        return version;
    } // End of getAppVersion method
    
    /**
     * Four parameters static method of the <code>decode</code> method.
     * 
     * @param dfn <code>File</code> Dicom file name to decode.
	 * @param hfn <code>File</code> Headers file name.
	 * @param afn <code>File</code> Ascii image file name.
	 * @param ifn <code>File</code> PGM-P2 image file name.
	 */
    public static void decode(File dfn, File hfn, File afn, File ifn) {
        DicomReader instance = new DicomReader(dfn, hfn, afn, ifn);
        instance.decode();
    } // End of decode(dfn, hfn, afn, ifn) static method
    
    /**
     * Three parameters static method of the <code>decode</code> method.
     * 
     * @param dfn <code>File</code> Dicom file name to decode.
	 * @param hfn <code>File</code> Headers file name.
	 * @param afn <code>File</code> Ascii image file name.
	 */
    public static void decode(File dfn, File hfn, File afn) {
        DicomReader instance = new DicomReader(dfn, hfn, afn);
        instance.decode();
    } // End of decode(dfn, hfn, afn, ifn) static method
    
    /**
     * The <code>main</code> methods of the class simply calls the <code>decode</code>
     *  static method.
     * 
     * @param dicomF <code>File</code> Dicom file name to decode.
	 * @param headersF <code>File</code> Headers file name.
	 * @param asciiF <code>File</code> Ascii image file name.
	 * @param imageF <code>File</code> PGM-P2 image file name.
	 */
    public static void main(String[] args) {
        // Variables initialization
        int argc = 0;
		int nArgs = args.length;
		String thisArg = null;
		
		File dicomF = null;
		File headersF = null;
		File asciiF = null;
		File imageF = null;
		
		// Arguments check and initialization
		while (argc < nArgs) {
			thisArg = args[argc++].trim();
			if (thisArg != null) {
				if (dicomF == null) {
					dicomF = new File(thisArg);
				} else if (headersF == null) {
					headersF = new File(thisArg);
				} else if (asciiF == null) {
					asciiF = new File(thisArg);
				} else if (imageF == null) {
					imageF = new File(thisArg);
				} // End of if-else block
			} // End of if block
		} // End of while block
        
		// First three parameters presence control
		if (dicomF != null && headersF != null && asciiF != null) {
            decode(dicomF, headersF, asciiF, imageF);
		} else {
		    String message = "Missed parameters:" + "\n" +
		    				 " File dfn, File hfn, File afn, File ifn";
		    System.err.println(message);
		    JOptionPane.showMessageDialog(null, message, "Missed parameters",
		            								JOptionPane.ERROR_MESSAGE);
		    System.exit(-1);
		} // End of if-else block
    } // End of main method
    
    /**
     * The <code>Image</code> private class is an utility class.<br />
     * Its object will contain needed values to the correct image storing process.
     */
    private class Image {
        int rows = 0;
        int cols = 0;
        int colorDepth = 0;
        int slices = 1;
        int totBytes = 0;
    } // End of Image private class
    
} // End of DicomReader class 
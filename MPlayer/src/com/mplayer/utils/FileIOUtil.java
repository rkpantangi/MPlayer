
package com.mplayer.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;

import com.sun.xml.internal.messaging.saaj.packaging.mime.util.LineInputStream;

/**
 * Convenience methods for File IO
 * 
 */
public class FileIOUtil {
    private final static Logger log = Logger.getLogger(FileIOUtil.class);
	public final static String DEFAULT_FILE_SEPARATOR = System.getProperty("file.separator");
	
    /**
     * Given the directory name, file name and Form file, this method writes the
     * contents of the form file to the given path with the given name.
     * 
     * @param dirName
     * @param fileName
     * @param fileContent
     */

    public static void writeToDisk(String absolutePath, byte[] fileContent) {
        OutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(absolutePath));
            bos.write(fileContent);

        } catch (IOException ioe) {
            log.error("Exception in writing the file to disk ");
            log.error(ioe.getMessage());
            throw new RuntimeException();
        } finally {
            if (bos != null) {
                try {
                    bos.flush();
                } catch (IOException e) {
                    log.error("Error flushing stream", e);
                }
                try {
                    bos.close();
                } catch (IOException e) {
                    log.error("Error closing stream", e);
                }
            }
        }
    }

    /**
     * Given the path and file name to be read, this method reads the content
     * into a byte[] from the specified location and returns the byte[].
     * 
     * @param dirName
     * @param fileName
     * @return
     */
    public static byte[] readFromDisk(String absolutePath) {
        byte bContent[] = null;
        FileInputStream fileInputStream = null;
        File file = null;
        try {
            file = new File(absolutePath);
            fileInputStream = new FileInputStream(file);
        } catch (NullPointerException npe) {
            log.error("absolutePath is null", npe);
            throw npe;
        } catch (FileNotFoundException fe) {
            log.error(absolutePath + " not found", fe);
            throw new RuntimeException();
        }
        try {
            bContent = new byte[(int) file.length()];
            fileInputStream.read(bContent);
        } catch (IOException ioe) {
            log.error(
                    "I/O Exception has occured while reading " + absolutePath,
                    ioe);
            throw new RuntimeException();

        } finally {
            try {
                fileInputStream.close();
            } catch (IOException ioe) {
                log.error("I/O Exception has occured while closing"
                        + absolutePath, ioe);
            }
        }
        return bContent;
    }

    /**
     * Given the path and file name to be read, this method reads the content
     * into a byte[] from the specified location and returns the byte[].
     * 
     * @param dirName
     * @param fileName
     * @return
     */
    public static String readFromDisk(URL fileUrl) {
        StringBuffer content = new StringBuffer(1000);
        URLConnection urlConn = null;
        LineInputStream data = null;
        try {
            urlConn = fileUrl.openConnection();
            urlConn.connect();
            log.info("file path - " + fileUrl.getFile());
        } catch (NullPointerException npe) {
            log.error("file url is null", npe);
            throw npe;
        } catch (IOException fe) {
            log.error(fileUrl.toString() + " not found", fe);
            throw new RuntimeException();
        }
        try {
        	String line = null;
        	data = new LineInputStream(new BufferedInputStream(
                           urlConn.getInputStream()));
            while ((line = data.readLine()) != null) {
              content.append(line + "\n");
            }
        } catch (IOException ioe) {
            log.error(
                    "I/O Exception has occured while reading " + urlConn.toString(),
                    ioe);
            throw new RuntimeException();

        } finally {
            try {
                data.close();
            } catch (IOException ioe) {
                log.error("I/O Exception has occured while closing"
                        + fileUrl.getFile(), ioe);
            }
        }
        return content.toString();
    }

    public static void deleteEmptyDirectory(String dirName) {
        File dir = null;
        String[] files = null;
        try {
            dir = new File(dirName);
            if (dir.isDirectory()) {
                files = dir.list();
                if (files != null && files.length == 0) {
                    if (log.isInfoEnabled())
                        log.info("Deleting empty directory: [" + dirName + "]");
                    dir.delete();
                }

            }
        } catch (NullPointerException npe) {
            log.error("absolutePath is null", npe);
        } catch (SecurityException se) {
            log.error("System security manager doesnot permit to detete directory :"
                + dirName, se);
        }
    }

    /**
     * deletes the file specified by the filename and directory path.
     * 
     * @param dirName
     * @param fileName
     * @return
     */
    public static void deleteFile(String absolutePath) {
        try {
            File file = new File(absolutePath);
            log.info("File to be deleted is " + absolutePath);
            if (file.isFile()) {
                file.delete();
            }
        } catch (NullPointerException npe) {
            log.error("absolutePath is null", npe);
        } catch (SecurityException se) {
            log.error("System security manager doesnot permit to file"
                    + absolutePath, se);
        }
    }

    /**
     * Creates a directory with given argment dirName at the path specified.
     * 
     * @param path
     * @param dirName
     */
    public static void createDirectory(String absolutepath) {

        File file = null;
        try {
            file = new File(absolutepath);
        } catch (NullPointerException npe) {
            log.error("absolutePath is null", npe);
            throw npe;
        }

        if (file.exists()) {
            if (log.isInfoEnabled())
                log.info("WebTemporay Directory :" + absolutepath
                        + " already got created");
        } else {
            try {
                file.mkdirs();
                if (log.isInfoEnabled())
                    log.info("WebTemporay Directory :" + absolutepath
                            + " got created");
            } catch (SecurityException se) {
                log.error("System security manager doesnot permit to create directory :"
                            + absolutepath, se);
                throw se;
            }
        }
    }

    /**
     * Deletes the directory specified by the argument. if the directory is not
     * empty then delete all the files in it and then delete the directory.
     * 
     * @param dirName
     * @return boolean
     */
    public static void deleteNonEmptyDirectory(String dirName) {

        try {
            File file1 = new File(dirName);
            if (file1.isDirectory()) {
                File[] filesInDirectory = file1.listFiles();
                for (int i = 0; i < filesInDirectory.length; i++) {
                    File file2 = filesInDirectory[i];
                    try {
                        if (file2.isFile()) {
                            file2.delete();
                        }
                    } catch (SecurityException se) {
                        log.error("System security manager doesnot permit to delete file :"
                            + file2.getName(), se);
                    }
                }
            } else {
                try {
                    if (file1.isFile()) {
                        file1.delete();
                    }
                } catch (SecurityException se) {
                    log.error("System security manager doesnot permit to  detlete file :"
                        + file1.getName(), se);
                }
            }
        } catch (NullPointerException npe) {
            log.error("absolutePath is null", npe);
        }
    }

    /**
     * 
     * getConcatinatedFileName, some databases restrict the filename length, so
     * this function accepts the filename, maximum allowed filename length and
     * returns the concatinated file name. Concatination is done after taking
     * out the file extension. for e.g if given file name is Save and Retrieve
     * Domain Objects Command Design.doc(file name length = 52), after
     * concatination file name would be Save and Retrieve Domain~.doc(file name
     * length=25)
     * 
     * @param fileName
     * @param legalFileLength
     * @return
     */
    public static String getConcatinatedFileName(String fileName,
            int legalFileLength) {

        String extn = getFileExtn(fileName);
        int concatFileNameLength = getFileNameLength(legalFileLength, extn
                .length());
        return getConcatinatedFileName(fileName, extn, concatFileNameLength);
    }

    /**
     * getFileNameLength method returns the legally allowed file name length
     * after taking out the file extension.
     * 
     * @param allowedFileNameLength
     * @param extnLength
     * @return int
     */
    public static int getFileNameLength(int allowedFileNameLength,
            int extnLength) {
        /*
         * total file name length after taking out file extension, "." and "~"
         * ("~" tild is to indicate that file name has been concatinated)
         */
        int concatFileNameLength = allowedFileNameLength - (extnLength + 3);
        return concatFileNameLength;

    }

    /**
     * Returns the legally allowed file name based on the file name length
     * restriction(s).
     * 
     * @param fileName
     * @param fileNameExtn
     * @param fileNameLengh
     * @return String
     */
    public static String getConcatinatedFileName(String fileName,
            String fileNameExtn, int fileNameLengh) {
        String str = fileName.substring(0, fileName.lastIndexOf("."));
        String concatFileName = str.substring(0, fileNameLengh);
        return concatFileName + "~." + fileNameExtn;
    }

    /**
     * Given a file name this method returns the file extension. e.g if file
     * name is Save and Retrieve Domain Objects Command Design.doc, this method
     * returns 'doc'.
     * 
     * @param fileName
     * @return String
     */
    public static String getFileExtn(String fileName) {
        int lastIndexofFile = fileName.lastIndexOf(".");
        String fileExtn = "";
        if (lastIndexofFile != -1) {
            fileExtn = fileName.substring(lastIndexofFile + 1, fileName
                    .length());
        }
        return fileExtn;
    }
    
    /**
     * Given a file name including extension, this method returns just the file name.
     * e.g if file name is Save.xml this method returns 'Save'.
     * 
     * @param fileName
     * @return String
     */
    public static String getFilenameWithoutExtn(String fileName) {
        int lastIndexofFile = fileName.lastIndexOf(".");
        String fileNameWithoutExtn = "";
        if (lastIndexofFile != -1) {
        	fileNameWithoutExtn = fileName.substring(0, lastIndexofFile);
        }
        return fileNameWithoutExtn;
    }
    
	/**
		 * Given the directory name, file name and Form file, this method writes the
		 * contents of the form file to the given path with the given name.
		 * 
		 * @param dirName
		 * @param fileName
		 * @param fileContent
		 */
		public static void writeToDisk(String dirName, String fileName,
				byte[] fileContent) {
			String path = dirName + FileIOUtil.DEFAULT_FILE_SEPARATOR + fileName;
			OutputStream bos = null;
			try {
				bos = new BufferedOutputStream(new FileOutputStream(path));
				bos.write(fileContent);

			} catch (FileNotFoundException fnfe) {
				log.error("WriteToDisk: File not found: [" + path + "]", fnfe);
			} catch (IOException ioe) {
				log.error("WriteToDisk: IO Error: [" + path + "]", ioe);
			} finally {
				if (bos != null) {
					try {
						bos.flush();
					} catch (IOException e) {
						log.error("Error flushing stream", e);
					}
					try {
						bos.close();
					} catch (IOException e) {
						log.error("Error closing stream", e);
					}
				}
			}
		}
}
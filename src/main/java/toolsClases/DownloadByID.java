
package toolsClases;

import innerLogicTools.GoogleDriveUtils;
//***************************************
//**************************************

//import com.google.api.services.drive.Drive;
//import com.google.api.services.drive.model.File;
//import com.google.api.services.drive.model.FileList;
//
//import com.google.api.client.http.AbstractInputStreamContent;
//import com.google.api.client.http.ByteArrayContent;
//import com.google.api.client.http.FileContent;
//import com.google.api.client.http.HttpResponse;
//import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
//import com.google.api.services.drive.Drive.Files;
//import com.google.api.services.drive.Drive.Files.Get;
import com.google.api.services.drive.model.File;
//***************************************
//**************************************

//**********************
import java.nio.file.Paths;
import java.nio.file.Path;
//import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
//import java.util.Iterator;
//import java.util.List;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import synchronisator.Exchanger;

//***********************
/**
 *
 * @author Shkirmantsev
 */
public class DownloadByID
{
    ////////////// HELP FOR PRINTING /////////////////
    static MyLogPrinter printer = new MyLogPrinter(DownloadByID.class, Level.OFF, Level.INFO);
    /////////////////////////////////////////////////
    

    //Names of File format
    private static String[] commonBinaryDocs =
    {
        "text/html",
        "application/zip",
        "video/mp4",
        "application/vnd.google-apps.script+json",
        "application/epub+zip",
        "text/plain",
        "application/pdf",
        "application/rtf",
        "application/vnd.oasis.opendocument.text",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        //"application/vnd.google-apps.document", <<==== this is not standart format this is Google format
        
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "application/x-vnd.oasis.opendocument.spreadsheet",
        "text/csv",
        "text/tab-separated-values",
       
        "image/jpeg",
        "image/png",
        "image/svg+xml",
        "application/vnd.openxmlformats-officedocument.presentationml.presentation",
        "application/vnd.oasis.opendocument.presentation",
        

    };

    private static String[] googleDocsFormats = //<<==== this is not standart format this is Google format
    {

        "application/vnd.google-apps.document",   //Doc 0
        "application/vnd.google-apps.drawing",//Drawing 1
        "application/vnd.google-apps.form",     //Forms 2
        "application/vnd.google-apps.map",        //map 3
        "application/octet-stream",         //script.gs 4
        "application/vnd.google-apps.presentation", //presentation 5
        "application/vnd.google-apps.site",                 //site 6
        "application/vnd.google-apps.spreadsheet",         //sheet 7
        "application/pdf"                                  //Doc pdf 8
        

    };

    //-----------STATIC CONSTRUCTOR -------------------
    //*************************************************
    private static final HashSet<String> commonBinaryFormatsHSet;
    private static final HashMap<String, String> googleFormatsHshRelation;

    static

    {
        commonBinaryFormatsHSet = new HashSet<>(Arrays.asList(commonBinaryDocs));
        //////////////////////////
        googleFormatsHshRelation = new HashMap<>();
        ///////////////////////////
        googleFormatsHshRelation.put(googleDocsFormats[0], "application/vnd.oasis.opendocument.text");
        googleFormatsHshRelation.put(googleDocsFormats[1], "image/jpeg");
        googleFormatsHshRelation.put(googleDocsFormats[2], "application/zip");
        googleFormatsHshRelation.put(googleDocsFormats[3], "application/zip");
        googleFormatsHshRelation.put(googleDocsFormats[4], "application/vnd.google-apps.script+json");
        googleFormatsHshRelation.put(googleDocsFormats[5], "application/vnd.oasis.opendocument.presentation");
        googleFormatsHshRelation.put(googleDocsFormats[6], "application/zip");
        googleFormatsHshRelation.put(googleDocsFormats[7], "application/x-vnd.oasis.opendocument.spreadsheet");
        googleFormatsHshRelation.put(googleDocsFormats[8], "application/pdf");

    }
//*****************************************************    
//-----------------------------------------------------

    private static void downloadGoogleDoc(String fileID, String mimeType, String pathFileNameToWrite)
        throws IOException
    {
        Drive myDriveService = GoogleDriveUtils.getDriveService();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        myDriveService.files().export(fileID, mimeType).executeMediaAndDownloadTo(byteArrayOutputStream);

        try (BufferedOutputStream bufOut = new BufferedOutputStream(new FileOutputStream(pathFileNameToWrite)))
        {
            byteArrayOutputStream.writeTo(bufOut);
            byteArrayOutputStream.flush();           
                       
        }
        finally{byteArrayOutputStream.close();}
        
    }

    private static void downloadSimpleDoc(String fileID, String pathFileNameToWrite)
        throws IOException
    {
        Drive myDriveService = GoogleDriveUtils.getDriveService();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        myDriveService.files().get(fileID).executeMediaAndDownloadTo(byteArrayOutputStream);

        try (BufferedOutputStream bufOut = new BufferedOutputStream(new FileOutputStream(pathFileNameToWrite)))
        {
            byteArrayOutputStream.writeTo(bufOut);
            byteArrayOutputStream.flush();

            

        }
        finally{byteArrayOutputStream.close();}
    }

    public static void downloadOneFileFromDrive(String fileID, String pathFileNameToWrite)
        throws IOException
    {

        File findedfile = null;
        try
        {
            findedfile = FindByID.findGoogleFileByID(fileID);
        }
        catch (Exception ex)
        {
            ex.getMessage();
        }

        if (findedfile == null)
        {
            printer.print("No files found to download with this ID");
        }
        else
        {
            String filename = findedfile.getName();
            String mimetype = "";
            String relatedMimeType = null;
            
            try
            {
                mimetype = findedfile.getMimeType();
                printer.print("mimetype ==>>" + mimetype);
            }
            catch (Exception ex)
            {
                printer.print("file is without MimeType");
            }
            
            //check if the file has the standart google format
            boolean containsGooglFormat = googleFormatsHshRelation.containsKey(mimetype);

            if (commonBinaryFormatsHSet.contains(mimetype) || (!mimetype.contains("googl")))
            {
                printer.print(String.format("Trying to downloadoad binary format file %s...\n", filename));
                try
                {
                    downloadSimpleDoc(fileID, pathFileNameToWrite);
                }
                catch (IOException ex)
                {
                    printer.print(Level.WARNING,String.format("Can't download binary format file %s\n", filename));
                    printer.print(Level.WARNING,String.format(" MIMETYPE IS : %s\n",findedfile.getMimeType()));
                }
            }
            else if (containsGooglFormat || (mimetype.contains("googl")))
            {
                printer.print(String.format("Trying to downloadoad Google format file %s...\n", filename));
                printer.print(String.format("mimetype is: %s...\n",mimetype ));
                try
                {
                    try
                    {
                        relatedMimeType=googleFormatsHshRelation.get(mimetype);
                        downloadGoogleDoc(fileID, relatedMimeType, pathFileNameToWrite);
                    }
                    catch (Exception ex2)
                    {
                        //cycle
                        printer.print(" Trying any some other format ...  ");
                        int i=1;
                        String tmpexc="";
                        
                        boolean good=false; //is download successful?
                        
                        label: // <=point to break if successful
                        for (String format : commonBinaryDocs)
                        {
                            
                            try
                            {   printer.print(String.format("Trying %s %s",i," =>"));
                                downloadGoogleDoc(fileID, format, pathFileNameToWrite);
                                good=true;
                                break label;
                            }
                            catch (Error er)
                            {
                                i++;
                                tmpexc=er.getMessage();
                                
                            }
                            catch (IOException ex3)
                            {
                                i++;
                                tmpexc=ex3.getMessage();
                                
                            }
                            
                            
                        }
                        if ((i==commonBinaryDocs.length) && !good) throw new IOException(tmpexc);
                    }

                }
                catch (IOException ex)
                {
                    printer.print(Level.WARNING,String.format("Can't download finded google format file %s\n", filename));
                    printer.print(Level.WARNING,String.format(" MIMETYPE IS : ",findedfile.getMimeType()));

                    ex.printStackTrace();
                }
            }
            else
            {
                printer.print(Level.WARNING,String.format("Can't download this format -- %s -- \n", mimetype));
            }

        }

    }

    public static void main(String[] args) throws IOException
    {
        Path pathnameTodownload;

        String idGoogleDocFile = "1rjWBs7eNFsyuZ46EgBxkN6oTBPhWXdUU";
        pathnameTodownload = Paths.get(".", "DownloadedGoogleDocFile.pdf");
        downloadOneFileFromDrive(idGoogleDocFile, pathnameTodownload.toAbsolutePath().toString());

//        String idbinaryFile="1Yu-2XqLJcQbkz9Z5ZTFJp7FC2oXH8Tbz";
//        pathnameTodownload=Paths.get(".","DownloadedBinaryFile.mp4");
//        downloadOneFileFromDrive(idbinaryFile, pathnameTodownload.toAbsolutePath().toString());
        System.out.println("Download Done!");
    }
}

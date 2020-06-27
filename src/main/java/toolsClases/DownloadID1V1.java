
package toolsClases;

import innerLogicTools.GoogleDriveUtils;

//import com.google.api.services.drive.Drive;
//import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

//import com.google.api.client.http.AbstractInputStreamContent;
//import com.google.api.client.http.ByteArrayContent;
//import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpResponse;
//import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.Drive.Files.Get;
import com.google.api.services.drive.model.File;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.ArrayList;
//import java.util.Iterator;
import java.util.List;

public class DownloadID1V1
{

    private static void downloadGoogleDoc(String fileID, String mimeType, String name)
        throws IOException
    {

        Drive myDriveService = GoogleDriveUtils.getDriveService();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        myDriveService.files().export(fileID, mimeType).executeMediaAndDownloadTo(byteArrayOutputStream);

        

        try (BufferedOutputStream bufOut = new BufferedOutputStream(new FileOutputStream(name)))
        {
            byteArrayOutputStream.writeTo(bufOut);
            byteArrayOutputStream.flush();

            byteArrayOutputStream.close();
            

        }
    }
    
     private static void downloadSimpleDoc(String fileID, String name)
        throws IOException
    {

        Drive myDriveService = GoogleDriveUtils.getDriveService();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        myDriveService.files().get(fileID).executeMediaAndDownloadTo(byteArrayOutputStream);
        

        

        try (BufferedOutputStream bufOut = new BufferedOutputStream(new FileOutputStream(name)))
        {
            byteArrayOutputStream.writeTo(bufOut);
            byteArrayOutputStream.flush();

            byteArrayOutputStream.close();
            

        }
    }

    public static final void DownloadGoogFileByID(String fileID)
        throws IOException
    {
        //Names of Google format
        String[] googleDocs =
        {
            "text/html", "application/zip", "text/plain"
        };

        // Build a new authorized API client service.
        Drive myDriveService = GoogleDriveUtils.getDriveService();
        // Print the names and IDs for up to 10 files.
        FileList result = myDriveService.files().list().execute();

        List<File> files = result.getFiles();

        if (files == null || files.isEmpty())
        {
            System.out.println("No files found.");
        }
        else
        {

            for (File file : files)
            {
                String fname = file.getName();
                // extension from file
                String ex = fname.substring(fname.lastIndexOf(".") + 1);
                try
                {
                    Files filesFromDrive = myDriveService.files();
                    HttpResponse httpResponse = null;

                    if (ex.equalsIgnoreCase("xlsx"))
                    {
                        httpResponse = filesFromDrive
                            .export(file.getId(),
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                            .executeMedia();

                    }
                    else if (ex.equalsIgnoreCase("docx"))
                    {
                        httpResponse = filesFromDrive
                            .export(file.getId(),
                                "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                            .executeMedia();
                    }
                    else if (ex.equalsIgnoreCase("pptx"))
                    {
                        httpResponse = filesFromDrive
                            .export(file.getId(),
                                "application/vnd.openxmlformats-officedocument.presentationml.presentation")
                            .executeMedia();

                    }
                    else if (ex.equalsIgnoreCase("pdf")
                        || ex.equalsIgnoreCase("jpg")
                        || ex.equalsIgnoreCase("txt")
                        || ex.equalsIgnoreCase("png"))
                    {

                        Get get = filesFromDrive.get(file.getId());
                        httpResponse = get.executeMedia();

                    }
                    if (null != httpResponse)
                    {
                        InputStream instream = httpResponse.getContent();
                        FileOutputStream output = new FileOutputStream(
                            file.getName());
                        try
                        {
                            int l;
                            byte[] tmp = new byte[2048];
                            while ((l = instream.read(tmp)) != -1)
                            {
                                output.write(tmp, 0, l);
                            }
                        }
                        finally
                        {
                            output.close();
                            instream.close();
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        //*************************************
        //************************************
        System.out.println("Download is Done!");

    }
}


package toolsClases;

import innerLogicTools.GoogleDriveUtils;

//import com.google.api.services.drive.Drive;
//import com.google.api.services.drive.model.File;
//
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
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Shkirmantsev
 */
public class DeleteGoogleFile
{
    ////////////// HELP FOR PRINTING /////////////////
    static MyLogPrinter printer = new MyLogPrinter(DeleteGoogleFile.class, Level.OFF, Level.WARNING);
    /////////////////////////////////////////////////
    
    

    public static boolean deleteFile(String fileID) throws IOException
    {
              
        File findedFile = FindByID.findGoogleFileByID(fileID);
        if ((findedFile == null) ||(fileID==null))              // Null is ROOT!!!
        {
            printer.print("delete file: file with ID=" + fileID + " is not found");
            return false;
        }

        try
        {
            String fileName = findedFile.getName();
            Drive myDriveService = GoogleDriveUtils.getDriveService();
            printer.print("Deleting file in Drive " + fileName + " with fileID = " + fileID);
            myDriveService.files().delete(fileID).execute();
            printer.print("file " + fileName + " ID = " + fileID + " was deleted from drive");
            return true;

        }
        catch (IOException ex)
        {
            printer.print(Level.SEVERE,"file with ID" + fileID + " can't delete");
            return false;
        }
        finally
        {
            printer.print(Level.FINER,"func deleteFile is finished");
        }

    }

    public static void main(String[] args) throws IOException
    {
        String fileID = "1wZUOiCdV9TcW3dgWAyAZwKiPNFm_MMeu";
        deleteFile(fileID);

        System.out.println("Done!");
    }
}

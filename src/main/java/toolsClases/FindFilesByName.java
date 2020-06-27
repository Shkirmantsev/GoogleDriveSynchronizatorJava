

package toolsClases;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import innerLogicTools.GoogleDriveUtils;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;

import java.util.logging.Logger;



/**
 * 
 * @author Shkirmantsev
 */
public class FindFilesByName
{
    ////////////// HELP FOR PRINTING /////////////////
    static MyLogPrinter printer = new MyLogPrinter(FindFilesByName.class, Level.FINEST, Level.FINEST);
    /////////////////////////////////////////////////
    
    
    // com.google.api.services.drive.model.File
    public static final List<File> getGoogleFilesByName(String fileNameToFind)
        throws IOException
    {
        StringBuffer strBuf=new StringBuffer();
        

        Drive myDriveService = GoogleDriveUtils.getDriveService();

        String pageToken = null;
        //found files
        List<File> list = new ArrayList<File>();
        
        //GoogleDrive query
        String query = " name contains '" + fileNameToFind + "' " //
            + " and mimeType != 'application/vnd.google-apps.folder' and trashed != True "; // it is NOT findedFile and it is not in trash box

        do
        {
            FileList result = myDriveService.files().list().setQ(query).setSpaces("drive")
                // Fields will be assigned values: id, name, createdTime, mimeType...(it means "we use these fields)
                .setFields("nextPageToken, files(id, name, createdTime,modifiedTime, mimeType,parents)")//
                .setPageToken(pageToken).execute();

            for (File file : result.getFiles())
            {

                list.add(file);
            }
            pageToken = result.getNextPageToken();
        } while (pageToken != null);
        
        if (list.isEmpty()) return null;
        printer.print(Level.FINE,"Searching...");
        
        for (File findedFile : list)
        {
            //System.out.println(findedFile); // <----------JSON Format

            printer.print(Level.FINE,strBuf.append("Mime Type: ")
                .append(findedFile.getMimeType())
                .append(" --- Name: ")
                .append(findedFile.getName())
                .append(" ID: ").append(findedFile.getId())
                .toString());
        }
        strBuf.setLength(0);
            
        printer.print(Level.FINE,"found files");
        printer.print(Level.FINE,list.toString());
        //
        return list;
    }

    public static void main(String[] args) throws IOException
    {
        
        List<File> rootGoogleFolders = getGoogleFilesByName("Lebenslauf_2019-09-18.pdf");
        rootGoogleFolders.forEach((findedFile) ->
        {
            //System.out.println(findedFile); // <----------JSON Format
            
            printer.print("Mime Type: " + findedFile.getMimeType() + " --- Name: " + findedFile.getName()+" ID: "+findedFile.getId());
        });

        printer.print(Level.FINE,"Done!");
    }

}

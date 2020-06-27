
package toolsClases;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import innerLogicTools.GoogleDriveUtils;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import java.util.Optional;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Shkirmantsev
 */
public class CreateFolder
{
    ////////////// HELP FOR PRINTING /////////////////
    static MyLogPrinter printer = new MyLogPrinter(CreateFolder.class, Level.FINEST, Level.WARNING);
    /////////////////////////////////////////////////
    
    
    public static final File createGoogleFolder(String folderIdParent, String folderName)
        throws IOException
    {   
        printer.print(Level.FINE, "--IN createGoogleFolder()");
        Optional<File> folderAlreadyExist = Optional.ofNullable(FindOldestFolderByName.oldestFolderByName(folderIdParent, folderName));
        //
        //*************
        if (folderAlreadyExist.isPresent())
        {
            printer.print("folder is already Exist, return the oldest Folder");
            return folderAlreadyExist.get();
        }
        //*************
        //
        File fileMetadata = new File();
        printer.print("fileMetadata.setName(folderName)"+folderName);
        fileMetadata.setName(folderName);
        fileMetadata.setMimeType("application/vnd.google-apps.folder");

        if (folderIdParent != null)
        {
            List<String> parents = Arrays.asList(folderIdParent);

            fileMetadata.setParents(parents);
        }
        Drive myDriveService = GoogleDriveUtils.getDriveService();

        // Create a Folder.
        // Returns File object with id & name fields will be assigned values
        File file = myDriveService.files().create(fileMetadata).setFields("id, name, parents, modifiedTime, createdTime").execute();
        printer.print("folder"+folderName+"created");
        return file;
    }

    //we need main only at once, when we are testing this "module"
    public static void main(String[] args) throws IOException
    {

        // Create a Root Folder
        File folder = createGoogleFolder(null, "Exsistfolder");

        System.out.println("Created folder with id= " + folder.getId());
        System.out.println("                   name= " + folder.getName());

        System.out.println("Done!");
    }

}

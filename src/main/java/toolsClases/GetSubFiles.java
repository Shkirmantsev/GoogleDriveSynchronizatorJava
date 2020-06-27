package toolsClases;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import innerLogicTools.GoogleDriveUtils;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

/**
 *
 * @author Shkirmantsev
 */
public class GetSubFiles
{

    // com.google.api.services.drive.model.File
    public static final List<File> getGoogleSubFiles(String googleFolderIdParent)
        throws IOException
    {
        
        Drive myDriveService = GoogleDriveUtils.getDriveService();

        String pageToken = null;
        List<File> list = new ArrayList<File>();

        /*
        * To find files on Google Drive, you should use the following query condition:
        * mimeType != 'application/vnd.google-apps.folder'
         */
        String query = null;
        if (googleFolderIdParent == null)
        {
            query = " mimeType != 'application/vnd.google-apps.folder' "
                + " and 'root' in parents and trashed != True"; // <------ search only "files" in root
        } else
        {
            query = " mimeType != 'application/vnd.google-apps.folder' " // <------ search only "folder"
                + " and '" + googleFolderIdParent + "' in parents and trashed != True";
        }

        do
        {

            FileList result = myDriveService.files().list().setQ(query).setSpaces("drive") // 
                    //                 Fields will be assigned values: id, name, createdTime
                    //                Note: You have an object.
                    //                  com.google.api.services.drive.model.File,
                    //                  but not all of its fields have an attached value.
                    //                  Only the fields in which you are interested have attached
                    //                  value, if vice versa it is null.
                
                .setFields("nextPageToken, files(" + ConstantsForGoogleWork.QUERYFIELDS + ")")//
                .setPageToken(pageToken).execute();
            for (File file : result.getFiles())
            {
                list.add(file);
            }
            pageToken = result.getNextPageToken();
        } while (pageToken != null);
        
        
        
        if (list.isEmpty())
        {
            return null;
        }
        //
        return list;

    }

    // com.google.api.services.drive.model.File
    public static final List<File> getGoogleRootFiles() throws IOException
    {
        return getGoogleSubFiles(null);
    }

    public static void main(String[] args) throws IOException
    {

        List<File> googleRootFolders = getGoogleRootFiles();
        for (File findeFile : googleRootFolders)
        {

            System.out.println("Folder ID: " + findeFile.getId() + " --- Name: " + findeFile.getName());
        }
    }
}

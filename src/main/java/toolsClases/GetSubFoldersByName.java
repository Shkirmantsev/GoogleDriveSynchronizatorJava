
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
public class GetSubFoldersByName
{

    // com.google.api.services.drive.model.File
    public static final List<File> getGoogleSubFolderByName(String googleFolderIdParent, String subFolderName)
        throws IOException
    {
        if (googleFolderIdParent != null)
        {
            File perentFolder = FindByID.findGoogleFileByID(googleFolderIdParent);
            if (perentFolder == null)
            {
                System.out.println(" Searched Folder PerentID " + googleFolderIdParent + " not Exist");
                return null;
            }
        }

        Drive myDriveService = GoogleDriveUtils.getDriveService();

        String pageToken = null;
        List<File> list = new ArrayList<File>();

        String query = null;
        if (googleFolderIdParent == null)
        {
            query = " name = '" + subFolderName + "' " //
                + " and mimeType = 'application/vnd.google-apps.folder' " //
                + " and 'root' in parents and trashed != True";
        }
        else
        {
            query = " name = '" + subFolderName + "' " //
                + " and mimeType = 'application/vnd.google-apps.folder' " //
                + " and '" + googleFolderIdParent + "' in parents and trashed != True";
        }

        do
        {
            FileList result = myDriveService.files().list().setQ(query).setSpaces("drive") //
                .setFields("nextPageToken, files("+ConstantsForGoogleWork.QUERYFIELDS+")")//
                .setPageToken(pageToken).execute();

            for (File file : result.getFiles())
            {
                list.add(file);
            }
            pageToken = result.getNextPageToken();
        }
        while (pageToken != null);
        //
        if (list.isEmpty())
        {
            return null;
        }
        try
        {
            String testname = list.get(0).getName();
            if (testname == null || testname.equals(""))
            {
                System.out.println("No subfolders with name " + subFolderName + "folders" + "in Folder ID=" + googleFolderIdParent + " found");
                return null;
            }
        }
        catch (Exception ex)
        {
            System.out.println("No subfolders with name " + subFolderName + "folders" + "in Folder ID=" + googleFolderIdParent + " found");
            return null;
        }

        //
        return list;
    }

    // com.google.api.services.drive.model.File
    public static final List<File> getGoogleRootFoldersByName(String subFolderName)
        throws IOException
    {
        return getGoogleSubFolderByName(null, subFolderName);
    }

    public static void main(String[] args) throws IOException
    {

        List<File> rootGoogleFolders = getGoogleRootFoldersByName("Exsistfolder");
        if (rootGoogleFolders != null)
        {
            for (File folder : rootGoogleFolders)
            {

                System.out.println("Folder in root -- ID: " + folder.getId() + " --- Name: " + folder.getName()+" created "+folder.getCreatedTime());
            }
        }
        
        List<File> GoogleFolders = getGoogleSubFolderByName("10IJYW2Vy0yiPbmjsvyxTH0X7RXAgjRYW","TEST");
        if (rootGoogleFolders != null)
        {
            for (File folder : GoogleFolders)
            {

                System.out.println("Folder in ID -- ID: " + folder.getId() + " --- Name: " + folder.getName()+" created "+folder.getCreatedTime());
            }
        }
    }
}

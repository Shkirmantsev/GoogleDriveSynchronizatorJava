
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
public class GetSubFilesByName
{

    // com.google.api.services.drive.model.File
    public static final List<File> getGoogleSubFilesByName(String googleFolderIdParent, String subFileName)
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
            query = " name = '" + subFileName + "' " //
                + " and mimeType != 'application/vnd.google-apps.folder' " //
                + " and 'root' in parents and trashed != True";
        }
        else
        {
            query = " name = '" + subFileName + "' " //
                + " and mimeType != 'application/vnd.google-apps.folder' " //
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
                System.out.println("No subfolders with name " + subFileName + "folders" + "in Folder ID=" + googleFolderIdParent + " found");
                return null;
            }
        }
        catch (Exception ex)
        {
            System.out.println("No subfolders with name " + subFileName + "folders" + "in Folder ID=" + googleFolderIdParent + " found");
            return null;
        }

        //
        return list;
    }

    // com.google.api.services.drive.model.File
    public static final List<File> getGoogleRootFilesByName(String subFolderName)
        throws IOException
    {
        return getGoogleSubFilesByName(null, subFolderName);
    }

    public static void main(String[] args) throws IOException
    {

        List<File> rootGoogleFiles = getGoogleRootFilesByName("Exsistfolder");
        if (rootGoogleFiles != null)
        {
            for (File googfile : rootGoogleFiles)
            {

                System.out.println("Folder in root -- ID: " + googfile.getId() + " --- Name: " + googfile.getName()+" created "+googfile.getCreatedTime());
            }
        }
        
        List<File> GoogleFolders = getGoogleSubFilesByName("10IJYW2Vy0yiPbmjsvyxTH0X7RXAgjRYW","TEST");
        if (rootGoogleFiles != null)
        {
            for (File folder : GoogleFolders)
            {

                System.out.println("Folder in ID -- ID: " + folder.getId() + " --- Name: " + folder.getName()+" created "+folder.getCreatedTime());
            }
        }
    }
}

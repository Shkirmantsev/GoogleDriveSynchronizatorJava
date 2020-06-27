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
public class GetSubFolders
{

    // com.google.api.services.drive.model.File
    public static final List<File> getGoogleSubFolders(String googleFolderIdParent)
        throws IOException
    {

        Drive myDriveService = GoogleDriveUtils.getDriveService();

        String pageToken = null;
        List<File> list = new ArrayList<File>();

        String query = null;
        if (googleFolderIdParent == null)
        {
            query = " mimeType = 'application/vnd.google-apps.folder' " 
                + " and 'root' in parents and trashed != True"; // <------ search only "folder" in root
        }
        else
        {
            query = " mimeType = 'application/vnd.google-apps.folder' " // <------ search only "folder"
                + " and '" + googleFolderIdParent + "' in parents and trashed != True";
        }

        do
        {
            
            FileList result = myDriveService.files().list().setQ(query).setSpaces("drive") // 
                // Fields will be assigned values: id, name, createdTime
//                Примечание: У вас есть объект
//                    com.google.api.services.drive.model.File,
//                но не все его поля (field) имеют прикрепленное значение.
//                Только поля, в которых вы заинтересованы имеют прикрепленные
//                значение, если наоборот то имеет значение  null.
                .setFields("nextPageToken, files("+ConstantsForGoogleWork.QUERYFIELDS+")")//
                .setPageToken(pageToken).execute();
            for (File file : result.getFiles())
            {
                list.add(file);
            }
            pageToken = result.getNextPageToken();
        }
        while (pageToken != null);
        if (list.isEmpty()) return null;
        //
        return list;

    }
    
    // com.google.api.services.drive.model.File
    public static final List<File> getGoogleRootFolders() throws IOException {
//        return getGoogleSubFolders("0ACEtlTsf_1vQUk9PVA");
        return getGoogleSubFolders(null);
    }
 
    public static void main(String[] args) throws IOException {
 
        List<File> googleRootFolders = getGoogleRootFolders();
        for (File folder : googleRootFolders) {
 
            System.out.println("Folder ID: " + folder.getId() + " --- Name: " + folder.getName()+" ParentTrueID: "+folder.getParents().toString());
        }
    }
}

/*
 * Чтобы найти файлы на  Google Drive, вам следует использовать следующее условие запроса:
 * mimeType != 'application/vnd.google-apps.folder'
 */
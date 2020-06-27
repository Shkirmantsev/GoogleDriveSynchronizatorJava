
package toolsClases;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import innerLogicTools.GoogleDriveUtils;

import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 1. byte[]
 * 2. Upload
 * 3. File InputStream
 *
 * @author Shkirmantsev
 */
public class UploadFile
{

    // PRIVATE!
    private static File _createGoogleFile(String googleFolderIdParent, String contentType, //
        String customFileName, AbstractInputStreamContent uploadStreamContent)
        throws IOException
    {
        // google File
        File fileMetadata = new File();
        fileMetadata.setName(customFileName);

        List<String> parents = Arrays.asList(googleFolderIdParent.split(","));
        fileMetadata.setParents(parents);
        //
        Drive myDriveService = GoogleDriveUtils.getDriveService();

        File file = myDriveService.files().create(fileMetadata, uploadStreamContent)
            .setFields(ConstantsForGoogleWork.QUERYFIELDS).execute();

        return file;
    }
//-------------------  1 from byte[]  --------------------
    // Create Google File from byte[]

    public static File createGoogleFile(String googleFolderIdParent, String contentType, //
        String customFileName, byte[] uploadData) throws IOException
    {
        //
        AbstractInputStreamContent uploadStreamContent = new ByteArrayContent(contentType, uploadData);
        //
        
        return _createGoogleFile(googleFolderIdParent, contentType, customFileName, uploadStreamContent);
    }
    //---------------------------------------------------------
    //

    //-------------------  2 from Upload  --------------------
    // Create Google File from java.io.File
    public static File createGoogleFile(String googleFolderIdParent, String contentType, //
        String customFileName, java.io.File uploadFile) throws IOException
    {

        //
        AbstractInputStreamContent uploadStreamContent = new FileContent(contentType, uploadFile);
        //
        return _createGoogleFile(googleFolderIdParent, contentType, customFileName, uploadStreamContent);
    }

    // Create Google File from InputStream
    public static File createGoogleFile(String googleFolderIdParent, String contentType, //
        String customFileName, InputStream inputStream) throws IOException
    {

        //
        AbstractInputStreamContent uploadStreamContent = new InputStreamContent(contentType, inputStream);
        //
        return _createGoogleFile(googleFolderIdParent, contentType, customFileName, uploadStreamContent);
    }

    

    //we need main only at once, when we are testing this "module"
    public static void main(String[] args) throws IOException
    {
        System.out.println(System.getProperty("--user.dir"));
        Path pathnameToUpload = Paths.get("./testfiles", "test1.txt");
        java.io.File uploadFile = new java.io.File(pathnameToUpload.toAbsolutePath().toString());
        String mimetype =java.nio.file.Files.probeContentType(pathnameToUpload);
        // Create Google File:
        File googleFile = createGoogleFile(null, mimetype, "test1.txt", uploadFile);

        System.out.println("Created Google file!");
        System.out.println("WebContentLink: " + googleFile.getWebContentLink());
        System.out.println("WebViewLink: " + googleFile.getWebViewLink());

        System.out.println("Done!");
    }
}

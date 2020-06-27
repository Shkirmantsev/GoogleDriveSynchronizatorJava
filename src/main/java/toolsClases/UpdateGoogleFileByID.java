package toolsClases;

import innerLogicTools.GoogleDriveUtils;

//import com.google.api.services.drive.Drive;
//mport com.google.api.services.drive.model.File;
//import com.google.api.services.drive.model.FileList;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.FileContent;
//import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.util.DateTime;
import com.google.api.services.drive.Drive;
//import com.google.api.services.drive.Drive.Files;
//import com.google.api.services.drive.Drive.Files.Get;
import com.google.api.services.drive.model.File;
import com.google.common.base.Optional;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Shkirmantsev
 */
public class UpdateGoogleFileByID
{
    ////////////// HELP FOR PRINTING /////////////////

    static MyLogPrinter printer = new MyLogPrinter(UpdateGoogleFileByID.class, Level.FINEST, Level.WARNING);
    /////////////////////////////////////////////////

    private static StringBuffer strBuf = new StringBuffer();

    public static File updateGoogleFileWithDeleting(String fileID, String customFileName, String mimeType, //
                                                     String description,java.io.File uploadFile) throws IOException{
        
        printer.print(Level.FINEST, "updateGoogleFileWithDeleting: "+customFileName);
        
        File fileToRewrite=toolsClases.ToolsDriveFacade.findGoogleFileByID(fileID);
        String parentID=fileToRewrite.getParents().get(0);
        
        toolsClases.ToolsDriveFacade.deleteFileInDrive(fileID);
        
        File newFile=toolsClases.ToolsDriveFacade.UploadFileOnGoogleDrive(parentID, mimeType, customFileName, uploadFile);
        return newFile;
    }
    

    //-------------------  2 from Upload  --------------------
    // Create Google File from java.io.File
    public static File updateGoogleFile(String fileID, String customFileName, String mimeType, //
        String description,java.io.File uploadFile) throws IOException
    {
        printer.print( " START OF UPDATING ... "+customFileName);
        File updatedFile = null;
        try
        {
            //create google fileMetadata
            printer.print(Level.FINEST, "( 1 )");

            File fileMetadata = new File(); 
           
            
            printer.print(Level.FINEST,"( 2 )");
            fileMetadata.setName(customFileName);
            
            printer.print(Level.FINEST, "( 3 )");
            fileMetadata.setDescription(description);
            
            printer.print(Level.FINEST, "( 4 )");
            fileMetadata.setMimeType(mimeType); //TO DO: Try OS/Drive Mimetype
//         FORBIDDED BY GOOGLE in NEW API   
//            BasicFileAttributes fatr = Files.readAttributes(uploadFile.toPath(), 
//                BasicFileAttributes.class);
//            String rfc3339Str=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new Date(fatr.creationTime().toMillis())).toString();
//               
//            fileMetadata.setModifiedByMeTime(DateTime.parseRfc3339(rfc3339Str));
            printer.print(Level.FINEST, "( 5 )");
            //
            java.io.File fileContent = uploadFile;
             printer.print(Level.FINEST, "( 6 )");
            Drive myDriveService = GoogleDriveUtils.getDriveService();
             printer.print(Level.FINEST, "( 7 )");
            FileContent mediaContent = new FileContent(mimeType, fileContent);
           
            // Send the request to the API.
            updatedFile = myDriveService.files().update(fileID, fileMetadata, mediaContent)
                .setFields(ConstantsForGoogleWork.QUERYFIELDS)
                .execute();

                
            

        } catch (IOException ioex)
        {
            ioex.printStackTrace();

        }

        printer.print(Level.FINEST, "( 8 )");
        printer.print( " END OF file UPDATING "+customFileName);
        return updatedFile;
        
    }
    
 

    

    public static File UpdateOrUpload(String fileID, String googleFolderIdParent, String newOrOldFileName, //
        String mimeType,String description,java.io.File uploadFile) throws IOException
    {
        File result = null;
        try
        {
           result=updateGoogleFile(fileID, newOrOldFileName, mimeType,description, //
        uploadFile);

        } catch (IOException iex)
        {
            try{
                toolsClases.ToolsDriveFacade.UploadFileOnGoogleDrive(googleFolderIdParent, mimeType, newOrOldFileName, uploadFile);
            }catch(Exception ex2){return null;}
            
        }
        return result;

    }

    //we need main only at once, when we are testing this "module"
    public static void main(String[] args) throws IOException
    {
        Path uploadAppFilePath = Paths.get("./TestDrive/AlfatrainingTestDrive/test/","Lebenslauf_2019-09-18.pdf");
        
        Optional<List<File>> FilesToUpdate = Optional.fromNullable(toolsClases.ToolsDriveFacade.findGoogleFilesByName("TestTime.txt"));
       
        if (FilesToUpdate.isPresent())
        {
            List<File> FilesToUpdateList = FilesToUpdate.get();           
            
            File file1 = FilesToUpdateList.get(0);           
            
            String parID = file1.getParents().get(0);            
            String file1ID=file1.getId();
            
            
            System.out.println(" -- file1 parID : " + parID);
            String parName = toolsClases.FindByID.findGoogleFileByID(parID).getName();
            
            System.out.println("Folder name : " + parName);
            System.out.println("Name in Drive: " + file1.getName());
             //System.out.println("sout Folder ID : " + toolsClases.FindByID.findGoogleFileByID(file1.getParents().get(0)).getId());
            String folderName1 = toolsClases.FindByID.findGoogleFileByID(file1.getParents().get(0)).getName();

            Path uploadFilePath = Paths.get("./TestDrive/AlfatrainingTestDrive/test/", "Lebenslauf_2019-09-18.pdf");
            System.out.println("FILE EXIST???: " + uploadFilePath.toFile().exists());
            
            
            
           

        }
        //System.out.println(FilesToUpdate);
        ////////TEST TIME/////
            
            
            Path uploadTestTimePath = Paths.get("./TestDrive/AlfatrainingTestDrive/test/","Lebenslauf_2019-09-18.pdf");//
           
            System.out.println("Files.exists: "+Files.exists(uploadTestTimePath));
             File testTimef=toolsClases.ToolsDriveFacade.findGoogleFilesByName("Lebenslauf_2019-09-18.pdf").get(0);
             
            BasicFileAttributes fatrTest = Files.readAttributes(uploadTestTimePath, 
                BasicFileAttributes.class);
            
            System.out.println("testTimef created Drive-OS");
            System.out.println(testTimef.getCreatedTime().getValue());
            System.out.println(fatrTest.creationTime().toMillis());
            
            System.out.println("testTimef modified Drive-OS");
            System.out.println(testTimef.getModifiedTime().getValue());
            System.out.println(fatrTest.lastModifiedTime().toMillis());
            
            System.out.println("testTimef modified Drive-OS");
//            System.out.println(testTimef.getModifiedByMeTime().getValue());
            System.out.println(fatrTest.lastModifiedTime().toMillis());



    }

}

package toolsClases;

import bases.FileObjDrive;
import bases.RootDriveFolderTree;
import bases.FileObjOs;

import bases.RootOSFolderTree;
import com.google.api.services.drive.model.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import synchronisator.Synchronisator;

/**
 *
 * @author Shkirmantsev
 */
public class ToolsDriveFacade
{

     ////////////// HELP FOR PRINTING /////////////////
     static MyLogPrinter printer=new MyLogPrinter(ToolsDriveFacade.class,Level.FINEST,Level.WARNING);    
    /////////////////////////////////////////////////
     
     
    // ////////////////////////////////////////////////////////////////////////
    // *** FIND ***
    public static List<File> findGoogleFilesByName(String fileName) throws IOException
    {
        return FindFilesByName.getGoogleFilesByName(fileName);
    }

    public static File findOldestSubFileByName(String googleFolderIdParent, String subFileName)
    {
        try
        {
            return FindOldestFileByName.oldestSubFileByName(googleFolderIdParent, subFileName);
        } catch (IOException ex)
        {   printer.print(Level.WARNING,ex);
            return null;
        }
    }

    public static File findGoogleFileByID(String fileID) throws IOException
    {
        return FindByID.findGoogleFileByID(fileID);
    }
    // ////////////////////////////////////////////////////////////////////////
    // *** List ***

    public static List<File> listOfDriveSubFoldersInFolderByID(String googleFolderIdParent) throws IOException          // <--------------
    {
        return GetSubFolders.getGoogleSubFolders(googleFolderIdParent);
    }

    public static List<File> listOfDriveSubFoldersInRoot() throws IOException
    {
        return GetSubFolders.getGoogleRootFolders();
    }

    public static List<File> listOfDriveSubFoldersInIDFolder(String googleFolderIdParent) throws IOException      // <--------------
    {
        return GetSubFolders.getGoogleSubFolders(googleFolderIdParent);
    }

    public static List<File> listOfDriveSubFilesInRoot() throws IOException
    {
        return GetSubFiles.getGoogleRootFiles();
    }

    public static List<File> listOfDriveSubFilesInIDFolder(String googleFolderIdParent) throws IOException
    {
        return GetSubFiles.getGoogleSubFiles(googleFolderIdParent);
    }

    public static List<File> listGoogleSubFolderByName(String googleFolderIdParent, String subFolderName) throws IOException
    {
        return GetSubFoldersByName.getGoogleSubFolderByName(googleFolderIdParent, subFolderName);
    }

    public static List<File> listGoogleRootFoldersByName(String subFolderName) throws IOException
    {
        return GetSubFoldersByName.getGoogleRootFoldersByName(subFolderName);
    }

    // ////////////////////////////////////////////////////////////////////////
    // *** Download ***
    public static java.io.File downloadFileFromDriveByID(String fileID, String pathFileNameToWrite)
    {
         try
         {
             DownloadByID.downloadOneFileFromDrive(fileID, pathFileNameToWrite);
         } catch (IOException ex)
         {
             printer.print(Level.WARNING,ex);
            return null;
         }
         return Paths.get(pathFileNameToWrite).toFile();
    }

    public static java.io.File downloadFileFromDriveByID(String fileID, Path pathFileNameToWrite)
    {
        try
        {
            DownloadByID.downloadOneFileFromDrive(fileID, pathFileNameToWrite.toString());
        } catch (IOException ex)
        {
            printer.print(Level.WARNING,ex);
            return null;
        }
        return pathFileNameToWrite.toFile();
    }

    // ////////////////////////////////////////////////////////////////////////
    // *** Create/ Upload ***
    public static File createFolderOnGoogleDrive(String folderIdParent, String folderName) throws IOException
    {

        return CreateFolder.createGoogleFolder(folderIdParent, folderName);
    }

    public static File UploadFileOnGoogleDrive(String googleFolderIdParent, String contentType, //
        String customFileName, java.io.File uploadFile) throws IOException
    {
        return UploadFile.createGoogleFile(googleFolderIdParent, contentType, customFileName, uploadFile);
    }

    // ////////////////////////////////////////////////////////////////////////
    // *** Update ***
    ////////////Main func version//
    public static File updateFileInDriveByID(String fileID, String newOrOldFileName, String mimeType, String description, java.io.File uploadFile) throws IOException
    {

        return UpdateGoogleFileByID.updateGoogleFile(fileID, newOrOldFileName, mimeType, description, uploadFile);
//        return UpdateGoogleFileByID.updateGoogleFileWithDeleting(fileID, newOrOldFileName, mimeType, description, uploadFile);
    }

    /////////////
    //Other reloaded func versions//
    public static File updateFileInDriveByID(String fileID, String newOrOldFileName, String mimeType, java.io.File uploadFile) throws IOException
    {
        String description = "updated from \"GoogleDrive SynchronizatorJava\"";
        return updateFileInDriveByID(fileID, newOrOldFileName, mimeType, description, uploadFile);
    }

    public static File updateFileInDriveByID(String fileID, String parentID, String newOrOldFileName, String mimeType, String description, Path uploadFilePath) throws IOException
    {
        java.io.File file = uploadFilePath.toFile();
        return updateFileInDriveByID(fileID, newOrOldFileName, mimeType, description, file);
    }

    public static File updateFileInDriveByID(String fileID, String newOrOldFileName, String mimeType, Path uploadFilePath) throws IOException
    {
        java.io.File file = uploadFilePath.toFile();
        return updateFileInDriveByID(fileID, newOrOldFileName, mimeType, file);
    }

    public static File updateFileInDriveByID(String fileID, String newOrOldFileName, String mimeType, String description, String uploadFileStrPath) throws IOException
    {
        java.io.File file = Paths.get(uploadFileStrPath).toFile();
        return updateFileInDriveByID(fileID, newOrOldFileName, mimeType, description, file);
    }

    public static File updateFileInDriveByID(String fileID, String newOrOldFileName, String mimeType, String uploadFileStrPath) throws IOException
    {
        java.io.File file = Paths.get(uploadFileStrPath).toFile();
        return updateFileInDriveByID(fileID, newOrOldFileName, mimeType, file);
    }
    //-----------------------------------
    
     public static File updateFileWithDeletingInDriveByID(String fileID, String newOrOldFileName, String mimeType, String description, java.io.File uploadFile) throws IOException
    {

        
        return UpdateGoogleFileByID.updateGoogleFileWithDeleting(fileID, newOrOldFileName, mimeType, description, uploadFile);
    }
    
    // ////////////////////////////////////////////////////////////////////////

    // ////////////////////////////////////////////////////////////////////////
    // *** Update OR Load(if Feller) ***
    ////////////Main func version//
    public static File updateOrLoadFileInDriveByID(String fileID, String googleFolderIDParent, String newOrOldFileName, String mimeType, String description, java.io.File uploadFile) throws IOException
    {

        return UpdateGoogleFileByID.UpdateOrUpload(fileID, googleFolderIDParent, newOrOldFileName, mimeType, description, uploadFile);
    }

    public static File updateOrLoadFileInDriveByID(String fileID, String googleFolderIDParent, String newOrOldFileName, String mimeType, String description, Path uploadFilePath) throws IOException
    {

        return UpdateGoogleFileByID.UpdateOrUpload(fileID, googleFolderIDParent, newOrOldFileName, mimeType, description, uploadFilePath.toFile());
    }

    public static File updateOrLoadFileInDriveByID(String fileID, String googleFolderIDParent, String newOrOldFileName, String mimeType, String description, String uploadFileStrPath) throws IOException
    {

        return UpdateGoogleFileByID.UpdateOrUpload(fileID, googleFolderIDParent, newOrOldFileName, mimeType, description, Paths.get(uploadFileStrPath).toFile());
    }

    public static File updateOrLoadFileInDriveByID(String fileID, String googleFolderIDParent, String newOrOldFileName, String mimeType, java.io.File uploadFile) throws IOException
    {
        String description = "updated from \"GoogleDrive SynchronizatorJava\"";
        return UpdateGoogleFileByID.UpdateOrUpload(fileID, googleFolderIDParent, newOrOldFileName, mimeType, description, uploadFile);
    }

    public static File updateOrLoadFileInDriveByID(String fileID, String googleFolderIDParent, String newOrOldFileName, String mimeType, Path uploadFilePath) throws IOException
    {
        String description = "updated from \"GoogleDrive SynchronizatorJava\"";
        return UpdateGoogleFileByID.UpdateOrUpload(fileID, googleFolderIDParent, newOrOldFileName, mimeType, description, uploadFilePath.toFile());
    }

    public static File updateOrLoadFileInDriveByID(String fileID, String googleFolderIDParent, String newOrOldFileName, String mimeType, String uploadFileStrPath) throws IOException
    {
        String description = "updated from \"GoogleDrive SynchronizatorJava\"";
        return UpdateGoogleFileByID.UpdateOrUpload(fileID, googleFolderIDParent, newOrOldFileName, mimeType, description, Paths.get(uploadFileStrPath).toFile());
    }

    // ////////////////////////////////////////////////////////////////////////
    // *** Delete ***
    public static boolean deleteFileInDrive(String fileID) throws IOException
    {

        return DeleteGoogleFile.deleteFile(fileID);
    }

    //----------------------------------
    public static boolean deleteFileInOS(Path filePath) throws IOException
    {
        return Files.deleteIfExists(filePath.normalize().toAbsolutePath());
    }

    //----------------------------------
    public void deleteDirectoryRecursion(Path path) throws IOException
    {
        if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS))
        {
            try (DirectoryStream<Path> entries = Files.newDirectoryStream(path))
            {
                for (Path entry : entries)
                {
                    deleteDirectoryRecursion(entry);
                }
            }
        }
        Files.delete(path);

    }

    /**
     * Delete recursively FilefolderObjectsInOS Directories must have only
     * directories AS Content
     *
     * @param osDirObj
     * @param rFTreeInOS
     * @param sThis
     */
    public static boolean deleteSpecDirObjOSRec(FileObjOs osDirObj, RootOSFolderTree rFTreeInOS, Synchronisator synchronisator)
    {
        DirObjRecRemoover.deleteSpecDirObjOSRec(osDirObj, rFTreeInOS, synchronisator);
        return true;

    }

    public static boolean deleteSpecDirObjDriveRec(FileObjDrive driveDirObj, RootDriveFolderTree driveTree, Synchronisator synchronisator)
    {
        DirObjRecRemoover.deleteSpecDirObjDriveRec(driveDirObj, driveTree, synchronisator);
        return true;

    }

    // ////////////////////////////////////////////////////////////////////////
    // *** Math ***
    public static String getMD5HsFromString(String str) throws UnsupportedEncodingException, NoSuchAlgorithmException
    {
        return MyMD5Hash.getMD5HsFromString(str);
    }
}

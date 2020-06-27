/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dbfilerefresher;

import bases.FileObjOs;
import dto.IModTime;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;

import java.util.Collections;

import java.util.logging.Level;
import java.util.logging.Logger;
import mainGUI_FX.StartGUIWindowController;
import toolsClases.MyLogPrinter;
import toolsClases.ToolsDriveFacade;

/**
 *
 * @author Shkirmantsev
 */
public class DBRefresher
{

    ////////////// HELP FOR PRINTING /////////////////
    static MyLogPrinter printer = new MyLogPrinter(FileObjOs.class, Level.FINEST, Level.FINE);

    ////////////////////////////////////////////////////////////////////
    //!!!!!!
    public static final String FILENAME = "DO_NOT_Delete_Syncdb.sqlite";
    //!!!!!!
    public static final String DRIVE_ROOT_ID = "root";
    ////////////////////////////////////////////////////////////////////


    private static final String PATH_STR_WORK_VERSION = StartGUIWindowController.getRootPathStr() + File.separator + FILENAME;

    private static final String PATH_STR_DRIVE_VERSION = System.getProperty("user.dir")
        + File.separator + "DB" + File.separator + "dbdrive" + File.separator + FILENAME;

    //in Resource folder
    private static final String PATH_STR_TEMPLATE_VERSION = "db" + File.separator + FILENAME;

    ///////////////////////////////////////////////////////////////////////
    private static final Path PATH_OS_DB = Paths.get(PATH_STR_WORK_VERSION);
    private static final Path PATH_DRIVE_DB = Paths.get(PATH_STR_DRIVE_VERSION);


    static
    {
        System.out.println("PATH_OS_DB: " + PATH_OS_DB);
        System.out.println("PATH_DRIVE_DB: " + PATH_DRIVE_DB);

    }

    // /mnt/data/PROJECTS/Alfatraining/googleDriveSync/syncGoogleDrive/gradleSyncGoogleDriveRoot/gradleSyncGoogleDriveLogic/src/main/resources/DO_NOT_Delete_Syncdb.sqlite
    public static void chooseActualVersion() throws IOException
    {
        
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
       
        long driveVersion = 0;
        long osVersion = 0;
        long templateVersion = 1;

        //download if not null DB from Drive
        com.google.api.services.drive.model.File drivefileMeta
            = ToolsDriveFacade.findOldestSubFileByName(DRIVE_ROOT_ID, FILENAME);

        File driveDBFile = null;
        if (drivefileMeta != null)
        {
            driveDBFile = ToolsDriveFacade.downloadFileFromDriveByID(drivefileMeta.getId(), PATH_DRIVE_DB);
        }
        //        
        //connect to find version, if not long or ==0 ==>DriveVersion=0;
        if (driveDBFile != null && driveDBFile.exists())
        {
            driveVersion = IModTime.getAnyDBChangeVersion(PATH_DRIVE_DB);

        }

        File osDBFile;
        osDBFile = PATH_OS_DB.toFile();
        if (osDBFile.exists())
        {
            osVersion = IModTime.getAnyDBChangeVersion(PATH_OS_DB);

        }

        File templDBFile;
        templDBFile = new File(classLoader.getResource(PATH_STR_TEMPLATE_VERSION).getFile());

        long maxVersion = Collections.max(Arrays.asList(new Long[]
        {
            driveVersion, osVersion, templateVersion
        }));
       
        if (maxVersion == driveVersion)
        {
            Files.copy(PATH_DRIVE_DB, PATH_OS_DB, StandardCopyOption.REPLACE_EXISTING);
            printer.print(Level.FINE, "DB pulled from drive");
        } else if (maxVersion == osVersion)
        {
            //pass
            printer.print(Level.FINE, "DB is in actuale state");
        } else
        {
            Files.copy( templDBFile.toPath(), PATH_OS_DB, StandardCopyOption.REPLACE_EXISTING);
            printer.print(Level.FINE, "DB pulled from template");
        }

    }

    public static com.google.api.services.drive.model.File pushInfoDBOnDrive() throws IOException
    {
        String mimetype = Files.probeContentType(PATH_OS_DB);

        com.google.api.services.drive.model.File drivefileMeta = ToolsDriveFacade.findOldestSubFileByName(DRIVE_ROOT_ID, FILENAME);
        if (drivefileMeta != null)
        {
            String id = drivefileMeta.getId();
            com.google.api.services.drive.model.File newDriveDBInfo
                = ToolsDriveFacade.updateFileWithDeletingInDriveByID(
                    id, FILENAME, mimetype, "", PATH_OS_DB.toFile());
            return newDriveDBInfo;
        } else
        {
            try
            {
                return ToolsDriveFacade.UploadFileOnGoogleDrive(DRIVE_ROOT_ID, mimetype, FILENAME, PATH_OS_DB.toFile());
            } catch (Exception e)
            {
                printer.print(Level.SEVERE, e);
            }

        }

        return null;
    }

    public static void main(String[] args) throws IOException
    {
        DBRefresher.chooseActualVersion();

    }
}

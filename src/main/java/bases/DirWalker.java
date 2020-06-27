/*
        !!! THIS IS ONLY TEST CLASS (TEMPLATE) FOR PROOF ROOT FOLDER TREE!!! 
*/
package bases;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Shkirmantsew
 */
public class DirWalker
{
    public static void main(String[] args) throws UnsupportedEncodingException, NoSuchAlgorithmException, IOException 
    {
        
        Path osPathToFile=Paths.get("./TestDrive/fileDoc0_1.txt");
        BasicFileAttributes attr = Files.readAttributes(osPathToFile.toAbsolutePath().normalize(), BasicFileAttributes.class);
        System.out.println("attr.creationTime().toMillis(): "+attr.creationTime().toMillis());
        System.out.println("attr.lastModifiedTime().toMillis(): "+attr.lastModifiedTime().toMillis());
        System.out.println("attr.isDirectory(): "+attr.isDirectory());
        System.out.println("Files.probeContentType(osPathToFile): "+Files.probeContentType(osPathToFile));
        
        //****************************************
        
        Path rootDrivePathInOS=Paths.get("./TestDrive");
        RootOSFolderTree root=RootOSFolderTree.getInstance();
        RootOSFolderTree.getInstance().chooseRootPath(rootDrivePathInOS);
        
        ///////////////////////////////////////////////////////////////////////
//        System.out.println("Chek in main method wether root in base");
//        System.out.println("Root obj: "+RootOSFolderTree.getInstance().getDirFileObjOsByMyID(toolsClases.ToolsDriveBox.getMD5HsFromString("root")));
//        System.out.println("Root obj: "+RootOSFolderTree.getInstance().getDirFileObjOsByMyID(toolsClases.ToolsDriveBox.getMD5HsFromString("root")));
//        //RootFolderTreeInOs.getInstance().addToBaseMyIDHshDirsOS(RootOSFolderTree.getInstance().getMySimulatedID(), RootOSFolderTree.getInstance());
//        //
//        Path drivePathInOS=Paths.get("./TestDrive/Folder0_1"); 
//        FileObjOs node=new FileObjOs(drivePathInOS);
//        //
//        rootDrivePathInOS=Paths.get("./TestDrive/Folder0_1/Folder1_1"); 
//        node=new FileObjOs(rootDrivePathInOS);
//        //
//        rootDrivePathInOS=Paths.get("./TestDrive/fileDoc0_1.txt"); 
//        node=new FileObjOs(rootDrivePathInOS);
//        //
//        rootDrivePathInOS=Paths.get("./TestDrive/Folder0_2"); 
//        node=new FileObjOs(rootDrivePathInOS);
//        RootOSFolderTree.getInstance().showMeAndMyContent();
//        //
//        rootDrivePathInOS=Paths.get("./TestDrive/Folder0_2/fileDoc0_2_1.txt"); 
//        node=new FileObjOs(rootDrivePathInOS);
//        //
//        //
//        //
    //////////////////////////////////////////////////////////////////////
   RootOSFolderTree.getInstance().toGrowTreeFromThis();
//    Files.walk(rootDrivePathInOS)
//        .forEach((t) ->
//    {
//        System.out.println(t);
//        
//    });
    
    
    //
    //
    //
    RootOSFolderTree.getInstance().showMeAndMyContent();
    }
}

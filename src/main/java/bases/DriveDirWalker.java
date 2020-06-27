/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 * TEST CLASS
 * @author Shkirmantsev
 */
public class DriveDirWalker
{
    public static void main(String[] args) throws UnsupportedEncodingException, NoSuchAlgorithmException, IOException 
    {
        
        
        
        Path rootDrivePathInOS=Paths.get("./TestDrive");
        RootDriveFolderTree rootDrive=RootDriveFolderTree.getInstance();
        RootDriveFolderTree.getInstance().chooseRootPath(rootDrivePathInOS);
        ///////////////////////////////////////////////////////////////
        RootDriveFolderTree.getInstance().toGrowTreeFromThis();
        //
        //
        //
        RootDriveFolderTree.getInstance().showMeAndMyContent();
    }
}

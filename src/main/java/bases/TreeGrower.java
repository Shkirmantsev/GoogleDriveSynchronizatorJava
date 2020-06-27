/*
 * This Class Help to grow the Tree
 */
package bases;

import com.google.api.services.drive.model.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Formatter;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import toolsClases.MyLogPrinter;
import toolsClases.ToolsDriveFacade;
import toolsClases.UpdateGoogleFileByID;

/**
 *
 * @author Shkirmantsev
 */
public class TreeGrower
{
    ////////////// HELP FOR PRINTING /////////////////
     static MyLogPrinter printer=new MyLogPrinter(TreeGrower.class,Level.FINEST,Level.WARNING);    
    /////////////////////////////////////////////////
     
    public static void toGrowTreeFromFolderObj(AbstrFolderTree FldrTrObj)throws IOException, UnsupportedEncodingException{
        
       
        
        if (FldrTrObj.isThisIsInAnyMainBase())                                     // TO DO: probably first argument in Drive not be!!, then delete    
        {

            String name = FldrTrObj.isIsFolder() ? FldrTrObj.getTrueName() : "   --- " + FldrTrObj.getTrueName();
            TreeGrower.printer.print(Level.FINER,name);
            
            /////////////////////////////////////////////////////////////////   // TO DO: walk in google Cloud
            if (FldrTrObj.getGeneratedName().equals("root") || FldrTrObj.isIsFolder())
            {
                TreeGrower.printer.print(Level.FINE,String.format("Growing tree from %s ...", FldrTrObj.getGeneratedName()));
                //
                //Find all !!SubFiles!! in this Folder
                List<File> DirFilesList = ToolsDriveFacade.listOfDriveSubFilesInIDFolder(FldrTrObj.getTrueID());
                //
                Comparator<File> comparator = Comparator.comparing((File eachFile) -> eachFile.getModifiedTime().getValue());
                //
//                Stream strStream = DirFilesList.stream().map((d) -> (String) d.getName());
//                ArrayList<String> strArr = (ArrayList<String>) strStream.collect(Collectors.toCollection(ArrayList::new));
                //
                if ((DirFilesList != null) && (!DirFilesList.isEmpty()))
                {
//                    TreeGrower.printer.print(Level.FINEST," =====>>>> " + strArr);
                    for (File file : DirFilesList)
                    {
                        //Set with same names of files, but different created Time
                        TreeSet<File> sortedFilesWithDuplicate;

                        sortedFilesWithDuplicate = new TreeSet<>(comparator);
                        sortedFilesWithDuplicate.add(file);
                        TreeGrower.printer.print(Level.FINER,String.format("%s - is jast added to Map --",file.getName()));
                        
                        //If FileName before not existed then create
                        //"TreeSet<File> sortedFilesWithDuplicate" with single file:
                        FldrTrObj.getStructSubFiles().putIfAbsent(file.getName(), sortedFilesWithDuplicate);
                        //If FileName before already existed then add File to
                        //"TreeSet<File> sortedFilesWithDuplicate" with many files:
                        FldrTrObj.getStructSubFiles().computeIfPresent(file.getName(), ((t, u) ->
                        {
                            u.add(file);
                            return u;
                        }));

                    }
                    int i;
                    
                    //Temporary values:
                    //------
                    String generatedName_i;
                    String trueFileName_i;
                    String parentID_i;
                    String myGenParentID_i;
                    String trueID_i;
                    //String myPerentGenPath_i,  // <-- not need
                    Long createdTime_i;
                    Long modified_i;
                    boolean isDuplicate_i;
                    String oldestDuplicateID_i;
                    boolean isFolder_i;
                    String mimeTypeGoog_i;
                    String mimeType_i;
                    //---------
                    FileObjDTO fileDtoTmp=null;
                    for (String sortedName : FldrTrObj.getStructSubFiles().keySet())
                    {
                        i = 0;
                        oldestDuplicateID_i = null;
                        //TreeGrower.myPrintLog.finer("====FldrTrObj.getStructSubFiles().get(sortedName): "+FldrTrObj.getStructSubFiles().get(sortedName));
                        
                        for (File sortedFile : FldrTrObj.getStructSubFiles().get(sortedName))
                        {
                            trueFileName_i = sortedName;
                            parentID_i = FldrTrObj.getTrueID();
                            myGenParentID_i = FldrTrObj.getMySimulatedID();
                            trueID_i = sortedFile.getId();
                            createdTime_i = sortedFile.getCreatedTime().getValue();
                            modified_i = sortedFile.getModifiedTime().getValue();
                            if (i == 0)
                            {
                                isDuplicate_i = false;
                                oldestDuplicateID_i = trueID_i;

                            }
                            else
                            {
                                
                                isDuplicate_i = true;
                            }
                            isFolder_i = false;     //<== ToolsDriveFacade.listOfDriveSubFilesInIDFolder() was only for files                             
                            mimeTypeGoog_i = sortedFile.getMimeType();
                            mimeType_i = mimeTypeGoog_i;                              
                            //
                            //
                            generatedName_i = FldrTrObj.generateGeneratedName(trueFileName_i, isDuplicate_i, trueID_i);
                            TreeGrower.printer.print(Level.FINEST,"_____new DriveFileObj____ name " + trueFileName_i + " ID = " + trueID_i + " generatedName_i : " + generatedName_i);
                            
                            try
                            {
                                fileDtoTmp=new FileObjDTO(
                                        generatedName_i,
                                        trueFileName_i,
                                        parentID_i,
                                        myGenParentID_i,
                                        trueID_i,
                                        createdTime_i,
                                        modified_i,
                                        isDuplicate_i,
                                        oldestDuplicateID_i,
                                        isFolder_i,
                                        mimeTypeGoog_i,
                                        mimeType_i);
                                //
                                //auto construct and adding:
                                new FileObjDrive(fileDtoTmp);                          
                                                                  
                               

                            }
                            catch (NoSuchAlgorithmException ex)
                            {
                                throw new ExceptionInInitializerError(" Problem by growing GoogleTree");
                            }

                            //
                            i++;
                        }
                    }

                }
                //==============================================================
                //==============================================================
                ////Find all !!SubFolders!! in this Folder
                List<File> DirFoldersList = ToolsDriveFacade.listOfDriveSubFoldersInIDFolder(FldrTrObj.getTrueID());

                if ((DirFoldersList != null) && !DirFoldersList.isEmpty())
                {
                    for (File folder : DirFoldersList)
                    {
                        TreeSet<File> sortedFoldersWithDuplicate;

                        sortedFoldersWithDuplicate = new TreeSet<>(comparator);
                        sortedFoldersWithDuplicate.add(folder);
                        
                        TreeGrower.printer.print(Level.FINE,String.format("%s - is jast added to Map --",folder.getName()));
                        FldrTrObj.getStructSubDirs().putIfAbsent(folder.getName(), sortedFoldersWithDuplicate);

                        FldrTrObj.getStructSubDirs().computeIfPresent(folder.getName(), ((t, u) ->
                        {
                            u.add(folder);
                            return u;
                        }));
                        
                        
                    }
                    int i;

                    //------
                    String generatedName_i;
                    String trueFileName_i;
                    String parentID_i;
                    String myGenParentID_i;
                    String trueID_i;
                    //String myPerentGenPath_i,  // <-- not need
                    Long createdTime_i;
                    Long modified_i;
                    boolean isDuplicate_i;
                    String oldestDuplicateID_i;
                    boolean isFolder_i;
                    String mimeTypeGoog_i;
                    String mimeType_i;
                    //---------
                    for (String sortedName : FldrTrObj.getStructSubDirs().keySet())
                    {
                        i = 0;
                        oldestDuplicateID_i = null;
                        isDuplicate_i = false;
                        FileObjDTO folderDtoTmp=null;
                        for (File sortedFolder : FldrTrObj.getStructSubDirs().get(sortedName))
                        {
                            trueFileName_i = sortedName;
                            parentID_i = FldrTrObj.getTrueID();
                            myGenParentID_i = FldrTrObj.getMySimulatedID();
                            trueID_i = sortedFolder.getId();
                            createdTime_i = sortedFolder.getCreatedTime().getValue();
                            modified_i = sortedFolder.getModifiedTime().getValue();
                            if (i == 0)
                            {
                                isDuplicate_i = false;
                                oldestDuplicateID_i = trueID_i;

                            }
                            else
                            {
                                //TO DO: duplicate + new generate name
                                isDuplicate_i = true;
                            }
                            isFolder_i = true;                                       
                            mimeTypeGoog_i = sortedFolder.getMimeType();
                            mimeType_i = null;                              
                            //
                            //
                            generatedName_i = FldrTrObj.generateGeneratedName(trueFileName_i, isDuplicate_i, trueID_i);
                            try
                            {
                                //
                                folderDtoTmp=new FileObjDTO(
                                        generatedName_i,
                                        trueFileName_i,
                                        parentID_i,
                                        myGenParentID_i,
                                        trueID_i,
                                        createdTime_i,
                                        modified_i,
                                        isDuplicate_i,
                                        oldestDuplicateID_i,
                                        isFolder_i,
                                        mimeTypeGoog_i,
                                        mimeType_i);
                                
                                //
                                (new FileObjDrive(folderDtoTmp)).toGrowTreeFromThis();
                            }
                            catch (NoSuchAlgorithmException ex)
                            {
                                throw new ExceptionInInitializerError(" Problem by growing GoogleTree");
                            }
                            //
                            i++;
                        }
                    }
                }

            }
            ///////////////////////////////////////////////////////////////////
            //--------- IF this. NOT Folder, must be Already added : ------------------------------- 

        }
        else
        {
            Formatter f = new Formatter();
            f.format(" This Node %s must be already added to DriveFolderTree", FldrTrObj.getGeneratedName());
            throw new ExceptionInInitializerError(f.toString());
        }
        
    }

    
}

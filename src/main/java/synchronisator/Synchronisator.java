package synchronisator;

import bases.*;
import com.google.api.services.drive.model.File;
import dbfilerefresher.DBRefresher;
import dto.FileModTimeModel;
import dto.IModTime;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;

import java.util.stream.Collectors;
import toolsClases.MyLogPrinter;

/**
 *
 * @author Shkirmantsev
 */
public class Synchronisator
{

    static MyLogPrinter printer = new MyLogPrinter(Synchronisator.class, Level.FINEST, Level.INFO);
    ///////////////////////////////////////////////////
    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    private static volatile Synchronisator instance = null;
    ///////////////////////////////////////////////////

    private final HashSet<String> idDirsExistBoth = new HashSet<>();
    private final HashSet<String> idFilesExistBoth = new HashSet<>();

    private final HashSet<String> presentDirOSAbsentDrive = new HashSet<>();
    private final HashSet<String> presentFileOSAbsentDrive = new HashSet<>();

    private final HashSet<String> presentDirDriveAbsentOS = new HashSet<>();
    private final HashSet<String> presentFileDriveAbsentOS = new HashSet<>();

    private RootDriveFolderTree driveTree;
    private RootOSFolderTree rFTreeInOS;
    private Path rootDir;
    private TypeOfSync typeOfSync;
    String rootGenID;

    private Synchronisator()
    {
        super();
        System.out.println("ampty Singelton Synchronisator is initialised");

    }
    //----------------------------------------

    public void setInitialParametrs(RootDriveFolderTree dft, RootOSFolderTree rft, Path rtDir)
    {
        this.rootDir = rtDir;
        this.rFTreeInOS = rft;

        //----------------
        this.driveTree = dft;
        this.rootGenID = rFTreeInOS.getMySimulatedID();
    }

    //
    private Synchronisator(RootDriveFolderTree dft, RootOSFolderTree rft, Path rtDir)
        throws UnsupportedEncodingException, NoSuchAlgorithmException, IOException
    {

        this.rootDir = rtDir;
        this.rFTreeInOS = rft;

        //----------------
        this.driveTree = dft;

        printer.print("Synchronisator: setInitialParametrs(DriveFolderTree dft, RootFolderTreeInOs rft, Path rtDir)");
        this.rootGenID = rFTreeInOS.getMySimulatedID();
    }

    private boolean doBuildingTrees() throws UnsupportedEncodingException, NoSuchAlgorithmException, IOException
    {

        this.rFTreeInOS.chooseRootPath(this.rootDir);
        //----------------

        this.driveTree.chooseRootPath(this.rootDir);
        //----------------
        this.rFTreeInOS.toGrowTreeFromThis();
        this.driveTree.toGrowTreeFromThis();
        return true;

    }

    public boolean doSynchronysation(TypeOfSync typeS) throws Exception
    {
        printer.print("IN doSynchronysation..");
        //Fill hash bases

        this.typeOfSync = typeS;

        DBRefresher.chooseActualVersion();

        printer.print(Level.FINE, "==>fillDBInfo()()");
        //
        if (!this.fillDBInfo())
        {
            throw new Exception(" fillDBInfo() is not successful");
        }
        //
        printer.print(Level.FINE, "==>fillIDExistBoth()");
        //
        if (!this.fillIDExistBoth())
        {
            throw new Exception(" fillIDExistBoth() is not successful");
        }
        //
        printer.print(Level.FINE, "==>fillPresentOSAbsentDrive()");
        if (!this.fillPresentOSAbsentDrive())
        {
            throw new Exception(" fillPresentOSAbsentDrive() is not successful");
        }
        //-------------------
        //
        printer.print(Level.FINE, "==>fillPresentDriveAbsentOS()");
        if (!this.fillPresentDriveAbsentOS())
        {
            throw new Exception(" fillPresentDriveAbsentOS() is not successful");
        }
        //-------------------

        //---------------
        //Syhchronisation all markers
        printer.print(Level.FINE, "==>syncTreesMarkerInfos()");
        if (!this.syncTreesMarkerInfos())
        {
            throw new Exception(" syncTreesmarkerInfos() is not successful");
        }

        switch (this.typeOfSync)
        {
            case MainDRIVE:
                printer.print(" Nothing is Uploaded because of MainDRIVE ");

                if (!this.downloadMissingFolders())
                {
                    throw new Exception(" downloadMissingFolders() is not successful");
                }
                ;

                if (!this.downloadMissingFiles())
                {
                    throw new Exception(" downloadMissingFiles() is not successful");
                }
                ;
                if (!this.delObjectsFromOsMissingInDrive())
                {
                    throw new Exception(" delObjectsFromOsMissingInDrive() is not successful");
                }
                ;

                if (!this.updateOldFilesInOs())
                {
                    throw new Exception(" updateOldFilesInOs() is not successful");
                }
                ;

                break;
            case MainOS:
                printer.print(" Nothing is downloaded because of Main OS mode ");

                if (!this.deleteInDriveExstSpecFiles())
                {
                    throw new Exception(" deleteInDriveExstSpecFiles() is not successful because of MainOS is not implemented ");
                }

                if (!this.uploadMissingFoldersAndFiles())
                {
                    throw new Exception(" uploadMissingFoldersAndFiles() is not successful because of MainOS is not implemented ");
                }
                ;

                if (!this.updateOldFilesInDrive())
                {
                    throw new Exception(" updateOldFilesInDrive() is not successful because of MainOS is not implemented ");
                }
                ;

                break;

            case UPDATEALL:

            default:
                if (!this.downloadMissingFolders())
                {
                    throw new Exception(" downloadMissingFolders() is not successful");
                }
                ;

                if (!this.downloadMissingFiles())
                {
                    throw new Exception(" downloadMissingFiles() is not successful");
                }
                ;

                if (!this.uploadMissingFoldersAndFiles())
                {
                    throw new Exception(" uploadMissingFoldersAndFiles() is not successful");
                }

                if (!this.updateOldFilesInDrive())
                {
                    throw new Exception(" updateOldFilesInDrive() is not successful");
                }
                if (!this.updateOldFilesInOs())
                {
                    throw new Exception(" updateOldFilesInOs() is not successful");
                }

                break;
        }

        return true;
    }

    ///////////////////////////////////
    
   

    private boolean fillDBInfo()
    {   
        FileModTimeModel.cleanDBTresh(rFTreeInOS, driveTree);
         
        Set<String> set;

        //Drive, Folder
        set = driveTree.getKeySetMyIDHshDirs();

        set.stream()
            .filter(id -> !id.equalsIgnoreCase(this.rootGenID))
            .map(Id -> (FileObjDrive) driveTree.getFObjByIDInMyIDHshDirs(Id))
            .forEach(dfObj
                -> //SAVE INFO IN DB
                (new FileModTimeModel(dfObj, HoldPl.DRIVE)).computeAndUpdDB(HoldPl.DRIVE));
       
        final List<String> strToDelete = new LinkedList<>();
        
        //Drive=> Files
        set = driveTree.getKeySetMyIDHshFiles();
        
        set.stream()
            .map(Id -> (FileObjDrive) driveTree.getFObjByIDInMyIDHshFiles(Id))
            .forEach(dfObj ->
            {
                if (((FileObjDrive)dfObj).getTrueName().startsWith(DBRefresher.FILENAME))
                {
                     strToDelete.add(dfObj.getMySimulatedID()) ;

                } else
                //SAVE INFO IN DB                
                {
                    (new FileModTimeModel(dfObj, HoldPl.DRIVE)).computeAndUpdDB(HoldPl.DRIVE);
                }
            });

        /////////////////
        if (!strToDelete.isEmpty())
        {
            strToDelete.forEach(str->driveTree.removeFromBaseMyIDHshFilesOS(str));
            
        }
        /////////////////

        //OS, Folder
        set = rFTreeInOS.getKeySetMyIDHshDirs();
        set.stream()
            .filter(id -> !id.equalsIgnoreCase(this.rootGenID))
            .map(Id -> (FileObjOs) rFTreeInOS.getFObjByIDInMyIDHshDirs(Id))
            .forEach(dfObj
                -> //SAVE INFO IN DB
                (new FileModTimeModel(dfObj, HoldPl.OS)).computeAndUpdDB(HoldPl.OS));

        //OS=>Files
        set = rFTreeInOS.getKeySetMyIDHshFiles();
        set.stream()
            .map(Id -> (FileObjOs) rFTreeInOS.getFObjByIDInMyIDHshFiles(Id))
            .forEach(dfObj ->
            {
                if (((FileObjOs)dfObj).getTrueName().startsWith(DBRefresher.FILENAME))
                {
                     strToDelete.add(dfObj.getMySimulatedID()) ;
                } else
                //SAVE INFO IN DB                
                {
                    (new FileModTimeModel(dfObj, HoldPl.OS)).computeAndUpdDB(HoldPl.OS);
                }
            });

        /////////////////
        if (!strToDelete.isEmpty())
        {
            strToDelete.forEach(str->driveTree.removeFromBaseMyIDHshFilesOS(str));
            
        }
        /////////////////
        return true;
    }

    private boolean fillIDExistBoth()  //Exchange into intersection
    {
        printer.print(Level.FINER, "IN fillIDExistBoth()");

        boolean res = true;
        Set<String> set;
        set = driveTree.getKeySetMyIDHshDirs();

        if (!this.idDirsExistBoth.addAll(set))
        {
            printer.print(Level.FINEST, "this.idDirsExistBoth.addAll(set) is FALSE");

        }

        set = rFTreeInOS.getKeySetMyIDHshDirs();

        if (!this.idDirsExistBoth.retainAll(set));
        {
            printer.print(Level.FINEST, "this.idDirsExistBoth.retainAll(set) is FALSE");

        }

        set = driveTree.getKeySetMyIDHshFiles();

        if (!idFilesExistBoth.addAll(set))
        {
            printer.print(Level.FINEST, "idFilesExistBoth.addAll(set) IS FALSE");

        }

        set = rFTreeInOS.getKeySetMyIDHshFiles();
        printer.print(Level.FINEST, "---  rFTreeInOS.getKeySetMyIDHshFiles(): " + set);

        if (!idFilesExistBoth.retainAll(set))
        {
            printer.print(Level.FINEST, "idFilesExistBoth.retainAll(set) IS FALSE");

        }

        return res;

    }
//===========================================

    private boolean fillPresentOSAbsentDrive()
    {
        boolean res = true;
        Set<String> set;
        set = rFTreeInOS.getKeySetMyIDHshDirs();

        if (!this.presentDirOSAbsentDrive.addAll(set))
        {

        }

        set = driveTree.getKeySetMyIDHshDirs();

        if (!this.presentDirOSAbsentDrive.removeAll(set));
        {

        }

        set = rFTreeInOS.getKeySetMyIDHshFiles();

        if (!this.presentFileOSAbsentDrive.addAll(set))
        {

        }

        set = driveTree.getKeySetMyIDHshFiles();

        if (!this.presentFileOSAbsentDrive.removeAll(set));
        {

        }

        return res;
    }
//===========================================

    private boolean fillPresentDriveAbsentOS()
    {
        boolean res = true;
        Set<String> set;
        set = driveTree.getKeySetMyIDHshDirs();

        if (!this.presentDirDriveAbsentOS.addAll(set))
        {

        }

        set = rFTreeInOS.getKeySetMyIDHshDirs();

        if (!this.presentDirDriveAbsentOS.removeAll(set));
        {

        }

        set = driveTree.getKeySetMyIDHshFiles();

        if (!this.presentFileDriveAbsentOS.addAll(set))
        {

        }

        set = rFTreeInOS.getKeySetMyIDHshFiles();

        if (!this.presentFileDriveAbsentOS.removeAll(set));
        {

        }

        return res;
    }
//===========================================
    //*****************************************************************************

    /**
     *
     * @return Exchange data and synchronisation all markers (whether
 succsessfull) between FileObjOs and FileObjDrive
     */
    private boolean syncTreesMarkerInfos()                                                              // <---------TO DO: ALL FIELDS
    {

        switch (this.typeOfSync)
        {
            case MainDRIVE:
                printer.print("==>syncTreesMarkerInfos()==> TypeOfSync.MainDRIVE:");

                ////////----- FILES -----////////
                if (!idFilesExistBoth.isEmpty())
                {
                    idFilesExistBoth.forEach((myHashId) ->
                    {
                        printer.print(Level.FINE, "==>syncTreesMarkerInfos()==> case MainDRIVE:==>>idFilesExistBoth.forEach((myHashId) ->");
                        FileObjOs fDirObj = (FileObjOs) rFTreeInOS.getFObjByIDInMyIDHshFiles(myHashId);
                        FileObjDrive driveFDirObj = (FileObjDrive) driveTree.getFObjByIDInMyIDHshFiles(myHashId);

                        Exchanger.exchangeMainDrive(fDirObj, driveFDirObj, rFTreeInOS, driveTree, myHashId);

                    });
                }

                break;
            case MainOS:
                printer.print("==>syncTreesMarkerInfos()==> case MainOS:");

                ////////----- FILES -----////////
                if (!idFilesExistBoth.isEmpty())
                {
                    idFilesExistBoth.forEach((myHashId) ->
                    {
                        FileObjOs fDirObj = (FileObjOs) rFTreeInOS.getFObjByIDInMyIDHshFiles(myHashId);
                        FileObjDrive driveFDirObj = (FileObjDrive) driveTree.getFObjByIDInMyIDHshFiles(myHashId);

                        Exchanger.exchangeMainOS(fDirObj, driveFDirObj, rFTreeInOS, driveTree, myHashId);

                    });
                }
                break;
            case UPDATEALL:

            default:
                printer.print("==>syncTreesMarkerInfos()==> case TypeOfSync.UPDATEALL/Default:");

                if (!idDirsExistBoth.isEmpty())
                {
                    printer.print(Level.FINEST, "idDirsExistBoth: " + idDirsExistBoth);
                    idDirsExistBoth.stream().filter(myHashId -> !myHashId.equalsIgnoreCase(rootGenID)).forEach((myHashId) ->
                    {
                        ////////----- DIRs -----////////

                        FileObjOs fDirObj = (FileObjOs) rFTreeInOS.getFObjByIDInMyIDHshDirs(myHashId);

                        FileObjDrive driveFDirObj = (FileObjDrive) driveTree.getFObjByIDInMyIDHshDirs(myHashId);

                        Exchanger.exchangeApdateAll(fDirObj, driveFDirObj, rFTreeInOS, driveTree, myHashId);
                        //printer.print(Level.FINEST, "File Obj exchangeApdateAll " + myHashId);
                    });
                }
                printer.print(Level.FINE, "==>syncTreesMarkerInfos()==> case case UPDATEALL/default (FILES):");
                ////////----- FILES -----////////
                if (!idFilesExistBoth.isEmpty())
                {
                    printer.print(Level.FINER, "idFilesExistBoth: " + idFilesExistBoth);
                    idFilesExistBoth.forEach((myHashId) ->
                    {

                        FileObjOs fDirObj = (FileObjOs) rFTreeInOS.getFObjByIDInMyIDHshFiles(myHashId);
                        FileObjDrive driveFDirObj = (FileObjDrive) driveTree.getFObjByIDInMyIDHshFiles(myHashId);

                        Exchanger.exchangeApdateAll(fDirObj, driveFDirObj, rFTreeInOS, driveTree, myHashId);

                    });
                }
                //
                //------------
                //------------ END Swith-CASE: --
                break;
        }
        return true;
    }
//-------------------------------------------------------------------------------
//-------------------------------------------------------------------------------

    private boolean uploadMissingFoldersAndFiles() throws IOException, UnsupportedEncodingException, NoSuchAlgorithmException
    {   DBRefresher.chooseActualVersion();
        printer.print("--- uploadMissingFoldersAndFiles() START---");
        // iterator with nodes, wich must be upload from OS
        Iterator uploadDirIterator = Collections.synchronizedList(
            this.presentDirOSAbsentDrive.stream()
                .filter(id -> !id.equalsIgnoreCase(this.rootGenID))
                .map(Id -> (FileObjOs) rFTreeInOS.getFObjByIDInMyIDHshDirs(Id))
                .collect(Collectors.toList())).listIterator();
        //check this node whether the whole chain of nodes to this node in DRIVE is complete
        boolean findParent = false;

        printer.print(Level.FINE, "-uploadDirIterator.hasNext()--");
        Deque<FileObjOs> stack = new ArrayDeque<>();
        Optional ParIDTmp;
        FileObjDrive driveFileObjTmp = null;
        long modTimeTmp;
        while (uploadDirIterator.hasNext())
        {
            FileObjOs dirObj = (FileObjOs) uploadDirIterator.next();
            //stack 
            printer.print(Level.FINEST, "\n  Make tmp stack");
            stack.clear();
            //Upload if Parent is Exist and add to stack if not Exist,search Parent:
            findParent = false;

            do
            {
                printer.print(Level.FINEST, "uploadMissingFoldersAndFiles()==>-uploadDirIterator.hasNext()==>> Find true parent ID from OS Dir in Drive tree\n");
                //Find true parent ID from OS Dir in DRIVE tree
                ParIDTmp = Optional.ofNullable((driveTree.getFObjByIDInMyIDHshDirs(dirObj.getMyGenParentID())).getTrueID());

                //if our node (Dir) have parents in DRIVE:
                if (ParIDTmp.isPresent())
                {
                    dirObj.setTrueParentID((String) ParIDTmp.get());
                    findParent = true;

                    // create Folder On GoogleDrive 
                    File createdFolder = toolsClases.ToolsDriveFacade.createFolderOnGoogleDrive(dirObj.getParentID(), dirObj.getTrueName());
                    printer.print("folder was created: " + createdFolder.getName());
                    //Exchange TrueID from DRIVE to dirObj in Os
                    dirObj.setTrueID(createdFolder.getId());

                    //--Create node (Dir) Object (self adding with links to RootDriveFolderTree):
                    printer.print(Level.FINER, "Create node (Dir) Object (self adding with links to DriveFolderTree):");

                    modTimeTmp = (new FileModTimeModel(dirObj, HoldPl.OS)).getCompMTime(HoldPl.OS);
                    driveFileObjTmp = new FileObjDrive(
                        new FileObjDTO(
                            dirObj.getGeneratedName(),//generatedName,
                            dirObj.getTrueName(),//trueFileName,
                            dirObj.getParentID(),//parentID,
                            dirObj.getMyGenParentID(),//myGenParentID,
                            dirObj.getTrueID(),//trueID,
                            //String myPerentGenPath,
                            dirObj.getModifiedTime(),//createdTime,
                            createdFolder.getModifiedTime().getValue(),//modified,
                            dirObj.isIsDuplicate(),//isDuplicate,
                            dirObj.getOldestDuplicateID(), //oldestDuplicateID,
                            dirObj.isIsFolder(),//isFolder,
                            createdFolder.getMimeType(),//mimeTypeGoog,
                            createdFolder.getMimeType()));//mimeType
                    //
                    dirObj.setActionMarker(ActionMarkers.NeedNoAction);

                    //--Delete member from upload list                    
                    uploadDirIterator.remove();
//                    this.presentDirOSAbsentDrive.remove(dirObj.getMySimulatedID());

                    //SAVE INFO IN DB
                    (new FileModTimeModel(driveFileObjTmp, HoldPl.DRIVE))
                        .setFileModTimeReal(modTimeTmp)
                        .updMTimeInDB(HoldPl.DRIVE);

                } else
                {
                    //we add this folder later to DRIVE
                    printer.print(Level.FINEST, "we add this folder later to Drive" + dirObj.getGeneratedName());
                    stack.addFirst(dirObj);
                    //level up to Parent-folder. 
                    dirObj = (FileObjOs) rFTreeInOS.getFObjByIDInMyIDHshDirs(dirObj.getMyGenParentID());
                    // Change OsDir object untill we have found Patent in drive

                }

            } while (!findParent);
            //---------------------
            printer.print(Level.FINER, " start \"while (!stack.isEmpty())\"");
            while (!stack.isEmpty())
            {
                dirObj = stack.removeFirst();
                //True ParentID from OS Object in DriveTree:
                ParIDTmp = Optional.ofNullable(((FileObjOs) rFTreeInOS.getFObjByIDInMyIDHshDirs(dirObj.getMyGenParentID())).getTrueID());

                dirObj.setTrueParentID((String) ParIDTmp.get());
                //Create Dir in GoogleDrive
                File createdFolder = toolsClases.ToolsDriveFacade.createFolderOnGoogleDrive(dirObj.getParentID(), dirObj.getTrueName());
                dirObj.setTrueID(createdFolder.getId());

                printer.print(Level.FINEST, " Create node (Dir) Object (FROM Steak)");

                modTimeTmp = (new FileModTimeModel(dirObj, HoldPl.OS)).getCompMTime(HoldPl.OS);

                driveFileObjTmp = new FileObjDrive(
                    new FileObjDTO(
                        dirObj.getGeneratedName(),//generatedName,
                        dirObj.getTrueName(),//trueFileName,
                        dirObj.getParentID(),//parentID,
                        dirObj.getMyGenParentID(),//myGenParentID,
                        dirObj.getTrueID(),//trueID,
                        //String myPerentGenPath,
                        dirObj.getCreatedTime(),//createdTime,
                        createdFolder.getModifiedTime().getValue(),//modified,
                        dirObj.isIsDuplicate(),//isDuplicate,
                        dirObj.getOldestDuplicateID(), //oldestDuplicateID,
                        dirObj.isIsFolder(),//isFolder,
                        createdFolder.getMimeType(),//mimeTypeGoog,
                        dirObj.getMimeType()));//mimeType
                //                
                dirObj.setActionMarker(ActionMarkers.NeedNoAction);

                //--Delete member from apload list
                this.presentDirOSAbsentDrive.remove(dirObj.getMySimulatedID());

                //SAVE INFO IN DB
                (new FileModTimeModel(driveFileObjTmp, HoldPl.DRIVE))
                    .setFileModTimeReal(modTimeTmp)
                    .updMTimeInDB(HoldPl.DRIVE);

            }
        }

        /// !!!!!!! ///     MAKE for FILE:
        Iterator uploadFileIterator = Collections.synchronizedList(
            this.presentFileOSAbsentDrive.stream()
                .map(Id -> (FileObjOs) rFTreeInOS.getFObjByIDInMyIDHshFiles(Id))
                .collect(Collectors.toList())).listIterator();
        // Use old "findParent" from last Block;
        while (uploadFileIterator.hasNext())
        {
            FileObjOs FileObj = (FileObjOs) uploadFileIterator.next();

            //Exchange TrueParentID from DRIVE to FileObj in Os
            ParIDTmp = Optional.ofNullable((driveTree.getFObjByIDInMyIDHshDirs(FileObj.getMyGenParentID())).getTrueID());
            FileObj.setTrueParentID((String) ParIDTmp.get());

            //Define file:
            java.io.File uploadFile = new java.io.File(FileObj.getMyPath().toAbsolutePath().toString());
            // "File" is googleDrive File-Object. Upload java.io.File with FileObj Parametrs from RootFolderTree
            File uploadedFile = toolsClases.ToolsDriveFacade.UploadFileOnGoogleDrive(FileObj.getParentID(), FileObj.getMimeType(), FileObj.getTrueName(), uploadFile);
            //
            printer.print("file " + uploadedFile.getName() + " was uploaded ");
            //Exchange TrueID from DRIVE to FileObj in Os
            FileObj.setTrueID(uploadedFile.getId());

            modTimeTmp = (new FileModTimeModel(FileObj, HoldPl.OS)).getCompMTime(HoldPl.OS);
            //--Create node (File) Object (self adding with links to RootDriveFolderTree):

            driveFileObjTmp = new FileObjDrive(
                new FileObjDTO(
                    FileObj.getGeneratedName(),//generatedName,
                    FileObj.getTrueName(),//trueFileName,
                    FileObj.getParentID(),//parentID,
                    FileObj.getMyGenParentID(),//myGenParentID,
                    uploadedFile.getId(),//trueID,
                    //String myPerentGenPath,
                    FileObj.getCreatedTime(),//createdTime,
                    uploadedFile.getModifiedTime().getValue(),//modified,
                    FileObj.isIsDuplicate(),//isDuplicate,
                    FileObj.getOldestDuplicateID(), //oldestDuplicateID,
                    FileObj.isIsFolder(),//isFolder,
                    uploadedFile.getMimeType(),//mimeTypeGoog,
                    FileObj.getMimeType()));//mimeType

            FileObj.setActionMarker(ActionMarkers.NeedNoAction);
            //
            //--Delete member from apload list
            //this.presentFileOSAbsentDrive.remove(FileObj.getMySimulatedID());

            //SAVE INFO IN DB
            (new FileModTimeModel(driveFileObjTmp, HoldPl.DRIVE))
                .setFileModTimeReal(modTimeTmp)
                .updMTimeInDB(HoldPl.DRIVE);

        }
        printer.print("///////////////  <<== uploadMissingFoldersAndFiles() END---");
        DBRefresher.pushInfoDBOnDrive();
        return true;
    }
//--------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------

    private boolean downloadMissingFolders() throws IOException
    {
        printer.print("-- IN  downloadMissingFolders() ----");

        DBRefresher.chooseActualVersion();

        Iterator<FileObjDrive> foldersToDownload = Collections.synchronizedList(//IMPORTANT!!! Without will not work with Iterator.Remove()
this.presentDirDriveAbsentOS.stream()
                .filter(id -> !id.equalsIgnoreCase(this.rootGenID))
                .map(Id -> (FileObjDrive) driveTree.getFObjByIDInMyIDHshDirs(Id))
                .collect(Collectors.toList())) //IMPORTANT!!! Without will not work with Iterator.Remove()
            .listIterator();

        Deque<FileObjDrive> stack = new ArrayDeque<>();
        Optional genParByIDOpt;
        String genParIDTmp;
        long modTimeTmp;

        //check this node whether the whole chain of nodes to this node in OS is complete
        boolean findParent = false;
        printer.print(Level.FINER, "-- foldersToDownload.hasNext():");
        while (foldersToDownload.hasNext())
        {
            printer.print(Level.FINE, "downloadMissingFolders()=>foldersToDownload.hasNext()==>> driveDirObj=foldersToDownload.next()");
            FileObjDrive driveDirObj = (FileObjDrive) foldersToDownload.next();

            //stack 
            stack.clear();
            //Download if Parent is Exist and add to stack if not Exist,search Parent:
            findParent = false;
            FileObjOs fileObjOs;
            Path dirPath;

            do
            {
                printer.print(Level.FINEST, "downloadMissingFolders()=>foldersToDownload.hasNext()==>> IN DO while (!findParent)");
                findParent = false;
                //Find simulated parent ID from OS Dir in DRIVE tree

                genParByIDOpt = Optional.ofNullable(rFTreeInOS.getFObjByIDInMyIDHshDirs(driveDirObj.getMyGenParentID()));

                //if our node (Dir) have parents in DRIVE:
                if (genParByIDOpt.isPresent())
                {
                    printer.print(Level.FINEST, "downloadMissingFolders()=>foldersToDownload.hasNext()==>> IN DO while (!findParent)==> genParIDTmp.isPresent()");
                    findParent = true;

                    modTimeTmp = (new FileModTimeModel(driveDirObj, HoldPl.DRIVE)).getCompMTime(HoldPl.DRIVE);
                    //--Create node (Dir) Object (self adding with links to RootOSFolderTree):
                    try
                    {
                        fileObjOs = new FileObjOs(
                            new FileObjDTO(
                                driveDirObj.getGeneratedName(),//generatedName,
                                driveDirObj.getTrueName(),//trueFileName,
                                driveDirObj.getParentID(),//parentID,
                                driveDirObj.getMyGenParentID(),//myGenParentID,
                                driveDirObj.getTrueID(),//trueID,
                                //String myPerentGenPath,
                                driveDirObj.getCreatedTime(),//createdTime,
                                driveDirObj.getModifiedTime(),//modified,
                                driveDirObj.isIsDuplicate(),//isDuplicate,
                                driveDirObj.getOldestDuplicateID(), //oldestDuplicateID,
                                driveDirObj.isIsFolder(),//isFolder,
                                driveDirObj.getMimeTypeGoog(), //mimeTypeGoog,
                                null));//mimeType

                        //
                        //Create folder in OS
                        dirPath = fileObjOs.getMyPath();

                        printer.print(Level.FINEST, String.format("Create node (Dir) Object (self adding with links to RootFolderTreeInOs) \n %s", dirPath.toString()));

                        if (!Files.exists(dirPath))
                        {

                            Files.createDirectories(dirPath);
                            printer.print(Level.FINER, "Directory chain created");

                            //SAVE INFO IN DB
                            new FileModTimeModel(
                                (new FileObjDTO(fileObjOs)).setModified(dirPath.toFile().lastModified()), HoldPl.OS
                            )
                                .setFileModTimeReal(modTimeTmp).updMTimeInDB(HoldPl.OS);
                            //
                        } else
                        {

                            System.out.println("Directory chain already exists");
                        }
                    } catch (UnsupportedEncodingException | NoSuchAlgorithmException e)
                    {
                        printer.print(Level.SEVERE, e.getCause());
                    } catch (IOException e2)
                    {
                        printer.print(Level.SEVERE, e2.getCause());
                    }

                    //
                    driveDirObj.setActionMarker(ActionMarkers.NeedNoAction);

                    //--Delete member from upload list
                    foldersToDownload.remove();
                    this.presentDirDriveAbsentOS.remove(driveDirObj.getMySimulatedID());
                } else //if parent not present -> wait, save in stack
                {
                    printer.print(Level.FINEST, "parent not present -> wait, save in stack");
                    //we add this folder later to DRIVE
                    stack.addFirst(driveDirObj);
                    //level up to Parent-folder. 
                    driveDirObj = (FileObjDrive) driveTree.getFObjByIDInMyIDHshDirs(driveDirObj.getMyGenParentID());
                    // Change OsDir object untill we have found Patent in drive

                }

            } while (!findParent);
            //-----------------------
            // we go down through waiting driveDirObj in stack
            //-----------------------
            printer.print(Level.FINEST, "we go down through waiting driveDirObj in stack while (!stack.isEmpty())");

            while (!stack.isEmpty())
            {
                printer.print(Level.FINER, "IN while (!stack.isEmpty())...");
                driveDirObj = stack.removeFirst();

                modTimeTmp = (new FileModTimeModel(driveDirObj, HoldPl.DRIVE)).getCompMTime(HoldPl.DRIVE);
                try
                {
                    fileObjOs = new FileObjOs(
                        new FileObjDTO(
                            driveDirObj.getGeneratedName(),//generatedName,
                            driveDirObj.getTrueName(),//trueFileName,
                            driveDirObj.getParentID(),//parentID,
                            driveDirObj.getMyGenParentID(),//myGenParentID,
                            driveDirObj.getTrueID(),//trueID,
                            //String myPerentGenPath,
                            driveDirObj.getCreatedTime(),//createdTime,
                            driveDirObj.getModifiedTime(),//modified,
                            driveDirObj.isIsDuplicate(),//isDuplicate,
                            driveDirObj.getOldestDuplicateID(), //oldestDuplicateID,
                            driveDirObj.isIsFolder(),//isFolder,
                            driveDirObj.getMimeTypeGoog(), //mimeTypeGoog,
                            null));//mimeType

                    //
                    //Create folder in OS
                    dirPath = fileObjOs.getMyPath();

                    if (!Files.exists(dirPath))
                    {

                        Files.createDirectories(dirPath);
                        printer.print(Level.FINEST, " Directory chain created in stack-Block");

                        //SAVE INFO IN DB
                        new FileModTimeModel(
                            (new FileObjDTO(fileObjOs)).setModified(dirPath.toFile().lastModified()), HoldPl.OS
                        )
                            .setFileModTimeReal(modTimeTmp).updMTimeInDB(HoldPl.OS);
                        //
                    } else
                    {

                        printer.print(Level.FINEST, " stack-Block: Directory chain already exists");
                    }
                } catch (UnsupportedEncodingException | NoSuchAlgorithmException e)
                {
                    e.printStackTrace();
                } catch (IOException e2)
                {
                    e2.printStackTrace();
                }

                //
                driveDirObj.setActionMarker(ActionMarkers.NeedNoAction);

                //--Delete member from upload list
                this.presentDirDriveAbsentOS.remove(driveDirObj.getMySimulatedID());
            }
            printer.print(Level.FINER, "TO NEXT FOLDER (Iterator)");

        }
        printer.print("END Download missing Folders");
        DBRefresher.pushInfoDBOnDrive();

        return true;
    }
//--------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------

    private boolean downloadMissingFiles() throws IOException
    {
        printer.print(" --IN downloadMissingFiles() --: ");

        DBRefresher.chooseActualVersion();

        Iterator<FileObjDrive> filesToDownload = Collections.synchronizedList(this.presentFileDriveAbsentOS
                .stream()
                .map(Id -> (FileObjDrive) driveTree.getFObjByIDInMyIDHshFiles(Id))
                .collect(Collectors.toList())).listIterator();

        Optional genParByIDOpt;

        FileObjOs fileObjOs;
        Path filePath;
        long modTimeTmp;
        printer.print(Level.FINE, " --filesToDownload.hasNext() --: ");
        while (filesToDownload.hasNext())
        {
            fileObjOs = null;
            filePath = null;
            FileObjDrive driveFileObj = (FileObjDrive) filesToDownload.next();

//            genParByIDOpt = Optional.ofNullable(rFTreeInOS.getFObjByIDInMyIDHshDirs(driveDirObj.getMyGenParentID()));
//
//                genParIDTmp = genParByIDOpt.isPresent() ? rFTreeInOS.getFObjByIDInMyIDHshDirs(driveDirObj.getMyGenParentID()).getMySimulatedID() : null;
            //Find simulated parent ID from OS Dir in DRIVE tree
            genParByIDOpt = Optional.ofNullable((rFTreeInOS.getFObjByIDInMyIDHshDirs(driveFileObj.getMyGenParentID())));
            //if our node (Dir) have parents in DRIVE:
            try
            {
                if (genParByIDOpt.isPresent())
                {
                    modTimeTmp = (new FileModTimeModel(driveFileObj, HoldPl.DRIVE)).getCompMTime(HoldPl.DRIVE);
                    //--Create node (Dir) Object (self adding with links to RootOSFolderTree):
                    try
                    {
                        fileObjOs = new FileObjOs(
                            new FileObjDTO(
                                driveFileObj.getGeneratedName(),//generatedName,
                                driveFileObj.getTrueName(),//trueFileName,
                                driveFileObj.getParentID(),//parentID,
                                driveFileObj.getMyGenParentID(),//myGenParentID,
                                driveFileObj.getTrueID(),//trueID,
                                //String myPerentGenPath,
                                driveFileObj.getCreatedTime(),//createdTime,
                                driveFileObj.getModifiedTime(),//modified,
                                driveFileObj.isIsDuplicate(),//isDuplicate,
                                driveFileObj.getOldestDuplicateID(), //oldestDuplicateID,
                                driveFileObj.isIsFolder(),//isFolder,
                                driveFileObj.getMimeTypeGoog(), //mimeTypeGoog,
                                driveFileObj.getMimeType()));//mimeType

                        //
                        //Create folder in OS
                        filePath = fileObjOs.getMyPath();

                        if (!Files.exists(filePath))
                        {
                            printer.print(String.format("Downloading GeneratedName %s \n with Path %s ....", fileObjOs.getGeneratedName(), filePath.toAbsolutePath().toString()));
                            toolsClases.ToolsDriveFacade.downloadFileFromDriveByID(fileObjOs.getTrueID(), filePath);
                            printer.print(Level.FINE, String.format("File with GeneratedName %s \n with Path %s downloaded", fileObjOs.getGeneratedName(), filePath.toAbsolutePath().toString()));
                            //SAVE INFO IN DB
                            new FileModTimeModel(
                                (new FileObjDTO(fileObjOs)).setModified(filePath.toFile().lastModified()), HoldPl.OS
                            )
                                .setFileModTimeReal(modTimeTmp).updMTimeInDB(HoldPl.OS);
                            //
                        } else
                        {

                            printer.print(Level.INFO, String.format("File GeneratedName %s \n with Path %s \n is already exist !!", fileObjOs.getGeneratedName(), filePath.toAbsolutePath().toString()));
                        }
                    } catch (UnsupportedEncodingException | NoSuchAlgorithmException e)
                    {
                        e.printStackTrace();
                    }

                    //
                    driveFileObj.setActionMarker(ActionMarkers.NeedNoAction);

                    //--Delete member from upload list
                    this.presentFileDriveAbsentOS.remove(driveFileObj.getMySimulatedID());
                } else //if parent not present -> feller
                {
                    throw new IOException("IOException: Before Downloading file it must be parent folder");

                }
            } catch (IOException ioex)
            {
                ioex.printStackTrace();
            }

        }
        printer.print(" --END OF MISSING FILES DOWNLOAD --: ");

        DBRefresher.pushInfoDBOnDrive();

        return true;
    }
//--------------------------------------------------------------------------------------

    private boolean delObjectsFromOsMissingInDrive() throws IOException
    {
        DBRefresher.chooseActualVersion();
        //DELETE FILES
        Iterator<FileObjOs> filesToDelete = Collections.synchronizedList(this.presentFileOSAbsentDrive.stream()
            .filter(id -> !id.equalsIgnoreCase(this.rootGenID))
            .map(Id -> (FileObjOs) rFTreeInOS.getFObjByIDInMyIDHshFiles(Id))
                .filter(fobj -> !fobj.getTrueName().equals(DBRefresher.FILENAME))
                .collect(Collectors.toList())) //IMPORTANT!!! Without will not work with Iterator.Remove()
                .listIterator();
       
        Optional parOpt;
        String genParIDTmp;
        boolean deleted = false;
        Path filePath;
        while (filesToDelete.hasNext())
        {
            FileObjOs osfileObj = filesToDelete.next();
            //Find simulated parent ID from OS Dir in DRIVE tree

            parOpt = Optional.ofNullable(rFTreeInOS.getFObjByIDInMyIDHshDirs(osfileObj.getMyGenParentID()));

            try
            {

                //
                //Create file in OS
                filePath = osfileObj.getMyPath();

                if (Files.exists(filePath))
                {
                    deleted = toolsClases.ToolsDriveFacade.deleteFileInOS(filePath);
                    printer.print(String.format("and with GeneratedName %s \n with Path %s deleted: %b", osfileObj.getGeneratedName(),
                        filePath.toAbsolutePath().toString(),
                        deleted));
                } else
                {

                    System.out.printf("File GeneratedName %s \n with Path %s \n is not exist !!", osfileObj.getGeneratedName(), filePath.toAbsolutePath().toString());
                }

                //DELETE FROM DB
                IModTime.deleteHashFromDB(osfileObj.getMySimulatedID());

            } catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            } catch (IOException e2)
            {
                e2.printStackTrace();
            }
            //
            if (parOpt.isPresent())
            {
                try
                {
                    genParIDTmp = rFTreeInOS.getFObjByIDInMyIDHshDirs(osfileObj.getMyGenParentID()).getMySimulatedID();
                    rFTreeInOS.getInstance().removeFromBaseMyIDHshFilesOS(osfileObj.getMySimulatedID());
                    rFTreeInOS.getInstance().getFObjByIDInMyIDHshDirs(genParIDTmp).removeFromBaseMyIDHshChildFilesOS(osfileObj.getMySimulatedID());
                } catch (Exception ex)
                {
                    ex.printStackTrace();
                }

            }
            //--Delete member from upload list
            filesToDelete.remove();
            this.presentFileOSAbsentDrive.remove(osfileObj.getMySimulatedID());

        }

        //DELETE FOLDERS
        Iterator<FileObjOs> FoldersToDelete = Collections.synchronizedList(this.presentDirOSAbsentDrive.stream()
            .filter(id -> !id.equalsIgnoreCase(this.rootGenID))
            .map(Id -> (FileObjOs) rFTreeInOS.getFObjByIDInMyIDHshDirs(Id)).collect(Collectors.toList())) //IMPORTANT!!! Without will not work with Iterator.Remove()
            .listIterator();

        deleted = false;
        while (filesToDelete.hasNext())
        {
            FileObjOs osDirObj = FoldersToDelete.next();
            //Find simulated parent ID from OS Dir in DRIVE tree

            toolsClases.ToolsDriveFacade.deleteSpecDirObjOSRec(osDirObj, rFTreeInOS, this);
            //Delete from DB
            this.presentDirOSAbsentDrive.stream()
                .filter(id -> !id.equalsIgnoreCase(this.rootGenID))
                .map(Id -> (FileObjOs) rFTreeInOS.getFObjByIDInMyIDHshDirs(Id))
                .forEach(fobj -> IModTime.deleteHashFromDB(fobj.getMySimulatedID()));

        }

        DBRefresher.pushInfoDBOnDrive();
        return true;
    }
//--------------------------------------------------------------------------------------

    private boolean deleteInDriveExstSpecFiles() throws IOException
    {
        DBRefresher.chooseActualVersion();
        printer.print("Delete specific files in Drive...");
        Iterator<FileObjDrive> filesToDelete = Collections.synchronizedList(this.presentFileDriveAbsentOS
                .stream().
                filter(id -> !id.equalsIgnoreCase(this.rootGenID))
                .map(Id -> (FileObjDrive) driveTree.getFObjByIDInMyIDHshFiles(Id))
                .collect(Collectors.toList()))
            .listIterator();

        Optional parOpt;
        String genParIDTmp;

        boolean deleted = false;
        FileObjDrive drivefileObj;
        while (filesToDelete.hasNext())
        {
            drivefileObj = filesToDelete.next();
            //Find simulated parent ID in DRIVE tree

//            genParIDTmp = Optional.ofNullable(((FileObjDrive) driveTree.getFObjByIDInMyIDHshDirs(drivefileObj.getMyGenParentID())).getMySimulatedID());
            parOpt = Optional.ofNullable(rFTreeInOS.getFObjByIDInMyIDHshDirs(drivefileObj.getMyGenParentID()));

            Path filePath;

            try
            {

                //
                //Create file path in OS
                filePath = drivefileObj.getMyPath();

                deleted = toolsClases.ToolsDriveFacade.deleteFileInDrive(drivefileObj.getTrueID());
                printer.print(Level.FINE, String.format("File with GeneratedName %s \n with Path %s deleted: %b in Drive....", drivefileObj.getGeneratedName(),
                    filePath.toAbsolutePath().toString(),
                    deleted));

                //DELETE FROM DB
                IModTime.deleteHashFromDB(drivefileObj.getMySimulatedID());

            } catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            } catch (IOException e2)
            {
                e2.printStackTrace();
            }
            //
            if (parOpt.isPresent())
            {
                try
                {
                    genParIDTmp = driveTree.getFObjByIDInMyIDHshDirs(drivefileObj.getMyGenParentID()).getMySimulatedID();
                    driveTree.getInstance().removeFromBaseMyIDHshFilesOS(drivefileObj.getMySimulatedID());
                    driveTree.getInstance().getFObjByIDInMyIDHshDirs(genParIDTmp).removeFromBaseMyIDHshChildFilesOS(drivefileObj.getMySimulatedID());
                } catch (Exception ex)
                {
                    ex.printStackTrace();
                }

            }
            //--Delete member from upload list
            filesToDelete.remove();
            this.presentFileDriveAbsentOS.remove(drivefileObj.getMySimulatedID());

        }

        //DELETE FOLDERS
        Iterator FoldersToDelete = Collections.synchronizedList(this.presentDirDriveAbsentOS.stream().
                filter(id -> !id.equalsIgnoreCase(this.rootGenID))
                .map(Id -> (FileObjDrive) driveTree.getFObjByIDInMyIDHshDirs(Id))
                .collect(Collectors.toList()))
            .listIterator();

        deleted = false;
        while (filesToDelete.hasNext())
        {
            FileObjDrive driveDirObj = (FileObjDrive) FoldersToDelete.next();
            //Find simulated parent ID from DRIVE file in DRIVE tree

            toolsClases.ToolsDriveFacade.deleteSpecDirObjDriveRec(driveDirObj, driveTree, this);

            //DELETE FROM DB
            this.presentFileDriveAbsentOS
                .stream().
                filter(id -> !id.equalsIgnoreCase(this.rootGenID))
                .map(Id -> (FileObjDrive) driveTree.getFObjByIDInMyIDHshFiles(Id))
                .forEach(dObj -> IModTime.deleteHashFromDB(dObj.getMySimulatedID()));

        }

        DBRefresher.pushInfoDBOnDrive();
        return true;
    }
//--------------------------------------------------------------------------------------

    private boolean updateOldFilesInOs() throws IOException
    {
        DBRefresher.chooseActualVersion();
        printer.print("START OF UPDATING FILE in OS");
        Iterator<FileObjDrive> filesFromUpdate = Collections.synchronizedList(this.idFilesExistBoth.stream()
            .filter(fileId ->
            {
                try
                {
                    return driveTree.getInstance().getFObjByIDInMyIDHshFiles(fileId)
                        .getActionMarker().equals(ActionMarkers.MustUPDATE_Opponennt);

                } catch (Exception ex)
                {
                    ex.printStackTrace();
                }
                return false;
            }).map(Id -> (FileObjDrive) driveTree.getFObjByIDInMyIDHshFiles(Id))
            .collect(Collectors.toList())).listIterator();

        Optional parOpt;
        FileObjDrive driveFileObj = null;
        FileObjOs newFileObjOs = null;
        long modTimeTmp;

        while (filesFromUpdate.hasNext())
        {
            driveFileObj = filesFromUpdate.next();

            newFileObjOs = null;
            Path filePath;

            //Find simulated parent ID from DRIVE Dir in OS tree
            parOpt = Optional.ofNullable(rFTreeInOS.getFObjByIDInMyIDHshDirs(driveFileObj.getMyGenParentID()));

            //if our node (Dir) have parents in DRIVE:
            try
            {
                if (parOpt.isPresent())
                {

                    //--Create node (Dir) Object (self adding with links to RootOSFolderTree):
                    try
                    {

                        //
                        //Create File in OS
                        filePath = driveFileObj.getMyPath();

                        if (Files.exists(filePath))
                        {
                            printer.print(String.format("Updating IN OS GeneratedName -------->          %s \n with Path %s ....", driveFileObj.getGeneratedName(), filePath.toAbsolutePath().toString()));
                            toolsClases.ToolsDriveFacade.downloadFileFromDriveByID(driveFileObj.getTrueID(), filePath);

                            /////////////
                            if (parOpt.isPresent())
                            {
                                try
                                {
                                    rFTreeInOS.getInstance().removeFromBaseMyIDHshFilesOS(driveFileObj.getMySimulatedID());
                                    rFTreeInOS.getInstance()
                                        .getFObjByIDInMyIDHshDirs((rFTreeInOS.getFObjByIDInMyIDHshDirs(driveFileObj.getMyGenParentID())).getMySimulatedID())
                                        .removeFromBaseMyIDHshChildFilesOS(driveFileObj.getMySimulatedID());
                                } catch (Exception ex)
                                {
                                    ex.printStackTrace();
                                }

                            }
                            ////////////////
                            try
                            {
                                modTimeTmp = (new FileModTimeModel(driveFileObj, HoldPl.DRIVE)).getCompMTime(HoldPl.DRIVE);

                                newFileObjOs = new FileObjOs(
                                    new FileObjDTO(
                                        driveFileObj.getGeneratedName(),//generatedName,
                                        driveFileObj.getTrueName(),//trueFileName,
                                        driveFileObj.getParentID(),//parentID,
                                        driveFileObj.getMyGenParentID(),//myGenParentID,
                                        driveFileObj.getTrueID(),//trueID,
                                        //String myPerentGenPath,
                                        driveFileObj.getCreatedTime(),//createdTime,
                                        driveFileObj.getModifiedTime(),//modified,
                                        driveFileObj.isIsDuplicate(),//isDuplicate,
                                        driveFileObj.getOldestDuplicateID(), //oldestDuplicateID,
                                        driveFileObj.isIsFolder(),//isFolder,
                                        driveFileObj.getMimeTypeGoog(), //mimeTypeGoog,
                                        driveFileObj.getMimeType()));//mimeType

                                /////SAVE INFO IN DB
                                new FileModTimeModel(
                                    (new FileObjDTO(newFileObjOs)).setModified(filePath.toFile().lastModified()), HoldPl.OS
                                )
                                    .setFileModTimeReal(modTimeTmp).updMTimeInDB(HoldPl.OS);
                                //////
                            } catch (NoSuchAlgorithmException ex)
                            {
                                ex.printStackTrace();
                            }

                            newFileObjOs.setActionMarker(ActionMarkers.NeedNoAction);
                            driveFileObj.setActionMarker(ActionMarkers.NeedNoAction);
                            printer.print(Level.FINE, String.format("File with GeneratedName %s \n with Path %s updatet in OS", newFileObjOs.getGeneratedName(), filePath.toAbsolutePath().toString()));
                        } else
                        {

                            printer.print(String.format("File GeneratedName %s \n with Path %s \n is Not exist for Updating!!", driveFileObj.getGeneratedName(), filePath.toAbsolutePath().toString()));
                        }
                    } catch (UnsupportedEncodingException e)
                    {
                        e.printStackTrace();
                    }

                    //
                    driveFileObj.setActionMarker(ActionMarkers.NeedNoAction);

                    //--Delete member from update list
                    filesFromUpdate.remove();
                    this.idFilesExistBoth.remove(driveFileObj.getMySimulatedID());
                } else //if parent not present -> feller
                {
                    throw new IOException("IOException: Before Downloading file it must be parent folder");

                }
            } catch (IOException ioex)
            {
                ioex.printStackTrace();
            }

        }
        printer.print(Level.FINE, "END OF UPDATING FILE in OS");

        DBRefresher.pushInfoDBOnDrive();
        return true;
    }
//--------------------------------------------------------------------------------------

    private boolean updateOldFilesInDrive() throws IOException
    {
        DBRefresher.chooseActualVersion();

        Iterator<FileObjDrive> filesToUpdate = Collections.synchronizedList(this.idFilesExistBoth.stream()
                .filter(fileId ->
                {
                    try
                    {
                        return driveTree.getInstance().getFObjByIDInMyIDHshFiles(fileId)
                            .getActionMarker().equals(ActionMarkers.MustBeUpgreaded_self);

                    } catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                    return false;
                }).map(Id -> (FileObjDrive) driveTree.getFObjByIDInMyIDHshFiles(Id)).collect(Collectors.toList())
        )
            .listIterator();

        Optional parOpt;
        String parId;
        FileObjDrive driveFileObj = null;
        AbstrFolderTree driveParentObj = null;

        File driveFileNew = null;
        java.io.File fileFromOS;

        Path filePath;

        FileObjOs fileObjOs = null;
        FileObjDrive driveFileObjTmp;
        long modTimeTmp;

        while (filesToUpdate.hasNext())
        {
            driveFileObj = filesToUpdate.next();

            //Find simulated parent ID from OS Dir in DRIVE tree
            parOpt = Optional.ofNullable((driveTree.getFObjByIDInMyIDHshDirs(driveFileObj.getMyGenParentID())));

            //if our node (Dir) have parents in DRIVE:
            try
            {
                if (parOpt.isPresent())
                {

                    //--Create node (FileFromOS) Object:
                    try
                    {

                        //
                        //Create folder in OS
                        fileObjOs = (FileObjOs) rFTreeInOS.getFObjByIDInMyIDHshFiles(driveFileObj.getMySimulatedID());
                        filePath = fileObjOs.getMyPath();
                        fileFromOS = filePath.toFile();

                        if (Files.exists(filePath))
                        {
                            printer.print(String.format("Updating Drive GeneratedName %s \n from Path %s ....", driveFileObj.getGeneratedName(), filePath.toAbsolutePath().toString()));

                            driveFileNew = toolsClases.ToolsDriveFacade.updateFileInDriveByID(
                                driveFileObj.getTrueID(),
                                driveFileObj.getTrueName(),
                                driveFileObj.getMimeTypeGoog(),//fileObjOs.getMimeType(),
                                fileFromOS);

                            /////////////
                            if (driveFileObj.getMyGenParentID() != null && driveParentObj != null)
                            {
                                try
                                {
                                    driveTree.getInstance().removeFromBaseMyIDHshFilesOS(driveFileObj.getMySimulatedID());
                                    driveTree.getInstance().getFObjByIDInMyIDHshDirs(driveFileObj.getMyGenParentID()).removeFromBaseMyIDHshChildFilesOS(driveFileObj.getMySimulatedID());
                                } catch (Exception ex)
                                {
                                    ex.printStackTrace();
                                }

                            }
                            ////////////////
                            try
                            {
                                modTimeTmp = (new FileModTimeModel(fileObjOs, HoldPl.OS)).getCompMTime(HoldPl.OS);
                                driveFileObjTmp = new FileObjDrive(
                                    new FileObjDTO(
                                        driveFileObj.getGeneratedName(),//generatedName,
                                        driveFileObj.getTrueName(),//trueFileName,
                                        driveFileObj.getParentID(),//parentID,
                                        driveFileObj.getMyGenParentID(),//myGenParentID,

                                        driveFileNew.getId(),//trueID,                                        
                                        //String myPerentGenPath,
                                        fileObjOs.getCreatedTime(),//createdTime,
                                        driveFileNew.getModifiedTime().getValue(),//modified,
                                        driveFileObj.isIsDuplicate(),//isDuplicate,
                                        driveFileObj.getOldestDuplicateID(), //oldestDuplicateID,
                                        false,//isFolder,
                                        driveFileObj.getMimeTypeGoog(), //mimeTypeGoog,
                                        driveFileObj.getMimeType()));

                                driveFileObjTmp.setActionMarker(ActionMarkers.NeedNoAction);//mimeType

                                //SAVE INFO IN DB
                                (new FileModTimeModel(driveFileObjTmp, HoldPl.DRIVE))
                                    .setFileModTimeReal(modTimeTmp)
                                    .updMTimeInDB(HoldPl.DRIVE);

                            } catch (NoSuchAlgorithmException ex)
                            {
                                ex.printStackTrace();
                            }

                            printer.print(Level.FINE, String.format("File with GeneratedName %s \n with Path %s Uploaded toUpdate", fileObjOs.getGeneratedName(), filePath.toAbsolutePath().toString()));
                        } else
                        {

                            printer.print(String.format("File GeneratedName %s \n with Path %s \n is Not exist for use it as updating sourse!!", driveFileObj.getGeneratedName(), filePath.toAbsolutePath().toString()));
                        }
                    } catch (UnsupportedEncodingException e)
                    {
                        e.printStackTrace();
                    } catch (IOException e2)
                    {
                        e2.printStackTrace();
                    }

                    //
                    driveFileObj.setActionMarker(ActionMarkers.NeedNoAction);
                    fileObjOs.setActionMarker(ActionMarkers.NeedNoAction);
                    //--Delete member from update list
                    filesToUpdate.remove(); //<<==from Iterator (Important Before next line)
                    this.idFilesExistBoth.remove(driveFileObj.getMySimulatedID());

                } else //if parent not present -> feller
                {
                    throw new IOException("IOException: Before Downloading file it must be parent folder");

                }
            } catch (IOException ioex)
            {
                ioex.printStackTrace();
            }

        }
        printer.print("END OF UPDATING");

        DBRefresher.pushInfoDBOnDrive();
        return true;
    }
//--------------------------------------------------------------------------------------
    //******************************************************************************
//chooseTreesAndSyncType

    public static Synchronisator chooseTreesAndSyncType(RootDriveFolderTree dft, RootOSFolderTree rft, Path rtDir)
        throws UnsupportedEncodingException, NoSuchAlgorithmException, IOException
    {
        Synchronisator localInstance = instance;
        if (localInstance == null)
        {
            synchronized (Synchronisator.class)
            {
                localInstance = instance;
                if (localInstance == null)
                {
                    instance = localInstance = getInstance(dft, rft, rtDir);
                    return localInstance;
                }
            }
        }
        return instance;
    }
//-----------------------------------------------

    public static Synchronisator getInstance()

    {
        Synchronisator localInstance = instance;
        if (localInstance == null)
        {
            synchronized (Synchronisator.class)
            {
                localInstance = instance;
                if (localInstance == null)
                {
                    instance = new Synchronisator();
                    localInstance = instance;
                }
            }
        }
        return localInstance;
    }

    public HashSet<String> getPresentDirOSAbsentDrive()
    {
        return this.presentDirOSAbsentDrive;
    }

    public HashSet<String> getPresentDirDriveAbsentOS()
    {
        return this.presentDirDriveAbsentOS;
    }

    //--------------------------------------
    public static Synchronisator getInstance(RootDriveFolderTree dft, RootOSFolderTree rft, Path rtDir)
        throws NoSuchAlgorithmException, IOException

    {
        Synchronisator localInstance = instance;

        if (localInstance == null)
        {
            synchronized (Synchronisator.class)
            {
                localInstance = Synchronisator.instance;
                if (localInstance == null)
                {
                    instance = new Synchronisator(dft, rft, rtDir);
                    localInstance = instance;
                }
            }
        }
        return localInstance;
    }
//*****************************************************************************

    public static void main(String[] args) throws UnsupportedEncodingException, NoSuchAlgorithmException, IOException
    {

    }
}

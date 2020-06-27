package toolsClases;

import bases.FileObjDrive;
import bases.RootDriveFolderTree;
import bases.FileObjOs;
import bases.RootOSFolderTree;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import synchronisator.Synchronisator;

/**
 *
 * @author Shkirmantsev
 */
public class DirObjRecRemoover
{

    /**
     * Delete recursively FilefolderObjectsInOS Directories must have only
     * directories AS Content
     *
     * @param osDirObj,rFTreeInOS
     * @param rFTreeInOS
     * @param sThis
     */
    public static void deleteSpecDirObjOSRec(FileObjOs osDirObj, RootOSFolderTree rFTreeInOS, Synchronisator sThis)
    {
        //Find simulated parent ID from OS Dir in Drive tree

        Optional<String> genParIDTmp = Optional.ofNullable(((FileObjOs) rFTreeInOS.getFObjByIDInMyIDHshDirs(osDirObj.getMyGenParentID())).getMySimulatedID());
        Path filePath;
        boolean deleted;

        {
            if (!osDirObj.getBaseMyIDHshChildDirsSet().isEmpty())
            {
                osDirObj.getBaseMyIDHshChildDirsSet().forEach(dirID ->
                {
                    if (dirID != null)
                    {
                        DirObjRecRemoover.deleteSpecDirObjOSRec((FileObjOs) rFTreeInOS.getFObjByIDInMyIDHshDirs(dirID), rFTreeInOS, sThis);
                    }
                });
            }
        }

        try
        {

            //
            //Create folder in OS
            filePath = osDirObj.getMyPath();

            if (Files.exists(filePath))
            {
                System.out.printf("Deleting GeneratedName %s \n with Path %s ....", osDirObj.getGeneratedName(), filePath.toAbsolutePath().toString());
                deleted = toolsClases.ToolsDriveFacade.deleteFileInOS(filePath);
                System.out.printf("Folder with GeneratedName %s \n with Path %s deleted: %b", osDirObj.getGeneratedName(),
                    filePath.toAbsolutePath().toString(),
                    deleted);
            } else
            {

                System.out.printf("Folder GeneratedName %s \n with Path %s \n is not exist !!", osDirObj.getGeneratedName(), filePath.toAbsolutePath().toString());
            }
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        } catch (IOException e2)
        {
            e2.printStackTrace();
        }
        //
        if (genParIDTmp.isPresent())
        {
            try
            {
                rFTreeInOS.getInstance().getFObjByIDInMyIDHshDirs(genParIDTmp.get()).removeFromBaseMyIDHshDirsOS(osDirObj.getMySimulatedID());
                rFTreeInOS.getInstance().getFObjByIDInMyIDHshDirs(genParIDTmp.get()).removeFromMyIDHshChildDirsOS(osDirObj.getMySimulatedID());
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }

        }
        //--Delete member from upload list
        sThis.getPresentDirOSAbsentDrive().remove(osDirObj.getMySimulatedID());

    }

    /**
     * Delete recursively FilefolderObjectsInDrive Directories must have only
     * directories AS Content
     *
     * @param driveDirObj
     * @param driveTree
     * @param sThis
     */
    public static void deleteSpecDirObjDriveRec(FileObjDrive driveDirObj, RootDriveFolderTree driveTree, Synchronisator sThis)
    {
        //Find simulated parent ID from OS Dir in Drive tree

        Optional<String> genParIDTmp = Optional.ofNullable(((FileObjDrive) driveTree.getFObjByIDInMyIDHshDirs(driveDirObj.getMyGenParentID())).getMySimulatedID());
        Path filePath;
        String trueObjID;
        boolean deleted;

        {
            if (!driveDirObj.getBaseMyIDHshChildDirsSet().isEmpty())
            {
                driveDirObj.getBaseMyIDHshChildDirsSet().forEach(dirID ->
                {
                    if (dirID != null)
                    {
                        DirObjRecRemoover.deleteSpecDirObjDriveRec((FileObjDrive) driveTree.getFObjByIDInMyIDHshDirs(dirID), driveTree, sThis);
                    }
                });
            }
        }

        try
        {

            //
            
            trueObjID = driveDirObj.getTrueID();

            if (trueObjID!=null)
            {
                System.out.printf("Deleting GeneratedName %s \n with Drive Path %s ....", driveDirObj.getGeneratedName(), driveDirObj.getMyPath());
                deleted = toolsClases.ToolsDriveFacade.deleteFileInDrive(trueObjID);
                System.out.printf("Folder with GeneratedName %s \n with Path %s deleted: %b", driveDirObj.getGeneratedName(),
                    driveDirObj.getMyPath(),
                    deleted);
            } else
            {

                System.out.printf("Folder GeneratedName %s \n with ID %s \n is not exist !!", driveDirObj.getGeneratedName(), trueObjID);
            }
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        } catch (IOException e2)
        {
            e2.printStackTrace();
        }
        //
        if (genParIDTmp.isPresent())
        {
            try
            {
                driveTree.getInstance().getFObjByIDInMyIDHshDirs(genParIDTmp.get()).removeFromBaseMyIDHshDirsOS(driveDirObj.getMySimulatedID());
                driveTree.getInstance().getFObjByIDInMyIDHshDirs(genParIDTmp.get()).removeFromMyIDHshChildDirsOS(driveDirObj.getMySimulatedID());
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }

        }
        //--Delete member from upload list
        sThis.getPresentDirDriveAbsentOS().remove(driveDirObj.getMySimulatedID());

    }
}

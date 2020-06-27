/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package synchronisator;

import bases.ActionMarkers;
import bases.FileObjDrive;
import bases.RootDriveFolderTree;
import bases.FileObjOs;
import bases.HoldPl;
import bases.RootOSFolderTree;
import dto.FileModTimeModel;
import java.util.Objects;
import java.util.logging.Level;
import toolsClases.MyLogPrinter;

/**
 *
 * @author Shkirmantsev
 */
public class Exchanger
{

    ////////////// HELP FOR PRINTING /////////////////
    static MyLogPrinter printer = new MyLogPrinter(Exchanger.class, Level.OFF, Level.OFF);
    /////////////////////////////////////////////////

    public static void exchangeApdateAll(FileObjOs fDirObj, FileObjDrive driveFDirObj, RootOSFolderTree rFTreeInOS, RootDriveFolderTree driveTree, String myHashId)
    {
        printer.print( "IN exchangeApdateAll: ");
        //if Dir is NOT Root:
        if (!rFTreeInOS.getMySimulatedID().equals(myHashId) && !driveTree.getMySimulatedID().equals(myHashId))
        {
            //Exchange true ID from DRIVE Object to Os Object
            fDirObj.setTrueID(driveFDirObj.getTrueID());
            fDirObj.setTrueParentID(driveFDirObj.getParentID());
            printer.print(Level.FINEST, "exchangeApdateAll: FileObjOs fDirObj --setted TrueID and True Parent ID");
            //
            //Exchange MimeType
            fDirObj.setMimeTypeGoog(driveFDirObj.getMimeTypeGoog());
            driveFDirObj.setMimeType(fDirObj.getMimeType());
            //Exchange Duplicate IDs from DRIVE Object to Os Object
            fDirObj.setIsDuplicate(driveFDirObj.isIsDuplicate());
            fDirObj.setOldestDuplicateID(driveFDirObj.getOldestDuplicateID());
            //--------------------------------------------------
            //Exchange Action Options depend from Date of File
            //
            //if DRIVE has new or updated files:
            long driveModifyTime=(new FileModTimeModel(driveFDirObj,HoldPl.DRIVE)).getCompMTime(HoldPl.DRIVE);
            long osModifyTime=(new FileModTimeModel(fDirObj,HoldPl.OS)).getCompMTime(HoldPl.OS);
            
            if ( driveModifyTime>osModifyTime )
            {
                driveFDirObj.setActionMarker(ActionMarkers.MustUPDATE_Opponennt);
                fDirObj.setActionMarker(ActionMarkers.MustBeUpgreaded_self);
            } else if (driveModifyTime < osModifyTime)
            {
                driveFDirObj.setActionMarker(ActionMarkers.MustBeUpgreaded_self);
                fDirObj.setActionMarker(ActionMarkers.MustUPDATE_Opponennt);
            } else
            {
                driveFDirObj.setActionMarker(ActionMarkers.NeedNoAction);
                fDirObj.setActionMarker(ActionMarkers.NeedNoAction);
            }
        }
        //

    }

    public static void exchangeMainDrive(FileObjOs fDirObj, FileObjDrive driveFDirObj, RootOSFolderTree rFTreeInOS, RootDriveFolderTree driveTree, String myHashId)
    {

        //if Dir is NOT Root:
        if (!rFTreeInOS.getMySimulatedID().equals(myHashId) && !driveTree.getMySimulatedID().equals(myHashId))
        {
            //Exchange true ID from DRIVE Object to Os Object
            fDirObj.setTrueID(driveFDirObj.getTrueID());
            fDirObj.setTrueParentID(driveFDirObj.getParentID());
            //
            //Exchange MimeType
            fDirObj.setMimeTypeGoog(driveFDirObj.getMimeTypeGoog());
            driveFDirObj.setMimeType(fDirObj.getMimeType());
            //Exchange Duplicate IDs from DRIVE Object to Os Object
            fDirObj.setIsDuplicate(driveFDirObj.isIsDuplicate());
            fDirObj.setOldestDuplicateID(driveFDirObj.getOldestDuplicateID());
            //--------------------------------------------------
            //Exchange Action Options depend from Date of File
            //
             long driveModifyTime=(new FileModTimeModel(driveFDirObj,HoldPl.DRIVE)).getCompMTime(HoldPl.DRIVE);
            long osModifyTime=(new FileModTimeModel(fDirObj,HoldPl.OS)).getCompMTime(HoldPl.OS);
            
            //if DRIVE has new or updated files:
            if ((driveModifyTime > osModifyTime) ||(driveModifyTime < osModifyTime))
            {
                driveFDirObj.setActionMarker(ActionMarkers.MustUPDATE_Opponennt);
                fDirObj.setActionMarker(ActionMarkers.MustBeUpgreaded_self);
            } else
            {
                driveFDirObj.setActionMarker(ActionMarkers.NeedNoAction);
                fDirObj.setActionMarker(ActionMarkers.NeedNoAction);
            }
        }

    }

    public static void exchangeMainOS(FileObjOs fDirObj, FileObjDrive driveFDirObj, RootOSFolderTree rFTreeInOS, RootDriveFolderTree driveTree, String myHashId)
    {
       
        //if Dir is NOT Root:
        if (!rFTreeInOS.getMySimulatedID().equals(myHashId) && !driveTree.getMySimulatedID().equals(myHashId))
        {   
            //Exchange true ID from DRIVE Object to Os Object
            fDirObj.setTrueID(driveFDirObj.getTrueID());
            fDirObj.setTrueParentID(driveFDirObj.getParentID());
            //
            //Exchange MimeType
            fDirObj.setMimeTypeGoog(driveFDirObj.getMimeTypeGoog());
            driveFDirObj.setMimeType(fDirObj.getMimeType());
            //Exchange Duplicate IDs from DRIVE Object to Os Object
            fDirObj.setIsDuplicate(driveFDirObj.isIsDuplicate());
            fDirObj.setOldestDuplicateID(driveFDirObj.getOldestDuplicateID());
            //--------------------------------------------------
            //Exchange Action Options depend from Date of File
            //
             long driveModifyTime=(new FileModTimeModel(driveFDirObj,HoldPl.DRIVE)).getCompMTime(HoldPl.DRIVE);
             long osModifyTime=(new FileModTimeModel(fDirObj,HoldPl.OS)).getCompMTime(HoldPl.OS);
            //if DRIVE has new or updated files:
            
            if ((driveModifyTime < osModifyTime) ||(driveModifyTime > osModifyTime))
            {
                driveFDirObj.setActionMarker(ActionMarkers.MustBeUpgreaded_self);
                fDirObj.setActionMarker(ActionMarkers.MustUPDATE_Opponennt);
            }
            else 
            {
                driveFDirObj.setActionMarker(ActionMarkers.NeedNoAction);
                fDirObj.setActionMarker(ActionMarkers.NeedNoAction);
            }
        }

    }

}

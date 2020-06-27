
package bases;


import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.nio.file.Path;

import java.security.NoSuchAlgorithmException;

import java.util.Optional;

import java.util.logging.Level;

import toolsClases.MyLogPrinter;


/**
 *
 * @author Shkirmantsev
 */
public final class FileObjDrive extends AbstrFolderTree implements IFolderTree
{
    ////////////// HELP FOR PRINTING /////////////////
     static MyLogPrinter printer=new MyLogPrinter(FileObjDrive.class,Level.OFF,Level.WARNING);    
    /////////////////////////////////////////////////
    

    private boolean isThissynchronized; //TO do: Synchronised
   
    final public String myGenParentID;
    


    final private Long createdTime;

    
    final private Long modified;

    private boolean isDuplicate = false;  //there is one more Older instanse in Dir, this is isDuplicate          //  <--------TO do: Synchronised
    private String oldestDuplicateID;

    final private String myPerentGenPath;
//    final private String myGenPath;
//    final private boolean isFolder;

    final private String mimeTypeGoog;   
    private String mimeType;
    


    //************************
    //    CONSTRUCTOR //
    //************************
    public FileObjDrive(
        FileObjDTO fileDto

    ) throws UnsupportedEncodingException, NoSuchAlgorithmException
    {   super();
        this.isMyIDAddedInBases=false;
        this.trueName =fileDto.getTrueFileName();
        this.isDuplicate =fileDto.isIsDuplicate();
        this.generatedName = generateGeneratedName(
            fileDto.getTrueFileName(),
            fileDto.isIsDuplicate(),
            fileDto.getTrueID() );

        //
        renewParentID(fileDto.getParentID());
        //
        this.myGenParentID = fileDto.getMyGenParentID();
        this.myPerentGenPath = generatePerentGenPath(fileDto.getMyGenParentID()); //<=it peeck from Drive IFolderTree up

        this.setMyPath(generateTrueMyPath());  // <<==True Path like in OS
        //
        renewTrueID(fileDto.getTrueID());
        //        
        
        this.createdTime = fileDto.getCreatedTime();
        this.modified = fileDto.getModified();

        this.oldestDuplicateID = fileDto.getOldestDuplicateID();
        this.isFolder = fileDto.isIsFolder();
        this.mimeTypeGoog = fileDto.getMimeTypeGoog();
        this.mimeType = fileDto.getMimeType();
        
        //
        //**************************
        //---- DINAMIC Changes ----
        //
        this.myGenPath = this.myPerentGenPath + File.separator + this.generatedName;
        
        if (!ParentIsInMainBases())
        {
            throw new ExceptionInInitializerError(String.format("FileObject: %s Path: %s \n"
                + " There are no Parent from Node in main Bases during building"
                + " in constructor ", this.generatedName, this.myGenPath));
        }

        this.mySimulatedID = toolsClases.ToolsDriveFacade.getMD5HsFromString(this.myGenPath);
//        this.isDuplicate = !this.generatedName.equals(this.trueName);
        /////////////////////////////
        ///---- !!!! ADDING IN BASES !!!----s
        this.isMyIDAddedInBases = addThisToBases();
        //-------------------------------------------
        // if building from GoogleTree Class (synchronisation):
        boolean isTrueIDAddedInBases = trueID != null ? addThisToTrueIDBases() : false;
        //--------------------------------------------
        /////////////////////////////
        //*********************************************
        //
    }

    

    ////////////////////////////////////////////////////////////////////////////
    //
    synchronized private boolean addThisToBases()
    {
        if (!isFolder)
        {
            try
            {
                printer.print(Level.FINER,this.getGeneratedName() + ", adding in File mainBase...");
                RootDriveFolderTree.getInstance().addToBaseMyIDHshFilesOS(this.mySimulatedID, this);
                return addThisToParentMyIDBase();
            }
            catch (UnsupportedEncodingException | NoSuchAlgorithmException ee)
            {
                return false;
            }
        }
        else
        {
            try
            {
                printer.print(Level.FINER,this.getGeneratedName() + ", " + "adding me in Folder mainBase...");
                printer.print(Level.FINER,"adding ID: " + this.mySimulatedID);
                RootDriveFolderTree.getInstance().addToBaseMyIDHshDirsOS(this.mySimulatedID, this);
                return addThisToParentMyIDBase();
            }
            catch (UnsupportedEncodingException | NoSuchAlgorithmException ee)
            {
                return false;
            }
        }

    }
    //--------------------------------------------------------------------------

    //--------------------------------------------------------------------------
    private boolean addThisToTrueIDBases()
    {
        if (!isFolder)
        {
            try
            {
                RootDriveFolderTree.getInstance().addToBaseIDHshFilesOS(this.trueID, this);

            }
            catch (UnsupportedEncodingException | NoSuchAlgorithmException ee)
            {
                return false;
            }
        }
        else
        {
            try
            {
                RootDriveFolderTree.getInstance().addToBaseIDHshDirsOS(this.getTrueID(), this);
            }
            catch (UnsupportedEncodingException | NoSuchAlgorithmException ee)
            {
                return false;
            }
        }

        return true;

    }

    //-----------------------------------
     @Override
    public boolean isDuplicate(){return this.isDuplicate;}
    @Override
    public <F extends AbstrFolderTree> boolean addToBaseMyIDHshChildFilesOS(String myIDHshFile, F fobj)
    {

        return (java.util.Optional.ofNullable((F) this.baseMyIDHshChildFiles.put(myIDHshFile, (FileObjDrive) fobj)).isPresent());

    }

    @Override
    public <F extends AbstrFolderTree> boolean addTobaseMyIDHshChildDirsOS(String myIDHshFolder, F fobj)
    {

        return (java.util.Optional.ofNullable((F) this.baseMyIDHshChildDirs.put(myIDHshFolder, (FileObjDrive) fobj)).isPresent());
    }

    //-----------------------------------
    private boolean addThisToParentMyIDBase()
    {
        if (!this.isFolder)
        {
            try
            {
                printer.print(Level.FINER,this.getGeneratedName() + ", " + "addThisToParentMyIDBase MyIDHshChildFilesOS(): ");
                printer.print(Level.FINER,"this.myGenPath: " + this.myGenPath);
                printer.print(Level.FINER,"this.myPerentGenPath: " + this.myPerentGenPath);
                //System.out.println("parent generated name in base:"+RootDriveFolderTree.getInstance().getDirFileObjOsByMyID(this.myGenParentID));

                RootDriveFolderTree.getInstance().getFObjByIDInMyIDHshDirs(this.myGenParentID).addToBaseMyIDHshChildFilesOS(this.mySimulatedID, this);
                //DriveFolderTree.getInstance().baseMyIDHshFilesOS.get(this.parentID);
            }
            catch (UnsupportedEncodingException | NoSuchAlgorithmException ee)
            {
                return false;
            }
        }
        else
        {
            try
            {
                RootDriveFolderTree.getInstance().getFObjByIDInMyIDHshDirs(this.myGenParentID).addTobaseMyIDHshChildDirsOS(this.mySimulatedID, this);
            }
            catch (UnsupportedEncodingException | NoSuchAlgorithmException ee)
            {
                return false;
            }
        }

        return true;
    }

    //-----------------------------------
    ////////////////////////////////////
    //
    public final void renewTrueID(Object trueID)
    {
        if (trueID == null)
        {
            this.trueID = null;
        }
        if (trueID instanceof String)
        {
            this.trueID = (String) trueID;

        }
        if (trueID instanceof Integer)
        {
            this.trueID = ((Integer) trueID).toString();

        }

    }

    //
    //-----------------------------------
    public final void renewParentID(Object trueParentID)
    {

        if (trueParentID == null)
        {
            this.parentID = null;
        }
        if (trueParentID instanceof String)
        {
            this.parentID = (String) trueParentID;

        }
        if (trueParentID instanceof Integer)
        {
            this.parentID = ((Integer) trueParentID).toString();

        }
    }
    //------------------------------------------------------------------------
    @Override
    public boolean setTrueID(String trueID)
    {
        if (trueID != null && trueID instanceof String)
        {
            this.trueID = trueID;

        }
        return true;
    }

    //------------------------------------
    //--V1 in AbstrFolderTree
    //--V2
    public String generateGeneratedName(Path osPathToFile)
    {
        return this.isDuplicate ? (osPathToFile.getFileName().toString() + this.trueID) : osPathToFile.getFileName().toString();
    }

    //----------------------------------------
    //---V1
    private String generatePerentGenPath(String myGenParentID) throws UnsupportedEncodingException, NoSuchAlgorithmException
    {
        return RootDriveFolderTree.getInstance().getFObjByIDInMyIDHshDirs(myGenParentID).getMyGenPath();

    }

    //---------------------------------------------
    private Path generateTrueMyPath() throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchAlgorithmException, NoSuchAlgorithmException
    {

        return RootDriveFolderTree.getInstance().getFObjByIDInMyIDHshDirs(this.myGenParentID).getMyPath().resolve(this.generatedName);               // <-------------- To DO: probably mus be Paths.get(....
    }

    //---------------------------------------------
    String separateTrueFileName(Path osPathToFile)
    {
        return osPathToFile.getFileName().toString();
    }

    //
    //  ----------------   getInstance() --------------------------------------
    public FileObjDrive getInstance()
    {
        return this;
    }

    //-----------------------------
   

   
    //---------------------------------------------

    @Override
    public String getGeneratedName()
    {
        return this.generatedName;
    }

    
    //-------------------------------------------------------------------------
  
    //------------------------------------------------------------------------
     @Override
    public Long getCreatedTime()
    {
        return this.createdTime;
    }
    //------------------------------------------------------------------------
     @Override
    public Long getModifiedTime()
    {
        return this.modified;
    }
    
    //------------------------------
    
     @Override
      public ActionMarkers getActionMarker()
    {
       return this.actionMarker;
    }
    //---------------------------------------------------
    
  
    //-----------------------------------------------------------------------
    @Override
    public String getMyGenParentID()
    {
        return this.myGenParentID;
    }
    //------------------------------------------------------------------------

    //-----------------------------
    public String getMimeTypeGoog()
    {
        return this.mimeTypeGoog;
    }
    
    //------------------------------------------------------------------------
    //-----------------------------
    public String getMimeType()
    {
        return this.mimeType;
    }
    
    //------------------------------------------------------------------------
    public boolean isIsDuplicate()
    {
        return this.isDuplicate;
    }
  
    //------------------------------------------------------------------------
    public String getOldestDuplicateID()
    {
        return this.oldestDuplicateID;
    }
    //------------------------------------------------------------------------
    public void setIsDuplicate(boolean isDuplicate)
    {
        this.isDuplicate = isDuplicate;
    }
    //------------------------------------------------------------------------
    public void setOldestDuplicateID(String oldestDuplicateID)
    {
        this.oldestDuplicateID = oldestDuplicateID;
    }

    @Override
    public void setMyPath(Path myPath)
    {
        this.myPath = myPath;
    }
    //----------------------------------------------------
    public void setActionMarker(ActionMarkers actionMarker)
    {
        this.actionMarker = actionMarker;
    }
    //----------------------------------------------------
    public boolean setTrueParentID(String parentID)
    {
        
        if (parentID != null && parentID instanceof String)
        {
            this.parentID = parentID;

        }
        return true;
    }
    //----------------------------------------------------
    public void setMimeType(String mimeType)
    {
        this.mimeType = mimeType;
    }

    //
    //**************************************************************************
    //**************************************************************************
    @Override
    public boolean toGrowTreeFromThis() throws IOException, UnsupportedEncodingException
    {
        TreeGrower.toGrowTreeFromFolderObj(this);
        return true;
    }
//----- END OF toGrowTree --------------------------------------------------    
///////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    //**************************************************************************
    //

    @Override
    public void showMeAndMyContent()
    {
        String name = this.isFolder ? this.getTrueName() : "   --- " + this.getTrueName();
        System.out.println(name);
        if (!this.baseMyIDHshChildFiles.isEmpty())
        {
            this.baseMyIDHshChildFiles.values().forEach((t) -> t.showMeAndMyContent());
        }
        else
        {
            System.out.println("\n --");
        }
        //
        if (!this.baseMyIDHshChildDirs.isEmpty())
        {
            this.baseMyIDHshChildDirs.values().forEach((t) -> t.showMeAndMyContent());
        }
    }

    boolean ParentIsInMainBases() throws UnsupportedEncodingException, NoSuchAlgorithmException
    {

        printer.print(Level.FINER,"this.myGenParentID :" + this.myGenParentID);
        return Optional.ofNullable(RootDriveFolderTree.getInstance().getFObjByIDInMyIDHshDirs(this.myGenParentID)).isPresent();

        // <---- TO DO: if not im BASE .....
    }
    
    
    ///////////////////////////////////////////////////////////////////////////
    
    
    
}


package bases;

import static bases.FileObjDrive.printer;
import com.google.common.base.Optional;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;

import java.nio.file.Path;

import java.nio.file.attribute.BasicFileAttributes;
import java.security.NoSuchAlgorithmException;

import java.util.logging.Level;
import toolsClases.MyLogPrinter;

/**
 *
 * @author Shkirmantsev
 */
public final class FileObjOs extends AbstrFolderTree implements IFolderTree
{
     ////////////// HELP FOR PRINTING /////////////////
     static MyLogPrinter printer=new MyLogPrinter(FileObjOs.class,Level.OFF,Level.OFF);    
    /////////////////////////////////////////////////

    private boolean isThissynchronized; //TO do: Synchronised
  
    final public String myGenParentID;

    final private Long createdTime;
    final private Long modified;

    private boolean isDuplicate = false;  //there is one more Older instanse in Dir, this is isDuplicate          //  <--------TO do: Synchronised

    
    private String oldestDuplicateID;

    final private String myPerentGenPath;
    

    private String mimeTypeGoog;
    
    final private String mimeType;


    //************************
    //    CONSTRUCTOR //
    //************************
    public FileObjOs(
        
        FileObjDTO fileDto
//        
    ) throws UnsupportedEncodingException, NoSuchAlgorithmException
    {
        super();
        this.trueName = fileDto.getTrueFileName();
        this.isDuplicate = fileDto.isIsDuplicate();
        this.generatedName = generateGeneratedName(fileDto.getTrueFileName(), fileDto.isIsDuplicate(), fileDto.getTrueID());

        //
        renewParentID(fileDto.getParentID());
        //
        this.myGenParentID = fileDto.getMyGenParentID();
        this.myPerentGenPath = generatePerentGenPath(fileDto.getMyGenParentID());

        this.setMyPath(generateTrueMyPath());                                            //<----------TO DO: read and chek in walker
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
        //
        if (!ParentIsInMainBases())
        {
            throw new ExceptionInInitializerError("FileObject: %s Path: %s \n"
                + " There are no Parent from Node in main Bases during building"
                + " in constructor " + this.generatedName + this.myGenPath);
        }

        this.mySimulatedID = toolsClases.ToolsDriveFacade.getMD5HsFromString(myPerentGenPath + File.separator + this.generatedName);
        this.isDuplicate = !this.generatedName.equals(this.trueName);
        /////////////////////////////
        ///---- !!!! ADDING IN BASES !!!----s
        this.isMyIDAddedInBases = addThisToBases();
        //-------------------------------------------
        // if building from GoogleTree Class (synchronisation):
        boolean isTrueIDAddedInBases;
        isTrueIDAddedInBases = trueID != null ? addThisToTrueIDBases() : false;
        //--------------------------------------------
        /////////////////////////////
        //*********************************************
        //
    }

    //*******************************
    public FileObjOs(
        Path osPathToFile
    
    //Long createdTime, //  <------------------------ TO DO: COMPARE DATE FORMAT
    //Long modified, //  

    ) throws UnsupportedEncodingException, NoSuchAlgorithmException, IOException
    {
        super();
        printer.print("LOG In FileObj OS Constructor");
        this.setMyPath(osPathToFile);
        this.trueName = separateTrueFileName(osPathToFile);
        this.generatedName = generateGeneratedName(osPathToFile);
        
        //
        renewTrueID(null);
        //
        //--------------------
        renewParentID(null);        // True Parent ID, not generated                                        //<----- TO DO: ?????
        //

        this.myPerentGenPath = generatePerentGenPath(osPathToFile);
        this.myGenParentID = toolsClases.ToolsDriveFacade.getMD5HsFromString(myPerentGenPath);

        this.myGenPath = this.myPerentGenPath + File.separator + this.generatedName;
        BasicFileAttributes attr = Files.readAttributes(osPathToFile, BasicFileAttributes.class);
        this.createdTime = attr.creationTime().toMillis();
        this.modified = attr.lastModifiedTime().toMillis();

        this.oldestDuplicateID = null;                                      // <---- TO DO: check with Null???
        this.isFolder = attr.isDirectory();
        this.mimeType = this.mimeTypeGoog = Files.probeContentType(osPathToFile);

        //
        //**************************
        //---- DINAMIC Changes ----
        //
        
        this.mySimulatedID = toolsClases.ToolsDriveFacade.getMD5HsFromString(myPerentGenPath + java.io.File.separator + this.generatedName);
        FileObjOs.printer.print("ID this.mySimulatedID : "+this.mySimulatedID);
        if (!ParentIsInMainBases())
        {
            String err = String.format("FileObject: %s Path: %s \n"
                + " There are no Parent from Node in main Bases during building"
                + " in constructor ", this.generatedName, this.myGenPath);
            throw new ExceptionInInitializerError(err);//+ this.generatedName + this.myGenPath
        }

        
        this.isDuplicate = !this.generatedName.equals(this.trueName);
        /////////////////////////////
        ///---- !!!! ADDING IN BASES !!!----s
        this.isMyIDAddedInBases = addThisToBases();

        FileObjOs.printer.print("Node " + this.generatedName + " is addet themself to bases: " + this.isMyIDAddedInBases + "\n");
        //-------------------------------------------
        // if building from GoogleTree Class (synchronisation):
        boolean isTrueIDAddedInBases;
        isTrueIDAddedInBases = trueID != null ? addThisToTrueIDBases() : false;

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
                FileObjOs.printer.print("adding in File mainBase...");
                RootOSFolderTree.getInstance().addToBaseMyIDHshFilesOS(this.mySimulatedID, this);
                return addThisToParentMyIDBase();
            } catch (UnsupportedEncodingException | NoSuchAlgorithmException ee)
            {
                return false;
            }
        } else
        {
            try
            {
                FileObjOs.printer.print(Level.FINER,"adding me in Folder mainBase...");
                FileObjOs.printer.print(Level.FINER,"adding ID: " + this.mySimulatedID);
                RootOSFolderTree.getInstance().addToBaseMyIDHshDirsOS(this.mySimulatedID, this);
                return addThisToParentMyIDBase();
            } catch (UnsupportedEncodingException | NoSuchAlgorithmException ee)
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
                RootOSFolderTree.getInstance().addToBaseIDHshFilesOS(this.trueID, this);

            } catch (UnsupportedEncodingException | NoSuchAlgorithmException ee)
            {
                return false;
            }
        } else
        {
            try
            {
                RootOSFolderTree.getInstance().addToBaseIDHshDirsOS(this.trueID, this);
            } catch (UnsupportedEncodingException | NoSuchAlgorithmException ee)
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

        return (java.util.Optional.ofNullable((F) this.baseMyIDHshChildFiles.put(myIDHshFile, (FileObjOs) fobj)).isPresent());

    }

    @Override
    public <F extends AbstrFolderTree> boolean addTobaseMyIDHshChildDirsOS(String myIDHshFolder, F fobj)
    {

        return (java.util.Optional.ofNullable((F) this.baseMyIDHshChildDirs.put(myIDHshFolder, (FileObjOs) fobj)).isPresent());
    }

    //-----------------------------------
    private boolean addThisToParentMyIDBase()
    {
        if (!this.isFolder)
        {
            try
            {
               printer.print(Level.FINER,"addThis addToBaseMyIDHshChildFilesOS() : ");
                printer.print(Level.FINER,"this.myGenPath: " + this.myGenPath);
                printer.print(Level.FINER,"this.myPerentGenPath: " + this.myPerentGenPath);
                //System.out.println("parent generated name in base:"+RootOSFolderTree.getInstance().getDirFileObjOsByMyID(this.myGenParentID));

                RootOSFolderTree.getInstance().getFObjByIDInMyIDHshDirs(this.myGenParentID).addToBaseMyIDHshChildFilesOS(this.mySimulatedID, this);
                //RootFolderTreeInOs.getInstance().baseMyIDHshFilesOS.get(this.parentID);
            } catch (UnsupportedEncodingException | NoSuchAlgorithmException ee)
            {
                return false;
            }
        } else
        {
            try
            {
                RootOSFolderTree.getInstance().getFObjByIDInMyIDHshDirs(this.myGenParentID).addTobaseMyIDHshChildDirsOS(this.mySimulatedID, this);
            } catch (UnsupportedEncodingException | NoSuchAlgorithmException ee)
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

    //------------------------------------
    //--V1 in folder OSTree
    //--V2
    public String generateGeneratedName(Path osPathToFile)
    {
        return this.isDuplicate ? (osPathToFile.getFileName().toString() + trueID) : osPathToFile.getFileName().toString();
    }

    //----------------------------------------
    //---V1
    private String generatePerentGenPath(String myGenParentID) throws UnsupportedEncodingException, NoSuchAlgorithmException
    {
        return RootOSFolderTree.getInstance().getFObjByIDInMyIDHshDirs(myGenParentID).getMyGenPath();

    }

    //--V2
    String generatePerentGenPath(Path osPathToFile) throws UnsupportedEncodingException, NoSuchAlgorithmException
    {
        printer.print(Level.FINEST," new node: ");                                       //  <------ TO DO: delete after test
        printer.print(Level.FINEST,osPathToFile);                                        //  <------ TO DO: delete after test
        //root.getMyGenPath +/+ (absolute.normalrootPath).relativise(osPathToFile)--> to string
        String root = RootOSFolderTree.getInstance().getMyGenPath();
        printer.print(Level.FINEST," root path: ");                                     //  <------ TO DO: delete after test
        printer.print(Level.FINEST,root);                                               //  <------ TO DO: delete after test

        Path relativ = RootOSFolderTree.getInstance().getMyPath()
            .toAbsolutePath().normalize()
            .relativize(osPathToFile.toAbsolutePath().normalize());

        printer.print(Level.FINEST,"relativ Path to node incl. : " + relativ.toString());             //  <------ TO DO: delete after test

        String relativeStr = relativ.getNameCount() < 2 ? "" : File.separator + relativ.subpath(0, relativ.getNameCount() - 1);

        printer.print(Level.FINEST,"relativ.getNameCount(): " + relativ.getNameCount());
        printer.print(Level.FINEST,"String perent path : " + root + relativeStr);

        return root + relativeStr;
    }

    //---------------------------------------------
    Path generateTrueMyPath() throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchAlgorithmException, NoSuchAlgorithmException
    {

        return RootOSFolderTree.getInstance().getFObjByIDInMyIDHshDirs(myGenParentID).getMyPath().resolve(this.generatedName);               // <-------------- To DO: probably mus be Paths.get(....
    }

    //---------------------------------------------
    String separateTrueFileName(Path osPathToFile)
    {
        return osPathToFile.getFileName().toString();
    }

    //
    //  ----------------   getInstance() --------------------------------------
    public FileObjOs getInstance()
    {
        return this;
    }

    //-----------------------------
    

    @Override
    public String getGeneratedName()
    {
        return this.generatedName;
    }

    //-------------------------------------------------------------------------
     
    
     //-----------------------------------------------------------------------
    @Override
    public String getMyGenParentID()
    {
        return myGenParentID;
    }
    //------------------------------------------------------------------------

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
    //-----------------------------
     @Override
    public String getMimeTypeGoog()
    {
        return this.mimeTypeGoog;
    }
    //-----------------------------
     @Override
    public String getMimeType()
    {
        return this.mimeType;
    }
    //------------------------------
    
     @Override
      public ActionMarkers getActionMarker()
    {
       return this.actionMarker;
    }
    //---------------------------------------------------

    public boolean isIsDuplicate()
    {
        return this.isDuplicate;
    }
    
    //------------------------------------------------------------------------
     @Override
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
    //-----------------------------------------------------------------------
    public boolean setTrueParentID(String parentID)
    {
        
        if (parentID != null && parentID instanceof String)
        {
            this.parentID = parentID;

        }
        return true;
    }
    //------------------------------------------------------------------------

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
    //---------------------------------------------------
    public void setMimeTypeGoog(String mimeTypeGoog)
    {
        this.mimeTypeGoog = mimeTypeGoog;
    }

    //-------------------
    boolean checkIsThisFolder()
    {
        return this.isFolder;
    }

    //
    //**************************************************************************
    //**************************************************************************
    @Override
    public boolean toGrowTreeFromThis() throws IOException
    {
        if (this.isThisIsInAnyMainBase())
        {
            FileObjOs.printer.print("Growing tree from " + this.generatedName + "...");
            String name = this.isIsFolder() ? this.getTrueName() : "   --- " + this.getTrueName();
            FileObjOs.printer.print(name);

            Files.walk(this.myPath).forEach(
                (Path f) ->
            {
                try
                {
                    // System.out.println("f Path: " + f);
                    if (!f.toAbsolutePath().normalize().toString().equalsIgnoreCase(this.myPath.toAbsolutePath().normalize().toString())){
                    FileObjOs obj = new FileObjOs(f);}
                    //obj.toGrowTreeFromThis();
                } catch (NoSuchAlgorithmException | IOException ex)
                {
                    throw new ExceptionInInitializerError("Can not grow Tree from this node");
                }
            });
        }
        return true;
    }

    //----------------------------------------------
    //**************************************************************************
    //
    @Override
    public void showMeAndMyContent()
    {
        String name = this.isFolder ? this.getTrueName() : "   --- " + this.getTrueName();
        //FileObjOs.printer.print(name);
        System.out.println(name);
        if (!this.baseMyIDHshChildFiles.isEmpty())
        {
            this.baseMyIDHshChildFiles.values().forEach((t) -> t.showMeAndMyContent());
        } else
        {
            System.out.println("\n --");
        }
        if (!this.baseMyIDHshChildDirs.isEmpty())
        {
            this.baseMyIDHshChildDirs.values().forEach((t) -> t.showMeAndMyContent());
        }
    }

    boolean ParentIsInMainBases() throws UnsupportedEncodingException, NoSuchAlgorithmException
    {

        printer.print(Level.FINER,"this.myGenParentID :" + this.myGenParentID);
        
        
        return Optional.fromNullable(RootOSFolderTree.getInstance().getFObjByIDInMyIDHshDirs(this.myGenParentID)).isPresent();

        
    }
    
    //    //------------------------------------------------------------------------
   
    
    
    
 
}

/**
 *  ABSTRACT CLASS FOR FileObjOs,
 */
package bases;


import com.google.api.services.drive.model.File;

import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
//-----------------------------------------
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
//--------------------------------


/**
 *
 * @author Shkirmantsev
 */
public abstract class AbstrFolderTree implements IFolderTree
{

    private static volatile AbstrFolderTree instance;
    protected boolean isFolder;

    protected String generatedName;
    protected String trueName = "trueNameInFolderOSTree";
    protected String myGenPath;
    protected ActionMarkers actionMarker = ActionMarkers.PASS;
    protected Path myPath;

    ////////////////////////////////////
    //*****DATABASES*****************//
    //
    protected HashMap<String, AbstrFolderTree> baseIDHshFilesOS ;
    protected HashMap<String, AbstrFolderTree> baseIDHshDirsOS ;
//
    protected HashMap<String, AbstrFolderTree> baseMyIDHshFilesOS ;
    protected HashMap<String, AbstrFolderTree> baseMyIDHshDirsOS ;

    protected HashMap<String, IFolderTree> baseMyIDHshChildDirs ;
    protected HashMap<String, IFolderTree> baseMyIDHshChildFiles ;

//*********************************
    // TMP BASES:
    //-----------------------------------------------------
    protected final HashMap<String, TreeSet<File>> structSubFiles;
    protected final HashMap<String, TreeSet<File>> structSubDirs;

//*********************************
    ///////////////////////////////////
    protected String parentID;
    protected String myParentID;

    protected String trueID;
    protected String mySimulatedID;
    protected boolean isMyIDAddedInBases = false;

    //************************
    //    CONSTRUCTOR //
    //************************
    protected AbstrFolderTree() throws UnsupportedEncodingException, NoSuchAlgorithmException
    {
        baseIDHshFilesOS = new HashMap<>();
        baseIDHshDirsOS = new HashMap<>();
//
        baseMyIDHshFilesOS = new HashMap<>();
        baseMyIDHshDirsOS = new HashMap<>();

        baseMyIDHshChildDirs = new HashMap<>();
        baseMyIDHshChildFiles = new HashMap<>();
        
        structSubFiles=new HashMap<>();
        structSubDirs=new HashMap<>();   

    }
//    
//**************************
    //--------------------------------------------------------------------------

    public final String generateGeneratedName(String trueFileName, boolean isDuplicate, String trueID)
    {

        return isDuplicate ? (trueFileName + trueID) : trueFileName;
    }

    //---------------------------------------
  
    public final String getMySimulatedID()
    {
        return this.mySimulatedID;
    }

    public abstract Long getCreatedTime();

    public abstract Long getModifiedTime();

    public abstract ActionMarkers getActionMarker();

    public abstract boolean isDuplicate();

    public abstract String getOldestDuplicateID();

    public abstract String getMimeTypeGoog();

    //-----------------------------
    public abstract String getMimeType();

    //------------------------------------------------------------------------
 
     public <F extends AbstrFolderTree> boolean addToBaseMyIDHshChildFilesOS(String myIDHshFile, F fobj)
    {

        return (Optional.ofNullable((F) this.baseMyIDHshChildFiles.put(myIDHshFile, fobj)).isPresent());

    }
  
    
    public <F extends AbstrFolderTree> boolean addTobaseMyIDHshChildDirsOS(String myIDHshFolder, F fobj)
    {

        return (Optional.ofNullable((F) this.baseMyIDHshChildDirs.put(myIDHshFolder, fobj)).isPresent());
    }
    
    //------------------------------------------------------------------------

    //------------------------------------------------------------------------
     //------------------------------------------------------------------------
     
     public <F extends AbstrFolderTree> boolean removeFromBaseMyIDHshChildFilesOS(String myIDHshFile)
    {

        return (java.util.Optional.ofNullable((F) this.baseMyIDHshChildFiles.remove(myIDHshFile)).isPresent());

    }
  
     
    public <F extends AbstrFolderTree> boolean removeFromMyIDHshChildDirsOS(String myIDHshFolder)
    {

        return (java.util.Optional.ofNullable((F) this.baseMyIDHshChildDirs.remove(myIDHshFolder)).isPresent());
    }

    //------------------------------------------------------------------------
    //
//    //**************************************************************************
    //**************************************************************************
    //NEED FOR PATTERN BRIDGE and Composition:
    //************************************************************************
    public <F extends AbstrFolderTree> boolean addToBaseMyIDHshFilesOS(String myIDHshFile, F fobj)
    {
//        throw new UnsupportedOperationException("addToBaseMyIDHshFilesOS(String myIDHshFile, F fobj) Not implemented yet.");
        return (Optional.ofNullable((F) this.baseMyIDHshFilesOS.put(myIDHshFile, fobj)).isPresent());

    }

    public <F extends AbstrFolderTree> boolean addToBaseMyIDHshDirsOS(String myIDHshFolder, F fobj)
    {
//        throw new UnsupportedOperationException("addToBaseMyIDHshDirsOS(String myIDHshFolder, F fobj) Not implemented yet.");
        return (Optional.ofNullable((F) this.baseMyIDHshDirsOS.put(myIDHshFolder, fobj)).isPresent());

    }

    //------------------------------------------------------------------------
    public <F extends AbstrFolderTree> boolean removeFromBaseMyIDHshFilesOS(String myIDHshFile)
    {
//        throw new UnsupportedOperationException("removeFromBaseMyIDHshFilesOS(String myIDHshFile) Not implemented yet.");
        return (Optional.ofNullable((F) this.baseMyIDHshFilesOS.remove(myIDHshFile)).isPresent());

    }

    public <F extends AbstrFolderTree> boolean removeFromBaseMyIDHshDirsOS(String myIDHshFolder)
    {
//        throw new UnsupportedOperationException("removeFromBaseMyIDHshDirsOS(String myIDHshFolder) Not implemented yet.");
        return (Optional.ofNullable((F) this.baseMyIDHshDirsOS.remove(myIDHshFolder)).isPresent());

    }

    public <F extends AbstrFolderTree> boolean addToBaseIDHshFilesOS(String trueIDHshFile, F fobj)
    {
//        throw new UnsupportedOperationException("addToBaseIDHshFilesOS(String trueIDHshFile, F fobj) Not implemented yet.");
        return (Optional.ofNullable((F) this.baseIDHshFilesOS.put(trueIDHshFile, fobj)).isPresent());

    }

    //------------------------------------------------------------------   
    public <F extends AbstrFolderTree> boolean addToBaseIDHshDirsOS(String trueIDHshFolder, F fobj)
    {
//        throw new UnsupportedOperationException("addToBaseIDHshDirsOS(String trueIDHshFolder, F fobj) Not implemented yet.");
        return (Optional.ofNullable((F) this.baseIDHshDirsOS.put(trueIDHshFolder, fobj)).isPresent());

    }

    //------------------------------------------------------------------------
    //**************************************************************************
    //    //-------------------------------------------------------------------------
    public String getTrueName()
    {
//        throw new UnsupportedOperationException("getTrueName() Not implemented yet.");
        return this.trueName;
    }

    //--------------------------------------
    public Set<String> getBaseMyIDHshChildDirsSet()
    {
//        throw new UnsupportedOperationException("getBaseMyIDHshChildDirsSet() Not implemented yet.");
        return Collections.synchronizedSet(new HashSet<>(this.baseMyIDHshChildDirs.keySet()));
    }

    public boolean isThisIsInAnyMainBase()
    {
//        throw new UnsupportedOperationException("isThisIsInAnyMainBase() Not implemented yet.");
        return this.isMyIDAddedInBases;
    }

    //------------------------------
    @Override
    public boolean isIsFolder()
    {
//        throw new UnsupportedOperationException("isIsFolder() Not implemented yet.");
        return this.isFolder;
    }

    //**************************************************************************
    //**************************************************************************
    //
    public String getMyGenPath()
    {
//        throw new UnsupportedOperationException("getMyGenPath() Not implemented yet.");
        return this.myGenPath;
    }
    //-------------------------------------------------------------------------

    @Override
    public Path getMyPath()
    {
//        throw new UnsupportedOperationException("getMyPath() Not implemented yet.");
        return this.myPath;
    }

    //-----------------------------------------------------------------------
    //-----------------------------------------------------------------------
    public String getParentID()
    {
//        throw new UnsupportedOperationException("getParentID() Not implemented yet.");
        return this.parentID;
    }
    //-----------------------------------------------------------------------
    //will be overriding in subclases

    @Override
    public String getMyGenParentID()
    {
//        throw new UnsupportedOperationException("getMyGenParentID() Not implemented yet.");
        return this.myParentID;
    }

    //------------------------------------------------------------------------
    public HashMap<String, TreeSet<File>> getStructSubFiles()
    {
//        throw new UnsupportedOperationException("getStructSubFiles() Not implemented yet.");
        return this.structSubFiles;
    }

    public HashMap<String, TreeSet<File>> getStructSubDirs()
    {
//        throw new UnsupportedOperationException("getStructSubDirs() Not implemented yet.");
        return this.structSubDirs;
    }

    //-----------------------------------
    public IFolderTree getChildFileFileObjByMyID(String myID)
    {
//        throw new UnsupportedOperationException("getChildFileFileObjByMyID Not implemented yet.");
        return this.baseMyIDHshChildFiles.get(myID);
    }

    public IFolderTree getChildDirFileObjByMyID(String myID)
    {
//        throw new UnsupportedOperationException("getChildDirFileObjByMyID Not implemented yet.");
        return this.baseMyIDHshChildDirs.get(myID);
    }
    //-----------------------------------------------------------------------

    //In OS it is not present
    public String getTrueID()
    {
//        throw new UnsupportedOperationException("getTrueID() Not implemented yet.");
        return this.trueID;
    }

    //------------------------------------------------------------------------
    public boolean setTrueID(String trueID)
    {
//        throw new UnsupportedOperationException("setTrueID Not implemented yet.");
        return false;
    }

}

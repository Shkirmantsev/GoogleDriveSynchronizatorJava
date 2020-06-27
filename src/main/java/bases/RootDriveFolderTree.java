package bases;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
//import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
//-----------------------------------------
import java.nio.file.Path;

import java.util.Optional;
import java.util.Set;

import java.util.logging.Level;

import toolsClases.MyLogPrinter;

//--------------------------------
import toolsClases.ToolsDriveFacade;

/**
 *
 * @author Shkirmantsev
 */
public class RootDriveFolderTree extends AbstrFolderTree implements IFolderTree
{

    ////////////// HELP FOR PRINTING /////////////////
    static MyLogPrinter printer = new MyLogPrinter(RootDriveFolderTree.class, Level.OFF, Level.WARNING);
    /////////////////////////////////////////////////

    ///////////////////////////////////////////////////
    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    private static volatile RootDriveFolderTree instance = null;
    ///////////////////////////////////////////////////


    //************************
    //    CONSTRUCTOR //
    //************************
    private RootDriveFolderTree() throws UnsupportedEncodingException, NoSuchAlgorithmException
    {
        super();
        this.generatedName = "root";
        this.trueName = null;
        this.isFolder = true;
        
        this.parentID = null;
        this.myParentID = null;
        this.trueID = tryFindRealRootID();

        this.mySimulatedID = ToolsDriveFacade.getMD5HsFromString(this.generatedName);
        this.myGenPath = "root";
        this.isMyIDAddedInBases = addThisToBaseMyIDHshDirsOS();

        printer.print(" - myIDHshRoot (Drive) in FolderBases(Drive) : " + this.getMySimulatedID());
        printer.print(" this.baseMyIDHshDirsOS(Drive) size: " + this.baseMyIDHshDirsOS.size());

        String tmp1 = this.getMySimulatedID();
        String tmp2 = this.baseMyIDHshDirsOS.get(tmp1).getGeneratedName();

        printer.print(" find root(Drive) Object by ID GeneratedName : " + tmp2);

    }
//
//*************************

    private String tryFindRealRootID()
    {
        String basicTrueRootID = "root";
        try
        {
            if (ToolsDriveFacade.listOfDriveSubFilesInRoot() != null
                && !ToolsDriveFacade.listOfDriveSubFilesInRoot().isEmpty())
            {
                return ToolsDriveFacade.listOfDriveSubFilesInRoot().get(0).getParents().get(0);
            }
        } catch (IOException ex)
        {
            try
            {
                if (ToolsDriveFacade.listOfDriveSubFoldersInRoot() != null
                    && !ToolsDriveFacade.listOfDriveSubFoldersInRoot().isEmpty())
                {
                    return ToolsDriveFacade.listOfDriveSubFilesInRoot().get(0).getParents().get(0);
                }
            } catch (IOException ex2)
            {
                return basicTrueRootID;
            }
        }
        return basicTrueRootID;
    }

//**************************
    private boolean addThisToBaseMyIDHshDirsOS()
    {
        printer.print("addThis ROOT (Drive) to baseMyIDHshDirsOS");
        printer.print("root (Drive) this: " + this);
        printer.print("this.mySimulatedID (Drive) : " + this.mySimulatedID);

        this.baseMyIDHshDirsOS.put(this.mySimulatedID, this);
        printer.print("root this==null? : " + (this.baseMyIDHshDirsOS.get(this.mySimulatedID) == null));
        return true;
    }

    //
    //**************************************************************************
    //----------------------------------------------------
    //------------------------------------------------------------------------
    @Override
    public boolean isDuplicate()
    {
        return false;
    }

    @Override
    public String getOldestDuplicateID()
    {
        return null;
    }

    @Override
    public String getMimeTypeGoog()
    {
        return null;
    }

    //-----------------------------
    @Override
    public String getMimeType()
    {
        return null;
    }

  

    //------------------------------------------------------------------------
    //------------------------------------------------------------------------
    //**************************************************************************
    @Override
    public Long getCreatedTime()
    {
        return 0L;
    }

    ;
     @Override
    public Long getModifiedTime()
    {
        return 0L;
    }

    ;
    //--------------------------------------------------------------------------
    //----------------------------------------------------
    @Override
    public String getGeneratedName()
    {
        return this.generatedName;
    }

    //-------------------------------------------
    //-----------------------------------------------------------------------
    @Override
    public String getMyGenParentID()
    {
        return myParentID;
    }

    
    //-------------------------------------------
    @Override
    public ActionMarkers getActionMarker()
    {
        return ActionMarkers.PASS;
    }

    public AbstrFolderTree getFObjByIDInMyIDHshDirs(String myID)
    {
        //        System.out.println("return mySearchingID: " + myID);
        //        System.out.println("real  root    GenID: " + this.mySimulatedID);
        return this.baseMyIDHshDirsOS.get(myID);

    }

    //------------------------------------------------------------------------
    public AbstrFolderTree getFObjByIDInMyIDHshFiles(String myID)
    {
        return this.baseMyIDHshFilesOS.get(myID);

    }
    //------------------------------------------------------------------------

//**************************************************************************
    //
    //  ----------------   getInstance() --------------------------------------
    public Path chooseRootPath(Path rootDrivePathInOS) throws UnsupportedEncodingException, NoSuchAlgorithmException // <---- TO DO: 2 constructor
    {

        RootDriveFolderTree.getInstance().setMyPath(rootDrivePathInOS);
//        addThisToBaseMyIDHshDirsOS();
        return this.getMyPath();
    }

    //----------------------------------------------------
    //------------------------------------------------------------------------
    public Set<String> getKeySetMyIDHshDirs()
    {
        return this.baseMyIDHshDirsOS.keySet();

    }

    //------------------------------------------------------------------------
    public Set<String> getKeySetMyIDHshFiles()
    {
        return this.baseMyIDHshFilesOS.keySet();

    }
    //------------------------------------------------------------------------

    @Override
    public void setMyPath(Path myPath)
    {
        this.myPath = myPath;
    }
    //

    //
    //********************************
    @Override
    public void showMeAndMyContent()
    {
        System.out.println("####____-DRIVE: showMeAndMyContent():  ");
        String name = this.isFolder ? this.getTrueName() : "   --- " + this.getTrueName();
        System.out.println(name);
        if (!this.baseMyIDHshChildFiles.isEmpty())
        {
            this.baseMyIDHshChildFiles.values().forEach((t) -> t.showMeAndMyContent());
        } else
        {
            System.out.println("\n --// --");
        }
        if (!this.baseMyIDHshChildDirs.isEmpty())
        {
            this.baseMyIDHshChildDirs.values().forEach((t) -> t.showMeAndMyContent());
        }
    }

    //**********************************
    //The auto growing from this place
    @Override
    public boolean toGrowTreeFromThis() throws IOException, UnsupportedEncodingException
    {
        TreeGrower.toGrowTreeFromFolderObj(this);
        return true;
    }
//----- END OF toGrowTree --------------------------------------------------    
///////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////

    //Singleton Instance
    public static RootDriveFolderTree getInstance() throws UnsupportedEncodingException, NoSuchAlgorithmException
    {
        RootDriveFolderTree localInstance = instance;
        if (localInstance == null)
        {
            synchronized (RootDriveFolderTree.class)
            {
                localInstance = instance;
                if (localInstance == null)
                {
                    instance = localInstance = new RootDriveFolderTree();
                }
            }
        }
        return localInstance;
    }

    
}

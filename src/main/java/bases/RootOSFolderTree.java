
package bases;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
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
public class RootOSFolderTree extends AbstrFolderTree implements IFolderTree
{
     ////////////// HELP FOR PRINTING /////////////////
     static MyLogPrinter printer=new MyLogPrinter(RootOSFolderTree.class,Level.OFF,Level.WARNING);    
    /////////////////////////////////////////////////

    ///////////////////////////////////////////////////
    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    private static volatile RootOSFolderTree instance = null;
    ///////////////////////////////////////////////////

    
    //************************
    //    CONSTRUCTOR //
    //************************
    private RootOSFolderTree() throws UnsupportedEncodingException, NoSuchAlgorithmException
    {   super();
        this.generatedName = "root";
        this.isMyIDAddedInBases=false;
        this.parentID=null;
        
        this.myParentID = null;
        this.trueID = "root";
        this.mySimulatedID = ToolsDriveFacade.getMD5HsFromString(generatedName);
        this.myGenPath = "root";
        this.isMyIDAddedInBases = addThisToBaseMyIDHshDirsOS();
        System.out.println("myIDHshRoot in FolderBases : " + this.getMySimulatedID());
        System.out.println(" this.baseMyIDHshDirsOS size: " + this.baseMyIDHshDirsOS.size());
        String tmp1 = this.getMySimulatedID();
        String tmp2 = this.baseMyIDHshDirsOS.get(tmp1).getGeneratedName();
        System.out.println(" find root Object by ID GeneratedName : " + tmp2);

    }
//    
//**************************

    private boolean addThisToBaseMyIDHshDirsOS()
    {
        printer.print("addThis ROOT ToParentMyIDBase");
        printer.print("root this: " + this);
        printer.print("this.mySimulatedID : " + this.mySimulatedID);

        this.baseMyIDHshDirsOS.putIfAbsent(this.mySimulatedID, this);
        System.out.println("root this==null? : " + (this.baseMyIDHshDirsOS.get(this.mySimulatedID) == null));
        return true;
    }


    //------------------------------------------------------------------------
    //**************************************************************************
    //--------------------------------------------------------------------------
    @Override
     public Long getCreatedTime(){return 0L;};
     @Override
     public Long getModifiedTime(){return 0L;};
     
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
    //--------------------------------------------------------------------------
   
    @Override
    public boolean isDuplicate(){return false;}
    
    @Override
    public String getOldestDuplicateID(){return null;}
    //-----------------------------------------------
//    AbstrFolderTree getFileFileObjOsByMyID(String myID)
//    {
//        return this.baseMyIDHshFilesOS.get(myID);
//    }

    //**************************************************************************
    //
    //  ----------------   getInstance() --------------------------------------
    public Path chooseRootPath(Path rootDrivePathInOS) throws UnsupportedEncodingException, NoSuchAlgorithmException // <---- TO DO: 2 constructor. One make setPath from path, other path ./Drive
    {

        getInstance().setMyPath(rootDrivePathInOS);
//        addThisToBaseMyIDHshDirsOS();
        System.out.println("rooTfolderTreeInOs is choosed");
        return this.getMyPath();
    }

 

    @Override
    public String getGeneratedName()
    {
        return this.generatedName;
    }

    //-------------------------------------------
    @Override
      public ActionMarkers getActionMarker()
    {
       return ActionMarkers.PASS;
    }
    //------------------------------------------------------------------------
    public AbstrFolderTree getFObjByIDInMyIDHshDirs(String searchKey)
    {
        return this.baseMyIDHshDirsOS.get(searchKey);
        
    }
    //------------------------------------------------------------------------
    public AbstrFolderTree getFObjByIDInMyIDHshFiles(String searchKey)
    {
        return this.baseMyIDHshFilesOS.get(searchKey);
        
    }
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
     //------------------------------------------------------------------------
    
    //

    //
    //********************************
    @Override
    public void showMeAndMyContent()
    {
        System.out.println("####____-OS : showMeAndMyContent():  ");
        System.out.println("IN showMeAndMyContent():");
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
    @Override
    public boolean toGrowTreeFromThis() throws IOException
    {
        System.out.println("IN toGrowTreeFromThis() From RootFolderTree:");
        if (this.isThisIsInAnyMainBase())
        {
            System.out.println("Growing tree from " + this.generatedName + "...");
            String name = this.isIsFolder() ? this.getTrueName() : "   --- " + this.getTrueName();
            System.out.println("true root Name: "+name);

            Files.walk(this.myPath).forEach(
                (Path f) ->
            {
                try
                {
//                    System.out.println("f Path: " + f);
                    if (!f.toAbsolutePath().normalize().toString().equalsIgnoreCase(this.myPath.toAbsolutePath().normalize().toString())){
                        
                    FileObjOs obj = new FileObjOs(f);}
                    //obj.toGrowTreeFromThis();
                } catch (NoSuchAlgorithmException | IOException ex)
                {   System.out.println("Exception????");
                    throw new ExceptionInInitializerError("Can not grow Tree from this node");
                }
            });
        }
        return true;
    }

    public static RootOSFolderTree getInstance() throws UnsupportedEncodingException, NoSuchAlgorithmException
    {
        RootOSFolderTree localInstance = instance;
        if (localInstance == null)
        {
            synchronized (RootOSFolderTree.class)
            {
                localInstance = instance;
                if (localInstance == null)
                {
                    instance =localInstance= new RootOSFolderTree();
                }
            }
        }
        return localInstance;
    }
    
    
    
    
    
    
//    //------------------------------------------------------------------------
   
 

}

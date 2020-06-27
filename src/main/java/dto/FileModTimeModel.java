package dto;

import bases.FileObjDrive;
import bases.FileObjDTO;
import bases.AbstrFolderTree;
import bases.HoldPl;
import bases.RootDriveFolderTree;
import bases.RootOSFolderTree;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 *
 * @author Shkirmantsev
 */
public class FileModTimeModel implements IModTime
{

    private String fileMD5Name;

    private long fileModTimeGoog;
    private long fileModTimeOS;
    private long fileModTimeReal;

    private long dbChangeVersion;
    //
    private Connection conn;
    //
    private long modTimeDB = 0;
    private long modTimeRealDB = 0;
    private long modTimeDBOS = 0;
    private long modTimeRealDBOS = 0;
    //

    public FileModTimeModel(FileObjDTO fObj, HoldPl hp)
    {
        super();
        this.fileMD5Name = fObj.getHashName();
        this.fileMD5Name = fObj.getHashName();
        if (hp.equals(HoldPl.DRIVE))
        {
            this.fileModTimeGoog = fObj.getModified();

        } else if (hp.equals(HoldPl.OS))
        {
            this.fileModTimeOS = fObj.getModified();

        }

        loadParams1FromDB();
        loadParams2FromDB();

    }

    public FileModTimeModel(AbstrFolderTree fObj, HoldPl hp)
    {
        super();

        this.fileMD5Name = fObj.getMySimulatedID();

        if (hp.equals(HoldPl.DRIVE))
        {
            this.fileModTimeGoog = fObj.getModifiedTime();
        } else if (hp.equals(HoldPl.OS))
        {
            this.fileModTimeOS = fObj.getModifiedTime();
        }

        loadParams1FromDB();
        loadParams2FromDB();

    }

    public FileModTimeModel(AbstrFolderTree dFObj, long realTime, HoldPl hp)
    {
        this(dFObj, hp);
        this.fileModTimeReal = realTime;

        loadParams1FromDB();
        loadParams2FromDB();

    }

    ///////////////////////////////////////////////
    @Override
    public FileModTimeModel setFileModTimeReal(long fileModTimeReal)
    {
        this.fileModTimeReal = fileModTimeReal;
        return this;
    }

//---------------------
    @Override
    public long getCompMTime(HoldPl hp)
    {

        switch (hp)
        {
            case DRIVE:
                //find hash->modTimeg
                //if hash not exist-> ModTime= Obj modtime
                if (this.modTimeRealDB == 0)
                {
                    return this.fileModTimeGoog;
                }
                //if exist:        
                //--then if mTime==mTime => take from real in table;
                if (this.modTimeDB != 0 && this.fileModTimeGoog == this.modTimeDB)
                {
                    return this.modTimeRealDB;
                }

                //-------if mdata!=mdata ==>>take from obj
                if (this.modTimeDB != 0 && this.fileModTimeGoog != this.modTimeDB)
                {
                    return this.fileModTimeGoog;
                }
                return this.fileModTimeGoog;

            case OS:
                //find hash->modTimeg
                //if hash not exist-> ModTime= Obj modtime
                if (this.modTimeRealDBOS == 0)
                {
                    return this.fileModTimeOS;
                }

                if (this.modTimeDBOS != 0 && this.fileModTimeOS == this.modTimeDBOS)
                {
                    return this.modTimeRealDBOS;
                }
                //-------if mdata!=mdata ==>>take from obj
                if (this.modTimeDBOS != 0 && this.fileModTimeOS != this.modTimeDBOS)
                {
                    return this.fileModTimeOS;
                }
                return this.fileModTimeOS;

        }
        return -1;
    }

    public long getModTimeRealDB()
    {
        return modTimeRealDB;
    }

    @Override
    public FileModTimeModel updMTimeInDB(HoldPl hp)
    {
        if (hp.equals(HoldPl.DRIVE))
        {
            this.isertOrUpdRow(this.fileMD5Name, this.fileModTimeGoog, this.fileModTimeReal, hp);
            loadParams1FromDB();
        } else if (HoldPl.OS.equals(hp))
        {
            this.isertOrUpdRow(this.fileMD5Name, this.fileModTimeOS, this.fileModTimeReal, hp);

            loadParams2FromDB();
        }
        return this;
    }

    @Override
    public FileModTimeModel computeAndUpdDB(HoldPl hp)
    {

        this.fileModTimeReal = this.getCompMTime(hp);
        updMTimeInDB(hp);

        return this;

    }

    //==============================
    //==============================
    private void loadParams1FromDB()
    {
        String sqlReq = "SELECT ModifTimeLongGoog,ModifTimeLongReal FROM filemodifytime WHERE FileNameMD5Hash=?;";
        try (Connection conn = dbUtil.DB.getConnection();
            PreparedStatement pStat = conn.prepareStatement(sqlReq);)
        {
            
            pStat.setString(1, this.fileMD5Name);
            ResultSet res = pStat.executeQuery();
            if (res.next())
            {
                this.modTimeDB = res.getLong("ModifTimeLongGoog"); //ModifTimeLongGoog,ModifTimeLongReal
                this.modTimeRealDB = res.getLong("ModifTimeLongReal");
            }

        } catch (SQLException ex)
        {
            printer.print(Level.SEVERE, "TEST POINT1 (FileModTimeModel)");
            printer.print(Level.SEVERE, ex);
        }

    }

    private void loadParams2FromDB()
    {
        String sqlReq = "SELECT ModifTimeLongOS,ModifTimeLongReal FROM filemodifyos WHERE FileNameMD5Hash=?;";
        try (Connection conn = dbUtil.DB.getConnection();
            PreparedStatement pStat = conn.prepareStatement(sqlReq);)
        {
            pStat.setString(1, this.fileMD5Name);
            ResultSet res = pStat.executeQuery();
            if (res.next())
            {
                this.modTimeDBOS = res.getLong("ModifTimeLongOS"); //ModifTimeLongGoog,ModifTimeLongReal
                this.modTimeRealDBOS = res.getLong("ModifTimeLongReal");
            }

        } catch (SQLException ex)
        {
            printer.print(Level.SEVERE, "TEST POINT2 (FileModTimeModel)");
            printer.print(Level.SEVERE, ex);
        }

    }

    private boolean isHashExistInDB(String hash, HoldPl hp)
    {
        boolean exist = false;
        String sqlReq = "";
        ResultSet res;
        if (hp.equals(HoldPl.DRIVE))
        {
            sqlReq = "SELECT FileNameMD5Hash FROM filemodifytime WHERE FileNameMD5Hash=?;";
        } else if (hp.equals(HoldPl.OS))
        {
            sqlReq = "SELECT FileNameMD5Hash FROM filemodifyos WHERE FileNameMD5Hash=?;";
        }
        try (Connection conn = dbUtil.DB.getConnection();
            PreparedStatement pStat = conn.prepareStatement(sqlReq);)
        {

            pStat.setString(1, hash);

            res = pStat.executeQuery();

            return res.next();
        } catch (SQLException ex)
        {
            printer.print(Level.SEVERE, "TEST POINT3 (FileModTimeModel)");
            printer.print(Level.SEVERE, ex);
        }
        return exist;
    }

    private int isertOrUpdRow(String hash, long mTimeSys, long mTimeReal, HoldPl hp)
    {
//        System.out.println("TEST hash: " + hash);
//        System.out.println("TEST mTimeSys: " + mTimeSys);
//        System.out.println("TEST mTimeReal: " + mTimeReal);
//        System.out.println("TEST hp: " + hp);

        int res = 0;
        boolean alreadyExist = false;

        if (!this.isHashExistInDB(hash, hp))
        {
            String sql = "";
            if (hp.equals(HoldPl.DRIVE))
            {
                sql = "INSERT INTO filemodifytime (FileNameMD5Hash, ModifTimeLongGoog,ModifTimeLongReal) VALUES (?,?,?);";
            }
            if (hp.equals(HoldPl.OS))
            {
                sql = "INSERT INTO filemodifyos (FileNameMD5Hash, ModifTimeLongOS,ModifTimeLongReal) VALUES (?,?,?);";
            }

            try (Connection conn = dbUtil.DB.getConnection();
                PreparedStatement stat = conn.prepareStatement(sql);)
            {

                stat.setString(1, hash);
                stat.setLong(2, mTimeSys);
                stat.setLong(3, mTimeReal);
                res += stat.executeUpdate();

            } catch (SQLException ex)
            {
                printer.print(Level.SEVERE, "TEST POINT4 (FileModTimeModel)");
                printer.print(Level.SEVERE, ex);

            }
            IModTime.setDBChangeVersion((new Date()).toInstant().toEpochMilli());

            return res;
        } else
        {
            String sql = "";
            if (hp.equals(HoldPl.DRIVE))
            {
                sql = "UPDATE filemodifytime SET ModifTimeLongGoog=?,ModifTimeLongReal=? WHERE FileNameMD5Hash=?;";
            }
            if (hp.equals(HoldPl.OS))
            {
                sql = "UPDATE  filemodifyos SET ModifTimeLongOS=?,ModifTimeLongReal=? WHERE FileNameMD5Hash=?;";
            }

            try (Connection conn = dbUtil.DB.getConnection();
                PreparedStatement stat = conn.prepareStatement(sql);)
            {

                stat.setString(3, hash);
                stat.setLong(1, mTimeSys);
                stat.setLong(2, mTimeReal);

                res += stat.executeUpdate();

            } catch (SQLException ex)
            {
                printer.print(Level.SEVERE, "TEST POINT5 (FileModTimeModel)");
                printer.print(Level.SEVERE, ex);

            }
            IModTime.setDBChangeVersion((new Date()).toInstant().toEpochMilli());
            return res;

        }

    }

    public static boolean cleanDBTresh(RootOSFolderTree rFTreeInOS, RootDriveFolderTree driveTree)
    {

        final String rootGenID = rFTreeInOS.getMySimulatedID();

        Set<String> rDirOSDet = new HashSet<>(rFTreeInOS.getKeySetMyIDHshDirs());
        Set<String> setRFileOS = new HashSet<>(rFTreeInOS.getKeySetMyIDHshFiles());

        Set<String> rDirDriveSet = new HashSet<>(driveTree.getKeySetMyIDHshDirs());
        Set<String> setRFileDrive = new HashSet<>(driveTree.getKeySetMyIDHshFiles());

        //=== UNION
        rDirOSDet.addAll(setRFileOS);
        rDirDriveSet.addAll(setRFileDrive);

        Set<String> rOSUnionSet = new HashSet<>();
        rOSUnionSet.addAll(rDirOSDet);

        Set<String> rDriveUnionSet = new HashSet<>(rDirDriveSet);
        rDriveUnionSet.addAll(rDirDriveSet);
        //==================================
        Set<String> rDriveDBUnionSet = new HashSet<>(); //all HashName in Drive (In DB)
        Set<String> rOSDBUnionSet = new HashSet<>(); //all HashName in OS (In DB)

        //===================================
        //For DRIVE
        //
        String sqlReq = "SELECT FileNameMD5Hash FROM filemodifytime;";

        try (Connection conn = dbUtil.DB.getConnection();
            Statement stat = conn.createStatement();)
        {

            ResultSet res = stat.executeQuery(sqlReq);
            while (res.next())
            {
                rDriveDBUnionSet.add(res.getString("FileNameMD5Hash"));

            }

        } catch (SQLException ex)
        {
            printer.print(Level.SEVERE, "TEST POINT cleanDBTresh (1)");
            printer.print(Level.SEVERE, ex);
        }

        //==============
        //For OS
        sqlReq = "SELECT FileNameMD5Hash FROM filemodifyos;";

        try (Connection conn = dbUtil.DB.getConnection();
            Statement stat = conn.createStatement();)
        {

            ResultSet res = stat.executeQuery(sqlReq);
            while (res.next())
            {
                rOSDBUnionSet.add(res.getString("FileNameMD5Hash"));

            }

        } catch (SQLException ex)
        {
            printer.print(Level.SEVERE, "TEST POINT cleanDBTresh (2)");
            printer.print(Level.SEVERE, ex);
        }

        rDriveDBUnionSet.removeAll(rDriveUnionSet);
        rOSDBUnionSet.removeAll(rOSUnionSet);

        rOSDBUnionSet.retainAll(rDriveDBUnionSet); //Findet TRESH
        if (!rOSDBUnionSet.isEmpty())
        {
            rOSDBUnionSet.forEach(key -> IModTime.deleteHashFromDB(key));
        }
        //============
        return true;
    }

}

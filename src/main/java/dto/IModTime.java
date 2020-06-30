package dto;

import bases.FileObjDrive;
import bases.RootDriveFolderTree;
import bases.AbstrFolderTree;
import bases.HoldPl;
import bases.RootOSFolderTree;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import toolsClases.MyLogPrinter;

/**
 *
 * @author Shkirmantsev
 */
public interface IModTime
{

    ////////////// HELP FOR PRINTING /////////////////
    static MyLogPrinter printer = new MyLogPrinter(FileModTimeModel.class, Level.FINEST, Level.WARNING);
    /////////////////////////////////////////////////

    //int isertOrUpdRow(String hash,long mTimeGoog,long mTimeReal);
    //void setDBChangeVersion(long dateTimeUTC);
    FileModTimeModel setFileModTimeReal(long fileModTimeReal);

    long getCompMTime(HoldPl hp);

    FileModTimeModel updMTimeInDB(HoldPl hp);

    FileModTimeModel computeAndUpdDB(HoldPl hp);

    static public long getDBChangeVersion()
    {
        ResultSet res=null;
        try (Connection conn = dbUtil.DB.getConnection();
            Statement stat = conn.createStatement();)
        {
            res = stat.executeQuery("SELECT 'Version' FROM version WHERE ID=1;");
            return res.next() ? res.getLong(1) : 0;
        } catch (SQLException ex)
        {
            printer.print(Level.SEVERE, "TEST POINT6");
            printer.print(Level.SEVERE, ex);
        }
        finally {
            if (res != null) try {
                res.close();
            } catch (SQLException ex) {
                printer.print(Level.SEVERE, ex);
            }
        }
        return 0;
    }

    static public long getAnyDBChangeVersion(Path dbpath)
    {
        ResultSet res=null;
        try (Connection conn = dbUtil.DB.getAnyConnection(dbpath);
            Statement stat = conn.createStatement();)
        {
            res = stat.executeQuery("SELECT Version FROM version WHERE ID=1;");
            long reslong=res.next() ? res.getLong(1) : 0;
            res.close();
            return reslong;
        } catch (SQLException ex)
        {
            printer.print(Level.SEVERE, "FELLER IN TEST POINT6 IMODTime");
            printer.print(Level.SEVERE, ex);
        }
        finally {
            if (res != null) try {
                res.close();
            } catch (SQLException ex) {
                printer.print(Level.SEVERE, ex);
            }
        }
        return 0;
    }

    static void setDBChangeVersion(long dateTimeUTC)
    {
        String sql = "UPDATE version SET Version=(?) WHERE ID=1;";
        try (Connection conn = dbUtil.DB.getConnection();
            PreparedStatement stat = conn.prepareStatement(sql);)
        {
            stat.setLong(1, dateTimeUTC);
            int res = stat.executeUpdate();

        } catch (SQLException ex)
        {
            printer.print(Level.SEVERE, "TEST POINT7");
            printer.print(Level.SEVERE, ex);
        }

    }

    static boolean deleteHashFromDB(String Hash)
    {
        int res = 0;
        String sql = "DELETE FROM filemodifytime WHERE FileNameMD5Hash=?;";
        String sql2 = "DELETE FROM filemodifyos WHERE FileNameMD5Hash=?;";
        Connection conn1 = null;
        try (Connection conn = dbUtil.DB.getConnection();
            PreparedStatement stat = conn.prepareStatement(sql);
            PreparedStatement stat2 = conn.prepareStatement(sql2);)
        {
            conn1 = conn;

//            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            stat.setString(1, Hash);
            stat2.setString(1, Hash);

            res += stat.executeUpdate();
            res += stat2.executeUpdate();

//            conn.commit();            
        } catch (SQLException ex)
        {
            System.out.println("TEST POINT8");
            try
            {
                conn1.rollback();
            } catch (SQLException ex1)
            {
                System.out.println("TEST POINT9");
                printer.print(Level.SEVERE, ex1);
            }
            printer.print(Level.SEVERE, ex);

        }

        setDBChangeVersion((new Date()).toInstant().toEpochMilli());

        return res > 0;

    }

    

}

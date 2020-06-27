package dbUtil;

import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import mainGUI_FX.StartGUIWindowController;

/**
 *
 * @author Shkirmantsev
 */
public class DB
{

    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    //SQLite
    private static final String SQLDBLASS = "org.sqlite.JDBC";
    private static final String DBTYPE = "jdbc:sqlite:";
    private static final String DBFILENAME = "DO_NOT_Delete_Syncdb.sqlite";
    private static final String DBPATHSTR = java.nio.file.Paths.get(StartGUIWindowController.getRootPathStr()+File.separator + DBFILENAME).toAbsolutePath().toString();
    //
    private static final String SQLDBCONN = DBTYPE + DBPATHSTR;

    public static Connection getAnyConnection(Path dbPath)
    {
        String dbPathStr = dbPath.toAbsolutePath().toString();
        Connection conn = null;
        try
        {
            Class.forName(SQLDBLASS);
            conn = DriverManager.getConnection(DBTYPE + dbPathStr, USERNAME, PASSWORD);

            return conn;
        } catch (ClassNotFoundException | SQLException e)
        {
            System.out.println("(Any): IS NOT CONNECTED to  " + DBTYPE + dbPathStr);
            e.printStackTrace();
        }
        return conn;
    }

    public static Connection getConnection()
    {   
        Connection conn = null;
        try
        {
            Class.forName(SQLDBLASS);
            conn = DriverManager.getConnection(SQLDBCONN, USERNAME, PASSWORD);
            
            return conn;
        } catch (ClassNotFoundException | SQLException e)
        {
            System.out.println(" IS NOT CONNECTED to " + SQLDBCONN);
            e.printStackTrace();
        }
        
        if (conn == null)
        {
            System.out.println("CAN NOT to connect to:  " + SQLDBCONN);
        }
        return conn;
    }

    ;
    public static void main(String[] args)
    {

        try
        {
            Connection conn = DB.getConnection();

            if (conn != null)
            {
                System.out.println("Connected to:  " + DBPATHSTR);
            } else
            {
                System.out.println("CAN NOT to connect to:  " + DBPATHSTR);
            };
            Statement stat = conn.createStatement();
            ResultSet res = stat.executeQuery("SELECT * FROM filemodifytime;");

            while (res.next())
            {

                System.out.println("fileHash: " + res.getString(1));
                conn.close();
            }
//       
        } catch (SQLException ex)
        {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}



package bases;

//import com.google.api.client.auth.oauth2.Credential;
//import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
//import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
//import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
//import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
//import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
//import com.google.api.client.http.javanet.NetHttpTransport;
//import com.google.api.client.json.JsonFactory;
//import com.google.api.client.json.jackson2.JacksonFactory;
//import com.google.api.client.util.store.FileDataStoreFactory;
//import com.google.api.services.drive.Drive;
//import com.google.api.services.drive.DriveScopes;
//import com.google.api.services.drive.model.File;
//import com.google.api.services.drive.model.FileList;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
//import java.util.EnumMap;
//-----------------------------------------
import java.util.HashMap;
import java.util.Optional;
//--------------------------------
import toolsClases.ToolsDriveFacade;

/**
 *
 * @author Shkirmantsev
 */
public interface IFolderTree
{

   

    default void showMeAndMyContent()
    {
        throw new UnsupportedOperationException("showMeAndMyContent() Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
     //-----------------------------------------------------------------------
   String getMyGenParentID();
//    {
//        return myParentID;
//    }
    //------------------------------------------------------------------------
   public Path getMyPath();
//------------------------------------------------------------------------
    //////// abstract methods must be implemented ////
   //////////////////////////////////////////////////
   
   //generate virtual name, because we can have two or more same name in one folder in google Drive
    default String getGeneratedName()
    {
        throw new UnsupportedOperationException("getGeneratedName() Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    //     //------------------------------------------------------------------------
    //Path to this File object (can be Folder or File)
    default void setMyPath(Path myPath)
    {
        throw new UnsupportedOperationException("setMyPath Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    //------------------------------------------------------------------------
    
    
    default boolean toGrowTreeFromThis() throws IOException
    {
        throw new UnsupportedOperationException("toGrowTreeFromThis() Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public boolean isIsFolder();
}

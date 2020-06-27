package innerLogicTools;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 *
 * @author Shkirmantsev
 */
public class GoogleDriveUtils
{
    private static final String APPLICATION_NAME = "GoogleDrive SynchronizatorJava";
//    private static final String APPLICATION_NAME = "Google Drive API Java syncdrivegood";

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    
    static String pathStr=java.nio.file.Paths.get("./Secret").toAbsolutePath().toString();
    // Directory to store user credentials for this application.
    private static final java.io.File CREDENTIALS_FOLDER //
        = new java.io.File(pathStr);
    //= new java.io.File(System.getProperty("user.home"), "credentials");
    
    private static final String CLIENT_SECRET_FILE_NAME = "credentials.json";

    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    
    // private static final java.util.Collection<String> SCOPES = DriveScopes.all(); // <----- this version not tested yet, make it onli with new json kredentials 

    // Global instance of the {@link FileDataStoreFactory}.
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    // Global instance of the HTTP transport.
    private static HttpTransport HTTP_TRANSPORT;

    private static Drive _driveService;

    static
    {
        try
        {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(CREDENTIALS_FOLDER);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            System.exit(1);
        }
    }

    public static Credential getCredentials() throws IOException
    {

        java.io.File clientSecretFilePath = new java.io.File(CREDENTIALS_FOLDER, CLIENT_SECRET_FILE_NAME);

        //see instruction in google API of google Drive (to take credential file)
        if (!clientSecretFilePath.exists())
        {
            throw new FileNotFoundException("Please copy " + CLIENT_SECRET_FILE_NAME //
                + " to folder: " + CREDENTIALS_FOLDER.getAbsolutePath());
        }

        InputStream in = new FileInputStream(clientSecretFilePath);

        //this code frome google API START==>>
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
            clientSecrets, SCOPES).setDataStoreFactory(DATA_STORE_FACTORY).setAccessType("offline").build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");

        return credential;
         //<<== END this code frome google API
    }

    //getting a service fo managingstorage files
    public static Drive getDriveService() throws IOException
    {
        if (_driveService != null)
        {
            return _driveService;
        }
        Credential credential = getCredentials();
        //
        _driveService = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential) //
            .setApplicationName(APPLICATION_NAME).build();
        return _driveService;
    }

}

package mainGUI_FX;

import bases.RootDriveFolderTree;
import bases.RootOSFolderTree;
import bases.TypeOfSync;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Hashtable;

import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import synchronisator.Synchronisator;

/**
 * FXML Controller class
 *
 * @author Shkirmantsev
 */
public class StartGUIWindowController implements Initializable
{
    //
    private PrintStream prntStream ;
    //
    private static volatile Thread threadToStop;

    private File propertyFile;
    private static String rootPathStr;

    
    private TypeOfSync choosedSyncType;

    private MySyncService mySyncService;

    //
    private final Hashtable<String, String> mapTmp = new Hashtable();
    
    @FXML
    private TextArea consoleTextArea;
    

    @FXML
    private Button stopButton;
    @FXML
    private Button startButton;
    @FXML
    private Label statusInfo;
    @FXML
    private Button folderChooser;
    @FXML
    private RadioButton UpdateAllRadio;
    @FXML
    private RadioButton mainDriveRadio;
    @FXML
    private RadioButton mainOSRadio;
    @FXML
    private Button saveMail_ID;
    @FXML
    private TextField discMailField;
    @FXML
    private TextField currentRootLbl;
    @FXML
    private ToggleGroup typeSyncTogGroup;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        prntStream = new PrintStream(new MyConsole(consoleTextArea)) ;
        System.setOut(prntStream);
        System.setErr(prntStream);
        
       
        ///-------------------------
        
        UpdateAllRadio.setUserData((TypeOfSync) TypeOfSync.UPDATEALL);
        mainDriveRadio.setUserData((TypeOfSync) TypeOfSync.MainDRIVE);
        mainOSRadio.setUserData((TypeOfSync) TypeOfSync.MainOS);
        
        this.checkSecret();
        this.checkDB();
        this.checkProperty();

        this.setLastRootPath();
        this.showRootIfPresent();
        this.setLastMail();
        this.showMailIfPresent();

        this.choosedSyncType = (TypeOfSync) typeSyncTogGroup.getSelectedToggle().getUserData();
        //// ADD Listener To toggle Group:
        typeSyncTogGroup.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) ->
        {
            if (typeSyncTogGroup.getSelectedToggle() != null)
            {

                this.choosedSyncType = (TypeOfSync) typeSyncTogGroup.getSelectedToggle().getUserData();
                //System.out.println("New value: "+new_toggle);
                System.out.println("choosedSyncType: " + this.choosedSyncType);

            }
        });

        System.out.println("Default choosedSyncType:" + this.choosedSyncType);

    }

    @FXML
    private void exitFromProgram(ActionEvent event
    )
    {
        System.out.println("EXIT...");
        try
        {
            stopServiceOperation();
        } catch (Exception ex)
        {

            ex.printStackTrace();
        } finally
        {
            Platform.exit();
            System.exit(0);
        }

    }

    @FXML
    private void saveMailName(ActionEvent event
    )
    {
        System.out.println("Save Mail...");
        String mailName = discMailField.getText();
        this.setNewParamInProperty("discMailField", mailName);
        this.showMailIfPresent();

    }

    @FXML
    private void startProgram(ActionEvent event
    )
    {
        System.out.println("Start program...");
        showMailIfPresent();
        saveEnteredPath();
        statusInfo.setTextFill(Color.BLUE);
        statusInfo.setText("synchronization ...");
        startButton.setDisable(true);

        ///////////////////////////////
        this.mySyncService = new MySyncService();

        this.mySyncService.setOnCancelled(worker ->
        {
            doAfterCanceledService();

        });
        this.mySyncService.setOnFailed(new EventHandler<WorkerStateEvent>()
        {
            @Override
            public void handle(WorkerStateEvent worker)
            {
                StringBuilder sb = new StringBuilder();
                for (StackTraceElement element : worker.getSource().getException().getStackTrace())
                {
                    sb.append(element.toString());
                    sb.append("\n");
                }
                System.out.println(sb.toString());
              
                StartGUIWindowController.this.statusInfo.setTextFill(Color.RED);
                StartGUIWindowController.this.statusInfo.setText("sync. is Faled");
                System.out.println("\n Service: sync. is Faled");
//            this.startButton.setDisable(false);
            }
        });

        this.mySyncService.setOnSucceeded(worker ->
        {
            this.statusInfo.setTextFill(Color.GREEN);
            this.statusInfo.setText("sync is COMPLETE");
            System.out.println("Service: sync is COMPLETE");
            this.startButton.setDisable(false);
        });

        ////////////////////////////////
        Boolean bul = this.synchronizeThisRoot();
        if (bul != null || bul.booleanValue())
        {
            statusInfo.setTextFill(Color.GREEN);
//            startButton.setDisable(false);
        } else
        {
            statusInfo.setTextFill(Color.RED);
//            startButton.setDisable(false);
        }

        //typeSyncTogGroup.;
    }

    @FXML
    private void chooseFolder(ActionEvent event
    )
    {

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("."));
        File selectedDirectory = directoryChooser.showDialog(null);
        if (selectedDirectory != null)
        {

            System.out.println(selectedDirectory.getAbsolutePath());
            this.rootPathStr = selectedDirectory.getAbsolutePath().intern();
            currentRootLbl.setText(this.rootPathStr);
            this.setNewParamInProperty("rootPath", this.rootPathStr);
        }

    }
    
      private void checkDB()
    {
        File dir = new File("./DB/dbdrive");
        if (!dir.exists())
        {
            dir.mkdirs();
            System.out.println("created new folder");
        } else
        {
            System.out.println("dir ./DB/dbdrive is elready exist");
        }


    }

            private void checkSecret()
    {
        File dir = Paths.get("./Secret").toFile();
        File file = Paths.get("./Secret/credentials.json").toFile();
        
        if (!dir.exists()||!file.exists())
        {
            dir.mkdirs();
            System.out.println("created new folder \"./Secret");
            System.out.println("\n ==============================================="
                    + "\nPLEASE PUT YOUR CREDENTIAL FILE.Json FROM GOOGLE DRIVE IN FOLDER: \"./Secret\"\n"
                    + "================================================");
            System.out.println("\n NAME OF FILE MUST BE: \"credentials.json\" \n"
                    + "/n================================================");
        } else
        {
            System.out.println("dir \"./Secret\" is elready exist");
        }
        
       
    }



    private void checkProperty()
    {
        File dir = new File("./property");
        if (!dir.exists())
        {
            dir.mkdirs();
            System.out.println("created new folder");
        } else
        {
            System.out.println("dir ./property is elready exist");
        }

        File propfile = new File("./property/myFx.property");
        if (!propfile.exists())
        {
            try
            {
                propfile.createNewFile();
                System.out.println("created new file with property");
                this.propertyFile = propfile;
            } catch (IOException ex)
            {
                System.out.println("Can't create a file ./property/myFx.property");
            }
        } else
        {
            this.propertyFile = propfile;
            System.out.println("file ./property/myFx.property is elready exist");
        }

    }

    private void setLastRootPath()
    {
        this.fillAndApdateMap();
        Optional<String> rootPathOptStr = Optional.ofNullable((String) this.mapTmp.get("rootPath"));
        this.rootPathStr = rootPathOptStr.isPresent() ? rootPathOptStr.get() : null;
    }

    private void setLastMail()
    {
        this.fillAndApdateMap();
        Optional<String> rootPathOptStr = Optional.ofNullable((String) this.mapTmp.get("discMailField"));
        if (rootPathOptStr.isPresent())
        {
            discMailField.setText(rootPathOptStr.get());
        }

    }

    private void saveEnteredPath()
    {
        System.out.println("Save EnteredPath...");
        this.rootPathStr = currentRootLbl.getText();
        this.setNewParamInProperty("rootPath", this.rootPathStr);

    }

    private void showRootIfPresent()
    {
        if (rootPathStr != null)
        {
            currentRootLbl.setText(rootPathStr);
        }
    }

    private void showMailIfPresent()
    {
        String mail = discMailField.getText();
        if (mail != null && !mail.equalsIgnoreCase(""))
        {
            MainJFX.setStageTitle("sync: " + mail);

        }

    }

    private boolean fillAndApdateMap()
    {
        this.mapTmp.clear();
        Properties properties = new Properties();
        FileInputStream fInStream = null;
        try
        {
            fInStream = new FileInputStream(this.propertyFile);
        } catch (FileNotFoundException ex)
        {
            System.out.println("can't read Property file");
            return false;
        }
        try
        {
            properties.load(fInStream);
        } catch (IOException ex)
        {
            System.out.println("can't load Property");
            return false;
        }
        if (!properties.isEmpty())
        {

            properties.entrySet().stream().forEach(entry -> this.mapTmp.put((String) entry.getKey(), (String) entry.getValue()));

        }
        return true;

    }

    private void setNewParamInProperty(String param, String value)
    {
        this.mapTmp.clear();
        this.fillAndApdateMap();
        this.mapTmp.put(param, value);
        Properties properties = new Properties();

        FileOutputStream fOutStream = null;
        try
        {
            fOutStream = new FileOutputStream(this.propertyFile);
            this.mapTmp.entrySet().forEach(entry -> properties.setProperty(entry.getKey(), entry.getValue()));

            properties.store(fOutStream, null);
        } catch (IOException ex)
        {
            System.out.println("can't write Property");
        }

    }

    private boolean synchronizeThisRoot()
    {

        System.out.println("Sync in process...");

        System.out.println("sync. with choosedSyncType:" + this.choosedSyncType);
        System.out.println(String.format("Root Path: \n %s ", this.rootPathStr));

        this.mySyncService.setRootPathStr(this.rootPathStr);
        this.mySyncService.setStatusInfoLabel(this.statusInfo);
        this.mySyncService.setstartButton(this.startButton);
        this.mySyncService.setChoosedSyncType(this.choosedSyncType);

        this.statusInfo.setTextFill(Color.BLUE);
        this.mySyncService.start();
        Boolean bul = this.mySyncService.getValue(); //Can be Null!!!

        return bul != null ? bul.booleanValue() : false;

    }

    private void doAfterCanceledService()
    {
        this.statusInfo.setTextFill(Color.RED);
        this.statusInfo.setText("sync. is canceled by user");
        System.out.println("sync. is canceled by user");
        this.startButton.setDisable(false);
    }

    @FXML
    private boolean stopServiceOperation()
    {

        try
        {

            mySyncService.cancel();
            MySyncService.cancelTask();
        } catch (InterruptedException ex)
        {
            Platform.exit();
            System.exit(0);
        }
        return this.mySyncService.cancel();
    }

    public static String getRootPathStr()
    {
        return rootPathStr;
    }
    ///////////////// NESTED STATIC SERVISE /////////////////////////
    private static class MySyncService extends Service<Boolean>
    {

        private StringProperty rootPathStr = new SimpleStringProperty();
        private Label statusInfo = null;
        private Button startButton;
        private TypeOfSync choosedSyncType;

        private static volatile Task<Boolean> taskToCancel;

        public final void setRootPathStr(String value)
        {
            rootPathStr.set(value);
        }

        public final void setStatusInfoLabel(Label label)
        {
            this.statusInfo = label;
        }

        public final void setstartButton(Button startButton)
        {
            this.startButton = startButton;
        }

        public final void setChoosedSyncType(TypeOfSync choosedSyncType)
        {
            this.choosedSyncType = choosedSyncType;
        }

        public final String getRootPathStr()
        {
            return rootPathStr.get();
        }

        public final StringProperty rootPathStrProperty()
        {
            return rootPathStr;
        }

        public static void cancelTask() throws InterruptedException
        {
            //MySyncService.taskToCancel.cancel();
//            System.out.println(" ===STATE OF THREAD (bofore) ====:" + StartGUIWindowController.threadToStop.getState());

            Thread.getAllStackTraces().keySet().
                stream().
                filter(t -> t.getName().equalsIgnoreCase("mySyncThread"))
                .forEach(t -> t.interrupt());

            Thread.getAllStackTraces().keySet().
                stream().
                filter(t -> t.getName().equalsIgnoreCase("mySyncThread"))
                .forEach(t -> t.stop());
            if(threadToStop!=null){
            StartGUIWindowController.threadToStop.interrupt();
            StartGUIWindowController.threadToStop.stop();}

//            System.out.println(" ===STATE OF THREAD ====:" + StartGUIWindowController.threadToStop.getState());

        }

        @Override
        protected Task<Boolean> createTask()
        {
            final String pathStr = getRootPathStr();

            Task<Boolean> task;
            task = new Task<Boolean>()
            {
                @Override
                protected Boolean call() throws Exception
                {

//                    Thread.currentThread().setDaemon(true);
                    Thread curThread = Thread.currentThread();
                    curThread.setName("mySyncThread");
                    curThread.sleep(1000);
                    StartGUIWindowController.threadToStop = curThread;

                    Boolean bul = Boolean.FALSE;

                    System.out.println("IN Service:...");
//                    throw new Exception("TEST EXCEPTION");

                    //Create Path of root folder
                    Path rootDrivePathInOS = Paths.get(getRootPathStr());
                    //Create Tree of file Objects from Drive root foldrer in PC System
                    RootOSFolderTree.getInstance().chooseRootPath(rootDrivePathInOS);
                    RootOSFolderTree root = RootOSFolderTree.getInstance();
                    RootOSFolderTree.getInstance().toGrowTreeFromThis();
                    //Print files structure
//                    RootOSFolderTree.getInstance().showMeAndMyContent();

                    //            
                    //Create Tree of file Objects from Drive root foldrer in PC System
                    RootDriveFolderTree rootDrive = RootDriveFolderTree.getInstance();
                    RootDriveFolderTree.getInstance().chooseRootPath(rootDrivePathInOS);
                    RootDriveFolderTree.getInstance().toGrowTreeFromThis();
                    //Print files structure
//                      RootDriveFolderTree.getInstance().showMeAndMyContent();

                    Synchronisator synchronisator = Synchronisator.getInstance(rootDrive, root, rootDrivePathInOS);
                    Synchronisator.getInstance().doSynchronysation(choosedSyncType);
                    bul = Boolean.TRUE;
                    /////
                    return bul.booleanValue();

                }
            };
            MySyncService.taskToCancel = task;
            return task;
        }
    }

}

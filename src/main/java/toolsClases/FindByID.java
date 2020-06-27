
package toolsClases;

import java.io.IOException;


import innerLogicTools.GoogleDriveUtils;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
//import com.google.api.services.drive.model.FileList;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Shkirmantsev
 */
public class FindByID
{
    ////////////// HELP FOR PRINTING /////////////////
    static MyLogPrinter printer = new MyLogPrinter(FindByID.class, Level.OFF, Level.WARNING);
    /////////////////////////////////////////////////

    

    //GoogleFile can be Folder or File
    public static File findGoogleFileByID(String fileID) throws IOException
    {
        StringBuffer strMsg=new StringBuffer();
        try
        {   //System.out.println("step1:");
            Drive myDriveService = GoogleDriveUtils.getDriveService();
            //System.out.println("step2:");
            File result = myDriveService
                .files()
                .get(fileID)
                .setFields(ConstantsForGoogleWork.QUERYFIELDS)
                .execute();
            //System.out.println("step3:");
            
            strMsg=strMsg.append("result : ").append(result).append("\n");
            printer.print(Level.FINE,strMsg.toString());
            strMsg.setLength(0);

            String name = result.getName();
            printer.print(Level.FINE,strMsg.append("result name : ").append(name).append("\n")
                .toString() );

            
            strMsg.setLength(0);
                
            if (name == null)
            {
                return null;
            }

            printer.print(Level.FINE,
                String.format("File %2$s with ID=%1$s is found by ID \n", fileID, name));
            //System.out.println("step4 result:");
            return result;
            
        } catch (Exception ex)
        {
            printer.print(Level.WARNING,String.format("File with ID=%s is not found \n", fileID));
        }
        return null;
    }

    public static void main(String[] args) throws IOException
    {

        File findedfile = findGoogleFileByID("1rjWBs7eNFsyuZ46EgBxkN6oTBPhWXdUU"); //"1rjWBs7eNFsyuZ46EgBxkN6oTBPhWXdUU"
         printer.print(Level.FINE,"Mime Type: " + findedfile.getMimeType() + " --- Name: " + findedfile.getName() + " ID: " + findedfile.getId());

         printer.print(Level.FINE,"Done!");
    }

}

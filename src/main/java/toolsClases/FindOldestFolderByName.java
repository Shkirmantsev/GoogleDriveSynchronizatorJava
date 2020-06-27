
package toolsClases;

import com.google.api.services.drive.model.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Shkirmantsev
 */
public class FindOldestFolderByName
{
    
    public static final File oldestFolderByName(String googleFolderIdParent, String subFolderName) throws IOException

    { 
        
        List<File> foldersWithSameName = GetSubFoldersByName.getGoogleSubFolderByName(googleFolderIdParent, subFolderName);
        
        Comparator foldercomparator=new FolderDateComparator();        
        
        Optional<File> oldestFolder=foldersWithSameName!=null?foldersWithSameName.stream().min(foldercomparator):Optional.empty();
       
        return oldestFolder.orElse(null);
    }
   
}

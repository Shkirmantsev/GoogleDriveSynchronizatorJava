
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
public class FindOldestFileByName
{
    
    public static final File oldestSubFileByName(String googleFolderIdParent, String subFileName) throws IOException

    {
        List<File> filesWithSameName = GetSubFilesByName.getGoogleSubFilesByName(googleFolderIdParent, subFileName);
        Comparator filecomparator=new FolderDateComparator();
        
        Optional<File> oldestFile=filesWithSameName!=null?filesWithSameName.stream().min(filecomparator):Optional.empty();
       
        return oldestFile.orElse(null);
        
    }
    /* Compartor
(File o1, File o2) ->
        {
            Long oo1=o1.getCreatedTime().getValue();
            Long oo2=o2.getCreatedTime().getValue();
            return oo1.compareTo(oo2);
        }
*/
}


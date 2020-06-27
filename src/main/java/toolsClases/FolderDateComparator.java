
package toolsClases;

import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.util.Comparator;

/**
 *
 * @author Shkirmantsev
 */
public class FolderDateComparator implements Comparator<File>
{

    @Override
    public int compare(File t, File t1)
    {
        Long tt1=t.getCreatedTime().getValue();
        
        Long tt2=t1.getCreatedTime().getValue();
        return tt1.compareTo(tt2);
        
    }

    
    
}

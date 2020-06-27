package bases;

/**
 * This Class used as DTO in constructor for FileObjOs and DriveFileObj
 *
 * @author Shkirmantsev
 */
public class FileObjDTO
{

    private String generatedName;
    private String trueFileName;
    private String parentID;
    private String myGenParentID;
    private String trueID;
   


    //String myPerentGenPath,
    private Long createdTime;
    private Long modified;
    private boolean isDuplicate;
    private String oldestDuplicateID;
    private boolean isFolder;
    private String mimeTypeGoog;
    private String mimeType;
    //for fileModTimeDTO:
    private String hashName;

    public FileObjDTO(AbstrFolderTree ftree)
    {
        this.generatedName = ftree.getGeneratedName();
        this.trueFileName = ftree.getTrueName();
        this.parentID = ftree.getParentID();
        this.myGenParentID = ftree.getMyGenParentID();
        this.trueID = ftree.getTrueID();
        this.createdTime = ftree.getCreatedTime();
        this.modified = ftree.getModifiedTime();
        this.isDuplicate = ftree.isDuplicate();
        this.oldestDuplicateID = ftree.getOldestDuplicateID();
        this.isFolder = ftree.isIsFolder();
        this.mimeTypeGoog = ftree.getMimeTypeGoog();
        this.mimeType =ftree.getMimeType() ;
        this.hashName=ftree.getMySimulatedID();
    }

    ;    
    public FileObjDTO(
        String generatedName,
        String trueFileName,
        String parentID,
        String myGenParentID,
        String trueID,
        Long createdTime,
        Long modified,
        boolean isDuplicate,
        String oldestDuplicateID,
        boolean isFolder,
        String mimeTypeGoog,
        String mimeType)
    {
        this.generatedName = generatedName;
        this.trueFileName = trueFileName;
        this.parentID = parentID;
        this.myGenParentID = myGenParentID;
        this.trueID = trueID;
        this.createdTime = createdTime;
        this.modified = modified;
        this.isDuplicate = isDuplicate;
        this.oldestDuplicateID = oldestDuplicateID;
        this.isFolder = isFolder;
        this.mimeTypeGoog = mimeTypeGoog;
        this.mimeType = mimeType;
    }

    public FileObjDTO setGeneratedName(String generatedName)
    {
        this.generatedName = generatedName;
        return this;
    }

    public FileObjDTO setTrueFileName(String trueFileName)
    {
        this.trueFileName = trueFileName;
        return this;
    }

    public FileObjDTO setParentID(String parentID)
    {
        this.parentID = parentID;
        return this;
    }

    public FileObjDTO setMyGenParentID(String myGenParentID)
    {
        this.myGenParentID = myGenParentID;
        return this;
    }

    public FileObjDTO setTrueID(String trueID)
    {
        this.trueID = trueID;
        return this;
    }

    public FileObjDTO setCreatedTime(Long createdTime)
    {
        this.createdTime = createdTime;
        return this;
    }

    public FileObjDTO setModified(Long modified)
    {
        this.modified = modified;
        return this;
    }

    public FileObjDTO setIsDuplicate(boolean isDuplicate)
    {
        this.isDuplicate = isDuplicate;
        return this;
    }

    public FileObjDTO setOldestDuplicateID(String oldestDuplicateID)
    {
        this.oldestDuplicateID = oldestDuplicateID;
        return this;
    }

    public FileObjDTO setIsFolder(boolean isFolder)
    {
        this.isFolder = isFolder;
        return this;
    }

    public FileObjDTO setMimeTypeGoog(String mimeTypeGoog)
    {
        this.mimeTypeGoog = mimeTypeGoog;
        return this;
    }

    public FileObjDTO setMimeType(String mimeType)
    {
        this.mimeType = mimeType;
        return this;
    }

    public String getGeneratedName()
    {
        return generatedName;
    }

    public String getTrueFileName()
    {
        return trueFileName;
    }

    public String getParentID()
    {
        return parentID;
    }

    public String getMyGenParentID()
    {
        return myGenParentID;
    }

    public String getTrueID()
    {
        return trueID;
    }

    public Long getCreatedTime()
    {
        return createdTime;
    }

    public Long getModified()
    {
        return modified;
    }

    public boolean isIsDuplicate()
    {
        return isDuplicate;
    }

    public String getOldestDuplicateID()
    {
        return oldestDuplicateID;
    }

    public boolean isIsFolder()
    {
        return isFolder;
    }

    public String getMimeTypeGoog()
    {
        return mimeTypeGoog;
    }

    public String getMimeType()
    {
        return mimeType;
    }

    public String getHashName()
    {
        return hashName;
    }

    public void setHashName(String hashName)
    {
        this.hashName = hashName;
    }
}

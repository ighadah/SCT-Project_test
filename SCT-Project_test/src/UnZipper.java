import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class UnZipper {
	//The distination of the zip files
	final static File folder = new File("/Users/ghadahalghamdi/Documents/NHS-SCT-Subsets");
	
	//loop through the folder and call unzip;
	public void listFilesForFolder(final File folder) throws Exception {
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            listFilesForFolder(fileEntry);
	        } else if(!fileEntry.getName().equals(".DS_Store")) {
	            System.out.println(fileEntry.getName());
	            unzip(fileEntry.getAbsolutePath());
	        }
	    }
	}
	
	
	private void unzip(String zipFile) throws Exception 
	{
	    int BUFFER = 2048;
	    File file = new File(zipFile);
	    @SuppressWarnings("resource")
	    ZipFile zip = new ZipFile(file);
	    //specify the folder directory
	    
	    String newPath = "NHS-SCT-Subsets/Un-zipped/" + zipFile.substring(0, zipFile.length() - 4);
	    new File(newPath).mkdir();
	    @SuppressWarnings("rawtypes")
	    Enumeration zipFileEntries = zip.entries();
	    while (zipFileEntries.hasMoreElements())
	    {
	        ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
	        String currentEntry = entry.getName();
	        File destFile = new File(newPath, currentEntry);
	        File destinationParent = destFile.getParentFile();
	        destinationParent.mkdirs();
	        if (!entry.isDirectory())
	        {
	            BufferedInputStream is = new BufferedInputStream(zip.getInputStream(entry));
	            int currentByte;
	            byte data[] = new byte[BUFFER];
	            FileOutputStream fos = new FileOutputStream(destFile);
	            BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);
	            while ((currentByte = is.read(data, 0, BUFFER)) != -1)
	            {
	                dest.write(data, 0, currentByte);
	            }
	            dest.flush();
	            dest.close();
	            is.close();
	        }
	    }
	}
	
	public static void main(String []args) throws Exception {
		UnZipper uz = new UnZipper();
		uz.listFilesForFolder(folder);
	}

}

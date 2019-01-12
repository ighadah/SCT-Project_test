import java.io.File;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ExcelFileFilter implements java.io.FileFilter {

	@Override
	public boolean accept(File file) {
		// TODO Auto-generated method stub
		 return file != null &&
	                file.isFile() &&
	                file.canRead() &&
	                (file.getName().endsWith("xls")
	                || file.getName().endsWith("xlsx"));
		//return false;
	}
	//ZipFile a;
	//ZipEntry ze;

}

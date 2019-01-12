import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;




public class Files_Processor {
	

	
		private final File_names fn;
		
		public Files_Processor(File_names fn) {
			this.fn = fn;
		}
		
		
		ShortFormProvider shortform = new SimpleShortFormProvider();
		//ShortFormProvider shortform;
		Set<OWLOntology> onts = new HashSet<>();
		//Set<OWLOntology> onts;
		//BidirectionalShortFormProviderAdapter b = 	new BidirectionalShortFormProviderAdapter(onts, shortform);
		BidirectionalShortFormProviderAdapter b;
		//public static File_names file_names;
		//The destination of the excel files
		//final static File excel_folder = new File("/Users/ghadahalghamdi/Documents/NHS-SCT-Subsets");
		//The destination of the owl files
		final static File owl_folder = new File("/Users/ghadahalghamdi/Documents/AAAI-results");
		
		final static File folder = new File("/Users/ghadahalghamdi/git/SCT-Project_test/SCT-Project_test/NHS-SCT-Subsets/Un-zipped/Users/ghadahalghamdi/Documents");
		
		//final static File folder = new File("/Users/ghadahalghamdi/git/SCT-Project_test/SCT-Project_test/ERA-subset");
		//loop through the folder and call unzip;
		public void listFilesForFolder(final File folder) throws Exception {
			List<String> owl_files = new ArrayList<>();
			List<String> excel_files = new ArrayList<>();
	 	    for (final File fileEntry : folder.listFiles()) {
		        if (fileEntry.isDirectory()) {
		            listFilesForFolder(fileEntry);
		        }
		        else if(!fileEntry.getName().equals(".DS_Store")) {
		            //System.out.println(fileEntry.getName());
		         	//System.out.println("file names are " + fileEntry.getPath());
		         	//the file path is the one assigned by the processor
		             String fileExt = getFileExtension(fileEntry);
		             if (fileExt.equals(".xlsx")) {
		            		excel_files.add(fileEntry.getPath());
		            		fn.setExcel_files(excel_files);
		            		//System.out.println("excel_files names are " + excel_files.toString());
		             }
		            	else if(fileExt.equals(".owl")) {
		            		owl_files.add(fileEntry.getPath());
		            		fn.setOwl_files(owl_files);
		            		//System.out.println("owl_files names are " + owl_files.toString());
		            	}
		            
		            //for each fileEntry that has the extension .xlsx
		            //call the method that reads the excel files
		            //inside the method, whenever it founds matching concept id inside the excel with the concepts of the input witnesses 
		            //the method will be called several times (for each witness ontology)
		            //ignore the unzip method for now, we will give it a folder of unzippped files
		            //unzip(fileEntry.getAbsolutePath());
		        }
		        
		    }
	 	    
	 	    System.out.println("OWL files size " + owl_files.size());
	 	    System.out.println("Excel files size " + excel_files.size());
	 	   //return new File_names(excel_files, owl_files);
		    
	 	    //it should return one thing at a time (return list of file names).
	 	    //then do the call of other methods 
	 	    //usually in libraries they do things at one time, but in my case,
	 	    
	 	    //After returning the list of files (names,) we want to take them as inputs to the method
	 	    //(1)(for owl files) first we need to create the owl files from the file names, then converting their entities to short form
	 	    //(2)(for excel files) read them (input excel file, list of owl entities), and get cell values and check...
	 	    //...if owl entities contain the cell value, if so add those in a list of string List<String> excel_file_name_subset
	 	    //then make them an input to the expansion method.
		    
		}
		
		   //either call the method one time and inside if the file extension is zip (do unzip),
		//if the file extension is owl, put it in a list and return that list
		//in both cases you want to return list of files. List<String> or List<Files>?
		
		//process owl files 
		//get owl files
		public Set<OWLOntology> get_owl_files() throws Exception{
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			//return all of the ontologies entitiites
			Set<OWLOntology> ontologies = new HashSet<>();
			List<String> owl_f = fn.getOwl_files();
			System.out.println("*********owl files are  " + owl_f.size());
			for(String file_path: owl_f) {
			OWLOntology ontology = manager.loadOntologyFromOntologyDocument(new File(file_path));
			ontologies.add(ontology);
			b = new BidirectionalShortFormProviderAdapter(ontologies, shortform); 
			}
			return ontologies;
		}
		//get owl entities
		public Set<Set<OWLEntity>> get_owl_entities() throws Exception{
			Set<OWLOntology> onts = get_owl_files();
			Set<OWLEntity> owl_entities = new HashSet<>();
			Set<Set<OWLEntity>> set_of_owl_entities = new HashSet<>();
			for(OWLOntology ontology: onts) {
				
				//this is the owl entities for each (one) ontology
			owl_entities.addAll(ontology.getSignature());
			//this will save all of owl entities of all ontologies in the set
			set_of_owl_entities.add(owl_entities);
			}
			
			System.out.println("Size all of the owl entities is " + set_of_owl_entities.size());
			return set_of_owl_entities;
		}
		//read excel files
		//public
		
		
		//read the owl entities of all owl files and convert them to short forms
		//This method will be called inside read excel files
		public List<String> getShortForm(Set<OWLEntity> owl_entities) {
			//ShortFormProvider shortform = new SimpleShortFormProvider();
			//Set<OWLOntology> onts = new HashSet<>();
			//onts.add(witness_ontology);
			//BidirectionalShortFormProviderAdapter b = 
				//	new BidirectionalShortFormProviderAdapter(onts, shortform);
			List<String> shortForm_of_entities = new ArrayList<>();
			for (OWLEntity e: owl_entities) {
				String s = b.getShortForm(e);
				shortForm_of_entities.add(s);
				
				}
			return shortForm_of_entities;
			}
		
		//read excel files, the input is (one) excel file, another input is the shortForm list of string
		//how to go through each of the list of the files??
		//how to do that......
		//should I go through them in the main (loop around them) //this step should be after the files filtering or names
		//I should do that either in the main file_names.getExcelFiles();
		//In the main after calling ListFilesForFolder, (we will get two list of files excel and owl)
		//Pass the owl files names to (process owl files) 
		//(no need to pass since we can get them using the object that have the filed owl files)
		//Then we will get list of OWLEntities (from the witness ontology)
		//So in the readExcelFiles give it the set of OWLEntities
		//but how to call readExcelFiles for each of the filenames in the list.
		//
		
		//this method should it get the mathced values as list of strings?
		public Set<OWLEntity> getMatchedValues() throws Exception{
			listFilesForFolder(folder);
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			Set<Set<OWLEntity>> owlEntities = this.get_owl_entities();
			List<String> excel_files = fn.getExcel_files();
			System.out.println("*********excel files are  " + excel_files.size()); // the result i 1
			//check it gets excel files
			//System.out.println("Excel files are " + excel_files.toString());
			Set<OWLEntity> matchedEntities = new HashSet<>();
			int i = 0;
			for(Set<OWLEntity> owl_es : owlEntities) {
				for(String file_path: excel_files) {
				i++;
				List<String> matched_concepts = readExcelFiles(file_path, owl_es);
				//we want to make sure that the returned list will then be immediately proccesed by convertToOWLEntity
				//Afraid that this will return the last set only!
				//
				matchedEntities = convertToOWLEntity(matched_concepts, owl_es);
				System.out.println("File #" + i + file_path + " has " + matchedEntities.size() + " number of matched entities.");
				//here save the matched entities to owl ontologies
				//save the entities to owl ontologies
				save_entities(matchedEntities, file_path);
				}
			}
			return matchedEntities;
		}
		
		//save the converted entities to owl ontologies
		public OWLOntology save_entities(Set<OWLEntity> owl_entities, String file_path) throws OWLOntologyCreationException, FileNotFoundException, OWLOntologyStorageException {
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			OWLOntology ontology_matched = manager.createOntology();
			Set<OWLAxiom> axioms_matched = new HashSet<>();
			Filename fileName = new Filename(file_path, '/', '.');
			OutputStream os_matched = new FileOutputStream(new File("/Users/ghadahalghamdi/git/SCT-Project_test/SCT-Project_test/NHS-SCT-Subsets/Un-zipped/Users/ghadahalghamdi/Documents/Matched-entities/" 
			+ fileName.filename() + ".owl"));
			
			OWLDataFactory factory = manager.getOWLDataFactory();
			for(OWLEntity entity : owl_entities) {
			OWLAxiom ax = factory.getOWLDeclarationAxiom(entity);
			axioms_matched.add(ax);
			}
			manager.addAxioms(ontology_matched, axioms_matched);
			manager.saveOntology(ontology_matched, new OWLXMLOntologyFormat(), os_matched);
			
			return ontology_matched;
			}			
		
		//This method reads single excel file
		
		public List<String> readExcelFiles(String filePath, Set<OWLEntity> owlEntities) {
	
			List<String> matched_concepts = new ArrayList<>();
			List<String> shortforms = getShortForm(owlEntities);
			File file = new File(filePath);
			try {
			XSSFWorkbook xb = new XSSFWorkbook(file);
			XSSFSheet sheet = xb.getSheetAt(0);
			XSSFRow row; 
			XSSFCell cell; 
			int rows; // No of rows 
			rows = sheet.getPhysicalNumberOfRows(); 
			int cols = 0; // No of columns 
			int tmp = 0; // This trick ensures that we get the data properly even if it doesn't start from first few rows 
			for(int i = 0; i < 10 || i < rows; i++) { 
				row = sheet.getRow(i); 
				if(row != null) { 
					tmp = sheet.getRow(i).getPhysicalNumberOfCells(); 
					if(tmp > cols) 
						cols = tmp; 
					}
				} 
			for(int r = 0; r < rows; r++) { 
				row = sheet.getRow(r); 
				if(row != null) { 
					for(int c = 0; c < cols; c++) { 
						cell = row.getCell((short)c);
						if(cell != null) { 
							 
							// Your code here
							if(cell.getCellType() == CellType.STRING) {
							//System.out.println("Cell content is " + cell.getStringCellValue());
        						if(shortforms.contains(String.valueOf(cell.getStringCellValue()))){
        							System.out.println("matched concept is " + cell.getStringCellValue());
        							matched_concepts.add(cell.getStringCellValue());
        							}
							}
							//here check if there is a number (concept id) that matches a concept id in the witness ontology
							
							//if(cell.getCellType() == CellType.NUMERIC) {
								//double cellValue = cell.getNumericCellValue();
								//System.out.println("Double value " + cell.getNumericCellValue());
							//	if(shortforms.contains(String.valueOf(cellValue))){
									//if they match then add those cell values to a list
									//return them
									//create a method that appends the SNOMED CT IRI? 
									//and convert these values to owl entity
									//save them in a list
									//contains takes any object while the add should take double
									
								//	 matched_concepts.add(String.valueOf(cellValue));
									// System.out.println("Cell content is " + cell.getNumericCellValue());
							//	}
								
							//}
							
							} 
						} 
					} 
				} 
			} 
			catch(Exception ioe) { ioe.printStackTrace(); }
			//this will return matched entites (cell values)
		return matched_concepts;
		}
		
		//convert the set of the cell values (string to OWLEntity) 
		//This is the last step for the first part
		//the cell values could not been added to 
		public Set<OWLEntity> convertToOWLEntity(List<String> matched_concepts, Set<OWLEntity> owlEntities){
			Set<OWLEntity> matched_entities = new HashSet<>();
			List<String> entities_sf = getShortForm(owlEntities);
		if(!matched_concepts.isEmpty()) {
			//for(String concept: matched_concepts) {
				for(String strE: entities_sf) {
					if(matched_concepts.contains(strE)) {
						OWLEntity entity = b.getEntity(strE);
						matched_entities.add(entity);
					}
			}
			
		}
		return matched_entities;
		}		
		
		//get the file extension
		private String getFileExtension(File file) {
		    String name = file.getPath();
		    int lastIndexOf = name.lastIndexOf(".");
		    if (lastIndexOf == -1) {
		    		System.out.println("last index of is " + lastIndexOf);
		        return ""; // empty extension
		       
		    }
		    return name.substring(lastIndexOf);
		}
		
		public static void main(String args[]) throws Exception {
			File_names fnn = new File_names();
			Files_Processor fp = new Files_Processor(fnn);
			Creating_views cv = new Creating_views(fnn);
			//fp.listFilesForFolder(folder);
			//Set<OWLEntity> matched_entities = fp.getMatchedValues();
			//System.out.println("Matched entities are : " + matched_entities.toString());
			
			//how to know from where those matched entities are coming from?
			cv.Create_views();
		}
		

}

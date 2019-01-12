import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;

import com.google.common.collect.Sets;

import concepts.AtomicConcept;
import converter.BackConverter;
import converter.Converter;
import forgetting.Forgetter;
import formula.Formula;
import preprocessing.PreProcessor;
import roles.AtomicRole;
import uk.ac.manchester.cs.owlapi.modularity.SyntacticLocalityModuleExtractor;

public class Creating_views {

	private final File_names fn;
	
	public Creating_views(File_names fn) {
		this.fn = fn;
	}
	
	final static File matched_folder = new File("/Users/ghadahalghamdi/git/SCT-Project_test/SCT-Project_test/NHS-SCT-Subsets/Un-zipped/Users/ghadahalghamdi/Documents/Matched-entities");
	//method to create modules based on the chosen entities
	
	//This method will be called inside a method that pass each classes (entities) ontology
	
	public OWLOntology extractModules_symbolsSelction(OWLOntology chosen_entities, OWLOntology input_ontology) throws OWLOntologyCreationException, FileNotFoundException, OWLOntologyStorageException {
		Set<OWLEntity> entities_in_onto = new HashSet<>();
		Set<OWLEntity> resulting_entity = new HashSet<>();
		//Set<OWLObjectProperty> op_set = new HashSet<>();
		//Set<OWLClass> c_set = new HashSet<>();
		//should I pass the chosen entities as owl entity objects or as an owl ontology?
				for(OWLEntity e : chosen_entities.getSignature()) {
					if(input_ontology.containsEntityInSignature(e)) {
						entities_in_onto.add(e);
						//System.out.println("Entity in the ontology: " + e);
					}
				}
				int i=0;
				System.out.println("Classes set size is (before selection method) " + entities_in_onto.size());
				for(OWLEntity e : entities_in_onto) {
					if(e.isOWLClass()) {
						//c_set.add(e.asOWLClass());
						resulting_entity.add(e);
						Set<OWLSubClassOfAxiom> subClassofAxioms = input_ontology.getSubClassAxiomsForSubClass(e.asOWLClass());
						
						for(OWLSubClassOfAxiom ax: subClassofAxioms) {
							i++;
							System.out.println("subclassofAxiom # " + i + " of the chosen class in the ontology: " + ax.toString());
							OWLClassExpression ce = ax.getSuperClass();
							
							if(ce.containsConjunct(ce)) {
								Set<OWLClassExpression> ces = ce.asConjunctSet();
								//go through every class expression
								for(OWLClassExpression cess : ces) {
									//check if class expression in the conjunct set is owlclass (atomic one)
									if(cess instanceof OWLClass) {
										//B
										OWLClass cessCl = (OWLClass) cess;
										//c_set.add(cessCl);
										resulting_entity.add(cessCl);
										//check if class expression in the conjunct set is a value of a property
										//check if the atomic class has another axiom (complex?)
									
										//thr.D 
									}else if (cess instanceof OWLObjectSomeValuesFrom) {
										//if yes add all of the properties in the cess exp to the roles set
										//r
										//op_set.addAll(cess.getObjectPropertiesInSignature());	
										resulting_entity.addAll(cess.getObjectPropertiesInSignature());
										//and add the class to the owl classes set!
										//D
										
										//c_set.addAll(cess.getClassesInSignature());
										resulting_entity.addAll(cess.getClassesInSignature());
									}//add another check, if the owlsubclassofaxiom of 
									//classexp in hand doesn't equal empty set then (do the above checks again!)	
								}
							}//check if the class expression of the subclass of axioms is owlclass
							else if(ce instanceof OWLClass) {
									OWLClass ceCl = (OWLClass) ce;
									System.out.println("OWL Class of subclassofAxiom in the ontology: " + ceCl.toString());
									//c_set.add(ceCl);
									resulting_entity.add(ceCl);
								}else if (ce instanceof OWLObjectSomeValuesFrom) {
									//op_set.addAll(ce.getObjectPropertiesInSignature());
									resulting_entity.addAll(ce.getObjectPropertiesInSignature());
									//c_set.addAll(ce.getClassesInSignature());
									resulting_entity.addAll(ce.getClassesInSignature());
									System.out.println("OWLObjectSomeValuesFrom in the ontology: " + ce.toString());
								} 
							}
						} else if(e.isOWLObjectProperty()) {
							//op_set.add(e.asOWLObjectProperty());
							resulting_entity.add(e);
						}
					}
				System.out.println("Entities in the set after selection method " + resulting_entity.size());
			//System.out.println("Properties in the set after selection method " + op_set.size());
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		SyntacticLocalityModuleExtractor sme_original = new SyntacticLocalityModuleExtractor(manager,
				input_ontology, uk.ac.manchester.cs.owlapi.modularity.ModuleType.BOT);
		/*SyntacticLocalityModuleExtractor sme_original = new SyntacticLocalityModuleExtractor(manager,
				input_ontology, uk.ac.manchester.cs.owlapi.modularity.ModuleType.STAR);*/
			/*SyntacticLocalityModuleExtractor sme_original = new SyntacticLocalityModuleExtractor(manager,
		input_ontology, uk.ac.manchester.cs.owlapi.modularity.ModuleType.STAR);*/
		//Set<OWLEntity> seed = new HashSet<>();
		//OWLOntology module_original = sme_original.extractAsOntology(chosen_entities.getSignature(), IRI.generateDocumentIRI());
		OWLOntology module_original = sme_original.extractAsOntology(resulting_entity, IRI.generateDocumentIRI());
		//OWLOntology mod = moduleExtractor.extractAsOntology(seedSig, nOntology.getOntologyID().getOntologyIRI());
		
		return module_original;
		//sc2.close();
	}
	
	//pass the created modules and interpolate for Sigma expanded
	
public OWLOntology FameRC_with_symbols_selection(OWLOntology chosen_entities, OWLOntology onto) throws CloneNotSupportedException, OWLOntologyCreationException, FileNotFoundException, OWLOntologyStorageException {
		
		if (chosen_entities.isEmpty()) {
			return onto;
		}
		
		Converter ct = new Converter();
		PreProcessor pp = new PreProcessor();
		Set<OWLEntity> entities_in_onto = new HashSet<>();
		//Selection of symbols method
		Set<OWLObjectProperty> op_set = new HashSet<>();
		Set<OWLClass> c_set = new HashSet<>();
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		//check if the ontology contain the chosen entities
		//Q: 
		//should I pass the chosen entities as owl entity objects or as an owl ontology?
		for(OWLEntity e : chosen_entities.getSignature()) {
			if(onto.containsEntityInSignature(e)) {
				entities_in_onto.add(e);
				//System.out.println("Entity in the ontology: " + e);
			}
		}
		int i=0;
		System.out.println("Classes set size is (before selection method) " + entities_in_onto.size());
		for(OWLEntity e : entities_in_onto) {
			if(e.isOWLClass()) {
				c_set.add(e.asOWLClass());
				Set<OWLSubClassOfAxiom> subClassofAxioms = onto.getSubClassAxiomsForSubClass(e.asOWLClass());
				
				for(OWLSubClassOfAxiom ax: subClassofAxioms) {
					i++;
				//	System.out.println("subclassofAxiom # " + i + " of the chosen class in the ontology: " + ax.toString());
					OWLClassExpression ce = ax.getSuperClass();
					
					if(ce.containsConjunct(ce)) {
						Set<OWLClassExpression> ces = ce.asConjunctSet();
						//go through every class expression
						for(OWLClassExpression cess : ces) {
							//check if class expression in the conjunct set is owlclass (atomic one)
							if(cess instanceof OWLClass) {
								//B
								OWLClass cessCl = (OWLClass) cess;
								c_set.add(cessCl);
								//check if class expression in the conjunct set is a value of a property
								//check if the atomic class has another axiom (complex?)
							
								//thr.D 
							}else if (cess instanceof OWLObjectSomeValuesFrom) {
								//if yes add all of the properties in the cess exp to the roles set
								//r
								op_set.addAll(cess.getObjectPropertiesInSignature());	
								//and add the class to the owl classes set!
								//D
								
								c_set.addAll(cess.getClassesInSignature());
							}//add another check, if the owlsubclassofaxiom of 
							//classexp in hand doesn't equal empty set then (do the above checks again!)	
						}
					}//check if the class expression of the subclass of axioms is owlclass
					else if(ce instanceof OWLClass) {
							OWLClass ceCl = (OWLClass) ce;
						//	System.out.println("OWL Class of subclassofAxiom in the ontology: " + ceCl.toString());
							c_set.add(ceCl);
						}else if (ce instanceof OWLObjectSomeValuesFrom) {
							op_set.addAll(ce.getObjectPropertiesInSignature());
							c_set.addAll(ce.getClassesInSignature());
					//		System.out.println("OWLObjectSomeValuesFrom in the ontology: " + ce.toString());
						} 
					}
				} else if(e.isOWLObjectProperty()) {
					op_set.add(e.asOWLObjectProperty());	
				}
			}
		
		/*//------------//For the modularisation example
		for(OWLEntity e: entities_in_onto) {
			if(e.isOWLClass()) {
				c_set.add(e.asOWLClass());
			}else if (e.isOWLObjectProperty()) {
				op_set.add(e.asOWLObjectProperty());
			}
		}
		//------------//
*/		System.out.println("Classes in the set after selection method " + c_set.size());
		System.out.println("Properties in the set after selection method " + op_set.size());
		//Make the method accepts owl entities
		//take the difference between the c_set and the ontology classes, and also between the op_set and the ontology object properties
		Set<OWLClass> owl_classes = onto.getClassesInSignature();
		Set<OWLObjectProperty> objectProperties = onto.getObjectPropertiesInSignature();
		Set<OWLClass> forgetting_classes = Sets.difference(owl_classes, c_set);
		Set<OWLObjectProperty> forgetting_properties = Sets.difference(objectProperties, op_set);
		System.out.println("***Forgetting_classes size " + forgetting_classes.size());
		System.out.println("***Forgetting_properties size " + forgetting_properties.size());
		Set<AtomicRole> r_sig = ct.getRolesfromObjectProperties(forgetting_properties);
		Set<AtomicConcept> c_sig = ct.getConceptsfromClasses(forgetting_classes);
		List<Formula> formula_list = pp.getCNF(pp.getSimplifiedForm(pp.getClauses(ct.OntologyConverter(onto))));
		
		Forgetter ft = new Forgetter();
		//List<Formula> uniform_interpolant = ft.Forgetting(r_sig, c_sig, formula_list);
	//	Set<OWLAxiom> uniform_interpolant = bc.toOWLAxioms(bc.toAxioms(pp.getCNF(pp.getSimplifiedForm(forget.Forgetting(role_set, concept_set, formula_list)))));

		BackConverter bc = new BackConverter();
		Set<OWLAxiom> uniform_interpolant = bc.toOWLAxioms(bc.toAxioms(pp.getCNF(pp.getSimplifiedForm(ft.Forgetting(r_sig, c_sig, formula_list)))));
		//OWLOntology result_onto = bc.toOWLOntology(uniform_interpolant);
		OWLOntology result_onto = manager.createOntology();
		manager.addAxioms(result_onto, uniform_interpolant);
		return result_onto;
	}

//The method should read the folder that contains the owl files and for each of them call the create module and create the views
public void Create_views( ) throws Exception {
	//call listFilesForFolder
	//File_names fnn = new File_names();
	Files_Processor fp = new Files_Processor(fn);
	//the method will set fn with owl files
	fp.listFilesForFolder(matched_folder);
	//then get the owl files
	OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	
	String snomed_ct_july_path = "/Users/ghadahalghamdi/Downloads/snomed_ct_simplified/snomed_ct_intl_20170731_simplified.owl";
	OWLOntology snomed_ct_july_ont = manager.loadOntologyFromOntologyDocument(new File(snomed_ct_july_path));
	String snomed_ct_aus_path = "/Users/ghadahalghamdi/Downloads/snomed_ct_simplified/snomed_ct_australian_simplified.owl";
	OWLOntology snomed_ct_aus_ont = manager.loadOntologyFromOntologyDocument(new File(snomed_ct_aus_path));
	
	
	//return all of the ontologies entitiites
	
	List<String> owl_f = fn.getOwl_files();
	int i = 0;
	System.out.println("*********owl files are  " + owl_f.size());
	for(String file_path: owl_f) {
	i++;
	OWLOntology matched_ontology = manager.loadOntologyFromOntologyDocument(new File(file_path));
	//OWLOntology module = extractModules_symbolsSelction(matched_ontology, snomed_ct_aus_ont);
	OWLOntology module = extractModules_symbolsSelction(matched_ontology, snomed_ct_july_ont);
	OWLOntology view = FameRC_with_symbols_selection(matched_ontology, module);
	Filename fileName = new Filename(file_path, '/', '.');
	OutputStream os_result_onto = new FileOutputStream(new File("/Users/ghadahalghamdi/git/SCT-Project_test/SCT-Project_test/NHS-SCT-Subsets/Un-zipped/Users/ghadahalghamdi/Documents/resulting-views/view-" + i + "-"  
	+ fileName.filename() + "-aus.owl"));
	manager.saveOntology(view, new OWLXMLOntologyFormat(), os_result_onto);
	//ontologies.add(module);
	
	}
	//we just want to get the owl files and pass each file to the methods: create module and create view
	//keep in mind that getting owl entities from fn is a list of names, so it's better in this case to call get_owl_files from fp
	//instead of returning the whole set of ontologies at one go, inside the method of getting the owl files call the method of extracting simple modules.. and 
}

//get owl files
		/*public Set<OWLOntology> create_views() throws Exception{
			File_names fn = new File_names();
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			
			String snomed_ct_july_path = "/Users/ghadahalghamdi/Downloads/snomed_ct_simplified/snomed_ct_intl_20170731_simplified.owl";
			OWLOntology snomed_ct_july_ont = manager.loadOntologyFromOntologyDocument(new File(snomed_ct_july_path));
			String snomed_ct_aus_path = "/Users/ghadahalghamdi/Downloads/snomed_ct_simplified/snomed_ct_australian_simplified.owl";
			OWLOntology snomed_ct_aus_ont = manager.loadOntologyFromOntologyDocument(new File(snomed_ct_aus_path));
			
			
			//return all of the ontologies entitiites
			Set<OWLOntology> ontologies = new HashSet<>();
			
			List<String> owl_f = fn.getOwl_files();
			int i = 0;
			System.out.println("*********owl files are  " + owl_f.size());
			for(String file_path: owl_f) {
			i++;
			OWLOntology matched_ontology = manager.loadOntologyFromOntologyDocument(new File(file_path));
			OWLOntology module = extractModules_symbolsSelction(matched_ontology, snomed_ct_aus_ont);
			OWLOntology view = FameRC_with_symbols_selection(matched_ontology, module);
			Filename fileName = new Filename(file_path, '/', '.');
			OutputStream os_result_onto = new FileOutputStream(new File("/Users/ghadahalghamdi/git/SCT-Project_test/SCT-Project_test/NHS-SCT-Subsets/Un-zipped/Users/ghadahalghamdi/Documents/resulting-views/view-" + i + "-"  
			+ fileName.filename() + "-aus.owl"));
			manager.saveOntology(view, new OWLXMLOntologyFormat(), os_result_onto);
			//ontologies.add(module);
			
			}
			return ontologies;
		}
	*/
}

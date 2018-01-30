package pro.taskana.data.generation;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.data.generation.builder.ClassificationBuilder;
import pro.taskana.data.generation.builder.WorkbasketStructureBuilder;
import pro.taskana.data.generation.builder.TaskBuilder;
import pro.taskana.data.generation.persistence.PersistenceService;
import pro.taskana.data.generation.persistence.TaskanaAPI;
import pro.taskana.data.generation.util.DomainPrinter;
import pro.taskana.data.generation.util.ElementStack;
import pro.taskana.data.generation.util.WorkbasketWrapper;
import pro.taskana.impl.ClassificationImpl;
import pro.taskana.impl.TaskImpl;
import pro.taskana.impl.WorkbasketImpl;
import pro.taskana.model.TaskState;
import pro.taskana.model.WorkbasketAccessItem;

/**
 * Class for creating test data.
 * 
 * @author fe
 *
 */
public class PrepareTestData {

	private static final Logger LOGGER = LoggerFactory.getLogger(PrepareTestData.class);
	private static PersistenceService persistenceService;
	private static TaskanaAPI taskana;

	/**
	 * Build test data
	 * 
	 * @param args
	 * @throws SQLException 
	 * @throws NoSuchFieldException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, NoSuchFieldException, SQLException {
		persistenceService = new PersistenceService();
		taskana = new TaskanaAPI();
		
                //taskana.createClassification();
                //taskana.createWorkbasket();
		buildDomainA();
		//buildDomainB();
		//buildDomainC();
	}

	/**
	 * Creates domain A. 
	 * @throws SQLException 
	 * @throws NoSuchFieldException 
	 * @throws FileNotFoundException 
	 */
	private static void buildDomainA() {	 
	    Map<String, List<ClassificationImpl>> classificationsByType = createClassificationsForDomain("A");
	    
		WorkbasketStructureBuilder structureBuilder = new WorkbasketStructureBuilder("A");
		ElementStack<WorkbasketWrapper> personalWorkbaskets = structureBuilder.createSimpleWorkbaskets(50);
		List<WorkbasketWrapper> layer0 = structureBuilder.newLayer().withWb(5).withNumberOfDistTargets(10).selectFrom(personalWorkbaskets)
				.build();
		structureBuilder.newLayer().withWb(1).withDistTargets(layer0).build();	
		persistDomain(structureBuilder);
		DomainPrinter.printStructureOfDomain(structureBuilder);
		
		
		//ADD TASKS
	    TaskBuilder taskBuilder = new TaskBuilder(classificationsByType.get("Aufgabentyp"));
	    List<WorkbasketImpl> wbsInDomain = structureBuilder.getGeneratedWorkbaskets();
	    List<TaskImpl> tasks = taskBuilder.affect(wbsInDomain)
	            .addTasks(TaskState.COMPLETED, 30)
	            .addTasks(TaskState.CLAIMED, 15)
	            .addTasks(TaskState.READY, 15).build();
	    taskana.createTasks(tasks);
	}

	/**
	 * Creates domain B.
	 */
	private static void buildDomainB() {
		WorkbasketStructureBuilder structureBuilder = new WorkbasketStructureBuilder("B");

		ElementStack<WorkbasketWrapper> personalWorkbaskets = structureBuilder.createSimpleWorkbaskets(100);

		List<WorkbasketWrapper> layer0 = structureBuilder.newLayer().withWb(20).withNumberOfDistTargets(5)
				.selectFrom(personalWorkbaskets).build();
		ElementStack<WorkbasketWrapper> layer0Wbs = new ElementStack<>(layer0);

		List<WorkbasketWrapper> layer1 = structureBuilder.newLayer().withWb(4).withNewOwner().withNumberOfDistTargets(5).selectFrom(layer0Wbs).build();

		structureBuilder.newLayer().withWb(1).withDistTargets(layer1).build();

		persistDomain(structureBuilder);
		DomainPrinter.printStructureOfDomain(structureBuilder);
	}

	/**
	 * Creates domain C.
	 */
	private static void buildDomainC() {
		WorkbasketStructureBuilder structureBuilder = new WorkbasketStructureBuilder("C");

		ElementStack<WorkbasketWrapper> personalWorkbaskets = structureBuilder.createSimpleWorkbaskets(27000);

		List<WorkbasketWrapper> layer0FwdTo10 = structureBuilder.newLayer().withWb(1875).withNumberOfDistTargets(15)
				.selectFrom(personalWorkbaskets).build();
		ElementStack<WorkbasketWrapper> layer1Wb = new ElementStack<>(layer0FwdTo10);

		List<WorkbasketWrapper> layer2FwdTo10 = structureBuilder.newLayer().withWb(75).withNewOwner().withNumberOfDistTargets(25)
				.selectFrom(layer1Wb).build();
		ElementStack<WorkbasketWrapper> layer2Wb = new ElementStack<>(layer2FwdTo10);

		structureBuilder.newLayer().withWb(3).withNewOwner().withNumberOfDistTargets(25).selectFrom(layer2Wb).build();
		
		persistDomain(structureBuilder);
		DomainPrinter.printStructureOfDomain(structureBuilder);
	}

    private static Map<String, List<ClassificationImpl>> createClassificationsForDomain(String domain) {
        ClassificationBuilder classificationBuilder = new ClassificationBuilder(domain);
        classificationBuilder.newClassificationCategory("Maschinell").withType("Aufgabentyp").withChildren(100).build();
        classificationBuilder.newClassificationCategory("Manuell").withType("Aufgabentyp").withChildren(100).build();
        classificationBuilder.newClassificationCategory("Extern").withType("Aufgabentyp").withChildren(100).build();
        classificationBuilder.newClassificationCategory("Doktyp_Extern").withType("Dokumenttyp").withChildren(100)
                .build();
        taskana.createClassification(classificationBuilder.getAllGeneratedClassifications());
        return classificationBuilder.getClassificationsByType();
    }

	private static void persistDomain(WorkbasketStructureBuilder domainBuilder) {
		LOGGER.info("Persisting domain {}", domainBuilder.getDomainName());
		List<WorkbasketImpl> wbs = domainBuilder.getGeneratedWorkbaskets();
		List<WorkbasketAccessItem> wbAi = domainBuilder.getGeneratedAccessItems();

		persistenceService.persistWorkbaskets(wbs);
		persistenceService.persistAccessItems(wbAi);

		LOGGER.info("Domain {} with {} workbaskets, {} users and {} access items successfully persisted.",
				domainBuilder.getDomainName(), wbs.size(), domainBuilder.getGeneratedUsers().size(), wbAi.size());
	}

}

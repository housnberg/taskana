package pro.taskana.data.generation;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.TaskState;
import pro.taskana.data.generation.builder.ClassificationBuilder;
import pro.taskana.data.generation.builder.TaskBuilder;
import pro.taskana.data.generation.builder.WorkbasketStructureBuilder;
import pro.taskana.data.generation.persistence.TaskanaAPI;
import pro.taskana.data.generation.util.ClassificationType;
import pro.taskana.data.generation.util.DomainPrinter;
import pro.taskana.data.generation.util.ElementStack;
import pro.taskana.data.generation.util.WorkbasketWrapper;
import pro.taskana.impl.ClassificationImpl;
import pro.taskana.impl.TaskImpl;
import pro.taskana.impl.WorkbasketAccessItemImpl;
import pro.taskana.impl.WorkbasketImpl;



/**
 * Class for creating test data.
 *
 * @author fe
 *
 */
public class PrepareTestData {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrepareTestData.class);
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
        taskana = new TaskanaAPI();

        buildDomainA();
        buildDomainB();
        buildDomainC();
    }

    /**
     * Creates domain A.
     *
     * @throws SQLException
     * @throws NoSuchFieldException
     * @throws FileNotFoundException
     */
    private static void buildDomainA() {
        //Build workbaskets
        WorkbasketStructureBuilder structureBuilder = new WorkbasketStructureBuilder("A");
        ElementStack<WorkbasketWrapper> personalWorkbaskets = structureBuilder.createSimpleWorkbaskets(50);
        List<WorkbasketWrapper> layer0 = structureBuilder
                .newLayer()
                .withWb(5)
                .withNumberOfDistTargets(10)
                .selectFrom(personalWorkbaskets)
                .build();
        structureBuilder
                .newLayer()
                .withWb(1)
                .withDistTargets(layer0)
                .build();
        persistDomain(structureBuilder);

        //Build classifications
        Map<ClassificationType, List<ClassificationImpl>> classificationsByType = createClassificationsForDomain("A");

        //Build tasks
       TaskBuilder taskBuilder = new TaskBuilder(classificationsByType, 150000);
        
        List<WorkbasketWrapper> wbsWithTasks = WorkbasketStructureBuilder.getWorkbasketsForLayer(layer0);

        List<TaskImpl> tasks = taskBuilder
                .affect(halveList(wbsWithTasks))
                .addTasks(TaskState.COMPLETED, 30000)
                .addTasks(TaskState.CLAIMED, 15000)
                .addTasks(TaskState.READY, 15000)
                .withObjectReferences(2)
                .build();
        taskana.createTasks(tasks);

    }

    /**
     * Creates domain B.
     */
    private static void buildDomainB() {
        WorkbasketStructureBuilder structureBuilder = new WorkbasketStructureBuilder("B");

        ElementStack<WorkbasketWrapper> personalWorkbaskets = structureBuilder.createSimpleWorkbaskets(100);

        List<WorkbasketWrapper> layer0 = structureBuilder
                .newLayer()
                .withWb(20)
                .withNumberOfDistTargets(5)
                .selectFrom(personalWorkbaskets).build();
        ElementStack<WorkbasketWrapper> layer0Wbs = new ElementStack<>(layer0);

        List<WorkbasketWrapper> layer1 = structureBuilder
                .newLayer()
                .withWb(4)
                .withNewOwner()
                .withNumberOfDistTargets(5)
                .selectFrom(layer0Wbs)
                .build();

        structureBuilder
                .newLayer()
                .withWb(1)
                .withDistTargets(layer1)
                .build();

        persistDomain(structureBuilder);

        //Build classifications
        Map<ClassificationType, List<ClassificationImpl>> classificationsByType = createClassificationsForDomain("B");

        List<WorkbasketWrapper> wbsWithTasks = WorkbasketStructureBuilder.getWorkbasketsForLayer(layer0);
        
        //Build tasks
        TaskBuilder taskBuilder = new TaskBuilder(classificationsByType, 50000);
        List<TaskImpl> tasks = taskBuilder
                .affect(halveList(wbsWithTasks))
                .addTasks(TaskState.COMPLETED, 5000)
                .addTasks(TaskState.CLAIMED, 2500)
                .addTasks(TaskState.READY, 2500)
                .withAttachments(1)
                .withObjectReferences(2)
                .build();
        taskana.createTasks(tasks);
    }

    /**
     * Creates domain C.
     */
    private static void buildDomainC() {
        WorkbasketStructureBuilder structureBuilder = new WorkbasketStructureBuilder("C");

        ElementStack<WorkbasketWrapper> personalWorkbaskets = structureBuilder.createSimpleWorkbaskets(27000);

        List<WorkbasketWrapper> layer0FwdTo10 = structureBuilder
                .newLayer()
                .withWb(1875)
                .withNumberOfDistTargets(15)
                .selectFrom(personalWorkbaskets)
                .build();
        ElementStack<WorkbasketWrapper> layer1Wb = new ElementStack<>(layer0FwdTo10);

        List<WorkbasketWrapper> layer2FwdTo10 = structureBuilder
                .newLayer()
                .withWb(75)
                .withNewOwner()
                .withNumberOfDistTargets(25)
                .selectFrom(layer1Wb)
                .build();
        ElementStack<WorkbasketWrapper> layer2Wb = new ElementStack<>(layer2FwdTo10);

        List<WorkbasketWrapper> uppermostLayer = structureBuilder
                .newLayer()
                .withWb(3)
                .withNewOwner()
                .withNumberOfDistTargets(25)
                .selectFrom(layer2Wb)
                .build();

        persistDomain(structureBuilder);
        
        //Build classifications
        Map<ClassificationType, List<ClassificationImpl>> classificationsByType = createClassificationsForDomain("C");
        
        List<WorkbasketWrapper> wbsWith0Attachments = WorkbasketStructureBuilder.getWorkbasketsForLayer(uppermostLayer.get(0).getDirectChildren());
        List<WorkbasketWrapper> wbsWith1Attachment = WorkbasketStructureBuilder.getWorkbasketsForLayer(uppermostLayer.get(1).getDirectChildren());
        List<WorkbasketWrapper> wbsWith2Attachments = WorkbasketStructureBuilder.getWorkbasketsForLayer(uppermostLayer.get(2).getDirectChildren());
        
        //Build tasks
        TaskBuilder taskBuilder = new TaskBuilder(classificationsByType, 300000);
        
        List<TaskImpl> tasks = taskBuilder
                .affect(halveList(wbsWith0Attachments))
                .addTasks(TaskState.COMPLETED, 100)
                .addTasks(TaskState.CLAIMED, 50)
                .addTasks(TaskState.READY, 50)
                .withObjectReferences(2)
                .build();
        taskana.createTasks(tasks);
        
        tasks = taskBuilder
                .affect(halveList(wbsWith1Attachment))
                .addTasks(TaskState.COMPLETED, 100)
                .addTasks(TaskState.CLAIMED, 50)
                .addTasks(TaskState.READY, 50)
                .withObjectReferences(2)
                .withAttachments(1)
                .build();
        taskana.createTasks(tasks);
        
        tasks = taskBuilder
                .affect(halveList(wbsWith2Attachments))
                .addTasks(TaskState.COMPLETED, 100)
                .addTasks(TaskState.CLAIMED, 50)
                .addTasks(TaskState.READY, 50)
                .withObjectReferences(2)
                .withAttachments(0)
                .build();
        taskana.createTasks(tasks);
    }

    private static Map<ClassificationType, List<ClassificationImpl>> createClassificationsForDomain(String domain) {
        ClassificationBuilder classificationBuilder = new ClassificationBuilder(domain);
        classificationBuilder.newClassificationCategory("MASCHINELL").withType(ClassificationType.AUFGABENTYP).withChildren(100).build();
        classificationBuilder.newClassificationCategory("MANUELL").withType(ClassificationType.AUFGABENTYP).withChildren(100).build();
        classificationBuilder.newClassificationCategory("EXTERN").withType(ClassificationType.AUFGABENTYP).withChildren(100).build();
        classificationBuilder.newClassificationCategory("DOKTYP_EXTERN").withType(ClassificationType.DOKUMENTTYP).withChildren(100)
                .build();
        taskana.createClassification(classificationBuilder.getAllGeneratedClassifications());
        return classificationBuilder.getClassificationsByType();
    }

    private static void persistDomain(WorkbasketStructureBuilder domainBuilder) {
        LOGGER.info("Persisting domain {}", domainBuilder.getDomainName());
        List<WorkbasketImpl> workbaskets = domainBuilder.getGeneratedWorkbaskets();
        List<WorkbasketAccessItemImpl> workbasketAccessItems = domainBuilder.getGeneratedAccessItems();

        taskana.createWorkbaskets(workbaskets);
        taskana.createWorkbasketAccesItem(workbasketAccessItems);


        DomainPrinter.printStructureOfDomain(domainBuilder);
        LOGGER.info("Domain {} with {} workbaskets, {} users and {} builded.",
                domainBuilder.getDomainName(), workbaskets.size(), domainBuilder.getGeneratedUsers().size(), workbasketAccessItems.size());
    }

    private static List<WorkbasketWrapper> halveList(List<WorkbasketWrapper> listToHalve) {
        return listToHalve.subList(0, listToHalve.size() / 2);
    }
}

package pro.taskana.data.generation;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.TaskState;
import pro.taskana.adapter.TaskanaAPI;
import pro.taskana.data.enums.ClassificationType;
import pro.taskana.data.generation.builder.ClassificationBuilder;
import pro.taskana.data.generation.builder.TaskBuilder;
import pro.taskana.data.generation.builder.WorkbasketStructureBuilder;
import pro.taskana.data.generation.util.ElementStack;
import pro.taskana.data.wrapper.ClassificationWrapper;
import pro.taskana.data.wrapper.DataWrapper;
import pro.taskana.data.wrapper.TaskWrapper;
import pro.taskana.data.wrapper.WorkbasketAccessItemWrapper;
import pro.taskana.data.wrapper.WorkbasketWrapper;
import pro.taskana.export.ScenarioExporter;

/**
 * Class for generate, persist and export test data.
 *
 * @author fe
 * @author el
 *
 */
public class DataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataGenerator.class);

    private static final String OUPUT_PATH_IDENTIFIER = "-o";
    private static TaskanaAPI taskana;

    /**
     * Generate, persist and export test data.
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Path outputDir = null;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals(OUPUT_PATH_IDENTIFIER) && args.length > i + 1) {
                outputDir = Paths.get(args[i + 1]);
            }
        }

        taskana = new TaskanaAPI();

        DataWrapper generatedData;
        generatedData = buildDomainA();
        generatedData = generatedData.union(buildDomainB());
        generatedData = generatedData.union(buildDomainC());

        if (outputDir != null) {
            if (outputDir != null && !Files.exists(outputDir)) {
                Files.createDirectories(outputDir);
            }
            ScenarioExporter.exportData(generatedData, outputDir);
        }
    }

    private static DataWrapper buildDomainA() throws Exception {
        // Build workbaskets
        WorkbasketStructureBuilder structureBuilder = new WorkbasketStructureBuilder("A");
        ElementStack<WorkbasketWrapper> personalWorkbaskets = structureBuilder.createSimpleWorkbaskets(50);
        List<WorkbasketWrapper> layer0 = structureBuilder.newLayer().withWb(5).withNumberOfDistTargets(10)
                .selectFrom(personalWorkbaskets).build();
        structureBuilder.newLayer().withWb(1).withDistTargets(layer0).build();
        persistDomain(structureBuilder);

        // Build classifications
        Map<ClassificationType, List<ClassificationWrapper>> classificationsByType = createClassificationsForDomain(
                "A");

        // Build tasks
        TaskBuilder taskBuilder = new TaskBuilder(classificationsByType, 150000);

        List<WorkbasketWrapper> wbsWithTasks = WorkbasketStructureBuilder.getWorkbasketsForLayer(layer0);

        List<TaskWrapper> tasks = taskBuilder.affect(halveList(wbsWithTasks)).addTasks(TaskState.COMPLETED, 30000)
                .addTasks(TaskState.CLAIMED, 15000).addTasks(TaskState.READY, 15000).withObjectReferences(2).build();
        taskana.createTasks(tasks);

        return new DataWrapper(structureBuilder.getGeneratedWorkbaskets(), tasks,
                classificationsByType.values().stream().flatMap(List::stream).collect(Collectors.toList()));
    }

    private static DataWrapper buildDomainB() throws Exception {
        WorkbasketStructureBuilder structureBuilder = new WorkbasketStructureBuilder("B");

        ElementStack<WorkbasketWrapper> personalWorkbaskets = structureBuilder.createSimpleWorkbaskets(100);

        List<WorkbasketWrapper> layer0 = structureBuilder.newLayer().withWb(20).withNumberOfDistTargets(5)
                .selectFrom(personalWorkbaskets).build();
        ElementStack<WorkbasketWrapper> layer0Wbs = new ElementStack<>(layer0);

        List<WorkbasketWrapper> layer1 = structureBuilder.newLayer().withWb(4).withNewOwner().withNumberOfDistTargets(5)
                .selectFrom(layer0Wbs).build();

        structureBuilder.newLayer().withWb(1).withDistTargets(layer1).build();

        persistDomain(structureBuilder);

        // Build classifications
        Map<ClassificationType, List<ClassificationWrapper>> classificationsByType = createClassificationsForDomain(
                "B");

        List<WorkbasketWrapper> wbsWithTasks = WorkbasketStructureBuilder.getWorkbasketsForLayer(layer0);

        // Build tasks
        TaskBuilder taskBuilder = new TaskBuilder(classificationsByType, 50000);
        List<TaskWrapper> tasks = taskBuilder.affect(halveList(wbsWithTasks)).addTasks(TaskState.COMPLETED, 5000)
                .addTasks(TaskState.CLAIMED, 2500).addTasks(TaskState.READY, 2500).withAttachments(1)
                .withObjectReferences(2).build();
        taskana.createTasks(tasks);
        return new DataWrapper(structureBuilder.getGeneratedWorkbaskets(), tasks,
                classificationsByType.values().stream().flatMap(List::stream).collect(Collectors.toList()));
    }

    private static DataWrapper buildDomainC() throws Exception {
        WorkbasketStructureBuilder structureBuilder = new WorkbasketStructureBuilder("C");

        ElementStack<WorkbasketWrapper> personalWorkbaskets = structureBuilder.createSimpleWorkbaskets(27000);

        List<WorkbasketWrapper> layer0FwdTo10 = structureBuilder.newLayer().withWb(1875).withNumberOfDistTargets(15)
                .selectFrom(personalWorkbaskets).build();
        ElementStack<WorkbasketWrapper> layer1Wb = new ElementStack<>(layer0FwdTo10);

        List<WorkbasketWrapper> layer2FwdTo10 = structureBuilder.newLayer().withWb(75).withNewOwner()
                .withNumberOfDistTargets(25).selectFrom(layer1Wb).build();
        ElementStack<WorkbasketWrapper> layer2Wb = new ElementStack<>(layer2FwdTo10);

        List<WorkbasketWrapper> uppermostLayer = structureBuilder.newLayer().withWb(3).withNewOwner()
                .withNumberOfDistTargets(25).selectFrom(layer2Wb).build();

        persistDomain(structureBuilder);

        // Build classifications
        Map<ClassificationType, List<ClassificationWrapper>> classificationsByType = createClassificationsForDomain(
                "C");

        List<WorkbasketWrapper> wbsWith0Attachments = WorkbasketStructureBuilder
                .getWorkbasketsForLayer(uppermostLayer.get(0).getDirectChildren());
        List<WorkbasketWrapper> wbsWith1Attachment = WorkbasketStructureBuilder
                .getWorkbasketsForLayer(uppermostLayer.get(1).getDirectChildren());
        List<WorkbasketWrapper> wbsWith2Attachments = WorkbasketStructureBuilder
                .getWorkbasketsForLayer(uppermostLayer.get(2).getDirectChildren());

        // Build tasks
        TaskBuilder taskBuilder = new TaskBuilder(classificationsByType, 300000);

        List<TaskWrapper> tasks = taskBuilder.affect(halveList(wbsWith0Attachments)).addTasks(TaskState.COMPLETED, 100)
                .addTasks(TaskState.CLAIMED, 50).addTasks(TaskState.READY, 50).withAttachments(0)
                .withObjectReferences(2).build();
        taskana.createTasks(tasks);

        tasks = taskBuilder.affect(halveList(wbsWith1Attachment)).addTasks(TaskState.COMPLETED, 100)
                .addTasks(TaskState.CLAIMED, 50).addTasks(TaskState.READY, 50).withObjectReferences(2)
                .withAttachments(1).build();
        taskana.createTasks(tasks);

        tasks = taskBuilder.affect(halveList(wbsWith2Attachments)).addTasks(TaskState.COMPLETED, 100)
                .addTasks(TaskState.CLAIMED, 50).addTasks(TaskState.READY, 50).withObjectReferences(2)
                .withAttachments(2).build();
        taskana.createTasks(tasks);
        return new DataWrapper(structureBuilder.getGeneratedWorkbaskets(), tasks,
                classificationsByType.values().stream().flatMap(List::stream).collect(Collectors.toList()));
    }

    private static Map<ClassificationType, List<ClassificationWrapper>> createClassificationsForDomain(String domain)
            throws Exception {
        ClassificationBuilder classificationBuilder = new ClassificationBuilder(domain);
        classificationBuilder.newClassificationCategory("MASCHINELL").withType(ClassificationType.AUFGABENTYP)
                .withChildren(100).build();
        classificationBuilder.newClassificationCategory("MANUELL").withType(ClassificationType.AUFGABENTYP)
                .withChildren(100).build();
        classificationBuilder.newClassificationCategory("EXTERN").withType(ClassificationType.AUFGABENTYP)
                .withChildren(100).build();
        classificationBuilder.newClassificationCategory("DOKTYP_EXTERN").withType(ClassificationType.DOKUMENTTYP)
                .withChildren(100).build();
        taskana.createClassification(classificationBuilder.getAllGeneratedClassifications());
        return classificationBuilder.getClassificationsByType();
    }

    private static void persistDomain(WorkbasketStructureBuilder domainBuilder) throws Exception {
        LOGGER.info("Persisting domain {}", domainBuilder.getDomainName());
        List<WorkbasketWrapper> workbaskets = domainBuilder.getGeneratedWorkbaskets();
        List<WorkbasketAccessItemWrapper> workbasketAccessItems = domainBuilder.getGeneratedAccessItems();

        taskana.createWorkbaskets(workbaskets);
        taskana.createDistributionTargets(workbaskets);
        taskana.createWorkbasketAccesItem(workbasketAccessItems);
        LOGGER.info("Domain {} successfully persisted", domainBuilder.getDomainName());
    }

    private static List<WorkbasketWrapper> halveList(List<WorkbasketWrapper> listToHalve) {
        return listToHalve.subList(0, listToHalve.size() / 2);
    }
}

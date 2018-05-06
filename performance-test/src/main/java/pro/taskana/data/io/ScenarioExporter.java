package pro.taskana.data.io;

import java.nio.file.Path;
import java.util.List;

import pro.taskana.data.generation.util.DataWrapper;
import pro.taskana.data.generation.util.TaskWrapper;
import pro.taskana.data.generation.util.WorkbasketWrapper;

public class ScenarioExporter {

    /**
     * Generate files containing informations of the build test data.
     * 
     * @param data
     *            which was persisted.
     * @param outputDir
     *            output directory for new files.
     */
    public static void exportData(DataWrapper data, Path outputDir) {
        FileUtils fileUtils = new FileUtils(outputDir, FileType.CSV);

        DataExporter<WorkbasketWrapper> ownerKeyExistingTasks = new DataExporter<>(data.workbaskets);
        ownerKeyExistingTasks.addPredicate(wb -> !wb.getTasks().isEmpty());
        ownerKeyExistingTasks.addPredicate(wb -> wb.getDomain().equals("C"));
        
        ownerKeyExistingTasks.addLineValueProducer(wb -> wb.getOwner());
        ownerKeyExistingTasks.addLineValueProducer(wb -> wb.getKey());
        fileUtils.createFile("00_auslesen_einer_aufgabe_aus_einem_postkorb",
                ownerKeyExistingTasks.generateFileContent());

        DataExporter<TaskWrapper> taskIDOwner = new DataExporter<>(data.tasks);
        taskIDOwner.maxLines(100000);
        taskIDOwner.addPredicate(t -> t.getDomain().equals("C"));
        taskIDOwner.addLineValueProducer(t -> t.getId());
        taskIDOwner.addLineValueProducer(t -> data.workbaskets.stream()
                .filter(wb -> wb.getKey().equals(t.getWorkbasketKey())).findFirst().get().getOwner());
        fileUtils.createFile("01_auslesen_einer_aufgabe_per_id", taskIDOwner.generateFileContent());

        DataExporter<TaskWrapper> porOwner = new DataExporter<>(data.tasks);
        taskIDOwner.maxLines(100000);
        porOwner.addPredicate(t -> t.getDomain().equals("C"));
        porOwner.addLineValueProducer(t -> t.getPrimaryObjRef().getValue());
        porOwner.addLineValueProducer(t -> data.workbaskets.stream()
                .filter(wb -> wb.getKey().equals(t.getWorkbasketKey())).findFirst().get().getOwner());
        fileUtils.createFile("02_suchen_von_aufgaben_mit_ordnungsbegriff", porOwner.generateFileContent());

        DataExporter<WorkbasketWrapper> ownerKey = new DataExporter<>(data.workbaskets);
        ownerKey.addPredicate(wb -> wb.getDomain().equals("C"));
        ownerKey.addLineValueProducer(wb -> wb.getOwner());
        ownerKey.addLineValueProducer(wb -> wb.getKey());
        fileUtils.createFile("03_lesen_der_daten_eines_postkorbs", ownerKey.generateFileContent());

        DataExporter<TaskWrapper> typeCategoryCustomDomain = new DataExporter<>(data.tasks);
        typeCategoryCustomDomain.maxLines(100000);
        typeCategoryCustomDomain.addPredicate(t -> t.getDomain().equals("C"));
        typeCategoryCustomDomain.addConstantLineValue(data.workbaskets.iterator().next().getOwner());
        typeCategoryCustomDomain.addLineValueProducer(t -> t.getClassificationSummary().getType());
        typeCategoryCustomDomain.addLineValueProducer(t -> t.getClassificationCategory());
        typeCategoryCustomDomain.addLineValueProducer(t -> t.getClassification().getCustom1());
        typeCategoryCustomDomain.addLineValueProducer(t -> t.getDomain());
        fileUtils.createFile("04_suchen_einer_klassifikation", typeCategoryCustomDomain.generateFileContent());

        DataExporter<TaskWrapper> keyDomain = new DataExporter<>(data.tasks);
        typeCategoryCustomDomain.maxLines(100000);
        keyDomain.addPredicate(t -> t.getDomain().equals("C"));
        keyDomain.addConstantLineValue(data.workbaskets.iterator().next().getOwner());
        keyDomain.addLineValueProducer(t -> t.getClassificationKey());
        keyDomain.addLineValueProducer(t -> t.getDomain());
        fileUtils.createFile("05_lesen_einer_klassifikation", keyDomain.generateFileContent());

        DataExporter<WorkbasketWrapper> ownerKeyForPermission = new DataExporter<>(data.workbaskets);
        ownerKeyForPermission.addPredicate(wb -> wb.getDirectOrIndirectChildren().size() < 20);
        ownerKeyForPermission.addPredicate(wb -> wb.getDomain().equals("C"));
        ownerKeyForPermission.addLineValueProducer(wb -> wb.getOwner());
        ownerKeyForPermission.addLineValueProducer(wb -> wb.getKey());
        List<List<String>> content = ownerKeyForPermission.generateFileContent();
        fileUtils.createFile("07_postkoerbe_suchen_auf_die_der_aufrufer_das_recht_open_hat", content);
        fileUtils.createFile("08_postkoerbe_suchen_auf_die_der_aufrufer_das_recht_append_hat", content);
    }

}

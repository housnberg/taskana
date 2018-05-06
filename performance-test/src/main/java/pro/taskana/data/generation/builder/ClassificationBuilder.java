package pro.taskana.data.generation.builder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import pro.taskana.data.generation.util.ClassificationType;
import pro.taskana.data.generation.util.ClassificationWrapper;
import pro.taskana.impl.ClassificationImpl;

public class ClassificationBuilder {
    
    private Map<ClassificationType, List<ClassificationWrapper>> classificationsByType;
    private static final List<String> CUSTOM_1_VALUES = Arrays.asList("ANR", "VNR", "RVNR", "KOLVNR");
    
    private final String domain;
    private String category;
    private ClassificationType type;
    private int numberOfChildren;
    
    public ClassificationBuilder(String domain) {
        this.domain = domain;
        this.classificationsByType = new HashMap<>();
    }
    
    private void init() {
        category = null;
        type = null;
        numberOfChildren = 0;
    }
    
    public ClassificationBuilder newClassificationCategory(String category) {
        init();
        this.category = category;
        return this;
    }
    
    public ClassificationBuilder withType(ClassificationType type) {
        this.type = type;
        return this;
    }
    
    public ClassificationBuilder withChildren(int numberOfChildren) {
        this.numberOfChildren = numberOfChildren;
        return this;
    }
    
    public List<ClassificationImpl> build() {
        List<ClassificationImpl> result = new ArrayList<>();
        
        ClassificationImpl classificationParent = generateClassification(category, type, null, -1);
        result.add(classificationParent);
        
        for (int i = 0; i < numberOfChildren; i++) {
            ClassificationImpl classificationChild = generateClassification(category, type, classificationParent.getId(), i);
            result.add(classificationChild);
        }
        
        return result;
    }
    
    public List<ClassificationImpl> getAllGeneratedClassifications() {
        return classificationsByType.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }
    
    public Map<ClassificationType, List<ClassificationWrapper>> getClassificationsByType() {
        return classificationsByType;
    }
    
    private ClassificationImpl generateClassification(String category, ClassificationType type, String parentId, int childIndex) {
        ClassificationWrapper classification = new ClassificationWrapper();
        classification.setCategory(category);
        classification.setType(type.toString());
        if(parentId != null) {
            classification.setParentId(parentId);
            classification.setKey(category + "_" + childIndex);
        } else {
            classification.setKey(category);
        }
        classification.setId(UUID.randomUUID().toString());
                
        classification.setDomain(domain);
        classification.setIsValidInDomain(true);
        classification.setCreated(Instant.now());
        
        Collections.shuffle(CUSTOM_1_VALUES);
        classification.setCustom1(CUSTOM_1_VALUES.stream().collect(Collectors.joining(", ")));
        
        initClassificationTypeIfNeccessary(type);
        classificationsByType.get(type).add(classification);
        return classification;
    }
    
    private void initClassificationTypeIfNeccessary(ClassificationType type) {
        if(classificationsByType.containsKey(type)) {
            if(classificationsByType.get(type) == null) {
                classificationsByType.put(type, new ArrayList<>());
            }
        } else {
            classificationsByType.put(type, new ArrayList<>());
        }
    }
    
}

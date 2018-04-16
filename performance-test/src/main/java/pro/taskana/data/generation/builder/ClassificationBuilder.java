package pro.taskana.data.generation.builder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import pro.taskana.data.generation.util.ClassificationType;
import pro.taskana.data.generation.util.ClassificationWrapper;
import pro.taskana.impl.ClassificationImpl;

public class ClassificationBuilder {

    private static final String CHILD_CATEGORY_PREFIX = "SC";
    private static final String PREFIX_CATEGROY_SEPARATOR = "_";
    
    private Map<ClassificationType, List<ClassificationImpl>> classificationsByType;
    
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
        
        ClassificationImpl classificationParent = generateClassification(category, type, null);
        result.add(classificationParent);
        
        for (int i = 0; i < numberOfChildren; i++) {
            ClassificationImpl classificationChild = generateClassification(category, type, classificationParent.getId());
            result.add(classificationChild);
        }
        
        return result;
    }
    
    public List<ClassificationImpl> getAllGeneratedClassifications() {
        return classificationsByType.values().stream().flatMap(List::stream).collect(Collectors.toList());
    }
    
    public Map<ClassificationType, List<ClassificationImpl>> getClassificationsByType() {
        return classificationsByType;
    }
    
    private ClassificationImpl generateClassification(String category, ClassificationType type, String parentId) {
        ClassificationWrapper classification = new ClassificationWrapper();
        classification.setCategory(category);
        classification.setType(type.toString());
        if(parentId != null) {
            classification.setParentId(parentId);
        }
        classification.setId(UUID.randomUUID().toString());
        classification.setKey(classification.getId().substring(0, 32));
        classification.setDomain(domain);
        classification.setIsValidInDomain(true);
        classification.setCreated(Instant.now());
        
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

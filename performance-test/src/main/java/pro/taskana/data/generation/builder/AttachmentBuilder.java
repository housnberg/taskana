package pro.taskana.data.generation.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pro.taskana.Attachment;
import pro.taskana.data.generation.util.ClassificationType;
import pro.taskana.impl.ClassificationImpl;

/**
 *
 * @author EL
 */
public class AttachmentBuilder {
  
    
    private List<ClassificationImpl> documentClassifications;
    
    public AttachmentBuilder(Map<ClassificationType, List<ClassificationImpl>> classifications) {
        this.documentClassifications = classifications.get(ClassificationType.DOKUMENTTYP);
    }
    
    public List<Attachment> getAttachments(int numberOfAttachments) {
        return new ArrayList<>();
    }
}

package fr.sparna.rdf.shacl.shaclplay;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.FileManagerImpl;

public class PreventLoadingIfPresentFileManager extends FileManagerImpl {
    
    private final Model baseModel;

    public PreventLoadingIfPresentFileManager(Model baseModel) {
        this.baseModel = baseModel;
    }

    @Override
    public Model loadModelInternal(String filename) {
        if (baseModel.containsResource(baseModel.createResource(filename))) {
            return baseModel;
        }
        return super.loadModelInternal(filename);
    }
}

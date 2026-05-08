package fr.sparna.rdf.jena.shacl;

import java.util.Comparator;
import java.util.Optional;

import org.apache.jena.rdf.model.Resource;
import org.topbraid.shacl.vocabulary.SH;


public class ShOrderComparator implements Comparator<Resource> {		

    public ShOrderComparator() {
    }

    public static Double getShOrderOf(Resource r) {
        return Optional.ofNullable(r.getProperty(SH.order)).map(s -> s.getDouble()).orElse(null);
    }


    @Override
    public int compare(Resource r1, Resource r2) {
        if (getShOrderOf(r1) != null) {
            if (getShOrderOf(r2) != null) {
                return ((getShOrderOf(r1) - getShOrderOf(r2)) > 0)?1:-1;
            } else {
                return -1;
            }
        } else {
            if (getShOrderOf(r2) != null) {
                return 1;
            } else {
                return 1;
            }
        }
    }
    
}
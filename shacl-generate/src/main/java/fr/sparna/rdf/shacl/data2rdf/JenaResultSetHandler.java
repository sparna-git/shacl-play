package fr.sparna.rdf.shacl.data2rdf;


import org.apache.jena.query.ResultSet;

public interface JenaResultSetHandler<T> {

  T handle(ResultSet resultSet);

}

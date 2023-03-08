package fr.sparna.rdf.jena;


import org.apache.jena.query.ResultSet;

public interface JenaResultSetHandler<T> {

  T handle(ResultSet resultSet);

}

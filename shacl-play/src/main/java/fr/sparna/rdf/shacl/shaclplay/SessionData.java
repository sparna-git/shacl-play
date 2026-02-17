package fr.sparna.rdf.shacl.shaclplay;

import java.util.Locale;

import jakarta.servlet.http.HttpSession;

import org.apache.jena.rdf.model.Model;

import fr.sparna.rdf.shacl.shaclplay.validate.ShapesGraph;

public class SessionData {

	public static final String KEY = SessionData.class.getCanonicalName();

	// The user Locale
	protected Locale userLocale;
	
	private ShapesGraph shapesGraph;
	private Model results;
	private Model validatedData;
	
	/**
	 * Stores this data into session
	 * @param session
	 */
	public void store(HttpSession session) {
		session.setAttribute(KEY, this);
	}
	
	/**
	 * Retrieves the SessionData object stored into the session.
	 * 
	 * @param session
	 * @return
	 */
	public static SessionData get(HttpSession session) {
		return (SessionData)session.getAttribute(KEY);
	}

	public Locale getUserLocale() {
		return userLocale;
	}

	public void setUserLocale(Locale userLocale) {
		this.userLocale = userLocale;
	}

	public Model getResults() {
		return results;
	}

	public void setResults(Model results) {
		this.results = results;
	}

	public Model getValidatedData() {
		return validatedData;
	}

	public void setValidatedData(Model validatedData) {
		this.validatedData = validatedData;
	}

	public ShapesGraph getShapesGraph() {
		return shapesGraph;
	}

	public void setShapesGraph(ShapesGraph shapesGraph) {
		this.shapesGraph = shapesGraph;
	}

}

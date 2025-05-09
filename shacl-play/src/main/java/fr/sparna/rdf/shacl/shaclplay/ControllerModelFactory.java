package fr.sparna.rdf.shacl.shaclplay;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.RiotException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import fr.sparna.rdf.shacl.shaclplay.catalog.AbstractCatalogEntry;
import fr.sparna.rdf.shacl.shaclplay.catalog.Catalog;


public class ControllerModelFactory {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	protected Catalog<? extends AbstractCatalogEntry> catalog;

	protected String sourceName;

	public ControllerModelFactory(Catalog<? extends AbstractCatalogEntry> catalog) {
		super();
		this.catalog = catalog;
	}

	public enum SOURCE_TYPE {
		FILE,
		URL,
		INLINE,
		CATALOG,
		ENDPOINT
	}
	
	public void populateModelFromUrl(Model model, String url) throws ControllerModelException {
		populateModel(model, SOURCE_TYPE.URL, url, null, null, null);
	}

	public void populateModel(
			Model model,
			SOURCE_TYPE source,
			String url,
			String text,
			List<MultipartFile> files,
			String catalogId
	) throws ControllerModelException {

		switch(source) {
		case FILE: {
			// get uploaded file
			if(files.isEmpty()) {
				throw new ControllerModelException("Uploaded file is empty");
			}

			log.debug("Data is in one or more uploaded file : "+files.stream().map(f -> f.getOriginalFilename()).collect(Collectors.joining(", ")));			
			try {
				for (MultipartFile f : files) {

					if(f.getOriginalFilename().endsWith("zip")) {
						log.debug("Detected a zip extension");
						ControllerCommons.populateModelFromZip(model, f.getInputStream());
					} else if(f.getOriginalFilename().endsWith("xls") || f.getOriginalFilename().endsWith("xlsx")) {
						log.debug("Detected a xls/xlsx extension");
						ControllerCommons.populateModelFromExcel(model, f.getInputStream());
					}
					else {
						String lang = RDFLanguages.filenameToLang(f.getOriginalFilename(), Lang.RDFXML).getName();
						log.debug("Detected RDF format "+lang+" from file name "+f.getOriginalFilename());
						ControllerCommons.populateModel(model, f.getInputStream(), lang);
					}

					// shape name is name of file
					this.sourceName = f.getOriginalFilename().substring(0, f.getOriginalFilename().lastIndexOf('.'));
				}
			} catch (Exception e) {
				throw new ControllerModelException(e.getMessage(), e);
			}

			break;
		}
		case URL: {
			log.debug("Data is from a URL "+url);

			try {
				
				URL actualUrl = new URL(url);	
				URLConnection connection = actualUrl.openConnection();
				connection.connect();
				
				// Get the MIME type
				String mimeType = connection.getContentType();
				log.debug("MIME type of the response: " + mimeType);
				
				if(actualUrl.getFile().endsWith("zip")) {
					log.debug("Detected a zip extension");					
					// read zip file
					InputStream FileZip = actualUrl.openStream();					
					ControllerCommons.populateModelFromZip(model, FileZip);
				} else if (
					"application/vnd.ms-excel".equals(mimeType)
					|| 
					"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(mimeType))
				{
					log.debug("Detected an Excel file from the MIME type "+mimeType);
					InputStream FileZip = actualUrl.openStream();
					ControllerCommons.populateModelFromExcel(model, FileZip);
				} else if (actualUrl.getHost().contains("docs.google.com") && actualUrl.getPath().contains("/spreadsheets/")) {
					log.debug("The provided URL is a Google Spreadsheet URL.");
				
					// Ensure "/export?format=xlsx" is placed correctly
					String exportUrl;
					if (actualUrl.getPath().contains("/edit")) {
						// Replace "/edit" and everything after with "/export?format=xlsx"
						exportUrl = url.replaceAll("/edit.*", "/export?format=xlsx");
					} else {
						// Append "/export?format=xlsx" if "/edit" is not present
						exportUrl = url.endsWith("/") ? url + "export?format=xlsx" : url + "/export?format=xlsx";
					}
				
					log.debug("Transformed Google Spreadsheet URL for export: " + exportUrl);
				
					URL excelExportUrl = new URL(exportUrl);
					ControllerCommons.populateModelFromExcel(model, excelExportUrl.openStream());
				} else {
					ControllerCommons.populateModel(model, actualUrl);						
					this.sourceName = getSourceNameForUrl(url);
				}			
			} catch (Exception e) {
				throw new ControllerModelException(e.getMessage(), e);
			}

			if(model.size() == 0) {
				throw new ControllerModelException("No data could be fetched from "+url);
			}

			break;
		}
		case INLINE: {
			log.debug("Data is given inline ");

			try {
				ControllerCommons.populateModel(model, text);
				// shape name is "inline-rdf"
				this.sourceName = "inline-data";
			} catch (RiotException e) {
				throw new ControllerModelException(e.getMessage(), e);
			}

			if(model.size() == 0) {
				throw new ControllerModelException("No data could be parsed from inline text.");
			}

			break;
		}
		case CATALOG: {
			log.debug("Data is from a catalog, ID : "+catalogId);

			AbstractCatalogEntry entry = this.catalog.getCatalogEntryById(catalogId);

			try {
				ControllerCommons.populateModel(model, entry.getTurtleDownloadUrl());
				// shape name is key from catalog
				this.sourceName = catalogId;
			} catch (Exception e) {
				throw new ControllerModelException(e.getMessage(), e);
			}

			if(model.size() == 0) {
				throw new ControllerModelException("No data could be fetched from catalog entry "+catalogId+" at "+entry.getTurtleDownloadUrl());
			}

			break;
		}
		default: {
			throw new ControllerModelException("Cannot determine input source to use");	
		}
		}
	}
	
	public static final String getSourceNameForUrl(String url) {
		try {
			URL actualUrl = new URL(url);
			// shape name is file part of URL
			if(actualUrl.getPath().contains(".")) {
				try {
					return url.substring(url.lastIndexOf('/')+1, url.lastIndexOf('.'));
				} catch (Exception e) {
					return url.substring(url.lastIndexOf('/')+1, url.length());
				}
			} else {
				// no file format in URL, like for shacl-shacl
				return url.substring(url.lastIndexOf('/')+1, url.length());
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return "url";
		}
	}	
	
	public String getSourceName() {
		return sourceName;
	}

}

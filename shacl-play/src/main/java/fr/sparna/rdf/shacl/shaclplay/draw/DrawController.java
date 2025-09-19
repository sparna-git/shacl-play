package fr.sparna.rdf.shacl.shaclplay.draw;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.owasp.encoder.Encode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import fr.sparna.rdf.shacl.diagram.PlantUmlDiagramGenerator;
import fr.sparna.rdf.shacl.diagram.PlantUmlDiagramOutput;
import fr.sparna.rdf.shacl.shaclplay.ApplicationData;
import fr.sparna.rdf.shacl.shaclplay.ControllerModelFactory;
import fr.sparna.rdf.shacl.shaclplay.ControllerModelFactory.SOURCE_TYPE;
import fr.sparna.rdf.shacl.shaclplay.catalog.AbstractCatalogEntry;
import fr.sparna.rdf.shacl.shaclplay.catalog.shapes.ShapesCatalog;
import fr.sparna.rdf.shacl.shaclplay.catalog.shapes.ShapesCatalogService;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.code.TranscoderUtil;


@Controller
public class DrawController {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	protected ApplicationData applicationData;
	
	@Autowired
	protected ShapesCatalogService catalogService;
	
	enum FORMAT {
		
		SVG("image/svg+xml", FileFormat.SVG, "svg"),
		PNG("image/png", FileFormat.PNG, "png"),
		// html page that will contain the SVG diagrams
		HTML("text/html", null, "html"),
		TXT("text/pain", null, "txt");
		
		protected String mimeType;
		protected FileFormat plantUmlFileFormat;
		protected String extension;
		
		private FORMAT(String mimeType, FileFormat plantUmlFileFormat, String extension) {
			this.mimeType = mimeType;
			this.plantUmlFileFormat = plantUmlFileFormat;
			this.extension = extension;
		}

		
	}

	@RequestMapping(
			value = {"draw"},
			method=RequestMethod.GET
	)
	public ModelAndView validate(
			HttpServletRequest request,
			HttpServletResponse response
	){
		DrawFormData vfd = new DrawFormData();
		
		ShapesCatalog catalog = this.catalogService.getShapesCatalog();
		vfd.setCatalog(catalog);
		
		return new ModelAndView("draw-form", DrawFormData.KEY, vfd);	
	}
	
	@RequestMapping(
			value = {"draw"},
			params={"url"},
			method=RequestMethod.GET
	)
	public ModelAndView drawUrl(
			@RequestParam(value="url", required=true) String shapesUrl,
			@RequestParam(value="format", required=false, defaultValue = "svg") String format,
			// hide Properties
			@RequestParam(value="hideProperties", required=false) boolean hideProperties,
			HttpServletRequest request,
			HttpServletResponse response
	){
		try {
			log.debug("drawUrl(shapesUrl='"+shapesUrl+"')");		

			// read format
			FORMAT fmt = FORMAT.valueOf(format.toUpperCase());
			
			Model shapesModel = ModelFactory.createDefaultModel();
			ControllerModelFactory modelPopulator = new ControllerModelFactory(this.catalogService.getShapesCatalog());
			modelPopulator.populateModelFromUrl(shapesModel, shapesUrl);
			log.debug("Done Loading Shapes. Model contains "+shapesModel.size()+" triples");
			doOutputDiagram(
					shapesModel,
					modelPopulator.getSourceName(),
					fmt,
					hideProperties,
					response);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return handleViewFormError(request, e.getClass().getName() +" : "+e.getMessage(), e);
		}
	}
	
	@RequestMapping(
			value="/draw",
			params={"shapesSource"},
			method = RequestMethod.POST
	)
	public ModelAndView draw(
			// radio box indicating type of shapes
			@RequestParam(value="shapesSource", required=true) String shapesSourceString,
			// reference to Shapes URL if shapeSource=sourceShape-inputShapeUrl
			@RequestParam(value="inputShapeUrl", required=false) String shapesUrl,
			// reference to Shapes Catalog ID if shapeSource=sourceShape-inputShapeCatalog
			@RequestParam(value="inputShapeCatalog", required=false) String shapesCatalogId,
			// uploaded shapes if shapeSource=sourceShape-inputShapeFile
			@RequestParam(value="inputShapeFile", required=false) List<MultipartFile> shapesFiles,
			// inline Shapes if shapeSource=sourceShape-inputShapeInline
			@RequestParam(value="inputShapeInline", required=false) String shapesText,
			// output format svg / png
			@RequestParam(value="format", required=false, defaultValue = "svg") String format,
			// hide Properties
			@RequestParam(value="hideProperties", required=false) boolean hideProperties,
			HttpServletRequest request,
			HttpServletResponse response
	) {
		try {
			log.debug("draw(shapeSourceString='"+shapesSourceString+"')");
			
			// get the shapes source type
			ControllerModelFactory.SOURCE_TYPE shapesSource = ControllerModelFactory.SOURCE_TYPE.valueOf(shapesSourceString.toUpperCase());
			
			// read format 
			FORMAT fmt = FORMAT.valueOf(format.toUpperCase());
			
			// if source is a ULR, redirect to the API
			if(shapesSource == SOURCE_TYPE.URL) {
				return new ModelAndView("redirect:/draw?format="+fmt.name().toLowerCase()+"&hideProperties="+hideProperties+"&url="+URLEncoder.encode(shapesUrl, "UTF-8"));
			} else if (shapesSource == SOURCE_TYPE.CATALOG) {
				AbstractCatalogEntry entry = this.catalogService.getShapesCatalog().getCatalogEntryById(shapesCatalogId);
				return new ModelAndView("redirect:/draw?format="+fmt.name().toLowerCase()+"&hideProperties="+hideProperties+"&url="+URLEncoder.encode(entry.getTurtleDownloadUrl().toString(), "UTF-8"));
			} else {
				
			}			
			
			// initialize shapes first			
			log.debug("Determining Shapes source...");
			Model shapesModel = ModelFactory.createDefaultModel();
			ControllerModelFactory modelPopulator = new ControllerModelFactory(this.catalogService.getShapesCatalog());
			modelPopulator.populateModel(
					shapesModel,
					shapesSource,
					shapesUrl,
					shapesText,
					shapesFiles,
					shapesCatalogId
			);
			log.debug("Done Loading Shapes. Model contains "+shapesModel.size()+" triples");
			
			doOutputDiagram(
					shapesModel,
					modelPopulator.getSourceName(),
					fmt,
					hideProperties,
					response
			);
			return null;
			
			
		} catch (Exception e) {
			e.printStackTrace();
			return handleViewFormError(request, e.getClass().getName() +" : "+e.getMessage(), e);
		}
	}
	
	protected void doOutputDiagram(
			Model shapesModel,
			String filename,
			FORMAT format,
			boolean hideProperties,
			HttpServletResponse response
	) throws IOException {
		
		
		// Add a list of prefixes in the model for default
		//shapesModel.setNsPrefixes(shapesModel);
		
		
		PlantUmlDiagramGenerator writer = new PlantUmlDiagramGenerator(
				// includes the subClassOf links in the generated diagram
				true,
				// don't generate hyperlinks
				false,
				// avoid arrows to empty boxes
				true,
				// hide Properties
				hideProperties,
				// a language for label and description reading
				"en"
		);
		
		List<PlantUmlDiagramOutput> diagrams = writer.generateDiagrams(
				shapesModel,
				// OWL Model
				ModelFactory.createDefaultModel()
		);
		
		
		switch(format) {
		case PNG :
		case SVG :
		case TXT : {
			
			if(diagrams.size() == 1) {
				response.setContentType(format.mimeType);
				response.setHeader("Content-Disposition", "inline; filename=\""+filename+"."+format.extension+"\"");
				
				if(format == FORMAT.TXT) {
					response.setCharacterEncoding("UTF-8");
					response.getOutputStream().write(diagrams.get(0).getPlantUmlString().getBytes("UTF-8"));
					response.getOutputStream().flush();
				} else {
					
					SourceStringReader reader = new SourceStringReader(diagrams.get(0).getPlantUmlString());
			        ByteArrayOutputStream baos = new ByteArrayOutputStream();
			        // read class for create a svg or png file
					reader.outputImage(baos, new FileFormatOption(format.plantUmlFileFormat));
					String diagram = baos.toString();
					
			        response.setCharacterEncoding("UTF-8");
			        
			        // display a ong file, generate from PlantUml
			        if (format.plantUmlFileFormat.toString().equals("PNG")) {
			        	
			        	String pngLink = "http://www.plantuml.com/plantuml/png/"+TranscoderUtil.getDefaultTranscoder().encode(diagrams.get(0).getPlantUmlString());
			        	URL pngURL = new URL(pngLink);
			        	InputStream is = pngURL.openStream();
			        	
			        	byte[] b = new byte[2048];
			            int length;
			            while ((length = is.read(b)) != -1) {
			                response.getOutputStream().write(b, 0, length);
			            }
			            is.close();			        	
			        } 		        
			        
			        // fix the problem when is generated a svg file
			        if (format.plantUmlFileFormat.toString().equals("SVG")) {
			        	diagram = diagram.replace("g xmlns=\"\"","g");
			        	diagram = diagram.replace("\" --> \"", "\" - -> \"");			        	
			        	
				        response.getOutputStream().write(diagram.getBytes("UTF-8"));
			        	
			        }
			        
			        response.getOutputStream().flush();
				}
			} else {
				// create a zip
				response.setContentType("application/zip");
				response.setHeader("Content-Disposition", "inline; filename=\""+filename+".zip\"");

				ZipOutputStream zos = new ZipOutputStream(response.getOutputStream(), Charset.forName("UTF-8"));
				zos.setLevel(9);
				
				for (PlantUmlDiagramOutput oneDiagram : diagrams) {
					String uri = oneDiagram.getDiagramUri();
					String localPart;
					if(uri.indexOf('#') > -1) {
						localPart = uri.substring(uri.lastIndexOf('#')+1);
					} else {
						localPart = uri.substring(uri.lastIndexOf('/')+1);
					}
					
					
					if(format == FORMAT.TXT) {
						String entryName = URLEncoder.encode(localPart, "UTF-8") + ".txt";
						zos.putNextEntry(new ZipEntry(entryName));
						zos.write(oneDiagram.getPlantUmlString().getBytes("UTF-8"));
						zos.closeEntry();
					} else {
						String entryName = URLEncoder.encode(localPart, "UTF-8") + "." + format.extension;
						zos.putNextEntry(new ZipEntry(entryName));
						SourceStringReader reader = new SourceStringReader(oneDiagram.getPlantUmlString());
						reader.generateImage(zos, new FileFormatOption(format.plantUmlFileFormat));
						zos.closeEntry();
					}
					
				}
				
				zos.flush();
				zos.close();
				response.flushBuffer();

			}
			
			break;
		}
		case HTML : {
			response.setContentType("text/html");
			response.setHeader("Content-Disposition", "inline; filename=\""+filename+".html\"");
			for (PlantUmlDiagramOutput oneDiagram : diagrams) {
				SourceStringReader reader = new SourceStringReader(oneDiagram.getPlantUmlString());
				if(oneDiagram.getDisplayTitle() != null) {
					response.getOutputStream().write(("<h2>"+oneDiagram.getDisplayTitle()+"</h2>\n").getBytes());					
				}
				if(oneDiagram.getDiagramDescription() != null) {
					response.getOutputStream().write(("<p>"+oneDiagram.getDiagramDescription()+"</p>\n").getBytes());
				}
				// render in SVG inside HTML
				reader.generateImage(response.getOutputStream(), new FileFormatOption(FORMAT.SVG.plantUmlFileFormat));
				response.getOutputStream().write("\n".getBytes());
				response.getOutputStream().write("<hr /><br />\n".getBytes());
			}
			response.flushBuffer();
			break;
		}
		} // end switch
		} // end else
		
	/**
	 * Handles an error in the validation form (stores the message in the Model, then forward to the view).
	 * 
	 * @param request
	 * @param message
	 * @return
	 */
	protected ModelAndView handleViewFormError(
			HttpServletRequest request,
			String message,
			Exception e
	) {
		DrawFormData vfd = new DrawFormData();
		vfd.setErrorMessage(Encode.forHtml(message));
		
		ShapesCatalog catalog = this.catalogService.getShapesCatalog();
		vfd.setCatalog(catalog);
		
		if(e != null) {
			e.printStackTrace();
		}
		return new ModelAndView("draw-form", DrawFormData.KEY, vfd);
	}
	
}

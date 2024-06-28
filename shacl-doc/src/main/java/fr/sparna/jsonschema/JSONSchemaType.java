package fr.sparna.jsonschema;

import java.util.Arrays;
import java.util.Optional;

	
	//Type-specific keywords
    enum JSONSchemaType {

	// TODO : à la place du localName dans "format", il faut avoir l'URI entière du datatype
	STRING("http://www.w3.org/2001/XMLSchema#string", "string"),
	BOOLEAN("http://www.w3.org/2001/XMLSchema#boolean", "boolean"),
	DECIMAL("http://www.w3.org/2001/XMLSchema#integer", "number"),
	INTEGER("http://www.w3.org/2001/XMLSchema#integer", "integer"),
	FLOAT("http://www.w3.org/2001/XMLSchema#float", "number"),
	DATE("http://www.w3.org/2001/XMLSchema#date", "string", "date"),  // with "format", "date"
	TIME("http://www.w3.org/2001/XMLSchema#time", "string", "time"),  // with "format", "time"
	DATETIME("http://www.w3.org/2001/XMLSchema#dateTime", "string", "date-time"),  // with "format", "date-time"
	DATETIMESTAMP("http://www.w3.org/2001/XMLSchema#dateTimeStamp", "string", "date-time"),  // with "format", "date-time"
	GMONTH("http://www.w3.org/2001/XMLSchema#gMonth", "string"),
	GDAY("http://www.w3.org/2001/XMLSchema#gDay", "string"),
	GYEAR("http://www.w3.org/2001/XMLSchema#gYear", "string"),
	GYEARMONTH("http://www.w3.org/2001/XMLSchema#gYearMonth", "string"),
	GMONTHDAY("http://www.w3.org/2001/XMLSchema#gMonthDay", "string"),
	DURATION("http://www.w3.org/2001/XMLSchema#duration", "string"),
	//YEARMONTHDURATION("http://www.w3.org/2001/XMLSchema#yearMonthDuration", "string"),
	//DAYTIMEDURATION("dayTimeDuration", "string"),
	SHORT("http://www.w3.org/2001/XMLSchema#short", "integer"),
	INT("http://www.w3.org/2001/XMLSchema#int", "integer"),
	LONG("http://www.w3.org/2001/XMLSchema#long", "integer"),
	UNSIGNEDBYTE("http://www.w3.org/2001/XMLSchema#unsignedByte", "integer"),
	UNSIGNEDSHORT("http://www.w3.org/2001/XMLSchema#unsignedShort", "integer"),
	UNSIGNEDINT("http://www.w3.org/2001/XMLSchema#unsignedInt", "integer"),
	UNSIGNEDLONG("http://www.w3.org/2001/XMLSchema#unsignedLong", "integer"),
	POSITIVEINTEGER("http://www.w3.org/2001/XMLSchema#positiveInteger", "integer"),
	NONNEGATIVEINTEGER("http://www.w3.org/2001/XMLSchema#nonNegativeInteger", "integer"),
	NEGATIVEINTEGER("http://www.w3.org/2001/XMLSchema#negativeInteger", "integer"),
	NONPOSITIVEINTEGER("http://www.w3.org/2001/XMLSchema#nonPositiveInteger", "integer"),
	HEXBINARY("http://www.w3.org/2001/XMLSchema#hexBinary", "string"),
	BASE64BINARY("http://www.w3.org/2001/XMLSchema#base64Binary", "string"),
	LANGUAGE("http://www.w3.org/2001/XMLSchema#language", "string"),
	NORMALIZEDSTRING("http://www.w3.org/2001/XMLSchema#normalizedString", "string"),
	TOKEN("http://www.w3.org/2001/XMLSchema#token", "string"),
	MNTOKEN("http://www.w3.org/2001/XMLSchema#NMTOKEN", "string"),
	NAME("http://www.w3.org/2001/XMLSchema#Name", "string"),
	NCNAME("http://www.w3.org/2001/XMLSchema#NCName", "string");
    
    private final String datatypeUri;
	private final String jsonSchemaType;
	private final String jsonSchemaFormat;
	
	JSONSchemaType (String datatypeUri,String jsonSchemaType) {
		this.datatypeUri = datatypeUri;
		this.jsonSchemaType = jsonSchemaType;
		this.jsonSchemaFormat = null;
	}

	JSONSchemaType (String datatypeUri,String jsonSchemaType, String jsonSchemaFormat) {
		this.datatypeUri = datatypeUri;
		this.jsonSchemaType = jsonSchemaType;
		this.jsonSchemaFormat = jsonSchemaFormat;
	}

	public String getDatatypeUri() {
		return datatypeUri;
	}

	public String getJsonSchemaType() {
		return jsonSchemaType;
	}

	public String getJsonSchemaFormat() {
		return jsonSchemaFormat;
	}

	
	public static Optional<JSONSchemaType> findByDatatypeUri(String valueType) {
		
		return Arrays.stream(JSONSchemaType.values())
				.filter(e -> e.getDatatypeUri().toString().equals(valueType))
				.findFirst();
	}	
}

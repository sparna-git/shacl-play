package fr.sparna.jsonschema;

import java.util.Arrays;
import java.util.Optional;

	
	//Type-specific keywords
    enum JSONSchemaType {

	// TODO : à la place du localName dans "format", il faut avoir l'URI entière du datatype
	STRING("string", "string"),
	BOOLEAN("boolean", "boolean"),
	DECIMAL("decimal", "number"),
	INTEGER("integer", "integer"),
	FLOAT("float", "number"),
	DATE("date", "string", "date"),  // with "format", "date"
	TIME("time", "string", "time"),  // with "format", "time"
	DATETIME("dateTime", "string", "date-time"),  // with "format", "date-time"
	DATETIMESTAMP("dateTimeStamp", "string", "date-time"),  // with "format", "date-time"
	GMONTH("gMonth", "string"),
	GDAY("gDay", "string"),
	GYEAR("gYear", "string"),
	GYEARMONTH("gYearMonth", "string"),
	GMONTHDAY("gMonthDay", "string"),
	DURATION("duration", "string"),
	YEARMONTHDURATION("yearMonthDuration", "string"),
	DAYTIMEDURATION("dayTimeDuration", "string"),
	SHORT("short", "integer"),
	INT("int", "integer"),
	LONG("long", "integer"),
	UNSIGNEDBYTE("unsignedByte", "integer"),
	UNSIGNEDSHORT("unsignedShort", "integer"),
	UNSIGNEDINT("unsignedInt", "integer"),
	UNSIGNEDLONG("unsignedLong", "integer"),
	POSITIVEINTEGER("positiveInteger", "integer"),
	NONNEGATIVEINTEGER("nonNegativeInteger", "integer"),
	NEGATIVEINTEGER("negativeInteger", "integer"),
	NONPOSITIVEINTEGER("nonPositiveInteger", "integer"),
	HEXBINARY("hexBinary", "string"),
	BASE64BINARY("base64Binary", "string"),
	LANGUAGE("language", "string"),
	NORMALIZEDSTRING("normalizedString", "string"),
	TOKEN("token", "string"),
	MNTOKEN("NMTOKEN", "string"),
	NAME("Name", "string"),
	NCNAME("NCName", "string");
    
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

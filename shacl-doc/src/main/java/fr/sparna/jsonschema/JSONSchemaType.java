package fr.sparna.jsonschema;

import java.util.Arrays;
import java.util.Optional;

	
	//Type-specific keywords
    enum JSONSchemaType {

	// TODO : à la place du localName dans "format", il faut avoir l'URI entière du datatype
	// TODO : il faut gérer l'ajout d'un 
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
	private final String type;
	private final String format;
	
	JSONSchemaType (String datatypeUri,String type) {
		this.datatypeUri = datatypeUri;
		this.type = type;
		this.format = null;
	}

	JSONSchemaType (String datatypeUri,String type, String format) {
		this.datatypeUri = datatypeUri;
		this.type = type;
		this.format = format;
	}

	public String getDatatypeUri() {
		return datatypeUri;
	}

	public String getType() {
		return type;
	}

	public String getFormat() {
		return format;
	}

	
	public static Optional<JSONSchemaType> findByDatatypeUri(String valueType) {
		
		return Arrays.stream(JSONSchemaType.values())
				.filter(e -> e.getDatatypeUri().toString().equals(valueType))
				.findFirst();
	}	
}

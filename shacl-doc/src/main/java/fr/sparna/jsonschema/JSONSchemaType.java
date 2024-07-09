package fr.sparna.jsonschema;

import java.util.Arrays;
import java.util.Optional;


	enum JsonSchemaType {
		STRING("string"),
		INTEGER("integer"),
		NUMBER("number"),
		BOOLEAN("boolean"), 
		DATE("date"), 
		TIME("time"), 
		DATETIME("date-time");
		
		private final String typeString;
		
		JsonSchemaType(String typeString) {
			this.typeString = typeString;
		}
		
		public String getTypeString() {
			return typeString;
		}		
	}

	
	// Type-specific keywords
	enum JSONSchemaType {

		STRING("http://www.w3.org/2001/XMLSchema#string", JsonSchemaType.STRING),
		BOOLEAN("http://www.w3.org/2001/XMLSchema#boolean", JsonSchemaType.BOOLEAN),
		DECIMAL("http://www.w3.org/2001/XMLSchema#decimal", JsonSchemaType.NUMBER),
		INTEGER("http://www.w3.org/2001/XMLSchema#integer", JsonSchemaType.INTEGER),
		FLOAT("http://www.w3.org/2001/XMLSchema#float",JsonSchemaType.NUMBER),
		//DATE("http://www.w3.org/2001/XMLSchema#date", TYPELiteral.STRING, TYPELiteral.DATE), // with "format", "date"
		DATES("http://www.w3.org/2001/XMLSchema#date", JsonSchemaType.STRING), // with "format", "date"
		DATED("http://www.w3.org/2001/XMLSchema#date", JsonSchemaType.DATE), // with "format", "date"
		//TIME("http://www.w3.org/2001/XMLSchema#time", TYPELiteral.STRING, TYPELiteral.TIME), // with "format", "time"
		TIMES("http://www.w3.org/2001/XMLSchema#time", JsonSchemaType.STRING), // with "format", "time"
		TIMET("http://www.w3.org/2001/XMLSchema#time", JsonSchemaType.TIME), // with "format", "time"
		//DATETIME("http://www.w3.org/2001/XMLSchema#dateTime", TYPELiteral.STRING, TYPELiteral.DATETIME), // with "format", "date-time"
		DATETIMES("http://www.w3.org/2001/XMLSchema#dateTime", JsonSchemaType.STRING), // with "format", "date-time"
		DATETIMEDT("http://www.w3.org/2001/XMLSchema#dateTime", JsonSchemaType.DATETIME), // with "format", "date-time"
		//DATETIMESTAMP("http://www.w3.org/2001/XMLSchema#dateTimeStamp", TYPELiteral.STRING, TYPELiteral.DATETIME), // with "format",// "date-time"
		DATETIMESTAMPSDTS("http://www.w3.org/2001/XMLSchema#dateTimeStamp", JsonSchemaType.STRING), // with "format",// "date-time"
		DATETIMESTAMPDDTS("http://www.w3.org/2001/XMLSchema#dateTimeStamp", JsonSchemaType.DATETIME), // with "format",// "date-time"
		GMONTH("http://www.w3.org/2001/XMLSchema#gMonth", JsonSchemaType.STRING),
		GDAY("http://www.w3.org/2001/XMLSchema#gDay", JsonSchemaType.STRING),
		GYEAR("http://www.w3.org/2001/XMLSchema#gYear", JsonSchemaType.STRING),
		GYEARMONTH("http://www.w3.org/2001/XMLSchema#gYearMonth", JsonSchemaType.STRING),
		GMONTHDAY("http://www.w3.org/2001/XMLSchema#gMonthDay", JsonSchemaType.STRING),
		DURATION("http://www.w3.org/2001/XMLSchema#duration", JsonSchemaType.STRING),
		// YEARMONTHDURATION("http://www.w3.org/2001/XMLSchema#yearMonthDuration",
		// "string"),
		// DAYTIMEDURATION("dayTimeDuration", "string"),
		SHORT("http://www.w3.org/2001/XMLSchema#short", JsonSchemaType.INTEGER),
		INT("http://www.w3.org/2001/XMLSchema#int", JsonSchemaType.INTEGER),
		LONG("http://www.w3.org/2001/XMLSchema#long", JsonSchemaType.INTEGER),
		UNSIGNEDBYTE("http://www.w3.org/2001/XMLSchema#unsignedByte", JsonSchemaType.INTEGER),
		UNSIGNEDSHORT("http://www.w3.org/2001/XMLSchema#unsignedShort", JsonSchemaType.INTEGER),
		UNSIGNEDINT("http://www.w3.org/2001/XMLSchema#unsignedInt", JsonSchemaType.INTEGER),
		UNSIGNEDLONG("http://www.w3.org/2001/XMLSchema#unsignedLong", JsonSchemaType.INTEGER),
		POSITIVEINTEGER("http://www.w3.org/2001/XMLSchema#positiveInteger", JsonSchemaType.INTEGER),
		NONNEGATIVEINTEGER("http://www.w3.org/2001/XMLSchema#nonNegativeInteger", JsonSchemaType.INTEGER),
		NEGATIVEINTEGER("http://www.w3.org/2001/XMLSchema#negativeInteger", JsonSchemaType.INTEGER),
		NONPOSITIVEINTEGER("http://www.w3.org/2001/XMLSchema#nonPositiveInteger", JsonSchemaType.INTEGER),
		HEXBINARY("http://www.w3.org/2001/XMLSchema#hexBinary", JsonSchemaType.STRING),
		BASE64BINARY("http://www.w3.org/2001/XMLSchema#base64Binary", JsonSchemaType.STRING),
		LANGUAGE("http://www.w3.org/2001/XMLSchema#language", JsonSchemaType.STRING),
		NORMALIZEDSTRING("http://www.w3.org/2001/XMLSchema#normalizedString", JsonSchemaType.STRING),
		TOKEN("http://www.w3.org/2001/XMLSchema#token", JsonSchemaType.STRING),
		MNTOKEN("http://www.w3.org/2001/XMLSchema#NMTOKEN", JsonSchemaType.STRING),
		NAME("http://www.w3.org/2001/XMLSchema#Name", JsonSchemaType.STRING),
		NCNAME("http://www.w3.org/2001/XMLSchema#NCName", JsonSchemaType.STRING);

		private final String datatypeUri;
		private final String jsonSchemaType;
		private final String jsonSchemaFormat;
		private final JsonSchemaType jsonSchemaLiteral;
		
		JSONSchemaType(String datatypeUri, String jsonSchemaType,JsonSchemaType jsonSchemaLiteral) {
			this.datatypeUri = datatypeUri;
			this.jsonSchemaType = jsonSchemaType;
			this.jsonSchemaFormat = null;
			this.jsonSchemaLiteral = jsonSchemaLiteral;
		}

		JSONSchemaType(String datatypeUri, String jsonSchemaType, String jsonSchemaFormat,JsonSchemaType jsonSchemaLiteral) {
			this.datatypeUri = datatypeUri;
			this.jsonSchemaType = jsonSchemaType;
			this.jsonSchemaFormat = jsonSchemaFormat;
			this.jsonSchemaLiteral = jsonSchemaLiteral;
		}

		JSONSchemaType(String datatypeUri, JsonSchemaType jsonSchemaLiteral) {
			this.datatypeUri = datatypeUri;
			this.jsonSchemaType = null;
			this.jsonSchemaFormat = null;
			this.jsonSchemaLiteral = jsonSchemaLiteral;
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
		
		public JsonSchemaType getJsonSchemaLiteral() {
			return jsonSchemaLiteral;
		}
		

		public static Optional<JSONSchemaType> findByDatatypeUri(String valueType) {

			return Arrays.stream(JSONSchemaType.values()).filter(e -> e.getDatatypeUri().toString().equals(valueType))
					.findFirst();
		}
	}
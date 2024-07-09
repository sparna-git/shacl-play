package fr.sparna.jsonschema;

import java.util.Arrays;
import java.util.Optional;


	enum TYPELiteral {
		STRING("string"),
		INTEGER("integer"),
		NUMBER("number"),
		BOOLEAN("boolean"), 
		DATE("date"), 
		TIME("time"), 
		DATETIME("date-time");
		
		private final String typeliteral;
		
		TYPELiteral(String typeliteral) {
			this.typeliteral = typeliteral;
		}
		
		public String getType_literal() {
			return typeliteral;
		}
		
	}

	
	// Type-specific keywords
	enum JSONSchemaType {

		// TODO : à la place du localName dans "format", il faut avoir l'URI entière du
		// datatype
		STRING("http://www.w3.org/2001/XMLSchema#string", TYPELiteral.STRING),
		BOOLEAN("http://www.w3.org/2001/XMLSchema#boolean", TYPELiteral.BOOLEAN),
		DECIMAL("http://www.w3.org/2001/XMLSchema#decimal", TYPELiteral.NUMBER),
		INTEGER("http://www.w3.org/2001/XMLSchema#integer", TYPELiteral.INTEGER),
		FLOAT("http://www.w3.org/2001/XMLSchema#float",TYPELiteral.NUMBER),
		//DATE("http://www.w3.org/2001/XMLSchema#date", TYPELiteral.STRING, TYPELiteral.DATE), // with "format", "date"
		DATES("http://www.w3.org/2001/XMLSchema#date", TYPELiteral.STRING), // with "format", "date"
		DATED("http://www.w3.org/2001/XMLSchema#date", TYPELiteral.DATE), // with "format", "date"
		//TIME("http://www.w3.org/2001/XMLSchema#time", TYPELiteral.STRING, TYPELiteral.TIME), // with "format", "time"
		TIMES("http://www.w3.org/2001/XMLSchema#time", TYPELiteral.STRING), // with "format", "time"
		TIMET("http://www.w3.org/2001/XMLSchema#time", TYPELiteral.TIME), // with "format", "time"
		//DATETIME("http://www.w3.org/2001/XMLSchema#dateTime", TYPELiteral.STRING, TYPELiteral.DATETIME), // with "format", "date-time"
		DATETIMES("http://www.w3.org/2001/XMLSchema#dateTime", TYPELiteral.STRING), // with "format", "date-time"
		DATETIMEDT("http://www.w3.org/2001/XMLSchema#dateTime", TYPELiteral.DATETIME), // with "format", "date-time"
		//DATETIMESTAMP("http://www.w3.org/2001/XMLSchema#dateTimeStamp", TYPELiteral.STRING, TYPELiteral.DATETIME), // with "format",// "date-time"
		DATETIMESTAMPSDTS("http://www.w3.org/2001/XMLSchema#dateTimeStamp", TYPELiteral.STRING), // with "format",// "date-time"
		DATETIMESTAMPDDTS("http://www.w3.org/2001/XMLSchema#dateTimeStamp", TYPELiteral.DATETIME), // with "format",// "date-time"
		GMONTH("http://www.w3.org/2001/XMLSchema#gMonth", TYPELiteral.STRING),
		GDAY("http://www.w3.org/2001/XMLSchema#gDay", TYPELiteral.STRING),
		GYEAR("http://www.w3.org/2001/XMLSchema#gYear", TYPELiteral.STRING),
		GYEARMONTH("http://www.w3.org/2001/XMLSchema#gYearMonth", TYPELiteral.STRING),
		GMONTHDAY("http://www.w3.org/2001/XMLSchema#gMonthDay", TYPELiteral.STRING),
		DURATION("http://www.w3.org/2001/XMLSchema#duration", TYPELiteral.STRING),
		// YEARMONTHDURATION("http://www.w3.org/2001/XMLSchema#yearMonthDuration",
		// "string"),
		// DAYTIMEDURATION("dayTimeDuration", "string"),
		SHORT("http://www.w3.org/2001/XMLSchema#short", TYPELiteral.INTEGER),
		INT("http://www.w3.org/2001/XMLSchema#int", TYPELiteral.INTEGER),
		LONG("http://www.w3.org/2001/XMLSchema#long", TYPELiteral.INTEGER),
		UNSIGNEDBYTE("http://www.w3.org/2001/XMLSchema#unsignedByte", TYPELiteral.INTEGER),
		UNSIGNEDSHORT("http://www.w3.org/2001/XMLSchema#unsignedShort", TYPELiteral.INTEGER),
		UNSIGNEDINT("http://www.w3.org/2001/XMLSchema#unsignedInt", TYPELiteral.INTEGER),
		UNSIGNEDLONG("http://www.w3.org/2001/XMLSchema#unsignedLong", TYPELiteral.INTEGER),
		POSITIVEINTEGER("http://www.w3.org/2001/XMLSchema#positiveInteger", TYPELiteral.INTEGER),
		NONNEGATIVEINTEGER("http://www.w3.org/2001/XMLSchema#nonNegativeInteger", TYPELiteral.INTEGER),
		NEGATIVEINTEGER("http://www.w3.org/2001/XMLSchema#negativeInteger", TYPELiteral.INTEGER),
		NONPOSITIVEINTEGER("http://www.w3.org/2001/XMLSchema#nonPositiveInteger", TYPELiteral.INTEGER),
		HEXBINARY("http://www.w3.org/2001/XMLSchema#hexBinary", TYPELiteral.STRING),
		BASE64BINARY("http://www.w3.org/2001/XMLSchema#base64Binary", TYPELiteral.STRING),
		LANGUAGE("http://www.w3.org/2001/XMLSchema#language", TYPELiteral.STRING),
		NORMALIZEDSTRING("http://www.w3.org/2001/XMLSchema#normalizedString", TYPELiteral.STRING),
		TOKEN("http://www.w3.org/2001/XMLSchema#token", TYPELiteral.STRING),
		MNTOKEN("http://www.w3.org/2001/XMLSchema#NMTOKEN", TYPELiteral.STRING),
		NAME("http://www.w3.org/2001/XMLSchema#Name", TYPELiteral.STRING),
		NCNAME("http://www.w3.org/2001/XMLSchema#NCName", TYPELiteral.STRING);

		private final String datatypeUri;
		private final String jsonSchemaType;
		private final String jsonSchemaFormat;
		private final TYPELiteral jsonSchemaLiteral;
		
		JSONSchemaType(String datatypeUri, String jsonSchemaType,TYPELiteral jsonSchemaLiteral) {
			this.datatypeUri = datatypeUri;
			this.jsonSchemaType = jsonSchemaType;
			this.jsonSchemaFormat = null;
			this.jsonSchemaLiteral = jsonSchemaLiteral;
		}

		JSONSchemaType(String datatypeUri, String jsonSchemaType, String jsonSchemaFormat,TYPELiteral jsonSchemaLiteral) {
			this.datatypeUri = datatypeUri;
			this.jsonSchemaType = jsonSchemaType;
			this.jsonSchemaFormat = jsonSchemaFormat;
			this.jsonSchemaLiteral = jsonSchemaLiteral;
		}

		JSONSchemaType(String datatypeUri, TYPELiteral jsonSchemaLiteral) {
			// TODO Auto-generated constructor stub
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
		
		public TYPELiteral getJsonSchemaLiteral() {
			return jsonSchemaLiteral;
		}
		

		public static Optional<JSONSchemaType> findByDatatypeUri(String valueType) {

			return Arrays.stream(JSONSchemaType.values()).filter(e -> e.getDatatypeUri().toString().equals(valueType))
					.findFirst();
		}
	}
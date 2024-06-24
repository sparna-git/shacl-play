package fr.sparna.jsonschema;

import java.util.Arrays;
import java.util.Optional;

	//Type-specific keywords
    enum JSONTYPE {

	STRING("string", "string"),
	BOOLEAN("boolean", "boolean"),
	DECIMAL("decimal", "number"),
	INTEGER("integer", "integer"),
	FLOAT("float", "number"),
	DATE("date", "string"),  // with "format", "date"
	TIME("time", "string"),  // with "format", "time"
	DATETIME("dateTime", "string"),  // with "format", "date-time"
	DATETIMESTAMP("dateTimeStamp", "string"),  // with "format", "date-time"
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
	NCNAME("NCName", "string"),
	LANGSTRING("langString","string"),
	PUBLICREGISTER("publicRegister","string");
	
	private final String formatType;
	private final String type;
	
	JSONTYPE (String format,String type) {
		this.formatType = format;
		this.type = type;
	}

	public String getFormatType() {
		return formatType;
	}

	public String getType() {
		return type;
	}
	
	public static Optional<JSONTYPE> findTyeValue(String valueType) {
		
		return Arrays.stream(JSONTYPE.values())
				.filter(e -> e.getFormatType().toString().equals(valueType))
				.findFirst();
	}	
}

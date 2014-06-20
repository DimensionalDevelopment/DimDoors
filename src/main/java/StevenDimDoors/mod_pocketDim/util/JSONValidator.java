package StevenDimDoors.mod_pocketDim.util;

import static java.util.Collections.singleton;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

public class JSONValidator
{

	static final public String TYPE = "type";
	static final public String ANY = "any";
	static final public String PROPERTIES = "properties";
	static final public String OPTIONAL = "optional";
	static final public String ADDITIONAL_PROPERTIES = "additionalProperties";
	static final public String MIN_LENGTH = "minLength";
	static final public String MAX_LENGTH = "maxLength";
	static final public String MINIMUM = "minimum";
	static final public String MAXIMUM = "maximum";
	static final public String PATTERN = "pattern";
	static final public String ITEMS = "items";
	static final public String ENUM = "enum";
	static final public String REQUIRED = "required";


	JsonObject schema;

	public JsonObject getSchema()
	{
		return schema;
	}

	public JSONValidator(JsonObject schema)
	{
		this.schema = schema;
	}

	static class WrongType extends JsonParseException
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		WrongType(String msg)
		{
			super(msg);
		}

		static WrongType generate(String path, Set<Type> types, Type found)
		{
			boolean first = true;
			String typeList = "'unknown'";
			for (Type type : types)
			{
				if (first)
				{
					typeList = "'" + type.getTypeString() + "'";
					first = false;
				}
				else
				{
					typeList += " or '" + type.getTypeString() + "'";
				}
			}

			return new WrongType("Invalid: Expected type " + typeList + " at '" + path + "', but " + "found type '" + found.getTypeString() + "'");
		}
	}

	static enum Type
	{
		STRING("string"), NUMBER("number"), INTEGER("integer"), BOOLEAN("boolean"), OBJECT("object"), ARRAY("array"), NULL("null");

		String typeString;

		Type(String typeString)
		{
			this.typeString = typeString;
		}

		public String getTypeString()
		{
			return typeString;
		}
	}

	static Set<Type> anyTypeSet()
	{
		HashSet<Type> hashSet = new HashSet<Type>();
		hashSet.add(Type.STRING);
		hashSet.add(Type.NUMBER);
		hashSet.add(Type.INTEGER);
		hashSet.add(Type.BOOLEAN);
		hashSet.add(Type.OBJECT);
		hashSet.add(Type.ARRAY);
		hashSet.add(Type.NULL);
		return hashSet;
	}

	static Set<Type> getSimpleType(String path, String type)
	{
		for (Type t : Type.values())
		{
			if (t.getTypeString().equals(type))
			{
				if (t != Type.NUMBER)
				{
					return singleton(t);
				}
				else
				{
					HashSet<Type> set = new HashSet<Type>();
					set.add(Type.NUMBER);
					set.add(Type.INTEGER);
					return set;
				}
			}
		}

		if (ANY.equals(type))
		{
			return anyTypeSet();
		}

		// Unknown type, spec says to allow any.
		return anyTypeSet();
	}

	static Set<Type> getTypeSet(String path, JsonObject schema) throws JsonParseException
	{
		JsonElement typeElement = schema.get(TYPE);

		if (typeElement == null)
		{
			// Spec says that a missing type object means accept any type.
			return anyTypeSet();
		}

		if (typeElement.isJsonPrimitive())
		{
			JsonPrimitive primitive = typeElement.getAsJsonPrimitive();
			if (primitive.isString())
			{
				return getSimpleType(path, primitive.getAsString());
			}
		}

		if (typeElement.isJsonArray())
		{
			HashSet<Type> set = new HashSet<Type>();
			JsonArray array = typeElement.getAsJsonArray();
			for (JsonElement element : array)
			{
				if (element.isJsonPrimitive())
				{
					JsonPrimitive primitive = element.getAsJsonPrimitive();
					if (primitive.isString())
					{
						set.addAll(getSimpleType(path, primitive.getAsString()));
					}
				}

				// Unknown type. Accept all.
				return anyTypeSet();
			}
		}

		// Don't know what this is, assume any.
		return anyTypeSet();
	}

	static Type getType(JsonElement element)
	{
		if (element.isJsonArray())
		{
			return Type.ARRAY;
		}
		if (element.isJsonObject())
		{
			return Type.OBJECT;
		}
		if (element.isJsonNull())
		{
			return Type.NULL;
		}
		JsonPrimitive primitive = element.getAsJsonPrimitive();
		if (primitive.isString())
		{
			return Type.STRING;
		}
		if (primitive.isBoolean())
		{
			return Type.BOOLEAN;
		}
		if (primitive.isNumber())
		{
			BigDecimal decimal = primitive.getAsBigDecimal();
			int scale = decimal.scale();
			if (scale > 0)
			{
				return Type.NUMBER;
			}
			else
			{
				return Type.INTEGER;
			}
		}

		// Don't know. Punt and call it a string.
		return Type.STRING;
	}

	static void validateObject(String path, JsonObject schema, JsonObject obj) throws JsonParseException
	{
		Set<String> propertiesSeen = new HashSet<String>();
		
		JsonArray required = schema.getAsJsonArray(REQUIRED);
		JsonObject properties = schema.getAsJsonObject(PROPERTIES);
		
		if (properties == null)
		{
			return;
		}

		Set<Map.Entry<String, JsonElement>> propertySet = properties.entrySet();
		ArrayList<String> requiredFields = new ArrayList<String>();
		
		for(JsonElement st : required.getAsJsonArray())
		{
			requiredFields.add(st.getAsString());
		}
		
		for (Map.Entry<String, JsonElement> property : propertySet)
		{
			String name = property.getKey();
			String newPath = path + "['" + name + "']";
			JsonElement element = property.getValue();
			propertiesSeen.add(name);

			if (!element.isJsonObject())
			{
				throw new JsonParseException("Bad Schema: property definition not an object at '" + newPath + "'");
			}

			JsonObject definition = element.getAsJsonObject();

			JsonElement newTarget = obj.get(name);

			if (newTarget == null)
			{
				JsonPrimitive optional = definition.getAsJsonPrimitive(OPTIONAL);
				boolean needed = ((optional==null) && requiredFields.contains(name)) || (optional != null && !optional.getAsBoolean()); 
			
				if (needed)
				{
					throw new JsonParseException("Invalid: Required property '" + newPath + "' not found");
				}
			}
			else
			{
				validate(newPath, definition, newTarget);
			}
		}

		JsonElement additionalProperties = schema.get(ADDITIONAL_PROPERTIES);
		JsonObject additionalSchema = null;
		if (additionalProperties == null)
		{
			additionalSchema = new JsonObject();
		}
		else
		{
			if (additionalProperties.isJsonObject())
			{
				additionalSchema = additionalProperties.getAsJsonObject();
			}
		}

		/*
		 * if (additionalSchema == null) {
		 * logger.debug("No additional schema for '"+path+"'"); } else {
		 * logger.debug("Additional schema for '"+path+"': "+
		 * additionalSchema.toString()); }
		 */

		Set<Map.Entry<String, JsonElement>> objectProperties = obj.entrySet();
		for (Map.Entry<String, JsonElement> property : objectProperties)
		{
			String name = property.getKey();
			String newPath = path + "['" + name + "']";
			if (!propertiesSeen.contains(name))
			{
				if (additionalSchema == null)
				{
					throw new JsonParseException("Invalid: Found additional property '" + newPath + "'");
				}
				validate(newPath, additionalSchema, property.getValue());
			}
		}
	}

	static Integer getInt(String path, String attributeName, JsonObject schema) throws JsonParseException
	{
		JsonElement attributeElement = schema.get(attributeName);

		if (attributeElement == null)
		{
			return null;
		}

		if (!attributeElement.isJsonPrimitive())
		{
			throw new JsonParseException("Bad Schema: '" + attributeName + "' attribute is not an integer at '" + path + "'");
		}
		JsonPrimitive attributePrimitive = attributeElement.getAsJsonPrimitive();
		if (!attributePrimitive.isNumber())
		{
			throw new JsonParseException("Bad Schema: '" + attributeName + "' attribute is not an integer at '" + path + "'");
		}

		return attributePrimitive.getAsInt();
	}

	static String getString(String path, String attributeName, JsonObject schema) throws JsonParseException
	{
		JsonElement attributeElement = schema.get(attributeName);

		if (attributeElement == null)
		{
			return null;
		}

		if (!attributeElement.isJsonPrimitive())
		{
			throw new JsonParseException("Bad Schema: '" + attributeName + "' attribute is not a string at '" + path + "'");
		}
		JsonPrimitive attributePrimitive = attributeElement.getAsJsonPrimitive();
		if (!attributePrimitive.isString())
		{
			throw new JsonParseException("Bad Schema: '" + attributeName + "' attribute is not a string at '" + path + "'");
		}

		return attributePrimitive.getAsString();
	}

	static BigDecimal getBigDecimal(String path, String attributeName, JsonObject schema) throws JsonParseException
	{
		JsonElement attributeElement = schema.get(attributeName);

		if (attributeElement == null)
		{
			return null;
		}

		if (!attributeElement.isJsonPrimitive())
		{
			throw new JsonParseException("Bad Schema: '" + attributeName + "' attribute is not a number at '" + path + "'");
		}
		JsonPrimitive attributePrimitive = attributeElement.getAsJsonPrimitive();
		if (!attributePrimitive.isNumber())
		{
			throw new JsonParseException("Bad Schema: '" + attributeName + "' attribute is not a number at '" + path + "'");
		}

		return attributePrimitive.getAsBigDecimal();
	}

	static void validateString(String path, JsonObject schema, String str) throws JsonParseException
	{
		Integer minLength = getInt(path, MIN_LENGTH, schema);
		Integer maxLength = getInt(path, MAX_LENGTH, schema);

		if ((minLength != null) && (str.length() < minLength))
		{
			throw new JsonParseException("Invalid: String '" + path + "' is too short.  The string needs to be more than " + minLength + " characters");
		}

		if ((maxLength != null) && (str.length() > maxLength))
		{
			throw new JsonParseException("Invalid: String '" + path + "' is too long.  The string needs to be less than " + maxLength + " characters");
		}

		String pattern = getString(path, PATTERN, schema);
		if ((pattern != null) && (!str.matches(pattern)))
		{
			throw new JsonParseException("Invalid: String '" + path + "' does not match pattern '" + pattern + "'");
		}
	}

	static void validateTuple(String path, JsonArray tupleSchema, JsonObject additionalSchema, JsonArray array) throws JsonParseException
	{
		return;
	}

	static void validateArray(String path, JsonObject schema, JsonArray array) throws JsonParseException
	{
		JsonElement additionalProperties = schema.get(ADDITIONAL_PROPERTIES);
		JsonObject additionalSchema = null;
		if (additionalProperties == null)
		{
			additionalSchema = new JsonObject();
		}
		else
		{
			if (additionalProperties.isJsonObject())
			{
				additionalSchema = additionalProperties.getAsJsonObject();
			}
		}

		JsonElement itemsElement = schema.get(ITEMS);
		if (itemsElement == null)
		{
			return;
		}

		if (itemsElement.isJsonArray())
		{
			validateTuple(path, itemsElement.getAsJsonArray(), additionalSchema, array);
			return;
		}

		JsonObject itemsSchema = null;
		if (itemsElement.isJsonObject())
		{
			itemsSchema = itemsElement.getAsJsonObject();
		}
		else
		{
			// Bogus items parameter, assume everything is valid.
			itemsSchema = new JsonObject();
		}

		int i = 0;
		for (JsonElement element : array)
		{
			++i;
			String curPath = path + "[" + i + "]";
			validate(curPath, itemsSchema, element);
		}
	}

	static void validateEnum(String path, JsonObject schema, JsonElement element) throws JsonParseException
	{
		JsonElement enumElement = schema.get(ENUM);
		if (enumElement == null)
		{
			return;
		}

		if (!enumElement.isJsonArray())
		{}

		JsonArray enumArray = enumElement.getAsJsonArray();

		for (JsonElement curElement : enumArray)
		{
			if (element.equals(curElement))
			{
				// We found a valid value.
				return;
			}
		}

		throw new JsonParseException("Invalid: Property '" + path + "' is not one of the enum values.");
	}

	static void validateNumber(String path, JsonObject schema, BigDecimal number) throws JsonParseException
	{
		BigDecimal minimum = getBigDecimal(path, MINIMUM, schema);
		if (minimum != null)
		{
			if (number.compareTo(minimum) < 0)
			{
				throw new JsonParseException("Invalid: Property '" + path + "' has a value of '" + number + "' which is less than the minimum of '" + minimum
						+ "'.");
			}
		}

		BigDecimal maximum = getBigDecimal(path, MAXIMUM, schema);
		if (maximum != null)
		{
			if (number.compareTo(maximum) > 0)
			{
				throw new JsonParseException("Invalid: Property '" + path + "' has a value of '" + number + "' which is greater than the maximum of '"
						+ maximum + "'.");
			}
		}
	}

	static void validate(String path, JsonObject schema, JsonElement element) throws JsonParseException
	{
		Set<Type> typeSet = getTypeSet(path, schema);

		Type type = getType(element);
		if (!typeSet.contains(type))
		{
			throw WrongType.generate(path, typeSet, type);
		}

		switch (type)
		{
			case BOOLEAN:
			case NULL:
				break;
			case NUMBER:
			case INTEGER:
				validateNumber(path, schema, element.getAsBigDecimal());
				break;
			case ARRAY:
				validateArray(path, schema, element.getAsJsonArray());
				break;
			case STRING:
				validateString(path, schema, element.getAsString());
				break;
			case OBJECT:
				validateObject(path, schema, element.getAsJsonObject());
				break;
			default:
				// Unknown type
				throw new JsonParseException("Internal Error");
		}

		validateEnum(path, schema, element);
	}

	static public void validate(JsonObject schema, JsonElement element) throws JsonParseException
	{
		validate("$", schema, element);
	}

	public void validate(JsonElement element) throws JsonParseException
	{
		validate(getSchema(), element);
	}
}

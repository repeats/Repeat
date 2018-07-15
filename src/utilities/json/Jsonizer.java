package utilities.json;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonStringNode;
import utilities.StringUtilities;

public class Jsonizer {

	private static final Logger LOGGER = Logger.getLogger(Jsonizer.class.getName());

	private Jsonizer() {}

	public static boolean parse(JsonNode node, Object dest) {
		try {
			return internalParse(node, dest);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException
				| InstantiationException | InvocationTargetException e) {
			LOGGER.log(Level.WARNING, "Unable to parse JSON into object.", e);
			return false;
		}
	}

	@SuppressWarnings("rawtypes")
	private static boolean internalParse(JsonNode node, Object dest) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException {
		Class clazz = dest.getClass();

		for (Entry<JsonStringNode, JsonNode> inner : node.getFields().entrySet()) {
			JsonStringNode nameNode = inner.getKey();
			JsonNode valueNode = inner.getValue();

			String fieldName = StringUtilities.toCamelCase(nameNode.getStringValue());
			Field field = clazz.getDeclaredField(fieldName);

			int modifier = field.getModifiers();
			if (Modifier.isStatic(modifier) || !Modifier.isPrivate(modifier)) {
				LOGGER.warning("Skipping field " + fieldName + " when parsing JSON. Field is either static or non-private.");
        		continue;
        	}

			field.setAccessible(true);
			if (isPrimitiveOrString(field.getType())) {
				toPrimitiveOrString(valueNode, dest, field);
				continue;
			}

			boolean foundConstructor = false;
			for (Constructor constructor : field.getType().getDeclaredConstructors()) {
				if (constructor.getParameterCount() == 0) {
					constructor.setAccessible(true);
					Object o = constructor.newInstance();
					if (!parse(valueNode, o)) {
						return false;
					}
					field.set(dest, o);
					foundConstructor = true;
					break;
				}
			}

			if (!foundConstructor) {
				return false;
			}
		}

		return true;
	}

	public static JsonNode jsonize(Object o) {
		try {
			return internalSsonize(o);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			LOGGER.log(Level.WARNING,"Failed to jsonize object " + o.getClass(), e);
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	private static JsonNode internalSsonize(Object o) throws IllegalArgumentException, IllegalAccessException {
		Map<JsonStringNode, JsonNode> data = new HashMap<>();

		Class clazz = o.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
        	Class fieldClass = field.getType();
        	if (Modifier.isStatic(field.getModifiers())) {
        		continue;
        	}
        	field.setAccessible(true);
        	Object value = field.get(o);

        	String jsonName = StringUtilities.toSnakeCase(field.getName());
        	JsonStringNode nameNode = JsonNodeFactories.string(jsonName);
        	if (isPrimitiveOrString(fieldClass)) {
        		data.put(nameNode, fromPrimitiveOrString(fieldClass, value));
        		continue;
        	}

        	if (value == null) {
        		continue;
        	}
        	JsonNode node = jsonize(value);
        	data.put(nameNode, node);
        }

		return JsonNodeFactories.object(data);
	}

	@SuppressWarnings("rawtypes")
	private static void toPrimitiveOrString(JsonNode node, Object dest, Field field) throws IllegalArgumentException, IllegalAccessException {
		Class clazz = field.getType();

		if (clazz == String.class) {
			field.set(dest, node.getStringValue());
		} else if (clazz == Boolean.TYPE) {
			field.set(dest, node.getBooleanValue());
		} else if (clazz == Byte.TYPE) {
			int value = Integer.parseInt(node.getNumberValue());
			field.set(dest, (byte)value);
		} else if (clazz == Character.TYPE) {
			String value = node.getStringValue();
			field.set(dest, value.charAt(0));
		} else if (clazz == Short.TYPE) {
			int value = Integer.parseInt(node.getNumberValue());
			field.set(dest, (short)value);
		} else if (clazz == Integer.TYPE) {
			int value = Integer.parseInt(node.getNumberValue());
			field.set(dest, value);
		} else if (clazz == Long.TYPE) {
			int value = Integer.parseInt(node.getNumberValue());
			field.set(dest, (long)value);
		} else if (clazz == Float.TYPE) {
			float value = Float.parseFloat(node.getNumberValue());
			field.set(dest, value);
		} else if (clazz == Double.TYPE) {
			double value = Double.parseDouble(node.getNumberValue());
			field.set(dest, value);
		} else {
			throw new IllegalArgumentException("Unknown type " + clazz);
		}
	}

	private static JsonNode fromPrimitiveOrString(Class<?> clazz, Object value) {
		if (clazz == String.class) {
			return JsonNodeFactories.string((String)value);
		} else if (clazz == Boolean.TYPE) {
			return JsonNodeFactories.booleanNode((boolean) value);
		} else if (clazz == Byte.TYPE) {
			return JsonNodeFactories.number((byte)value);
		} else if (clazz == Character.TYPE) {
			return JsonNodeFactories.string(Character.toString((char)value));
		} else if (clazz == Short.TYPE) {
			return JsonNodeFactories.number((short)value);
		} else if (clazz == Integer.TYPE) {
			return JsonNodeFactories.number((int)value);
		} else if (clazz == Long.TYPE) {
			return JsonNodeFactories.number((long)value);
		} else if (clazz == Float.TYPE) {
			return JsonNodeFactories.number("" + (float)value);
		} else if (clazz == Double.TYPE) {
			return JsonNodeFactories.number("" + (double)value);
		}
		throw new IllegalArgumentException("Unknown type " + clazz);
	}

	private static boolean isPrimitiveOrString(Class<?> clazz) {
		return clazz == String.class
				|| clazz == Boolean.TYPE
				|| clazz == Byte.TYPE
				|| clazz == Character.TYPE
				|| clazz == Short.TYPE
				|| clazz == Integer.TYPE
				|| clazz == Long.TYPE
				|| clazz == Float.TYPE
				|| clazz == Double.TYPE;
	}
}

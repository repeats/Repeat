package utilities.json;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonStringNode;
import utilities.StringUtilities;

/**
 * Automatically turns a JSON into a Java object and back using reflection.
 * Only consider private fields. Inherited fields are not considered.
 */
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

	private static boolean internalParse(JsonNode node, Object dest) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException {
		Class<?> clazz = dest.getClass();

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
				field.set(dest, toPrimitiveOrString(valueNode, field.getType()));
				continue;
			}

			if (isIterableType(field.getType())) {
				field.set(dest, parseIterableField(valueNode, field));
				continue;
			}

			Object o = getDefaultConstructor(field.getType()).newInstance();
			if (!internalParse(valueNode, o)) {
				return false;
			}
			field.set(dest, o);
		}

		return true;
	}

	private static List<Object> parseIterableField(JsonNode valueNode, Field field) throws IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchFieldException, SecurityException {
		if (!valueNode.isArrayNode()) {
			throw new IllegalArgumentException("Expecting node to be array but is type " + valueNode.getType() + ". " + JSONUtility.jsonToString(valueNode));
		}

		List<JsonNode> valueNodes = valueNode.getArrayNode();
		ParameterizedType genericType = (ParameterizedType) field.getGenericType();
		Type[] iterableTypes = genericType.getActualTypeArguments();
		if (iterableTypes.length != 1) {
			throw new IllegalArgumentException("Expecting one type arguments for iterable attribute but found " + iterableTypes.length);
		}
		Class<?> clazz = (Class<?>) iterableTypes[0];
		if (isPrimitiveOrString(clazz)) {
			List<Object> output = new ArrayList<>();
			for (JsonNode n : valueNodes) {
				output.add(toPrimitiveOrString(n, clazz));
			}

			return output;
		}

		Constructor<?> constructor = getDefaultConstructor(clazz);
		List<Object> output = new ArrayList<>();
		for (JsonNode n : valueNodes) {
			Object o = constructor.newInstance();
			if (!internalParse(n, o)) {
				throw new IllegalArgumentException("Unable to parse internal node.");
			}
			output.add(o);
		}
		return output;
	}

	/**
	 * Retrieves the constructor with zero parameter and set it to be accessible.
	 */
	private static Constructor<?> getDefaultConstructor(Class<?> clazz) {
		for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
			if (constructor.getParameterCount() != 0) {
				continue;
			}
			constructor.setAccessible(true);
			return constructor;
		}

		throw new IllegalArgumentException("No constructor with zero parameter found for " + clazz.getName());
	}

	public static JsonNode jsonize(Object o) {
		try {
			return internalJsonize(o);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			LOGGER.log(Level.WARNING,"Failed to jsonize object " + o.getClass(), e);
			return null;
		}
	}

	private static JsonNode internalJsonize(Object o) throws IllegalArgumentException, IllegalAccessException {
		Class<?> objectClass = o.getClass();
		if (isPrimitiveOrString(objectClass)) {
			return fromPrimitiveOrString(objectClass, o);
		}

		if (isIterableType(objectClass)) {
    		Iterable<?> it = (Iterable<?>) o;
    		List<JsonNode> nodes = new ArrayList<>();
    		for (Iterator<?> i = it.iterator(); i.hasNext(); ) {
    			Object next = i.next();
    			JsonNode node = internalJsonize(next);
    			nodes.add(node);
    		}
    		return JsonNodeFactories.array(nodes);
    	}

		Map<JsonStringNode, JsonNode> data = new HashMap<>();

		Class<?> clazz = o.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
        	int modifier = field.getModifiers();
        	if (Modifier.isStatic(modifier) || !Modifier.isPrivate(modifier)) {
        		continue;
        	}
        	field.setAccessible(true);
        	Object value = field.get(o);

        	String jsonName = StringUtilities.toSnakeCase(field.getName());
        	JsonStringNode nameNode = JsonNodeFactories.string(jsonName);
        	if (value == null) {
        		continue;
        	}
        	JsonNode node = internalJsonize(value);
        	data.put(nameNode, node);
        }

		return JsonNodeFactories.object(data);
	}

	@SuppressWarnings("rawtypes")
	private static Object toPrimitiveOrString(JsonNode node, Class clazz) throws IllegalArgumentException, IllegalAccessException {
		if (clazz == String.class) {
			return node.getStringValue();
		} else if (clazz == Boolean.TYPE || clazz == Boolean.class) {
			return node.getBooleanValue();
		} else if (clazz == Byte.TYPE || clazz == Byte.class) {
			int value = Integer.parseInt(node.getNumberValue());
			return (byte) value;
		} else if (clazz == Character.TYPE || clazz == Character.class) {
			String value = node.getStringValue();
			return value.charAt(0);
		} else if (clazz == Short.TYPE || clazz == Short.class) {
			int value = Integer.parseInt(node.getNumberValue());
			return (short) value;
		} else if (clazz == Integer.TYPE || clazz == Integer.class) {
			int value = Integer.parseInt(node.getNumberValue());
			return value;
		} else if (clazz == Long.TYPE || clazz == Long.class) {
			int value = Integer.parseInt(node.getNumberValue());
			return (long) value;
		} else if (clazz == Float.TYPE || clazz == Float.class) {
			float value = Float.parseFloat(node.getNumberValue());
			return value;
		} else if (clazz == Double.TYPE || clazz == Double.class) {
			double value = Double.parseDouble(node.getNumberValue());
			return value;
		} else {
			throw new IllegalArgumentException("Unknown type " + clazz);
		}
	}

	private static JsonNode fromPrimitiveOrString(Class<?> clazz, Object value) {
		if (clazz == String.class) {
			return JsonNodeFactories.string((String)value);
		} else if (clazz == Boolean.class) {
			return JsonNodeFactories.booleanNode((boolean) value);
		} else if (clazz == Byte.class) {
			return JsonNodeFactories.number((byte)value);
		} else if (clazz == Character.class) {
			return JsonNodeFactories.string(Character.toString((char)value));
		} else if (clazz == Short.class) {
			return JsonNodeFactories.number((short)value);
		} else if (clazz == Integer.class) {
			return JsonNodeFactories.number((int)value);
		} else if (clazz == Long.class) {
			return JsonNodeFactories.number((long)value);
		} else if (clazz == Float.class) {
			return JsonNodeFactories.number("" + (float)value);
		} else if (clazz == Double.class) {
			return JsonNodeFactories.number("" + (double)value);
		}
		throw new IllegalArgumentException("Unknown type " + clazz);
	}

	private static boolean isIterableType(Class<?> clazz) {
		return Iterable.class.isAssignableFrom(clazz);
	}

	public static boolean isPrimitiveOrString(Class<?> clazz) {
		return isString(clazz)
				|| clazz == Boolean.class || clazz == Boolean.TYPE
				|| clazz == Byte.class || clazz == Byte.TYPE
				|| clazz == Character.class|| clazz == Character.TYPE
				|| clazz == Short.class || clazz == Short.TYPE
				|| clazz == Integer.class || clazz == Integer.TYPE
				|| clazz == Long.class || clazz == Long.TYPE
				|| clazz == Float.class || clazz == Float.TYPE
				|| clazz == Double.class || clazz == Double.TYPE;
	}

	private static boolean isString(Class<?> clazz) {
		return clazz == String.class;
	}
}

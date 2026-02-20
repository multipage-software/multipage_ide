/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import java.awt.Font;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.swing.JOptionPane;

import org.multipage.util.Resources;
import org.multipage.util.Safe;
import org.multipage.util.j;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.security.ArrayTypePermission;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.NullPermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;

/**
 * State serializer for storing and retrieving application settings to and from given file.
 * @author vakol
 */
public class StateSerializer {
	
	/**
	 * Root tag name within the settings file and a record tags name.
	 */
	private static final String SETTINGS_NODE = "settings";
	private static final String RECORD_NODE = "record";
	
	/**
	 * Specify character set for a text file.
	 */
	private static final Charset settingsOutputCharset = StandardCharsets.UTF_8;
	
	/**
	 * Auxiliary table for mapping type names to class names.
	 */
	private static Hashtable<String, String> tableTypeClass = new Hashtable<>();
	
	/**
	 * Initialize the type class table.
	 */
	private static void initTypeClassTable() {
		try {
			
			tableTypeClass.put("String", "java.lang.String");
			tableTypeClass.put("Integer", "java.lang.Integer");
			tableTypeClass.put("Long", "java.lang.Long");
			tableTypeClass.put("Float", "java.lang.Float");
			tableTypeClass.put("Double", "java.lang.Double");
			tableTypeClass.put("Boolean", "java.lang.Boolean");
			tableTypeClass.put("List", "java.util.LinkedList");
			tableTypeClass.put("Rectangle", "java.awt.Rectangle");
			tableTypeClass.put("Point", "java.awt.Point");
			tableTypeClass.put("Font", "java.awt.Font");
			tableTypeClass.put("Color", "java.awt.Color");
		}
		catch (Exception e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Static constructor.
	 */
	static {
		
		initTypeClassTable();
	}
	
	/**
	 * Follows a list of converter classes used by the XStream library.
	 * If some of the objects cannot be serialized with default converters, you
	 * must create a new converter class for that kind of objects. Place the new
	 * converter classes nested in StateSerializer class and register
	 * them inside the registerXStreamConverters(...) method defined below. 
	 */
	
	/**
	 * XStream converter defined for types objects.
	 */
	private static class GenericXStreamConverter<T> implements Converter {
		
		/**
		 * Gets default object of type T.
		 */
		private Supplier<T> defaultObjectLambda = null;
		
		/**
		 * Auxiliary field map for the reader.
		 */
		private HashMap<String, String> auxiliaryFieldMap = null;
		
		/**
		 * Writer lambda function.
		 */
		private BiFunction<HierarchicalStreamWriter, MarshallingContext, Consumer<T>> writeObjectLambda = null;
		
		/**
		 * Reader lambda function.
		 */
		private Function<HierarchicalStreamReader, Function<String, Function<String, Function<HashMap<String, String>, Consumer<T>>>>> readerObjectLambda = null;
		
		/**
		 * Lambda function which finalizes the object.
		 */
		private Function<HashMap<String, String>, T> finalizeObjectLambda = null;
		
		/**
		 * Reference to the XStream object which uses this converter.
		 */
		private XStream xStream = null;
		
		/**
		 * Register a new converter.
		 * @param <T> - type of object to register
		 * @param xStream
		 * @param alias
		 * @param defaultObjectLambda
		 * @param writeObjectLambda
		 * @param readerObjectLambda
		 * @param finalizeObjectLambda
		 */
		public static <T> void register(XStream xStream,
				String alias,
				Supplier<T> defaultObjectLambda,
				BiFunction<HierarchicalStreamWriter, MarshallingContext, Consumer<T>> writeObjectLambda,
				Function<HierarchicalStreamReader, Function<String, Function<String, Function<HashMap<String, String>, Consumer<T>>>>> readerObjectLambda,
				Function<HashMap<String, String>, T> finalizeObjectLambda
				) 
						throws Exception {
			
			// Check input.
			if (xStream == null || defaultObjectLambda == null || writeObjectLambda == null 
					|| (readerObjectLambda == null && !RECORD_NODE.equals(alias))) {
				throw new NullPointerException();
			}
			
			try {
				// Create, setup and register a new converter based on the default object type.
				GenericXStreamConverter<T> converter = new GenericXStreamConverter<T>();
				
				// Assign lambda functions.
				converter.defaultObjectLambda = defaultObjectLambda;
				converter.writeObjectLambda = writeObjectLambda;
				converter.readerObjectLambda = readerObjectLambda;
				converter.finalizeObjectLambda = finalizeObjectLambda;
				
				// Register above converter (remember reference to the XStream object).
				converter.xStream = xStream;
				xStream.registerConverter(converter);
				
				// Possibly set alias for the converter.
				if (alias != null && !alias.isEmpty()) {
					T classObject = defaultObjectLambda.get();
					Class<?> theClass = classObject.getClass();
					xStream.alias(alias, theClass);
				}
			}
			catch (Exception e) {
				Safe.exception(e);
			}
		}
		
		/**
		 * Register a new converter.
		 * @param <T> - type of object to register
		 * @param xStream
		 * @param defaultObjectLambda
		 * @param writeObjectLambda
		 * @param readerObjectLambda
		 * @param finalizeObjectLambda
		 */
		public static <T> void register(XStream xStream,
				Supplier<T> defaultObjectLambda,
				BiFunction<HierarchicalStreamWriter, MarshallingContext, Consumer<T>> writeObjectLambda,
				Function<HierarchicalStreamReader, Function<String, Function<String, Function<HashMap<String, String>, Consumer<T>>>>> readerObjectLambda,
				Function<HashMap<String, String>, T> finalizeObjectLambda) 
						throws Exception {
			
			// Delegate the call to the main register method with empty alias.
			register(xStream, null, defaultObjectLambda, writeObjectLambda, readerObjectLambda, finalizeObjectLambda);
		}

		/**
		 * Check the object class if it can be converted.
		 */
		@SuppressWarnings("rawtypes")
		public boolean canConvert(Class theClassToConvert) {
			
			if (defaultObjectLambda == null) {
				return true;
			}
			
			Class<?> theClassObject = defaultObjectLambda.get().getClass();
			boolean canConvert = theClassToConvert.equals(theClassObject);
			return canConvert;
		}
	
		/**
		 * Do marshaling of the typed object.
		 */
		@SuppressWarnings("unchecked")
		public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {

			// Get class of the value object.
			Class<? extends Object> valueClass = value.getClass();
			if (canConvert(valueClass)) {
				writeObjectLambda.apply(writer, context).accept((T) value);
			}
		}
		
		/**
		 * Do unmarshaling of the typed object.
		 */
		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			
			// Get default object.
			T defaultObject = defaultObjectLambda.get();
			
			try {
				Class<? extends Object> defaultObjectClass = defaultObject.getClass();
				
				// On new record.
				String nodeName = reader.getNodeName();
				if (StateStreamRecordInfo.class.equals(defaultObjectClass) && RECORD_NODE.equals(nodeName)) {
					
					StateStreamRecordInfo record = (StateStreamRecordInfo) defaultObject;
					
					// Get type attribute and check it with object type name.
					String typeAttribute = reader.getAttribute("type");
					if (typeAttribute == null || typeAttribute.isEmpty() || typeAttribute.equals("null")) {
						return record.value;
					}
					
					// Read value of the record recursively for multidimensional arrays, sets and maps.
					Object returnedObject = readValueRecursive(xStream, reader, context, typeAttribute);
					if (returnedObject == null) {
						returnedObject = record.value;
					}
					return returnedObject;
				}
				else {
					// On other objects that are not records.
					
					// Make initialization.
					auxiliaryFieldMap = new HashMap<String, String>();
					String value = null;
					
					// Read all fields contained in current object.
					while (reader.hasMoreChildren()) {
						
						reader.moveDown();
		
						// Get field name and value.
						nodeName = reader.getNodeName();
						value = reader.getValue();
						
						// Apply lambda function on them. Also enable to use an auxiliary field map.
						if (readerObjectLambda != null) {
							readerObjectLambda.apply(reader).apply(nodeName).apply(value).apply(auxiliaryFieldMap).accept(defaultObject);
						}
						
						reader.moveUp();
					}
					
					// Possibly finalize object using the field map.
					T finalObject = null;
					
					if (finalizeObjectLambda != null) {
						finalObject = finalizeObjectLambda.apply(auxiliaryFieldMap);
					}
							
					if (finalObject == null) {
						finalObject = defaultObject;
					}
					
					// Release field map.
					auxiliaryFieldMap = null;
					
					return finalObject;
				}
			}
			catch (Exception e) {
				Safe.exception(e);
				return defaultObject;
			}
		}
	}
	
	/**
	 * Write value of the record. If the value is an array, write each element of the array as a separate node.
	 * This function is recursive for multidimensional arrays.
	 * @param xStream
	 * @param writer
	 * @param context
	 * @param value
	 */
	private void writeValueRecursive(XStream xStream, HierarchicalStreamWriter writer, MarshallingContext context,
			Object value) {
		try {
			
			// Check if the value is null. If it is null, do not write anything.
			if (value == null) {
				return;
			}
			
			// If the value is a list or a set, convert it to an array.
			if (value instanceof List) {
				List<?> list = (List<?>) value;
				value = list.toArray();
			}
			else if (value instanceof Set) {
				java.util.Set<?> set = (Set<?>) value;
				value = set.toArray();
			}
			
			// Set class attribute for the record.
			Class<? extends Object> valueClass = value.getClass();
			
			// If the value is a map, convert it to an array of map entries.
			if (value instanceof Map) {
				Map<?, ?> map = (Map<?, ?>) value;
				
				// Write map type attribute for the record.
				writer.addAttribute("type", "map");
				
				// For each entry of the map write a separate item with key and value nodes.
				for (Entry<?, ?> entry : map.entrySet()) {
					
					// Get key and value of the map entry.
					Object key = entry.getKey();
					Object entryValue = entry.getValue();
					
					// Write map item recursively with current method.
					writer.startNode("item");
					
					writer.startNode("key");
					writeValueRecursive(xStream, writer, context, key);
					writer.endNode();
					
					writer.startNode("value");
					writeValueRecursive(xStream, writer, context, entryValue);
					writer.endNode();
					
					writer.endNode();
				}
			}
			// If the value is an array, write each element of the array as a separate node.
			else if (valueClass.isArray()) {
				
				// Write array type attribute for the record.
				writer.addAttribute("type", "array");
				
				// For each element of the array write a separate item with type attribute.
				int length = java.lang.reflect.Array.getLength(value);
				for (int i = 0; i < length; i++) {
					
					// Get array element.
					Object itemValue = java.lang.reflect.Array.get(value, i);
					
					// Write array item recursively with current method.
					writer.startNode("item");
					writeValueRecursive(xStream, writer, context, itemValue);
					writer.endNode();
				}
			}
			else {
				// Write value of the record using aproppriate converter.
				writer.addAttribute("type", getTypeFromClass(valueClass.getName()));
				Converter valueConverter = xStream.getConverterLookup().lookupConverterForType(valueClass);						
				context.convertAnother(value, valueConverter);
			}
		}
		catch (Exception e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Read value of the element.
	 * @param xStream
	 * @param reader
	 * @param context
	 * @param typeAttribute
	 * @return
	 */
	public static Object readValueRecursive(XStream xStream, HierarchicalStreamReader reader, UnmarshallingContext context,
			String typeAttribute) {
		
		// If the type attribute is "array" or "map", use converters for array items.
		if ("array".equals(typeAttribute)) {
			
			// Read all items contained in current array.
			LinkedList<Object> array = new LinkedList<>();
			while (reader.hasMoreChildren()) {
				
				reader.moveDown();
				
				// Get field name and value.
				String itemName = reader.getNodeName();
				if ("item".equals(itemName)) {
					
					typeAttribute = reader.getAttribute("type");

					// Read array item recursively.
					Object item = readValueRecursive(xStream, reader, context, typeAttribute);
					array.add(item);
				}
				
				reader.moveUp();
			}
			return array;
		}
		else if ("map".equals(typeAttribute)) {
			
			// Read all items contained in current array.
			LinkedHashMap<Object, Object> map = new LinkedHashMap<>();
			while (reader.hasMoreChildren()) {
				
				reader.moveDown();
				
				// Get field name and value.
				String itemName = reader.getNodeName();
				if ("item".equals(itemName)) {

					// Read map key recursively.
					reader.moveDown();
					itemName = reader.getNodeName();
					if (!"key".equals(itemName)) {
						reader.moveUp();
						reader.moveUp();
						continue;
					}
					typeAttribute = reader.getAttribute("type");
					Object key = readValueRecursive(xStream, reader, context, typeAttribute);
					reader.moveUp();
					
					// Read map value recursively.
					reader.moveDown();
					itemName = reader.getNodeName();
					Object value = null;
					if ("value".equals(itemName)) {
						typeAttribute = reader.getAttribute("type");
						value = readValueRecursive(xStream, reader, context, typeAttribute);
					}
					
					reader.moveUp();
					
					// Put key and value entry into the map.
					map.put(key, value);
				}
				
				reader.moveUp();
			}
			return map;
		}
		else {
			// Try to get class and object by class name.
			Class<?> valueClass = null;
			String valueClassName = getClassFromType(typeAttribute);
			try {
				valueClass = Class.forName(valueClassName);
	        }
	        catch (Exception e) {
	            Safe.exception(e);
	            return null;
	        }

	        // Get converter for the value class and check if it can convert the value class.
	        Converter valueConverter = xStream.getConverterLookup().lookupConverterForType(valueClass);
	        if (valueConverter instanceof GenericXStreamConverter) {
				
				// Delegate the call to foound type converter.
				GenericXStreamConverter<?> typeConverter = (GenericXStreamConverter<?>) valueConverter;
				return typeConverter.unmarshal(reader, context);
			}
			
			// Convert value object using simple converter.
			Object returnedObject = context.convertAnother(null, valueClass, valueConverter);
			return returnedObject;
		}
	}

	/**
	 * Register converters for XStream library.
	 * @param xStream
	 * @throws Exception 
	 */
	public void registerAllXStreamConverters(XStream xStream)
			throws Exception {
		
		// Register the "StateStreamRecordInfo" converter.
		GenericXStreamConverter.register(xStream,
				// Alias for the converter.
				RECORD_NODE,
				// Default value for the converter.
				() -> new StateStreamRecordInfo(Object.class),
				// A writer defined for the StateStreamRecordInfo object.
				(writer, context) -> recordInfo -> {
					
					// Set source attribute for the record.
					writer.addAttribute("source", recordInfo.source);
					Object value = recordInfo.value;

					// Write value of the record recursively for multidimensional arrays, sets and maps.
					writeValueRecursive(xStream, writer, context, value);
				},
				// No reader for the StateStreamRecordInfo object.
				// Look at unmarshal method of the GenericXStreamConverter class for details.
				null,
				null
			);
		
		// Register the "array" converter.
		/*GenericXStreamConverter.register(xStream,
				null,
				,
				
			);*/
		
		// Register the "java.awt.Rectangle" converter.
		GenericXStreamConverter.register(xStream,
				// Default value for the converter.
				() -> new java.awt.Rectangle(),
				// A writer defined for the rectangle object.
				(writer, context) -> rectangle -> {
					
					{
						writer.startNode("x");
						writer.setValue(String.valueOf(rectangle.x));
						writer.endNode();
					}
					{
						writer.startNode("y");
						writer.setValue(String.valueOf(rectangle.y));
						writer.endNode();
					}
					{
						writer.startNode("width");
						writer.setValue(String.valueOf(rectangle.width));
						writer.endNode();
					}
					{
						writer.startNode("height");
						writer.setValue(String.valueOf(rectangle.height));
						writer.endNode();
					}
				},
				// A reader for the rectangle object.
				reader -> nodeName -> textValue -> auxiliaryFieldMap -> rectangle -> {
					
					if ("x".equals(nodeName)) {
						rectangle.x = Integer.parseInt(textValue);
					}
					else if ("y".equals(nodeName)) {
						rectangle.y = "y".equals(nodeName) ? Integer.parseInt(textValue) : 0;
					}
					else if ("width".equals(nodeName)) {
						rectangle.width = "width".equals(nodeName) ? Integer.parseInt(textValue) : 0;
					}
					else if ("height".equals(nodeName)) {
						rectangle.height = "height".equals(nodeName) ? Integer.parseInt(textValue) : 0;
					}
				},
				null
			);
		
		// Register the "java.awt.Point" converter.
		GenericXStreamConverter.register(xStream,
				// Default value for the converter.
				() -> new java.awt.Point(),
				// Define writer for the point object.
				(writer, context) -> point -> {
					{
						writer.startNode("x");
						writer.setValue(String.valueOf(point.x));
						writer.endNode();
					}
					{
						writer.startNode("y");
						writer.setValue(String.valueOf(point.y));
						writer.endNode();
					}
				},
				// A reader for the point object.
				reader -> nodeName -> textValue -> auxiliaryFieldMap -> point -> {
					
					if ("x".equals(nodeName)) {
						point.x = Integer.parseInt(textValue);
					}
					else if ("y".equals(nodeName)) {
						point.y = "y".equals(nodeName) ? Integer.parseInt(textValue) : 0;
					}
				},
				null
			);
		
		// Register the "java.awt.Fon"t converter.
		GenericXStreamConverter.register(xStream,
				// Default value.
				() -> new java.awt.Font("Arial", java.awt.Font.PLAIN, 12),
				// Writer for the font.
				(writer, context) -> font -> {
					{
						writer.startNode("name");
						writer.setValue(String.valueOf(font.getFontName()));
						writer.endNode();
					}
					{
						writer.startNode("size");
						writer.setValue(String.valueOf(font.getSize()));
						writer.endNode();
					}
					{
						writer.startNode("style");
						writer.setValue(String.valueOf(font.getStyle()));
						writer.endNode();
					}
				},
				// Reader for the font object.
				reader -> nodeName -> textValue -> auxiliaryFieldMap -> font -> {
					
					if (reader.hasMoreChildren()) {
						
						reader.moveDown();
						auxiliaryFieldMap.put(nodeName, textValue);
						reader.moveUp();
					}
					else {
						
						String readNodeName = reader.getNodeName();
						String readTextValue = reader.getValue();
						auxiliaryFieldMap.put(readNodeName, readTextValue);
					}
				},
				// Finalize the font object.
				auxiliaryFieldMap -> {
					
					int size = Integer.parseInt(auxiliaryFieldMap.get("size"));
					int style = Integer.parseInt(auxiliaryFieldMap.get("style"));
					
					Font font =  new java.awt.Font(
						auxiliaryFieldMap.get("name"),
						style,
						size);
					
					return font;
				}
			);
		
		// Set permissions for all the converters above.
		xStream.addPermission(NoTypePermission.NONE);
		xStream.addPermission(NullPermission.NULL);
		xStream.addPermission(PrimitiveTypePermission.PRIMITIVES);
		xStream.addPermission(ArrayTypePermission.ARRAYS);
		
		xStream.allowTypesByWildcard(new String[] {
			    "java.lang.**",
			    "java.util.**",
				"java.awt.**",
				"org.maclan.**",
				"org.multipage.**"
			});
	}

	/**
	 * Get type name from class name.
	 * @param className
	 * @return
	 */
	private static String getTypeFromClass(String className) {
		try {
			
			// Find type name in the table by class name.
			for (Entry<String, String> entry : tableTypeClass.entrySet()) {
				String typeName = entry.getKey();
				String typeClassName = entry.getValue();
				
				// On match return type name.
				if (typeClassName.equals(className)) {
					return typeName;
				}
			}
		}
		catch (Exception e) {
			Safe.exception(e);
		}
		// If there is no match, return class name as type name.
		return className;
	}
	
	/**
	 * Get class name from type name.
	 * @param typeName
	 * @return
	 */
	private static String getClassFromType(String typeName) {
		try {
			
			// Find class name in the table by type name.
			for (Entry<String, String> entry : tableTypeClass.entrySet()) {
				String classTypeName = entry.getKey();
				String className = entry.getValue();
				
				// On match return class name.
				if (classTypeName.equals(typeName)) {
					return className;
				}
			}
		}
		catch (Exception e) {
			Safe.exception(e);
		}
		// If there is no match, return type name as class name.
		return typeName;
	}

	/**
	 * Name of a file with stored application settings.
	 */
	public String settingsFileName = null;
	
	/**
	 * An input stream which is used when loading application states.
	 */
	public StateInputStreamImpl stateInputStream = null;
	
	/**
	 * An output stream which is used when saving application states.
	 */
	public StateOutputStreamImpl stateOutputStream = null;

	/**
	 * Additionally allowed types. Wild cards are used in each specification of group of types (for example ["javax.swing.**", ...]).
	 */
	private String[] additionalAllowedTypes = null;
	
	/**
	 * List of connected listeners. 
	 */
	private static LinkedList<SerializeStateAdapter> serializeStateListenersRef =
		new LinkedList<SerializeStateAdapter>();

	/**
	 * Constructor.
	 * @param settingsFileName
	 */
	public StateSerializer(String settingsFileName) {

		this.settingsFileName = settingsFileName;
		j.log("StateSerializer: settings file name is \"%s\"", settingsFileName);
	}
	
	/**
	 * Constructor.
	 * @param settingsFilePath
	 */
	public StateSerializer(Path settingsFilePath) {
		
		this.settingsFileName = settingsFilePath.toString();
	}

	/**
	 * Constructor.
	 */
	public StateSerializer() {
		this("");
	}

	/**
	 * Add new listener.
	 * @param listener
	 */
	public void add(SerializeStateAdapter listener) {

		serializeStateListenersRef.add(listener);
	}
	
	/**
	 * Set additional types. You must use wild cards to specify group of types.
	 * @param additionalAllowedTypes
	 */
	public void setAllowedTypesByWildcard(String ... additionalAllowedTypes) {	
		
		this.additionalAllowedTypes = additionalAllowedTypes;
	}
	
	/**
	 * Open input stream.
	 * @param settingsFileName 
	 */
	public StateInputStreamImpl openStateInputStream(String settingsFileName) {
		
		try {
			
			// Open settings file and create input stream.
			StateInputStreamImpl stateInputStream = StateInputStreamImpl.newXStreamInstance(settingsFileName);
			XStream xStream = stateInputStream.getXStream();
			
			// Register all available converters from and to XML with the XStream library.
			registerAllXStreamConverters(xStream);
			if (additionalAllowedTypes != null && additionalAllowedTypes.length > 0) {
				xStream.allowTypesByWildcard(additionalAllowedTypes);
			}
			
			return stateInputStream;
		}
		catch (Exception e) {
			
			// Inform user about raised exception and return a null value.
			JOptionPane.showMessageDialog(null, Resources.getString("org.multipage.gui.errorCannotFindApplicationStateUsingDef"));
			return null;
		}
	}
	
	/**
	 * Close the input stream.
	 */
	public boolean closeStateInputStream() {
		
		try {
			// Try to close the stream.
			stateInputStream.close();
			return true;
		}
		catch (Exception e) {
			
			// Inform user about the error.
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		return false;
	}

	/**
	 * Opens output stream which can save application settings.
	 * @param settingsFileName 
	 * @return - true if the file can be found, otherwise returns a false value
	 */
	public StateOutputStreamImpl openStateOutputStream(String settingsFileName) {
		
		try {
			
			// Opens the settings file and creates output stream of objects.
			StateOutputStreamImpl stateOutputStream = StateOutputStreamImpl.newXStreamInstance(settingsFileName, SETTINGS_NODE);
			XStream xStream = stateOutputStream.getXStream();
			
			// Registers all known XML converters.
			registerAllXStreamConverters(xStream);
			if (additionalAllowedTypes != null && additionalAllowedTypes.length > 0) {
				xStream.allowTypesByWildcard(additionalAllowedTypes);
			}
			
			return stateOutputStream;
		}
		catch (Exception e) {
			
			// Inform user about an error and return false value.
			String exceptionText = e.getLocalizedMessage();
			String errorMessage = String.format(Resources.getString("org.multipage.gui.errorCannotSaveApplicationState"), exceptionText);
			JOptionPane.showMessageDialog(null, errorMessage);
			return null;
		}
	}
	
	/**
	 * Closes current output stream.
	 * @return - return true if the stream is closed, otherwise it returns false.
	 */
	public boolean closeStateOutputStream() {
		
		try {
			// Try to close the stream.
			stateOutputStream.close();
			return true;
		}
		catch (Exception e) {
			
			// Inform user about an error.
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		return false;
	}
	
	/**
	 * Load default settings.
	 */
	public void loadDefaultStates() {

		// Do loop for all known listeners. Invoke related event on each of them.
		for (SerializeStateAdapter listener : serializeStateListenersRef) {
			// Invoke the event.
			listener.onSetDefaultState();
		}

	}
	
	/**
	 * Starts to loading application settings.
	 */
	public void startLoadingSerializedStates() {
		
		// Open input stream.
		stateInputStream = openStateInputStream(settingsFileName);
		if (stateInputStream != null) {
			
			j.log("Loading application settings from \"%s\"", settingsFileName);
			try {
				// Do loop for all known listeners. Invoke events that can read the settings.
				for (SerializeStateAdapter listener : serializeStateListenersRef) {
					
					// Invoke lister.
					listener.onReadState(stateInputStream);
				}
			}
			catch (Exception e) {
				
				// Inform user about an exception.
				JOptionPane.showMessageDialog(null, Resources.getString("org.multipage.gui.errorCannotFindApplicationStateUsingDef"));
				
				// Load default settings.
				loadDefaultStates();
			}
			
			// Close the stream.
			closeStateInputStream();
		}
		else {
			// Load default settings.
			loadDefaultStates();
		}
	}

	/**
	 * Start to save application settings.
	 */
	public void startSavingSerializedStates() {
		
		// Open new output stream.
		stateOutputStream = openStateOutputStream(settingsFileName);
		if (stateOutputStream != null) {
			
			j.log("Saving application settings to \"%s\"", settingsFileName);
			try {
				// Get direct output stream and write a BOM in the beginning.
				OutputStream rawOutputStream = stateOutputStream.getRawOutputStream();
				stateOutputStream.writeBom();
				
				// Write XML header into the output stream.
				Utility.writeXmlHeader(rawOutputStream, settingsOutputCharset, true);
				
				for (SerializeStateAdapter listener : serializeStateListenersRef) {
					
					// Invoke listers which can write application objects to the output stream.
					listener.onWriteState(stateOutputStream);
				}
			}
			catch (Exception e) {
				
				// Inform user.
				String exceptionText = e.getLocalizedMessage();
				String errorMessage = String.format(Resources.getString("org.multipage.gui.errorCannotSaveApplicationState"), exceptionText);
				JOptionPane.showMessageDialog(null, errorMessage);
			}
			
			// Close the stream.
			closeStateOutputStream();
		}
	}
}
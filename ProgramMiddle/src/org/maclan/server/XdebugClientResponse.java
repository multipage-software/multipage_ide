/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2023-05-09
 *
 */
package org.maclan.server;

import java.io.StringReader;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.text.StringEscapeUtils;
import org.maclan.Area;
import org.maclan.Language;
import org.maclan.StartResource;
import org.maclan.VersionObj;
import org.multipage.util.Obj;
import org.multipage.util.Resources;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;

/**
 * Xdebug client response.
 * @author vakol
 *
 */
public class XdebugClientResponse {
	
	/**
	 * Xdebug packet constants.
	 */
	public static final String MULTIPAGE_IDE_KEY = "MULTIPAGE_IDE";
	public static final String APPLICATION_ID = "AREA_SERVER";
	public static final String LANGUAGE_NAME = "Maclan";
	public static final String PROTOCOL_VERSION = "1.0";
	
	/**
     * Xdebug NULL symbol.
     */
	public static final byte [] NULL_SYMBOL = new byte [] { 0 };
	public static final int NULL_SIZE = XdebugClientResponse.NULL_SYMBOL.length;
	
	/**
	 * Compiled XPATH expresions
	 */
	private static XPathExpression xpathRootNodeName = null;
	private static XPathExpression xpathInitIdeKeyName = null;
	private static XPathExpression xpathInitAppIdName = null;
	private static XPathExpression xpathLanguageName = null;
	private static XPathExpression xpathProtocolVersion = null;
	private static XPathExpression xpathDebuggedUri = null;
	private static XPathExpression xpathResponseTransactionId = null;
	private static XPathExpression xpathResponseCommandName = null;
	private static XPathExpression xpathResponseFeatureName = null;
	private static XPathExpression xpathResponseFeatureSupported = null;
	private static XPathExpression xpathResponseFeatureValue = null;
	private static XPathExpression xpathSuccessResponse = null;
	private static XPathExpression xpathSourceResponse = null;
	private static XPathExpression xpathEvalResponse = null;
	private static XPathExpression xpathErrorCode = null;
	private static XPathExpression xpathErrorMessage = null;
	private static XPathExpression xpathContextsResponse = null;
	private static XPathExpression xpathStackProcessId = null;
	private static XPathExpression xpathStackProcessName = null;
	private static XPathExpression xpathStackThreadId = null;
	private static XPathExpression xpathStackThreadName = null;
	private static XPathExpression xpathStackRootNodes = null;
	private static XPathExpression xpathRelativeStackLevel = null;
	private static XPathExpression xpathRelativeStackType = null;
	private static XPathExpression xpathRelativeStackStateHash = null;
	private static XPathExpression xpathRelativeStackCmdBegin = null;
	private static XPathExpression xpathRelativeStackCmdEnd = null;
	private static XPathExpression xpathRelativeSourceCode = null;
	private static XPathExpression xpathPropertyName = null;
	private static XPathExpression xpathPropertyFullName = null;
	private static XPathExpression xpathPropertyValue = null;
	private static XPathExpression xpathPropertyValueType = null;
	private static XPathExpression xpathResponseProperties = null;
	private static XPathExpression xpathResponsePropertyName = null;
	private static XPathExpression xpathResponsePropertyFullName = null;
	private static XPathExpression xpathResponsePropertyTypeName = null;
	private static XPathExpression xpathResponseNullContext = null;
	private static XPathExpression xpathNotificationName = null;
	private static XPathExpression xpathResponseSuccess = null;
	private static XPathExpression xpathFinalSourceResponse = null;
	private static XPathExpression xpathSourceInfoResourceId = null;
	private static XPathExpression xpathSourceInfoResourceName = null;
	private static XPathExpression xpathSourceInfoSlotId = null;
	private static XPathExpression xpathSourceInfoSlotName = null;
	private static XPathExpression xpathSourceInfoAreaId = null;
	private static XPathExpression xpathSourceInfoAreaName = null;
	
	/**
	 * Regular expression.
	 */
	private static Pattern regexUriParser = null;
	
	/**
	 * XML serializer.
	 */
    private static LSSerializer lsSerializer = null;
    
	/**
	 * XML Document representing the packet.
	 */
	private Document xml = null;
	
    /**
     * Static constructor.
     */
    static {
		try {
			// XML DOM document serializer. Converts DOM document to text representation of the XML.
			DOMImplementationLS domImplementationLS = (DOMImplementationLS) DOMImplementationRegistry.newInstance().getDOMImplementation("LS");
			lsSerializer = domImplementationLS.createLSSerializer();
			
			// Prerequisites needed for XPath selector.
			XPathFactory xpathFactory = XPathFactory.newInstance();
			XPath xpath = xpathFactory.newXPath();
			
			// Compile XPATH expressions used in this packet class.
			xpathRootNodeName = xpath.compile("name(/*)");
			xpathInitIdeKeyName = xpath.compile("/init/@idekey");
			xpathInitAppIdName = xpath.compile("/init/@appid");
			xpathLanguageName = xpath.compile("/init/@language");
			xpathProtocolVersion = xpath.compile("/init/@protocol_version");
			xpathDebuggedUri = xpath.compile("/init/@fileuri");
			xpathResponseTransactionId = xpath.compile("/response/@transaction_id");
			xpathResponseCommandName = xpath.compile("/response/@command");
			xpathResponseFeatureName = xpath.compile("/response/@feature_name");
			xpathResponseFeatureSupported = xpath.compile("/response/@supported");
			xpathResponseFeatureValue = xpath.compile("/response/text()");
			xpathSuccessResponse = xpath.compile("/response/@success");
			xpathSourceResponse = xpath.compile("/response/text()");
			xpathEvalResponse = xpath.compile("/response/property/text()");
			xpathErrorCode = xpath.compile("/response/error/@code");
			xpathErrorMessage = xpath.compile("/response/error/message/text()");
			xpathContextsResponse = xpath.compile("/response/*");
			xpathStackProcessId = xpath.compile("/response/@process_id");
			xpathStackProcessName = xpath.compile("/response/@process_name");
			xpathStackThreadId = xpath.compile("/response/@thread_id");
			xpathStackThreadName = xpath.compile("/response/@thread_name");
			xpathStackRootNodes = xpath.compile("/response/*");
			xpathRelativeStackLevel = xpath.compile("@level");
			xpathRelativeStackType = xpath.compile("@type");
			xpathRelativeStackStateHash = xpath.compile("@statehash");
			xpathRelativeStackCmdBegin = xpath.compile("@cmdbegin");
			xpathRelativeStackCmdEnd = xpath.compile("@cmdend");
			xpathRelativeSourceCode = xpath.compile("input/text()");
			xpathPropertyName = xpath.compile("/response/property/name/text()");
			xpathPropertyFullName = xpath.compile("/response/property/fullname/text()");
			xpathPropertyValue = xpath.compile("/response/property/value/text()");
			xpathPropertyValueType = xpath.compile("/response/property/@type");
			xpathResponseProperties = xpath.compile("/response/*");
			xpathResponsePropertyName = xpath.compile("@name");
			xpathResponsePropertyFullName = xpath.compile("@fullname");
			xpathResponsePropertyTypeName = xpath.compile("@type");
			xpathResponseNullContext = xpath.compile("/response/null_context");
			xpathNotificationName = xpath.compile("/notify/@name");
			xpathResponseSuccess = xpath.compile("/response/@success");
			xpathFinalSourceResponse = xpath.compile("/notify/text()");
			xpathSourceInfoResourceId = xpath.compile("/response/property/value/resource/@id");
			xpathSourceInfoResourceName = xpath.compile("/response/property/value/resource/@name");
			xpathSourceInfoSlotId = xpath.compile("/response/property/value/slot/@id");
			xpathSourceInfoSlotName = xpath.compile("/response/property/value/slot/@name");
			xpathSourceInfoAreaId = xpath.compile("/response/property/value/area/@id");
			xpathSourceInfoAreaName = xpath.compile("/response/property/value/area/@name");
			
			// Create regex patterns.
			regexUriParser = Pattern.compile("debug:\\/\\/(?<computer>[^\\/]*)\\/\\?pid=(?<pid>\\d*)&tid=(?<tid>\\d*)&aid=(?<aid>\\d*)&statehash=(?<statehash>\\d*)", Pattern.CASE_INSENSITIVE);
		}
		catch (Exception e) {
			onException(e);
		}
    }
    
	/**
	 * Constructor.
	 * @param xml
	 */
	public XdebugClientResponse(Document xml) {
		
		this.xml = xml;
	}

	/**
	 * Create INIT packet.
	 * @return
	 * @throws Exception 
	 */
	public static XdebugClientResponse createInitPacket(String areaServerStateLocator)
			throws Exception {
		
		// Set packet content.
		Document xml = newXmlDocument();
		Element rootElement = xml.createElement("init");
		rootElement.setAttribute("appid", APPLICATION_ID);
		rootElement.setAttribute("idekey", MULTIPAGE_IDE_KEY);
		rootElement.setAttribute("session", "");
		rootElement.setAttribute("thread", "");
		rootElement.setAttribute("parent", "");
		rootElement.setAttribute("language", LANGUAGE_NAME);
		rootElement.setAttribute("protocol_version", PROTOCOL_VERSION);
		rootElement.setAttribute("fileuri", areaServerStateLocator);
		xml.appendChild(rootElement);
        
		// Create new packet.
		XdebugClientResponse initPacket = new XdebugClientResponse(xml);
		return initPacket;
	}
	
	/**
	 * Create error packet.
	 * @param command
	 * @param xdebugError
	 * @param exception 
	 * @return
	 * @throws Exception 
	 */
	public static XdebugClientResponse createErrorPacket(XdebugCommand command, XdebugError xdebugError, Exception exception)
			throws Exception {
		
		// Get Xdebug command name.
		String commandName = command.getName();
		
		// Get transaction ID from the input command.
		int transactionId = command.getTransactionId();
        if (transactionId < 1) {
        	onThrownException("org.maclan.server.messageXdebugBadTransactionId", transactionId);
        }
        
        // Get error code and error message.
        int errorCode = xdebugError.getErrorCode();
        String errorCodeText = String.valueOf(errorCode);
        
        // Create new XML DOM document object.
        Document xml = newXmlDocument();
        Element rootElement = xml.createElement("response");
        rootElement.setAttribute("command", commandName);
        rootElement.setAttribute("transaction_id", String.valueOf(transactionId));
        xml.appendChild(rootElement);
        Element errorElement = xml.createElement("error");
        errorElement.setAttribute("code", errorCodeText);
        rootElement.appendChild(errorElement);
        
        if (exception != null) {
        	Element messageElement = xml.createElement("message");
        	String errorText = exception.getLocalizedMessage();
        	messageElement.setTextContent(errorText);
        	errorElement.appendChild(messageElement);
        }

        // Create new packet.
		XdebugClientResponse errorPacket = new XdebugClientResponse(xml);
        return errorPacket;
	}

	/**
     * Create feature packet.
     * @param command
     * @param featureValue
     * @return
	 * @throws Exception 
     */
	public static XdebugClientResponse createGetFeatureResult(XdebugCommand command, Object featureValue)
			throws Exception {
		
		// Get the feature name from the input command.
		String featureName = command.getArgument("-n");
		
		// Check if the feature name is supported.
		boolean supported = !(featureValue instanceof Exception);
		
		// Get transaction ID from the input command.
		int transactionId = command.getTransactionId();
        if (transactionId < 1) {
        	onThrownException("org.maclan.server.messageXdebugBadTransactionId", transactionId);
        }
        
        // Convert feature value to string.
        String featureValueString = supported ? String.valueOf(featureValue) : "";
        
        // Create new XML DOM document object.
        Document xml = newXmlDocument();
        Element rootElement = xml.createElement("response");
        rootElement.setAttribute("command", "feature_get");
        rootElement.setAttribute("feature_name", featureName);
        rootElement.setAttribute("supported", supported ? "1" : "0");
        rootElement.setAttribute("transaction_id", String.valueOf(transactionId));
        rootElement.setTextContent(featureValueString);
        xml.appendChild(rootElement);
        
        // Create and return new packet.
		XdebugClientResponse featurePacket = new XdebugClientResponse(xml);
		return featurePacket;
	}
	
	/**
	 * Creates response packet to the set featrue command.
	 * @param command
	 * @param success
	 * @return
	 * @throws Exception 
	 */
	public static XdebugClientResponse createSetFeatureResult(XdebugCommand command, boolean success)
			throws Exception {
		
		// Get the feature name from the input command.
		String featureName = command.getArgument("-n");

		// Get transaction ID from the input command.
		int transactionId = command.getTransactionId();
        if (transactionId < 1) {
        	onThrownException("org.maclan.server.messageXdebugBadTransactionId", transactionId);
        }
        
        // Create new XML DOM document object.
        Document xml = newXmlDocument();
        Element rootElement = xml.createElement("response");
        rootElement.setAttribute("command", "feature_set");
        rootElement.setAttribute("feature", featureName);
        rootElement.setAttribute("success", success ? "1" : "0");
        rootElement.setAttribute("transaction_id", String.valueOf(transactionId));
        xml.appendChild(rootElement);
		
        // Create and return new packet.
		XdebugClientResponse featurePacket = new XdebugClientResponse(xml);
		return featurePacket;
	}
	
	/**
	 * Create "set property" response.
	 * @param command
	 * @param propertyName
	 * @return
	 * @throws Exception 
	 */
	public static XdebugClientResponse createSetPropertyResponse(XdebugCommand command, String propertyName)
			throws Exception {
		
		// Get transaction ID from the input command.
		int transactionId = command.getTransactionId();
        if (transactionId < 1) {
        	onThrownException("org.maclan.server.messageXdebugBadTransactionId", transactionId);
        }
		
		// Create new XML DOM document object.
        Document xml = newXmlDocument();
        Element rootElement = xml.createElement("response");
        rootElement.setAttribute("command", "property_set");
        rootElement.setAttribute("success", "1");
        rootElement.setAttribute("transaction_id", String.valueOf(transactionId));
        xml.appendChild(rootElement);
        
        // Create and return new packet.
		XdebugClientResponse setPropertyPacket = new XdebugClientResponse(xml);
		return setPropertyPacket;
	}
	
	/**
	 * Create notification for breakpoint resolved event.
	 * @return
	 * @throws Exception 
	 */
	public static XdebugClientResponse createBreakpointNotification()
			throws Exception {
		
		// Create new XML DOM document object.
        Document xml = newXmlDocument();
        Element rootElement = xml.createElement("notify");
        rootElement.setAttribute("name", "breakpoint_resolved");
        xml.appendChild(rootElement);
        
        // Create and return  new packet.
		XdebugClientResponse featurePacket = new XdebugClientResponse(xml);
		return featurePacket;
	}
	
	/**
	 * Create final notification.
	 * @param server 
	 * @return
	 * @throws Exception 
	 */
	public static XdebugClientResponse createFinalNotification(AreaServer server)
			throws Exception {
		
		// Create new XML DOM document object.
        Document xml = newXmlDocument();
        Element rootElement = xml.createElement("notify");
        rootElement.setAttribute("name", "final_debug_info");
        xml.appendChild(rootElement);
        
        String resultSourceCode = server.state.text.toString();
        rootElement.setTextContent(resultSourceCode);
        
        // Create and return  new packet.
		XdebugClientResponse featurePacket = new XdebugClientResponse(xml);
		return featurePacket;
	}
	
	/**
	 * Creates response to Xdebug source command.
	 * @param command
	 * @param sourceCode
	 * @return
	 * @throws Exception 
	 */
	public static XdebugClientResponse createSourceResult(XdebugCommand command, String sourceCode)
			throws Exception {
		
		// Get transaction ID from the input command.
		int transactionId = command.getTransactionId();
        if (transactionId < 1) {
        	onThrownException("org.maclan.server.messageXdebugBadTransactionId", transactionId);
        }

        // Create new XML DOM document object.
        Document xml = newXmlDocument();
        Element rootElement = xml.createElement("response");
        rootElement.setAttribute("command", "source");
        boolean success = sourceCode != null && !sourceCode.isEmpty();
		rootElement.setAttribute("success", success ? "1" : "0");
		rootElement.setAttribute("transaction_id", String.valueOf(transactionId));
		if (success) {
			// Set source code text.
			rootElement.setTextContent(sourceCode);
		}
		xml.appendChild(rootElement);
        
        // Create and return new packet.
		XdebugClientResponse featurePacket = new XdebugClientResponse(xml);
		return featurePacket;
	}
	
	/**
	 * Creates response packet with context names.
	 * @param areaServer 
	 * @param command 
	 * @return
	 */
	public static XdebugClientResponse createContextNamesResult(XdebugCommand command, AreaServer areaServer)
			throws Exception {
		
		// Get transaction ID from the input command.
		int transactionId = command.getTransactionId();
        if (transactionId < 1) {
        	onThrownException("org.maclan.server.messageXdebugBadTransactionId", transactionId);
        }
        
        // Create new XML DOM document object.
        Document xml = newXmlDocument();
        Element rootElement = xml.createElement("response");
        rootElement.setAttribute("command", "context_names");
        rootElement.setAttribute("transaction_id", String.valueOf(transactionId));
        xml.appendChild(rootElement);
        
        // Append child node For all Xdebug client contexts.
        Map<String, Integer> contexts = XdebugClient.getContexts();
        for (Entry<String, Integer> entry : contexts.entrySet()) {
        	
        	// Get context name and ID.
        	String contextName = entry.getKey();
        	int contextId = entry.getValue();
        	String contextIdText = String.valueOf(contextId);
        	
            // Add context node.
            Element contextElement = xml.createElement("context");
            contextElement.setAttribute("name", contextName);
	        contextElement.setAttribute("id", contextIdText);
	        rootElement.appendChild(contextElement);
        }
        
        // Create and return new packet.
		XdebugClientResponse propertyPacket = new XdebugClientResponse(xml);
		return propertyPacket;
	}
		
	/**
	 * Create property XML element.
	 * @param xml
	 * @param theObject
	 * @param propertyName
	 * @param elementLambda
	 * @return
	 */
	private static void createPropertyElement(Document xml, Object theObject, String propertyName, Function<Element, Boolean> elementLambda) {
		
		Element propertyElement = xml.createElement("property");
		propertyElement.setAttribute("name", propertyName);
		propertyElement.setAttribute("type", "object");
		String className = theObject.getClass().getSimpleName();
		propertyElement.setAttribute("classname", className);
		propertyElement.setAttribute("encoding", "none");
		
		boolean hasChildren = elementLambda.apply(propertyElement);
		propertyElement.setAttribute("children", hasChildren ? "1" : "0");
	}	
	
	/**
	 * Create property value XML element.
	 * @param xml
	 * @param propertyName
	 * @param typeName
	 */
	private static void createPropertyValueElement(Document xml, String propertyName, String typeName, Consumer<Element> elementLambda) {
		
		Element propertyElement = xml.createElement("property");
		propertyElement.setAttribute("name", propertyName);
		propertyElement.setAttribute("type", typeName);
		propertyElement.setAttribute("classname", "none");
		propertyElement.setAttribute("encoding", "none");
		
		elementLambda.accept(propertyElement);
		propertyElement.setAttribute("children", "0");		
	}

	/**
	 * Creates response packet to the expr command.
	 * @param command
	 * @param exprResultText
	 * @return
	 * @throws Exception 
	 */
	public static XdebugClientResponse createExprResult(XdebugCommand command, String exprResultText)
			throws Exception {
		
		// Get transaction ID from the input command.
		int transactionId = command.getTransactionId();
        if (transactionId < 1) {
        	onThrownException("org.maclan.server.messageXdebugBadTransactionId", transactionId);
        }

        // Create new XML DOM document object.
        Document xml = newXmlDocument();
        Element rootElement = xml.createElement("response");
        rootElement.setAttribute("command", "expr");
        rootElement.setAttribute("transaction_id", String.valueOf(transactionId));
        xml.appendChild(rootElement);
        Element propertyElement = xml.createElement("property");
        propertyElement.setTextContent(exprResultText);
        rootElement.appendChild(propertyElement);
		
        // Create and return  new packet.
		XdebugClientResponse exprPacket = new XdebugClientResponse(xml);
		return exprPacket;
	}
	
	/**
	 * Create continuation command result.
	 * @param command
	 * @return
	 * @throws Exception 
	 */
	public static XdebugClientResponse createContinuationCommandResult(XdebugCommand command)
			throws Exception {
		
		// Get transaction ID from the input command.
		int transactionId = command.getTransactionId();
        if (transactionId < 1) {
        	onThrownException("org.maclan.server.messageXdebugBadTransactionId", transactionId);
        }
        
        // Get command name.
        String commandName = command.getName();

        // Create new XML DOM document object.
        Document xml = newXmlDocument();
        Element rootElement = xml.createElement("response");
        rootElement.setAttribute("command", commandName);
        rootElement.setAttribute("transaction_id", String.valueOf(transactionId));
        rootElement.setAttribute("status", "running");
        rootElement.setAttribute("reason", "ok");
        xml.appendChild(rootElement);
		
        // Create and return new packet.
		XdebugClientResponse runPacket = new XdebugClientResponse(xml);
		return runPacket;
	}
	
	/**
	 * Create stack get command result.
	 * @param command
	 * @param processId 
	 * @param processName 
	 * @param threadId 
	 * @param threadName 
	 * @param stack 
	 * @return
	 * @throws Exception 
	 */
	public static XdebugClientResponse createStackGetResult(XdebugCommand command, long processId, String processName,
			long threadId, String threadName, LinkedList<XdebugStackLevel> stack)
				throws Exception {
		
		// Get transaction ID from the input command.
		int transactionId = command.getTransactionId();
        if (transactionId < 1) {
        	onThrownException("org.maclan.server.messageXdebugBadTransactionId", transactionId);
        }
        
        // Get command name.
        String commandName = command.getName();
        
        // Create new XML DOM document object.
        Document xml = newXmlDocument();
        Element rootElement = xml.createElement("response");
        rootElement.setAttribute("command", commandName);
        
        // Add process and thread information.
        String text = String.valueOf(processId);
        rootElement.setAttribute("process_id", text);
        rootElement.setAttribute("process_name", processName);
        
        text = String.valueOf(threadId);
        rootElement.setAttribute("thread_id", text);
        rootElement.setAttribute("thread_name", threadName);
        
        rootElement.setAttribute("transaction_id", String.valueOf(transactionId));
        xml.appendChild(rootElement);
        
        for (XdebugStackLevel stackLevel : stack) {
        	
        	// Create stack XML element.
        	Element stackElement = xml.createElement("stack");
        	
        	// Set level attribute of the stack element.
        	int level = stackLevel.getLevel();
        	String levelText = String.valueOf(level);
        	stackElement.setAttribute("level", levelText);
        	
        	String type = stackLevel.getType();
        	stackElement.setAttribute("type", type);
        	
        	int stateHashCode = stackLevel.getStateHashCode();
        	String stateHashText = String.valueOf(stateHashCode);
        	stackElement.setAttribute("statehash", stateHashText);
        	
        	// Set Area Server state. The tag start position and current text position.
        	int tagStartPosition = stackLevel.getCmdBegin();
        	String tagStartPositionText = String.valueOf(tagStartPosition);
        	stackElement.setAttribute("cmdbegin", tagStartPositionText);
        	
        	int position = stackLevel.getCmdEnd();
        	String positionText = String.valueOf(position);
        	stackElement.setAttribute("cmdend", positionText);
        	
        	// Create child element for source code in the given stack level.
        	Element inputElement = xml.createElement("input");        	
        	String sourceCode = stackLevel.getSourceCode();
        	inputElement.setTextContent(sourceCode);
        	stackElement.appendChild(inputElement);
        	
        	// Append stack element to its parent element.
        	rootElement.appendChild(stackElement);
        }
        
        // Create and return new packet.
		XdebugClientResponse stackPacket = new XdebugClientResponse(xml);
		return stackPacket;
	}
	
	/**
	 * Create block variable response.
	 * @param command
	 * @param state
	 * @param variableName
	 * @return
	 * @throws Exception
	 */
	public static XdebugClientResponse createBlockVariableResponse(XdebugCommand command, AreaServerState state, String variableName)
			throws Exception {
		
		// Get transaction ID from the input command.
		int transactionId = command.getTransactionId();
        if (transactionId < 1) {
        	onThrownException("org.maclan.server.messageXdebugBadTransactionId", transactionId);
        }
        
        // Get command name.
        String commandName = command.getName();

        // Create new XML DOM document object.
        Document xml = newXmlDocument();
        Element rootElement = xml.createElement("response");
        rootElement.setAttribute("command", commandName);
        rootElement.setAttribute("transaction_id", String.valueOf(transactionId));
        xml.appendChild(rootElement);
        
		// Try to find variable and get its value.
        Obj<BlockDescriptor> blockDescriptor = new Obj<>(null);
		Variable variable = state.blocks.findVariable(variableName, blockDescriptor);
		
		String variableValueText = "unknown";
		Class<?> variableType = null;
		String blockName = null;
		
		if (variable != null) {
			
			Object variableValue = variable.value;
			if (variableValue != null) {
				
				variableValueText = variableValue.toString();
				variableType = variableValue.getClass();
			}
			else {
				variableValueText = "null";
			}
			
			if (blockDescriptor.ref != null) {
				blockName = blockDescriptor.ref.name;
				
				// If the block name doesn't exist, set it to the value of hash code from the block. 
				if (blockName == null || blockName.isEmpty()) {
					blockName = String.valueOf(blockDescriptor.ref.hashCode());
				}
			}
		}
		
		// Get variable full name with block name part as the first part of the full name.
		String variableFullName = null;
		if (blockName != null) {
			variableFullName = blockName + ':' + variableName;
		}
		
		// Get variable type name.
		String typeName = null;
		if (variableType != null) {
			typeName = variableType.getSimpleName();
		}
		else {
			typeName = "unknown";
		}
        
        // Create the property node.
        Element propertyElement = xml.createElement("property");
        propertyElement.setAttribute("type", typeName);
        propertyElement.setAttribute("children", "false");
        rootElement.appendChild(propertyElement);
        
        // Create the name node.
        Element nameElement = xml.createElement("name");
        nameElement.setAttribute("encoding", "none");
        nameElement.setTextContent(variableName);
        propertyElement.appendChild(nameElement);
        
        // Create the full name node.
        Element fullNameElement = xml.createElement("fullname");
        fullNameElement.setAttribute("encoding", "none");
        fullNameElement.setTextContent(variableFullName);
        propertyElement.appendChild(fullNameElement);

         // Create the value node.
        Element valueElement = xml.createElement("value");
        valueElement.setAttribute("encoding", "none");
        valueElement.setTextContent(variableValueText);
        propertyElement.appendChild(valueElement);
        
        // Create and return new packet.
		XdebugClientResponse blockVariablePacket = new XdebugClientResponse(xml);
		return blockVariablePacket;
	}
	
	/**
	 * Create tag property response.
	 * @param command
	 * @param state
	 * @param propertyName
	 * @return
	 * @throws Exception 
	 */
	public static XdebugClientResponse createTagPropertyResponse(XdebugCommand command, AreaServerState state, String propertyName) 
					throws Exception {

		// Get transaction ID from the input command.
		int transactionId = command.getTransactionId();
        if (transactionId < 1) {
        	onThrownException("org.maclan.server.messageXdebugBadTransactionId", transactionId);
        }
        
        // Get command name.
        String commandName = command.getName();

        // Create new XML DOM document object.
        Document xml = newXmlDocument();
        Element rootElement = xml.createElement("response");
        rootElement.setAttribute("command", commandName);
        rootElement.setAttribute("transaction_id", String.valueOf(transactionId));
        xml.appendChild(rootElement);
        
        // Try to get property full name and value.
        String tagName = null;
        Object value = null;
        DebugInfo debugInfo = state.getDebugInfo();
        if (debugInfo != null) {
        	
        	DebugTagInfo tagInfo = debugInfo.getTagInfo();
        	if (tagInfo != null) {
        		
        		tagName = tagInfo.getTagName();
        		if (tagName != null) {

            		Properties tagProperties = tagInfo.getProperties();
            		if (tagProperties != null) {
            			
            			boolean propertyExists = tagProperties.containsKey(propertyName);
            			if (propertyExists) {
            				
            				value = tagProperties.get(propertyName);
            			}
            		}        			
        		}
        	}
        }
        
        if (tagName == null) {
        	tagName = "unknown";
        }
        
        String propertyFullName = tagName + ':' + propertyName;
        String propertyValueText = null;
        String typeName = null;
        
        if (value != null) {
        	propertyValueText = value.toString();
        	typeName = value.getClass().getSimpleName();
        }
        else {
        	propertyValueText = "null";
        	typeName = "*unknown*";
        }
        
        // Create the property node.
        Element propertyElement = xml.createElement("property");
        propertyElement.setAttribute("type", typeName);
        propertyElement.setAttribute("children", "false");
        rootElement.appendChild(propertyElement);
        
        // Create the name node.
        Element nameElement = xml.createElement("name");
        nameElement.setAttribute("encoding", "none");
        nameElement.setTextContent(propertyName);
        propertyElement.appendChild(nameElement);
        
        // Create the full name node.
        Element fullNameElement = xml.createElement("fullname");
        fullNameElement.setAttribute("encoding", "none");
        fullNameElement.setTextContent(propertyFullName);
        propertyElement.appendChild(fullNameElement);

         // Create the value node.
        Element valueElement = xml.createElement("value");
        valueElement.setAttribute("encoding", "none");
        valueElement.setTextContent(propertyValueText);
        propertyElement.appendChild(valueElement);        
        
        // Create and return new packet.
		XdebugClientResponse tagPacket = new XdebugClientResponse(xml);
		return tagPacket;
	}
	
	/**
	 * Create area property response.
	 * @param command
	 * @param server
	 * @param propertyName
	 * @return
	 * @throws Exception 
	 */
	public static XdebugClientResponse createAreaPropertyResponse(XdebugCommand command, AreaServer server, String propertyName)
			throws Exception {
		
		// Get transaction ID from the input command.
		int transactionId = command.getTransactionId();
        if (transactionId < 1) {
        	onThrownException("org.maclan.server.messageXdebugBadTransactionId", transactionId);
        }
        
        // Get command name.
        String commandName = command.getName();
		
        // Create new XML DOM document object.
        Document xml = newXmlDocument();
        Element rootElement = xml.createElement("response");
        rootElement.setAttribute("command", commandName);
        rootElement.setAttribute("transaction_id", String.valueOf(transactionId));
        xml.appendChild(rootElement);
        
        // Get current Area Server state.
        AreaServerState state = server.state;
        
        // Set properties.
        Area area = null;
        VersionObj version = null;
        Language language = null;
        StartResource startResource = null;
        
        DebugWatchItemType propertyType = DebugWatchItemType.UNKNOWN;
        		
        switch (propertyName) {
        case "thisArea":
        	area = state.area;
        	propertyType = DebugWatchItemType.AREA;
        	break;
        case "requestedArea":
        	area = state.requestedArea;
        	propertyType = DebugWatchItemType.AREA;
        	break;
        case "startArea":
        	area = state.startArea;
        	propertyType = DebugWatchItemType.AREA;
        	break;
        case "homeArea":
        	area = server.getHomeArea();
        	propertyType = DebugWatchItemType.AREA;
        	break;        	
        case "currentVersion":
        	version = server.geCurrentVersion();
        	propertyType = DebugWatchItemType.VERSION;
            break;
        case "currentLanguage":
        	language = server.getCurrentLanguage();
        	propertyType = DebugWatchItemType.LANGUAGE;
            break;
        case "startResource":
        	startResource = server.getCurrentStartResource();
        	propertyType = DebugWatchItemType.START_RESOURCE;
            break;             
        }
        
        // Get property value and type name.
        String propertyValue = null;
        switch (propertyType) {
        case AREA:
        	propertyValue = (area != null ? area.getDescriptionForced(true) : "*unknown*");
        	break;
        case VERSION:
        	propertyValue = (version != null ? version.getDescriptionWithId() : "*unknown*");
        	break;
        case LANGUAGE:
        	propertyValue = (language != null ? language.toString() : "*unknown*");
        	break;
        case START_RESOURCE:
        	propertyValue = (startResource != null ? startResource.toString() : "*unknown*");
        	break;
        default:
        	propertyValue = "";
        }
        
        String propertyTypeName = propertyType.getName();

        // Create the property node.
        Element propertyElement = xml.createElement("property");
        propertyElement.setAttribute("type", propertyTypeName);
        propertyElement.setAttribute("children", "false");
        rootElement.appendChild(propertyElement);
        
        // Create the name node.
        Element nameElement = xml.createElement("name");
        nameElement.setAttribute("encoding", "none");
        nameElement.setTextContent(propertyName);
        propertyElement.appendChild(nameElement);
        
        // Create the full name node.
        Element fullNameElement = xml.createElement("fullname");
        fullNameElement.setAttribute("encoding", "none");
        fullNameElement.setTextContent(propertyName);
        propertyElement.appendChild(fullNameElement);

         // Create the value node.
        Element valueElement = xml.createElement("value");
        valueElement.setAttribute("encoding", "none");
        valueElement.setTextContent(propertyValue);
        propertyElement.appendChild(valueElement);
		
		// Create and return new packet.
		XdebugClientResponse areaPacket = new XdebugClientResponse(xml);
		return areaPacket;
	}
	
	/**
	 * Create server property response.
	 * @param command
	 * @param server
	 * @param propertyName
	 * @return
	 * @throws Exception 
	 */
	public static XdebugClientResponse createServerPropertyResponse(XdebugCommand command, AreaServer server, String propertyName)
					throws Exception {
		
		// Get transaction ID from the input command.
		int transactionId = command.getTransactionId();
        if (transactionId < 1) {
        	onThrownException("org.maclan.server.messageXdebugBadTransactionId", transactionId);
        }
        
        // Get command name.
        String commandName = command.getName();
		
        // Create new XML DOM document object.
        Document xml = newXmlDocument();
        Element rootElement = xml.createElement("response");
        rootElement.setAttribute("command", commandName);
        rootElement.setAttribute("transaction_id", String.valueOf(transactionId));
        xml.appendChild(rootElement);
        
        // Set server properties.
        String serverValue = null;
        String serverType = "Unknown";
        DebugSourceInfo sourceInfo = null;
        
        switch (propertyName) {
        
        case "serverUrl":
        	serverValue = server.getServerUrl();
        	serverType = "String";
        	break;
        	
        case "serverLevel":
        	long serverLevel = server.getServerLevel();
        	serverValue = String.valueOf(serverLevel);
        	serverType = "Long";
        	break; 
        	
        case "sourceInfo":
        	String stateHashCodeText = command.getArgument("-h");
        	int stateHashCode = Integer.valueOf(stateHashCodeText);
        	sourceInfo = server.getSourceInfo(stateHashCode);
        	serverType = "XML";
        	break;
        }
        
        // Create the property node.
        Element propertyElement = xml.createElement("property");
        propertyElement.setAttribute("type", serverType);
        propertyElement.setAttribute("children", "false");
        rootElement.appendChild(propertyElement);
        
        // Create the name node.
        Element nameElement = xml.createElement("name");
        nameElement.setAttribute("encoding", "none");
        nameElement.setTextContent(propertyName);
        propertyElement.appendChild(nameElement);
        
        // Create the full name node.
        Element fullNameElement = xml.createElement("fullname");
        fullNameElement.setAttribute("encoding", "none");
        fullNameElement.setTextContent(propertyName);
        propertyElement.appendChild(fullNameElement);

        // Create the value node.
        if (serverValue == null || serverValue.isEmpty()) {
        	serverValue = "*unknown*";
        }
        
        Element valueElement = xml.createElement("value");
        valueElement.setAttribute("encoding", "none");
        
        if (sourceInfo != null) {
        	
        	boolean hasSourceInfo = false;
        	
        	// Add resource information. 
        	Long resourceId = sourceInfo.getResourceId();
        	if (resourceId != null) {
        		
        		Element resourceElement = xml.createElement("resource");
        		
        		String resourceIdText = String.valueOf(resourceId);
        		resourceElement.setAttribute("id", resourceIdText);
        		String resourceName = sourceInfo.getResourceName();
        		resourceElement.setAttribute("name", resourceName);
        		
        		valueElement.appendChild(resourceElement);
        		hasSourceInfo = true;
        	};
        	
        	// Add slot information.
        	Long slotId = sourceInfo.getSlotId();
        	if (slotId != null) {
        		
        		Element slotElement = xml.createElement("slot");
        		
        		String slotIdText = String.valueOf(slotId);
        		slotElement.setAttribute("id", slotIdText);
        		String slotName = sourceInfo.getSlotName();
        		slotElement.setAttribute("name", slotName);
        		
        		valueElement.appendChild(slotElement);
        		hasSourceInfo = true;
        	};
        	
        	// Add area information.
        	Long areaId = sourceInfo.getAreaId();
        	if (hasSourceInfo && (areaId != null)) {
                
        		Element areaElement = xml.createElement("area");
        		
        		String areaIdText = String.valueOf(areaId);
        		areaElement.setAttribute("id", areaIdText);
        		String areaName = sourceInfo.getAreaName();
        		areaElement.setAttribute("name", areaName);
        		
        		valueElement.appendChild(areaElement);
            }
        }
        else {
        	valueElement.setTextContent(serverValue);
        }
        propertyElement.appendChild(valueElement);
        
		// Create and return new packet.
		XdebugClientResponse serverPacket = new XdebugClientResponse(xml);
		return serverPacket;
	}
	
	/**
	 * Create context items response.
	 * @param command
	 * @param watchItems
	 * @return
	 * @throws Exception 
	 */
	public static XdebugClientResponse createContextGetResult(XdebugCommand command, LinkedList<DebugWatchItem> watchItems)
				throws Exception {
		
		// Get transaction ID from the input command.
		int transactionId = command.getTransactionId();
        if (transactionId < 1) {
        	onThrownException("org.maclan.server.messageXdebugBadTransactionId", transactionId);
        }
        
        // Get command name.
        String commandName = command.getName();

        // Create new XML DOM document object.
        Document xml = newXmlDocument();
        Element rootElement = xml.createElement("response");
        rootElement.setAttribute("command", commandName);
        rootElement.setAttribute("transaction_id", String.valueOf(transactionId));
        xml.appendChild(rootElement);
        
        // Create property nodes.
        for (DebugWatchItem watchItem : watchItems) {
        	
        	Element propertyElement = xml.createElement("property");
        	String propertyName = watchItem.getName();
        	propertyElement.setAttribute("name", propertyName);
        	String propertyFullName = watchItem.getFullName();
        	propertyElement.setAttribute("fullname", propertyFullName);
        	String typeName = watchItem.getGroupName();
        	propertyElement.setAttribute("type", typeName);
        	rootElement.appendChild(propertyElement);
        }
        		
        // Create and return new packet.
		XdebugClientResponse contextPacket = new XdebugClientResponse(xml);
		return contextPacket;
	}
	
	/**
	 * Create no context result.
	 * @param command
	 * @return
	 * @throws Exception 
	 */
	public static XdebugClientResponse createContextGetNullResult(XdebugCommand command)
			throws Exception {
		
		// Get transaction ID from the input command.
		int transactionId = command.getTransactionId();
        if (transactionId < 1) {
        	onThrownException("org.maclan.server.messageXdebugBadTransactionId", transactionId);
        }
        
        // Get command name.
        String commandName = command.getName();

        // Create new XML DOM document object.
        Document xml = newXmlDocument();
        Element rootElement = xml.createElement("response");
        rootElement.setAttribute("command", commandName);
        rootElement.setAttribute("transaction_id", String.valueOf(transactionId));
        xml.appendChild(rootElement);
        
        Element nullContextElement = xml.createElement("null_context");
        rootElement.appendChild(nullContextElement);
        
        // Create and return new packet.
		XdebugClientResponse nullContextPacket = new XdebugClientResponse(xml);
		return nullContextPacket;
	}
	
	/**
	 * Check if the input packet is an INIT packet.
	 * @return
	 * @throws Exception 
	 */
	public boolean isInitPacket()
			throws Exception {
		
		// Check packet.
		if (xml == null) {
			return false;
		}
		
		// Try to get packet root node.
		String nodeName = (String) xpathRootNodeName.evaluate(xml, XPathConstants.STRING);
		boolean isInitPacket = "init".equalsIgnoreCase(nodeName);
		return isInitPacket;
	}
	
	/**
	 * Check if the input packet is a notification packet.
	 * @return
	 * @throws Exception 
	 */
	public boolean isNotificationPacket()
			throws Exception {
		
		// Check packet.
		if (xml == null) {
			return false;
		}
		
		// Try to get packet root node.
		String nodeName = (String) xpathRootNodeName.evaluate(xml, XPathConstants.STRING);
		boolean isNotificationPacket = "notify".equalsIgnoreCase(nodeName);
		return isNotificationPacket;
	}
	
	/**
	 * Check if the input packet is an error packet.
	 * @return
	 * @throws Exception 
	 */
	public boolean isErrorPacket()
			throws Exception {
		
		// Check packet.
		if (xml == null) {
			return false;
		}
		
		// Try to get packet root node.
		String codeText = (String) xpathErrorCode.evaluate(xml, XPathConstants.STRING);
		boolean isErrorPacket = !codeText.isEmpty();
		return isErrorPacket;
	}
	
	/**
	 * Check if the input packet is result of successful property set operation.
	 * @return
	 * @throws Exception 
	 */
	public boolean isPropertySetSuccess() {

		try {
			String successText = (String) xpathResponseSuccess.evaluate(xml, XPathConstants.STRING);
			boolean success = "1".equals(successText);
			return success;
		}
		catch (Exception e) {
		}
		
		return false;
	}
	
	/**
	 * Creates new XML DOM document object.
	 * @return
	 * @throws ParserConfigurationException 
	 */
	private static Document newXmlDocument() 
			throws Exception {
		
        // Create a new DocumentBuilderFactory.
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        // Use the factory to create a new DocumentBuilder.
        DocumentBuilder builder = factory.newDocumentBuilder();

        // Create a new Document object.
        Document document = builder.newDocument();
		return document;
	}
	
	/**
	 * Get Xdebug XML response.
	 * @param xmlText
	 * @return
	 * @throws Exception 
	 */
	static XdebugClientResponse getXmlContent(String xmlText)
				throws Exception {
		
        // Parse the XML string into a Document.
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        
        Document xml = builder.parse(new InputSource(new StringReader(xmlText)));
		
        // Create new packet object.
    	XdebugClientResponse clientResponse = new XdebugClientResponse(xml);
    	
    	// Return XML response.
    	return clientResponse;
	}
	
	/**
	 * Get packet text.
	 * @return
	 * @throws Exception 
	 */
	public String getText() 
			throws Exception {
		
		if (xml == null) {
			return "";
		}
		
		synchronized (lsSerializer) {
			
			String text = lsSerializer.writeToString(xml);
			return text;
		}
	}

	/**
	 * Get packet bytes.
	 * @return
	 * @throws Exception 
	 */
	public byte [] getBytes() 
			throws Exception {
		
        // Delegate the call to get string buffer with XML text representation.
		String text = getText();
		if (text == null) {
			return null;
		}
		
		byte [] bytes = text.getBytes("UTF-8");
		return bytes;
	}
	
	/**
	 * Check if the IDE key in current packet macthes the input value.
	 * @param ideKey
	 * @throws Exception 
	 */
	public boolean checkIdeKey(String ideKey, Obj<String> packetIdeKey)
			throws Exception {
		
		// Check input value.
		if (ideKey == null) {
			packetIdeKey.ref = "null";
			return false;
		}
		
		// Get packet IDE key.
		packetIdeKey.ref = (String) xpathInitIdeKeyName.evaluate(xml, XPathConstants.STRING);
		boolean matches = ideKey.equalsIgnoreCase(packetIdeKey.ref);
		return matches;
	}
	
	/**
	 * Check if the application ID in current packet macthes the input value.
	 * @param appId
	 * @param foundAppId
	 * @return
	 * @throws Exception 
	 */
	public boolean checkAppId(String appId, Obj<String> foundAppId)
			throws Exception {
		
		// Check input value.
		if (appId == null) {
			foundAppId.ref = "null";
			return false;
		}
		
		// Get packet IDE key.
		foundAppId.ref = (String) xpathInitAppIdName.evaluate(xml, XPathConstants.STRING);
		boolean matches = appId.equalsIgnoreCase(foundAppId.ref);
		return matches;
	}
	
	/**
	 * Check if the debugged language in current packet macthes the input value.
	 * @param languageName
	 * @param foundLanguageName
	 * @return
	 */
	public boolean checkLanguage(String languageName, Obj<String> foundLanguageName)
			throws Exception {
		
		// Check input value.
		if (languageName == null) {
			foundLanguageName.ref = "null";
			return false;
		}
		
		// Get language name from the packet data.
		foundLanguageName.ref = (String) xpathLanguageName.evaluate(xml, XPathConstants.STRING);
		boolean matches = languageName.equalsIgnoreCase(foundLanguageName.ref);
		return matches;		
	}
	
	/**
	 * Check if protocol version in current packet macthes the input value.
	 * @param protocolVersion
	 * @param foundProtocolVersion
	 * @return
	 */
	public boolean checkProtocolVersion(String protocolVersion, Obj<String> foundProtocolVersion)
			throws Exception {
		
		// Check input value.
		if (protocolVersion == null) {
			foundProtocolVersion.ref = "null";
			return false;
		}
		
		// Get protocol version from the packet data.
		foundProtocolVersion.ref = (String) xpathProtocolVersion.evaluate(xml, XPathConstants.STRING);
		boolean matches = protocolVersion.equalsIgnoreCase(foundProtocolVersion.ref);
		return matches;	
	}
	
	/**
	 * Get debugged process URI.
	 * @return
	 */
	public String getDebuggedUri()
		throws Exception {
		
		// Try to get URI from current packet.
		String debuggedUri = (String) xpathDebuggedUri.evaluate(xml, XPathConstants.STRING);
		debuggedUri = URLDecoder.decode(debuggedUri, "UTF-8");
		debuggedUri = StringEscapeUtils.unescapeHtml4(debuggedUri);
		return debuggedUri;
	}
	
	/**
	 * Parse debugger URI.
	 * @param debuggerUri
	 * @return
	 */
	public static CurrentXdebugClientData parseDebuggedUri(String debuggerUri) 
			throws Exception {
		
		// Create URI matcher with regular expression.
		Matcher matcher = regexUriParser.matcher(debuggerUri);
		
		boolean success = matcher.find();
		int groupCount = matcher.groupCount();
		if (success && groupCount == 5) {
			
			CurrentXdebugClientData clientParameters = new CurrentXdebugClientData();
			String computer = matcher.group("computer");
		    clientParameters.setComputer(computer);
		    String processIdText = matcher.group("pid");
		    Long processId = Long.parseLong(processIdText);
		    clientParameters.setProcessId(processId);
		    String threadIdText = matcher.group("tid");
		    Long thredId = Long.parseLong(threadIdText);
		    clientParameters.setThreadId(thredId);
		    String areaIdText = matcher.group("aid");
		    Long areaId = Long.parseLong(areaIdText);
		    clientParameters.setAreaId(areaId);
		    String hashText = matcher.group("statehash");
		    Integer stateHash = Integer.parseInt(hashText);
		    clientParameters.setStatehash(stateHash);
		    return clientParameters;
		}
		
		onThrownException("org.maclan.server.messageBadDebuggerUri", debuggerUri);
		return null;
	}
	
	/**
	 * Get transaction ID.
	 * @return
	 */
	public int getTransactionId()
			throws Exception {
		
		try {
			// Get transaction ID from XML Document.
			String transactionIdText = (String) xpathResponseTransactionId.evaluate(xml, XPathConstants.STRING);
			int transactionId = Integer.parseInt(transactionIdText);
			return transactionId;
		}
		catch (Exception e) {
			onThrownException("org.maclan.server.messageBadXdebugTransactionId");
		}
		return -1;
	}
	
	/**
	 * Get Xdebug feature.
	 * @return
	 * @throws Exception 
	 */
	public XdebugFeature getFeatureValue()
			throws Exception {
		
		// Get feature attributes.
		String commandName = (String) xpathResponseCommandName.evaluate(xml, XPathConstants.STRING);
		if (!"feature_get".equals(commandName)) {
			onThrownException("org.maclan.server.messageBadXdebugCommandName", "feature_get", commandName);
		}
		String featureName = (String) xpathResponseFeatureName.evaluate(xml, XPathConstants.STRING);
		String supportedString = (String) xpathResponseFeatureSupported.evaluate(xml, XPathConstants.STRING);
		String featureValue = (String) xpathResponseFeatureValue.evaluate(xml, XPathConstants.STRING);
		
		// Create feature object from this packet.
		XdebugFeature feature = XdebugFeature.createFeature(featureName, supportedString, featureValue);
		return feature;
	}
	
	/**
	 * Get feature result. 
	 * @return
	 * @throws Exception 
	 */
	public boolean getSettingFeatureResult()
			throws Exception {
		
		// Check command name.
		String commandName = (String) xpathResponseCommandName.evaluate(xml, XPathConstants.STRING);
		if (!"feature_set".equals(commandName)) {
			onThrownException("org.maclan.server.messageBadXdebugCommandName", "feature_set", commandName);
		}
		
		boolean success = (boolean) xpathSuccessResponse.evaluate(xml, XPathConstants.BOOLEAN);
		return success;
	}
	
	/**
	 * Check if it is a "breakpoint resolved" response.
	 * @return
	 */
	public boolean isBreakpointResolved()
			throws Exception {
		
		String notificationName = (String) xpathNotificationName.evaluate(xml, XPathConstants.STRING);
		boolean success = "breakpoint_resolved".equals(notificationName);
		return success;
	}
	
	/**
	 * Check if it is a "final_debug_info" response.
	 * @return
	 */
	public boolean isFinalDebugInfo()
			throws Exception {
		
		String notificationName = (String) xpathNotificationName.evaluate(xml, XPathConstants.STRING);
		boolean success = "final_debug_info".equals(notificationName);
		return success;
	}
	
	/**
	 * Get source result.
	 * @return
	 * @throws Exception
	 */
	public String getSourceResult()
			throws Exception {
		
		// Check command name.
		String commandName = (String) xpathResponseCommandName.evaluate(xml, XPathConstants.STRING);
		if (!"source".equals(commandName)) {
			onThrownException("org.maclan.server.messageBadXdebugCommandName", "source", commandName);
		}
		
		// Get source code.
		String sourceCode = (String) xpathSourceResponse.evaluate(xml, XPathConstants.STRING);
		return sourceCode;
	}
	
	/**
	 * Get final spource code.
	 * @return
	 * @throws Exception 
	 */
	public String getFinalSourceCode() throws Exception {
		
		// Check notification name.
		String notificationName = (String) xpathNotificationName.evaluate(xml, XPathConstants.STRING);
		boolean success = "final_debug_info".equals(notificationName);
		if (!success) {
			return "";
		}
		
		// Get final source code.
		String finalSourceCode = (String) xpathFinalSourceResponse.evaluate(xml, XPathConstants.STRING);
        return finalSourceCode;
	}

	/**
	 * Get expr result.
	 * @throws Exception
	 */
	public String getExprResult()
			throws Exception {
		
		// Check command name.
		String commandName = (String) xpathResponseCommandName.evaluate(xml, XPathConstants.STRING);
		if (!"expr".equals(commandName)) {
			onThrownException("org.maclan.server.messageBadXdebugCommandName", "expr", commandName);
		}
		
		// Get expression result.
		String evalResult = (String) xpathEvalResponse.evaluate(xml, XPathConstants.STRING);
		return evalResult;
	}
	
	/**
	 * Get context names.
	 * @return
	 * @throws Exception 
	 */
	public LinkedHashMap<String, Integer> getContextNames()
			throws Exception {
		
		// Check command name.
		String commandName = (String) xpathResponseCommandName.evaluate(xml, XPathConstants.STRING);
		if (!"context_names".equals(commandName)) {
			onThrownException("org.maclan.server.messageBadXdebugCommandName", "context_names", commandName);
		}
		
		// Initialize context map.
		LinkedHashMap<String, Integer> contextMap = null;
		
		// Load context names and IDs.
		NodeList nodeList = (NodeList) xpathContextsResponse.evaluate(xml, XPathConstants.NODESET);
		int length = nodeList.getLength();
		
		for (int index = 0; index < length; index++) {
			
			Node node = nodeList.item(index);
			String nodeName = node.getNodeName();
			
			// Check node.
			if (!"context".equals(nodeName)) {
				continue;
			}
			
			// Get context name and ID.
			NamedNodeMap nodeAttributes = node.getAttributes();
			if (nodeAttributes == null) {
				continue;
			}
			
			node = nodeAttributes.getNamedItem("name");
			String contextName = node.getNodeValue();
			
			node = nodeAttributes.getNamedItem("id");
			String contextIdText = node.getNodeValue();
			Integer contextId = null;
			try {
				contextId = Integer.parseInt(contextIdText);
			}
			catch (Exception e) {
				continue;
			}
			
			// Add context to map.
			if (contextMap == null) {
				contextMap = new LinkedHashMap<String, Integer>();
			}
			contextMap.put(contextName, contextId);
		}
		
		return contextMap;
	}
	
	/**
	 * Get Area Server stack.
	 * @param processId 
	 * @param processName 
	 * @param threadId 
	 * @param threadName 
	 * @return
	 * @throws XPathExpressionException 
	 */
	public LinkedList<XdebugStackLevel> getXdebugAreaServerStack(Obj<Long> processId, Obj<String> processName,
			Obj<Long> threadId, Obj<String> threadName)
					throws Exception {
		
		String commandName = (String) xpathResponseCommandName.evaluate(xml, XPathConstants.STRING);
		if (!"stack_get".equals(commandName)) {
			onThrownException("org.maclan.server.messageBadXdebugCommandName", "stack_get", commandName);
		}
		
		// Get process ID and process name.
		if (processId != null) {
			String processIdText = (String) xpathStackProcessId.evaluate(xml, XPathConstants.STRING);
			processId.ref = Long.parseLong(processIdText);
		}
		
		if (processName != null) {
			processName.ref = (String) xpathStackProcessName.evaluate(xml, XPathConstants.STRING);
		}
		
		// Get thread ID and thread name.
		if (threadId != null) {
			String threadIdText = (String) xpathStackThreadId.evaluate(xml, XPathConstants.STRING);
			threadId.ref = Long.parseLong(threadIdText);
		}
		
		if (threadName != null) {
			threadName.ref = (String) xpathStackThreadName.evaluate(xml, XPathConstants.STRING);
		}
		
		LinkedList<XdebugStackLevel> stack = new LinkedList<>();
		
		try {
			// Get stack root, i.e. the current stack level which is level 0.
			NodeList stackNodes = (NodeList) xpathStackRootNodes.evaluate(xml, XPathConstants.NODESET);
			int nodeCount = stackNodes.getLength();
			
			for (int index = 0; index < nodeCount; index++) {
				
				Node stackNode = stackNodes.item(index);
				String nodeName = stackNode.getNodeName();
				if (!"stack".equals(nodeName)) {
					onThrownException("org.maclan.server.messageXdebugProtocolExpectingStackNode", nodeName);
				}
				
				// Create new stack level object from XML stack node.
				String textValue = (String) xpathRelativeStackLevel.evaluate(stackNode, XPathConstants.STRING);
				int level = Integer.valueOf(textValue);
				
				String type = (String) xpathRelativeStackType.evaluate(stackNode, XPathConstants.STRING);
				
				String stateHashText = (String) xpathRelativeStackStateHash.evaluate(stackNode, XPathConstants.STRING);
				int stateHash = Integer.valueOf(stateHashText);
				
				textValue = (String) xpathRelativeStackCmdBegin.evaluate(stackNode, XPathConstants.STRING);
				int cmdBegin = Integer.valueOf(textValue);
				
				textValue = (String) xpathRelativeStackCmdEnd.evaluate(stackNode, XPathConstants.STRING);
				int cmdEnd = Integer.valueOf(textValue);
				
				String sourceCode = (String) xpathRelativeSourceCode.evaluate(stackNode, XPathConstants.STRING);
				
				XdebugStackLevel stackLevel = new XdebugStackLevel(level, type, stateHash, cmdBegin, cmdEnd, sourceCode);
				stack.add(stackLevel);
			}
		}
		catch (Exception e) {
			onThrownException(e);
		}
		return stack;
	}
	
	/**
	 * Get watched item.
	 * @param watchedType 
	 * @return
	 * @throws XPathExpressionException 
	 */
	public DebugWatchItem getXdebugWathItemResult(DebugWatchGroup watchedType)
			throws Exception {
		
		String commandName = (String) xpathResponseCommandName.evaluate(xml, XPathConstants.STRING);
		if (!"property_get".equals(commandName)) {
			onThrownException("org.maclan.server.messageBadXdebugCommandName", "stack_get", commandName);
		}
		
		// Get watched item name, full name, type and value.
		String itemName = (String) xpathPropertyName.evaluate(xml, XPathConstants.STRING);
		String itemFullName = (String) xpathPropertyFullName.evaluate(xml, XPathConstants.STRING);
		String valueText = (String) xpathPropertyValue.evaluate(xml, XPathConstants.STRING);
		String valueTypeText = (String) xpathPropertyValueType.evaluate(xml, XPathConstants.STRING);

		// Create watched item object.
		DebugWatchItem watchItem = new DebugWatchItem(watchedType, itemName, itemFullName, valueText, valueTypeText);
		return watchItem;
	}
	
	/**
	 * Get the Area Server context properties for debugger watch list.
	 * @return
	 * @throws Exception 
	 */
	public LinkedList<DebugWatchItem> getContextProperties()
			throws Exception {
		
		// Check Xdebug command name.
		String commandName = (String) xpathResponseCommandName.evaluate(xml, XPathConstants.STRING);
		if (!"context_get".equals(commandName)) {
			onThrownException("org.maclan.server.messageBadXdebugCommandName", "context_get", commandName);
		}
		
		// Check null context.
		Node nullContextNode = (Node) xpathResponseNullContext.evaluate(xml, XPathConstants.NODE);
		if (nullContextNode != null) {
			
			return null;
		}
		
		// Initialize watch list.
		LinkedList<DebugWatchItem> watchList = new LinkedList<DebugWatchItem>();
		
		// Get watch list items from current response object.
		NodeList properties = (NodeList) xpathResponseProperties.evaluate(xml, XPathConstants.NODESET);
		int count = properties.getLength();
		
		for (int index = 0; index < count; index++) {
			
			Node propertyNode = properties.item(index);
			String propertyName = propertyNode.getNodeName();
			
			if (!"property".equals(propertyName)) {
				onThrownException("org.maclan.server.messageXdebugExpectingResponseNode", "property", propertyName);
			}
			
			// Get watched item properties.
			String name = (String) xpathResponsePropertyName.evaluate(propertyNode, XPathConstants.STRING);
			String fullName = (String) xpathResponsePropertyFullName.evaluate(propertyNode, XPathConstants.STRING);
			String typeName = (String) xpathResponsePropertyTypeName.evaluate(propertyNode, XPathConstants.STRING);
			DebugWatchGroup type = DebugWatchGroup.getByName(typeName);
			
			// Create debugger watched item and add it to the list.
			DebugWatchItem watchedItem = new DebugWatchItem(type, name, fullName, null, null);
			watchList.add(watchedItem);
		}
		
		return watchList;
	}
	
	/**
	 * Get code source information.
	 * @return
	 */
	public DebugSourceInfo getSourceInformation() {
		
		try {
			String commandName = (String) xpathResponseCommandName.evaluate(xml, XPathConstants.STRING);
			if (!"property_get".equals(commandName)) {
				onThrownException("org.maclan.server.messageBadXdebugCommandName", "property_get", commandName);
			}
			
			DebugSourceInfo sourceInfo = new DebugSourceInfo();
			
			// Resource ID.
			String resourceIdText = (String) xpathSourceInfoResourceId.evaluate(xml, XPathConstants.STRING);
			if (resourceIdText != null && !resourceIdText.isEmpty()) {
				
                Long resourceId = Long.parseLong(resourceIdText);
                sourceInfo.setResourceId(resourceId);
            }
			
			// Resource name.
			String resourceName = (String) xpathSourceInfoResourceName.evaluate(xml, XPathConstants.STRING);
			if (resourceName != null && !resourceName.isEmpty()) {
                
                sourceInfo.setResourceName(resourceName);
            }
			
			// Slot ID.
			String slotIdText = (String) xpathSourceInfoSlotId.evaluate(xml, XPathConstants.STRING);
			if (slotIdText != null && !slotIdText.isEmpty()) {
				
                Long slotId = Long.parseLong(slotIdText);
                sourceInfo.setSlotId(slotId);
            }
			
			// Slot name.
			String slotName = (String) xpathSourceInfoSlotName.evaluate(xml, XPathConstants.STRING);
			if (slotName != null && !slotName.isEmpty()) {
                
                sourceInfo.setSlotName(slotName);
            }
			
			// Area ID.
			String areaIdText = (String) xpathSourceInfoAreaId.evaluate(xml, XPathConstants.STRING);
			if (areaIdText != null && !areaIdText.isEmpty()) {
				
                Long areaId = Long.parseLong(areaIdText);
                sourceInfo.setAreaId(areaId);
            }
			
			// Area name.
			String areaName = (String) xpathSourceInfoAreaName.evaluate(xml, XPathConstants.STRING);
			if (areaName != null && !areaName.isEmpty()) {	
				
				sourceInfo.setAreaName(areaName);
			}
			
			return sourceInfo;
		}
		catch (Exception e) {
            onException(e);
        }
		
		return null;
	}
	
	/**
	 * Get debug string.
	 * @return
	 */
	public String getDebugString() {
		
		try {
			String command = (String) xpathResponseCommandName.evaluate(xml, XPathConstants.STRING);
			if (command != null && !command.isEmpty()) {
				command = "command " + command;
				return command;
			}
			
		}
		catch (Exception e) {
		}
		try {
			String notification = (String) xpathNotificationName.evaluate(xml, XPathConstants.STRING);
			if (notification != null && !notification.isEmpty()) {
				notification = "notification " + notification;
				return notification;
			}
		}
		catch (Exception e) {
		}	
		return "unknown";
	}

	/**
	 * Get error message.
	 * @return
	 */
	public String getErrorMessage()
			throws Exception {
		
		// Check command name, error code with description and message text.
		String commandName = (String) xpathResponseCommandName.evaluate(xml, XPathConstants.STRING);
		
		String codeText = (String) xpathErrorCode.evaluate(xml, XPathConstants.STRING);
		if (codeText.isEmpty()) {
			onThrowException("org.maclan.server.messageMissingErrorCode");
		}
		
		String errorCodeDescription = "";
		try {
			int errorCode = Integer.parseInt(codeText);
			errorCodeDescription = XdebugError.getErrorDescription(errorCode);
		}
		catch (Exception e) {
			onException(e);
		}
		
		String messageText = (String) xpathErrorMessage.evaluate(xml, XPathConstants.STRING);
		String errorMessage = String.format("ERROR %s, Xdebug command \"%s\". %s %s", codeText, commandName, errorCodeDescription, messageText);
		return errorMessage;
	}
	
	/**
	 * Throws exception created from the response packet.
	 */
	public void throwPacketException()
			throws Exception {
		
		String errorMessage = getErrorMessage();
		Exception exception = new Exception(errorMessage);
		throw exception;
	}
	
	/**
	 * Called on exception.
	 * @param messageId
	 * @throws Exception
	 */
	private void onThrowException(String messageId) throws Exception {
		
		String message = Resources.getString(messageId);
		Exception exception = new Exception(message);
		onException(exception);
		throw exception;
	}
	
	/**
	 * On exception.
	 * @param messageFormatId
	 * @param parameters
	 */
	private static void onThrownException(String messageFormatId, Object ... parameters)
			throws Exception {
		
		String messageFormat = Resources.getString(messageFormatId);
		String message = String.format(messageFormat, parameters);
		Exception exception = new Exception(message);
		onException(exception);
		throw exception;		
	}
	
	/**
	 * On exception.
	 * @param e
	 * @throws Exception 
	 */
	private void onThrownException(Exception e)
			throws Exception {
		
		onException(e);
		throw e;
	}
	
	/**
	 * On exception.
	 * @param e
	 */
	private static void onException(Throwable e) {
		
		// Override this method.
		e.printStackTrace();
	}
}
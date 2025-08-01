/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */
package org.maclan.server;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.maclan.Area;
import org.maclan.AreaRelation;
import org.maclan.AreaResource;
import org.maclan.AreaVersion;
import org.maclan.EnumerationObj;
import org.maclan.EnumerationValue;
import org.maclan.Language;
import org.maclan.LoadSlotHint;
import org.maclan.MiddleLight;
import org.maclan.MiddleResult;
import org.maclan.MiddleUtility;
import org.maclan.MimeType;
import org.maclan.Resource;
import org.maclan.Slot;
import org.maclan.SlotHolder;
import org.maclan.SlotType;
import org.maclan.StartResource;
import org.maclan.VersionObj;
import org.maclan.expression.ExpressionSolver;
import org.maclan.expression.ProcedureParameter;
import org.multipage.gui.Utility;
import org.multipage.util.Obj;
import org.multipage.util.Resources;

/**
 * Class for Area Server object.
 * @author vakol
 */
public class AreaServer {
	
	/**
	 * Left and right bracket tags.
	 */
	public static final String leftBracketTag = "@lb;";
	public static final String rightBracketTag = "@rb;";
	public static final String leftHtmlBracketTag = "@l;";
	public static final String rightHtmlBracketTag = "@r;";
	public static final String atTag = "@at;";
	public static final String newLineTag = "@nl;";
	
	/**
	 * Constants.
	 */
	
	/**
	 * Default evaluation of property value.
	 */
	private static final int DEFAULT = 0;
	
	/**
	 * Results of metalanguage expressions can be "null".
	 */
	private static final int NULL = 1;
	
	/**
	 * Metalanguage property can be a flag of boolean type without any present value.
	 */
	private static final int FLAG = 2;
	
	/**
	 * Property must be specified int given tag.
	 */
	private static final int REQUIRED_PROPERTY = 4;
	
	/**
	 * The value of property must be specified.
	 */
	private static final int REQUIRED_VALUE = 8;
	
	/**
	 * Strict processing of tag property. The property must be present and the value cannot be null.
	 */
	private static final int STRICT = REQUIRED_PROPERTY | REQUIRED_VALUE;
	
	/**
	 * Unzipped resources list file name. (The file is stored in servlet temporary
	 * directory.
	 */
	public static final String UNZIPPED_FILENAME = "unzipped.txt";

	/**
     * List of supported text encodings.
     */
	private static final LinkedList<String> SUPPORTED_ENCODINGS = new LinkedList<String>();
	
	/**
	 * Number of JavaScript engines in the pool.
	 */
	private static final int JAVASCRIPT_ENGINE_COUNT = 10;

	/**
	 * Area ID of the page which source code has to be displayed.
	 */
	private static Long showSourceCodeForAreaId;
	
	/**
	 * Synchronized container for future JavaScript engine pool.
	 */
	private static Obj<ArrayList<ScriptingEngine>> javaScriptEnginePool = new Obj<ArrayList<ScriptingEngine>>(null);
	
	/**
	 * Show page source code.
	 * @param areaId 
	 */
	public static void showPageSourceCode(long areaId) {
		
		// ID = 0L is a home page
		showSourceCodeForAreaId = areaId;
	}

	/**
	 * Disables to show source code of the page.
	 */
	public static void dontShowSourceCode() {
		
		showSourceCodeForAreaId = null;
	}
	
	/**
	 * Development mode lambda function.
	 */
	private static Supplier<Boolean> isDevelopmentLambda = null;
	
	/**
	 * Can log lambda function.
	 */
	private static Supplier<Boolean> canLogLambda = null;
	
	/**
	 * Log lambda function.
	 */
	private static Consumer<String> logLambda = null;
	
	/**
	 * Involve user action in the logging process. A lambda function.
	 */
	private static Runnable logInvolveUserLambda = null;
	
	
	/**
	 * Static constructor.
	 */
	static {
		
		// List supported text encodings.
		SUPPORTED_ENCODINGS.add("UTF-8");
		SUPPORTED_ENCODINGS.add("UTF-16");
    }
	
	/**
	 * Set lambda that gets the "is development mode" flag.
	 * @return
	 */
	public static void setDevelopmentModeLambda(Supplier<Boolean> isDevelopmentLambda) {
		
		AreaServer.isDevelopmentLambda = isDevelopmentLambda;
	}
	
	/**
	 * Set "can log" lambda function.
	 * @param logLambda
	 */
	public static void setCanLogLambda(Supplier<Boolean> canLogLambda) {
		
		AreaServer.canLogLambda = canLogLambda;
	}

	/**
	 * Set logging lambda function.
	 * @param logLambda
	 */
	public static void setLogLambda(Consumer<String> logLambda) {
		
		AreaServer.logLambda = logLambda;
	}
	
	/**
	 * Set a lambda function that enables user actions in the logging process.
	 * @param logInvolveUserLambda
	 */
	public static void setLogInvolveUserLambda(Runnable logInvolveUserLambda) {
		
		AreaServer.logInvolveUserLambda = logInvolveUserLambda;
	}
	
	/**
	 * Returns true is the development mode is on.
	 * @return
	 */
	private static boolean isDevelopmentMode() {

		if (isDevelopmentLambda == null) {
			return false;
		}
		boolean isDevelopment = isDevelopmentLambda.get();
		return isDevelopment;
	}
	
	/**
	 * Check if the application can log messages.
	 * @return
	 */
	public static boolean canLog() {
		
		if (logLambda != null && canLogLambda != null) {
			
			boolean canLog =  canLogLambda.get();
			return canLog;
		}
		return false;
	}
	
	/**
	 * Log text.
	 */
	public static void log(String logText) {
		
		if (logLambda != null) {
			logLambda.accept(logText);
		}
	}
	
	/**
	 * Log parametrized text.
	 */
	public static void log(String logText, Object ... textParameters) {
		
		if (logLambda != null) {
			
			if (textParameters.length > 0) {
				logText = String.format(logText, textParameters);
			}
			
			logLambda.accept(logText);
		}
	}
	
	/**
	 * Involve user action in the logging process.
	 */
	public static void logInvolveUser() {
		
		if (logInvolveUserLambda != null) {
			logInvolveUserLambda.run();
		}
	}
	
	/**
	 * Error format for HTML.
	 */
	protected static final String errorFormatHtml = "<font color='red'>#%s:%s#</font>";
	
	/**
	 * Error format.
	 */
	protected static final String errorFormat = "#%s:%s#";
	
	/**
	 * SlotiInheritance level regex.
	 */
	private static final Pattern slotInheritancePattern = Pattern.compile("i([0,9]*)");
	
	/**
	 * Provider watch service.
	 */
	private static ProviderWatchService providerWatchService = new ProviderWatchService();
	
	/**
	 * Set middle layer.
	 */
	public void setMiddle(MiddleLight middle) {
		
		this.state.middle = middle;
	}
	
	/**
	 * Get render directory.
	 * @return
	 */
	protected String getRenderingTarget() {
		
		return state.listener.getRenderingTarget();
	}
	
	/**
	 * Enables/disables rendering of resource files with common file names.
	 * @param flag
	 */
	public void setCommonResourceFileNames(boolean flag) {
		
		this.state.commonResourceFileNames = flag;
	}
	
	/**
	 * Get web interface directory
	 * @return
	 */
	public String getWebInterfaceDirectory() throws Exception {
		
		if (!state.webInterfaceDirectory.ref.isEmpty()) {
			return state.webInterfaceDirectory.ref;
		}
		return MiddleUtility.getWebInterfaceDirectory();
	}

	/**
	 * Gets redirection object.
	 * @return
	 */
	public Redirection getRedirection() {
		
		return state.redirection;
	}
	
	/**
	 * Initialize server state.
	 */
	public void initServerState() {
		
		// Create new server state.
		this.state = new AreaServerState();
	}
	
	/**
	 * Area server state.
	 */
	public AreaServerState state;
	
	/**
	 * Flag indicating that the Area Server is debugged.
	 */
	private boolean enabledDebugger = false;
	
	/**
	 * Set flag indicating that the Area Server is debugged.
	 * @param isDebuggerEnabled
	 */
	public void setDebuggerEnabled(boolean isDebuggerEnabled) {
		
		this.enabledDebugger = isDebuggerEnabled;
	}
	
	/**
	 * Get flag indicating that the Area Server is debugged.
	 * @return
	 */
	public boolean isDebuggerEnabled() {
		
		return enabledDebugger;
	}
	
	/**
	 * Set the "debugger can visit" flag.
	 * @param canVisit
	 */
	public void setDebuggerCanVisit(boolean canVisit) {
		
		DebugInfo debugInfo = state.getDebugInfo();
		if (debugInfo == null) {
			return;
		}
		
		debugInfo.setCanDebug(canVisit);
	}
	
	/**
	 * Tag processors.
	 */
	private static HashMap<String, SimpleSingleTagProcessor> simpleSingleTagProcessors =
		new HashMap<String, SimpleSingleTagProcessor>();
	private static HashMap<String, ComplexSingleTagProcessor> complexSingleTagProcessors =
		new HashMap<String, ComplexSingleTagProcessor>();
	private static HashMap<String, FullTagProcessor> fullTagProcessors =
		new HashMap<String, FullTagProcessor>();


	/**
	 * Initialization of tag processors.
	 */
	public static void initialize() {
		
		// Simple language tags processors.
		simpleLanguageTagsProcessors();
		// Area processor.
		areaProcessor();
		// Resource processor.
		resourceProcessor();
		// Slot processor.
		slotProcessor();
		// Input processor.
		inputProcessor();
		// Output processor
		outputProcessor();
		// Store processor.
		storeProcessor();
		// Image macro processor.
		imageMacroProcessor();
		// Variables processor.
		createVariablesProcessor();
		// Set variables processor.
		setVariablesProcessor();
		// Get variable value processor.
		getVariableValueProcessor();
		// Anchor macro processor.
		anchorMacroProccessor();
		// Sub areas processor.
		subareasProcessor();
		// Super areas processor.
		superareasProcessor();
		// Remarks processor.
		remarksProcessor();
		// Pack processor.
		packProcessor();
		// Block processor.
		blockProcessor();
		// Loop processor.
		loopProcessor();
		// List processor.
		listProcessor();
		// Last processor.
		lastProcessor();
		// Procedure processor.
		procedureProcessor();
		// Language list processor.
		languagelistProcessor();
		// Trace processor.
		traceProcessor();
		// Break processor.
		breakProcessor();
		// PText processor.
		pptextProcessor();
		// Inner processor
		innerProcessor();
		// URL processor.
		urlProcessor();
		// USING processor.
		usingProcessor();
		// RENDER_CLASS processor.
		renderClassProcessor();
		// VERSION processor.
		versionProcessor();
		// VERSION_URL processor.
		versionUrlProcessor();
		// VERSION_NAME processor.
		versionNameProcessor();
		// VERSION_ID processor.
		versionIdProcessor();
		// TIMEOUT processor.
		timeoutProcessor();
		// BOOKMARK processor.
		bookmarkProcessor();
		// REPLACE_BOOKMARK processor.
		replaceBookmarkProcessor();
		// INCLUDE_ONCE processor.
		includeOnceProcessor();
		// LITERAL processor.
		literalProcessor();
		// PRAGMA processor.
		pragmaProcessor();
		// JAVASCRIPT processor.
		javaScriptProcessor();
		// INDENT tag processor.
		indentProcessor();
		// NOINDENT tag processor.
		noindentProcessor();
		// CSS_LOOKUP_TABLE tag processor.
		cssLookupTableProcessor();
		// UNZIP tag processor.
		unzipProcessor();
		// RUN tag processor.
		runProcessor();
		// REDIRECT processor.
		redirectProcessor();
		// TRAY_MENU processor.
		trayMenuProcessor();
		// POST processor
		postProcessor();
	}

	/**
	 * Load area.
	 * @param request2
	 * @param response2 
	 * @return
	 * @throws Exception 
	 */
	public void loadServerAreaData(Request request2, Response response2)
		throws Exception {
	
		MiddleResult result;
		Obj<Area> outputArea = new Obj<Area>();

		// Get area with given ID.
		String areaIdParameter = request2.getParameter("area_id");
		if (areaIdParameter != null) {
	
			try {
				long areaId = Long.parseLong(areaIdParameter);
				result = state.middle.loadArea(areaId, outputArea);
				if (result.isNotOK()) {
					throwErrorText(result.getMessage());
				}
				
				state.requestedArea = outputArea.ref;
				state.area = outputArea.ref;
				return;
			}
			catch (NumberFormatException e) {
			}
		}
		else {
			
			// Get project alias.
			String projectAliasParameter = request2.getParameter("project");
			
			// Get area with given alias.
			String areaAliasParameter = request2.getParameter("alias");
			
			if (areaAliasParameter != null) {
				
				// Get area from alias.
				result = state.middle.loadProjectAreaWithAlias(projectAliasParameter, areaAliasParameter, outputArea);
				if (result.isNotOK()) {
					throwErrorText(result.getMessage());
				}
				
				state.requestedArea = outputArea.ref;
				state.area = outputArea.ref;
				return;
			}	
		}
		
		// If the parameter is missing, get home area.
		result = state.middle.loadHomeAreaData(outputArea);
		if (result.isNotOK()) {
			throwErrorText(result.getMessage());
		}
		
		state.requestedArea = outputArea.ref;
		state.area = outputArea.ref;
	}

	/**
	 * Load home area data.
	 * @param area
	 * @throws Exception
	 */
	public void loadHomeAreaData(Obj<Area> area)
		throws Exception {
		
		// Get home area.
		MiddleResult result = state.middle.loadHomeAreaData(area);
		if (result.isNotOK()) {
			throwErrorText(result.getMessage());
		}
	}

	/**
	 * Load related area data.
	 * @param area
	 */
	public void loadRelatedAreaData(Area area) 
		throws Exception {
		
		// Load related area.
		MiddleResult result = state.middle.loadRelatedAreaData(area);
		if (result.isNotOK()) {
			throwErrorText(result.getMessage());
		}
	}

	/**
	 * Get area ID.
	 * @param middle
	 * @param request2
	 * @return
	 */
	public static long getAreaId(MiddleLight middle, Request request2)
		throws Exception {
		
		MiddleResult result;
		Obj<Area> outputArea = new Obj<Area>();

		// Get area ID parameter.
		String areaIdParameter = request2.getParameter("area_id");
		if (areaIdParameter != null) {
	
			try {
				long areaId = Long.parseLong(areaIdParameter);
				return areaId;
			}
			catch (NumberFormatException e) {
			}
		}
		
		// Get area alias parameter.
		String alias = request2.getParameter("area_alias");
		if (alias != null) {
			
			// Load area ID for given area alias.
			result = middle.loadAreaWithAlias(alias, outputArea);
			if (result.isOK()) {
				return outputArea.ref.getId();
			}
			throw new Exception(String.format(
					Resources.getString("server.messageCannotFindAreaWithAlias"),
					alias));
		}
		
		// If the parameter is missing, get home area ID.
		result = middle.loadHomeAreaData(outputArea);
		if (result.isNotOK()) {
			throwErrorText(result.getMessage());
		}
		
		return outputArea.ref.getId();
	}

	/**
	 * Initialize simple single tag processors.
	 */
	private static void simpleLanguageTagsProcessors() {

		SimpleSingleTagProcessor processor = new SimpleSingleTagProcessor(){
			// Process.
			@Override
			public String processTag(AreaServer server) throws Exception {
				
				// Render flag image.
				if (server.state.renderingFlags != null) {
					
					// Get area assembled path.
					String absolutePath = server.getAreaAssembledPath(server.getRequestedArea().getId(), server.getCurrentVersionId());
					
					RenderedFlag.addToSet(server.state.renderingFlags, server.state.currentLanguage.id, absolutePath);
				}
				
				String format = server.isRendering() ? "<img src='flag%d.png'>" : "<img src='?flag_id=%d'>";
				return String.format(format, server.state.currentLanguage.id);
			}
		};
		
		simpleSingleTagProcessors.put("LANG_FLAG", processor);
		simpleSingleTagProcessors.put("CURRENT_LANG_FLAG", processor);
		
		
		processor = new SimpleSingleTagProcessor() {
			// Process.
			@Override
			public String processTag(AreaServer server) throws Exception {
				return server.state.currentLanguage.description;
			}
		};
		
		simpleSingleTagProcessors.put("LANG_DESCRIPTION", processor);
		simpleSingleTagProcessors.put("CURRENT_LANG_DESCRIPTION", processor);
		
		
		processor = new SimpleSingleTagProcessor() {
			// Process.
			@Override
			public String processTag(AreaServer server) throws Exception {
				return server.state.currentLanguage.alias;
			}
		};
		
		simpleSingleTagProcessors.put("LANG_ALIAS", processor);
		simpleSingleTagProcessors.put("CURRENT_LANG_ALIAS", processor);
		
		
		processor = new SimpleSingleTagProcessor() {
			// Process.
			@Override
			public String processTag(AreaServer server) throws Exception {
				return String.valueOf(server.state.currentLanguage.id);
			}
		};
		
		simpleSingleTagProcessors.put("LANG_ID", processor);
		simpleSingleTagProcessors.put("CURRENT_LANG_ID", processor);
	}
	
	/**
	 * Loop processor.
	 */
	private static void loopProcessor() {
		
		fullTagProcessors.put("LOOP", new FullTagProcessor() {

			@Override
			public String processText(AreaServer server, String innerText, TagProperties properties)
					throws Exception {
				
				server.state.analysis.loop_calls++;
				
				// Get "count" value.
				Long count = server.evaluateProperty(properties, "count", Long.class, null, NULL);
				
				// Initialize empty loop flag.
				boolean emptyLoop = false;
				
				// Do infinite loop if the count value is -1.
				boolean infiniteLoop = count != null && count == -1L;
				
				// Check the "count" property.
				if (count != null && count < -1) {
					throwError("server.messageCountCannotBeNegative");
				}
				
				// Get "from" value.
				long from = server.evaluateProperty(properties, "from", Long.class, 0L, DEFAULT);
				
				// Get "to" property.
				Long to = server.evaluateProperty(properties, "to", Long.class, null, NULL);
				
				// Get "step" property.
				long step = server.evaluateProperty(properties, "step", Long.class, 1L, DEFAULT);
				
				// Check step property.
				if (step == 0L) {
					throwError("server.messageStepValueCannotBeZero");
				}
				
				// If not set, compute "to" value.
				if (to == null && count != null) {
					to = from + step * (count - 1);
				}
				
				// Check "to" value.
				if (to == null) {
					emptyLoop = true;
				}
				
				// Get divider property.
				String divider = server.evaluateProperty(properties, "divider", String.class, "", DEFAULT);
				
				// Get index variable name.
				String indexVariableText = properties.getProperty("index");
				
				// Get break variable name.
				String breakVariableText = properties.getProperty("break");
				
				// Get discard variable name.
				String discardVariableText = properties.getProperty("discard");
				
				// Compiled text.
				StringBuilder compiledText = new StringBuilder();

				long index = from;
				boolean isFirstIteration = true;
				
				// Do loop.
				while (true) {
					
					// Check server response timeout.
					server.checkResponseTimeout();
					
					// Break on empty loop.
					if (emptyLoop) {
						break;
					}
		
					// Check index.
					if (!infiniteLoop) {
						if ((step > 0) && (index > to) || (step < 0) && (index < to)) {
							break;
						}
					}
					
					// Push new block.
					BreakBlockDescriptor block = server.state.blocks.pushNewBreakBlockDescriptor();
					// Add index variable.
					if (indexVariableText != null) {
						block.createBlockVariable(indexVariableText, index);
					}
					
					Variable breakVariable = null;
					Variable discardVariable = null;
					
					// Add break variable.
		 			if (breakVariableText != null) {
						breakVariable = block.createBlockVariable(breakVariableText, false);
						
						// Add discard variable.
			 			if (discardVariableText != null) {
			 				discardVariable = block.createBlockVariable(discardVariableText, false);
			 			}
					}

					// Process inner text.
					String processedInnerText = server.processTextCloned(innerText);
					
					// Get error flag.
					boolean error = server.isThrownException();
					
					// Initialization of break variables.
					boolean breakLoop = false;
					boolean discard = false;
					
					if (breakVariable != null && breakVariable.value instanceof Boolean) {
						breakLoop = (Boolean) breakVariable.value;
					}
					if (!breakLoop) {
						breakLoop = block.isBreaked();
					}
					
					// Get discard flag.
					if (breakLoop) {
						if (discardVariable != null && discardVariable.value instanceof Boolean) {
							discard = (Boolean) discardVariable.value;
						}
						if (!discard) {
							discard = block.getDiscard();
						}
					}
		
					// Pop block.
					boolean transparent = properties.containsKey("transparent");
					server.state.blocks.popBlockDescriptor(transparent);
					
					// When not to discard the result...
					if (!discard || error) {
						
						// Add divider text.
						if (divider != null && !isFirstIteration) {
							compiledText.append(divider);
						}
						
						// Append processed text.
						compiledText.append(processedInnerText);
					}
					
					// Break the loop.
					if (breakLoop || error) {
						break;
					}
					
					// Compute new index value.
					index += step;
					isFirstIteration = false;
				}
				
				// Get replacement string.
				String replacement = compiledText.toString();
				
				// Wrap into meta information.
				return replacement;
			}
		});
	}

	/**
	 * Last processor.
	 */
	private static void lastProcessor() {
		
		complexSingleTagProcessors.put("LAST", new ComplexSingleTagProcessor() {
			// Process the last tag.
			@Override
			public String processTag(AreaServer server, TagProperties properties) throws Exception {
				
				// Get top list descriptor.
				BreakBlockDescriptor listBlock = (BreakBlockDescriptor) server.state.blocks.findFirstDescriptor(
						BreakBlockDescriptor.class);
				// Last list item.
				if (listBlock != null) {
					
					// Get discard parameter.
					boolean discard = server.evaluateProperty(properties, "discard", Boolean.class, false, FLAG);
					
					listBlock.setBreaked(discard);
				}
				
				return "";
			}
		});
	}
	
	/**
	 * Area processor.
	 */
	private static void areaProcessor() {
		
		complexSingleTagProcessors.put("AREA_NAME", new ComplexSingleTagProcessor() {
			// Process.
			@Override
			public String processTag(AreaServer server, TagProperties properties) throws Exception {
				Area area = getAreaFromProperties(server, properties, null);
				String text = area.getDescriptionForced();
				return (server.state.showLocalizedTextIds && area.isLocalized() ? getIdHtml("A", area.getId()) + text : text);
			}
		});
		complexSingleTagProcessors.put("AREA_ALIAS", new ComplexSingleTagProcessor() {
			// Process.
			@Override
			public String processTag(AreaServer server, TagProperties properties) throws Exception {
				Area area = getAreaFromProperties(server, properties, null);
				String text = area.getAlias();
				return text;
			}
		});
		complexSingleTagProcessors.put("AREA_ID", new ComplexSingleTagProcessor() {
			// Process.
			@Override
			public String processTag(AreaServer server, TagProperties properties) throws Exception {
				Area area = getAreaFromProperties(server, properties, null);
				return String.valueOf(area.getId());
			}
		});
	}
	
	/**
	 * Resource processor.
	 */
	private static void resourceProcessor() {
		
		complexSingleTagProcessors.put("RESOURCE_ID", new ComplexSingleTagProcessor() {
			// Process
			@Override
			public String processTag(AreaServer server, TagProperties properties)
					throws Exception {

				Long resourceId = server.getResourceFromProperties(properties).getId();
				return resourceId.toString();
			}
		});
		
		complexSingleTagProcessors.put("RESOURCE_EXT", new ComplexSingleTagProcessor() {
			// Process
			@Override
			public String processTag(AreaServer server, TagProperties properties)
					throws Exception {

				Long mimeId = server.getResourceFromProperties(properties).getMimeTypeId();
				MimeType mime = server.getMimeType(mimeId);
				return mime.extension;
			}
		});
		
		complexSingleTagProcessors.put("RESOURCE_VALUE", new ComplexSingleTagProcessor() {
			// Process
			@Override
			public String processTag(AreaServer server, TagProperties properties)
					throws Exception {
				
				Long resourceId = server.getResourceFromProperties(properties).getId();
				
				// Get coding.
				String encoding = properties.getProperty("coding");
				if (encoding == null) {
					encoding = properties.getProperty("encoding");
				}
				
				
				// Get resource text.
				return server.getResourceContentText(resourceId, encoding);
			}
		});
		
		complexSingleTagProcessors.put("RESOURCE", new ComplexSingleTagProcessor() {
			// Process
			@Override
			public String processTag(AreaServer server, TagProperties properties)
					throws Exception {
				
				Long resourceId = server.getResourceFromProperties(properties).getId();
				
				// Get coding.
				String coding = properties.getProperty("coding");
				
				String codingProperty = "";
				if (coding != null) {
					codingProperty = String.format(", coding=\"%s\"", coding);
				}
				
				// Get resource text.
				return String.format("[@@RESOURCE resId=%d%s]", resourceId, codingProperty);
			}
		});
	}

	/**
	 * Get resource from properties.
	 * @param properties
	 * @throws Exception
	 */
	private Resource getResourceFromProperties(TagProperties properties)
			throws Exception {
		
		// Get resource alias.
		String resourceAlias = evaluateProperty(properties, "res", String.class, null, DEFAULT);
	
		// Get area.
		Area area = getAreaFromProperties(this, properties, null);
		
		// Get resource.
		AreaResource resource = resource(resourceAlias, area);
		if (resource == null) {
			AreaServer.throwError("server.messageResourceOfAreaNotFound",
					resourceAlias, area.getDescriptionForced());
		}
		
		// If there is an use parameter.
		boolean render = evaluateProperty(properties, "render", Boolean.class, false, FLAG);
		if (state.renderingResources != null && render) {
			
			setRenderingResourceExt(resource);
		}
		
		return resource;
	}
	
	/**
	 * Get area from properties.
	 * @param server
	 * @param properties
	 * @param existsAreaSpecification 
	 * @return
	 */
	protected static Area getAreaFromProperties(AreaServer server,
			TagProperties properties, Obj<Boolean> existsAreaSpecification) throws Exception {
		
		return getAreaFromProperties(server, properties, "", existsAreaSpecification);
	}
	
	/**
	 * Area properties array.
	 */
	private static final String [] areaPropertiesArray = {
		"areaId", "areaAlias", "projectAlias", "area", "startArea", "homeArea", "requestedArea", "thisArea", "versionId", "versionId"};
	
	/**
	 * Get area from properties.
	 * @param server
	 * @param properties
	 * @param propertyPrefix - text that precedes property name
	 * @param existsAreaSpecification 
	 * @return
	 */
	protected static Area getAreaFromProperties(AreaServer server,
			TagProperties properties, String propertyPrefix, Obj<Boolean> existsAreaSpecification)
					throws Exception {
		
		// Reset flag.
		if (existsAreaSpecification != null) {
			existsAreaSpecification.ref = true;
		}
		
		Long versionId = null;
		MiddleResult result;
		
		// Get area ID.
		Long areaId = server.evaluateProperty(properties, propertyPrefix + "areaId", Long.class, null, NULL);		
		if (areaId == null) {

			// Evaluate alias text.
			String alias = server.evaluateProperty(properties, propertyPrefix + "areaAlias", String.class, null, NULL);
			if (alias != null) {
				
				// Get project alias and evaluate it.
				String projectAlias = server.evaluateProperty(properties, propertyPrefix + "projectAlias", String.class, null, NULL);
				 
				// Try to get area.
				Obj<Area> outputArea = new Obj<Area>();
				result = server.state.middle.loadProjectAreaWithAlias(projectAlias, alias, outputArea);
				if (result.isNotOK()) {
					AreaServer.throwError("server.messageAreaAliasNotFound", alias);
				}
				return outputArea.ref;
			}
		}
		
		if (areaId == null) {
			
			// Get area ID.
			Area outputArea = server.evaluateProperty(properties, propertyPrefix + "area", Area.class, null, NULL);
			if (outputArea != null) {
				areaId = outputArea.getId();
			}
		}
		if (areaId == null) {
			boolean startArea = server.evaluateProperty(properties, propertyPrefix + "startArea", Boolean.class, false, FLAG);
			if (startArea) {
				areaId = server.state.startArea.getId();
			}
		}
		if (areaId == null) {
			boolean homeArea = server.evaluateProperty(properties, propertyPrefix + "homeArea", Boolean.class, false, FLAG);
			if (homeArea) {
				Obj<Area> area = new Obj<Area>();
				server.loadHomeAreaData(area);
				areaId = area.ref.getId();
			}
		}
		if (areaId == null) {
			boolean requestedArea = server.evaluateProperty(properties, propertyPrefix + "requestedArea", Boolean.class, false, FLAG);
			if (requestedArea) {
				areaId = server.state.requestedArea.getId();
			}
		}
		if (areaId == null) {
			boolean thisArea = server.evaluateProperty(properties, propertyPrefix + "thisArea", Boolean.class, false, FLAG);
			if (thisArea) {
				areaId = server.state.area.getId();
			}
		}
		if (areaId == null) {
			String slotName = server.evaluateProperty(properties, propertyPrefix + "areaSlot", String.class, null, NULL);
			if (slotName != null) {
				
				// Load slot value.
				Object value = server.slotValue(slotName, false, false, 0L);
				if (value instanceof Area) {
					
					Area area = (Area) value;
					areaId = area.getId();
				}
				else {
					// Throw exception.
					throwError("server.messageAreaSlotBadValue", slotName);
				}
			}
		}
		
		if (areaId == null) {
			// Set flag.
			if (existsAreaSpecification != null) {
				existsAreaSpecification.ref = false;
			}
			return server.state.area;
		}
		
		// Try to get version ID from version alias.
		String versionAlias = server.evaluateProperty(properties, propertyPrefix + "versionAlias", String.class, null, DEFAULT);
		if (versionAlias != null) {
			
			VersionObj version = server.getVersion(versionAlias);
			versionId = version.getId();
		}
		
		// Get version ID.
		if (versionId == null) {
			versionId = server.evaluateProperty(properties, propertyPrefix + "versionId", Long.class, 0L, DEFAULT);
		}
			
		// Load area of given version.
		Obj<Area> area = new Obj<Area>();
		result = server.state.middle.loadArea(areaId, versionId, area);
		if (result.isNotOK()) {
			AreaServer.throwError("server.messageAreaWithIdNotFound", areaId);
		}
		
		return area.ref;
	}
	
	/**
	 * Get slot from properties.
	 * @param server
	 * @param properties
	 * @return
	 * @throws Exception
	 */
	private static Slot getSlotFromProperties(AreaServer server, TagProperties properties)
			throws Exception {
		
		// Get slot area.
		Obj<Boolean> existsAreaSpecification = new Obj<Boolean>();
		Area slotArea = getAreaFromProperties(server, properties, existsAreaSpecification);

		// Get slot expression.
		String slotExpression = properties.getProperty("use");
		if (slotExpression == null) {
			slotExpression = properties.getProperty("slot");
		}

		if (slotExpression == null && !existsAreaSpecification.ref) {
			try {
				// Get first property name.
				slotExpression = (String) properties.keys().nextElement();
			}
			catch (NoSuchElementException e) {
			}
		}
								
		// Evaluate slot alias.
		Object slotRef = server.evaluateText(slotExpression, Object.class, false);
		
		// If it references a slot object, return it.
		if (slotRef instanceof Slot) {
			
			Slot resultSlot = (Slot) slotRef;
			return resultSlot;
		}
		// Load slot value.
		else if (slotRef instanceof String) {
			
			String alias = (String) slotRef;
			if (!alias.isEmpty()) {
				
				// Get "skip_default" modifier.
				boolean skipDefault = properties.getProperty("skipDefault") != null;
				// Parent flag.
				boolean parent = properties.containsKey("parent");
				
				if (slotArea == null) {
					slotArea = server.state.area;
				}
	
				Obj<Slot> slot = new Obj<Slot>();
				
				MiddleResult result = server.state.middle.loadSlot(slotArea, alias,
						true, parent, skipDefault, slot, true);
				if (result.isNotOK()) {
					throwError("server.messageDatabaseError", result.getMessage());
				}
	
				// If slot not found.
				if (slot.ref == null) {
					throwError("server.messageSlotNotFoundOrNotInheritable", slotRef);
				}
				
				return slot.ref;
			}
		}
		
		throwError("server.messageMissingSlotNameInTag", slotRef);
		return null;
	}

	/**
	 * Slot processor.
	 */
	private static void slotProcessor() {

		complexSingleTagProcessors.put("TAG", new ComplexSingleTagProcessor() {
			@Override
			public String processTag(AreaServer server, TagProperties properties) throws Exception {

				server.state.analysis.tag_calls++;
				
				// Get slot area.
				Obj<Boolean> existsAreaSpecification = new Obj<Boolean>();
				Area slotArea = getAreaFromProperties(server, properties, existsAreaSpecification);

				// Get slot name.
				String slotAlias = null;
				boolean slotExists = properties.containsKey("slot");
				
				if (slotExists) {
					slotAlias = properties.getProperty("slot");
				}
				
				if (!slotExists && !existsAreaSpecification.ref) {
					try {
						// Get the first property. It determines the name of required slot.
						slotAlias = (String) properties.keys().nextElement();
					}
					catch (NoSuchElementException e) {
						// Missing slot name specification.
						Utility.throwException("org.maclan.server.messageMissingSlot");
					}
				}
				else {				
					// Evaluate slot alias.
					slotAlias = server.evaluateExpression(slotAlias, String.class, null, NULL);
				}

				// Get slot value.
				if (slotAlias != null && !slotAlias.isEmpty()) {

					// Get "local" modifier.
					boolean local = server.evaluateProperty(properties, "local", Boolean.class, false, FLAG);
					// Get "skip_default" modifier.
					boolean skipDefault = server.evaluateProperty(properties, "skipDefault", Boolean.class, false, FLAG);
					// Get "parent" modifier.
					boolean parent = server.evaluateProperty(properties, "parent", Boolean.class, false, FLAG);
					// Get "enableSpecialValue" modifier.
					boolean enableSpecialValue = server.evaluateProperty(properties, "enableSpecialValue", Boolean.class, false, FLAG);
					
					// Set "this" area.
					if (slotArea == null) {
						slotArea = server.state.area;
					}

					// Load slot from database.
					Obj<Slot> slot = new Obj<Slot>();
					
					MiddleResult result = server.state.middle.loadSlot(slotArea, slotAlias,
							true, parent, skipDefault, slot, true);
					if (result.isNotOK()) {
						throwError("server.messageDatabaseError", result.getMessage());
					}

					// If slot is not found, throw exception.
					if (slot.ref == null) {
						throwError("server.messageSlotNotFoundOrNotInheritable", slotAlias);
					}
					
					// Set debugger info.
					if (server.isDebuggerEnabled()) {
						
						long slotId = slot.ref.getId();
						String slotName = slot.ref.getAlias();
						
						SlotHolder slotHolder = slot.ref.getHolder();
						Long areaId = slotHolder.getId();
						String areaName = slotHolder.getDescriptionForced();
						
						DebugSourceInfo sourceInfo = DebugSourceInfo.newSlot(slotId, slotName, areaId, areaName);
						DebugInfo.setDebugInfo(server, sourceInfo);
					}
					
					// Create text representation of slot ID. It starts with an "S" letter (which means slot).
					String textId = server.state.showLocalizedTextIds && slot.ref.isLocalized() ? getIdHtml("S", slot.ref.getId()) : "";
					
					// Initialize text value.
					String slotTextValue = null;
					
					// Try to set special value.
					if (enableSpecialValue) {
						String specialValue = slot.ref.getSpecialValueNull();
						
						if (specialValue != null) {
							slotTextValue = specialValue;
						}
					}
					if (slotTextValue == null) {
						// Get the text value.
						slotTextValue = slot.ref.getTextValue();
					}

					// Returned text value.
					String returnedText = null;
					if (local) {
						
						// Process the text with cloned area server.
						returnedText =  textId + server.processTextCloned((Area) slot.ref.getHolder(), slotTextValue);
					}
					else {
						returnedText =  textId + slotTextValue;
					}
					
					return returnedText;
				}
				else {
					throwError("server.messageMissingSlotNameInTag");
				}
				return "";
			}
		});
	}

	/**
	 * INPUT processor.
	 */
	private static void inputProcessor() {
		
		complexSingleTagProcessors.put("INPUT", new ComplexSingleTagProcessor() {
			
			@Override
			public String processTag(AreaServer server, TagProperties properties)
					throws Exception {
				
				// Get slot properties.
				Slot slot = getSlotFromProperties(server, properties);
				
				// Try to load value from the external provider
				Object value = server.evaluateProperty(properties, "value", Object.class, null, NULL);
				if (value != null) {
					slot.setValue(value);
				}

				// Input slot.
				server.input(slot);
				
				// Empty text output.
				return "";
			}
		});
	}
	
	/**
	 * OUTPUT processor.
	 */
	private static void outputProcessor() {
		
		fullTagProcessors.put("OUTPUT", new FullTagProcessor() {

			@Override
			public String processText(AreaServer server, String innerText, TagProperties properties)
					throws Exception {
				
				// Load slot from properties.
				Slot slot = getSlotFromProperties(server, properties);
				
				// Get link for slot output.
				String link = slot.getExternalProvider();
				boolean writesOutput = slot.getWritesOutput();
				if (link != null && writesOutput) {
					
					long slotId = slot.getId();
					
					// Get write lock.
					Obj<Boolean> locked = new Obj<Boolean>();
					MiddleResult result = server.state.middle.loadSlotOutputLock(slotId, locked);
					if (result.isOK() && !locked.ref) {
						
						// Create output block.
						server.state.blocks.pushNewBlockDescriptor();
						
						// Process inner text
						String outputText = server.processTextCloned(server.state.area, innerText);
						
						// If the link exists, write output text to external provider.
						result = MiddleUtility.saveValueToExternalProvider(server.state.middle, link, outputText);
						
						// Remember sent text.
						if (result.isOK()) {
							result = server.state.middle.updateSlotOutputText(slotId, outputText);
						}
						
						// Release output block.
						server.state.blocks.popBlockDescriptor(false);
					}
					
					// Handle error.
					if (result.isNotOK()) {
						throwError("server.messageErrorOutputToExternalProvider", result.getMessage());
					}
				}
				
				// Empty text output.
				return "";
			}
		});
	}
	
	/**
	 * STORE processor.
	 */
	static int i = 1;
	private static void storeProcessor() {
		
		complexSingleTagProcessors.put("STORE", new ComplexSingleTagProcessor() {
			
			@Override
			public String processTag(AreaServer server, TagProperties properties)
					throws Exception {
				
				// TODO: <---FINISH Get area from properties.
				/*Obj<Boolean> existsAreaSpecification = new Obj<Boolean>(false);
				Area area = getAreaFromProperties(server, properties, existsAreaSpecification);
				
				if (existsAreaSpecification.ref) {
					
					// Try to get "areaAlias" property.
					String areaAlias = server.evaluateProperty(properties, "newAlias", String.class, "", NULL);
					area.setAlias(areaAlias);
					
					// Update area alias.
					server.areaStore(area);
					
					return "";
				}*/
				
				// Get slot from properties.
				Slot slot = getSlotFromProperties(server, properties);
				
				// Try to get slot type.
				String typeText = server.evaluateProperty(properties, "type", String.class, null, NULL);
				SlotType type =  SlotType.parseType(typeText);
				slot.setValueMeaning(type);
				
				// Try to get slot value.
				Object value = null;
				
				String valueText = server.evaluateProperty(properties, "value", String.class, null, NULL);
				if (valueText != null) {
					
					Obj<Exception> exception = new Obj<Exception>();
					value = type.parseValue(valueText,
							// Load enumeration.
							() -> {
								try {
									EnumerationObj enumeration = server.getEnumeration(typeText);
									return enumeration;
								}
								catch (Exception e) {
									exception.ref = e;
									return null;
								}
							},
							// Load area reference.
							() -> {
								try {
									Area areaRef = server.getArea(typeText);
									return areaRef;
								}
								catch (Exception e) {
									exception.ref = e;
									return null;
								}
							});
					
					slot.setValue(value);
				}
				else {
					value = slot.getValue();
				}
					
				// Try to get text value target language and set the "localized" flag.
				String language = server.evaluateProperty(properties, "lang", String.class, null, NULL);
				boolean isLocalized = SlotType.LOCALIZED_TEXT.equals(type) || (value instanceof String) && (language != null);
				slot.setLocalized(isLocalized);
				
				// Try to get value meaning.
				String valueMeaning = server.evaluateProperty(properties, "valueMeaning", String.class, null, NULL);
				slot.setValueMeaning(valueMeaning);
				
				// Try to get special value.
				String specialValue = server.evaluateProperty(properties, "specialValue", String.class, null, NULL);
				slot.setSpecialValue(specialValue);
				
				// Try to get slot access.
				Character access = null;
				String accessText = server.evaluateProperty(properties, "access", String.class, null, NULL);
				if (accessText != null && !accessText.isEmpty()) {
					access = accessText.charAt(0);
				}
				slot.setAccess(access);
				
				// Try to get external provider connection string.
				String externalProvider = server.evaluateProperty(properties, "external", String.class, null, NULL);
				slot.setExternalProvider(externalProvider);
				
				// Try to get other flags.
				Boolean isDefault = server.evaluateProperty(properties, "isDefault", Boolean.class, true, NULL | FLAG);
				slot.setDefault(isDefault);
				Boolean readsInput = server.evaluateProperty(properties, "readsExternal", Boolean.class, true, NULL | FLAG);
				slot.setReadsInput(readsInput);
				Boolean writesOutput = server.evaluateProperty(properties, "writesExternal", Boolean.class, true, NULL | FLAG);
				slot.setWritesOutput(writesOutput);
				
				// TODO: <---MAKE Implement following STORE tag properties.
				/*
				Boolean lockInput = server.evaluateProperty(properties, "lockInput", Boolean.class, true, NULL | FLAG);
				Boolean expose = server.evaluateProperty(properties, "expose", Boolean.class, true, NULL | FLAG);
				Boolean revision = server.evaluateProperty(properties, "revision", Boolean.class, true, NULL | FLAG);
				*/
				
				// Store new slot value and settings.
				server.slotStore(slot);
				
				return "";
			}
		});
	}

	/**
	 * Get localized text ID html text.
	 * @param startText 
	 * @param id
	 * @return
	 */
	public static String getIdHtml(String startText, long id) {
		
		return String.format("<font color=red>[%s%d]</font>", startText, id);
	}

	/**
	 * Image macro processor.
	 */
	private static void imageMacroProcessor() {

		complexSingleTagProcessors.put("IMAGE", new ComplexSingleTagProcessor() {
			@Override
			public String processTag(AreaServer server, TagProperties properties)
					throws Exception {
				
				server.state.analysis.image_calls++;
				
				// Get extra properties that are not processed but simply inserted into the result.
				String customPropertiesText = getExtraProperties(properties, areaPropertiesArray, "res");
				
				// Get resource name.
				String resourceName = server.evaluateProperty(properties, "res", String.class, null, NULL);
				if (resourceName == null) {
					throwError("server.messageMissingResourceProperty");
				}
				
				// Get area from properties.
				Area area = getAreaFromProperties(server, properties, null);
				long areaId = area.getId();
				
				// Find resource ID.
				AreaResource resource = server.resource(resourceName, areaId);
				long resourceId = resource.getId();

				if (server.isRendering()) {
					String mimeExtension = server.getResourceMimeExt(resourceId);
					
					// Set rendering resource.
					server.setRenderingResourceExt(resource);

					if (customPropertiesText.isEmpty()) {
						
						if (server.state.commonResourceFileNames) {
							return String.format("<img src=\"%s\">", resource.getDescription());
						}
						else if (mimeExtension != null && !mimeExtension.isEmpty()) {
							return String.format("<img src=\"res%d.%s\">", resourceId, mimeExtension);
						}
						else {
							return String.format("<img src=\"res%d\">", resourceId);
						}
					}
					else {
						
						if (server.state.commonResourceFileNames) {
							return String.format("<img src=\"%s\"%s>", resource.getDescription(),
								customPropertiesText);
						}
						else if (mimeExtension != null && !mimeExtension.isEmpty()) {
							return String.format("<img src=\"res%d.%s\"%s>", resourceId, mimeExtension,
								customPropertiesText);
						}
						else {
							return String.format("<img src=\"res%d\"%s>", resourceId,
								customPropertiesText);
						}
					}
				}
				else {
					if (customPropertiesText.isEmpty()) {
						return String.format("<img src=\"?res_id=%d\">", resourceId);
					}
					else {
						return String.format("<img src=\"?res_id=%d\"%s>", resourceId,
							customPropertiesText);
					}
				}
			}
		});
	}

	/**
	 * Get resource MIME extension.
	 * @param resourceId
	 * @return
	 * @throws Exception
	 */
	protected String getResourceMimeExt(long resourceId)
			throws Exception {
		
		Obj<String> mimeExtension = new Obj<String>();
		
		MiddleResult result = state.middle.loadResourceMimeExt(resourceId, mimeExtension);
		if (result.isNotOK() && result != MiddleResult.NO_RECORD) {
			throwError("server.messageGetResourceMimeExtError", result.getMessage());
		}
		
		return mimeExtension.ref;
	}

	/**
	 * Variables processor.
	 */
	private static void createVariablesProcessor() {

		complexSingleTagProcessors.put("VAR", new ComplexSingleTagProcessor() {
			@Override
			public String processTag(AreaServer server, TagProperties properties)
					throws Exception {
				
				server.state.analysis.var_calls++;
				
				// Get global variable flag.
				boolean isGlobalVariable = server.evaluateProperty(properties, "$global", Boolean.class, false, FLAG);
				
				// Get the number of super block in which the variable will be created.
				long superBlockNumber = server.evaluateProperty(properties, "$super", Long.class, 0L, DEFAULT);
				
				// Do loop for all properties.
				for (Object key : properties.keySet()) {
					
					Object value = properties.get(key);
					if (key instanceof String && value instanceof String) {
						
						// Skip global flag
						if (key.equals("$global") || key.equals("$super")) {
							continue;
						}
						
						String name = (String) key;
						String expressionText = (String) value;
						
						// Evaluate expression.
						try {
							Object resultObject = null;
							
							if (expressionText.length() != 0) {
								
								resultObject = ExpressionSolver.evaluate(expressionText,
										new AreaIdentifierSolver(server),
										new AreaFunctionSolver(server));
							}
							
							// Create variable (local or global).
							try {
								if (isGlobalVariable) {
									server.state.blocks.createGlobalVariable(name, resultObject);
								}
								else {
									if (superBlockNumber == 0) {
										server.state.blocks.createVariable(name, resultObject);
									}
									else {
										server.state.blocks.createVariable(name, resultObject, superBlockNumber);
									}
								}
							}
							catch (Exception e) {
								throwError("server.messageCreateVariableError", e.getMessage());
							}
						}
						catch (Throwable e) {
							throwError("server.messageCreateVariableExpressionError", e.getLocalizedMessage());
						}
					}
				}

				return "";
			}
		});
	}

	/**
	 * Set variables processor.
	 */
	private static void setVariablesProcessor() {

		complexSingleTagProcessors.put("SET", new ComplexSingleTagProcessor() {
			@Override
			public String processTag(AreaServer server, TagProperties properties)
					throws Exception {
				
				server.state.analysis.set_calls++;
				
				// Do loop for all properties.
				for (Object key : properties.keySet()) {
					
					Object value = properties.get(key);
					if (key instanceof String && value instanceof String) {
						
						String name = (String) key;
						String expressionText = (String) value;

						// Evaluate expression.
						try {
							Object resultObject = server.evaluateText(expressionText, null, true);
							
							// Set variable, if it is not unknown.
							if (!name.equals("unknown")) {
								try {
									server.state.blocks.setVariable(name, resultObject);
								}
								catch (Exception e) {
									throwError("server.messageCannotSetVariable", name,
											e.getMessage());
								}
							}
						}
						catch (Throwable e) {
							if (name.equals("unknown")) {
								throwError("server.messageCannotEvaluateExpression",
										e.getLocalizedMessage());
							}
							else {
								throwError("server.messageCannotSetVariable", name,
										e.getLocalizedMessage());
							}
						}
					}
				}

				return "";
			}
		});
	}

	/**
	 * Get variable value.
	 */
	private static void getVariableValueProcessor() {
		
		complexSingleTagProcessors.put("GET", new ComplexSingleTagProcessor() {
			@Override
			public String processTag(AreaServer server, TagProperties properties)
					throws Exception {
				
				server.state.analysis.get_calls++;
				
				if (properties.size() == 1) {
					// Get "exp" property.
					String expText = properties.getProperty("exp");

					// Get expression.
					String expression;
					if (expText != null) {
						expression = expText;
					}
					else {
						expression = properties.keys().nextElement().toString();
					}
					
					Object value = server.evaluateText(expression, null, true);
					if (value == null) {
						return "null";
					}
					
					if (value instanceof Slot) {
						
						Slot slot = (Slot) value;
						server.loadSlotValue(slot);
					}
					return value.toString();
				}
				
				throwError("server.messageExpectingSingleVariableName");
				return "";
			}
		});
	}

	/**
	 * Anchor macro processor.
	 */
	private static void anchorMacroProccessor() {

		FullTagProcessor processor = new FullTagProcessor() {
			@Override
			public String processText(AreaServer server, String innerText,
					TagProperties properties) throws Exception {
				
				server.state.analysis.anchor_calls++;
				
				// Get URL string.
				String href = properties.getProperty("href");
				
				// Evaluate the URL string.
				href = server.evaluateText(href, String.class, true);
				
				// Get extra properties.
				String extraProperties = getExtraProperties(properties,
						areaPropertiesArray,
						"href",
						"res",
						"file",
						"download");

				// If the href exists, use it.
				if (href != null) {
					return String.format("<a href=\"%s\"%s>%s</a>", href, extraProperties, innerText);
				}

				long currentLangId = server.state.middle.getCurrentLanguageId();
				
				Obj<Boolean> existsAreaSpecification = new Obj<Boolean>();
				Area area = getAreaFromProperties(server, properties, existsAreaSpecification);
				
				long versionId = area.getVersionId();
				long areaId = area.getId();
				
				if (existsAreaSpecification.ref) {
					
					// Get resource name.
					String resourceName = properties.getProperty("res");
					
					// If resource name exists.
					if (resourceName != null) {
						
						// Evaluate resource name.
						resourceName = server.evaluateText(resourceName, String.class, false);
						
						// Get file name.
						String fileName = properties.getProperty("file");
						
						// Evaluate file name.
						if (fileName != null) {
							fileName = server.evaluateText(fileName, String.class, false);
						}
						
						// Get "download" flag.
						boolean isDownload = properties.containsKey("download");

						// Return resource URL.
						String resourceUrl = server.getResourceUrl(areaId, resourceName, fileName, isDownload);
						String anchor = String.format("<a href=\"%s\"%s>%s</a>", resourceUrl, extraProperties, innerText);
						
						return anchor;
					}
					else {
						// Get area URL.
						String areaUrl = server.getAreaUrl(areaId, currentLangId, versionId, null, null);
						String anchor = String.format("<a href=\"%s\"%s>%s</a>", areaUrl, extraProperties, innerText);
						
						return anchor;
					}
				}
				
				return String.format("<a %s>%s</a>", extraProperties, innerText);
			}
		};
		
		fullTagProcessors.put("A", processor);
		fullTagProcessors.put("ANCHOR", processor);
	}

	/**
	 * Get version object.
	 * @param versionId
	 * @return
	 * @throws Exception
	 */
	protected VersionObj getVersion(long versionId) throws Exception {
		
		Obj<VersionObj> version = new Obj<VersionObj>();
		
		// Load version object from cache or database table.
		MiddleResult result = state.middle.loadVersion(versionId, version);
		if (result.isNotOK()) {
			throwError("server.messageCannotGetVersionWithGivenId", versionId, result.getMessage());
		}
		
		return version.ref;
	}

	/**
	 * Get version object.
	 * @param versionAlias
	 * @return
	 */
	protected VersionObj getVersion(String versionAlias) throws Exception {
		
		MiddleResult result = MiddleResult.UNKNOWN_ERROR;
		
		Obj<VersionObj> version = new Obj<VersionObj>();
		boolean isEmptyVersion = versionAlias == null || versionAlias.isEmpty();
		
		// Load version object from cache or database table.
		if (!isEmptyVersion) {
			
			result = state.middle.loadVersion(versionAlias, version);
			if (result.isNotOK()) {
				throwError("server.messageCannotGetVersionWithGivenAlias", versionAlias, result.getMessage());
			}
		}
		else {	
			result = state.middle.loadVersion(0L, version);
			if (result.isNotOK()) {
				throwError("server.messageCannotGetVersionWithGivenAlias", versionAlias, result.getMessage());
			}
		}
		
		return version.ref;
	}
	
	/**
	 * Get current version object.
	 * @return
	 * @throws Exception
	 */
	public VersionObj geCurrentVersion() throws Exception {
		
		long currentVersionId = getCurrentVersionId();
		
        VersionObj currentVersion = getVersion(currentVersionId);
        return currentVersion;
	}
	
	/**
	 * Get current server level.
	 * @return
	 */
	public long getServerLevel() {
		
		return state.level;
	}
	
	/**
	 * Get code source information.
	 * @param stateHashCode 
	 * @return
	 */
	public DebugSourceInfo getSourceInfo(int stateHashCode) {
		
		// Find Area Server state with given hash code.
		AreaServerState foundState = findState(stateHashCode);
		if (foundState == null) {
			return null;
		}
		
		// Get debug info.
		DebugInfo debugInfo = foundState.debugInfo;
        if (debugInfo == null) {
            return null;
        }
        
        // Get source info.
        DebugSourceInfo sourceInfo = debugInfo.getSourceInfo();
		return sourceInfo;
	}
	
	/**
	 * Get language.
	 * @param languageId
	 * @return
	 */
	protected Language getLanguage(long languageId) throws Exception {
		
		for (Language language : state.languages) {
			if (language.id == languageId) {
				return language;
			}
		}
		
		throwError("server.messageLanguageIdNotFound", languageId);
		return null;
	}
	
	/**
	 * Get language from alias.
	 * @param languageAlias
	 * @return
	 * @throws Exception
	 */
	protected Language getLanguage(String languageAlias) throws Exception {
		
		for (Language language : state.languages) {
			if (language.alias.equals(languageAlias)) {
				return language;
			}
		}
		
		throwError("server.messageLanguageAliasNotFound", languageAlias);
		return null;
	}

	/**
	 * URL processor.
	 */
	private static void urlProcessor() {

		complexSingleTagProcessors.put("URL", new ComplexSingleTagProcessor() {
			@Override
			public String processTag(AreaServer server, TagProperties properties)
					throws Exception {
				
				// Get area ID.
				Area area = getAreaFromProperties(server, properties, null);
				long areaId = area.getId();
				
				// Get resource ID.
				String resourceName = server.evaluateProperty(properties, "res", String.class, null, NULL);
				if (resourceName != null) {
					
					// Get file name.
					String fileName = server.evaluateProperty(properties, "file", String.class, null, REQUIRED_VALUE);
					
					// Get "download" flag.
					boolean isDownload = server.evaluateProperty(properties, "download", Boolean.class, false, FLAG);

					// Return resource URL.
					return server.getResourceUrl(areaId, resourceName, fileName, isDownload);
				}
				
				// Get language ID and evaluate it.
				Long languageId = server.evaluateProperty(properties, "langId", Long.class, null, NULL);
				if (languageId == null) {
					
					// Try to get ID from a language alias.
					String languageAlias = server.evaluateProperty(properties, "langAlias", String.class, null, NULL);
					if (languageAlias != null) {
						
						Language language = server.getLanguage(languageAlias);
						languageId = language.id;
					}
				}
				
				if (languageId == null) {
					languageId = server.state.middle.getCurrentLanguageId();
				}
				
				// Get version ID.
				Long versionId = null;

				String versionAlias = server.evaluateProperty(properties, "versionAlias", String.class, null, NULL);
				if (versionAlias != null) {
					
					VersionObj version = server.getVersion(versionAlias);
					versionId = version.getId();
				}
				else {
                    versionId = area.getVersionId();
                }
				
				// Get other properties.
				TagProperties otherProperties = excludeProperties(properties, areaPropertiesArray, "res", "file", "download", "langId", "langAlias", "versionAlias", "localhost");
				boolean useLocalhost = !server.isRendering() && properties.containsKey("localhost");
				
				// Get area URL.
				String areaUrl = server.getAreaUrl(areaId, languageId, versionId, useLocalhost, otherProperties);
				
				// Return area URL.
				return areaUrl;
			}
		});
	}
	
	/**
	 * Exclude properties.
	 * @param properties
	 * @param objects
	 * @return
	 */
	protected static TagProperties excludeProperties(TagProperties properties, Object ... objects) {
		
		// Add reserved properties.
		HashSet<String> reservedProperties = new HashSet<String>();
		for (Object object : objects) {
			
			if (object instanceof String) {
				reservedProperties.add((String) object);
			}
			else if (object instanceof String []) {
				
				for (String name : (String []) object) {
					reservedProperties.add(name);
				}
			}
		}
		
		TagProperties extraProperties = new TagProperties();
		for (Object key : properties.keySet()) {
			
			// If it is reserved property, continue the loop.
			if (reservedProperties.contains(key)) {
				continue;
			}
			// Add extra property.
			extraProperties.put(key, properties.get(key));
		}
		
		return extraProperties;
	}

	/**
	 * Get resource URL.
	 * @param areaProperty
	 * @param resourceName
	 * @param fileName
	 * @param useOriginalFileName
	 * @return
	 * @throws Exception 
	 */
	public String getResourceUrl(long areaId, String resourceName, String fileName,
			boolean useOriginalFileName) throws Exception {
		
		AreaResource resource = resource(resourceName, areaId);
		long resourceId = resource.getId();
		
		// Use original file name.
		if (useOriginalFileName) {
			fileName = resource.getDescription();
		}

		if (fileName == null) {

			// Insert URL of given resource.
			if (isRendering()) {
				
				String relativePath = setRenderingResourceExt(resource);
				
				if (state.commonResourceFileNames) {
					
					fileName = resource.getDescription();
					return relativePath + '/' + fileName;
				}
				
				String mimeExtension = getResourceMimeExt(resourceId); 
				
				if (mimeExtension != null) {
					return String.format("res%d.%s", resourceId, mimeExtension);
				}
				else {
					return String.format("res%d", resourceId);
				}
			}
			else {
				return String.format("?res_id=%d", resourceId);
			}
		}
		else {
			if (isRendering()) {
				
				String relativePath = setRenderingResourceFile(resource, fileName);
				return relativePath + '/' + fileName;
			}
			else {
				return String.format("?res_id=%d&filename=%s", resourceId, fileName);
			}
		}
	}
	
	/**
	 * Get area URL.
	 * @param areaId
	 * @param languageId
	 * @param versionId
	 * @param localhost
	 * @param otherProperties
	 * @return
	 */
	public String getAreaUrl(long areaId, Long languageId, Long versionId, Boolean localhost, TagProperties otherProperties)
			throws Exception {
		
		if (languageId == null) {
			languageId = 0L;
		}
		
		if (versionId == null) {
			versionId = 0L;
		}

		if (isRendering()) {
			
			// Remember related area version.
			Area area = getArea(areaId);
			if (area != null && state.relatedAreaVersions != null) {
				
				VersionObj version = getVersion(versionId);
				
				state.relatedAreaVersions.add(new AreaVersion(area, version));
			}
			
			// Return rendered area URL.
			return getRenderedAreaUrl(areaId, languageId, versionId, otherProperties);
		}
		else {
			
			// Use possible server URL.
			String serverUrl = "";
			if (localhost != null && localhost) {
				serverUrl = state.request.getServerUrl();
			}
			
			// Extra properties.
			String extraProperties = MiddleUtility.chainUrlProperties(otherProperties);
			if (!extraProperties.isEmpty()) {
				extraProperties = '&'+ extraProperties;
			}
			
			String url;
			
			// Return server area URL.
			if (versionId == 0L) {
				// Area ID, language ID.
				url = String.format("%s?area_id=%d&lang_id=%d%s", serverUrl, areaId, languageId, extraProperties);
			}
			else {
				// Area ID, language ID, version ID.
				url = String.format("%s?area_id=%d&lang_id=%d&ver_id=%d%s", serverUrl, areaId, languageId, versionId, extraProperties);
			}
			
			return url;
		}
	}
	
	/**
	 * Get area URL.
	 * @param areaId
	 * @return
	 * @throws Exception
	 */
	public String getAreaUrl(long areaId, Boolean localhost) throws Exception {
		
		return getAreaUrl(areaId, state.currentLanguage.id, state.currentVersionId, localhost, null);
	}
	
	/**
	 * Get rendered area URL.
	 * @param areaId
	 * @param languageId
	 * @param versionId
	 * @param otherProperties 
	 * @return
	 * @throws Exception
	 */
	public String getRenderedAreaUrl(long areaId, Long languageId,
			Long versionId, TagProperties otherProperties) throws Exception {

		try {
			// Get file name.
			String fileName = getRenderedAreaFileName(areaId, languageId, versionId);
			
			// Get assembled path of current page and referenced area.
			String currentPageAbsolutePath = getAreaAssembledPath(state.requestedArea.getId(), state.currentVersionId);
			String referencedAreaAbsolutePath = getAreaAssembledPath(areaId, versionId);
	
			
			// Get relative path.
			String relativePath = Utility.getRelativePath(currentPageAbsolutePath, referencedAreaAbsolutePath);
			
			// Return URL.
			if (relativePath.isEmpty()) {
				return fileName;
			}
			
			// Extra properties.
			String extraProperties = MiddleUtility.chainUrlProperties(otherProperties);
			if (!extraProperties.isEmpty()) {
				extraProperties += "/?";
			}
			
			return relativePath + File.separator + fileName + extraProperties;
		}
		catch (Exception e) {
			throwErrorText(String.format(Resources.getString("server.messageGetRenderedAreaUrlError"), e.getMessage()));
		}
		
		return null;
	}
	
	/**
	 * Get rendered area file name.
	 * @param areaId
	 * @param languageId
	 * @param versionId
	 * @return
	 * @throws Exception
	 */
	public String getRenderedAreaFileName(long areaId, long languageId,
			long versionId) throws Exception  {
		
		// Load area.
		Area area = getArea(areaId);
		if (area == null) {
			throwError("server.messageBadGetRenderedAreaFileNameAreaId", areaId);
		}
		
		// Get start resource.
		StartResource startResource = getStartResource(areaId, versionId);
		
		// Set file extension.
		String extensionText = area.getFileExtension();
		if (extensionText.isEmpty() || versionId != 0L) {
			
			extensionText = startResource.mimeExtension;
		}
		
		boolean notLocalized = startResource.notLocalized;
		
		// Trim extension.
		if (extensionText == null) {
			extensionText = "";
		}
		
		// If the rendered file is not localized, set language to default.
		if (notLocalized) {
			languageId = 0L;
		}
		
		// Get file name.
		String fileNameText = area.getFileName();
		if (fileNameText == null) {
			fileNameText = "";
		}
		
		// Return index.htm.
		if (isIndexHtm(areaId, languageId, versionId, fileNameText)) {
			if (!extensionText.isEmpty()) {
				return "index." + extensionText;
			}
			else {
				return "index";
			}
		}
		
		// Is file name flag.
		boolean isFileName = !fileNameText.isEmpty();
		
		// File name representation.
		if (!isFileName) {
			fileNameText = "area" + areaId;
		}
		
		// Language representation.
		Language language = getLanguage(languageId);
		String languageText = null;
		
		if (languageId == 0L) {
			languageText = "";
		}
		else {
			if (isFileName) {
				languageText = "_" + language.alias;
			}
			else {
				languageText = "_" + languageId;
			}
		}
		
		// Version representation.
		VersionObj version = getVersion(versionId);
		String versionText = null;
		
		if (versionId == 0L) {
			versionText = "";
		}
		else {
			if (isFileName) {
				versionText = "_" + version.getAlias();
			}
			else {
				versionText = "_v" + versionId;
			}
		}
		
		// Extension representation.
		if (!extensionText.isEmpty()) {
			extensionText = "." + extensionText;
		}
		
		// Compile rendered file name.
		String renderedFileName = fileNameText + languageText + versionText + extensionText;
		return renderedFileName;
	}

	/**
	 * Get start resource.
	 * @param areaId
	 * @param versionId 
	 * @return
	 */
	protected StartResource getStartResource(long areaId, long versionId) throws Exception {
		
		Area area = getArea(areaId);
		
		Obj<StartResource> startResource = new Obj<StartResource>();
		
		// Load inherited start resource.
		MiddleResult result = state.middle.loadAreaInheritedStartResource(area, versionId,
				startResource);
		if (result.isNotOK()) {
			throwErrorText(String.format(Resources.getString("server.messageCannotGetAreaStartResource"),
					area.toString(), result.getMessage()));
		}
		
		if (startResource.ref.mimeExtension == null) {
			startResource.ref.mimeExtension = "";
		}
		
		return startResource.ref;
	}
	
	/**
	 * Get current start resource.
	 * @return
	 */
	public StartResource getCurrentStartResource() {
		
		try {
			long areaId = state.area.getId();
			long versionId = state.currentVersionId;
			
			StartResource startResource = getStartResource(areaId, versionId);
			return startResource;
		}
		catch (Exception e) {
            e.printStackTrace();
        }
		return null;
	}

	/**
	 * USING processor.
	 */
	private static void usingProcessor() {
		
		complexSingleTagProcessors.put("USING", new ComplexSingleTagProcessor() {
			@Override
			public String processTag(AreaServer server, TagProperties properties)
					throws Exception {
				
				// Get area from the command properties.
				Area area = getAreaFromProperties(server, properties, null);
				
				// If the area server renders source files
				if (server.state.renderingResources != null) {

					// Get resource ID.
					String resourceIdText = properties.getProperty("resId");
					if (resourceIdText != null) {
						
						long resourceId = server.evaluateText(resourceIdText, Long.class, false);

						// Try to get resource.
						Resource resource = server.resource(resourceId);

						// Get file name.
						String fileName = properties.getProperty("file");

						if (fileName == null) {
							
							server.setRenderingResourceExt(resource);
						}
						else {
							
							// Evaluate file name.
							fileName = server.evaluateText(fileName, String.class, false);
							
							server.setRenderingResourceFile(resource, fileName);
						}
						
						return "";
					}
										
					// Get area ID.
					long areaId = area.getId();
					
					// Get resource ID.
					String resourceName = properties.getProperty("res");
					if (resourceName != null) {
						
						// Evaluate resource name.
						resourceName = server.evaluateText(resourceName, String.class, false);

						AreaResource resource = server.resource(resourceName, areaId);
						
						// Get file name.
						String fileName = properties.getProperty("file");

						if (fileName == null) {
							
							server.setRenderingResourceExt(resource);
						}
						else {
							// Evaluate file name.
							fileName = server.evaluateText(fileName, String.class, false);
							
							server.setRenderingResourceFile(resource, fileName);
						}
					}
					else {
						LinkedList<Long> resourcesIds = new LinkedList<Long>();
						// Add all resources of given area.
						MiddleResult result = server.state.middle.loadAreaResourcesIds(areaId, resourcesIds);
						if (result.isNotOK()) {
							throwError(result.getMessage());
						}
						
						// Create rendering resources.
						for (long resourceId : resourcesIds) {
							
							AreaResource resource = server.resource(resourceId, areaId);
							
							server.setRenderingResourceExt(resource);
						}
					}
				}
				
				// If not rendering and the files should be extracted to application directory, do it.
				else if (properties.containsKey("extract")) {
					
					// Get "render related areas" flag that is a value of the extract property.
					boolean renderRelatedAreas = false;
					
					String extract = properties.getProperty("extract");
					if (extract != null) {
						renderRelatedAreas = server.evaluateText(extract, Boolean.class, false);
					}
					
					// Try to get page content encoding.
					String encoding = properties.getProperty("encoding");
					encoding = server.evaluateText(encoding, String.class, true);
					
					// Render area or related areas to application directory on server.
					TextRenderer.renderToServer(server, area, renderRelatedAreas, encoding);
				}
				
				return "";
			}
		});
	}

	/**
	 * RENDER_CLASS processor.
	 */
	private static void renderClassProcessor() {
		
		complexSingleTagProcessors.put("RENDER_CLASS", new ComplexSingleTagProcessor() {
			@Override
			public String processTag(AreaServer server, TagProperties properties)
					throws Exception {
				
				if (server.isRendering() && server.state.response != null) {
					
					// Try to get "name" property.
					String name = server.evaluateProperty(properties, "name", String.class, "", REQUIRED_PROPERTY);
					
					// Try to get "text" property.
					String text = server.evaluateProperty(properties, "text", String.class, "", REQUIRED_PROPERTY);
					
					server.state.response.setRenderClass(name, text);
				}
				return "";
			}
		});
	}
	
	/**
	 * VERSION processor.
	 */
	private static void versionProcessor() {
		
		fullTagProcessors.put("VERSION_ANCHOR", new FullTagProcessor() {
			@Override
			public String processText(AreaServer server, String innerText, TagProperties properties)
					throws Exception {
				
				// Get version URL.
				Obj<Boolean> existsAreaSpecification = new Obj<Boolean>();
				Area area = getAreaFromProperties(server, properties, existsAreaSpecification);
				if (!existsAreaSpecification.ref) {
					area = server.state.area;
				}
				
				// Get version ID.
				long versionId = area.getVersionId();
				
				// Get area URL.
				String areaUrl = server.getAreaUrl(area.getId(), server.state.currentLanguage.id, versionId, null, null);

				// Get extra properties.
				String extraProperties = getExtraProperties(properties, areaPropertiesArray, "href");

				// Create anchor.
				String anchor = String.format("<a href=\"%s\"%s>%s</a>", areaUrl, extraProperties, innerText);
				return anchor;
			}
		});
	}
	
	/**
	 * Get extra properties.
	 * @param properties
	 * @param string
	 * @return
	 */
	protected static String getExtraProperties(TagProperties properties,
			Object ... objects) {
		
		
		// Add reserved properties.
		HashSet<String> reservedProperties = new HashSet<String>();
		for (Object object : objects) {
			
			if (object instanceof String) {
				reservedProperties.add((String) object);
			}
			else if (object instanceof String []) {
				
				for (String name : (String []) object) {
					reservedProperties.add(name);
				}
			}
		}
		
		String extraProperties = "";
		for (Object key : properties.keySet()) {
			// If it is reserved property, continue the loop.
			if (reservedProperties.contains(key)) {
				continue;
			}
			// Add extra property.
			extraProperties += String.format(" %s=\"%s\"", key, properties.get(key));
		}
		
		return extraProperties;
	}

	/**
	 * VERSION_URL processor.
	 */
	private static void versionUrlProcessor() {
		
		complexSingleTagProcessors.put("VERSION_URL", new ComplexSingleTagProcessor() {
			@Override
			public String processTag(AreaServer server, TagProperties properties)
					throws Exception {
				
				// Get version URL.
				Obj<Boolean> existsAreaSpecification = new Obj<Boolean>();
				Area area = getAreaFromProperties(server, properties, existsAreaSpecification);
				if (!existsAreaSpecification.ref) {
					area = server.state.area;
				}
				
				// Get version ID.
				long versionId = area.getVersionId();
				
				// Other properties.
				TagProperties otherProperties = excludeProperties(properties, null, areaPropertiesArray);
				
				// Get area URL.
				String areaUrl = server.getAreaUrl(area.getId(), server.state.currentLanguage.id, versionId, null, otherProperties);
				return areaUrl;
			}
		});
	}
	
	/**
	 * VERSION_NAME processor.
	 */
	private static void versionNameProcessor() {
		
		complexSingleTagProcessors.put("VERSION_NAME", new ComplexSingleTagProcessor() {
			@Override
			public String processTag(AreaServer server, TagProperties properties)
					throws Exception {
				
				// Initialize version object.
				VersionObj version = null;
				
				// Get version from ID.
				Long versionId = server.evaluateProperty(properties, "versionId", Long.class, null, DEFAULT);
				if (versionId != null) {
					version = server.getVersion(versionId);
				}
				
				// Get version alias.
				if (version != null) {
					String alias = server.evaluateProperty(properties, "versionAlias", String.class, null, DEFAULT);
					version = server.getVersion(alias);
				}
				
				String text = version.getDescription();

				// Get version name.
				return (server.state.showLocalizedTextIds ? getIdHtml("V", versionId) + text : text);
			}
		});
	}

	/**
	 * VERSION_ID processor.
	 */
	private static void versionIdProcessor() {
		
		complexSingleTagProcessors.put("VERSION_ID", new ComplexSingleTagProcessor() {
			@Override
			public String processTag(AreaServer server, TagProperties properties)
					throws Exception {
				
				// Initialize version object.
				VersionObj version = null;
				
				// Get version from ID.
				Long versionId = server.evaluateProperty(properties, "versionId", Long.class, null, DEFAULT);
				if (versionId != null) {
					version = server.getVersion(versionId);
				}
				
				// Get version alias.
				if (version != null) {
					String alias = server.evaluateProperty(properties, "versionAlias", String.class, null, DEFAULT);
					version = server.getVersion(alias);
				}
				
				long id = version.getId();
				String text = String.valueOf(id);

				// Get version name.
				return (server.state.showLocalizedTextIds ? getIdHtml("V", versionId) + text : text);
			}
		});
	}
	
	/**
	 * TIMEOUT processor.
	 */
	private static void timeoutProcessor() {
		
		complexSingleTagProcessors.put("TIMEOUT", new ComplexSingleTagProcessor() {
			@Override
			public String processTag(AreaServer server, TagProperties properties)
					throws Exception {
				
				// Initialization.
				Long timeoutMilliseconds = null;
				
				try {
					
					// Get timeout in milliseconds.
					Enumeration<Object> items = properties.keys();
					
					// Check parameters.
					if (items.hasMoreElements()) {
						
						// Try to get timeout value in milliseconds directly.
						Object item = items.nextElement();
						if (item instanceof String) {
							String timeoutText = (String) item;
						
							timeoutMilliseconds = Long.parseLong(timeoutText);
							
							// Set milliseconds.
							server.state.responseTimeoutMilliseconds = timeoutMilliseconds;
						}
						
						// Initialize constants.
						final String msConstant = "ms";
						final String defaultConstant = "default";
						
						boolean useDefaultValue = false;
						
						// Try to get "ms" property value specifying timeout milliseconds.
						boolean msPropertyExists = properties.containsKey(msConstant);
						if (timeoutMilliseconds == null && msPropertyExists) {
							
							// Get property value.
							String msPropertyText = properties.getProperty(msConstant);
							
							// Try to set default timeout when the "ms" property has null or the "default" string value.
							useDefaultValue = (msPropertyText == null);
							if (!useDefaultValue) {
								
								useDefaultValue = (defaultConstant.equals(msPropertyText));
								if (!useDefaultValue) {
									
									String defaultText = server.evaluateExpression(msPropertyText, String.class, null, NULL);
									if (defaultConstant.equals(defaultText)) {
										
										useDefaultValue = true;
									}
								}
							}
							
							// Set default timeout.
							if (useDefaultValue) {
								timeoutMilliseconds = AreaServerState.defaultTimeoutValue;
							}
							// Else set user timeout value.
							else {
								try {
									// Try to get 
									timeoutMilliseconds = server.evaluateExpression(msPropertyText, Long.class, null, NULL);
									if (timeoutMilliseconds == null) {
										timeoutMilliseconds = AreaServerState.defaultTimeoutValue;
									}
								}
								catch (Exception e) {
								}
							}
						}
					}
				}
				catch (Exception e) {
				}
				
				// Throw error.
				if (timeoutMilliseconds == null) {
					throwError("server.messageExpectingTimoutInMilliseconds");
				}
				
				// Return empty string.
				return "";
			}
		});
	}

	/**
	 * BOOKMARK processor.
	 */
	private static void bookmarkProcessor() {
		
		complexSingleTagProcessors.put("BOOKMARK", new ComplexSingleTagProcessor() {
			@Override
			public String processTag(AreaServer server, TagProperties properties)
					throws Exception {
				
				// Get the "name" property.
				String name = server.evaluateProperty(properties, "name", String.class, null, NULL);
				if (name == null) {

					// Get first property name.
					Enumeration<Object> keys = properties.keys();
					
					if (!keys.hasMoreElements()) {
						throwError("server.messageMissingBookMarkName");
					}
					
					name = (String) keys.nextElement();
				}
				
				// If bookmark value exists, return it.
				String bookmarkValue = server.state.bookmarkReplacement.get(name);
				if (bookmarkValue != null) {
					return bookmarkValue;
				}
				
				//  Otherwise put bookmark into a map.
				server.state.bookmarkReplacement.put(name, "");
				
				// Returns a book mark tag which is processed in the next iteration of the Area Server preprocessor.
				return String.format("[@@BOOKMARK %s]", name);
			}
		});
	}

	/**
	 * REPLACE_BOOKMARK processor.
	 */
	private static void replaceBookmarkProcessor() {
		
		fullTagProcessors.put("REPLACE_BOOKMARK", new FullTagProcessor() {
			@Override
			public String processText(AreaServer server, String innerText, TagProperties properties)
					throws Exception {
				
				// Get name property.
				String name = properties.getProperty("name");
				if (name != null) {
					
					// Evaluate name property.
					name = server.evaluateText(name, String.class, false);
				}
				else {
					// Get first property name.
					Enumeration<?> keys = properties.keys();
					
					if (!keys.hasMoreElements()) {
						throwError("server.messageMissingBookMarkName");
					}
					
					name = (String) keys.nextElement();
				}
				
				// Process inner text.
				String text = server.processTextCloned(innerText);
			
                // Add bookmark replace text.
				server.state.bookmarkReplacement.put(name, text);
				
				return "";
			}
		});
	}

	/**
	 * INCLUDE_ONCE processor.
	 */
	private static void includeOnceProcessor() {
		
		fullTagProcessors.put("INCLUDE_ONCE", new FullTagProcessor() {
			@Override
			public String processText(AreaServer server, String innerText, TagProperties properties)
					throws Exception {
				
				// Check property.
				if (properties.size() != 1) {
					throwError("server.messageMissingIncludeOnceIdentifier");
				}
				
				// Get identifier.
				String identifier;
				if (properties.containsKey("name")) {
					identifier = properties.getProperty("name");
				}
				else {
					identifier = properties.keys().nextElement().toString();
				}
				
				// If the identifier already exists, return empty string.
				if (server.state.foundIncludeIdentifiers.contains(identifier)) {
					return "";
				}
				
				// Otherwise save identifier and return inner text.
				server.state.foundIncludeIdentifiers.add(identifier);
				return innerText;
			}
		});
	}

	/**
	 * LITERAL processor.
	 */
	private static void literalProcessor() {
		
		fullTagProcessors.put("LITERAL", new FullTagProcessor() {
			@Override
			public String processText(AreaServer server, String innerText, TagProperties properties)
					throws Exception {
				
				// Replace command open brackets.
				return innerText.replace("[", "@lb;");
			}
		});
	}

	/**
	 * PRAGMA processor.
	 */
	private static void pragmaProcessor() {
		
		complexSingleTagProcessors.put("PRAGMA", new ComplexSingleTagProcessor() {
			@Override
			public String processTag(AreaServer server, TagProperties properties)
					throws Exception {
				
				// Get php property and evaluate it.
				Boolean php = server.evaluateProperty(properties, "php", Boolean.class, null, FLAG | NULL);
				if (php != null) {
					server.state.enablePhp.ref = php;
				}
				
				// Set tabulator.
				String tabulator = server.evaluateProperty(properties, "tabulator", String.class, null, NULL);
				if (tabulator != null) {
					server.state.tabulator.ref = tabulator;
				}
				
				// Set web interface directory
				String webInterface = server.evaluateProperty(properties, "webInterface", String.class, null, NULL);
				if (webInterface != null) {

					// Set and check web interface directory path
					Path path = Paths.get(webInterface);
					if (!path.isAbsolute()) {
						path = Paths.get(MiddleUtility.getWebInterfaceDirectory(), webInterface);
					}
					
					if (!Files.isDirectory(path)) {
						throwError("server.messageWebInterfaceDirectoryNotFound", webInterface);
					}
					
					server.state.webInterfaceDirectory.ref = path.toString();
				}
				
				// Include "<meta ... charset=...>" in the "<head>" section of the page.
				Boolean metaCharset = server.evaluateProperty(properties, "metaCharset", Boolean.class, false, FLAG);
				if (metaCharset != null) {
					server.state.useMetaCharset = metaCharset;
				}
				
				// Get "meta info" value.
				String metaInfo = server.evaluateProperty(properties, "metaInfo", String.class, null, NULL);
				if (metaInfo != null) {
					
					// Change current Area Server state.
					if ("false".equals(metaInfo)) {
						server.state.enableMetaTags = AreaServerState.metaInfoFalse;
					}
					else if ("true".equals(metaInfo)) {
						server.state.enableMetaTags = AreaServerState.metaInfoTrue;
					}
					else if ("temporary".equals(metaInfo)) {
						server.state.enableMetaTags = AreaServerState.metaInfoTemporary;
					}
				}
				
				// Nothing to return.
				return "";
			}
		});
	}
	
	/**
	 * JAVASCRIPT processor.
	 */
	private static void javaScriptProcessor() {
		
		fullTagProcessors.put("JAVASCRIPT", new FullTagProcessor() {
			@Override
			public String processText(AreaServer server, String innerText, TagProperties properties)
					throws Exception {
				
				// Get global flag.
				Boolean isGlobal = server.evaluateProperty(properties, "global", Boolean.class, null, FLAG);
				if (isGlobal == null) {
					server.evaluateProperty(properties, "$global", Boolean.class, false, FLAG);
				}
				
				// Strip meta information.
				innerText = server.stripMetaInformation(innerText);
				
				// Get process inner text flag.
				boolean isPitext = properties.containsKey("pitext") || properties.containsKey("$pitext");
				if (isPitext) {
					innerText = server.processTextCloned(innerText);
				}
				
				// Create JavaScript block descriptor.
				server.state.blocks.pushNewJavaScriptBlockDescriptor();
				
				// Evaluate inner text.
				try {
					
					// Bind area server with scripting engine.
					server.state.scriptingEngine.bindAreaServer(server);
					// If this block is local (not global) wrap the code with function invocation.
					if (!isGlobal) {
						innerText = String.format("(function(){%s})();", innerText);
					}
					
					// Run script code.
					server.state.scriptingEngine.eval(innerText);
				}
				catch (Exception e) {
					
					e.printStackTrace();
					
					server.state.blocks.popBlockDescriptor(false);
					
					String exceptionMessage = server.compileErrorMessage(e, errorFormatHtml);
					throw new Exception(exceptionMessage);
				}
				
				// Get flags used when releasing the block.
				boolean transparentVariables = server.evaluateProperty(properties, "transparentVar", Boolean.class, false, FLAG);
				boolean transparentProcedures = server.evaluateProperty(properties, "transparentProc", Boolean.class, false, FLAG);
				
				// Pop block and get result text.
				String outputText = server.state.blocks.popJavaScriptDescriptor(transparentVariables, transparentProcedures);
				
				// Return final text.
				return outputText;
			}
		});
	}

	/**
	 * INDENT processor.
	 */
	private static void indentProcessor() {
		
		fullTagProcessors.put("INDENT", new FullTagProcessor() {
			@Override
			public String processText(AreaServer server, String innerText, TagProperties properties)
					throws Exception {
				
				// Get level.
				long level = 1;
				
				String levelProperty = properties.getProperty("level");
				if (levelProperty != null) {
					
					level = server.evaluateText(levelProperty, Long.class, false);
					if (level <= 0) {
						return innerText;
					}
				}
				
				// Process text.
				innerText = server.processTextCloned(innerText);
				
				// Get tabulator.
				String tabulator;
				String tabulatorProperty = properties.getProperty("tab");
				
				if (tabulatorProperty != null) {
					tabulator = server.evaluateText(tabulatorProperty, String.class, false);
				}
				else if (server.state.tabulator .ref != null) {
					tabulator = server.state.tabulator.ref;
				}
				else {
					tabulator = "\t";
				}
				
				// Compile tabulators.
				String tabulators = "";
				for (int index = 0; index < level; index++) {
					tabulators += tabulator;
				}
				
				// Insert tabulators on each line start.
				String outputText = "";
				String lines[] = innerText.split("\r\n|\r|\n");
				
				for (String line : lines) {
					outputText += tabulators + line + "\n";
				}
				
				return outputText;
			}
		});
	}
	
	/**
	 * NOINDENT tag processor.
	 */
	private static void noindentProcessor() {
		
		fullTagProcessors.put("NOINDENT", new FullTagProcessor() {
			@Override
			public String processText(AreaServer server, String innerText, TagProperties properties) throws Exception {
				
				// Process text.
				innerText = server.processTextCloned(innerText);
				
				String uuid = UUID.randomUUID().toString();
				
				return String.format("[noindent\uE000 %s]%s[/noindent\uE000 %s]", uuid, innerText, uuid);
			}
		});
	}

	/**
	 * CSS_LOOKUP_TABLE processor.
	 */
	private static void cssLookupTableProcessor() {
		
		fullTagProcessors.put("CSS_LOOKUP_TABLE", new FullTagProcessor() {
			@Override
			public String processText(AreaServer server, String innerText, TagProperties properties) throws Exception {
				
				// Get collect flag.
				boolean collect = properties.containsKey("collect");
				if (!collect) {
					server.state.cssLookupTable = new CssLookupTable();
				}
				
				// Strip meta tags.
				innerText = server.stripMetaInformation(innerText);
				
				// Create CSS slots lookup table.
				server.createCssSlotsLookup(innerText);

				return "";
			}
		});
	}
	
	/**
	 * UNZIP processor.
	 */
	private static void unzipProcessor() {
		
		complexSingleTagProcessors.put("UNZIP", new ComplexSingleTagProcessor() {
			@Override
			public String processTag(AreaServer server, TagProperties properties) throws Exception {
				
				// Get resource from properties.
				Resource resource = server.getResourceFromProperties(properties);
				long resourceId = resource.getId();
				
				// If it was already unzipped, quit the method.
				if (properties.containsKey("once") && server.isUnzipped(resourceId)) {
					return "";
				}
				
				// Get name folder and unzip method.
				String folder = server.evaluateProperty(properties, "folder", String.class, null, STRICT);
				
				// Get web application root directory.
				String path = null;
				if (server.isRendering()) {
					path = server.getRenderingTarget();
				}
				else {
					path = server.state.request.getServerRootPath();
				}
				if (path == null) {
					throwError("server.messageCannotGetUnzipPath");
				}
				if (!folder.isEmpty()) {
					path += File.separator + folder;
				}
				
				// Remember unziped resource.
				server.rememberUnzipped(resourceId);
				
				// Unzip resource to given folder in server root directory.				
				MiddleResult result = server.state.middle.loadResourceAndUnzip(resourceId, path, "");
				if (result.isNotOK()) {
					throwErrorText(result.getMessage());
				}
				
				return "";
			}
		});
	}

	/**
	 * RUN tag processor.
	 */
	private static void runProcessor() {
		
		complexSingleTagProcessors.put("RUN" , new ComplexSingleTagProcessor() {
			@Override
			public String processTag(AreaServer server, TagProperties properties) throws Exception {
				
				// Get "cmd" property.
				String command = server.evaluateProperty(properties, "cmd", String.class, null, DEFAULT);
				
				// Get working directory path.
				String workdir = server.evaluateProperty(properties, "workdir", String.class, null, NULL);
				if (workdir == null) {
					workdir = server.state.request.getServerRootPath();
				}
				
				// Get timeout in millisecond.
				Integer timeout = server.evaluateProperty(properties, "timeout", Integer.class, null, NULL);
				
				// Run the command and get output text from its standard output stream.
				try {
					String standardOutput = Utility.runExecutable(workdir, command, timeout, TimeUnit.MILLISECONDS);
					
					// Enable to insert command output into the resuling text.
					boolean output = server.evaluateProperty(properties, "output", Boolean.class, false, FLAG);
					if (output) {
						
						// Enable escape of HTML special characters ('<', '>', '"' and '&' characters).
						boolean escape = server.evaluateProperty(properties, "escape", Boolean.class, false, FLAG);
						if (escape) {
							return Utility.htmlSpecialChars(standardOutput);
						}
						return standardOutput;
					}
				}
				catch (Exception e) {
					
					String errorMessage = e.getMessage();
					
					// Enable to throw raised exception and stop Area Server processor.
					Boolean error = server.evaluateProperty(properties, "escape", Boolean.class, false, FLAG);
					if (error) {
						throwErrorText(errorMessage);
					}					
					return errorMessage;
				}
				
				return "";
			}
		});
	}
	
	/**
	 * REDIRECT tag processor.
	 */
	private static void redirectProcessor() {
		
		complexSingleTagProcessors.put("REDIRECT" , new ComplexSingleTagProcessor() {
			@Override
			public String processTag(AreaServer server, TagProperties properties) throws Exception {
				
				// Try to get area specification for redirection.
				Obj<Boolean> existsArea = new Obj<Boolean>();
				Area area = getAreaFromProperties(server, properties, existsArea);
				
				// Set area for redirection.
				if (existsArea.ref == true && area != null) {
					
					// Get language ID, evaluate it.
					Long languageId = server.evaluateProperty(properties, "langId", Long.class, null, NULL);
					
					// Try to get ID from a language alias.
					if (languageId == null) {
						
						String languageAlias = server.evaluateProperty(properties, "langAlias", String.class, null, NULL);
						if (languageAlias != null) {
							Language language = server.getLanguage(languageAlias);
							languageId = language.id;
						}
					}
					
					if (languageId == null) {
						languageId = server.state.middle.getCurrentLanguageId();
					}
					
					// Get version ID.
					Long versionId = null;
					String versionAlias = server.evaluateProperty(properties, "versionAlias", String.class, null, NULL);
					if (versionAlias != null) {
						
						VersionObj version = server.getVersion(versionAlias);
						versionId = version.getId();
					}
					
					if (versionId == null) {
						versionId = server.state.currentVersionId;
					}
					
					// Get other properties.
					TagProperties otherProperties = excludeProperties(properties, areaPropertiesArray, "uri", "langId", "langAlias", "versionAlias");
					
					// Get area URI.
					String areaUri = server.getAreaUrl(area.getId(), server.state.currentLanguage.id, versionId, false, otherProperties);
					server.state.redirection.setUri(areaUri, false);
				}
				else {
					// Get "uri" tag property.
					String uri = server.evaluateProperty(properties, "uri", String.class, null, DEFAULT);
					
					// Set redirection URI.
					server.state.redirection.setUri(uri, true);
				}
				
				return "";
			}
		});
	}
	
	/**
	 * TRAY_MENU tag processor.
	 */
	private static void trayMenuProcessor() {
		
		complexSingleTagProcessors.put("TRAY_MENU" , new ComplexSingleTagProcessor() {
			@Override
			public String processTag(AreaServer server, TagProperties properties) throws Exception {
				
				// Get request action value and check it
				String actionValue = server.state.request.getAction();
				if (!"LoadMenu".equals(actionValue)) {
					return "";
				}
				
				// Get "name" property.
				String name = server.evaluateProperty(properties, "name", String.class, null, STRICT | NULL);
				if (name == null) {
					return "";
				}
				
				// Get "action" property.
				String action = server.evaluateProperty(properties, "url", String.class, null, STRICT | NULL);
				if (action == null) {
					return "";
				}
				
				// Set redirection.
				server.state.trayMenu.add(name, action);
				
				return "";
			}
		});
	}
	
	/**
	 * POST processor
	 */
	private static void postProcessor() {
		
		simpleSingleTagProcessors.put("POST", new SimpleSingleTagProcessor() {
			@Override
			public String processTag(AreaServer server) throws Exception {
				
				String post = server.state.request.post();
				
				post = Utility.replaceEmptyLines(post, newLineTag);
				
				return post;
			}
		});
	}
	
	/**
	 * Remembers the unzipped resource ID.
	 * @param resourceId
	 * @throws IOException 
	 */
	protected void rememberUnzipped(long resourceId) {
		
		// Only for current area server.
		state.unzippedResourceIds.add(resourceId);
		
		// If not rendering, use servlet temporary file.
		if (!isRendering())  {
			
			Path path = Paths.get(state.request.getServerTempPath() + File.separator + UNZIPPED_FILENAME);
			
			TreeSet<Long> resourceIds = Utility.loadLongsFromFile(path);
			resourceIds.add(resourceId);
			
			// Save new file content.
			Obj<String> newContent = new Obj<String>("");
			resourceIds.forEach(id -> { newContent.ref += String.valueOf(id) + ' '; });
			
			try {
				Files.write(path, newContent.ref.trim().getBytes());
			}
			catch (Exception e) {
			}
		}
	}

	/**
	 * Returns true value if the resource was already unzipped.
	 * @param resourceId
	 * @return
	 */
	protected boolean isUnzipped(long resourceId) {
		
		// Only for current area server.
		if (state.unzippedResourceIds.contains(resourceId)) {
			return true;
		}
		
		// If not rendering, use servlet temporary file.
		if (!isRendering()) {
			
			Path path = Paths.get(state.request.getServerTempPath() + File.separator + UNZIPPED_FILENAME);
			
			TreeSet<Long> resourceIds = Utility.loadLongsFromFile(path);
			
			return resourceIds.contains(resourceId);
		}
		
		return false;
	}

	/**
	 * Evaluate JavaScript expression.
	 * @param text
	 * @return
	 * @throws Exception
	 */
	private Object evaluateJsExpression(String text) throws Exception {
		
		// If an input text begins with "js:" remove it.
		if (text.startsWith("js:")) {
			text = text.replaceFirst("^js:", "");
		}
		
		// Evaluate inner text.
		try {
			
			// Prepare prerequisites and evaluate expression.
			putFunctionsIntoJavaScriptEngine(this);
			
			// Wrap the script code into anonymous function, run it and get result value.
			String scriptText = String.format("(function(){return %s;})()", text);
			Object value = state.scriptingEngine.eval(scriptText);
			
			return value;
		}
		catch (Exception e) {
			
			// Trim and throw exception.
			String exceptionMessage = String.format(Resources.getString("server.messageErrorInJavaScript"),
					e.getLocalizedMessage());
			
			throw new Exception(exceptionMessage);
		}
	}
	
	/**
	 * Put server functions into the JavaScript engine.
	 * @param server 
	 * @throws Exception 
	 */
	protected static void putFunctionsIntoJavaScriptEngine(AreaServer server) throws Exception {
		
		// Define JavaScript functions.
		final String javaScriptFunctionsDefinition = 
				
				  "function print(text) { _.print(text); }; "
				+ "function println(text) { _.println(text); }; "
				+ "function box(object) { return _.box(object); }; "
				+ "function unbox(object) { return _.unbox(object); }; "
				+ "function set(variableName, value) { _.set(variableName, value); }; "
				+ "function get(variableName) { return _.get(variableName); }; "
				+ "function variable(variableName, value) { _.variable(variableName, value); }; "
				+ "function defined (variableName) { return _.defined(variableName); }; "
				+ "function process(text, area) { return _.process(text, area); }; "
				+ "function processWithErrors(text, area) { return _.processWithErrors(text, area); }; "
				+ "function area(idOrAlias) { return _.area(idOrAlias); }; "
				+ "function subareas(area) { return _.subareas(area); }; "
				+ "function superareas(area) { return _.superareas(area); }; "
				+ "function slot(slotAlias, area, typeOrSkipDefault, parent, inheritanceLevel) { return _.slot(slotAlias, area, typeOrSkipDefault, parent, inheritanceLevel); }; "
				+ "function slotv(slotAlias, area, skipDefault, parent, inheritanceLevel) { var slotobj = _.slot(slotAlias, area, skipDefault, parent, inheritanceLevel); return slotobj != null ? slotobj.value : null; }; "
				+ "function slotd(slotAlias, area) { return _.slot(slotAlias, area, true, false, 0); }; "
				+ "function slotdv(slotAlias, area) { var slotobj = _.slot(slotAlias, area, true, false, 0); return slotobj != null ? slotobj.value : null; }; "
				+ "function slotDefined(slotAlias, area, skipDefault, parent) { return _.slotDefined(slotAlias, area, skipDefault, parent); }; "
				+ "function input(slot) { return _.input(slot); }; "
				+ "function watch(slot) { return _.watch(slot); }; "
				+ "function stopWatchingAll(slot) { _.stopWatchingAll(); }; "
				+ "function areaResource(name, area) { return _.areaResource(name, area); }; "
				+ "function resource(id) { return _.resource(id); }; "
				+ "function getResourceUrl(resourceName, area) { return _.getResourceUrl(resourceName, area); }; "
				+ "function getAreaUrl(area, languageId, versionId, localhost) { return _.getAreaUrl(area, languageId, versionId, localhost); }; "
				+ "function createTree(rootArea, relationName) { return _.createTree(rootArea, relationName); }; "
				+ "function getEnumeration(description) { return _.getEnumeration(description); }; "
				+ "function getCurrentLangId() { return _.getCurrentLangId(); }; "
				+ "function clearCssRules() { _.clearCssRules(); }; "
				+ "function insertCssRules(area, socketSelector, media, important) { _.insertCssRules(area, socketSelector, media, important); }; "
				+ "function getCssRules() { return _.getCssRules(); }; "
				+ "function trace(object) { var dbg = new org.maclan.server.JavaScriptDebugger(); dbg.display(typeof object, object); }; ";
		
		// Prepare the above code in the scripting engine so that all listed functions are defined in the script context.
		server.state.scriptingEngine.preparePrerequisites(javaScriptFunctionsDefinition);
	}

	/**
	 * Returns true value if a PHP interpreter is enabled.
	 */
	public boolean isPhpEnabled() {
		
		if (state.enablePhp.ref == null) {
			return false;
		}
		
		return (boolean) state.enablePhp.ref;
	}
	
	/**
	 * Disable timeout.
	 */
	@SuppressWarnings("unused")
	private void disableTimeout() {
		
		state.responseTimeoutMilliseconds = Long.MAX_VALUE;
	}

	/**
	 * Remarks processor.
	 */
	private static void remarksProcessor() {

		fullTagProcessors.put("REM", new FullTagProcessor() {
			@Override
			public String processText(AreaServer server, String innerText, TagProperties properties)
					throws Exception {

				server.state.analysis.rem_calls++;
				
				return "";
			}
		});
	}
	
	/**
	 * Pack processor.
	 */
	private static void packProcessor() {
		
		fullTagProcessors.put("PACK", new FullTagProcessor(){
			@Override
			public String processText(AreaServer server, String innerText, TagProperties properties)
					throws Exception {
				
				server.state.analysis.pack_calls++;
				
				// Process text.
				innerText = server.processTextCloned(innerText);
				if (properties.containsKey("strong")) {
					innerText = packTextStrong(innerText);
				}
				else {
					innerText = packText(innerText);
				}
				
				if (properties.containsKey("trim")) {
					innerText = innerText.trim();
					return innerText;
				}
				
				if (properties.containsKey("trimEnd") ) {
					innerText = innerText.replaceFirst("\\s+$", "");
				}
				
				if (properties.containsKey("trimBegin")) {
					innerText = innerText.replaceFirst("^\\s+", "");
				}
				
				return innerText;
			}
		});
	}

	/**
	 * Block processor.
	 */
	private static void blockProcessor() {
		
		fullTagProcessors.put("BLOCK", new FullTagProcessor() {
			@Override
			public String processText(AreaServer server, String innerText, TagProperties properties)
					throws Exception {
				
				server.state.analysis.block_calls++;
				
				// Get area from properties.
				Area area = getAreaFromProperties(server, properties, null);
				// Push new block descriptor.
				server.state.blocks.pushNewBlockDescriptor();
				// Process text.
				innerText = server.processTextCloned(area, innerText);
				// Get flags used when releasing the block.
				boolean transparentVariables = server.evaluateProperty(properties, "transparentVar", Boolean.class, false, FLAG);
				boolean transparentProcedures = server.evaluateProperty(properties, "transparentProc", Boolean.class, false, FLAG);
				// Pop block descriptor.
				server.state.blocks.popBlockDescriptor(transparentVariables, transparentProcedures);
				
				// Return text.
				return innerText;
			}
		});
	}

	/**
	 * Process properties text (PPTEXT) processor.
	 */
	private static void pptextProcessor() {
		
		fullTagProcessors.put("PPTEXT", new FullTagProcessor() {
			@Override
			public String processText(AreaServer server, String innerText, TagProperties properties)
					throws Exception {
				
				// Set server processProperties flag.
				boolean oldFlag = server.state.processProperties;
				server.state.processProperties  = true;
				// Process text.
				innerText = server.processTextCloned(innerText);
				// Restore the flag.
				server.state.processProperties = oldFlag;
				
				// Return text.
				return innerText;
			}
		});
	}

	/**
	 * Sub areas processor.
	 */
	private static void subareasProcessor() {
		
		complexSingleTagProcessors.put("SUBAREAS", new ComplexSingleTagProcessor() {
			@Override
			public String processTag(final AreaServer server, TagProperties properties)
					throws Exception {
				
				server.state.analysis.subareas_calls++;
				
				// Process common area list properties.
				return processCommonAreaListProperties(server, properties, new AreasListGetter() {
					@Override
					LinkedList<Area> getAreaList(Area area) throws Exception {
						server.loadSubAreasData(area);
						return area.getSubareas();
					}
					@Override
					AreaRelation getRelation(Area area, long relatedAreaId) {
						return area.getSubRelation(relatedAreaId);
					}
				});
			}
		});
	}

	/**
	 * Super areas processor.
	 */
	private static void superareasProcessor() {

		complexSingleTagProcessors.put("SUPERAREAS", new ComplexSingleTagProcessor() {
			@Override
			public String processTag(final AreaServer server, TagProperties properties)
					throws Exception {

				server.state.analysis.superareas_call++;
				
				// Process common area list properties.
				return processCommonAreaListProperties(server, properties, new AreasListGetter() {
					@Override
					LinkedList<Area> getAreaList(Area area) throws Exception {
						server.loadSuperAreasData(area);
						return area.getSuperareas();
					}
					@Override
					AreaRelation getRelation(Area area, long relatedAreaId) {
						return area.getSuperRelation(relatedAreaId);
					}
				});
			}
		});
	}
	
	/**
	 * TRACE processor.
	 */
	private static void traceProcessor() {
		
		complexSingleTagProcessors.put("TRACE", new ComplexSingleTagProcessor() {
			@Override
			public String processTag(AreaServer server, TagProperties properties)
					throws Exception {
				
				// Get trace name.
				String traceName = server.evaluateProperty(properties, "name", String.class, null, NULL);
								
				// Set break point name.
				server.state.breakPointName = traceName;
				
				// Get flags.
				boolean decorated = !server.evaluateProperty(properties, "simple", Boolean.class, false, FLAG);
				boolean log = server.evaluateProperty(properties, "log", Boolean.class, false, FLAG);
				
				// Initialize trace.
				String trace = decorated ? "<div style='width: 600px'>" : "\n";
				
				// Get trace caption.
				trace += getTraceCaption(traceName, decorated);

				// Get server info.
				trace += server.getServerTrace(decorated);
				
				// Close trace.
				trace += decorated ? "</div>" : "________________________________________________________\n";
				
				// Log the trace result.
				if (log) {
					log(trace);
				}
				
				return trace;
			}
		});
	}
	
	/**
	 * INNER processor
	 */
	private static void innerProcessor() {
		
		simpleSingleTagProcessors.put("INNER", new SimpleSingleTagProcessor() {
			@Override
			public String processTag(AreaServer server) throws Exception {
				
				Object innerObject = server.state.blocks.findVariableValue("$inner");
				return innerObject.toString();
			}
		});
	}
	
	/**
	 * BREAK processor.
	 */
	private static void breakProcessor() {
		
		complexSingleTagProcessors.put("BREAK", new ComplexSingleTagProcessor() {
			@Override
			public String processTag(AreaServer server, TagProperties properties)
					throws Exception {
				
				// Get break name.
				String breakName = server.evaluateProperty(properties, "name", String.class, null, DEFAULT);

				server.state.breakPointName = breakName;
				
				// Get the "no" flag. Do not break the Area Server if flag is satisfied.
				Boolean noBreak = server.evaluateProperty(properties, "no", Boolean.class, null, FLAG | NULL);
				if (noBreak) {
					return "";
				}
				
				// If debugger is connected set throw exception.
				boolean debuggerSuccess = server.isDebuggerConnected();
				if (!debuggerSuccess) {

					if (breakName != null) {
						AreaServer.throwError("server.messageProgramBreakName", breakName);
					}
					else {
						AreaServer.throwError("server.messageProgramBreak");
					}
				}
				return "";
			}
		});
	}
	
	/**
	 * Check if debugger is connected.
	 * @return
	 */
	protected boolean isDebuggerConnected() {
		
		// Get debug flag.
		boolean isDebugged = isDebuggerEnabled();
		if (isDebugged) {
			
			// Check current Xdebug client.
			DebugInfo debugInfo = state.debugInfo;
			if (debugInfo != null) {
				
				XdebugClient debugClient = debugInfo.getDebugClient();
				boolean debuggerSuccess = (debugClient != null);
				
				return debuggerSuccess;
			}
		}
		return false;
	}

	/**
	 * Get server info.
	 * @param decorated 
	 * @return
	 * @throws Exception 
	 */
	protected String getServerTrace(boolean decorated) throws Exception {
		
		String trace = null;

		// Load home area.
		Obj<Area> homeArea = new Obj<Area>();
		loadHomeAreaData(homeArea);
		
		// Table descriptor.
		String [][] descriptor = {
				{"server.textRequestTrace", state.request.trace(decorated)},
				{"server.textSourceTrace", getSourceTrace(decorated)},
				{"server.textCurrentLanguageTrace", state.currentLanguage.trace(decorated)},
				{"server.textCurrentVersionTrace", String.valueOf(state.currentVersionId)},
				{"server.textServerAreaTrace", state.area.trace(decorated)},
				{"server.textStartAreaTrace", state.startArea.trace(decorated)},
				{"server.textRequestedAreaTrace", state.requestedArea.trace(decorated)},
				{"server.textHomeAreaTrace", homeArea.ref.trace(decorated)},
				{"server.textIncludeIdentifiers", getIncludeIdentifersTrace(decorated)},
				{"server.textBlocksTrace", state.blocks.trace(decorated)}
		};
		
		// Create HTML table.
		String table = MiddleUtility.createLocalizedTraceTable(descriptor, decorated);
		
		if (decorated) {

			trace = String.format(
					"<style>" +
					"  td {vertical-align: top}" +
					"  .TraceInfo {background-color: black; color: green; border: solid 1px black}" +
					"  .TraceTable {color: lightgreen}" +
					"</style>" +
					"<div class='TraceInfo'>" +
					"%s" +
					"</div>",
					table
					);
		}
		else {
			trace = table;
		}
		
		return trace;
	}

	/**
	 * Get include identifiers trace.
	 * @param decorated
	 * @return
	 */
	private String getIncludeIdentifersTrace(boolean decorated) {
		
		String listText = "";
		
		int index = 1;
		int size = state.foundIncludeIdentifiers.size();
		
		for (String identifier : state.foundIncludeIdentifiers) {
			
			listText += identifier;
			
			if (index < size) {
				listText += decorated ? "<br>" : ", ";
			}
			
			index++;
		}
		
		String trace = decorated ? "<span style='color: black; background-color: lightgreen'>" + listText  + "</span>"
				: listText;
		
		return trace;
	}

	/**
	 * Get source trace.
	 * @param decorated
	 * @return
	 */
	private String getSourceTrace(boolean decorated) {
		
		final int numberCharacters = 128;
		
		int startPosition = state.tagStartPosition - numberCharacters;
		if (startPosition < 0) {
			startPosition = 0;
		}
		
		String substring = state.text.substring(startPosition, state.tagStartPosition);
		substring = MiddleUtility.trimTextWithTags(substring, decorated);
		
		String trace = decorated ? "<span style='color: black; background-color: lightgreen'>" + substring  + "</span>"
				: "";
		
		return trace.trim();
	}

	/**
	 * Get trace caption.
	 * @param traceName
	 * @param decorated
	 * @return
	 */
	protected static String getTraceCaption(String traceName,
			boolean decorated) {
		
		// Check if trace name exists.
		String traceCaptionText = traceName != null ? Resources.getString("server.textTraceName") + ": " + traceName
				: Resources.getString("server.textTraceNoName");
		
		String traceText = null;

		if (decorated) {
			traceText = String.format(
					"<style>" +
					"  .TraceCaption {background-color: red; text-align: center; color: yellow; font-weight: bold; border: solid 1px black}" +
					"</style>" +
					"<div class='TraceCaption'>%s</div>", traceCaptionText);
		}
		else {
			traceText = String.format
					("______________________%s______________________\n",
							traceCaptionText);
		}
		
		return traceText;
	}

	/**
	 * Get blocks trace.
	 * @param decorated 
	 * @return
	 */
	protected static String getStackTrace(boolean decorated) {
		
		String stackTrace = "<br><span style='font-style: italic'>";
		for (StackTraceElement traceElement : Thread.currentThread().getStackTrace()) {
			stackTrace += traceElement.toString() + "<br>";
		}
		stackTrace += "</span>";
		return stackTrace;
	}

	/**
	 * Process common area list properties.
	 * @param server
	 * @param properties
	 * @param areasListGetter
	 */
	protected static String processCommonAreaListProperties(AreaServer server,
			TagProperties properties, AreasListGetter areasListGetter)
		throws Exception {
		
		// Get variable property.
		String variableName = properties.getProperty("list");
		
		String variableFirst = null;
		String variableLast = null;
		if (variableName == null) {
			
			variableFirst = properties.getProperty("first");
			if (variableFirst == null) {
				
				variableLast = properties.getProperty("last");
				if (variableLast == null) {
			    
					throwError("server.messageExpectingVariableProperty");
				}
			}
		}
		
		// Get area.
		Area area = getAreaFromProperties(server, properties, null);
		
		LinkedList<Area> areas = areasListGetter.getAreaList(area);
		LinkedList<Area> selectedAreas = new LinkedList<Area>();
		
		// Get "cond" property.
		String condition = properties.getProperty("cond");
		
		if (condition != null) {
			
			// Push area descriptor.
			BlockDescriptor descriptor = new BlockDescriptor();
			server.state.blocks.pushBlockDescriptor(descriptor);
			
			// Create "this" variable.
			Variable thisVariable = descriptor.createBlockVariable("this", null);
			
			// Create "thisRel" variable.
			Variable thisRelVariable = descriptor.createBlockVariable("thisRel", null);
			
			// Create "thisIndex" variable.
			Variable thisIndexVariable = descriptor.createBlockVariable("thisIndex", null);
			
			// Create "thisSize" variable.
			descriptor.createBlockVariable("thisSize", (long) areas.size());
			
			long index = 0;
			for (Area thisArea : areas) {

				// Set this area.
				thisVariable.value = thisArea;
				
				// Set this_rel value.
				thisRelVariable.value = areasListGetter.getRelation(area, thisArea.getId());
				
				thisIndexVariable.value = index;
				
				// Evaluate condition expression.
				if (server.evaluateText(condition, Boolean.class, false)) {
					
					selectedAreas.add(thisArea);
				}
				
				index++;
			}
			
			// Get flags used when releasing the block.
			boolean transparentVariables = server.evaluateProperty(properties, "transparentVar", Boolean.class, false, FLAG);
			boolean transparentProcedures = server.evaluateProperty(properties, "transparentProc", Boolean.class, false, FLAG);
			
			// Pop area descriptor.
			server.state.blocks.popBlockDescriptor(transparentVariables, transparentProcedures);
		}
		else {
			selectedAreas.addAll(areas);
		}

		// If there is a "reversed" flag, reverse the list items.
		boolean reversed = server.evaluateProperty(properties, "reversed", Boolean.class, false, FLAG);
		if (reversed) {
			
			LinkedList<Area> reversedAreas = new LinkedList<Area>();
			for (Area selectedArea : selectedAreas) {
				reversedAreas.addFirst(selectedArea);
			}
			selectedAreas = reversedAreas;
		}
			
		// Create variables.
		if (variableName != null) {
			server.state.blocks.createVariable(variableName, selectedAreas);
		};
		if (variableFirst != null) {
			Area selectedArea = selectedAreas.isEmpty() ? null : selectedAreas.getFirst();
			server.state.blocks.createVariable(variableFirst, selectedArea);
		}
		if (variableLast != null) {
			Area selectedArea = selectedAreas.isEmpty() ? null : selectedAreas.getLast();
			server.state.blocks.createVariable(variableLast, selectedArea);
		}

		return "";

	}

	/**
	 * Evaluate text
	 * @param text
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T evaluateText(String text, Class<T> type, boolean enableNull)
		throws Exception {
		
		if (enableNull && text == null) {
			return null;
		}
		
		Object value = null;
		
		// If an expression starts with "js:" prefix, evaluate JavaScript expression.
		if (text.startsWith("js:")) {
			value = evaluateJsExpression(text);
		}
		else {
			value = ExpressionSolver.evaluate(text, new AreaIdentifierSolver(this),
					new AreaFunctionSolver(this));
		}
		
		// If null value not enabled, throw error.
		if (!enableNull && value == null) {
			throwError("server.messageBadExpressionValueType", "null",
					type.getCanonicalName());
		}
		
		if (enableNull && value == null) {
			return null;
		}
		
		// If type is null, do not check value type.
		if (type == null) {
			return (T) value;
		}
		
		// Check value type.
		if (!type.isAssignableFrom(value.getClass())) {
			throwError("server.messageBadExpressionValueType", value.getClass().getCanonicalName(),
					type.getCanonicalName());
		}

		return (T) value;
	}
	
	/**
	 * Evaluate expression.
	 * @param expressionString
	 * @param type
	 * @param defaultValue
	 * @param flags
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	protected <T> T evaluateExpression(String expressionString, Class<T> type, T defaultValue, int flags)
			throws Exception {
		
		// If the value text is not present, return default value.
		if (expressionString == null || expressionString.isEmpty()) {
			return defaultValue;
		}
		
		T value = null;
	
		// On simple types just parse the input text with appropriate method or return default value.
		try { 
			if (Long.class.equals(type)) {
				value = (T)(Long) Long.parseLong(expressionString);
			}
			else if (Double.class.equals(type)) {
				value = (T)(Double) Double.parseDouble(expressionString);
			}
			else if (Boolean.class.equals(type)) {
				value = (T)(Boolean) MiddleUtility.parseBoolean(expressionString);
			}
		}
		catch (Exception e) {
		}
		if (value != null) {
			return value;
		}
		
		// When a direct string is identified, return the string value.
		if (String.class.equals(type) && expressionString != null && expressionString.length() > 0 && expressionString.charAt(0) == '#') {
			return (T) expressionString.substring(1);
		}
		
		// Get flags.
		boolean enableNull = (flags & NULL) != 0;
		
		// Evaluate value string. If needed, checks for null value.
		value = evaluateText(expressionString, type, enableNull);
		
		// Possibly use the default value.
		if (value == null) {
			return defaultValue;
		}
		
		// Return result value taken from evaluated expression.
		return value;
	}
	
	/**
	 * Evaluate property.
	 * @param <T>
	 * @param properties
	 * @param name
	 * @param type
	 * @param defaultValue
	 * @param flags - NULL | FLAG
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <T> T evaluateProperty(TagProperties properties, String name, Class<T> type, T defaultValue, int flags)
			throws Exception {
		
		String expressionString = null;
		boolean propertyExists = false;
		boolean valueExists = false;
		boolean badComputedValue = false;
		
		// Check if property is already evaluated.
		Object computedValue = properties.getComputed(name);
		if (computedValue != null) {
			
			// Return evaluated value.
			if (computedValue.getClass().equals(type)) {
				return (T) computedValue;
			}
			
			propertyExists = true;
			valueExists = true;
			badComputedValue = true;
		}
		else {
			// Get value string.
			expressionString = properties.getProperty(name);
			
			// Get conditions.
			propertyExists = properties.containsKey(name);
			valueExists = expressionString != null && !expressionString.isEmpty();
		}
		
		// Get flags.
		boolean enableFlag = (flags & FLAG) != 0;
		boolean requiredProperty = (flags & REQUIRED_PROPERTY) != 0;
		boolean requiredValue = (flags & REQUIRED_VALUE) != 0;
		
		// If the property is required, check its existence.
		if (requiredProperty && !propertyExists) {
			Utility.throwException("org.maclan.server.messagePropertyIsRequired", name, type.getSimpleName());
		}
		
		// If the value is required, check its existence.
		if (requiredValue && !valueExists) {
			Utility.throwException("org.maclan.server.messageMissingPropertyValueSpecification", name, type.getSimpleName());
		}
		
		// If the property can be used as a flag without value, check its type.
		if (enableFlag) {
			if (!Boolean.class.equals(type)) {
				Utility.throwException("org.maclan.server.messageFlagPropertyNotEvaluatedBoolean", name, type.getSimpleName());
			}
			// If a value of property is not present in the source code, the property is interpreted as a flag.
			if (!valueExists) {
				return (T)(Boolean) propertyExists;
			}
		}
		
		// If the propety doesn't exist, return the default value.
		if (!propertyExists || badComputedValue) {
			return defaultValue;
		}

		// Delegate the call.
		T value = evaluateExpression(expressionString, type, defaultValue, flags);
		
		// Save computed value.
		properties.putComputed(name, value);
		return value;
	}
	
	/**
	 * Evaluate boolaen flag.
	 * @param properties
	 * @param flagName
	 * @param enableNoValue
	 * @return
	 */
	public boolean evaluateFlag(TagProperties properties, String flagName, boolean enableNoValue)
		throws Exception {
		
		Boolean flag = properties.containsKey(flagName);
		if (!enableNoValue && flag) {
			
			String flagValueString = properties.getProperty(flagName);
			if (flagValueString != null) {
				flag = evaluateText(flagValueString, Boolean.class, false);
			}
		}
		return flag;
	}

	/**
	 * Process properties texts.
	 * @param properties
	 */
	private void processPropertyTexts(TagProperties properties)
		throws Exception {
		
		if (state.processProperties || properties.containsKey("pptext")) {

			// Remove pptext property.
			properties.remove("pptext");
			// Reset the flag to prevent the recursion.
			state.processProperties = false;
			// Process property values.
			for (Object key : properties.keySet()) {
				
				String propertyText = (String) properties.get(key);
				if (propertyText != null) {
					propertyText = processTextCloned(propertyText);
					properties.setProperty((String) key, propertyText);
				}
			}
			// Set the flag.
			state.processProperties = true;
		}
	}

	/**
	 * Process text.
	 * @param textValue
	 */
	public String processTextCloned(String textValue)
		throws Exception {
		
		return processTextCloned(state.area, textValue);
	}
	
	/**
	 * Process text.
	 * @param area
	 * @param textValue
	 * @return
	 */
	public String processTextCloned(Area area, String textValue)
		throws Exception {
		
		return processTextCloned(area, textValue, true);
	}
	
	/**
	 * Process text. Get error (do not throw exception).
	 * @param area
	 * @param textValue
	 * @return
	 */
	public String processTextClonedWithErrors(Area area, String textValue)
		throws Exception {
		
		return processTextCloned(area, textValue, false);
	}

	/**
	 * Process text.
	 * @param area
	 * @param textValue
	 * @param propagateErrors
	 * @return
	 */
	public String processTextCloned(Area area, String textValue, boolean propagateErrors)
		throws Exception {
		
		// Save original server state and clone server state.
		AreaServerState originalState = this.state;
		AreaServerState subState = state.cloneState(area, textValue); //cloneServerState(area, textValue);
		subState.parentState = originalState;
		
		// Get current block reference.
		BlockDescriptor currentBlock = originalState.blocks.getCurrentBlockDescriptor();
		
		try {
			// Set new state.
			this.state = subState;
			
			// Process text and tags.
			processAreaServerTextAndTags();
			
			// Get back old state.
			this.state.parentState = null;
			this.state = originalState;
			this.state.progateFromSubState(subState);
		}
		catch (Exception e) {
			
			// Set exception flag.
			if (propagateErrors) {
				this.state.setExceptionThrown(e);
			}
			
			// Insert exception message.
			insertErrorNoHtml(e);
			
			// Get this state text result.
			String stateText = this.state.text.toString();
			
			// Get back old state.
			this.state = originalState;
			// Propagate sub state.
			this.state.progateFromSubState(subState);
			// Return to current block.
			this.state.blocks.popToBlockDescriptor(currentBlock);
			
			return stateText;
		}
		
		// Return sub state text.
		String subStateText = subState.text.toString();
		return subStateText;
	}

	/**
	 * Create JavaScript engines.
	 */
	private void createJavaScriptEngines() {
		
		// Fill the pool with scripting engines.
		new Thread(() -> {
			synchronized (javaScriptEnginePool) {
				
				// Create a new pool, if it doesn't exist.
				if (javaScriptEnginePool.ref == null) {
					javaScriptEnginePool.ref = new ArrayList<ScriptingEngine>();
				}
				
				// Find out current pool content.
				int count = javaScriptEnginePool.ref.size();
				
				// Refill the pool.
				for (int index = count; index < JAVASCRIPT_ENGINE_COUNT; index++) {
					
					// Create scripting engine.
					ScriptingEngine engine = new ScriptingEngine();
					engine.create();
					
					// Set it unused.
					engine.setUsed(false);
					
					// Add the engine to the pool.
					synchronized (javaScriptEnginePool.ref) {
						javaScriptEnginePool.ref.add(engine);
					}
				}
			}
		}).start();
		
		ScriptingEngine scriptingEngine = null;
		
		// Main loop that searches for unused scripting engine in the pool.
		while (true) {
			
			// Get single scripting engine from the pool which is currently not used.
			synchronized (javaScriptEnginePool) {
				
				// Check if the pool was created.
				if (javaScriptEnginePool.ref != null) {
				
					int count = javaScriptEnginePool.ref.size();
					for (int index = 0; index < count; index++) {
						
						synchronized (javaScriptEnginePool.ref) {
						ScriptingEngine engine = javaScriptEnginePool.ref.get(index);
							synchronized (engine) {
								
								if (!engine.isUsed()) {
									
									scriptingEngine = engine;
									break;
								}
							}
						}
					}
					
					// If some engine was selected, exit the main loop.
					if (scriptingEngine != null) {
						break;
					}
				}
			}
			
			// Wait for released engine.
			try {
				synchronized (javaScriptEnginePool) {
					
					while (!ScriptingEngine.signalReleased) {
						
						// Wait for 20 ms.
						javaScriptEnginePool.wait(20);
					}
				}
			}
			catch (Exception e) {
			}
		}
		
		// Initialize the scripting engine and assing it to current Area Server.
		synchronized (scriptingEngine) {
			
			state.scriptingEngine = scriptingEngine;
			scriptingEngine.setUsed(true);
			state.scriptingEngine.initialize();
		}
	}

	/**
	 * Set area not visible flag
	 * @param notVisible
	 */
	public void setAreaNotVisible(boolean notVisible) {
		
		this.state.areaNotVisible = notVisible;
	}
	
	/**
	 * Get area not visible flag
	 * @return
	 */
	public boolean isAreaNotVisible() {
		
		return state.areaNotVisible;
	}
	
	/**
	 * Process text for given area.
	 * @param areaId
	 * @param versionId
	 * @param text
	 * @return
	 */
	synchronized public String loadAreaText(long areaId, long versionId, String text) throws Exception {
		
		// Create JavaScript engine.
		createJavaScriptEngines();
		
		// Set current root area.
		state.middle.setCurrentRootArea(state.area);
		
		// Create text memory
		this.state.position = 0;
		this.state.text = new StringBuilder(text);
		
		// Initialize level number.
		state.level = 1;
		
		// Load area data
		try {
			Obj<Area> outputArea = new Obj<Area>();
			MiddleResult result = state.middle.loadArea(areaId, outputArea);
			if (result.isNotOK()) {
				throwErrorText(result.getMessage());
			}
			
			// Prepare prerequisites
			state.requestedArea = outputArea.ref;
			state.area = outputArea.ref;
			
			this.state.analysis = new Analysis();
			this.state.blocks = new BlockDescriptorsStack();
			
			// Set area version.
			state.currentVersionId = versionId;
			
			// Initialize top level area server.
			initializeTopLevel();
			
			// Load languages.
			loadLanguages();
			
			// Start response timeout.
			startResponseTimeout();
			
			// Process all found levels of tags.
			while (true) {
				
				// Reset position and continue with processing.
				state.position = 0;
				state.tagStartPosition = 0;
				
				// Process text.
				processAreaServerTextAndTags();
				
				// Process book marks.
				processBookmarks();
				
				// Process "no indentation".
				processNoIndentation();
				
				// Pack text.
				packTextEx();
				
				// Check number of blocks.
				int blockCount = state.blocks.getCount();
				if (blockCount != 1) {
					throwError("server.messageBlocksError", blockCount);
				}
				
				// Decrement tags level.
				boolean decremented = decrementTagsLevel();
				
				// Post process text.
				postProcessAreaText();
				
				if (!decremented) {
					break;
				}
				
				// Shift level.
				state.level++;
			}
		}
		catch (Exception e) {
			
			// Trim error message.
			String message = String.format("[L%d] %s", state.level, e.getLocalizedMessage());
			// Throw exception
			throw new Exception(message);
		}
		
		invokeFinalListeners();
		
		// Return result
		return this.state.text.toString();
	}
	
	/**
	 * Load area page.
	 * @param middle 
	 * @param blocks 
	 * @param procedures 
	 * @param analysis 
	 * @param request2
	 * @param response2
	 * @return - true if the response must be processed by writer
	 */
	public synchronized boolean loadAreaPage(MiddleLight middle,
			BlockDescriptorsStack blocks,
			Analysis analysis, Request request2,
			Response response2) {
		
		Obj<Boolean> processResponse = new Obj<Boolean>(false);
		
		// Create JavaScript engine.
		createJavaScriptEngines();
		
		// Set middle layer reference.
		this.state.middle = middle;
		// Set blocks descriptors reference.
		this.state.blocks = blocks;
		// Set analysis object.
		this.state.analysis = analysis;
		// Set request reference.
		this.state.request = request2;
		// Set response reference.
		this.state.response = response2;
		
		try {
			// Try to execute Area Server API operation.
			if (executeApiOperation(processResponse, ApiCallType.apiCall, null)) {
				
				// Finalize page loading
				finalizeAreaPageLoading(MiddleResult.OK, response2);
				return true;
			}
			
			// Set response reference to area server.
			response2.setAreaServer(this);
			
			// Load area server data.
			loadServerAreaData(request2, response2);
		}
		catch (Exception e) {
			
			response2.setErrorHeader2(e.getLocalizedMessage());
			ServerUtilities.output(response2.getOutputStream(), 
					e.getLocalizedMessage());
			
			listenerOnError(null, e.getMessage());
			return true;
		}
		
		// Get area visibility.
		boolean visible = state.area.isVisible();
		if (!visible) {
			setAreaNotVisible(true);
			
			String errorMessage = Resources.getString("server.messageRequestedAreaNotVisible");
			response2.setErrorHeader2(errorMessage);
			
			ServerUtilities.output(response2.getOutputStream(), errorMessage);
			
			listenerOnError(state.area, errorMessage);
			return true;
		}
		
		MiddleResult result;

		// Set current root area.
		middle.setCurrentRootArea(state.area);
		
		// Get version ID.
		try {
			String versionIdText = state.request.getParameter("ver_id");
			String versionAlias = state.request.getParameter("version");
			
			state.currentVersionId = 0L;
			
			if (versionIdText != null && !versionIdText.isEmpty()) {
				state.currentVersionId = Long.parseLong(versionIdText);
			}
			
			if (versionAlias != null && !versionAlias.isEmpty()) {
				Obj<VersionObj> version = new Obj<VersionObj>();
				
				result = state.middle.loadVersion(versionAlias, version);
				if (result.isNotOK()) {
					return false;
				}
				
				state.currentVersionId = version.ref.getId();
			}
		}
		catch (Exception e) {
		}
		
		// Load inherited start resource.
		Obj<StartResource> startResource = new Obj<StartResource>();
		
		result = middle.loadAreaInheritedStartResource(state.area, state.currentVersionId, startResource);
		if (result.isOK()) {
			
			this.state.startArea = startResource.ref.foundArea;
			
			// Get start resource ID.
			long startResourceId = startResource.ref.resourceId;
			
			// Set content type.
			response2.setContentType(startResource.ref.mimeType);
			
			// Set file extension.
			String fileExtension = state.area.getFileExtension();
			if (fileExtension.isEmpty()) {
				fileExtension = startResource.ref.mimeExtension;
			}
			response2.setContentExtension(fileExtension);
			
			// Callback method.
			if (startResource.ref.notLocalized) {
				response2.setOutputNotLocalized();
			}
			
			try {
				// Load languages.
				loadLanguages();
				
				// Get output stream.
				OutputStream outputStream = response2.getOutputStream();

				// Get resource saving method.
				Obj<Boolean> savedAsText = new Obj<Boolean>();
				result = middle.loadResourceSavingMethod(startResourceId, savedAsText);
				if (result.isOK()) {
					
					if (savedAsText.ref) {
						
						// Set coding.
						response2.setCharacterEncoding("UTF-8");
						
						// Load from text.
						Obj<String> resourceText = new Obj<String>();
						result = middle.loadResourceTextToString(startResourceId, resourceText);
						if (result.isOK()) {
							
							// Initialize level number.
							state.level = 1L;
							
							// Add debug information about code source.
							if (isDebuggerEnabled()) {
								
								Resource resource = loadResource(startResourceId);
								String resourceDescription = resource.getDescription();
								
								Long areaId = state.startArea.getId();
								String areaName = state.startArea.getDescriptionForced();
								
								DebugSourceInfo sourceInfo = DebugSourceInfo.newResource(startResourceId, resourceDescription, areaId, areaName);
								DebugInfo.setDebugInfo(this, sourceInfo);
							}
						
							// Process page text for the area.
							state.text = new StringBuilder(resourceText.ref);
							try {
								
								// Initialize top level area server.
								initializeTopLevel();
								
								// Start response timeout.
								startResponseTimeout();
								
								// Process all found levels of tags.
								while (true) {
									
									// Reset position and continue with processing.
									state.position = 0;
									state.tagStartPosition = 0;
									
									// Process area page text with tags.
									processAreaServerTextAndTags();
									
									// Process book marks.
									processBookmarks();
									
									// Process "no indentation".
									processNoIndentation();
									
									// Pack text.
									packTextEx();
									
									// Check number of blocks.
									int blockCount = blocks.getCount();
									if (blockCount != 1) {
										throwError("server.messageBlocksError", blockCount);
									}
									
									// Decrement tags level.
									boolean decremented = decrementTagsLevel();
									
									// Post process text.
									postProcessAreaText();
									
									if (!decremented) {
										break;
									}
									
									// Shift level.
									state.level++;
								}
							}
							catch (Exception e) {

								response2.setErrorHeader2(e.getLocalizedMessage());
								insertError(e, state.level);
								listenerOnError(state.area, e.getMessage());
								// Pack text.
								packTextEx();
							}
							
							// Replace new line tags
							String textString = replaceNewLineTags(state.text);
							
							// Do post processing
							textString = response2.postProcessText(textString);

							// Debugger final entry point.
							DebugInfo.setFinalDebugInfo(this);
							DebugInfo.finalDebugPoint(this);
							// After the final break point, close the debugger.
							closeDebugger();
							
							// Try to execute Area Server API operation.
							if (executeApiOperation(processResponse, ApiCallType.apiCallForAreaServerResult, textString)) {
								
								// On error
								// Finalize page loading.
								finalizeAreaPageLoading(result, response2);
								
								return processResponse.ref;
							}
							
							// Write text result.
							writeOutput(response2, textString);
						}
					}
					else {
						// Load from BLOB.
						result = middle.loadResourceBlobToStream(startResourceId, outputStream);
					}
				}
			}
			catch (Exception e) {
				result = new MiddleResult(null, e.getMessage());
			}
		}
		
		// Finalize page loading
		finalizeAreaPageLoading(result, response2);
		return true;
	}

	/**
	 * Close Area Server debugger if it is open.
	 */
	private void closeDebugger() {
		
		try {
			DebugInfo debugInfo = state.debugInfo;
			if (debugInfo == null) {
				return;
			}
			
			XdebugClient client = debugInfo.getDebugClient();
			if (client == null) {
				return;
			}
			
			boolean isConnected = client.isConnected();
			if (isConnected) {
				client.close();
			}
			
			debugInfo.setDebugClient(null);
		}
		catch (Exception e) {
            e.printStackTrace();
        }
	}

	/**
	 * Finalize Area Server page loading
	 * @param areaServerResult
	 * @param response
	 */
	private void finalizeAreaPageLoading(MiddleResult areaServerResult, Response response) {
		
		// Finalize response headers
		response.finalizeHeaders();
		
		// Process error.
		if (areaServerResult.isNotOK()) {
			
			if (!state.rendering) {
				response.setErrorHeader2(areaServerResult.getMessage());
				response.finalizeHeaders();
				ServerUtilities.output(response.getOutputStream(), 
						areaServerResult.getMessage());
			}
			else {
				// If rendering, omit start resource not found error.
				if (areaServerResult == MiddleResult.RESOURCE_AREA_START_NOT_SPECIFIED) {
					areaServerResult = MiddleResult.OK;
				}
			}
			
			if (areaServerResult.isNotOK()) {
				listenerOnError(state.area, areaServerResult.getMessage());
			}
		}
		
		// Invoke final listeners.
		invokeFinalListeners();
		
		// Notify scripting engine pool that this engine was signalReleased.
		synchronized (javaScriptEnginePool) {
			
			state.scriptingEngine.setUsed(false);
			ScriptingEngine.signalReleased = true;
			try {
				javaScriptEnginePool.notify();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Replace new line tags
	 * @param text
	 * @return
	 */
	private String replaceNewLineTags(StringBuilder text) {
		
		String textString = text.toString();
		
		textString = textString.replaceAll(newLineTag + "\r?\n", this.state.newLine + this.state.newLine);
		
		textString = textString.replaceAll(newLineTag, this.state.newLine);
		
		return textString;
	}

	/**
	 * Initialize top level objects.
	 */
	private void initializeTopLevel()
		throws Exception {
		
		// Prepare JavaScript prerequisites.
		putFunctionsIntoJavaScriptEngine(this);
		
		// Create empty list of updated slots.
		this.state.updatedSlots = new LinkedList<Long>();
		
		// Reset debugger flag.
		JavaScriptDebugger.continueit = false;
		
		// Update external providers change flags.
		updateExternalProvidersChange();
	}
	
	/**
	 * Update external change flags.
	 */
	private void updateExternalProvidersChange() {
		
		LinkedList<Long> notUsedSlots = new LinkedList<Long>();
				
		// Update flags.
		for (Long slotId : ProviderWatchService.affectedSlots) {
			
			if (slotId == null) {
				continue;
			}
			
			// Update flag.
			MiddleResult result = state.middle.updateSlotExternalChange(slotId, true);
			if (result.isNotOK()) {
				notUsedSlots.add(slotId);
			}
		}
		
		// Clear unused slots.
		ProviderWatchService.affectedSlots.removeAll(notUsedSlots);
	}

	/**
	 * Invoke final callbacks.
	 */
	private void invokeFinalListeners() {
		
		LinkedList<Long> slotIds = state.updatedSlots;
			
		// If any slots were updated, inform client.
		if (slotIds != null && !slotIds.isEmpty()) {
			state.listener.updatedSlots(slotIds);
		}
	}

	/**
	 * Try to execute area server operation.
	 * @param processResponse - is true if the response needs further processing 
	 * @param areaServerResult 
	 * @param apiCallType 
	 * @return true if the area server operation exists
	 */
	private boolean executeApiOperation(Obj<Boolean> processResponse, ApiCallType apiCallType, String areaServerResult) {
		
		// Get request
		HttpServletRequest request = this.state.request.getOriginalRequest();
		if (request == null) {
			return false;
		}
		
		// Try to get the requested operation
		String operation = request.getHeader("AreaServer-API");
		if (operation == null) {
			return false;
		}
		
		// Check if the operation is permitted
		if (!isPermittedOperation(operation, apiCallType)) {
			return false;
		}
		
		// Try to get request user
		String user = request.getHeader("AreaServer-User");
		if (user == null) {
			return false;
		}
		
		// Try to get request password
		String password = request.getHeader("AreaServer-Password");
		if (password == null) {
			return false;
		}
		
		// Check the password. MAKE: check password for user, not for Area Server
		if (!checkUserAndPassword(user, password)) {
			this.state.response.badPassword();
			return true;
		}
		
		// Call API operation.
		boolean isResponse = false;
		try {
			java.lang.reflect.Method method = null;
			if (ApiCallType.apiCall.equals(apiCallType)) {
				method = getClass().getMethod(operation);
				isResponse = (Boolean) method.invoke(this);
			}
			else if (ApiCallType.apiCallForAreaServerResult.equals(apiCallType)) {
				method = getClass().getMethod(operation, String.class);
				isResponse = (Boolean) method.invoke(this, areaServerResult);
			}
		}
		catch (Exception e) {
			this.state.response.setErrorHeader2(e.getMessage());
		}
		return isResponse;
	}
	
	/**
	 * Load trayMenu for Sync tray icon.
	 * @return
	 */
	
	public boolean loadMenu(String areaServerResultText) 
		throws Exception {
		
		final String encoding = "UTF-8";
		String xml = null;
		String errorMessage = null;
		
		// Open root tag.
		xml = String.format("<?xml version=\"1.0\" encoding=\"%s\"?>\n", encoding);
		xml += "<Result>\n";
		
		try {
			
			// If the Area Server result is bad, throw exception.
			if (state.exceptionThrown != null) {
				String messageText = state.exceptionThrown.getMessageText();
				Utility.throwException("org.maclan.server.messageErrorInMaclan", messageText);
			}
			
			// Insert menu item tags.
			for (TrayMenuResult.Item item : state.trayMenu.getItems()) {
				
				// Evaluate item statements.
				item.name = evaluateText(item.name, String.class, false);
				item.action = evaluateText(item.action, String.class, false);
				
				// Escape special characters for use in XML document.
				item.name = org.apache.commons.text.StringEscapeUtils.escapeXml10(item.name);
				item.action = org.apache.commons.text.StringEscapeUtils.escapeXml10(item.action);
				
				xml += String.format("<MenuItem name='%s'>%s</MenuItem>\n", item.name, item.action);
			}
		}
		catch (Exception e) {
			
			// Process exception.
			errorMessage = Resources.getString("org.maclan.server.messageErrorWhenLoadingMenu");
			errorMessage = String.format("%s %s", errorMessage, e.getLocalizedMessage());
		}
		
		// Process error message.
		if (errorMessage != null) {
			this.state.response.setErrorHeader2(errorMessage);
			errorMessage = org.apache.commons.text.StringEscapeUtils.escapeXml10(errorMessage);
			areaServerResultText = org.apache.commons.text.StringEscapeUtils.escapeXml10(areaServerResultText);
			xml += String.format("<MenuException>%s\n\n%s</MenuException>\n", errorMessage, areaServerResultText);
			
		}
		
		// Close the root tag.
		xml += "</Result>";
		
		// Write the response
		try {
			xml = xml.replaceAll("&", "&amp;");
			this.state.response.writeText(xml, encoding);
		}
		catch (Exception e) {
			throw e;
		}
		
		return true;
	}
	
	/**
	 * Checks if operation is permitted.
	 * @param operation
	 * @param apiCallType 
	 * @return
	 */
	private boolean isPermittedOperation(String operation, ApiCallType apiCallType) {
		
		if (operation == null) {
			return false;
		}
		
		// Call type is first item of each sub array.
		final Object [][] permittedOperations = {
				{
				ApiCallType.apiCall
				}
				,
				{
				ApiCallType.apiCallForAreaServerResult,
				"loadMenu"
				}};

		
		for (Object [] set : permittedOperations) {
			
			if (!apiCallType.equals(set[0])) {
				continue;
			}
			int count = set.length;
			for (int index = 1; index < count; index++) {
				
				if (operation.contentEquals((String) set[index])) {
					return true;
				}
			}
		}
		
		return false;
	}

	/**
	 * Checks area server password.
	 * @param user
	 * @param password 
	 * @return true if the password is OK
	 */
	public boolean checkUserAndPassword(String user, String password) {
		
		if (user == null || password == null) {
			return false;
		}
		
		MiddleResult result = state.middle.checkLogin(user, password);
		boolean success = result.isOK();
		return success;
	}
	
	/**
	 * Process bookmarks.
	 */
	private void processBookmarks() {
		
		// Do loop for all bookmarks.
		for (String bookmarkName : state.bookmarkReplacement.keySet()) {
			
			String value = state.bookmarkReplacement.get(bookmarkName);
			if (value == null) {
				value = "";
			}
			
			// Replace all bookmarks.
			String bookmark = String.format("[@@BOOKMARK %s]", bookmarkName);
			replaceString(state.text, bookmark, value);
		}
	}

	/**
	 * Process "no indentation".
	 * @param text 
	 */
	private void processNoIndentation()
		throws Exception {
		
		// Repeat indentation removal.
		while (noIndentation());
	}
	
	/**
	 * Remove indentation.
	 * @param text
	 * @return
	 */
	private boolean noIndentation()
		throws Exception {
		
		// Prepare regular expression pattern.
		final Pattern pattern = Pattern.compile("^\\s*\\[noindent\uE000 ([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})\\](.*)\\[/noindent\uE000 \\1\\]",
				Pattern.DOTALL | Pattern.MULTILINE);
		
		Matcher matcher = pattern.matcher(state.text);
		
		// Try to find a block where an indentation should be removed and remove it.
		if (matcher.find()) {
			
			String innerText = matcher.group(2);
			int start = matcher.start();
			int end = matcher.end();
			
			innerText = removeIndentation(innerText);
			
			state.text.replace(start, end, innerText);
			
			return true;
		}
		
		return false;
	}

	/**
	 * Remove text indentation.
	 * @param text
	 * @return
	 */
	public static String removeIndentation(String text)
		throws Exception {
		
		// Compile regular expression patterns.
		final Pattern regexEmptyLine = Pattern.compile("\\s*");
		final Pattern regexStartingWhitespaces = Pattern.compile("^(\\s*)");
		
		Obj<Integer> position = new Obj<Integer>(0);
		StringBuilder lineText = new StringBuilder();
		
		boolean isFirstNonEmpty = true;
		String startingWhiteSpaces = "";
		
		String outputText = "";
		
		while (getTextLine(text, lineText, position)) {
			
			// If the line is empty, skip it.
			Matcher emptyLineMatcher = regexEmptyLine.matcher(lineText);
			if (emptyLineMatcher.matches()) {
				
				continue;
			}
			
			// Get starting whites paces of first nonempty line.
			if (isFirstNonEmpty) {
				
				Matcher startingWhitespacesMatcher = regexStartingWhitespaces.matcher(lineText);
				if (!startingWhitespacesMatcher.find()) {
					
					throwError("server.messageNoIndentationError");
				}
				
				startingWhiteSpaces = startingWhitespacesMatcher.group(1);
			}
			
			String lineString = lineText.toString();
			
			// Remove matching white spaces at start of the line and add the line 
			// to the output text.
			if (!startingWhiteSpaces.isEmpty()) {
				lineString = lineString.replaceFirst("^" + startingWhiteSpaces, "");
			}
			
			isFirstNonEmpty = false;
			
			outputText += lineString + '\n';
		}
		
		return outputText;
	}

	/**
	* Utility method to replace the string from StringBuilder.
	* @param sb          the StringBuilder object.
	* @param toReplace   the String that should be replaced.
	* @param replacement the String that has to be replaced by.
	* 
	*/
	public static void replaceString(StringBuilder sb,
	                                 String toReplace,
	                                 String replacement) { 
		
	    int index = -1;
	    while ((index = sb.lastIndexOf(toReplace)) != -1) {
	        sb.replace(index, index + toReplace.length(), replacement);
	    }
	}

	/**
	 * Write output. (Try to set PHP scripting engine flag.)
	 * @param response
	 * @param textString
	 */
	private void writeOutput(Response response, String textString)
		throws Exception {

		OutputStream outputStream = response.getOutputStream();
		
		// Try to create writer.
		Writer writer = response.getWriter(outputStream);
		
		if (writer == null) {
			try {
				writer = new PrintWriter(
						new OutputStreamWriter(outputStream, "UTF-8"), true);
			}
			catch (Exception e) {

				throwError("server.messageCannotCreateResultWriter",
						e.getMessage());
			}
		}

		boolean isWritten = false;
		
		// Check if PHP command exists and is enabled.
		if (isPhpEnabled() && phpCommandExists(textString)) {
			
			response.setPhpCommandExists(true);
		}
		
		// If not written, write it.
		if (!isWritten) {
			try {
				writer.write(textString);
			}
			catch (IOException e) {
				try { writer.close(); } catch (IOException e2) { }
				throwError("server.messageCannotWriteServerResult",
						e.getMessage());
			}
		}

		// Close writer.
		try {
			writer.close();
		}
		catch (IOException e) {
		}
	}
	
	/**
	 * Returns true value if there is a PHP command in the input string.
	 * @param textString
	 * @return
	 */
	private boolean phpCommandExists(String textString) {
		
		Pattern pattern = Pattern.compile("<?\\s*php");
		Matcher matcher = pattern.matcher(textString);
		
		return matcher.find();
	}

	/**
	 * On error.
	 * @param source
	 * @param message
	 */
	private void listenerOnError(Object source, String message) {
		
		if (state.listener != null) {
			
			String messageStart = null;
			
			if (source instanceof Area) {
				messageStart = String.format(Resources.getString("server.textAreaError"),
						state.area.getDescriptionForced(true));
			}
			else {
				messageStart = Resources.getString("server.textError2");
			}
			state.listener.onError(messageStart + " " + message);
		}
	}

	/**
	 * Post process text.
	 */
	private void postProcessAreaText() {
		
		String textAux = state.text.toString();
		textAux = textAux.replaceAll(leftBracketTag, "[");
		textAux = textAux.replaceAll(rightBracketTag, "]");
		textAux = textAux.replaceAll(leftHtmlBracketTag, "<");
		textAux = textAux.replaceAll(rightHtmlBracketTag, ">");
		textAux = textAux.replaceAll(atTag, "@");
		
		state.text.replace(0, state.text.length(), textAux);
	}

	/**
	 * Pack text.
	 * @param text
	 */
	private static String packText(String text) {
		
		final int START = 1;
		final int NEXT = 2;

		StringBuilder resultText = new StringBuilder();
		int index = 0;
		int state = START;
		
		while (index < text.length()) {
			
			char character = text.charAt(index);
			boolean isWhitespace = (character == '\r' || character == '\n'
				|| character == ' ' || character == '\t');
				
			switch (state) {
			case START:
				if (isWhitespace) {
					state = NEXT;
					resultText.append(' ');
				}
				else {
					resultText.append(character);
				}
				break;
				
			case NEXT:
				if (isWhitespace) {
					break;
				}
				resultText.append(character);
				state = START;
				break;
			}
			
			index++;
		}
		
		return resultText.toString();
	}

	/**
	 * Pack text strongly.
	 * @param text
	 */
	private static String packTextStrong(String text) {

		StringBuilder resultText = new StringBuilder();
		int index = 0;
		
		while (index < text.length()) {
			
			char character = text.charAt(index);
			boolean isWhitespace = (character == '\r' || character == '\n'
				|| character == ' ' || character == '\t');
				
			if (!isWhitespace) {
				resultText.append(character);
			}
			
			index++;
		}
		
		return resultText.toString();
	}
	
	/**
	 * Pack text.
	 */
	private void packTextEx() {

		StringBuilder resultText = new StringBuilder();
		StringBuilder lineText = new StringBuilder();
		
		Obj<Integer> position = new Obj<Integer>(0);
		
		// Do loop for all text lines.
		boolean exit = false;
		while (!exit) {
            
			exit = !getTextLine(state.text, lineText, position);
			
			String line = lineText.toString();
			if (line.matches("\\s*")) {
				continue;
			}
			
			resultText.append(line);
			if (exit) {
				break;
			}
			resultText.append("\n");
		}
		
		state.text = resultText;
	}

	/**
	 * Get line of text.
	 * @param lineText
	 * @param position
	 * @return
	 */
	private static boolean getTextLine(StringBuilder text, StringBuilder lineText, Obj<Integer> position) {
		
		lineText.delete(0, lineText.length());
		boolean expectingNewLine = false;
		
		while (position.ref < text.length()) {
			
			char character = text.charAt(position.ref);
			if (character == '\n') {
				position.ref++;
				break;
			}
			if (expectingNewLine) {
				break;
			}
			expectingNewLine = false;
			
			if (character == '\r') {
				expectingNewLine = true;
				position.ref++;
				continue;
			}
			
			lineText.append(character);
			position.ref++;
		}

		return position.ref < text.length();
	}
	

	/**
	 * Get line of text.
	 * @param lineText
	 * @param position
	 * @return
	 */
	private static boolean getTextLine(String text, StringBuilder lineText, Obj<Integer> position) {
		
		int textLength = text.length();
		
		if (position.ref >= textLength) {
			return false;
		}
		
		lineText.delete(0, lineText.length());
		boolean expectingNewLine = false;
		
		while (position.ref < text.length()) {
			
			char character = text.charAt(position.ref);
			if (character == '\n') {
				position.ref++;
				break;
			}
			if (expectingNewLine) {
				break;
			}
			expectingNewLine = false;
			
			if (character == '\r') {
				expectingNewLine = true;
				position.ref++;
				continue;
			}
			
			lineText.append(character);
			position.ref++;
		}
		
		return position.ref <= textLength;
	}

	/**
	 * Load languages.
	 */
	private void loadLanguages() throws Exception {

		// Load languages.
		MiddleResult result = state.middle.loadLanguagesNoFlags(state.languages);
		if (result.isNotOK()) {
			throwError("server.messageErrorLoadingLanguages");
		}
		
		// Get current language.
		for (Language language : state.languages) {
			if (language.id == state.middle.getCurrentLanguageId()) {
				state.currentLanguage = language;
				break;
			}
		}
		
		if (state.currentLanguage == null) {
			throwError("server.messageCannotLoadCurrentLanguage");
		}
	}
	
	/**
	 * Returns true value if an exception was thrown.
	 * @return
	 */
	private boolean isThrownException() {
		
		// Check state.
		if (state == null) {
			return false;
		}
		
		// Check if an exception exists.
		boolean isException = state.exceptionThrown != null;
		return isException;
	}
	
	/**
	 * Compile error message.
	 * @param exception
	 * @param level
	 * @return
	 */
	private String compileErrorMessage(Exception exception, String format) {
		
		return compileErrorMessage(exception, state.level, format);
	}
	
	/**
	 * Compile error message.
	 * @param exception
	 * @param level
	 * @return
	 */
	private String compileErrorMessage(Exception exception, long level, String format) {
		
		// Compile error message.
		String message = String.format(format,
				String.format(Resources.getString("server.textError"), level),
				exception.getLocalizedMessage());
		
		return message;
	}

	/**
	 * Insert exception.
	 * @param exception
	 */
	protected void insertError(Exception exception) {
		
		insertError(exception, state.level);
	}
	
	/**
	 * Insert exception.
	 * @param e
	 */
	private void insertErrorNoHtml(Exception e) {
		
		insertErrorNoHtml(e, state.level);
	}
	
	/**
	 * Insert exception.
	 * @param exception
	 * @param level 
	 */
	private void insertError(Exception exception, long level) {
		
		insertError(exception, level, errorFormatHtml);
	}
	
	/**
	 * Insert exception.
	 * @param e
	 * @param leve
	 */
	private void insertErrorNoHtml(Exception e, long level) {
		
		insertError(e, level, errorFormat);
	}
	
	/**
	 * Insert exception.
	 * @param exception
	 * @param level 
	 */
	private void insertError(Exception exception, long level, String format) {
		
		// Compile error message.
		String message = compileErrorMessage(exception, level, format);
		
		// Ensure error visibility.
		int length = state.text.length();
		if (state.position < length) {
			// Insert error message.
			state.text.insert(state.position, message);
		}
		else {
			// Append error message.
			state.text.append(message);
		}
	}

	/**
	 * Throw error.
	 * @param resourceIdText
	 * @param parameters
	 */
	public static void throwError(String resourceIdText, Object ... parameters)
		throws Exception {

		throwErrorText(Resources.getString(resourceIdText), parameters);
	}

	/**
	 * Throw error text.
	 * @param text
	 */
	private static void throwErrorText(String text, Object ... parameters)
		throws Exception {

		throw new Exception(String.format(text, parameters));
	}

	/**
	 * Create area from ID.
	 * @param areaId
	 * @return
	 * @throws Exception
	 */
	public Area getArea(Long areaId)
		throws Exception {
		
		if (areaId == null) {
			throwError("server.messageAreaFromIdNullParam");
		}
		
		Obj<Area> area = new Obj<Area>();
		
		MiddleResult result;
		
		// Load area.
		result = state.middle.loadArea(areaId, area);
		if (result.isNotOK()) {
			throwError("server.messageCannotLoadAreaUiData", result.getMessage());
		}

		return area.ref;
	}

	/**
	 * Load sub areas data.
	 * @param area
	 */
	public void loadSubAreasData(Area area)
		throws Exception {
		
		MiddleResult result;
		
		// Load area.
		result = state.middle.loadSubAreasData(area);
		if (result.isNotOK()) {
			throwError("server.messageCannotLoadSubAreaUisData", result.getMessage());
		}
	}
	
	/**
	 * Load super areas data.
	 * @param area
	 * @throws Exception
	 */
	public void loadSuperAreasData(Area area)
		throws Exception {
			
		MiddleResult result;
		
		// Load area.
		result = state.middle.loadSuperAreasData(area);
		if (result.isNotOK()) {
			throwError("server.messageCannotLoadSuperAreaUisData", result.getMessage());
		}
	}

	/**
	 * 
	 * @param alias
	 * @return
	 */
	public Area getArea(String alias)
		throws Exception {

		Obj<Area> outputArea = new Obj<Area>();
		// Load area ID for given area alias.
		MiddleResult result = state.middle.loadAreaWithAlias(alias, outputArea);
		if (result.isNotOK()) {
			throwError("server.messageCannotLoadAreaWithAlias", alias);
		}
		
		return outputArea.ref;
	}

	/**
	 * Process area server text.
	 * @param text
	 * @param position
	 * @param areaProperty
	 */
	private void processAreaServerTextAndTags()
		throws Exception {

		state.analysis.process_area_calls++;
		
		// Find tag start.
		while (true) {
			
			// If an exception has been thrown, exit the loop.
			if (state.exceptionThrown != null) {
				break;
			}	
			
			// Find tag start.
			Obj<Integer> positionOut = new Obj<Integer>(state.position);
			Obj<Integer> tagStartPositionOut = new Obj<Integer>(state.tagStartPosition);
			if (!ServerUtilities.findTagStart(state.text, positionOut, tagStartPositionOut)) {
				
				// End of processing.
				break;
			}
			state.position = positionOut.ref;
			state.tagStartPosition = tagStartPositionOut.ref;
			
			// Check response timeout.
			checkResponseTimeout();

			// Read tag name.
			positionOut.ref = state.position;
			String tagName = ServerUtilities.readTagName(state.text, positionOut);
			state.position = positionOut.ref;
			
			// Try to process single tags.
			if (processSimpleSingleTags(tagName)) {
				continue;
			}
			// Try to process single tags.
			if (processComplexSingleTags(tagName)) {
				continue;
			}
			// Try to process full tags.
			if (processFullTags(tagName)) {
				continue;
			}
			// Try to process IF THEN tags.
			if (processIfThenTags(tagName)) {
				continue;
			}			
			// Try to process call tags. (Must be the last tag processor.)
			if (!processCallTags(tagName)) {
				// On error throw exception.
				throwError("server.messageUnknownTagName", tagName);
			}
		}
	}


	/**
	 * Decrement tags level.
	 * @return
	 */
	private boolean decrementTagsLevel() throws Exception {
		
		boolean decremented = false;
		
		// Decrement start tags level.
		int position = 0;
		while (true) {
			
			// Find tag start.
			Obj<Integer> positionOut = new Obj<Integer>(position);
			Obj<Integer> tagStartPositionOut = new Obj<Integer>();
			
			// Find next level tag.
			if (!ServerUtilities.findTagStartNextLevel(state.text, positionOut, tagStartPositionOut)) {
				break;
			}
			
			// Read tag name.
			int tagNameStart = positionOut.ref;
			String tagName = ServerUtilities.readTagName(state.text, positionOut);
			
			// Decrement tag name except META tag.
			if (!tagName.isEmpty() && !(!state.processMetaTags && "@META".equals(tagName))) {
				
				char firstCharacter = tagName.charAt(0);
				if (firstCharacter == '@') {
					
					tagName = tagName.substring(1);
					state.text.replace(tagNameStart, positionOut.ref, tagName);
					
					positionOut.ref--;
				}
				decremented = true;
			}
			
			// Parse properties.
			TagProperties properties = new TagProperties();
			Obj<Integer> endPosition = new Obj<Integer>(positionOut.ref);
			MiddleResult result = ServerUtilities.parseTagProperties(state.text, endPosition, properties, true);
			result.throwPossibleException();
			
			position = endPosition.ref;
		}
		
		// Decrement end tags level.
		position = 0;
		while (true) {
			
			// Find tag start.
			Obj<Integer> positionOut = new Obj<Integer>(position);
			Obj<Integer> tagStartPositionOut = new Obj<Integer>();
			
			// Find next level tag.
			if (!ServerUtilities.findTagEndNextLevel(state.text, positionOut, tagStartPositionOut)) {
				break;
			}
			
			// Read tag name.
			int tagNameStart = positionOut.ref;
			String tagName = ServerUtilities.readTagName(state.text, positionOut);
			
			// Decrement tag name except META tag.
			if (!tagName.isEmpty() && !(!state.processMetaTags && "@META".equals(tagName))) {
				
				char firstCharacter = tagName.charAt(0);
				if (firstCharacter == '@') {
					
					tagName = tagName.substring(1);
					state.text.replace(tagNameStart, positionOut.ref, tagName);
					
					positionOut.ref--;
				}
				decremented = true;
			}
			
			position = positionOut.ref;
		}
		
		return decremented;
	}

	/**
	 * Start watch dog.
	 */
	private void startResponseTimeout() {
		
		state.responseStartTime = System.currentTimeMillis();
	}
	
	/**
	 * Check response timeout.
	 */
	private void checkResponseTimeout() throws Exception {
		
		// Do not check timeout when development mode is on.
		boolean developmentMode = isDevelopmentMode();
		if (developmentMode) {
			return;
		}

		long deltaMilliseconds = System.currentTimeMillis() - state.responseStartTime;
		
		if (deltaMilliseconds >= state.responseTimeoutMilliseconds) {
			throwError("server.messageResponseTimeExceedsTimout", state.responseTimeoutMilliseconds);
		}
	}

	/**
	 * Returns remaining time out.
	 * @return
	 */
	protected long getRemainingTimeout() {
		
		long terminationTime = state.responseStartTime + state.responseTimeoutMilliseconds;
		long remainingMilliseconds = terminationTime - System.currentTimeMillis();
		
		if (remainingMilliseconds < 0) {
			remainingMilliseconds = 0;
		}
		
		return remainingMilliseconds;
	}
	
	/**
	 * Strip meta information.
	 * @param string
	 * @return
	 */
	private String stripMetaInformation(String string)
			throws Exception {
		
		// If the PRAGMA "metaInfo" property is set to false and meta tags are not enabled, return the result without changes.
		if (state.enableMetaTags == AreaServerState.metaInfoFalse && !ProgramServlet.areMetaTagsEnabled()) {
			return string;
		}
		
		StringBuilder text = new StringBuilder(string);
		
		// META tag starts.
		int position = 0;
		while (true) {
			
			// Find tag start.
			Obj<Integer> positionOut = new Obj<Integer>(position);
			Obj<Integer> tagStartPositionOut = new Obj<Integer>();
			
			// Find next tag.
			if (!ServerUtilities.findTagStartNextLevel(text, positionOut, tagStartPositionOut)) {
				break;
			}
			
			// Read tag name.
			String tagName = ServerUtilities.readTagName(text, positionOut);
			
			// Remove META tag.
			if ("@META".equals(tagName)) {
				
				// Parse properties.
				TagProperties properties = new TagProperties();
				Obj<Integer> endPosition = new Obj<Integer>(positionOut.ref);
				MiddleResult result = ServerUtilities.parseTagProperties(text, endPosition, properties, true);
				result.throwPossibleException();
				
				text.replace(tagStartPositionOut.ref, endPosition.ref, "");
				
				position = tagStartPositionOut.ref;
			}
		}
		
		// META tag ends.
		position = 0;
		while (true) {
			
			// Find tag start.
			Obj<Integer> positionOut = new Obj<Integer>(position);
			Obj<Integer> tagStartPositionOut = new Obj<Integer>();
			
			// Find next tag.
			if (!ServerUtilities.findTagEndNextLevel(text, positionOut, tagStartPositionOut)) {
				break;
			}
			
			// Read tag name.
			String tagName = ServerUtilities.readTagName(text, positionOut);
			
			// Remove META tag.
			if ("@META".equals(tagName)) {
				
				text.replace(tagStartPositionOut.ref, positionOut.ref + 1, "");
				positionOut.ref = tagStartPositionOut.ref;
			}
		}
		
		return text.toString();
	}

	/**
	 * Process area single tags.
	 * @param tagName 
	 * @param areaProperty
	 * @return
	 */
	private boolean processSimpleSingleTags(String tagName)
		throws Exception {
				
		// Get processor.
		SimpleSingleTagProcessor processor = simpleSingleTagProcessors.get(tagName);
		if (processor == null) {
			return false;
		}
		
		Obj<Integer> positionOut = new Obj<Integer>(state.position);
		ServerUtilities.findTagEnd(state.text, positionOut);
		state.position = positionOut.ref;
		
		// Add debug information about current tag.
		DebugInfo.setDebugInfo(this, tagName, null, state.tagStartPosition, state.position, null);
		// Debug point for simple tags.
		DebugInfo.debugPoint(this);
		
		// Call processor.
		String replace = processor.processTag(this);
		
		// Use replace text.
		state.text.replace(state.tagStartPosition, state.position, replace);
		state.position = state.tagStartPosition;
		return true;
	}

	/**
	 * Process single tags.
	 * @param tagName
	 * @return
	 */
	private boolean processComplexSingleTags(String tagName)
		throws Exception {
		
		// Get processor.
		ComplexSingleTagProcessor processor = complexSingleTagProcessors.get(tagName);
		if (processor == null) {
			return false;
		}
		
		// Get properties.
		Obj<Integer> startTagPosition = new Obj<Integer>(state.position);
		TagProperties properties = new TagProperties();
		
		MiddleResult result = ServerUtilities.parseTagProperties(state.text, startTagPosition,
				properties, true);
		if (result.isNotOK()) {
			throwError("server.messageTagParseError", result.getMessage());
		}
		
		// Process property texts.
		processPropertyTexts(properties);

		state.position = startTagPosition.ref;
		
		// Get debug flag.
		boolean isDebugged = isDebuggerEnabled();
		if (isDebugged) {
			// On BREAK tag try to connect to debugger with Xdebug protocol.
			if ("BREAK".equals(tagName)) {
			
				// Connect debugger.
				Obj<String> ideHost = new Obj<String>();
				Obj<Integer> xdebugPort = new Obj<Integer>();
				boolean debugProperties = state.listener.getXdebugHostPort(ideHost, xdebugPort);
				if (debugProperties) {
					
					XdebugClient debugClient = state.getDebugClient();
					
					// Connect to debugger with Xdebug protocol.
					if (debugClient == null) {
						debugClient = DebugInfo.connectXdebug(this, ideHost.ref, xdebugPort.ref);
					}
					
					// Add debug information about Xdebug protocol client.
					DebugInfo.setBreakDebugInfo(this, debugClient);
				}					
			}			
		}
		
		// Add debug information about current tag.
		DebugInfo.setDebugInfo(this, tagName, properties, state.tagStartPosition, state.position, null);
		// Debug point for complex single tags.
		DebugInfo.debugPoint(this);
		
		// Call tag processor.
		String replace = processor.processTag(this, properties);
		
		state.text.replace(state.tagStartPosition, state.position, replace);
		state.position = state.tagStartPosition;
		
		return true;
	}

	/**
	 * Process IF THEN tags.
	 * @param tagName
	 * @return
	 */
	private boolean processIfThenTags(String tagName)
		throws Exception {
		
		// Check tag name.
		if (!tagName.equals("IF")) {
			return false;
		}
		
		state.analysis.if_calls++;
		
		Obj<Boolean> transparentVariables = new Obj<Boolean>(false);
		Obj<Boolean> transparentProcedures = new Obj<Boolean>(false);

		// Create structured IF THEN command parser.
		IfThenStructuredParser parser = new IfThenStructuredParser(this, state.text, state.tagStartPosition) {
			@Override
			protected boolean resolveCondition(AreaServer server, TagProperties properties)
					throws Exception {
				
				// Process properties' texts.
				processPropertyTexts(properties);
				
				// Get flags used when releasing the block.
				transparentVariables.ref = evaluateProperty(properties, "transparentVar", Boolean.class, false, FLAG);
				transparentProcedures.ref = evaluateProperty(properties, "transparentProc", Boolean.class, false, FLAG);
				
				// Get condition value.
				Boolean condition = server.evaluateProperty(properties, "cond", Boolean.class, false, STRICT);
				return condition;
			}
		};
		
		StringBuilder resolvedText = new StringBuilder();
		
		// Parse structured IF command.
		try {
			parser.parseIfStructuredCommand(resolvedText);
		}
		catch (Exception e) {
			state.position = parser.position; // Because of placing error message.
			throw e;
		}
		
		// Get result text.
		String resultText = resolvedText.toString();
		
		// Remove whole IF command.
		state.text.delete(state.tagStartPosition, parser.getPosition());
		
		// Add block for variables and procedures.
		state.blocks.pushNewBlockDescriptor();
		resultText = processTextCloned(state.area, resultText);
		
		// Get flags used when releasing the block.
		state.blocks.popBlockDescriptor(transparentVariables.ref, transparentProcedures.ref);

		// Insert result text.
		state.text.insert(state.tagStartPosition, resultText);

		state.position = state.tagStartPosition;
		
		return true;
	}

	/**
	 * Process full tags.
	 * @param tagName
	 * @return
	 */
	private boolean processFullTags(String tagName) throws Exception {
		
		// Get processor.
		FullTagProcessor processor = fullTagProcessors.get(tagName);
		if (processor == null) {
			return false;
		}

		// Get properties.
		Obj<Integer> startPosition = new Obj<Integer>(state.position);
		TagProperties properties = new TagProperties();
		
		MiddleResult result = ServerUtilities.parseTagProperties(state.text, startPosition, properties);
		if (result.isNotOK()) {
			throwError("server.messageTagParseError", result.getMessage());
		}
		
		// Process property texts.
		processPropertyTexts(properties);

		// Shift position. Because of errors.
		state.position = startPosition.ref;
		
		StringBuilder innerText = new StringBuilder();
		
		// Call parser.
		FullTagParser parser = new FullTagParser(this, tagName, state.text, state.tagStartPosition);
		parser.parseFullCommand(innerText);
		
		int parserPosition = parser.getPosition();
		String innerTextString = innerText.toString();
		
		// Add debug information about current tag and replacement.
		DebugInfo.setDebugInfo(this, tagName, properties, state.tagStartPosition, parserPosition, innerTextString);
		// Debug point for full tags.
		DebugInfo.debugPoint(this);

		// Call processor.
		String replace = processor.processText(this, innerTextString, properties);
		state.position = parserPosition;
		
		// Replace statement with its result.
		state.text.replace(state.tagStartPosition, state.position, replace);
		state.position = state.tagStartPosition;
		return true;
	}
	
	/**
	 * LIST tag processor.
	 */
	private static void listProcessor() {
		
		fullTagProcessors.put("LIST", new FullTagProcessor() {
			@Override
			public String processText(AreaServer server, String innerText, TagProperties properties)
					throws Exception {
				
				server.state.analysis.list_calls++;
				
				// Get list variable name.
				String listName = properties.getProperty("list");
				if (listName == null) {
					throwError("server.messageExpectingUseProperty");
				}
				
				LinkedList<?> list = null;
				
				// Try to get list object.
				Object listObject = server.evaluateText(listName, null, false);
				if (!(listObject instanceof LinkedList<?>)) {
					
					// Try to get map and convert it to list.
					if (listObject instanceof Map) {
						
						Map<?, ?> map = (Map<?, ?>) listObject;
						list = new LinkedList<Entry<?, ?>>(map.entrySet());
					}
					else {
						throwError("server.messageExpectingListOrMapValue", listName);
					}
				}
				else {
					list = (LinkedList<?>) listObject;
				}
				
				// Try to get divider value.
				String dividerText = server.evaluateProperty(properties, "divider", String.class, "", DEFAULT);
				// Get iterator variable name.
				String iteratorVariableText = properties.getProperty("iterator");
				// Get item variable name.
				String itemVariableText = properties.getProperty("item");
				// Get break variable name.
				String breakVariableText = properties.getProperty("break");
				// Get discard variable name.
				String discardVariableText = properties.getProperty("discard");
				
				StringBuilder compiledText = new StringBuilder();
				
				// Push list into the stack.
				ListBlockDescriptor listDescriptor = new ListBlockDescriptor(0, list);
				server.state.blocks.pushBlockDescriptor(listDescriptor);
				
				// Create iterator variable..
				if (iteratorVariableText != null) {
					listDescriptor.createBlockVariable(iteratorVariableText, listDescriptor);
				}

				// Get the "local" flag value.
				boolean local = server.evaluateProperty(properties, "local", Boolean.class, false, FLAG);
				
				int count = list.size();

				// Do loop for all list items.
				for (int index = 1; index <= count; index++) {

					// Set index.
					listDescriptor.setIndex(index);
					
					// Create new block descriptor.
					BlockDescriptor blockDescriptor = server.state.blocks.pushNewBlockDescriptor();
					
					Object listItem = list.get(index - 1);
					
					// If item variable exists, create it.
					if (itemVariableText != null) {
						blockDescriptor.createBlockVariable(itemVariableText, listItem);
					}
					
					Variable breakVariable = null;
					Variable discardVariable = null;
					// Add break variable.
					if (breakVariableText != null) {
						breakVariable = blockDescriptor.createBlockVariable(breakVariableText, false);
						// Add discard variable.
						if (discardVariableText != null) {
							discardVariable = blockDescriptor.createBlockVariable(discardVariableText, false);
						}
					}
					
					// Process inner text.
					String itemInnerText;
					if (local && listItem instanceof Area) {
						Area localArea = (Area) listItem;
						itemInnerText = server.processTextCloned(localArea, innerText);
					}
					else {
						itemInnerText = server.processTextCloned(innerText);
					}
					
					// Get error flag.
					boolean error = server.isThrownException();
					
					// On break.
					boolean breakLoop = false;
					boolean discard = false;
					
					if (breakVariable != null && breakVariable.value instanceof Boolean) {
						breakLoop = (Boolean) breakVariable.value;
					}
					if (!breakLoop) {
						breakLoop = listDescriptor.isBreaked();
					}
					
					if (breakLoop) {
						if (discardVariable != null && discardVariable.value instanceof Boolean) {
							discard = (Boolean)discardVariable.value;
						}
						if (!discard) {
							discard = listDescriptor.getDiscard();
						}
					}

					// Remove block descriptor.
					server.state.blocks.popBlockDescriptor(false, false);

					// If not discard, append new text.
					if (!discard || error) {
						compiledText.append(itemInnerText);
						// Add divider.
						if (dividerText != null && index < count) {
							compiledText.append(dividerText);
						}
					}
					
					if (breakLoop || error) {
						break;
					}
				}
				
				// Get flags used when releasing the block.
				boolean transparentVariables = server.evaluateProperty(properties, "transparentVar", Boolean.class, false, FLAG);
				boolean transparentProcedures = server.evaluateProperty(properties, "transparentProc", Boolean.class, false, FLAG);
				
				// Pop list from the stack.
				server.state.blocks.popBlockDescriptor(transparentVariables, transparentProcedures);
				
				// Replacement text.
				String replacement = compiledText.toString();
				return replacement;
			}
		});
	}

	/**
	 * PROCEDURE tag processor.
	 */
	private static void procedureProcessor() {
		
		fullTagProcessors.put("PROCEDURE", new FullTagProcessor() {
			@Override
			public String processText(AreaServer server, String innerText, TagProperties properties)
					throws Exception {
				
				server.state.analysis.procedure_calls++;
				
				// Try to add new procedure.
				try {
					server.state.blocks.addProcedure(server, properties, innerText);
				}
				catch (Exception e) {
					throwError("server.messageProcedureDefinitionError", e.getLocalizedMessage());
				}
				return "";
			}
		});
	}
	
	/**
	 * Process @CALL tags.
	 * @param tagName
	 * @return
	 */
	private boolean processCallTags(String tagName) throws Exception {
		
		// Procedure name.
		String procedureName = null;
		
		// Check tag name.
		if (!tagName.equals("CALL")) {

			procedureName = tagName;
		}
		
		state.analysis.call_calls++;
		
		// Process the rest of tag.
		final TagProperties properties = new TagProperties();
		Obj<Integer> endPosition = new Obj<Integer>(state.position);
		
		MiddleResult result = ServerUtilities.parseTagProperties(state.text, endPosition, properties, true);
		if (result.isNotOK()) {
			throwError("server.messageTagParseError", result.getMessage());
		}
		
		// Process property texts.
		processPropertyTexts(properties);
		
		// Shift position because of error reporting.
		state.position = endPosition.ref;
		
		// Get procedure name from properties.
		if (procedureName == null) {
			
			// Try to get the procedure name.
			procedureName = properties.getProperty("$name");
			if (procedureName == null) {
				procedureName = properties.getProperty("name");
			}
			
			procedureName = evaluateExpression(procedureName, String.class, "", DEFAULT);
			
			if (procedureName.isEmpty()) {
				throwError("server.messageExpectingProcedureName");
			}
		}
		
		// Try to get area and slot.
		//
		// Get area from properties.
		final String propertyPrefix = "$";
		Obj<Boolean> existsAreaSpecification = new Obj<Boolean>(false);
		
		Area area = getAreaFromProperties(this, properties, propertyPrefix, existsAreaSpecification);

		// Get slot alias from properties.
		String slotAlias = evaluateProperty(properties, propertyPrefix + "slot", String.class, null, NULL);
		
		// If it is a slot calling.
		String processedSlotText = "";
		
		boolean procedureDefined = state.blocks.definedProcedure(procedureName);
		boolean slotAliasExists = slotAlias != null && !slotAlias.isEmpty();
		
		// Set slot alias to procedure name.
		if (!slotAliasExists && !procedureDefined) {
		
			slotAlias = procedureName;
			slotAliasExists = true;
		}
		
		// Get procedure.
		Procedure procedure = state.blocks.getProcedure(procedureName);
		
		// If slot alias exists.
		if (slotAliasExists) {
			
			// Push new block.
			state.blocks.pushNewBlockDescriptor();
			
			// Get parent flag.
			boolean parent = evaluateProperty(properties, "$parent", Boolean.class, false, FLAG);
			
			// Slot area.
			Area slotArea = existsAreaSpecification.ref ? area : state.area;
			
			// Get slot value.
			Slot slotObject = slot(slotAlias, slotArea, false, parent, null, true);
			if (slotObject != null) {
				
				Object value = slotObject.getValue();
				if (value != null) {
					
					String slotTextValue = value.toString();
					
					// Process slot text value.
					processedSlotText = processTextCloned(slotTextValue);
					if (state.exceptionThrown == null) {
						
						// Try to find procedure.
						procedure = state.blocks.getProcedure(procedureName);
					}
				}
			}
		}
		
		if (procedure == null) {
			throwError("server.messageProcedureDoesntExist", procedureName);
		}
		
		final AreaServer server = this;

		// Push procedure block.
		ProcedureBlockDescriptor block = new ProcedureBlockDescriptor(procedure,
				new ParameterInitializeListener() {
					@Override
					public Object getValue(String parameterName, ProcedureParameterType type,
							Obj<Variable> _outputVariable)
						throws Exception {
											
						String parameterText = properties.getProperty(parameterName);
						if (parameterText == null) {
							return null;
						}
						
						// If it is a output variable.
						if (type.isOutput()) {
							// Get output variable.
							Variable outputVariable = state.blocks.findVariable(parameterText);
							
							// If variable does't exist.
							if (outputVariable == null) {
								throwError("server.messageReferencedVariableDoesntExist",
										parameterText);
							}
							
							// Set variable.
							_outputVariable.ref = outputVariable;
							// Return variable value.
							return outputVariable.value;
						}
						
						// Evaluate expression.
						Object value = ExpressionSolver.evaluate(parameterText,
								new AreaIdentifierSolver(server), new AreaFunctionSolver(server));
						
						return value;
					}
		});
		state.blocks.pushBlockDescriptor(block);
		
		// If the procedure declares full calls, try to find inner text and end position of the call
		// and set new $inner variable to inner text of this CALL.
		if (procedure.isFullCall()) {
			
			// Call parser.
			FullTagParser parser = new FullTagParser(this, tagName, state.text, endPosition.ref, true);
			StringBuilder innerText = new StringBuilder();
			
			parser.parseFullCommand(innerText);
			endPosition.ref = parser.getPosition();
			
			// Set the $inner variable
			block.createBlockVariable("$inner", innerText.toString());
		}
		
		// Get inner text.
		String procedureInnerText = procedure.getInnerText();
		
		// Add debug information about current procedure call and about resulting replacement.
		DebugInfo.setDebugInfo(this, tagName, properties, state.tagStartPosition, state.position, procedureInnerText);
		// Debug point for procedure call tags.
		DebugInfo.debugPoint(this);
		
		// Process the inner text.
		String replaceText = processTextCloned(processedSlotText + procedureInnerText);
		
		// Get transparency of the blocks
		boolean transparent = procedure.isTransparent() || properties.containsKey("$transparent");
				
		// Pop procedure block descriptor.
		state.blocks.popBlockDescriptor(transparent);
		
		// If a slot is called, remove pushed block descriptor.
		if (slotAliasExists) {
			state.blocks.popBlockDescriptor(transparent);
		}
		
		// Remove tag.
		state.text.replace(state.tagStartPosition, endPosition.ref, replaceText);
		// Set position.
		state.position = state.tagStartPosition;
		
		return true;
	}
	
	/**
	 * LANGUAGES tag processor.
	 */
	private static void languagelistProcessor() {
		
		fullTagProcessors.put("LANGUAGES", new FullTagProcessor() {
			@Override
			public String processText(AreaServer server, String innerText, TagProperties properties)
					throws Exception {
				
				server.state.analysis.languages_call++;
				
				// Get divider.
				String divider = server.evaluateProperty(properties, "divider", String.class, "", DEFAULT);
				
				// Push languages list block descriptor.
				LanguagesBlockDescriptor descriptor = new LanguagesBlockDescriptor(server.state.languages);
				server.state.blocks.pushBlockDescriptor(descriptor);
				
				StringBuilder compiledText = new StringBuilder();
				int count = server.state.languages.size();
				
				// Do loop for all languages.
				for (int index = 0; index < count; index++) {
					descriptor.setIndex(index);
					
					Language language = server.state.languages.get(index);
					
					// Check if the language is rendered.
					if (server.state.listener != null) {
						if (!server.state.listener.isRendered(language.id)) {
							continue;
						}
					}
					
					String textItem = LanguageServer.processSingleSubTags(innerText, language,
							server.isRendering(), server, server.state.renderingFlags);
					
					// Set local current language.
					server.state.middle.setCurrentLanguageId(language.id);
					
					textItem = server.processTextCloned(textItem);
					compiledText.append(textItem);
					
					if (index > 0 && index < count - 1 && divider != null) {
						compiledText.append(divider);
					}
				}
				
				// Restore current language.
				server.state.middle.setCurrentLanguageId(server.state.currentLanguage.id);
				
				// Get flags used when releasing the block.
				boolean transparentVariables = server.evaluateProperty(properties, "transparentVar", Boolean.class, false, FLAG);
				boolean transparentProcedures = server.evaluateProperty(properties, "transparentProc", Boolean.class, false, FLAG);
				
				// Pop block descriptor.
				server.state.blocks.popBlockDescriptor(transparentVariables, transparentProcedures);
				
				String replacement = compiledText.toString();
				return replacement;
			}
		});
	}
	
	/**
	 * Get home area.
	 * @return
	 * @throws Exception 
	 */
	protected Area getHomeArea() throws Exception {
		
		Obj<Area> homeArea = new Obj<Area>();
		loadHomeAreaData(homeArea);

		return homeArea.ref;
	}

	/**
	 * If it is start language and the home area and rendering is in progress, return
	 * true value.
	 * @param areaId
	 * @param langId
	 * @param versionId 
	 * @param fileName 
	 * @return
	 * @throws Exception 
	 */
	protected boolean isIndexHtm(Long areaId, long langId, long versionId, String fileName)
			throws Exception {
		
		if (isRendering()) {
			Area homeArea = getHomeArea();
			
			if (fileName == null) {
				fileName = "";
			}
			
			if (areaId == homeArea.getId() || fileName.equalsIgnoreCase("index")) {
				
				long startLangId = getStartLanguageId();
				if (langId == startLangId) {
					
					return versionId == 0L;
				}
			}
		}
		
		return false;
	}

	/**
	 * Get start language ID.
	 * @return
	 * @throws Exception 
	 */
	public long getStartLanguageId() throws Exception {
		
		Obj<Long> startLanguageId = new Obj<Long>();
		
		MiddleResult result = state.middle.loadStartLanguageId(startLanguageId);
		if (result.isNotOK()) {
			throwError("server.messageCannotLoadStartLanguage");
		}
		
		return startLanguageId.ref;
	}

	/**
	 * Create areas tree.
	 * @param area
	 * @param relation 
	 * @return
	 */
	public StringBuilder createTree(Area area, String relation)
		throws Exception {
		
		StringBuilder text = new StringBuilder();
		text.append("{name:'");
		text.append(area.getDescriptionForced());
		text.append("',url:'");
		text.append(getAreaUrl(area.getId(), null));
		text.append("',subareas:[");
		
		// Call this method recursively.
		LinkedList<Area> areas = new LinkedList<Area>();
		loadSubAreasData(area);
		for (Area subarea : area.getSubareas()) {
			if (!subarea.isVisible()) {
				continue;
			}
			if (!relation.equals("undefined")) {
				if (area.getSubRelationName(subarea.getId()).equals(relation)) {
					areas.add(subarea);
				}
			}
			else {
				areas.add(subarea);
			}
		}
		for (Area subarea : areas) {

			text.append(createTree(subarea, relation));
			if (subarea != areas.getLast()) {
				text.append(",");
			}
		}
		
		text.append("]}");
		
		return text;
	}

	/**
	 * Create tree with IDs.
	 * @param area
	 * @param relation
	 * @return
	 */
	public StringBuilder createTreeIds(Area area, String relation)
		throws Exception {
		
		StringBuilder text = new StringBuilder();
		text.append("{name:'");
		text.append(area.getDescriptionForced());
		text.append("',id:");
		text.append(area.getId());
		text.append(",subareas:[");
		
		// Call this method recursively.
		LinkedList<Area> areas = new LinkedList<Area>();
		loadSubAreasData(area);
		for (Area subarea : area.getSubareas()) {
			if (!subarea.isVisible()) {
				continue;
			}
			if (!relation.equals("undefined")) {
				if (area.getSubRelationName(subarea.getId()).equals(relation)) {
					areas.add(subarea);
				}
			}
			else {
				areas.add(subarea);
			}
		}
		for (Area subarea : areas) {

			text.append(createTreeIds(subarea, relation));
			if (subarea != areas.getLast()) {
				text.append(",");
			}
		}
		
		text.append("]}");
		
		return text;
	}

	/**
	 * Get slot.
	 * @param slotAlias
	 * @param areaAlias
	 * @param skipDefault
	 * @param parent
	 * @param inheritanceLevel 
	 * @param loadValue 
	 * @return
	 * @throws Exception
	 */
	public Slot slot(String slotAlias, String areaAlias, boolean skipDefault, boolean parent, Long inheritanceLevel, boolean loadValue)
		throws Exception {
		
		Area area = getArea(areaAlias);
		return slot(slotAlias, area, skipDefault, parent, inheritanceLevel, loadValue);
	}

	/**
	 * Get slot.
	 * @param slotAlias
	 * @param areaId
	 * @param skipDefault
	 * @param parent
	 * @param inheritanceLevel
	 * @param loadValue 
	 * @return
	 * @throws Exception
	 */
	public Slot slot(String slotAlias, long areaId, boolean skipDefault, boolean parent, Long inheritanceLevel, boolean loadValue)
		throws Exception {
		
		Area area = getArea(areaId);
		return slot(slotAlias, area, skipDefault, parent, inheritanceLevel, loadValue);
	}

	/**
	 * Get slot.
	 * @param slotAlias
	 * @param skipDefault
	 * @param parent
	 * @param inheritanceLevel
	 * @param loadValue 
	 * @return
	 * @throws Exception
	 */
	public Slot slot(String slotAlias, boolean skipDefault, boolean parent, Long inheritanceLevel, boolean loadValue)
		throws Exception {
		
		return slot(slotAlias, state.area, skipDefault, parent, inheritanceLevel, loadValue);
	}

	/**
	 * Get slot.
	 * @param slotAlias
	 * @param area
	 * @param skipDefault
	 * @param parent
	 * @param inheritanceLevel
	 * @param loadValue 
	 * @return
	 */
	public Slot slot(String slotAlias, Area area, boolean skipDefault, boolean parent, Long inheritanceLevel, boolean loadValue)
		throws Exception {

		Obj<Slot> slot = new Obj<Slot>();
		
		MiddleResult result = state.middle.loadSlotInheritanceLevel(area, slotAlias, LoadSlotHint.area | LoadSlotHint.superAreas | LoadSlotHint.subAreas, skipDefault, parent, inheritanceLevel, slot, loadValue);
		if (result.isNotOK()) {
			throwError("server.messageSlotNotFoundOrNotInheritable");
		}
		
		if (slot.ref == null) {
			return null;
		}
		
		// Append possible localized text ID to slot text value.
		if (state.showLocalizedTextIds && slot.ref.isLocalized()) {
		
			String textId = getIdHtml("S", slot.ref.getId());
			slot.ref.insertTextValuePrefix(textId);
		}
		
		return slot.ref;
	}
	
	/**
	 * Get slot or its value.
	 * @param slotAlias
	 * @param area
	 * @param type - "d" skip default values, "v" return slot value, "i or iXXX" inherit (from XXX level), "p" only slots from parents
	 * @return
	 */
	public Object slot(String slotAlias, Area area, String type)
		throws Exception {
		
		boolean skipDefault = type.indexOf('d', 0) != -1;
		boolean getValue = type.indexOf('v', 0) != -1;
		boolean parent = type.indexOf('p', 0) != -1;
		
		Long inheritanceLevel = null;
		Matcher matcher = slotInheritancePattern.matcher(type);
		boolean found = matcher.find();
		
		if (found) {
			int groupCount = matcher.groupCount();
			if (groupCount == 1) {
				
				String levelText = matcher.group(1);
				if (!levelText.isEmpty()) {
					inheritanceLevel = Long.parseLong(levelText);
				}
			}
		}
		
		org.maclan.Slot middleSlot = slot(
				slotAlias,
				area,
				skipDefault, 
				parent, inheritanceLevel, getValue);
		
		if (middleSlot == null) {
			return null;
		}
		
		if (getValue) {
			return middleSlot.getValue();
		}
		
		return middleSlot;
	}

	/**
	 * Get slot value.
	 * @param slotAlias
	 * @param skipDefault
	 * @param parent
	 * @param inheritanceLevel
	 * @return
	 * @throws Exception
	 */
	public Object slotValue(String slotAlias, boolean skipDefault, boolean parent, Long inheritanceLevel)
			throws Exception {
		
		Slot slot = slot(slotAlias, skipDefault, parent, inheritanceLevel, true);
		if (slot == null) {
			return null;
		}
		
		return slot.getSimpleValue();
	}

	/**
	 * Get slot value.
	 * @param slotAlias
	 * @param areaAlias
	 * @param skipDefault
	 * @param parent
	 * @param inheritanceLevel
	 * @return
	 * @throws Exception
	 */
	public Object slotValue(String slotAlias, String areaAlias, boolean skipDefault, boolean parent, Long inheritanceLevel)
			throws Exception {

		Slot slot = slot(slotAlias, areaAlias, skipDefault, parent, inheritanceLevel, true);
		if (slot == null) {
			return null;
		}
		
		return slot.getSimpleValue();
	}

	/**
	 * Get slot value.
	 * @param slotAlias
	 * @param areaId
	 * @param skipDefault
	 * @param parent
	 * @param inheritanceLevel
	 * @return
	 * @throws Exception
	 */
	public Object slotValue(String slotAlias, long areaId, boolean skipDefault, boolean parent, Long inheritanceLevel)
			throws Exception {
		
		Slot slot = slot(slotAlias, areaId, skipDefault, parent, inheritanceLevel, true);
		if (slot == null) {
			return null;
		}
		
		return slot.getSimpleValue();
	}

	/**
	 * Get slot value.
	 * @param slotAlias
	 * @param area
	 * @param skipDefault
	 * @param parent
	 * @param inheritanceLevel
	 * @return
	 * @throws Exception
	 */
	public Object slotValue(String slotAlias, Area area, boolean skipDefault, boolean parent, Long inheritanceLevel)
			throws Exception {

		Slot slot = slot(slotAlias, area, skipDefault, parent, inheritanceLevel, true);
		if (slot == null) {
			return null;
		}
		
		return slot.getSimpleValue();
	}
	/**
	 * Get slot existence.
	 * @param slotAlias
	 * @param areaAlias
	 * @param skipDefault
	 * @param parent
	 * @return
	 * @throws Exception
	 */
	public Boolean slotDefined(String slotAlias, String areaAlias, boolean skipDefault, boolean parent)
		throws Exception {
		
		Area area = getArea(areaAlias);
		return slotDefined(slotAlias, area, skipDefault, parent);
	}

	/**
	 * Get slot existence.
	 * @param slotAlias
	 * @param areaId
	 * @param skipDefault
	 * @param parent
	 * @return
	 * @throws Exception
	 */
	public Boolean slotDefined(String slotAlias, long areaId, boolean skipDefault, boolean parent)
		throws Exception {
		
		Area area = getArea(areaId);
		return slotDefined(slotAlias, area, skipDefault, parent);
	}

	/**
	 * Get slot existence.
	 * @param slotAlias
	 * @param skipDefault
	 * @param parent
	 * @return
	 * @throws Exception
	 */
	public Boolean slotDefined(String slotAlias, boolean skipDefault, boolean parent)
		throws Exception {
		
		return slotDefined(slotAlias, state.area, skipDefault, parent);
	}

	/**
	 * Get slot existence.
	 * @param slotAlias
	 * @param area
	 * @param skipDefault
	 * @param parent
	 * @return
	 */
	public Boolean slotDefined(String slotAlias, Area area, boolean skipDefault, boolean parent)
		throws Exception {
		
		Obj<Slot> slot = new Obj<Slot>();
		MiddleResult result = state.middle.loadSlot(area, slotAlias, true, parent, skipDefault, slot, false);
		if (result.isNotOK()) {
			throw new Exception(result.getMessage());
		}

		return slot.ref != null;
	}
	
	/**
	 * Store slot value.
	 * @param slot
	 */
	protected void slotStore(Slot slot)
			throws Exception {
		
		MiddleLight middle = this.state.middle;
		
		// Store slot value.
		MiddleResult result = middle.updateSlotValue(slot);
		result.throwPossibleException();
	}
	
	/**
	 * Update area record.
	 * @param area
	 */
	protected void areaStore(Area area)
			throws Exception{
		
		MiddleLight middle = this.state.middle;
		
		// Store area record.
		MiddleResult result = middle.updateArea(area);
		result.throwPossibleException();
	}

	/**
	 * Gets true value if area inherits from inherited area.
	 * @param area
	 * @param inheritedArea
	 * @param level 
	 * @return
	 * @throws Exception
	 */
	public boolean inherits(Area area, Area inheritedArea, Long level)
		throws Exception {
		
		if (level != null && level == 0) {
			return false;
		}
		
		// Load area super areas.
		loadSuperAreasData(area);
		
		// If the area inherits directly from inheritedArea.
		if (area.inheritsFrom(inheritedArea)) {
			return true;
		}
		
		// Call this method recursively for inherited super areas.
		for (Area inheritedSuperArea : area.getInheritsFrom()) {
			
			if (inherits(inheritedSuperArea, inheritedArea,
					level != null ? level - 1 : null)) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Get resource.
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public AreaResource resource(String name)
		throws Exception {
		
		return resource(name, state.area);
	}

	/**
	 * Get resource.
	 * @param name
	 * @param alias
	 * @return
	 * @throws Exception
	 */
	public AreaResource resource(String name, String alias)
		throws Exception {
		
		Area areaAux = getArea(alias);
		return resource(name, areaAux);
	}

	/**
	 * Get resource.
	 * @param name
	 * @param areaId
	 * @return
	 * @throws Exception
	 */
	public AreaResource resource(String name, long areaId)
		throws Exception {
		
		Area areaAux = getArea(areaId);
		return resource(name, areaAux);
	}

	/**
	 * Get resource.
	 * @param name
	 * @param area
	 * @return
	 * @throws Exception
	 */
	public AreaResource resource(String name, Area area)
		throws Exception {

		Obj<AreaResource> resource = new Obj<AreaResource>();
		
		// Load resource. Resource is cached.
		MiddleResult result = state.middle.loadAreaResource(area.getId(), name,
				resource);
		if (result.isOK()) {

			return resource.ref;
		}
		
		// Throw error.
		throwError("server.messageCannotLoadAreaResource", area, name, result.getMessage());
		return null;
	}

	/**
	 * Returns resource object.
	 * @param resourceId
	 * @return
	 */
	public Resource resource(long resourceId)
		throws Exception {
		
		Obj<Resource> resource = new Obj<Resource>();
		
		// Load resource. Resource is cached.
		MiddleResult result = state.middle.loadResource(resourceId, resource);
		if (result.isOK()) {
			
			return resource.ref;
		}
		
		// Throw error.
		throwError("server.messageCannotLoadResource", resourceId, result.getMessage());
		return null;
	}
	
	/**
	 * Get resource length.
	 * @param resource
	 * @return
	 */
	public long getResourceLength(Resource resource) throws Exception {
		
		if (resource.getLength() == 0L) {
			
			Obj<Long> length = new Obj<Long>();
			state.middle.loadResourceDataLength(resource.id, length);
			
			resource.setLength(length.ref);
		}
		
		return resource.getLength();
	}

	/**
	 * Returns area resource.
	 * @param resourceId
	 * @param areaProperty
	 * @return
	 */
	private AreaResource resource(long resourceId, long areaId)
		throws Exception {
		
		Obj<AreaResource> resource = new Obj<AreaResource>();
		
		// Load resource. Resource is cached.
		MiddleResult result = state.middle.loadAreaResource(resourceId, areaId, resource);
		if (result.isOK()) {

			return resource.ref;
		}
		
		// Throw error.
		throwError("server.messageCannotLoadAreaResource2", areaId, resourceId, result.getMessage());
		return null;
	}

	/**
	 * Get MIME type.
	 * @param mimeTypeId
	 * @return
	 */
	public MimeType getMimeType(long mimeTypeId)
		throws Exception {
		
		Obj<MimeType> mimeType = new Obj<MimeType>();
		MiddleResult result = state.middle.loadMimeType(mimeTypeId, mimeType);
		if (result.isNotOK()) {
			AreaServer.throwErrorText(result.getMessage());
		}
		
		return mimeType.ref;
	}

	/**
	 * @return the rendering
	 */
	public boolean isRendering() {
		return state.rendering;
	}

	/**
	 * @param rendering the rendering to set
	 */
	public void setRendering(boolean rendering) {
		this.state.rendering = rendering;
	}

	/**
	 * Set related area versions set.
	 * @param relatedAreaVersions
	 */
	public void setRelatedAreaVersions(HashSet<AreaVersion> relatedAreaVersions) {
		this.state.relatedAreaVersions = relatedAreaVersions;
	}

	/**
	 * Set rendering flags list reference.
	 * @param flags
	 */
	public void setRenderingFlags(HashMap<Long, RenderedFlag> flags) {
		
		state.renderingFlags = flags;
	}

	/**
	 * Set rendering resources list reference.
	 * @param resources
	 */
	public void setRenderingResources(Map<Long, LinkedList<RenderedResource>> resources) {
		
		state.renderingResources = resources;
	}
	
	/**
	 * Set rendering resource.
	 * @param areaId
	 * @param resource
	 * @param mimeExtension
	 * @return resource render path 
	 */
	protected String setRenderingResourceExt(Resource resource)
		throws Exception {
		
		if (state.renderingResources != null) {
			
			long resourceId = resource.getId();
			if (resourceId == 0L) {
				return "";
			}
			
			// Get MIME extension.
			String mimeExtension = getResourceMimeExt(resourceId);
			
			// Get resource's list.
			LinkedList<RenderedResource> resources = state.renderingResources.get(resourceId);
			if (resources == null) {
				
				resources = new LinkedList<RenderedResource>();
				
				state.renderingResources.put(resourceId, resources);
			}
			
			RenderedResource renderedResource = null;
			
			// Find existing resource.
			for (RenderedResource foundResource : resources) {
				
				String extension = foundResource.getExtension();
				if (extension != null) {
					
					if (extension.compareTo(mimeExtension) == 0) {
						
						renderedResource = foundResource;
						break;
					}
				}
			}
			
			// If rendered resource not found, create new.
			if (renderedResource == null) {
				
				if (state.commonResourceFileNames) {
					// In this case use original resource file name
					renderedResource = new RenderedResource(resourceId, resource.getDescription(),
							RenderedResource.ORIGINAL_FILENAME);
				}
				else {
					renderedResource = new RenderedResource(resourceId, mimeExtension,
							RenderedResource.EXTENSION);
				}
				
				resources.add(renderedResource);
			}
			
			// Get resource render path.
			// TODO: specify render path for each resource individually
			String renderedPath = state.resourcesRenderFolder;
			
			// Add rendered path.
			renderedResource.addRenderedPath(renderedPath);
			
			return renderedPath;
		}
		
		return "";
	}

	/**
	 * Set rendering resource.
	 * @param resource
	 * @param fileName
	 * @return resource render path
	 */
	protected String setRenderingResourceFile(Resource resource, String fileName)
		throws Exception {
		
		if (state.renderingResources != null) {
			
			long resourceId = resource.getId();
			if (resourceId == 0L) {
				return "";
			}
			
			// Get resource's list.
			LinkedList<RenderedResource> resources = state.renderingResources.get(resourceId);
			
			if (resources == null) {
				
				resources = new LinkedList<RenderedResource>();
				state.renderingResources.put(resourceId, resources);
			}
			
			RenderedResource renderedResource = null;
			
			// Try to find resource with given file name.
			for (RenderedResource foundResource : resources) {
				
				String foundFileName = foundResource.getFileName();
				if (foundFileName != null) {
					
					if (foundFileName.compareTo(fileName) == 0) {
						
						renderedResource = foundResource;
						break;
					}
				}
			}
			
			// If rendered resource not found, create new.
			if (renderedResource == null) {
				
				renderedResource = new RenderedResource(resourceId, fileName,
						RenderedResource.FILENAME);
				
				resources.add(renderedResource);
			}
			
			// Get resource render path.
			// TODO: specify render path for each resource individually
			String renderedPath = state.resourcesRenderFolder;
			
			// Add rendered path.
			renderedResource.addRenderedPath(renderedPath);
			
			return renderedPath;
		}
		
		return "";
	}

	/**
	 * @return the showLocalizedTextIds
	 */
	public boolean isShowLocalizedTextIds() {
		return state.showLocalizedTextIds;
	}

	/**
	 * @param showLocalizedTextIds the showLocalizedTextIds to set
	 */
	public void setShowLocalizedTextIds(boolean showLocalizedTextIds) {
		this.state.showLocalizedTextIds = showLocalizedTextIds;
	}

	/**
	 * Set listener.
	 * @param areaServerListener
	 */
	public void setListener(AreaServerListener areaServerListener) {
		
		this.state.listener = areaServerListener;
	}
	
	/**
	 * Get resource content text.
	 * @param resourceId
	 * @return
	 */
	protected String getResourceContentText(long resourceId, String coding)
		throws Exception {
		
		Obj<Boolean> savedAsText = new Obj<Boolean>();
		
		// Load saving method.
		MiddleResult result = state.middle.loadResourceSavingMethod(resourceId, savedAsText);
		if (result.isNotOK()) {
			AreaServer.throwError("server.messageCannotGetResourceSavingMethod", result.getMessage());
		}
		
		Obj<String> contentText = new Obj<String>("");
		
		// Load text.
		if (savedAsText.ref) {
			result = state.middle.loadResourceTextToString(resourceId, contentText);
		}
		else {
			result = state.middle.loadResourceBlobToString(resourceId, coding, contentText);
		}
		
		if (result.isNotOK()) {
			AreaServer.throwErrorText("server.messageCannotGetResourceContent", result.getMessage());
		}
		
		return contentText.ref;
	}
	
	/**
	 * Queue item class definition.
	 * @author
	 *
	 */
	class QueueItem {

		long areaId;
		boolean omit;
		
		QueueItem(long areaId, boolean omit) {
			
			this.areaId = areaId;
			this.omit = omit;
		}
	}
	
	/**
	 * Returns true value if the search can continue.
	 * @param queue
	 * @return
	 */
	private static boolean isContinueSearchFolder(LinkedList<QueueItem> queue) {
		
		for (QueueItem item : queue) {
			
			if (!item.omit) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Get area assembled path.
	 * @param areaId
	 * @param versionId 
	 * @return
	 */
	public String getAreaAssembledPath(long areaId, long versionId)
			throws Exception {
		
		Obj<Area> currentArea = new Obj<Area>();

		// Create queue.
		LinkedList<QueueItem> queue = new LinkedList<QueueItem>();
		LinkedList<Long> visited = new LinkedList<Long>();
		
		// Initialize queues.
		queue.add(new QueueItem(areaId, false));
		visited.add(areaId);
		
		while (isContinueSearchFolder(queue)) {
			
			QueueItem queueItem = queue.removeFirst();
			
			long currentAreaId = queueItem.areaId;
			
			// Load area.
			MiddleResult result = state.middle.loadArea(currentAreaId, currentArea);
			if (result.isNotOK()) {
				throwError("server.messageCannotGetAbsolutePath", result.getMessage());
			}
			
			// Check if folder exists.
			String folder = currentArea.ref.getFolderOSDependent();
			if (!queueItem.omit) {
				
				if (folder == null) {
					folder = "";
				}
				
				// If the version matches and folder is not empty or the area is a start area.
				if (currentArea.ref.getVersionId() == versionId
						&& (!folder.isEmpty() || currentArea.ref.isStartArea())) {
					
					return folder.equals(File.separator) ? "" : folder;
				}
			}
			
			// Load super areas.
			result = state.middle.loadSuperAreasData(currentArea.ref);
			if (result.isNotOK()) {
				throwError("server.messageCannotGetAbsolutePath", result.getMessage());
			}
			
			// Do loop for all super areas.
			for (Area superArea : currentArea.ref.getSuperareas()) {
				
				long superAreaId = superArea.getId();
				
				if (!MiddleUtility.contains(visited, superAreaId)) {
					
					// Add super area to the queue.
					visited.add(superAreaId);
					queue.addLast(new QueueItem(superAreaId, queueItem.omit));
				}
			}
		}
		
		return "";
	}

	/**
	 * Get requested area.
	 * @return
	 */
	public Area getRequestedArea() {
		
		return state.requestedArea;
	}
	
	/**
	 * Get server URL.
	 * @return
	 */
	public String getServerUrl() 
			throws Exception {
		
		String servertUrl = state.request.getServerUrl();
		return servertUrl;
	}

	/**
	 * Get current version ID.
	 * @return
	 */
	public long getCurrentVersionId() {
		
		return state.currentVersionId;
	}

	/**
	 * Get enumeration.
	 * @param description
	 * @param value
	 * @return
	 */
	public EnumerationValue getEnumeration(String description, String value) 
			throws Exception {
		
		Obj<EnumerationValue> enumerationValue = new Obj<EnumerationValue>();
		
		// Load enumeration value object (may be cached).
		MiddleResult result = state.middle.loadEnumerationValue(description, value, enumerationValue);
		if (result.isNotOK()) {
			
			// Throw error.
			throwError("server.messageCannotLoadEnumerationValueObject",
					description, value, result.getMessage());
		}
		
		return enumerationValue.ref;
	}

	/**
	 * Get enumeration.
	 * @param description
	 * @return
	 */
	public EnumerationObj getEnumeration(String description)
		throws Exception {
				
		// Load enumeration.
		Obj<Long> enumerationId = new Obj<Long>();
		MiddleResult result = state.middle.loadEnumerationId(description, enumerationId);
		if (result.isNotOK()) {
			
			// Throw error.
			throwError("server.messageCannotFindEnumeration", description);
		}
		
		if (enumerationId.ref == null) {
			return null;
		}
		
		// Create enumeration.
		EnumerationObj enumeration = new EnumerationObj(enumerationId.ref, description);
		
		// Load enumeration values.
		result = state.middle.loadEnumerationValues(enumeration);
		if (result.isNotOK()) {
			
			// Throw error.
			throwError("server.messageCannotLoadEnumerationValues", description);
		}
		
		return enumeration;
	}

	/**
	 * Get resource image size.
	 * @param resource
	 * @return
	 */
	public Dimension getImageSize(Resource resource) 
		throws Exception {
		
		Dimension size = resource.getImageSize();
		
		if (size != null) {
			return size;
		}
		
		// If the size is not loaded, try to load it.
		MiddleResult result = state.middle.loadImageSize(resource);
		if (result.isNotOK()) {
			
			AreaServer.throwError("server.messageLoadImageSizeError", resource.toString());
		}
		
		return resource.getImageSize();	
	}

	/**
	 * Load area slots.
	 * @param area
	 * @param loadValues 
	 */
	public void loadAreaSlotsData(Area area)
		throws Exception {
		
		if (area == null) {
			AreaServer.throwError("server.messageAreaParameterIsNull");
		}

		MiddleResult result = state.middle.loadAreaSlotsRefData(area);
		
		// On error throw exception.
		if (result.isNotOK()) {
			throw new Exception(result.getMessage());
		}
	}

	/**
	 * Load area slots (extended).
	 * @param area
	 * @param isDefaultValue
	 */
	public void loadAreaSlotsDataExt(Area area, Boolean isDefaultValue)
		throws Exception {
		
		if (area == null) {
			AreaServer.throwError("server.messageAreaParameterIsNull");
		}
		
		MiddleResult result = state.middle.loadAreaSlotsRefDataEx(area, isDefaultValue);
		
		// On error throw exception.
		if (result.isNotOK()) {
			throw new Exception(result.getMessage());
		}
	}
	
	/**
	 * Load resource.
	 * @param resourceId
	 * @return
	 */
	private Resource loadResource(long resourceId)
	    throws Exception {
		
		Obj<Resource> resource = new Obj<>();
		MiddleResult result = state.middle.loadResource(resourceId, resource);
		result.throwPossibleException();
		
		return resource.ref;
	}

	/**
	 * Get constructor area of given area.
	 * @param area
	 * @return
	 */
	public Area getConstructorArea(Area area) throws Exception {
		
		// Get constructor area.
		if (area.isConstructingAreaSet()) {
			return area.getConstructingArea();
		}
		
		// Get constructor holder ID.
		Long constructorHolderId = area.getConstructorHolderId();
		if (constructorHolderId == null) {
			
			area.setConstructingAreaSet(true);
			return null;
		}
		
		// Get constructor area ID.
		Obj<Long> constructorAreaId = new Obj<Long>();
		MiddleResult result = state.middle.loadConstructorHolderAreaId(constructorHolderId, constructorAreaId);
		
		if (result.isNotOK()) {
			throw new Exception(result.getMessage());
		}
		
		// Load new area.
		if (constructorAreaId.ref == null) {
			
			area.setConstructingAreaSet(true);
			return null;
		}
		
		Area constructingArea = getArea(constructorAreaId.ref);
		if (constructingArea == null) {
			throw new Exception(MiddleResult.AREA_NODE_DOESNT_EXIST.getMessage());
		}
		
		area.setConstructingArea(constructingArea);
		area.setConstructingAreaSet(true);
		
		return constructingArea;
	}

	/**
	 * Call user defined procedure.
	 * @param name
	 * @param parameters
	 * @param returnedValue
	 * @return
	 */
	public boolean callProcedure(String name, final Object[] parameters, Obj<Object> returnedValue)
					throws Exception {
		
		returnedValue.ref = null;
		
		// Get procedure.
		Procedure procedure = state.blocks.getProcedure(name);
		if (procedure == null) {
			return false;
		}
		
		// Variables for returned value and text.
		final Obj<Variable> returnedVariableReference = new Obj<Variable>();
		final Obj<Variable> resultTextVariableReference = new Obj<Variable>();

		// Push procedure block.
		ProcedureBlockDescriptor block = new ProcedureBlockDescriptor(procedure,
				new ParameterInitializeListener() {
					@Override
					public Object getValue(String parameterName, ProcedureParameterType type,
							Obj<Variable> _outputVariable)
						throws Exception {
						
						ProcedureParameter procedureParameter = null;
											
						// Find procedure parameter.
						for (Object parameterObject : parameters) {
							if (parameterObject instanceof ProcedureParameter) {
								
								ProcedureParameter procedureParameterItem = (ProcedureParameter) parameterObject;
								if (procedureParameterItem.getName().equals(parameterName)) {
									
									procedureParameter = procedureParameterItem;
									break;
								}
							}
						}
						
						// If a return parameter is specified, handle it.
						if (type == ProcedureParameterType.returned) {
							
							// Find return variable.
							if (procedureParameter != null) {
								
								String outputObjectName = procedureParameter.getOutputObjectIdentifier();
								if (outputObjectName == null) {
									throwError("server.messageNoOutputVariable");
								}
								
								_outputVariable.ref = state.blocks.findVariable(outputObjectName);
								
								if (_outputVariable.ref == null) {
									throwError("server.messageReferencedVariableDoesntExist",
											outputObjectName);
								}
							}
							// If not found, create new.
							if (_outputVariable.ref == null) {
								_outputVariable.ref = new Variable("", null);
							}

							returnedVariableReference.ref = _outputVariable.ref;
						}
						
						// Set result text output variable reference.
						if (type == ProcedureParameterType.resultText && procedureParameter != null) {

							// Find text output variable.
							String outputObjectName = procedureParameter.getOutputObjectIdentifier();
							if (outputObjectName == null) {
								throwError("server.messageNoOutputVariable");
							}
							
							_outputVariable.ref = state.blocks.findVariable(outputObjectName);
							resultTextVariableReference.ref = _outputVariable.ref;
							
							if (_outputVariable.ref == null) {
								throwError("server.messageReferencedVariableDoesntExist",
										outputObjectName);
							}
						}
						
						// If it is an output variable.
						if (type == ProcedureParameterType.output && procedureParameter != null) {
							
							// Get output variable.
							String outputObjectName = procedureParameter.getOutputObjectIdentifier();
							if (outputObjectName == null) {
								throwError("server.messageNoOutputVariable");
							}
							
							_outputVariable.ref = state.blocks.findVariable(outputObjectName);
							
							if (_outputVariable.ref == null) {
								throwError("server.messageReferencedVariableDoesntExist",
										outputObjectName);
							}
						}
						
						// Return input value.
						if (procedureParameter != null) {
							return procedureParameter.getInputValue();
						}
						return null;
					}
		});
		
		// Push procedure block.
		state.blocks.pushBlockDescriptor(block);
		
		// Get inner text.
		String procedureInnerText = procedure.getInnerText();
		// Process the inner text.
		String resultText = processTextCloned(procedureInnerText);
		
		// Pop procedure block descriptor.
		state.blocks.popBlockDescriptor(false);

		// Set output values.
		if (resultTextVariableReference.ref != null) {
			resultTextVariableReference.ref.value = resultText;
		}
		if (returnedVariableReference.ref != null) {
			returnedValue.ref = returnedVariableReference.ref.value;
		}
		if (procedure.isReturnText()) {
			returnedValue.ref = resultText;
		}
		
		return true;
	}

	/**
	 * Load slot value.
	 * @param slot
	 */
	public void loadSlotValue(Slot slot) throws Exception {
		
		// Load slot value.
		MiddleResult result = state.middle.loadSlotValue(slot);
		if (result.isNotOK()) {
			
			throwError("server.messageCannotLoadSlotValue", result.getMessage());
		}
	}
	
	/**
	 * Watch source change in external provider.
	 * @param slot
	 */
	public void watch(Slot slot)  throws Exception {
		
		if (slot.isExternalProvider()) {
		
			// Start watching external provider changes.
			providerWatchService.register(slot);
		}
	}
	
	/**
	 * Stop watching all external providers.
	 */
	public void stopWatchingAll() {
		
		// Unregister all watching services.
		ProviderWatchService.unregisterAll();
	}
	
	/**
	 * Input slot value.
	 * @param slot
	 */
	public void input(Slot slot)
		throws Exception {
		
		MiddleResult result = MiddleResult.OK;
		
		// If the slot reads external provider, load value from it.
		boolean readsInput = slot.getReadsInput();
		boolean isExternalProvider = slot.isExternalProvider();

		// Load slot value.
		if (isExternalProvider && readsInput) {
			result = MiddleUtility.loadSlotValueFromExternal(slot);
		}
		
		if (result.isOK()) {
			
			// Save the value
			long slotId = slot.getId();
			String textValue = slot.getTextValue();
			
			Obj<Boolean> slotUpdated = new Obj<Boolean>();
			result = state.middle.updateSlotTextValue(slotId, textValue, slotUpdated);
			
			// Add slot to list of updated slots.
			if (isExternalProvider) {
				slot.setUpdatedExternally(slotUpdated.ref);
			}
			
			if (slotUpdated.ref) {
				state.updatedSlots.add(slot.getId());
			}
		}
			
		result.throwPossibleException();
	}
	
	/**
	 * Output text value.
	 * @param slot
	 * @param outputText
	 */
	public void output(Slot slot, String outputText)
		throws Exception {
		
		// Get link for slot output.
		String link = slot.getExternalProvider();
		boolean writesOutput = slot.getWritesOutput();
		if (link != null && writesOutput) {
			
			long slotId = slot.getId();
			
			// Get write lock.
			Obj<Boolean> locked = new Obj<Boolean>();
			MiddleResult result = state.middle.loadSlotOutputLock(slotId, locked);
			result.throwPossibleException();
			
			if (!locked.ref) {
				
				// If the link exists, write output text to external provider.
				result = MiddleUtility.saveValueToExternalProvider(state.middle, link, outputText);
				result.throwPossibleException();
				
				// Remember sent text.
				result = state.middle.updateSlotOutputText(slotId, outputText);
				result.throwPossibleException();
			}
		}
	}
	
	/**
	 * Make external slot backup.
	 * @param fileExtension
	 * @param slotId
	 */
	public void backup(Slot slot, String fileExtension)
		throws Exception {
		
		MiddleUtility.backup(slot, fileExtension);
	}
	
	/**
	 * Revert external provider source code.
	 * @param slot
	 * @throws Exception
	 */
	public void revert(Slot slot)
		throws Exception {
		
		// Get link for slot output.
		String link = slot.getExternalProvider();
		boolean writesOutput = slot.getWritesOutput();
		if (link != null && writesOutput) {
			
			long slotId = slot.getId();
			
			// Get write lock.
			Obj<Boolean> locked = new Obj<Boolean>();
			MiddleResult result = state.middle.loadSlotOutputLock(slotId, locked);
			result.throwPossibleException();
			
			if (!locked.ref) {
				
				// Load slot text value.
				result = state.middle.loadSlotTextValueDirectly(slotId, slot);
				result.throwPossibleException();
				
				String outputText = slot.getTextValue();
				
				// If the link exists, write output text to external provider.
				result = MiddleUtility.saveValueToExternalProvider(state.middle, link, outputText);
				result.throwPossibleException();
			}
		}
	}
	
	/**
	 * Update input lock.
	 * @param slotId 
	 * @param locked
	 */
	public void updateInputLock(long slotId, boolean locked)
		throws Exception {
		
		MiddleResult result = state.middle.updateInputLock(slotId, locked);
		result.throwPossibleException();
	}

	/**
	 * Is input lock.
	 * @param slotId
	 * @return
	 */
	public boolean isInputLock(long slotId)
		throws Exception {
		
		Obj<Boolean> locked = new Obj<Boolean>();
		MiddleResult result = state.middle.loadSlotInputLock(slotId, locked);
		
		result.throwPossibleException();
		return locked.ref;
	}
	
	/**
	 * Update output lock.
	 * @param slotId
	 * @param locked
	 */
	public void updateOutputLock(long slotId, boolean locked)
		throws Exception {
		
		MiddleResult result = state.middle.updateOutputLock(slotId, locked);
		result.throwPossibleException();
	}
	
	/**
	 * Is output lock.
	 * @param slotId
	 * @return
	 */
	public boolean isOutputLock(long slotId)
		throws Exception {
		
		Obj<Boolean> locked = new Obj<Boolean>();
		MiddleResult result = state.middle.loadSlotOutputLock(slotId, locked);
		
		result.throwPossibleException();
		return locked.ref;
	}
	
	/**
	 * Get first visible super area.
	 * @param areaId
	 * @return
	 * @throws Exception 
	 */
	public Area getFirstVisibleSuperArea(long areaId) throws Exception {
		
		Area area = getArea(areaId);
		if (area == null) {
			return null;
		}
		
		// Find first visible super area.
		LinkedList<Area> queue = new LinkedList<Area>();
		queue.add(area);
		
		while (!queue.isEmpty()) {
			
			area = queue.removeFirst();
			
			if (area.isVisible()) {
				return area;
			}
			
			loadSuperAreasData(area);
			queue.addAll(area.getSuperareas());
		}
		
		return null;
	}

	/**
	 * Get tabulator character.
	 * @return
	 */
	public String getTabulator() {
		
		return state.tabulator.ref;
	}
	
	/**
	 * Create CSS area slots lookup table.
	 * @param collect - if true, the server collects multiple CSS lookup tables
	 * @param inputText 
	 */
	private void createCssSlotsLookup(String inputText) 
			throws Exception {
		
		// Parse input text and add items to the map.		
		final Pattern pattern = Pattern.compile("^(.*?)=([^,;]*)(\\s*,\\s*!\\s*important\\s*)?(\\s*,\\s*pptext\\s*)?;?\\s*$");
		String lines[] = inputText.split("\r\n|\r|\n");

		for (String line : lines) {
			
			line = line.trim();
			if (line.isEmpty()) {
				continue;
			}
			
			boolean error = false;
			
			Matcher matcher = pattern.matcher(line);
			if (matcher.matches()) {
			
				String slotName = matcher.group(1);
				String propertyName = matcher.group(2);
				
				if (slotName == null || propertyName == null) {
					error = true;
				}
				else {
					slotName = slotName.trim();
					propertyName = propertyName.trim();
					
					if (slotName.isEmpty() || propertyName.isEmpty()) {
						error = true;
					}
					else {
					
						boolean isImportant = matcher.group(3) != null;
						boolean process = matcher.group(4) != null;
						
						// Add new item to the map.
						state.cssLookupTable.insert(slotName, propertyName, isImportant, process);
					}
				}
			}
			else {
				error = true;
			}
			
			if (error) {
				throwError("server.messageErrorWhileCreatingCssLookup", line);
			}
		}
	}
	
	/**
	 * Clear CSS rules cache.
	 */
	public void clearCssRules() {
		
		state.cssRulesCache.clear();
	}
	
	/**
	 * Insert CSS rules. (Used for optimization purpose.)
	 * @param socketSelector 
	 */
	public void insertCssRules(String selector, String mediaSlotName, String importantSlotName)
			throws Exception {
		
		// Delegate call.
		insertCssRules(state.area, selector, mediaSlotName, importantSlotName);
	}

	/**
	 * Insert CSS rules. (Used for optimization purpose.)
	 * @param socketSelector 
	 */
	public void insertCssRules(Area area, String selector, String mediaSlotName, String importantSlotName)
			throws Exception {
		
		if (selector == null || selector.isEmpty()
				|| mediaSlotName == null || mediaSlotName.isEmpty()
				|| importantSlotName == null || importantSlotName.isEmpty()) {
			return;
		}
		
		String media = "all";
		Slot mediaSlot = slot(mediaSlotName, false, false, 0L, true);
		if (mediaSlot != null && !mediaSlot.isDefault()) {
			media = mediaSlot.getValue().toString();
		}
		
		boolean isImportant = false;
		Slot importantSlot = slot(importantSlotName, false, false, 0L, true);
		if (importantSlot != null && !importantSlot.isDefault() && importantSlot.getBooleanValue() == true) {
			isImportant = true;
		}
		
		// Load area slots.
		MiddleResult result = state.middle.loadAreaSlotsRefDataEx(area, false);
		if (result.isNotOK()) {
			throwErrorText(result.getMessage());
		}
		
		// Process slots.
		for (Slot slotObject : area.getSlots()) {
			
			if (slotObject.isDefault()) {
				continue;
			}
			
			// Get slot alias and use lookup table.
			String alias = slotObject.getAlias();
			LinkedList<CssLookupTableValue> tableValues = state.cssLookupTable.get(alias);
			if (tableValues == null) {
				continue;
			}
			
			for (CssLookupTableValue tableValue : tableValues) {
			
				String property = tableValue.propertyName;
				if (property == null) {
					continue;
				}
				
				// Get slot value and process it.
				String slotValue = slotObject.getSpecialValueNull();
				if (slotValue == null) {
					
					loadSlotValue(slotObject);
					slotValue = slotObject.getTextValue();
				}
				if (slotValue == null) {
					continue;
				}
				
				// Possibly process the value.
				if (tableValue.process) {
					
					slotValue = processTextCloned(slotValue);
					
					if (slotValue == null) {
						continue;
					}
				}
				
				// Get rules for given media.
				state.cssRulesCache.insert(area.getId(), media, selector, property, slotValue,
						isImportant || tableValue.isImportant);
			}
		}
	}

	/**
	 * Get CSS rules.
	 */
	public String getCssRules() {
		
		String newText = "";
		String tab = getTabulator();
		if (tab == null) {
			tab = "\t";
		}
		
		// Print CSS rules.
		for (AreaMediasRules areaMediasRules : state.cssRulesCache.areasMediasRules) {
			
			// Insert comment.
			long areaId = areaMediasRules.areaId;
			try {
				Area area = getArea(areaId);
				String areaName = area.getDescription();
				newText += "/* " + areaName + " */\n";
			}
			catch (Exception e) {
			}
			
			// Compile CSS rules.
			for (MediaRules mediaRules : areaMediasRules.mediasRules.mediasRules) {
				
				String media = mediaRules.media;
				boolean isMediaAll = media.equals("all");
				String mediaTab = "";
				if (!isMediaAll) {
					
					newText += "@media " + media + " {\n";
					mediaTab = tab;
				}
				
				for (SelectorRules selectorRules : mediaRules.selectorsRules) {
					
					String selector = selectorRules.selector;
					newText += mediaTab + selector + " {\n";
					
					for (CssPropertyValue propertyValue : selectorRules.cssPropertiesValues) {
						
						String property = propertyValue.property;
						String value = propertyValue.value;
						boolean isImportant = propertyValue.isImportant;
						
						newText += mediaTab + tab + property + ": " + value + (isImportant ? " !important" : "") + ";\n";
					}
					
					newText += mediaTab + "}\n";
				}
				
				if (!isMediaAll) {
					newText += "}\n";
				}
			}
		}
		
		return newText;
	}

	/**
	 * Get current language.
	 * @return
	 */
	public Language getCurrentLanguage() {
		
		return state.currentLanguage;
	}

	/**
	 * Returns true if the source code has to be displayed.
	 * @return
	 */
	public boolean showSourceCode() {
		
		if (showSourceCodeForAreaId == null) {
			return false;
		}
		return state.requestedArea.getId() == showSourceCodeForAreaId;
	}

	/**
	 * Tells renderer if it should include <meta http-equiv="Content-Type" content="text/html; charset=..."> 
	 * into <head> section.
	 * @return
	 */
	public boolean useMetaCharset() {
		
		return state.useMetaCharset;
	}
	
	/**
	 * Set resources render folder.
	 * @param resourcesRenderFolder
	 */
	public void setResourcesRenderFolder(String resourcesRenderFolder) {
		
		this.state.resourcesRenderFolder  = resourcesRenderFolder;
	}
	
	/**
	 * Get slot text
	 * @param slotId
	 * @return
	 */
	public String getSlotText(Long slotId) {

		if (slotId == null) {
			return "";
		}
		
		// Directly load slot value using slot ID.
		Slot slot = new Slot();
		MiddleResult result = state.middle.loadSlotTextValueDirectly(slotId, slot);
		
		if (result.isNotOK()) {
			return "";
		}
		
		return slot.getTextValue();
	}
	
	/**
	 * Get path value of input slot.
	 * @param slot
	 * @return
	 * @throws Exception 
	 */
	public String getPath(Slot slot) throws Exception {
		
		// Check slot type.
		SlotType type = slot.getType();
		if (!SlotType.PATH.equals(type)) {
			return "";
		}
		
		// Get slot value.
		loadSlotValue(slot);
		Object value = slot.getValue();
		if (value == null) {
			return "";
		}
		
		String valueText = value.toString();
		
		// Process value only if may have tags.
		if (valueText.indexOf('@') != -1) {
			valueText = processTextCloned(valueText);
		}
		
		// Remove trailing backslashes.
		StringBuilder path = new StringBuilder(valueText);
		int length = path.length();
		int index = length - 1;
		
		for (; index >= 0; index--) {
			
			char character = path.charAt(index);
			if (character != '\\' & character != '/') {
				break;
			}
		};
		if (index >= -1) {
			path = path.delete(index + 1, length);
		}
		
		return path.toString();
	}
	
	/**
	 * Get current encoding.
	 * @return
	 */
	public String getCurrentEncoding() {
		
		return state.encoding;
	}
	
	/**
	 * Get supported text encodings of the Area Server.
	 * @param areaServer
	 * @return
	 */
	public static String getSupportedEncodings() {
		
		String encodings = "";
		String divider = "";
		
		for (String encoding : SUPPORTED_ENCODINGS) {
			encodings += divider + encoding;
			divider = ", ";
		}
		
		return encodings;
	}
	
	/**
	 * Get thread name.
	 * @param threadId
	 * @return
	 */
	public String getThreadName(Long threadId) {
	        
        // Get the current thread group.
        ThreadGroup group = Thread.currentThread().getThreadGroup();
	        
        // Iterate over all threads in the thread group
        int activeThreadCount = group.activeCount();
        Thread[] threads = new Thread[activeThreadCount];
        group.enumerate(threads);
        
        for (Thread thread : threads) {
            if (thread != null && thread.getId() == threadId) {
                String threadName = thread.getName();
                return threadName;
            }
        }
		return "UNKNOWN";
	}
	
	/**
	 * Find area server state.
	 * @param stateHashCode
	 * @return
	 */
	public AreaServerState findState(int stateHashCode) {
		
		AreaServerState currentState = state;
		while (currentState != null) {
			
			int currentHashCode = currentState.hashCode();
			if (currentHashCode == stateHashCode) {
				
				return currentState;
			}
			currentState = currentState.parentState;
		}
		return null;
	}

	/**
	 * Stop Area Server.
	 */
	public static void stop() {
		
		DebugInfo.setExitDebugger(true);
	}
}

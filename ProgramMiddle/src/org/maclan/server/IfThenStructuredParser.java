/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.maclan.server;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.maclan.MiddleResult;
import org.multipage.util.Obj;

/**
 * Pareser for if-then-else tags.
 * @author vakol
 *
 */
public class IfThenStructuredParser extends ParserBase {

	/**
	 * Token types.
	 */
	private static final int IF = 1;
	private static final int ELSEIF = 2;
	private static final int ELSE = 3;
	private static final int ENDIF = 4;

	/**
	 * Text matchers.
	 */
	private Matcher ifMatcher;
	private Matcher elseifMatcher;
	private Matcher elseMatcher;
	private Matcher endMatcher;

	/**
	 * Condition resolve.
	 */
	protected boolean resolveCondition(AreaServer server, TagProperties properties)
			throws Exception {
		
		return true;
	}
	
	/**
	 * Get condition value.
	 * @param properties
	 * @return
	 */
	private boolean getConditionValue(TagProperties properties, int position)
			throws Exception {
		
		// Resolve condition.
		boolean condition = false;
			
		try {
			condition = resolveCondition(server, properties);
		}
		catch (Exception e) {
			
			this.position = position;
			throw e;
		}
		
		return condition;
	}
	
	/**
	 * Get next token.
	 * @param text
	 * @return
	 */
	private Token next() throws Exception {
		
		Token [] tokens = new Token [4];

		if (ifMatcher.find(position)) {
			
			// Parse IF tag properties.
			TagProperties properties = new TagProperties();
			Obj<Integer> endPosition = new Obj<Integer>(ifMatcher.end());
			
			MiddleResult result = ServerUtilities.parseTagProperties(text, endPosition,
					properties, false);
			if (result.isNotOK()) {
				
				AreaServer.throwError("server.messageTagParseError", result.getMessage());
			}

			tokens[0] = new Token(IF, ifMatcher.start(), endPosition.ref, properties);
		}
		if (elseifMatcher.find(position)) {
			
			// Parse ELSEIF tag properties.
			TagProperties properties = new TagProperties();
			Obj<Integer> endPosition = new Obj<Integer>(elseifMatcher.end());
			
			MiddleResult result = ServerUtilities.parseTagProperties(text, endPosition,
					properties, false);
			if (result.isNotOK()) {
				
				AreaServer.throwError("server.messageTagParseError", result.getMessage());
			}
			
			tokens[1] = new Token(ELSEIF, elseifMatcher.start(), endPosition.ref, properties);
		}
		if (elseMatcher.find(position)) {
			
			tokens[2] = new Token(ELSE, elseMatcher.start(), elseMatcher.end());

		}
		if (endMatcher.find(position)) {
			
			tokens[3] = new Token(ENDIF, endMatcher.start(), endMatcher.end());
		}
		
		return getFirstToken(tokens);
	}

	/**
	 * Constructor.
	 * @param server
	 * @param text
	 * @param position
	 */
	public IfThenStructuredParser(AreaServer server, StringBuilder text,
			int position) {
		
		super(server, text, position);

		this.ifMatcher = Pattern.compile("\\[\\s*@\\s*IF\\b").matcher(text);
		this.elseifMatcher = Pattern.compile("\\[\\s*@\\s*ELSEIF\\b").matcher(text);
		this.elseMatcher = Pattern.compile("\\[\\s*@\\s*ELSE\\s*]").matcher(text);
		this.endMatcher = Pattern.compile("\\[\\s*/@\\s*IF\\s*]").matcher(text);
	}
	
	/**
	 * Parse IF command texts.
	 * @param resolvedText
	 * @return
	 */
	public void parseIfStructuredCommand(StringBuilder resolvedText)
		throws Exception {
		
		// States.
		final int EXIT = -1;
		
		boolean isOutputSet = false;
		int state = IF;
		
		// Do loop until exit.
		while (state != EXIT) {
			
			token = next();
			
			switch (state) {
			
			case IF:
			case ELSEIF:
				
				if (state == IF && token.type != IF) {
					AreaServer.throwError("server.messageExpectingIfTag");
				}
				if (state == ELSEIF && token.type != ELSEIF) {
					AreaServer.throwError("server.messageExpectingElseIfTag");
				}
				
				// Get condition value.
				boolean condition = false;
				if (resolvedText != null) {
					condition = getConditionValue(token.properties, token.end);
					
					// Debugger point.
					if (!isOutputSet) {
						DebugInfo.setDebugInfo(server, "IF", token.properties, token.start, token.end, resolvedText.toString());
						DebugInfo.debugPoint(server);
					}
				}
				
				int resultTextBegin = token.end;
				consume(token);
				token = next();
				
				// If next token is IF.
				if (token.type == IF) {
					// Parse sequence.
					parseIfCommandsSequence();
				}
				// If next is ELSE or ENDIF.
				if (token.type == ELSE || token.type == ELSEIF || token.type == ENDIF) {
					int resultTextEnd = token.start;
					
					// Set THEN text and continue.
					if (condition && !isOutputSet) {
						setTextSubstring(resolvedText, resultTextBegin, resultTextEnd);
						isOutputSet = true;
					}
					
					// Debugger point.
					if (condition) {
						DebugInfo.setDebugInfo(server, "IF", token.properties, resultTextBegin, resultTextEnd, resolvedText.toString());
						DebugInfo.debugPoint(server);
					}
					
					state = token.type;
					break;
				}

				AreaServer.throwError("server.messageExpectingIfOrElseifOrElseOrEndIf");
				
			case ELSE:
				
				if (token.type != ELSE) {
					AreaServer.throwError("server.messageExpectingElse");
				}
				
				int resolvedTextBegin = token.end;
				consume(token);
				token = next();
				
				// If next is IF.
				if (token.type == IF) {
					// Parse sequence.
					parseIfCommandsSequence();
				}
				// If next is ENDIF.
				if (token.type == ENDIF) {
					int resolvedTextEnd = token.start;
					// Set ELSE text and continue.
					if (resolvedText != null && !isOutputSet) {
						setTextSubstring(resolvedText, resolvedTextBegin, resolvedTextEnd);
						isOutputSet = true;
						
						// Debugger point.
						DebugInfo.setDebugInfo(server, "IF", token.properties, resolvedTextBegin, resolvedTextEnd, resolvedText.toString());
						DebugInfo.debugPoint(server);
					}
					
					state = token.type;
					break;
				}

				AreaServer.throwError("server.messageExpectingEndIfOrIf");
			
			case ENDIF:
				if (token.type != ENDIF) {
					AreaServer.throwError("server.messageExpectingEndIf");
				}
				consume(token);
				token = next();
				state = EXIT;
				break;
				
			default:
				AreaServer.throwError("server.messageUnknownIfCommandParserState");
			}
		}
	}

	/**
	 * Parse IF command sequence.
	 */
	private void parseIfCommandsSequence()
		throws Exception {
		
		while (true) {
			token = next();
			if (token.type != IF) {
				return;
			}
	
			parseIfStructuredCommand(null);
		}
	}
}

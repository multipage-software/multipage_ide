/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */
package org.multipage.generator;

import java.awt.BorderLayout;
import java.awt.Panel;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.event.ChangeEvent;

import org.multipage.gui.ApplicationEvents;
import org.multipage.gui.GuiSignal;
import org.multipage.gui.Message;
import org.multipage.gui.PreventEventEchos;
import org.multipage.gui.UpdatableComponent;
import org.multipage.util.Closable;
import org.multipage.util.Safe;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * Panel that displays Area Server output monitor.
 * @author vakol
 *
 */
public class MonitorPanel extends Panel implements TabItemInterface, PreventEventEchos, UpdatableComponent, Closable {

	/**
	 * Version
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Regular expression for getting the requested area ID.
	 */
	private static final Pattern requestedAreaIdRegex = Pattern.compile("^.*?\\?.*?area_id=(?<areaId>\\d+).*$");
	
	/**
	 * Web view objects.
	 */
    private WebView webViewBrowser = null;
    private WebEngine webEngine = null;
	private Scene scene = null;
	
	/**
	 * SWT view objects.
	 */
	private SwtBrowserCanvas swtBrowser = null;
	
	/**
	 * Home URL.
	 */
	private String url;

	/**
	 * List of previous messages.
	 */
	private LinkedList<Message> previousMessages = new LinkedList<Message>();

	/**
	 * Create the panel.
	 * @param url 
	 */
	public MonitorPanel(String url) {
		
		try {
			this.url = url;
			
			// Initialize components.
			initComponents();
			// Post creation.
			postCreation(); //$hide$
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Initialize components.
	 */
	private void initComponents() {

		setLayout(new BorderLayout(0, 0));
	}
	
	/**
	 * Post creation.
	 */
	private void postCreation() {
		try {
			
			// Ensure the panel is visible and open the browser.
			setVisible(true);
			openBrowser(url);
			setListeners();
			// Register for updates.
			GeneratorMainFrame.registerForUpdate(this);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Open browser.
	 */
	private void openBrowser(String url) {
		
		// Try to open SWT browser (with native code).
		Safe.invokeLater(() -> {
			
			swtBrowser = SwtBrowserCanvas.createBrowserCanvas(browserCanvas -> {
				try {
					
					// Add SWT browser into center of the panel.
					MonitorPanel.this.add(browserCanvas, BorderLayout.CENTER);
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
				
				// Retur URL to load.
				return url;
			}
			,
			urlChanged -> {
				try {
				
					onUrlChanged(urlChanged);
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
		
			if (swtBrowser == null) {
				
				// Otherwise use JavaFX panel for webViewBrowser.
				JFXPanel javaFxPanel = new JFXPanel();
				
				// Add JavaFX panel.
				add(javaFxPanel, BorderLayout.CENTER);
				
				// Initialize scene.
				Platform.setImplicitExit(false);
				Platform.runLater(() -> {
					try {
						
						webViewBrowser = new WebView();
						webEngine = webViewBrowser.getEngine();
						
						// Set web engine action listeners.
						setWebEngineListeners(webEngine);
						
						webEngine.load(url);
						
						scene = new Scene(webViewBrowser, 750, 500, Color.web("#666970"));
						javaFxPanel.setScene(scene);
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				});
			}
		});
	}

	/**
	 * Callback that is called when browser URL changes.
	 * @param urlChanged
	 */
	private void onUrlChanged(String urlChanged) {
		try {
			
			// Update main panel.
			Safe.invokeLater(() -> {
				MonitorPanel.this.revalidate();
			});
			
			// If there exists requested area ID, display the area properties.
			if (urlChanged == null || urlChanged.isEmpty()) {
				return;
			}
			
			Long currentAreaId = getCurrentAreaId(urlChanged);
			if (currentAreaId <= 0L) {
				return;
			}
			
			// Select new areas.
			HashSet<Long> selectedAreaIds = new HashSet<Long>();
			selectedAreaIds.add(currentAreaId);
			
			ApplicationEvents.transmit(MonitorPanel.this, GuiSignal.displayAreaProperties, selectedAreaIds);
			
			// This patch resets SWT shells so that they do not grab input focus.
			ApplicationEvents.transmit(this, GuiSignal.resetSwtBrowser);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set web engine listeners.
	 * @param webEngine
	 */
	protected void setWebEngineListeners(WebEngine webEngine) {
		try {
			
			ReadOnlyObjectProperty<javafx.concurrent.Worker.State> property = webEngine.getLoadWorker().stateProperty();
	
			ChangeListener<State> changeListener = new ChangeListener<State>() {
				@Override
				public void changed(ObservableValue<? extends State> arobserable, State oldState, State newState) {
					
					if (newState == State.SUCCEEDED) {
	                    String location = webEngine.getLocation();
	                    
	                    // TODO: <---FINISH IT
	                }
				}
			};
			property.addListener(changeListener);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set listeners.
	 */
	private void setListeners() {
		try {
			
			// Receive the "reset SWT" signal.
			ApplicationEvents.receiver(this, GuiSignal.resetSwtBrowser, message -> {
				try {
					
					swtBrowser.enableSwt(false);
					swtBrowser.enableSwt(true);
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Return the requested area ID.
	 * @param url
	 * @return
	 */
	private Long getCurrentAreaId(String url) {
		
		try {
			Matcher matcher = requestedAreaIdRegex.matcher(url);
			boolean success = matcher.find();
			if (!success) {
				return -1L;
			}
			
			int groupCount = matcher.groupCount();
			if (groupCount != 1) {
				return -1L;
			}
			
			// Get areaId matching group value.
			String areaIdText = matcher.group("areaId");
			long areaId = Long.parseLong(areaIdText);
			
			return areaId;
		}
		catch (Exception e) {
		}
		return -1L;
	}

	/**
	 * Load URL.
	 */
	public boolean load(String url) {
		
		// Delegate the call.
		if (webViewBrowser == null) {
			return false;
		}
		
		// Load URL into the webViewBrowser.
		return true;
	}
	
	/**
	 * Dispose monitor.
	 */
	public void dispose() {
		try {
			
			// Close event listeners.
			ApplicationEvents.removeReceivers(this);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Recreate browser.
	 */
	public void recreateBrowser() {
		try {
			
			swtBrowser.close();
			MonitorPanel.this.remove(swtBrowser);
			
			swtBrowser = SwtBrowserCanvas.createBrowserCanvas(
				browser -> {
					
					try {
						// Add SWT browser to the center of the panel.
						MonitorPanel.this.add(browser, BorderLayout.CENTER);
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					// Load the URL provided.
					return url;
				}
				,
				urlChanged -> {
					try {
						
						onUrlChanged(urlChanged);
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On tab panel change event.
	 */
	@Override
	public void onTabPanelChange(ChangeEvent e, int selectedIndex) {
		
	}
	
	/**
	 * Called before the tab panel is removed. 
	 */
	@Override
	public void beforeTabPanelRemoved() {
		try {
			
			if (swtBrowser != null) {
				swtBrowser.close();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Called when the tab panel needs to recreate its content.
	 */
	@Override
	public void recreateContent() {
		try {
			
			recreateBrowser();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get list of previous messages.
	 */
	@Override
	public LinkedList<Message> getPreviousMessages() {
		
		return previousMessages;
	}

	@Override
	public String getTabDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reload() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public TabState getTabState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTabLabel(TabLabel tabLabel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAreaId(Long topAreaId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public HashSet<Long> getSelectedTabAreaIds() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * On update components.
	 */
	@Override
	public void updateComponents() {
		try {
			
			// Reload the browser.
			swtBrowser.reload();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Close the panel.
	 */
	@Override
	public void close() {
		try {
			
			// Unregister from updates.
			GeneratorMainFrame.unregisterFromUpdate(this);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}

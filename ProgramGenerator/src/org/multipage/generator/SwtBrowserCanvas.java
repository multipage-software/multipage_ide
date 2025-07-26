/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2023-12-16
 *
 */

package org.multipage.generator;

import java.awt.Canvas;
import java.util.HashSet;
import java.util.function.Consumer;
import java.util.function.Function;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.multipage.util.Lock;
import org.multipage.util.Obj;
import org.multipage.util.Safe;

/**
 * Implementation of an AWT panel that embeds a SWT component.
 * @author vakol
 */
public final class SwtBrowserCanvas extends Canvas {

    /**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;	
    
	/**
	 * Single SWT background thread.
	 */
	private static SwtThread swtThread = null;

	/**
	 * Indicates that SWT is available for the OS.
	 */
	private static boolean swtAvailable = false;
	
	/**
	 * Set of created shells.
	 */
	private final static HashSet<Shell> swtShells = new HashSet<>();
	
	/**
	 * Associated browser.
	 */
	public Browser browser = null;
	
	/**
	 * Initial URL for the browser.
	 */
	private String initialUrl = "";

	/**
	 * URL changed lambda callback.
	 */
	protected Consumer<String> locationChangedLambda = null;
	
    /**
     * Implementation of a SWT thread.
     */
    private static class SwtThread extends Thread {
    	
		/**
		 * Single SWT display that is needed when running SWT thread.
		 */
		protected static Display display = null;
    	
		/**
		 * This flag terminates the SWT thread.
		 */
		private boolean exitThread = false;

        /**
         * Run SWT thread.
         */
		@Override
        public void run() {
			try {
				
				// Create SWT display.
	        	display = new Display();
	
	            // Execute the SWT event dispatch loop.
	            try {
	                while (!isInterrupted() && !exitThread) {
	                	
	    				// Set SWT available.
	    				swtAvailable = true;
	    				
	                    if (!display.readAndDispatch()) {
	                        display.sleep();
	                    }
	                }
	            }
	            catch (Exception e) {
	                interrupt();
	            }
	            
	            // Set SWT not available.
				swtAvailable = false;
			}
			catch(Throwable expt) {
				Safe.exception(expt);
			};
		}
		
		/**
		 * Disposal of thread resources. Must be run at the end of application.
		 */
		void terminate() {
			try {
				
				// Signal SWT thread termination.
				exitThread = true;
				// Release display.
				display.dispose();
			}
			catch(Throwable expt) {
				Safe.exception(expt);
			};
		}
    }
	
	/**
     * Static constructor. Required for Linux, harmless for other OS.
     */
    static {
    	try {
			
			System.setProperty("sun.awt.xembedserver", "true");
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
    }
    
    /**
     * Starts the SWT thread.
     */
    public static boolean startSwtThread() {
    	
    	try {
	    	boolean runThread = (swtThread == null);
	    	
	    	// Create the background thread if it doesn't already exist.
	    	if (runThread) {
				swtThread = new SwtThread();
				swtThread.start();
	    	}
	        return runThread;
	    }
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
    }
    
    /**
     * Stops SWT thread. Disposes all SWT shells.
     */
    public static void stopSwtThread() {
    	try {
			
			SwtThread.display.syncExec(() -> {
				try {
					
					closeAllShells();
			    	
			        if (swtThread != null) {
			            swtThread.terminate();
			        }
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
     * Close all SWT shells.
     */
    private static void closeAllShells() {
    	try {
			
			for (Shell swtShell : swtShells) {
				try {
					
					swtShell.dispose();
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
	    	}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
    }
    
    /**
     * Create browser canvas.
     * @return
     */
	public static SwtBrowserCanvas createBrowserCanvas(Function<SwtBrowserCanvas, String> initialUrlLambda, Consumer<String> locationChangedLambda) {
		
		// Check if the SWT thread is available (started).
		if (swtAvailable == false) {
			return null;
		}
		
		try {
			// Create browser object and attach it to SWT shell.
			SwtBrowserCanvas browserCanvas = new SwtBrowserCanvas();
			boolean success = browserCanvas.attachBrowser(initialUrlLambda, locationChangedLambda);
			if (success) {
				return browserCanvas;
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		// On failure.
		return null;
	}
	
    /**
     * Close the browser.
     */
	public void close() {
		try {
			
			SwtThread.display.syncExec(() -> {
					
				// Close the SWT shell on dispatch thread.
				try {
					if (browser != null && !browser.isDisposed()) {
						Shell swtShell = browser.getShell();
						if (swtShell != null) {
							
							closeShell(swtShell);
						}
					}
				}
				catch (Exception e) {
					Safe.exception(e);
				}
				browser = null;
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
    
    /**
     * Create new browser and attach it to a SWT shell.
     */
    private boolean attachBrowser(Function<SwtBrowserCanvas, String> initialUrlLambda, Consumer<String> locationChangedLambda)
    		throws Exception {
    	
    	try {
	    	Obj<Boolean> success = new Obj<Boolean>(false);
	    	
	    	SwtThread.display.syncExec(() -> {
	    			
		        try {
		            // Get initial URL.
		            initialUrl = initialUrlLambda.apply(SwtBrowserCanvas.this);
		        	
		            // Create SWT shell attached to this Canvas.
		    		Shell swtShell = SWT_AWT.new_Shell(SwtThread.display, SwtBrowserCanvas.this);
		            swtShell.setLayout(new FillLayout());
		            
				    // Open the SWT shell and remember it.
		            swtShell.open();
		            swtShells.add(swtShell);
		            
		            // Create new browser in the SWT shell.
		            browser = new Browser(swtShell, SWT.NONE);
		            
		            setWebEngineListeners(browser, locationChangedLambda);
		
		            // Set URL must be executed on the SWT thread.
		            Display display = browser.getDisplay();
		            
		            display.asyncExec(() -> {
		            	browser.setUrl(initialUrl);
		            });
		            
		            success.ref = true;
		        }
		        catch (Throwable e) {
		        	Safe.exception(e);
		        }    	    	
	    	});
	    	
	    	return success.ref;
	    }
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
    }
    
	/**
	 * Set web engine listeners.
	 * @param browser
	 * @param locationChangedLambda 
	 */
	public void setWebEngineListeners(Browser browser, Consumer<String> locationChangedLambda) {
		try {
			
			this.locationChangedLambda = locationChangedLambda;
			
			browser.addLocationListener(new LocationListener() {
				
				@Override
				public void changing(LocationEvent event) {
				}
				
				@Override
				public void changed(LocationEvent event) {
					try {
						
						// Run callback.
						if (locationChangedLambda != null) {
							
							String url = event.location;
							
							Safe.invokeLater(() -> {
								locationChangedLambda.accept(url);
							});
						}
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
    
    /**
     * Reload current URL.
     */
	public void reload() {
		try {
			
			SwtThread.display.syncExec(() -> {
	    	    try {
					
					// Check browser object.
					if (browser == null) {
						return;
					}
					boolean isDisposed = browser.isDisposed();
					if (isDisposed) {
						return;
					}
					
					// Reload browser contents.
					browser.refresh();
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
     * Close the SWT shell.
     * @param swtShell
     */
    private boolean closeShell(Shell swtShell) {
    	
    	try {
	    	// Check if the SWT shell exists.
	    	boolean exists = swtShells.contains(swtShell);
	    	if (!exists) {
	    		return false;
	    	}
	    	
	    	// Remove it from the set of SWT shells.
	    	swtShells.remove(swtShell);
	    	
	    	// Close the SWT shells.
	    	if (!swtShell.isDisposed()) {
	    		swtShell.close();
	    	}
	    	return true;
	    }
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
    }
    
    /**
     * Get current URl string.
     * @return
     */
	public String getUrl() {
		
		try {
			Obj<String> currentUrl = new Obj<String>("");
			
			final long timeoutMs = 1000;
			Lock lock = new Lock();
			
	    	SwtThread.display.syncExec(() -> {
	    		try {
					
					// Check browser object.
					if (browser == null) {
						return;
					}
					
					boolean isDisposed = browser.isDisposed();
					if (isDisposed) {
						return;
					}
					
					currentUrl.ref = browser.getUrl();
					Lock.notify(lock);
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
	    	});
			
	    	Lock.waitFor(lock, timeoutMs);
			
			return currentUrl.ref;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Enable or disable SWT shells.
	 * @param enable
	 */
	public void enableSwt(boolean enable) {
		try {
			
			// Check display objects.
			boolean isDisplayDisposed = SwtThread.display.isDisposed();
			if (isDisplayDisposed) {
				return;
			}
			
			if (browser == null) {
				return;
			}
			
			// Enable or disable SWT shells.
	    	SwtThread.display.syncExec(() -> {
    			
    			try {
    				boolean isDisposed;
    				
	    			for (Shell shell : swtShells) {
	    				
	    				isDisposed = shell.isDisposed();
	    				if (!isDisposed) {
	    					shell.setEnabled(enable);
	    				}
	    			}
	    			
	    			isDisposed = browser.isDisposed();
	    			if (!isDisposed) {
	    				browser.setEnabled(enable);
	    			}
    			}
    			catch (Exception e) {
    				Safe.exception(e);
    			}
	    	});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}

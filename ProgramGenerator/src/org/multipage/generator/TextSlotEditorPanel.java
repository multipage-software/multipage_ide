/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import org.maclan.Area;
import org.maclan.Slot;
import org.maclan.help.Intellisense;
import org.maclan.help.HelpUtility;
import org.maclan.server.ServerUtilities;
import org.multipage.gui.Callback;
import org.multipage.gui.CssAnimationPanel;
import org.multipage.gui.CssBackgroundImagesPanel;
import org.multipage.gui.CssBorderImagePanel;
import org.multipage.gui.CssBorderPanel;
import org.multipage.gui.CssBorderRadiusPanel;
import org.multipage.gui.CssBoxShadowPanel;
import org.multipage.gui.CssClipPanel;
import org.multipage.gui.CssCountersPanel;
import org.multipage.gui.CssCursorPanel;
import org.multipage.gui.CssFlexPanel;
import org.multipage.gui.CssFontPanel;
import org.multipage.gui.CssKeyframesPanel;
import org.multipage.gui.CssListStylePanel;
import org.multipage.gui.CssNumberPanel;
import org.multipage.gui.CssOutlinesPanel;
import org.multipage.gui.CssPerspectiveOriginPanel;
import org.multipage.gui.CssQuotesPanel;
import org.multipage.gui.CssResourcePanel;
import org.multipage.gui.CssResourceUrlsPanel;
import org.multipage.gui.CssSpacingPanel;
import org.multipage.gui.CssTextLinePanel;
import org.multipage.gui.CssTextShadowPanel;
import org.multipage.gui.CssTransformOriginPanel;
import org.multipage.gui.CssTransformPanel;
import org.multipage.gui.CssTransitionPanel;
import org.multipage.gui.EditorValueHandler;
import org.multipage.gui.FoundAttr;
import org.multipage.gui.HtmlAnchorPanel;
import org.multipage.gui.Images;
import org.multipage.gui.StringValueEditor;
import org.multipage.gui.TextEditorPane;
import org.multipage.gui.TextPopupMenuAddIn;
import org.multipage.gui.Utility;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Panel that contains editors of text slots.
 * @author vakol
 *
 */
public class TextSlotEditorPanel extends JPanel implements SlotValueEditorPanelInterface {
	
	/**
	 * Components.
	 */
	private JPanel panelMenu;
	private JMenuBar menuBar;
	private JMenu menuSelectEditor;
	private JMenuItem menuText;
	private JMenuItem menuCssFont;
	private JMenuItem menuCssBorder;
	private JMenuItem menuCssOutlines;
	private JMenuItem menuCssBoxShadow;
	private JMenuItem menuCssBackground;
	private JMenuItem menuCssNumber;
	private JMenuItem menuCssBorderRadius;
	private JMenuItem menuCssTextShadow;
	private JMenuItem menuCssBorderImage;
	private JMenuItem menuCssClip;
	private JMenuItem menuCssFlex;
	private JMenuItem menuCssSpacing;
	private JMenuItem menuCssCounter;
	private JMenuItem menuCssListStyle;
	private JMenuItem menuCssKeyframes;
	private JMenuItem menuCssAnimation;
	private JMenuItem menuCssPerspectiveOrigin;
	private JMenuItem menuCssTransform;
	private JMenuItem menuCssTransformOrigin;
	private JMenuItem menuCssTransition;
	private JMenuItem menuCssCursor;
	private JMenuItem menuCssQuotes;
	private JMenuItem menuCssTextLine;
	private JMenuItem menuCssResource;
	private JMenuItem menuCssResourceUrl;
	private JMenuItem menuCssResourcesUrls;
	private JMenuItem menuCssMime;
	private JMenuItem menuHtmlAnchor;
	private JMenuItem menuFilePath;
	private JMenuItem menuFolderPath;

	// $hide>>$
	/**
	 * Open GHTML editor flag.
	 */
	public static boolean openHtmlEditor = true;

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Text editor panel.
	 */
	private TextEditorPane textEditorPanel;
	
	/**
	 * Selected panel reference.
	 */
	private StringValueEditor selectedPanel;
	
	/**
	 * Popup trayMenu add in.
	 */
	private TextPopupMenuAddIn plainMenuAddIn;
	private TextPopupMenuAddIn htmlMenuAddIn;

	/**
	 * Slot reference.
	 */
	private Slot slot;

	// $hide<<$

	/**
	 * Initialize components.
	 */
	private void initComponents() {
		setLayout(new BorderLayout(0, 0));

		
		panelMenu = new JPanel();
		add(panelMenu, BorderLayout.NORTH);
		panelMenu.setLayout(new BorderLayout(0, 0));
		
		menuBar = new JMenuBar();
		panelMenu.add(menuBar);
		
		menuSelectEditor = new JMenu("org.multipage.generator.menuSelectTextEditor");
		menuBar.add(menuSelectEditor);
		
		menuText = new JMenuItem("org.multipage.generator.menuShowTextEditor");
		menuText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				showTextEditor();
			}
		});
		menuSelectEditor.add(menuText);
		
		menuCssFont = new JMenuItem("org.multipage.generator.menuShowCssFontEditor");
		menuCssFont.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showCssFontEditor();
			}
		});
		menuSelectEditor.add(menuCssFont);
		
		menuCssBorder = new JMenuItem("org.multipage.generator.menuShowCssBorder");
		menuCssBorder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showCssBorderEditor();
			}
		});
		menuSelectEditor.add(menuCssBorder);
		
		menuCssOutlines = new JMenuItem("org.multipage.generator.menuShowCssOutlines");
		menuCssOutlines.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showCssOutlinesEditor();
			}
		});
		menuSelectEditor.add(menuCssOutlines);
		
		menuCssBoxShadow = new JMenuItem("org.multipage.generator.menuShowCssBoxShadow");
		menuCssBoxShadow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				showCssBoxShadowEditor();
			}
		});
		menuSelectEditor.add(menuCssBoxShadow);
		
		menuCssBackground = new JMenuItem("org.multipage.generator.menuShowCssBackground");
		menuCssBackground.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				showCssBackgroundEditor();
			}
		});
		
		menuCssTextShadow = new JMenuItem("org.multipage.generator.menuShowCssTextShadow");
		menuCssTextShadow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showCssTextShadowEditor();
			}
		});
		menuSelectEditor.add(menuCssTextShadow);
		menuSelectEditor.add(menuCssBackground);
		
		menuCssNumber = new JMenuItem("org.multipage.generator.menuShowCssNumber");
		menuCssNumber.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				showCssNumberEditor();
			}
		});
		menuSelectEditor.add(menuCssNumber);
		
		menuCssBorderRadius = new JMenuItem("org.multipage.generator.menuShowCssBorderRadius");
		menuCssBorderRadius.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showCssBorderRadiusEditor();
			}
		});
		menuSelectEditor.add(menuCssBorderRadius);
		
		menuCssBorderImage = new JMenuItem("org.multipage.generator.menuCssBorderImage");
		menuCssBorderImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showCssBorderImageEditor();
			}
		});
		menuSelectEditor.add(menuCssBorderImage);
		
		menuCssClip = new JMenuItem("org.multipage.generator.menuCssClip");
		menuCssClip.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showCssClipEditor();
			}
		});
		menuSelectEditor.add(menuCssClip);

		menuCssFlex = new JMenuItem("org.multipage.generator.menuCssFlex");
		menuCssFlex.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showCssFlexEditor();
			}
		});
		menuSelectEditor.add(menuCssFlex);
		
		menuCssSpacing = new JMenuItem("org.multipage.generator.menuCssSpacing");
		menuCssSpacing.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showCssSpacingEditor();
			}
		});
		menuSelectEditor.add(menuCssSpacing);
		
		menuCssCounter = new JMenuItem("org.multipage.generator.menuCssCounter");
		menuCssCounter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showCssCounterEditor();
			}
		});
		menuSelectEditor.add(menuCssCounter);
		
		menuCssListStyle = new JMenuItem("org.multipage.generator.menuCssListStyle");
		menuCssListStyle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showCssListStyleEditor();
			}
		});
		menuSelectEditor.add(menuCssListStyle);
		
		menuCssKeyframes = new JMenuItem("org.multipage.generator.menuCssKeyframes");
		menuCssKeyframes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showCssKeyframesEditor();
			}
		});
		menuSelectEditor.add(menuCssKeyframes);
		
		menuCssAnimation = new JMenuItem("org.multipage.generator.menuCssAnimation");
		menuCssAnimation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showCssAnimationEditor();
			}
		});
		menuSelectEditor.add(menuCssAnimation);

		menuCssPerspectiveOrigin = new JMenuItem("org.multipage.generator.menuCssPerspectiveOrigin");
		menuCssPerspectiveOrigin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showCssPerspectiveOriginEditor();
			}
		});
		menuSelectEditor.add(menuCssPerspectiveOrigin);
		
		menuCssTransform = new JMenuItem("org.multipage.generator.menuCssTransform");
		menuCssTransform.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showCssTransformEditor();
			}
		});
		menuSelectEditor.add(menuCssTransform);
		
		menuCssTransformOrigin = new JMenuItem("org.multipage.generator.menuCssTransformOrigin");
		menuCssTransformOrigin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showCssTransformOriginEditor();
			}
		});
		menuSelectEditor.add(menuCssTransformOrigin);
		
		menuCssTransition = new JMenuItem("org.multipage.generator.menuCssTransition");
		menuCssTransition.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showCssTransitionEditor();
			}
		});
		menuSelectEditor.add(menuCssTransition);
		
		menuCssCursor = new JMenuItem("org.multipage.generator.menuCssCursor");
		menuCssCursor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showCssCursorEditor();
			}
		});
		menuSelectEditor.add(menuCssCursor);
		
		menuCssQuotes = new JMenuItem("org.multipage.generator.menuCssQuotes");
		menuCssQuotes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showCssQuotesEditor();
			}
		});
		menuSelectEditor.add(menuCssQuotes);
		
		menuCssTextLine = new JMenuItem("org.multipage.generator.menuCssTextLine");
		menuCssTextLine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showCssTextLineEditor();
			}
		});
		menuSelectEditor.add(menuCssTextLine);
		
		menuCssResource = new JMenuItem("org.multipage.generator.menuCssResource");
		menuCssResource.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showCssResourceEditor();
			}
		});
		menuSelectEditor.add(menuCssResource);
		
		menuCssResourceUrl = new JMenuItem("org.multipage.generator.menuCssResourceUrl");
		menuCssResourceUrl.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showCssResourceUrlEditor();
			}
		});
		menuSelectEditor.add(menuCssResourceUrl);
				
		menuCssResourcesUrls = new JMenuItem("org.multipage.generator.menuCssResourcesUrls");
		menuCssResourcesUrls.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showCssResourcesUrlsEditor();
			}
		});
		menuSelectEditor.add(menuCssResourcesUrls);
		
		menuCssMime = new JMenuItem("org.multipage.generator.menuCssMime");
		menuCssMime.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showCssMimeEditor();
			}
		});
		menuSelectEditor.add(menuCssMime);
		
		menuHtmlAnchor = new JMenuItem("org.multipage.generator.menuHtmlAnchor");
		menuHtmlAnchor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showHtmlAnchorEditor(StringValueEditor.meansHtmlAnchorAreaRef);
			}
		});
		menuSelectEditor.add(menuHtmlAnchor);
		
		menuFilePath = new JMenuItem("org.multipage.generator.menuFilePath");
		menuFilePath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showFileEditor(StringValueEditor.meansFile);
			}
		});
		menuSelectEditor.add(menuFilePath);
		
		menuFolderPath = new JMenuItem("org.multipage.generator.menuFolderPath");
		menuFolderPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showFolderEditor(StringValueEditor.meansFolder);
			}
		});
		menuSelectEditor.add(menuFolderPath);
	}
	
	/**
	 * Create the panel.
	 * @param useHtmlEditor 
	 * @param slot 
	 */
	public TextSlotEditorPanel(Window parentWindow, boolean useHtmlEditor, Slot slot) {
		
		try {
			initComponents();
			
			// $hide>>$
			this.slot = slot;
			createTextEditorPanel(parentWindow, useHtmlEditor);
			
			selectedPanel = textEditorPanel;
			add(textEditorPanel, BorderLayout.CENTER);
			
			// If the application is Generator and the slot is not a user slot,
			// disable editor selection.
			boolean disableEditorSelection = !ProgramGenerator.isExtensionToBuilder() && !slot.isUserDefined();
			
			// Disable editor selection for Builder.
			if (disableEditorSelection) {
				panelMenu.setVisible(false);
			}
			
			localize();
			setIcons();
			
			// Use intellisense for the text editor.
			Intellisense.applyTo(textEditorPanel, (helpPageAlias, fragmentAlias) -> GeneratorMainFrame.displayOnlineArea(HelpUtility.maclanReference, helpPageAlias, fragmentAlias));
			
			// $hide<<$
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Set icons.
	 */
	private void setIcons() {
		try {
			
			menuSelectEditor.setIcon(Images.getIcon("org/multipage/generator/images/edit.png"));
			menuText.setIcon(Images.getIcon("org/multipage/generator/images/edit_text.png"));
			menuCssFont.setIcon(Images.getIcon("org/multipage/gui/images/font_icon.png"));
			menuCssBorder.setIcon(Images.getIcon("org/multipage/gui/images/border.png"));
			menuCssOutlines.setIcon(Images.getIcon("org/multipage/gui/images/outlines.png"));
			menuCssBoxShadow.setIcon(Images.getIcon("org/multipage/gui/images/shadow.png"));
			menuCssTextShadow.setIcon(Images.getIcon("org/multipage/gui/images/text_shadow.png"));
			menuCssBackground.setIcon(Images.getIcon("org/multipage/gui/images/background.png"));
			menuCssNumber.setIcon(Images.getIcon("org/multipage/gui/images/number.png"));
			menuCssBorderRadius.setIcon(Images.getIcon("org/multipage/gui/images/radius.png"));
			menuCssBorderImage.setIcon(Images.getIcon("org/multipage/gui/images/border_image.png"));
			menuCssClip.setIcon(Images.getIcon("org/multipage/gui/images/clip.png"));
			menuCssFlex.setIcon(Images.getIcon("org/multipage/gui/images/flex.png"));
			menuCssSpacing.setIcon(Images.getIcon("org/multipage/gui/images/spacing.png"));
			menuCssCounter.setIcon(Images.getIcon("org/multipage/gui/images/increment.png"));
			menuCssListStyle.setIcon(Images.getIcon("org/multipage/gui/images/list.png"));
			menuCssKeyframes.setIcon(Images.getIcon("org/multipage/gui/images/keyframes.png"));
			menuCssAnimation.setIcon(Images.getIcon("org/multipage/gui/images/animation.png"));
			menuCssPerspectiveOrigin.setIcon(Images.getIcon("org/multipage/gui/images/perspective_origin.png"));
			menuCssTransform.setIcon(Images.getIcon("org/multipage/gui/images/transform.png"));
			menuCssTransformOrigin.setIcon(Images.getIcon("org/multipage/gui/images/transform_origin.png"));
			menuCssTransition.setIcon(Images.getIcon("org/multipage/gui/images/transition.png"));
			menuCssCursor.setIcon(Images.getIcon("org/multipage/gui/images/cursor.png"));
			menuCssQuotes.setIcon(Images.getIcon("org/multipage/gui/images/quotes.png"));
			menuCssTextLine.setIcon(Images.getIcon("org/multipage/gui/images/text_line.png"));
			menuCssResource.setIcon(Images.getIcon("org/multipage/gui/images/resource.png"));
			menuCssResourceUrl.setIcon(Images.getIcon("org/multipage/gui/images/url.png"));
			menuCssResourcesUrls.setIcon(Images.getIcon("org/multipage/gui/images/urls.png"));
			menuCssMime.setIcon(Images.getIcon("org/multipage/generator/images/mime_icon.png"));
			menuHtmlAnchor.setIcon(Images.getIcon("org/multipage/gui/images/anchor.png"));
			menuFilePath.setIcon(Images.getIcon("org/multipage/gui/images/folder.png"));
			menuFolderPath.setIcon(Images.getIcon("org/multipage/gui/images/folder.png"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Show text editor.
	 */
	protected void showTextEditor() {
		try {
			
			selectedPanel = textEditorPanel;
			
			remove(1);
			add(textEditorPanel, BorderLayout.CENTER);
			
			validate();
			updateUI();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Show CSS font editor.
	 */
	protected void showCssFontEditor() {
		try {
			
			selectedPanel = new CssFontPanel(null);
			
			remove(1);
			add(selectedPanel.getComponent(), BorderLayout.CENTER);
			
			validate();
			updateUI();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Show CSS border editor.
	 */
	protected void showCssBorderEditor() {
		try {
			
			selectedPanel = new CssBorderPanel(null);
			
			remove(1);
			add(selectedPanel.getComponent(), BorderLayout.CENTER);
			
			validate();
			updateUI();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Show CSS outlines editor.
	 */
	protected void showCssOutlinesEditor() {
		try {
			
			selectedPanel = new CssOutlinesPanel(null);
			
			remove(1);
			add(selectedPanel.getComponent(), BorderLayout.CENTER);
			
			validate();
			updateUI();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Show CSS box shadow editor.
	 */
	protected void showCssBoxShadowEditor() {
		try {
			
			selectedPanel = new CssBoxShadowPanel(null);
			
			remove(1);
			add(selectedPanel.getComponent(), BorderLayout.CENTER);
			
			validate();
			updateUI();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Show CSS text shadow editor.
	 */
	protected void showCssTextShadowEditor() {
		try {
			
			selectedPanel = new CssTextShadowPanel(null);
			
			remove(1);
			add(selectedPanel.getComponent(), BorderLayout.CENTER);
			
			validate();
			updateUI();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Show CSS background editor.
	 */
	protected void showCssBackgroundEditor() {
		try {
			
			CssBackgroundImagesPanel editor = new CssBackgroundImagesPanel(null);
			editor.setResourceNameCallback(new Callback() {
				@Override
				public Object run(Object input) {
					
					try {
						// Select slot area resource name
						String selectedResourceName = selectSlotAreaResourceName(slot, editor);
						if (selectedResourceName != null) {
							
							return selectedResourceName;
						}
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					// On cancel return -1.
					return -1;
				}
			});
			
			selectedPanel = editor;
			
			remove(1);
			add(selectedPanel.getComponent(), BorderLayout.CENTER);
			
			validate();
			updateUI();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Select slot area resource name.
	 * @param slot
	 * @param parent 
	 * @return
	 */
	protected String selectSlotAreaResourceName(Slot slot, Component parent) {
		
		try {
			Area area = (Area) slot.getHolder();
			if (area == null) {
				return null;
			}
			
			// Update area from model.
			area = ProgramGenerator.getArea(area.getId());
			
			LinkedList<String> resourceNames = area.getResourceNames();
			
			// Select resource name.
			String selectedResourceName = SelectStringDialog.showDialog(parent, resourceNames,
					"org/multipage/generator/images/resource.png", "org.multipage.generator.textSelectResourceName",
					Resources.getString("org.multipage.generator.messageSelectResourceName"));
			
			return selectedResourceName;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Show CSS number editor.
	 */
	protected void showCssNumberEditor() {
		try {
			
			selectedPanel = new CssNumberPanel(null);
			
			remove(1);
			add(selectedPanel.getComponent(), BorderLayout.CENTER);
			
			validate();
			updateUI();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Show CSS border radius editor.
	 */
	protected void showCssBorderRadiusEditor() {
		try {
			
			selectedPanel = new CssBorderRadiusPanel(null);
			
			remove(1);
			add(selectedPanel.getComponent(), BorderLayout.CENTER);
			
			validate();
			updateUI();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Show CSS clip editor.
	 */
	protected void showCssClipEditor() {
		try {
			
			selectedPanel = new CssClipPanel(null);
			
			remove(1);
			add(selectedPanel.getComponent(), BorderLayout.CENTER);
			
			validate();
			updateUI();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Show border image editor.
	 */
	private void showCssBorderImageEditor() {
		try {
			
			CssBorderImagePanel editor = new CssBorderImagePanel(null);
			editor.setResourceNameCallback(new Callback() {
				@Override
				public Object run(Object input) {
					
					try {
						// Select slot area resource name
						String selectedResourceName = selectSlotAreaResourceName(slot, editor);
						if (selectedResourceName != null) {
							
							return selectedResourceName;
						}
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					// On cancel return -1.
					return -1;
				}
			});
			
			selectedPanel = editor;
			
			remove(1);
			add(selectedPanel.getComponent(), BorderLayout.CENTER);
			
			validate();
			updateUI();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
			
	}

	/**
	 * Show CSS flex editor.
	 */
	protected void showCssFlexEditor() {
		try {
			
			selectedPanel = new CssFlexPanel(null);
			
			remove(1);
			add(selectedPanel.getComponent(), BorderLayout.CENTER);
			
			validate();
			updateUI();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Show CSS spacing editor.
	 */
	protected void showCssSpacingEditor() {
		try {
			
			selectedPanel = new CssSpacingPanel(null);
			
			remove(1);
			add(selectedPanel.getComponent(), BorderLayout.CENTER);
			
			validate();
			updateUI();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Show CSS counter editor.
	 */
	protected void showCssCounterEditor() {
		try {
			
			selectedPanel = new CssCountersPanel(null);
			
			remove(1);
			add(selectedPanel.getComponent(), BorderLayout.CENTER);
			
			validate();
			updateUI();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Show CSS animation editor.
	 */
	protected void showCssAnimationEditor() {
		try {
			
			selectedPanel = new CssAnimationPanel(null);
			
			remove(1);
			add(selectedPanel.getComponent(), BorderLayout.CENTER);
			
			validate();
			updateUI();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Show CSS list style editor.
	 */
	protected void showCssListStyleEditor() {
		try {
			
			CssListStylePanel editor = new CssListStylePanel(null);
			editor.setResourceNameCallback(new Callback() {
				@Override
				public Object run(Object input) {
					
					try {
						// Select slot area resource name
						String selectedResourceName = selectSlotAreaResourceName(slot, editor);
						if (selectedResourceName != null) {
							
							return selectedResourceName;
						}
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					// On cancel return -1.
					return -1;
				}
			});
			selectedPanel = editor;
			
			remove(1);
			add(selectedPanel.getComponent(), BorderLayout.CENTER);
			
			validate();
			updateUI();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Show CSS keyframes editor.
	 */
	protected void showCssKeyframesEditor() {
		try {
			
			selectedPanel = new CssKeyframesPanel(null);
			
			remove(1);
			add(selectedPanel.getComponent(), BorderLayout.CENTER);
			
			validate();
			updateUI();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Show CSS perspective origin editor.
	 */
	protected void showCssPerspectiveOriginEditor() {
		try {
			
			selectedPanel = new CssPerspectiveOriginPanel(null);
			
			remove(1);
			add(selectedPanel.getComponent(), BorderLayout.CENTER);
			
			validate();
			updateUI();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Show CSS transform editor.
	 */
	protected void showCssTransformEditor() {
		try {
			
			selectedPanel = new CssTransformPanel(null);
			
			remove(1);
			add(selectedPanel.getComponent(), BorderLayout.CENTER);
			
			validate();
			updateUI();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Show CSS transform origin editor.
	 */
	protected void showCssTransformOriginEditor() {
		try {
			
			selectedPanel = new CssTransformOriginPanel(null);
			
			remove(1);
			add(selectedPanel.getComponent(), BorderLayout.CENTER);
			
			validate();
			updateUI();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Show CSS transition editor.
	 */
	protected void showCssTransitionEditor() {
		try {
			
			selectedPanel = new CssTransitionPanel(null);
			
			remove(1);
			add(selectedPanel.getComponent(), BorderLayout.CENTER);
			
			validate();
			updateUI();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Show CSS cursor editor.
	 */
	protected void showCssCursorEditor() {
		try {
			
			CssCursorPanel editor = new CssCursorPanel(null);
			editor.setResourceNameCallback(new Callback() {
				@Override
				public Object run(Object input) {
					
					try {
						// Select slot area resource name
						String selectedResourceName = selectSlotAreaResourceName(slot, editor);
						if (selectedResourceName != null) {
							
							return selectedResourceName;
						}
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					// On cancel return -1.
					return -1;
				}
			});
			selectedPanel = editor;
			
			remove(1);
			add(selectedPanel.getComponent(), BorderLayout.CENTER);
			
			validate();
			updateUI();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Show CSS quotes editor.
	 */
	protected void showCssQuotesEditor() {
		try {
			
			selectedPanel = new CssQuotesPanel(null);
			
			remove(1);
			add(selectedPanel.getComponent(), BorderLayout.CENTER);
			
			validate();
			updateUI();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Show CSS text line editor.
	 */
	protected void showCssTextLineEditor() {
		try {
			
			selectedPanel = new CssTextLinePanel(null);
			
			remove(1);
			add(selectedPanel.getComponent(), BorderLayout.CENTER);
			
			validate();
			updateUI();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Show CSS resource editor.
	 */
	protected void showCssResourceEditor() {
		try {
			
			CssResourcePanel editor = new CssResourcePanel(null, false);
			editor.setResourceNameCallback(new Callback() {
				@Override
				public Object run(Object input) {
					
					try {
						// Select slot area resource name
						String selectedResourceName = selectSlotAreaResourceName(slot, editor);
						if (selectedResourceName != null) {
							
							return selectedResourceName;
						}
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					// On cancel return -1.
					return -1;
				}
			});
			selectedPanel = editor;
	
			remove(1);
			add(selectedPanel.getComponent(), BorderLayout.CENTER);
			
			validate();
			updateUI();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Show CSS resource URL editor.
	 */
	protected void showCssResourceUrlEditor() {
		try {
			
			CssResourcePanel editor = new CssResourcePanel(null, true);
			editor.setResourceNameCallback(new Callback() {
				@Override
				public Object run(Object input) {
					
					try {
						// Select slot area resource name
						String selectedResourceName = selectSlotAreaResourceName(slot, editor);
						if (selectedResourceName != null) {
							
							return selectedResourceName;
						}
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					// On cancel return -1.
					return -1;
				}
			});
			selectedPanel = editor;
	
			remove(1);
			add(selectedPanel.getComponent(), BorderLayout.CENTER);
			
			validate();
			updateUI();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Show CSS resources URLs editor.
	 */
	protected void showCssResourcesUrlsEditor() {
		try {
			
			CssResourceUrlsPanel editor = new CssResourceUrlsPanel(null);
			editor.setResourceNameCallback(new Callback() {
				@Override
				public Object run(Object input) {
					
					try {
						// Select slot area resource name
						String selectedResourceName = selectSlotAreaResourceName(slot, editor);
						if (selectedResourceName != null) {
							
							return selectedResourceName;
						}
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					// On cancel return -1.
					return -1;
				}
			});
			selectedPanel = editor;
	
			remove(1);
			add(selectedPanel.getComponent(), BorderLayout.CENTER);
			
			validate();
			updateUI();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Show CSS MIME editor.
	 */
	protected void showCssMimeEditor() {
		try {
			
			selectedPanel = new CssMimePanel(null);
			
			remove(1);
			add(selectedPanel.getComponent(), BorderLayout.CENTER);
			
			validate();
			updateUI();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Show HTML anchor editor.
	 * @param valueMeaning 
	 */
	protected void showHtmlAnchorEditor(String valueMeaning) {
		try {
			
			// Create editor.
			final Component thisComponent = this;
			
			HtmlAnchorPanel editor = new HtmlAnchorPanel(null);
			
			// Set value meaning.
			editor.setValueMeaning(valueMeaning);
			
			editor.setAreaAliasHandler(new EditorValueHandler() {
				
				String areaAlias;
				
				@Override
				public boolean ask() {
					
					try {
						// Gets area alias
						areaAlias = SelectAreaDialog.showDialog(Utility.findWindow(thisComponent), null);
						return areaAlias != null;
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return false;
				}
	
				@Override
				public String getText() {
					
					return areaAlias;
				}
	
				@Override
				public String getValue() {
					
					try {
						if (areaAlias == null) {
							return null;
						}
						return String.format("[@REM] areaAlias is %s[/@REM]", areaAlias);
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return null;
				}
			});
			
			editor.setResourceNameHandler(new EditorValueHandler() {
				
				String areaResource;
				
				@Override
				public boolean ask() {
					
					try {
						// Select area resource name
						areaResource = selectSlotAreaResourceName(slot, editor);
						return areaResource != null;
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return false;
				}
	
				@Override
				public String getText() {
					
					return areaResource;
				}
	
				@Override
				public String getValue() {
					
					try {
						return String.format("[@REM]resource[/@REM][@URL res=#%s]", areaResource);
					}
					catch (Throwable e) {
						Safe.exception(e);
					}
					return "";
				}
			});
			
			selectedPanel = editor;
			
			remove(1);
			add(selectedPanel.getComponent(), BorderLayout.CENTER);
			
			validate();
			updateUI();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
			
	}
	
	/**
	 * Show file path editor
	 * @param valueMeaning
	 */
	protected void showFileEditor(String valueMeaning) {
		try {
			
			FilePanel filePanel = new FilePanel(null);
			Area area = (Area) slot.getHolder();
			filePanel.setArea(area);
			selectedPanel = filePanel;
			
			remove(1);
			add(selectedPanel.getComponent(), BorderLayout.CENTER);
			
			validate();
			updateUI();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Show folder path editor
	 * @param valueMeaning
	 */
	protected void showFolderEditor(String valueMeaning) {
		try {
			
			selectedPanel = new FolderPanel(null);
			
			remove(1);
			add(selectedPanel.getComponent(), BorderLayout.CENTER);
			
			validate();
			updateUI();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Show path editor
	 */
	private void showPathEditor() {
		try {
			
			PathPanel pathPanel = new PathPanel(null);
			selectedPanel = pathPanel;
			
			// Set area reference.
			Area area = (Area) slot.getHolder();
			pathPanel.setArea(area);
			
			remove(1);
			add(selectedPanel.getComponent(), BorderLayout.CENTER);
			
			validate();
			updateUI();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Show editor.
	 * @param valueMeaning
	 */
	private void showEditor(String valueMeaning) {
		try {
			
			if (valueMeaning == null) {
				showTextEditor();
				return;
			}
			
			if (valueMeaning.equals(StringValueEditor.meansCssBorder)) {
				showCssBorderEditor();
				return;
			}
			
			if (valueMeaning.equals(StringValueEditor.meansCssFont)) {
				showCssFontEditor();
				return;
			}
			
			if (valueMeaning.equals(StringValueEditor.meansCssOutlines)) {
				showCssOutlinesEditor();
				return;
			}
			if (valueMeaning.equals(StringValueEditor.meansCssBoxShadow)) {
				showCssBoxShadowEditor();
				return;
			}
			if (valueMeaning.equals(StringValueEditor.meansCssTextShadow)) {
				showCssTextShadowEditor();
				return;
			}
			if (valueMeaning.equals(StringValueEditor.meansCssBackground)) {
				showCssBackgroundEditor();
				return;
			}
			if (valueMeaning.equals(StringValueEditor.meansCssNumber)) {
				showCssNumberEditor();
				return;
			}
			if (valueMeaning.equals(StringValueEditor.meansCssBorderRadius)) {
				showCssBorderRadiusEditor();
				return;
			}
			if (valueMeaning.equals(StringValueEditor.meansCssClip)) {
				showCssClipEditor();
				return;
			}
			if (valueMeaning.equals(StringValueEditor.meansCssBorderImage)) {
				showCssBorderImageEditor();
				return;
			}
			if (valueMeaning.equals(StringValueEditor.meansCssFlex)) {
				showCssFlexEditor();
				return;
			}
			if (valueMeaning.equals(StringValueEditor.meansCssSpacing)) {
				showCssSpacingEditor();
				return;
			}
			if (valueMeaning.equals(StringValueEditor.meansCssCounter)) {
				showCssCounterEditor();
				return;
			}
			if (valueMeaning.equals(StringValueEditor.meansCssListStyle)) {
				showCssListStyleEditor();
				return;
			}
			if (valueMeaning.equals(StringValueEditor.meansCssKeyframes)) {
				showCssKeyframesEditor();
				return;
			}
			if (valueMeaning.equals(StringValueEditor.meansCssAnimation)) {
				showCssAnimationEditor();
				return;
			}
			if (valueMeaning.equals(StringValueEditor.meansCssPerspectiveOrigin)) {
				showCssPerspectiveOriginEditor();
				return;
			}
			if (valueMeaning.equals(StringValueEditor.meansCssTransform)) {
				showCssTransformEditor();
				return;
			}
			if (valueMeaning.equals(StringValueEditor.meansCssTransformOrigin)) {
				showCssTransformOriginEditor();
				return;
			}
			if (valueMeaning.equals(StringValueEditor.meansCssTransition)) {
				showCssTransitionEditor();
				return;
			}
			if (valueMeaning.equals(StringValueEditor.meansCssCursor)) {
				showCssCursorEditor();
				return;
			}
			if (valueMeaning.equals(StringValueEditor.meansCssQuotes)) {
				showCssQuotesEditor();
				return;
			}
			if (valueMeaning.equals(StringValueEditor.meansCssTextLine)) {
				showCssTextLineEditor();
				return;
			}
			if (valueMeaning.equals(StringValueEditor.meansCssResource)) {
				showCssResourceEditor();
				return;
			}
			if (valueMeaning.equals(StringValueEditor.meansCssUrlResource)) {
				showCssResourceUrlEditor();
				return;
			}
			if (valueMeaning.equals(StringValueEditor.meansCssUrlsResources)) {
				showCssResourcesUrlsEditor();
				return;
			}
			if (valueMeaning.equals(StringValueEditor.meansCssMime)) {
				showCssMimeEditor();
				return;
			}
			if (StringValueEditor.meansHtmlAnchor(valueMeaning)) {
				showHtmlAnchorEditor(valueMeaning);
				return;
			}
			if (valueMeaning.equals(StringValueEditor.meansFile)) {
				showFileEditor(valueMeaning);
				return;
			}
			if (valueMeaning.equals(StringValueEditor.meansFolder)) {
				showFolderEditor(valueMeaning);
				return;
			}
			if (valueMeaning.equals(StringValueEditor.meansPath)) {
				showPathEditor();
				return;
			}
					
			// Otherwise.
			showTextEditor();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
			
	}

	/**
	 * Localize.
	 */
	private void localize() {
		try {
			
			Utility.localize(menuSelectEditor);
			Utility.localize(menuText);
			Utility.localize(menuCssFont);
			Utility.localize(menuCssBorder);
			Utility.localize(menuCssOutlines);
			Utility.localize(menuCssBoxShadow);
			Utility.localize(menuCssTextShadow);
			Utility.localize(menuCssBackground);
			Utility.localize(menuCssNumber);
			Utility.localize(menuCssBorderRadius);
			Utility.localize(menuCssBorderImage);
			Utility.localize(menuCssClip);
			Utility.localize(menuCssFlex);
			Utility.localize(menuCssSpacing);
			Utility.localize(menuCssCounter);
			Utility.localize(menuCssListStyle);
			Utility.localize(menuCssKeyframes);
			Utility.localize(menuCssAnimation);
			Utility.localize(menuCssPerspectiveOrigin);
			Utility.localize(menuCssTransform);
			Utility.localize(menuCssTransformOrigin);
			Utility.localize(menuCssTransition);
			Utility.localize(menuCssCursor);
			Utility.localize(menuCssQuotes);
			Utility.localize(menuCssTextLine);
			Utility.localize(menuCssResource);
			Utility.localize(menuCssResourceUrl);
			Utility.localize(menuCssResourcesUrls);
			Utility.localize(menuCssMime);
			Utility.localize(menuHtmlAnchor);
			Utility.localize(menuFilePath);
			Utility.localize(menuFolderPath);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Create text editor panel.
	 * @param parentWindow
	 * @param useHtmlEditor
	 * @param slot 
	 */
	@SuppressWarnings("serial")
	private void createTextEditorPanel(Window parentWindow,
			boolean useHtmlEditor) {
		try {
			
			// Create text editor.
			textEditorPanel = new TextEditorPane(parentWindow, useHtmlEditor) {
				
				// Constructor.
				{
					try {
						
						// Add pop-up trayMenu add-in.
						plainMenuAddIn = ProgramGenerator.newGeneratorTextPopupMenuAddIn(slot);
						addPopupMenusPlain(plainMenuAddIn);
						htmlMenuAddIn = ProgramGenerator.newGeneratorTextPopupMenuAddIn(slot);
						addPopupMenusHtml(htmlMenuAddIn);
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
	
				/**
				 * Load dialog.
				 */
				@Override
				protected void loadDialog() {
					try {
						
						super.loadDialog();
						selectHtmlEditor(openHtmlEditor);
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
				
				/**
				 * Save dialog.
				 */
				@Override
				protected void saveDialog() {
					try {
						
						super.saveDialog();
						openHtmlEditor = tabbedPane.getSelectedIndex() == 1;
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
	
				/**
				 * Highlight script commands.
				 * 
				 *  (non-Javadoc)
				 * @see org.multipage.gui.TextEditorPane#highlightScriptCommands(javax.swing.JTextPane)
				 */
				@Override
				protected void highlightScriptCommands(JTextPane textPane) {
					try {
						
						// Delegate call.
						ServerUtilities.highlightScriptCommands(textPane, CustomizedColors.get(ColorId.SCRIPT_COMMAND_HIGHLIGHT));
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			};
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Update information.
	 */
	public void updateInformation() {
		try {
			
			plainMenuAddIn.updateInformation();
			htmlMenuAddIn.updateInformation();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}


	/**
	 * Get value.
	 */
	@Override
	public Object getValue() {
		
		try {
			return selectedPanel.getStringValue();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Set value.
	 */
	@Override
	public void setValue(Object value) {
		try {
			
			selectedPanel.setStringValue(value.toString());
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Clear editor.
	 */
	public void clear() {
		try {
			
			textEditorPanel.setText("");
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set default value state.
	 */
	@Override
	public void setDefault(boolean isDefault) {
		
		// Nothing to do.
	}

	/**
	 * Get text font.
	 * @return
	 */
	public Font getTextFont() {
		
		try {
			return textEditorPanel.getTextFont();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Set text font.
	 * @param font
	 */
	public void setTextFont(Font font) {
		try {
			
			textEditorPanel.setTextFont(font);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Highlight found text.
	 * @param foundAttr
	 */
	public void highlightFound(FoundAttr foundAttr) {
		try {
			
			textEditorPanel.highlightFound(foundAttr);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get value meaning.
	 */
	@Override
	public String getValueMeaning() {
		
		try {
			return selectedPanel.getValueMeaning();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Set value meaning.
	 * @param valueMeaning
	 */
	public void setValueMeaning(String valueMeaning) {
		try {
			
			showEditor(valueMeaning);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set grayed controls if there are default.
	 * @param isDefault 
	 * @return
	 */
	public boolean setControlsGrayed(boolean isDefault) {
		
		try {
			// Delegate call to selected panel.
			if (selectedPanel != null) {
				return selectedPanel.setControlsGrayed(isDefault);
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}
}

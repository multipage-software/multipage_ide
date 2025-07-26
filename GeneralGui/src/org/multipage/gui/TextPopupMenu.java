/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 26-04-2017
 *
 */

package org.multipage.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.text.JTextComponent;

import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Popup menu for text editor.
 * @author vakol
 *
 */
public class TextPopupMenu extends JPopupMenu {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Enable CSS editors.
	 */
	public static boolean enableCss = true;

	/**
	 * Text component.
	 */
	private JTextComponent textComponent;
	
	/**
	 * Menu items.
	 */
	private JMenuItem menuCut;
	private JMenuItem menuCopy;
	private JMenuItem menuPaste;
	private JSeparator separator;
	private JMenuItem menuSelectAll;
    private JMenu menuEdit;
    private JMenu menuInsert;
    private JMenuItem menuEditMoveLeft;
    private JMenuItem menuEditMoveRight;
	private JMenuItem menuInsertDateTime;
	private JMenuItem menuInsertAnchor;
	private JMenuItem menuInsertLoremIpsum;
	private JMenuItem menuInsertCssFont;
	private JMenuItem menuInsertCssBorder;
	private JMenuItem menuInsertCssOutlines;
	private JMenuItem menuInsertCssBoxShadow;
	private JMenuItem menuInsertCssBackground;
	private JMenuItem menuInsertCssNumber;
	private JMenuItem menuInsertCssBorderRadius;
	private JMenuItem menuInsertCssTextShadow;
	private JMenuItem menuInsertCssBorderImage;
	private JMenuItem menuInsertCssClip;
	private JMenuItem menuInsertCssFlex;
	private JMenuItem menuInsertCssSpacing;
	private JMenuItem menuInsertCssCounter;
	private JMenuItem menuInsertCssListStyle;
	private JMenuItem menuInsertCssKeyframes;
	private JMenuItem menuInsertCssAnimation;
	private JMenuItem menuInsertCssPerspectiveOrigin;
	private JMenuItem menuInsertCssTransform;
	private JMenuItem menuInsertCssTransformOrigin;
	private JMenuItem menuInsertCssTransition;
	private JMenuItem menuInsertCssCursor;
	private JMenuItem menuInsertCssQuotes;
	private JMenuItem menuInsertCssTextLine;
	private JMenuItem menuInsertCssResource;
	private JMenuItem menuInsertCssResourceUrl;
	private JMenuItem menuInsertCssResourcesUrls;
	
	/**
	 * Constructor.
	 * @param textComponent 
	 */
	public TextPopupMenu(JTextComponent textComponent) {
		try {
			
			this.textComponent = textComponent;
			// Create menu.
			createPopupMenu();
			// Localize components.
			localize();
			// Set icons.
			setIcons();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Create popup menu.
	 */
	private void createPopupMenu() {
		try {
			
			boolean isEditable = textComponent.isEditable();
				
			addPopup(textComponent, this);
			
			menuCut = new JMenuItem("textCut");
			menuCut.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						
						onCutText();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			add(menuCut);
			
			menuCopy = new JMenuItem("textCopy");
			menuCopy.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						
						onCopyText();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			add(menuCopy);
			
			menuPaste = new JMenuItem("textPaste");
			menuPaste.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
                        
                        onPasteText();
                    }
                    catch(Throwable expt) {
                    	Safe.exception(expt);
                    };
				}
			});
			add(menuPaste);
			
			separator = new JSeparator();
			add(separator);
	
			menuEdit = new JMenu("org.multipage.gui.textEdit");
			if (isEditable) {
				add(menuEdit);
			}
			
			menuInsert = new JMenu("org.multipage.gui.textInsert");
			if (isEditable) {
				add(menuInsert);
			}
			
			separator = new JSeparator();
			add(separator);
			
			menuSelectAll = new JMenuItem("org.multipage.gui.textSelectAll");
			menuSelectAll.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
											
						onSelectAll();				
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			add(menuSelectAll);
			
			menuEditMoveLeft = new JMenuItem("org.multipage.gui.textMoveLeft");
			menuEdit.add(menuEditMoveLeft);
			menuEditMoveLeft.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						
						onMoveLeft();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			
			menuEditMoveRight = new JMenuItem("org.multipage.gui.textMoveRight");
			menuEdit.add(menuEditMoveRight);
			menuEditMoveRight.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						
						onMoveRight();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			
			menuInsertDateTime = new JMenuItem("org.multipage.gui.textInsertDataTime");
			menuInsert.add(menuInsertDateTime);
			menuInsertDateTime.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						
						onInsertDateTime();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			
			menuInsertAnchor = new JMenuItem("org.multipage.gui.textInsertAnchor");
			if (enableCss) {
				menuInsert.add(menuInsertAnchor);
				menuInsertAnchor.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							
							onInsertAnchor();
						}
						catch(Throwable expt) {
							Safe.exception(expt);
						};
					}
				});
			}
		
			menuInsertLoremIpsum = new JMenuItem("org.multipage.gui.textInsertLoremIpsum");
			menuInsert.add(menuInsertLoremIpsum);
			menuInsertLoremIpsum.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						
						onInsertLoremIpsum();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			
			menuInsertCssFont = new JMenuItem("org.multipage.gui.textInsertCssFont");
			if (enableCss) {
				menuInsert.add(menuInsertCssFont);
				menuInsertCssFont.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							
							onInsertCssFont();
						}
						catch(Throwable expt) {
							Safe.exception(expt);
						};
					}
				});
			}
			
			menuInsertCssBorder = new JMenuItem("org.multipage.gui.textInsertCssBorder");
			if (enableCss) {
				menuInsert.add(menuInsertCssBorder);
				menuInsertCssBorder.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							
							onInsertCssBorder();
						}
						catch(Throwable expt) {
							Safe.exception(expt);
						};
					}
				});
			}
			
			menuInsertCssOutlines = new JMenuItem("org.multipage.gui.textInsertCssOutlines");
			if (enableCss) {
				menuInsert.add(menuInsertCssOutlines);
				menuInsertCssOutlines.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							
							onInsertCssOulines();
						}
						catch(Throwable expt) {
							Safe.exception(expt);
						};
					}
				});
			}
			
			menuInsertCssBoxShadow = new JMenuItem("org.multipage.gui.textInsertCssBoxShadow");
			if (enableCss) {
				menuInsert.add(menuInsertCssBoxShadow);
				menuInsertCssBoxShadow.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							
							onInsertCssBoxShadow();
						}
						catch(Throwable expt) {
							Safe.exception(expt);
						};
					}
				});
			}
			
			menuInsertCssTextShadow = new JMenuItem("org.multipage.gui.textInsertCssTextShadow");
			if (enableCss) {
				menuInsert.add(menuInsertCssTextShadow);
				menuInsertCssTextShadow.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							
							onInsertCssTextShadow();
						}
						catch(Throwable expt) {
							Safe.exception(expt);
						};
					}
				});
			}
			
			menuInsertCssBackground = new JMenuItem("org.multipage.gui.textInsertCssBackground");
			if (enableCss) {
				menuInsert.add(menuInsertCssBackground);
				menuInsertCssBackground.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							
							onInsertCssBackground();
						}
						catch(Throwable expt) {
							Safe.exception(expt);
						};
					}
				});
			}
			
			menuInsertCssNumber = new JMenuItem("org.multipage.gui.textInsertCssNumber");
			if (enableCss) {
				menuInsert.add(menuInsertCssNumber);
				menuInsertCssNumber.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							
							onInsertCssNumber();
						}
						catch(Throwable expt) {
							Safe.exception(expt);
						};
					}
				});
			}
			
			menuInsertCssBorderRadius = new JMenuItem("org.multipage.gui.textInsertCssBorderRadius");
			if (enableCss) {
				menuInsert.add(menuInsertCssBorderRadius);
				menuInsertCssBorderRadius.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							
							onInsertCssBorderRadius();
						}
						catch(Throwable expt) {
							Safe.exception(expt);
						};
					}
				});
			}
			
			menuInsertCssBorderImage = new JMenuItem("org.multipage.gui.textInsertCssBorderImage");
			if (enableCss) {
				menuInsert.add(menuInsertCssBorderImage);
				menuInsertCssBorderImage.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							
							onInsertCssBorderImage();
						}
						catch(Throwable expt) {
							Safe.exception(expt);
						};
					}
				});
			}
			
			menuInsertCssClip = new JMenuItem("org.multipage.gui.textInsertCssClip");
			if (enableCss) {
				menuInsert.add(menuInsertCssClip);
				menuInsertCssClip.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							
							onInsertCssClip();
						}
						catch(Throwable expt) {
							Safe.exception(expt);
						};
					}
				});
			}
			
			menuInsertCssFlex = new JMenuItem("org.multipage.gui.textInsertCssFlex");
			if (enableCss) {
				menuInsert.add(menuInsertCssFlex);
				menuInsertCssFlex.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							
							onInsertCssFlex();
						}
						catch(Throwable expt) {
							Safe.exception(expt);
						};
					}
				});
			}
			
			menuInsertCssSpacing = new JMenuItem("org.multipage.gui.textInsertCssSpacing");
			if (enableCss) {
				menuInsert.add(menuInsertCssSpacing);
				menuInsertCssSpacing.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							
							onInsertCssSpacing();
						}
						catch(Throwable expt) {
							Safe.exception(expt);
						};
					}
				});
			}
			
			menuInsertCssCounter = new JMenuItem("org.multipage.gui.textInsertCssCounter");
			if (enableCss) {
				menuInsert.add(menuInsertCssCounter);
				menuInsertCssCounter.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							
							onInsertCssCounter();
						}
						catch(Throwable expt) {
							Safe.exception(expt);
						};
					}
				});
			}
			
			menuInsertCssListStyle = new JMenuItem("org.multipage.gui.textInsertCssListStyle");
			if (enableCss) {
				menuInsert.add(menuInsertCssListStyle);
				menuInsertCssListStyle.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							
							onInsertCssListStyle();
						}
						catch(Throwable expt) {
							Safe.exception(expt);
						};
					}
				});
			}
			
			menuInsertCssKeyframes = new JMenuItem("org.multipage.gui.textInsertCssKeyframes");
			if (enableCss) {
				menuInsert.add(menuInsertCssKeyframes);
				menuInsertCssKeyframes.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							
							onInsertCssKeyframes();
						}
						catch(Throwable expt) {
							Safe.exception(expt);
						};
					}
				});
			}
			
			menuInsertCssAnimation = new JMenuItem("org.multipage.gui.textInsertCssAnimation");
			if (enableCss) {
				menuInsert.add(menuInsertCssAnimation);
				menuInsertCssAnimation.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							
							onInsertCssAnimation();
						}
						catch(Throwable expt) {
							Safe.exception(expt);
						};
					}
				});
			}
			
			menuInsertCssPerspectiveOrigin = new JMenuItem("org.multipage.gui.textInsertCssPerspectiveOrigin");
			if (enableCss) {
				menuInsert.add(menuInsertCssPerspectiveOrigin);
				menuInsertCssPerspectiveOrigin.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							
							onInsertCssPerspectiveOrigin();
						}
						catch(Throwable expt) {
							Safe.exception(expt);
						};
					}
				});
			}
			
			menuInsertCssTransform = new JMenuItem("org.multipage.gui.textInsertCssTransform");
			if (enableCss) {
				menuInsert.add(menuInsertCssTransform);
				menuInsertCssTransform.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							
							onInsertCssTransform();
						}
						catch(Throwable expt) {
							Safe.exception(expt);
						};
					}
				});
			}
			
			menuInsertCssTransformOrigin = new JMenuItem("org.multipage.gui.textInsertCssTransformOrigin");
			if (enableCss) {
				menuInsert.add(menuInsertCssTransformOrigin);
				menuInsertCssTransformOrigin.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							
							onInsertCssTransformOrigin();
						}
						catch(Throwable expt) {
							Safe.exception(expt);
						};
					}
				});
			}
			
			menuInsertCssTransition = new JMenuItem("org.multipage.gui.textInsertCssTransition");
			if (enableCss) {
				menuInsert.add(menuInsertCssTransition);
				menuInsertCssTransition.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							
							onInsertCssTransition();
						}
						catch(Throwable expt) {
							Safe.exception(expt);
						};
					}
				});
			}
			
			menuInsertCssCursor = new JMenuItem("org.multipage.gui.textInsertCssCursor");
			if (enableCss) {
				menuInsert.add(menuInsertCssCursor);
				menuInsertCssCursor.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							
							onInsertCssCursor();
						}
						catch(Throwable expt) {
							Safe.exception(expt);
						};
					}
				});
			}
			
			menuInsertCssQuotes = new JMenuItem("org.multipage.gui.textInsertCssQuotes");
			if (enableCss) {
				menuInsert.add(menuInsertCssQuotes);
				menuInsertCssQuotes.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							
							onInsertCssQuotes();
						}
						catch(Throwable expt) {
							Safe.exception(expt);
						};
					}
				});
			}
			
			menuInsertCssTextLine = new JMenuItem("org.multipage.gui.textInsertCssTextLine");
			if (enableCss) {
				menuInsert.add(menuInsertCssTextLine);
				menuInsertCssTextLine.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							
							onInsertCssTextLine();
						}
						catch(Throwable expt) {
							Safe.exception(expt);
						};
					}
				});
			}
			
			menuInsertCssResource = new JMenuItem("org.multipage.gui.textInsertCssResource");
			if (enableCss) {
				menuInsert.add(menuInsertCssResource);
				menuInsertCssResource.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							
							onInsertCssResource();
						}
						catch(Throwable expt) {
							Safe.exception(expt);
						};
					}
				});
			}
			
			menuInsertCssResourceUrl = new JMenuItem("org.multipage.gui.textInsertCssResourceUrl");
			if (enableCss) {
				menuInsert.add(menuInsertCssResourceUrl);
				menuInsertCssResourceUrl.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							
							onInsertCssResourceUrl();
						}
						catch(Throwable expt) {
							Safe.exception(expt);
						};
					}
				});
			}
			
			menuInsertCssResourcesUrls = new JMenuItem("org.multipage.gui.textInsertCssResourcesUrls");
			if (enableCss) {
				menuInsert.add(menuInsertCssResourcesUrls);
				menuInsertCssResourcesUrls.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							
							onInsertCssResourcesUrls();
						}
						catch(Throwable expt) {
							Safe.exception(expt);
						};
					}
				});
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Move left.
	 */
	protected void onMoveLeft() {
		try {
			
			if (textComponent.isEditable()) {
				TextEditorPane.moveLines(true, textComponent);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Move right.
	 */
	protected void onMoveRight() {
		try {
			
			if (textComponent.isEditable()) {
				TextEditorPane.moveLines(false, textComponent);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Localize components.
	 */
	private void localize() {
		try {
			
			Utility.localize(menuCut);
			Utility.localize(menuCopy);
			Utility.localize(menuPaste);
			Utility.localize(menuSelectAll);
			Utility.localize(menuEdit);
			Utility.localize(menuEditMoveLeft);
			Utility.localize(menuEditMoveRight);
			Utility.localize(menuInsert);
			Utility.localize(menuInsertDateTime);
			Utility.localize(menuInsertAnchor);
			Utility.localize(menuInsertLoremIpsum);
			Utility.localize(menuInsertCssFont);
			Utility.localize(menuInsertCssBorder);
			Utility.localize(menuInsertCssOutlines);
			Utility.localize(menuInsertCssBoxShadow);
			Utility.localize(menuInsertCssTextShadow);
			Utility.localize(menuInsertCssBackground);
			Utility.localize(menuInsertCssNumber);
			Utility.localize(menuInsertCssBorderRadius);
			Utility.localize(menuInsertCssBorderImage);
			Utility.localize(menuInsertCssClip);
			Utility.localize(menuInsertCssFlex);
			Utility.localize(menuInsertCssSpacing);
			Utility.localize(menuInsertCssCounter);
			Utility.localize(menuInsertCssListStyle);
			Utility.localize(menuInsertCssKeyframes);
			Utility.localize(menuInsertCssAnimation);
			Utility.localize(menuInsertCssPerspectiveOrigin);
			Utility.localize(menuInsertCssTransform);
			Utility.localize(menuInsertCssTransformOrigin);
			Utility.localize(menuInsertCssTransition);
			Utility.localize(menuInsertCssCursor);
			Utility.localize(menuInsertCssQuotes);
			Utility.localize(menuInsertCssTextLine);
			Utility.localize(menuInsertCssResource);
			Utility.localize(menuInsertCssResourceUrl);
			Utility.localize(menuInsertCssResourcesUrls);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set icons.
	 */
	private void setIcons() {
		try {
			
			menuCut.setIcon(Images.getIcon("org/multipage/gui/images/cut_icon.png"));
			menuCopy.setIcon(Images.getIcon("org/multipage/gui/images/copy_icon.png"));
			menuPaste.setIcon(Images.getIcon("org/multipage/gui/images/paste_icon.png"));
			menuSelectAll.setIcon(Images.getIcon("org/multipage/gui/images/select_all.png"));
			menuEdit.setIcon(Images.getIcon("org/multipage/gui/images/edit.png"));
			menuEditMoveLeft.setIcon(Images.getIcon("org/multipage/gui/images/left.png"));
			menuEditMoveRight.setIcon(Images.getIcon("org/multipage/gui/images/right.png"));
			menuInsert.setIcon(Images.getIcon("org/multipage/gui/images/insert.png"));
			menuInsertDateTime.setIcon(Images.getIcon("org/multipage/gui/images/watch.png"));
			menuInsertAnchor.setIcon(Images.getIcon("org/multipage/gui/images/anchor.png"));
			menuInsertLoremIpsum.setIcon(Images.getIcon("org/multipage/gui/images/lorem_ipsum.png"));
			menuInsertCssFont.setIcon(Images.getIcon("org/multipage/gui/images/font_icon.png"));
			menuInsertCssBorder.setIcon(Images.getIcon("org/multipage/gui/images/border.png"));
			menuInsertCssOutlines.setIcon(Images.getIcon("org/multipage/gui/images/outlines.png"));
			menuInsertCssBoxShadow.setIcon(Images.getIcon("org/multipage/gui/images/shadow.png"));
			menuInsertCssTextShadow.setIcon(Images.getIcon("org/multipage/gui/images/text_shadow.png"));
			menuInsertCssBackground.setIcon(Images.getIcon("org/multipage/gui/images/background.png"));
			menuInsertCssNumber.setIcon(Images.getIcon("org/multipage/gui/images/number.png"));
			menuInsertCssBorderRadius.setIcon(Images.getIcon("org/multipage/gui/images/radius.png"));
			menuInsertCssBorderImage.setIcon(Images.getIcon("org/multipage/gui/images/border_image.png"));
			menuInsertCssClip.setIcon(Images.getIcon("org/multipage/gui/images/clip.png"));
			menuInsertCssFlex.setIcon(Images.getIcon("org/multipage/gui/images/flex.png"));
			menuInsertCssSpacing.setIcon(Images.getIcon("org/multipage/gui/images/spacing.png"));
			menuInsertCssCounter.setIcon(Images.getIcon("org/multipage/gui/images/increment.png"));
			menuInsertCssListStyle.setIcon(Images.getIcon("org/multipage/gui/images/list.png"));
			menuInsertCssKeyframes.setIcon(Images.getIcon("org/multipage/gui/images/keyframes.png"));
			menuInsertCssAnimation.setIcon(Images.getIcon("org/multipage/gui/images/animation.png"));
			menuInsertCssPerspectiveOrigin.setIcon(Images.getIcon("org/multipage/gui/images/perspective_origin.png"));
			menuInsertCssTransform.setIcon(Images.getIcon("org/multipage/gui/images/transform.png"));
			menuInsertCssTransformOrigin.setIcon(Images.getIcon("org/multipage/gui/images/transform_origin.png"));
			menuInsertCssTransition.setIcon(Images.getIcon("org/multipage/gui/images/transition.png"));
			menuInsertCssCursor.setIcon(Images.getIcon("org/multipage/gui/images/cursor.png"));
			menuInsertCssQuotes.setIcon(Images.getIcon("org/multipage/gui/images/quotes.png"));
			menuInsertCssTextLine.setIcon(Images.getIcon("org/multipage/gui/images/text_line.png"));
			menuInsertCssResource.setIcon(Images.getIcon("org/multipage/gui/images/resource.png"));
			menuInsertCssResourceUrl.setIcon(Images.getIcon("org/multipage/gui/images/url.png"));
			menuInsertCssResourcesUrls.setIcon(Images.getIcon("org/multipage/gui/images/urls.png"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Add popup menu.
	 * @param component
	 * @param popup
	 */
	private static void addPopup(Component component, final JPopupMenu popup) {
		try {
			
			component.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					try {
						
						if (e.isPopupTrigger()) {
							showMenu(e);
						}
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
				public void mouseReleased(MouseEvent e) {
					try {
						
						if (e.isPopupTrigger()) {
							showMenu(e);
						}
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
				private void showMenu(MouseEvent e) {
					try {
						
						popup.show(e.getComponent(), e.getX(), e.getY());
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
	 * On cut text.
	 */
	protected void onCutText() {
		try {
			
			if (textComponent.isEditable()) {
				textComponent.grabFocus();
				textComponent.cut();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On copy text.
	 */
	protected void onCopyText() {
		try {
			
			textComponent.grabFocus();
			textComponent.copy();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On paste text.
	 */
	protected void onPasteText() {
		try {
			
			if (textComponent.isEditable()) {
				textComponent.grabFocus();
				textComponent.paste();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On select all.
	 */
	protected void onSelectAll() {
		try {
			
			textComponent.grabFocus();
			textComponent.selectAll();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Date / time dialog.
	 */
	protected void onInsertDateTime() {
		try {
			
			if (textComponent.isEditable()) {
				
				String dateTimeText = DateTimeDialog.showDialog(this);
				textComponent.replaceSelection(dateTimeText);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On insert Lorem Ipsum.
	 */
	protected void onInsertLoremIpsum() {
		try {
			
			if (textComponent.isEditable()) {
				
				String loremIpsumText = LoremIpsumDialog.showDialog(this);
				
				if (loremIpsumText != null) {
					textComponent.replaceSelection(loremIpsumText);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On insert CSS font.
	 */
	protected void onInsertCssFont() {
		try {
			
			if (textComponent.isEditable()) {
				
				String selectedText = textComponent.getSelectedText();
				String cssFont = InsertPanelContainerDialog.showDialog(textComponent, new CssFontPanel(selectedText));
				
				if (cssFont != null) {
					textComponent.replaceSelection(cssFont);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On insert CSS border.
	 */
	protected void onInsertCssBorder() {
		try {
			
			if (textComponent.isEditable()) {
				
				String selectedText = textComponent.getSelectedText();
				String cssBorder = InsertPanelContainerDialog.showDialog(textComponent, new CssBorderPanel(selectedText));
				
				if (cssBorder != null) {
					textComponent.replaceSelection(cssBorder);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Insert CSS outlines.
	 */
	protected void onInsertCssOulines() {
		try {
			
			if (textComponent.isEditable()) {
				
				String selectedText = textComponent.getSelectedText();
				String cssOutlines = InsertPanelContainerDialog.showDialog(textComponent, new CssOutlinesPanel(selectedText));
				
				if (cssOutlines != null) {
					textComponent.replaceSelection(cssOutlines);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Insert CSS box shadow.
	 */
	protected void onInsertCssBoxShadow() {
		try {
			
			if (textComponent.isEditable()) {
				
				String selectedText = textComponent.getSelectedText();
				String cssShadow = InsertPanelContainerDialog.showDialog(textComponent, new CssBoxShadowPanel(selectedText));
				
				if (cssShadow != null) {
					textComponent.replaceSelection(cssShadow);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Insert CSS text shadow.
	 */
	protected void onInsertCssTextShadow() {
		try {
			
			if (textComponent.isEditable()) {
				
				String selectedText = textComponent.getSelectedText();
				String cssShadow = InsertPanelContainerDialog.showDialog(textComponent, new CssTextShadowPanel(selectedText));
				
				if (cssShadow != null) {
					textComponent.replaceSelection(cssShadow);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Insert CSS background.
	 */
	protected void onInsertCssBackground() {
		try {
			
			if (textComponent.isEditable()) {
				
				String selectedText = textComponent.getSelectedText();
				String cssBackground = InsertPanelContainerDialog.showDialog(textComponent, new CssBackgroundImagesPanel(selectedText));
				
				if (cssBackground != null) {
					textComponent.replaceSelection(cssBackground);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Insert SS number.
	 */
	protected void onInsertCssNumber() {
		try {
			
			if (textComponent.isEditable()) {
				
				String selectedText = textComponent.getSelectedText();
				String cssNumber = InsertPanelContainerDialog.showDialog(textComponent, new CssNumberPanel(selectedText));
				
				if (cssNumber != null) {
					textComponent.replaceSelection(cssNumber);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Insert border radius.
	 */
	protected void onInsertCssBorderRadius() {
		try {
			
			if (textComponent.isEditable()) {
				
				String selectedText = textComponent.getSelectedText();
				String cssBorderRadius = InsertPanelContainerDialog.showDialog(textComponent, new CssBorderRadiusPanel(selectedText));
				
				if (cssBorderRadius != null) {
					textComponent.replaceSelection(cssBorderRadius);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Insert clip.
	 */
	protected void onInsertCssClip() {
		try {
			
			if (textComponent.isEditable()) {
				
				String selectedText = textComponent.getSelectedText();
				String cssClip = InsertPanelContainerDialog.showDialog(textComponent, new CssClipPanel(selectedText));
				
				if (cssClip != null) {
					textComponent.replaceSelection(cssClip);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Insert border image.
	 */
	protected void onInsertCssBorderImage() {
		try {
			
			if (textComponent.isEditable()) {
				
				String selectedText = textComponent.getSelectedText();
				String cssBorderImage = InsertPanelContainerDialog.showDialog(textComponent, new CssBorderImagePanel(selectedText));
				
				if (cssBorderImage != null) {
					textComponent.replaceSelection(cssBorderImage);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Insert flex.
	 */
	protected void onInsertCssFlex() {
		try {
			
			if (textComponent.isEditable()) {
				
				String selectedText = textComponent.getSelectedText();
				String cssFlex = InsertPanelContainerDialog.showDialog(textComponent, new CssFlexPanel(selectedText));
				
				if (cssFlex != null) {
					textComponent.replaceSelection(cssFlex);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Insert spacing.
	 */
	protected void onInsertCssSpacing() {
		try {
			
			if (textComponent.isEditable()) {
				
				String selectedText = textComponent.getSelectedText();
				String cssSpacing = InsertPanelContainerDialog.showDialog(textComponent, new CssSpacingPanel(selectedText));
				
				if (cssSpacing != null) {
					textComponent.replaceSelection(cssSpacing);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Insert counter.
	 */
	protected void onInsertCssCounter() {
		try {
			
			if (textComponent.isEditable()) {
				
				String selectedText = textComponent.getSelectedText();
				String cssCounter = InsertPanelContainerDialog.showDialog(textComponent, new CssCountersPanel(selectedText));
				
				if (cssCounter != null) {
					textComponent.replaceSelection(cssCounter);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Insert list style.
	 */
	protected void onInsertCssListStyle() {
		try {
			
			if (textComponent.isEditable()) {
				
				String selectedText = textComponent.getSelectedText();
				String cssListStyle = InsertPanelContainerDialog.showDialog(textComponent, new CssListStylePanel(selectedText));
				
				if (cssListStyle != null) {
					textComponent.replaceSelection(cssListStyle);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Insert keyframes.
	 */
	protected void onInsertCssKeyframes() {
		try {
			
			if (textComponent.isEditable()) {
				
				String selectedText = textComponent.getSelectedText();
				String cssKeyframes = InsertPanelContainerDialog.showDialog(textComponent, new CssKeyframesPanel(selectedText));
				
				if (cssKeyframes != null) {
					textComponent.replaceSelection(cssKeyframes);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Insert animation.
	 */
	protected void onInsertCssAnimation() {
		try {
			
			if (textComponent.isEditable()) {
				
				String selectedText = textComponent.getSelectedText();
				String cssAnimation = InsertPanelContainerDialog.showDialog(textComponent, new CssAnimationPanel(selectedText));
				
				if (cssAnimation != null) {
					textComponent.replaceSelection(cssAnimation);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Insert perspective origin.
	 */
	protected void onInsertCssPerspectiveOrigin() {
		try {
			
			if (textComponent.isEditable()) {
				
				String selectedText = textComponent.getSelectedText();
				String cssPerspectiveOrigin = InsertPanelContainerDialog.showDialog(textComponent, new CssPerspectiveOriginPanel(selectedText));
				
				if (cssPerspectiveOrigin != null) {
					textComponent.replaceSelection(cssPerspectiveOrigin);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Insert transform.
	 */
	protected void onInsertCssTransform() {
		try {
			
			if (textComponent.isEditable()) {
				
				String selectedText = textComponent.getSelectedText();
				String cssTransform = InsertPanelContainerDialog.showDialog(textComponent, new CssTransformPanel(selectedText));
				
				if (cssTransform != null) {
					textComponent.replaceSelection(cssTransform);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Insert transform origin.
	 */
	protected void onInsertCssTransformOrigin() {
		try {
			
			if (textComponent.isEditable()) {
				
				String selectedText = textComponent.getSelectedText();
				String cssTransformOrigin = InsertPanelContainerDialog.showDialog(textComponent, new CssTransformOriginPanel(selectedText));
				
				if (cssTransformOrigin != null) {
					textComponent.replaceSelection(cssTransformOrigin);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Insert transition.
	 */
	protected void onInsertCssTransition() {
		try {
			
			if (textComponent.isEditable()) {
				
				String selectedText = textComponent.getSelectedText();
				String cssTransition = InsertPanelContainerDialog.showDialog(textComponent, new CssTransitionPanel(selectedText));
				
				if (cssTransition != null) {
					textComponent.replaceSelection(cssTransition);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Insert cursor.
	 */
	protected void onInsertCssCursor() {
		try {
			
			if (textComponent.isEditable()) {
				
				String selectedText = textComponent.getSelectedText();
				String cssCursor = InsertPanelContainerDialog.showDialog(textComponent, new CssCursorPanel(selectedText));
				
				if (cssCursor != null) {
					textComponent.replaceSelection(cssCursor);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Insert quotes.
	 */
	protected void onInsertCssQuotes() {
		try {
			
			if (textComponent.isEditable()) {
				
				String selectedText = textComponent.getSelectedText();
				String cssQuotes = InsertPanelContainerDialog.showDialog(textComponent, new CssQuotesPanel(selectedText));
				
				if (cssQuotes != null) {
					textComponent.replaceSelection(cssQuotes);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Insert text line.
	 */
	protected void onInsertCssTextLine() {
		try {
			
			if (textComponent.isEditable()) {
				
				String selectedText = textComponent.getSelectedText();
				String cssTextLine = InsertPanelContainerDialog.showDialog(textComponent, new CssTextLinePanel(selectedText));
				
				if (cssTextLine != null) {
					textComponent.replaceSelection(cssTextLine);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Insert resource.
	 */
	protected void onInsertCssResource() {
		try {
			
			if (textComponent.isEditable()) {
				
				String selectedText = textComponent.getSelectedText();
				String cssResourceUrl = InsertPanelContainerDialog.showDialog(textComponent, new CssResourcePanel(selectedText, false));
				
				if (cssResourceUrl != null) {
					textComponent.replaceSelection(cssResourceUrl);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Insert resource URL.
	 */
	protected void onInsertCssResourceUrl() {
		try {
			
			if (textComponent.isEditable()) {
				
				String selectedText = textComponent.getSelectedText();
				String cssResourceUrl = InsertPanelContainerDialog.showDialog(textComponent, new CssResourcePanel(selectedText, true));
				
				if (cssResourceUrl != null) {
					textComponent.replaceSelection(cssResourceUrl);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Insert anchor.
	 */
	protected void onInsertAnchor() {
		try {
			
			if (textComponent.isEditable()) {
				
				String selectedText = textComponent.getSelectedText();
				String anchorText = AnchorDialog.showDialog(this, selectedText);
				
				if (anchorText != null) {
					textComponent.replaceSelection(anchorText);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Insert resources URLs.
	 */
	protected void onInsertCssResourcesUrls() {
		try {
			
			if (textComponent.isEditable()) {
				
				String selectedText = textComponent.getSelectedText();
				String cssResourcesUrls = InsertPanelContainerDialog.showDialog(textComponent, new CssResourceUrlsPanel(selectedText));
				
				if (cssResourcesUrls != null) {
					textComponent.replaceSelection(cssResourcesUrls);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Insert menu item.
	 * @param ordinal
	 * @param itemTextResource
	 * @param itemIconResource
	 * @param callback
	 */
	public void insertItem(int ordinal, String itemTextResource, String itemIconResource, Runnable callback) {
		try {
			
			// Create menu item.
			JMenuItem menuItem = new JMenuItem();
			
			// Set item text and icon.
			menuItem.setText(Resources.getString(itemTextResource));
			if (itemIconResource != null) {
				menuItem.setIcon(Images.getIcon(itemIconResource));
			}
			
			// Add callback.
			menuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						
						callback.run();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
				}
			});
			
			// Insert new menu item.
			this.insert(menuItem, ordinal);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Insert separator.
	 * @param ordinal
	 */
	public void insertSeparator(int ordinal) {
		try {
			
			this.insert(new JSeparator(), ordinal);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}

/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serializable;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * <code>FontChooser</code> provides a pane of controls designed to allow
 * a user to manipulate and select a font.
 *
 * This class provides three levels of API:
 * <ol>
 * <li>A static convenience method which shows a modal font-chooser
 * dialog and returns the font selected by the user.
 * <li>A static convenience method for creating a font-chooser dialog
 * where <code>ActionListeners</code> can be specified to be invoked when
 * the user presses one of the dialog buttons.
 * <li>The ability to create instances of <code>FontChooser</code> panes
 * directly (within any container). <code>PropertyChange</code> listeners
 * can be added to detect when the current "font" property changes.
 * </ol>
 * <p>
 *
 * @author Adrian BER
 */
@SuppressWarnings("serial")
public class FontChooser extends JComponent {

    /** The list of possible font sizes. */
    private static final Integer[] SIZES =
            {8, 9, 10, 11, 12, 13, 14, 16, 18, 20, 24, 26, 28, 32, 36, 40, 48, 56, 64, 72};

    /** The list of possible fonts. */
    private static final String[] FONTS = GraphicsEnvironment.getLocalGraphicsEnvironment()
            .getAvailableFontFamilyNames();


    private FontSelectionModel selectionModel;

    private JList fontList;

    private JList sizeList;

    private JCheckBox boldCheckBox;

    private JCheckBox italicCheckBox;

    private JLabel previewLabel;

    /** The preview text, if null the font name will be the preview text. */
    private String previewText;

    /** Listener used to update the font of the selection model. */
    private SelectionUpdater selectionUpdater = new SelectionUpdater();

    /** Listener used to update the font in the components. This should be registered
     * with the selection model. */
    private LabelUpdater labelUpdater = new LabelUpdater();

    /** True if the components are being updated and no event should be generated. */
    private boolean updatingComponents = false;

    /** Listener class used to update the font in the components. This should be registered
      * with the selection model. */
    private class LabelUpdater implements ChangeListener {

        public void stateChanged(ChangeEvent e) {
        	try {
				
				 updateComponents();
			}
			catch(Throwable expt) {
				Safe.exception(expt);
			};
        }
    }

    /** Listener class used to update the font of the preview label. */
    private class SelectionUpdater implements ChangeListener, ListSelectionListener {

        public void stateChanged(ChangeEvent e) {
        	try {
				
				if (!updatingComponents) {
	                setFont(buildFont());
	            }
			}
			catch(Throwable expt) {
				Safe.exception(expt);
			};
        }

        public void valueChanged(ListSelectionEvent e) {
        	try {
				
				if (!updatingComponents) {
	                setFont(buildFont());
	            }
			}
			catch(Throwable expt) {
				Safe.exception(expt);
			};
        }
    }

    /**
     * Shows a modal font-chooser dialog and blocks until the
     * dialog is hidden.  If the user presses the "OK" button, then
     * this method hides/disposes the dialog and returns the selected color.
     * If the user presses the "Cancel" button or closes the dialog without
     * pressing "OK", then this method hides/disposes the dialog and returns
     * <code>null</code>.
     *
     * @param component    the parent <code>Component</code> for the dialog
     * @param textFont 
     * @return the selected font or <code>null</code> if the user opted out
     * @exception HeadlessException if GraphicsEnvironment.isHeadless()
     * returns true.
     * @see java.awt.GraphicsEnvironment#isHeadless
     */
    public Font showDialog(Component component, Font textFont) {
    	
    	try {
	        FontTracker ok = new FontTracker(this);
	        JDialog dialog = createDialog(component,
	        		Resources.getString("org.multipage.gui.textSelectFont"), true, ok, null,
	        		textFont);
	        dialog.addWindowListener(new FontChooserDialog.Closer());
	        dialog.addComponentListener(new FontChooserDialog.DisposeOnClose());
	        dialog.setIconImage(Images.getImage("org/multipage/gui/images/font_icon.png"));
	
	        dialog.setVisible(true); // blocks until user brings dialog down...
	
	        return ok.getFont();
	    }
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
    }


    /**
     * Creates and returns a new dialog containing the specified
     * <code>ColorChooser</code> pane along with "OK", "Cancel", and "Reset"
     * buttons. If the "OK" or "Cancel" buttons are pressed, the dialog is
     * automatically hidden (but not disposed).  If the "Reset"
     * button is pressed, the color-chooser's color will be reset to the
     * font which was set the last time <code>show</code> was invoked on the
     * dialog and the dialog will remain showing.
     *
     * @param c              the parent component for the dialog
     * @param title          the title for the dialog
     * @param modal          a boolean. When true, the remainder of the program
     *                       is inactive until the dialog is closed.
     * @param okListener     the ActionListener invoked when "OK" is pressed
     * @param cancelListener the ActionListener invoked when "Cancel" is pressed
     * @param textFont 
     * @return a new dialog containing the font-chooser pane
     * @exception HeadlessException if GraphicsEnvironment.isHeadless()
     * returns true.
     * @see java.awt.GraphicsEnvironment#isHeadless
     */
    public JDialog createDialog(Component c, String title, boolean modal,
        ActionListener okListener, ActionListener cancelListener,
        Font textFont) {
    	
    	try {
	        return new FontChooserDialog(c, title, modal, this,
	                okListener, cancelListener, textFont);
	    }
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
    }

    /**
     * Creates a color chooser pane with an initial font which is the same font
     * as the default font for labels.
     */
    public FontChooser() {
        this(new DefaultFontSelectionModel());
    }

    /**
     * Creates a font chooser pane with the specified initial font.
     *
     * @param initialFont the initial font set in the chooser
     */
    public FontChooser(Font initialFont) {
        this(new DefaultFontSelectionModel(initialFont));
    }

    /**
     * Creates a font chooser pane with the specified
     * <code>FontSelectionModel</code>.
     *
     * @param model the font selection model used by this component
     */
    public FontChooser(FontSelectionModel model) {
    	try {
			
			selectionModel = model;
	        init(model.getSelectedFont());
	        selectionModel.addChangeListener(labelUpdater);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
    }

    private void init(Font font) {
    	
    	try {
	        setLayout(new GridBagLayout());
	
	        Insets ins = new Insets(2, 2, 2, 2);
	
	        fontList = new JList(FONTS);
	        fontList.setVisibleRowCount(10);
	        fontList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	        add(new JScrollPane(fontList), new GridBagConstraints(0, 0, 1, 1, 2, 2,
	                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
	                ins, 0, 0));
	
	        sizeList = new JList(SIZES);
	        ((JLabel)sizeList.getCellRenderer()).setHorizontalAlignment(JLabel.RIGHT);
	        sizeList.setVisibleRowCount(10);
	        sizeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	        add(new JScrollPane(sizeList), new GridBagConstraints(1, 0, 1, 1, 1, 2,
	                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
	                ins, 0, 0));
	
	        boldCheckBox = new JCheckBox("Bold");
	        add(boldCheckBox, new GridBagConstraints(0, 1, 2, 1, 1, 0,
	                GridBagConstraints.WEST, GridBagConstraints.NONE,
	                ins, 0, 0));
	
	        italicCheckBox = new JCheckBox("Italic");
	        add(italicCheckBox, new GridBagConstraints(0, 2, 2, 1, 1, 0,
	                GridBagConstraints.WEST, GridBagConstraints.NONE,
	                ins, 0, 0));
	
	        previewLabel = new JLabel("");
	        previewLabel.setHorizontalAlignment(JLabel.CENTER);
	        previewLabel.setVerticalAlignment(JLabel.CENTER);
	        add(new JScrollPane(previewLabel), new GridBagConstraints(0, 3, 2, 1, 1, 1,
	                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
	                ins, 0, 0));
	
	        setFont(font == null ? previewLabel.getFont() : font);
	
	        fontList.addListSelectionListener(selectionUpdater);
	        sizeList.addListSelectionListener(selectionUpdater);
	        boldCheckBox.addChangeListener(selectionUpdater);
	        italicCheckBox.addChangeListener(selectionUpdater);
	    }
		catch (Throwable e) {
			Safe.exception(e);
		}
    }

    private Font buildFont() {
    	
    	try {
	//        Font labelFont = previewLabel.getFont();
	
	        String fontName = (String)fontList.getSelectedValue();
	        if (fontName == null) {
	            return null;
	//            fontName = labelFont.getName();
	        }
	        Integer sizeInt = (Integer)sizeList.getSelectedValue();
	        if (sizeInt == null) {
	//            size = labelFont.getSize();
	            return null;
	        }
	
	        // create the font
	//        // first create the font attributes
	//        HashMap map = new HashMap();
	//        map.put(TextAttribute.BACKGROUND, Color.white);
	//        map.put(TextAttribute.FAMILY, fontName);
	//        map.put(TextAttribute.FOREGROUND, Color.black);
	//        map.put(TextAttribute.SIZE , new Float(size));
	//        map.put(TextAttribute.UNDERLINE, italicCheckBox.isSelected() ? TextAttribute.UNDERLINE_LOW_ONE_PIXEL : TextAttribute.UNDERLINE_LOW_TWO_PIXEL);
	//        map.put(TextAttribute.STRIKETHROUGH, italicCheckBox.isSelected() ? TextAttribute.STRIKETHROUGH_ON : Boolean.FALSE);
	//        map.put(TextAttribute.WEIGHT, boldCheckBox.isSelected() ? TextAttribute.WEIGHT_BOLD : TextAttribute.WEIGHT_REGULAR);
	//        map.put(TextAttribute.POSTURE,
	//                italicCheckBox.isSelected() ? TextAttribute.POSTURE_OBLIQUE : TextAttribute.POSTURE_REGULAR);
	//
	//        return new Font(map);
	
	        return new Font(fontName,
	                (italicCheckBox.isSelected() ? Font.ITALIC : Font.PLAIN)
	                | (boldCheckBox.isSelected() ? Font.BOLD : Font.PLAIN),
	                sizeInt);
	    }
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
    }

    /** Updates the font in the preview component according to the selected values. */
    private void updateComponents() {
    	try {
			
			updatingComponents = true;
	
	        Font font = getFont();
	
	        fontList.setSelectedValue(font.getName(), true);
	        sizeList.setSelectedValue(font.getSize(), true);
	        boldCheckBox.setSelected(font.isBold());
	        italicCheckBox.setSelected(font.isItalic());
	
	        if (previewText == null) {
	            previewLabel.setText(font.getName());
	        }
	
	        // set the font and fire a property change
	        Font oldValue = previewLabel.getFont();
	        previewLabel.setFont(font);
	        firePropertyChange("font", oldValue, font);
	
	        updatingComponents = false;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
    }

    /**
     * Returns the data model that handles font selections.
     *
     * @return a FontSelectionModel object
     */
    public FontSelectionModel getSelectionModel() {
        return selectionModel;
    }


    /**
     * Set the model containing the selected font.
     *
     * @param newModel   the new FontSelectionModel object
     */
    public void setSelectionModel(FontSelectionModel newModel ) {
    	try {
			
			FontSelectionModel oldModel = selectionModel;
	        selectionModel = newModel;
	        oldModel.removeChangeListener(labelUpdater);
	        newModel.addChangeListener(labelUpdater);
	        firePropertyChange("selectionModel", oldModel, newModel);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
    }

    /**
     * Gets the current font value from the font chooser.
     *
     * @return the current font value of the font chooser
     */
    public Font getFont() {
    	
    	try {
    		return selectionModel.getSelectedFont();
    	}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
    }

    /**
     * Sets the current font of the font chooser to the specified font.
     * The <code>ColorSelectionModel</code> will fire a <code>ChangeEvent</code>
     * @param font the font to be set in the font chooser
     * @see JComponent#addPropertyChangeListener
     */
    public void setFont(Font font) {
    	try {
			
			selectionModel.setSelectedFont(font);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
    }

    /** Returns the preview text displayed in the preview component.
     * @return the preview text, if null the font name will be displayed
     */
    public String getPreviewText() {
        return previewText;
    }

    /** Sets the preview text displayed in the preview component.
     * @param previewText the preview text, if null the font name will be displayed
     */
    public void setPreviewText(String previewText) {
    	try {
			
			this.previewText = previewText;
	        previewLabel.setText("");
	        updateComponents();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
    }
}


/*
 * Class which builds a font chooser dialog consisting of
 * a FontChooser with "Ok", "Cancel", and "Reset" buttons.
 *
 * Note: This needs to be fixed to deal with localization!
 */
@SuppressWarnings("serial")
class FontChooserDialog extends JDialog {
	
    private static final Dimension buttonDimension = new Dimension(80, 25);
	private static final Insets buttonMargin = new Insets(0, 0, 0, 0);
	private Font initialFont;
    private FontChooser chooserPane;

    public FontChooserDialog(Component c, String title, boolean modal,
              FontChooser chooserPane,
              ActionListener okListener, ActionListener cancelListener,
              Font textFont) {
        super(JOptionPane.getFrameForComponent(c), title, modal);
        
        try {
			
			String okString = Resources.getString("textOk");
	        String cancelString = Resources.getString("textCancel");
	        String resetString = Resources.getString("textReset");
	
	        /*
	         * Create Lower button panel
	         */
	        JPanel buttonPane = new JPanel();
	        buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
	        JButton okButton = new JButton(okString);
	        getRootPane().setDefaultButton(okButton);
	        okButton.setActionCommand("OK");
	        if (okListener != null) {
	            okButton.addActionListener(okListener);
	        }
	        okButton.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	            	try {
						
						setVisible(false);
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
	            }
	        });
	        okButton.setPreferredSize(buttonDimension);
	        okButton.setIcon(Images.getIcon("org/multipage/gui/images/ok_icon.png"));
	        okButton.setMargin(buttonMargin);
	        buttonPane.add(okButton);
	
	        JButton cancelButton = new JButton(cancelString);
	        cancelButton.setPreferredSize(buttonDimension);
	        cancelButton.setMargin(buttonMargin);
	        cancelButton.setIcon(Images.getIcon("org/multipage/gui/images/cancel_icon.png"));
	
	        // The following few lines are used to register esc to close the dialog
	        Action cancelKeyAction = new AbstractAction() {
	            public void actionPerformed(ActionEvent e) {
	                // todo make it in 1.3
	//                ActionListener[] listeners
	//                        = ((AbstractButton) e.getSource()).getActionListeners();
	//                for (int i = 0; i < listeners.length; i++) {
	//                    listeners[i].actionPerformed(e);
	//                }
	            }
	        };
	        KeyStroke cancelKeyStroke = KeyStroke.getKeyStroke((char) KeyEvent.VK_ESCAPE);
	        InputMap inputMap = cancelButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
	        ActionMap actionMap = cancelButton.getActionMap();
	        if (inputMap != null && actionMap != null) {
	            inputMap.put(cancelKeyStroke, "cancel");
	            actionMap.put("cancel", cancelKeyAction);
	        }
	        // end esc handling
	
	        cancelButton.setActionCommand("cancel");
	        if (cancelListener != null) {
	            cancelButton.addActionListener(cancelListener);
	        }
	        cancelButton.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	            	try {
						
						setVisible(false);
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
	            }
	        });
	        buttonPane.add(cancelButton);
	
	        JButton resetButton = new JButton(resetString);
	        resetButton.setPreferredSize(buttonDimension);
	        resetButton.setMargin(buttonMargin);
	        resetButton.setIcon(Images.getIcon("org/multipage/gui/images/reset_icon.png"));
	        
	        resetButton.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	            	try {
						
						reset();
					}
					catch(Throwable expt) {
						Safe.exception(expt);
					};
	            }
	        });
	        int mnemonic = UIManager.getInt("ColorChooser.resetMnemonic");
	        if (mnemonic != -1) {
	            resetButton.setMnemonic(mnemonic);
	        }
	        buttonPane.add(resetButton);
	
	        // initialiase the content pane
	        this.chooserPane = chooserPane;
	
	        Container contentPane = getContentPane();
	        contentPane.setLayout(new BorderLayout());
	        contentPane.add(chooserPane, BorderLayout.CENTER);
	
	        contentPane.add(buttonPane, BorderLayout.SOUTH);
	
	        pack();
	        setLocationRelativeTo(c);
	        
	        setBounds(new Rectangle(0, 0, 350, 400));
	        setResizable(false);
	        
	        // Center dialog.
	        Utility.centerOnScreen(this);
	        // Set font.
	        chooserPane.setFont(textFont);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
    }

    public void setVisible(boolean visible) {
    	try {
			
			if (visible)
	            initialFont = chooserPane.getFont();
	        super.setVisible(visible);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
    }

    public void reset() {
    	try {
			
			chooserPane.setFont(initialFont);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
    }

    static class Closer extends WindowAdapter implements Serializable {
        public void windowClosing(WindowEvent e) {
        	try {
				
				Window w = e.getWindow();
	            w.setVisible(false);
			}
			catch(Throwable expt) {
				Safe.exception(expt);
			};
        }
    }

    static class DisposeOnClose extends ComponentAdapter implements Serializable {
        public void componentHidden(ComponentEvent e) {
        	
        	try {
			
        		Window w = (Window) e.getComponent();
	            w.dispose();
        	}
        	catch(Throwable expt) {
        		Safe.exception(expt);
        	};
        }
    }
}

@SuppressWarnings("serial")
class FontTracker implements ActionListener, Serializable {
    FontChooser chooser;
    Font color;

    public FontTracker(FontChooser c) {
        chooser = c;
    }

    public void actionPerformed(ActionEvent e) {
    	try {
			
			color = chooser.getFont();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
    }

    public Font getFont() {
        return color;
    }
}



/**
 * A generic implementation of <code>{@link FontSelectionModel}</code>.
 *
 * @author Adrian BER
 */
class DefaultFontSelectionModel implements FontSelectionModel {

    /** The default selected font. */
    private static final Font DEFAULT_INITIAL_FONT = new Font("Dialog", Font.PLAIN, 12);
    
    /** The selected font. */
    private Font selectedFont;

    /** The change listeners notified by a change in this model. */
    private EventListenerList listeners = new EventListenerList();

    /**
     * Creates a <code>DefaultFontSelectionModel</code> with the
     * current font set to <code>Dialog, 12</code>.  This is
     * the default constructor.
     */
    public DefaultFontSelectionModel() {
        this(DEFAULT_INITIAL_FONT);
    }

    /**
     * Creates a <code>DefaultFontSelectionModel</code> with the
     * current font set to <code>font</code>, which should be
     * non-<code>null</code>.  Note that setting the font to
     * <code>null</code> is undefined and may have unpredictable
     * results.
     *
     * @param selectedFont the new <code>Font</code>
     */
    public DefaultFontSelectionModel(Font selectedFont) {
        if (selectedFont == null) {
            selectedFont = DEFAULT_INITIAL_FONT;
        }
        this.selectedFont = selectedFont;
    }

    public Font getSelectedFont() {
        return selectedFont;
    }

    public void setSelectedFont(Font selectedFont) {
    	try {
			
			if (selectedFont != null) {
	            this.selectedFont = selectedFont;
	            fireChangeListeners();
	        }
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
    }

    public void addChangeListener(ChangeListener listener) {
    	try {
			
			listeners.add(ChangeListener.class, listener);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
    }

    public void removeChangeListener(ChangeListener listener) {
    	try {
			
			listeners.remove(ChangeListener.class, listener);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
    }

    /** Fires the listeners registered with this model. */
    protected void fireChangeListeners() {
    	try {
			
			ChangeEvent ev = new ChangeEvent(this);
	        Object[] l = listeners.getListeners(ChangeListener.class);
	        for (Object listener : l) {
	            ((ChangeListener) listener).stateChanged(ev);
	        }
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
    }
}



/**
 * A model that supports selecting a <code>Font</code>.
 *
 * @author Adrian BER
 *
 * @see java.awt.Font
 */
interface FontSelectionModel {
    /**
     * Returns the selected <code>Font</code> which should be
     * non-<code>null</code>.
     *
     * @return  the selected <code>Font</code>
     * @see     #setSelectedFont
     */
    Font getSelectedFont();

    /**
     * Sets the selected font to <code>font</code>.
     * Note that setting the font to <code>null</code>
     * is undefined and may have unpredictable results.
     * This method fires a state changed event if it sets the
     * current font to a new non-<code>null</code> font.
     *
     * @param font the new <code>Font</code>
     * @see   #getSelectedFont
     * @see   #addChangeListener
     */
    void setSelectedFont(Font font);

    /**
     * Adds <code>listener</code> as a listener to changes in the model.
     * @param listener the <code>ChangeListener</code> to be added
     */
    void addChangeListener(ChangeListener listener);

    /**
     * Removes <code>listener</code> as a listener to changes in the model.
     * @param listener the <code>ChangeListener</code> to be removed
     */
    void removeChangeListener(ChangeListener listener);
}

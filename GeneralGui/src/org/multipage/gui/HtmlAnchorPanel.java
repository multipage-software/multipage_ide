/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;

import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Panel that displays HTML anchor editor.
 * @author vakol
 *
 */
public class HtmlAnchorPanel extends InsertPanel implements StringValueEditor {
	
	/**
	 * Components.
	 */
	private JLabel labelAnchorSource;
	private JRadioButton radioAreaAlias;
	private JRadioButton radioUrl;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JRadioButton radioAreaResource;
	private TextFieldEx textAreaAlias;
	private TextFieldEx textPageUrl;
	private TextFieldEx textAreaResource;
	private JButton buttonFindResource;
	private JButton buttonFindAreaAlias;

	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Serialized dialog states.
	 */
	protected static Rectangle bounds;
	private static boolean boundsSet;
	
	/**
	 * This editor object reference.
	 */
	private HtmlAnchorPanel editorReference = this;
	
	/**
	 * Handlers.
	 */
	private EditorValueHandler areaAliasHandler;
	private EditorValueHandler areaResourceHandler;
	
	/**
	 * Information attached to editor type.
	 * @author user
	 *
	 */
	class Attachment {

		/**
		 * Radio button.
		 */
		private JRadioButton radioButton;
		
		/**
		 * Event reference.
		 */
		private Runnable event;
		
		/**
		 * Reference to a method that gets string value.
		 */
		private ActionAdapter getValueMethodAdapter;
		
		/**
		 * Set value method name.
		 */
		private String setValueMethod;
		
		/**
		 * Constructor.
		 */
		public Attachment(JRadioButton radioButton, Runnable event, String getValueMethod, String setValueMethod) {
			try {
				
				this.radioButton = radioButton;
				this.event = event;
				this.getValueMethodAdapter = new ActionAdapter(editorReference, getValueMethod, null);
				this.setValueMethod = setValueMethod;
			}
			catch(Throwable expt) {
				Safe.exception(expt);
			};
		}
	}
	
	/**
	 * Maps editor type or editor control to additional objects.
	 */
	HashMap<String, Attachment> mapMeanings = new HashMap<String, Attachment>();
	
	/**
	 * Load map.
	 */
	private void loadMap() {
		try {
			
			mapMeanings.put(StringValueEditor.meansHtmlAnchorAreaAlias, new Attachment(radioAreaAlias, this::onAreaAlias, "getValueFromAreaAlias", "setAreaAlias"));
			mapMeanings.put(StringValueEditor.meansHtmlAnchorUrl, new Attachment(radioUrl, this::onPageUrl, "getValueFromUrl", "setUrl"));
			mapMeanings.put(StringValueEditor.meansHtmlAnchorAreaRes, new Attachment(radioAreaResource, this::onAreaResource, "getValueFromAreaResource", "setAreaResource"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set default states.
	 */
	public static void setDefaultData() {
		
		bounds = new Rectangle(0, 0, 500, 330);
		boundsSet = false;
	}

	/**
	 * Load serialized states.
	 * @param inputStream
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void serializeData(StateInputStream inputStream)
			throws IOException, ClassNotFoundException {
		
		bounds = Utility.readInputStreamObject(inputStream, Rectangle.class);
		boundsSet = true;
	}

	/**
	 * Save serialized states.
	 * @param outputStream
	 * @throws IOException
	 */
	public static void serializeData(StateOutputStream outputStream)
			throws IOException {
		
		outputStream.writeObject(bounds);
	}

	/**
	 * Initial string. 
	 */
	private String initialString;
	
	// $hide<<$
	/**
	 * Create the dialog.
	 * @param parentWindow 
	 */
	public HtmlAnchorPanel(String initialString) {
		
		try {
			initComponents();
			// $hide>>$
			this.initialString = initialString;
			postCreate();
			// $hide<<$
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Initialize components.
	 */
	private void initComponents() {
		
		SpringLayout sl_panelMain = new SpringLayout();
		setLayout(sl_panelMain);
		
		labelAnchorSource = new JLabel("org.multipage.gui.textAnchorSourceSelection");
		sl_panelMain.putConstraint(SpringLayout.NORTH, labelAnchorSource, 30, SpringLayout.NORTH, this);
		sl_panelMain.putConstraint(SpringLayout.WEST, labelAnchorSource, 30, SpringLayout.WEST, this);
		add(labelAnchorSource);
		
		radioAreaAlias = new JRadioButton("org.multipage.gui.textAreaAlias");
		sl_panelMain.putConstraint(SpringLayout.WEST, radioAreaAlias, 50, SpringLayout.WEST, this);
		radioAreaAlias.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onRadioButtonEvent();
			}
		});
		buttonGroup.add(radioAreaAlias);
		add(radioAreaAlias);
		
		radioAreaResource = new JRadioButton("org.multipage.gui.textResourceReference");
		sl_panelMain.putConstraint(SpringLayout.NORTH, radioAreaResource, 6, SpringLayout.SOUTH, radioAreaAlias);
		sl_panelMain.putConstraint(SpringLayout.WEST, radioAreaResource, 50, SpringLayout.WEST, this);
		radioAreaResource.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onRadioButtonEvent();
			}
		});
		buttonGroup.add(radioAreaResource);
		add(radioAreaResource);
		
		radioUrl = new JRadioButton("org.multipage.gui.textPageUrl");
		sl_panelMain.putConstraint(SpringLayout.NORTH, radioAreaAlias, 6, SpringLayout.SOUTH, radioUrl);
		sl_panelMain.putConstraint(SpringLayout.NORTH, radioUrl, 21, SpringLayout.SOUTH, labelAnchorSource);
		sl_panelMain.putConstraint(SpringLayout.WEST, radioUrl, 50, SpringLayout.WEST, this);
		radioUrl.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onRadioButtonEvent();
			}
		});
		buttonGroup.add(radioUrl);
		add(radioUrl);
		
		textAreaAlias = new TextFieldEx();
		sl_panelMain.putConstraint(SpringLayout.NORTH, textAreaAlias, 0, SpringLayout.NORTH, radioAreaAlias);
		sl_panelMain.putConstraint(SpringLayout.WEST, textAreaAlias, 53, SpringLayout.EAST, radioAreaAlias);
		textAreaAlias.setPreferredSize(new Dimension(6, 22));
		textAreaAlias.setColumns(25);
		add(textAreaAlias);
		
		textPageUrl = new TextFieldEx();
		sl_panelMain.putConstraint(SpringLayout.NORTH, textPageUrl, 0, SpringLayout.NORTH, radioUrl);
		sl_panelMain.putConstraint(SpringLayout.WEST, textPageUrl, 0, SpringLayout.WEST, textAreaAlias);
		textPageUrl.setPreferredSize(new Dimension(6, 22));
		textPageUrl.setColumns(25);
		add(textPageUrl);
		
		textAreaResource = new TextFieldEx();
		sl_panelMain.putConstraint(SpringLayout.NORTH, textAreaResource, 0, SpringLayout.NORTH, radioAreaResource);
		sl_panelMain.putConstraint(SpringLayout.WEST, textAreaResource, 0, SpringLayout.WEST, textAreaAlias);
		textAreaResource.setPreferredSize(new Dimension(6, 22));
		textAreaResource.setColumns(25);
		add(textAreaResource);
		
		buttonFindResource = new JButton("");
		sl_panelMain.putConstraint(SpringLayout.EAST, buttonFindResource, -33, SpringLayout.EAST, this);
		sl_panelMain.putConstraint(SpringLayout.EAST, textAreaResource, -3, SpringLayout.WEST, buttonFindResource);
		buttonFindResource.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onFindResource();
			}
		});
		sl_panelMain.putConstraint(SpringLayout.NORTH, buttonFindResource, 0, SpringLayout.NORTH, radioAreaResource);
		buttonFindResource.setPreferredSize(new Dimension(22, 22));
		buttonFindResource.setMargin(new Insets(0, 0, 0, 0));
		add(buttonFindResource);
		
		buttonFindAreaAlias = new JButton("");
		sl_panelMain.putConstraint(SpringLayout.EAST, textPageUrl, 0, SpringLayout.EAST, buttonFindAreaAlias);
		sl_panelMain.putConstraint(SpringLayout.EAST, buttonFindAreaAlias, -33, SpringLayout.EAST, this);
		sl_panelMain.putConstraint(SpringLayout.EAST, textAreaAlias, -3, SpringLayout.WEST, buttonFindAreaAlias);
		buttonFindAreaAlias.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onFindAreaAlias();
			}
		});
		sl_panelMain.putConstraint(SpringLayout.NORTH, buttonFindAreaAlias, 0, SpringLayout.NORTH, radioAreaAlias);
		buttonFindAreaAlias.setPreferredSize(new Dimension(22, 22));
		buttonFindAreaAlias.setMargin(new Insets(0, 0, 0, 0));
		add(buttonFindAreaAlias);
	}
	

	/**
	 * On radio button event.
	 */
	protected void onRadioButtonEvent() {
		try {
			
			// Call servicing method.
			String meaning = buttonGroup.getSelection().getActionCommand();
			
			Attachment attachment = mapMeanings.get(meaning);
			if (attachment != null) {
				attachment.event.run();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On area alias.
	 */
	private void onAreaAlias() {
		try {
			
			hideEditors();
			
			textAreaAlias.setVisible(true);
			buttonFindAreaAlias.setVisible(true);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On page URL.
	 */
	private void onPageUrl() {
		try {
			
			hideEditors();
			textPageUrl.setVisible(true);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On area resource.
	 */
	private void onAreaResource() {
		
		try {
			
			hideEditors();
			textAreaResource.setVisible(true);
			buttonFindResource.setVisible(true);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Save dialog.
	 */
	@Override
	public void saveDialog() {
		
		
	}

	/**
	 * Post creation.
	 */
	private void postCreate() {
		try {
			
			loadMap();
			localize();
			setIcons();
			setToolTips();
			setButtonActions();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set radio button's action commands
	 */
	private void setButtonActions() {
		try {
			
			radioAreaAlias.setActionCommand(StringValueEditor.meansHtmlAnchorAreaAlias);
			radioUrl.setActionCommand(StringValueEditor.meansHtmlAnchorUrl);
			radioAreaResource.setActionCommand(StringValueEditor.meansHtmlAnchorAreaRes);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get provider value from area alias.
	 */
	@SuppressWarnings("unused")
	private String getValueFromAreaAlias() {
		
		try {
			String alias = textAreaAlias.getText();
			if (alias.isEmpty()) {
				return "";
			}
			return String.format("[@URL areaAlias=\"#%s\"]", alias);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
	
	/**
	 * Get provider value from URL.
	 */
	@SuppressWarnings("unused")
	private String getValueFromUrl() {
		
		try {
			return textPageUrl.getText();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
	
	/**
	 * Get provider from area resource.
	 */
	@SuppressWarnings("unused")
	private String getValueFromAreaResource() {
		
		try {
			String resource = textAreaResource.getText();
			if (resource.isEmpty()) {
				return "";
			}
			return String.format("[@URL res=\"#%s\"]", resource);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
	
	/**
	 * Get provider value.
	 * @return
	 */
	@Override
	public String getSpecification() {
		
		try {
			String meaning = buttonGroup.getSelection().getActionCommand();
			ActionAdapter methodAdapter = mapMeanings.get(meaning).getValueMethodAdapter;
			String specification = (String) methodAdapter.run();
			return specification;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Set from initial string.
	 */
	private void setFromInitialString() {
		try {
			
			if (initialString != null) {
				
				String meaning = buttonGroup.getSelection().getActionCommand();
				String setValueMethod = mapMeanings.get(meaning).setValueMethod;
				ActionAdapter adapter = new ActionAdapter(editorReference, setValueMethod, new Class [] { String.class });
				adapter.run(initialString);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Sets area alias.
	 * @param initialString
	 */
	@SuppressWarnings("unused")
	private void setAreaAlias(String initialString) {
		try {
			
			// Retrieve area alias and set text field.
			Pattern pattern = Pattern.compile("\\[\\@URL areaAlias\\=\\\"\\#(.+?)\\\"\\]");
			Matcher matcher = pattern.matcher(initialString);
			
			if (matcher.matches() && matcher.groupCount() == 1) {
				String areaAlias = matcher.group(1);
				
				if (!areaAlias.isEmpty()) {
					textAreaAlias.setText(areaAlias);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Sets URL.
	 * @param initialString
	 */
	@SuppressWarnings("unused")
	private void setUrl(String initialString) {
		try {
			
			// Set text field.
			textPageUrl.setText(initialString);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * 
	 * @param initialString
	 */
	@SuppressWarnings("unused")
	private void setAreaResource(String initialString) {
		try {
			
			// Retrieve area resource and set text field.
			Pattern pattern = Pattern.compile("\\[\\@URL res\\=\\\"\\#(.+?)\\\"\\]");
			Matcher matcher = pattern.matcher(initialString);
			
			if (matcher.matches() && matcher.groupCount() == 1) {
				String areaResource = matcher.group(1);
				textAreaResource.setText(areaResource);
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
			
			Utility.localize(labelAnchorSource);
			Utility.localize(radioAreaAlias);
			Utility.localize(radioUrl);
			Utility.localize(radioAreaResource);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set panel component's icons.
	 */
	private void setIcons() {
		try {
			
			buttonFindResource.setIcon(Images.getIcon("org/multipage/gui/images/find_icon.png"));
			buttonFindAreaAlias.setIcon(Images.getIcon("org/multipage/gui/images/find_icon.png"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Sets component's tool tips.
	 */
	private void setToolTips() {
		try {
			
			buttonFindResource.setToolTipText(Resources.getString("org.multipage.gui.tooltipFindResource"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set callback
	 * @param callback
	 */
	public void setAreaAliasHandler(EditorValueHandler handler) {
		
		areaAliasHandler = handler;
	}
	
	/**
	 * On find area alias.
	 */
	protected void onFindAreaAlias() {
		try {
			
			if (areaAliasHandler == null) {
				
				Utility.show(this, "org.multipage.gui.messageAreasNotAvailable");
				return;
			}
			
			// Get handler.
			areaAliasHandler.ask();
			if (areaAliasHandler == null) {
				return;
			}
			
			// Set area resource.
			textAreaAlias.setText(areaAliasHandler.getText());
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set callback.
	 * @param callback
	 */
	public void setResourceNameHandler(EditorValueHandler handler) {
		
		areaResourceHandler = handler;
	}
	
	/**
	 * On find image.
	 */
	protected void onFindResource() {
		try {
			
			if (areaResourceHandler == null) {
				
				Utility.show(this, "org.multipage.gui.messageNoResourcesAssociated");
				return;
			}
			
			// Ask user.
			if (!areaResourceHandler.ask()) {
				return;
			}
			
			String areaResource = areaResourceHandler.getText();
			
			// Set area resource.
			textAreaResource.setText(areaResource);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Hide value editors.
	 */
	public void hideEditors() {
		try {
			
			textAreaAlias.setVisible(false);
			textPageUrl.setVisible(false);
			textAreaResource.setVisible(false);
			
			buttonFindAreaAlias.setVisible(false);
			buttonFindResource.setVisible(false);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/* (non-Javadoc)
	 * @see org.multipage.gui.InsertPanel#getWindowTitle()
	 */
	@Override
	public String getWindowTitle() {
		
		try {
			return Resources.getString("org.multipage.gui.textCssUrlBuilder");
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/* (non-Javadoc)
	 * @see org.multipage.gui.InsertPanel#getResultText()
	 */
	@Override
	public String getResultText() {
		
		try {
			return getSpecification();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/* (non-Javadoc)
	 * @see org.multipage.gui.InsertPanel#getContainerDialogBounds()
	 */
	@Override
	public Rectangle getContainerDialogBounds() {
		
		return bounds;
	}

	/* (non-Javadoc)
	 * @see org.multipage.gui.InsertPanel#setContainerDialogBounds(java.awt.Rectangle)
	 */
	@Override
	public void setContainerDialogBounds(Rectangle bounds) {
		
		CssTextLinePanel.bounds = bounds;
	}
	
	/* (non-Javadoc)
	 * @see org.multipage.gui.InsertPanel#boundsSet()
	 */
	@Override
	public boolean isBoundsSet() {

		return boundsSet;
	}
	
	/* (non-Javadoc)
	 * @see org.multipage.gui.InsertPanel#setBoundsSet(boolean)
	 */
	@Override
	public void setBoundsSet(boolean set) {
		
		boundsSet = set;
	}

	/**
	 * Get component.
	 */
	@Override
	public Component getComponent() {
		
		return this;
	}

	/**
	 * Get string value.
	 */
	@Override
	public String getStringValue() {
		
		try {
			return getSpecification();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Set string value.
	 */
	@Override
	public void setStringValue(String string) {
		try {
			
			initialString = string;
			setFromInitialString();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set value meaning.
	 */
	public void setValueMeaning(String valueMeaning) {
		try {
			
			Attachment attachment = mapMeanings.get(valueMeaning);
			if (attachment != null) {
				
				attachment.radioButton.setSelected(true);
				Safe.invokeLater(() -> {
					attachment.event.run();
				});
			}
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
			String meaning = buttonGroup.getSelection().getActionCommand();
			return meaning;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
	
	/**
	 * Set controls grayed. If a false value is returned a high level editor grayed this panel.
	 */
	@Override
	public boolean setControlsGrayed(boolean isDefault) {
		
		return false;
	}
}

/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Locale;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.multipage.util.Obj;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Panel that displays transitioneditor.
 * @author vakol
 *
 */
public class CssTransitionPanel extends InsertPanel implements StringValueEditor {

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
	 * Set default states.
	 */
	public static void setDefaultData() {
		
		bounds = new Rectangle(0, 0, 469, 450);
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
	
	/**
	 * Setting controls flag.
	 */
	private boolean settingControls = false;

	/**
	 * CSS transition class.
	 * @author 
	 *
	 */
	private class CssTransition {

		CssProperty property;
		String delay;
		String duration;
		String timingFunction;

		/**
		 * Constructor.
		 * @param property
		 * @param delay
		 * @param duration
		 * @param timingFunction
		 */
		public CssTransition(CssProperty property, String delay,
				String duration, String timingFunction) {
			
			this.property = property;
			this.delay = delay;
			this.duration = duration;
			this.timingFunction = timingFunction;
		}

		/**
		 * Constructor.
		 */
		public CssTransition() {
			this(new CssProperty("all"), "", "", "");
		}

		/**
		 * Convert to string.
		 */
		@Override
		public String toString() {
			
			try {
				return property.getHtmlText();
			}
			catch (Throwable e) {
				Safe.exception(e);
			}
			return "";
		}
	}

	/**
	 * List model reference.
	 */
	private DefaultListModel<CssTransition> listTransitionsModel;
	
	// $hide<<$
	/**
	 * Components.
	 */
	private JLabel labelProperty;
	private JScrollPane scrollPane;
	private JLabel labelDuration;
	private JTextField textDuration;
	private JComboBox comboDurationUnits;
	private JLabel labelTimingFunction;
	private JComboBox comboTimingFunction;
	private JLabel labelBezier;
	private JTextField textX1;
	private JTextField textY1;
	private JTextField textX2;
	private JTextField textY2;
	private JLabel labelX1;
	private JLabel labelY1;
	private JLabel labelX2;
	private JLabel labelY2;
	private JLabel labelSteps;
	private JTextField textSteps;
	private JComboBox comboStepsDirection;
	private TextFieldEx textDelay;
	private JComboBox comboDelayUnits;
	private JLabel labelDelay;
	private JButton buttonAddTransition;
	private JList<CssTransition> listTransitions;
	private JPopupMenu popupMenu;
	private JMenuItem menuAddTransition;
	private JMenuItem menuRemoveTransition;
	private JButton buttonSaveTransitionProperties;
	private JCheckBox checkAll;

	/**
	 * Create the dialog.
	 * @param parentWindow 
	 */
	public CssTransitionPanel(String initialString) {
		
		try {
			startSettingControls();
	
			initComponents();
			
			// $hide>>$
			this.initialString = initialString;
			postCreate();
			// $hide<<$
			
			stopSettingControls();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Initialize components.
	 */
	private void initComponents() {
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);

		labelProperty = new JLabel("org.multipage.gui.textTransitionProperty");
		springLayout.putConstraint(SpringLayout.NORTH, labelProperty, 47, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, labelProperty, 40, SpringLayout.WEST, this);
		add(labelProperty);
		
		scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(300, 100));
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 0, SpringLayout.NORTH, labelProperty);
		springLayout.putConstraint(SpringLayout.WEST, scrollPane, 6, SpringLayout.EAST, labelProperty);
		add(scrollPane);
		
		labelDuration = new JLabel("org.multipage.gui.textTransitionDuration");
		add(labelDuration);
		
		textDuration = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.NORTH, textDuration, 20, SpringLayout.SOUTH, scrollPane);
		springLayout.putConstraint(SpringLayout.EAST, labelDuration, -3, SpringLayout.WEST, textDuration);
		springLayout.putConstraint(SpringLayout.WEST, textDuration, 0, SpringLayout.WEST, scrollPane);
		springLayout.putConstraint(SpringLayout.NORTH, labelDuration, 0, SpringLayout.NORTH, textDuration);
		add(textDuration);
		textDuration.setColumns(6);
		
		comboDurationUnits = new JComboBox();
		springLayout.putConstraint(SpringLayout.NORTH, comboDurationUnits, 0, SpringLayout.NORTH, textDuration);
		comboDurationUnits.setPreferredSize(new Dimension(50, 20));
		springLayout.putConstraint(SpringLayout.WEST, comboDurationUnits, 0, SpringLayout.EAST, textDuration);
		add(comboDurationUnits);
		
		labelTimingFunction = new JLabel("org.multipage.gui.textTimingFunction");
		springLayout.putConstraint(SpringLayout.EAST, labelTimingFunction, 0, SpringLayout.EAST, labelProperty);
		add(labelTimingFunction);
		
		comboTimingFunction = new JComboBox();
		springLayout.putConstraint(SpringLayout.NORTH, comboTimingFunction, 14, SpringLayout.SOUTH, textDuration);
		comboTimingFunction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onTimingFunctionCombo();
			}
		});
		comboTimingFunction.setPreferredSize(new Dimension(100, 20));
		springLayout.putConstraint(SpringLayout.NORTH, labelTimingFunction, 3, SpringLayout.NORTH, comboTimingFunction);
		springLayout.putConstraint(SpringLayout.WEST, comboTimingFunction, 0, SpringLayout.WEST, scrollPane);
		add(comboTimingFunction);
		
		labelBezier = new JLabel("org.multipage.gui.textBezierFunction");
		springLayout.putConstraint(SpringLayout.NORTH, labelBezier, 0, SpringLayout.NORTH, labelTimingFunction);
		springLayout.putConstraint(SpringLayout.WEST, labelBezier, 33, SpringLayout.EAST, comboTimingFunction);
		add(labelBezier);
		
		textX1 = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, textX1, 0, SpringLayout.NORTH, labelTimingFunction);
		springLayout.putConstraint(SpringLayout.WEST, textX1, 6, SpringLayout.EAST, labelBezier);
		add(textX1);
		textX1.setColumns(5);
		
		textY1 = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, textY1, 0, SpringLayout.NORTH, labelTimingFunction);
		springLayout.putConstraint(SpringLayout.WEST, textY1, 0, SpringLayout.EAST, textX1);
		textY1.setColumns(5);
		add(textY1);
		
		textX2 = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, textX2, 0, SpringLayout.NORTH, labelTimingFunction);
		springLayout.putConstraint(SpringLayout.WEST, textX2, 0, SpringLayout.EAST, textY1);
		textX2.setColumns(5);
		add(textX2);
		
		textY2 = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, textY2, 0, SpringLayout.NORTH, labelTimingFunction);
		springLayout.putConstraint(SpringLayout.WEST, textY2, 0, SpringLayout.EAST, textX2);
		textY2.setColumns(5);
		add(textY2);
		
		labelX1 = new JLabel("x1");
		springLayout.putConstraint(SpringLayout.SOUTH, labelX1, -3, SpringLayout.NORTH, textX1);
		labelX1.setHorizontalAlignment(SwingConstants.CENTER);
		springLayout.putConstraint(SpringLayout.WEST, labelX1, 0, SpringLayout.WEST, textX1);
		springLayout.putConstraint(SpringLayout.EAST, labelX1, 0, SpringLayout.EAST, textX1);
		add(labelX1);
		
		labelY1 = new JLabel("y1");
		springLayout.putConstraint(SpringLayout.SOUTH, labelY1, -3, SpringLayout.NORTH, textY1);
		labelY1.setHorizontalAlignment(SwingConstants.CENTER);
		springLayout.putConstraint(SpringLayout.WEST, labelY1, 0, SpringLayout.WEST, textY1);
		springLayout.putConstraint(SpringLayout.EAST, labelY1, 0, SpringLayout.EAST, textY1);
		add(labelY1);
		
		labelX2 = new JLabel("x2");
		springLayout.putConstraint(SpringLayout.SOUTH, labelX2, -3, SpringLayout.NORTH, textX2);
		labelX2.setHorizontalAlignment(SwingConstants.CENTER);
		springLayout.putConstraint(SpringLayout.WEST, labelX2, 0, SpringLayout.WEST, textX2);
		springLayout.putConstraint(SpringLayout.EAST, labelX2, 0, SpringLayout.EAST, textX2);
		add(labelX2);
		
		labelY2 = new JLabel("y2");
		springLayout.putConstraint(SpringLayout.SOUTH, labelY2, -3, SpringLayout.NORTH, textY2);
		labelY2.setHorizontalAlignment(SwingConstants.CENTER);
		springLayout.putConstraint(SpringLayout.WEST, labelY2, 0, SpringLayout.WEST, textY2);
		springLayout.putConstraint(SpringLayout.EAST, labelY2, 0, SpringLayout.EAST, textY2);
		add(labelY2);
		
		labelSteps = new JLabel("org.multipage.gui.textStepFunction");
		springLayout.putConstraint(SpringLayout.NORTH, labelSteps, 14, SpringLayout.SOUTH, labelBezier);
		springLayout.putConstraint(SpringLayout.EAST, labelSteps, 0, SpringLayout.EAST, labelBezier);
		add(labelSteps);
		
		textSteps = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, textSteps, 0, SpringLayout.NORTH, labelSteps);
		springLayout.putConstraint(SpringLayout.WEST, textSteps, 0, SpringLayout.WEST, textX1);
		textSteps.setColumns(5);
		add(textSteps);
		
		comboStepsDirection = new JComboBox();
		springLayout.putConstraint(SpringLayout.WEST, comboStepsDirection, 0, SpringLayout.WEST, textY1);
		springLayout.putConstraint(SpringLayout.SOUTH, comboStepsDirection, 0, SpringLayout.SOUTH, textSteps);
		comboStepsDirection.setPreferredSize(new Dimension(100, 20));
		add(comboStepsDirection);
		
		textDelay = new TextFieldEx();
		springLayout.putConstraint(SpringLayout.NORTH, textDelay, 14, SpringLayout.SOUTH, comboTimingFunction);
		springLayout.putConstraint(SpringLayout.WEST, textDelay, 0, SpringLayout.WEST, scrollPane);
		textDelay.setColumns(6);
		add(textDelay);
		
		comboDelayUnits = new JComboBox();
		springLayout.putConstraint(SpringLayout.NORTH, comboDelayUnits, 0, SpringLayout.NORTH, textDelay);
		springLayout.putConstraint(SpringLayout.WEST, comboDelayUnits, 0, SpringLayout.EAST, textDelay);
		comboDelayUnits.setPreferredSize(new Dimension(50, 20));
		add(comboDelayUnits);
		
		labelDelay = new JLabel("org.multipage.gui.textTransitionDelay");
		springLayout.putConstraint(SpringLayout.NORTH, labelDelay, 0, SpringLayout.NORTH, textDelay);
		springLayout.putConstraint(SpringLayout.EAST, labelDelay, 0, SpringLayout.EAST, labelProperty);
		add(labelDelay);
		
		buttonAddTransition = new JButton("");
		springLayout.putConstraint(SpringLayout.WEST, buttonAddTransition, 3, SpringLayout.EAST, scrollPane);
		buttonAddTransition.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onAddTransition();
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, buttonAddTransition, 0, SpringLayout.NORTH, labelProperty);
		
		JList list = new JList();
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				onListSelection();
			}
		});
		listTransitions = list;
		scrollPane.setViewportView(listTransitions);
		
		popupMenu = new JPopupMenu();
		addPopup(list, popupMenu);
		
		menuAddTransition = new JMenuItem("org.multipage.gui.menuAddTransition");
		menuAddTransition.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onAddTransition();
			}
		});
		popupMenu.add(menuAddTransition);
		
		menuRemoveTransition = new JMenuItem("org.multipage.gui.menuRemoveTransition");
		menuRemoveTransition.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onRemoveTransition();
			}
		});
		popupMenu.add(menuRemoveTransition);
		buttonAddTransition.setPreferredSize(new Dimension(24, 24));
		add(buttonAddTransition);
		
		buttonSaveTransitionProperties = new JButton("");
		buttonSaveTransitionProperties.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onSaveTransitionProperties();
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, buttonSaveTransitionProperties, 3, SpringLayout.SOUTH, buttonAddTransition);
		springLayout.putConstraint(SpringLayout.WEST, buttonSaveTransitionProperties, 0, SpringLayout.WEST, buttonAddTransition);
		buttonSaveTransitionProperties.setPreferredSize(new Dimension(24, 24));
		add(buttonSaveTransitionProperties);
		
		checkAll = new JCheckBox("org.multipage.gui.textAllProperties");
		checkAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onAllProperties();
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, checkAll, 0, SpringLayout.NORTH, labelProperty);
		springLayout.putConstraint(SpringLayout.WEST, checkAll, 33, SpringLayout.EAST, buttonAddTransition);
		add(checkAll);
	}

	/**
	 * On all properties flag changed.
	 */
	protected void onAllProperties() {
		try {
			
			boolean isAllSelected = checkAll.isSelected();
			
			listTransitions.setEnabled(!isAllSelected);
			labelProperty.setEnabled(!isAllSelected);
			buttonAddTransition.setEnabled(!isAllSelected);
			buttonSaveTransitionProperties.setEnabled(!isAllSelected);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
			
	}

	/**
	 * On save transition properties.
	 */
	protected void onSaveTransitionProperties() {
		try {
			
			CssTransition transition = listTransitions.getSelectedValue();
			if (transition == null) {
				
				Utility.show(this, "org.multipage.gui.messageSelectSingleTransition");
				return;
			}
			
			// Save properties.
			transition.delay = getDelay();
			transition.duration = getDuration();
			transition.timingFunction = getTimingFunction();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On remove transition.
	 */
	protected void onRemoveTransition() {
		try {
			
			CssTransition transition = listTransitions.getSelectedValue();
			if (transition == null) {
				
				Utility.show(this, "org.multipage.gui.messageSelectSingleTransition");
				return;
			}
			
			// Ask user.
			if (!Utility.ask(this, "org.multipage.gui.messageRemoveSelectedTransition")) {
				return;
			}
			
			listTransitionsModel.removeElement(transition);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On list selection.
	 */
	protected void onListSelection() {
		try {
			
			CssTransition transition = listTransitions.getSelectedValue();
			if (transition == null) {
				
				resetControls();
				return;
			}
			
			// Set controls.
			setDelay(transition.delay);
			setDuration(transition.duration);
			setTimingFunction(transition.timingFunction);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Reset controls.
	 */
	private void resetControls() {
		try {
			
			setDelay("0.0s");
			setDuration("0.0s");
			setTimingFunction("ease");
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set delay.
	 * @param delay
	 */
	private void setDelay(String delay) {
		try {
			
			Utility.setCssValueAndUnits(delay, textDelay, comboDelayUnits, "0.0", "s");
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set duration.
	 * @param duration
	 */
	private void setDuration(String duration) {
		try {
			
			Utility.setCssValueAndUnits(duration, textDuration, comboDurationUnits, "0.0", "s");
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set timing function.
	 * @param timingFunction
	 */
	private void setTimingFunction(String timingFunction) {
		try {
			
			Obj<Integer> position = new Obj<Integer>(0);
			
			// Get timing function.
			String text = Utility.getNextMatch(timingFunction, position, "\\G\\s*(linear|ease-in-out|ease-in|ease-out|ease|step-start|step-end)");
			if (text != null) {
				Utility.selectComboNamedItem(comboTimingFunction, text.trim());
				
				// Reset other components.
				resetBezierFunction();
				resetStepFunction();
			}
			else {
				position.ref = 0;
				if (!processNextBezierFunction(timingFunction, position)) {
	
					position.ref = 0;
					if (!processNextStepFunction(timingFunction, position)) {
						return;
					}
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On add transition.
	 */
	protected void onAddTransition() {
		try {
			
			CssProperty property = CssFindPropertyDialog.showDialog(this, 'a');
			if (property == null) {
				return;
			}
			
			CssTransition transition = new CssTransition(property, getDelay(), getDuration(), getTimingFunction());
			listTransitionsModel.addElement(transition);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On timing function combo.
	 */
	protected void onTimingFunctionCombo() {
		try {
			
			if (comboTimingFunction.getSelectedIndex() <= 0) {
				return;
			}
			
			if (settingControls) {
				return;
			}
			startSettingControls();
			
			textX1.setText("");
			textY1.setText("");
			textX2.setText("");
			textY2.setText("");
			textSteps.setText("");
			comboStepsDirection.setSelectedIndex(0);
			
			stopSettingControls();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
			
	}

	/**
	 * Start setting controls.
	 */
	public void startSettingControls() {
		
		settingControls = true;
	}

	/**
	 * Stop setting controls.
	 */
	public void stopSettingControls() {
		
		Safe.invokeLater(() -> {

			settingControls = false;
		});
	}

	/**
	 * Load dialog.
	 */
	private void loadDialog() {
		try {
			
			setFromInitialString();
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
			
			localize();
			setToolTips();
			setIcons();
			
			loadUnits();
			loadComboBoxes();
			
			initList();
			
			loadDialog();
			
			resetControls();
			setListeners();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Initialize list.
	 */
	private void initList() {
		try {
			
			listTransitionsModel = new DefaultListModel<CssTransition>();
			listTransitions.setModel(listTransitionsModel);
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
			
			buttonAddTransition.setIcon(Images.getIcon("org/multipage/gui/images/insert.png"));
			menuAddTransition.setIcon(Images.getIcon("org/multipage/gui/images/insert.png"));
			menuRemoveTransition.setIcon(Images.getIcon("org/multipage/gui/images/cancel_icon.png"));
			buttonSaveTransitionProperties.setIcon(Images.getIcon("org/multipage/gui/images/save_icon.png"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set tool tips.
	 */
	private void setToolTips() {
		try {
			
			buttonAddTransition.setToolTipText(Resources.getString("org.multipage.gui.tooltipAddTransition"));
			buttonSaveTransitionProperties.setToolTipText(Resources.getString("org.multipage.gui.tooltipSaveTransition"));	
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
			
			// On Bezier change.
			Runnable onBezierChange = () -> {
				try {
					
					onBezierChange();
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
			};
			
			Utility.setTextChangeListener(textX1, onBezierChange);
			Utility.setTextChangeListener(textY1, onBezierChange);
			Utility.setTextChangeListener(textX2, onBezierChange);
			Utility.setTextChangeListener(textY2, onBezierChange);
			
			// On step function change.
			Utility.setTextChangeListener(textSteps, () -> {
				try {
					
					onStepFunctionChange();
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
	 * On step function change.
	 */
	protected void onStepFunctionChange() {
		try {
			
			if (settingControls) {
				return;
			}
			startSettingControls();
			
			comboTimingFunction.setSelectedIndex(0);
			textX1.setText("");
			textY1.setText("");
			textX2.setText("");
			textY2.setText("");
			
			stopSettingControls();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On Bezier change.
	 */
	protected void onBezierChange() {
		try {
			
			if (settingControls) {
				return;
			}
			startSettingControls();
			
			comboTimingFunction.setSelectedIndex(0);
			textSteps.setText("");
			comboStepsDirection.setSelectedIndex(0);
			
			stopSettingControls();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Load combo boxes values.
	 */
	private void loadComboBoxes() {
		try {
			
			Utility.loadEmptyItem(comboTimingFunction);
			Utility.loadNamedItems(comboTimingFunction, new String [][] {
					{"linear", "org.multipage.gui.textStepsTimingLinear"},
					{"ease", "org.multipage.gui.textStepsTimingEase"},
					{"ease-in", "org.multipage.gui.textStepsTimingEaseIn"},
					{"ease-in-out", "org.multipage.gui.textStepsTimingEaseInOut"},
					{"ease-out", "org.multipage.gui.textStepsTimingEaseOut"},
					{"step-start", "org.multipage.gui.textStepsTimingStepStart"},
					{"step-end", "org.multipage.gui.textStepsTimingStepEnd"}
			});
			
			Utility.loadNamedItems(comboStepsDirection, new String [][] {
					{"start", "org.multipage.gui.textStepsDirectionStart"},
					{"end", "org.multipage.gui.textStepsDirectionEnd"}
			});
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Load units.
	 */
	private void loadUnits() {
		try {
			
			final String [] timeUnits = new String [] { "s", "ms" };
			
			Utility.loadCssUnits(comboDurationUnits, timeUnits);
			Utility.loadCssUnits(comboDelayUnits, timeUnits);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get font specification.
	 * @return
	 */
	@Override
	public String getSpecification() {
		
		try {
			
			String specification = "";
			
			if (checkAll.isSelected()) {
				
				specification += getDuration();
				specification += " " + getDelay();
				specification += " all";
				specification += " " + getTimingFunction();
				
				return specification;
			}
			
			// Compile specification.
			boolean isFirst = true;
			int size = listTransitionsModel.getSize();
			
			if (size == 0) {
				return "none";
			}
			
			for (int index = 0; index < size; index++) {
				
				CssTransition transition = listTransitionsModel.get(index);
				CssProperty property = transition.property;
				
				for (String propertyName : property.getNames()) {
					
					if (!isFirst) {
						specification += ", ";
					}
					
					specification += transition.duration;
					specification += " " + transition.delay;
					specification += " " + propertyName;
					specification += " " + transition.timingFunction;
					
					isFirst = false;
				}
			}
			
			return specification;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Get delay.
	 * @return
	 */
	private String getDelay() {
		
		try {
			String delay = textDelay.getText();
			if (delay.isEmpty()) {
				return "0s";
			}
			
			String units = (String) comboDelayUnits.getSelectedItem();
			if (units == null) {
				units = "s";
			}
			try {
				Float.parseFloat(delay);
			}
			catch (Exception e) {
				delay = "0";
			}
			return delay + units;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Get timing function.
	 * @return
	 */
	private String getTimingFunction() {
		
		try {
			// Get named function.
			String functionName = Utility.getSelectedNamedItem(comboTimingFunction);
			if (!functionName.isEmpty()) {
				return functionName;
			}
			
			// Get Bezier function.
			String x1Text = textX1.getText();
			String y1Text = textY1.getText();
			String x2Text = textX2.getText();
			String y2Text = textY2.getText();
			
			if (!x1Text.isEmpty() || !y1Text.isEmpty() || !x2Text.isEmpty() || !y2Text.isEmpty()) {
				
				try {
					float x1 = Float.parseFloat(x1Text);
					x1Text = Utility.removeFloatNulls(String.valueOf(x1));
				}
				catch (Exception e) {
					x1Text = "0";
				}
				try {
					float y1 = Float.parseFloat(y1Text);
					y1Text = Utility.removeFloatNulls(String.valueOf(y1));
				}
				catch (Exception e) {
					y1Text = "0";
				}
				try {
					float x2 = Float.parseFloat(x2Text);
					x2Text = Utility.removeFloatNulls(String.valueOf(x2));
				}
				catch (Exception e) {
					x2Text = "0";
				}
				try {
					float y2 = Float.parseFloat(y2Text);
					y2Text = Utility.removeFloatNulls(String.valueOf(y2));
				}
				catch (Exception e) {
					y2Text = "0";
				}
				return String.format(Locale.ENGLISH, "cubic-bezier(%s, %s, %s, %s)", x1Text, y1Text, x2Text, y2Text);
			}
			
			// Get step function.
			String stepsText = textSteps.getText();
			String stepsDirection = Utility.getSelectedNamedItem(comboStepsDirection);
			
			if (stepsText.isEmpty()) {
				return "ease";
			}
	
			if (stepsDirection.isEmpty()) {
				stepsDirection = "start";
			}
	
			try {
				Float.parseFloat(stepsText);
			}
			catch (Exception e) {
				stepsText = "1";
			}
			
			return String.format("steps(%s, %s)", stepsText, stepsDirection);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}

	/**
	 * Get duration.
	 * @return
	 */
	private String getDuration() {
		
		try {
			String duration = textDuration.getText();
			if (duration.isEmpty()) {
				return "0s";
			}
			
			String units = (String) comboDurationUnits.getSelectedItem();
			if (units == null) {
				units = "s";
			}
			try {
				Float.parseFloat(duration);
			}
			catch (Exception e) {
				duration = "0";
			}
			return duration + units;
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
			
			// Initialize controls.
			resetControls();
			listTransitionsModel.clear();
	
			if (initialString != null) {
				
				initialString = initialString.trim();
				
				if (initialString.equals("none")) {
					return;
				}
				
				Obj<Integer> position = new Obj<Integer>(0);
				
				try {
					while (true) {
						
						CssTransition transition = new CssTransition();
	
						// Get duration.
						String text = Utility.getNextMatch(initialString, position, "\\G\\s*[0-9\\.]+(s|ms)");
						if (text == null) {
							break;
						}
						transition.duration = text.trim();
						
						// Get transition delay.
						text = Utility.getNextMatch(initialString, position, "\\G\\s*[0-9\\.\\-\\+]+(s|ms)");
						if (text == null) {
							break;
						}
						transition.delay = text.trim();
						
						// Get property name.
						text = Utility.getNextMatch(initialString, position, "\\G\\s*[A-Za-z0-9\\-_]+");
						if (text == null) {
							break;
						}
						String propertyName = text.trim();
						boolean isAll = propertyName.equals("all");
						
						if (!isAll) {
							transition.property = new CssProperty(propertyName);
						}
											
						// Get timing function.
						text = Utility.getNextMatch(initialString, position, "\\G\\s*(linear|ease-in-out|ease-in|ease-out|ease|step-start|step-end|cubic-bezier[^\\)]+\\)|steps[^\\)]+\\))");
						if (text == null) {
							break;
						}
						transition.timingFunction = text.trim();
						
						if (isAll) {
							
							setDuration(transition.duration);
							setDelay(transition.delay);
							setTimingFunction(transition.timingFunction);
							
							listTransitionsModel.clear();
							
							checkAll.setSelected(true);
							Safe.invokeLater(() -> {
								onAllProperties();
							});
							
							break;
						}
						
						listTransitionsModel.addElement(transition);
						
						// Get next comma.
						text = Utility.getNextMatch(initialString, position, "\\G\\s*,");
						if (text == null) {
							break;
						}
					}
				}
				catch (Exception e) {
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
			
	}

	/**
	 * Reset step function.
	 */
	private void resetStepFunction() {
		try {
			
			textSteps.setText("");
			comboStepsDirection.setSelectedIndex(0);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Reset Bezier function input components.
	 */
	private void resetBezierFunction() {
		try {
			
			textX1.setText("");
			textY1.setText("");
			textX2.setText("");
			textY2.setText("");
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Process next step function
	 * @param initialString
	 * @param position
	 * @return
	 */
	private boolean processNextStepFunction(String initialString,
			Obj<Integer> position) {
		
		try {
			String text = Utility.getNextMatch(initialString, position, "\\G\\s*steps\\s*\\(");
			if (text == null) {
				return false;
			}
			
			// Reset other components.
			comboTimingFunction.setSelectedIndex(0);
			resetBezierFunction();
			
			// Load number of steps.
			text = Utility.getNextMatch(initialString, position, "\\G\\s*[0-9]+");
			if (text == null) {
				return false;
			}
			text = text.trim();
			try {
				Integer.parseInt(text);
			}
			catch (Exception e) {
				text = "1";
			}
			textSteps.setText(text);
			
			// Skip comma.
			text = Utility.getNextMatch(initialString, position, "\\G\\s*\\,");
			if (text == null) {
				return false;
			}
			
			// Get direction.
			text = Utility.getNextMatch(initialString, position, "\\G\\s*(start|end)");
			if (text == null) {
				return false;
			}
			text = text.trim();
			Utility.selectComboNamedItem(comboStepsDirection, text);
			
			// Find closing bracket.
			text = Utility.getNextMatch(initialString, position, "\\G\\s*\\)");
			if (text == null) {
				return false;
			}
			
			return true;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Process next Bezier function.
	 * @param initialString
	 * @param position
	 * @return
	 */
	private boolean processNextBezierFunction(String initialString,
			Obj<Integer> position) {
		
		try {
			String text = Utility.getNextMatch(initialString, position, "\\G\\s*cubic-bezier\\s*\\(");
			if (text == null) {
				return false;
			}
				
			// Reset other time function components.
			comboTimingFunction.setSelectedIndex(0);
			resetStepFunction();
			
			text = getNextFloat(initialString, position);
			if (text == null) {
				return false;
			}
			textX1.setText(text);
			text = Utility.getNextMatch(initialString, position, "\\G\\s*\\,");
			if (text == null) {
				return false;
			}
			text = getNextFloat(initialString, position);
			if (text == null) {
				return false;
			}
			textY1.setText(text);
			text = Utility.getNextMatch(initialString, position, "\\G\\s*\\,");
			if (text == null) {
				return false;
			}
			text = getNextFloat(initialString, position);
			if (text == null) {
				return false;
			}
			textX2.setText(text);
			text = Utility.getNextMatch(initialString, position, "\\G\\s*\\,");
			if (text == null) {
				return false;
			}
			text = getNextFloat(initialString, position);
			if (text == null) {
				return false;
			}
			textY2.setText(text);
			
			// Find closing bracket.
			text = Utility.getNextMatch(initialString, position, "\\G\\s*\\)");
			if (text == null) {
				return false;
			}
			
			return true;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Get next float number from the text.
	 * @param text
	 * @param position
	 * @return
	 */
	private String getNextFloat(String text, Obj<Integer> position) {
		
		try {
			String floatNumber = Utility.getNextMatch(text, position, "\\G\\s*[0-9\\.\\-\\+]+");
			if (floatNumber == null) {
				return null;
			}
			
			try {
				Float.parseFloat(floatNumber.trim());
				
				floatNumber = Utility.removeFloatNulls(floatNumber);
				return floatNumber;
			}
			catch (Exception e) {
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Localize components.
	 */
	private void localize() {
		try {
			
			Utility.localize(labelProperty);
			Utility.localize(labelDuration);
			Utility.localize(labelTimingFunction);
			Utility.localize(labelBezier);
			Utility.localize(labelSteps);
			Utility.localize(labelDelay);
			Utility.localize(menuAddTransition);
			Utility.localize(menuRemoveTransition);
			Utility.localize(checkAll);
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
			return Resources.getString("org.multipage.gui.textCssTransitionBuilder");
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
		
		CssTransitionPanel.bounds = bounds;
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
	 * @return
	 */
	@Override
	public Component getComponent() {
		
		return this;
	}

	/**
	 * Get string value.
	 * @return
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
	 * @param string
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
	 * Get value meaning.
	 * @return
	 */
	@Override
	public String getValueMeaning() {
		
		return meansCssTransition;
	}

	/**
	 * Set controls grayed. If a false value is returned a high level editor grayed this panel.
	 */
	@Override
	public boolean setControlsGrayed(boolean isDefault) {
		
		if (!isDefault) {
			Safe.invokeLater(() -> {
				onAllProperties();
			});
		}
		return false;
	}
	
	/**
	 * Add popup menu.
	 * @param component
	 * @param popup
	 */
	private void addPopup(Component component, final JPopupMenu popup) {
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
						
						if (!checkAll.isSelected()) {
							popup.show(e.getComponent(), e.getX(), e.getY());
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
}

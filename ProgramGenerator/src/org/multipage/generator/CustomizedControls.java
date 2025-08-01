/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.multipage.gui.StateInputStream;
import org.multipage.gui.StateOutputStream;
import org.multipage.gui.Utility;
import org.multipage.util.Safe;

/**
 * Dialog that displays editor for GUI control's properties.
 * @author vakol
 *
 */
public class CustomizedControls extends JDialog {

	// $hide>>$
	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Tools minimum and maximum values.
	 */
	private static final int toolsMinimum = 32;
	private static final int toolsMaximum = 64;
	
	/**
	 * Arc size percent.
	 */
	private static int arcSizePercent = 5;

	/**
	 * Serialized states.
	 */
	private static int toolSizeState;
	
	/**
	 * Focused area width.
	 */
	private static int focusedAreaWidth;

	/**
	 * Serialize module data.
	 * @param inputStream
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void seriliazeData(StateInputStream inputStream)
		throws IOException, ClassNotFoundException {

		// Read tool size.
		toolSizeState = inputStream.readInt();
		// Read arc size.
		arcSizePercent = inputStream.readInt();
		
		focusedAreaWidth = inputStream.readInt();
		AreaDiagramPanel.focusAreaShapeWidth = focusedAreaWidth;
	}
		
	/**
	 * Serialize module data.
	 * @param outputStream
	 * @throws IOException
	 */
	public static void serializeData(StateOutputStream outputStream)
		throws IOException {
		
		// Write tool size.
		outputStream.writeInt(toolSizeState);
		// Write arc size.
		outputStream.writeInt(arcSizePercent);
		
		outputStream.writeInt(focusedAreaWidth);
	}

	/**
	 * Set default.
	 */
	public static void setDefaultData() {

		toolSizeState = 48;
		arcSizePercent = 0;
		focusedAreaWidth = 600;
	}

	/**
	 * Load dialog.
	 */
	private void loadDialog() {
		try {
			
			// Set tool width and height.
			Tool.setWidth(toolSizeState);
			Tool.setHeight(toolSizeState);
			// Set slider position.
			toolsSlider.setValue(toolSizeState);
			// Set slider position.
			arcSlider.setValue(arcSizePercent);
			
			sliderFocusedAreaWidth.setValue((int) focusedAreaWidth);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
		
	/**
	 * Save dialog.
	 */
	private void saveDialog() {
		try {
			
			// Write tool size.
			toolSizeState = Tool.getWidth();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	// $hide<<$
	/**
	 * Components.
	 */
	protected final JPanel contentPanel = new JPanel();
	private javax.swing.JSlider toolsSlider;
	private JLabel labelToolsSize;
	private JLabel labelAreaArcSize;
	private JSlider arcSlider;
	private JLabel labelFocusedAreaWidth;
	private JSlider sliderFocusedAreaWidth;

	/**
	 * Constructor.
	 * @param owner
	 */
	public CustomizedControls(Window owner) {
		super(owner, ModalityType.APPLICATION_MODAL);
		
		try {
			// Initialize components.
			initComponents();
			// $hide>>$
			// Post creation.
			postCreation();
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

		setBounds(100, 100, 390, 284);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

        toolsSlider = new javax.swing.JSlider();
        toolsSlider.setValue(48);
        toolsSlider.setBounds(10, 37, 364, 45);
        contentPanel.add(toolsSlider);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		
		setResizable(false);

		// Set tools size slider minimum and maximum.
		toolsSlider.setMinimum(toolsMinimum);
		toolsSlider.setMaximum(toolsMaximum);
        
		// Turn on labels at major tick marks.
		toolsSlider.setMajorTickSpacing(16);
		toolsSlider.setMinorTickSpacing(2);
		toolsSlider.setPaintTicks(true);
		toolsSlider.setPaintLabels(true);
		// Set slider listener.
		toolsSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				onToolsSizeChanged();
			}
		});
		
		labelToolsSize = new JLabel("org.multipage.generator.textToolsSize");
		labelToolsSize.setBounds(10, 11, 364, 14);
		contentPanel.add(labelToolsSize);
		
		labelAreaArcSize = new JLabel("org.multipage.generator.textAreaArcSize");
		labelAreaArcSize.setBounds(10, 93, 364, 14);
		contentPanel.add(labelAreaArcSize);
		
		arcSlider = new JSlider();
		arcSlider.setValue(5);
		arcSlider.setMaximum(27);
		arcSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				onArcChanged();
			}
		});
		arcSlider.setMajorTickSpacing(5);
		arcSlider.setMinorTickSpacing(1);
		arcSlider.setPaintLabels(true);
		arcSlider.setPaintTicks(true);
		arcSlider.setBounds(10, 118, 364, 47);
		contentPanel.add(arcSlider);
		
		labelFocusedAreaWidth = new JLabel("org.multipage.generator.textFocusedAreaWidth");
		labelFocusedAreaWidth.setBounds(10, 173, 364, 14);
		contentPanel.add(labelFocusedAreaWidth);
		
		sliderFocusedAreaWidth = new JSlider();
		sliderFocusedAreaWidth.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				onFocusedAreaWidthChaged();
			}
		});
		sliderFocusedAreaWidth.setValue(600);
		sliderFocusedAreaWidth.setMinimum(600);
		sliderFocusedAreaWidth.setPaintTicks(true);
		sliderFocusedAreaWidth.setPaintLabels(true);
		sliderFocusedAreaWidth.setMinorTickSpacing(100);
		sliderFocusedAreaWidth.setMaximum(2000);
		sliderFocusedAreaWidth.setMajorTickSpacing(200);
		sliderFocusedAreaWidth.setBounds(10, 198, 364, 47);
		contentPanel.add(sliderFocusedAreaWidth);

		
		// Center dialog.
        Dimension dimension = getSize();
        Dimension screen = getToolkit().getScreenSize();
        setLocation((screen.width - dimension.width) / 2, (screen.height - dimension.height) / 2);
        
        setTitle("org.multipage.generator.textCustomizedControls");
    }


	/**
	 * Localizes components.
	 */
	private void localize() {
		try {
			
			Utility.localize(this);
			Utility.localize(labelToolsSize);
			Utility.localize(labelAreaArcSize);
			Utility.localize(labelFocusedAreaWidth);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Post creation.
	 */
	private void postCreation() {
		try {
			
			// Localize components.
			localize();
			// Set icons.
			setIcons();
			// Load dialog.
	        loadDialog();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set icons.
	 */
	private void setIcons() {

	}
	
	/**
	 * On tools size changed event.
	 */
	protected void onToolsSizeChanged() {
		try {
			
			// Set tools width and height.
			int newSize = toolsSlider.getValue();
			Tool.setWidth(newSize);
			Tool.setHeight(newSize);
			// Repaint main window.
			GeneratorMainFrame mainFrame = GeneratorMainFrame.getFrame();
			if (mainFrame != null) {
	
				mainFrame.repaintAfterToolsChanged();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * On arc changed.
	 */
	protected void onArcChanged() {
		try {
			
			// Set new value.
			arcSizePercent = arcSlider.getValue();
	
			// Repaint main window.
			GeneratorMainFrame mainFrame = GeneratorMainFrame.getFrame();
			if (mainFrame != null) {
				mainFrame.repaintAfterToolsChanged();
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On focused area width changed.
	 */
	protected void onFocusedAreaWidthChaged() {
		try {
			
			// Set and save slider value.
			focusedAreaWidth = sliderFocusedAreaWidth.getValue();
			AreaDiagramPanel.focusAreaShapeWidth = focusedAreaWidth;
			
			GeneratorMainFrame.getVisibleAreasDiagram().setNotAnimateNextFocus();
			GeneratorMainFrame.getVisibleAreasDiagram().focusBasicArea();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Dispose dialog.
	 */
	public void disposeDialog() {
		try {
			
			saveDialog();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Get arc size in percent.
	 * @return
	 */
	public static int getArcSizePercent() {
		
		return arcSizePercent;
	}
}

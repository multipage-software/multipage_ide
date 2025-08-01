/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2020-04-21
 *
 */
package org.multipage.sync;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.multipage.gui.HttpException;
import org.multipage.gui.Images;
import org.multipage.gui.StateInputStream;
import org.multipage.gui.StateOutputStream;
import org.multipage.gui.TextPaneEx;
import org.multipage.gui.Utility;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Dialog that displays message from the Area Server.
 * @author vakol
 *
 */
public class MessageDialog extends JDialog {

	/**
	 * Version
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Label texts
	 */
	private String detailLabel = null;
	private String backLabel = null;
	
	/**
	 * Color of the controls
	 */
	private static final Color controlsColor = new Color(0, 117, 177);
	
	/**
	 * Color of an Area Server error
	 */
	private static final Color errorColor = Color.RED;
	
	/**
	 * Area server error REGEX group name
	 */
	private static final String regexErrorGroup = "Error";
	
	/**
	 * Area server error REGEX
	 */
	private static final Pattern areaServerErrorRegex = Pattern.compile(
															String.format("(?<%s>\\#ERROR.+?\\#)", regexErrorGroup),
															Pattern.MULTILINE
															);

	/**
	 * Top margin of the text panel
	 */
	private static final int textTopMarging = 40;
	
	/**
	 * Dialog bounds
	 */
	private static Rectangle bounds = null;
	
	/**
	 * Dialog singleton
	 */
	private static MessageDialog dialog = null;
		
	/**
	 * Read data
	 * @param inputStream
	 */
	public static void serializeData(StateInputStream inputStream)
		throws IOException, ClassNotFoundException {
		
		bounds = Utility.readInputStreamObject(inputStream, Rectangle.class);
	}
	
	/**
	 * Write data
	 * @param outputStream
	 */
	public static void seriliazeData(StateOutputStream outputStream)
		throws IOException {
		
		outputStream.writeObject(bounds);
	}
	
	/**
	 * The flag is true if a detailed info is displayed
	 */
	private boolean detailsDisplayed = false;

	/**
	 * Message text.
	 */
	private String message = null;
	
	/**
	 * Detailed text
	 */
	private String messageDetails = null;
	
	/**
	 * Controls
	 */
	private final JPanel contentPanel = new JPanel();
	private JButton buttonOK;
	private TextPaneEx textPane;
	private JScrollPane scrollPane;
	private JLabel labelDetail;
	private SpringLayout sl_contentPanel;
	
	/**
	 * Create the dialog.
	 */
	public MessageDialog() {
		
		try {
			initComponents();
			localize();
			setIcons();
			setControls();
			
			loadDialog();
			
			// Make the dialog top most window.
			setAlwaysOnTop(true);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Set icons
	 */
	private void setIcons() {
		try {
			
			this.setIconImage(Images.getImage("org/multipage/generator/images/main_icon.png"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Initialize components.
	 */
	private void initComponents() {
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setTitle("org.multipage.sync.menuMessageDialogTitle");
		setBounds(100, 100, 375, 246);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				onCancel();
			}
		});
		sl_contentPanel = new SpringLayout();
		contentPanel.setLayout(sl_contentPanel);
		{
			scrollPane = new JScrollPane();
			sl_contentPanel.putConstraint(SpringLayout.NORTH, scrollPane, textTopMarging, SpringLayout.NORTH, contentPanel);
			sl_contentPanel.putConstraint(SpringLayout.WEST, scrollPane, 5, SpringLayout.WEST, contentPanel);
			sl_contentPanel.putConstraint(SpringLayout.EAST, scrollPane, -5, SpringLayout.EAST, contentPanel);
			scrollPane.setBorder(null);
			contentPanel.add(scrollPane);

		}
		{
			buttonOK = new JButton("org.multipage.sync.textOK");
			sl_contentPanel.putConstraint(SpringLayout.SOUTH, scrollPane, -6, SpringLayout.NORTH, buttonOK);
			
			textPane = new TextPaneEx();
			textPane.setBackground(UIManager.getColor("control"));
			scrollPane.setViewportView(textPane);
			textPane.setContentType("text/html");
			textPane.setBackground(SystemColor.control);
			textPane.setBorder(null);
			textPane.setMargin(new Insets(0, 0, 0, 0));

			sl_contentPanel.putConstraint(SpringLayout.SOUTH, buttonOK, -10, SpringLayout.SOUTH, contentPanel);
			sl_contentPanel.putConstraint(SpringLayout.EAST, buttonOK, -10, SpringLayout.EAST, contentPanel);
			contentPanel.add(buttonOK);
			buttonOK.setMargin(new Insets(0, 0, 0, 0));
			buttonOK.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onOK();
				}
			});
			buttonOK.setPreferredSize(new Dimension(80, 25));
			buttonOK.setActionCommand("OK");
			getRootPane().setDefaultButton(buttonOK);
		}
		
		labelDetail = new JLabel("org.multipage.sync.textDetail");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, labelDetail, 0, SpringLayout.NORTH, buttonOK);
		labelDetail.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				onDetail();
			}
		});
		sl_contentPanel.putConstraint(SpringLayout.WEST, labelDetail, 16, SpringLayout.WEST, contentPanel);
		labelDetail.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		contentPanel.add(labelDetail);
	}
	
	/**
	 * Load dialog
	 */
	private void loadDialog() {
		try {
			
			// Set bounds
			if (bounds == null || bounds.isEmpty()) {
				Utility.centerOnScreen(this);
			}
			else {
				setBounds(bounds);
			}
			
			// Load messages
			detailLabel = Resources.getString("org.multipage.sync.textDetail");
			backLabel = Resources.getString("org.multipage.sync.textBackToMessage");
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Save dialog
	 */
	private void saveDialog() {
		try {
			
			bounds = this.getBounds();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Localize dialog controls
	 */
	private void localize() {
		try {
			
			Utility.localize(this);
			Utility.localize(buttonOK);
			Utility.localize(labelDetail);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On detail
	 */
	protected void onDetail() {
		try {
			
			// Switch the flag.
			if (areDetails()) {
				detailsDisplayed = !detailsDisplayed;
			}
			else {
				// ... or disable the flag
				detailsDisplayed = false;
			}
			
			// Display remembered message depending on the previous flag
			displayRememberedMessage();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Display remembered message
	 */
	private void displayRememberedMessage() {
		try {
			
			// Check the detailed message
			if (!areDetails()) {
				detailsDisplayed = false;
			}
			
			// Get text pane attributes
			StyledDocument document = textPane.getStyledDocument();
			SimpleAttributeSet attributes = new SimpleAttributeSet();
			
			// Display message
			labelDetail.setVisible(detailsDisplayed);
			if (detailsDisplayed) {
				
				textPane.setText(messageDetails);
				labelDetail.setText(backLabel);
				StyleConstants.setAlignment(attributes, StyleConstants.ALIGN_LEFT);
				sl_contentPanel.putConstraint(SpringLayout.NORTH, scrollPane, 10, SpringLayout.NORTH, contentPanel);
			}
			else {
				
				textPane.setText(message);
				labelDetail.setText(detailLabel);
				StyleConstants.setAlignment(attributes, StyleConstants.ALIGN_CENTER);
				sl_contentPanel.putConstraint(SpringLayout.NORTH, scrollPane, textTopMarging, SpringLayout.NORTH, contentPanel);
			}
			
			// Set modified text pane attributes
			document.setParagraphAttributes(0, document.getLength(), attributes, false);
			
			// Scroll the detailed text to the beginning
			textPane.grabFocus();
			textPane.setCaretPosition(0);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Returns true if there are details to display
	 * @return
	 */
	private boolean areDetails() {
		
		try {
			boolean areDetails = (messageDetails != null && !messageDetails.isEmpty());
			return areDetails;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * On dialog end
	 */
	private void endDialog() {
		try {
			
			// Reset messages
			message = null;
			messageDetails = null;	
			detailsDisplayed = false;
			
			// Save and dispose the dialog
			saveDialog();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
			
		dispose();		
	}
	
	/**
	 * On OK
	 */
	protected void onOK() {
		try {
			
			endDialog();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On cancel dialog
	 */
	protected void onCancel() {
		try {
			
			endDialog();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set button color
	 * @param button
	 */
	private void setButtonColor(JButton button) {
		try {
			
			BasicButtonUI basicButtonUI = new BasicButtonUI();
			button.setUI(basicButtonUI);
			button.setOpaque(true);
			button.setContentAreaFilled(true);
			button.setBackground(controlsColor);
			button.setForeground(Color.WHITE);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set controls
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	private void setControls() {
		try {
			
			// Set OK button
			setButtonColor(buttonOK);
			
			// Set label
			labelDetail.setForeground(controlsColor);
			Font font = labelDetail.getFont();
			Map attributes = font.getAttributes();
			attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_GRAY);
			labelDetail.setFont(font.deriveFont(attributes));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Display message
	 * @param parameters
	 * @param messageResourceId
	 */
	public static void show(String messageResourceId, Object ... parameters) {
		try {
			
			// Get localized message
			String message = Resources.getString(messageResourceId);
			// Set message parameters
			message = String.format(message, parameters);
			
			// Display message
			showDialog(message, null);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Display message
	 * @param messageResourceId
	 * @param exception
	 */
	public static void showException(String messageResourceId, Exception exception) {
		try {
			
			// Get localized message
			String messageTemplate = Resources.getString(messageResourceId);
			String message = String.format(messageTemplate, exception.getLocalizedMessage());
			String messageDetails = null;
			
			// On HTTP exception
			if (exception instanceof HttpException) {
				HttpException httpException = (HttpException) exception;
				
				// Get exception body
				messageDetails = httpException.getExceptionBody();
			}
			
			// Show dialog
			showDialog(message, messageDetails);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Make the dialog message HTML coded
	 * @param message
	 * @return
	 */
	public static String makeHtmlMessage(String message) {

		try {
			message = message.trim();
			
			String finalMessage = message.replaceAll("\n|(\r\n?)", "<br>");
			finalMessage = String.format("<html><font size=\"3\" face=\"Monospace\">%s</font></html>", finalMessage);
			
			return finalMessage;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
	
	/**
	 * Display message
	 * @param message
	 * @param messageDetails
	 */
	public static void showDialog(String message, String messageDetails) {
		
		Safe.invokeLater(() -> {
			
			try {
				// Create dialog window
				if (MessageDialog.dialog == null) {
					MessageDialog.dialog = new MessageDialog();
				}
				String messageText = message;
						
				// Trim the message
				if (messageText == null || messageText.trim().isEmpty()) {
					messageText = Resources.getString("org.multipage.sync.messageUnknownError");
				}
				
				// Load message prefix
				String prefix = Resources.getString("org.multipage.sync.messageSyncError");
				messageText = String.format("%s\n%s", prefix, message);
				
				// Check if detailed message exists
				boolean existsDetail = messageDetails != null && !messageDetails.trim().isEmpty();
				
				// Show/hide details control
				MessageDialog.dialog.labelDetail.setVisible(existsDetail);
				
				// Remember the message
				MessageDialog.dialog.message = makeHtmlMessage(messageText);
				
				// If there exist message details, inform user about that
				if (existsDetail) {
					messageText += "\n\n" + Resources.getString("org.multipage.sync.messageSeeDetails");
					
					// Prepare message details. Color possible Area Server exceptions in the detailed message.
					String finalMessageDetails = Utility.colorHtmlTexts(messageDetails, areaServerErrorRegex, regexErrorGroup, errorColor);
					MessageDialog.dialog.messageDetails = makeHtmlMessage(finalMessageDetails);
				}
				else {
					MessageDialog.dialog.messageDetails = "";
				}
				
				// Initialize the flag
				MessageDialog.dialog.detailsDisplayed = false;
				
				// Display remembered message depending on the previous flag
				MessageDialog.dialog.displayRememberedMessage();
				
				// TODO: Setting the window always on top may cause other active message boxes displayed behind this dialog
				// to block this window with a dead lock.
				//dialog.setAlwaysOnTop(true);
				
				// Set visible
				dialog.setVisible(true);
			}
			catch (Exception e) {
				Utility.show2(null, e.getLocalizedMessage());
			}
		});
	}
	
	/**
	 * Display message
	 * @param message
	 */
	public static void showDialog(String message) {
		try {
			
			// Delegate the call
			showDialog(message, null);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}

/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2021-12-20
 */
package program.builder;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.function.Consumer;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.SpringLayout;

import org.multipage.gui.Images;
import org.multipage.gui.Utility;
import org.multipage.util.Safe;

/**
 * Dialog that displays user input for pin.
 * @author vakol
 *
 */
public class PinFrame extends JFrame {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Window boundaries.
	 */
	private static Rectangle bounds = null;
	
	/**
	 * Frame controls.
	 */
	private JButton buttonOk;
	private JButton buttonCancel;
	private JPasswordField pinField;
	private JLabel labelIsertPin;

	//$hide>>$
	/**
	 * Frame object fields.
	 */
	private Consumer<Boolean> confirmedLambda = null;
	//$hide<<$
	
	/**
	 * Show frame.
	 * @param parent
	 */
	public static void showFrame(Consumer<Boolean> confirmedLambda) {
		try {
			
			// Create new dialog object and make it visible.
			PinFrame dialog = new PinFrame();
			dialog.confirmedLambda = confirmedLambda;
			dialog.setVisible(true);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Create the frame.
	 */
	public PinFrame() {
		
		try {
			setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			initComponents();
			postCreate(); //$hide$
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Initialize components.
	 */
	private void initComponents() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				onCancel();
			}
		});
		setTitle("program.builder.titleInsertPin");
		SpringLayout springLayout = new SpringLayout();
		getContentPane().setLayout(springLayout);
		
		buttonCancel = new JButton("textCancel");
		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});
		buttonCancel.setMargin(new Insets(0, 0, 0, 0));
		buttonCancel.setPreferredSize(new Dimension(80, 25));
		springLayout.putConstraint(SpringLayout.SOUTH, buttonCancel, -10, SpringLayout.SOUTH, getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, buttonCancel, -10, SpringLayout.EAST, getContentPane());
		getContentPane().add(buttonCancel);
		
		buttonOk = new JButton("textOk");
		buttonOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onOk();
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, buttonOk, 0, SpringLayout.NORTH, buttonCancel);
		springLayout.putConstraint(SpringLayout.EAST, buttonOk, -6, SpringLayout.WEST, buttonCancel);
		buttonOk.setPreferredSize(new Dimension(80, 25));
		buttonOk.setMargin(new Insets(0, 0, 0, 0));
		getContentPane().add(buttonOk);
		
		labelIsertPin = new JLabel("program.builder.textInsertPin");
		springLayout.putConstraint(SpringLayout.NORTH, labelIsertPin, 30, SpringLayout.NORTH, getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, labelIsertPin, 10, SpringLayout.WEST, getContentPane());
		getContentPane().add(labelIsertPin);
		
		pinField = new JPasswordField();
		springLayout.putConstraint(SpringLayout.NORTH, pinField, 6, SpringLayout.SOUTH, labelIsertPin);
		springLayout.putConstraint(SpringLayout.WEST, pinField, 0, SpringLayout.WEST, labelIsertPin);
		springLayout.putConstraint(SpringLayout.EAST, pinField, -10, SpringLayout.EAST, getContentPane());
		getContentPane().add(pinField);
	}

	/**
	 * Post creation of the dialog.
	 */
	private void postCreate() {
		try {
			
			localize();
			setIcons();
			loadDialog();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
			
	}
	
	/**
	 * Localize texts of the dialog controls.
	 */
	private void localize() {
		try {
			
			Utility.localize(this);
			Utility.localize(buttonOk);
			Utility.localize(buttonCancel);
			Utility.localize(labelIsertPin);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Set dialog icons.
	 */
	private void setIcons() {
		try {
			
			setIconImage(Images.getImage("org/multipage/gui/images/main.png"));
			buttonOk.setIcon(Images.getIcon("org/multipage/gui/images/ok_icon.png"));
			buttonCancel.setIcon(Images.getIcon("org/multipage/gui/images/cancel_icon.png"));
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Dialog confirmed by user click on the [OK] button.
	 */
	protected void onOk() {
		try {
			
			saveDialog();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
		
		dispose();
		
		if (confirmedLambda != null) {
			try {
				
				// TODO: <---MAKE Enable checking PIN.
				// Check PIN.
				confirmedLambda.accept(true);//checkPin());
			}
			catch(Throwable expt) {
				Safe.exception(expt);
			};
		}
	}
	
	/**
	 * Check PIN code.
	 * @return
	 */
	private Boolean checkPin() {
		
		try {
			// Stored PIN hash.
			final byte [] salt = {97, -55, -124, 54, 96, -99, 97, -72, 3, -21, 57, -85, -11, -50, -108, -14};

			// Get password hash.
			char [] password = pinField.getPassword();
			KeySpec spec = new PBEKeySpec(password, salt, 65536, 128);
			Arrays.fill(password, 0, password.length, '\0');
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			
			// Generate hash.
			//byte [] newHash = factory.generateSecret(spec).getEncoded();
			
			// Check the hash.
			if (Arrays.equals(new byte [] {29, 38, -101, -114, 14, 93, -33, 3, 118, 54, -59, 22, 23, 52, -62, 60},
					factory.generateSecret(spec).getEncoded())) {
				return true;
			}
		}
		catch (Exception e) {
		}
		
		return false;
	}

	/**
	 * Dialog has been canceled with [Cancel] or [X] button.
	 */
	protected void onCancel() {
		try {
			
			saveDialog();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
		
		dispose();
		
		if (confirmedLambda != null) {
			try {
				
				confirmedLambda.accept(false);
			}
			catch(Throwable expt) {
				Safe.exception(expt);
			};
		}
	}
	
	/**
	 * Load and set initial state of the dialog.
	 */
	private void loadDialog() {
		try {
			
			// Set dialog window boundaries.
			if (bounds != null && !bounds.isEmpty()) {
				setBounds(bounds);
			}
			else {
				setBounds(new Rectangle(330, 180));
				Utility.centerOnScreen(this);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Save current state of the dialog.
	 */
	private void saveDialog() {
		try {
			
			// Save current dialog window boundaries.
			bounds = getBounds();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}


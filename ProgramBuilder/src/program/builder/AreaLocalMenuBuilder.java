/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package program.builder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JCheckBox;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.maclan.Area;
import org.maclan.VersionObj;
import org.multipage.generator.AreaLocalMenu;
import org.multipage.generator.GeneratorMainFrame;
import org.multipage.generator.SelectVersionDialog;
import org.multipage.gui.Images;
import org.multipage.gui.Utility;
import org.multipage.util.Obj;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Class for local popup menu.
 * @author vakol
 *
 */
public class AreaLocalMenuBuilder extends AreaLocalMenu {

	/**
	 * Constructor.
	 * @param callbacks
	 */
	public AreaLocalMenuBuilder(AreaLocalMenu.Callbacks callbacks) {
		super(callbacks);
		
	}

	/**
	 * Constructor.
	 * @param callbacks
	 * @param purpose
	 */
	public AreaLocalMenuBuilder(AreaLocalMenu.Callbacks callbacks, int purpose) {
		super(callbacks, purpose);
	}

	/**
	 * Edit text resource.
	 * @param inherits 
	 */
	protected void editTextResource(boolean inherits) {
		try {
			
			// Get selected areas.
			Area area = getAreaInformUser();
			if (area == null) {
				return;
			}
			
			// Edit start resource.
			ProgramBuilder.editTextResource(area, inherits);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Edit start resource.
	 * @param inherits 
	 */
	protected void editStartResource(boolean inherits) {
		try {
			
			// Get selected areas.
			Area area = getAreaInformUser();
			if (area == null) {
				return;
			}
			
			// Edit start resource.
			GeneratorMainFrame.editStartResource(area, inherits);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Insert edit trayMenu items.
	 * @param index
	 * @return
	 */
	@Override
	protected int insertEditResourceMenuItems(JPopupMenu popupMenu, int index) {
		
		try {
			final JCheckBox checkInheritResource = new JCheckBox(
					Resources.getString("builder.textInheritResource"));
			checkInheritResource.setSelected(true);
			checkInheritResource.setIconTextGap(15);
			
			
			JMenu editMenu = new JMenu(Resources.getString("builder.menuTextResource"));
			
			JMenuItem menuEditStartResource = new JMenuItem(
					Resources.getString("builder.menuEditAreaResource"));
			menuEditStartResource.setIcon(Images.getIcon("org/multipage/generator/images/edit_text.png"));
			
			JMenuItem menuOpenTextResource = new JMenuItem(
					Resources.getString("builder.menuOpenTextResource"));
			menuOpenTextResource.setIcon(Images.getIcon("org/multipage/generator/images/edit_resource.png"));
			
			menuEditStartResource.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					editStartResource(checkInheritResource.isSelected());
				}
			});
			
			menuOpenTextResource.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					editTextResource(checkInheritResource.isSelected());
				}
			});
			
			popupMenu.insert(editMenu, index++);
			
			editMenu.add(menuEditStartResource);
			editMenu.add(menuOpenTextResource);
			editMenu.addSeparator();
			editMenu.add(checkInheritResource);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return index;
	}

	/**
	 * Insert focus trayMenu items.
	 */
	@Override
	protected int insertFocusMenuItems(JMenu focusMenu, int index) {
		
		try {
			JMenuItem menuFocusStartArea = new JMenuItem(
					Resources.getString("builder.menuFocusStartArea"));
			
			// TODO: <---REFACTOR Change icon.
			menuFocusStartArea.setIcon(Images.getIcon("org/multipage/generator/images/start_resource.png"));
			
			menuFocusStartArea.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					focusStartArea();
				}
			});
			
			focusMenu.insert(menuFocusStartArea, index++);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return index;
	}

	/**
	 * Focus start area.
	 */
	protected void focusStartArea() {
		try {
			
			// Get selected areas.
			Area area = getAreaInformUser();
			if (area == null) {
				return;
			}
			
			// Select version.
			Obj<VersionObj> version = new Obj<VersionObj>();
			if (!SelectVersionDialog.showDialog(null, version)) {
				return;
			}
			
			// Focus start area.
			BuilderMainFrame.getFrame().getVisibleAreasEditor().focusStartArea(area.getId(), version.ref.getId());
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/* (non-Javadoc)
	 * @see org.multipage.generator.AreaLocalMenu#insertEditAreaMenu(javax.swing.JMenu)
	 */
	@Override
	protected void insertEditAreaMenu(JMenu menuEditArea) {
		try {
			
			JMenuItem menuAreaEdit = new JMenuItem(Resources.getString("org.multipage.generator.menuAreaEdit"));
			menuAreaEdit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onEditArea(AreaEditorFrameBuilder.NOT_SPECIFIED);
				}
			});
			menuEditArea.add(menuAreaEdit);
			menuEditArea.addSeparator();
			
			JMenuItem menuEditInheritance = new JMenuItem(Resources.getString("builder.menuAreaEditInheritance"));
			menuEditInheritance.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onEditArea(AreaEditorFrameBuilder.INHERITANCE);
				}
			});
			menuEditArea.add(menuEditInheritance);
			
			JMenuItem menuEditResources = new JMenuItem(Resources.getString("org.multipage.generator.menuAreaEditResources"));
			menuEditResources.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onEditArea(AreaEditorFrameBuilder.RESOURCES);
				}
			});
			menuEditArea.add(menuEditResources);
	
			JMenuItem menuEditStartResource = new JMenuItem(Resources.getString("builder.menuAreaEditStartResource"));
			menuEditStartResource.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onEditArea(AreaEditorFrameBuilder.START_RESOURCE);
				}
			});
			menuEditArea.add(menuEditStartResource);
			
			JMenuItem menuEditDependencies = new JMenuItem(Resources.getString("org.multipage.generator.menuAreaEditDependencies"));
			menuEditDependencies.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onEditArea(AreaEditorFrameBuilder.DEPENDENCIES);
				}
			});
			menuEditArea.add(menuEditDependencies);
			
			JMenuItem menuEditConstructors = new JMenuItem(Resources.getString("builder.menuAreaEditConstructors"));
			menuEditConstructors.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onEditArea(AreaEditorFrameBuilder.CONSTRUCTORS);
				}
			});
			menuEditArea.add(menuEditConstructors);
			
			JMenuItem menuEditHelp = new JMenuItem(Resources.getString("builder.menuAreaEditHelp"));
			menuEditHelp.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onEditArea(AreaEditorFrameBuilder.HELP);
				}
			});
			menuEditArea.add(menuEditHelp);
			
			menuEditArea.addSeparator();
			
			JMenuItem menuSetFlags = new JMenuItem(Resources.getString("builder.menuAreaSetFlags"));
			menuSetFlags.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onSetAreaFlags();
				}
			});
			menuEditArea.add(menuSetFlags);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * On settings this area and the sub areas flags.
	 */
	protected void onSetAreaFlags() {
		try {
			
			// Get a reference to the main application window.
			GeneratorMainFrame frame = GeneratorMainFrame.getFrame();
			
			// Get selected areas.
			LinkedList<Area> selectedAreas = frame.getSelectedAreas();
			if (selectedAreas.isEmpty()) {
				Utility.show(frame, "org.multipage.generator.textAreaCursorToolDescription");
				return;
			}
			
			// Open dialog that enables setting of the selected areas' flags.
			boolean success = AreasFlagsDialog.showDialog(frame, selectedAreas);
			if (success) {
				Utility.show(frame, "builder.textAreaFlagsSuccessfulySet");
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}

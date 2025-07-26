/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package program.builder;

import java.util.LinkedList;

import org.maclan.Slot;
import org.maclan.SlotHolder;
import org.multipage.generator.FoundSlot;
import org.multipage.generator.SlotsTableModel;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Table model for slots table.
 * @author vakol
 *
 */
public class SlotsTableModelBuilder extends SlotsTableModel {

	/**
	 * Version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 * @param holders
	 * @param foundSlots
	 * @param showOnlyFound
	 */
	public SlotsTableModelBuilder(LinkedList<? extends SlotHolder> holders,
			LinkedList<FoundSlot> foundSlots, boolean showOnlyFound, boolean showAllSlots) {
		
		super(holders, foundSlots, showOnlyFound, showAllSlots);
	}

	/* (non-Javadoc)
	 * @see org.multipage.generator.SlotsTableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		
		return 6;
	}

	/**
	 * Return value.
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		
		try {
			if (rowIndex < 0 || rowIndex >= slots.size()) {
				return null;
			}
			
			Slot slot = slots.get(rowIndex);
			if (slot == null) {
				return null;
			}
			
			switch (columnIndex) {
			
			case 0:
				return slot.getAccess();
			case 1:
				return slot.getAlias();
			case 2:
				if (!slot.isDefault()) {
					
					String specialValue = slot.getSpecialValueNull();
					if (specialValue != null) {
						return specialValue;
					}
					return slot.getTextValueDecorated();
				}
				return "\uFFFFdefault";
			case 3:
				return slot.getTypeUseValueMeaning().toString();
			case 4:
				if (holders.size() == 1) {
					return Resources.getString("org.multipage.generator.textThis");
				}
				return slot.getHolder().toString();
			case 5:
				return slot.getName();
			case 6:
				return slot;
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int column) {
		
		try {
			String columnTextId;
			
			switch (column) {
			
			case 0:
				columnTextId = "org.multipage.generator.textSlotAccess";
				break;
			case 1:
				columnTextId = "org.multipage.generator.textSlotAlias";
				break;
			case 2:
				columnTextId = "org.multipage.generator.textSlotValue";
				break;
			case 3:
				columnTextId = "builder.textSlotValueType";
				break;
			case 4:
				columnTextId = "org.multipage.generator.textSlotHolder";
				break;
			case 5:
				columnTextId = "builder.textSlotName";
				break;
			default:
				return "";
			}
			
			String columnText = Resources.getString(columnTextId);
			return columnText;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
	}
}

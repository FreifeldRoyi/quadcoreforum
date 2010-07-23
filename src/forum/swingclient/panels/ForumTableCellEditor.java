package forum.swingclient.panels;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JTable;


public class ForumTableCellEditor extends DefaultCellEditor implements ItemListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -434171644116264053L;
	private JRadioButton btn_selected;

	public ForumTableCellEditor(JCheckBox checkBox) {
		super(checkBox);
	}

	
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		if (value == null)
			return null;
		btn_selected = (JRadioButton) value;
		btn_selected.addItemListener(this);
		if (isSelected)
			btn_selected.setSelected(true);
		return (Component) value;
	}

	public Object getCellEditorValue() {
		btn_selected.removeItemListener(this);
		return btn_selected;
	}

	public void itemStateChanged(ItemEvent e) {
		super.fireEditingStopped();
	}
}

package forum.swingclient.panels;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.*;

public class ForumTableCellRenderer extends JTextPane implements TableCellRenderer {

	private static final long serialVersionUID = 6501807970779590686L;

	private static final Color EVEN_ROWS_COLOR = new Color(238, 244, 252);


	public ForumTableCellRenderer(int alignment) {
		this.setOpaque(true);
		// Sets the currently installed kit for handling content to be Y axis centered kit
		this.setEditorKit(new YAxisCenteredEditorKit());
		// Creates an attributes set and sets its horizontal alignment to be the given one 
		SimpleAttributeSet tAttrs = new SimpleAttributeSet();
		StyleConstants.setAlignment(tAttrs, alignment);
		// Fetches the model associated with the editor
		StyledDocument tStyledDoc=(StyledDocument)this.getDocument();
		// Sets paragraph attributes without changing the previous attributes
		tStyledDoc.setParagraphAttributes(0, tStyledDoc.getLength() - 1, tAttrs, false);
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		if (isSelected) {
			setForeground(table.getSelectionForeground());
			setBackground(new Color (195, 215, 245));
		}
		else {
			setForeground(table.getForeground());
			if (row % 2 == 0) {
				setBackground(EVEN_ROWS_COLOR);
			}
			else
				setBackground(Color.WHITE);
		}
		setFont(table.getFont());
		setText((value == null) ? "" : value.toString());
		return this;
	}

	/**
	 * This class is an abstract factory class which is used in order to return a new styled view factory
	 * which implements Y axis centralized display.
	 */
	private class YAxisCenteredEditorKit extends StyledEditorKit {
		private static final long serialVersionUID = 263249279308272267L;

		public ViewFactory getViewFactory() {
			return new StyledViewFactory();
		}
	}

	/**
	 * This is an implementation of a Y axis centralized display.
	 */
	private class StyledViewFactory implements ViewFactory {
		public View create(Element elem) {
			String kind = elem.getName();
			if (kind != null) {
				if (kind.equals(AbstractDocument.ContentElementName))
					return new LabelView(elem);
				else if (kind.equals(AbstractDocument.ParagraphElementName))
					return new ParagraphView(elem);
				else if (kind.equals(AbstractDocument.SectionElementName))
					return new CenteredBoxView(elem, View.Y_AXIS);
				else if (kind.equals(StyleConstants.ComponentElementName))
					return new ComponentView(elem);
				else if (kind.equals(StyleConstants.IconElementName))
					return new IconView(elem);
				return new LabelView(elem);
			}
			return null;
		}
	}

	/**
	 * Centralized box view display.
	 */
	private class CenteredBoxView extends BoxView {

		public CenteredBoxView(Element elem, int axis) {
			super(elem, axis);
		}

		protected void layoutMajorAxis(int targetSpan, int axis, int[] offsets, int[] spans) {
			super.layoutMajorAxis(targetSpan,axis,offsets,spans);
			int textBlockHeight = 0;
			int offset = 0;
			for (int i = 0; i < spans.length; i++) {
				textBlockHeight += spans[i];
			}
			offset = (targetSpan - textBlockHeight) / 2;
			for (int i = 0; i < offsets.length; i++) {
				offsets[i] += offset;
			}
		}
	}
}
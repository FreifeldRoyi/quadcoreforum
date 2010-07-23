package forum.server.learning;


import javax.swing.*;

import java.awt.event.*;
import java.awt.*;

public class MinFrame {
	public static void main(String[] args) {
		JDialog f = new JDialog();
		//JPanel p = new JPanel(new BorderLayout());
		//p.setBorder(BorderFactory.createEmptyBorder(3,3,0,3));
		f.add(new MyComp(f), BorderLayout.SOUTH);
		//f.setContentPane(p);
		f.setMinimumSize(new Dimension(200, 200));
		f.setResizable(false);
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		f.pack();
		f.setVisible(true);
		
		JDialog C = new JDialog();
		C.setMinimumSize(new Dimension(200, 200));
		C.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		C.pack();
		//C.setVisible(true);
		/*
		JFrame T = new JFrame();
		T.setMinimumSize(new Dimension(200, 200));

		T.setLayout(new GridBagLayout());
		T.setMaximumSize(new Dimension(250, 200));
		T.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		T.setVisible(true);
	*/	
	}
}

class MyComp extends JComponent implements MouseMotionListener {
	JDialog f;
	public MyComp(final JDialog f) {
		setOpaque(false);
		setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
		setPreferredSize(new Dimension(3, 3));
		addMouseMotionListener(this);
		this.f = f;
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		/*System.out.println("ppp");
		System.out.println(f.getY());
		System.out.println(e.getY());
		if (e.getX() == f.getHeight() - getHeight()) {
			f.setResizable(false);
			System.out.println("dddd");
		}*/
	//	else
	//		f.setResizable(true);
	}
	public void mouseDragged(MouseEvent e) {
		System.out.println("res " + e.getXOnScreen());
		//f.setResizable(true);
		f.setSize( f.getWidth(), f.getHeight() + e.getY() - 3);
		f.validate();
	}
} 
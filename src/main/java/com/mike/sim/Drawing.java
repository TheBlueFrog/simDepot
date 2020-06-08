package com.mike.sim;

import com.mike.util.Location;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

//derived from Oracle's Java tutorials and StackOverflow

public class Drawing extends JPanel {

    private double scale = 1.0;
    JFrame mFrame;

    public Drawing (double pixPerCell) {

        super ();

        scale = pixPerCell;

        //Create and set up the drawing area
        mFrame = new JFrame("Drawing");
        mFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        setOpaque(true); //content panes must be opaque
        mFrame.setContentPane(this);

        //Display the window, make large enough to hold grid
        mFrame.setSize(new Dimension((int) Location.MapWidth, (int) Location.MapHeight));
//	        frame.pack();
        mFrame.setLocation(2500, 10);
        mFrame.setVisible(true);

        // Listen for mouse clicks
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                final Point pos = e.getPoint();
                final int x = pos.x;
                final int y = pos.y;
//                Main.simulation.onClick(new WorldLocation(x / mPixelsPerCellX, y / mPixelsPerCellY));
//                Main.drawing.mFrame.repaint();
            }

            @Override
            public void mouseEntered(MouseEvent arg0) {
            }
            @Override
            public void mouseExited(MouseEvent arg0) {
            }
            @Override
            public void mousePressed(MouseEvent arg0) {
            }
            @Override
            public void mouseReleased(MouseEvent arg0) {
            }
        });
    }

    private int where = 0;

    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Main.paint (g2);
    }
}

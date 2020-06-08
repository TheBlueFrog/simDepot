package com.mike.sim;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.*;

/**
 * Created by mike on 2/14/2016.
 */
public class Controls extends JPanel implements ActionListener {
        protected JButton runButton, stepButton, pauseButton;
        private JFrame mFrame;

        boolean running = false;

        public Controls() {

            // Create and set up the controls
            runButton = new JButton("RUN");
            runButton.setVerticalTextPosition(AbstractButton.CENTER);
            runButton.setHorizontalTextPosition(AbstractButton.LEADING); // aka LEFT, for
            // left-to-right
            // locales
            runButton.setMnemonic(KeyEvent.VK_D);
            runButton.setActionCommand("run");
            runButton.setEnabled( ! running);

            stepButton = new JButton("STEP");
            stepButton.setVerticalTextPosition(AbstractButton.BOTTOM);
            stepButton.setHorizontalTextPosition(AbstractButton.CENTER);
            stepButton.setMnemonic(KeyEvent.VK_M);
            stepButton.setActionCommand("step");
            stepButton.setEnabled( ! running);

            pauseButton = new JButton("PAUSE");
            // Use the default text position of CENTER, TRAILING (RIGHT).
            pauseButton.setMnemonic(KeyEvent.VK_E);
            pauseButton.setActionCommand("pause");
            pauseButton.setEnabled(running);

            // Listen for actions on the buttons
            runButton.addActionListener(this);
            stepButton.addActionListener(this);
            pauseButton.addActionListener(this);

            runButton.setToolTipText("Click this button to disable the middle button.");
            stepButton.setToolTipText("This middle button repaints");
            pauseButton.setToolTipText("Click this button to enable the middle button.");

            // Add Components to this container, using the default FlowLayout.
            add(runButton);
            add(stepButton);
            add(pauseButton);

            mFrame = new JFrame("Controls");
            mFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			mFrame.setLocation(2200, 10);

            setOpaque(true); // content panes must be opaque
            mFrame.setContentPane(this);

            // Display the window.
            mFrame.pack();
            mFrame.setVisible(true);

            int delay = 100; // milliseconds
            ActionListener taskPerformer = new ActionListener() {
                public void actionPerformed(ActionEvent evt) {

                    if (running) {

//                        Main.simulation.step();
                        Main.drawing.mFrame.repaint();
                    }
                }
            };
//            new Timer(delay, taskPerformer).start();
        }

        public void actionPerformed(ActionEvent e) {
            if ("pause".equals(e.getActionCommand())) {
                runButton.setEnabled(true);
                stepButton.setEnabled(true);
                pauseButton.setEnabled(false);
                running = false;
            } else if ("run".equals(e.getActionCommand())) {
                runButton.setEnabled(false);
                stepButton.setEnabled(false);
                pauseButton.setEnabled(true);
                running = true;
            }
            else if ("step".equals(e.getActionCommand())) {
//                Main.simulation.step();
                Main.drawing.mFrame.repaint();
            }
        }

    }

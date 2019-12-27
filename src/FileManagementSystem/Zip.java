package FileManagementSystem;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

public class Zip {
    public JFrame zipFrame;
    public JPanel zipPanel;
    public JProgressBar jProgressBar;
    public JButton cancelButton;
    public String threadId;

    public Zip() {
        zipFrame = new JFrame("Copy Files");

        zipPanel = new JPanel();
        zipPanel.setLayout(new BoxLayout(zipPanel, BoxLayout.Y_AXIS));

        jProgressBar = new JProgressBar();
        jProgressBar.setStringPainted(true);

        cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(50, 50));
        cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        cancelButton.addActionListener(e -> {
            Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
            for(Thread thread : threadSet){
                if(thread.getName().equals(threadId)){
                    if (!thread.isInterrupted()) {
                        thread.interrupt();
                    }
                }
            }
            zipFrame.setVisible(false);
            zipFrame.dispose();
        });

        zipPanel.add(jProgressBar);
        zipPanel.add(Box.createRigidArea(new Dimension(50, 5)));
        zipPanel.add(cancelButton);
        zipPanel.setPreferredSize(new Dimension(500, 50));
        zipFrame.getContentPane().add(zipPanel);

        zipFrame.pack();
        zipFrame.setLocationRelativeTo(null);
        zipFrame.setVisible(true);
    }
}


package FileManagementSystem;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

public class Copy {
    public JFrame copyFrame;
    public JPanel copyPanel;
    public JProgressBar jProgressBar;
    public JButton cancelButton;
    public String threadId;

    public Copy() {
        copyFrame = new JFrame("Copy Files");

        copyPanel = new JPanel();
        copyPanel.setLayout(new BoxLayout(copyPanel, BoxLayout.Y_AXIS));

        jProgressBar = new JProgressBar(0, 100);
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
            copyFrame.setVisible(false);
            copyFrame.dispose();
        });

        copyPanel.add(jProgressBar);
        copyPanel.add(Box.createRigidArea(new Dimension(50, 5)));
        copyPanel.add(cancelButton);
        copyPanel.setPreferredSize(new Dimension(500, 50));
        copyFrame.getContentPane().add(copyPanel);

        copyFrame.pack();
        copyFrame.setLocationRelativeTo(null);
        copyFrame.setVisible(true);
    }
}

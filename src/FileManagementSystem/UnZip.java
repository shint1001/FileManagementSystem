package FileManagementSystem;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

public class UnZip {
    public JFrame UnzipFrame;
    public JPanel UnzipPanel;
    public JProgressBar jProgressBar;
    public JButton cancelButton;
    public String threadId;

    public UnZip() {
        UnzipFrame = new JFrame("Unzip Files");

        UnzipPanel = new JPanel();
        UnzipPanel.setLayout(new BoxLayout(UnzipPanel, BoxLayout.Y_AXIS));

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
            UnzipFrame.setVisible(false);
            UnzipFrame.dispose();
        });

        UnzipPanel.add(jProgressBar);
        UnzipPanel.add(Box.createRigidArea(new Dimension(50, 5)));
        UnzipPanel.add(cancelButton);
        UnzipPanel.setPreferredSize(new Dimension(500, 50));
        UnzipFrame.getContentPane().add(UnzipPanel);

        UnzipFrame.pack();
        UnzipFrame.setLocationRelativeTo(null);
        UnzipFrame.setVisible(true);
    }
}


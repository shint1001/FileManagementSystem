package FileManagementSystem;

import javax.swing.*;
import java.io.*;
import java.util.List;

public class CopyThread extends Thread {
    private JProgressBar progressBar;
    public double progress;
    private List<File> srcFiles;
    private String path;
    private Copy copy;

    public CopyThread(JProgressBar progressBar, List<File> srcFiles, String path, Copy copy) {
        this.progressBar = progressBar;
        this.srcFiles = srcFiles;
        this.path = path;
        this.copy = copy;
    }

    public void run() {
        try {
            FileInputStream is;
            long total = 0;
            long size = 0;
            for (var srcFile : srcFiles) {
                size += srcFile.length();
            }
            byte[] buffer = new byte[1024];

            for (var srcFile : srcFiles) {
                if (!srcFile.isDirectory()) {
                    is = new FileInputStream(srcFile);
                    File desFile = new File(path + "/" + srcFile.getName());
                    if (desFile.exists())
                        desFile = new File(CheckPostFix(path + "/" + srcFile.getName()));
                    FileOutputStream os = new FileOutputStream(desFile);
                    int length;
                    while ((length = is.read(buffer)) > 0 && !Thread.currentThread().isInterrupted()) {
                        os.write(buffer, 0, length);
                        total += length;
                        progress = total / (double) size * 100;
                        SwingUtilities.invokeLater(() -> progressBar.setValue((int) progress));
                    }
                    is.close();
                    os.close();
                }
            }
            sleep(1000);
            copy.copyFrame.setVisible(false);
        }catch (IOException e){
                e.printStackTrace();
            }
        catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
    }
    private String CheckPostFix(String s) {
        File f = new File(s);
        String fileNameWithoutExt = s.substring(0, s.lastIndexOf("."));
        String extName = s.substring(s.lastIndexOf("."));
        String newFileName = "";
        int num = 0;
        if (f.exists() && !f.isDirectory()) {
            while (f.exists()) {
                num++;
                newFileName = fileNameWithoutExt + "(" + num + ")" + extName;
                f = new File(newFileName);
            }
        }
        return newFileName;
    }
}

package FileManagementSystem;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipThread extends Thread {
    private JProgressBar progressBar;
    public double progress;
    private List<String> srcFiles;
    private String desFile;

    public ZipThread(JProgressBar progressBar, List<String> srcFiles, String desFile) {
        this.progressBar = progressBar;
        this.srcFiles = srcFiles;
        this.desFile = desFile;
    }

    public void run() {
        try {
            FileOutputStream os = new FileOutputStream(desFile);
            ZipOutputStream zos = new ZipOutputStream(os);
            long total = 0;
            long size = 0;
            for(var srcFile : srcFiles){
                size += srcFile.length();
            }
            byte[] buffer = new byte[1024];

            for (int i = 0; i < srcFiles.size(); i++) {
                File srcFile = new File(srcFiles.get(i));
                if(!srcFile.isDirectory()) {
                    FileInputStream is = new FileInputStream(srcFile);
                    zos.putNextEntry(new ZipEntry(srcFile.getName()));
                    int length;
                    while ((length = is.read(buffer)) > 0 && !Thread.currentThread().isInterrupted()) {
                        zos.write(buffer, 0, length);
                        total += length;
                        progress = (total / (double)size) * 100;
                        SwingUtilities.invokeLater(() -> progressBar.setValue((int) progress));
                    }
                    zos.closeEntry();
                    is.close();
                }
            }
            zos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

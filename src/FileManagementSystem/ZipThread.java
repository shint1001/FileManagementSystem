package FileManagementSystem;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipThread extends Thread {
    private JProgressBar progressBar;
    public double progress;
    private List<File> srcFiles;
    private String desFile;
    private Zip zip;

    public ZipThread(JProgressBar progressBar, List<File> srcFiles, String desFile, Zip zip) {
        this.progressBar = progressBar;
        this.srcFiles = srcFiles;
        this.desFile = desFile;
        this.zip = zip;
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

            for(var srcFile : srcFiles) {
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
            sleep(1000);
            zip.zipFrame.setVisible(false);
        } catch (IOException e){
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

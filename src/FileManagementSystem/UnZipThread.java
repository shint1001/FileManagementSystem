package FileManagementSystem;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class UnZipThread extends Thread {
    private JProgressBar progressBar;
    public double progress;
    private  File srcFile;
    private String desFile;
    private UnZip unzip;

    public UnZipThread(JProgressBar progressBar, File srcFile, String desFile, UnZip unzip) {
        this.progressBar = progressBar;
        this.srcFile = srcFile;
        this.desFile = desFile;
        this.unzip = unzip;
    }

    public void run() {
        FileInputStream is;

        long total = 0;
        long size = srcFile.length();
        byte[] buffer = new byte[1024];
        try {
            is = new FileInputStream(srcFile);
            ZipInputStream zis = new ZipInputStream(is);
            ZipEntry ze = zis.getNextEntry();
            while(ze != null) {
                File newFile = new File(desFile + "/" + ze.getName());
                new File(newFile.getParent()).mkdirs();
                FileOutputStream os = new FileOutputStream(newFile);
                int length;
                while((length = zis.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                    total += length;
                    progress = total/(double)size * 100;
                    SwingUtilities.invokeLater(() -> progressBar.setValue((int) progress));
                }
                os.close();
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
            is.close();
            sleep(1000);
            unzip.UnzipFrame.setVisible(false);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

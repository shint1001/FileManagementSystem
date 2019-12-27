package FileManagementSystem;

import javax.swing.*;
import java.io.*;
import java.util.List;

public class CopyThread extends Thread {
    private JProgressBar progressBar;
    public double progress;
    private List<String> srcFiles;
    private String path;

    public CopyThread(JProgressBar progressBar, List<String> srcFiles, String path) {
        this.progressBar = progressBar;
        this.srcFiles = srcFiles;
        this.path = path;
    }

    public void run() {
        try {
            FileInputStream is;
            double total = 0;
            double size = 0;
            for(var srcFile : srcFiles){
                size += srcFile.length();
            }
            byte[] buffer = new byte[1024];

            for (int i = 0; i < srcFiles.size(); i++) {
                File srcFile = new File(srcFiles.get(i));
                if(!srcFile.isDirectory()) {
                    is = new FileInputStream(srcFile);
                    File desFile = new File(path + "/" + srcFile.getName());
                    if(desFile.exists())
                        desFile = new File(CheckPostFix(path + "/" + srcFile.getName()));
                    FileOutputStream os = new FileOutputStream(desFile);
                    int length;
                    while ((length = is.read(buffer)) > 0 && !Thread.currentThread().isInterrupted()) {
                        os.write(buffer, 0, length);
                        total += length;
                        progress = total /size * 100;
                        SwingUtilities.invokeLater(() -> progressBar.setValue((int) progress));
                    }
                    is.close();
                    os.close();
                }
            }
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
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

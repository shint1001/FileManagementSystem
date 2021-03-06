package FileManagementSystem;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Array;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.*;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class GUI extends JFrame {
    static Desktop desktop;
    static JPanel mainPanel = new JPanel();
    static JPanel body = new JPanel();
    static JTextField addr = new JTextField();
    static FileSystemView fileSystemView;
    static JScrollPane itemPanel = new JScrollPane();
    static JSplitPane splitPane = new JSplitPane();
    static File curFile;
    static JTable table;
    static FileTableModel fileTableModel;
    static ListSelectionListener listSelectionListener;
    static JTree direcTree = new JTree();

    void setModel() {
        desktop = Desktop.getDesktop();
        fileSystemView = FileSystemView.getFileSystemView();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("/");

        table = new JTable();
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.setShowVerticalLines(false);

        listSelectionListener = lse -> {
            int row = table.getSelectionModel().getLeadSelectionIndex();
            setAddrPanel(((FileTableModel) table.getModel()).getFile(row));
        };
        table.getSelectionModel().addListSelectionListener(listSelectionListener);

        TreeSelectionListener treeSelectionListener = tse -> {
            File node =
                    (File)tse.getPath().getLastPathComponent();
            showChildren(new DefaultMutableTreeNode(node));
            setAddrPanel(node);
            //viewFileContent(node);
        };

        File[] roots = fileSystemView.getRoots();
        for (File fileSystemRoot : roots) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileSystemRoot);
            root.add( node );

            File[] files = fileSystemView.getFiles(fileSystemRoot, true);
            for (File file : files) {
                if (file.isDirectory()) {
                    node.add(new DefaultMutableTreeNode(file));
                }
            }
        }

        direcTree.setModel(new FilesContentProvider(root.toString()));
        direcTree.setCellRenderer(new ItemTreeCellRenderer());
        direcTree.addTreeSelectionListener(treeSelectionListener);
        direcTree.setRootVisible(false);
        direcTree.expandRow(0);
    }

    GUI() {
        super();
        setTitle("File Management System");
        setDefaultLookAndFeelDecorated(true);
        setContentPane(mainPanel);
        setPreferredSize(new Dimension(760, 520));

        mainPanel.setVisible(true);
        mainPanel.setBorder(new EmptyBorder(5,5,5,5));

        JMenuBar mb = menuBar();

        setModel();

        itemPanel = new JScrollPane(table);
        itemPanel.setPreferredSize(new Dimension(520, 400));

        JScrollPane listItem = new JScrollPane(direcTree);
        listItem.setPreferredSize(new Dimension(200, 400));


        splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                listItem,
                itemPanel);

        body.add(splitPane);

        setJMenuBar(mb);
        JPanel addrPanel = addressPanel();
        addrPanel.setPreferredSize(new Dimension(750, 30));
        mainPanel.add(addrPanel, BorderLayout.NORTH);
        mainPanel.add(body, BorderLayout.CENTER);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        direcTree.setSelectionInterval(0, 0);
    }

    JMenuBar menuBar() {
        JMenuBar mb = new JMenuBar();
        mb.setBorderPainted(true);

        JMenu file = new JMenu("File");
        JMenuItem createFile = new JMenuItem("Create File");
        createFile.addActionListener((e) -> {
            createFileClicked();
        });
        JMenuItem deleteFile = new JMenuItem("Delete File");
        deleteFile.addActionListener((e) -> {
            deleteFileClicked();
        });
        JMenuItem renameFile = new JMenuItem("Rename File");
        renameFile.addActionListener((e) -> {
            renameFileClicked();
        });
        JMenuItem copyFile = new JMenuItem("Copy File");
        copyFile.addActionListener((e) -> {
            copyFileClicked();
        });
        JMenuItem zipFile = new JMenuItem("Zip File");
        zipFile.addActionListener((e) -> {
            zipFileClicked();
        });
        JMenuItem unzipFile = new JMenuItem("Unzip File");
        unzipFile.addActionListener((e) -> {
            unzipFileClicked();
        });

        file.add(createFile);
        file.add(deleteFile);
        file.add(renameFile);
        file.add(copyFile);
        file.add(zipFile);
        file.add(unzipFile);

        JMenu folder = new JMenu("Folder");
        JMenuItem createFolder = new JMenuItem("Create Folder");
        createFolder.addActionListener((e) -> {
            createFolderClicked();
        });
        JMenuItem deleteFolder = new JMenuItem("Delete Folder");
        deleteFolder.addActionListener((e) -> {
            deleteFolderClicked();
        });
        JMenuItem renameFolder = new JMenuItem("Rename Folder");
        renameFolder.addActionListener((e) -> {
            renameFolderClicked();
        });

        folder.add(createFolder);
        folder.add(deleteFolder);
        folder.add(renameFolder);

        mb.add(file);
        mb.add(folder);
        return mb;
    }

    private void unzipFileClicked() {
        JFrame unzipFileFrame = new JFrame("UnZip File");
        JPanel unzipFilePanel = new JPanel();

        JFileChooser chooser = new JFileChooser("/");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        JTextField dest = new JTextField();
        if(chooser.showOpenDialog(unzipFileFrame) == JFileChooser.APPROVE_OPTION)
            dest.setText(chooser.getSelectedFile().getAbsolutePath());

        JButton submitButton = new JButton("Submit");
        JButton cancelButton = new JButton("Cancel");

        unzipFilePanel.add(new JLabel("Destination: "));
        unzipFilePanel.add(dest);
        unzipFilePanel.add(submitButton);
        unzipFilePanel.add(cancelButton);
        unzipFilePanel.setLayout(new GridLayout(2, 2));
        unzipFilePanel.setPreferredSize(new Dimension(500, 50));

        unzipFileFrame.setContentPane(unzipFilePanel);

        submitButton.addActionListener((e) -> {
            String Root = direcTree.getModel().getRoot().toString();
            TreePath[] paths = direcTree.getSelectionPaths();
            for (TreePath path : paths) {
                String filePath = path.getLastPathComponent().toString();

                UnZip unzip = new UnZip();
                unzip.UnzipFrame.setVisible(true);
                UnZipThread unzipThread = new UnZipThread(unzip.jProgressBar, new File(filePath), dest.getText(), unzip);
                String threadId = unzipThread.getName();
                unzip.threadId = threadId;
                unzipThread.start();
                System.out.println(unzipThread.progress);
            }

            direcTree.setModel(new FilesContentProvider(Root));

            unzipFileFrame.setVisible(false);
            direcTree.setModel(new FilesContentProvider(""));
            direcTree.setModel(new FilesContentProvider(Root));
        });
        cancelButton.addActionListener((e) -> {
            unzipFileFrame.setVisible(false);
        });

        unzipFileFrame.pack();
        unzipFileFrame.setLocationRelativeTo(null);
        unzipFileFrame.setVisible(true);
    }

    private void renameFolderClicked() {
        JFrame renameFolderFrame = new JFrame("Rename File");
        JPanel renameFolderPanel = new JPanel();

        JTextField newFilePath = new JTextField(direcTree.getLastSelectedPathComponent().toString());
        JButton submitButton = new JButton("Submit");
        JButton cancelButton = new JButton("Cancel");

        renameFolderPanel.add(new JLabel("Path"));
        renameFolderPanel.add(newFilePath);
        renameFolderPanel.add(submitButton);
        renameFolderPanel.add(cancelButton);
        renameFolderPanel.setLayout(new GridLayout(2, 2));
        renameFolderPanel.setPreferredSize(new Dimension(500, 50));

        renameFolderFrame.setContentPane(renameFolderPanel);

        submitButton.addActionListener((e) -> {
            var newFileName = newFilePath.getText();
            File newFile = new File(newFileName);
            String Root = direcTree.getModel().getRoot().toString();
            TreePath [] paths = direcTree.getSelectionPaths();
            for(TreePath path : paths) {
                String filePath = path.getLastPathComponent().toString();
                File f = new File(filePath);
                if(!f.renameTo(newFile) && !f.isDirectory()){
                    System.out.println(f);
                    JOptionPane.showMessageDialog(null, f.getAbsolutePath() + "can't be renamed.");
                    return;
                }
                JOptionPane.showMessageDialog(null, f.getAbsolutePath()+ " is renamed.");
            }
            renameFolderFrame.setVisible(false);
            direcTree.setModel(new FilesContentProvider(""));
            direcTree.setModel(new FilesContentProvider(Root));
        });
        cancelButton.addActionListener((e) -> {
            renameFolderFrame.setVisible(false);
        });

        renameFolderFrame.pack();
        renameFolderFrame.setLocationRelativeTo(null);
        renameFolderFrame.setVisible(true);

    }

    private void deleteFolderClicked() {
        String Root = direcTree.getModel().getRoot().toString();
        TreePath [] paths = direcTree.getSelectionPaths();
        for(TreePath path : paths) {
            String filePath = path.getLastPathComponent().toString();
            File f = new File(filePath);
            if(!deleteDirectory(f)){
                JOptionPane.showMessageDialog(null, f.getAbsolutePath() + " can't be deleted.");
                return;
            }
            JOptionPane.showMessageDialog(null, f.getAbsolutePath()+ " is deleted.");
        }
        direcTree.setModel(new FilesContentProvider(""));
        direcTree.setModel(new FilesContentProvider(Root));
    }

    public boolean deleteDirectory(File directory) {
        if(directory.exists()){
            File[] files = directory.listFiles();
            if(null!=files){
                for(int i=0; i<files.length; i++) {
                    if(files[i].isDirectory()) {
                        deleteDirectory(files[i]);
                    }
                    else {
                        files[i].delete();
                    }
                }
            }
        }
        return(directory.delete());
    }

    private void createFolderClicked() {
        JFrame createFolderFrame = new JFrame("Create Folder");
        JPanel createFolderPanel = new JPanel();

        JTextField nameFolder = new JTextField();
        JButton submitButton = new JButton("Submit");
        JButton cancelButton = new JButton("Cancel");

        createFolderPanel.add(new JLabel("Folder Name: "));
        createFolderPanel.add(nameFolder);
        createFolderPanel.add(submitButton);
        createFolderPanel.add(cancelButton);
        createFolderPanel.setLayout(new GridLayout(2, 2));
        createFolderPanel.setPreferredSize(new Dimension(500, 50));

        createFolderFrame.setContentPane(createFolderPanel);

        submitButton.addActionListener((e) -> {
            String Root = direcTree.getModel().getRoot().toString();
            TreePath[] paths = direcTree.getSelectionPaths();
            for (TreePath path : paths) {
                String filePath = path.getLastPathComponent().toString();
                String fileName = nameFolder.getText();

                File f = new File(filePath + "/" + fileName);
                System.out.println(f);
                boolean newFolder = f.mkdirs();
                if (newFolder)
                    JOptionPane.showMessageDialog(createFolderPanel, "Directory created successfully");
                else
                    JOptionPane.showMessageDialog(createFolderPanel, "Couldn’t create specified directory");
                createFolderFrame.setVisible(false);
                direcTree.setModel(new FilesContentProvider(""));
                direcTree.setModel(new FilesContentProvider(Root));
            }
        });
        cancelButton.addActionListener((e) -> {
            createFolderFrame.setVisible(false);
        });

        createFolderFrame.pack();
        createFolderFrame.setLocationRelativeTo(null);
        createFolderFrame.setVisible(true);
    }


    private void copyFileClicked() {
        JFrame copyFileFrame = new JFrame("Copy File");
        JPanel copyFilePanel = new JPanel();

        JFileChooser chooser = new JFileChooser("/");
        JTextField dest = new JTextField();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if(chooser.showOpenDialog(copyFileFrame) == JFileChooser.APPROVE_OPTION)
            dest.setText(chooser.getSelectedFile().getAbsolutePath());

        JButton submitButton = new JButton("Submit");
        JButton cancelButton = new JButton("Cancel");

        copyFilePanel.add(new JLabel("Path"));
        copyFilePanel.add(dest);
        copyFilePanel.add(submitButton);
        copyFilePanel.add(cancelButton);
        copyFilePanel.setLayout(new GridLayout(2, 2));
        copyFilePanel.setPreferredSize(new Dimension(500, 50));

        copyFileFrame.setContentPane(copyFilePanel);

        submitButton.addActionListener((e) -> {
            String Root = direcTree.getModel().getRoot().toString();
            TreePath[] paths = direcTree.getSelectionPaths();
            List<File> copyList = new ArrayList<>();
            for (TreePath path : paths) {
                String filePath = path.getLastPathComponent().toString();
                File f = new File(filePath);
                copyList.add(f);
            }
            Copy copy = new Copy();
            copy.copyFrame.setVisible(true);
            try {
                CopyThread copyThread = new CopyThread(copy.jProgressBar, copyList, dest.getText(), copy);
                String threadId = copyThread.getName();
                copy.threadId = threadId;
                copyThread.start();
                if (copyThread.progress == 100)
                    copy.copyFrame.setVisible(false);

                direcTree.setModel(new FilesContentProvider(Root));

            } catch (Exception ex) {

            }

            copyFileFrame.setVisible(false);
            direcTree.setModel(new FilesContentProvider(""));
            direcTree.setModel(new FilesContentProvider(Root));
        });
        cancelButton.addActionListener((e) -> {
            copyFileFrame.setVisible(false);
        });

        copyFileFrame.pack();
        copyFileFrame.setLocationRelativeTo(null);
        copyFileFrame.setVisible(true);
    }

    private void zipFileClicked() {
        JFrame zipFileFrame = new JFrame("Zip File");
        JPanel zipFilePanel = new JPanel();

        JFileChooser chooser = new JFileChooser("/");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.showOpenDialog(zipFileFrame);
        JTextField dest = new JTextField();
        JButton submitButton = new JButton("Submit");
        JButton cancelButton = new JButton("Cancel");

        zipFilePanel.add(new JLabel("Zip Name: "));
        zipFilePanel.add(dest);
        zipFilePanel.add(submitButton);
        zipFilePanel.add(cancelButton);
        zipFilePanel.setLayout(new GridLayout(2, 2));
        zipFilePanel.setPreferredSize(new Dimension(500, 50));

        zipFileFrame.setContentPane(zipFilePanel);

        submitButton.addActionListener((e) -> {
            String Root = direcTree.getModel().getRoot().toString();
            List<File> zipList = new ArrayList<>();
            TreePath[] paths = direcTree.getSelectionPaths();
            for (TreePath path : paths) {
                String filePath = path.getLastPathComponent().toString();
                File f = new File(filePath);
                zipList.add(f);
            }
            Zip zip = new Zip();
            zip.zipFrame.setVisible(true);
            ZipThread zipThread = new ZipThread(zip.jProgressBar, zipList, chooser.getSelectedFile() + "/" + dest.getText() + ".zip", zip);
            String threadId = zipThread.getName();
            zip.threadId = threadId;
            zipThread.start();
            if (zipThread.progress == 100)
                zip.zipFrame.setVisible(false);

            direcTree.setModel(new FilesContentProvider(Root));

            zipFileFrame.setVisible(false);
            direcTree.setModel(new FilesContentProvider(""));
            direcTree.setModel(new FilesContentProvider(Root));
        });
        cancelButton.addActionListener((e) -> {
            zipFileFrame.setVisible(false);
        });

        zipFileFrame.pack();
        zipFileFrame.setLocationRelativeTo(null);
        zipFileFrame.setVisible(true);
    }

    private void renameFileClicked() {
        JFrame renameFileFrame = new JFrame("Rename File");
        JPanel renameFilePanel = new JPanel();

        JTextField newFilePath = new JTextField(direcTree.getLastSelectedPathComponent().toString());
        JButton submitButton = new JButton("Submit");
        JButton cancelButton = new JButton("Cancel");

        renameFilePanel.add(new JLabel("Path"));
        renameFilePanel.add(newFilePath);
        renameFilePanel.add(submitButton);
        renameFilePanel.add(cancelButton);
        renameFilePanel.setLayout(new GridLayout(2, 2));
        renameFilePanel.setPreferredSize(new Dimension(500, 50));

        renameFileFrame.setContentPane(renameFilePanel);

        submitButton.addActionListener((e) -> {
            var newFileName = newFilePath.getText();
            File newFile = new File(newFileName);
            String Root = direcTree.getModel().getRoot().toString();
            TreePath [] paths = direcTree.getSelectionPaths();
            for(TreePath path : paths) {
                String filePath = path.getLastPathComponent().toString();
                File f = new File(filePath);
                if(!f.renameTo(newFile)){
                    System.out.println(f);
                    JOptionPane.showMessageDialog(null, f.getAbsolutePath() + "can't be renamed.");
                    return;
                }
                JOptionPane.showMessageDialog(null, f.getAbsolutePath()+ " is renamed.");
            }
            renameFileFrame.setVisible(false);
            direcTree.setModel(new FilesContentProvider(""));
            direcTree.setModel(new FilesContentProvider(Root));
        });
        cancelButton.addActionListener((e) -> {
            renameFileFrame.setVisible(false);
        });

        renameFileFrame.pack();
        renameFileFrame.setLocationRelativeTo(null);
        renameFileFrame.setVisible(true);
    }

    private void deleteFileClicked() {
        String Root = direcTree.getModel().getRoot().toString();
        TreePath [] paths = direcTree.getSelectionPaths();
        for(TreePath path : paths) {
            String filePath = path.getLastPathComponent().toString();
            File f = new File(filePath);
            if(!f.delete()){
                JOptionPane.showMessageDialog(null, f.getAbsolutePath() + " can't be deleted.");
                return;
            }
            JOptionPane.showMessageDialog(null, f.getAbsolutePath()+ " is deleted.");
        }
        direcTree.setModel(new FilesContentProvider(""));
        direcTree.setModel(new FilesContentProvider(Root));
        splitPane.repaint();
    }

    private void createFileClicked() {
        JFrame createFileFrame = new JFrame("Create File");
        JPanel createFilePanel = new JPanel();

        JTextField _path = new JTextField();
        JButton submitButton = new JButton("Submit");
        JButton cancelButton = new JButton("Cancel");

        createFilePanel.add(new JLabel("File Name"));
        createFilePanel.add(_path);
        createFilePanel.add(submitButton);
        createFilePanel.add(cancelButton);
        createFilePanel.setLayout(new GridLayout(2, 2));
        createFilePanel.setPreferredSize(new Dimension(500, 50));

        createFileFrame.setContentPane(createFilePanel);

        submitButton.addActionListener((e) -> {
            String Root = direcTree.getModel().getRoot().toString();
            TreePath [] paths = direcTree.getSelectionPaths();
            String fileName = _path.getText();
            for (TreePath path : paths) {
                String filePath = path.getLastPathComponent().toString();
                if (fileName != null) {
                    File newFile = new File(filePath + "/" + fileName);
                    System.out.println(newFile);
                    if (newFile.exists()) {
                        String newFileName = CheckPostFix(filePath + "/" + fileName);

                        File file = new File(newFileName);
                        try {
                            if (file.createNewFile()) {
                                JOptionPane.showMessageDialog(null, "File Created");
                            }
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(null, "File Created Failed");
                        }
                    } else {
                        try {
                            if (newFile.createNewFile()) {
                                JOptionPane.showMessageDialog(null, "File Created");
                            }
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(null, "File Created Failed");
                        }
                    }
                }
            }
            createFileFrame.setVisible(false);
            direcTree.setModel(new FilesContentProvider(""));
            direcTree.setModel(new FilesContentProvider(Root));
        });
        cancelButton.addActionListener((e) -> {
            createFileFrame.setVisible(false);
        });

        createFileFrame.pack();
        createFileFrame.setLocationRelativeTo(null);
        createFileFrame.setVisible(true);
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
                    newFileName = fileNameWithoutExt + " (" + num + ")" + extName;
                    f = new File(newFileName);
                }
            }
            return newFileName;
    }

    JPanel addressPanel() {
        JPanel menu = new JPanel(new BorderLayout());
        JPanel btn = new JPanel();

        JButton prevBtn = new JButton("<");
        prevBtn.addActionListener((e)->{
            prevBtnClick();
        });
        prevBtn.setPreferredSize(new Dimension(30, 20));
        JButton nextBtn = new JButton(">");
        nextBtn.setPreferredSize(new Dimension(30, 20));
        btn.add(prevBtn);
        btn.add(nextBtn);
        JTextField search = new JTextField();
        search.setPreferredSize(new Dimension(200, 20));

        menu.add(btn, BorderLayout.WEST);
        menu.add(addr, BorderLayout.CENTER);
        menu.add(search, BorderLayout.EAST);

        return menu;
    }

    private void prevBtnClick() {
        var paths = direcTree.getSelectionPaths();
        for (TreePath path : paths) {
            String filePath = path.getLastPathComponent().toString();
            File f = new File(filePath);
            String prevRoot = f.getParent();
            direcTree.setModel(new FilesContentProvider(prevRoot));
        }
    }

    void setAddrPanel(File file) {
        itemPanel.setViewportView(table);
        curFile = file;
        addr.setText(file.getPath());
    }

    void viewFileContent(File file) {

        try {
            JTextArea jt = new JTextArea();
            BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            jt.read(input, file);
            itemPanel.setViewportView(jt);


        }catch(Exception e){
        }
    }

    private void setTableData(final File[] files) {
        SwingUtilities.invokeLater(() -> {
            if (fileTableModel == null) {
                fileTableModel = new FileTableModel();
                table.setModel(fileTableModel);
            }
            table.getSelectionModel().removeListSelectionListener(listSelectionListener);
            fileTableModel.setFiles(files);
            table.getSelectionModel().addListSelectionListener(listSelectionListener);
        });
    }

    private void showChildren(final DefaultMutableTreeNode node) {
        SwingWorker<Void, File> worker = new SwingWorker<>() {
            @Override
            public Void doInBackground() {
                File file = (File) node.getUserObject();
                if (file.isDirectory()) {
                    File[] files = fileSystemView.getFiles(file, true); //!!
                    if (node.isLeaf()) {
                        for (File child : files) {
                            if (child.isDirectory()) {
                                publish(child);
                            }
                        }
                    }
                    setTableData(files);
                }
                return null;
            }

            @Override
            protected void process(List<File> chunks) {
                for (File child : chunks) {
                    node.add(new DefaultMutableTreeNode(child));
                }
            }
        };
        worker.execute();
    }

}



package FileManagementSystem;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.*;

public class GUI extends JFrame {
    static JPanel mainPanel = new JPanel();
    static JPanel body = new JPanel();
    static JTextField addr = new JTextField();
    static JPanel lPanel = new JPanel();
    static JPanel rPanel = new JPanel();
    static FileSystemView fileSystemView;
    static File curFile;
    static JTable table;
    static FileTableModel fileTableModel;
    static ListSelectionListener listSelectionListener;
    static JTree direcTree = new JTree();

    void setModel() {
        fileSystemView = FileSystemView.getFileSystemView();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("/");

        table = new JTable();
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.setShowVerticalLines(false);

        listSelectionListener = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                int row = table.getSelectionModel().getLeadSelectionIndex();
                setAddrPanel(((FileTableModel) table.getModel()).getFile(row));
            }
        };

        TreeSelectionListener treeSelectionListener = new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent tse){
                DefaultMutableTreeNode node =
                        (DefaultMutableTreeNode)tse.getPath().getLastPathComponent();
                showChildren(node);
                setAddrPanel((File)node.getUserObject());
            }
        };

        // hien thi he thong tap tin goc.
        File[] roots = fileSystemView.getRoots();
        for (File fileSystemRoot : roots) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileSystemRoot);
            root.add( node );
            //showChildren(node);
            //
            File[] files = fileSystemView.getFiles(fileSystemRoot, true);
            for (File file : files) {
                if (file.isDirectory()) {
                    node.add(new DefaultMutableTreeNode(file));
                }
            }
            //
        }

        table.getSelectionModel().addListSelectionListener(listSelectionListener);
        direcTree.setModel(new FilesContentProvider(root.toString()));
        direcTree.setRootVisible(false);
        direcTree.expandRow(0);
    }

    GUI() {
        super();
        setTitle("File Management System");
        JFrame.setDefaultLookAndFeelDecorated(true);
        setContentPane(mainPanel);
        setPreferredSize(new Dimension(850, 550));

        mainPanel.setVisible(true);
        mainPanel.setLayout(new BorderLayout());

        JMenuBar mb = menuBar();

        setModel();

        JScrollPane itemPanel = new JScrollPane(table);
        itemPanel.setPreferredSize(new Dimension(400, 400));

        JScrollPane listItem = new JScrollPane(direcTree);
        listItem.setPreferredSize(new Dimension(400, 400));
        lPanel.add(listItem);

        rPanel.add(itemPanel);

        body.add(lPanel, BorderLayout.WEST);
        body.add(rPanel, BorderLayout.CENTER);

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
        JMenuItem zipFile = new JMenuItem("Zip File");
        JMenuItem unzipFile = new JMenuItem("Unzip File");
        JMenuItem viewFileContent = new JMenuItem("View File Content");

        file.add(createFile);
        file.add(deleteFile);
        file.add(renameFile);
        file.add(copyFile);
        file.add(zipFile);
        file.add(unzipFile);
        file.add(viewFileContent);

        JMenu folder = new JMenu("Folder");
        JMenuItem createFolder = new JMenuItem("Create Folder");
        JMenuItem deleteFolder = new JMenuItem("Delete Folder");
        JMenuItem renameFolder = new JMenuItem("Rename Folder");

        folder.add(createFolder);
        folder.add(deleteFolder);
        folder.add(renameFolder);

        mb.add(file);
        mb.add(folder);
        return mb;
    }

    private void renameFileClicked() {
        JFrame renameFileFrame = new JFrame("Rename File");
        JPanel renameFilePanel = new JPanel();

        JTextField path = new JTextField(direcTree.getLastSelectedPathComponent().toString());
        JButton submitButton = new JButton("Submit");
        JButton cancelButton = new JButton("Cancel");

        renameFilePanel.add(new JLabel("Path"));
        renameFilePanel.add(path);
        renameFilePanel.add(submitButton);
        renameFilePanel.add(cancelButton);
        renameFilePanel.setLayout(new GridLayout(2, 2));
        renameFilePanel.setPreferredSize(new Dimension(500, 50));

        renameFileFrame.setContentPane(renameFilePanel);

        submitButton.addActionListener((e) -> {
        });
    }

    private void deleteFileClicked() {
        String Root = direcTree.getModel().getRoot().toString();
        TreePath [] paths = direcTree.getSelectionPaths();
        for(TreePath path : paths) {
            String filePath = path.getLastPathComponent().toString();
            File f = new File(filePath);
            if(!f.delete()){
                JOptionPane.showMessageDialog(null, f.getAbsolutePath() + "can't be deleted.");
                return;
            }
            JOptionPane.showMessageDialog(null, f.getAbsolutePath()+ " is deleted.");
        }
        direcTree.setModel(new FilesContentProvider(""));
        direcTree.setModel(new FilesContentProvider(Root));
    }

    private void createFileClicked() {
        JFrame createFileFrame = new JFrame("Create File");
        JPanel createFilePanel = new JPanel();

        JTextField path = new JTextField();
        JButton submitButton = new JButton("Submit");
        JButton cancelButton = new JButton("Cancel");

        createFilePanel.add(new JLabel("Path"));
        createFilePanel.add(path);
        createFilePanel.add(submitButton);
        createFilePanel.add(cancelButton);
        createFilePanel.setLayout(new GridLayout(2, 2));
        createFilePanel.setPreferredSize(new Dimension(500, 50));

        createFileFrame.setContentPane(createFilePanel);

        submitButton.addActionListener((e) -> {
            String Root = direcTree.getModel().getRoot().toString();
            String currentRoot = direcTree.getLastSelectedPathComponent().toString();
            String fileName = path.getText();
            if (fileName != null) {
                File newFile = new File(currentRoot + "/" + fileName);
                System.out.println(newFile);
                if (newFile.exists()) {
                    String newFileName = createFileWithPostFix(currentRoot + "/" + fileName);

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

    private String createFileWithPostFix(String s) {
            File target = new File(s);
            String fileNameWithoutExt = s.substring(0, s.lastIndexOf("."));
            String extName = s.substring(s.lastIndexOf("."));
            String newFileName = "";
            int fileNO = 0;
            if (target.exists() && !target.isDirectory()) {
                while (target.exists()) {
                    fileNO++;
                    newFileName = fileNameWithoutExt + " (" + fileNO + ")" + extName;
                    target = new File(newFileName);
                }
            }
            return newFileName;
    }

    JPanel addressPanel() {
        JPanel menu = new JPanel(new BorderLayout());
        JPanel btn = new JPanel();

        JButton prevBtn = new JButton("<");
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

    void setAddrPanel(File file) {
        curFile = file;
        addr.setText(file.getPath());
    }

    private void setTableData(final File[] files) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (fileTableModel == null) {
                    fileTableModel = new FileTableModel();
                    table.setModel(fileTableModel);
                }
                table.getSelectionModel().removeListSelectionListener(listSelectionListener);
                fileTableModel.setFiles(files);
                table.getSelectionModel().addListSelectionListener(listSelectionListener);
            }
        });
    }

    private void showChildren(final DefaultMutableTreeNode node) {
        direcTree.setEnabled(false);

        SwingWorker<Void, File> worker = new SwingWorker<Void, File>() {
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
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
    static File curFile;
    static Desktop desktop;
    static FileSystemView view;
    static DefaultTreeModel treeModel;
    static JTable table;
    static FileTableModel fileTableModel;
    static ListSelectionListener listSelectionListener;
    static JTree direcTree = new JTree();

    void setModel() {
        table = new JTable();
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.setShowVerticalLines(false);

        view = FileSystemView.getFileSystemView();

        listSelectionListener = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                int row = table.getSelectionModel().getLeadSelectionIndex();
            }
        };
        table.getSelectionModel().addListSelectionListener(listSelectionListener);
        JScrollPane tableScroll = new JScrollPane(table);
        Dimension d = tableScroll.getPreferredSize();
        tableScroll.setPreferredSize(new Dimension((int) d.getWidth(), (int) d.getHeight() / 2));

        // tao cay thu muc
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        treeModel = new DefaultTreeModel(root);

        TreeSelectionListener treeSelectionListener = new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent tse) {
                DefaultMutableTreeNode node =
                        (DefaultMutableTreeNode) tse.getPath().getLastPathComponent();
                showChildren(node);
            }
        };

        // hien thi he thong tap tin goc.
        File[] roots = view.getRoots();
        for (File fileSystemRoot : roots) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileSystemRoot);
            root.add(node);
            //showChildren(node);
            //
            File[] files = view.getFiles(fileSystemRoot, true);
            for (File file : files) {
                if (file.isDirectory()) {
                    node.add(new DefaultMutableTreeNode(file));
                }
            }
            //
        }

        direcTree = new JTree(treeModel);
        direcTree.setRootVisible(false);
        direcTree.addTreeSelectionListener(treeSelectionListener);
        direcTree.setCellRenderer(new FileTreeCellRenderer());
        direcTree.expandRow(0);
    }

    GUI() {
        super();
        setTitle("File Management System");
        JFrame.setDefaultLookAndFeelDecorated(true);
        setContentPane(mainPanel);
        setPreferredSize(new Dimension(750, 550));

        mainPanel.setVisible(true);
        mainPanel.setLayout(new BorderLayout());

        JMenuBar mb = menuBar();
        JPanel lPanel = new JPanel();
        JPanel rPanel = new JPanel();

        setModel();
        JPanel detailView = new JPanel(new BorderLayout(3,3));

        JPanel fileMainDetails = new JPanel(new BorderLayout(2,2));
        fileMainDetails.setBorder(new EmptyBorder(0,6,0,6));

        JPanel fileDetailsLabels = new JPanel(new GridLayout(0,1,2,2));
        fileMainDetails.add(fileDetailsLabels, BorderLayout.WEST);

        JPanel fileDetailsValues = new JPanel(new GridLayout(0,1,2,2));
        fileMainDetails.add(fileDetailsValues, BorderLayout.CENTER);

        JScrollPane listItem = new JScrollPane(direcTree);
        listItem.setPreferredSize(new Dimension(250, 400));
        lPanel.add(listItem);


        table.getSelectionModel().addListSelectionListener(listSelectionListener);
        JScrollPane itemPanel = new JScrollPane(table);
        itemPanel.setPreferredSize(new Dimension(400, 400));
        detailView.add(fileMainDetails);
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
        setVisible(true);
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
        JMenuItem renameFile = new JMenuItem("Rename File");
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

    private void createFileClicked() {
        List<String> filePaths = new ArrayList<>();
        boolean deleteFlag = true;
        String currentRoot = direcTree.getModel().getRoot().toString();
        TreePath[] paths = direcTree.getSelectionPaths();
        for (TreePath path : paths) {
            String filePath = path.getLastPathComponent().toString();
        }
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
                    File[] files = view.getFiles(file, true); //!!
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

            @Override
            protected void done() {

                direcTree.setEnabled(true);
            }
        };
        worker.execute();
    }
}



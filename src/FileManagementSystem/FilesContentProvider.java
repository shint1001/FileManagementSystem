package FileManagementSystem;

import java.io.File;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class FilesContentProvider implements TreeModel {

    private File node;

    public FilesContentProvider(String path) {
        node = new File(path);
    }

    @Override
    public Object getRoot() {
        return node;
    }

    @Override
    public Object getChild(Object parent, int index) {
        if (parent == null) {
            return null;
        }
        return ((File) parent).listFiles()[index];
    }

    @Override
    public int getChildCount(Object parent) {
        if (parent == null) {
            return 0;
        }
        return ((File) parent).listFiles() != null ? ((File) parent).listFiles().length : 0;
    }

    @Override
    public boolean isLeaf(Object node) {
        return ((File) node).isFile();
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {

    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        return 0;
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {

    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {

    }
}

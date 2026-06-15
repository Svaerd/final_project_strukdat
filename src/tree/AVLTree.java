package tree;
import model.Course;
import java.util.ArrayList;
import java.util.List;

/**
 * AVL Tree untuk menyimpan dan mencari mata kuliah berdasarkan kode.
 * Kode dari Anggota 2; ditambahkan searchByPrefix() dan inOrderList()
 * oleh System Integrator untuk mendukung fitur menu utama.
 */
public class AVLTree {
    public AVLNode root;

    // ==================== ROTASI & BALANCING (Anggota 2) ====================

    int height(AVLNode N) {
        if (N == null) return 0;
        return N.height;
    }

    int max(int a, int b) {
        return (a > b) ? a : b;
    }

    AVLNode rightRotate(AVLNode y) {
        AVLNode x = y.left;
        AVLNode T2 = x.right;
        x.right = y;
        y.left = T2;
        y.height = max(height(y.left), height(y.right)) + 1;
        x.height = max(height(x.left), height(x.right)) + 1;
        return x;
    }

    AVLNode leftRotate(AVLNode x) {
        AVLNode y = x.right;
        AVLNode T2 = y.left;
        y.left = x;
        x.right = T2;
        x.height = max(height(x.left), height(x.right)) + 1;
        y.height = max(height(y.left), height(y.right)) + 1;
        return y;
    }

    int getBalance(AVLNode N) {
        if (N == null) return 0;
        return height(N.left) - height(N.right);
    }

    // ==================== INSERT (Anggota 2) ====================

    public void insert(Course course) {
        root = insertRec(root, course);
    }

    AVLNode insertRec(AVLNode node, Course course) {
        if (node == null) return new AVLNode(course);

        if (course.code.compareTo(node.course.code) < 0)
            node.left = insertRec(node.left, course);
        else if (course.code.compareTo(node.course.code) > 0)
            node.right = insertRec(node.right, course);
        else
            return node; // duplikat tidak diizinkan

        node.height = 1 + max(height(node.left), height(node.right));
        int balance = getBalance(node);

        // Left Left
        if (balance > 1 && course.code.compareTo(node.left.course.code) < 0)
            return rightRotate(node);
        // Right Right
        if (balance < -1 && course.code.compareTo(node.right.course.code) > 0)
            return leftRotate(node);
        // Left Right
        if (balance > 1 && course.code.compareTo(node.left.course.code) > 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }
        // Right Left
        if (balance < -1 && course.code.compareTo(node.right.course.code) < 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }
        return node;
    }

    // ==================== SEARCH (Anggota 2) ====================

    public Course search(String code) {
        AVLNode res = searchRec(root, code);
        return res != null ? res.course : null;
    }

    AVLNode searchRec(AVLNode root, String code) {
        if (root == null || root.course.code.equals(code))
            return root;
        if (root.course.code.compareTo(code) > 0)
            return searchRec(root.left, code);
        return searchRec(root.right, code);
    }

    // ==================== TAMBAHAN OLEH SYSTEM INTEGRATOR ====================

    /**
     * Mencari semua mata kuliah yang kodenya dimulai dengan prefix tertentu.
     * Memanfaatkan struktur sorted AVL Tree untuk efisiensi.
     * Kompleksitas: O(k + log n) di mana k = jumlah hasil.
     */
    public List<Course> searchByPrefix(String prefix) {
        List<Course> result = new ArrayList<>();
        searchByPrefixRec(root, prefix.toUpperCase(), result);
        return result;
    }

    private void searchByPrefixRec(AVLNode node, String prefix, List<Course> result) {
        if (node == null) return;
        String code = node.course.code.toUpperCase();
        if (code.startsWith(prefix)) {
            // Kunjungi kiri dulu (urutan alfabet)
            searchByPrefixRec(node.left, prefix, result);
            result.add(node.course);
            searchByPrefixRec(node.right, prefix, result);
        } else if (prefix.compareTo(code) < 0) {
            searchByPrefixRec(node.left, prefix, result);
        } else {
            searchByPrefixRec(node.right, prefix, result);
        }
    }

    /**
     * In-order traversal: mengembalikan semua Course terurut berdasarkan kode.
     */
    public List<Course> inOrderList() {
        List<Course> result = new ArrayList<>();
        inOrderRec(root, result);
        return result;
    }

    private void inOrderRec(AVLNode node, List<Course> result) {
        if (node == null) return;
        inOrderRec(node.left, result);
        result.add(node.course);
        inOrderRec(node.right, result);
    }

    /**
     * Menghapus node dari AVL Tree berdasarkan kode matkul.
     * Operasi Tree tambahan (selain insert dan search) sesuai requirement.
     */
    public void delete(String code) {
        root = deleteRec(root, code);
    }

    private AVLNode deleteRec(AVLNode node, String code) {
        if (node == null) return null;

        if (code.compareTo(node.course.code) < 0)
            node.left = deleteRec(node.left, code);
        else if (code.compareTo(node.course.code) > 0)
            node.right = deleteRec(node.right, code);
        else {
            // Node ditemukan
            if (node.left == null || node.right == null) {
                node = (node.left != null) ? node.left : node.right;
            } else {
                // Cari in-order successor (terkecil di subtree kanan)
                AVLNode successor = getMinNode(node.right);
                node.course = successor.course;
                node.right = deleteRec(node.right, successor.course.code);
            }
        }

        if (node == null) return null;

        node.height = 1 + max(height(node.left), height(node.right));
        int balance = getBalance(node);

        if (balance > 1 && getBalance(node.left) >= 0) return rightRotate(node);
        if (balance > 1 && getBalance(node.left) < 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }
        if (balance < -1 && getBalance(node.right) <= 0) return leftRotate(node);
        if (balance < -1 && getBalance(node.right) > 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }
        return node;
    }

    private AVLNode getMinNode(AVLNode node) {
        while (node.left != null) node = node.left;
        return node;
    }
}
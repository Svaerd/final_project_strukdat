package tree;
import model.Course;

public class AVLTree {
    public AVLNode root;

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

    public void insert(Course course) {
        root = insertRec(root, course);
    }

    AVLNode insertRec(AVLNode node, Course course) {
        if (node == null) return new AVLNode(course);

        // Sorting berdasarkan kode mata kuliah
        if (course.code.compareTo(node.course.code) < 0)
            node.left = insertRec(node.left, course);
        else if (course.code.compareTo(node.course.code) > 0)
            node.right = insertRec(node.right, course);
        else
            return node;

        node.height = 1 + max(height(node.left), height(node.right));
        int balance = getBalance(node);

        if (balance > 1 && course.code.compareTo(node.left.course.code) < 0)
            return rightRotate(node);
        if (balance < -1 && course.code.compareTo(node.right.course.code) > 0)
            return leftRotate(node);
        if (balance > 1 && course.code.compareTo(node.left.course.code) > 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }
        if (balance < -1 && course.code.compareTo(node.right.course.code) < 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }
        return node;
    }

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
}

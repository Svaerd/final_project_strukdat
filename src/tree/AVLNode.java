package tree;
import model.Course;

public class AVLNode {
    public Course course;
    public AVLNode left, right;
    public int height;

    public AVLNode(Course course) {
        this.course = course;
        this.height = 1;
    }
}

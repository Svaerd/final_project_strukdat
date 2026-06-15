import graph.CourseGraph;

public class Main {

    public static void main(String[] args) {

        CourseGraph graph = new CourseGraph();

        graph.loadPrerequisites("../data/prerequisites.csv");

        graph.displayGraph();

        if (graph.hasCourse("IT101")) {
            graph.dfs("IT101");
        } else {
            System.out.println("Course tidak ditemukan!");
        }
    }
}
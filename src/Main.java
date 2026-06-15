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

        // FITUR 3: TOPOLOGICAL SORT
        graph.topologicalSort();
 
        // FITUR 4: CYCLE DETECTION (kondisi normal)
        graph.detectCycle();
 
        // DEMO HOTS: Simulasi konflik kurikulum (siklus buatan)
        System.out.println("\n===== SIMULASI KONFLIK KURIKULUM =====");
        System.out.println("Menambahkan relasi buatan: IT202 -> IT101");
        System.out.println("(Padahal IT101 -> IT202 sudah ada dari prerequisites.csv)");
 
        graph.addPrerequisite("IT202", "IT101");
 
        graph.detectCycle();
        graph.topologicalSort();
    }
}

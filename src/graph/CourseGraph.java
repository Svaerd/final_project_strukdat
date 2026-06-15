package graph;

import java.util.*;
import java.io.*;

public class CourseGraph {

    private Map<String, List<String>> adjacencyList;

    public CourseGraph() {
        adjacencyList = new HashMap<>();
    }

    // Tambah course (vertex)
    public void addCourse(String courseCode) {
        adjacencyList.putIfAbsent(courseCode, new ArrayList<>());
    }

    // Tambah prerequisite (edge)
    // prerequisite -> course
    public void addPrerequisite(String prerequisite, String course) {

        adjacencyList.putIfAbsent(prerequisite, new ArrayList<>());
        adjacencyList.putIfAbsent(course, new ArrayList<>());

        adjacencyList.get(prerequisite).add(course);
    }

    public boolean hasCourse(String courseCode) {
    return adjacencyList.containsKey(courseCode);
}

    // Load graph dari prerequisites.csv
    public void loadPrerequisites(String filename) {

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {

            String line;

            while ((line = br.readLine()) != null) {

                String[] parts = line.split(",");

                if (parts.length < 2)
                    continue;

                String course = parts[0].trim();
                String prerequisite = parts[1].trim();

                addPrerequisite(prerequisite, course);
            }

        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    // Tampilkan graph
    public void displayGraph() {

        System.out.println("\n===== COURSE GRAPH =====");

        for (String course : adjacencyList.keySet()) {

            System.out.print(course + " -> ");

            List<String> neighbors = adjacencyList.get(course);

            if (neighbors.isEmpty()) {
                System.out.print("(no next course)");
            } else {
                for (String next : neighbors) {
                    System.out.print(next + " ");
                }
            }

            System.out.println();
        }
    }

    // DFS Traversal
    public void dfs(String startCourse) {

        System.out.println("\n===== DFS TRAVERSAL =====");

        Set<String> visited = new HashSet<>();

        dfsHelper(startCourse, visited);
    }

    private void dfsHelper(String course, Set<String> visited) {

        visited.add(course);

        System.out.println(course);

        for (String neighbor :
                adjacencyList.getOrDefault(course, new ArrayList<>())) {

            if (!visited.contains(neighbor)) {

                dfsHelper(neighbor, visited);
            }
        }
    }
}


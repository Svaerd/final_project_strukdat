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

    //  FITUR 3: TOPOLOGICAL SORT (Kahn's Algorithm / BFS-based)
    public List<String> topologicalSort() {
        System.out.println("\n===== TOPOLOGICAL SORT (Urutan Pengambilan MK) =====");
 
        // 1. Hitung in-degree semua node
        Map<String, Integer> inDegree = new HashMap<>();
        for (String course : adjacencyList.keySet()) {
            inDegree.put(course, 0);
        }
        for (String course : adjacencyList.keySet()) {
            for (String neighbor : adjacencyList.get(course)) {
                inDegree.put(neighbor, inDegree.getOrDefault(neighbor, 0) + 1);
            }
        }
 
        // 2. Masukkan semua node dengan in-degree 0 ke queue
        Queue<String> queue = new LinkedList<>();
        for (String course : inDegree.keySet()) {
            if (inDegree.get(course) == 0) {
                queue.offer(course);
            }
        }
 
        // 3. Proses queue
        List<String> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            String current = queue.poll();
            result.add(current);
 
            for (String neighbor : adjacencyList.getOrDefault(current, new ArrayList<>())) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.offer(neighbor);
                }
            }
        }
 
        // 4. Cek apakah ada siklus
        if (result.size() != adjacencyList.size()) {
            System.out.println("PERINGATAN: Topological sort tidak lengkap!");
            System.out.println("Kemungkinan ada SIKLUS pada graph prasyarat.");
            System.out.println("Berhasil diurutkan: " + result.size() + " dari " + adjacencyList.size() + " matkul.");
        } else {
            System.out.println("Urutan rekomendasi pengambilan matkul:");
            for (int i = 0; i < result.size(); i++) {
                System.out.println((i + 1) + ". " + result.get(i));
            }
        }
 
        return result;
    }


    //  FITUR 4: CYCLE DETECTION (DFS dengan 3 warna)
    public boolean detectCycle() {
        System.out.println("\n===== CYCLE DETECTION =====");
 
        Map<String, Integer> color = new HashMap<>();
        for (String course : adjacencyList.keySet()) {
            color.put(course, 0); // semua mulai WHITE
        }
 
        List<String> path = new ArrayList<>();
        boolean cycleFound = false;
 
        for (String course : adjacencyList.keySet()) {
            if (color.get(course) == 0) {
                if (detectCycleHelper(course, color, path)) {
                    cycleFound = true;
                }
            }
        }
 
        if (!cycleFound) {
            System.out.println("Tidak ada siklus. Kurikulum VALID.");
        } else {
            System.out.println("Kurikulum TIDAK VALID! Ada prasyarat melingkar.");
        }
 
        return cycleFound;
    }
 
    private boolean detectCycleHelper(String course, Map<String, Integer> color, List<String> path) {
        color.put(course, 1); // GRAY
        path.add(course);
 
        boolean found = false;
        for (String neighbor : adjacencyList.getOrDefault(course, new ArrayList<>())) {
            if (color.get(neighbor) == 1) {
                int idx = path.indexOf(neighbor);
                System.out.print("Siklus ditemukan: ");
                for (int i = idx; i < path.size(); i++) {
                    System.out.print(path.get(i) + " -> ");
                }
                System.out.println(neighbor);
                found = true;
            } else if (color.get(neighbor) == 0) {
                if (detectCycleHelper(neighbor, color, path)) {
                    found = true;
                }
            }
        }
 
        color.put(course, 2); // BLACK
        path.remove(path.size() - 1);
        return found;
    }
}

package graph;

import java.util.*;
import java.io.*;

/**
 * Graph berarah (Directed Graph) merepresentasikan relasi prasyarat antar matkul.
 * Edge: prerequisite -> course (artinya: prerequisite harus diambil dulu sebelum course).
 *
 * Kode Anggota 3 (DFS, Topological Sort, Cycle Detection) diintegrasikan di sini.
 * Diperbaiki: syntax error (kode di luar class) oleh System Integrator.
 * Ditambah: getPrerequisites(), simulateNewCourse() oleh System Integrator.
 */
public class CourseGraph {

    private Map<String, List<String>> adjacencyList; // prerequisite -> [courses yang membutuhkannya]

    public CourseGraph() {
        adjacencyList = new HashMap<>();
    }

    // ==================== OPERASI DASAR GRAPH (Anggota 3) ====================

    public void addCourse(String courseCode) {
        adjacencyList.putIfAbsent(courseCode, new ArrayList<>());
    }

    /**
     * Menambah relasi prasyarat: prerequisite harus diambil sebelum course.
     * prerequisite -> course (directed edge)
     */
    public void addPrerequisite(String prerequisite, String course) {
        adjacencyList.putIfAbsent(prerequisite, new ArrayList<>());
        adjacencyList.putIfAbsent(course, new ArrayList<>());
        adjacencyList.get(prerequisite).add(course);
    }

    public boolean hasCourse(String courseCode) {
        return adjacencyList.containsKey(courseCode);
    }

    public Set<String> getAllCourses() {
        return adjacencyList.keySet();
    }

    /**
     * Load relasi prasyarat dari file CSV.
     * Format CSV: courseCode,prerequisiteCode
     */
    public void loadPrerequisites(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            int count = 0;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 2) continue;
                String course = parts[0].trim();
                String prerequisite = parts[1].trim();
                addPrerequisite(prerequisite, course);
                count++;
            }
            System.out.println("[Graph] " + count + " relasi prasyarat berhasil dimuat.");
        } catch (IOException e) {
            System.out.println("Error membaca file: " + e.getMessage());
        }
    }

    public void displayGraph() {
        System.out.println("\n===== COURSE PREREQUISITE GRAPH =====");
        System.out.println("Format: [Matkul] -> [Matkul yang membutuhkannya sebagai prasyarat]");
        List<String> sorted = new ArrayList<>(adjacencyList.keySet());
        Collections.sort(sorted);
        for (String course : sorted) {
            System.out.print("  " + course + " -> ");
            List<String> neighbors = adjacencyList.get(course);
            if (neighbors.isEmpty()) {
                System.out.print("(tidak menjadi prasyarat matkul lain)");
            } else {
                System.out.print(String.join(", ", neighbors));
            }
            System.out.println();
        }
    }

    // ==================== DFS TRAVERSAL (Anggota 3) ====================

    /**
     * DFS dari sebuah matkul: menampilkan rantai matkul yang membutuhkan matkul ini.
     * Kompleksitas: O(V + E)
     */
    public void dfs(String startCourse) {
        System.out.println("\n===== DFS TRAVERSAL dari " + startCourse + " =====");
        System.out.println("(Menampilkan rantai matkul yang terbuka setelah " + startCourse + ")");
        Set<String> visited = new HashSet<>();
        dfsHelper(startCourse, visited, 0);
    }

    private void dfsHelper(String course, Set<String> visited, int depth) {
        visited.add(course);
        System.out.println("  " + "  ".repeat(depth) + "-> " + course);
        for (String neighbor : adjacencyList.getOrDefault(course, new ArrayList<>())) {
            if (!visited.contains(neighbor)) {
                dfsHelper(neighbor, visited, depth + 1);
            }
        }
    }

    // ==================== TOPOLOGICAL SORT (Anggota 3) ====================

    /**
     * Topological Sort menggunakan Kahn's Algorithm (BFS-based).
     * Menghasilkan urutan pengambilan matkul yang valid.
     * Kompleksitas: O(V + E)
     */
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

        // 2. Masukkan semua node dengan in-degree 0 ke queue (matkul tanpa prasyarat)
        Queue<String> queue = new PriorityQueue<>(); // PriorityQueue agar urutan deterministik
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

        // 4. Cek siklus
        if (result.size() != adjacencyList.size()) {
            System.out.println("PERINGATAN: Topological sort tidak lengkap!");
            System.out.println("Kemungkinan ada SIKLUS pada graph prasyarat.");
            System.out.println("Berhasil diurutkan: " + result.size() + " dari " + adjacencyList.size() + " matkul.");
        } else {
            System.out.println("Urutan rekomendasi pengambilan matkul:");
            for (int i = 0; i < result.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + result.get(i));
            }
        }

        return result;
    }

    // ==================== CYCLE DETECTION (Anggota 3) ====================

    /**
     * Deteksi siklus menggunakan DFS dengan 3 warna (WHITE/GRAY/BLACK).
     * Kompleksitas: O(V + E)
     */
    public boolean detectCycle() {
        System.out.println("\n===== CYCLE DETECTION =====");

        Map<String, Integer> color = new HashMap<>();
        for (String course : adjacencyList.keySet()) {
            color.put(course, 0); // WHITE = belum dikunjungi
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
            System.out.println("  Tidak ada siklus. Kurikulum VALID.");
        } else {
            System.out.println("  Kurikulum TIDAK VALID! Ada prasyarat melingkar.");
        }

        return cycleFound;
    }

    private boolean detectCycleHelper(String course, Map<String, Integer> color, List<String> path) {
        color.put(course, 1); // GRAY = sedang diproses
        path.add(course);

        boolean found = false;
        for (String neighbor : adjacencyList.getOrDefault(course, new ArrayList<>())) {
            if (!color.containsKey(neighbor)) continue;
            if (color.get(neighbor) == 1) {
                // Siklus ditemukan
                int idx = path.indexOf(neighbor);
                System.out.print("  Siklus ditemukan: ");
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

        color.put(course, 2); // BLACK = selesai
        path.remove(path.size() - 1);
        return found;
    }

    // ==================== TAMBAHAN OLEH SYSTEM INTEGRATOR ====================

    /**
     * Mengembalikan daftar prasyarat langsung dari sebuah matkul.
     * (Arah terbalik dari adjacency list: cari siapa yang mengarah ke courseCode)
     */
    public List<String> getDirectPrerequisites(String courseCode) {
        List<String> prereqs = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : adjacencyList.entrySet()) {
            if (entry.getValue().contains(courseCode)) {
                prereqs.add(entry.getKey());
            }
        }
        Collections.sort(prereqs);
        return prereqs;
    }

    /**
     * Mengembalikan semua prasyarat (langsung + tidak langsung) dari sebuah matkul
     * menggunakan DFS ke arah terbalik.
     */
    public Set<String> getAllPrerequisites(String courseCode) {
        Set<String> allPrereqs = new HashSet<>();
        getAllPrerequisitesRec(courseCode, allPrereqs);
        return allPrereqs;
    }

    private void getAllPrerequisitesRec(String courseCode, Set<String> visited) {
        for (String prereq : getDirectPrerequisites(courseCode)) {
            if (!visited.contains(prereq)) {
                visited.add(prereq);
                getAllPrerequisitesRec(prereq, visited);
            }
        }
    }

    /**
     * FITUR SIMULASI: Tambah matkul baru dengan prasyarat tertentu,
     * lalu analisis dampaknya terhadap urutan topological sort.
     *
     * @param newCode       kode matkul baru
     * @param prerequisites daftar kode prasyarat matkul baru ini
     * @return topological sort hasil setelah penambahan
     */
    public List<String> simulateNewCourse(String newCode, List<String> prerequisites) {
        System.out.println("\n===== SIMULASI: PENAMBAHAN MATKUL BARU =====");
        System.out.println("Matkul baru: " + newCode);
        System.out.println("Prasyarat: " + (prerequisites.isEmpty() ? "(tidak ada)" : String.join(", ", prerequisites)));

        // Simpan urutan sebelum
        List<String> before = topologicalSortSilent();

        // Tambahkan matkul baru ke graph
        addCourse(newCode);
        for (String prereq : prerequisites) {
            if (hasCourse(prereq)) {
                addPrerequisite(prereq, newCode);
            } else {
                System.out.println("  PERINGATAN: Prasyarat '" + prereq + "' tidak ditemukan di graph, dilewati.");
            }
        }

        // Cek siklus dulu
        boolean hasCycle = detectCycleSilent();

        // Urutan sesudah
        List<String> after = topologicalSortSilent();

        // Analisis dampak
        System.out.println("\n--- Analisis Dampak ---");
        System.out.println("Jumlah matkul sebelum: " + before.size());
        System.out.println("Jumlah matkul sesudah: " + after.size());

        if (hasCycle) {
            System.out.println("MASALAH: Penambahan matkul ini menciptakan SIKLUS prasyarat!");
            System.out.println("Matkul " + newCode + " tidak dapat ditambahkan dengan prasyarat tersebut.");
        } else {
            // Cari posisi matkul baru di urutan
            int pos = after.indexOf(newCode) + 1;
            System.out.println("Status: Kurikulum tetap VALID (tidak ada siklus).");
            System.out.println("Posisi " + newCode + " dalam urutan pengambilan: ke-" + pos);

            // Cari matkul yang posisinya bergeser
            System.out.println("\nUrutan matkul sesudah penambahan " + newCode + ":");
            for (int i = 0; i < after.size(); i++) {
                String marker = after.get(i).equals(newCode) ? " <-- BARU" : "";
                System.out.println("  " + (i + 1) + ". " + after.get(i) + marker);
            }
        }

        return after;
    }

    /**
     * Topological sort tanpa output (untuk dipakai secara internal).
     */
    public List<String> topologicalSortSilent() {
        Map<String, Integer> inDegree = new HashMap<>();
        for (String course : adjacencyList.keySet()) inDegree.put(course, 0);
        for (String course : adjacencyList.keySet())
            for (String neighbor : adjacencyList.get(course))
                inDegree.put(neighbor, inDegree.getOrDefault(neighbor, 0) + 1);

        Queue<String> queue = new PriorityQueue<>();
        for (String course : inDegree.keySet())
            if (inDegree.get(course) == 0) queue.offer(course);

        List<String> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            String current = queue.poll();
            result.add(current);
            for (String neighbor : adjacencyList.getOrDefault(current, new ArrayList<>())) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) queue.offer(neighbor);
            }
        }
        return result;
    }

    /**
     * Cycle detection tanpa output (untuk simulasi).
     */
    private boolean detectCycleSilent() {
        Map<String, Integer> color = new HashMap<>();
        for (String course : adjacencyList.keySet()) color.put(course, 0);
        for (String course : adjacencyList.keySet())
            if (color.get(course) == 0 && detectCycleHelperSilent(course, color, new ArrayList<>()))
                return true;
        return false;
    }

    private boolean detectCycleHelperSilent(String course, Map<String, Integer> color, List<String> path) {
        color.put(course, 1);
        path.add(course);
        for (String neighbor : adjacencyList.getOrDefault(course, new ArrayList<>())) {
            if (!color.containsKey(neighbor)) continue;
            if (color.get(neighbor) == 1) return true;
            if (color.get(neighbor) == 0 && detectCycleHelperSilent(neighbor, color, path)) return true;
        }
        color.put(course, 2);
        path.remove(path.size() - 1);
        return false;
    }

    /**
     * Menghapus matkul dari graph (beserta semua edge yang berkaitan).
     */
    public boolean removeCourse(String courseCode) {
        if (!hasCourse(courseCode)) return false;
        adjacencyList.remove(courseCode);
        for (List<String> neighbors : adjacencyList.values()) {
            neighbors.remove(courseCode);
        }
        return true;
    }
}
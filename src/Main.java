import graph.CourseGraph;
import model.Course;
import tree.AVLTree;
import utils.CSVReader;

import java.util.*;

/**
 * ============================================================
 *  COURSE PREREQUISITE PLANNER — Topik 9
 *  Final Project Struktur Data 2026
 * ============================================================
 *  System Integrator  : [Nama Kamu]
 *  Anggota 1 (Model)  : [Nama Anggota 1]   → Course.java, dataset CSV
 *  Anggota 2 (Tree)   : [Nama Anggota 2]   → AVLTree.java, AVLNode.java
 *  Anggota 3 (Graph)  : [Nama Anggota 3]   → CourseGraph.java
 * ============================================================
 *
 * Peran System Integrator:
 *  1. Menyatukan semua komponen menjadi satu program CLI
 *  2. Membuat menu utama interaktif
 *  3. Menambah fitur simulasi penambahan matkul baru
 *  4. Memperbaiki bug (kode di luar class di CourseGraph)
 *  5. Melengkapi CSVReader.java yang masih kosong
 *  6. Menambah metode pendukung di AVLTree & CourseGraph
 */
public class Main {

    static Scanner sc = new Scanner(System.in);
    static AVLTree avlTree = new AVLTree();
    static CourseGraph graph = new CourseGraph();

    // Path relatif ke file data — sesuaikan jika dijalankan dari direktori berbeda
    static final String COURSES_FILE       = "data/courses.csv";
    static final String PREREQUISITES_FILE = "data/prerequisites.csv";

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("       COURSE PREREQUISITE PLANNER — Topik 9");
        System.out.println("       Final Project Struktur Data 2026");
        System.out.println("============================================================");

        // ===== INISIALISASI: Load data dari CSV =====
        System.out.println("\n[INIT] Memuat data...");
        List<Course> courses = CSVReader.readCourses(COURSES_FILE);

        // Masukkan semua matkul ke AVL Tree (Anggota 2)
        for (Course c : courses) {
            avlTree.insert(c);
            graph.addCourse(c.code);
        }

        // Load relasi prasyarat ke Graph (Anggota 3)
        graph.loadPrerequisites(PREREQUISITES_FILE);

        System.out.println("[INIT] Sistem siap!\n");

        // ===== MENU UTAMA =====
        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("Pilihan Anda: ");
            String input = sc.nextLine().trim();

            switch (input) {
                case "1" -> menuSearchCourse();
                case "2" -> menuShowPrerequisites();
                case "3" -> menuTopoSort();
                case "4" -> menuCycleDetection();
                case "5" -> menuDFSChain();
                case "6" -> menuShowAllCourses();
                case "7" -> menuShowGraph();
                case "8" -> menuSimulateNewCourse();
                case "9" -> menuInsertCourse();
                case "10" -> menuDeleteCourse();
                case "0" -> {
                    System.out.println("\nTerima kasih! Program selesai.");
                    running = false;
                }
                default -> System.out.println("  [!] Pilihan tidak valid. Coba lagi.\n");
            }
        }
    }

    // ==================== MENU DISPLAY ====================

    static void printMenu() {
        System.out.println("------------------------------------------------------------");
        System.out.println("  MENU UTAMA");
        System.out.println("------------------------------------------------------------");
        System.out.println("  [TREE - AVL Tree]");
        System.out.println("   1. Cari matkul berdasarkan kode / prefix");
        System.out.println("   9. Tambah matkul baru");
        System.out.println("  10. Hapus matkul");
        System.out.println("  [GRAPH - Directed Graph]");
        System.out.println("   2. Tampilkan prasyarat langsung & tidak langsung");
        System.out.println("   3. Rekomendasi urutan pengambilan (Topological Sort)");
        System.out.println("   4. Deteksi siklus prasyarat (Cycle Detection)");
        System.out.println("   5. Telusuri rantai matkul dari suatu titik (DFS)");
        System.out.println("   7. Tampilkan seluruh graph prasyarat");
        System.out.println("  [UMUM]");
        System.out.println("   6. Tampilkan semua matkul (In-Order AVL)");
        System.out.println("   8. SIMULASI: Tambah matkul baru & lihat dampaknya");
        System.out.println("   0. Keluar");
        System.out.println("------------------------------------------------------------");
    }

    // ==================== MENU 1: SEARCH ====================

    static void menuSearchCourse() {
        System.out.println("\n--- CARI MATKUL ---");
        System.out.print("Masukkan kode / prefix (contoh: IT3, IT401): ");
        String input = sc.nextLine().trim().toUpperCase();

        // Coba exact search dulu (O(log n))
        Course exact = avlTree.search(input);
        if (exact != null) {
            System.out.println("\n[Hasil Tepat]");
            System.out.println("  " + exact);
            List<String> prereqs = graph.getDirectPrerequisites(exact.code);
            System.out.println("  Prasyarat langsung: " + (prereqs.isEmpty() ? "tidak ada" : String.join(", ", prereqs)));
        } else {
            // Prefix search
            List<Course> results = avlTree.searchByPrefix(input);
            if (results.isEmpty()) {
                System.out.println("  Tidak ada matkul dengan prefix '" + input + "'.");
            } else {
                System.out.println("\n[Hasil Prefix Search '" + input + "'] — " + results.size() + " matkul:");
                for (Course c : results) {
                    System.out.println("  " + c);
                }
            }
        }
        System.out.println();
    }

    // ==================== MENU 2: PREREQUISITES ====================

    static void menuShowPrerequisites() {
        System.out.println("\n--- TAMPILKAN PRASYARAT ---");
        System.out.print("Masukkan kode matkul (contoh: IT301): ");
        String code = sc.nextLine().trim().toUpperCase();

        if (!graph.hasCourse(code)) {
            System.out.println("  Matkul '" + code + "' tidak ditemukan.\n");
            return;
        }

        Course course = avlTree.search(code);
        System.out.println("\nMatkul: " + (course != null ? course : code));

        List<String> direct = graph.getDirectPrerequisites(code);
        System.out.println("\nPrasyarat LANGSUNG (" + direct.size() + "):");
        if (direct.isEmpty()) {
            System.out.println("  (tidak ada — matkul ini bisa langsung diambil)");
        } else {
            for (String p : direct) {
                Course pc = avlTree.search(p);
                System.out.println("  - " + p + (pc != null ? " (" + pc.name + ")" : ""));
            }
        }

        Set<String> allPrereqs = graph.getAllPrerequisites(code);
        allPrereqs.removeAll(direct); // hanya prasyarat TIDAK LANGSUNG
        System.out.println("\nPrasyarat TIDAK LANGSUNG (" + allPrereqs.size() + "):");
        if (allPrereqs.isEmpty()) {
            System.out.println("  (tidak ada)");
        } else {
            List<String> indirect = new ArrayList<>(allPrereqs);
            Collections.sort(indirect);
            for (String p : indirect) {
                Course pc = avlTree.search(p);
                System.out.println("  - " + p + (pc != null ? " (" + pc.name + ")" : ""));
            }
        }
        System.out.println();
    }

    // ==================== MENU 3: TOPOLOGICAL SORT ====================

    static void menuTopoSort() {
        graph.topologicalSort();
        System.out.println();
    }

    // ==================== MENU 4: CYCLE DETECTION ====================

    static void menuCycleDetection() {
        graph.detectCycle();
        System.out.println();
    }

    // ==================== MENU 5: DFS ====================

    static void menuDFSChain() {
        System.out.println("\n--- DFS: RANTAI MATKUL ---");
        System.out.print("Masukkan kode matkul awal (contoh: IT101): ");
        String code = sc.nextLine().trim().toUpperCase();
        if (!graph.hasCourse(code)) {
            System.out.println("  Matkul '" + code + "' tidak ditemukan.\n");
            return;
        }
        graph.dfs(code);
        System.out.println();
    }

    // ==================== MENU 6: SEMUA MATKUL ====================

    static void menuShowAllCourses() {
        System.out.println("\n--- SEMUA MATA KULIAH (In-Order AVL Tree, urut kode) ---");
        List<Course> all = avlTree.inOrderList();
        if (all.isEmpty()) {
            System.out.println("  (belum ada data)");
        } else {
            System.out.printf("  %-8s %-45s %4s %5s %s%n", "KODE", "NAMA", "SKS", "SMT", "TIPE");
            System.out.println("  " + "-".repeat(75));
            for (Course c : all) {
                System.out.printf("  %-8s %-45s %4d %5d %s%n",
                        c.code, c.name, c.sks, c.semester, c.type);
            }
            System.out.println("  Total: " + all.size() + " mata kuliah.");
        }
        System.out.println();
    }

    // ==================== MENU 7: TAMPILKAN GRAPH ====================

    static void menuShowGraph() {
        graph.displayGraph();
        System.out.println();
    }

    // ==================== MENU 8: SIMULASI MATKUL BARU ====================

    /**
     * Fitur utama System Integrator:
     * Mensimulasikan penambahan matkul baru ke kurikulum dan menganalisis dampaknya
     * terhadap urutan pengambilan matkul (topological sort).
     *
     * What-if: "Apa yang terjadi jika tiba-tiba ada matkul baru ditambahkan?"
     */
    static void menuSimulateNewCourse() {
        System.out.println("\n===== SIMULASI: PENAMBAHAN MATKUL BARU =====");
        System.out.println("Fitur ini mensimulasikan penambahan matkul baru ke kurikulum");
        System.out.println("dan menganalisis efeknya terhadap urutan pengambilan matkul.\n");

        System.out.print("Kode matkul baru (contoh: IT507): ");
        String newCode = sc.nextLine().trim().toUpperCase();

        if (graph.hasCourse(newCode)) {
            System.out.println("  [!] Matkul '" + newCode + "' sudah ada di sistem!");
            System.out.println("  Gunakan menu 2 untuk melihat prasyaratnya.\n");
            return;
        }

        System.out.print("Nama matkul: ");
        String name = sc.nextLine().trim();

        System.out.print("SKS: ");
        int sks = 3;
        try { sks = Integer.parseInt(sc.nextLine().trim()); } catch (Exception e) { /* default */ }

        System.out.print("Semester: ");
        int semester = 6;
        try { semester = Integer.parseInt(sc.nextLine().trim()); } catch (Exception e) { /* default */ }

        System.out.print("Tipe (Wajib/Pilihan): ");
        String type = sc.nextLine().trim();
        if (type.isEmpty()) type = "Pilihan";

        System.out.println("\nMasukkan prasyarat matkul ini (pisahkan dengan koma, kosongkan jika tidak ada):");
        System.out.print("Prasyarat: ");
        String prereqInput = sc.nextLine().trim().toUpperCase();

        List<String> prereqs = new ArrayList<>();
        if (!prereqInput.isEmpty()) {
            for (String p : prereqInput.split(",")) {
                prereqs.add(p.trim());
            }
        }

        // Tambah ke AVL Tree juga
        Course newCourse = new Course(newCode, name, sks, semester, type);
        avlTree.insert(newCourse);

        // Jalankan simulasi di graph
        List<String> newOrder = graph.simulateNewCourse(newCode, prereqs);

        System.out.println("\n[INFO] Matkul " + newCode + " berhasil ditambahkan ke sistem.");
        System.out.println("       Gunakan menu 3 untuk melihat urutan lengkap terbaru.\n");
    }

    // ==================== MENU 9: INSERT MATKUL ====================

    static void menuInsertCourse() {
        System.out.println("\n--- TAMBAH MATA KULIAH ---");
        System.out.print("Kode (contoh: IT601): ");
        String code = sc.nextLine().trim().toUpperCase();

        if (graph.hasCourse(code)) {
            System.out.println("  [!] Matkul '" + code + "' sudah ada.\n");
            return;
        }

        System.out.print("Nama: ");
        String name = sc.nextLine().trim();
        System.out.print("SKS: ");
        int sks = 3;
        try { sks = Integer.parseInt(sc.nextLine().trim()); } catch (Exception e) {}
        System.out.print("Semester: ");
        int semester = 1;
        try { semester = Integer.parseInt(sc.nextLine().trim()); } catch (Exception e) {}
        System.out.print("Tipe (Wajib/Pilihan): ");
        String type = sc.nextLine().trim();
        if (type.isEmpty()) type = "Wajib";

        Course newCourse = new Course(code, name, sks, semester, type);
        avlTree.insert(newCourse);
        graph.addCourse(code);

        System.out.print("Prasyarat (pisah koma, kosong = tidak ada): ");
        String prereqInput = sc.nextLine().trim().toUpperCase();
        if (!prereqInput.isEmpty()) {
            for (String p : prereqInput.split(",")) {
                String prereq = p.trim();
                if (graph.hasCourse(prereq)) {
                    graph.addPrerequisite(prereq, code);
                } else {
                    System.out.println("  [!] Prasyarat '" + prereq + "' tidak ditemukan, dilewati.");
                }
            }
        }

        System.out.println("  [OK] Matkul '" + code + " - " + name + "' berhasil ditambahkan.\n");
    }

    // ==================== MENU 10: DELETE MATKUL ====================

    static void menuDeleteCourse() {
        System.out.println("\n--- HAPUS MATA KULIAH ---");
        System.out.print("Kode matkul yang akan dihapus: ");
        String code = sc.nextLine().trim().toUpperCase();

        Course c = avlTree.search(code);
        if (c == null) {
            System.out.println("  [!] Matkul '" + code + "' tidak ditemukan.\n");
            return;
        }

        System.out.println("  Matkul ditemukan: " + c);
        System.out.print("  Yakin ingin menghapus? (y/n): ");
        String confirm = sc.nextLine().trim().toLowerCase();
        if (!confirm.equals("y")) {
            System.out.println("  Penghapusan dibatalkan.\n");
            return;
        }

        avlTree.delete(code);
        boolean removed = graph.removeCourse(code);
        System.out.println("  [OK] Matkul '" + code + "' berhasil dihapus dari AVL Tree dan Graph.\n");
    }
}
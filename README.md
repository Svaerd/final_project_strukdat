# Final Project Struktur Data — Course Prerequisite Planner

## Cara Menjalankan

### Prasyarat
- JDK 11 / 17 / 21 sudah terinstall dan `javac` serta `java` tersedia di PATH.

### Compile
```bash
javac -d bin src/**/*.java src/tree/*.java src/utils/*.java src/graph/*.java src/model/*.java
```

Atau (pada bash dengan glob expansion):
```bash
find src -name "*.java" | xargs javac -d bin
```

### Jalankan
```bash
java -cp bin Main
```

> **Catatan:** Program akan membaca file `data/courses.csv` dan `data/prerequisites.csv` secara otomatis. Pastikan menjalankan perintah dari direktori root proyek ini.

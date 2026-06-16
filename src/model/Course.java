package model;

public class Course {
    public String code;
    public String name;
    public int sks;
    public int semester;
    public String type;
    public int kuota;
    public String departemen;

    public Course(String code, String name, int sks, int semester, String type, int kuota, String departemen) {
        this.code = code;
        this.name = name;
        this.sks = sks;
        this.semester = semester;
        this.type = type;
        this.kuota = kuota;
        this.departemen = departemen;
    }

    @Override
    public String toString() {
        return code + " - " + name + " (" + sks + " SKS, Kuota: " + kuota + ")";
    }
}

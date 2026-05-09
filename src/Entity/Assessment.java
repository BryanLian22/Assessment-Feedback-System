package Entity;

import Users.Student;
import java.util.HashMap;

public class Assessment {
    private String name;
    private Module module;
    private HashMap<Student, Integer> marks = new HashMap<>();

    public Assessment(String name, Module module) {
        this.name = name;
        this.module = module;
    }

    public String getName() { return name; }
    public Module getModule() { return module; }
    public HashMap<Student, Integer> getMarks() { return marks; }

    public void enterMark(Student s, int mark) { marks.put(s, mark); }
}
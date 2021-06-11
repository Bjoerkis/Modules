package corepackage;

public class PersonCore {
    private int id;
    private String name;
    private int age;
    private String programmer;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getProgrammer() {
        return programmer;
    }

    public void setProgrammer(String programmer) {
        this.programmer = programmer;
    }


    @Override
    public String toString() {
        return "[Id: " + id + ", Name: " + name + ", age: " + age + ", Programmer: " + programmer + "]\n";
    }
}
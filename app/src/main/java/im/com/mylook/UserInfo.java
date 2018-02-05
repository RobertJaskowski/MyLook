package im.com.mylook;

/**
 * Created by Robert on 05.02.2018 - 11:53.
 */

public class UserInfo {
    private String name;
    private int age;
    private String sex;

    public UserInfo() {
    }

    public UserInfo(String name, int age, String sex) {
        this.name = name;
        this.age = age;
        this.sex = sex;
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

    public  String getSex() {
        return sex;
    }


}

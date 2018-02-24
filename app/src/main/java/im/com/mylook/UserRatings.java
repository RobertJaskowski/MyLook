package im.com.mylook;

import java.util.ArrayList;

/**
 * Created by Robert on 06.02.2018 - 10:50.
 */

public class UserRatings {
    private ArrayList<Integer> ratings;
    private Integer users;

    public UserRatings() {
    }

    public UserRatings(ArrayList<Integer> ratings, Integer users) {
        this.ratings = ratings;
        this.users = users;
    }

    public ArrayList<Integer> getRatings() {
        return ratings;
    }

    public void setRatings(ArrayList<Integer> ratings) {
        this.ratings = ratings;
    }

    public Integer getUsers() {
        return users;
    }

    public void setUsers(Integer users) {
        this.users = users;
    }
}

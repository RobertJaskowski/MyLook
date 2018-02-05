package im.com.mylook;

/**
 * Created by Robert on 05.02.2018 - 11:02.
 */

public class User {
    private String image;
    private String date;
    private String path;
    private int average;
    private int ratings;//todo no rat and ave at start

    public User() {
    }

    public User(String image, String date, String path, int average, int ratings) {
        this.image = image;
        this.date = date;
        this.path = path;
        this.average = average;
        this.ratings = ratings;

    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getAverage() {
        return average;
    }

    public void setAverage(int average) {
        this.average = average;
    }

    public int getRatings() {
        return ratings;
    }

    public void setRatings(int ratings) {
        this.ratings = ratings;
    }
}

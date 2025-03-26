public class rating {
    private int rating;
    private int driving_skills;
    private int friendliness;
    private int good_music;
    private String Driverusername;
    public rating(String Driverusername,int rating, int driving_skills, int friendliness, int good_music) {
        this.rating = rating;
        this.driving_skills = driving_skills;
        this.friendliness = friendliness;
        this.good_music = good_music;
        this.Driverusername = Driverusername;
    }
    public int getRating() {
        return rating;
    }
    public void setRating(int rating) {
        this.rating = rating;
    }
    public int getDrivingSkills() {
        return driving_skills;
    }
    public void setDrivingSkills(int driving_skills) {
        this.driving_skills = driving_skills;
    }
    public int getFriendliness() {
        return friendliness;
    }
    public void setFriendliness(int friendliness) {
        this.friendliness = friendliness;
    }
    public int getGoodMusic() {
        return good_music;
    }
    public void setGoodMusic(int good_music) {
        this.good_music = good_music;
    }
    public String getDriverusername() {
        return Driverusername;
    }
    public void setDriverusername(String Driverusername) {
        this.Driverusername = Driverusername;
    }
}

package accents.bot.accents_study_bot.database;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usersDataTable")
public class User {
    @Id
    private long userId;
    private String name;
    private boolean flagStartTest;
    private int score;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name;}

    public boolean isFlagStartTest() {
        return flagStartTest;
    }

    public void setFlagStartTest(boolean flagStartTest) {
        this.flagStartTest = flagStartTest;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}

package accents.bot.accents_study_bot.userDataBase;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name = "userDataTable")
public class User {
    @Id
    private long userId;
    private boolean flagStartTest;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public boolean isFlagStartTest() {
        return flagStartTest;
    }

    public void setFlagStartTest(boolean flagStartTest) {
        this.flagStartTest = flagStartTest;
    }
}

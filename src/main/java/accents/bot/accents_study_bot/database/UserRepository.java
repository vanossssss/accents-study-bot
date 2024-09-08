package accents.bot.accents_study_bot.database;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    @Query(value = "select * from users_data_table where user_id = ?1", nativeQuery = true)
    User findByUserId(long userId);

    @Query(value = "select * from users_data_table order by score desc limit 5", nativeQuery = true)
    ArrayList<User> findByScore();
}

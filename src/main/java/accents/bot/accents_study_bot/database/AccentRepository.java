package accents.bot.accents_study_bot.database;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccentRepository extends CrudRepository<Accent, Long> {
    @Query(value = "select * from accents_data_table order by rand() limit 1", nativeQuery = true)
    Accent findByRandom();
}

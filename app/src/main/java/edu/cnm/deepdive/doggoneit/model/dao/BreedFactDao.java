package edu.cnm.deepdive.doggoneit.model.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import edu.cnm.deepdive.doggoneit.model.entity.BreedFact;
import java.util.List;

@Dao
public interface BreedFactDao {

  @Insert
  long insert(BreedFact breedFact);

  @Insert
  List<Long> insert(BreedFact... breedFacts);

  @Update
  int update(BreedFact... breedFacts);

  @Delete
  int delete(BreedFact... breedFacts);

  @Query("SELECT * FROM breed_fact WHERE breed_fact_id = :breedFactId")
  BreedFact findById(long breedFactId);

  @Query("SELECT * FROM breed_fact WHERE dog_facts_api_id = :dogFactsApiId")
  BreedFact findByDogFactsApiId(long dogFactsApiId);

  @Query("SELECT * FROM breed_fact WHERE name LIKE '%' || :nameFragment || '%' ORDER BY name")
  List<BreedFact> findByNameFragment(String nameFragment);

  @Query("SELECT * FROM breed_fact ORDER BY name")
  List<BreedFact> findAll();
}

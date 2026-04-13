package edu.cnm.deepdive.doggoneit.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import edu.cnm.deepdive.doggoneit.model.entity.BreedInfo;
import java.util.List;

@Dao
/**
 * DAO for querying and persisting {@link BreedInfo} rows.
 */
public interface BreedInfoDao {

  /**
   * Inserts one breed-info row.
   *
   * @param breedInfo Entity to insert.
   * @return Generated row id.
   */
  @Insert
  long insert(BreedInfo breedInfo);

  @Insert
  List<Long> insert(BreedInfo... breedInfo);

  /**
   * Updates existing breed-info rows.
   *
   * @param breedInfo Entities to update.
   * @return Number of rows updated.
   */
  @Update
  int update(BreedInfo... breedInfo);

  /**
   * Deletes breed-info rows.
   *
   * @param breedInfo Entities to delete.
   * @return Number of rows deleted.
   */
  @Delete
  int delete(BreedInfo... breedInfo);

  /**
   * Observes a breed-info row by local id.
   *
   * @param breedInfoId Local row id.
   * @return Live entity stream.
   */
  @Query("SELECT * FROM breed_info WHERE breed_info_id = :breedInfoId")
  LiveData<BreedInfo> findById(long breedInfoId);

  @Query("SELECT * FROM breed_info WHERE dog_api_breed_id = :dogApiBreedId")
  LiveData<BreedInfo> findByDogApiBreedId(long dogApiBreedId);

  @Query("SELECT * FROM breed_info WHERE dog_api_breed_id = :dogApiBreedId")
  BreedInfo findByDogApiBreedIdNow(long dogApiBreedId);

  @Query("SELECT * FROM breed_info WHERE name LIKE '%' || :nameFragment || '%' ORDER BY name")
  LiveData<List<BreedInfo>> findByNameFragment(String nameFragment);

  @Query("SELECT * FROM breed_info ORDER BY name")
  LiveData<List<BreedInfo>> findAll();
}

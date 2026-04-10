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
public interface BreedInfoDao {

  @Insert
  long insert(BreedInfo breedInfo);

  @Insert
  List<Long> insert(BreedInfo... breedInfo);

  @Update
  int update(BreedInfo... breedInfo);

  @Delete
  int delete(BreedInfo... breedInfo);

  @Query("SELECT * FROM breed_info WHERE breed_fact_id = :breedInfoId")
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

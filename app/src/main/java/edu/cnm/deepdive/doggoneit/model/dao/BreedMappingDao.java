package edu.cnm.deepdive.doggoneit.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import edu.cnm.deepdive.doggoneit.model.entity.BreedMapping;
import java.util.List;

@Dao
public interface BreedMappingDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  long insert(BreedMapping mapping);

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  List<Long> insert(BreedMapping... mappings);

  @Query("SELECT * FROM breed_mapping WHERE model_label = :modelLabel")
  LiveData<BreedMapping> findByModelLabel(String modelLabel);

  @Query("SELECT * FROM breed_mapping WHERE model_label = :modelLabel")
  BreedMapping findByModelLabelNow(String modelLabel);
}

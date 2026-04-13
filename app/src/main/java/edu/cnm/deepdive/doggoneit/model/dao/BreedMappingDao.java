package edu.cnm.deepdive.doggoneit.model.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import edu.cnm.deepdive.doggoneit.model.entity.BreedMapping;
import java.util.List;

@Dao
/**
 * DAO for persisted model-label to Dog API breed-id mappings.
 */
public interface BreedMappingDao {

  /**
   * Inserts or replaces one mapping row.
   *
   * @param mapping Mapping entity.
   * @return Row id.
   */
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  long insert(BreedMapping mapping);

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  List<Long> insert(BreedMapping... mappings);

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  List<Long> insertAll(List<BreedMapping> mappings);

  /**
   * @return Number of mapping rows currently stored.
   */
  @Query("SELECT COUNT(*) FROM breed_mapping")
  int count();

  /**
   * Observes a mapping by model label.
   *
   * @param modelLabel Model output label.
   * @return Live mapping stream.
   */
  @Query("SELECT * FROM breed_mapping WHERE model_label = :modelLabel")
  LiveData<BreedMapping> findByModelLabel(String modelLabel);

  @Query("SELECT * FROM breed_mapping WHERE model_label = :modelLabel")
  BreedMapping findByModelLabelNow(String modelLabel);
}

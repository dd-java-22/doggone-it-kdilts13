package edu.cnm.deepdive.doggoneit.service.seed;

import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import edu.cnm.deepdive.doggoneit.model.entity.BreedMapping;
import dagger.hilt.android.qualifiers.ApplicationContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Loads initial breed mapping rows from bundled JSON seed data.
 */
@Singleton
public class BreedMappingSeedLoader {

  private static final String TAG = BreedMappingSeedLoader.class.getSimpleName();
  private static final String ASSET_FILE = "breed_mapping.json";

  private final Context context;
  private final Gson gson;

  @Inject
  public BreedMappingSeedLoader(@ApplicationContext Context context) {
    this.context = context;
    gson = new Gson();
  }

  /**
   * Parses the seed asset into mapping entities, skipping invalid rows.
   *
   * @return Mapping rows ready for insertion, or an empty list on failure.
   */
  public List<BreedMapping> loadMappings() {
    try (
        InputStream input = context.getAssets().open(ASSET_FILE);
        Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8)
    ) {
      Type listType = new TypeToken<List<BreedMappingSeedDto>>() {
      }.getType();
      List<BreedMappingSeedDto> rows = gson.fromJson(reader, listType);
      if (rows == null || rows.isEmpty()) {
        return Collections.emptyList();
      }
      List<BreedMapping> mappings = new ArrayList<>(rows.size());
      for (BreedMappingSeedDto row : rows) {
        if (row == null || row.getModelLabel() == null || row.getModelLabel().isBlank()) {
          continue;
        }
        BreedMapping mapping = new BreedMapping();
        mapping.setModelLabel(row.getModelLabel());
        mapping.setDogApiBreedId(row.getDogApiBreedId());
        mappings.add(mapping);
      }
      return mappings;
    } catch (IOException | JsonParseException e) {
      Log.e(TAG, "Unable to load seed data from asset " + ASSET_FILE, e);
      return Collections.emptyList();
    }
  }
}

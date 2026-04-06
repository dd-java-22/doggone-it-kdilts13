package edu.cnm.deepdive.doggoneit.service;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import edu.cnm.deepdive.doggoneit.model.dao.BreedFactDao;
import edu.cnm.deepdive.doggoneit.model.dao.BreedPredictionDao;
import edu.cnm.deepdive.doggoneit.model.dao.ScanDao;
import edu.cnm.deepdive.doggoneit.model.dao.UserProfileDao;
import edu.cnm.deepdive.doggoneit.model.entity.BreedFact;
import edu.cnm.deepdive.doggoneit.model.entity.BreedPrediction;
import edu.cnm.deepdive.doggoneit.model.entity.Scan;
import edu.cnm.deepdive.doggoneit.model.entity.UserProfile;
import java.time.Instant;

@Database(
    entities = {UserProfile.class, Scan.class, BreedPrediction.class, BreedFact.class},
    version = DoggoneItDatabase.VERSION,
    exportSchema = true
)
@TypeConverters(DoggoneItDatabase.Converters.class)
public abstract class DoggoneItDatabase extends RoomDatabase {

  static final String DATABASE_NAME = "doggone_it";
  static final int VERSION = 2;

  public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
    @Override
    public void migrate(SupportSQLiteDatabase database) {
      database.execSQL(
          "ALTER TABLE breed_prediction ADD COLUMN rank INTEGER NOT NULL DEFAULT 0"
      );
    }
  };

  public abstract UserProfileDao getUserProfileDao();

  public abstract ScanDao getScanDao();

  public abstract BreedPredictionDao getBreedPredictionDao();

  public abstract BreedFactDao getBreedFactDao();

  public static class Converters {

    @TypeConverter
    public static Long instantToLong(Instant instant) {
      return (instant != null) ? instant.toEpochMilli() : null;
    }

    @TypeConverter
    public static Instant longToInstant(Long milliseconds) {
      return (milliseconds != null) ? Instant.ofEpochMilli(milliseconds) : null;
    }
  }
}

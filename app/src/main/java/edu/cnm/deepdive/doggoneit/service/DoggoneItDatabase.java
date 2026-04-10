package edu.cnm.deepdive.doggoneit.service;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import edu.cnm.deepdive.doggoneit.model.dao.BreedInfoDao;
import edu.cnm.deepdive.doggoneit.model.dao.BreedMappingDao;
import edu.cnm.deepdive.doggoneit.model.dao.BreedPredictionDao;
import edu.cnm.deepdive.doggoneit.model.dao.ScanDao;
import edu.cnm.deepdive.doggoneit.model.dao.UserProfileDao;
import edu.cnm.deepdive.doggoneit.model.entity.BreedInfo;
import edu.cnm.deepdive.doggoneit.model.entity.BreedMapping;
import edu.cnm.deepdive.doggoneit.model.entity.BreedPrediction;
import edu.cnm.deepdive.doggoneit.model.entity.Scan;
import edu.cnm.deepdive.doggoneit.model.entity.UserProfile;
import java.time.Instant;

@Database(
    entities = {UserProfile.class, Scan.class, BreedPrediction.class, BreedInfo.class,
        BreedMapping.class},
    version = DoggoneItDatabase.VERSION,
    exportSchema = true
)
@TypeConverters(DoggoneItDatabase.Converters.class)
public abstract class DoggoneItDatabase extends RoomDatabase {

  static final String DATABASE_NAME = "doggone_it";
  static final int VERSION = 4;

  public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
    @Override
    public void migrate(SupportSQLiteDatabase database) {
      database.execSQL(
          "ALTER TABLE breed_prediction ADD COLUMN rank INTEGER NOT NULL DEFAULT 0"
      );
    }
  };

  public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
    @Override
    public void migrate(SupportSQLiteDatabase database) {
      database.execSQL("PRAGMA foreign_keys=OFF");
      database.execSQL(
          "CREATE TABLE IF NOT EXISTS `breed_info` ("
              + "`breed_fact_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
              + "`dog_api_breed_id` INTEGER NOT NULL, "
              + "`name` TEXT COLLATE NOCASE, "
              + "`weight_metric` TEXT, "
              + "`weight_imperial` TEXT, "
              + "`height_metric` TEXT, "
              + "`height_imperial` TEXT, "
              + "`bred_for` TEXT, "
              + "`breed_group` TEXT, "
              + "`life_span` TEXT, "
              + "`temperament` TEXT, "
              + "`origin` TEXT, "
              + "`reference_image_id` TEXT, "
              + "`image_id` TEXT, "
              + "`image_width` INTEGER, "
              + "`image_height` INTEGER, "
              + "`image_url` TEXT)"
      );
      database.execSQL(
          "CREATE UNIQUE INDEX IF NOT EXISTS `index_breed_info_dog_api_breed_id` "
              + "ON `breed_info` (`dog_api_breed_id`)"
      );
      database.execSQL(
          "INSERT INTO `breed_info` ("
              + "`breed_fact_id`, `dog_api_breed_id`, `name`, `bred_for`, "
              + "`breed_group`, `life_span`, `temperament`, `origin`, "
              + "`reference_image_id`, `image_id`, `image_width`, `image_height`, `image_url`"
              + ") "
              + "SELECT "
              + "`breed_fact_id`, `dog_facts_api_id`, `name`, `bred_for`, "
              + "`breed_group`, `life_span`, `temperament`, `origin`, "
              + "`reference_image_id`, `image_id`, `image_width`, `image_height`, `image_url` "
              + "FROM `breed_fact`"
      );
      database.execSQL(
          "CREATE TABLE IF NOT EXISTS `breed_prediction_new` ("
              + "`breed_prediction_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
              + "`scan_id` INTEGER NOT NULL, "
              + "`breed_fact_id` INTEGER, "
              + "`name` TEXT COLLATE NOCASE NOT NULL, "
              + "`probability` REAL NOT NULL, "
              + "`rank` INTEGER NOT NULL, "
              + "FOREIGN KEY(`scan_id`) REFERENCES `scan`(`scan_id`) ON UPDATE NO ACTION ON DELETE CASCADE, "
              + "FOREIGN KEY(`breed_fact_id`) REFERENCES `breed_info`(`breed_fact_id`) ON UPDATE NO ACTION ON DELETE SET NULL)"
      );
      database.execSQL(
          "INSERT INTO `breed_prediction_new` ("
              + "`breed_prediction_id`, `scan_id`, `breed_fact_id`, `name`, `probability`, `rank`"
              + ") "
              + "SELECT "
              + "`breed_prediction_id`, `scan_id`, `breed_fact_id`, `name`, `probability`, `rank` "
              + "FROM `breed_prediction`"
      );
      database.execSQL("DROP TABLE `breed_prediction`");
      database.execSQL("ALTER TABLE `breed_prediction_new` RENAME TO `breed_prediction`");
      database.execSQL(
          "CREATE INDEX IF NOT EXISTS `index_breed_prediction_scan_id` "
              + "ON `breed_prediction` (`scan_id`)"
      );
      database.execSQL(
          "CREATE INDEX IF NOT EXISTS `index_breed_prediction_breed_fact_id` "
              + "ON `breed_prediction` (`breed_fact_id`)"
      );
      database.execSQL(
          "CREATE INDEX IF NOT EXISTS `index_breed_prediction_scan_id_rank` "
              + "ON `breed_prediction` (`scan_id`, `rank`)"
      );
      database.execSQL("DROP TABLE `breed_fact`");
      database.execSQL("PRAGMA foreign_keys=ON");
    }
  };

  public static final Migration MIGRATION_3_4 = new Migration(3, 4) {
    @Override
    public void migrate(SupportSQLiteDatabase database) {
      database.execSQL(
          "CREATE TABLE IF NOT EXISTS `breed_mapping` ("
              + "`model_label` TEXT COLLATE NOCASE NOT NULL, "
              + "`dog_api_breed_id` INTEGER NOT NULL, "
              + "PRIMARY KEY(`model_label`))"
      );
    }
  };

  public abstract UserProfileDao getUserProfileDao();

  public abstract ScanDao getScanDao();

  public abstract BreedPredictionDao getBreedPredictionDao();

  public abstract BreedInfoDao getBreedInfoDao();

  public abstract BreedMappingDao getBreedMappingDao();

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

package edu.cnm.deepdive.doggoneit.service;

import android.content.Context;
import androidx.room.Room;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import edu.cnm.deepdive.doggoneit.model.dao.BreedFactDao;
import edu.cnm.deepdive.doggoneit.model.dao.BreedPredictionDao;
import edu.cnm.deepdive.doggoneit.model.dao.ScanDao;
import edu.cnm.deepdive.doggoneit.model.dao.UserProfileDao;
import jakarta.inject.Singleton;

@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {

  @Provides
  @Singleton
  DoggoneItDatabase provideDatabase(@ApplicationContext Context context) {
    return Room.databaseBuilder(
      context,
      DoggoneItDatabase.class,
      DoggoneItDatabase.DATABASE_NAME
    ).addMigrations(DoggoneItDatabase.MIGRATION_1_2)
        .build();
  }

  @Provides
  @Singleton
  UserProfileDao provideUserProfileDao(DoggoneItDatabase database) {
    return database.getUserProfileDao();
  }

  @Provides
  @Singleton
  ScanDao provideScanDao(DoggoneItDatabase database) {
    return database.getScanDao();
  }

  @Provides
  @Singleton
  BreedPredictionDao provideBreedPrediction(DoggoneItDatabase database) {
    return database.getBreedPredictionDao();
  }

  @Provides
  @Singleton
  BreedFactDao provideBreedFact(DoggoneItDatabase database) {
    return database.getBreedFactDao();
  }

}

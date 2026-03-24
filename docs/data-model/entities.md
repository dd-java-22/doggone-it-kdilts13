---
title: Entity classes
description: Links to all entity classes in Github
order: 30
---

## Entity classes

The following Room entity classes correspond to the tables shown in the ERD.

- [UserProfile](https://github.com/dd-java-22/doggone-it-kdilts13/blob/main/app/src/main/java/edu/cnm/deepdive/doggoneit/model/entity/UserProfile.java)
- [Scan](https://github.com/dd-java-22/doggone-it-kdilts13/blob/main/app/src/main/java/edu/cnm/deepdive/doggoneit/model/entity/Scan.java)
- [BreedPrediction](https://github.com/dd-java-22/doggone-it-kdilts13/blob/main/app/src/main/java/edu/cnm/deepdive/doggoneit/model/entity/BreedPrediction.java)
- [BreedFact](https://github.com/dd-java-22/doggone-it-kdilts13/blob/main/app/src/main/java/edu/cnm/deepdive/doggoneit/model/entity/BreedFact.java)

## DAO interfaces

Each entity has a corresponding DAO interface defining its supported database operations.

- [UserProfileDao](https://github.com/dd-java-22/doggone-it-kdilts13/blob/main/app/src/main/java/edu/cnm/deepdive/doggoneit/model/dao/UserProfileDao.java)
- [ScanDao](https://github.com/dd-java-22/doggone-it-kdilts13/blob/main/app/src/main/java/edu/cnm/deepdive/doggoneit/model/dao/ScanDao.java)
- [BreedPredictionDao](https://github.com/dd-java-22/doggone-it-kdilts13/blob/main/app/src/main/java/edu/cnm/deepdive/doggoneit/model/dao/BreedPredictionDao.java)
- [BreedFactDao](https://github.com/dd-java-22/doggone-it-kdilts13/blob/main/app/src/main/java/edu/cnm/deepdive/doggoneit/model/dao/BreedFactDao.java)

## Database class

The Room database class defines the entities included in the schema and provides access to the DAO interfaces.

- [DoggoneItDatabase](https://github.com/dd-java-22/doggone-it-kdilts13/blob/main/app/src/main/java/edu/cnm/deepdive/doggoneit/service/DoggoneItDatabase.java)
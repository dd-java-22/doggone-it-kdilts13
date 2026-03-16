---
title: Data Model
description: "UML class diagram, entity-relationship diagram, and DDL."
order: 10
---

{% include ddc-abbreviations.md %}

## Page contents
{:.no_toc:}

- ToC
{:toc}

## UML class diagram

[![UML class diagram](img/doggone-it-uml.svg)](pdf/doggone-it-uml.pdf)

## Entity-relationship diagram

[![Entity-relationship diagram](img/doggone-it-erd.svg)](pdf/doggone-it-erd.pdf)

## DDL

{% include linked-file.md type="sqlite" file="sql/ddl.sql" %}

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

# AWS

We recommend hosting the server on AWS Elastic Beanstalk but you can use hosting service that supports Jar files.

1. First go to Releases and download the latest .jar file
2. Upload the .jar file to your service of choice.
3. You will need the following environment variables

| Name            | Value                                                                                  |
|-----------------|----------------------------------------------------------------------------------------|
| DEBUG           | true or false                                                                          |
| DATABASE_DRIVER | Based on your database type. For postgresql use `org.postgresql.ds.PGSimpleDataSource` |
| DB_USER         | Database user name                                                                     |
| DB_PASSWORD     | Database user password                                                                 |
| DATABASE_NAME   | Name of the database                                                                   |
| DATABASE_PORT   | Port of the database                                                                   |
| DATABASE_SERVER | Server url of the database                                                             |
| PORT            | Port that the server will attach to                                                    |
| RUN_API_KEY     | Api Key that server will authenticate against                                          |
| SECRET_KEY      | Key used to generate user passwords                                                    |
| DOCTOR_URL      | Url for Doctor Server                                                                  |



# Persistence post

http://www.jerolba.com/persistiendo-rapido-en-base-de-datos/

These instructions are written for *nix system.

## Prerequisites
You must have Docker installed (for the databases).


## How to run the benchmark yourself

1. Download the dataset
* `chmod +x downloadDataset.sh`
* `./downloadDataset.sh`

2. Generate the JAR and copy it to the root folder of the repo
* `gradle build`
* `cp ./build/libs/persistence-post.jar .`

3. Depending on the database you want to try:

    3.1. MySQL
    * Install the database client (in case you don't have it, just for checking the connection)
        * `sudo apt-get install postgresql-client`
        * `sudo apt-get install mysql-client`
    * Run a MySQL from a Docker
        * `docker run --name jerolba-mysql -e MYSQL_ROOT_PASSWORD=my-secret-pw -e MYSQL_DATABASE=testdb -e MYSQL_USER=test -e MYSQL_PASSWORD=test --publish 127.0.0.1:3306:3306 -d mysql:5.6.35`
        * Check that it's up and running (password 'my-secret-pw'): `mysql -u root -p`

    3.2. PostgreSQL
    * Install the database client (in case you don't have it, just for checking the connection)
        * `sudo apt-get install postgresql-client`
        * `sudo apt-get install mysql-client`
    * Run a PostgreSQL from a Docker
        * `docker run --name jerolba-postgres -e POSTGRES_USER=test -e POSTGRES_PASSWORD=test -e POSTGRES_DB=testdb --publish 127.0.0.1:5432:5432 -d postgres:9.6.8`
        * Check that it's up and running (password 'mysecretpassword'): `psql -h 127.0.0.1 -p 5432 -U postgres`

6. Run the benchmark
* `chmod +x run.sh`
* For MySQL: `./run.sh -m 127.0.0.1`
* For PostgreSQL: `./run.sh -p 127.0.0.1`

7. Check that the benchmark is running.
You should see some output here: `tail -f log/JdbcSimpleInsert_<db_type>_1000_1.log`, something similar to:
```
➜  persistence-post git:(master) ✗ tail -f log/JdbcSimpleInsert_postgres_1000_1.log
13:39:26.669 [main] INFO  c.j.b.shared.ConnectionProvider - Connecting with {urlConnection=jdbc:postgresql://127.0.0.1:5432/testdb, password=mysecretpassword, driver=org.postgresql.Driver, user=postgres}
13:39:54.086 [main] INFO  c.j.benchmark.shared.StreamCounter - Processed 10000 items in 27298 ms
13:40:21.100 [main] INFO  c.j.benchmark.shared.StreamCounter - Processed 20000 items in 54311 ms
```

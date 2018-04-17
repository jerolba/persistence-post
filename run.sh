#!/bin/bash
set -e

IP_POSTGRES="localhost"
USER_POSTGRES="test"
PASS_POSTGRES="test"
IP_MYSQL="localhost"
USER_MYSQL="test"
PASS_MYSQL="test"
 
TIMES=10

dbs=()

while getopts ":p:m:t:" opts
do
  case $opts in
	t)
		TIMES=$OPTARG
	    ;;
    p)
        echo "Postgres IP: $OPTARG"
		IP_POSTGRES=$OPTARG
		echo "driver=org.postgresql.Driver" > postgres.properties
		echo "user=$USER_POSTGRES" >> postgres.properties
		echo "password=$PASS_POSTGRES" >> postgres.properties
		echo "urlConnection=jdbc:postgresql://$IP_POSTGRES:5432/testdb" >> postgres.properties

        echo "driver=org.postgresql.Driver" > postgres2.properties
        echo "user=$USER_POSTGRES" >> postgres2.properties
        echo "password=$PASS_POSTGRES" >> postgres2.properties
        echo "urlConnection=jdbc:postgresql://$IP_POSTGRES:5432/testdb?reWriteBatchedInserts=true" >> postgres2.properties
		
		echo "$IP_POSTGRES:5432:testdb:$USER_POSTGRES:$PASS_POSTGRES" > ~/.pgpass
		chmod 600 ~/.pgpass
		dbs+=("postgres")
        ;;
	m)
        echo "MySQL IP: $OPTARG"
		IP_MYSQL=$OPTARG
		echo "driver=com.mysql.jdbc.Driver" > mysql.properties
		echo "user=$USER_MYSQL" >> mysql.properties
		echo "password=$PASS_MYSQL" >> mysql.properties
		echo "urlConnection=jdbc:mysql://$IP_MYSQL/testdb?useSSL=false" >> mysql.properties

        echo "driver=com.mysql.jdbc.Driver" > mysql2.properties
        echo "user=$USER_MYSQL" >> mysql2.properties
        echo "password=$PASS_MYSQL" >> mysql2.properties
        echo "urlConnection=jdbc:mysql://$IP_MYSQL/testdb?useSSL=false&rewriteBatchedStatements=true" >> mysql2.properties
		dbs+=("mysql")
        ;;		
  esac
done

echo "Number of times: $TIMES"

if [ ! -x "log" ]
then
	mkdir log
fi

#classes=("JpaSimpleInsert" "JpaSimpleInsertInBlocks" "JpaSimpleInsertInBlocksFlush" "JpaBatchIdentity" "JpaBatchInsert" "JpaBatchInsertStatementRewrite")
classes=("JdbcSimpleInsert" "JdbcSimpleInsertInBlocks" "JdbcBatchInsert" "JdbcBatchInsertStatementRewrite")
sizes=(1000)
for db in "${dbs[@]}"
do
	for i in `seq 1 $TIMES`;
	do
	    for class in "${classes[@]}"
        do
    		echo "Running $class for $db"
    		for size in "${sizes[@]}"
    		do
    			echo Iteration $i for size $size
    			java -cp ./persistence-post.jar com.jerolba.benchmark.$class $db.properties $size > log/$class\_$db\_$size\_$i.log
    			sleep 10
    		done
    	done
	done    
done


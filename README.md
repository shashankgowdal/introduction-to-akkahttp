Examples for Introduction to Akka HTTP.

# Build

    sbt clean package


# Akka Http with Spark

### Load a file 

curl -X POST -H "Content-Type: application/json" -d '{
	"path":"/Users/shashank/Personal/meetup/spark-meetup/Akka http/introduction-to-akkahttp/src/main/resources/sales.csv"
}' "http://localhost:8080/load"

### View data

curl -X GET "http://localhost:8080/view/664893820153605"
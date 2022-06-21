import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types._
import org.apache.spark.sql.functions._

object App {
    
    def main(args: Array[String]) : Unit = 
        
        val spark = SparkSession.builder
            .appName("Simple Application")
            .master("local[*]")
            .getOrCreate()
        
        import spark.implicits._

        val tweetSchema = StructType{Array(
                StructField("id", StringType, false),
                StructField("created_at", StringType, false),
                StructField("text", StringType, false)
            )
        }
        
        val getEntities = udf((text: String) => {
            requests
                .get(s"http://localhost:8000/entities", 
                     params = Map("text" -> text))
                .text()
        }, StringType)

        val df = spark
            .readStream
            .format("kafka")
            .option("kafka.bootstrap.servers", "localhost:9092")
            .option("subscribe", "tweets")
            .option("includeHeaders", "true")
            .option("startingOffsets", "earliest")
            .load()
        
        val parsed = df
            .select(from_json($"value".cast(StringType),tweetSchema).alias("parsed_json"))
            .select($"parsed_json.*")
            .withColumn("entities", getEntities($"text"))
        
        parsed.writeStream
            .format("json")
            .option("path", "streaming/")
            .option("checkpointLocation","checkpoints")
            .start()
            .awaitTermination()

        spark.stop()

}

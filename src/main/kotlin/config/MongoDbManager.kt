package cm.daccvo.config

import cm.daccvo.utils.Constants.AUTH_COLLECTION
import cm.daccvo.utils.Constants.AVIS_COLLECTION
import cm.daccvo.utils.Constants.DATABASE_NAME
import cm.daccvo.utils.Constants.MAX_POOL_SIZE
import cm.daccvo.utils.Constants.MONGO_HOST
import cm.daccvo.utils.Constants.MONGO_PASSWORD
import cm.daccvo.utils.Constants.MONGO_PORT
import cm.daccvo.utils.Constants.MONGO_USER
import cm.daccvo.utils.Constants.RECETTE_COLLECTION
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document

object MongoDbManager {

    private val database: MongoDatabase
    val users: MongoCollection<Document>
    val recette : MongoCollection<Document>
    val avisMongo: MongoCollection<Document>

    init {
        val credentials =
            if (MONGO_USER.isNotBlank() && MONGO_PASSWORD.isNotBlank())
                "$MONGO_USER:$MONGO_PASSWORD@"
            else ""

        val uri = "mongodb://$credentials$MONGO_HOST:$MONGO_PORT/?maxPoolSize=$MAX_POOL_SIZE&w=majority"
        val mongoClient = MongoClients.create(uri)
        database = mongoClient.getDatabase(DATABASE_NAME)
        users = database.getCollection(AUTH_COLLECTION)
        recette = database.getCollection(RECETTE_COLLECTION)
        avisMongo = database.getCollection(AVIS_COLLECTION)


    }
}
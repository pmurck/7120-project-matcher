package com.pmurck.projectMatcher

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import com.pmurck.projectMatcher.dao.DAO
import com.pmurck.projectMatcher.dao.mongo.DAOImpl
import io.ktor.application.*
import io.ktor.util.*
import org.koin.core.module.Module
import org.koin.dsl.module
import org.litote.kmongo.KMongo

@KtorExperimentalAPI
fun Application.koinModule(): Module = module {
    single<MongoClient> {
        with(environment.config.propertyOrNull("mongo.uri")) {
            if (this != null) {
                KMongo.createClient(this.getString())
            } else {
                KMongo.createClient() //"mongodb://localhost:27017"
            }
        }
    }
    single<MongoDatabase> {
        val client: MongoClient = get()
        client.getDatabase(environment.config.property("mongo.dbName").getString())
    }
    single<DAO> { DAOImpl(get()) }
}
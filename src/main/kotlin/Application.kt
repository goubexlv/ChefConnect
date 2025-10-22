package cm.daccvo

import cm.daccvo.config.MinioManager.makeBucketPublic
import cm.daccvo.plugins.configureAdministration
import cm.daccvo.plugins.configureFrameworks
import cm.daccvo.plugins.configureHTTP
import cm.daccvo.plugins.configureMonitoring
import cm.daccvo.plugins.configureRouting
import cm.daccvo.plugins.configureSecurity
import cm.daccvo.plugins.configureSerialization
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    //makeBucketPublic()
    configureAdministration()
    configureFrameworks()
    configureSerialization()
    configureMonitoring()
    configureSecurity()
    configureHTTP()
    configureRouting()
}

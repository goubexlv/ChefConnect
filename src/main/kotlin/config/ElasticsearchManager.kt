package cm.daccvo.config

import cm.daccvo.utils.Constants.ELASTIC_HOST
import cm.daccvo.utils.Constants.ELASTIC_PORT
import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.json.jackson.JacksonJsonpMapper
import co.elastic.clients.transport.rest_client.RestClientTransport
import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
import java.util.concurrent.TimeUnit

object ElasticsearchManager {

    fun createClient(): ElasticsearchClient {
        val restClient = RestClient.builder(HttpHost(ELASTIC_HOST, ELASTIC_PORT))
            .setRequestConfigCallback { requestConfigBuilder ->
                requestConfigBuilder
                    .setConnectTimeout(5000)    // Temps d'attente pour établir la connexion (5 secondes)
                    .setSocketTimeout(60000)    // Temps d'attente pour la réponse (60 secondes)
            }
            .setHttpClientConfigCallback { httpClientBuilder ->
                httpClientBuilder
                    .setMaxConnTotal(200)       // Nombre total de connexions
                    .setMaxConnPerRoute(100)    // Connexions par route
                    .setKeepAliveStrategy { _, _ -> TimeUnit.MINUTES.toMillis(5) }
            }
            .build()

        val transport = RestClientTransport(
            restClient,
            JacksonJsonpMapper()
        )

        return ElasticsearchClient(transport)
    }
}
package api.azure.msv_wikimedia_producer.stream;

import api.azure.msv_wikimedia_producer.producer.WikimediaProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class WikimediaStreamConsumer {

    private final WebClient webClient;
    private final WikimediaProducer producer;

    public WikimediaStreamConsumer(WebClient.Builder webClientBuilder, WikimediaProducer producer) {
        this.webClient = webClientBuilder
                .baseUrl("https://stream.wikimedia.org/v2")
                .build();
        this.producer = producer;
    }

    public void consumerStreamAndPublish() {
        webClient.get()
                .uri("/stream/recentchange")
                .retrieve()
                .bodyToFlux(String.class)
//                .doOnNext(event -> {
//                    // Manejo de cada evento recibido
//                    System.out.println("Evento recibido: " + event);
//                    // Enviar a Kafka u otra lógica de procesamiento
//                })
                .doOnError(error -> System.err.println("Error en el stream: " + error.getMessage()))
                // .subscribe(log::info);
                .retry() // Reintenta automáticamente si ocurre algún error
                .subscribe(producer::sendMessage);
    }
}

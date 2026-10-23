package gt.umg.ml.practicaspring.ia;

import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ia")
public class IaController {

    private final EmbeddingModel embeddingModel;   // bge-m3, creado por la auto-configuración
    private final ChatModel chatModel;             // llama3.1:8b, creado por la auto-configuración
    private final VectorStore vectorStore;         // pgvector, creado por la auto-configuración

    public IaController(EmbeddingModel embeddingModel, ChatModel chatModel,
                        VectorStore vectorStore) {
        this.embeddingModel = embeddingModel;
        this.chatModel = chatModel;
        this.vectorStore = vectorStore;
    }

    // 1. Un embedding: texto → vector
    @GetMapping("/embedding")
    public Resultados.EmbeddingInfo embedding(@RequestParam String texto) {
        float[] vector = embeddingModel.embed(texto);
        List<Float> primeros = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            primeros.add(vector[i]);
        }
        return new Resultados.EmbeddingInfo(texto, vector.length, primeros);
    }

    // 2. El LLM solo, sin RAG
    @GetMapping("/chat")
    public String chat(@RequestParam String pregunta) {
        Prompt prompt = new Prompt(List.of(
                new SystemMessage("Responde en español, de forma breve."),
                new UserMessage(pregunta)));
        return chatModel.call(prompt).getResult().getOutput().getText();
    }

    // 3. Flujo A en miniatura: guardar fragmentos en pgvector
    @PostMapping("/cargar")
    public String cargar() {
        List<Document> documentos = List.of(
                new Document("Tarifa vigente del servicio 1/4 de paja: Q25.00 por periodo. "
                        + "Incluye 15,000 litros de consumo.", Map.of("fuente", "tarifas")),
                new Document("Tarifa vigente por consumo en exceso: Q3.00 por cada 1,000 "
                        + "litros que superen lo incluido en el servicio.", Map.of("fuente", "tarifas")),
                new Document("Si tienes una fuga dentro de tu casa, cierra la llave de paso "
                        + "y revisa llaves, inodoros y tuberías.", Map.of("fuente", "fugas")),
                new Document("El partido de fútbol del domingo terminó empatado.",
                        Map.of("fuente", "ruido")));
        vectorStore.add(documentos);   // calcula 4 embeddings con bge-m3 y los inserta
        return "Se cargaron " + documentos.size() + " fragmentos";
    }

    // 4. Búsqueda semántica: top-k + umbral
    @GetMapping("/buscar")
    public List<Resultados.Fragmento> buscar(@RequestParam String pregunta,
                                             @RequestParam(defaultValue = "4") int topK,
                                             @RequestParam(defaultValue = "0.0") double umbral) {
        return vectorStore.similaritySearch(SearchRequest.builder()
                        .query(pregunta)
                        .topK(topK)
                        .similarityThreshold(umbral)
                        .build())
                .stream()
                .map(d -> new Resultados.Fragmento(d.getText(), d.getScore(), d.getMetadata().get("fuente")))
                .toList();
    }

    // 5. Mini-RAG: recuperar + aumentar + generar
    @GetMapping("/rag")
    public Resultados.RespuestaRag rag(@RequestParam String pregunta) {
        // RECUPERAR
        List<Document> fragmentos = vectorStore.similaritySearch(SearchRequest.builder()
                .query(pregunta).topK(3).similarityThreshold(0.3).build());

        List<Resultados.Fragmento> usados = fragmentos.stream()
                .map(d -> new Resultados.Fragmento(d.getText(), d.getScore(), d.getMetadata().get("fuente")))
                .toList();

        if (fragmentos.isEmpty()) {
            return new Resultados.RespuestaRag(pregunta, usados,
                    "No tengo información sobre eso. Comunícate con la Oficina del Agua.");
        }

        // AUMENTAR
        String contexto = fragmentos.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n---\n"));
        String mensajeUsuario = "INFORMACIÓN DE REFERENCIA:\n" + contexto
                + "\n\nPREGUNTA:\n" + pregunta;

        // GENERAR
        Prompt prompt = new Prompt(List.of(
                new SystemMessage("Eres el asistente de la Oficina del Agua. Usa SOLO la "
                        + "información de referencia. Si no está ahí, dilo. Nunca inventes "
                        + "montos. Responde en español y de forma breve."),
                new UserMessage(mensajeUsuario)));
        String respuesta = chatModel.call(prompt).getResult().getOutput().getText();

        return new Resultados.RespuestaRag(pregunta, usados, respuesta);
    }
}

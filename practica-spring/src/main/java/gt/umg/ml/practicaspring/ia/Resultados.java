package gt.umg.ml.practicaspring.ia;

import java.util.List;

public class Resultados {
    public record EmbeddingInfo(String texto, int dimensiones, List<Float> primeros5) {}
    public record Fragmento(String texto, Double similitud, Object fuente) {}
    public record RespuestaRag(String pregunta, List<Fragmento> fragmentosUsados, String respuesta) {}
}

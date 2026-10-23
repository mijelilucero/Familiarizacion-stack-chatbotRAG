package gt.umg.ml.practicaspring.saludo;

public class ConfiguracionGlobal {

    // 2. La única instancia, guardada en un atributo estático
    private static ConfiguracionGlobal instancia;

    private final String nombreSistema;

    // 1. Constructor privado: nadie puede hacer "new ConfiguracionGlobal()" desde afuera
    private ConfiguracionGlobal() {
        this.nombreSistema = "Oficina del Agua";
    }

    // 3. Punto de acceso: siempre devuelve la misma instancia
    public static ConfiguracionGlobal getInstance() {
        if (instancia == null) {               // la primera vez la crea
            instancia = new ConfiguracionGlobal();
        }
        return instancia;                      // las demás veces devuelve la misma
    }

    public String getNombreSistema() { return nombreSistema; }
}

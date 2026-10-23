package gt.umg.ml.practicaspring.saludo;

import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api")
public class SaludoController {

    private final SaludoService saludoService;
    private final DateTimeFormatter formatoHora;

    public SaludoController(SaludoService saludoService, DateTimeFormatter formatoHora) {   // inyección por constructor
        this.saludoService = saludoService;
        this.formatoHora = formatoHora;
    }

    @GetMapping("/saludo")
    public SaludoResponse saludo(@RequestParam(defaultValue = "visitante") String nombre) {
        return new SaludoResponse(saludoService.saludar(nombre), LocalTime.now().format(formatoHora));
    }

    @PostMapping("/eco")
    public SaludoResponse eco(@RequestBody EcoRequest request) {
        return new SaludoResponse("Recibí tu pregunta: " + request.pregunta(),
                LocalTime.now().format(formatoHora));
    }

    @GetMapping("/instancia")
    public String instancia() {
        return "SaludoController usa usa Configuracion id = " + System.identityHashCode(ConfiguracionGlobal.getInstance());
    }
}

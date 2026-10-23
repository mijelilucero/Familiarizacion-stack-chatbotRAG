package gt.umg.ml.practicaspring.saludo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OtroController {

    private final SaludoService saludoService;

    public OtroController(SaludoService saludoService) {
        this.saludoService = saludoService;
    }

    @GetMapping("/api/otro/instancia")
    public String instancia() {
        return "OtroController usa Configuracion id = " + System.identityHashCode(ConfiguracionGlobal.getInstance());
    }
}

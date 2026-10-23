package gt.umg.ml.practicaspring.saludo;

import gt.umg.ml.practicaspring.api.SaludoApi;
import gt.umg.ml.practicaspring.api.model.SaludoRequest;
import gt.umg.ml.practicaspring.api.model.SaludoRespuesta;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@RestController
public class SaludoV2Controller implements SaludoApi {

    private final SaludoService saludoService;
    private final DateTimeFormatter formatoHora;

    public SaludoV2Controller(SaludoService saludoService, DateTimeFormatter formatoHora) {
        this.saludoService = saludoService;
        this.formatoHora = formatoHora;
    }

    @Override
    public ResponseEntity<SaludoRespuesta> crearSaludo(SaludoRequest saludoRequest) {
        SaludoRespuesta respuesta = new SaludoRespuesta()
                .mensaje(saludoService.saludar(saludoRequest.getNombre()))
                .hora(LocalTime.now().format(formatoHora));
        return ResponseEntity.ok(respuesta);
    }
}

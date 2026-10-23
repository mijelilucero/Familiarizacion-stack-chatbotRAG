package gt.umg.ml.practicaspring.saludo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SaludoService {

    private final String saludo;

    public SaludoService(@Value("${app.saludo}") String saludo) {
        this.saludo = saludo;
        System.out.println(">>> Creando SaludoService, id = " + System.identityHashCode(this));
    }

    public String saludar(String nombre) {
        return saludo + ", " + nombre + "!";
    }
}

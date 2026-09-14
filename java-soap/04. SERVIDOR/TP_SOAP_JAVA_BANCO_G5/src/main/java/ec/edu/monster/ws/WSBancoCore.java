package ec.edu.monster.ws;

import ec.edu.monster.modelo.RespuestaCredito;
import ec.edu.monster.servicio.BancoCoreService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

@WebService(serviceName = "WSBancoCore")
public class WSBancoCore {

    private final BancoCoreService service = new BancoCoreService();

    @WebMethod(operationName = "verificarYCrearCredito")
    public RespuestaCredito verificarYCrearCredito(
            @WebParam(name = "cedula") String cedula,
            @WebParam(name = "montoCompra") double montoCompra,
            @WebParam(name = "plazoMeses") int plazoMeses) {
        return service.verificarYCrearCredito(cedula, montoCompra, plazoMeses);
    }
}

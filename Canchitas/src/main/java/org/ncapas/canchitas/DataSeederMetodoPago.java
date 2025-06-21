package org.ncapas.canchitas;
import lombok.RequiredArgsConstructor;
import org.ncapas.canchitas.entities.MetodoPago;
import org.ncapas.canchitas.repositories.MetodoPagoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeederMetodoPago implements CommandLineRunner {

    private final MetodoPagoRepository metodoPagoRepo;

    @Override
    public void run(String... args) throws Exception {
        if (metodoPagoRepo.count() == 0) {
            metodoPagoRepo.save(
                    MetodoPago.builder()
                            .metodoPago(MetodoPago.Metodo.TARJETA_DEBITO)
                            .build()
            );
            metodoPagoRepo.save(
                    MetodoPago.builder()
                            .metodoPago(MetodoPago.Metodo.TARJETA_CREDITO)
                            .build()
            );
            System.out.println("Métodos de pago precargados!");
        }
    }
}

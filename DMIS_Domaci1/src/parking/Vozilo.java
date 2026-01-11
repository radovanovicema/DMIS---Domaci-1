package parking;

import parking.Izuzeci.NeispravnaZonaException;

public final class Vozilo implements Tarifirano {

    private final int tarifnaZona;

    public Vozilo(int tarifnaZona) {
        if (tarifnaZona <= 0) {
            throw new NeispravnaZonaException(
                "Tarifna zona mora biti pozitivan ceo broj."
            );
        }
        this.tarifnaZona = tarifnaZona;
    }

    @Override
    public int dohvatiTarifnuZonu() {
        return tarifnaZona;
    }
}

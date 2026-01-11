package parking;

import java.util.Arrays;
import parking.Izuzeci.NeispravnaZonaException;
import parking.Izuzeci.NeispravanArgumentException;

public final class Tarifnik {

    private final int[] cene; // indeks 0 = zona 1

    public Tarifnik(int[] cenePoZonama) {
        if (cenePoZonama == null) {
            throw new NeispravanArgumentException("Niz cena ne sme biti null.");
        }
        if (cenePoZonama.length == 0) {
            throw new NeispravanArgumentException("Tarifnik mora imati bar jednu zonu.");
        }
        for (int i = 0; i < cenePoZonama.length; i++) {
            if (cenePoZonama[i] < 0) {
                throw new NeispravanArgumentException("Cena za zonu " + (i + 1) + " ne sme biti negativna.");
            }
        }
        this.cene = Arrays.copyOf(cenePoZonama, cenePoZonama.length);
    }

    public int dohvatiBrojZona() {
        return cene.length;
    }

    public int dohvatiCenu(int zona) {
        if (zona < 1 || zona > dohvatiBrojZona()) {
            throw new NeispravnaZonaException(
                    "Tarifna zona " + zona + " ne postoji. Dozvoljeno: 1.." + dohvatiBrojZona()
            );
        }
        return cene[zona - 1];
    }
}

package parking;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import parking.Izuzeci.NeispravnaZonaException;
import parking.Izuzeci.TarifnikNedefinisanException;
import parking.Izuzeci.NeispravanBrojSatiException;

public final class ParkingAutomat {

    private static final AtomicInteger ID_GEN = new AtomicInteger(0);

    private final int id;
    private volatile Tarifnik tarifnik;
    private final AtomicLong naplaceno;

    public ParkingAutomat() {
        this.id = ID_GEN.incrementAndGet();
        this.tarifnik = null;
        this.naplaceno = new AtomicLong(0);
    }

    private ParkingAutomat(Tarifnik tarifnik) {
        this.id = ID_GEN.incrementAndGet();
        this.tarifnik = tarifnik;
        this.naplaceno = new AtomicLong(0);
    }

    public void postaviTarifnik(Tarifnik noviTarifnik) {
        if (noviTarifnik == null) {
            throw new IllegalArgumentException("Tarifnik ne sme biti null.");
        }
        this.tarifnik = noviTarifnik;
        this.naplaceno.set(0);
    }

    public ParkingAutomat kopija() {
        return new ParkingAutomat(this.tarifnik);
    }

    public long naplati(Tarifirano vozilo, int brojZapocetihSati) {
        if (brojZapocetihSati <= 0) {
            throw new NeispravanBrojSatiException("Broj započetih sati mora biti pozitivan.");
        }
        if (vozilo == null) {
            throw new IllegalArgumentException("Vozilo ne sme biti null.");
        }

        Tarifnik t = this.tarifnik;
        if (t == null) {
            throw new TarifnikNedefinisanException("U trenutku naplate nije definisan tarifnik.");
        }

        int zona = vozilo.dohvatiTarifnuZonu();
        if (zona < 1 || zona > t.dohvatiBrojZona()) {
            throw new NeispravnaZonaException(
                    "Tarifna zona vozila " + zona + " je van dozvoljenog opsega (1.." + t.dohvatiBrojZona() + ")."
            );
        }

        long iznos = (long) t.dohvatiCenu(zona) * (long) brojZapocetihSati;
        naplaceno.addAndGet(iznos);
        return iznos;
    }

    public long dohvatiUkupnoNapalceno() {
        return naplaceno.get();
    }

    @Override
    public String toString() {
        return id + "(" + dohvatiUkupnoNapalceno() + ")";
    }
}

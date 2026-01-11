package parking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import parking.Izuzeci.NeispravanArgumentException;
import parking.Izuzeci.PogresnoStanjeZoneException;

public final class AktivnaParkingZona {

    private enum Stanje { NAPRAVLJENA, OTVORENA, ZATVORENA, UNISTENA }

    private final String naziv;
    private final int brojAutomata;
    private final long tsrMillis;
    private final List<ParkingAutomat> automati;

    private final Random rng;
    private final Object lock = new Object();

    private volatile Stanje stanje = Stanje.NAPRAVLJENA;
    private volatile boolean stop = false;
    private Thread generator = null;

    private static final int MAX_SATI = 8;

    public AktivnaParkingZona(String naziv, int brojAutomata, long tsrMillis, ParkingAutomat prototip, long seed) {
        if (naziv == null || naziv.isBlank()) {
            throw new NeispravanArgumentException("Naziv zone ne sme biti prazan.");
        }
        if (brojAutomata <= 0) {
            throw new NeispravanArgumentException("Broj automata mora biti pozitivan.");
        }
        if (tsrMillis <= 0) {
            throw new NeispravanArgumentException("tsr mora biti pozitivan (u ms).");
        }
        if (prototip == null) {
            throw new NeispravanArgumentException("Prototip automata ne sme biti null.");
        }

        this.naziv = naziv;
        this.brojAutomata = brojAutomata;
        this.tsrMillis = tsrMillis;
        this.rng = new Random(seed);

        List<ParkingAutomat> tmp = new ArrayList<>(brojAutomata);
        tmp.add(prototip);
        for (int i = 1; i < brojAutomata; i++) {
            tmp.add(prototip.kopija());
        }
        this.automati = Collections.unmodifiableList(tmp);
    }

    public void otvori(Tarifnik noviTarifnik) {
        if (noviTarifnik == null) throw new IllegalArgumentException("Tarifnik ne sme biti null.");

        synchronized (lock) {
            if (stanje == Stanje.UNISTENA) {
                throw new PogresnoStanjeZoneException("Zona je uništena i ne može se otvoriti.");
            }
            if (stanje == Stanje.OTVORENA) {
                throw new PogresnoStanjeZoneException("Zona je već otvorena.");
            }

            for (ParkingAutomat a : automati) {
                a.postaviTarifnik(noviTarifnik);
            }

            stop = false;
            stanje = Stanje.OTVORENA;

            generator = new Thread(() -> runSimulacija(noviTarifnik), "Generator-" + naziv);
            generator.start();
        }
    }

    public void zatvori() {
        Thread t;
        synchronized (lock) {
            if (stanje == Stanje.UNISTENA) {
                throw new PogresnoStanjeZoneException("Zona je uništena.");
            }
            if (stanje != Stanje.OTVORENA) {
                throw new PogresnoStanjeZoneException("Zona nije otvorena, ne može se zatvoriti.");
            }
            stop = true;
            stanje = Stanje.ZATVORENA;
            t = generator;
        }

        if (t != null) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Prekinuto čekanje na gašenje simulacije.", e);
            }
        }
    }

    public void unisti() {
        synchronized (lock) {
            if (stanje == Stanje.UNISTENA) {
                throw new PogresnoStanjeZoneException("Zona je već unistena.");
            }
        }

        synchronized (lock) {
            if (stanje == Stanje.OTVORENA) {
                // izlazimo iz lock-a i pozivamo zatvori()
            } else {
                stanje = Stanje.UNISTENA;
                return;
            }
        }

        zatvori();

        synchronized (lock) {
            stanje = Stanje.UNISTENA;
        }
    }

    public long dohvatiUkupnoNapalcenoOdOtvaranja() {
        synchronized (lock) {
            if (stanje == Stanje.UNISTENA) {
                throw new PogresnoStanjeZoneException("Zona je unistena.");
            }
            if (stanje == Stanje.NAPRAVLJENA) {
                return 0;
            }
        }

        long sum = 0;
        for (ParkingAutomat a : automati) {
            sum += a.dohvatiUkupnoNapalceno();
        }
        return sum;
    }

    private void runSimulacija(Tarifnik tarifnik) {
        while (!stop) {
            try {
                Thread.sleep(slucajanInterval());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
           if (stop) break;

            int zona = 1 + rng.nextInt(tarifnik.dohvatiBrojZona());
            Vozilo v = new Vozilo(zona);

            if (stop) break;

            ParkingAutomat a = automati.get(rng.nextInt(brojAutomata));

            if (stop) break;

            int sati = 1 + rng.nextInt(MAX_SATI);

            if (stop) break;

            a.naplati(v, sati);
        }
    }

    private long slucajanInterval() {
        double faktor = 0.7 + rng.nextDouble() * 0.6;
        long ms = (long) Math.max(1, Math.round(tsrMillis * faktor));
        return ms;
    }

    @Override
    public String toString() {
        long naplaceno = dohvatiUkupnoNapalcenoOdOtvaranja();
        StringBuilder sb = new StringBuilder();
        sb.append(naziv).append("(").append(naplaceno).append("): ");
        for (int i = 0; i < automati.size(); i++) {
            sb.append(automati.get(i));
            if (i < automati.size() - 1) sb.append(", ");
        }
        return sb.toString();
    }
}

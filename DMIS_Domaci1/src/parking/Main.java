package parking;

public final class Main {

    private static final long SEED = 20251227L;
    private static final int BROJ_AUTOMATA = 5;
    private static final long TSR_MS = 800;

    private static final long VREME_DO_PRVOG_ISPISA_MS = 4000;
    private static final long VREME_DO_ZATVARANJA_MS = 4000;

    public static void main(String[] args) throws InterruptedException {

        ParkingAutomat prototip = new ParkingAutomat();

        AktivnaParkingZona zona = new AktivnaParkingZona(
                "Zona-1",
                BROJ_AUTOMATA,
                TSR_MS,
                prototip,
                SEED
        );

        Tarifnik tarifnik = new Tarifnik(new int[]{50, 80, 120});

        zona.otvori(tarifnik);

        Thread.sleep(VREME_DO_PRVOG_ISPISA_MS);
        System.out.println("STANJE 1 (zona otvorena):");
        System.out.println(zona);

        Thread.sleep(VREME_DO_ZATVARANJA_MS);
        zona.zatvori();

        System.out.println("\nSTANJE 2 (zona zatvorena):");
        System.out.println(zona);

        zona.unisti();
        System.out.println("\nZona je uništena.");
    }
}

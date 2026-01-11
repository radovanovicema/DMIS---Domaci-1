# Parking Zona Simulator

## Opis arhitekture

**Parking zona simulator** implementiran kroz sledeće klase:

- **`Tarifirano`** - interfejs koji definiše objekte sa tarifnom zonom
- **`Vozilo`** - implementira `Tarifirano`, ima fiksnu tarifnu zonu
- **`Tarifnik`** - immutable struktura cena po zonama
- **`ParkingAutomat`** - thread-safe automat sa jedinstvenim ID-em (`AtomicInteger`), tarifnikom i naplaćenim iznosom (`AtomicLong`)
- **`AktivnaParkingZona`** - upravlja životnim ciklusom zone i pokretanjem generatorske niti za simulaciju dolazaka vozila
- **`Izuzeci`** - domenski izuzeci sa jasnim porukama

**Životni ciklus zone:** NAPRAVLJENA → OTVORENA → ZATVORENA → UNIŠTENA
---

## Primer izlaza programa
STANJE 1 (zona otvorena): Zona-1(1920): 1(480), 2(0), 3(640), 4(320), 5(480)

STANJE 2 (zona zatvorena): Zona-1(3840): 1(800), 2(560), 3(1040), 4(640), 5(800)
Zona je uništena.

**Objašnjenje:**
- Ukupan naplaćen iznos je u zagradi pored naziva zone
- Svaki automat prikazuje `ID(naplaćeno)`
- STANJE 2 ima veće iznose jer je prošlo više vremena i stiglo više vozila
- Rezultati su deterministički (fiksni seed:  20251227L)


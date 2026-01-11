/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package parking;

/**
 *
 * @author Ema
 */
public final class Izuzeci {
    private Izuzeci() {}

    @SuppressWarnings("serial")
	public static class NeispravnaZonaException extends RuntimeException {
        public NeispravnaZonaException(String msg) { super(msg); }
    }

    @SuppressWarnings("serial")
	public static class TarifnikNedefinisanException extends RuntimeException {
        public TarifnikNedefinisanException(String msg) { super(msg); }
    }

    @SuppressWarnings("serial")
	public static class NeispravanBrojSatiException extends RuntimeException {
        public NeispravanBrojSatiException(String msg) { super(msg); }
    }

    @SuppressWarnings("serial")
	public static class PogresnoStanjeZoneException extends RuntimeException {
        public PogresnoStanjeZoneException(String msg) { super(msg); }
    }

    @SuppressWarnings("serial")
	public static class NeispravanArgumentException extends RuntimeException {
        public NeispravanArgumentException(String msg) { super(msg); }
    }
}

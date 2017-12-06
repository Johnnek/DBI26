/**
 * Klasse Timer zur Zeiterfassung des Benchmarks
 *
 */
public class Timer{
	/**
	 * Variable, die vom System die Zeit in Millisekunden speichert
	 */
	long millis;
	
	/**
	 * Funktion zum Starten der Zeiterfassung
	 */
	void start() {
		millis = System.currentTimeMillis();
	}
	
	/**
	 * Funktion zum Stoppen der Zeiterfassung
	 */
	double stop() {
		millis = System.currentTimeMillis() - millis;
		double dmillis = (double)millis/1000;
		return dmillis;
	}
}
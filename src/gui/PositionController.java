/**
 * 
 */
package gui;

import java.math.BigDecimal;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Die Klasse PositionController realisiert den eigentlichen Regelalgorithmus
 * der Gleichlaufregelung. Der Algorithmus wird durch Beauftragung der 
 * doControl()-Methode realisiert. Die notwendigen Parameter werden zuvor
 * gesetzt.
 * 
 * @author Detlef Tribius
 */
public class PositionController
{

    /**
     * logger...
     */
    private final static Logger logger = LoggerFactory.getLogger(PositionController.class);    
    
    /**
     * Darstellung der Taktzeit...
     */
    public static int SCALE_CYCLE_TIME = 3;
    
    /**
     * SCALE_OUTPUT = 3, Genauigkeit fuer die Ausgabewerte (Nachkommastellen)
     */
    public static int SCALE_OUTPUT = 3;
    
    /**
     * 
     */
    public static int SCALE_INTERN = 6;
    
    /**
     * Zeitpunkt der aktuellen Beauftragung...
     */
    private Instant now = null;
    
    /**
     * Zeitpunkt der letzten Beauftragung...
     */
    private Instant past = null;
    
    /**
     * cycleTimeDecimal - Zykluszeit (Abtast-/Regler-Zeit)
     */
    private BigDecimal cycleTimeDecimal = BigDecimal.ZERO;    
    
    /**
     * enhancement - Reglerverstaerkung...
     * <p>
     * Die Reglerverstaerkung kann ueber die Oberflaeche geaendert werden...
     * </p>
     */
    private BigDecimal enhancement;
    
    /**
     * boolean isMAControlled - die Regelung wirkt auf den Motor A
     */
    private boolean isMAControlled;
    
    /**
     * boolean isMBControlled - die Regelung wirkt auf den Motor B
     */
    private boolean isMBControlled;
    
    /**
     * wheelSteps - Anzahl der Impulse des Gebers pro Umdrehung
     */
    private final int wheelSteps;
    
    /**
     * PositionController(int wheelSteps)
     * @param wheelSteps: Anzahl der Impulse des Gebers pro Umdrehung
     */
    PositionController(int wheelSteps)
    {
        // Es ist wahrscheinlich sinnvoll zu fordern, dass 
        // wheelSteps >= 1 gelten muss! 
        // (Da evtl. Division durch wheelSteps notwendig wird!)
        this.wheelSteps = (wheelSteps > 1)? wheelSteps : 1;
        this.enhancement = BigDecimal.ZERO;
    }
    
    /**
     * setEnhancement(BigDecimal enhancement) - Reglerverstaerkung setzen
     * @param enhancement
     */
    public void setEnhancement(BigDecimal enhancement)
    {
        this.enhancement = (enhancement != null)? enhancement : BigDecimal.ZERO;
    }
    
    /**
     * getEnhancement() - Reglerverstaerkung
     * @return
     */
    public BigDecimal getEnhancement()
    {
        return this.enhancement;
    }
    
    /**
     * isMAControlled()
     * @return the isMAControlled
     */
    public final boolean isMAControlled()
    {
        return isMAControlled;
    }

    /**
     * setMAControlled(boolean isMAControlled)
     * @param isMAControlled the isMAControlled to set
     */
    public final void setMAControlled(boolean isMAControlled)
    {
        this.isMAControlled = isMAControlled;
    }

    /**
     * isMBControlled()
     * @return the isMBControlled
     */
    public final boolean isMBControlled()
    {
        return isMBControlled;
    }

    /**
     * setMBControlled(boolean isMBControlled)
     * @param isMBControlled the isMBControlled to set
     */
    public final void setMBControlled(boolean isMBControlled)
    {
        this.isMBControlled = isMBControlled;
    }

    /**
     * doControl() - Regelalgorithmus...
     * <p>
     * Der Regelalgorithmus realisiert einen einfachen P-Regler der Lage. Die Verstaerkung
     * ergibt sich zu (Verstaerkung an Oberflaeche) / (Impulse pro Umdrehung) * Lagedifferenz.
     * Das Ergebnis (Reglerausgang) wird wahlweise zum Sollwert addiert oder subtrahiert.
     * Soll keine Auswirkung auf einen Motor vorliegen (Checkbox nicht gesetzt), so eeinflusst
     * die Reglerdifferenz nicht den entsprechenden Sollwert. 
     * <p>
     * @return Output(output)
     */
    public Output doControl(BigDecimal valueMA, long numberMA, 
                            BigDecimal valueMB, long numberMB)
    {
        // diffNumber: Lage-Differenz zwischen MA und MB...
        final long diffNumber = numberMA - numberMB;
        // Faktor P-Anteil (p_factor) ergibt sich aus der Verstaerkung an der Oberflaeche dividiert durch Impulse pro Umdrehung:
        final BigDecimal p_factor = this.enhancement.divide(BigDecimal.valueOf(this.wheelSteps), SCALE_INTERN, BigDecimal.ROUND_CEILING);
        // diffValue - Reglerausgangs
        final BigDecimal diffValue = BigDecimal.valueOf(diffNumber).multiply(p_factor).setScale(SCALE_OUTPUT, BigDecimal.ROUND_HALF_UP);
        // Aufschalten des Reglerausgangs?
        final BigDecimal outputMA = this.isMAControlled? valueMA.subtract(diffValue) : valueMA.setScale(SCALE_OUTPUT, BigDecimal.ROUND_HALF_UP);
        
        final BigDecimal outputMB = this.isMBControlled? valueMB.add(diffValue) : valueMB.setScale(SCALE_OUTPUT, BigDecimal.ROUND_HALF_UP);
        
        return new Output(diffValue, outputMA, outputMB);
    }
    
    /**
     * Output - Zusammenfassung des Reglerausgangs
     * 
     * @author Detlef Tribius
     *
     */
    final class Output
    {
        /**
         * BigDecimal diffValue
         */
        private final BigDecimal diffValue;
        
        /**
         * BigDecimal outputMA
         */
        private final BigDecimal outputMA;
        
        /**
         * BigDecimal outputMB
         */
        private final BigDecimal outputMB; 
        
        /**
         * Output() - Konstruktor aus den Attributen...
         * @param diffValue
         * @param outputMA
         * @param outputMB
         */
        public Output(BigDecimal diffValue, 
                      BigDecimal outputMA,      
                      BigDecimal outputMB)
        {
            this.diffValue = (diffValue != null)? diffValue : BigDecimal.ZERO;
            this.outputMA = (outputMA != null)? outputMA : BigDecimal.ZERO.setScale(SCALE_OUTPUT);
            this.outputMB = (outputMB != null)? outputMB : BigDecimal.ZERO.setScale(SCALE_OUTPUT);
        }

        /**
         * @return the diffValue
         */
        public final BigDecimal getDiffValue()
        {
            return diffValue;
        }

        /**
         * @return the outputMA
         */
        public final BigDecimal getOutputMA()
        {
            return outputMA;
        }

        /**
         * @return the outputMB
         */
        public final BigDecimal getOutputMB()
        {
            return outputMB;
        }

        /**
         * String toString() - zu Protokollzwecken...
         */
        @Override
        public String toString()
        {
            return new StringBuilder().append("[")
                                      .append(this.diffValue.toString())
                                      .append(" ")
                                      .append(this.outputMA.toString())
                                      .append(" ")
                                      .append(this.outputMB.toString())
                                      .append("]")
                                      .toString();
        }
    }
}

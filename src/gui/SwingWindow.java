package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.math.BigDecimal;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.JTextComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Das SwingWindow ist ein JFrame (GUI-Swing-Klasse).
 * Es gestaltet die GUI mit einer Liste und den 
 * entsprechenden Buttons.
 * </p>
 * <p>
 * Die Datenhaltung erfolgt im Model.
 * </p> 
 * <p>
 * Vgl.: https://dbs.cs.uni-duesseldorf.de/lehre/docs/java/javabuch/html/k100242.html<br>
 * Auch: http://www.willemer.de/informatik/java/guimodel.htm<br>
 * </p>
 * <p>
 * Radio-Button: http://www.fredosaurus.com/notes-java/GUI/components/50radio_buttons/25radiobuttons.html
 * </p>
 * @author Detlef Tribius
 *
 */
public class SwingWindow extends JFrame implements View   
{
    /**
     * serialVersionUID = 1L - durch Eclipse generiert...
     */
    private static final long serialVersionUID = 1L;

    /**
     * logger - Instanz zur Protokollierung...
     */
    private final static Logger logger = LoggerFactory.getLogger(SwingWindow.class);      

    /**
     * textComponentMap - nimmt die Controls zur Darstellung der Daten (hier JTextField) auf...
     */
    private final java.util.Map<String, JTextComponent> textComponentMap = new java.util.TreeMap<>();
    
    /**
     * comboBoxMap - nimmt die Controls vom Typ JComboBox<> auf (Vereinfachung, value vom Typ BigDecimal)...
     */
    private final java.util.Map<String, JComboBox<BigDecimal>> comboBoxMap = new java.util.TreeMap<>();
    
    /**
     * checkBoxMap - nimmt die Controls vom Typ JCheckBox auf...
     */
    private final java.util.Map<String, JCheckBox> checkBoxMap = new java.util.TreeMap<>();
  
    /**
     * isDestinationSimultan - boolsche Kennung, die Sollwerte werden simultan gesetzt...
     */
    private boolean isDestinationSimultan = false;
    
    /**
     * TEXT_FIELD - Kennung fuer ein Textfeld...
     */
    public final static String TEXT_FIELD = JTextField.class.getCanonicalName();
    
    /**
     * COMBO_BOX - Kennung fuer eine ComboBox...
     */
    private static final String COMBO_BOX = JComboBox.class.getCanonicalName();    
    
    /**
     * CHECK_BOX - Kennung fuer eine CheckBox...
     */
    private static final String CHECK_BOX = JCheckBox.class.getCanonicalName();
    
    /**
     * controlData - Beschreibungsdaten der Oberflaechenelemente...
     */
    public final static String[][] controlData = new String[][]
    {
        {TEXT_FIELD,    Data.COUNTER_KEY,               "Counter"             },        
        {TEXT_FIELD,    Data.CYCLE_TIME_KEY,            "Zyklusdauer (in s)"  },
        {TEXT_FIELD,    Data.TOKEN_KEY,                 "Token"               },
        {CHECK_BOX,     Model.DESTINATION_SIMULTAN_KEY, "Sollwerte Motor A/B simultan?" },
        {COMBO_BOX,     Model.VALUE_MA_KEY,             "Sollwert Motor A"    },
        {COMBO_BOX,     Model.VALUE_MB_KEY,             "Sollwert Motor B"    },
        {TEXT_FIELD,    Data.NUMBER_MA_KEY,             "Position Motor A"    },
        {TEXT_FIELD,    Data.NUMBER_MB_KEY,             "Position Motor B"    },
        {CHECK_BOX,     Model.CONTROL_MA_KEY,           "Reglerausgang auf Motor A?"},
        {TEXT_FIELD,    Data.OUTPUT_MA_KEY,             "Stellgroesse Motor A"},
        {CHECK_BOX,     Model.CONTROL_MB_KEY,           "Reglerausgang auf Motor B?"},
        {TEXT_FIELD,    Data.OUTPUT_MB_KEY,             "Stellgroesse Motor B"},
        {CHECK_BOX,     Model.CONTROL_KEY,              "Mit Regelung?"       },
        {COMBO_BOX,     Model.ENHANCEMENT_KEY,          "Verstärkung"         }
    };
    
    /**
     * 
     */
    private ActionListener actionListener = null; 
    
    /**
     * Start-Button...
     */
    private final JButton startButton = new JButton("Start");
    
    /**
     * Stop-Button...
     */
    private final JButton stopButton = new JButton("Stop");
    
    /**
     * Reset-Button...
     */
    private final JButton resetButton = new JButton("Reset");
    
    /**
     * Ende-Button... beendet die Anwendung
     */
    private final JButton endButton = new JButton("Ende");

    /**
     * 
     */
    private final JButton buttons[] = new JButton[] 
    { 
        startButton,
        stopButton,
        resetButton,
        endButton
    };
    
    /**
     * jContentPane - Referenz auf das Haupt-JPanel 
     */
    private JPanel jContentPane = null;
    
    /**
     * This is the default constructor
     */
    public SwingWindow(Model model)
    {
        super();
        initialize();
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent event)
            {
                logger.debug("windowClosing(WindowEvent)...");
                model.shutdown();
                System.exit(0);
            }
        });
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize()
    {
        this.setSize(450, 250);
        this.setContentPane(getJContentPane());
        this.setTitle( "Schwebe-Regelung-Motor A/B" );
        this.startButton.setName(Model.NAME_START_BUTTON);
        this.stopButton.setName(Model.NAME_STOP_BUTTON);
        this.resetButton.setName(Model.NAME_RESET_BUTTON);
        this.endButton.setName(Model.NAME_END_BUTTON);
    }

    /**
     * This method initializes jContentPane
     * 
     * getJContentPane() - Methode baut das SwingWindow-Fenster auf.
     * Es werden alle sichtbaren Komponenten instanziiert.
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane()
    {
        if (jContentPane == null)
        {
            jContentPane = new JPanel();
            // BorderLayout hat die Bereiche
            // BorderLayou.NORTH
            // BorderLayout.CENTER
            // BorderLayout.SOUTH
            jContentPane.setLayout(new BorderLayout(10, 10));
            
            {   // NORTH
                JPanel northPanel = new JPanel();
                northPanel.setLayout(new BoxLayout(northPanel, javax.swing.BoxLayout.Y_AXIS));
                
                // northPanel wird in den Bereich NORTH eingefuegt.
                jContentPane.add(northPanel, BorderLayout.NORTH);
            }
            
            { // WEST
                // leeres Panel (Platzhalter)...
                jContentPane.add(new JPanel(), BorderLayout.WEST);
            }
            
            { // EAST
                // leeres Panel (Platzhalter)...
                jContentPane.add(new JPanel(), BorderLayout.EAST);
            }
            
            {   // CENTER
                // Struktur: centerPanel als BoxLayout, Ausrichtung von oben nach unten.
                // Jede Zelle erneut als BoxLayout von links nach rechts.
                JPanel centerPanel = new JPanel();
                centerPanel.setLayout(new BoxLayout(centerPanel, javax.swing.BoxLayout.Y_AXIS));
                
                for(String[] controlParam: SwingWindow.controlData)
                {
                    final String controlType = controlParam[0];
                    final String controlId = controlParam[1];
                    final String labelText = controlParam[2];
                    {
                        JPanel controlPanel = new JPanel();
                        controlPanel.setLayout(new BoxLayout(controlPanel, javax.swing.BoxLayout.X_AXIS));
                        controlPanel.add(Box.createHorizontalGlue());
                        controlPanel.add(new JLabel(labelText));
                        controlPanel.add(Box.createRigidArea(new Dimension(10, 0)));
                    
                        if (TEXT_FIELD.equals(controlType))
                        {
                            // Jetzt ein TextField instanziieren...
                            JTextField controlTextField = new JTextField(10);
                            controlTextField.setMaximumSize(new Dimension(100, controlTextField.getMinimumSize().height));
                            this.textComponentMap.put(controlId, controlTextField);
                            controlTextField.setEditable(false);
                            controlPanel.add(controlTextField);
                            controlPanel.add(Box.createRigidArea(new Dimension(4, 0)));
                            centerPanel.add(controlPanel);
                        } // end() TEXT_FIELD.
                        
                        if (COMBO_BOX.equals(controlType) && Model.VALUE_MA_KEY.equals(controlId)
                         || COMBO_BOX.equals(controlType) && Model.VALUE_MB_KEY.equals(controlId)     )
                        {
                            // Jetzt eine ComboBox mit Vorgabe Motor A oder Motor B instanziieren...
                            // (Dabei Mitgabe moeglicher Sollwerte als Auswahlelemente der ComboBox.)
                            JComboBox<BigDecimal> valueMXComboBox = new JComboBox<>(Model.MX_VALUES);
                            valueMXComboBox.setName(controlId);
                            valueMXComboBox.setMaximumSize(new Dimension(100, valueMXComboBox.getMinimumSize().height));
                            // Die Box wird etwas vergroessert... 
                            valueMXComboBox.setPreferredSize(new Dimension(80, valueMXComboBox.getMinimumSize().height));
                            this.comboBoxMap.put(controlId, valueMXComboBox); 
                            controlPanel.add(valueMXComboBox);
                            controlPanel.add(Box.createRigidArea(new Dimension(4, 0)));
                            centerPanel.add(controlPanel);
                            
                            // Selektion des Eintrages mit BigDecimal.ZERO...
                            valueMXComboBox.setSelectedIndex(Model.SELECTED_MX_VALUES_INDEX); 
                            
                            valueMXComboBox.addActionListener(new ActionListener() 
                            {
                                @Override
                                @SuppressWarnings("unchecked")
                                public void actionPerformed(ActionEvent event)
                                {
                                    JComboBox<BigDecimal> source = (JComboBox<BigDecimal>)event.getSource();   
                                    logger.info(source.getName() + ": " + event.getActionCommand());   
                                    
                                    actionCommandDelegate(event);
                                }
                            });                            
                        } // end() COMBO_BOX.
                        
                        if (COMBO_BOX.equals(controlType) && Model.ENHANCEMENT_KEY.equals(controlId))
                        {
                            // Jetzt die Combobox mit den moeglichen Regler-Verstaerkungen instanziieren...
                            JComboBox<BigDecimal> enhancementsComboBox = new JComboBox<>(Model.ENHANCEMENTS);
                            enhancementsComboBox.setName(controlId);
                            enhancementsComboBox.setMaximumSize(new Dimension(100, enhancementsComboBox.getMinimumSize().height));
                            this.comboBoxMap.put(controlId, enhancementsComboBox); 
                            controlPanel.add(enhancementsComboBox);
                            controlPanel.add(Box.createRigidArea(new Dimension(4, 0)));
                            centerPanel.add(controlPanel);
                            
                            // Selektion des Eintrages mit BigDecimal.ZERO...
                            enhancementsComboBox.setSelectedIndex(Model.SELECTED_ENHANCEMENTS_INDEX);
                            
                            enhancementsComboBox.addActionListener(new ActionListener() 
                            {
                                @Override
                                @SuppressWarnings("unchecked")
                                public void actionPerformed(ActionEvent event)
                                {
                                    JComboBox<BigDecimal> source = (JComboBox<BigDecimal>)event.getSource();   
                                    logger.info(source.getName() + ": " + event.getActionCommand());   
                                    
                                    actionCommandDelegate(event);
                                }
                            });
                        }
                        
                        if (CHECK_BOX.equals(controlType) && Model.CONTROL_KEY.equals(controlId)
                         || CHECK_BOX.equals(controlType) && Model.DESTINATION_SIMULTAN_KEY.equals(controlId)
                         || CHECK_BOX.equals(controlType) && Model.CONTROL_MA_KEY.equals(controlId)
                         || CHECK_BOX.equals(controlType) && Model.CONTROL_MB_KEY.equals(controlId))                                
                        {
                            JCheckBox controlCheckBox = new JCheckBox();
                            controlCheckBox.setName(controlId);
                            this.checkBoxMap.put(controlId, controlCheckBox);
                            controlPanel.add(controlCheckBox);
                            controlPanel.add(Box.createRigidArea(new Dimension(4, 0)));
                            centerPanel.add(controlPanel);
                            
                            controlCheckBox.addItemListener(new ItemListener() 
                            {
                                @Override
                                public void itemStateChanged(ItemEvent event)
                                {
                                    JCheckBox source = (JCheckBox) event.getSource();
                                    
                                    final boolean isDeSelected = (event.getStateChange() == ItemEvent.DESELECTED);
                                    final boolean isSelected = (event.getStateChange() == ItemEvent.SELECTED);
                                    
                                    final String eventMsg = (isDeSelected? "deselected" : "")
                                                          + (isSelected? "selected" : "")
                                                          + ((!isDeSelected && !isSelected)? "?" : "");
                                    logger.info(source.getName() + ": " + eventMsg); 
                                    
                                    itemStateChangedDelegate(event);                                    
                                }
                            });
                        }
                    }

                    {
                        // Leerzeile...
                        JPanel emptyPanel = new JPanel();
                        emptyPanel.setLayout(new BoxLayout(emptyPanel, javax.swing.BoxLayout.Y_AXIS));
                        emptyPanel.add(Box.createRigidArea(new Dimension(0, 4)));
                        centerPanel.add(emptyPanel);
                    }
                }
                
                jContentPane.add(centerPanel, BorderLayout.CENTER);
            }
            
            {   // SOUTH...
                // buttonPanel beinhaltet die Button...
                JPanel buttonPanel = new JPanel();
                FlowLayout flowLayout = (FlowLayout) buttonPanel.getLayout();
                flowLayout.setAlignment(FlowLayout.RIGHT);
            
                for(JButton button: buttons)
                {
                    button.setHorizontalAlignment(SwingConstants.RIGHT);
                    button.addActionListener(new ActionListener()
                    {
                        @Override
                        public void actionPerformed(ActionEvent event)
                        {
                            final JButton source = (JButton)event.getSource();
                            logger.debug(source.getName());
                            //
                            actionCommandDelegate(event);
                        }
                    });
                    //
                    buttonPanel.add(button);
                }
                
                jContentPane.add(buttonPanel, BorderLayout.SOUTH);
            }
        }
        return jContentPane;
    }

    @Override
    public void addActionListener(ActionListener listener)
    {
        logger.debug("Controller hinzugefuegt (ActionListener)...");
        this.actionListener = listener;
    }

    /**
     * propertyChange(PropertyChangeEvent event) - wird vom Model her beaufragt
     * und muss die View evtl. nachziehen...  
     */
    @Override
    public void propertyChange(PropertyChangeEvent event)
    {
        final String propertyName = event.getPropertyName();
        final Object newValue = event.getNewValue();
        
        if (Model.DATA_KEY.equals(propertyName) && (newValue instanceof Data))
        {
            final Data data = (Data) newValue;
            for( String key: data.getKeys())
            {
                if (this.textComponentMap.containsKey(key))
                {
                    final JTextComponent textComponent = this.textComponentMap.get(key);
                    textComponent.setText(data.getValue(key));
                    continue;
                }
            }
        }
        
        // ComboBox...
        if (Model.VALUE_MA_KEY.equals(propertyName) 
         || Model.VALUE_MB_KEY.equals(propertyName)
         || Model.ENHANCEMENT_KEY.equals(propertyName))
        {
            // propertyChange vom Model her mit VALUE_MA_KEY oder VALUE_MB_KEY...
            final BigDecimal newData = (BigDecimal) newValue;
            
            if (this.comboBoxMap.containsKey(propertyName))
            {
                JComboBox<BigDecimal> valueComboBox = this.comboBoxMap.get(propertyName);
                // => Achtung!!
                // => newData muss mit seScale(...) des Listeneintrages uebereinstimmen!
                valueComboBox.setSelectedItem(newData);
                logger.debug(propertyName + ": " + newValue);
            }
        }
        if (this.isDestinationSimultan && Model.VALUE_MA_KEY.equals(propertyName))
        {
            if (this.comboBoxMap.containsKey(propertyName))
            {
                JComboBox<BigDecimal> valueComboBox = this.comboBoxMap.get(propertyName);
                final int index = valueComboBox.getSelectedIndex();
                JComboBox<BigDecimal> otherComboBox = this.comboBoxMap.get(Model.VALUE_MB_KEY);
                otherComboBox.setSelectedIndex(index);
            }
        }
        if (this.isDestinationSimultan && Model.VALUE_MB_KEY.equals(propertyName))
        {
            if (this.comboBoxMap.containsKey(propertyName))
            {
                JComboBox<BigDecimal> valueComboBox = this.comboBoxMap.get(propertyName);
                final int index = valueComboBox.getSelectedIndex();
                JComboBox<BigDecimal> otherComboBox = this.comboBoxMap.get(Model.VALUE_MA_KEY);
                otherComboBox.setSelectedIndex(index);
            }
        }
        
        if (Model.DESTINATION_SIMULTAN_KEY.equals(propertyName)
         || Model.CONTROL_KEY.equals(propertyName))
        {
            final boolean isSelected = Boolean.TRUE.equals(newValue);
            
            if (this.checkBoxMap.containsKey(propertyName))
            {
                JCheckBox checkBox = this.checkBoxMap.get(propertyName);
                checkBox.setSelected(isSelected);
                logger.debug(propertyName + ": " + (isSelected? "selected" : "deselected"));
            }
            // Hier Zustandsgroesse this.isDestinationSimultan zur  
            // GUI-Steuerung (Sonderloesung Sollwerte synchronisieren) ablegen...
            if (Model.DESTINATION_SIMULTAN_KEY.equals(propertyName))
            {
                this.isDestinationSimultan = isSelected;
            }
        }
        
        if (Model.CONTROL_MA_KEY.equals(propertyName)
         || Model.CONTROL_MB_KEY.equals(propertyName))
        {
            final boolean isSelected = Boolean.TRUE.equals(newValue);
            if (this.checkBoxMap.containsKey(propertyName))
            {
                JCheckBox checkBox = this.checkBoxMap.get(propertyName);
                checkBox.setSelected(isSelected);
                logger.debug(propertyName + ": " + (isSelected? "selected" : "deselected"));
            }
        }
        
        // Buttonsteuerung...
        // => Initial sind derzeit alle Button enabled!
        if (Model.GUI_STATUS_KEY.equals(propertyName) && newValue instanceof Model.GuiStatus)
        {
            final Model.GuiStatus guiStatus = (Model.GuiStatus) newValue;
            
            this.startButton.setEnabled(guiStatus == Model.GuiStatus.INIT
                                     || guiStatus == Model.GuiStatus.STOP);
            
            this.stopButton.setEnabled(guiStatus == Model.GuiStatus.START);
            
            // Reset-Button hat das gleiche Enabled-Verhalten wie der start-Button!
            this.resetButton.setEnabled(guiStatus == Model.GuiStatus.INIT
                                     || guiStatus == Model.GuiStatus.STOP);
            
            this.endButton.setEnabled(!(guiStatus == Model.GuiStatus.END));
            
            if (guiStatus == Model.GuiStatus.START)
            {
                logger.debug("Start...");
            }
        }
        ////////////////////////////////////////////////////////////////////
        // Evtl. Kontrollausgabe im Debuglevel...
        // logger.debug(event.toString());
        ////////////////////////////////////////////////////////////////////
    }

    /**
     * 
     * @param event
     */
    private void actionCommandDelegate(java.awt.event.ActionEvent event) 
    {                                       
        if (this.actionListener != null) 
        {
            this.actionListener.actionPerformed(event);
        }
    }
    
    /**
     * 
     * @param event
     */
    private void itemStateChangedDelegate(ItemEvent event)
    {
        if (this.actionListener != null)
        {
            JCheckBox source = (JCheckBox) event.getSource();
            
            final String name = source.getName();
            
            this.actionListener.actionPerformed(new ActionEvent(source,
                                                                ActionEvent.ACTION_PERFORMED,
                                                                name));
        }
    }
}

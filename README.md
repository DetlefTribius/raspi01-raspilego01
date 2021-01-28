# raspi01-raspilego01
Lageregelung Raspberry-Arduino-Lego-Motor
Die Lageregelung basiert auf folgender Hardware
1. Raspberry Pi
2. Arduino, kommuniziert mittels I2C mit dem Raspberry, der Raspberry ist der Master, wird aber durch den Arduino im festen Takt angestossen.
3. Angeschlossen am Raspberry Pi ist ein Motor_Driver_HAT (Waveshare), welcher die Ansteuerung zweier (Lego-) Motoren vornimmt.
4. Die Lageinformationen werden ueber Impulsgeber gewonnen und in den Arduino eingelesen (Interrupt-gesteuert). Der Arduino uebertraegt die Lageinformationen zum Raspberry, der auch den Regelalgorithmus implementiert.
5. Eine Swing-Anwendung ermoeglicht die Steuerung der Lageregelung (Regelung der relativen Lage der Motoren).
Die Java-Anwendung wird in Eclipse realisiert, Ant-Skripte ermoeglichen finale Builds.

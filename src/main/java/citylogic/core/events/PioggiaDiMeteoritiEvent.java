package citylogic.core.events;

import citylogic.domain.map.UrbanGrid;
import citylogic.domain.state.StatoCitta;
import citylogic.domain.entities.UrbanEntity;
import citylogic.domain.map.Cell;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PioggiaDiMeteoritiEvent extends RandomEvent {
    
    public PioggiaDiMeteoritiEvent(StatoCitta stato, UrbanGrid griglia) {
        super("Pioggia di Meteoriti", 2); // Dura 2 tick per mostrare l'effetto
        distruggiEdifici(stato, griglia);
    }

    private void distruggiEdifici(StatoCitta stato, UrbanGrid griglia) {
        Random rnd = new Random();
        List<Cell> celleOccupate = new ArrayList<>();
        
        for (int i = 0; i < griglia.getWidth(); i++) {
            for (int j = 0; j < griglia.getHeight(); j++) {
                Cell cella = griglia.getCell(i, j);
                if (cella.isOccupied()) {
                    celleOccupate.add(cella);
                }
            }
        }
        
        int numeroMeteoriti = 1 + rnd.nextInt(3); 
        for (int i = 0; i < numeroMeteoriti && !celleOccupate.isEmpty(); i++) {
            int bersaglioIndex = rnd.nextInt(celleOccupate.size());
            Cell bersaglio = celleOccupate.remove(bersaglioIndex);
            
            UrbanEntity distrutta = bersaglio.getEntity();
            if (distrutta != null) {
                griglia.removeEntity(distrutta.getX(), distrutta.getY());
            }
        }
    }

    @Override
    public void applyModifiers(StatoCitta stato, citylogic.domain.state.TickStats stats) {
        stato.setFelicita(stato.getFelicita() - 15.0);
        stato.setSicurezza(stato.getSicurezza() - 15.0);
    }
}

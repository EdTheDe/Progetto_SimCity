import java.util.List;

public class SimulationEngine {
    private StatoCitta stato;
    private List<Edificio> grigliaEdifici;
    private List<PoliticaStrategy> politicheAttive;

    public SimulationEngine(StatoCitta stato, List<Edificio> griglia, List<PoliticaStrategy> politiche) {
        this.stato = stato;
        this.grigliaEdifici = griglia;
        this.politicheAttive = politiche;
    }

    public void tick() {
        // 1. Variabili di accumulo temporaneo per il tick corrente
        int capacitaAbitativa = 0;
        int postiLavoro = 0;
        int puntiInquinamento = 0;
        double redditoCommerciale = 0.0;
        int puntiSicurezza = 0;
        int puntiSanita = 0;

        // 2. Lettura dei contributi dalla griglia
        for (Edificio edificio : grigliaEdifici) {
            if (!edificio.isValid()) continue; // Salta edifici senza servizi base

            double valoreBase = edificio.getLivello() * 10.0;

            switch (edificio.getTipo()) {
                case RESIDENZIALE: 
                    capacitaAbitativa += (int) valoreBase; 
                    break;
                case INDUSTRIALE: 
                    postiLavoro += (int) valoreBase;
                    puntiInquinamento += (int) valoreBase; 
                    break;
                case COMMERCIALE: 
                    redditoCommerciale += valoreBase; 
                    break;
                case POLIZIA: 
                    puntiSicurezza += (int) valoreBase; 
                    break;
                case OSPEDALE:
                case SCUOLA: 
                    puntiSanita += (int) valoreBase; 
                    break;
                default:
                    break;
            }
        }

        double modFelicita = calcolaModificatoreFelicita();

        // 3. Gestione Dinamica Popolazione
        // La popolazione cresce se c'è spazio, in base alla felicità
        if (stato.getPopolazione() < capacitaAbitativa) {
            int crescita = (int) Math.ceil((capacitaAbitativa - stato.getPopolazione()) * 0.2 * modFelicita);
            stato.setPopolazione(stato.getPopolazione() + Math.max(1, crescita));
        } else if (stato.getPopolazione() > capacitaAbitativa) {
            // Emigrazione immediata se mancano case
            stato.setPopolazione(capacitaAbitativa); 
        }

        int popAttiva = Math.max(1, stato.getPopolazione()); // Evita divisioni per 0

        // 4. Ricalcolo Metriche di Base (Copertura % rispetto alla popolazione)
        stato.setLavoro(((double) postiLavoro / popAttiva) * 100.0);
        stato.setSicurezza((((double) puntiSicurezza * modFelicita) / popAttiva) * 100.0);
        stato.setSanita((((double) puntiSanita * modFelicita) / popAttiva) * 100.0);
        
        // L'ecologia diminuisce in base all'inquinamento per abitante (formula calibrabile)
        stato.setEcologia(100.0 - (((double) puntiInquinamento / popAttiva) * 30.0));

        // 5. Entrate Commerciali
        stato.addFinanze(redditoCommerciale * modFelicita);

        // 6. Applicazione Malus e Dinamiche Globali
        applicaDinamicheGlobali();

        // 7. Applicazione Strategy (Politiche Cittadine)
        for (PoliticaStrategy politica : politicheAttive) {
            politica.applicaModificatori(stato);
        }
    }

    private void applicaDinamicheGlobali() {
        // Regola richiesta: Sanità diminuisce se ecologia è sotto il 60
        if (stato.getEcologia() < 60) {
            stato.setSanita(stato.getSanita() - 15.0); 
        }

        // Regola richiesta: Ricalcolo della felicità basato sui 4 parametri
        double mediaServizi = (stato.getEcologia() + stato.getLavoro() + stato.getSicurezza() + stato.getSanita()) / 4.0;
        double penalita = 0.0;

        if (stato.getEcologia() < 60) penalita += 10.0;
        if (stato.getLavoro() < 60) penalita += 10.0;
        if (stato.getSicurezza() < 60) penalita += 10.0;
        if (stato.getSanita() < 60) penalita += 10.0;

        stato.setFelicita(mediaServizi - penalita);
    }

    private double calcolaModificatoreFelicita() {
        // La felicità (0-100) viene mappata come modificatore da 0.5 (pessimo) a 1.5 (ottimo)
        return 0.5 + (stato.getFelicita() / 100.0);
    }
}

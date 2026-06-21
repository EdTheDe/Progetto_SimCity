```mermaid
classDiagram

  %% =====================
  %% DEFINIZIONE CLASSI
  %% =====================

  class AssetManager {
    -Map~String, Image~ imageCache$
    -double CELL_SIZE$
    +Image ottieniImmagine(UrbanEntity e, int x, int y, UrbanGrid grigliaLogica)$
  }

  class BuilderValidator {
    -List~RegolaCostruzione~ regole
    +BuilderValidator(UrbanGrid griglia)
    +void validaCostruzione(UrbanEntity entita, Cell cella, StatoCitta stato)
  }

  class Building {
    <<abstract>>
    -double energyDemand
    -double waterDemand
    +Building(double placementCost, double energyDemand, double waterDemand)
    +double getEnergyDemand()
    +double getWaterDemand()
  }

  class Cell {
    -int x
    -int y
    -UrbanEntity entity
    +Cell(int x, int y)
    +int getX()
    +int getY()
    +boolean isOccupied()
    +UrbanEntity getEntity()
    +void setEntity(UrbanEntity entity)
    +void clearEntity()
  }

  class CityApp {
    -StatoCitta statoCitta
    -UrbanGrid grigliaLogica
    -BuilderValidator validatore
    -SimulationEngine motore
    +void main(String[] args)$
  }

  class CityObserver {
    <<interface>>
    ~void onSimulationUpdated(StatoCitta stato)
    +void onEventStarted(String eventName, String description)
    +void onGameOver()
  }

  class Commercial {
    -double baseIncome
    +Commercial(double placementCost, double energyDemand, double waterDemand, double baseIncome)
    +double getBaseIncome()
  }

  class CostruzioneException {
    +CostruzioneException(String message)
  }

  class CrisiEconomicaEvent {
    +CrisiEconomicaEvent()
  }

  class FireStation {
    +FireStation(double placementCost, double baseMaintenanceCost, int baseFirefightingCapacity)
  }

  class GestoreEventiUI {
    +void mostraEvento(String eventName, String description, TimeBar timeBarRef, Window owner)$
    +void mostraGameOver()$
  }

  class GreenArea {
    +GreenArea(double placementCost, double baseMaintenanceCost, int basePollutionReduction)
    +int getPollutionReduction()
  }

  class GuerraEvent {
    +GuerraEvent()
  }

  class Hospital {
    +Hospital(double placementCost, double baseMaintenanceCost, int baseHealthCapacity)
  }

  class Industrial {
    -double basePollution
    -int baseJobsProduced
    +Industrial(double placementCost, double energyDemand, double waterDemand, double basePollution, int baseJobsProduced)
    +double getBasePollution()
    +int getJobsProduced()
  }

  class Infrastructure {
    <<abstract>>
    -double baseMaintenanceCost
    -int baseMaxCapacity
    +Infrastructure(double placementCost, double baseMaintenanceCost, int baseMaxCapacity)
    +double getMaintenanceCost()
    +int getMaxCapacity()
  }

  class MappaGriglia {
    -UrbanGrid grigliaLogica
    -BuilderValidator validatore
    -StatoCitta statoCitta
    -TopBar topBarRef
    -double CELL_SIZE$
    -String tipoEdificioSelezionato
    +MappaGriglia(UrbanGrid logica, BuilderValidator val, StatoCitta stato, TopBar topBar)
    +void setTipoEdificioSelezionato(String tipo)
    +void rinfrescaMappaCompleta()
    -void mostraMenuContestuale(Cell cella, StackPane nodoVisivo, int x, int y, double screenX, double screenY)
  }

  class MenuImpostazioni {
    -UrbanGrid logica
    -StatoCitta stato
    -MappaGriglia mappaVisiva
    -SimulationEngine engine
    -TopBar topBar
    -TimeBar timeBarRef
    +MenuImpostazioni(UrbanGrid logica, StatoCitta stato, MappaGriglia mappaVisiva, SimulationEngine engine, TopBar topBar, TimeBar timeBarRef)
    +void mostra()
    -Button creaBottone(String testo, String colore)
    -void eseguiImportazione(Stage popup)
    -void eseguiEsportazione(Stage popup)
    -void eseguiReset(Stage popup)
  }

  class PannelloControlliTempo {
    -Timeline simulazioneAuto
    -boolean inEsecuzione
    -Slider sliderVelocita
    -Button btnAzioneTempo
    +PannelloControlliTempo(SimulationEngine motore, TopBar topBar, MappaGriglia mappa, StatoCitta stato)
    -void fermaSimulazione(Button btn)
    +void fermaEImpostaManuale()
  }

  class PannelloNotifiche {
    -Label lblNotifiche
    +PannelloNotifiche()
    +void aggiornaNotifiche(SimulationEngine motore)
  }

  class PannelloRisorse {
    -ProgressBar pbAcqua
    -Label lblAcqua
    -ProgressBar pbEnergia
    -Label lblEnergia
    +PannelloRisorse()
    +void aggiornaRisorse(StatoCitta stato)
  }

  class PersistenceManager {
    -ObjectMapper objectMapper
    -Path saveDirectory
    +PersistenceManager(String directoryPath)
    -void inizializzaDirectory()
    +void salvaPartita(SaveGameData datiPartita, String nomeFile)
    +SaveGameData caricaPartita(String nomeFile)
    +SaveGameData loadGame(String absolutePath)
    +SaveGameData impacchettaDati(StatoCitta stato, UrbanGrid griglia)
    +void ripristinaDati(SaveGameData dati, StatoCitta stato, UrbanGrid griglia)
  }

  class PioggiaDiMeteoritiEvent {
    +PioggiaDiMeteoritiEvent(StatoCitta stato, UrbanGrid griglia)
    -void distruggiEdifici(StatoCitta stato, UrbanGrid griglia)
  }

  class PoliceStation {
    +PoliceStation(double placementCost, double baseMaintenanceCost, int baseSecurityCapacity)
  }

  class PoliticaAmbientale {
  }

  class PoliticaIndustriale {
  }

  class PoliticaNeutrale {
  }

  class PoliticaStrategy {
    <<interface>>
    ~void applicaModificatori(StatoCitta stato, TickStats stats)
  }

  class PowerPlant {
    +PowerPlant(double placementCost, double baseMaintenanceCost, int baseEnergyOutput)
    +int getEnergyOutput()
  }

  class PrimaveraEvent {
    +PrimaveraEvent()
  }

  class RandomEvent {
    <<abstract>>
    #int remainingTicks
    #String name
    +RandomEvent(String name, int durationTicks)
    +String getName()
    +int getRemainingTicks()
    +void decrementTick()
    +boolean isExpired()
    +void applyModifiers(StatoCitta stato, TickStats stats)*
  }

  class RegolaCollegamentoServizi {
    -UrbanGrid griglia
    -int raggioCopertura
    +RegolaCollegamentoServizi(UrbanGrid griglia, int raggioCopertura)
  }

  class RegolaCostruzione {
    <<interface>>
    ~void valida(UrbanEntity entita, Cell cella, StatoCitta stato)
  }

  class RegolaFondiSufficienti {
  }

  class RegolaSpazioLibero {
  }

  class Residential {
    -int baseHousingCapacity
    +Residential(double placementCost, double energyDemand, double waterDemand, int baseHousingCapacity)
    +int getHousingCapacity()
  }

  class Road {
    +Road(double placementCost)
  }

  class SaveGameData {
    -double finanze
    -int popolazione
    -double felicita
    -double ecologia
    -double sicurezza
    -double sanita
    -double lavoro
    -List~SavedEntityData~ edifici
    +SaveGameData()
    +double getFinanze()
    +void setFinanze(double finanze)
    +int getPopolazione()
    +void setPopolazione(int popolazione)
    +double getFelicita()
    +void setFelicita(double felicita)
    +double getEcologia()
    +void setEcologia(double ecologia)
    +double getSicurezza()
    +void setSicurezza(double sicurezza)
    +double getSanita()
    +void setSanita(double sanita)
    +double getLavoro()
    +void setLavoro(double lavoro)
    +List~SavedEntityData~ getEdifici()
    +void setEdifici(List~SavedEntityData~ edifici)
  }

  class SavedEntityData {
    -int x
    -int y
    -String tipo
    -int livello
    +SavedEntityData()
    +SavedEntityData(int x, int y, String tipo, int livello)
    +int getX()
    +void setX(int x)
    +int getY()
    +void setY(int y)
    +String getTipo()
    +void setTipo(String tipo)
    +int getLivello()
    +void setLivello(int livello)
  }

  class School {
    +School(double placementCost, double baseMaintenanceCost, int baseEducationCapacity)
  }

  class SideBar {
    -Button pulsanteAttivo
    -String stilePrecedente
    +SideBar(MappaGriglia mappa)
    -void addButtonEdificio(MappaGriglia mappa, String etichetta, int costo, String tipo, String colore)
  }

  class SimulationEngine {
    -StatoCitta stato
    -UrbanGrid griglia
    -PoliticaStrategy politicaAttiva
    -List~CityObserver~ observers
    -RandomEvent activeEvent
    -Random random
    -int ticksInNegativeFunds
    +SimulationEngine(StatoCitta stato, UrbanGrid griglia)
    +void addObserver(CityObserver observer)
    +void removeObserver(CityObserver observer)
    +void forceNotifyObservers()
    +void setPoliticaAttiva(PoliticaStrategy nuovaPolitica)
    +PoliticaStrategy getPoliticaAttiva()
    +List~UrbanEntity~ getActiveEntities()
    +void tick()
    -void gestisciEventi(TickStats stats)
    +boolean checkCoverage(UrbanEntity entity)
    +String getMotivoInattivita(UrbanEntity entity)
    -void applicaDinamicheGlobali()
    -double calcolaModificatoreFelicita()
  }

  class StateBuilding {
    <<abstract>>
    +StateBuilding(double placementCost, double baseMaintenanceCost, int baseMaxCapacity)
  }

  class StatoCitta {
    -int popolazione
    -double finanze
    -double felicita
    -double ecologia
    -double lavoro
    -double sicurezza
    -double sanita
    -double acquaFornita
    -double acquaRichiesta
    -double energiaFornita
    -double energiaRichiesta
    -int tickets
    +StatoCitta()
    +void addFinanze(double delta)
    +void setPopolazione(int pop)
    +void setAcquaFornita(double v)
    +void setAcquaRichiesta(double v)
    +void setEnergiaFornita(double v)
    +void setEnergiaRichiesta(double v)
    +void setFelicita(double v)
    +void setEcologia(double v)
    +void setLavoro(double v)
    +void setSicurezza(double v)
    +void setSanita(double v)
    -double clamp(double value)
    +void reset()
    +int getPopolazione()
    +double getFinanze()
    +double getFelicita()
    +double getEcologia()
    +double getLavoro()
    +double getSicurezza()
    +double getSanita()
    +double getAcquaFornita()
    +double getAcquaRichiesta()
    +double getEnergiaFornita()
    +double getEnergiaRichiesta()
    +int getTickets()
    +void setFinanze(double finanze)
    +void addTicket()
  }

  class TickStats {
    -int capacitaAbitativa
    -int postiLavoro
    -int puntiInquinamento
    -double redditoCommerciale
    -int puntiSicurezza
    -int puntiSanita
    -int puntiFelicita
    -double acquaFornita
    -double acquaRichiesta
    -double energiaFornita
    -double energiaRichiesta
    -int industrieAttive
    +void addCapacitaAbitativa(int v)
    +void addPostiLavoro(int v)
    +void addPuntiInquinamento(int v)
    +void addRedditoCommerciale(double v)
    +void addPuntiSicurezza(int v)
    +void addPuntiSanita(int v)
    +void addPuntiFelicita(int v)
    +void addAcquaFornita(double v)
    +void addAcquaRichiesta(double v)
    +void addEnergiaFornita(double v)
    +void addEnergiaRichiesta(double v)
    +void addIndustriaAttiva()
    +int getCapacitaAbitativa()
    +int getPostiLavoro()
    +int getPuntiInquinamento()
    +double getRedditoCommerciale()
    +int getPuntiSicurezza()
    +int getPuntiSanita()
    +int getPuntiFelicita()
    +double getAcquaFornita()
    +double getAcquaRichiesta()
    +double getEnergiaFornita()
    +double getEnergiaRichiesta()
    +int getIndustrieAttive()
  }

  class TimeBar {
    -PannelloNotifiche pannelloNotifiche
    -PannelloRisorse pannelloRisorse
    -PannelloControlliTempo pannelloTempo
    -SimulationEngine motore
    +TimeBar(SimulationEngine motore, TopBar topBar, MappaGriglia mappa, StatoCitta stato)
    +void fermaEImpostaManuale()
  }

  class TopBar {
    -Label lblFinanze
    -Label lblPopolazione
    -Label lblTickets
    -ProgressBar pbSicurezza
    -ProgressBar pbSanita
    -ProgressBar pbEcologia
    -ProgressBar pbFelicita
    -ProgressBar pbLavoro
    -ChoiceBox~String~ selettorePolitica
    -SimulationEngine engine
    -UrbanGrid logica
    -StatoCitta stato
    -MappaGriglia mappaVisiva
    -TimeBar timeBarRef
    +SimulationEngine getSimulationEngine()
    +TopBar()
    +void setSimulationEngine(SimulationEngine engine)
    +void setTimeBar(TimeBar timeBar)
    +void setRiferimenti(UrbanGrid logica, StatoCitta stato, MappaGriglia mappaVisiva)
    -VBox creaBarra(String nome, ProgressBar bar, String colore)
    +void aggiornaDati(StatoCitta stato)
    +void resetPolitica()
  }

  class TutorialPopup {
    +void mostraTutorial()$
  }

  class UrbanEntity {
    <<abstract>>
    -double placementCost
    -int developmentLevel
    -int x
    -int y
    +UrbanEntity(double placementCost)
    +int getX()
    +void setX(int x)
    +int getY()
    +void setY(int y)
    +double getPlacementCost()
    +int getDevelopmentLevel()
    +void upgradeLevel()
    +boolean isFunctioning()
    +void processTick(StatoCitta stato, TickStats stats)
  }

  class UrbanEntityFactory {
    +UrbanEntity createEntity(String type)$
  }

  class UrbanGrid {
    -int width
    -int height
    -Cell[][] grid
    -List~UrbanEntity~ activeEntities
    +UrbanGrid(int width, int height)
    -void initializeGrid()
    +void svuotaGriglia()
    +int getWidth()
    +int getHeight()
    +boolean isWithinBounds(int x, int y)
    +Cell getCell(int x, int y)
    +List~UrbanEntity~ getActiveEntities()
    +void placeEntity(UrbanEntity entity, int x, int y)
    +boolean removeEntity(int x, int y)
    +boolean hasAdjacentRoad(int x, int y)
    +void azzeraMappa()
  }

  class WaterPlant {
    +WaterPlant(double placementCost, double baseMaintenanceCost, int baseWaterOutput)
    +int getWaterOutput()
  }

  %% ==========================================
  %% RELAZIONI - 1. Generalizzazione / Ereditarietà
  %% ==========================================
  UrbanEntity <|-- Building
  UrbanEntity <|-- Infrastructure
  Infrastructure <|-- StateBuilding
  Building <|-- Residential
  Building <|-- Commercial
  Building <|-- Industrial
  Infrastructure <|-- PowerPlant
  Infrastructure <|-- WaterPlant
  Infrastructure <|-- Road
  Infrastructure <|-- GreenArea
  StateBuilding <|-- Hospital
  StateBuilding <|-- PoliceStation
  StateBuilding <|-- FireStation
  StateBuilding <|-- School
  RandomEvent <|-- CrisiEconomicaEvent
  RandomEvent <|-- GuerraEvent
  RandomEvent <|-- PioggiaDiMeteoritiEvent
  RandomEvent <|-- PrimaveraEvent
  PoliticaStrategy <|.. PoliticaAmbientale
  PoliticaStrategy <|.. PoliticaIndustriale
  PoliticaStrategy <|.. PoliticaNeutrale
  RegolaCostruzione <|.. RegolaSpazioLibero
  RegolaCostruzione <|.. RegolaFondiSufficienti
  RegolaCostruzione <|.. RegolaCollegamentoServizi
  Exception <|-- CostruzioneException

  %% ==========================================
  %% RELAZIONI - 2. Composizione (Rombo pieno)
  %% ==========================================
  UrbanGrid "1" *-- "*" Cell : -grid
  BuilderValidator "1" *-- "*" RegolaCostruzione : -regole
  SaveGameData "1" *-- "*" SavedEntityData : -edifici
  CityApp "1" *-- "1" SimulationEngine : -motore
  CityApp "1" *-- "1" UrbanGrid : -grigliaLogica
  CityApp "1" *-- "1" StatoCitta : -statoCitta
  CityApp "1" *-- "1" BuilderValidator : -validatore

  %% ==========================================
  %% RELAZIONI - 3. Aggregazione (Rombo vuoto)
  %% ==========================================
  Cell "1" o-- "0..1" UrbanEntity : -entity
  UrbanGrid "1" o-- "*" UrbanEntity : -activeEntities
  SimulationEngine "1" o-- "1" StatoCitta : -stato
  SimulationEngine "1" o-- "1" UrbanGrid : -griglia
  SimulationEngine "1" o-- "0..1" PoliticaStrategy : -politicaAttiva
  SimulationEngine "1" o-- "0..1" RandomEvent : -activeEvent
  SimulationEngine "1" o-- "*" CityObserver : -observers
  TopBar "1" o-- "1" SimulationEngine : -engine
  TopBar "1" o-- "1" StatoCitta : -stato
  TopBar "1" o-- "1" UrbanGrid : -logica
  MappaGriglia "1" o-- "1" UrbanGrid : -grigliaLogica
  MappaGriglia "1" o-- "1" BuilderValidator : -validatore
  MappaGriglia "1" o-- "1" StatoCitta : -statoCitta
  MenuImpostazioni "1" o-- "1" SimulationEngine : -engine
  TimeBar "1" o-- "1" SimulationEngine : -motore
  PannelloControlliTempo "1" o-- "1" SimulationEngine : -motore
  RegolaCollegamentoServizi "1" o-- "1" UrbanGrid : -griglia

  %% ==========================================
  %% RELAZIONI - 4. Dipendenza (Uso Temporaneo)
  %% ==========================================
  UrbanEntity ..> StatoCitta : processTick
  UrbanEntity ..> TickStats : processTick
  PoliticaStrategy ..> StatoCitta : applicaModificatori
  PoliticaStrategy ..> TickStats : applicaModificatori
  RandomEvent ..> StatoCitta : applyModifiers
  RandomEvent ..> TickStats : applyModifiers
  BuilderValidator ..> UrbanEntity : validaCostruzione
  BuilderValidator ..> Cell : validaCostruzione
  BuilderValidator ..> StatoCitta : validaCostruzione
  RegolaCostruzione ..> UrbanEntity : valida
  RegolaCostruzione ..> Cell : valida
  RegolaCostruzione ..> StatoCitta : valida
  UrbanEntityFactory ..> UrbanEntity : createEntity
  PersistenceManager ..> SaveGameData : DTO I/O
  AssetManager ..> UrbanEntity : ottieniImmagine
  CityObserver ..> StatoCitta : onSimulationUpdated
```

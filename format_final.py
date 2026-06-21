with open('javap_parsed_with_names.txt', 'r', encoding='utf-8') as f:
    text = f.read()

relazioni = """

### RELAZIONI TRA LE CLASSI (Standard UML)

Di seguito sono elencate tutte le relazioni architettoniche estratte dall'analisi incrociata dei campi e delle firme dei metodi, formattate secondo le convenzioni standard UML richieste:

#### 1. Generalizzazione / Ereditarietà (Linea continua con triangolo vuoto chiuso)
*Rappresenta una relazione "is-a". Se realizza un'interfaccia, si usa la linea tratteggiata.*

*   `Building` --|> `UrbanEntity`
*   `Infrastructure` --|> `UrbanEntity`
*   `StateBuilding` --|> `Infrastructure`
*   `Residential` --|> `Building`
*   `Commercial` --|> `Building`
*   `Industrial` --|> `Building`
*   `PowerPlant` --|> `Infrastructure`
*   `WaterPlant` --|> `Infrastructure`
*   `Road` --|> `Infrastructure`
*   `GreenArea` --|> `Infrastructure`
*   `Hospital` --|> `StateBuilding`
*   `PoliceStation` --|> `StateBuilding`
*   `FireStation` --|> `StateBuilding`
*   `School` --|> `StateBuilding`
*   `CrisiEconomicaEvent` --|> `RandomEvent`
*   `GuerraEvent` --|> `RandomEvent`
*   `PioggiaDiMeteoritiEvent` --|> `RandomEvent`
*   `PrimaveraEvent` --|> `RandomEvent`
*   `PoliticaAmbientale` ..|> `PoliticaStrategy` (Realizzazione interfaccia)
*   `PoliticaIndustriale` ..|> `PoliticaStrategy` (Realizzazione interfaccia)
*   `PoliticaNeutrale` ..|> `PoliticaStrategy` (Realizzazione interfaccia)
*   `RegolaSpazioLibero` ..|> `RegolaCostruzione` (Realizzazione interfaccia)
*   `RegolaFondiSufficienti` ..|> `RegolaCostruzione` (Realizzazione interfaccia)
*   `RegolaCollegamentoServizi` ..|> `RegolaCostruzione` (Realizzazione interfaccia)
*   `CostruzioneException` --|> `Exception`

#### 2. Composizione (Rombo pieno `*-->`, relazione intero-parte vitale forte)
*Le parti vengono distrutte assieme al contenitore. Unidirezionale verso la parte.*

*   `UrbanGrid` "1" *--> "*" `Cell` : Composizione (Ruolo bersaglio: `-grid`)
*   `BuilderValidator` "1" *--> "*" `RegolaCostruzione` : Composizione (Ruolo bersaglio: `-regole`)
*   `SaveGameData` "1" *--> "*" `SavedEntityData` : Composizione (Ruolo bersaglio: `-edifici`)
*   `CityApp` "1" *--> "1" `SimulationEngine` : Composizione (Ruolo bersaglio: `-motore` - l'App istanzia fisicamente il motore)
*   `CityApp` "1" *--> "1" `UrbanGrid` : Composizione (Ruolo bersaglio: `-grigliaLogica`)
*   `CityApp` "1" *--> "1" `StatoCitta` : Composizione (Ruolo bersaglio: `-statoCitta`)
*   `CityApp` "1" *--> "1" `BuilderValidator` : Composizione (Ruolo bersaglio: `-validatore`)

#### 3. Aggregazione (Rombo vuoto `o-->`, relazione intero-parte debole)
*Il contenitore ospita riferimenti a oggetti che possono sopravvivere separatamente.*

*   `Cell` "1" o--> "0..1" `UrbanEntity` : Aggregazione (Ruolo bersaglio: `-entity` - la cella ospita l'edificio)
*   `UrbanGrid` "1" o--> "*" `UrbanEntity` : Aggregazione (Ruolo bersaglio: `-activeEntities` - la lista di puntatori)
*   `SimulationEngine` "1" o--> "1" `StatoCitta` : Aggregazione (Ruolo bersaglio: `-stato`)
*   `SimulationEngine` "1" o--> "1" `UrbanGrid` : Aggregazione (Ruolo bersaglio: `-griglia`)
*   `SimulationEngine` "1" o--> "0..1" `PoliticaStrategy` : Aggregazione (Ruolo bersaglio: `-politicaAttiva`)
*   `SimulationEngine` "1" o--> "0..1" `RandomEvent` : Aggregazione (Ruolo bersaglio: `-activeEvent`)
*   `SimulationEngine` "1" o--> "*" `CityObserver` : Aggregazione (Ruolo bersaglio: `-observers`)
*   `TopBar` "1" o--> "1" `SimulationEngine` : Aggregazione (Ruolo bersaglio: `-engine`)
*   `TopBar` "1" o--> "1" `StatoCitta` : Aggregazione (Ruolo bersaglio: `-stato`)
*   `MappaGriglia` "1" o--> "1" `UrbanGrid` : Aggregazione (Ruolo bersaglio: `-grigliaLogica`)
*   `MappaGriglia` "1" o--> "1" `BuilderValidator` : Aggregazione (Ruolo bersaglio: `-validatore`)
*   `MenuImpostazioni` "1" o--> "1" `SimulationEngine` : Aggregazione (Ruolo bersaglio: `-engine`)
*   `TimeBar` "1" o--> "1" `SimulationEngine` : Aggregazione (Ruolo bersaglio: `-motore`)
*   `RegolaCollegamentoServizi` "1" o--> "1" `UrbanGrid` : Aggregazione (Ruolo bersaglio: `-griglia`)

#### 4. Dipendenza (Linea tratteggiata con freccia aperta `..>`)
*Uso temporaneo (passaggio di parametri, istanziazioni una tantum o ritorni da metodi).*

*   `UrbanEntity` ..> `StatoCitta` : Dipendenza (Parametro in `processTick`)
*   `UrbanEntity` ..> `TickStats` : Dipendenza (Parametro in `processTick`)
*   `PoliticaStrategy` ..> `StatoCitta` : Dipendenza (Parametro in `applicaModificatori`)
*   `PoliticaStrategy` ..> `TickStats` : Dipendenza (Parametro in `applicaModificatori`)
*   `RandomEvent` ..> `StatoCitta` : Dipendenza (Parametro in `applyModifiers`)
*   `RandomEvent` ..> `TickStats` : Dipendenza (Parametro in `applyModifiers`)
*   `BuilderValidator` ..> `UrbanEntity` : Dipendenza (Parametro in `validaCostruzione`)
*   `BuilderValidator` ..> `Cell` : Dipendenza (Parametro in `validaCostruzione`)
*   `RegolaCostruzione` ..> `UrbanEntity` : Dipendenza (Parametro in `valida`)
*   `RegolaCostruzione` ..> `Cell` : Dipendenza (Parametro in `valida`)
*   `RegolaCostruzione` ..> `StatoCitta` : Dipendenza (Parametro in `valida`)
*   `UrbanEntityFactory` ..> `UrbanEntity` : Dipendenza (Ritorno del factory method `createEntity`)
*   `PersistenceManager` ..> `SaveGameData` : Dipendenza (Gestione DTO I/O)
*   `AssetManager` ..> `UrbanEntity` : Dipendenza (Parametro per rendering sprite)
*   `CityObserver` ..> `StatoCitta` : Dipendenza (Parametro in `onSimulationUpdated`)
"""

with open('lista_completa.txt', 'w', encoding='utf-8') as f:
    f.write(text + relazioni)

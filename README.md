# Citylogic Simulator

Citylogic è un simulatore urbano gestionale rule-based, sviluppato in Java, incentrato sulla pianificazione strategica e la crescita della città. Il giocatore costruisce insediamenti, gestisce le infrastrutture, attiva politiche cittadine e fa avanzare la simulazione attraverso turni discreti (Tick).

L'obiettivo è bilanciare 7 metriche chiave (Finanze, Popolazione, Felicità, Lavoro, Sicurezza, Sanità ed Ecologia) su una griglia urbana fissa, rispondendo dinamicamente alle esigenze dei cittadini e agli eventi casuali.

## Il Progetto in Sintesi

- Costruisci e gestisci una griglia cittadina fissa di **24 x 16** (384 celle totali).
- Posiziona Zone Residenziali, Commerciali e Industriali per attirare abitanti ed espandere l'economia.
- Bilancia e fornisci servizi di base vitali: Strade, Ospedali, Caserme di Polizia e Pompieri, Centrali Elettriche e Acquedotti.
- Fai avanzare il tempo tramite *Tick*; ogni tick elabora i costi di mantenimento, la produzione di inquinamento, la crescita demografica, il gettito fiscale e gli eventi casuali.
- Usa lo strumento Distruggi per distruggere edifici sulla griglia urbana.
- Salva e ricarica le tue partite interamente tramite file JSON locali.

## Gameplay Loop

1. **Inizia una nuova partita** o carica un salvataggio esistente.
2. **Piazza i servizi essenziali** e collegali tramite le Strade per garantire copertura idrica, elettrica e di sicurezza.
3. **Costruisci edifici e zone**. Se un edificio appena piazzato non ha i requisiti necessari, rimarrà in uno stato di *Developing* (Inattivo) finché non verrà coperto dai servizi.
4. **Avanza di un Tick** per processare il mese: incassa le tasse, paga le spese di mantenimento delle infrastrutture e osserva i cambiamenti.
5. **Leggi l'HUD e i Pannelli**: monitora costantemente le 7 metriche globali, il livello di felicità e ispeziona gli edifici inattivi per capirne il motivo.
6. **Reagisci agli imprevisti**: gestisci eventi casuali (Guerre, Piogge di Meteoriti, Crisi Economiche) e attiva *Politiche Cittadine* (es. Tassa Ambientale) per alterare strategicamente le regole del gioco.

## Eseguire in Locale

Strumenti richiesti:
- Java 17 JDK (o superiore)
- Maven 3.8+

Dalla root del repository (cartella principale del progetto), apri il terminale ed esegui:

```bash
mvn clean install
mvn javafx:run
```

Comandi utili per lo sviluppo e il testing:
```bash
mvn clean test         # esegue l'intera suite di unit test (JUnit 5)
mvn clean package      # compila, esegue i test e pacchettizza l'applicativo
```

### Dipendenze e Librerie
- **JavaFX:** Per il rendering dell'interfaccia grafica, l'input del mouse e l'aggiornamento della dashboard.
- **Jackson/Gson:** Per la serializzazione e deserializzazione locale in formato JSON (Persistence).
- **JUnit 5:** Per la validazione rigorosa della logica di dominio (System & Unit Testing).
*(Nota: Il software è completamente standalone e non fa uso di API REST o servizi cloud esterni).*

## Architettura e Logica delle Cartelle

Il progetto rispetta una separazione rigorosa MVC (Model-View-Controller) ed è organizzato nei seguenti package principali:

| Cartella / Package | Responsabilità |
|--------|----------------|
| `citylogic.domain` | **(Model)** Logica di dominio pura. Contiene lo `StatoCitta`, la griglia `UrbanGrid`, e tutte le entità (`Building`, `Residential`, ecc.). Non contiene alcuna dipendenza grafica. |
| `citylogic.core` | **(Controller/Engine)** Motore del gioco. Contiene il `SimulationEngine` che processa i Tick, il `BuilderValidator` per le regole di costruzione, la gestione degli eventi e le strategie politiche. |
| `citylogic.infrastructure` | **(Persistence)** Gestisce il salvataggio e il caricamento del file JSON interfacciandosi con il filesystem locale. |
| `citylogic.ui` | **(View)** Layer JavaFX. Contiene le finestre, la HUD, i bottoni e si aggiorna ascoltando il dominio tramite l'Observer Pattern. |

## Comandi di Gioco

Le interazioni non avvengono tramite console, ma interamente attraverso l'HUD di JavaFX e il click del mouse sulla griglia.

| Comando | Come eseguirlo | Comportamento |
|---------|---------------|------------------|
| **Costruire un edificio** | Seleziona il tipo di zona o infrastruttura dall'apposito pannello di controllo laterale, quindi fai click sinistro su una cella libera della griglia centrale. | Piazza l'edificio selezionato e deduce immediatamente il costo dal budget. L'azione viene rigettata se la cella è già occupata o se i fondi cittadini sono insufficienti. |
| **Strumento Distruggi** | Seleziona l'opzione "Distruggi" dal pannello di controllo e fai click sinistro sull'edificio da abbattere. | Rimuove permanentemente la struttura, liberando la cella per nuove costruzioni. L'azione non restituisce denaro; i residenti o i lavoratori associati vengono persi all'istante (Esodo). |
| **Avanzare di Tick** | Clicca il pulsante principale "Next Tick" situato nella dashboard temporale in alto. | Manda avanti la simulazione di uno step. Il motore ricalcola tasse, spese, flussi di popolazione e applica gli effetti attivi delle Policy e degli Eventi Casuali. |
| **Attivare Politiche** | Seleziona e clicca su una Policy (es. "Tassa Ambientale" o "Espansione Industriale") dal pannello dedicato. | Cambia dinamicamente le formule matematiche di gettito e inquinamento tramite il Pattern Strategy per tutti i tick a venire, finché la policy rimane in vigore. |
| **Salvare la Partita** | Apri il menu delle opzioni (o premi l'apposito pulsante) e inserisci/conferma il salvataggio. | Scrive un file JSON locale che funge da snapshot esatto della mappa di gioco (`UrbanGrid`) e dello stato globale dei parametri (`StatoCitta`). |
| **Caricare la Partita** | Clicca sull'opzione di caricamento dal menu principale e seleziona il file JSON di salvataggio. | Ripristina interamente lo stato di gioco, ricostruendo le reti infrastrutturali e sovrascrivendo l'avanzamento. |

## Strumenti AI Utilizzati

Durante l'intero ciclo di vita dello sviluppo del software, i modelli avanzati di Intelligenza Artificiale (Gemini e l'agente autonomo Antigravity) sono stati impiegati in maniera strutturata per potenziare la produttività e supportare lo sviluppo, in particolare nelle seguenti aree:
- **Design Architetturale:** Supporto nell'ideazione di soluzioni di codice pulite tramite Design Pattern (MVC, Strategy, Factory, Observer) per garantire la corretta segregazione dei livelli (isolamento del Domain Model).
- **Test Automation & Quality Assurance:** Co-progettazione e stesura materiale di una rigorosa suite di unit e system testing tramite JUnit 5. Sono stati generati ed ottimizzati oltre 45 test case per stressare il motore di simulazione, testare eccezioni, e assicurare la compliance con tutte le *Acceptance Criteria*.

## Glossario Tecnico

- **TICK:** L'unità di tempo discreta che scatena il ricalcolo dell'intero motore di simulazione e l'aggiornamento simultaneo della UI.
- **INFRASTRUTTURA / SERVIZI:** Elementi essenziali (strade, ospedali, pompieri, centrali idriche ed elettriche) necessari affinché un edificio passi allo stato di attività utile. Ospedali, Pompieri e Polizia impongono un **Raggio di Copertura** limitato nella griglia (es. 7 celle massime calcolate secondo le distanze di Chebyshev).
- **STATO DEVELOPING:** Un edificio appena posizionato, ma a cui mancano i requisiti fondamentali di rete. Non genera tasse, non offre posti di lavoro né bonus ecologici/sanitari finché i servizi non lo raggiungono fisicamente.
- **STATO ACTIVE:** L'edificio è pienamente operativo e coperto. Contribuisce attivamente al pool di risorse di `TickStats` e alle dinamiche cittadine.
- **POLICY:** Strategia di calcolo globale (es. Politica Industriale o Ambientale) che altera pesantemente le costanti matematiche del gioco a runtime utilizzando il pattern architetturale Strategy.
- **METRICA:** Indicatore numerico percentuale (0-100) o assoluto dello stato della città visualizzato nella dashboard (Ecologia, Popolazione, Sicurezza, Lavoro, Finanze, Sanità, Felicità).
- **URBAN GRID:** La matrice logica bidimensionale che rappresenta il suolo cittadino e gestisce lo spazio (celle occupate e libere) della simulazione.
- **SIMULATION ENGINE:** Il "motore" nascosto del gioco che orchestra le chiamate di business logic ogni qual volta viene richiesto il progresso del tempo (Tick).
- **BUILDER VALIDATOR:** Modulo di controllo che verifica, prima del posizionamento effettivo, che la costruzione non violi le regole economiche (es. bancarotta) o topologiche (es. collisione con altri edifici).

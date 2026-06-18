# Manuale Utente e Tecnico: Citylogic

Questo manuale è progettato per guidare l'utente passo dopo passo, dall'installazione alla comprensione delle meccaniche di simulazione e dell'architettura tecnica del progetto.

---

## 1. Introduzione e Metriche Chiave

**Citylogic** è un simulatore urbano gestionale *rule-based* incentrato sulla pianificazione strategica e sulla gestione della crescita metropolitana, ambientato su una griglia logica fissa di **24x16 celle**. 

L'obiettivo del giocatore (l'amministratore cittadino) è ottimizzare in modo sinergico **7 metriche chiave** che determinano lo stato di salute dell'insediamento. L'avanzamento della simulazione è regolato da un motore a eventi discreti, cadenzato da unità temporali denominate **Tick**. Ad ogni scatto del Tick, il sistema esegue il ricalcolo completo di tutte le *business rules*, aggiornando le risorse e la UI.

| Metrica | Descrizione |
|---|---|
| 💰 **Finanze** | Il bilancio economico e la cassa municipale a disposizione per le costruzioni. |
| 👥 **Popolazione** | Il numero totale di abitanti residenti attualmente in città. |
| 😊 **Felicità** | Il livello di soddisfazione globale, influenzato da tasse, natura e servizi. |
| 💼 **Lavoro** | Il tasso di occupazione generale offerto dalle zone commerciali e industriali. |
| 🛡️ **Sicurezza** | La protezione garantita dalla copertura delle centrali di Polizia e Pompieri. |
| 🏥 **Sanità** | L'efficienza del sistema medico (Ospedali) rispetto alla popolazione totale. |
| 🌲 **Ecologia** | L'impatto ambientale delle scelte urbanistiche (Industrie vs Parchi). |

---

## 2. Configurazione di Ambiente e Avvio

Il software è interamente sviluppato utilizzando la piattaforma Java. 

### Runtime and development requirements

| Requisito | Scelta / Vincolo |
|---|---|
| **Linguaggio** | Java |
| **Versione Java** | **Java 17 (JDK) o superiore.** È necessario il Development Kit (JDK), non solo il runtime (JRE). |
| **Build tool** | Apache Maven 3.8+ |
| **Libreria Grafica** | JavaFX |
| **Testing Framework** | JUnit 5 (Jupiter) |
| **Formato Salvataggi** | JSON locale (su FileSystem, nessun database richiesto) |
| **IDE Consigliato** | Visual Studio Code, IntelliJ IDEA o Eclipse |

### Dependencies

Il progetto si appoggia ad un numero limitato e mirato di dipendenze esterne per le sue funzionalità core:
* **JavaFX:** Gestisce l'intero strato grafico, gli eventi del mouse e l'aggiornamento della dashboard.
* **Jackson / Gson:** Forniscono il motore di parsing per la serializzazione e deserializzazione degli stati di gioco nei salvataggi JSON.
* **JUnit 5:** Permette l'esecuzione della vasta suite di unit test per validare la robustezza delle formule matematiche.

### Procedura di Installazione e Lancio

Segui questi passaggi per avviare il simulatore per la prima volta:

**Passo 1: Installare Git**
Se non possiedi Git, scaricalo e installalo dal sito ufficiale: [git-scm.com/download](https://git-scm.com/downloads). Assicurati che l'eseguibile venga aggiunto alla variabile d'ambiente PATH durante l'installazione.

**Passo 2: Installare Java 17 JDK**
Assicurati di avere il JDK (Java Development Kit) installato e non solo il JRE. Puoi scaricare la versione corretta da Adoptium (Temurin). Verifica l'installazione aprendo un terminale e digitando:
```bash
java -version
```

**Passo 3: Installare Maven**
* **Windows:** Scarica il binario ZIP da [maven.apache.org](https://maven.apache.org/download.cgi), estrailo (es. in `C:\maven`) e aggiungi il percorso `C:\maven\bin` alle variabili d'ambiente `PATH` del tuo sistema. In alternativa, da PowerShell puoi usare il comando:
```bash
winget install Apache.Maven
```
* **macOS:** Apri il terminale e digita:
```bash
brew install maven
```
* **Linux:** Da terminale usa il package manager della tua distribuzione, ad esempio:
```bash
sudo apt install maven
```
Controlla che l'installazione sia andata a buon fine digitando in un nuovo terminale:
```bash
mvn -version
```

**Passo 4: Scaricare il codice e avviare il gioco**
Apri un terminale (o PowerShell) e digita i seguenti comandi in sequenza, premendo Invio dopo ciascuno di essi:

```bash
# 1. Clona la repository in locale scaricando tutto il codice
git clone https://github.com/EdTheDe/Progetto_SimCity.git

# 2. Spostati fisicamente nella cartella del progetto scaricato
cd SimCity

# 3. Scarica le dipendenze e compila il progetto risolvendo ogni modulo
mvn clean install

# 4. Avvia l'applicazione e lancia la finestra di gioco JavaFX
mvn javafx:run
```

---

## 3. Interfaccia Utente e Comandi di Gioco

L'architettura grafica (realizzata in JavaFX) è suddivisa in aree funzionali specifiche per garantire all'utente il massimo controllo gestionale in tempo reale.

### Aree Funzionali dell'Interfaccia
* **Mappa Centrale (Urban Grid):** Area interattiva di 24x16 quadrati. Ogni cella costituisce un'unità atomica che può ospitare una singola strada o un singolo edificio, e cambia il proprio sprite visivo in base allo stato (`Inattivo` o `ACTIVE`).
* **TopBar (Barra di Stato Superiore):** Una dashboard che mostra in tempo reale l'andamento quantitativo dei parametri vitali: Fondi, Popolazione e Tick corrente. Integra le barre di caricamento colorate per visualizzare istantaneamente le percentuali di Felicità, Sicurezza, Sanità, Lavoro ed Ecologia.
* **SideBar (Strumenti di Costruzione):** Un pannello sulla sinistra che raccoglie i bottoni raggruppati per l'edificazione. Qui si selezionano le Zone (Residenziali, Commerciali, Industriali), i Servizi Pubblici (Ospedali, Polizia, Pompieri) e le Infrastrutture (Strade, Parchi, Centrali Elettriche, Impianti Idrici).
* **Pannelli di Destra (Reti):** Sezione di analisi numerica dedicata in via esclusiva al monitoraggio delle risorse infrastrutturali. Visualizza le unità di capacità erogata e il carico richiesto attualmente dalla Rete Elettrica e dalla Rete Idrica della città.
* **Pannello Notifiche e Controlli Temporali:** Situato in basso o in aree designate, raccoglie i controlli temporali del motore fisico (per l'avanzamento dei Tick in modalità manuale o automatica) e un monitor a scorrimento per notifiche critiche (es. "Fondi insufficienti").

### Griglia dei Comandi Eseguibili

La seguente tabella riassume tutte le operazioni e interazioni disponibili durante il gameplay:

| Azione Eseguibile | Come Operare nell'Interfaccia | Descrizione dell'Effetto |
|---|---|---|
| **Costruire Edifici / Zone** | Clicca il pulsante corrispondente nella *SideBar* di sinistra, quindi fai click sinistro su una cella libera della *Mappa Centrale*. | Conferma il posizionamento e scala immediatamente il prezzo dal bilancio delle *Finanze*. Fallisce bloccando l'azione se la cella è già in uso. |
| **Avanzare di Livello** | Seleziona l'edificio sulla mappa e clicca sull'opzione di upgrade nel pannello di controllo. | Consuma fondi per aumentare il livello di sviluppo della struttura (fino al cap di livello 5), moltiplicando i bonus e il gettito fiscale offerto. |
| **Strumento Distruggi (Demolizione)** | Seleziona l'edificio, clicca sulla scheda "Gestione", e usa il pulsante "Demolisci". | Libera permanentemente la cella. Non vi è alcun rimborso spese e si perde istantaneamente la capacità infrastrutturale (causando eventuali *Esodi* demografici). |
| **Avanzare Manualmente (Skip)** | Clicca sul pulsante **"Avanti Turno"** situato nella plancia di comando temporale. | Invia un comando al `SimulationEngine` che porta il tempo avanti di un mese (Tick). Incassa le tasse, paga le spese, e applica tutte le regole biologiche. |
| **Avanzamento Automatico** | Fai scorrere il pallino sul cursore apposito posizionandolo su velocità **1x, 2x o 4x**. Portalo sullo zero per mettere in Pausa. | Il gioco calcolerà nuovi Tick in background senza ulteriori input dell'utente. Ottimo per attendere l'accumulo passivo di tasse nel tempo. |
| **Ispezionare Inattività** | Seleziona l'edificio e controlla la scheda "Stato" nel pannello di destra. | Mostrerà un report specifico sull'eventuale inattività (es. *"Manca Elettricità"*, *"Manca Ospedale"*) e il livello attuale di sviluppo (Lv. 1-5). |
| **Attivare Politiche** | Seleziona una Policy dal menù contestuale delle politiche (es. "Tassa Ambientale" o "Espansione Industriale"). | Carica istantaneamente la politica tramite lo *Strategy Pattern*. La mossa altererà drasticamente (in positivo o negativo) le equazioni di crescita dal prossimo Tick. |
| **Salvare Partita** | Apri il menù "Gestione Partita" e clicca **"Esporta Stato"**. | Scrive lo snapshot completo e persistente della griglia e dei numeri attuali su un file JSON. |
| **Caricare Partita** | Apri il menù "Gestione Partita" e clicca **"Importa Stato"** selezionando il file. | Ricostruisce la matrice di gioco distruggendo il progresso corrente e sostituendolo integralmente col file. |
| **Uscita dal gioco** | Apri il menù "Gestione Partita" e premi il tasto "Esci". | Arresta il programma e la Virtual Machine Java in esecuzione. |

---

## 4. Vita del Simulatore

Questa sezione delinea i passaggi logici interni che scandiscono la simulazione e muovono il bilanciamento matematico del progetto, diviso nelle tre grandi categorie logiche del dominio.

### 4.1 Validazione e Ciclo di Vita delle Celle
Ogni cella non è semplicemente "disegnata", ma segue una rigida logica di convalida ad opera del `BuilderValidator`. Ciascuna cella può transitare unicamente in tre stati:

1. **Stato EMPTY (Vuoto):** La cella non possiede sovrastrutture ed è idonea alla posa.
2. **Stato Inattivo:** Una volta piazzata la struttura, questa rimane bloccata e congelata. Non produce gettito fiscale e non sblocca bonus fintanto che *almeno uno* di questi requisiti è violato:
   * *Mancanza di Collegamento Stradale:* Deve essere fisicamente contigua a un pezzo di strada.
   * *Mancanza di Utenze:* Non è coperta dalla erogazione globale della Centrale Elettrica e Idrica.
   * *Violazione dei Vincoli Statali:* Deve rientrare fisicamente nel **Raggio di Copertura limitato a 7 celle** di almeno una Stazione di Polizia, di un Ospedale e di un distaccamento dei Pompieri.
3. **Stato ACTIVE (Operativo):** Raggiunti i requisiti, l'edificio si sblocca, produce gli effetti desiderati e partecipa al calcolo fiscale del Tick. Qualora vengano distrutte (o manchino) infrastrutture di supporto in futuro, lo stato regredisce a *Inattivo*.

### 4.2 Motore di Simulazione e Formule Logiche
La simulazione respira attraverso la funzione `tick()` del `SimulationEngine`. Quando invocato, questo ricalcola dinamicamente le metriche per l'aggiornamento della UI:

* **Entrate Economiche:** `(ZoneCommerciali * 10 + ZoneIndustriali * 15) * MoltiplicatorePolicy`
* **Punteggio Ecologia:** `ValoreBase - (ZoneIndustriali * 5) + (Parchi * 3)`
* **Efficienza Sanitaria:** `(Ospedali / PopolazioneTotale) * 100 - (Inquinamento * 0.5)`
* **Efficienza Sicurezza:** `(StazioniPolizia / PopolazioneTotale) * 100 - (ZoneIndustriali * 0.2)`
* **Indice di Felicità:** `(Occupazione * 0.5) + (Parchi * 1.2) - (Inquinamento * 2.0) + (Sanità * 0.4) + (Sicurezza * 0.4)`

Come dimostrato dalle equazioni centrali, la **Sanità** e la **Sicurezza** non sono fini a sé stesse, ma alterano il bilanciamento ultimo della Felicità, costringendo l'utente a calibrare le proprie ambizioni espansionistiche.

### 4.3 Gestione delle Politiche ed Eventi Casuali
The loop iterativo di base può essere modificato radicalmente in corsa:
* **Politiche (Scelta Strategica):** Modificando la "costituzione cittadina", il calcolo di default viene sovrascritto. Ad esempio, dichiarando una *Politica Industriale*, l'algoritmo moltiplicherà in modo esponenziale i rendimenti del lavoro e i profitti commerciali, demolendo tuttavia il calcolatore ecologico.
* **Eventi Casuali (Imprevisti RNG):** Ad ogni spunta di Tick, il generatore pseudocasuale può alterare forzatamente lo stato del mondo: abbattendo entrate tramite una temporanea *Crisi Economica*, oppure colpendo fisicamente gli edifici tramite un *Disastro Meteoritico*, che li riporta allo stato *Inattivo*.

---

## 5. Architettura Tecnica e Persistenza dei Dati

L'ingegnerizzazione del codice non è stata realizzata su strati confusi, ma si poggia su architetture standard e *Design Pattern* del gruppo GoF (Gang of Four). 

### Pattern Architetturale: MVC (Model-View-Controller)
Il codice sorgente si distanzia dall'implementazione BCE pura menzionata in precedenti versioni sperimentali e si stabilizza sul pattern MVC per un forte e garantito disaccoppiamento:
* **Model (`domain`):** Le classi come `StatoCitta`, `UrbanGrid` e le logiche delle celle. È il vero cuore della città ed è testabile indipendentemente dall'estetica, senza alcuna reference alle classi di JavaFX.
* **View (`ui`):** Esclusivamente dedicata al disegno e alla cattura degli eventi di JavaFX. Riceve istruzioni e renderizza.
* **Controller (`core` e `infrastructure`):** Intercetta i comandi dalla View, applica le regole fisiche e di costruzione (`BuilderValidator`) e coordina la progressione del tempo (`SimulationEngine`).

### Design Pattern Operativi
Per assicurare estensibilità e manutenibilità al codice, sono state integrate le seguenti scelte implementative:
* **Strategy Pattern:** Usato per isolare la gestione delle Politiche Cittadine e degli Eventi Casuali. Questo pattern garantisce di poter iniettare algoritmi differenti di calcolo a runtime (es. `PoliticaAmbientaleStrategy` o eventi imprevisti) senza modificare o intasare l'engine con istruzioni *if/else* infinite.
* **Factory Pattern:** Delega la responsabilità per l'istanziazione centralizzata delle varie tipologie strutturali (es. classe `UrbanEntityFactory`). Serve a snellire e nascondere la complessità di creazione quando l'utente richiede di "costruire" un nuovo blocco sulla mappa.
* **Observer Pattern:** Cruciale per la dinamica MVC. Ogni volta che il `SimulationEngine` esegue un Tick terminando le sue operazioni matematiche, avverte in modo asincrono la View (mediante notifica Observer) della necessità di "ridisegnare" le bare delle statistiche, mantenendo i due layer slegati.

### Gestione Dati e Persistenza
L'architettura garantisce l'astrazione del filesystem al `PersistenceManager`. Sfruttando la libreria Jackson (o Gson), le istanze fisiche e astratte (oggetti griglia, monete e turni passati) subiscono una serializzazione complessa che li trasforma in blocchi testo nativi del formato standard **JSON**. Ciò permette il caricamento della simulazione ricreando il mondo di gioco intero con un tasso di regressione nullo (100% fedeltà dallo snapshot salvato). Fra le proprietà persistite sono presenti gli stati `Active/Inattivo` delle varie celle.

---

## 6. Strumenti AI e IDE Utilizzati

L'ambiente di sviluppo raccomandato e impiegato per la stesura materiale del progetto è **Visual Studio Code (VS Code)**, in virtù della sua integrazione avanzata delle estensioni Maven for Java e Java Debugger.

In tutte le fasi critiche del ciclo di vita del software sono stati integrati assistenti *Large Language Model (LLM)*, specificamente **Google Gemini** affiancato dall'agente autonomo **Antigravity**. Essi hanno fornito un supporto metodico e determinante principalmente in questi rami d'azione:
* **Quality Assurance (Test Automation):** Gemini e Antigravity hanno coadiuvato la formulazione di parametri matematici ostili, ingegnerizzando una solidissima suite JUnit 5 di oltre 45 Unit e System Test. L'AI ha scoperto edge case occulti (divisione per zero, ricalcoli difettosi dei range) e testato i limiti architetturali.
* **Requirement Engineering (BDD):** Entrambi gli strumenti hanno supportato lo stesura di rigorose *Acceptance Criteria* (BDD) basate sulle originarie *User Stories* dell'applicativo, aiutando a tracciare la documentazione funzionale fino all'ultimo *commit* del codice.
* **Refactoring e Pattern Check:** Revisioni repentine sul codice sorgente volte all'implementazione pulita dell'architettura MVC, assicurandosi che lo strato di View JavaFX non inquinasse le librerie puramente logiche interne al dominio.
* **Visualizzazione del Design:** Supporto avanzato alla traduzione logica delle relazioni di classe in formato diagrammatico *Mermaid UML*, producendo documentazione grafica istantanea e chiara.

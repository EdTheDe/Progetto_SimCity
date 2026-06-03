# MANUALE DI PROGETTO DEFINITIVO: CITYLOGIC

## 1. DESCRIZIONE AD ALTO LIVELLO DEL PROGETTO
Citylogic è un simulatore urbano gestionale rule-based incentrato sulla gestione strategica della crescita urbana. Il software permette di pianificare una città su una griglia logica fissa di **24x16** (pari a un totale di **384 celle**). L'obiettivo è ottimizzare 7 metriche chiave: Finanze, Popolazione, Felicità, Lavoro, Sicurezza, Sanità ed Ecologia. L'avanzamento è scandito da un sistema a turni discreti (Tick) che ricalcola l'intero ecosistema.

## 2. ISTRUZIONI SU COME INSTALLARE E LANCIARE IL SOFTWARE
* **Requisiti:** Maven installato e configurato nel PATH di sistema.
* **Installazione:** 1. Clonare la repository da GitHub.
  2. Aprire il terminale nella directory radice del progetto.
  3. Eseguire `mvn clean install` per risolvere le dipendenze e compilare.
* **Lancio:** Eseguire `mvn javafx:run "`.

## 3. AMBIENTI DI ESECUZIONE E VINCOLI TECNICI
* **Linguaggio:** Java.
* **Versione Java:** JDK 17 o superiore.
* **Architettura:** Pattern Boundary-Control-Entity (BCE) per la separazione dei layer.
* **Pattern GoF:** Strategy (Politiche), Factory (Edifici), Observer (Aggiornamento UI).

## 4. LOGICA DI CALCOLO (BUSINESS RULES)
Il Simulation Engine applica le seguenti formule ad ogni Tick per determinare l'andamento della città:
* **Entrate:** `(ZoneCommerciali * 10 + ZoneIndustriali * 15) * MoltiplicatorePolicy`
* **Ecologia:** `ValoreBase - (ZoneIndustriali * 5) + (Parchi * 3)`
* **Sanità:** `(Ospedali / PopolazioneTotale) * 100 - (Inquinamento * 0.5)`
* **Sicurezza:** `(StazioniPolizia / PopolazioneTotale) * 100 - (ZoneIndustriali * 0.2)`
* **Felicità:** `(Occupazione * 0.5) + (Parchi * 1.2) - (Inquinamento * 2.0) + (Sanità * 0.4) + (Sicurezza * 0.4)`

## 5. MACCHINA A STATI DEGLI EDIFICI (LIFE CYCLE)
Ogni cella della griglia logica segue rigorosamente tre stati di transizione:
* **EMPTY:** Cella disponibile per la costruzione.
* **DEVELOPING:** L'edificio è stato posizionato sulla mappa ma non soddisfa i requisiti di validità del `BuilderValidator`. La struttura resta inattiva (senza generare tasse o bonus) se:
  1. Manca il collegamento stradale diretto.
  2. Si trova fuori dal raggio di copertura delle reti idriche o elettriche.
  3. *Vincolo Edifici Statali:* Manca la copertura di una stazione dei Pompieri, di un posto di Polizia o di un Ospedale entro un raggio d'azione 
  4. *Ripristino da Disastri:* Strutture colpite da eventi catastrofici (es. Pioggia di Meteoriti) perdono i requisiti di validità e regrediscono automaticamente in questo stato fino alla ricostruzione delle reti di servizio.
* **ACTIVE:** L'edificio è connesso correttamente a tutte le infrastrutture e ai servizi di raggio, genera tasse e influenza le metriche globali.

## 6. PRINCIPALI FUNZIONI E LIBRERIE RIUTILIZZATE
* **Jackson/Gson:** Serializzazione JSON per il `PersistenceManager`.
* **JavaFX:** Gestione della dashboard grafica, delle reti sui pannelli di destra e degli eventi UI.
* **JUnit 5:** Suite di test automatici per la validazione della logica di dominio.

## 7. INDICAZIONE DI PRINCIPALI API ESTERNE UTILIZZATE
Il software è standalone. Non utilizza API REST esterne. La persistenza dei dati (Salvataggio/Caricamento) avviene tramite l'accesso diretto al FileSystem locale in formato JSON.

## 8. STRUMENTI AI UTILIZZATI
Supporto del modello Gemini per:
* **Analisi Requisiti:** Definizione di 17 User Stories (KAN-4 a KAN-27).
* **Coding:** Verifica attraverso i test delle funzioni e aiuti generali nella stesura del codice.

## 9. GLOSSARIO TECNICO (UBIQUITOUS LANGUAGE)
* **TICK:** L'unità di tempo minima che scatena il ricalcolo delle metriche.
* **INFRASTRUTTURA:** Elementi di rete (tubi, cavi, strade) necessari al funzionamento delle Zone.
* **POLICY:** Strategia di calcolo globale (es. Politica Ambientale) che altera i parametri a runtime tramite pattern Strategy.
* **METRICA:** Indicatore numerico dello stato di salute della città (es. Ecologia).
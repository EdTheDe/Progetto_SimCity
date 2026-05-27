MANUALE DI PROGETTO DEFINITIVO: CITYLOGIC

1. DESCRIZIONE AD ALTO LIVELLO DEL PROGETTO
Citylogic è un simulatore urbano gestionale rule-based incentrato sulla gestione strategica della crescita urbana. Il software permette di pianificare una città su una griglia logica 20x20. L'obiettivo è ottimizzare 7 metriche chiave: Finanze, Popolazione, Felicità, Lavoro, Sicurezza, Sanità ed Ecologia. L'avanzamento è scandito da un sistema a turni discreti (Tick) che ricalcola l'intero ecosistema.

2. ISTRUZIONI SU COME INSTALLARE E LANCIARE IL SOFTWARE
- Requisiti: Maven installato e configurato nel PATH di sistema.
- Installazione: 
  1. Clonare la repository da GitHub.
  2. Aprire il terminale nella directory radice.
  3. Eseguire 'mvn clean install' per risolvere le dipendenze.
- Lancio: 
  Eseguire 'mvn exec:java -Dexec.mainClass="com.citylogic.Main"'.

3. AMBIENTI DI ESECUZIONE E VINCOLI TECNICI
- Linguaggio: Java.
- Versione Java: JDK 17 o superiore.
- Architettura: Pattern Boundary-Control-Entity (BCE) per la separazione dei layer.
- Pattern GoF: Strategy (Politiche), Factory (Edifici), Observer (Aggiornamento UI).

4. LOGICA DI CALCOLO (BUSINESS RULES)
Il Simulation Engine applica le seguenti formule semplificate ad ogni Tick:
- Entrate: (ZoneCommerciali * 10 + ZoneIndustriali * 15) * MoltiplicatorePolicy.
- Felicità: (Occupazione * 0.5) + (Parchi * 1.2) - (Inquinamento * 2.0).
- Ecologia: ValoreBase - (ZoneIndustriali * 5) + (Parchi * 3).
- Sanità: (Ospedali / PopolazioneTotale) * 100 - (Inquinamento * 0.5).

5. MACCHINA A STATI DEGLI EDIFICI (LIFE CYCLE)
Ogni cella della griglia segue questi stati logici:
- EMPTY: Cella disponibile per la costruzione.
- DEVELOPING: Edificio piazzato, in attesa di allacciamento reti (Acqua/Luce/Strade).
- ACTIVE: Edificio connesso, genera tasse e influenza le metriche.
- ABANDONED: Edificio privo di servizi o colpito da eventi negativi; bonus cessati.

6. PRINCIPALI FUNZIONI E LIBRERIE RIUTILIZZATE
- Jackson/Gson: Serializzazione JSON per il SaveLoadManager.
- JavaFX: Gestione della dashboard grafica e degli eventi UI.
- JUnit 5: Suite di test per la validazione della logica di dominio.

7. INDICAZIONE DI PRINCIPALI API ESTERNE UTILIZZATE
Il software è standalone. Non utilizza API REST esterne. La persistenza dei dati (Salvataggio/Caricamento) avviene tramite l'accesso diretto al FileSystem locale in formato JSON.

8. STRUMENTI AI UTILIZZATI
Supporto del modello Gemini 3 per:
- Analisi Requisiti: Definizione di 17 User Stories (KAN-4 a KAN-27).
- Coding: Verifica attraverso i test delle funzioni e aiuti generali nella stesura del codice
9. GLOSSARIO TECNICO (UBIQUITOUS LANGUAGE)
- TICK: L'unità di tempo minima che scatena il ricalcolo delle metriche.
- INFRASTRUTTURA: Elementi di rete (tubi, cavi, strade) necessari al funzionamento delle Zone.
- POLICY: Strategia di calcolo globale (es. Tassa Ambientale) che altera i parametri a runtime.
- METRICA: Indicatore numerico dello stato di salute della città (es. Ecologia).


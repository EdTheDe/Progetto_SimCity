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
3. **Costruisci edifici e zone**. Se un edificio appena piazzato non ha i requisiti necessari, rimarrà in uno stato di *Inattivo* finché non verrà coperto dai servizi.
4. **Avanza di un Tick** per processare il mese: incassa le tasse, paga le spese di mantenimento delle infrastrutture e osserva i cambiamenti.
5. **Leggi l'HUD e i Pannelli**: monitora costantemente le 7 metriche globali, il livello di felicità. Ispeziona gli edifici guardando il pannello di destra per capirne lo stato.
6. **Reagisci agli imprevisti**: gestisci eventi casuali (Guerre, Piogge di Meteoriti, Crisi Economiche) e attiva *Politiche Cittadine* (es. Tassa Ambientale) per alterare strategicamente le regole del gioco.

## Documentazione e Manuale

Per le istruzioni dettagliate su **come installare e avviare il gioco**, la spiegazione minuziosa dell'**interfaccia e dei comandi**, il glossario e i dettagli sui pattern architetturali utilizzati, ti invitiamo a leggere il **[Manuale Utente completo](manuale_utente.md)**.

## Architettura e Logica delle Cartelle

Il progetto rispetta una separazione rigorosa MVC (Model-View-Controller) ed è organizzato all'interno di `src/main/java/citylogic` con la seguente alberatura:

```text
SimCity/
├── Documento_di_design/      # Documentazione UML e grafici
│   ├── Code/                 #   Sorgenti in linguaggio Mermaid
│   └── Immagini/             #   Esportazioni grafiche (Class, Domain, ISD, SSD)
├── src/                      # Codice sorgente del simulatore
│   ├── main/java/citylogic/  #   Codice di produzione (Architettura MVC)
│   │   ├── core/             #     (Controller) Motore dei Tick, eventi, validazione e politiche
│   │   ├── domain/           #     (Model) Entità urbane, stato cittadino e griglia logica
│   │   ├── infrastructure/   #     (Persistence) Gestione salvataggi I/O in JSON
│   │   └── ui/               #     (View) Interfaccia grafica in JavaFX (Observer)
│   ├── main/resources/       #   Asset grafici (.png, .jpeg per le texture)
│   └── test/java/citylogic/  #   Suite estesa di Unit Test (JUnit 5)
├── Acceptance_Criteria_SimCity.txt # Criteri di Accettazione BDD
├── manuale_utente.md         # Documentazione utente e architetturale dettagliata
├── pom.xml                   # Configurazione Maven e dichiarazione dipendenze
├── README.md                 # Presentazione sintetica del progetto
├── system_test_report.md     # Report di Quality Assurance
└── User Stories.csv          # Requisiti Agili iniziali del progetto
```

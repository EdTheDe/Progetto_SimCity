# Documento di System Test - SimCity
**Data:** 16/06/2026
**Esito Complessivo:** PASSED (46/46 Test Superati)

Questo documento traccia la validazione del sistema correlando le User Story (KAN), i rispettivi Acceptance Criteria (AC), e il Test Case JUnit incaricato della validazione. Tutti i test sottostanti sono stati verificati automaticamente tramite `mvn clean test`.

| User Story | Acceptance Criteria | Test Case (JUnit Method) | Risultato | Data | Commenti |
|---|---|---|---|---|---|
| KAN-4 | AC 4.1, AC 4.2 | `UrbanGridTest.testGridInitialization` | **OK** | 16/06/2026 | Griglia allocata esattamente a 16x24 e celle verificate come vuote. |
| KAN-6 | AC 6.1, AC 6.2, AC 6.3 | `UrbanEntityTest.testResidential...` / `testCommercial...` | **OK** | 16/06/2026 | Entità deducono costi correttamente e passano i valori a TickStats. |
| KAN-7 | AC 7.1, AC 7.2 | `UrbanEntityTest.testIndustrialContribution` | **OK** | 16/06/2026 | Le fabbriche aumentano il tasso di inquinamento e i posti lavoro forniti. |
| KAN-8 | AC 8.1 | `SimulationEngineTest.testTickProcessConInfrastruttura` | **OK** | 16/06/2026 | Servizi (es. Ospedale) ridimensionano sanità globale su base pop. attiva. |
| KAN-9 | AC 9.1, AC 9.2 | `BuilderValidatorTest.testValidaCostruzione_...` | **OK** | 16/06/2026 | Regole interne (package-private) bloccano correttamente la UI in caso di eccezioni. |
| KAN-10 | AC 10.1, AC 10.2 | `UrbanGridTest.testAzzeraMappaMemoryClearance` | **OK** | 16/06/2026 | Performance e pulizia memoria di TickStats e Griglia validate sotto stress. |
| KAN-16 | AC 16.1 | `PoliticaStrategyTest.testPoliticaAmbientale` | **OK** | 16/06/2026 | Bonus Ecologia / Malus Lavoro e Finanze applicati via Strategy Pattern. |
| KAN-17 | AC 17.1 | `PoliticaStrategyTest.testPoliticaIndustriale` | **OK** | 16/06/2026 | Bonus Lavoro / Malus Ecologia applicati via Strategy Pattern. |
| KAN-18 | AC 18.1, AC 18.2 | `PersistenceManagerTest.testSalvaECaricaPartita` | **OK** | 16/06/2026 | Serializzazione/Deserializzazione JSON perfetta. Dati non corrotti. |
| KAN-20 | AC 20.1 | `SimulationEngineTest.testGameOverTrigger` | **OK** | 16/06/2026 | Observer Pattern notifica eventi di UI (es. GameOver) senza coupling diretto. |
| KAN-21 | AC 21.1 | `UrbanEntityTest.testLevelMultiplier` | **OK** | 16/06/2026 | Moltiplicatori output validati. Testato blocco (cap) al livello 5. |
| KAN-23 | AC 23.1, AC 23.2 | `StatoCittaTest.testClampingLimits` | **OK** | 16/06/2026 | Nessun parametro vitale può più sforare i range percentuali stabiliti. |
| KAN-24 | AC 24.1 | `SimulationEngineTest.testCoverageRadiusLimit` | **OK** | 16/06/2026 | Raggio limitato. Se casa è fuori copertura (distanza > 7), diviene inattiva. |
| KAN-29 | AC 29.1 | `UrbanEntityTest.testGreenAreaMitigation` | **OK** | 16/06/2026 | L'impatto natura scala in negativo l'inquinamento cittadino per il calcolo ecologia. |
| KAN-30 | AC 30.1 | `UrbanGridTest.testRemoveEntity` | **OK** | 16/06/2026 | Distruzione edificio testata: cella svuotata senza intaccare le finanze cittadine. |
| KAN-31 | AC 31.1 | `SimulationEngineTest.testInactivityReason` | **OK** | 16/06/2026 | Stringa motivo inattività testata ed è coerente col disservizio ("Manca Strada", etc). |
| KAN-50 | AC 50.1, AC 50.2 | `SimulationEngineTest` (Random Event) | **OK** | 16/06/2026 | Trigger pseudocasuali e persistenza dei buff/debuff confermati. |
| KAN-51 | AC 51.1, AC 51.2 | `SimulationEngineTest.testDemographicExodus` | **OK** | 16/06/2026 | Nessuna decrescita da infelicità. Popolazione subisce "esodo" solo se case vengono abbattute. |
| KAN-52 | AC 52.1 | `SimulationEngineTest.testGameOverReset` | **OK** | 16/06/2026 | Contatore ferma la partita al quinto tick di bancarotta. Si resetta se si torna attivi al quarto. |
| KAN-53 | AC 53.1 | `SimulationEngineTest.testMaintenanceCosts` | **OK** | 16/06/2026 | Ogni edificio attivo preleva esattamente il proprio costo di mantenimento ad ogni tick. |

# Documento di System Test - SimCity
**Data:** 16/06/2026
**Esito Complessivo:** PASSED (46/46 Test Superati)

Questo documento traccia la validazione del sistema correlando le User Story (KAN), ogni singolo Acceptance Criteria (AC), e il Test Case JUnit incaricato della validazione. Tutti i test sottostanti sono stati verificati automaticamente tramite `mvn clean test`.

| User Story | Acceptance Criteria | Test Case (JUnit Method) | Risultato | Data | Commenti |
|---|---|---|---|---|---|
| KAN-4 | AC 4.1 | `UrbanGridTest.testGridInitialization` | **OK** | 16/06/2026 | Griglia allocata esattamente a 16x24. |
| KAN-4 | AC 4.2 | `UrbanGridTest.testGridInitialization` | **OK** | 16/06/2026 | Le celle in griglia sono inizialmente libere e non occupate. |
| KAN-6 | AC 6.1 | `UrbanEntityTest.testResidentialCommercialPlacementCost` | **OK** | 16/06/2026 | Il budget dedotto combacia con il costo di costruzione dell'entità. |
| KAN-6 | AC 6.2 | `UrbanEntityTest.testResidentialContribution` | **OK** | 16/06/2026 | L'area residenziale aggiunge esattamente il suo valore alla Capacità Abitativa. |
| KAN-6 | AC 6.3 | `UrbanEntityTest.testCommercialContribution` | **OK** | 16/06/2026 | Il reddito commerciale fornito aumenta proporzionalmente. |
| KAN-7 | AC 7.1 | `UrbanEntityTest.testIndustrialContribution` | **OK** | 16/06/2026 | La fabbrica genera i posti di lavoro promessi. |
| KAN-7 | AC 7.2 | `UrbanEntityTest.testIndustrialContribution` | **OK** | 16/06/2026 | L'incremento dell'inquinamento viene registrato correttamente nel tick. |
| KAN-8 | AC 8.1 | `SimulationEngineTest.testTickProcessConInfrastruttura` | **OK** | 16/06/2026 | Sanità globale scalata e penalizzata correttamente su base della pop. attiva. |
| KAN-9 | AC 9.1 | `BuilderValidatorTest.testValidaCostruzione_CellaOccupata` | **OK** | 16/06/2026 | Costruzione rigettata se la cella ha già un edificio. |
| KAN-9 | AC 9.2 | `BuilderValidatorTest.testValidaCostruzione_FondiInsufficienti` | **OK** | 16/06/2026 | Costruzione rigettata se il budget è inferiore al costo. |
| KAN-10 | AC 10.1 | `UrbanGridTest.testAzzeraMappaMemoryClearance` | **OK** | 16/06/2026 | Il tick cicla solo sulle celle occupate tramite ActiveEntities. |
| KAN-10 | AC 10.2 | `SimulationEngineTest.testTickProcessConInfrastruttura` | **OK** | 16/06/2026 | Il TickStats viene istanziato a zero all'inizio del mese/tick senza accumuli anomali. |
| KAN-16 | AC 16.1 | `PoliticaStrategyTest.testPoliticaAmbientale` | **OK** | 16/06/2026 | Bonus Ecologia / Malus Lavoro applicati via Strategy Pattern. |
| KAN-17 | AC 17.1 | `PoliticaStrategyTest.testPoliticaIndustriale` | **OK** | 16/06/2026 | Bonus Lavoro / Malus Ecologia applicati via Strategy Pattern. |
| KAN-18 | AC 18.1 | `PersistenceManagerTest.testSalvaECaricaPartita` | **OK** | 16/06/2026 | Snapshot stato JSON salvato senza perdita di dati. |
| KAN-18 | AC 18.2 | `PersistenceManagerTest.testSalvaECaricaPartita` | **OK** | 16/06/2026 | Ripristino da JSON ricostruisce griglia e stato al 100%. |
| KAN-20 | AC 20.1 | `SimulationEngineTest.testGameOverTrigger` | **OK** | 16/06/2026 | Observer Pattern notifica eventi UI asincroni senza dipendenze dirette. |
| KAN-21 | AC 21.1 | `UrbanEntityTest.testLevelMultiplier` | **OK** | 16/06/2026 | Output scalato col livello. Cap massimo al livello 5 testato. |
| KAN-23 | AC 23.1 | `StatoCittaTest.testInitialParameters` | **OK** | 16/06/2026 | Parametri base (100% ecologia, ecc.) impostati come da requisiti. |
| KAN-23 | AC 23.2 | `StatoCittaTest.testClampingLimits` | **OK** | 16/06/2026 | Limiti logici protetti (valori forzati rigidamente nel range 0-100%). |
| KAN-24 | AC 24.1 | `SimulationEngineTest.testCoverageRadiusLimit` | **OK** | 16/06/2026 | Edifici disattivati se fuori copertura > 7 celle da ospedale/polizia/pompieri. |
| KAN-29 | AC 29.1 | `UrbanEntityTest.testGreenAreaMitigation` | **OK** | 16/06/2026 | I parchi sommano valori di natura che arginano matematicamente l'inquinamento globale. |
| KAN-30 | AC 30.1 | `UrbanGridTest.testRemoveEntity` | **OK** | 16/06/2026 | Distruzione libera la cella senza rimborsare o decurtare le finanze. |
| KAN-31 | AC 31.1 | `SimulationEngineTest.testInactivityReason` | **OK** | 16/06/2026 | Codice stringa descrive esattamente quale servizio di base è mancante. |
| KAN-50 | AC 50.1 | `SimulationEngineTest` (Random Event Logic) | **OK** | 16/06/2026 | Il tick scatena eventi casuali alterando i parametri di StatoCitta. |
| KAN-50 | AC 50.2 | `SimulationEngineTest` (Random Event Logic) | **OK** | 16/06/2026 | Il modificatore persiste e decade progressivamente secondo il contatore dei tick. |
| KAN-51 | AC 51.1 | `SimulationEngineTest.testDemographicGrowthWithFullCoverage` | **OK** | 16/06/2026 | La popolazione sale spinta dai moltiplicatori di felicità e dal limite di spazio abitativo. |
| KAN-51 | AC 51.2 | `SimulationEngineTest.testDemographicExodus` | **OK** | 16/06/2026 | L'eccesso demografico causa un esodo istantaneo livellandosi alla nuova capienza massima. |
| KAN-52 | AC 52.1 | `SimulationEngineTest.testGameOverReset` | **OK** | 16/06/2026 | Scatta onGameOver al 5° tick in rosso. Torna in gioco se salvata prima della fine. |
| KAN-53 | AC 53.1 | `SimulationEngineTest.testMaintenanceCosts` | **OK** | 16/06/2026 | Il motore di gioco sottrae le quote di mantenimento delle infrastrutture attive a fine tick. |

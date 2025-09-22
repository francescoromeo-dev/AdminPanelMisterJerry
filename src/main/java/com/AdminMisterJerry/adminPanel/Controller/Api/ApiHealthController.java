package com.AdminMisterJerry.adminPanel.Controller.Api;

import com.AdminMisterJerry.adminPanel.Service.ShoesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller per il monitoraggio della salute delle API
 * Fornisce endpoint per verificare lo stato del sistema
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ApiHealthController {

    @Autowired
    private ShoesService shoesService;

    /**
     * GET /api/health - Verifica lo stato di salute dell'API
     * @return Stato del sistema
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        
        try {
            // Test connessione database
            long totalShoes = shoesService.getGeneralStats().getTotalShoes();
            
            health.put("status", "UP");
            health.put("timestamp", LocalDateTime.now().toString());
            health.put("database", "CONNECTED");
            health.put("total_shoes", totalShoes);
            health.put("version", "1.0.0");
            health.put("environment", "production");
            
            return ResponseEntity.ok(health);
            
        } catch (Exception e) {
            health.put("status", "DOWN");
            health.put("timestamp", LocalDateTime.now().toString());
            health.put("database", "DISCONNECTED");
            health.put("error", e.getMessage());
            health.put("version", "1.0.0");
            
            return ResponseEntity.status(503).body(health);
        }
    }

    /**
     * GET /api/info - Informazioni generali sull'API
     * @return Informazioni del sistema
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> apiInfo() {
        Map<String, Object> info = new HashMap<>();
        
        info.put("name", "MisterJerry Shoes API");
        info.put("description", "API REST per la gestione del catalogo scarpe");
        info.put("version", "1.0.0");
        info.put("author", "AdminMisterJerry");
        info.put("contact", "info@misterjerry.it");
        info.put("documentation", "/api/docs");
        
        // Endpoint disponibili
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("GET /api/scarpe", "Lista tutte le scarpe");
        endpoints.put("GET /api/scarpe/{id}", "Dettagli scarpa per ID");
        endpoints.put("GET /api/scarpe/categoria/{categoria}", "Filtra per categoria");
        endpoints.put("GET /api/scarpe/stagione/{stagione}", "Filtra per stagione");
        endpoints.put("GET /api/scarpe/filtri", "Filtri multipli");
        endpoints.put("GET /api/scarpe/search", "Ricerca per codice");
        endpoints.put("GET /api/statistiche", "Statistiche generali");
        endpoints.put("GET /api/health", "Stato di salute");
        endpoints.put("GET /api/info", "Informazioni API");
        
        info.put("endpoints", endpoints);
        info.put("timestamp", LocalDateTime.now().toString());
        
        return ResponseEntity.ok(info);
    }

    /**
     * GET /api/docs - Documentazione rapida dell'API
     * @return Documentazione in formato JSON
     */
    @GetMapping("/docs")
    public ResponseEntity<Map<String, Object>> apiDocs() {
        Map<String, Object> docs = new HashMap<>();
        
        docs.put("title", "MisterJerry Shoes API - Documentazione");
        docs.put("version", "1.0.0");
        docs.put("baseUrl", "/api");
        
        // Esempi di utilizzo
        Map<String, Object> examples = new HashMap<>();
        examples.put("Tutte le scarpe", "GET /api/scarpe");
        examples.put("Scarpa specifica", "GET /api/scarpe/1");
        examples.put("Filtra bambini", "GET /api/scarpe/categoria/bambini");
        examples.put("Filtra estate", "GET /api/scarpe/stagione/primavera-estate");
        examples.put("Filtri combinati", "GET /api/scarpe/filtri?categoria=bambini&stagione=primavera-estate");
        examples.put("Ricerca codice", "GET /api/scarpe/search?q=MJ001");
        
        docs.put("examples", examples);
        
        // Schema dati
        Map<String, Object> schema = new HashMap<>();
        schema.put("id", "number - ID univoco");
        schema.put("codice", "string - Codice prodotto");
        schema.put("categoria", "string - bambini|primi-passi|cerimonia");
        schema.put("stagione", "string - primavera-estate|autunno-inverno");
        schema.put("immagine_principale", "string - URL immagine principale");
        schema.put("immagini", "array - Lista URL immagini");
        schema.put("numero_immagini", "number - Conteggio immagini");
        schema.put("data_creazione", "string - Data formato ISO");
        
        docs.put("schema", schema);
        docs.put("timestamp", LocalDateTime.now().toString());
        
        return ResponseEntity.ok(docs);
    }

    /**
     * GET /api/test - Endpoint di test
     * @return Risposta di test
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testEndpoint() {
        Map<String, Object> test = new HashMap<>();
        
        test.put("message", "API funzionante correttamente");
        test.put("timestamp", LocalDateTime.now().toString());
        test.put("status", "OK");
        test.put("method", "GET");
        test.put("endpoint", "/api/test");
        
        return ResponseEntity.ok(test);
    }
}
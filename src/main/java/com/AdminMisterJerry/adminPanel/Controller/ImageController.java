package com.AdminMisterJerry.adminPanel.Controller;

import com.AdminMisterJerry.adminPanel.Model.ImageEntity;
import com.AdminMisterJerry.adminPanel.Repositories.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/images")
public class ImageController {

    @Autowired
    private ImageRepository imageRepository;

    // Lista dei content type supportati per le immagini
    private final List<String> SUPPORTED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/bmp", "image/webp"
    );

    /**
     * Endpoint per caricare un'immagine singola nel database
     * @param file Il file immagine da caricare
     * @return ResponseEntity con messaggio di successo o errore
     */
    @PostMapping("/upload")
    @ResponseBody
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File vuoto. Seleziona un'immagine valida.");
        }

        // Verifica che il file sia un'immagine supportata
        String contentType = file.getContentType();
        if (contentType == null || !SUPPORTED_CONTENT_TYPES.contains(contentType)) {
            return ResponseEntity.badRequest()
                    .body("Formato file non supportato. Formati accettati: JPG, PNG, GIF, BMP, WEBP.");
        }

        // Verifica dimensione file (limite 10MB)
        if (file.getSize() > 10 * 1024 * 1024) {
            return ResponseEntity.badRequest()
                    .body("File troppo grande. Dimensione massima: 10MB.");
        }

        try {
            // Genera un nome file unico
            String originalFileName = file.getOriginalFilename();
            String fileName = generateUniqueFileName(originalFileName);

            // Crea l'entità immagine
            ImageEntity imageEntity = new ImageEntity(
                    fileName,
                    contentType,
                    file.getBytes()
            );

            // Salva nel database
            ImageEntity savedImage = imageRepository.save(imageEntity);

            return ResponseEntity.ok("Immagine caricata con successo. ID: " + savedImage.getId());

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore durante il caricamento dell'immagine: " + e.getMessage());
        }
    }

    /**
     * Endpoint per caricare multiple immagini
     * @param files Array di file immagini da caricare
     * @return ResponseEntity con messaggio di successo o errore
     */
    @PostMapping("/upload-multiple")
    @ResponseBody
    public ResponseEntity<String> uploadMultipleImages(@RequestParam("files") MultipartFile[] files) {
        
        if (files.length == 0) {
            return ResponseEntity.badRequest().body("Nessun file selezionato.");
        }

        StringBuilder result = new StringBuilder("Immagini caricate:\n");
        int successCount = 0;
        int failCount = 0;

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                result.append("- File vuoto ignorato\n");
                failCount++;
                continue;
            }

            try {
                // Verifica formato
                String contentType = file.getContentType();
                if (contentType == null || !SUPPORTED_CONTENT_TYPES.contains(contentType)) {
                    result.append("- ").append(file.getOriginalFilename())
                            .append(": formato non supportato\n");
                    failCount++;
                    continue;
                }

                // Verifica dimensione
                if (file.getSize() > 10 * 1024 * 1024) {
                    result.append("- ").append(file.getOriginalFilename())
                            .append(": file troppo grande (max 10MB)\n");
                    failCount++;
                    continue;
                }

                // Salva nel database
                String fileName = generateUniqueFileName(file.getOriginalFilename());
                ImageEntity imageEntity = new ImageEntity(fileName, contentType, file.getBytes());
                ImageEntity savedImage = imageRepository.save(imageEntity);

                result.append("- ").append(fileName)
                        .append(" (ID: ").append(savedImage.getId()).append(")\n");
                successCount++;

            } catch (Exception e) {
                result.append("- ").append(file.getOriginalFilename())
                        .append(": errore - ").append(e.getMessage()).append("\n");
                failCount++;
            }
        }

        result.append("\nTotale: ").append(successCount).append(" caricate, ")
                .append(failCount).append(" fallite.");

        return ResponseEntity.ok(result.toString());
    }

    /**
     * Endpoint per scaricare un'immagine dal database tramite ID
     * @param id L'ID dell'immagine nel database
     * @return ResponseEntity con i dati dell'immagine
     */
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> downloadImage(@PathVariable Long id) {
        
        Optional<ImageEntity> imageOptional = imageRepository.findById(id);
        
        if (imageOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ImageEntity image = imageOptional.get();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(image.getContentType()));
        headers.setContentLength(image.getFileSize());
        headers.setContentDispositionFormData("inline", image.getFileName());
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(image.getImageData());
    }

    /**
     * Endpoint per scaricare un'immagine dal database tramite nome file
     * @param fileName Il nome del file
     * @return ResponseEntity con i dati dell'immagine
     */
    @GetMapping("/file/{fileName}")
    @ResponseBody
    public ResponseEntity<byte[]> downloadImageByFileName(@PathVariable String fileName) {
        
        Optional<ImageEntity> imageOptional = imageRepository.findByFileName(fileName);
        
        if (imageOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ImageEntity image = imageOptional.get();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(image.getContentType()));
        headers.setContentLength(image.getFileSize());
        headers.setContentDispositionFormData("inline", image.getFileName());
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(image.getImageData());
    }

    /**
     * Endpoint per eliminare un'immagine
     * @param id L'ID dell'immagine da eliminare
     * @return ResponseEntity con messaggio di conferma
     */
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteImage(@PathVariable Long id) {
        
        if (!imageRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        imageRepository.deleteById(id);
        return ResponseEntity.ok("Immagine eliminata con successo.");
    }

    /**
     * Endpoint per ottenere informazioni su tutte le immagini
     * @return ResponseEntity con la lista delle immagini
     */
    @GetMapping("/list")
    @ResponseBody
    public ResponseEntity<List<ImageEntity>> listImages() {
        List<ImageEntity> images = imageRepository.findAllByOrderByCreatedAtDesc();
        
        // Rimuovi i dati binari dalla risposta per ridurre il payload
        images.forEach(image -> image.setImageData(null));
        
        return ResponseEntity.ok(images);
    }

    /**
     * Endpoint per ottenere statistiche sulle immagini
     * @return ResponseEntity con le statistiche
     */
    @GetMapping("/stats")
    @ResponseBody
    public ResponseEntity<String> getImageStats() {
        long totalImages = imageRepository.count();
        Long totalSize = imageRepository.getTotalImagesSize();
        
        if (totalSize == null) {
            totalSize = 0L;
        }
        
        String sizeFormatted = formatFileSize(totalSize);
        
        String stats = String.format(
                "Statistiche immagini:\n" +
                "- Totale immagini: %d\n" +
                "- Spazio occupato: %s",
                totalImages, sizeFormatted
        );
        
        return ResponseEntity.ok(stats);
    }

    /**
     * Pagina di esempio per testare l'upload (opzionale)
     * @param model Il modello per la vista
     * @return Il nome della vista
     */
    @GetMapping("/upload-page")
    public String uploadPage(Model model) {
        return "image-upload"; // Devi creare questo template HTML
    }

    // Metodi di utilità privati

    /**
     * Genera un nome file unico aggiungendo timestamp
     */
    private String generateUniqueFileName(String originalFileName) {
        if (originalFileName == null || originalFileName.isEmpty()) {
            originalFileName = "image.jpg";
        }
        
        String timestamp = String.valueOf(new Date().getTime());
        String extension = "";
        
        int lastDotIndex = originalFileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            extension = originalFileName.substring(lastDotIndex);
            originalFileName = originalFileName.substring(0, lastDotIndex);
        }
        
        return timestamp + "_" + originalFileName + extension;
    }

    /**
     * Formatta la dimensione del file in formato leggibile
     */
    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }
}
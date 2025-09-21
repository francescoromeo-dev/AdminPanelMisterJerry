package com.AdminMisterJerry.adminPanel.Controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.AdminMisterJerry.adminPanel.Model.ImageEntity;
import com.AdminMisterJerry.adminPanel.Model.Shoes;
import com.AdminMisterJerry.adminPanel.Model.ShoesDto;
import com.AdminMisterJerry.adminPanel.Repositories.ImageRepository;
import com.AdminMisterJerry.adminPanel.Repositories.ShoesRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/shoes")
public class ShoesController {

    @Autowired
    private ShoesRepository shoesRepo;
    
    @Autowired
    private ImageRepository imageRepository;

    // Lista dei content type supportati per le immagini
    private final List<String> SUPPORTED_CONTENT_TYPES = List.of(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/bmp", "image/webp"
    );

    @GetMapping({ "", "/" })
    public String getShoes(@RequestParam(required = false) String season, 
                          @RequestParam(required = false) String category,
                          Model model) {
        
        List<Shoes> shoes;
        
        // Logica filtri
        if (season != null && !season.isEmpty() && 
            category != null && !category.isEmpty()) {
            // Filtri stagione + categoria
            shoes = shoesRepo.findBySeasonAndCategoryOrderByIdDesc(season, category);
        } else if (season != null && !season.isEmpty()) {
            // Solo filtro stagione
            shoes = shoesRepo.findBySeasonOrderByIdDesc(season);
        } else if (category != null && !category.isEmpty()) {
            // Solo filtro categoria
            shoes = shoesRepo.findByCategoryOrderByIdDesc(category);
        } else {
            // Nessun filtro, mostra tutte
            shoes = shoesRepo.findAll(Sort.by(Sort.Direction.DESC, "id"));
        }

        model.addAttribute("shoes", shoes);
        return "shoes/shoes-manager";
    }

    // GET: Mostra il form per creare una nuova scarpa
    @GetMapping("/shoes-create")
    public String showCreateShoesForm(Model model) {
        ShoesDto shoesDto = new ShoesDto();
        model.addAttribute("shoesDto", shoesDto);
        return "shoes/shoes-create";
    }

    // POST: Processa il form per creare una nuova scarpa
    @PostMapping("/shoes-create")
    public String createShoes(@Valid @ModelAttribute ShoesDto shoesDto, BindingResult result, Model model) {

        // VALIDAZIONE: Verifica se il codice esiste già
        if (shoesDto.getCode() != null && !shoesDto.getCode().isEmpty()) {
            Optional<Shoes> existingShoes = shoesRepo.findByCode(shoesDto.getCode());
            if (existingShoes.isPresent()) {
                result.addError(new FieldError("shoesDto", "code", shoesDto.getCode(), false, null, null,
                        "Esiste già una scarpa con questo codice"));
            }
        }

        // Validazione immagini
        if (!shoesDto.hasImages()) {
            result.addError(new FieldError("shoesDto", "imageFiles", "", false, null, null,
                    "Carica almeno un'immagine"));
        }

        // Se ci sono errori, torna al form
        if (result.hasErrors()) {
            return "shoes/shoes-create";
        }

        List<Long> savedImageIds = new ArrayList<>();
        
        try {
            // Salva tutte le immagini nel database
            for (MultipartFile imageFile : shoesDto.getNonEmptyImageFiles()) {
                
                // Validazione formato immagine
                String contentType = imageFile.getContentType();
                if (contentType == null || !SUPPORTED_CONTENT_TYPES.contains(contentType)) {
                    result.addError(new FieldError("shoesDto", "imageFiles", "", false, null, null,
                            "Formato immagine non supportato: " + imageFile.getOriginalFilename()));
                    return "shoes/shoes-create";
                }

                // Validazione dimensione (max 10MB)
                if (imageFile.getSize() > 10 * 1024 * 1024) {
                    result.addError(new FieldError("shoesDto", "imageFiles", "", false, null, null,
                            "File troppo grande (max 10MB): " + imageFile.getOriginalFilename()));
                    return "shoes/shoes-create";
                }

                // Genera nome file unico
                String fileName = generateUniqueFileName(imageFile.getOriginalFilename());

                // Crea e salva l'entità immagine
                ImageEntity imageEntity = new ImageEntity(
                    fileName,
                    contentType,
                    imageFile.getBytes()
                );

                ImageEntity savedImage = imageRepository.save(imageEntity);
                savedImageIds.add(savedImage.getId());
            }

        } catch (Exception e) {
            System.out.println("Errore nel salvare le immagini: " + e.getMessage());
            result.addError(new FieldError("shoesDto", "imageFiles", "", false, null, null, 
                    "Errore nel salvare le immagini: " + e.getMessage()));
            return "shoes/shoes-create";
        }

        // Crea e salva la nuova scarpa
        Shoes shoes = new Shoes();
        shoes.setCode(shoesDto.getCode());
        shoes.setSeason(shoesDto.getSeason());
        shoes.setCategory(shoesDto.getCategory());
        shoes.setImageIdsList(savedImageIds); // Imposta la lista degli ID delle immagini
        shoes.setCreatedAt(new Date());

        shoesRepo.save(shoes);

        return "redirect:/shoes";
    }

    // GET: Mostra il form per modificare una scarpa
    @GetMapping("/shoes-edit")
    public String showEditShoesForm(Model model, @RequestParam int id) {
        Shoes shoes = shoesRepo.findById(id).orElse(null);
        if (shoes == null) {
            return "redirect:/shoes";
        }

        ShoesDto shoesDto = new ShoesDto();
        shoesDto.setCode(shoes.getCode());
        shoesDto.setSeason(shoes.getSeason());
        shoesDto.setCategory(shoes.getCategory());

        model.addAttribute("shoes", shoes);
        model.addAttribute("shoesDto", shoesDto);

        return "shoes/shoes-edit";
    }

    // POST: Processa la modifica di una scarpa
    @PostMapping("/shoes-edit")
    public String editShoes(Model model, @RequestParam int id, @Valid @ModelAttribute ShoesDto shoesDto,
            BindingResult result) {

        Shoes shoes = shoesRepo.findById(id).orElse(null);
        if (shoes == null) {
            return "redirect:/shoes";
        }

        model.addAttribute("shoes", shoes);

        // VALIDAZIONE: Controlla se il codice esiste già (escluso l'elemento corrente)
        if (shoesDto.getCode() != null && !shoesDto.getCode().isEmpty()) {
            Optional<Shoes> existingShoes = shoesRepo.findByCode(shoesDto.getCode());
            if (existingShoes.isPresent() && existingShoes.get().getId() != id) {
                result.addError(new FieldError("shoesDto", "code", shoesDto.getCode(), false, null, null,
                        "Esiste già una scarpa con questo codice"));
            }
        }

        if (result.hasErrors()) {
            return "shoes/shoes-edit";
        }

        // Gestione nuove immagini
        if (shoesDto.hasImages()) {
            // Elimina le vecchie immagini dal database
            List<Long> oldImageIds = shoes.getImageIdsList();
            
            for (Long oldImageId : oldImageIds) {
                try {
                    imageRepository.deleteById(oldImageId);
                } catch (Exception e) {
                    System.out.println("Errore nell'eliminare la vecchia immagine ID " + oldImageId + ": " + e.getMessage());
                }
            }

            // Salva le nuove immagini
            List<Long> newImageIds = new ArrayList<>();
            try {
                for (MultipartFile imageFile : shoesDto.getNonEmptyImageFiles()) {
                    
                    // Validazione formato immagine
                    String contentType = imageFile.getContentType();
                    if (contentType == null || !SUPPORTED_CONTENT_TYPES.contains(contentType)) {
                        result.addError(new FieldError("shoesDto", "imageFiles", "", false, null, null,
                                "Formato immagine non supportato: " + imageFile.getOriginalFilename()));
                        return "shoes/shoes-edit";
                    }

                    // Validazione dimensione (max 10MB)
                    if (imageFile.getSize() > 10 * 1024 * 1024) {
                        result.addError(new FieldError("shoesDto", "imageFiles", "", false, null, null,
                                "File troppo grande (max 10MB): " + imageFile.getOriginalFilename()));
                        return "shoes/shoes-edit";
                    }

                    // Genera nome file unico
                    String fileName = generateUniqueFileName(imageFile.getOriginalFilename());

                    // Crea e salva l'entità immagine
                    ImageEntity imageEntity = new ImageEntity(
                        fileName,
                        contentType,
                        imageFile.getBytes()
                    );

                    ImageEntity savedImage = imageRepository.save(imageEntity);
                    newImageIds.add(savedImage.getId());
                }

                shoes.setImageIdsList(newImageIds);
                
            } catch (Exception e) {
                System.out.println("Errore nel salvare le nuove immagini: " + e.getMessage());
                result.addError(new FieldError("shoesDto", "imageFiles", "", false, null, null,
                        "Errore nel salvare le immagini: " + e.getMessage()));
                return "shoes/shoes-edit";
            }
        }

        // Aggiorna i dettagli delle scarpe
        shoes.setCode(shoesDto.getCode());
        shoes.setSeason(shoesDto.getSeason());
        shoes.setCategory(shoesDto.getCategory());

        shoesRepo.save(shoes);

        return "redirect:/shoes";
    }

    // GET: Elimina una scarpa
    @GetMapping("/delete")
    public String deleteShoes(@RequestParam int id) {
        Shoes shoes = shoesRepo.findById(id).orElse(null);

        if (shoes != null) {
            // Elimina anche tutte le immagini dal database
            List<Long> imageIds = shoes.getImageIdsList();
            
            for (Long imageId : imageIds) {
                try {
                    imageRepository.deleteById(imageId);
                } catch (Exception e) {
                    System.out.println("Errore nell'eliminare l'immagine ID " + imageId + ": " + e.getMessage());
                }
            }

            shoesRepo.delete(shoes);
        }

        return "redirect:/shoes";
    }
    
    // Metodo helper per generare nomi file unici
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
}
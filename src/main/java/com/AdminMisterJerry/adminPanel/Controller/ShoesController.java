package com.AdminMisterJerry.adminPanel.Controller;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

import com.AdminMisterJerry.adminPanel.Model.Shoes;
import com.AdminMisterJerry.adminPanel.Model.ShoesDto;
import com.AdminMisterJerry.adminPanel.Repositories.ShoesRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/shoes")
public class ShoesController {

    @Autowired
    private ShoesRepository shoesRepo;

    @GetMapping({ "", "/" })
    public String getShoes(@RequestParam(required = false) String season, 
                          @RequestParam(required = false) String category,
                          @RequestParam(required = false) String color,
                          Model model) {
        
        List<Shoes> shoes;
        
        // Logica filtri aggiornata con colore
        if (season != null && !season.isEmpty() && 
            category != null && !category.isEmpty() && 
            color != null && !color.isEmpty()) {
            // Tutti e tre i filtri attivi
            shoes = shoesRepo.findBySeasonAndCategoryAndColorOrderByIdDesc(season, category, color);
        } else if (season != null && !season.isEmpty() && 
                   category != null && !category.isEmpty()) {
            // Filtri stagione + categoria
            shoes = shoesRepo.findBySeasonAndCategoryOrderByIdDesc(season, category);
        } else if (season != null && !season.isEmpty() && 
                   color != null && !color.isEmpty()) {
            // Filtri stagione + colore
            shoes = shoesRepo.findBySeasonAndColorOrderByIdDesc(season, color);
        } else if (category != null && !category.isEmpty() && 
                   color != null && !color.isEmpty()) {
            // Filtri categoria + colore
            shoes = shoesRepo.findByCategoryAndColorOrderByIdDesc(category, color);
        } else if (season != null && !season.isEmpty()) {
            // Solo filtro stagione
            shoes = shoesRepo.findBySeasonOrderByIdDesc(season);
        } else if (category != null && !category.isEmpty()) {
            // Solo filtro categoria
            shoes = shoesRepo.findByCategoryOrderByIdDesc(category);
        } else if (color != null && !color.isEmpty()) {
            // Solo filtro colore
            shoes = shoesRepo.findByColorOrderByIdDesc(color);
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

        // NUOVA VALIDAZIONE: Verifica se il codice esiste già (ora deve essere univoco)
        if (shoesDto.getCode() != null && !shoesDto.getCode().isEmpty()) {
            Optional<Shoes> existingShoes = shoesRepo.findByCode(shoesDto.getCode());
            if (existingShoes.isPresent()) {
                result.addError(new FieldError("shoesDto", "code", shoesDto.getCode(), false, null, null,
                        "Esiste già una scarpa con questo codice"));
            }
        }

        // Validazione colori
        if (shoesDto.getColors() == null || shoesDto.getColors().isEmpty()) {
            result.addError(new FieldError("shoesDto", "colors", "", false, null, null,
                    "Seleziona almeno un colore"));
        }

        MultipartFile image = shoesDto.getImageFile();
        if (image.isEmpty()) {
            result.addError(
                    new FieldError("shoesDto", "imageFile", "", false, null, null, "L'immagine è obbligatoria"));
        }

        // Se ci sono errori, torna al form
        if (result.hasErrors()) {
            return "shoes/shoes-create";
        }

        String storageFileName = "";
        try {
            String uploadDir = "public/images/";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Genera nome file unico
            storageFileName = new Date().getTime() + "_" + image.getOriginalFilename();

            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            System.out.println("Errore nel salvare l'immagine: " + e.getMessage());
            result.addError(
                    new FieldError("shoesDto", "imageFile", "", false, null, null, "Errore nel salvare l'immagine"));
            return "shoes/shoes-create";
        }

        // Crea e salva la nuova scarpa
        Shoes shoes = new Shoes();
        shoes.setCode(shoesDto.getCode());
        shoes.setColorsList(shoesDto.getColors()); // Usa il nuovo metodo per i colori multipli
        shoes.setSeason(shoesDto.getSeason());
        shoes.setCategory(shoesDto.getCategory());
        shoes.setImageFileName(storageFileName);
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
        shoesDto.setColors(shoes.getColorsList()); // Usa il nuovo metodo per ottenere i colori
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

        // NUOVA VALIDAZIONE: Controlla se il codice esiste già (escluso l'elemento corrente)
        if (shoesDto.getCode() != null && !shoesDto.getCode().isEmpty()) {
            Optional<Shoes> existingShoes = shoesRepo.findByCode(shoesDto.getCode());
            if (existingShoes.isPresent() && existingShoes.get().getId() != id) {
                result.addError(new FieldError("shoesDto", "code", shoesDto.getCode(), false, null, null,
                        "Esiste già una scarpa con questo codice"));
            }
        }

        // Validazione colori
        if (shoesDto.getColors() == null || shoesDto.getColors().isEmpty()) {
            result.addError(new FieldError("shoesDto", "colors", "", false, null, null,
                    "Seleziona almeno un colore"));
        }

        if (result.hasErrors()) {
            return "shoes/shoes-edit";
        }

        MultipartFile image = shoesDto.getImageFile();
        if (!image.isEmpty()) {
            // Elimina la vecchia immagine
            String uploadDir = "public/images/";
            Path oldImagePath = Paths.get(uploadDir + shoes.getImageFileName());

            try {
                Files.deleteIfExists(oldImagePath);
            } catch (Exception e) {
                System.out.println("Errore nell'eliminare la vecchia immagine: " + e.getMessage());
            }

            // Salva la nuova immagine
            String storageFileName = "";
            try {
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                storageFileName = new Date().getTime() + "_" + image.getOriginalFilename();

                try (InputStream inputStream = image.getInputStream()) {
                    Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
                            StandardCopyOption.REPLACE_EXISTING);
                }

                shoes.setImageFileName(storageFileName);
            } catch (Exception e) {
                System.out.println("Errore nel salvare la nuova immagine: " + e.getMessage());
                result.addError(new FieldError("shoesDto", "imageFile", "", false, null, null,
                        "Errore nel salvare l'immagine"));
                return "shoes/shoes-edit";
            }
        }

        // Aggiorna i dettagli delle scarpe
        shoes.setCode(shoesDto.getCode());
        shoes.setColorsList(shoesDto.getColors()); // Usa il nuovo metodo per i colori multipli
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
            // Elimina anche l'immagine dal disco
            String uploadDir = "public/images/";
            Path imagePath = Paths.get(uploadDir + shoes.getImageFileName());

            try {
                Files.deleteIfExists(imagePath);
            } catch (Exception e) {
                System.out.println("Errore nell'eliminare l'immagine: " + e.getMessage());
            }

            shoesRepo.delete(shoes);
        }

        return "redirect:/shoes";
    }
}
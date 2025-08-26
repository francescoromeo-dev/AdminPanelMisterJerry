package com.AdminMisterJerry.adminPanel.Controller;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

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
    public String getShoes(@RequestParam(required = false) String season, @RequestParam(required = false) String category,Model model) {
        
        List<Shoes> shoes;
        if (season != null && !season.isEmpty() && category != null && !category.isEmpty()) {
            // Entrambi i filtri attivi
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

        // Verifica se il codice esiste già (solo se il codice non è null/vuoto)
        if (shoesDto.getCode() != null && !shoesDto.getCode().isEmpty()
                && shoesRepo.findByCode(shoesDto.getCode()) != null) {
            result.addError(new FieldError("shoesDto", "code", shoesDto.getCode(), false, null, null,
                    "Questo codice è già in uso"));
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
        shoes.setSeason(shoesDto.getSeason());
        shoes.setCategory(shoesDto.getCategory());

        try {
            shoesRepo.save(shoes);
        } catch (Exception e) {
            result.addError(new FieldError("shoesDto", "code", shoesDto.getCode(), false, null, null,
                    "Questo codice è già in uso"));
            return "shoes/shoes-edit";
        }

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
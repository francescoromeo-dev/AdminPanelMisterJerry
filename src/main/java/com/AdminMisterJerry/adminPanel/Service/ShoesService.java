package com.AdminMisterJerry.adminPanel.Service;

import com.AdminMisterJerry.adminPanel.Dto.ShoesApiDto;
import com.AdminMisterJerry.adminPanel.Model.Shoes;
import com.AdminMisterJerry.adminPanel.Repositories.ShoesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service layer per la gestione della business logic delle scarpe
 * Separa la logica di business dai controller
 */
@Service
public class ShoesService {

    @Autowired
    private ShoesRepository shoesRepository;

    /**
     * Recupera tutte le scarpe
     * @return Lista di DTO delle scarpe
     */
    public List<ShoesApiDto> getAllShoes() {
        List<Shoes> shoes = shoesRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        return shoes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Recupera una scarpa per ID
     * @param id ID della scarpa
     * @return Optional del DTO della scarpa
     */
    public Optional<ShoesApiDto> getShoeById(int id) {
        Optional<Shoes> shoe = shoesRepository.findById(id);
        return shoe.map(this::convertToDto);
    }

    /**
     * Recupera scarpe per categoria
     * @param category Categoria delle scarpe
     * @return Lista di DTO delle scarpe
     */
    public List<ShoesApiDto> getShoesByCategory(String category) {
        List<Shoes> shoes = shoesRepository.findByCategoryOrderByIdDesc(category);
        return shoes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Recupera scarpe per stagione
     * @param season Stagione delle scarpe
     * @return Lista di DTO delle scarpe
     */
    public List<ShoesApiDto> getShoesBySeason(String season) {
        List<Shoes> shoes = shoesRepository.findBySeasonOrderByIdDesc(season);
        return shoes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Recupera scarpe filtrate per categoria e/o stagione
     * @param category Categoria (opzionale)
     * @param season Stagione (opzionale)
     * @return Lista di DTO delle scarpe filtrate
     */
    public List<ShoesApiDto> getFilteredShoes(String category, String season) {
        List<Shoes> shoes;
        
        if (isValidString(category) && isValidString(season)) {
            // Filtri categoria + stagione
            shoes = shoesRepository.findBySeasonAndCategoryOrderByIdDesc(season, category);
        } else if (isValidString(season)) {
            // Solo filtro stagione
            shoes = shoesRepository.findBySeasonOrderByIdDesc(season);
        } else if (isValidString(category)) {
            // Solo filtro categoria
            shoes = shoesRepository.findByCategoryOrderByIdDesc(category);
        } else {
            // Nessun filtro, restituisce tutte
            shoes = shoesRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        }
        
        return shoes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Recupera scarpe per codice (ricerca parziale)
     * @param codePattern Pattern del codice da cercare
     * @return Lista di DTO delle scarpe
     */
    public List<ShoesApiDto> getShoesByCodePattern(String codePattern) {
        List<Shoes> allShoes = shoesRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        
        List<Shoes> filteredShoes = allShoes.stream()
                .filter(shoe -> shoe.getCode() != null && 
                              shoe.getCode().toLowerCase().contains(codePattern.toLowerCase()))
                .collect(Collectors.toList());
        
        return filteredShoes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Conta le scarpe per categoria
     * @return Map con conteggio per categoria
     */
    public java.util.Map<String, Long> getShoeCountByCategory() {
        List<Shoes> allShoes = shoesRepository.findAll();
        
        return allShoes.stream()
                .filter(shoe -> shoe.getCategory() != null)
                .collect(Collectors.groupingBy(
                    Shoes::getCategory,
                    Collectors.counting()
                ));
    }

    /**
     * Conta le scarpe per stagione
     * @return Map con conteggio per stagione
     */
    public java.util.Map<String, Long> getShoeCountBySeason() {
        List<Shoes> allShoes = shoesRepository.findAll();
        
        return allShoes.stream()
                .filter(shoe -> shoe.getSeason() != null)
                .collect(Collectors.groupingBy(
                    Shoes::getSeason,
                    Collectors.counting()
                ));
    }

    /**
     * Recupera statistiche generali
     * @return DTO con statistiche
     */
    public ShoesStatsDto getGeneralStats() {
        long totalShoes = shoesRepository.count();
        java.util.Map<String, Long> categoryCounts = getShoeCountByCategory();
        java.util.Map<String, Long> seasonCounts = getShoeCountBySeason();
        
        return new ShoesStatsDto(totalShoes, categoryCounts, seasonCounts);
    }

    // Metodi helper privati

    /**
     * Converte un'entità Shoes in ShoesApiDto
     * @param shoe Entità Shoes
     * @return DTO per l'API
     */
    private ShoesApiDto convertToDto(Shoes shoe) {
        ShoesApiDto dto = new ShoesApiDto();
        dto.setId(shoe.getId());
        dto.setCode(shoe.getCode());
        dto.setCategory(shoe.getCategory());
        dto.setSeason(shoe.getSeason());
        dto.setCreatedAt(shoe.getCreatedAt());
        
        // Gestione immagini - URLs per accedervi
        List<Long> imageIds = shoe.getImageIdsList();
        List<String> imageUrls = imageIds.stream()
                .map(imageId -> "/images/" + imageId)
                .collect(Collectors.toList());
        dto.setImageUrls(imageUrls);
        
        // URL immagine principale (prima immagine)
        if (!imageUrls.isEmpty()) {
            dto.setPrimaryImageUrl(imageUrls.get(0));
        }
        
        dto.setImagesCount(shoe.getImagesCount());
        
        return dto;
    }

    /**
     * Verifica se una stringa è valida (non null e non vuota)
     * @param str Stringa da verificare
     * @return true se la stringa è valida
     */
    private boolean isValidString(String str) {
        return str != null && !str.trim().isEmpty();
    }

    // Classe DTO per le statistiche
    public static class ShoesStatsDto {
        private long totalShoes;
        private java.util.Map<String, Long> categoryCounts;
        private java.util.Map<String, Long> seasonCounts;

        public ShoesStatsDto(long totalShoes, 
                            java.util.Map<String, Long> categoryCounts, 
                            java.util.Map<String, Long> seasonCounts) {
            this.totalShoes = totalShoes;
            this.categoryCounts = categoryCounts;
            this.seasonCounts = seasonCounts;
        }

        // Getters
        public long getTotalShoes() { return totalShoes; }
        public java.util.Map<String, Long> getCategoryCounts() { return categoryCounts; }
        public java.util.Map<String, Long> getSeasonCounts() { return seasonCounts; }

        // Setters
        public void setTotalShoes(long totalShoes) { this.totalShoes = totalShoes; }
        public void setCategoryCounts(java.util.Map<String, Long> categoryCounts) { 
            this.categoryCounts = categoryCounts; 
        }
        public void setSeasonCounts(java.util.Map<String, Long> seasonCounts) { 
            this.seasonCounts = seasonCounts; 
        }
    }
}
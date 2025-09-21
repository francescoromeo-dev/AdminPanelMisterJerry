package com.AdminMisterJerry.adminPanel.Controller.Api;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.AdminMisterJerry.adminPanel.Model.Shoes;
import com.AdminMisterJerry.adminPanel.Model.ShoesApiDto;
import com.AdminMisterJerry.adminPanel.Repositories.ShoesRepository;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ShoesApiController {
    @Autowired
    private ShoesRepository shoesRepository;

    private ShoesApiDto converToDto(Shoes shoe){
        ShoesApiDto dto = new ShoesApiDto();
        dto.setId(shoe.getId());
        dto.setCode(shoe.getCode());
        dto.setCategory(shoe.getCategory());
        dto.setSeason(shoe.getSeason());
        dto.setCreatedAt(shoe.getCreatedAt());

        //Gestione immagini -> urls per accedervi
        List<Long> imageIds = shoe.getImageIdsList();
        List<String> imageUrls = imageIds.stream().map(imageId-> "/images" +imageId).collect(Collectors.toList());
        dto.setImageUrls(imageUrls);

        //URL immagine principale
        if(!imageUrls.isEmpty()){
            dto.setPrimaryImageUrl(imageUrls.get(0));
        }
        dto.setImagesCount(shoe.getImagesCount());

        return dto;
    }
    
    /**
     *  GET /api/scarpe -> restituisce tutte le scarpe
     * @return Lista di scarpe in formato JSON
     */

     @GetMapping("/scarpe")
     public ResponseEntity<List<ShoesApiDto>> getAllShoes(){
        try{
            List<Shoes> shoes = shoesRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
            List<ShoesApiDto> shoesDto = shoes.stream()
                    .map(this::converToDto)    
                    .collect(Collectors.toList());
            return ResponseEntity.ok(shoesDto); //200 ok
        } catch (Exception e){
            return ResponseEntity.internalServerError().build(); //500 internal error
        }
     }

     /**
      * GET /api/scarpe/{id} -> restituisce una scarpa specifica
      * @param id ID della scarpa
      * @return Scarpa in formato JSON 
      */

      @GetMapping("/scarpa/{id}")
      public ResponseEntity<ShoesApiDto> getShoesById(@PathVariable int id){
        try{
            Optional<Shoes> shoe = shoesRepository.findById(id);

            if(shoe.isPresent()){
                ShoesApiDto shoesDto = converToDto(shoe.get());
                return ResponseEntity.ok(shoesDto);
            }
            else{
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e){
             return ResponseEntity.internalServerError().build();
        }
      }

      
}

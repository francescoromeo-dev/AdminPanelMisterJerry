package com.AdminMisterJerry.adminPanel.Controller.Api;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.AdminMisterJerry.adminPanel.Dto.ShoesApiDto;
import com.AdminMisterJerry.adminPanel.Service.ShoesService;
import com.AdminMisterJerry.adminPanel.Model.Shoes;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ShoesApiController {
    @Autowired
    private ShoesService shoesService;
    
    /**
     *  GET /api/scarpe -> restituisce tutte le scarpe
     * @return Lista di scarpe in formato JSON
     */
    @GetMapping("/scarpe")
    public ResponseEntity<List<ShoesApiDto>> getAllShoes(){
        try{
            List<ShoesApiDto> shoes = shoesService.getAllShoes();
            return ResponseEntity.ok(shoes); //200 ok
        } catch (Exception e){
            return ResponseEntity.internalServerError().build(); //500 internal error
        }
    }

     /**
      * GET /api/scarpe/{id} -> restituisce una scarpa specifica
      * @param id ID della scarpa
      * @return Scarpa in formato JSON 
      */
    @GetMapping("/scarpe/{id}")
    public ResponseEntity<ShoesApiDto> getShoesById(@PathVariable int id){
        try{
            Optional<ShoesApiDto> shoe = shoesService.getShoeById(id);

            if(shoe.isPresent()){
                return ResponseEntity.ok(shoe.get());
            }
            else{
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e){
             return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/scarpe/categoria/{categoria} -> Filtra per categoria
     * @param categoria Categoria delle scarpe
     * @return Lista di scarpe filtrate per categoria
     */
    @GetMapping("/scarpe/categoria/{categoria}")
    public ResponseEntity<List<ShoesApiDto>> getShoesByCategory(@PathVariable String category){
        try{
            List<ShoesApiDto> shoes = shoesService.getShoesByCategory(category);
            return ResponseEntity.ok(shoes);
        }catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * GET /api/scarpe/stagione/{stagione} -> Filtra per stagione
     * @param stagione Stagione della scarpa
     * @return Lista di scarpe filtrate per stagione
     */
    @GetMapping("/scarpe/stagione/{stagione}")
    public ResponseEntity<List<ShoesApiDto>> getShoesBySeason(@PathVariable String season){
        try{
            List<ShoesApiDto> shoes = shoesService.getShoesBySeason(season);
            return ResponseEntity.ok(shoes);
        }catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/scarpe/filtri -> Filtra per categoria e/o stagione
     * @param categoria
     * @param stagione 
     * @return Lista di scarpe filtrate
     */
    @GetMapping("/scarpe/filtri")
    public ResponseEntity<List<ShoesApiDto>> getFilteredShoes(
        @RequestParam(required = false) String category, 
        @RequestParam(required = false) String season){
            try{
                List<ShoesApiDto> shoes = shoesService.getFilteredShoes(category, season);
                return ResponseEntity.ok(shoes);
            } catch (Exception e){
                return ResponseEntity.internalServerError().build();
            }
        }
}

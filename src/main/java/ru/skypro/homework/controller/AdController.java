package ru.skypro.homework.controller;

//import org.hibernate.Hibernate;
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdDtoIn;
import ru.skypro.homework.dto.AdExtendedDtoOut;
import ru.skypro.homework.dto.AdsDtoOut;
import ru.skypro.homework.dto.AdDtoOut;
import ru.skypro.homework.service.AdService;
//import java.sql.Blob;

@RestController
@CrossOrigin(value = "http://localhost:3000")
@RequestMapping("ads")
public class AdController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdController.class);
    private final AdService adService;

    public AdController(AdService adService) {
        this.adService = adService;
    }

    @GetMapping
    public AdsDtoOut getAllAds() {
        LOGGER.info("Получен запрос для getAllAds");
        return adService.getAllAds();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)  //MULTIPART_FORM_DATA_VALUE, иначе сваггер предложит заполнить json
    public AdDtoOut createAd(@RequestPart("properties") AdDtoIn adDtoIn,
                          //если первый параметр - объект типа AdDtoIn,
                          //то Swagger не справится в такой посылкой, он пошлет строку.
                          //А в Postman надо, используя 3 точки, открыть колонку ТипКонтента и задать там application/json
                          @RequestPart MultipartFile image ) {
        LOGGER.info("Получен запрос для addAd: properties = " + adDtoIn + ", image = " + image);
        //adServis.createAd(adDtoIn, image);
        return new AdDtoOut();
    }
    @GetMapping("{id}")
    public AdExtendedDtoOut getAdExtended(@PathVariable int id) {
        LOGGER.info("Получен запрос для getAdExtended: id = " + id);
        return adService.getAdById(id);
    }
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteAd(@PathVariable int id) {
        LOGGER.info("Получен запрос для deleteAd: id = " + id);
        return ResponseEntity.ok().build();
    }
    @PatchMapping("{id}")
    public AdDtoOut updateAd(@PathVariable int id, @RequestBody AdDtoIn adDtoIn) {
        LOGGER.info("Получен запрос для updateAd: id = " + id +", adDtoIn = " + adDtoIn);
        return new AdDtoOut();
    }
    @GetMapping("me")
    public AdsDtoOut getMyAds() {
        LOGGER.info("Получен запрос для getMyAds");
        return adService.getMyAds();
    }
    @PatchMapping(value = "{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updateImage(@PathVariable int id, @RequestPart MultipartFile image) {
        LOGGER.info("Получен запрос для updateImage:  id = " + id + ", image = " + image);
        /* В задании написано, что возвращать нужно
        application/octet-stream:
            schema:
                type: array
                items:
                    type: string
                    format: byte
         читается - как массив строк.
         Дима, можно ничего не возвращать? или что за массив?
         */
        adService.updateImage(id, image);
    }
    @GetMapping(value = "{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable int id) {
        LOGGER.info("Получен запрос для getImage:  id = " + id);
        byte[] photo = adService.getImage(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentLength(photo.length);

        return new ResponseEntity<>(photo, headers, HttpStatus.OK);   }
}

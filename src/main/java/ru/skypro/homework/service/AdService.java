package ru.skypro.homework.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdDtoIn;
import ru.skypro.homework.dto.AdDtoOut;
import ru.skypro.homework.dto.AdExtendedDtoOut;
import ru.skypro.homework.dto.AdsDtoOut;
import ru.skypro.homework.entity.Ad;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.mapper.AdMapper;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.function.Supplier;

@Service
public class AdService {
    private final AdRepository adRepository;
    private final AdMapper adMapper;
    private final UserRepository userRepository;

    //Читать текст ошибки в теле ответа фронт не будет. Прокинем сообщение хотя бы для Logger
    private Supplier<EntityNotFoundException> excSuppl(int id) {
        return () -> new EntityNotFoundException("Ad with id " + id + " not found");
    }

    public AdService(AdRepository adRepository, AdMapper adMapper, UserRepository userRepository) {
        this.adRepository = adRepository;
        this.adMapper = adMapper;
        this.userRepository = userRepository;
    }

    public void updateImage(int id, MultipartFile image) {
        Ad ad = adRepository.findById(id).orElseThrow(excSuppl(id));
        try {
            ad.setImage(image.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        adRepository.save(ad);
    }

    @Transactional  //для получения user.getAds()
    //без @Transactional возникает
    //PSQLException: Большие объекты не могут использоваться в режиме авто-подтверждения (auto-commit).
    //хотя мы не обращаемся ни к каким картинкам
    public AdsDtoOut getMyAds(Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        List<Ad> ads = user.getAds();
        //List<Ad> ads = adRepository.findAllByUserId(user.getId()); было временное решение
        return adMapper.toAdsDtoOut(ads);
    }

    public AdsDtoOut getAllAds() {
        return adMapper.toAdsDtoOut(adRepository.findAll());
    }

    @Transactional
    public byte[] getImage(int id) {
        return adRepository.findById(id).orElseThrow(excSuppl(id)).getImage();
    }

    public AdExtendedDtoOut getAdById(int id) {
        return adMapper.toAdExtendedDtoOut(adRepository.findById(id).orElseThrow(excSuppl(id)));
    }

    public AdDtoOut createAd(AdDtoIn adDtoIn, MultipartFile image, Principal principal) {
        Ad adBeforeSave = adMapper.toEntity(adDtoIn, principal);
        try {
            adBeforeSave.setImage(image.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        //adAfterSave отличается от adBeforeSave тем, что в нем проставлен id
        Ad adAfterSave = adRepository.save(adBeforeSave);
        return adMapper.toAdDtoOut(adAfterSave);
    }

    public AdDtoOut updateAd(int id, AdDtoIn adDtoIn) {
        //Поскольку в AdDtoIn - не все реквизиты, а сохранить надо все,
        //придется читать Ad для получения недостающих.
        //Хотя запросом к базе можно обновить только требуемые.
        Ad ad = adRepository.findById(id).orElseThrow(excSuppl(id));
        ad.setTitle(adDtoIn.getTitle());
        ad.setPrice(adDtoIn.getPrice());
        ad.setDescription(adDtoIn.getDescription());
        return adMapper.toAdDtoOut(adRepository.save(ad));
    }

    public void deleteAd(int id) {
        adRepository.deleteById(id);
    }

}

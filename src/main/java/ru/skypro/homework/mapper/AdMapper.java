package ru.skypro.homework.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.dto.AdDtoIn;
import ru.skypro.homework.dto.AdDtoOut;
import ru.skypro.homework.dto.AdExtendedDtoOut;
import ru.skypro.homework.dto.AdsDtoOut;
import ru.skypro.homework.entity.Ad;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.UserService;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AdMapper {

    private final UserRepository userRepository;

    public Ad toEntity(AdDtoIn adDtoIn, Principal principal) {
        Ad ad = new Ad();
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        ad.setUser(user);
        ad.setTitle(adDtoIn.getTitle());
        ad.setPrice(adDtoIn.getPrice());
        ad.setDescription(adDtoIn.getDescription());
        return ad;
    }

    public AdDtoOut toAdDtoOut(Ad ad) {
        if (ad==null) return null;
        AdDtoOut adDtoOut = new AdDtoOut();
        adDtoOut.setPk(ad.getId());
        adDtoOut.setAuthor(ad.getUser().getId());
        adDtoOut.setTitle(ad.getTitle());
        adDtoOut.setPrice(ad.getPrice());
        //http://localhost:8080/ads/5/image
        adDtoOut.setImage("/ads/"+ad.getId()+"/image"); //http://localhost:8080/ - не указываем

        return adDtoOut;
    }
    public AdExtendedDtoOut toAdExtendedDtoOut(Ad ad) {
        AdExtendedDtoOut adExtendedDtoOut = new AdExtendedDtoOut();
        adExtendedDtoOut.setPk(ad.getId());
        adExtendedDtoOut.setAuthorFirstName(ad.getUser().getFirstName());
        adExtendedDtoOut.setAuthorLastName(ad.getUser().getLastName());
        adExtendedDtoOut.setEmail(ad.getUser().getUsername());
        adExtendedDtoOut.setPhone(ad.getUser().getPhone());
        adExtendedDtoOut.setTitle(ad.getTitle());
        adExtendedDtoOut.setPrice(ad.getPrice());
        adExtendedDtoOut.setDescription(ad.getDescription());
        adExtendedDtoOut.setImage("/ads/"+ad.getId()+"/image");
        return adExtendedDtoOut;
    }
    public AdsDtoOut toAdsDtoOut(List<Ad> list) {
        AdsDtoOut adsDtoOut = new AdsDtoOut();
        adsDtoOut.setCount(list.size());
        adsDtoOut.setResults(list.stream().map(this::toAdDtoOut).collect(Collectors.toList()));
        return adsDtoOut;
    }
}

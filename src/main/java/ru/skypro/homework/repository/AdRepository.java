package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.entity.Ad;

import java.util.List;

@Repository
public interface AdRepository extends JpaRepository<Ad, Integer> {
    @Transactional  //для работы с Large Object. Почитать, что это.
    //иначе ошибка - Large Objects may not be used in auto-commit mode - при getMyAds (почему здесь?)
    List<Ad> findAllByUserId(Integer userId); //это временно. Надеюсь использовать коллекцию из user

}

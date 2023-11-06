package ru.skypro.homework.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import ru.skypro.homework.dto.Comment;
import ru.skypro.homework.dto.Comments;
import ru.skypro.homework.dto.CreateOrUpdateComment;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.entity.AdComment;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;

@Service
public class CommentService {

    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository,
                          CommentMapper commentMapper,
                          UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.userRepository = userRepository;
    }

    @Transactional
    public Comment createComment(Integer adId,
                                 CreateOrUpdateComment createOrUpdateComment,
                                 String userName){
        logger.info("Create comment.");
        return commentMapper.toDto(
                commentRepository.save(
                        commentMapper.toEntity(createOrUpdateComment, adId, getCurrentUser(userName).getId())));
    }

    @Transactional
    public Comments findComments(Integer adId, String userName){
        logger.info("Find all comments.");
        User currentUser = getCurrentUser(userName);    // При обращении не авторизованного пользователя UserNotFoundException
        return  commentMapper.toComments(commentRepository.findAllByAdId(adId));
    }

    @Transactional
    public Comment updateComment(Integer adId,
                                 Integer commentId,
                                 CreateOrUpdateComment createOrUpdateComment,
                                 String userName){
        logger.info("Update comment.");
        AdComment oldComment = commentRepository.findById(commentId).
                orElseThrow(() -> new EntityNotFoundException("Comment not found."));
        if(!oldComment.getAd().getId().equals(adId)){
            throw new EntityNotFoundException("You can't change the ad in a comment");
        }
        User currentUser = getCurrentUser(userName);
        if(hasPermission(oldComment, currentUser)){
            oldComment.setText(createOrUpdateComment.getText());
            oldComment.setCreatedAt(LocalDateTime.now());
            oldComment.setUser(currentUser);
        }else {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
        }
        return commentMapper.toDto(commentRepository.save(oldComment));
    }

    @Transactional
    public void deleteComment(Integer adId, Integer commentId, String userName){
        logger.info("Delete comment.");
        AdComment deletedComment = commentRepository.findById(commentId).
                orElseThrow(() -> new EntityNotFoundException("Comment not found."));
        if(!deletedComment.getAd().getId().equals(adId)){
            throw new EntityNotFoundException("Comment not found.");
        }
        if(hasPermission(deletedComment, getCurrentUser(userName))){
            commentRepository.delete(deletedComment);
        }else {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
        }
    }

    private User getCurrentUser(String userName){
        return userRepository.findByUsername(userName).
                orElseThrow(() -> new UserNotFoundException("User not found."));
    }

    private boolean hasPermission(AdComment comment, User currentUser){
        return currentUser.getRole().equals(Role.ADMIN)||comment.getUser().equals(currentUser);
    }
}
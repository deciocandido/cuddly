package se.kth.sda.skeleton.postlikes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Represents the controller layer (or the API). This exposes application functionality of PostLike as RESTful webservices.
 */
@RestController
@RequestMapping("/posts")
public class PostLikeController {

    PostLikeService postLikeService;

    @Autowired
    public PostLikeController(PostLikeService postLikeService) {
        this.postLikeService = postLikeService;
    }

    @PostMapping("/{postId}/likes")
    public ResponseEntity<PostLike> addLike(@PathVariable Long postId){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(postLikeService.addLike(postId));
    }
}
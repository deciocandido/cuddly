package se.kth.sda.skeleton.posts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.kth.sda.skeleton.auth.AuthService;
import se.kth.sda.skeleton.exception.ForbiddenException;
import se.kth.sda.skeleton.exception.ResourceNotFoundException;
import se.kth.sda.skeleton.user.User;
import se.kth.sda.skeleton.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Represents the service layer. It contains defined functionalities according to business logic for Posts.
 */
@Service
public class PostService {

    private PostRepository postRepository;
    private UserRepository userRepository;
    private AuthService authService;


    /**
     * Constructs a PostService and automatically assigns its postRepository field
     *
     * @param postRepository an object that implements interface PostRepository
     */
    @Autowired
    public PostService(PostRepository postRepository, UserRepository userRepository, AuthService authService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    /**
     * Return a list of all posts
     *
     * @return list of posts in reverse order from postRepository
     */
    public List<Post> listAllPosts() {
        List<Post> posts = postRepository.findAll();
        Collections.reverse(posts); //Shows newer posts first
        return posts;
    }

    /**
     * Return a specific post based on ID
     *
     * @param id the id of the specific post
     * @return a post
     */
    public Post getPostById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
        return post;
    }

    /**
     * Create a post
     *
     * @param newPost newly created post
     * @return newly created post
     */
    public Post createPost(Post newPost) {
        String email = authService.getLoggedInUserEmail();
        LocalDateTime createdTime = LocalDateTime.now();

        User relatedUser = userRepository.findByEmail(email);
        newPost.setRelatedPostUser(relatedUser);
        newPost.setCreatedTime(createdTime);

        relatedUser.getCreatedPosts().add(newPost);
        Post post = postRepository.save(newPost);
        return post;
    }

    /**
     * Update a post based on ID
     *
     * @param id          ID of the post to edit
     * @param updatedPost newly created body to update the post
     * @return updated post
     */
    public Post updatePost(Long id, Post updatedPost) {
        Post post = postRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
        LocalDateTime createdTime = LocalDateTime.now();
        String loggedInUserEmail = authService.getLoggedInUserEmail();

        User loggedInUser = userRepository.findByEmail(loggedInUserEmail);
        if (!loggedInUserEmail.equals(post.getRelatedPostUser().getEmail())) {
            throw new ForbiddenException();
        }
        updatedPost.setId(id);
        updatedPost.setCreatedTime(createdTime);
        updatedPost.setRelatedPostUser(loggedInUser);

        Post newPost = postRepository.save(updatedPost);
        return newPost;
    }

    /**
     * Delete a post based on ID
     *
     * @param id ID of the post to delete
     */
    public void deletePost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
        String loggedInUserEmail = authService.getLoggedInUserEmail();
        if (loggedInUserEmail.equals(post.getRelatedPostUser().getEmail())) {
            post.getRelatedPostUser().getCreatedPosts().remove(post);
            postRepository.delete(post);
        } else {
            throw new ForbiddenException();
        }
    }
}

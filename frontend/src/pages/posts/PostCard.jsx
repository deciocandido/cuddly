// NPM Packages
import React, { useEffect, useState } from "react";

// Project files
import CommentForm from "./comments/CommentForm";
import CommentList from "./comments/CommentList";
import CommentsApi from "../../api/CommentsApi";
import PostsApi from "../../api/PostsApi";
import PostUpdateForm from "./PostUpdateForm";

export default function PostCard({ post, onDeleteClick }) {
  // Local state
  const [comments, setComments] = useState([]);
  const [thisPost, setThisPost] = useState(post);
  const [toggleUpdatePost, setToggleUpdatePost] = useState(false);

  // Constants
  const postId = post.id;

  // Methods
  async function updatePost(updatedPost) {
    try {
      await PostsApi.updatePost(updatedPost, post.id);
      PostsApi.getPostById(post.id).then((response) => setThisPost(response));
    } catch (e) {
      console.error(e);
    }
  }

  async function createComment(postId, commentData) {
    try {
      const response = await CommentsApi.createComment(postId, commentData);
      const comment = response.data;
      const newComments = comments.concat(comment);
      setComments(newComments);
    } catch (e) {
      console.error(e);
    }
  }

  async function deleteComment(postId, commentToDelete) {
    try {
      await CommentsApi.deleteComment(postId, commentToDelete.id);
      const newComments = comments.filter(
        (comment) => comment.id !== commentToDelete.id
      );
      setComments(newComments);
    } catch (e) {
      console.error(e);
    }
  }

  useEffect(() => {
    CommentsApi.getAllComments(postId)
      .then(({ data }) => setComments(data))
      .catch((err) => console.error(err));
  }, [setComments, postId]);

  return (
    <div>
      <div>
        <p>{post.contentText}</p>
        <button onClick={onDeleteClick}>Delete</button>
        {!toggleUpdatePost && (
          <button onClick={() => setToggleUpdatePost(true)}>Update</button>
        )}
        {toggleUpdatePost && (
          <button onClick={() => setToggleUpdatePost(false)}>
            Hide update form
          </button>
        )}
        {toggleUpdatePost && (
          <PostUpdateForm onSubmit={updatePost} post={post} />
        )}
        <CommentList
          postId={postId}
          comments={comments}
          onDelete={deleteComment}
        />
        <CommentForm post={post} onSubmit={createComment} />
      </div>
    </div>
  );
}

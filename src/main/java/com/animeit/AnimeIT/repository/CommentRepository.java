package com.animeit.AnimeIT.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.animeit.AnimeIT.model.Comment;
import com.animeit.AnimeIT.model.Post;
import com.animeit.AnimeIT.model.User;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

	List<Comment> findByPost(Post post);

	List<Comment> findAllByUser(User user);

}

package com.animeit.AnimeIT.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.animeit.AnimeIT.model.Post;
import com.animeit.AnimeIT.model.Subreddit;
import com.animeit.AnimeIT.model.User;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
	
	List<Post> findAllBySubreddit(Subreddit subreddit);
	
	List<Post> findAllByUser(User user);
}

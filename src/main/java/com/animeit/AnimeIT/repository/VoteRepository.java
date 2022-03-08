package com.animeit.AnimeIT.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.animeit.AnimeIT.model.Post;
import com.animeit.AnimeIT.model.User;
import com.animeit.AnimeIT.model.Vote;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
	List<Vote> findAllByPost(Post post);
	
	Optional<Vote> findTopByPostAndUserOrderByVoteIdDesc(Post post, User currentUser);
}

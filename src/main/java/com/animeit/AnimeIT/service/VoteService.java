package com.animeit.AnimeIT.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.animeit.AnimeIT.dto.VoteDto;
import com.animeit.AnimeIT.exception.ResourceNotFoundException;
import com.animeit.AnimeIT.exception.VoteAlreadyCastedException;
import com.animeit.AnimeIT.model.Post;
import com.animeit.AnimeIT.model.Vote;
import com.animeit.AnimeIT.repository.PostRepository;
import com.animeit.AnimeIT.repository.VoteRepository;

import lombok.AllArgsConstructor;

import static com.animeit.AnimeIT.model.VoteType.UPVOTE;

@Service
@AllArgsConstructor
public class VoteService {
	private final VoteRepository voteRepository;
	private final PostRepository postRepository;
	private final AuthService authService;
	
	@Transactional
	public void vote(VoteDto voteDto) {
		Post post = getPost(voteDto.getPostId());
		Optional<Vote> voteByPostAndUser = voteRepository.findTopByPostAndUserOrderByVoteIdDesc(post, authService.getCurrentUser());
		if(voteByPostAndUser.isPresent() && voteByPostAndUser.get().getVoteType().equals(voteDto.getVoteType())) {
			throw new VoteAlreadyCastedException("You have already "+ voteDto.getVoteType()+"'d the post");
		}
		if(UPVOTE.equals(voteDto.getVoteType())) {
			post.setVoteCount(post.getVoteCount() + 1);
		} else {
			post.setVoteCount(post.getVoteCount() - 1);
		}
		voteRepository.save(mapDtoToVote(voteDto, post));
		postRepository.saveAndFlush(post);
	}
	
	// Below are the mapper and helper methods
	private Post getPost(Long postId) {
		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("No post found with Post Id: " + postId));
		return post;
	}
	
	private Vote mapDtoToVote(VoteDto voteDto, Post post) {
		return Vote.builder().post(post)
							 .user(authService.getCurrentUser())
							 .voteType(voteDto.getVoteType())
							 .build();
	}
	
}

package com.animeit.AnimeIT.service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.animeit.AnimeIT.dto.PostRequest;
import com.animeit.AnimeIT.dto.PostResponse;
import com.animeit.AnimeIT.exception.ResourceNotFoundException;
import com.animeit.AnimeIT.model.Post;
import com.animeit.AnimeIT.model.Subreddit;
import com.animeit.AnimeIT.model.User;
import com.animeit.AnimeIT.repository.PostRepository;
import com.animeit.AnimeIT.repository.SubredditRepository;
import com.animeit.AnimeIT.repository.UserRepository;
import com.github.marlonlom.utilities.timeago.TimeAgo;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PostService {
	
	private final PostRepository postRepository;
	private final SubredditRepository subredditRepository;
	private final AuthService authService;
	private final UserRepository userRepository;
//	private final CommentRepository commentRepository;
//	private final VoteRepository voteRepository;

	@Transactional
	public PostRequest save(PostRequest postRequest) {
		Post post = postRepository.save(mapDtoToPost(postRequest));
		postRequest.setPostId(post.getPostId());
		return postRequest;
	}
	
	@Transactional(readOnly = true)
	public PostResponse getPost(Long id) {
		Post post = postRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No Post found with ID :"+ id));
		return mapToDto(post);
	}
	
	@Transactional(readOnly = true)
	public List<PostResponse> getAllPosts() {
		return postRepository.findAll()
				.stream()
				.map(this::mapToDto)
				.collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public List<PostResponse> getPostsBySubreddit(Long subredditId) {
		Subreddit subreddit = subredditRepository.findById(subredditId)
												.orElseThrow(() -> new ResourceNotFoundException("No Subreddit found with Id: " + subredditId));
		List<Post> posts = postRepository.findAllBySubreddit(subreddit);
		return posts.stream().map(this::mapToDto).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<PostResponse> getPostsByUsername(String username) {
		User user = userRepository.findByUserName(username).orElseThrow(() -> new UsernameNotFoundException("No User found with username: "+ username));
		return postRepository.findAllByUser(user).stream()
				.map(this::mapToDto)
				.collect(Collectors.toList());
	}
	
	// Below are helper methods
	
	@Transactional(readOnly = true)
	private Subreddit findSubReddit(String subredditName) {
		Subreddit subreddit = subredditRepository.findByName(subredditName).orElseThrow(() -> new ResourceNotFoundException("No Subreddit with name "+ subredditName+" found"));
		return subreddit;
	}
	
	private Post mapDtoToPost(PostRequest postRequest) {
		return Post.builder().postName(postRequest.getPostName())
							 .createdDate(Instant.now())
							 .description(postRequest.getDescription())
							 .url(postRequest.getUrl())
							 .subreddit(findSubReddit(postRequest.getSubRedditName()))
							 .user(authService.getCurrentUser())
							 .voteCount(0)
							 .commentCount(0)
							 .build();
	}
	
	private PostResponse mapToDto(Post post) {
		return PostResponse.builder().description(post.getDescription())
									.id(post.getPostId())
									.postName(post.getPostName())
									.subRedditName(post.getSubreddit().getName())
									.url(post.getUrl())
									.userName(post.getUser().getUserName())
									.commentCount(post.getCommentCount())
									.voteCount(post.getVoteCount())
									 .duration(TimeAgo.using(post.getCreatedDate().toEpochMilli()))
									.build();
		
	}
}

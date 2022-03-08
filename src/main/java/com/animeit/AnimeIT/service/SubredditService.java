package com.animeit.AnimeIT.service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.animeit.AnimeIT.dto.SubRedditDto;
import com.animeit.AnimeIT.exception.ResourceNotFoundException;
import com.animeit.AnimeIT.model.Subreddit;
import com.animeit.AnimeIT.repository.SubredditRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SubredditService {
	
	private final SubredditRepository subredditRepository;
	private final AuthService authService;
	
	@Transactional
	public SubRedditDto save(SubRedditDto subRedditDto) {
		Subreddit subreddit = subredditRepository.save(this.mapDtoToSubreddit(subRedditDto));
		subRedditDto.setId(subreddit.getId());
		return subRedditDto;
	}
	
	@Transactional
	public List<SubRedditDto> getAll() {
		return subredditRepository.findAll()
				.stream()
				.map(this:: mapSubRedditToDto)
				.collect(Collectors.toList());
	}
	
	// Below are the mapper and helper methods
	
	private SubRedditDto mapSubRedditToDto(Subreddit subreddit) {
		return SubRedditDto.builder().name(subreddit.getName())
									 .description(subreddit.getDescription())
									 .numberOfPosts(subreddit.getPosts().size())
									 .id(subreddit.getId())
									 .build();
	}
	
	private Subreddit mapDtoToSubreddit(SubRedditDto subRedditDto) {
		return Subreddit.builder().name(subRedditDto.getName())
							.description(subRedditDto.getDescription())
							.createdDate(Instant.now())
							.user(authService.getCurrentUser())
							.build();
	}

	public SubRedditDto getSubreddit(Long id) {
		Subreddit subreddit = subredditRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Subreddit with Id: "+ id +" not found"));
		return mapSubRedditToDto(subreddit);
	}
	
	private String getSubredditName(String name) {
		String subredditName = name.replaceAll(" ", "");
		return "r/"+subredditName;
	}
}

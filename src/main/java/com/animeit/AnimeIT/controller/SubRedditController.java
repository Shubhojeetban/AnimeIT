package com.animeit.AnimeIT.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.animeit.AnimeIT.dto.SubRedditDto;
import com.animeit.AnimeIT.service.SubredditService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/subreddit")
@AllArgsConstructor
public class SubRedditController {
	
	private final SubredditService subredditService;
	
	@PostMapping
	public ResponseEntity<SubRedditDto> createSubreddit(@RequestBody SubRedditDto subredditDto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(subredditService.save(subredditDto));
	}
	
	@GetMapping
	public ResponseEntity<List<SubRedditDto>> getAllSubreddit() {
		return ResponseEntity.status(HttpStatus.OK).body(subredditService.getAll());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<SubRedditDto> getSubreddit(@PathVariable Long id) {
		return ResponseEntity.status(HttpStatus.OK).body(subredditService.getSubreddit(id));
	}
}

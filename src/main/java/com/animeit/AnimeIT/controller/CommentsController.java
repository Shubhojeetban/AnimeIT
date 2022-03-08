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

import com.animeit.AnimeIT.dto.CommentsDto;
import com.animeit.AnimeIT.service.CommentsService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/comments/")
@AllArgsConstructor
public class CommentsController {
	
	private final CommentsService commentsService;
	
	@PostMapping
	public ResponseEntity<Void> create(@RequestBody CommentsDto commentsDto) {
		commentsService.save(commentsDto);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
	
	@GetMapping("by-post/{postId}")
	public ResponseEntity<List<CommentsDto>> getAllCommentsForPost(@PathVariable Long postId) {
		return ResponseEntity.status(HttpStatus.OK).body(commentsService.getAllCommentsForPost(postId));
	}
	
	@GetMapping("by-user/{username}")
	public ResponseEntity<List<CommentsDto>> getAllCommentsForUser(@PathVariable String username) {
		return ResponseEntity.status(HttpStatus.OK).body(commentsService.getAllCommentsForUser(username));
	}
}

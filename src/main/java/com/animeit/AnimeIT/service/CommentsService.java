package com.animeit.AnimeIT.service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.animeit.AnimeIT.dto.CommentsDto;
import com.animeit.AnimeIT.exception.ResourceNotFoundException;
import com.animeit.AnimeIT.model.Comment;
import com.animeit.AnimeIT.model.NotificationEmail;
import com.animeit.AnimeIT.model.Post;
import com.animeit.AnimeIT.model.User;
import com.animeit.AnimeIT.repository.CommentRepository;
import com.animeit.AnimeIT.repository.PostRepository;
import com.animeit.AnimeIT.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CommentsService {
	private static final String POST_URL = "";
	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final AuthService authService;
	private final CommentRepository commentRepository;
	private final MailContentBuilder mailContentBuilder;
	private final MailService mailService;

	@Transactional
	public void save(CommentsDto commentsDto) {
		Post post = getPostByid(commentsDto.getPostId());
		Comment comment = mapDtoToComment(commentsDto, post);
		post.setCommentCount(post.getCommentCount()+1);
		commentRepository.save(comment);
		postRepository.saveAndFlush(post);
		String message = mailContentBuilder.build(
				comment.getUser().getUserName() + " posted a comment on your post " + post.getPostName() + POST_URL);
		sendMessageNotification(message, comment.getUser(), post.getUser());
	}

	@Transactional(readOnly = true)
	public List<CommentsDto> getAllCommentsForPost(Long postId) {
		Post post = getPostByid(postId);
		List<Comment> comments = commentRepository.findByPost(post);
		return comments.stream().map(this::mapToDto).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<CommentsDto> getAllCommentsForUser(String username) {
		User user = getUserByUsername(username);
		List<Comment> comments = commentRepository.findAllByUser(user);
		return comments.stream().map(this::mapToDto).collect(Collectors.toList());
	}

	private void sendMessageNotification(String message, User commenter, User user) {
		mailService.sendMail(new NotificationEmail(commenter.getUserName() + " posted a comment on your post",
				user.getEmail(), message));
	}

	// Below is the mapper functions
	private CommentsDto mapToDto(Comment comment) {
		return CommentsDto.builder().postId(comment.getPost().getPostId()).id(comment.getId())
				.createdDate(comment.getCreatedDate()).text(comment.getText()).username(comment.getUser().getUserName())
				.build();
	}

	private Comment mapDtoToComment(CommentsDto commentsDto, Post post) {
		return Comment.builder().text(commentsDto.getText()).createdDate(Instant.now()).post(post)
				.user(authService.getCurrentUser()).build();

	}

	private Post getPostByid(Long id) {
		Post post = postRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No post found with Post Id: " + id));
		return post;
	}

	private User getUserByUsername(String username) {
		User user = userRepository.findByUserName(username)
				.orElseThrow(() -> new UsernameNotFoundException("No User found with username: " + username));
		return user;
	}

}

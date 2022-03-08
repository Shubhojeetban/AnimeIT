package com.animeit.AnimeIT.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostRequest {
	private Long postId;
	private String subRedditName;
	private String postName;
	private String url;
	private String description;
}

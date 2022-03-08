package com.animeit.AnimeIT.dto;

import com.animeit.AnimeIT.model.VoteType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VoteDto {
	private VoteType voteType;
	private Long postId;
}

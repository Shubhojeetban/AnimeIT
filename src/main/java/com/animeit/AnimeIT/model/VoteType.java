package com.animeit.AnimeIT.model;

import java.util.Arrays;

import com.animeit.AnimeIT.exception.VoteNotFoundException;

public enum VoteType {
	UPVOTE(1), DOWNVOTE(-1),
	;
	private int direction;
	private VoteType(int direction) {
	}
	
	public static VoteType lookUp(Integer direction) {
		return Arrays.stream(VoteType.values())
				.filter(value -> value.getDirection().equals(direction))
				.findAny()
				.orElseThrow(() -> new VoteNotFoundException("Vote Not Found"));
	}
	private Integer getDirection() {
		return direction;
	}
}

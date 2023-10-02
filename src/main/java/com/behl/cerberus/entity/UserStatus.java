package com.behl.cerberus.entity;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserStatus {
	
	PENDING_APPROVAL("Pending Approval", List.of("selfservice.read", "selfservice.write")),
	APPROVED("Approved", List.of("fullaccess"));
	
	private final String value;
	private final List<String> scopes;

}
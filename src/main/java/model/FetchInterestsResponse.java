package model;

import lombok.Builder;

import java.util.Set;

@Builder
public class FetchInterestsResponse {
    private Set<String> interests;
}

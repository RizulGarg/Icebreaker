package inMemoryMaps;

import model.AddPostToInterestsRequest;
import model.DownvoteRequest;
import model.FetchPopularInterestsResponse;
import model.FetchPostsForInterestsRequest;
import model.FetchPostsForInterestsResponse;
import model.Post;
import model.UpvoteRequest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class InterestToPosts {
    // Map of interest -> Posts
    private Map<String, List<Post>> interestToPosts = new HashMap<>();

    /**
     * Maps posts to their interest for new feed when user posts something.
     * @param addPostToInterestsRequest
     */
    public void addPostToInterest(final AddPostToInterestsRequest addPostToInterestsRequest) {
        List<String> interests = addPostToInterestsRequest.getInterests();
        for(String interest : interests) {
            List<Post> posts = interestToPosts.computeIfAbsent(interest, k -> new ArrayList<>());
            posts.add(Post.builder()
                    .post(addPostToInterestsRequest.getPost())
                    .upvotes(0)
                    .downvotes(0)
                    .updatedAt(new Date().getTime())
                    .build());
        }
    }

    /**
     * Search posts using interests / refresh news feed.
     * @param fetchPostsForInterestsRequest
     * @return
     */
    public FetchPostsForInterestsResponse fetchPostsForInterests(final FetchPostsForInterestsRequest fetchPostsForInterestsRequest) {
        Set<Post> postsSet = new HashSet<>();
        List<String> interests = fetchPostsForInterestsRequest.getInterests();
        for(String interest : interests) {
            if(interestToPosts.containsKey(interest)) {
                postsSet.addAll(interestToPosts.get(interest));
            }
        }
        List<Post> posts = new ArrayList<>(postsSet);
        posts.sort(Comparator.comparing(Post::getUpdatedAt).reversed());
        return FetchPostsForInterestsResponse.builder().Posts(posts).build();
    }

    /**
     * Upvote.
     * @param upvoteRequest
     */
    public void upvote(final UpvoteRequest upvoteRequest) {
        List<Post> posts = interestToPosts.get(upvoteRequest.getInterest())
                .stream().filter(p -> p.getPost().equals(upvoteRequest.getPost())).collect(Collectors.toList());
        for(Post post : posts) {
            post.setUpvotes(post.getUpvotes() + 1);
        }
    }

    /**
     * Downvote.
     * @param downvoteRequest
     */
    public void downvote(final DownvoteRequest downvoteRequest) {
        List<Post> posts = interestToPosts.get(downvoteRequest.getInterest())
                .stream().filter(p -> p.getPost().equals(downvoteRequest.getPost())).collect(Collectors.toList());
        for(Post post : posts) {
            post.setDownvotes(post.getDownvotes() + 1);
        }
    }

    /**
     * Fetches popular trends to be displayed in sidebar of news feed.
     */
    public FetchPopularInterestsResponse fetchPopularInterests() {
        Map<String, Integer> popularity = new HashMap<>();
        for(Map.Entry<String, List<Post>> entry : interestToPosts.entrySet()) {
            popularity.put(entry.getKey(), entry.getValue().size());
        }
        popularity = popularity.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return FetchPopularInterestsResponse.builder().interests(new ArrayList<>(popularity.keySet())).build();
    }
}

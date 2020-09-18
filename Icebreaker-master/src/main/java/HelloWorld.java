import com.google.gson.Gson;
import inMemoryMaps.InterestToPosts;
import inMemoryMaps.UserToInterests;
import lombok.SneakyThrows;
import model.AddInterestRequest;
import model.AddPostToInterestsRequest;
import model.FetchInterestsRequest;
import model.FetchInterestsResponse;
import model.FetchPostsForInterestsRequest;
import spark.Spark;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static spark.Spark.get;

public class HelloWorld {
    @SneakyThrows
    public static void main(String[] args) {

        Spark.staticFiles.location("/public");
        UserToInterests userToInterests = new UserToInterests();
        InterestToPosts interestToPosts = new InterestToPosts();

        get("/hello", (req, res) -> "hi");

        get("/test/:input", ((request, response) ->
                "Hey " + request.params("input")
                ));

        get("/home", ((request, response) ->
                "<meta http-equiv = \"refresh\" content = \"2; url = http://localhost:4567/feed.html\" />\n"
                ));

        get("/add/interest", ((req, res) ->
            "<meta http-equiv = \"refresh\" content = \"2; url = http://localhost:4567/addUserInterest.html\" />\n"
                ));

        get("/add/post", ((req, res) ->
                "<meta http-equiv = \"refresh\" content = \"2; url = http://localhost:4567/addPost.html\" />\n"
        ));

//        addInterest
        get("/interest/add/:input", (req, res) -> {
            AddInterestRequest request = new AddInterestRequest();
            List<String> input = Arrays.asList(req.params("input").split(","));
            request.setUserName(input.get(0));
            request.setInterest(input.get(1));
            userToInterests.addInterest(request);
            return new Gson().toJson(userToInterests);
        });

//        fetchPostsForInterests
        get("/feed/:interests", (req, res) -> {
            FetchPostsForInterestsRequest request = new FetchPostsForInterestsRequest();
//            return Arrays.asList(req.params("interests").split("_"));
            request.setInterests(Arrays.asList(req.params("interests").split(",")));
            return new Gson().toJson(interestToPosts.fetchPostsForInterests(request));
        });

//        fetchInterestsForUser
        get("/interest/fetch/:user", ((req, res) -> {
            FetchInterestsRequest request = new FetchInterestsRequest();
            request.setUserName(req.params("user"));
            FetchInterestsResponse response = userToInterests.returnInterests(request);
            return new Gson().toJson(response);
        }));

        get("/add/post/:input", (req, res) -> {
            AddPostToInterestsRequest request = new AddPostToInterestsRequest();
            List<String> input = Arrays.asList(req.params("input").split("_"));
            request.setUser(input.get(0));
            request.setInterests(Collections.singletonList(input.get(1)));
            request.setPost(input.get(2));
            interestToPosts.addPostToInterest(request);
//            return new Gson().toJson(interestToPosts.interestToPosts);
            return  "<meta http-equiv = \"refresh\" content = \"2; url = http://localhost:4567/home\" />\n";
        });

//        get("/hello/:user1", (request, response) -> {
//            response.type("application/json");
//            return request.params(":user1");
//            String users[] = request.params(":user1").split(",");
//            return "Response:"+request.params(":user1")+"User1 : "+users[0] + " User 2 "+users[1];
//        });
    }
}
import static spark.Spark.*;

public class HelloWorld {
    public static void main(String[] args) {
        get("/hello", (req, res) -> "Hello Abhay");

        //addInterest
        get("/interest/add", (req, res) -> {

        });

        //fetchPostsForInterests
        get("/interest/add", (req, res) -> {

        });

        get("/hello/:user1", (request, response) -> {
//            response.type("application/json");
//            return request.params(":user1");
            String users[] = request.params(":user1").split(",");
            adduser(users);
            return "Response:"+request.params(":user1")+"User1 : "+users[0] + " User 2 "+users[1];
        });
    }
}
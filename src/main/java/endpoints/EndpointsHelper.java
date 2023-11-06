package endpoints;

public class EndpointsHelper {

    public static String createNewRepoBody(String repoName, String repoDescription) {

        return "{\n" +
                "    \"name\": \"" + repoName + "\",\n" +
                "    \"description\": \"" + repoDescription + "\" \n" +
                "}";
    }

}

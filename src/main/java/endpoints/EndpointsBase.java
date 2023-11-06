package endpoints;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import utilities.Constants;

import static endpoints.EndpointsHelper.createNewRepoBody;
import static io.restassured.RestAssured.given;

public class EndpointsBase {

    public static Response createAuthUserNewRepo(String repoName, String repoDescription, String... headers) {
        RequestSpecification request = given().log().all();

        for (int i = 0; i < headers.length; i += 2) {
            request = request.header(headers[i], headers[i + 1]);
        }
        return request
                .contentType(ContentType.JSON)
                .body(createNewRepoBody(repoName, repoDescription))
                .when()
                .post(Constants.BASE_URL + Constants.CREATE_REPO_FOR_AUTH_USER_URL).prettyPeek();

    }
}

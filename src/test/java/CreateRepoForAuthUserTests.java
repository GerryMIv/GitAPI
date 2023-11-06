
import io.restassured.http.ContentType;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import io.restassured.module.jsv.JsonSchemaValidator;
import org.junit.jupiter.params.provider.NullSource;
import utilities.Constants;

import static endpoints.EndpointsBase.createAuthUserNewRepo;
import static endpoints.EndpointsHelper.createNewRepoBody;
import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class CreateRepoForAuthUserTests {
    public static class ValidRequests extends CreateRepoForAuthUserTests {

        @Test
        public void createNewRepoForAuthUser() {
            String repoName = RandomStringUtils.randomAlphabetic(10);
            String repoDescription  = RandomStringUtils.randomAlphabetic(20);
            createAuthUserNewRepo(repoName, repoDescription, "Authorization", Constants.USER_AUTH, Constants.GIT_HUB_API_VERSION_KEY, Constants.GIT_HUB_API_VERSION_VALUE, "Accept", Constants.CONTENT_TYPE)
                    .prettyPeek()
                    .then()
                    .extract().response().then().statusCode(201).body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/CreateRepoForAuthUser.json"))
                    .body("name", equalTo(repoName)).and().body("description", equalTo(repoDescription));
        }

        @Test
        public void createNewRepoForAuthUserRepoNameMinimumCharacters() {
            String repoName = RandomStringUtils.randomAlphabetic(1);
            String repoDescription  = RandomStringUtils.randomAlphabetic(20);
            createAuthUserNewRepo(repoName, repoDescription, "Authorization", Constants.USER_AUTH, Constants.GIT_HUB_API_VERSION_KEY, Constants.GIT_HUB_API_VERSION_VALUE, "Accept", Constants.CONTENT_TYPE)
                    .prettyPeek()
                    .then()
                    .extract().response().then().statusCode(201).body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/CreateRepoForAuthUser.json"))
                    .body("name", equalTo(repoName)).and().body("description", equalTo(repoDescription));
        }

        @Test
        public void createNewRepoForAuthUserAndTokenWithSpaces() {
            String repoName = RandomStringUtils.randomAlphabetic(10);
            String repoDescription  = RandomStringUtils.randomAlphabetic(20);
            createAuthUserNewRepo(repoName, repoDescription, "Authorization", " " + Constants.USER_AUTH + "   ", Constants.GIT_HUB_API_VERSION_KEY, Constants.GIT_HUB_API_VERSION_VALUE, "Accept", Constants.CONTENT_TYPE)
                    .prettyPeek()
                    .then()
                    .extract().response().then().statusCode(201).body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/CreateRepoForAuthUser.json"))
                    .body("name", equalTo(repoName)).and().body("description", equalTo(repoDescription));
        }

        @Test
        public void createNewRepoForAuthUserEmptyDescription() {
            String repoName = RandomStringUtils.randomAlphabetic(10);
            createAuthUserNewRepo(repoName, "", "Authorization", " " + Constants.USER_AUTH + "   ", Constants.GIT_HUB_API_VERSION_KEY, Constants.GIT_HUB_API_VERSION_VALUE, "Accept", Constants.CONTENT_TYPE)
                    .prettyPeek()
                    .then()
                    .extract().response().then().statusCode(201).body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/CreateRepoForAuthUser.json"))
                    .body("name", equalTo(repoName)).and().body("description", equalTo(null));
        }

        @Test
        public void createDuplicatedNameRepoForAuthUser() {
            String repoName = RandomStringUtils.randomAlphabetic(10);
            createAuthUserNewRepo(repoName, RandomStringUtils.randomAlphabetic(20), "Authorization", Constants.USER_AUTH, Constants.GIT_HUB_API_VERSION_KEY, Constants.GIT_HUB_API_VERSION_VALUE, "Accept", Constants.CONTENT_TYPE);
            createAuthUserNewRepo(repoName, RandomStringUtils.randomAlphabetic(20), "Authorization", Constants.USER_AUTH, Constants.GIT_HUB_API_VERSION_KEY, Constants.GIT_HUB_API_VERSION_VALUE, "Accept", Constants.CONTENT_TYPE)
                    .prettyPeek()
                    .then()
                    .extract().response().then().statusCode(422).body("errors[0].message", equalTo("name already exists on this account"));
        }

        @Test
        public void missingHeaderAccept() {
            String repoName = RandomStringUtils.randomAlphabetic(10);
            String repoDescription = RandomStringUtils.randomAlphabetic(20);
            createAuthUserNewRepo(repoName, repoDescription, "Authorization", Constants.USER_AUTH, Constants.GIT_HUB_API_VERSION_KEY, Constants.GIT_HUB_API_VERSION_VALUE)
                    .prettyPeek()
                    .then()
                    .extract().response().then().statusCode(201).body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/CreateRepoForAuthUser.json"))
                    .body("name", equalTo(repoName)).and().body("description", equalTo(repoDescription));
        }

        @Test
        public void missingHeaderXGit() {
            String repoName = RandomStringUtils.randomAlphabetic(10);
            String repoDescription = RandomStringUtils.randomAlphabetic(20);
            createAuthUserNewRepo(repoName, repoDescription, "Authorization", Constants.USER_AUTH, "Accept", Constants.CONTENT_TYPE)
                    .prettyPeek()
                    .then()
                    .extract().response().then().statusCode(201).body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/CreateRepoForAuthUser.json"))
                    .body("name", equalTo(repoName)).and().body("description", equalTo(repoDescription));
        }
    }

    public static class InvalidRequests extends CreateRepoForAuthUserTests {

        @ParameterizedTest
        @ValueSource(strings = {"", Constants.MISSING_BEARER_TOKEN, "$*%$€)№*§€"})
        public void invalidToken(String invalidToken) {
            createAuthUserNewRepo(RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(20), "Authorization", invalidToken, Constants.GIT_HUB_API_VERSION_KEY, Constants.GIT_HUB_API_VERSION_VALUE, "Accept", Constants.CONTENT_TYPE)
                    .prettyPeek()
                    .then()
                    .extract().response().then().statusCode(401).body("message", equalTo("Requires authentication"));
        }

        @Test
        public void expiredToken() {
            createAuthUserNewRepo(RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(20), "Authorization", Constants.EXPIRED_USER_AUTH, Constants.GIT_HUB_API_VERSION_KEY, Constants.GIT_HUB_API_VERSION_VALUE, "Accept", Constants.CONTENT_TYPE)
                    .prettyPeek()
                    .then()
                    .extract().response().then().statusCode(401).body("message", equalTo("Bad credentials"));
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {" ", "123", "", "&*#$", "jj.0"})
        public void invalidValueRepoName(String repoName) {
            createAuthUserNewRepo(repoName, RandomStringUtils.randomAlphabetic(20), "Authorization", Constants.EXPIRED_USER_AUTH, Constants.GIT_HUB_API_VERSION_KEY, Constants.GIT_HUB_API_VERSION_VALUE, "Accept", Constants.CONTENT_TYPE)
                    .prettyPeek()
                    .then()
                    .extract().response().then().statusCode(401).body("message", equalTo("Bad credentials"));
        }

        @Test
        public void missingHeaderAuthorization() {
            createAuthUserNewRepo(RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(20), Constants.GIT_HUB_API_VERSION_KEY, Constants.GIT_HUB_API_VERSION_VALUE, "Accept", Constants.CONTENT_TYPE)
                    .prettyPeek()
                    .then()
                    .extract().response().then().statusCode(401).body("message", equalTo("Requires authentication"));
        }

        @Test
        public void missingNameAsKeyInJsonBody() {
            given().log().all()
                    .header("Authorization", Constants.USER_AUTH)
                    .header("Accept", Constants.CONTENT_TYPE)
                    .header(Constants.GIT_HUB_API_VERSION_KEY, Constants.GIT_HUB_API_VERSION_VALUE)
                    .contentType(ContentType.JSON)
                    .body("{\"description\":\"descr\"}")
                    .when()
                    .post(Constants.BASE_URL + Constants.CREATE_REPO_FOR_AUTH_USER_URL).prettyPeek()
                    .then()
                    .extract()
                    .response()
                    .then()
                    .statusCode(422)
                    .body("errors[1].message", equalTo("name is too short (minimum is 1 character)"));
        }

        @Test
        public void patch() {
            given().log().all()
                    .header("Authorization", Constants.USER_AUTH)
                    .header("Accept", Constants.CONTENT_TYPE)
                    .header(Constants.GIT_HUB_API_VERSION_KEY, Constants.GIT_HUB_API_VERSION_VALUE)
                    .contentType(ContentType.JSON)
                    .body(createNewRepoBody(RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(20)))
                    .when()
                    .patch(Constants.BASE_URL + Constants.CREATE_REPO_FOR_AUTH_USER_URL).prettyPeek()
                    .then()
                    .extract()
                    .response()
                    .then()
                    .statusCode(404)
                    .body("message", equalTo("Not Found"));
        }

        @Test
        public void emptyBody() {
            given().log().all()
                    .header("Authorization", Constants.USER_AUTH)
                    .header("Accept", Constants.CONTENT_TYPE)
                    .header(Constants.GIT_HUB_API_VERSION_KEY, Constants.GIT_HUB_API_VERSION_VALUE)
                    .contentType(ContentType.JSON)
                    .body("")
                    .when()
                    .post(Constants.BASE_URL + Constants.CREATE_REPO_FOR_AUTH_USER_URL).prettyPeek()
                    .then()
                    .extract()
                    .response()
                    .then()
                    .statusCode(400)
                    .body("message", equalTo("Body should be a JSON object"));
        }

    }


}

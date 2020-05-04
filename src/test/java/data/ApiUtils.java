package data;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;


public class ApiUtils {

    static String url = System.getProperty("sut.url");

    static RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri(url.split(":")[0] + ":" + url.split(":")[1])
            .setPort(Integer.parseInt(url.split(":")[2].split("/")[0]))
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    public static int getRequestStatusCode(Card card, String path) {
        int statusCode =
                given()
                        .spec(requestSpec)
                        .body(card)
                        .when()
                        .post(path)
                        .getStatusCode();
        System.out.println(statusCode);
        return statusCode;
    }
}

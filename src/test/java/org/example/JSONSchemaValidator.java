package org.example;

import io.restassured.RestAssured;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

public class JSONSchemaValidator {

    @Test
    void getReq() {
        RestAssured.baseURI = "https://vpic.nhtsa.dot.gov";

        given()
                .queryParam("format", "json").
                when()
                .get("/api/vehicles/getallmakes").
                then().log().all()
                .assertThat().body(matchesJsonSchemaInClasspath("schema.json"))
                .statusCode(200)
                .body("Count", equalTo(11160)).body("Message", equalTo("Response returned successfully"));
    }


    @Test
    void postReq() {
        RestAssured.baseURI = "http://restapi.adequateshop.com";

        String requestBody = "{ \"tourist_name\": \"Mike\", \"tourist_email\": \"uniqueemail@example.com\", \"tourist_location\": \"Paris\" }";

        SoftAssert softAssert = new SoftAssert();

        io.restassured.response.Response response =
                given()
                        .contentType("application/json")
                        .body(requestBody).
                        when()
                        .post("/api/Tourist").
                        then().log().all()
                        .extract().response();

        // Check status code
        int statusCode = response.getStatusCode();
        softAssert.assertTrue(statusCode == 201 || statusCode == 400, "Unexpected status code: " + statusCode);

        // Check response body based on status code
        if (statusCode == 200) {
            softAssert.assertEquals(response.path("tourist_name"), "Mike", "Incorrect tourist name");
            softAssert.assertEquals(response.path("tourist_email"), "uniqueemail@example.com", "Incorrect email");
            softAssert.assertEquals(response.path("tourist_location"), "Paris", "Incorrect location");

        } else if (statusCode == 400) {
            softAssert.assertEquals(response.path("Message"), "Pleae try with different email address!", "Incorrect error message");
        }

        softAssert.assertAll(); // This will report all failures at once
    }
}

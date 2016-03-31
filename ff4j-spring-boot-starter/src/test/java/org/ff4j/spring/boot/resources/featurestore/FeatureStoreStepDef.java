package org.ff4j.spring.boot.resources.featurestore;

import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.ff4j.cache.FF4jCacheProxy;
import org.ff4j.cache.InMemoryCacheManager;
import org.ff4j.core.Feature;
import org.ff4j.spring.boot.resources.AbstractStepDef;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Paul
 *
 * @author <a href="mailto:paul58914080@gmail.com">Paul Williams</a>
 */
public class FeatureStoreStepDef extends AbstractStepDef {

    @Before
    @Override
    public void init() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Given("^the feature store is cleared$")
    public void the_feature_store_is_cleared() throws Throwable {
        clearFeatureStore();
    }

    @Given("^the following features exists in the feature store$")
    public void the_following_features_exists_in_the_feature_store(List<FeaturePojo> features) throws Throwable {
        createFeatures(features);
    }

    @Given("^the following features are cached$")
    public void the_following_features_are_cached(List<FeaturePojo> features) throws Throwable {
        for (FeaturePojo featurePojo : features) {
            Feature feature = new Feature(featurePojo.getUid(), Boolean.valueOf(featurePojo.getEnable()),
                    featurePojo.getDescription(), featurePojo.getGroup(),
                    Arrays.asList(featurePojo.getPermissions().split(",")));
            ((FF4jCacheProxy) ff4j.getFeatureStore()).getCacheManager().putFeature(feature);
        }
    }

    @Given("^the feature store is cached$")
    public void the_feature_store_is_cached() throws Throwable {
        FF4jCacheProxy proxy = new FF4jCacheProxy(ff4j.getFeatureStore(), null, new InMemoryCacheManager());
        ff4j.setFeatureStore(proxy);
    }

    @When("^the user requests for a feature by \"([^\"]*)\" by \"([^\"]*)\" http method and content type as \"([^\"]*)\"$")
    public void the_user_requests_for_a_feature_by_by_http_method_and_content_type_as(String path, String httpMethod, String contentType) throws Throwable {
        constructRequestBuilder(path, httpMethod, contentType);
    }

    @Then("^the user gets the response with response code \"([^\"]*)\"$")
    public void the_user_gets_the_response_with_response_code(int expectedStatusCode) throws Throwable {
        assertStatus(expectedStatusCode);
    }

    @Then("^the user gets an error response with code \"([^\"]*)\" and error message as \"([^\"]*)\"$")
    public void the_user_gets_an_error_response_with_code_and_error_message_as(int statusCode, String expectedErrorResponse) throws Throwable {
        assertErrorCodeAndMessage(statusCode, expectedErrorResponse);
    }

    @Then("^the response body as$")
    public void the_response_body_as(String expectedResponse) throws Throwable {
        assertJsonResponse(expectedResponse);
    }
}

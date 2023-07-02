package implementations.backend.auth;

import com.orgname.qa.configuration.Services;
import com.orgname.qa.lib.restassured.RestAssuredFactory;
import io.restassured.specification.RequestSpecification;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.serenitybdd.core.Serenity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Auth0Controller {

    public static RequestSpecification buildReqSpecWithToken(final Services service,
                                                             final String endPoint,
                                                             final Services tokenService) {
        RequestSpecification ra = RestAssuredFactory.getRestClient(service);
        String user = Serenity.sessionVariableCalled("CURRENT_USER");
        String accessTokenKey = String.format("%s_%s_%s", tokenService.getService(), user, "AUTH0");
        return ra.basePath(endPoint).auth().preemptive().oauth2(Serenity.sessionVariableCalled(accessTokenKey));
    }

    public static RequestSpecification buildReqSpecWithToken(final Services service, final Services tokenService) {
        return buildReqSpecWithToken(service, "", tokenService);
    }

    public static RequestSpecification buildReqSpecWithToken(final Services service, final String endPoint) {
        return buildReqSpecWithToken(service, endPoint, service);
    }

    public static RequestSpecification buildReqSpecWithToken(final Services service) {
        return buildReqSpecWithToken(service, "", service);
    }
}

package adapter.in.Links;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

public class LinkHelper {

    public static void addTemplateLink(Response.ResponseBuilder responseBuilder,
                                       UriInfo uriInfo,
                                       String rel,
                                       String... queryParams) {


        String baseUri = uriInfo.getBaseUriBuilder()
                .path(uriInfo.getPath())
                .build()
                .toString();

        StringBuilder template = new StringBuilder(baseUri);
        if (queryParams.length > 0) {
            template.append("?");
            for (int i = 0; i < queryParams.length; i++) {

                template.append(queryParams[i]).append("={").append(queryParams[i]).append("}");

                if (i < queryParams.length - 1) {
                    template.append("&");
                }
            }
        }

        String headerValue = String.format("<%s>; rel=\"%s\"; templated=\"true\"",
                template,
                rel);

        responseBuilder.header("Link", headerValue);
    }
}

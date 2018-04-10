package com.application.controller.rest;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;
import javax.validation.constraints.Positive;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Response;
import java.util.concurrent.TimeUnit;

@Stateless
@Path("/restService")
public class RestService {

    private Client client;
    private WebTarget webTarget;

    @PostConstruct
    public void init() {
        client = ClientBuilder.newBuilder()
                .readTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();
        webTarget = client.target("http://localhost:8080/ReactiveJAXRS/remoteUser");
    }

    @PreDestroy
    public void destroy() {
        client.close();
    }

    @GET
    public void asyncService(@Suspended AsyncResponse asyncResponse) {
        webTarget.request().async().get(new InvocationCallback<Response>() {

            @Override
            public void completed(Response response) {
                asyncResponse.resume(response);
            }

            @Override
            public void failed(Throwable throwable) {
                asyncResponse.resume(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(throwable.getMessage()).build());
            }
        });
    }
}

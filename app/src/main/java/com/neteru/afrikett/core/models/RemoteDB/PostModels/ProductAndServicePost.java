package com.neteru.afrikett.core.models.RemoteDB.PostModels;

import com.neteru.afrikett.core.models.RemoteDB.Post;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class ProductAndServicePost extends Post {

    public ProductAndServicePost(){super();}

    public ProductAndServicePost(String id, String showcaseId, String authorId, Integer type, String productOrServiceName, String productOrServiceDescription, String productOrServicePrice, Boolean productOrServiceAvailability, ArrayList<String> previews){
        super(id, showcaseId, authorId, type);

        this.productOrServiceName = productOrServiceName;
        this.productOrServiceDescription = productOrServiceDescription;
        this.productOrServicePrice = productOrServicePrice;
        this.productOrServiceAvailability = productOrServiceAvailability;
        this.previews = previews;
    }
}

package com.foo;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;

@Entity
public class MyEntity {

    @Id
    protected String myEntityId;
    protected MyId myEmbeddedEntity;
}

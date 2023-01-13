package com.foo;

import com.antwerkz.bottlerocket.BottleRocket;
import com.antwerkz.bottlerocket.BottleRocketTest;
import com.github.zafarkhaja.semver.Version;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.mapping.MapperOptions;
import dev.morphia.mapping.NamingStrategy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Set;

public class ReproducerTest extends BottleRocketTest {
    private Datastore datastore;

    public ReproducerTest() throws IOException {
        MongoClient mongo = getMongoClient();
        MongoDatabase database = getDatabase();
        database.drop();

        MapperOptions mapperOptions = MapperOptions.builder()
                .collectionNaming(NamingStrategy.identity())
                .propertyNaming(NamingStrategy.identity())
                .build();

        datastore = Morphia.createDatastore(mongo, getDatabase().getName(), mapperOptions);

        Set<Class<?>> aggregates = AggrageteLoader.loadAnnotatedWithEntity();
        for (Class aggregate : aggregates) {
            datastore.getMapper().map(aggregate);
            datastore.ensureIndexes();
        }


        MongoCollection<BasicDBObject> collection = datastore
                .getDatabase()
                .getCollection("myCollection", BasicDBObject.class);
        BasicDBObject document = BasicDBObject.parse(fromInputStream(this.getClass().getClassLoader().getResourceAsStream("myfile.json")));
        collection.insertOne(document);

        collection = datastore
                .getDatabase()
                .getCollection("myCollection", BasicDBObject.class);
        document = BasicDBObject.parse(fromInputStream(this.getClass().getClassLoader().getResourceAsStream("myfile2.json")));
        collection.insertOne(document);

    }

    @NotNull
    @Override
    public String databaseName() {
        return "morphia_repro";
    }

    @Nullable
    @Override
    public Version version() {
        return BottleRocket.DEFAULT_VERSION;
    }

    @Test
    public void reproduce() {
        MongoCollection<BasicDBObject> collection = datastore
                .getDatabase()
                .getCollection("myCollection", BasicDBObject.class);

        collection.find(MyEntity.class).forEach(entity ->
                System.out.println("id: " + entity.myEntityId + "\n" +
                        "embedded-id: " + entity.myEmbeddedEntity.myId));
    }

    public static String fromInputStream(InputStream inputStream) throws IOException {
        InputStreamReader reader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
        StringBuilder stringBuilder = new StringBuilder();
        for (int c; (c = reader.read()) != -1; ) {
            stringBuilder.append((char) c);
        }
        return stringBuilder.toString();
    }

}

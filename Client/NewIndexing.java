package org.typesense.Client;

import org.typesense.api.Client;
import org.typesense.api.Configuration;
import org.typesense.model.*;
import org.typesense.resources.Node;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;

public class NewIndexing {

    public void data() throws Exception {
        ArrayList<Node> nodes = new ArrayList<>();
        nodes.add(new Node( "http", "localhost", "8108"));

        Configuration configuration = new Configuration(nodes, Duration.ofSeconds(2),"p4Sz1Q07jExjlX3jueUHCIt6aOucLLqfTC3rO3HDJ7ZFm6Mf");
        Client client = new Client(configuration);

        CollectionSchema collectionSchema = new CollectionSchema();
        collectionSchema.name("books").defaultSortingField("ratings_count")
                .addFieldsItem(new Field().name("title").type("string"))
                .addFieldsItem(new Field().name("authors").type("string[]").facet(true))
                .addFieldsItem(new Field().name("publication_year").type("int32").facet(true))
                .addFieldsItem(new Field().name("ratings_count").type("int32"))
                .addFieldsItem(new Field().name("average_rating").type("float"));

        try {
            client.collections("books").delete();
        }catch (Exception e)
        {
            System.out.println("Not found");
        }
        CollectionResponse collectionResponse = client.collections().create(collectionSchema);

        String booksData = Files.readString(Path.of("/home/vasudevparma/IdeaProjects/Typesense/typesense-java-master/src/main/java/org/typesense/Data/books.jsonl"));
        String getNotification=client.collections("books").documents().import_(booksData);
        System.out.println(getNotification);

        SearchParameters searchParameters = new SearchParameters()
                .q("harry")
                .queryBy("title")
                .filterBy("publication_year:<1998")
                .sortBy("ratings_count:desc");

        SearchResult searchResult = client.collections("books").documents().search(searchParameters);
        System.out.println(searchResult);
    }
}

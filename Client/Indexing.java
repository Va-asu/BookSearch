package org.typesense.Client;

import org.typesense.api.*;
import org.typesense.model.*;
import org.typesense.resources.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;

public class Indexing {

    public void newClient() throws Exception {
        ArrayList<Node> nodes = new ArrayList<>();
        nodes.add(
                new Node(
                        "http",
                        "localhost",
                        "8108"
                )
        );

        //System.out.println("Node created ");
        Configuration configuration = new Configuration(nodes, Duration.ofSeconds(2),"p4Sz1Q07jExjlX3jueUHCIt6aOucLLqfTC3rO3HDJ7ZFm6Mf");
        Client client = new Client(configuration);

        CollectionSchema collectionSchema = new CollectionSchema();
        collectionSchema.name("bookDetails").defaultSortingField("ratings_count")
                .addFieldsItem(new Field().name("title").type("string"))
                .addFieldsItem(new Field().name("authors").type("string[]").facet(true))
                .addFieldsItem(new Field().name("publication_year").type("string").facet(true))
                .addFieldsItem(new Field().name("ratings_count").type("int32"))
                .addFieldsItem(new Field().name("average_rating").type("float"));


        //System.out.println("schema created ");
        try {
            client.collections("bookDetails").delete();
          //  System.out.println("deleting the books collection");
        }catch (Exception e)
        {
            System.out.println("Not found");
        }

        CollectionResponse collectionResponse = client.collections().create(collectionSchema);
         String booksData = Files.readString(Path.of("/home/vasudevparma/IdeaProjects/Typesense/typesense-java-master/src/main/java/org/typesense/Data/books.jsonl"));
        client.collections("books").documents().import_(booksData);
        SearchParameters searchParameters = new SearchParameters()
                .q("Harry")
                .queryBy("title")
                .sortBy("ratings_count:desc");

        SearchResult searchResult = client.collections("books").documents().search(searchParameters);
       // System.out.println(searchResult);
    }


}

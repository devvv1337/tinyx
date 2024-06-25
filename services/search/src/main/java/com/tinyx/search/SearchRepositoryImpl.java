package com.tinyx.search;

import com.tinyx.base.SearchRepository;
import com.tinyx.models.PostEntity;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class SearchRepositoryImpl implements SearchRepository {
    @Inject
    RestHighLevelClient client;

    @Override
    public List<String> searchPosts(String query) {
        try {
            SearchRequest searchRequest = new SearchRequest("posts");
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

            String[] terms = query.split(" ");
            for (String term : terms) {
                if (term.startsWith("#")) {
                    boolQuery.must(QueryBuilders.termQuery("hashtags", term.substring(1)));
                } else {
                    boolQuery.should(QueryBuilders.matchQuery("content", term));
                }
            }

            sourceBuilder.query(boolQuery);
            searchRequest.source(sourceBuilder);

            SearchHit[] hits = client.search(searchRequest, RequestOptions.DEFAULT).getHits().getHits();
            return Arrays.stream(hits)
                    .map(hit -> hit.getSourceAsMap().toString())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Error while searching posts", e);
        }
    }

    @Override
    public List<String> searchUsers(String query) {
        try {
            SearchRequest searchRequest = new SearchRequest("users");
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

            String[] terms = query.split(" ");
            for (String term : terms) {
                boolQuery.should(QueryBuilders.matchQuery("name", term));
            }

            sourceBuilder.query(boolQuery);
            searchRequest.source(sourceBuilder);

            SearchHit[] hits = client.search(searchRequest, RequestOptions.DEFAULT).getHits().getHits();
            return Arrays.stream(hits)
                    .map(hit -> hit.getSourceAsMap().toString())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Error while searching users", e);
        }
    }

    @Override
    public void indexPost(PostEntity post) {
        try {
            Map<String, Object> postMap = Map.of(
                    "id", post.getId().toString(),
                    "userId", post.getUserId(),
                    "content", post.getContent(),
                    "replyTo", post.getReplyTo() != null ? post.getReplyTo().toString() : null,
                    "repostOf", post.getRepostOf() != null ? post.getRepostOf().toString() : null,
                    "createdAt", post.getCreatedAt()
            );
            IndexRequest request = new IndexRequest("posts").id(post.getId().toString()).source(postMap);
            client.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException("Error while indexing post", e);
        }
    }

    @Override
    public void deletePost(String postId) {
        try {
            DeleteRequest request = new DeleteRequest("posts", postId);
            client.delete(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException("Error while deleting post", e);
        }
    }
}

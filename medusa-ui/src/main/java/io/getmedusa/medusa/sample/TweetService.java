package io.getmedusa.medusa.sample;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class TweetService {

    private static final Map<String, Tweet> tweets = new HashMap<>() {
        {
            put("linustorvalds", new Tweet("Linus Torvalds", "Talk is cheap. Show me the code."));
            put("robertmartin", new Tweet("Robert Martin", "Truth can only be found in one place: the code."));
            put("martinfowler", new Tweet("Martin Fowler", "Any fool can write code that a computer can understand. Good programmers write code that humans can understand."));
        }
    };

    public Tweet getByAuthor(String author) {
        return Tweet.of(tweets.get(author));
    }

}

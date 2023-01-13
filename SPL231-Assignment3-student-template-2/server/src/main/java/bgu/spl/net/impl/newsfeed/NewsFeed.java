package bgu.spl.net.impl.newsfeed;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NewsFeed {

    private ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> channels = new ConcurrentHashMap<>();

    public ArrayList<String> fetch(String channel) {
        ConcurrentLinkedQueue<String> queue = channels.get(channel);
        if (queue == null) {
            return new ArrayList<>(0); //empty
        } else {
            return new ArrayList<>(queue); //copy of the queue, arraylist is serializable
        }
    }

    public void publish(String channel, String news) {
        ConcurrentLinkedQueue<String> queue = channels.computeIfAbsent(channel, k -> new ConcurrentLinkedQueue<>());
        queue.add(news);
    }

    public void clear() {
        channels.clear();
    }

    public String toString(){
        Collection<String> channelSet =  channels.keySet();
        String ans = "";
        for (String channelName : channelSet) {//FOR EACH CHANNEL
            ConcurrentLinkedQueue<String> channalsReports = channels.get(channelName);
            String chanString = "";
            for (String report : channalsReports) {
                chanString +=report+", ";
            }
            ans+=channelName+":\n" + chanString +"\n";
        }
        return ans;
    }
}

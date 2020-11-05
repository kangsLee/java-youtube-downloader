package com.github.kiulian.downloader.model.playlist;


import com.alibaba.fastjson.JSONObject;
import com.github.kiulian.downloader.YoutubeException.DownloadUnavailableException;
import com.github.kiulian.downloader.YoutubeException.LiveVideoException;
import com.github.kiulian.downloader.YoutubeException.RestrictedVideoException;
import com.github.kiulian.downloader.model.AbstractVideoDetails;

public class PlaylistVideoDetails extends AbstractVideoDetails {

    private int index;
    private boolean isPlayable;

    public PlaylistVideoDetails() {
    }

    public PlaylistVideoDetails(JSONObject json) {
        super(json);
        if (json.containsKey("shortBylineText")) {
            author = json.getJSONObject("shortBylineText").getJSONArray("runs").getJSONObject(0).getString("text");
        }
        JSONObject jsonTitle = json.getJSONObject("title");
        if (jsonTitle.containsKey("simpleText")) {
            title = jsonTitle.getString("simpleText");
        } else {
            title = jsonTitle.getJSONArray("runs").getJSONObject(0).getString("text");
        }
        if (!thumbnails().isEmpty()) {
            // Otherwise, contains "/hqdefault.jpg?"
            isLive = thumbnails().get(0).contains("/hqdefault_live.jpg?");
        }

        if (json.containsKey("index")) {
            index = json.getJSONObject("index").getIntValue("simpleText");
        }
        isPlayable = json.getBooleanValue("isPlayable");
    }

    @Override
    protected void checkDownload() throws DownloadUnavailableException {
        if (!isPlayable) {
            throw new RestrictedVideoException("Can not download " + title());
        } else if (isLive() || lengthSeconds() == 0) {
            throw new LiveVideoException("Can not download live stream");
        }
    }

    public int index() {
        return index;
    }

    public boolean isPlayable() {
        return isPlayable;
    }
}

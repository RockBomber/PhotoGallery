package com.bignerdranch.android.photogallery;

import android.net.Uri;
import android.util.Log;
import android.util.Pair;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FlickrFetchr {

    private static final String TAG = "FlickrFetchr";

    private static final String API_KEY = "88dfa6ea37cf036ff8f25d1b9a109720";

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0 , bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public Pair<Integer, List<GalleryItem>> fetchItems(int page) {

        List<GalleryItem> items = new ArrayList<>();
        int pages = 1;

        try {
            String url = Uri.parse("https://api.flickr.com/services/rest/")
                    .buildUpon()
                    .appendQueryParameter("method", "flickr.photos.getRecent")
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("extras", "url_s")
                    .appendQueryParameter("page", String.valueOf(page))
                    .build().toString();
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
            Response response = new Gson().fromJson(jsonString, Response.class);
            parseItems(items, response);
            pages = response.getPhotos().getPages();
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        }

        return new Pair<>(pages, items);
    }

    private void parseItems(List<GalleryItem> items, Response response) {
        List<Photo> photos = response.getPhotos().getPhoto();

        for (Photo photo : photos) {

            GalleryItem item = new GalleryItem();
            item.setId(photo.getId());
            item.setCaption(photo.getTitle());

            if (photo.getUrl_s() == null) {
                continue;
            }

            item.setUrl(photo.getUrl_s());
            items.add(item);
        }
    }

    private class Response {

        private Photos photos;

        public Photos getPhotos() {
            return photos;
        }

        public void setPhotos(Photos photos) {
            this.photos = photos;
        }
    }

    private class Photos {

        private int page;
        private int pages;
        private int perpage;
        private int total;
        private List<Photo> photo;

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getPages() {
            return pages;
        }

        public void setPages(int pages) {
            this.pages = pages;
        }

        public int getPerpage() {
            return perpage;
        }

        public void setPerpage(int perpage) {
            this.perpage = perpage;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<Photo> getPhoto() {
            return photo;
        }

        public void setPhoto(List<Photo> photo) {
            this.photo = photo;
        }
    }

    private class Photo {
        private String id;
        private String owner;
        private String secert;
        private String server;
        private int farm;
        private String title;
        private int ispublic;
        private int isfriend;
        private int isfamily;
        private String url_s;
        private String height_s;
        private String width_s;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public String getSecert() {
            return secert;
        }

        public void setSecert(String secert) {
            this.secert = secert;
        }

        public String getServer() {
            return server;
        }

        public void setServer(String server) {
            this.server = server;
        }

        public int getFarm() {
            return farm;
        }

        public void setFarm(int farm) {
            this.farm = farm;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getIspublic() {
            return ispublic;
        }

        public void setIspublic(int ispublic) {
            this.ispublic = ispublic;
        }

        public int getIsfriend() {
            return isfriend;
        }

        public void setIsfriend(int isfriend) {
            this.isfriend = isfriend;
        }

        public int getIsfamily() {
            return isfamily;
        }

        public void setIsfamily(int isfamily) {
            this.isfamily = isfamily;
        }

        public String getUrl_s() {
            return url_s;
        }

        public void setUrl_s(String url_s) {
            this.url_s = url_s;
        }

        public String getHeight_s() {
            return height_s;
        }

        public void setHeight_s(String height_s) {
            this.height_s = height_s;
        }

        public String getWidth_s() {
            return width_s;
        }

        public void setWidth_s(String width_s) {
            this.width_s = width_s;
        }
    }

}

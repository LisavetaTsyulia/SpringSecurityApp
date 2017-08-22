package net.proselyte.springsecurityapp.model;

public class Product {
    private String url;

    private String image;

    private String name;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Product{" +
                "url='" + url + '\'' +
                ", image='" + image + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

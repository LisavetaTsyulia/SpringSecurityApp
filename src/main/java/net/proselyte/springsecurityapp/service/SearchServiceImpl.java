package net.proselyte.springsecurityapp.service;


import net.proselyte.springsecurityapp.model.Product;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class SearchServiceImpl implements SearchService{
    public final static String BEL = "http://belchip.by/";
    public final static String CHIPDIP = "https://www.ru-chipdip.by";
    @Override
    public List<Product> getProductsFromBelchip(String query) {
        List<Product> result = new ArrayList<>();
        try {
            Document doc = Jsoup.connect("http://belchip.by/search/?query=" + query).get();
            Elements divElem = doc.getElementsByClass("cat-item");
            for (Element div : divElem) {
                Product product = new Product();
                Elements links = div.select("a[href]");
                Element priceElement = div.getElementsByClass("butt-add").first();
                product.setImage(BEL + links.get(0).attr("href"));
                product.setUrl(BEL + links.get(2).attr("href"));
                product.setName(links.get(2).text());
                if (priceElement.hasClass("denoPrice")) {
                    Element denoPrice = priceElement.getElementsByClass("denoPrice").first();
                    product.setPrice(denoPrice.text() +
                            denoPrice.getElementsByTag("span").first().text());
                }
                result.add(product);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public List<Product> getProductsFromChipDip(String query) {
        List<Product> result = new ArrayList<>();
        try {
            Document doc = Jsoup.connect("https://www.ru-chipdip.by/search?searchtext=" + query).get();
            Elements ulElem = doc.getElementsByClass("serp__group-col");
            Elements links = ulElem.select("a[href]");
            for (Element link: links) {
                System.out.println(link);
                Document modul = Jsoup.connect(CHIPDIP + link.attr("href")).get();
                if (modul.getElementsByClass("with-hover").size() != 0)
                    hoverClassSearch(modul, result);
                else
                    itemClassSearch(modul, result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public List<Product> getNextPage(String query) {
        List<Product> productList = new ArrayList<>();

        return productList;
    }

    private void itemClassSearch(Document modul, List<Product> result) {
        Elements divElements = modul.getElementsByClass("item__content");
        for (Element divElement : divElements) {
            Element direction = divElement.select("a[href]").get(0);
            Element priceElement = divElement.getElementsByClass("price").get(0);
            Element imageWrapper = direction.getElementsByClass("item__image-wrapper")
                    .get(0).getElementsByTag("img").get(0);
            Product product = new Product();
            product.setName(imageWrapper.attr("alt"));
            product.setUrl(CHIPDIP + direction.attr("href"));
            product.setImage(imageWrapper.attr("src"));
            product.setPrice(priceElement.text());
            result.add(product);
        }
    }

    private void hoverClassSearch(Document modul, List<Product> result) {
        Elements trElements = modul.getElementsByClass("with-hover");
        for (Element trElement : trElements) {
            Element priceElement = trElement.getElementsByClass("price_mr").first();
            Element direction = trElement.getElementsByClass("name").select("a[href]").get(0);
            Product product = new Product();
            if (trElement.getElementsByClass("img-wrapper").size() != 0) {
                Element imageWrapper = trElement.getElementsByClass("img-wrapper")
                        .get(0).getElementsByTag("img").get(0);
                product.setImage(imageWrapper.attr("src"));
            }
            product.setName(direction.text());
            product.setUrl(CHIPDIP + direction.attr("href"));
            product.setPrice(priceElement.text());
            result.add(product);
        }
        Elements rightElements = modul.getElementsByClass("right");
        if (rightElements.size() != 0) {
            if (rightElements.get(0).getElementsByTag("a").size() != 0) {
                try {
                    modul = Jsoup.connect(CHIPDIP + rightElements.select("a[href]").first().attr("href")).get();
                    hoverClassSearch(modul, result);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

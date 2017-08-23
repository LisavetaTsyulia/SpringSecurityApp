package net.proselyte.springsecurityapp.service;


import net.proselyte.springsecurityapp.model.Product;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Service
public class SearchServiceImpl implements SearchService{
    public final static String BEL = "http://belchip.by/";
    public final static String CHIPDIP = "https://www.ru-chipdip.by";
    private int pointBelchip = 0;
    private int linknum = 0;
    private boolean isHover = false;
    private String theQuery;
    private Elements belchipElements;
    private Elements chipdipLinks;
    private Document curModul;

    public String getTheQuery() {
        return theQuery;
    }

    private void setTheQuery(String theQuery) {
        this.theQuery = theQuery;
    }

    @Override
    public List<Product> getFirstPage(String query) {
        List<Product> productList = new ArrayList<>();
        setTheQuery(query);
        pointBelchip = 0;
        //initBelchipSearch();
        //productList.addAll(getProductsFromBelchip(belchipElements));
        initChipdipSearch();
        productList = getDocumentFromChipDip(chipdipLinks);
        return productList;
    }

    private void initBelchipSearch() {
        try {
            Document doc = Jsoup.connect("http://belchip.by/search/?query=" + getTheQuery()).get();
            Elements divElements = doc.getElementsByClass("cat-item");
            belchipElements = divElements;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Product> getProductsFromBelchip(Elements divElem) {
        List<Product> result = new ArrayList<>();
        int resSize = pointBelchip + 10;
        if (resSize > divElem.size())
            resSize = divElem.size();
        for (int i = pointBelchip; i < resSize; i ++) {
            Product product = new Product();
            Elements links = divElem.get(i).select("a[href]");
            Element priceElement = divElem.get(i).getElementsByClass("butt-add").first();
            product.setImage(BEL + links.get(0).attr("href"));
            product.setUrl(BEL + links.get(2).attr("href"));
            product.setName(links.get(2).text());
            Elements denoPrice = priceElement.getElementsByClass("denoPrice");
            if (denoPrice.size() != 0) {
                Element price = denoPrice.first();
                product.setPrice(price.text());
            } else {
                product.setPrice("цена по запросу");
            }
            result.add(product);
        }
        pointBelchip = resSize;
        return result;
    }

    private void initChipdipSearch() {
        try {
            Document doc = Jsoup.connect("https://www.ru-chipdip.by/search?searchtext=" + getTheQuery()).get();
            Elements ulElem = doc.getElementsByClass("serp__group-col");
            chipdipLinks = ulElem.select("a[href]");
            if (chipdipLinks.size() != 0) {
                linknum = 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Product> getDocumentFromChipDip(Elements links) {
        try {
            if (linknum >= chipdipLinks.size()) {
                curModul = null;
                return null;
            }
            curModul = Jsoup.connect(CHIPDIP + links.get(linknum).attr("href")).get();
            if (curModul.getElementsByClass("with-hover").size() != 0) {
                isHover = true;
            }
            else{
                isHover = false;
            }
            return getOnePageChipdip();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Product> getOnePageChipdip() {
        List<Product> result = new ArrayList<>();
        if (isHover) {
            hoverClassSearch(result);
        } else {
            itemClassSearch(result);
        }
        return result;
    }

    @Override
    public List<Product> getNextPage() {
        /*
        List<Product> productList = getProductsFromBelchip(belchipElements);
        for (Product product : productList) {
            try {
                product.setName(URLEncoder.encode(product.getName(), "UTF-8"));
                product.setPrice(URLEncoder.encode(product.getPrice(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        */
        List<Product> productList2 = getProductsFromChipdip();
        for (Product product : productList2) {
            try {
                product.setName(URLEncoder.encode(product.getName(), "UTF-8"));
                product.setPrice(URLEncoder.encode(product.getPrice(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        //productList.addAll(productList2);
        return productList2;
    }

    private List<Product> getProductsFromChipdip() {
        return getOnePageChipdip();
    }

    private void itemClassSearch(List<Product> result) {
        if (linknum > chipdipLinks.size())
            return;
        Elements divElements = curModul.getElementsByClass("item__content");
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
            if (curModul.getElementsByClass("pager").size() == 0) {
                linknum++;
                getDocumentFromChipDip(chipdipLinks);
            } else {
                Elements rightElements = curModul.getElementsByClass("right");
                    if (rightElements.size() != 0) {
                        if (rightElements.get(0).getElementsByTag("a").size() != 0) {
                            try {
                                curModul = Jsoup.connect(CHIPDIP + rightElements.select("a[href]").first().attr("href")).get();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            linknum++;
                            getDocumentFromChipDip(chipdipLinks);
                        }
                    }
            }


    }

    private void hoverClassSearch( List<Product> result) {
        Elements trElements = curModul.getElementsByClass("with-hover");
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
        if (curModul.getElementsByClass("pager__pages").size() == 0) {
            linknum++;
            getDocumentFromChipDip(chipdipLinks);
        } else {
            Elements rightElements = curModul.getElementsByClass("right");
            if (rightElements.size() != 0) {
                if (rightElements.get(0).getElementsByTag("a").size() != 0) {
                    try {
                        curModul = Jsoup.connect(CHIPDIP + rightElements.select("a[href]").first().attr("href")).get();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    linknum ++;
                    getDocumentFromChipDip(chipdipLinks);
                }
            }
        }

    }
}

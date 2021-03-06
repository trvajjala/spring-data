package com.nixmash.springdata.jsoup.components;

import com.nixmash.springdata.jsoup.base.JSoupHtmlParser;
import com.nixmash.springdata.jsoup.base.JsoupImage;
import com.nixmash.springdata.jsoup.dto.PagePreviewDTO;
import com.nixmash.springdata.jsoup.utils.JsoupUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.nixmash.springdata.jsoup.utils.JsoupUtil.attrIntToNull;
import static com.nixmash.springdata.jsoup.utils.JsoupUtil.getBaseUri;
import static com.nixmash.springdata.jsoup.utils.JsoupUtil.trim;

@Component
public class JsoupUI {

    private static final Logger logger = LoggerFactory.getLogger(JsoupUI.class);

    @Autowired
    @Qualifier("pagePreviewParser")
    JSoupHtmlParser<PagePreviewDTO> pagePreviewParser;

    private Document doc;

    public void init() {
        File in = JsoupUtil.getFile("/html/github.html");
        try {
            String linkUrl = "http://mysite.com/some/path";
            doc = Jsoup.parse(in, null, getBaseUri(linkUrl));
        } catch (IOException e) {
            e.printStackTrace();
        }
        displayPagePreviewDTO();

    }

    private void displayPagePreviewDTO() {

        PagePreviewDTO pagePreviewDTO = pagePreviewParser.parse(doc);

        System.out.println("Title: " + pagePreviewDTO.getTitle());
        System.out.println("Twitter Image: " + pagePreviewDTO.getTwitterImage());
        System.out.println("Facebook Image: " + pagePreviewDTO.getFacebookImage());

        System.out.println("First Image in Readme content: " +
                pagePreviewDTO.getImages().get(0).src);

        System.out.println("Avatar Image: " + pagePreviewDTO.getAvatar().src);

        System.out.println("First Link in Readme content: " +
                pagePreviewDTO.getLinks().get(0).href);

        System.out.println("Link with 'mylink' class: " + pagePreviewDTO.getLink().href);

    }

    // region non-used demos

    private void displayImports() {
        Elements imports = doc.select("link[href]");
        print("\nImports: (%d)", imports.size());
        for (Element link : imports) {
            print(" * %s <%s> (%s)", link.tagName(), link.attr("abs:href"), link.attr("rel"));
        }
    }

    private List<JsoupImage> getImages(Document doc) {
        List<JsoupImage> images = new ArrayList<>();
        Elements elements;
            elements = doc.select("[src]");

        for (Element media : elements) {
            if (media.tagName().equals("img")) {
                JsoupImage img = new JsoupImage();
                img.setSrc(media.attr("abs:src"));
                img.setAlt(trim(media.attr("alt"), 60));
                img.setHeight(attrIntToNull(media.attr("height")));
                img.setWidth(attrIntToNull(media.attr("width")));
                images.add(img);
            }
        }
        return images;
    }

    private void displayLInks() {
        Elements links = doc.select("a[href]");
        print("\nLinks: (%d)", links.size());
        for (Element link : links) {
            print(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35));
        }
    }

    private void displayImages() {
        Elements media = doc.select("[src]");

        print("\nMedia: (%d)", media.size());
        for (Element src : media) {
            if (src.tagName().equals("img"))
                print(" * %s: <%s> %sx%s (%s)",
                        src.tagName(), src.attr("abs:src"), src.attr("width"), src.attr("height"),
                        trim(src.attr("alt"), 60));
            else
                print(" * %s: <%s>", src.tagName(), src.attr("abs:src"));
        }

    }

    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }


    // endregion

}

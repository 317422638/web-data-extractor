package im.nll.data.extractor;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import im.nll.data.extractor.entity.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static im.nll.data.extractor.Extractors.*;

/**
 * @author <a href="mailto:fivesmallq@gmail.com">fivesmallq</a>
 * @version Revision: 1.0
 * @date 15/12/25 下午9:34
 */
public class ExtractorsTest {
    private String baseHtml;
    private String listHtml;
    private String jsonString;


    @Before
    public void before() {
        try {
            baseHtml = Resources.toString(Resources.getResource("base.html"), Charsets.UTF_8);
            listHtml = Resources.toString(Resources.getResource("list.html"), Charsets.UTF_8);
            jsonString = Resources.toString(Resources.getResource("example.json"), Charsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGet() throws Exception {
        //use extract method to extract data
        String title = Extractors.on(baseHtml).extract(selector("a.title")).asString();
        //use with method to extend value extractor
        String followers = Extractors.on(baseHtml).extract(selector("div.followers")).with(regex("\\d+")).asString();
        //use filter method to process value
        String description = Extractors.on(baseHtml).extract(selector("div.description")).filter(value -> value.toLowerCase()).asString();
        String year = Extractors.on("<div> Talk is cheap. Show me the code. - Fri, 25 Aug 2000 </div>")
                .extract(selector("div")) // extract with selector
                .filter(value -> value.trim()) // trim result
                .with(regex("20\\d{2}")) // get year with regex
                .filter(value -> "from " + value) // append 'from' string
                .asString();
        Assert.assertEquals("fivesmallq", title);
        Assert.assertEquals("29671", followers);
        Assert.assertEquals("talk is cheap. show me the code.", description);
        Assert.assertEquals("from 2000", year);
    }

    @Test
    public void testToMap() throws Exception {
        Map<String, String> dataMap = Extractors.on(baseHtml)
                .extract("title", selector("a.title"))
                .extract("followers", selector("div.followers")).with(regex("\\d+"))
                .extract("description", selector("div.description"))
                .asMap();
        Assert.assertEquals("fivesmallq", dataMap.get("title"));
        Assert.assertEquals("29671", dataMap.get("followers"));
        Assert.assertEquals("Talk is cheap. Show me the code.", dataMap.get("description"));
    }

    @Test
    public void testToMapList() throws Exception {
        List<Map<String, String>> languages = Extractors.on(listHtml).split(jerry("tr.item.html"))
                .extract("type", selector("td.type"))
                .extract("name", selector("td.name"))
                .extract("url", selector("td.url"))
                .asMapList();
        Assert.assertNotNull(languages);
        Map<String, String> second = languages.get(1);
        Assert.assertEquals(languages.size(), 3);
        Assert.assertEquals(second.get("type"), "dynamic");
        Assert.assertEquals(second.get("name"), "Ruby");
        Assert.assertEquals(second.get("url"), "https://www.ruby-lang.org");
    }

    @Test
    public void testToBean() throws Exception {
        Base base = Extractors.on(baseHtml)
                .extract("title", selector("a.title"))
                .extract("followers", selector("div.followers")).with(regex("\\d+"))
                .extract("description", selector("div.description"))
                .asBean(Base.class);
        Assert.assertEquals("fivesmallq", base.getTitle());
        Assert.assertEquals("29671", base.getFollowers());
        Assert.assertEquals("Talk is cheap. Show me the code.", base.getDescription());
    }

    @Test
    public void testToBeanList() throws Exception {
        List<Language> languages = Extractors.on(listHtml).split(jerry("tr.item.html"))
                .extract("type", selector("td.type"))
                .extract("name", selector("td.name"))
                .extract("url", selector("td.url"))
                .asBeanList(Language.class);
        Assert.assertNotNull(languages);
        Language second = languages.get(1);
        Assert.assertEquals(languages.size(), 3);
        Assert.assertEquals(second.getType(), "dynamic");
        Assert.assertEquals(second.getName(), "Ruby");
        Assert.assertEquals(second.getUrl(), "https://www.ruby-lang.org");
    }


    @Test
    public void testToBeanListByJerryString() throws Exception {
        List<Language> languages = Extractors.on(listHtml).split("jerry:tr.item.html")
                .extract("type", "jerry:td.type")
                .extract("name", "jerry:td.name")
                .extract("url", "jerry:td.url")
                .asBeanList(Language.class);
        Assert.assertNotNull(languages);
        Language second = languages.get(1);
        Assert.assertEquals(languages.size(), 3);
        Assert.assertEquals(second.getType(), "dynamic");
        Assert.assertEquals(second.getName(), "Ruby");
        Assert.assertEquals(second.getUrl(), "https://www.ruby-lang.org");
    }

    @Test
    public void testToBeanListByXPath() throws Exception {
        List<Language> languages = Extractors.on(listHtml).split(xpath("//tr[@class='item']"))
                .extract("type", xpath("//td[1]/text()"))
                .extract("name", xpath("//td[2]/text()"))
                .extract("url", xpath("//td[3]/text()"))
                .asBeanList(Language.class);
        Assert.assertNotNull(languages);
        Language second = languages.get(1);
        Assert.assertEquals(languages.size(), 3);
        Assert.assertEquals(second.getType(), "dynamic");
        Assert.assertEquals(second.getName(), "Ruby");
        Assert.assertEquals(second.getUrl(), "https://www.ruby-lang.org");
    }

    @Test
    public void testToBeanListByXPathString() throws Exception {
        List<Language> languages = Extractors.on(listHtml).split("xpath://tr[@class='item']")
                .extract("type", "xpath://td[1]/text()")
                .extract("name", "xpath://td[2]/text()")
                .extract("url", "xpath://td[3]/text()")
                .asBeanList(Language.class);
        Assert.assertNotNull(languages);
        Language second = languages.get(1);
        Assert.assertEquals(languages.size(), 3);
        Assert.assertEquals(second.getType(), "dynamic");
        Assert.assertEquals(second.getName(), "Ruby");
        Assert.assertEquals(second.getUrl(), "https://www.ruby-lang.org");
    }

    @Test
    public void testToBeanListFilter() throws Exception {
        List<Language> languages = Extractors.on(listHtml).split(xpath("//tr[@class='item']"))
                .extract("type", xpath("//td[1]/text()")).filter(value -> "type:" + value)
                .extract("name", xpath("//td[2]/text()")).filter(value -> "name:" + value)
                .extract("url", xpath("//td[3]/text()")).filter(value -> "url:" + value)
                .asBeanList(Language.class);
        Assert.assertNotNull(languages);
        Language second = languages.get(1);
        Assert.assertEquals(languages.size(), 3);
        Assert.assertEquals(second.getType(), "type:dynamic");
        Assert.assertEquals(second.getName(), "name:Ruby");
        Assert.assertEquals(second.getUrl(), "url:https://www.ruby-lang.org");
    }

    @Test
    public void testToBeanListByJson() throws Exception {
        List<Book> books = Extractors.on(jsonString).split(json("$..book.*"))
                .extract("category", json("$..category"))
                .extract("author", json("$..author"))
                .extract("title", json("$..title"))
                .extract("price", json("$..price"))
                .asBeanList(Book.class);
        Assert.assertNotNull(books);
        Book second = books.get(1);
        Assert.assertEquals(books.size(), 4);
        Assert.assertEquals(second.getCategory(), "fiction");
        Assert.assertEquals(second.getAuthor(), "Evelyn Waugh");
        Assert.assertEquals(second.getTitle(), "Sword of Honour");
        Assert.assertEquals(second.getPrice(), new Double(12.99));
    }

    @Test
    public void testToBeanListByJsonString() throws Exception {
        List<Book> books = Extractors.on(jsonString).split("json:$..book.*")
                .extract("category", "json:$..category")
                .extract("author", "json:$..author")
                .extract("title", "json:$..title")
                .extract("price", "json:$..price")
                .asBeanList(Book.class);
        Assert.assertNotNull(books);
        Book second = books.get(1);
        Assert.assertEquals(books.size(), 4);
        Assert.assertEquals(second.getCategory(), "fiction");
        Assert.assertEquals(second.getAuthor(), "Evelyn Waugh");
        Assert.assertEquals(second.getTitle(), "Sword of Honour");
        Assert.assertEquals(second.getPrice(), new Double(12.99));
    }

    @Test
    public void testToBeanListByEntityExtractor() throws Exception {
        List<Book> books = Extractors.on(jsonString).split(json("$..book.*")).asBeanList(new EntityExtractor<Book>() {
            @Override
            public Book extract(String data) {
                return Extractors.on(data)
                        .extract("category", json("$..category"))
                        .extract("author", json("$..author"))
                        .extract("title", json("$..title"))
                        .extract("price", json("$..price")).asBean(Book.class);
            }
        });
        Assert.assertNotNull(books);
        Book second = books.get(1);
        Assert.assertEquals(books.size(), 4);
        Assert.assertEquals(second.getCategory(), "fiction");
        Assert.assertEquals(second.getAuthor(), "Evelyn Waugh");
        Assert.assertEquals(second.getTitle(), "Sword of Honour");
        Assert.assertEquals(second.getPrice(), new Double(12.99));
    }

    @Test
    public void testToBeanListByEntityListExtractor() throws Exception {
        List<Book> books = Extractors.on(jsonString).asBeanList(new EntityListExtractor<Book>() {
            @Override
            public List<Book> extractList(String data) {
                return Extractors.on(jsonString).split(json("$..book.*"))
                        .extract("category", json("$..category"))
                        .extract("author", json("$..author"))
                        .extract("title", json("$..title"))
                        .extract("price", json("$..price"))
                        .asBeanList(Book.class);
            }
        });
        Assert.assertNotNull(books);
        Book second = books.get(1);
        Assert.assertEquals(books.size(), 4);
        Assert.assertEquals(second.getCategory(), "fiction");
        Assert.assertEquals(second.getAuthor(), "Evelyn Waugh");
        Assert.assertEquals(second.getTitle(), "Sword of Honour");
        Assert.assertEquals(second.getPrice(), new Double(12.99));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSplit() {
        //regex is not implements ListableExtractor
        List<Language> languages = Extractors.on(listHtml).split(regex("tr.item.html"))
                .extract("type", selector("td.type"))
                .extract("name", selector("td.name"))
                .extract("url", selector("td.url"))
                .asBeanList(Language.class);
        Assert.assertNotNull(languages);
        Language second = languages.get(1);
        Assert.assertEquals(languages.size(), 3);
        Assert.assertEquals(second.getType(), "dynamic");
        Assert.assertEquals(second.getName(), "Ruby");
        Assert.assertEquals(second.getUrl(), "https://www.ruby-lang.org");
    }

}
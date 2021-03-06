package im.nll.data.extractor.impl;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import im.nll.data.extractor.exception.ExtractException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @author <a href="mailto:fivesmallq@gmail.com">fivesmallq</a>
 * @version Revision: 1.0
 * @date 16/1/6 上午12:32
 */
public class XPathExtractorTest {
    XPathExtractor xPathExtractor;
    private String baseHtml;
    private String base2Html;

    @Before
    public void setUp() throws Exception {
        baseHtml = Resources.toString(Resources.getResource("base.html"), Charsets.UTF_8);
        base2Html = Resources.toString(Resources.getResource("base2.html"), Charsets.UTF_8);
    }

    @Test(expected = ExtractException.class)
    public void testExtractParseError() throws Exception {
        //attribute
        xPathExtractor = new XPathExtractor("//div/a[1]/@href");
        //FIXME parse not standard html error.
        String s = xPathExtractor.extract(base2Html);
        Assert.assertEquals("/fivesmallq", s);
        //element
        xPathExtractor = new XPathExtractor("//div/a[1]");
        s = xPathExtractor.extract(baseHtml);
        Assert.assertEquals("<a href=\"/fivesmallq\" class=\"title\">fivesmallq</a>", s);
        //text
        xPathExtractor = new XPathExtractor("//div/a[1]/text()");
        s = xPathExtractor.extract(baseHtml);
        Assert.assertEquals("fivesmallq", s);
    }
    @Test
    public void testExtract() throws Exception {
        //attribute
        xPathExtractor = new XPathExtractor("//div/a[1]/@href");
        String s = xPathExtractor.extract(baseHtml);
        Assert.assertEquals("/fivesmallq", s);
        //element
        xPathExtractor = new XPathExtractor("//div/a[1]");
        s = xPathExtractor.extract(baseHtml);
        Assert.assertEquals("<a href=\"/fivesmallq\" class=\"title\">fivesmallq</a>", s);
        //text
        xPathExtractor = new XPathExtractor("//div/a[1]/text()");
        s = xPathExtractor.extract(baseHtml);
        Assert.assertEquals("fivesmallq", s);
    }

    @Test
    public void testExtractList() throws Exception {
        //attribute
        xPathExtractor = new XPathExtractor("//div/a/@href");
        List<String> s = xPathExtractor.extractList(baseHtml);
        Assert.assertNotNull(s);
        Assert.assertEquals(2, s.size());
        String second = s.get(1);
        Assert.assertEquals("/fivesmallq/followers", second);

        //element
        xPathExtractor = new XPathExtractor("//div/a");
        s = xPathExtractor.extractList(baseHtml);
        Assert.assertNotNull(s);
        Assert.assertEquals(2, s.size());
        second = s.get(1);
        Assert.assertEquals("<a href=\"/fivesmallq/followers\">29671 Followers</a>", second);

        //text
        xPathExtractor = new XPathExtractor("//div/a/text()");
        s = xPathExtractor.extractList(baseHtml);
        Assert.assertNotNull(s);
        Assert.assertEquals(2, s.size());
        second = s.get(1);
        Assert.assertEquals("29671 Followers", second);
    }
}
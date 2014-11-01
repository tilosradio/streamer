package hu.tilos.radio.backend.converters;


import hu.radio.tilos.model.Tag;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

public class TagUtilTest {

    @Test
    public void getTags() {
        //given
        String text = "ahoj #bob\n poplacsek #juk asd";
        TagUtil util = new TagUtil();

        //when
        Set<Tag> tags = util.getTags(text);

        //then
        Assert.assertEquals(2, tags.size());

    }

    @Test
    public void getTagsColorCode() {
        //given
        String text = "ahoj #232323 asd";
        TagUtil util = new TagUtil();

        //when
        Set<Tag> tags = util.getTags(text);

        //then
        Assert.assertEquals(0, tags.size());

    }

    @Test
    public void getTagsColorCode2() {
        //given
        String text = "ahoj #BBCCDD asd";
        TagUtil util = new TagUtil();

        //when
        Set<Tag> tags = util.getTags(text);

        //then
        Assert.assertEquals(0, tags.size());

    }

    @Test
    public void getTagsColorCode4() {
        //given
        String text = "ahoj #BBCCDD; asd";
        TagUtil util = new TagUtil();

        //when
        Set<Tag> tags = util.getTags(text);

        //then
        Assert.assertEquals(0, tags.size());

    }

    @Test
    public void getTagsEmbeddedStyle() {
        //given
        String text = "<style>\n @fonf-face \n</style>\n asd";
        TagUtil util = new TagUtil();

        //when
        Set<Tag> tags = util.getTags(text);

        //then
        Assert.assertEquals(0, tags.size());

    }


    @Test
    public void getTagsAnchor() {
        //given
        String text = "ahoj http://index.hu#anchor asd";
        TagUtil util = new TagUtil();

        //when
        Set<Tag> tags = util.getTags(text);

        //then
        Assert.assertEquals(0, tags.size());

    }

    @Test
    public void getTagsColorCode3() {
        //given
        String text = "ahoj #ahojBBCCDD asd";
        TagUtil util = new TagUtil();

        //when
        Set<Tag> tags = util.getTags(text);

        //then
        Assert.assertEquals(1, tags.size());

    }


    @Test
    public void getTagsSegmented() {
        //given
        String text = "ahoj #{Lajos Bela} asd";
        TagUtil util = new TagUtil();

        //when
        Set<Tag> tags = util.getTags(text);

        //then
        Assert.assertEquals(1, tags.size());
        Tag tag = tags.iterator().next();
        Assert.assertEquals("Lajos Bela", tag.getName());

    }

    @Test
    public void replaceToHtml() {
        //given
        String source = "Valami #valami hej ho";

        //when
        String result = new TagUtil().replaceToHtml(source);

        //then
        Assert.assertEquals("Valami <a href=\"/tag/valami\"><span class=\"label label-primary\">valami</span></a> hej ho", result);

    }


    @Test
    public void replaceToHtmlWithTags() {
        //given
        String source = "<p class=\"#valami\">Valami</p> #valami hej ho";

        //when
        String result = new TagUtil().replaceToHtml(source);

        //then
        Assert.assertEquals("<p class=\"#valami\">Valami</p> <a href=\"/tag/valami\"><span class=\"label label-primary\">valami</span></a> hej ho", result);

    }


}
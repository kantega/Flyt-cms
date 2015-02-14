package no.kantega.publishing.admin.content.htmlfilter;

import no.kantega.commons.xmlfilter.FilterPipeline;
import no.kantega.publishing.common.ao.MultimediaDao;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.MultimediaDimensions;
import no.kantega.publishing.multimedia.ImageEditor;
import no.kantega.publishing.spring.RootContext;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ImgHeightAndWidthFilterTest {

    private MultimediaDao multimediaDao;
    private ImageEditor imageEditor;

    @Before
    public void setup(){
        ApplicationContext context = mock(ApplicationContext.class);
        RootContext.setInstance(context);
        multimediaDao = mock(MultimediaDao.class);
        imageEditor = mock(ImageEditor.class);
        when(context.getBean(MultimediaDao.class)).thenReturn(multimediaDao);
        when(context.getBean(ImageEditor.class)).thenReturn(imageEditor);
    }

    @Test
    public void rewriteUrlWhenHeightAndWidthAttribute(){
        FilterPipeline pipeline = new FilterPipeline();
        pipeline.addFilter(new ImgHeightAndWidthFilter());

        Multimedia multimedia = new Multimedia();
        multimedia.setHeight(500);
        multimedia.setHeight(500);
        multimedia.setId(12341);
        multimedia.setFilename("omg");
        when(multimediaDao.getMultimedia(12341)).thenReturn(multimedia);

        when(imageEditor.getResizedImageDimensions(anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(new MultimediaDimensions(200, 200));

        String input = "<img src=\"/multimedia/12341/omg\" width=\"123\" height=\"123\" />";
        String expectedOutput = "<img src=\"/multimedia/12341/omg?width=200\" width=\"200\" height=\"200\">";

        assertThat(pipeline.filter(input), is(expectedOutput));

    }
}
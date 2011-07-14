package no.kantega.publishing.api.taglibs.paginate.content;

import no.kantega.publishing.api.taglibs.content.paginate.PaginatePage;
import no.kantega.publishing.api.taglibs.content.paginate.Paginator;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class PaginatorTest {

    @Test
    public void shouldReturnEmptyListWhenOnlyASinglePage(){
        Paginator paginator = new Paginator();
        List<PaginatePage> pages = paginator.getPaginatedList(1, 0, 3);
        assertEquals(0, pages.size());
    }

    @Test
    public void shouldReturn8PagesWhen8Pages() {
        Paginator paginator = new Paginator();
        List<PaginatePage> pages = paginator.getPaginatedList(8, 0, 3);
        assertEquals("|(1)|2|3|4|5|6|7|8|", getReadablePaginationList(pages));
    }

    @Test
    public void shouldAddGapAfterAfterPage1And11WhenIndexIs7() {
        Paginator paginator = new Paginator();
        List<PaginatePage> pages = paginator.getPaginatedList(15, 7, 3);
        assertEquals("|1|...|5|6|7|(8)|9|10|11|...|15|", getReadablePaginationList(pages));
    }

    @Test
    public void shouldAddGapAfterPage7WhenFirstPageIsCurrentPage() {
        Paginator paginator = new Paginator();
        List<PaginatePage> pages = paginator.getPaginatedList(11, 0, 3);
        assertEquals("|(1)|2|3|4|5|6|7|...|11|", getReadablePaginationList(pages));
    }

    @Test
    public void shouldAddGapAfterPage1WhenLastPageIsCurrentPage() {
        Paginator paginator = new Paginator();
        List<PaginatePage> pages = paginator.getPaginatedList(11, 10, 3);
        assertEquals("|1|...|5|6|7|8|9|10|(11)|", getReadablePaginationList(pages));
    }


    private String getReadablePaginationList(List<PaginatePage> pages){

        String pagination = "";
        for (PaginatePage page : pages) {
            if (page.isGap()) {
                pagination += "|...";
            } else if (page.isCurrentPage()) {
                pagination += "|(" + page.getPageNumber() + ")";
            } else {
                pagination += "|" + page.getPageNumber() + "";
            }
        }
        pagination += "|";
        return pagination;
    }
}

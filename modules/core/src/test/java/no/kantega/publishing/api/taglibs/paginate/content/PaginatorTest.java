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
        printPagesList(pages);
        assertEquals(8, pages.size());

        assertTrue(pages.get(0).isCurrentPage());

        // Page numbers
        assertEquals(1, pages.get(0).getPageNumber());
        assertEquals(2, pages.get(1).getPageNumber());
        assertEquals(3, pages.get(2).getPageNumber());
        assertEquals(4, pages.get(3).getPageNumber());
        assertEquals(5, pages.get(4).getPageNumber());
        assertEquals(6, pages.get(5).getPageNumber());
        assertEquals(7, pages.get(6).getPageNumber());
        assertEquals(8, pages.get(7).getPageNumber());

        // No gap pages
        assertFalse(pages.get(0).isGap());
        assertFalse(pages.get(1).isGap());
        assertFalse(pages.get(2).isGap());
        assertFalse(pages.get(3).isGap());
        assertFalse(pages.get(4).isGap());
        assertFalse(pages.get(5).isGap());
        assertFalse(pages.get(6).isGap());
        assertFalse(pages.get(7).isGap());

    }

    @Test
    public void shouldAddGapAfterAfterPage1And11WhenIndexIs7() {
        Paginator paginator = new Paginator();
        List<PaginatePage> pages = paginator.getPaginatedList(15, 7, 3);
        printPagesList(pages);
        assertEquals(11, pages.size());

        assertTrue(pages.get(5).isCurrentPage());

        // Page numbers
        assertEquals(1, pages.get(0).getPageNumber());
        assertEquals(5, pages.get(2).getPageNumber());
        assertEquals(6, pages.get(3).getPageNumber());
        assertEquals(7, pages.get(4).getPageNumber());
        assertEquals(8, pages.get(5).getPageNumber());
        assertEquals(9, pages.get(6).getPageNumber());
        assertEquals(10, pages.get(7).getPageNumber());
        assertEquals(11, pages.get(8).getPageNumber());
        assertEquals(15, pages.get(10).getPageNumber());

        // One gap page
        assertFalse(pages.get(0).isGap());
        assertTrue(pages.get(1).isGap());
        assertFalse(pages.get(2).isGap());
        assertFalse(pages.get(3).isGap());
        assertFalse(pages.get(4).isGap());
        assertFalse(pages.get(5).isGap());
        assertFalse(pages.get(6).isGap());
        assertFalse(pages.get(7).isGap());
        assertFalse(pages.get(8).isGap());
        assertTrue(pages.get(9).isGap());
        assertFalse(pages.get(10).isGap());
    }

    @Test
    public void shouldAddGapAfterPage7WhenFirstPageIsCurrentPage() {
        Paginator paginator = new Paginator();
        List<PaginatePage> pages = paginator.getPaginatedList(11, 0, 3);
        printPagesList(pages);
        assertEquals(9, pages.size());

        assertTrue(pages.get(0).isCurrentPage());

        // Page numbers
        assertEquals(1, pages.get(0).getPageNumber());
        assertEquals(2, pages.get(1).getPageNumber());
        assertEquals(3, pages.get(2).getPageNumber());
        assertEquals(4, pages.get(3).getPageNumber());
        assertEquals(5, pages.get(4).getPageNumber());
        assertEquals(6, pages.get(5).getPageNumber());
        assertEquals(7, pages.get(6).getPageNumber());
        assertEquals(11, pages.get(8).getPageNumber());

        // One gap page
        assertFalse(pages.get(0).isGap());
        assertFalse(pages.get(1).isGap());
        assertFalse(pages.get(2).isGap());
        assertFalse(pages.get(3).isGap());
        assertFalse(pages.get(4).isGap());
        assertFalse(pages.get(5).isGap());
        assertFalse(pages.get(6).isGap());
        assertTrue(pages.get(7).isGap());
        assertFalse(pages.get(8).isGap());
    }

    private void printPagesList(List<PaginatePage> pages){

        String pagination = "";
        for (PaginatePage page : pages) {
            if (page.isGap()) {
                pagination += " | ...";
            } else if (page.isCurrentPage()) {
                pagination += " | (" + page.getPageNumber() + ")";
            } else {
                pagination += " | " + page.getPageNumber() + "";
            }
        }
        System.out.println(pagination + " |");
    }
}

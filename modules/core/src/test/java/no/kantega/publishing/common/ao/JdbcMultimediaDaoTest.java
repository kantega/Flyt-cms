package no.kantega.publishing.common.ao;

import no.kantega.publishing.common.data.ExifMetadata;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.enums.MultimediaType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath*:spring/testContext.xml")
public class JdbcMultimediaDaoTest {

    @Autowired
    private JdbcMultimediaDao dao;

    @Test
    public void testDeleteMultimedia() throws Exception {
        Multimedia multimedia = createMultimedia();
        int id = dao.setMultimedia(multimedia);
        dao.deleteMultimedia(id);
        assertNull("Multimedia object == null", dao.getMultimedia(id));
    }

    @Test
    public void testGetMultimedia() throws Exception {
        Multimedia folder = new Multimedia();
        folder.setType(MultimediaType.FOLDER);
        folder.setName("myfolder");
        int folderId = dao.setMultimedia(folder);

        Multimedia multimediaBeforeSave = createMultimedia();
        multimediaBeforeSave.setParentId(folderId);
        int id = dao.setMultimedia(multimediaBeforeSave);
        Multimedia multimediaAfterSave = dao.getMultimedia(id);
        assertNotNull("dao.getMultimedia != null", multimediaAfterSave);

        assertEquals(multimediaBeforeSave.getName(), multimediaAfterSave.getName());
        assertEquals(multimediaBeforeSave.getAltname(), multimediaAfterSave.getAltname());
        assertEquals(multimediaBeforeSave.getAuthor(), multimediaAfterSave.getAuthor());
        assertEquals(multimediaBeforeSave.getDescription(), multimediaAfterSave.getDescription());
        assertEquals(multimediaBeforeSave.getFilename(), multimediaAfterSave.getFilename());
        assertEquals(multimediaBeforeSave.getParentId(), multimediaAfterSave.getParentId());
    }

    @Test
    public void shouldGetExifData() throws Exception {
        Multimedia folder = new Multimedia();
        folder.setType(MultimediaType.FOLDER);
        folder.setName("myfolder");
        int folderId = dao.setMultimedia(folder);

        Multimedia multimediaBeforeSave = createMultimedia();
        multimediaBeforeSave.setParentId(folderId);
        int id = dao.setMultimedia(multimediaBeforeSave);
        Multimedia multimediaAfterSave = dao.getMultimedia(id);
        assertNotNull("dao.getMultimedia != null", multimediaAfterSave);

        assertEquals(2, multimediaAfterSave.getExifMetadata().size());
    }


    @Test
    public void testGetMultimediaByParentIdAndName() throws Exception {
        Multimedia multimedia = createMultimedia();
        multimedia.setName("newname");
        multimedia.setParentId(0);
        dao.setMultimedia(multimedia);
        Multimedia multimedia2 = dao.getMultimediaByParentIdAndName(0, "newname");
        assertNotNull("dao.getMultimedia != null", multimedia2);
    }

    @Test
    public void testGetProfileImageForUser() throws Exception {
        Multimedia multimedia = createMultimedia();
        multimedia.setProfileImageUserId("me");
        dao.setMultimedia(multimedia);
        Multimedia multimedia2 = dao.getProfileImageForUser("me");
        assertNotNull("dao.getProfileImageForUser != null", multimedia2);
    }

    @Test
    public void testStreamMultimediaData() throws Exception {

    }

    @Test
    public void testGetMultimediaList() throws Exception {
        Multimedia multimedia = createMultimedia();
        dao.setMultimedia(multimedia);
        assertTrue("dao.getMultimediaList(0).size() > 0", dao.getMultimediaList(0).size() > 0);
    }

    @Test
    public void testGetMultimediaCount() throws Exception {
        Multimedia multimedia = createMultimedia();
        dao.setMultimedia(multimedia);
        assertTrue("dao.getMultimediaCount() > 0", dao.getMultimediaCount() > 0);
    }

    @Test
    public void shouldReturn2ItemsForSearch() throws Exception {
        Multimedia multimedia1 = createMultimedia();
        multimedia1.setName("multimedia");
        dao.setMultimedia(multimedia1);

        Multimedia multimedia2 = createMultimedia();
        multimedia2.setName("multimedia");
        dao.setMultimedia(multimedia2);

        List<Multimedia> multimedia = dao.searchMultimedia("multimedia", -1, -1);
        assertEquals("searchMultimedia.size() == 2", 2, multimedia.size());
    }

    @Test
    public void shouldMoveMultimediaFolder() throws Exception {
        Multimedia folder = new Multimedia();
        folder.setType(MultimediaType.FOLDER);
        folder.setName("myfolder");
        int folderId = dao.setMultimedia(folder);

        Multimedia multimedia = createMultimedia();
        multimedia.setParentId(folderId);
        dao.setMultimedia(multimedia);

        Multimedia folder2 = new Multimedia();
        folder2.setType(MultimediaType.FOLDER);
        folder2.setName("myfolder2");
        int folderId2 = dao.setMultimedia(folder2);


        dao.moveMultimedia(folderId2, folderId);

        folder2 = dao.getMultimedia(folderId2);

        assertEquals("folder2.getParentId() == folderId", folder2.getParentId(), folderId);

        folder = dao.getMultimedia(folderId);
        assertEquals("folder.getNoSubFolders() == 1", folder.getNoSubFolders(), 1);
    }

    @Test
    public void shouldSaveAndUpdateMultimediaName() throws Exception {
        Multimedia multimedia = createMultimedia();
        int multimediaId = dao.setMultimedia(multimedia);

        Multimedia multimedia2 = dao.getMultimedia(multimediaId);
        assertTrue("multimedia2.getSize() > 0", multimedia2.getSize() > 0);

        assertEquals("multimedia.getName == multimedia2.getName", multimedia.getName(), multimedia2.getName());

        multimedia.setName("My new name");
        dao.setMultimedia(multimedia);

        Multimedia multimedia3 = dao.getMultimedia(multimediaId);
        assertEquals("multimedia3.getName == My new name", multimedia3.getName(), "My new name");
        assertTrue("multimedia3.getSize() > 0", multimedia3.getSize() > 0);
    }

    @Test
    public void shouldGetOwnerPerson() throws Exception {
        Multimedia multimediaBeforeSave = new Multimedia();
        multimediaBeforeSave.setOwnerPerson("terros");
        int id = dao.setMultimedia(multimediaBeforeSave);
        Multimedia multimediaAfterSave = dao.getMultimedia(id);
        assertEquals("multimediaBeforeSave.getOwnerPerson == multimediaAfterSave.getOwnerPerson", multimediaBeforeSave.getOwnerPerson(), multimediaAfterSave.getOwnerPerson());
    }

    private Multimedia createMultimedia() {
        Multimedia multimedia = new Multimedia();
        multimedia.setName("My file");
        multimedia.setAltname("Alttext");
        multimedia.setAuthor("Author");
        multimedia.setFilename("text.txt");
        multimedia.setUsage("Usage");
        multimedia.setCameraMake("Apple");
        multimedia.setCameraMake("iPhone");
        multimedia.setGpsLatitude("0");
        multimedia.setGpsLongitude("0");
        multimedia.setGpsLatitudeRef("W");
        multimedia.setGpsLongitudeRef("E");
        String data = "The quick brown fox jumps over the lazy dog";
        multimedia.setData(data.getBytes());

        ExifMetadata camera = new ExifMetadata();
        camera.setDirectory("EXIF");
        camera.setKey("Camera");
        camera.setValue("iPhone");
        multimedia.getExifMetadata().add(camera);

        ExifMetadata keywords = new ExifMetadata();
        keywords.setDirectory("EXIF2");
        keywords.setKey("Keywords");
        keywords.setValues(new String[]{"sand", "fog", "sea"});
        multimedia.getExifMetadata().add(keywords);

        return multimedia;
    }
}

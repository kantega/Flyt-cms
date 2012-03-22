package no.kantega.publishing.topicmaps.impl;

import no.kantega.publishing.topicmaps.data.Topic;
import no.kantega.publishing.topicmaps.data.TopicAssociation;
import no.kantega.publishing.topicmaps.data.TopicOccurence;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class XTMImportWorkerTest {
    private Document document;
    XTMImportWorker xtmImportWorker;
    @Before
    public void setUp() throws Exception {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("no/kantega/topicmaps/sample-1.0.xtm");
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = docFactory.newDocumentBuilder();
        document = builder.parse(is);
        xtmImportWorker = new XTMImportWorker(1);
    }

    @Test
    public void shouldGetCorrectNumberOfTopics() throws Exception {
        List<Topic> topicsFromDocument = xtmImportWorker.getTopicsFromDocument(document);
        assertEquals(3, topicsFromDocument.size());
    }

    @Test
    public void shouldGetCorrectNumberOfAssociation() throws Exception {
        List<TopicAssociation> associationsFromDocument = xtmImportWorker.getTopicAssociationsFromDocument(document);
        assertEquals(6, associationsFromDocument.size());
    }

    @Test
    public void verifyThatTopicHasCorrectInstanceOf() throws Exception {
        List<Topic> topicsFromDocument = xtmImportWorker.getTopicsFromDocument(document);
        Topic instanceOf = topicsFromDocument.get(0).getInstanceOf();
        assertTrue("http://psi.udir.no/kl06/hovedomraade".equals(instanceOf.getId()));
    }

    @Test
    public void verifyThatTopicHasCorrectBaseName() throws Exception {
        List<Topic> topicsFromDocument = xtmImportWorker.getTopicsFromDocument(document);
        Topic topic = topicsFromDocument.get(0);
        assertTrue("Tal og algebra".equals(topic.getBaseNames().get(0).getBaseName()));
    }

    @Test
    public void verifyThatTopicHasCorrectSubjectIdentity() throws Exception {
        List<Topic> topicsFromDocument = xtmImportWorker.getTopicsFromDocument(document);
        Topic topic = topicsFromDocument.get(0);
        assertTrue("uuid:cb3cbd2b-95d0-406c-8a19-ca25b41af2da".equals(topic.getSubjectIdentity()));
    }

    @Test
    public void verifyThatTopicHasCorrectNumberOfOccurences() throws Exception {
        List<Topic> topicsFromDocument = xtmImportWorker.getTopicsFromDocument(document);
        Topic topic = topicsFromDocument.get(0);
        assertEquals(1, topic.getOccurences().size());
    }

    @Test
    public void verifyThatTopicOccurenceIsCorrect() throws Exception {
        List<Topic> topicsFromDocument = xtmImportWorker.getTopicsFromDocument(document);
        TopicOccurence topicOccurence = topicsFromDocument.get(0).getOccurences().get(0);
        assertTrue(topicOccurence.getInstanceOf().getId().equals("http://psi.udir.no/kl06/beskrivelse"));
        assertTrue(topicOccurence.getResourceData().equals("Hovudomradet: tal og algebra"));
    }

    @Test
    public void verifyThatAssociactionHasCorrectInstanceOf() throws Exception {
        List<TopicAssociation> associationsFromDocument = xtmImportWorker.getTopicAssociationsFromDocument(document);
        TopicAssociation topicAssociation1 = associationsFromDocument.get(0);
        TopicAssociation topicAssociation2 = associationsFromDocument.get(1);
        assertTrue(topicAssociation1.getInstanceOf().getId().equals("http://psi.udir.no/kl06/erstatter"));
        assertTrue(topicAssociation2.getInstanceOf().getId().equals("http://psi.udir.no/kl06/erstatter"));
    }

    @Test
    public void verifyThatAssociactionHasCorrectRoleSpec() throws Exception {
        List<TopicAssociation> associationsFromDocument = xtmImportWorker.getTopicAssociationsFromDocument(document);
        TopicAssociation topicAssociation1 = associationsFromDocument.get(0);
        TopicAssociation topicAssociation2 = associationsFromDocument.get(1);
        assertTrue(topicAssociation1.getRolespec().getId().equals("http://psi.udir.no/kl06/erstatning"));
        assertTrue(topicAssociation2.getRolespec().getId().equals("http://psi.udir.no/kl06/erstattet"));
    }

    @Test
    public void verifyThatAssociactionHasCorrectTopicRef() throws Exception {
        List<TopicAssociation> associationsFromDocument = xtmImportWorker.getTopicAssociationsFromDocument(document);
        TopicAssociation topicAssociation1 = associationsFromDocument.get(0);
        TopicAssociation topicAssociation2 = associationsFromDocument.get(1);
        assertTrue(topicAssociation1.getTopicRef().getId().equals("http://psi.udir.no/kl06/MAT1-03"));
        assertTrue(topicAssociation2.getTopicRef().getId().equals("http://psi.udir.no/kl06/MAT1-02"));
    }

    @Test
    public void verifyThatAssociactionHasCorrectAssociatedTopicRef() throws Exception {
        List<TopicAssociation> associationsFromDocument = xtmImportWorker.getTopicAssociationsFromDocument(document);
        TopicAssociation topicAssociation1 = associationsFromDocument.get(0);
        TopicAssociation topicAssociation2 = associationsFromDocument.get(1);
        assertTrue(topicAssociation1.getAssociatedTopicRef().getId().equals("http://psi.udir.no/kl06/MAT1-02"));
        assertTrue(topicAssociation2.getAssociatedTopicRef().getId().equals("http://psi.udir.no/kl06/MAT1-03"));
    }

}

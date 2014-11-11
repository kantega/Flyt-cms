package no.kantega.openaksess.search.model;

import no.kantega.publishing.common.data.Multimedia;

import javax.servlet.http.HttpServletRequest;

public class AutocompleteMultimedia {
    private int id;
    private String label;
    private String value;
    private String image;

    public AutocompleteMultimedia(int id, String label, String value, String image) {
        this.id = id;
        this.label = label;
        this.value = value;
        this.image = image;
    }

    public AutocompleteMultimedia(Multimedia multimedia, HttpServletRequest request) {
        this.id = multimedia.getId();
        this.label = multimedia.getName();
        this.value = multimedia.getName();
        this.image = "<img src='" + request.getContextPath() + "/multimedia.ap?id=" +
                multimedia.getId() + "&width=50&height=50' alt='" +
                multimedia.getName() + "'>";
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    public String getImage() {
        return image;
    }
}

/*
 */

package no.kantega.publishing.modules.forms.validate;

public class FormError {
    public String id;
    public String message;

    public FormError(String id, String message) {
        this.id = id;
        this.message = message;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    

}

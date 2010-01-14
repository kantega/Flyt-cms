/*
 */

package no.kantega.publishing.modules.forms.validate;

public class FormError {
    public String field;
    public String message;
    public int index;

    public FormError(String field, int index, String message) {
        this.field = field;
        this.message = message;
        this.index = index;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    

}

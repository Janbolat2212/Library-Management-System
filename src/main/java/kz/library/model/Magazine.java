package kz.library.model;

import java.io.Serial;

public final class Magazine extends LibraryItem {
    @Serial
    private static final long serialVersionUID = 1L;

    private final int issueNumber;

    public Magazine(int id, String title, int year, int issueNumber) {
        super(id, title, year);
        if (issueNumber <= 0) {
            throw new IllegalArgumentException("Журнал нөмірі нөлден үлкен болуы керек.");
        }
        this.issueNumber = issueNumber;
    }

    public int getIssueNumber() {
        return issueNumber;
    }

    @Override
    public String getInfo() {
        return "Журнал {id=%d, атауы='%s', жыл=%d, шығарылым=%d, күйі=%s}"
                .formatted(getId(), getTitle(), getYear(), issueNumber, availabilityText());
    }
}

import java.util.Date;

public class DatedTask extends Task {

    private Date dueDate;

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    @Override
    public String toString() {
        return "DatedTask{" +
                "dueDate=" + dueDate +
                ", id=" + id +
                ", description='" + description + '\'' +
                ", title='" + title + '\'' +
                ", done=" + done +
                ", creator=" + creator +
                '}';
    }
}

package library.model;

import java.util.Calendar;
import java.util.Date;

public class AudioVideo extends Item {
    public AudioVideo(String title) {
        super(title);
    }

    @Override
    public double calculateOverdueFine(int daysOverdue) {
        // Example calculation logic
        double fine = 0.10 * daysOverdue;
        return Math.min(fine, getMaxFine());
    }

    private double getMaxFine() {
        // Return the max fine, possibly based on item value
        return 30.00;
    }

    public void checkout() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 14); // 2 weeks for audio/video materials
        setDueDate(calendar.getTime());
    }
}

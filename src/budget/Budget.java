package budget;

import entities.User;

import java.time.LocalDate;
import java.util.HashMap;

public class Budget
{
    private LocalDate start;
    private LocalDate finish;
    private HashMap<String, Integer> log;
    private int workingHours;

    public Budget(LocalDate start, LocalDate finish, int workingHours)
    {
        this.start = start;
        this.finish = finish;
        this.workingHours = workingHours;
        this.log = new HashMap<>();
    }

    public int daysRemaining()
    {
        return Math.abs(LocalDate.now().compareTo(finish));
    }

    public int workHoursRemaining()
    {
        return daysRemaining() * workingHours;
    }

    public int hoursWorked()
    {
        int total = 0;
        for (String record : log.keySet())
        {
            total = total + log.get(record);
        }
        return total;
    }

    public double totalHours()
    {
        return Math.abs(start.compareTo(finish)) * workingHours;
    }

    public int totalDays()
    {
        return Math.abs(start.compareTo(finish));
    }

    public void setStartDate(LocalDate newStart)
    {
        start = newStart;
    }

    public void setFinishDate(LocalDate newFinish)
    {
        start = newFinish;
    }

    public void logHoursWorked(User currentUser, int hours)
    {
        if (!log.containsKey(currentUser.getUserName()))
        {
            log.put(currentUser.getUserName(), hours);
        }
        else if (log.containsKey(currentUser.getUserName()))
        {
            int totalHours = log.get(currentUser.getUserName()) + hours;
            log.replace(currentUser.getUserName(), totalHours);
        }
        else
        {
            throw new NullPointerException("Does not exist in log");
        }
    }

    public String currentCost(double costPerHour)
    {
        return "Current Cost $" + costPerHour * hoursWorked();
    }

    public String totalCost(double costPerHour)
    {
        return "Total Cost $" + costPerHour * totalHours();
    }

    public String toString()
    {
        return "\nStart Date: " + start.toString() +
                "\nFinish Date: " + finish.toString() +
                "\nTotal Days: " + totalDays() +
                "\nTotal Hours: " + totalDays() +
                "\nWorked Hours: " + hoursWorked() +
                "\nDays Remaining: " + daysRemaining() +
                "\nHours Remaining: " + workHoursRemaining();
    }
}

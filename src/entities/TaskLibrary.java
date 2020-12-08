package entities;

import tools.Input;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static entities.Task.sortByDate;

public class TaskLibrary extends DataLibrary {
    private static final TaskLibrary instance = null;
    
    public static TaskLibrary getInstance() {
        if (instance == null) {
            return new TaskLibrary();
        } else {
            return instance;
        }
    }
    
    public boolean confirmAccess(Team projectTeam, User currentUser) {
        if (projectTeam.findTeamMember(currentUser).getRole().adminAccess()) {
            return true;
        } else {
            System.out.println("You are not authorized to perform this action!");
            return false;
        }
        // Below can be used in other methods for testing access.
        /*
        if (!confirmAccess(currentProject, currentUser)) {
            return;
        }
        */
    }
    public void addTaskToList(Project currentProject, User currentUser, String name, String description, LocalDate deadline) {
        currentProject.taskList.addToList(new Task(currentUser, name, description, deadline));
    }
    
    public void createTask(Project currentProject, User currentUser) {
        System.out.println("Enter 0 at any step to return to the previous menu: ");
        Input input = Input.getInstance();
        String name = input.getStr("Task Name: ");
        if (input.abort(name)) {
            System.out.println("Returning to project menu...");
            return;
        }
        
        String description = input.getStr("Task Description: ");
        if (input.abort(description)) {
            System.out.println("Returning to project menu...");
            return;
        }
        
        LocalDate deadline = input.getDate("Task Deadline (YYYY-MM-DD): ");
        if (input.abort(description)) {
            System.out.println("Returning to project menu...");
            return;
        }
        currentProject.taskList.addToList(new Task(currentUser, name, description, deadline));
    }
    
    public void deleteTask(Project currentProject, User currentUser) {
        Task currentTask = navigateBetweenTasks(currentProject);
        if (currentTask == null) {
            return;
        }
        Task taskToDelete = (Task)currentProject.taskList.findItInList(currentTask.getID());
        if(taskToDelete==null) {
            System.out.println("Task does not exist!");
            return;
        }
        Input input = Input.getInstance();
        String choice = "";
        System.out.println("You are about to delete this task!");
        do {
            choice = input.getStr("Are you sure you want to delete this task? Y/N: ");
        } while(!choice.toUpperCase().equals("Y") && !choice.toUpperCase().equals("N"));
        if(choice.toUpperCase().equals("Y")) {
            if (currentProject.taskList.removeItFromList(currentTask.getID())) {
                System.out.println("Task successfully deleted");
                System.out.println("Returning to project menu...");
                return;
            }
        } else {
            System.out.println("Task not deleted");
            System.out.println("Returning to project menu...");
            return;
        }
    }
    
    public ArrayList<Task> listProjectsTasks(Project currentProject) {
        ArrayList<Task> tempList = new ArrayList<>();
        for (Data task : currentProject.taskList.list) {
            Task currentTask = ((Task) task);
            tempList.add(currentTask);
        }
        if (tempList.size() == 0) {
            System.out.println("This task does not exist!");
        } else {
            for (int i = 0; i < tempList.size(); i++) {
                System.out.println(i + 1 + ". " + tempList.get(i).getName());
            }
        }
        return tempList;
    }
    
    public Task navigateBetweenTasks(Project currentProject) {
        Input input = Input.getInstance();
        ArrayList<Task> taskList = listProjectsTasks(currentProject);
        if (taskList.size() == 0) {
            return null;
        } else {
            int choice;
            do {
                choice = input.getInt("Enter task number or 0 to return to the previous menu: ");
            } while (choice < 0 || choice > taskList.size());
            
            if (choice == 0) {
                return null;
            } else
                return taskList.get(choice - 1);
        }
    }
    
    public void viewTaskDetails(Task currentTask){
        System.out.println("Task Name: " + currentTask.getName());
        if (!currentTask.getStatus().isEmpty()) {
            System.out.println("Status: " + currentTask.getStatus());
        }
        System.out.println("Description: " + currentTask.getDescription());
        System.out.println("Assignees: " + currentTask.getAssignees().toString());
    }
    
    public void updateStatus(Project currentProject, Task currentTask, User currentUser){
        if(!confirmAccess(currentProject.getTeam(), currentUser)) {
            return;
        }
        Input input = Input.getInstance();
        String newStatus = input.getStr("Enter the status: ");
        if(newStatus.equalsIgnoreCase("completed")) {
            for (int i = 0; i < currentTask.getAssignees().size(); i++) {
                String message = "The task " + currentTask.getName() +
                        " in the project: " +
                        currentProject.getName() +
                        " has been completed.";
                sendNotification(currentTask.getAssignees().get(i),message);
            }
        }
        currentTask.setStatus(newStatus);
    }
    
    public void addAssignee(Project currentProject, Task currentTask, User currentUser) {
        Team projectTeam = currentProject.getTeam();
        ArrayList<User> taskTeam = currentTask.getAssignees();
        if (!confirmAccess(projectTeam, currentUser)) {
            return;
        }
        Input input = Input.getInstance();
        List<User> tempList = projectTeam.getAllTeamUsers();
        for (int i = 0; i < tempList.size(); i++) {
            if(!taskTeam.contains(tempList.get(i))) {
                System.out.println(i+1 + ". " + tempList.get(i).getUserName());
            }
        }
        int choice;
        do{
            choice = input.getInt("Enter user number or 0 to return to the previous menu: ");
        } while(choice < 0 || choice > tempList.size());
        
        if (choice == 0){
            return;
        }
        User userToAdd = tempList.get(choice-1);
        taskTeam.add(userToAdd);
        System.out.println("Successfully assigned " + userToAdd.getUserName() + " to the task");
        String message =
                "You have been assigned a new task: " +
                        currentTask.getName() +
                        " in the project: " +
                        currentProject.getName();
        sendNotification(userToAdd, message);
    }
    
    public void removeAssignee(Team projectTeam, ArrayList<User> taskTeam, User currentUser) {
        if (!confirmAccess(projectTeam, currentUser)) {
            return;
        }
        Input input = Input.getInstance();
        if(taskTeam.size() == 0 || taskTeam == null) {
            return;
        }
        for (int i = 0; i < taskTeam.size(); i++) {
            System.out.println(i+1 + ". " + taskTeam.get(i).getUserName());
        }
        int choice;
        do{
            choice = input.getInt("Enter user number or 0 to return to the previous menu: ");
        } while(choice < 0 || choice > taskTeam.size());
        
        if (choice == 0){
            return;
        }
        User userToRemove = taskTeam.get(choice-1);
        taskTeam.remove(userToRemove);
        System.out.println("Successfully deallocated " + userToRemove.getUserName() + " from the task");
    }
    
    public void countdown(Project currentProject) {
        ArrayList<Data> countdown = currentProject.taskList.list;
        Collections.sort(countdown, sortByDate);
        String displayedDays = "";
        for (Data task : countdown) {
            Task projectTask = (Task) task;
            if (!projectTask.getStatus().equalsIgnoreCase("completed")){
                long daysToDeadline = ChronoUnit.DAYS.between(LocalDate.now(), projectTask.getDeadline());
                if (daysToDeadline < 4) {
                    displayedDays = Input.RED + daysToDeadline + Input.RESET;
                } else {
                    displayedDays = Input.BLUE + daysToDeadline + Input.RESET;
                }
                ArrayList<User> assignees = projectTask.getAssignees();
                System.out.println("Days to Deadline: " + displayedDays + "\n" + "Task: " + projectTask.getName() + "\n" + "Description: " + projectTask.getDescription() + "" + "\n" + "Team Members: " + assignees.toString() + "\n");
            }
        }
    }
    
    public void completedTasks(Project currentProject) {
        ArrayList<Data> tasks = currentProject.taskList.list;
        Collections.sort(tasks, sortByDate);
        for (Data task : tasks) {
            Task projectTask = (Task) task;
            if (projectTask.getStatus().equalsIgnoreCase("completed")) {
                ArrayList<User> assignees = projectTask.getAssignees();
                System.out.println("Task Deadline" + projectTask.getDeadline() + "\n" + " Task: " + projectTask.getName() + "\n" + "Description" + projectTask.getDescription() + "" + "\n" + "Team Members: " + assignees.toString() + "\n");
            }
        }
    }
    
    public void sendNotification(User userToNotify, String message) {
        userToNotify.getInbox().add(new Message("System", userToNotify.getUserName(), message));
    }

    public boolean isUserAssignee(Project currentProject, Task currentTask, User currentUser){
        if(currentProject.getProjectManager().checkID(currentUser.getID())){
            return true;
        }
        ArrayList<User> assignees = currentTask.getAssignees();
        for(User user: assignees){
            if(user.checkID(currentUser.getID())){
                return true;
            }
        } return false;
    }

    public void addWorkedHours(Project currentProject, Task currentTask, User currentUser){
        if(isUserAssignee(currentProject, currentTask,currentUser)){
            Input input = Input.getInstance();
            double workedHours = input.getDouble("Please enter the amount of worked hours: ");
            WorkedHours newLog = new WorkedHours(currentUser,workedHours);
            currentTask.addWorkedHours(newLog);
            System.out.println(getAllWorkedHours(currentTask) + " hours spent on this task.");
        }else {
            System.out.println("You are not authorized to perform this action!");
        }
    }

    protected double getAllWorkedHours(Task currentTask){
        ArrayList<WorkedHours> workedHours = currentTask.getWorkedHours();
        double totalHours=0;
        for(WorkedHours log : workedHours){
            totalHours += log.getWorkedHours();
        } return totalHours;
    }
}


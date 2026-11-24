import java.util.*;
import java.io.*;

public class VaccinationSlotBooking {

    // Patient class
    static class Patient {
        String id, name;
        int age;

        Patient(String id, String name, int age) {
            this.id = id;
            this.name = name;
            this.age = age;
        }

        public String toString() {
            return id + " | " + name + " | " + age;
        }
    }

    // Circular Queue
    static class CircularQueue {
        Patient[] arr = new Patient[3];
        int front = 0, rear = -1, size = 0;

        boolean isFull() {
            return size == 3;
        }

        boolean isEmpty() {
            return size == 0;
        }

        boolean enqueue(Patient p) {
            if (isFull()) return false;

            rear = (rear + 1) % 3;
            arr[rear] = p;
            size++;
            return true;
        }

        Patient dequeue() {
            if (isEmpty()) return null;

            Patient p = arr[front];
            front = (front + 1) % 3;
            size--;

            return p;
        }

        Patient get(int i) {
            return arr[(front + i) % 3];
        }

        void display() {
            if (isEmpty()) {
                System.out.println("Queue is empty.");
                return;
            }

            for (int i = 0; i < size; i++) {
                System.out.println((i + 1) + ". " + get(i));
            }
        }
    }

    static CircularQueue q = new CircularQueue();

    static HashMap<String, Patient> map = new HashMap<>();

    static int id = 101;

    static Scanner sc = new Scanner(System.in);

    // Book Slot
    static void bookSlot() {

        if (q.isFull()) {
            System.out.println("Slots full. Please try the next session.");
            return;
        }

        System.out.print("Enter Name: ");
        String name = sc.nextLine().trim();

        if (name.isEmpty() || name.contains("|")) {
            System.out.println("Invalid name.");
            return;
        }

        System.out.print("Enter Age: ");

        int age;

        try {
            age = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid age. Booking not done.");
            return;
        }

        if (age < 18) {
            System.out.println("Invalid age. Booking not done.");
            return;
        }

        String patientId = "V" + id++;

        Patient p = new Patient(patientId, name, age);

        q.enqueue(p);
        map.put(patientId, p);

        System.out.println("Slot booked. ID: " + patientId
                + " | " + name
                + " | Position: " + q.size);

        saveData();
    }

    // Vaccinate Next
    static void vaccinateNext() {

        Patient p = q.dequeue();

        if (p == null) {
            System.out.println("No patients available.");
            return;
        }

        map.remove(p.id);

        System.out.println(p.name + " vaccinated.");
        System.out.println("Slot freed.");

        saveData();
    }

    // View Queue
    static void viewQueue() {
        System.out.println("\n--- Patient Queue ---");
        q.display();
    }

    // Search Patient
    static void searchPatient() {

        System.out.print("Enter Patient ID: ");
        String patientId = sc.nextLine().trim();

        Patient p = map.get(patientId);

        if (p == null) {
            System.out.println("Patient not found.");
        } else {
            System.out.println("Patient found: " + p);
        }
    }

    // Save Data in File
    static void saveData() {

        try {
            PrintWriter pw = new PrintWriter("bookings.txt");

            for (int i = 0; i < q.size; i++) {
                Patient p = q.get(i);

                pw.println(p.id + "|" + p.name + "|" + p.age);
            }

            pw.close();

        } catch (Exception e) {
            System.out.println("File saving error.");
        }
    }

    // Load Data from File
    static void loadData() {

        try {
            File file = new File("bookings.txt");

            if (!file.exists()) return;

            Scanner fs = new Scanner(file);

            while (fs.hasNextLine()) {

                String[] data = fs.nextLine().split("\\|", 3);

                if (data.length != 3) continue;

                try {
                    String patientId = data[0];
                    String name = data[1];
                    int age = Integer.parseInt(data[2]);

                    if (age < 18 || name.isEmpty()
                            || q.isFull() || map.containsKey(patientId)) {
                        continue;
                    }

                    Patient p = new Patient(patientId, name, age);

                    q.enqueue(p);
                    map.put(patientId, p);

                    if (patientId.matches("V\\d+")) {
                        id = Math.max(id,
                                Integer.parseInt(patientId.substring(1)) + 1);
                    }

                } catch (NumberFormatException e) {
                    continue;
                }
            }

            fs.close();

        } catch (Exception e) {
            System.out.println("File loading error.");
        }
    }

    // Main Method
    public static void main(String[] args) {

        loadData();

        while (true) {

            System.out.println("\n===== VACCINATION SLOT BOOKING =====");
            System.out.println("1. Book Slot");
            System.out.println("2. Vaccinate Next");
            System.out.println("3. View Queue");
            System.out.println("4. Search Patient");
            System.out.println("5. Exit");

            System.out.print("Enter Choice: ");

            int choice;

            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid choice.");
                continue;
            }

            switch (choice) {

                case 1:
                    bookSlot();
                    break;

                case 2:
                    vaccinateNext();
                    break;

                case 3:
                    viewQueue();
                    break;

                case 4:
                    searchPatient();
                    break;

                case 5:
                    saveData();
                    System.out.println("Data saved. Goodbye!");
                    return;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}

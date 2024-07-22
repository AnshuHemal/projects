import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MovieTBS {
    static Scanner sc = new Scanner(System.in);
    String[][] regular = new String[7][9];
    String[][] recliner = new String[2][8];
    String[] movies = new String[10];
    static int bookedSeats[] = new int[79];
    int bookCount = 0;

    // static HashMap<Integer, Movie> hm = new HashMap();
    static HashMap<Integer, String> movieBookedSeats = new HashMap<>();
    Movie m;

    public void setSeats(int movieId) {
        for (int i = 1; i <= 7; i++) {
            for (int j = 1; j <= 9; j++) {
                int num = i * 10 + j;
                if (isSeatBooked(movieId, num)) {
                    System.out.print("|BK|\t\t");
                } else {
                    System.out.printf("|%2d|\t\t", num);
                }
            }
            System.out.println();
        }

        for (int i = 0; i < 2; i++) {
            for (int j = 1; j <= 8; j++) {
                int num;
                if (i == 0) {
                    num = i + 80 + j;
                } else {
                    num = i + 90 + (j - 1);
                }
                if (isSeatBooked(movieId, num)) {
                    System.out.print("\t|BK|\t");
                } else {
                    System.out.printf("\t|%2d|\t", num);
                }
            }
            System.out.println();
        }
    }

    boolean isSeatBooked(int movieId, int num) {
        if (movieBookedSeats.containsKey(movieId)) {
            String bookedSeats = movieBookedSeats.get(movieId);
            String[] bookedSeatsArray = bookedSeats.split(",");
            for (String seatStr : bookedSeatsArray) {
                int seat = Integer.parseInt(seatStr);
                if (seat == num) {
                    return true;
                }
            }
        }
        return false;
    }

    public void bookSeat(int movieId, int loopNum) {
        String bookedSeats = movieBookedSeats.getOrDefault(movieId, "");
        setSeats(movieId);
        do {
            // setSeats(movieId);
            System.out.println();
            System.out.print("Enter the seat number you want to book: ");
            int seatNumber = sc.nextInt();
            sc.nextLine();

            if (checkBookingSeatNumber(seatNumber) == false) {
                System.out.println("Invalid Seat.. Please Try again..");
                continue;
            }

            if (isSeatBooked(movieId, seatNumber)) {
                System.out.println("Sorry, The seat is already booked..");
            } else {
                if (!bookedSeats.isEmpty()) {
                    bookedSeats += ",";
                }
                bookedSeats += seatNumber;
                loopNum--;
            }
        } while (loopNum > 0);
        movieBookedSeats.put(movieId, bookedSeats);
        setSeats(movieId);
        saveBookedSeats();
        System.out.print("Booked Seats: " + bookedSeats);
    }

    public boolean checkBookingSeatNumber(int seatNumber) {
        if (seatNumber < 11 || seatNumber == 20 || seatNumber == 30 || seatNumber == 40 || seatNumber == 50
                || seatNumber == 60 || seatNumber == 70 || seatNumber == 80 || (seatNumber > 88 && seatNumber <= 90)
                || seatNumber > 98) {
            return false;
        } else {
            return true;
        }
    }

    public void movieList() throws Exception {
        BufferedReader br = new BufferedReader(new FileReader("MovieDetail.txt"));
        String read;
        System.out.println();
        System.out.println("-^-^-^-^-^-^-^-^-^-^-^-^-^-^-^ MOVIE LIST ^-^-^-^-^-^-^-^-^-^-^-^-^-^-^-");
        System.out.println();
        System.out.println("------------------------------------------------------------------------");
        System.out.println("Number\t\t    Name\t\tPrice\t\t     Duration");
        System.out.print("------------------------------------------------------------------------");
        int i = 0;
        while ((read = br.readLine()) != null) {
            String[] mDetails = read.split(" ");
            movies[i] = mDetails[1];
            System.out.printf("\n%5d %20s %20f %20s ",
                    Integer.parseInt(mDetails[0]), mDetails[1], Double.parseDouble(mDetails[2]), mDetails[3]);
            i++;
        }
        // System.out.println();
        // System.out.println(Arrays.toString(movies));
        br.close();
    }

    public void bookMovie() throws Exception {
        movieList();
        System.out.println();
        boolean checkMovieID = false;
        int movieId;
        do {
            System.out.print("Select Movie: ");
            movieId = sc.nextInt();
            if (movieId >= 1 && movieId <= 10) {
                checkMovieID = true;
                System.out.println("You selected \"" + movies[movieId - 1] + "\"..");
                break;
            }
        } while (checkMovieID != true);

        if (checkMovieID) {
            System.out.print("Enter No. of seats to book: ");
            int numSeats = sc.nextInt();
            sc.nextLine();
            loadBookedSeats();
            bookSeat(movieId, numSeats);
        }
    }

    private void saveBookedSeats() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("BookedSeats.txt"))) {
            for (Map.Entry<Integer, String> entry : movieBookedSeats.entrySet()) {
                bw.write(entry.getKey() + ":" + entry.getValue());
                bw.newLine();
            }
            // System.out.println("Booked seats saved to file.");
        } catch (IOException e) {
            // System.out.println("Error saving booked seats: " + e.getMessage());
        }
    }

    private void loadBookedSeats() {
        movieBookedSeats.clear();
        try (BufferedReader br = new BufferedReader(new FileReader("BookedSeats.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                int movieId = Integer.parseInt(parts[0]);
                String bookedSeats = parts[1];
                movieBookedSeats.put(movieId, bookedSeats);
            }
            // System.out.println("Booked seats loaded from file.");
        } catch (IOException e) {
            // System.out.println("No booked seats found or error loading booked seats: " + e.getMessage());
        }
    }
}

class Run {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        MovieTBS movie = new MovieTBS();

        do {
            System.out.print("\n1. See Movies\n2. Book Ticket\n0. Exit\nEnter your choice: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 0:
                    System.exit(0);
                    break;

                case 1:
                    movie.movieList();
                    System.out.println();
                    break;

                case 2:
                    System.out.println();
                    movie.bookMovie();
                    break;

                default:
                    System.out.println("Invalid Input!! Please Try again..");
                    break;
            }
        } while (true);
    }
}
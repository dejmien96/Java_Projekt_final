import java.io.*;
import java.util.Scanner;

public class Uzytkownik {
    private String login, haslo, nickname;
    private Scanner in;
    private Statystyki staty;

    public Uzytkownik(Scanner in) {
        this.in = in;
        init();
        staty = new Statystyki(nickname);
    }

    // uzywane w konstruktorze
    private void init() {
        System.out.println("Wybierz akcje:");
        System.out.println("\t0 - zaloguj");
        System.out.println("\t1 - zarejestruj");
        int input = in.nextInt();
        switch (input) {
            case 0:
                zaloguj();
                break;
            case 1:
                zarejestruj();
                break;
            default:
                System.err.println("Wrong Input");
                init();
                break;
        }
    }

    private boolean zaloguj() {
        System.out.print("Podaj login: ");
        login = in.next();
        System.out.print("Podaj haslo: ");
        haslo = in.next();

        // zaloguj inna funkcja jezeli zwroci prawde zaloguj jezeli nie zaloguj ponownie
        if(zaloguj(login, haslo)) {
            System.out.println("Logowanie zakonczone sukcesem");
            return true;
        } else {
            System.out.println("Bledny login lub haslo, sprobuj jeszcze raz");
            zaloguj();
        }
        return false;
    }

    private boolean zaloguj(String login, String haslo) {
        boolean logowanie = false;
        FileReader fr = null;

        // czytaj z pliku danelogowania
        try {
            fr = new FileReader("danelogowania.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        BufferedReader br = new BufferedReader(fr);
        String line = "not null";

        while(true)
        {
            // czytaj linie poki nie napotkasz konca pliku
            try {
                if (((line = br.readLine()) == null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }

            // jezeli wprowadzony login i haslo sie zgadzaja logowanie = true
            if(line.equals(login)) {
                try {
                    line = br.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(line.equals(haslo)) {
                    logowanie = true;
                    break;
                } else {
                    logowanie = false;
                    break;
                }
            } else {
                try {
                    br.readLine();
                    br.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // ustaw nickname
        try {
            line = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        nickname = line;

        // zamknij plik
        try {
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // logowanie zakonczone sukcesem
        return logowanie;
    }

    private boolean zarejestruj() {
        System.out.print("Podaj login: ");
        String login = in.next();
        System.out.print("Podaj haslo: ");
        String haslo = in.next();
        System.out.print("Podaj nickname: ");
        nickname = in.next();

        // jezeli zakladanie konta sie udalo stworz folder o nickname-ie tego uzytkownika
        if(wpiszRejestr(login, haslo)) {
            System.out.println("Konto zostalo pomyslnie zalozone!");
            File dir = new File(nickname);
            dir.mkdir();
        } else {
            System.out.println("Login jest juz zajety musisz wybrac inny");
            zarejestruj();
            return false;
        }

        FileWriter fw = null;

        // stworz plik uzytkownika
        try {
            fw = new FileWriter(nickname + "/" + nickname + ".txt", false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        PrintWriter pw = new PrintWriter(fw);

        // zainicjalizuj zerami statystyki uzytkownika ( trafienia i pudla )
        pw.println(0);
        pw.println(0);

        // zamknij wszystko
        pw.close();

        try {
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return zaloguj(login, haslo);
    }

    private boolean wpiszRejestr(String login, String haslo)  {
        FileReader fr = null;

        // otworz plik z danymi
        try {
            fr = new FileReader("danelogowania.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        BufferedReader br = new BufferedReader(fr);
        String line = "not null";

        while(true)
        {
            try {
                // czytaj do skonczenia sie wierszy w pliku
                if (((line = br.readLine()) == null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }

            // jezeli znajdzie sie o takim nicku jak ktos chce zarejestrowac konto zwroc false
            if(line.equals(login)) {
                return false;
            } else {
                try {
                    br.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileWriter fw = null;

        // otworz plik
        try {
            fw = new FileWriter("danelogowania.txt", true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // dopisz nowy login, haslo, nickname
        PrintWriter pw = new PrintWriter(fw);
        pw.println(login);
        pw.println(haslo);
        pw.println(nickname);
        pw.close();

        try {
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    public boolean zmienLogin() throws IOException {
        System.out.print("Podaj nowy login: ");
        String nowyLogin = in.next();

        boolean doliczaj = true;
        int linia = 0;

        // otworz danelogowania
        File file = new File("danelogowania.txt");
        String stareDane = "";
        BufferedReader reader = new BufferedReader(new FileReader(file));

        String line = reader.readLine();

        while(line != null) {
            // zapisuj linie w starychDanych oddzielajac lineSeparatorem
            stareDane = stareDane + line + System.lineSeparator();

            // jezeli trafisz na login do zmiany przestan doliczac do linii
            if(line.equals(login)) {
                doliczaj = false;
            }

            // jezeli istnieje juz taki login popros uzytkownika o wybranie innego
            if(line.equals(nowyLogin)) {
                System.err.println("Juz taki login istnieje wybierz inny");
                zmienLogin();
                return false;
            }

            // doliczaj dopoki nie trafisz na login uzytkownika
            if(doliczaj) {
                linia+=3;
            }

            line = reader.readLine();

            stareDane = stareDane + line + System.lineSeparator();
            line = reader.readLine();

            stareDane = stareDane + line + System.lineSeparator();
            line = reader.readLine();
        }

        // zamien stareDane na nowe w postaci tablicy
        String[] noweDane = stareDane.split(System.lineSeparator());

        // zmien login na zapisanej wczesniej linii
        noweDane[linia] = nowyLogin;

        // zapisz plik z nowym loginem
        FileWriter writer = new FileWriter(file);
        for(int i = 0; i < noweDane.length; i++) {
            System.out.println(noweDane[i]);
            writer.write(noweDane[i] + "\n");
        }

        reader.close();
        writer.close();

        return true;
    }

    public boolean zmienHaslo() throws IOException {
        System.out.print("Podaj nowe haslo: ");
        String noweHaslo = in.next();

        boolean doliczaj = true;
        int linia = 1;

        // otworz danelogowania
        File file = new File("danelogowania.txt");
        String stareDane = "";
        BufferedReader reader = new BufferedReader(new FileReader(file));

        String line = reader.readLine();
        stareDane = stareDane + line + System.lineSeparator();

        while(line != null) {
            line = reader.readLine();

            // zapisuj linie w starychDanych oddzielajac lineSeparatorem
            stareDane = stareDane + line + System.lineSeparator();

            // jezeli trafisz na haslo do zmiany przestan doliczac do linii
            if(line.equals(haslo)) {
                doliczaj = false;
            }

            // doliczaj dopoki nie trafisz na haslo uzytkownika
            if(doliczaj) {
                linia+=3;
            }

            line = reader.readLine();
            if(line != null)
                stareDane = stareDane + line + System.lineSeparator();

            line = reader.readLine();
            if(line != null)
                stareDane = stareDane + line + System.lineSeparator();
        }

        // zamien stareDane na nowe w postaci tablicy
        String[] noweDane = stareDane.split(System.lineSeparator());

        // zmien haslo na zapisanej wczesniej linii
        noweDane[linia] = noweHaslo;

        // zapisz plik ze zmienionym haslem
        FileWriter writer = new FileWriter(file);
        for(int i = 0; i < noweDane.length; i++) {
            System.out.println(noweDane[i]);
            writer.write(noweDane[i] + "\n");
        }

        reader.close();
        writer.close();

        return true;
    }

    public String zwrocOstatniaGre() {
        BufferedReader input = null;
        try {
            // otworz plik uzytkownika
            input = new BufferedReader(new FileReader(nickname + "/" + nickname + ".txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String ostatnia = "";

        // przewin do konca pliku i zapisz ostatnia gre
        while (true) {
            String line = "";
            try {
                if ((line = input.readLine()) == null) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            ostatnia = line;
        }

        // zwroc ostatnia gre
        return ostatnia;
    }

    public void zapiszStatystyki() {
        // zapisz statystyki uzytkownika
        try {
            staty.zapiszStatystyki();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNickname() {
        return nickname;
    }

    public Statystyki getStatystyki() {
        return staty;
    }

}
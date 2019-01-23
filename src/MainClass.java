import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class MainClass {
    public static void main(String[] args) {
        // konczy program
        boolean wyjdz = false;

        // czy trwa jakas gra
        boolean kontynuuj = false;

        // czy uzytkownik rozpoczal nowa gre
        boolean nowaGra = false;

        // czy ostatnia gra zostala wczytana
        boolean wczytana = false;

        // gracz nie ma zadnych gier zapisanych
        boolean brakOstaniejGry = false;

        // determinuje ture gracza
        int tura = 2;


        Scanner in = new Scanner(System.in);

        Gracz gracz1 = null;
        Gracz gracz2 = null;

        Uzytkownik uzytkownik = new Uzytkownik(in);

        // zapisuje zrodlo do zapisu/odczytu
        String zrodlo = "";

        // jezeli warunek jest spelniony to znaczy ze uzytkownik nie rozegral jeszcze zadnej gry
        if(uzytkownik.zwrocOstatniaGre().equals("0")) {
            brakOstaniejGry = true;
        }
        else
            zrodlo = uzytkownik.getNickname() + "/" + uzytkownik.zwrocOstatniaGre();

        // loopuj dopoki gracz nie bedzie chcial wyjsc z gry
        while(!wyjdz) {
            // drukuje menu glowne
            menuGlowne(kontynuuj, wczytana, brakOstaniejGry);

            // wybor opcji przez uzytkownika
            int wybor = in.nextInt();

            switch (wybor) {
                // KONTYNUUJ GRE
                case -1:
                    // jezeli jest jakas gra wczytana wyswietl jako kontynuuj
                    if(kontynuuj) {
                        tura = graj(tura, gracz1, gracz2, in, uzytkownik);
                    }
                break;

                // NOWA GRA
                case 0:
                    // jezeli jest juz wczytana gra spytaj o zapis gry
                    if(wczytana) {
                        System.out.print("Czy chcialbys zapisac gre (napisz \"tak\" lub \"nie\") : ");
                        String wynik = in.next();
                        if(wynik.equals("tak")) {
                            gracz1.serializujPlansze(zrodlo + "gracz1.bin");
                            gracz2.serializujPlansze(zrodlo + "gracz2.bin");
                            System.out.println("Gra zapisana pomyslnie");
                        } else {
                            System.err.println("Gra nie zostala zapisana");
                        }
                    }

                    // zmien na nowa gre
                    nowaGra = true;
                    wczytana = false;

                    // wypelnienie 1 planszy
                    gracz1 = new Gracz(in);
                    System.err.println("Teraz niech gracz2 wypelni swoje pola");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // wypelnienie 2 planszy
                    gracz2 = new Gracz(in);

                    // graj
                    tura = graj(tura, gracz1, gracz2, in, uzytkownik);


                    kontynuuj = true;
                    if(!uzytkownik.zwrocOstatniaGre().equals("0")) {
                        brakOstaniejGry = false;
                    }

                break;

                // WROC DO ROZGRYWKI GRE
                case 1:
                    // zapobiega wymazaniu danych kiedy uzytkownik chce wczytac ta sama gre oraz sprawdza
                    // czy w ogole ten uzytkownik posiada jakas ostatnia gre
                    if(!wczytana  && !brakOstaniejGry) {

                        // jezeli uzytkownik rozpoczal nowa gre spytaj czy chce ja zapisac
                        if (nowaGra) {
                            System.out.print("Czy chcialbys zapisac nowa gre (napisz \"tak\" lub \"nie\") : ");
                            String wynik = in.next();
                            if (wynik.equals("tak")) {
                                String zrodloNowejGry = zapiszNowaGre(uzytkownik.getNickname());
                                gracz1.serializujPlansze(uzytkownik.getNickname() + "/" + zrodloNowejGry + "gracz1.bin");
                                gracz2.serializujPlansze(uzytkownik.getNickname() + "/" + zrodloNowejGry + "gracz2.bin");
                                System.out.println("Gra zapisana pomyslnie");
                            } else {
                                System.err.println("Gra nie zostala zapisana");
                            }
                        }

                        // zmien na wczytana gre
                        nowaGra = false;
                        wczytana = true;
                        kontynuuj = true;


                        // jezeli gracze nie zostali zainicjalizowani zainicjuj drugin konstruktorem
                        if (gracz1 == null || gracz2 == null) {
                            gracz1 = new Gracz(in, 1);
                            gracz2 = new Gracz(in, 2);
                        }

                        // wez ostatnia gre z pliku uzytkownika
                        gracz1.deserializujPlansze(zrodlo + "gracz1.bin");
                        gracz2.deserializujPlansze(zrodlo + "gracz2.bin");

                        // oblicz kogo jest tura
                        System.out.println("Gracz0 plansza zatopien: " + gracz1.getPlZatopienSize() + " gracz1: " + gracz2.getPlZatopienSize());
                        tura = determinujTure(gracz1.getPlZatopienSize(), gracz2.getPlZatopienSize());

                    }

                break;

                // OBEJRZYJ PONOWNIE GRY
                case 2:
                    if(nowaGra) {
                        // todo spytaj czy zapisac nowa gre
                    }

                    if(wczytana) {
                        // todo spytaj czy nadpisac
                    }

//                    wczytana = false;
//                    nowaGra = false;
//                    kontynuuj = false;

                    //todo obejrzyj ponownie

                break;

                // PROFIL UZYTKOWNIKA
                case 3:
                    // przed wejsciem w ta opcje zapisz satystyki zeby byly juz widoczne
                    try {
                        uzytkownik.getStatystyki().zapiszStatystyki();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    boolean menuUzytkownika = true;

                    while(menuUzytkownika) {
                        // wyswietl menu uzytkownika
                        menuUzytkownika(uzytkownik);
                        int opcja = in.nextInt();

                        switch (opcja) {
                            // ZMIEN LOGIN
                            case 0:
                                try {
                                    uzytkownik.zmienLogin();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;

                            // ZMIEN HASLO
                            case 1:
                                try {
                                    uzytkownik.zmienHaslo();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;

                            // WYJDZ
                            case 3:
                                menuUzytkownika = false;
                            break;
                        }
                    }
                break;

                // WYJDZ Z GRY
                case 4:
                    // zapisz statysyki przed wyjsciem
                    uzytkownik.zapiszStatystyki();

                    // jezeli uzytkownik gral nowa gre spytaj czy ja zapisac
                    if(nowaGra) {
                        System.out.print("Czy chcialbys zapisac nowa gre (napisz \"tak\" lub \"nie\") : ");
                        String wynik = in.next();
                        if(wynik.equals("tak")) {
                            String zrodloNowejGry = zapiszNowaGre(uzytkownik.getNickname());
                            gracz1.serializujPlansze(uzytkownik.getNickname() + "/" + zrodloNowejGry + "gracz1.bin");
                            gracz2.serializujPlansze(uzytkownik.getNickname() + "/" + zrodloNowejGry + "gracz2.bin");
                            System.out.println("Gra zapisana pomyslnie");
                        } else {
                            System.err.println("Gra nie zostala zapisana");
                        }
                    }

                    // jezeli uzytkownik gral ostatnia rozgrywke spytaj czy ja nadpisac
                    if(wczytana && !(zrodlo.equals(""))) {
                        System.out.print("Czy chcialbys zapisac gre (napisz \"tak\" lub \"nie\") : ");
                        String wynik = in.next();
                        if(wynik.equals("tak")) {
                            gracz1.serializujPlansze(zrodlo + "gracz1.bin");
                            gracz2.serializujPlansze(zrodlo + "gracz2.bin");
                            System.out.println("Gra zapisana pomyslnie");
                        } else {
                            System.err.println("Gra nie zostala zapisana");
                        }
                    }

                    wyjdz = true;
                break;
            }
        }
    }


    // zwraca nr gracza jezeli wygral lub -1 jezeli nikt nie wygral
    public static int sprawdzWygrana(Gracz gracz1, Gracz gracz2) {
        int zatopienia1 = 20;
        int zatopienia2 = 20;

        // porownaj plansze ze statkami z plansza zatopien
        for(int x = 0; x < gracz1.getPlZatopien().getList().getLast().length; x++) {
            for(int y = 0; y < gracz1.getPlZatopien().getList().getLast()[x].length; y++) {
                // przelicz trafienia na planszy zatopien ( 20 'x'-ow )
                if(gracz1.getPlZatopien().getList().getLast()[x][y] == 'X') {
                    zatopienia1--;
                }
                if(gracz2.getPlZatopien().getList().getLast()[x][y] == 'X') {
                    zatopienia2--;
                }
            }
        }

        // wygral gracz1
        if(zatopienia1 == 0) {
            return 1;
        }

        // wygral gracz0
        if(zatopienia2 == 0) {
            return 0;
        }

        return -1;
    }

    public static void wyswieltKoncowaPlansze(Gracz gracz1, Gracz gracz2) {
        System.out.println("\t\t\t\tKONIEC GRY");

        char[][] koncowa1 = new char[10][10];
        char[][] koncowa2 = new char[10][10];

        // zapisz w tych tablicach koncowe plansze zatopien
        for (int x = 0; x < koncowa1.length; x++) {
            for (int y = 0; y < koncowa1[x].length; y++) {
                koncowa1[x][y] = gracz1.getPlZatopien().getList().getLast()[x][y];
                koncowa2[x][y] = gracz2.getPlZatopien().getList().getLast()[x][y];
            }
        }

        // jezeli zostaly jakies nie stracone statki pokaz je na mapie koncowej
        for (int x = 0; x < koncowa1.length; x++) {
            for (int y = 0; y < koncowa1[x].length; y++) {
                if(gracz1.getPl().getList().getLast()[x][y] == 'O' && koncowa1[x][y] == '.') {
                    koncowa1[x][y] = 'O';
                }
                if(gracz2.getPl().getList().getLast()[x][y] == 'O' && koncowa2[x][y] == '.') {
                    koncowa2[x][y] = 'O';
                }
            }
        }

        System.err.println("Plansza gracza0");
        gracz1.drukujPlansze(koncowa1);
        System.err.println("Plansza gracza1");
        gracz2.drukujPlansze(koncowa2);
    }

    public static int graj(int tura, Gracz gracz1, Gracz gracz2, Scanner in, Uzytkownik uzytkownik) {
        boolean skonczGre = false;

        while (!skonczGre) {
            // zapisz sprawdzenie wygranej i sprawdz czy ktorys gracz wygral
            int wygrana = sprawdzWygrana(gracz1, gracz2);

            // jezeli ktos wygral wyswietl napis i pokaz koncowe plansze
            if(wygrana != -1) {
                // wyswietl plansze i powiedz uzytkownikowi kto wygral
                wyswieltKoncowaPlansze(gracz1, gracz2);
                System.err.println("Wygral gracz" + wygrana);
                break;
            }

            menuGry();
            System.out.print("gracz" + nrGracza(tura) + ": ");
            int input = in.nextInt();
            switch (input) {
                // ZAATAKUJ
                case 0:
                    // w zaleznosci ktory gracz ma ture zaatakuj jego plansze ( atak przeprowadzany jest na samym sobie )
                    // czyli jezeli gracz2.atakuj() to wtedy atak przeprowadza gracz1
                    if(nrGracza(tura) == 0) {
                        while(true) {
                            int wynik = gracz2.atakuj();
                            if(wynik == 2) {
                                uzytkownik.getStatystyki().pudlo();
                                break;
                            } else if(wynik == -1) {
                                System.err.println("Cos poszlo nie tak zaatakuj jeszcze raz");
                            } else if(wynik == 1) {
                                uzytkownik.getStatystyki().trafiony();

                                // zapisz sprawdzenie wygranej i sprawdz czy ktorys gracz wygral
                                wygrana = sprawdzWygrana(gracz1, gracz2);

                                // jezeli ktos wygral wyswietl napis i pokaz koncowe plansze
                                if(wygrana != -1) {
                                    // wyswietl plansze i powiedz uzytkownikowi kto wygral
                                    wyswieltKoncowaPlansze(gracz1, gracz2);
                                    System.err.println("Wygral gracz" + wygrana);
                                    return ++tura;
                                }
                            }
                        }
                        tura++;
                    } else {
                        while(true) {
                            int wynik = gracz1.atakuj();
                            if(wynik == 2) {
                                uzytkownik.getStatystyki().pudlo();
                                break;
                            } else if(wynik == -1) {
                                System.err.println("Cos poszlo nie tak zaatakuj jeszcze raz");
                            } else if(wynik == 1) {
                                uzytkownik.getStatystyki().trafiony();

                                // zapisz sprawdzenie wygranej i sprawdz czy ktorys gracz wygral
                                wygrana = sprawdzWygrana(gracz1, gracz2);

                                // jezeli ktos wygral wyswietl napis i pokaz koncowe plansze
                                if(wygrana != -1) {
                                    // wyswietl plansze i powiedz uzytkownikowi kto wygral
                                    wyswieltKoncowaPlansze(gracz1, gracz2);
                                    System.err.println("Wygral gracz" + wygrana);
                                    return ++tura;
                                }
                            }
                        }
                        tura++;
                    }
                break;

                // WYJDZ Z GRY
                case 1:
                    skonczGre = true;
                break;

                default:
                    System.err.println("Zly input sprobuj jeszcze raz");
                break;
            }
        }

        // zwroc na ktorym graczu zatrzymala sie rozgrywka
        return tura;
    }

    // zwroc nr gracza na podstawie tury
    public static int nrGracza(int tura) {
        return (int)Math.floor(tura%2);
    }

    public static void menuGry() {
        System.out.println("Wykonaj ruch:");
        System.out.println("0 - Zaatakuj");
        System.out.println("1 - Wyjdz z rozgrywki");
    }

    public static void menuGlowne(boolean kontynuuj, boolean wczytana, boolean brakWczytanej) {
        System.out.println("\t\t\tMenu Glowne");
        if(kontynuuj)
            System.out.println("-1 - Kontynuuj gre");
        if(!kontynuuj || wczytana)
            System.out.println("0 - Nowa gra");
        if(!wczytana && !brakWczytanej)
            System.out.println("1 - Wroc do rozgrywki");
        else
            System.err.println("1 - Wroc do rozgrywki");
        System.out.println("2 - Obejrzyj ponownie gry");
        System.out.println("3 - Profil Uzytkownika");
        System.out.println("4 - Wyjdz z gry");
    }

    public static void menuUzytkownika(Uzytkownik uzytkownik) {
        System.out.println("Statystyki");
        System.out.println("trafione statki : " + uzytkownik.getStatystyki().getTrafienia());
        System.out.println("pudla : " + uzytkownik.getStatystyki().getPudla());
        System.out.println();
        System.out.println("0 - zmien login");
        System.out.println("1 - zmien haslo");
        System.out.println("3 - wyjdz");
    }

    // zwraca zrodlo do pliku
    public static String zapiszNowaGre(String nickname) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyy");

        int index = 1;
        File temp = new File(nickname + "/" + nickname + ".txt");

        if(temp.exists()) {


            // przeczytaj czy taka gra juz istnieje
            FileReader fr = null;

            try {
                fr = new FileReader(nickname + "/" + nickname + ".txt");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            BufferedReader br = new BufferedReader(fr);
            String line = "not null";

            while (true) {
                try {
                    if (((line = br.readLine()) == null)) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (line.equals(now.format(formatter) + "-gra" + index)) {
                    index++;
                }
            }

            try {
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        FileWriter fw = null;

        try {
            fw = new FileWriter(nickname + "/" + nickname + ".txt", true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        PrintWriter pw = new PrintWriter(fw);

        pw.println(now.format(formatter) + "-gra" + index);
        pw.close();

        try {
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return (now.format(formatter) + "-gra" + index);
    }

    // zwraca ture na podstawie danych np. ilosci plansz dwoch graczy
    public static int determinujTure(int gracz0, int gracz1) {
        if(gracz0 == gracz1) {
            return 2;
        }
        return 3;
    }
}

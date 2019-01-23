import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Gracz {
    Scanner in;

    private char[][] plansza = new char[10][10];
    private Plansza pl = new Plansza();
    private char[][] planszaZatopien = new char[10][10];
    private Plansza plZatopien = new Plansza();
    private char[] kolumny = new char[11];
    private int[] wiersze = new int[10];
    private int id = 0;

    // to bedzie potrzebne do ustalenia gdzie jest wolne miejsce podczas stawiania statkow
    private boolean goraWolna = true;
    private boolean dolWolny = true;
    private boolean lewaWolna = true;
    private boolean prawaWolna = true;

    public Gracz(Scanner in, int id) {
        this.in = in;
        this.id = id;

        // WypeĹ‚nia pierwszy rzÄ…d literami od A do J
        for (int i = 0; i < kolumny.length; i++) {
            kolumny[i] = (char)(64 + i);
        }

        // WypeĹ‚nia wiersze liczbami od 1 do 10
        for (int i = 0; i < wiersze.length; i++) {
            wiersze[i] = i+1;
        }
    }

    public Gracz(Scanner in) {
        this.in = in;

        id = (int)(Math.random() * Integer.MAX_VALUE);

        // WypeĹ‚nia pierwszy rzÄ…d literami od A do J
        for (int i = 0; i < kolumny.length; i++) {
            kolumny[i] = (char)(64 + i);
        }

        // WypeĹ‚nia wiersze liczbami od 1 do 10
        for (int i = 0; i < wiersze.length; i++) {
            wiersze[i] = i+1;
        }

        // WypeĹ‚nia plansze pustymi polami
        for (int x = 0; x < plansza.length; x++) {
            for (int y = 0; y < plansza[x].length; y++) {
                plansza[x][y] = '.';
                planszaZatopien[x][y] = '.';
            }
        }

        // dodaje do list plansze i planszeZatopien
        pl.dodajPlansze(plansza);
        plZatopien.dodajPlansze(planszaZatopien);

        // podaje ilosci statkow do postawienia
        int czteromasztowce = 1;
        int trojmasztowce = 2;
        int dwumasztowce = 3;
        int jednomasztowce = 4;

        // stawiam po kolei statki od najwiekszego do najmniejszego

        for(int i = 0; i < czteromasztowce; i++) {
            dodajStatek(4);
        }

        for(int i = 0; i < trojmasztowce; i++) {
            dodajStatek(3);
        }

        for(int i = 0; i < dwumasztowce; i++) {
            dodajStatek(2);
        }

        for(int i = 0; i < jednomasztowce; i++) {
            dodajStatek(1);
        }

    }

    // atak jest wykonywany na samym sobie!!
    public int atakuj() {
        drukujPlansze(planszaZatopien);
        System.out.println("Podaj koordynaty ataku");

        // zbieram koordynaty kolumny do ataku
        System.out.print("Numer kolumny ( od A do J ) : ");

        // biore pierwsza litere z inputu uzytkownika ( gdy wpisze Ed - zapisze sie tylko E )
        char kolumnaStr = in.next().charAt(0);

        // jezeli uzytkownik uzyl malej litery zmien na duza
        if(kolumnaStr >= 97 && kolumnaStr <= 106) {
            kolumnaStr -= 32;
        }

        // zmieniam ze stringa na nr kolumny
        int kolumna = kolumnaStr - 65;

        if(kolumna < 65 && kolumna > 74) {
            System.err.println("zly input kolumny sprobuj ponownie");
            return 0;
        }

        // brakujace koordy do ataku
        System.out.print("Numer wiersza ( od 1 do 10 ): ");

        // przypisuje i odejmuje 1 zeby latwiej bylo operowac na tablicach
        int wiersz = (in.nextInt() - 1);

        // jezeli uzytkownik podal zly przedzial wiersza zwroc 0 ( i przeprowadz atak od nowa )
        if(wiersz > 9 || wiersz < 0) {
            System.err.println("Podane koordynaty sa zle");
            return 0;
        }

        // jezeli byl juz atak w to pole, zwroc 0 ( i przeprowadz atak od nowa )
        if(planszaZatopien[wiersz][kolumna] == 'X' || planszaZatopien[wiersz][kolumna] == '~') {
            System.err.println("Juz atakowales w to pole, wybierz inne!");
            return 0;
        }

        // trafiony! -> Kolejny atak ( po zwroceniu 1 atak od nowa )
        if(plansza[wiersz][kolumna] == 'O') {
            System.err.println("\nBrawo, trafiles! Masz dodatkowy atak!");

            // zaznacz trafienie
            planszaZatopien[wiersz][kolumna] = 'X';
            plZatopien.dodajPlansze(planszaZatopien);
            return 1;
        }

        // pudlo (
        if(plansza[wiersz][kolumna] == '.') {
            System.err.println("\nPudlo! Kolej na ruch przeciwnika");

            // zaznacz pudlo
            planszaZatopien[wiersz][kolumna] = '~';
            plZatopien.dodajPlansze(planszaZatopien);
            return 2;
        }

        // cos poszlo nie tak...
        return -1;
    }

    // dodaje statki na poczÄ…tku nowej gry
    private boolean dodajStatek(int dlugosc) {
        // pokaz plansze przed postawieniem statkow
        drukujPlansze(plansza);

        System.out.println("Podaj koordynaty wierzcholka statku");
        System.out.print("Numer kolumny ( od A do J ) : ");
        char kolumna = in.next().charAt(0);

        if(kolumna >= 97 && kolumna <= 106) {
            kolumna -= 32;
        }

        if(kolumna < 65 && kolumna > 74) {
            System.err.println("zly input kolumny sprobuj ponownie");
            dodajStatek(dlugosc);
            return false;
        }

        System.out.print("Numer wiersza ( od 1 do 10 ): ");
        int wiersz = in.nextInt();
        if(wiersz < 1 || wiersz > 10) {
            System.err.println("za duza/mala liczba na wiersz");
            dodajStatek(dlugosc);
            return false;
        } else {
            // Jezli punkt jest pustym polem postaw statek
            if (plansza[wiersz - 1][(kolumna - 65)] == '.') {
                // jezeli statek ma dlugosc wieksza niz 1 obroc i postaw statek
                if (dlugosc > 1) {
                    // jezeli obrocStatek zwroci false dodaj statek od nowa
                    if(!obrocStatek((wiersz - 1), (kolumna - 65), dlugosc)) {
                        System.err.println("Wybierz inny punkt");
                        dodajStatek(dlugosc);

                        return false;
                    }
                    pl.dodajPlansze(plansza);
                }
                else {
                    plansza[wiersz - 1][(kolumna - 65)] = 'O';
                    pl.dodajPlansze(plansza);
                }
            } else {
                // error dodaj od nowa
                System.err.println("Podany punkt jest juz zajety przez inny statek! Wybierz inny");
                dodajStatek(dlugosc);
            }

        }

        // reset tych wartosci
        lewaWolna = true;
        prawaWolna = true;
        goraWolna = true;
        dolWolny = true;

        return true;
    }

    // dodaje obrocony statek do planszy
    private boolean obrocStatek(int wiersz, int kolumna, int dlugosc) {
        // drukuje kierunki w ktorych gracz moze postawic statki ( zwraca czy gracz ma opcje jak nie zwraca false )
        if(!drukujKierunki(wiersz, kolumna, dlugosc)) {
            return false;
        }
        System.out.println("Ĺ»eby ustawiÄ‡ statek uĹĽyj jednego ze znakĂłw '<' '>' '^' 'v', do dyspozycji masz: ");
        char znak = in.next().charAt(0);
        switch (znak) {
            // jesli lewa jest wolna postaw statek w lewo od danego punktu
            case '<':
                if(lewaWolna) {
                    // ustaw statek
                    for (int i = 0; i < dlugosc; i++)
                        plansza[wiersz][kolumna - i] = 'O';
                } else {
                    obrocStatek(wiersz, kolumna, dlugosc);
                }
            break;

            // jesli lewa jest wolna postaw statek w prawo od danego punktu
            case '>':
                if(prawaWolna) {
                    // ustaw statek
                    for (int i = 0; i < dlugosc; i++)
                        plansza[wiersz][kolumna + i] = 'O';
                } else {
                    obrocStatek(wiersz, kolumna, dlugosc);
                }
            break;

            // jesli lewa jest wolna postaw statek w gore od danego punktu
            case '^':
                if(goraWolna) {
                    // ustaw statek
                    for (int i = 0; i < dlugosc; i++)
                        plansza[wiersz - i][kolumna] = 'O';
                } else {
                    obrocStatek(wiersz, kolumna, dlugosc);
                }
            break;

            // jesli lewa jest wolna postaw statek w dol od danego punktu
            // ( dwa case-y zeby gracz mogl uzyc malej jak i duzej litery )
            case 'V':
            case 'v':
                if(dolWolny) {
                    // ustaw statek
                    for (int i = 0; i < dlugosc; i++)
                        plansza[wiersz + i][kolumna] = 'O';
                } else {
                    obrocStatek(wiersz, kolumna, dlugosc);
                }
            break;

            default:
                System.err.println("Zly input");
            break;
        }

        return true;
    }

    // pozwala zobaczyc kierunki w ktore uzytkownik moze postawic statek ( zwraca false gdy nie ma opcji )
    private boolean drukujKierunki(int wiersz, int kolumna, int dlugosc) {
        plansza[wiersz][kolumna] = 'O';

        // DOL //

        // czy punkt plus dlugosc statku mieszcza sie w planszy
        if(wiersz + dlugosc <= plansza.length) {
            // sprawdz czy nie ma statkow po drodze
            for (int y = wiersz + dlugosc - 1; y >= wiersz + 1; y--) {
                if(plansza[y][kolumna] == 'O') {
                    dolWolny = false;
                    break;
                }
            }

            // drukuj strzalki zeby pokazac graczowi opcje do wybrania tego kierunku
            if(dolWolny) {
                for (int i = wiersz + 1; i <= wiersz + (dlugosc - 1); i++)
                    plansza[i][kolumna] = 'v';
            }
        } else {
            dolWolny = false;
        }


        // GĂ“RA //

        // czy punkt plus dlugosc statku mieszcza sie w planszy
        if(wiersz + 1 - dlugosc >= 0) {
            // sprawdz czy nie ma statkow po drodze
            for (int y = wiersz - 1; y >= wiersz - dlugosc + 1; y--) {
                if(y < 0 || plansza[y][kolumna] == 'O') {
                    goraWolna = false;
                    break;
                }
            }

            // drukuj strzalki zeby pokazac graczowi opcje do wybrania tego kierunku
            if(goraWolna) {
                for (int i = wiersz - 1; i >= wiersz - (dlugosc - 1); i--)
                    plansza[i][kolumna] = '^';
            }
        } else {
            goraWolna = false;
        }


        // PRAWA //

        // czy punkt plus dlugosc statku mieszcza sie w planszy
        if(kolumna + dlugosc <= plansza.length) {
            // sprawdz czy nie ma statkow po drodze
            for (int x = kolumna + 1; x <= kolumna + dlugosc - 1; x++) {
                if(plansza[wiersz][x] == 'O') {
                    prawaWolna = false;
                    break;
                }
            }

            // drukuj strzalki zeby pokazac graczowi opcje do wybrania tego kierunku
            if(prawaWolna) {
                for (int i = kolumna + 1; i <= kolumna + (dlugosc - 1); i++)
                    plansza[wiersz][i] = '>';
            }
        } else {
            prawaWolna = false;
        }


        // LEWA //

        // czy punkt plus dlugosc statku mieszcza sie w planszy
        if(kolumna + 1 - dlugosc >= 0) {
            // sprawdz czy nie ma statkow po drodze
            for (int x = kolumna - 1; x >= kolumna - dlugosc + 1; x--) {
                if(plansza[wiersz][x] == 'O') {
                    lewaWolna = false;
                    break;
                }
            }

            // drukuj strzalki zeby pokazac graczowi opcje do wybrania tego kierunku
            if(lewaWolna) {
                for (int i = kolumna - 1; i >= kolumna - (dlugosc - 1); i--)
                    plansza[wiersz][i] = '<';
            }
        } else {
            lewaWolna = false;
        }

        drukujPlansze(plansza);

        // Czysci kierunki pokazywane na planszy
        for(int x = 0; x < plansza.length; x++) {
            for(int y = 0; y < plansza[x].length; y++) {
                switch (plansza[x][y]) {
                    case '<':
                        plansza[x][y] = '.';
                    break;

                    case '>':
                        plansza[x][y] = '.';
                    break;

                    case '^':
                        plansza[x][y] = '.';
                    break;

                    case 'v':
                        plansza[x][y] = '.';
                    break;
                }
            }
        }

        // jezeli nie ma opcji zwroc false
        if(!dolWolny && !goraWolna && !lewaWolna && !prawaWolna) {
            plansza[wiersz][kolumna] = '.';
            return false;
        }
        return true;
    }

    // drukuje podanÄ… tablice (plansze)
    public void drukujPlansze(char[][] tablica) {
        // pokazuje pierwszy rzÄ…d ( od @ do J )
        for(int i = 0; i < kolumny.length; i++)
            System.out.print(String.format("%-4s", kolumny[i]));

        System.out.println();

        // pokazuje zawartoĹ›Ä‡ planszy, kiedy jest pierwsza kolumna drukuje teĹĽ wiersz
        // a resztÄ™ drukuje tylko pole planszy
        for(int x = 0; x < tablica.length; x++) {
            for(int y = 0; y < tablica[x].length; y++) {
                if(y == 0) {
                    System.out.print(String.format("%-4s", wiersze[x]));
                    System.out.print(String.format("%-4s", tablica[x][y]));
                }
                else
                    System.out.print(String.format("%-4s", tablica[x][y]));
            }
            System.out.println();
        }
    }

    // zapisz plansze gracza w postaci binarnej
    public void serializujPlansze(String zrodlo) {
        // zwraca dzisiejsza date w formacie dzien-mies-rok
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyy");

        // Serializacja
        try {
            FileOutputStream file = new FileOutputStream(zrodlo);
            ObjectOutputStream out = new ObjectOutputStream(file);

            // zapisuje liste plansz gracza
            out.writeObject(pl);
            out.writeObject(plZatopien);

            out.close();
            file.close();

            System.out.println("Plansza zapisana");

        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    // wczytaj plansze gracza ( zwraca ture )
    public void deserializujPlansze(String zrodlo) {
        ObjectInputStream inputStream = null;

        try {
            inputStream = new ObjectInputStream(new FileInputStream(zrodlo));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            // wczytaj listy
            pl = (Plansza) inputStream.readObject();
            plZatopien = (Plansza) inputStream.readObject();

            // daj ostatnie elementy list do plansz
            plansza = pl.getList().getLast();
            planszaZatopien = plZatopien.getList().getLast();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public int getPlZatopienSize() {
        return plZatopien.getList().size();
    }

    public Plansza getPlZatopien() {
        return plZatopien;
    }

    public Plansza getPl() {
        return pl;
    }
}
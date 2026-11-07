package battleship;

public class GodClassDemo {

    // Vários métodos para ultrapassar o limite de NOM > 15
    public void m1() { check(1); }
    public void m2() { check(2); }
    public void m3() { check(3); }
    public void m4() { check(4); }
    public void m5() { check(5); }
    public void m6() { check(6); }
    public void m7() { check(7); }
    public void m8() { check(8); }
    public void m9() { check(9); }
    public void m10() { check(10); }
    public void m11() { check(11); }
    public void m12() { check(12); }
    public void m13() { check(13); }
    public void m14() { check(14); }
    public void m15() { check(15); }
    public void m16() { check(16); } // 16º método -> ativa ClassWithTooManyMethods
    public void m17() {check(17); }

    // Método com complexidade ciclomática elevada para ultrapassar WMC > 47
    private void check(int v) {
        if (v == 1) System.out.println(1);
        else if (v == 2) System.out.println(2);
        else if (v == 3) System.out.println(3);
        else if (v == 4) System.out.println(4);
        else if (v == 5) System.out.println(5);
        else if (v == 6) System.out.println(6);
        else if (v == 7) System.out.println(7);
        else if (v == 8) System.out.println(8);
        else if (v == 9) System.out.println(9);
        else if (v == 10) System.out.println(10);
    }
}
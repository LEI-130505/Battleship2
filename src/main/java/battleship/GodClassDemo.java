package battleship;

public class GodClassDemo {

    // 16 métodos = 16 de complexidade (1 por método) + dispara o NOM > 15
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
    public void m16() { check(16); }
    public void m17() { check(17); }
    public void m18() { check(18); }
    public void m19() { check(19); }
    public void m20() { check(20); }
    public void m21() { check(21); }
    public void m22() {check(22); }

    // Adiciona ~40 pontos de complexidade ciclomática de uma só vez
    // WMC total estimado da classe = 16 + 40 = 56 (Ultrapassa o limite de 47)
    private void check(int v) {
        switch (v) {
            case 1: System.out.println(1); break;
            case 2: System.out.println(2); break;
            case 3: System.out.println(3); break;
            case 4: System.out.println(4); break;
            case 5: System.out.println(5); break;
            case 6: System.out.println(6); break;
            case 7: System.out.println(7); break;
            case 8: System.out.println(8); break;
            case 9: System.out.println(9); break;
            case 10: System.out.println(10); break;
            case 11: System.out.println(11); break;
            case 12: System.out.println(12); break;
            case 13: System.out.println(13); break;
            case 14: System.out.println(14); break;
            case 15: System.out.println(15); break;
            case 16: System.out.println(16); break;
            case 17: System.out.println(17); break;
            case 18: System.out.println(18); break;
            case 19: System.out.println(19); break;
            case 20: System.out.println(20); break;
            case 21: System.out.println(21); break;
            case 22: System.out.println(22); break;
            case 23: System.out.println(23); break;
            case 24: System.out.println(24); break;
            case 25: System.out.println(25); break;
            case 26: System.out.println(26); break;
            case 27: System.out.println(27); break;
            case 28: System.out.println(28); break;
            case 29: System.out.println(29); break;
            case 30: System.out.println(30); break;
            case 31: System.out.println(31); break;
            case 32: System.out.println(32); break;
            case 33: System.out.println(33); break;
            case 34: System.out.println(34); break;
            case 35: System.out.println(35); break;
            case 36: System.out.println(36); break;
            case 37: System.out.println(37); break;
            case 38: System.out.println(38); break;
            case 39: System.out.println(39); break;
            case 40: System.out.println(40); break;
            default: System.out.println("Max"); break;
        }
    }
}
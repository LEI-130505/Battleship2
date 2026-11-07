// Deliberate "god class" to trigger Qodana with m_limit = 1.
// Put it in src/main/java (any package) and push it in a PR.
public class GodClassExample {

    // Many methods -> ClassWithTooManyMethods (limit 1)
    public int add(int a, int b) {
        return a + b;
    }

    public int subtract(int a, int b) {
        return a - b;
    }

    public int multiply(int a, int b) {
        return a * b;
    }

    public int divide(int a, int b) {
        if (b == 0) {
            throw new IllegalArgumentException("b must not be zero");
        }
        return a / b;
    }

    // Lots of branching -> OverlyComplexClass (cyclomatic complexity limit 1)
    public String classify(int value) {
        if (value < 0) {
            return "negative";
        } else if (value == 0) {
            return "zero";
        } else if (value < 10) {
            return "small";
        } else if (value < 100) {
            return "medium";
        }
        for (int i = 0; i < value; i++) {
            if (i % 2 == 0 && i % 3 == 0) {
                return "big-and-divisible";
            }
        }
        return "large";
    }
}

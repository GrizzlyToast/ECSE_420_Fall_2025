package ca.mcgill.ecse420.a3;

public class TestLazyList {

    public void TestContains1() {
        LazyList<String> list = new LazyList<>();
        list.add("a");
        list.add("b");
        list.add("c");

        assert list.contains("a") == true;
        assert list.contains("b") == true;
        assert list.contains("c") == true;
    }

    public void TestContains2() {
        LazyList<String> list = new LazyList<>();
        list.add("a");
        list.add("b");
        list.add("c");

        assert list.contains("d") == false;
        assert list.contains("e") == false;
    }

    public void TestContains3() {
        LazyList<String> list = new LazyList<>();
        list.add("a");
        list.add("b");
        list.add("c");

        list.markNode("b");

        assert list.contains("b") == false;;
    }

    public static void main(String[] args) {
        TestLazyList t = new TestLazyList();

        try { t.TestContains1(); System.out.println("TestContains1 passed."); }
        catch (AssertionError e) { System.out.println("TestContains1 FAILED."); }

        try { t.TestContains2(); System.out.println("TestContains2 passed."); }
        catch (AssertionError e) { System.out.println("TestContains2 FAILED."); }

        try { t.TestContains3(); System.out.println("TestContains3 passed."); }
        catch (AssertionError e) { System.out.println("TestContains3 FAILED."); }
}

}

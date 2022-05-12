import java.util.ArrayList;
import java.util.concurrent.*;

/**
 * This class represents resourses available for {@link Node}.
 * 
 * @author Volodymyr Davybida
 */

public class Resources {
    public ExecutorService executorService = Executors.newCachedThreadPool();
    public int Ares = 0, Bres = 0, Cres = 0;
    public static int Asum = 0, Bsum = 0, Csum = 0;

    /**
     * 
     * @param requests resourses that will be prescripted to specific {@link Node}
     *                 and added to overall pull of resurses.
     */
    public void service(ArrayList<String> requests) {
        try {
            for (String iterable : requests) {
                String[] req = iterable.split(":");
                switch (req[0]) {
                    case "A": {

                        Ares = Integer.valueOf(req[1]);
                        Asum += Integer.valueOf(req[1]);

                        break;
                    }
                    case "B": {

                        Bres = Integer.valueOf(req[1]);
                        Bsum += Integer.valueOf(req[1]);

                        break;
                    }
                    case "C": {

                        Cres = Integer.valueOf(req[1]);
                        Csum += Integer.valueOf(req[1]);

                        break;
                    }
                    default:
                        break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Executes passed {@code amoount} of {@code resourse}
     * 
     * 
     * @param resourse Specifies which resourse will be executed
     * @param amoount  Specifies the ammount of executable resourse
     * @param node     Node which uses its resourses for execution
     * @return int № of succesfully executed resourses
     */
    public int serve(String resourse, int amoount, Node node) {
        int completed_resoures = 0;
        switch (resourse) {
            case "A": {

                for (int i = 0; i < amoount; i++) {
                    if (node.resourses.Ares > 0) {
                        --node.resourses.Ares;
                        --Asum;
                        executorService.submit(new A<>());
                        ++completed_resoures;
                    } else {
                        break;
                    }

                }

                break;
            }
            case "B": {

                for (int i = 0; i < amoount; i++) {
                    if (node.resourses.Bres > 0) {
                        --node.resourses.Bres;
                        --Bsum;
                        executorService.submit(new B<>());
                        ++completed_resoures;
                    } else {
                        break;
                    }

                }

                break;
            }
            case "C": {
                for (int i = 0; i < amoount; i++) {
                    if (node.resourses.Cres > 0) {
                        --node.resourses.Cres;
                        --Csum;
                        executorService.submit(new C<>());
                        ++completed_resoures;
                    } else {
                        break;
                    }

                }
                break;
            }
        }
        return completed_resoures;
    }

    public void seeAvailableResourses() {
        System.out.println(
                " Available resourses -> A -> " + Ares + "; B -> " + Bres + "; C -> " + Cres);
    }

    public int getAres() {
        return Ares;
    }

    public void setAres(int ares) {
        Ares = ares;
    }

    public int getBres() {
        return Bres;
    }

    public void setBres(int bres) {
        Bres = bres;
    }

    public int getCres() {
        return Cres;
    }

    public void setCres(int cres) {
        Cres = cres;
    }
}

/**
 * Class that implements {@link Callable} and acts as {@link Resources} for
 * {@link Node}
 * <p>
 * (Shoud be parametrisised)
 */

class A<T> implements Callable<T> {

    @Override
    public T call() throws Exception {
        try {
            System.out.println("A is doing smth!");
            Thread.sleep(2500);
            System.out.println("A is done!");
            // return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

/**
 * Class that implements {@link Callable} and acts as {@link Resources} for
 * {@link Node}
 * <p>
 * (Shoud be parametrisised)
 */
class B<T> implements Callable<T> {

    @Override
    public T call() throws Exception {
        try {
            System.out.println("B is doing smth!");
            Thread.sleep(400);
            System.out.println("B is done!");
            // return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

/**
 * Class that implements {@link Callable} and acts as {@link Resources} for
 * {@link Node}
 * <p>
 * (Shoud be parametrisised)
 */
class C<T> implements Callable<T> {

    @Override
    public T call() throws Exception {
        try {
            System.out.println("C is doing smth!");
            Thread.sleep(1500);
            System.out.println("C is done!");
            // return null;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }
}

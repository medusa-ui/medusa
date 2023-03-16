package io.getmedusa.medusa.sample;

import io.getmedusa.medusa.core.annotation.UIEventPage;
import io.getmedusa.medusa.core.attributes.Attribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

@UIEventPage(path="/conditional", file = "/pages/conditional")
public class ConditionalController {

    private Integer number = 42;
    private Boolean show = Boolean.TRUE;
    // Yes, I know the Fibonacci sequence contains two ones... but the second 1 is not useful here
    private List<Integer> fibonacci = List.of( 0, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144, 233, 377, 610, 987, 1597, 2584, 4181, 6765);

    public List<Attribute> setupAttributes(){
        return List.of(
               new Attribute("number", number),
               new Attribute("fibo", fibonacciSum(number)),

               new Attribute("show", true),
               new Attribute("message", "Now, You see me")
        );
    }

    public List<Attribute> fibo(Integer number) {
        List<Integer> result = fibonacciSum(number);
        System.out.println(number + " => " + result + ", validation: " + result.stream().reduce(0, Integer::sum));
        return List.of(
                new Attribute("fibo", result)
        );
    }
    // try to simplify the problem
    public List<Attribute> toggleShow() {
        show = !show;
        String message =  show ? "Now, You see me" : "Now, You don't";
        System.out.println("show: " + show + ", message: " + message);
        return List.of(
                new Attribute("message", message),
                new Attribute("show", show)
        );
    }


    private List<Integer> fibonacciSum(Integer number) {
        List<Integer> result = new ArrayList<>();
        // loop backwards
        ListIterator<Integer> listIterator = fibonacci.listIterator(fibonacci.size());
        while (listIterator.hasPrevious()) {
            Integer fib = listIterator.previous();
            if(number == 0) {
                break;
            }
            if (number >= fib) {
                number -= fib;
                result.add(fib);
            }
        }
        Collections.reverse(result);
        return result;
    }

}
